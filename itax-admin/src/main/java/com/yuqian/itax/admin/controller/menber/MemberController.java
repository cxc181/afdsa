package com.yuqian.itax.admin.controller.menber;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.enums.BankCardTypeEnum;
import com.yuqian.itax.capital.enums.CardStatusEnum;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.RegOrderStatusEnum;
import com.yuqian.itax.order.service.MemberOrderRelaService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.snapshot.service.MemberSnapshotService;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.BankBinService;
import com.yuqian.itax.system.service.BankInfoService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.dto.MemberUpgradeDTO;
import com.yuqian.itax.user.entity.po.MemberProfitsRulesPO;
import com.yuqian.itax.user.entity.po.UpdateMemberPhonePO;
import com.yuqian.itax.user.entity.query.MemberQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.*;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.AuthKeyUtils;
import com.yuqian.itax.util.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 会员Controll
 * auth: HZ
 * time: 2019/12/09
 */
@RestController
@RequestMapping("/member")
@Slf4j
public class MemberController extends BaseController {

    @Autowired
    MemberAccountService memberAccountService;

    @Autowired
    OrderService orderService;

    @Autowired
    MemberProfitsRulesService memberProfitsRulesService;

    @Autowired
    ProductService productService;
    @Autowired
    OemService oemService;
    @Autowired
    UserBankCardService userBankCardService;
    @Autowired
    private BankBinService bankBinService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private BankInfoService bankInfoService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private OssService ossService;
    @Autowired
    MemberAccountChangeService memberAccountChangeService;
    @Autowired
    MemberSnapshotService memberSnapshotService;
    @Autowired
    ProfitsDetailService profitsDetailService;
    @Autowired
    MemberOrderRelaService memberOrderRelaService;
    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 会员列表查询
     * auth: HZ
     * time: 2019/12/09
     */
    @PostMapping("/memberPageInfo")
    public ResultVo memberPageInfo(@RequestBody MemberQuery memberQuery){
        //验证登陆
        getCurrUser();
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            memberQuery.setOemCode(getRequestHeadParams("oemCode"));
        }
        //查询当前登陆账号所在组织
        OrgEntity orgEntity=orgService.queryOrgEntityByUserId(getCurrUserId());
        if(null == orgEntity){
            return ResultVo.Fail("登陆账号没有组织");
        }
        //获取组织的管理员账号
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        //获取用户关系表
        Integer userClass=orgEntity.getOrgType();
        if(userClass==2||userClass==3){
            userClass=2;
        }else if(userClass==4){
            userClass=3;
        }else  if (userClass==5){
            userClass=4;
        }
        UserRelaEntity userRelaEntity=userRelaService.queryUserRelaEntityByUserId(userEntity.getId(),userClass);
        memberQuery.setTree(userRelaEntity.getUserTree());
        //分页查询
        PageInfo<MemberPageInfoVO> memberList=memberAccountService.memberPageInfo(memberQuery);
        return ResultVo.Success(memberList);
    }
    /**
     * 会员信息批量下载
     * auth: wangkail
     * time: 2021/4/28
     */
    @PostMapping("batch/member")
    public ResultVo batchMember(@RequestBody MemberQuery memberQuery){
        //验证登陆
        getCurrUser();
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            memberQuery.setOemCode(getRequestHeadParams("oemCode"));
        }
        //查询当前登陆账号所在组织
        OrgEntity orgEntity=orgService.queryOrgEntityByUserId(getCurrUserId());
        if(null == orgEntity){
            return ResultVo.Fail("登陆账号没有组织");
        }
        //获取组织的管理员账号
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        //只支持平台组织账号操作
        if (userEntity == null || userEntity.getPlatformType() != 1){
            return ResultVo.Fail("该账号不支持该操作");
        }
        //获取用户关系表
        Integer userClass=orgEntity.getOrgType();
        if(userClass==2||userClass==3){
            userClass=2;
        }else if(userClass==4){
            userClass=3;
        }else  if (userClass==5){
            userClass=4;
        }
        UserRelaEntity userRelaEntity=userRelaService.queryUserRelaEntityByUserId(userEntity.getId(),userClass);
        memberQuery.setTree(userRelaEntity.getUserTree());
        List<BatchMenberInfoExportVO> list= memberAccountService.batchMemberInfo(memberQuery);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("会员列表", "企业列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), BatchMenberInfoExportVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("批量下载企业信息导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 批量推送实名信息给国金
     * @return
     */
    @PostMapping("batch/pushAuth")
    public ResultVo pushAuth(){
        List<CompanyPushVo> list = memberAccountService.queryCompanyInfoByAuthPushState();
        if (list == null || list.size()<1){
            return ResultVo.Fail("无可推送数据");
        }
        rabbitTemplate.convertAndSend("companyAuthPush", list);
        return ResultVo.Success();
    }

    /**
     * 会员状态修改
     * @param data
     * id 用户id
     * status 0：禁用，1：解禁，2：注销
     * @return
     */
    @PostMapping("update/status")
    public ResultVo updateStatus(@RequestBody String data){
        Long id = getParameter(data, "id", Long.class);
        Integer status = getParameter(data, "status", Integer.class);
        if (id == null || status == null || status < 0 || status > 2) {
            return ResultVo.Fail("请求参数有误");
        }
        //验证登陆
        String useraccount = getCurrUseraccount();
        //分页查询
        MemberAccountEntity entity = memberAccountService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("当前操作账户不存在");
        }
        if (Objects.equals(MemberStateEnum.STATE_OFF.getValue(), entity.getStatus())) {
            return ResultVo.Fail("当前操作账户已经被注销");
        }
        if (Objects.equals(MemberStateEnum.STATE_UNACTIVE.getValue(), status) && Objects.equals(MemberStateEnum.STATE_UNACTIVE.getValue(), entity.getStatus())) {
            return ResultVo.Fail("当前操作账户已经被禁用");
        }
        if (Objects.equals(MemberStateEnum.STATE_ACTIVE.getValue(), status) && Objects.equals(MemberStateEnum.STATE_ACTIVE.getValue(), entity.getStatus())) {
            return ResultVo.Fail("当前操作账户已经被解禁");
        }
        //校验是否满足注销条件
        String result = canCancel(entity, status);
        if (StringUtils.isNotBlank(result)) {
            return ResultVo.Fail(result);
        }
        memberAccountService.updateStatus(id, status, useraccount);

        if(Objects.equals(MemberStateEnum.STATE_OFF.getValue(), status)) {
            entity.setStatus(status);
            entity.setMemberAccount(entity.getMemberAccount() + "_1");
            memberAccountService.updateMemberAccount(entity,useraccount);
        }
        //清除会员token
        String token =redisService.get(RedisKey.LOGIN_TOKEN_KEY+entity.getOemCode()+"_" + "userId_1_" + entity.getId());
        if(!org.apache.commons.lang3.StringUtils.isBlank(token)){
            redisService.delete(RedisKey.LOGIN_TOKEN_KEY +entity.getOemCode()+"_"+ token);
        }

        //添加会员变动表
        MemberAccountChangeEntity memberAccountChangeEntity=new MemberAccountChangeEntity();
        BeanUtils.copyProperties(entity,memberAccountChangeEntity);
        memberAccountChangeEntity.setMemberAccount(entity.getMemberAccount());
        memberAccountChangeEntity.setAccountId(entity.getId());
        memberAccountChangeEntity.setStatus(status);
        memberAccountChangeEntity.setId(null);
        memberAccountChangeEntity.setAddTime(new Date());
        memberAccountChangeEntity.setRemark("会员状态修改");
        memberAccountChangeService.insertSelective(memberAccountChangeEntity);
        return ResultVo.Success();
    }

    /**
     * 校验是否满足注销条件
     * @param entity
     * @param status
     * @return
     */
    public String canCancel(MemberAccountEntity entity, Integer status) {
        if (!Objects.equals(MemberStateEnum.STATE_OFF.getValue(), status)) {
            return null;
        }
        //查询未注销企业
        Integer size = memberCompanyService.countMemberCompany(entity.getId(), entity.getOemCode(), MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue());
        if (size > 0) {
            return "当前会员存在未注销企业，不允许注销";
        }
        UserCapitalAccountEntity userCapitalAccountEntity = userCapitalAccountService.queryByUserIdAndType(entity.getId(), MemberTypeEnum.MEMBER.getValue(), entity.getOemCode(),1);
        if (userCapitalAccountEntity != null && userCapitalAccountEntity.getTotalAmount() > 0) {
            return "当前会员账户总额不为0，不允许注销";
        }
        // 工商注册   0-待电子签字 1-待视频认证 2-审核中  3-待付款 4-待领证 5-已完成 6-已取消
        // 开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
        OrderEntity orderEntity = orderService.queryByMemberId(entity.getId(), entity.getOemCode(), OrderTypeEnum.REGISTER.getValue(), RegOrderStatusEnum.COMPLETED.getValue());
        if (orderEntity != null) {
            return "当前会员存在进行中的企业注册订单，不允许注销";
        }
        orderEntity = orderService.queryByMemberId(entity.getId(), entity.getOemCode(), OrderTypeEnum.INVOICE.getValue(), InvoiceOrderStatusEnum.SIGNED.getValue());
        if (orderEntity != null) {
            return "当前会员存在进行中的企业开票订单，不允许注销";
        }
        return null;
    }

    /**
     * 会员升级详情页
     * @param id
     * @return
     */
    @PostMapping("upgrade/detail")
    public ResultVo detail(@JsonParam Long id){
        //验证登陆
        getCurrUseraccount();
        //分页查询
        MemberAccountEntity entity;
        try {
            //校验会员信息
            entity = validatedMemberAccountEntity(id);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        MemberLevelEntity memberLevelEntity = memberLevelService.findById(entity.getMemberLevel());
        if (memberLevelEntity == null) {
            return ResultVo.Fail("操作会员等级不存在");
        }
        if (memberLevelEntity.getLevelNo() == 1){
            return ResultVo.Fail("vip用户不能升级");
        }
//        if (Objects.equals(MemberLevelEnum.DIAMOND.getValue(), memberLevelEntity.getLevelNo())) {
//            return ResultVo.Fail("当前操作账户已经是城市服务商了");
//        }
//        ProductEntity proEntity = productService.queryProductByProdType(ProductTypeEnum.DIAMOND.getValue(), entity.getOemCode());
//        if (proEntity == null) {
//            return ResultVo.Fail("会员升级产品不存在");
//        }
//        if (!Objects.equals(ProductStatusEnum.ON_SHELF.getValue(), proEntity.getStatus())) {
//            return ResultVo.Fail("会员升级产品未上架");
//        }
        List<OrderEntity> list = orderService.queryMemberLvUpOrder(entity.getId(), null);
        if (CollectionUtil.isNotEmpty(list)) {
            return ResultVo.Fail("已存在会员升级订单");
        }
        UserBankCardEntity cardEntity = new UserBankCardEntity();
        cardEntity.setOemCode(entity.getOemCode());
        cardEntity.setUserId(entity.getId());
        cardEntity.setUserType(UserTypeEnum.MEMBER.getValue());
        List<UserBankCardEntity> cards = userBankCardService.select(cardEntity);
        MemberAccountVO vo = new MemberAccountVO(entity, null, cards);
        try {
            vo.setLevelList(memberLevelService.getLevelUpList(entity,"asc"));
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        vo.setIdCardFront(ossService.getPrivateImgUrl(vo.getIdCardFront()));
        vo.setIdCardBack(ossService.getPrivateImgUrl(vo.getIdCardBack()));
        return ResultVo.Success(vo);
    }

    /**
     * 会员升级
     * @param dto
     * @return
     */
    @PostMapping("upgrade")
    public ResultVo upgrade(@RequestBody @Validated MemberUpgradeDTO dto, BindingResult result){
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //验证登陆
        String useraccount = getCurrUseraccount();
        //查询会员信息
        MemberAccountEntity entity;
        try {
            //校验会员信息
            entity = validatedMemberAccountEntity(dto.getId());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        MemberLevelEntity memberLevelEntity = memberLevelService.findById(entity.getMemberLevel());
        if (memberLevelEntity == null) {
            return ResultVo.Fail("操作会员等级不存在");
        }
//        if (Objects.equals(MemberLevelEnum.DIAMOND.getValue(), memberLevelEntity.getLevelNo())) {
//            return ResultVo.Fail("当前操作账户已经是城市服务商了");
//        }
        MemberLevelEntity targetLevel = memberLevelService.queryMemberLevel(entity.getOemCode(), dto.getLevelNo());
        if (targetLevel == null) {
            return ResultVo.Fail("需要升级会员等级不存在");
        }
        if (memberLevelEntity.getLevelNo() >= targetLevel.getLevelNo()) {
            return ResultVo.Fail("仅支持选择高于当前的会员等级！");
        }
        if ((entity.getExtendType() == null ||Objects.equals(entity.getExtendType(), ExtendTypeEnum.INDEPENDENT_CUSTOMER.getValue()))
                && !Objects.equals(targetLevel.getLevelNo(), MemberLevelEnum.BRONZE.getValue())) {
            //推广角色是散客时，升级成为的选项仅可选择VIP
            return ResultVo.Fail(ExtendTypeEnum.INDEPENDENT_CUSTOMER.getMessage() + "只能升级成" + MemberLevelEnum.BRONZE.getMessage());
        }
//        ProductEntity proEntity = productService.queryProductByProdType(ProductTypeEnum.DIAMOND.getValue(), entity.getOemCode());
//        if (proEntity == null) {
//            return ResultVo.Fail("会员升级产品不存在");
//        }
//        if (!Objects.equals(ProductStatusEnum.ON_SHELF.getValue(), proEntity.getStatus())) {
//            return ResultVo.Fail("会员升级产品未上架");
//        }
        List<OrderEntity> list = orderService.queryMemberLvUpOrder(entity.getId(), null);
        if (CollectionUtil.isNotEmpty(list)) {
            return ResultVo.Fail("当前有进行中的会员升级订单！");
        }
        //校验绑卡实名信息
        UserBankCardEntity userBankCardEntity = null;
        try {
            userBankCardEntity = validatedCard(entity, dto, useraccount);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        orderService.memberUpgrade(entity, targetLevel, userBankCardEntity, useraccount, dto.getRemark());
        return ResultVo.Success();
    }

    /**
     * 校验会员信息
     * @param id
     * @return
     * @throws BusinessException
     */
    public MemberAccountEntity validatedMemberAccountEntity(Long id) throws BusinessException{
        MemberAccountEntity entity = memberAccountService.findById(id);
        if (entity == null) {
            throw new BusinessException("当前操作账户不存在");
        }
        if (Objects.equals(MemberTypeEnum.EMPLOYEE.getValue(), entity.getMemberType())) {
            throw new BusinessException("员工不允许升级");
        }
        if (Objects.equals(MemberStateEnum.STATE_OFF.getValue(), entity.getStatus())) {
            throw new BusinessException("当前操作账户已经被注销");
        }
        OemEntity oemEntity=oemService.getOem(entity.getOemCode());
        if(oemEntity==null){
            throw new BusinessException("当前操作账户机构不存在");
        }
        if (Objects.equals(1, oemEntity.getIsBigCustomer())) {
            throw new BusinessException("该机构用户不允许升级");
        }
        return entity;
    }

    /**
     * 校验实名信息
     * @param entity
     * @param dto
     * @param useraccount
     * @return
     * @throws BusinessException
     */
    public UserBankCardEntity validatedCard(MemberAccountEntity entity, MemberUpgradeDTO dto, String useraccount) throws BusinessException {
        if (Objects.equals(MemberAuthStatusEnum.AUTH_SUCCESS.getValue(), entity.getAuthStatus())) {
            return null;
        }
        UserBankCardEntity cardEntity = new UserBankCardEntity();
        cardEntity.setOemCode(entity.getOemCode());
        cardEntity.setUserId(entity.getId());
        cardEntity.setUserType(UserTypeEnum.MEMBER.getValue());
        List<UserBankCardEntity> cards = userBankCardService.select(cardEntity);
        if (CollectionUtil.isNotEmpty(cards)) {
            return null;
        }
        if (StringUtils.isBlank(dto.getBankNumber())) {
            throw new BusinessException("银行卡号不能为空");
        }
        if (StringUtils.isBlank(dto.getIdCard())) {
            throw new BusinessException("身份证号不能为空");
        }
        if (StringUtils.isBlank(dto.getPhone())) {
            throw new BusinessException("预留手机号不能为空");
        }
        if (StringUtils.isBlank(dto.getUserName())) {
            throw new BusinessException("持卡人姓名不能为空");
        }
        if (StringUtils.isBlank(dto.getIdCardFront())) {
            throw new BusinessException("身份证正面照不能为空");
        }
        if (StringUtils.isBlank(dto.getIdCardBack())) {
            throw new BusinessException("身份证反面照不能为空");
        }
        if (StringUtils.isBlank(dto.getExpireDate())) {
            throw new BusinessException("身份证有效期不能为空");
        }
        if (StringUtils.isBlank(dto.getIdCardAddr())) {
            throw new BusinessException("身份证地址不能为空");
        }

        BankBinEntity bank = bankBinService.findByCardNo(dto.getBankNumber());
        if (bank == null || StringUtils.isBlank(bank.getBankName())) {
            throw new BusinessException("卡bin信息识别失败");
        }
        if (bank.getCardType() != 2){
            throw new BusinessException("只能绑定储蓄卡");
        }
        DictionaryEntity dict = dictionaryService.getByCode("itax.support.bank");
        if(null == dict){
            throw new BusinessException("系统未配置支持银行卡列表");
        }
        String bankCodes = dict.getDictValue();
        if(!bankCodes.contains(bank.getBankCode())){
            throw new BusinessException(MessageEnum.BANK_CARD_NOT_SUPPORT.getMessage());
        }
        bank.setBankCode(bank.getBankCode().substring(0,4));

        // 获取银行名称
        BankInfoEntity bankInfo = new BankInfoEntity();
        bankInfo.setBankCode(bank.getBankCode());
        bankInfo.setStatus(1);
        bankInfo = bankInfoService.selectOne(bankInfo);
        if (bankInfo == null) {
            throw new BusinessException("不支持的银行卡，请更换");
        }
        /**
         * 银行卡四要素验证
         */
        //读取要素认证相关配置 paramsType=6
        OemParamsEntity paramsEntity = oemParamsService.getParams(entity.getOemCode(), 6);
        if(null == paramsEntity){
            throw new BusinessException("未配置银行卡四要素相关信息！");
        }
        // agentNo
        String agentNo = paramsEntity.getAccount();
        // signKey
        String signKey = paramsEntity.getSecKey();
        // authUrl
        String authUrl = paramsEntity.getUrl();
        String authResult = AuthKeyUtils.auth4Key(agentNo,signKey,authUrl,dto.getUserName(),dto.getIdCard(),dto.getBankNumber(),dto.getPhone(),paramsEntity.getParamsValues());

        if(StringUtils.isBlank(authResult)){
            throw new BusinessException("银行卡四要素认证失败");
        }
        JSONObject resultObj = JSONObject.parseObject(authResult);
    
        if(!"00".equals(resultObj.getString("code"))){
            throw new BusinessException("银行卡四要素认证失败：" + resultObj.getString("msg"));
        }

        cardEntity = new UserBankCardEntity();
        cardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
        cardEntity.setUserType(UserTypeEnum.MEMBER.getValue());
        cardEntity.setStatus(CardStatusEnum.BIND.getValue());
        cardEntity.setUserId(entity.getId());
        cardEntity.setOemCode(entity.getOemCode());
        cardEntity.setPhone(dto.getPhone());
        cardEntity.setUserName(dto.getUserName());
        cardEntity.setBankNumber(dto.getBankNumber());
        cardEntity.setBankCode(bankInfo.getBankCode());
        cardEntity.setBankName(bankInfo.getBankName());
        cardEntity.setAddTime(new Date());
        cardEntity.setAddUser(useraccount);
        cardEntity.setIdCard(dto.getIdCard());
        entity.setIdCardBack(dto.getIdCardBack());
        entity.setIdCardFront(dto.getIdCardFront());
        entity.setExpireDate(dto.getExpireDate());
        entity.setIdCardAddr(dto.getIdCardAddr());
        return cardEntity;
    }

    /**
     * 会员政策列表
     */
    @PostMapping("memberPolicyList")
    public ResultVo memberPolicyList(@JsonParam String oemCodeBody){
        //验证登陆
        getCurrUser();
        List<MemberProfitsRulesEntity> list =new ArrayList<>();
        MemberProfitsRulesEntity entity=new MemberProfitsRulesEntity();
        String oemCode=getRequestHeadParams("oemCode");
        if(StringUtils.isNotBlank(oemCode)){
            entity.setOemCode(oemCode);
            list=memberProfitsRulesService.select(entity);
        }else{
            if(StringUtils.isNotBlank(oemCodeBody)){
                entity.setOemCode(oemCodeBody);
            }
            list=memberProfitsRulesService.select(entity);
        }
        List<MemberProfitsRulesVO> memberProfitsRulesList =new ArrayList<>();
        for (MemberProfitsRulesEntity memberProfitsRulesEntity:list) {
            MemberProfitsRulesVO memberProfitsRulesVO =new MemberProfitsRulesVO();
            OemEntity oemEntity=oemService.getOem(memberProfitsRulesEntity.getOemCode());
            if(oemEntity==null){
                continue;
            }
            BeanUtils.copyProperties(memberProfitsRulesEntity,memberProfitsRulesVO);
            memberProfitsRulesVO.setOemName(oemEntity.getOemName());
            memberProfitsRulesList.add(memberProfitsRulesVO);
        }
        return ResultVo.Success(memberProfitsRulesList);
    }

    /**
     * 会员政策编辑
     */
    @PostMapping("updateMemberPolicy")
    public ResultVo updateMemberPolicy(@RequestBody  @Valid MemberProfitsRulesPO memberProfitsRulesPO,BindingResult result){
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        //验证登陆
        getCurrUser();
        MemberProfitsRulesEntity memberProfitsRulesEntity =memberProfitsRulesService.updateMemberProfitsRulesEntity(memberProfitsRulesPO,getCurrUserId());
        return ResultVo.Success(memberProfitsRulesEntity);
    }


    /**
     * 根据订单ID查询注册名称
     */
    @PostMapping("queryRegisteredName")
    public ResultVo queryMemberCompany(@JsonParam Long id){
        //验证登陆
//        getCurrUser();
        OrderEntity orderEntity=orderService.findById(id);
        if(null == orderEntity){
            return ResultVo.Fail("订单不存在");
        }
        RegisterOrderEntity entity=new RegisterOrderEntity();
        entity.setOrderNo(orderEntity.getOrderNo());
        RegisterOrderEntity registerOrderEntity=registerOrderService.selectOne(entity);
        if(null == registerOrderEntity){
            return ResultVo.Fail("注册订单不存在");
        }
        return ResultVo.Success(registerOrderEntity.getRegisteredName());
    }

    /**
     * 设置员工数上限
     * @param id
     * @return
     */
    @PostMapping("employees/limit")
    public ResultVo setEmployeesLimit(@JsonParam Long id, @JsonParam Integer employeesLimit){
        if (id == null) {
            return ResultVo.Fail("操作会员主键不能为空");
        }
        if (employeesLimit < 0) {
            return ResultVo.Fail("员工数上限不能小于0");
        }
        if (employeesLimit > 100000) {
            return ResultVo.Fail("员工数上限不能大于100000");
        }
        //验证登陆
        String useraccount = getCurrUseraccount();
        //分页查询
        MemberAccountEntity entity = memberAccountService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("当前操作账户不存在");
        }
        if (Objects.equals(MemberTypeEnum.EMPLOYEE.getValue(), entity.getMemberType())) {
            return ResultVo.Fail("员工不允许设置员工数上限");
        }
        if (Objects.equals(MemberStateEnum.STATE_OFF.getValue(), entity.getStatus())) {
            return ResultVo.Fail("当前操作账户已经被注销");
        }
        MemberLevelEntity memberLevelEntity = memberLevelService.findById(entity.getMemberLevel());
        if (memberLevelEntity == null) {
            return ResultVo.Fail("操作会员等级不存在");
        }
        if (!Objects.equals(MemberLevelEnum.DIAMOND.getValue(), memberLevelEntity.getLevelNo())) {
            return ResultVo.Fail("当前操作账户不是城市服务商");
        }
        entity.setEmployeesLimit(employeesLimit);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(useraccount);
        memberAccountService.editByIdSelective(entity);
        return ResultVo.Success();
    }

    /**
     * 标记和取消标记海星会员
     * @param id
     * @param sign 0-普通 1-海星
     * @return
     */
    @PostMapping("signMember")
    public ResultVo signMember(@JsonParam Long id, @JsonParam Integer sign){
        if (id == null) {
            return ResultVo.Fail("操作会员主键不能为空");
        }
        if(sign==null){
            return ResultVo.Fail("会员标记不能为null");
        }
        //验证登陆
        getCurrUseraccount();
        //分页查询
        MemberAccountEntity entity = memberAccountService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("当前操作账户不存在");
        }
        //拷贝变动记录
        MemberAccountChangeEntity memberAccountChangeEntity=new MemberAccountChangeEntity();
        BeanUtils.copyProperties(entity,memberAccountChangeEntity);
        //修改标签
        entity.setSign(sign);
        entity.setUpdateUser(getCurrUseraccount());
        entity.setUpdateTime(new Date());
        memberAccountService.editByIdSelective(entity);
        //增加比会员变动记录
        memberAccountChangeEntity.setAccountId(entity.getId());
        memberAccountChangeEntity.setId(null);
        memberAccountChangeEntity.setSign(sign);
        memberAccountChangeService.insertSelective(memberAccountChangeEntity);
        return ResultVo.Success();
    }

    /**
     * 修改手机号
     * @return
     */
    @PostMapping("updatePhone")
    public ResultVo updatePhone(@RequestBody  @Valid UpdateMemberPhonePO po, BindingResult result){
        //验证登陆
        getCurrUseraccount();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //分页查询
        MemberAccountEntity entity = memberAccountService.findById(po.getId());
        if (entity == null) {
            return ResultVo.Fail("当前操作账户不存在");
        }
        if(po.getPhone().equals(entity.getMemberPhone())){
            return ResultVo.Fail("新手机号不能与原手机号重复！");
        }
        MemberAccountOemInfoVO memberAccountOemInfo = memberAccountService.queryMemberOemInfo(po.getId());
        if (memberAccountOemInfo == null){
            return ResultVo.Fail("oem机构配置错误");
        }
        if (memberAccountOemInfo.getParamsValue().equals("1")){
            return ResultVo.Fail("国金模式用户不支持修改手机号");
        }
        List<MemberAccountEntity> list = memberAccountService.queryMemberByPhoneAndOemCode(po.getPhone(),entity.getOemCode());
        if(list.size()>0){
            return ResultVo.Fail("新手机号已存在，不能重复！");
        }
        //会员修改手机号
        orderService.updateMemberPhone(entity,po,getCurrUseraccount());

        return ResultVo.Success();
    }

    /**
     * 查询手机号码修改记录
     */
    @PostMapping("queryMemberPhoneChange")
    public ResultVo queryMemberPhoneChange(@JsonParam Long id){
        //验证登陆
        getCurrUseraccount();
        List <MemberAccountChangeEntity> list =memberAccountChangeService.queryMemberPhoneChange(id);
        for (MemberAccountChangeEntity entity:list ) {
            try{
                entity.setFileUrl(ossService.getPrivateImgUrl(entity.getFileUrl()));
            }catch (Exception e){

            }
        }
        return ResultVo.Success(list);
    }

}
