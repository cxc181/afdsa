package com.yuqian.itax.admin.controller.oem;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemInvoiceCategoryRelaEntity;
import com.yuqian.itax.agent.entity.dto.OemSysConfigDTO;
import com.yuqian.itax.agent.entity.po.InvoiceInfoByOemPO;
import com.yuqian.itax.agent.entity.po.OemPO;
import com.yuqian.itax.agent.entity.query.OemQuery;
import com.yuqian.itax.agent.entity.vo.*;
import com.yuqian.itax.agent.service.*;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.InvoiceWayEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.entity.AgentProfitsRulesEntity;
import com.yuqian.itax.user.entity.MemberProfitsRulesEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.user.service.AgentProfitsRulesService;
import com.yuqian.itax.user.service.MemberProfitsRulesService;
import com.yuqian.itax.user.service.UserExtendService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@RestController
@RequestMapping("/oem")
public class OemController extends BaseController {

    @Autowired
    OemService oemService;
    @Autowired
    AgentProfitsRulesService agentProfitsRulesService;
    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    UserBankCardService userBankCardService;
    @Autowired
    UserExtendService userExtendService;
    @Autowired
    OssService ossService;
    @Autowired
    MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    ProductService productService;
    @Autowired
    InvoiceInfoByOemService invoiceInfoByOemService;
    @Autowired
    ProvinceService provinceService;
    @Autowired
    CityService cityService;
    @Autowired
    DistrictService districtService;
    @Autowired
    OemParkIndustryBlacklistRelaService oemParkIndustryBlacklistRelaService;
    @Autowired
    IndustryService industryService;
    @Autowired
    ParkService parkService;
    @Autowired
    OrderService orderSer;
    @Autowired
    InvoiceRecordService invoiceRecordService;
    @Autowired
    OemInvoiceCategoryRelaService oemInvoiceCategoryRelaService;
    @Autowired
    InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Autowired
    UserService userService;
    @Autowired
    OemAccessPartyService oemAccessPartyService;
    @Autowired
    OemParkRelaService oemParkRelaService;

    /**
     * OEM机构列表
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryOemList")
    public ResultVo queryOemList(@RequestBody OemQuery oemQuery){
        //验证登陆
        getCurrUser();
        UserEntity userEntity=userService.findById(getCurrUserId());
        if(userEntity.getPlatformType()!=null&&userEntity.getPlatformType()!=1){
            return ResultVo.Fail("不是平台账号不允许查看机构列表");
        }
        //分页查询
        PageInfo<OemListVO> list=oemService.queryOemPageInfo(oemQuery);
        List<OemListVO> voList=list.getList();
        List<OemListVO> listResult= new ArrayList<>();
        for(int i=0;i<voList.size();i++){
            OemListVO vo=voList.get(i);
            String logo=vo.getOemLogo();
            vo.setOemLogo(ossService.getPrivateImgUrl(logo));
            listResult.add(vo);
        }
        return ResultVo.Success(list);
    }

    /**
     * OEM机构列表(下拉使用)
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryOem")
    public ResultVo queryOem(@JsonParam String type, @JsonParam String status){
        //验证登陆
        getCurrUser();
        OemEntity entity=new OemEntity();
        if("2".equals(type)){
            entity.setIsCheckstand(1);
            entity.setOemStatus(1);
        }
        //entity.setOemStatus(1);
        List<OemEntity> list=oemService.select(entity);
        List<OemVO> voList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            OemVO oemVO=new OemVO();
            OemEntity oemEntity=list.get(i);
            if("1".equals(type) && (oemEntity.getOemCode()==null)||"".equals(oemEntity.getOemCode())){
                continue;
            }
            if(StringUtils.isNotBlank(status) && status.equals(oemEntity.getOemStatus().toString())){
                BeanUtils.copyProperties(oemEntity,oemVO);
                voList.add(oemVO);
            }else if(StringUtils.isBlank(status) && oemEntity.getOemStatus()!=0){
                BeanUtils.copyProperties(oemEntity,oemVO);
                voList.add(oemVO);
            }
        }
        return ResultVo.Success(voList);
    }
    /**
     * OEM机构状态变更
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/updateOemStatus")
    public ResultVo updateOemStatus(@JsonParam Long id,@JsonParam Integer status){
        if(null == id || null == status){
            return  ResultVo.Fail("参数不正确");
        }
        //验证登陆
        getCurrUser();
        try {
            OemEntity oemEntity=oemService.findById(id);

            if(status==0){
                //修改所有改园区下得账号为注销状态
                UserEntity userEntity=new UserEntity();
                userEntity.setOemCode(getRequestHeadParams("oemCode"));
                userEntity.setPlatformType(2);
                List<UserEntity> list= userService.select(userEntity);
                for (UserEntity entity:list) {
                    if(entity.getStatus()!=2){
                        return ResultVo.Fail("名下有未注销的账号，请注销以后再注销机构");
                    }
                }
                List<OemAccessPartyEntity> oemAccList = oemAccessPartyService.queryByOemCodeAndStatus(oemEntity.getOemCode(),1);
                if (oemAccList != null && oemAccList.size()>0){
                    return ResultVo.Fail("请先下架接入方再操作！");
                }
                //收银台被绑定的业务oem机构是否已下架
                if(oemEntity.getIsCheckstand()==1){
                    Example example = new Example(OemEntity.class);
                    example.createCriteria().andEqualTo("otherPayOemcode",oemEntity.getOemCode()).andNotEqualTo("oemStatus",0).andEqualTo("isOtherOemPay",1);
                    List<OemEntity> otherPayOemList = oemService.selectByExample(example);
                    if(otherPayOemList!=null && otherPayOemList.size()>0){
                        return ResultVo.Fail("存在未下架的业务oem机构，作为代收单主体的OEM机构不能下架");
                    }

                }
            }
            if(status==1){
                if(oemEntity.getIsCheckstand() != 1) {
                    //平台分润政策
                    AgentProfitsRulesEntity entity = new AgentProfitsRulesEntity();
                    entity.setOemCode(oemEntity.getOemCode());
                    entity.setAgentType(0);
                    List<AgentProfitsRulesEntity> agentProfitsRulesList = agentProfitsRulesService.select(entity);
                    if (agentProfitsRulesList.size() < 1) {
                        return ResultVo.Fail("请确认平台分润政策已配置后再进行上架操作。");
                    }
                    //会员政策
                    MemberProfitsRulesEntity entityMember = new MemberProfitsRulesEntity();
                    entityMember.setOemCode(oemEntity.getOemCode());
                    List<MemberProfitsRulesEntity> memberProfitsRulesEntityList = memberProfitsRulesService.select(entityMember);
                    if (memberProfitsRulesEntityList.size() < 1) {
                        return ResultVo.Fail("请确认会员政策已配置后再进行上架操作。");
                    }
                    //产品
                    ProductEntity productEntity = new ProductEntity();
                    productEntity.setOemCode(oemEntity.getOemCode());
                    List<ProductEntity> productEntityList = productService.select(productEntity);
                    if (productEntityList.size() < 1) {
                        return ResultVo.Fail("请确认产品已配置后再进行上架操作。");
                    }
                    //系统配置
                    if (oemEntity.getIsInviterCheck() == null || oemEntity.getIsOpenPromotion() == null || oemEntity.getCommissionServiceFeeRate() == null) {
                        return ResultVo.Fail("请确认机构系统配置已配置后再进行上架操作。");
                    }
                    //开票信息配置
                    InvoiceInfoByOemEntity invoiceInfoByOemEntity = invoiceInfoByOemService.queryInvoiceInfoByOemEntityByOemCode(entity.getOemCode());
                    if (invoiceInfoByOemEntity == null) {
                        return ResultVo.Fail("请确认机构开票信息配置已配置后再进行上架操作。");
                    }
                }
            }
            oemService.updateOem(id,status,getCurrUseraccount());
            //清除机构账号的token
            UserEntity userEntity=userService.getOemAccount(oemEntity.getOemCode());
            String oemCode = userEntity.getOemCode();
            if(StringUtils.isBlank(oemCode)){
                oemCode = "";
            }
            String token =redisService.get(RedisKey.LOGIN_TOKEN_KEY+oemCode+"_" + "userId_2_" + userEntity.getUsername());
            if(!StringUtils.isBlank(token)){
                redisService.delete(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
            }

            //设置缓存
            OemEntity entity = oemService.getOem(oemEntity.getOemCode());
            if(entity != null){
                redisService.set(RedisKey.OEM_CODE_KEY + entity.getOemCode(),entity);
            }
            return ResultVo.Success();
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }


    /**
     * OEM机构详情
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryOemDetail")
    public ResultVo queryOemDetail(@JsonParam Long id){
        //验证登陆
        getCurrUser();
        if(id == null){
            return ResultVo.Fail("参数不正确，id必须");
        }
        OemDetailVO oemDetailVO=oemService.getOemDteatailById(id);
        //设置账号
        UserEntity userEntity=userService.getOemAccount(oemDetailVO.getOemCode());
        if(null == userEntity){
            return ResultVo.Fail("机构管理员账号不存在！");
        }
        oemDetailVO.setUsername(userEntity.getUsername());
        String logo=oemDetailVO.getOemLogo();
        oemDetailVO.setOemLogo(logo);
        oemDetailVO.setDLogo(ossService.getPrivateImgUrl(logo));

        UserExtendEntity userExtendEntity=userExtendService.getUserExtendByUserId(userEntity.getId());
        oemDetailVO.setPhone(userExtendEntity.getPhone());
        //设置银行卡信息
        UserBankCardEntity userBankCardEntity=userBankCardService.getBankCardInfoByUserIdAndUserType(userEntity.getId(),2,userEntity.getOemCode());
        // 获取密钥和请求地址
        OemSysConfigDetailVO vo = oemService.queryOemSysConfigDetail(id);
        if (vo!= null && vo.getIsOpenChannel() != null ){
            oemDetailVO.setIsOpenChannel(vo.getIsOpenChannel());
            if (vo.getIsOpenChannel() == 1){
                oemDetailVO.setChannelUrl(vo.getChannelUrl());
                oemDetailVO.setSecKey(vo.getSecKey());
            }
        }
        if(userBankCardEntity!=null){
            oemDetailVO.setIsBank(1);
            oemDetailVO.setBankUserName(userBankCardEntity.getUserName());
            oemDetailVO.setIdCard(userBankCardEntity.getIdCard());
            oemDetailVO.setBankNumber(userBankCardEntity.getBankNumber());
            oemDetailVO.setBankName(userBankCardEntity.getBankName());
            oemDetailVO.setBankPhone(userBankCardEntity.getPhone());
        }
        if (StringUtil.isNotBlank(oemDetailVO.getOfficialSealImg())){
            oemDetailVO.setHttpOfficialSealImg(ossService.getPrivateImgUrl(oemDetailVO.getOfficialSealImg()));
        }
        return ResultVo.Success(oemDetailVO);
    }
    /**
     * OEM机构名称
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryOemName")
    public ResultVo queryOemName(@JsonParam String oemCode){
        if(null == oemCode && "".equals(oemCode)){
            return ResultVo.Fail("参数不正确，机构编码必须");
        }
        OemEntity entity=oemService.getOem(oemCode);
        Map<String ,Object> map=new HashMap();
        if(entity!=null){
            map.put("name",entity.getOemName());
            map.put("logo",entity.getOemLogo());
            map.put("tel",entity.getCustomerServiceTel());
        }
        return ResultVo.Success(map);
    }

    /**
     * OEM机构政策详情
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryOemPolicyDetail")
    public ResultVo queryOemPolicyDetail(@JsonParam Long id){
        OemPolicyVO oemPolicyVO=new OemPolicyVO();
        OemEntity oemEntity=oemService.findById(id);
        if(null == oemEntity){
            return ResultVo.Fail("机构不存在");
        }
        AgentProfitsRulesEntity agentProfitsRulesEntity=agentProfitsRulesService.queryAgentProfitsRulesEntityByOemCodeAndAgentType(oemEntity.getOemCode(),0);
        if(null == agentProfitsRulesEntity){
            return ResultVo.Fail("分润规则不存在");
        }
        oemPolicyVO.setCancelCompanyFee(agentProfitsRulesEntity.getCancelCompanyFee());
        oemPolicyVO.setInvoiceFee(agentProfitsRulesEntity.getInvoiceFee());
        oemPolicyVO.setRegisterFee(agentProfitsRulesEntity.getRegisterFee());
        oemPolicyVO.setMembershipFee(agentProfitsRulesEntity.getMembershipFee());
        oemPolicyVO.setOemCode(oemEntity.getOemCode());
        oemPolicyVO.setOemName(oemEntity.getOemName());
        oemPolicyVO.setSettlementCycle(oemEntity.getSettlementCycle());
        oemPolicyVO.setSettlementType(oemEntity.getSettlementType());

        return ResultVo.Success(oemPolicyVO);

    }

    /**
     * OEM机构政策配置
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/updateOemPolicy")
    public ResultVo updateOemPolicy(@RequestBody OemPO oemPO){
        //验证登陆
        getCurrUser();
        //oemPO.setOemCode(getRequestHeadParams("oemCode"));
        //更新OEM机构表
        OemEntity oemEntity=oemService.updateOemPolicy(oemPO,getCurrUseraccount());
        //配置代理商分润表
        AgentProfitsRulesEntity agentProfitsRulesEntity=agentProfitsRulesService.queryAgentProfitsRulesEntityByOemCodeAndAgentType(oemEntity.getOemCode(),0);
        if(null == agentProfitsRulesEntity){
            agentProfitsRulesEntity=new AgentProfitsRulesEntity();
            agentProfitsRulesEntity.setOemCode(oemPO.getOemCode());
            agentProfitsRulesEntity.setAgentType(0);
            agentProfitsRulesEntity.setMembershipFee(oemPO.getMembershipFee());
            agentProfitsRulesEntity.setRegisterFee(oemPO.getRegisterfee());
            agentProfitsRulesEntity.setInvoiceFee(oemPO.getInvoicefee());
            agentProfitsRulesEntity.setCancelCompanyFee(oemPO.getCancelCompanyFee());
            agentProfitsRulesEntity.setStatus(1);
            agentProfitsRulesEntity.setAddTime(new Date());
            agentProfitsRulesEntity.setAddUser(getCurrUseraccount());
            agentProfitsRulesService.insertSelective(agentProfitsRulesEntity);
        }else{
            agentProfitsRulesEntity.setMembershipFee(oemPO.getMembershipFee());
            agentProfitsRulesEntity.setRegisterFee(oemPO.getRegisterfee());
            agentProfitsRulesEntity.setInvoiceFee(oemPO.getInvoicefee());
            agentProfitsRulesEntity.setCancelCompanyFee(oemPO.getCancelCompanyFee());
            agentProfitsRulesEntity.setUpdateTime(new Date());
            agentProfitsRulesEntity.setUpdateUser(getCurrUseraccount());
            agentProfitsRulesService.editByIdSelective(agentProfitsRulesEntity);
        }


        return ResultVo.Success();
    }


    /**
     * OEM机构新增
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/addOem")
    public ResultVo addOem(@RequestBody @Validated OemPO oemPO, BindingResult result)  {
        if(result.hasErrors()){
            return  ResultVo.Fail(result);
        }
        //带登陆验证
        getCurrUser();
        if(oemPO.getIsCheckstand()!=null && oemPO.getIsCheckstand() == 1 && StringUtil.isBlank(oemPO.getOemAppid())){
            return ResultVo.Fail("收银台APPID不能为空");
        }
        if(oemPO.getIsOtherOemPay()!=null && oemPO.getIsOtherOemPay() == 1){
            if(StringUtil.isBlank(oemPO.getOtherPayOemcode())) {
                return ResultVo.Fail("收单OEM机构不能为空");
            }else{
                OemEntity otherOem = oemService.getOem(oemPO.getOtherPayOemcode());
                if(otherOem == null || otherOem.getOemStatus() != 1){
                    return ResultVo.Fail("收单OEM机构不存在或为非上架状态机构");
                }
            }
        }
        try {
            //新增账号
            userCapitalAccountService.addOemCapitalAccount(oemPO,getCurrUseraccount());
            //保存国金助手参数
            oemService.setChannelParams(oemPO,oemPO.getOemCode(),getCurrUser().getUseraccount());
            //设置缓存
            OemEntity entity = oemService.getOem(oemPO.getOemCode());
            if(entity != null){
                redisService.set(RedisKey.OEM_CODE_KEY + entity.getOemCode(),entity);
            }
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * OEM机构编辑
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/updateOem")
    public ResultVo updateOem(@RequestBody OemPO oemPO)  {
        //带登陆验证
        getCurrUser();
        //oemPO.setOemCode(getRequestHeadParams("oemCode"));
        if(oemPO.getIsCheckstand()!=null && oemPO.getIsCheckstand() == 1 && StringUtil.isBlank(oemPO.getOemAppid())){
            return ResultVo.Fail("收银台APPID不能为空");
        }
        if(oemPO.getIsOtherOemPay()!=null && oemPO.getIsOtherOemPay() == 1){
            if(StringUtil.isBlank(oemPO.getOtherPayOemcode())) {
                return ResultVo.Fail("收单OEM机构不能为空");
            }else{
                OemEntity otherOem = oemService.getOem(oemPO.getOtherPayOemcode());
                if(otherOem == null || otherOem.getOemStatus() != 1){
                    return ResultVo.Fail("收单OEM机构不存在或为非上架状态机构");
                }
            }
        }else{
            oemPO.setIsOtherOemPay(0);
            oemPO.setOtherPayOemcode(null);
        }
        try{
            //修改用户
            UserEntity userEntity=userService.updateOemtEntityById(oemPO,getCurrUseraccount());

            if(oemPO.getIsbank()==1){
                UserBankCardEntity userBankCardEntity =new UserBankCardEntity();
                BeanUtils.copyProperties(oemPO, userBankCardEntity);
                userBankCardEntity.setUserName(oemPO.getBankUserName());
                userBankCardEntity.setPhone(oemPO.getBankPhone());
                userBankCardEntity.setUserId(userEntity.getId());
                //修改银行卡
                userBankCardService.updateBankCardEntity(userBankCardEntity,getCurrUseraccount(),userEntity.getId());
            }

            //设置缓存
            OemEntity entity = oemService.getOem(oemPO.getOemCode());
            if(entity != null){
                redisService.set(RedisKey.OEM_CODE_KEY + entity.getOemCode(),entity);
            }
        }catch (BusinessException e){
            return  ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * OEM机构系统配置详情
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryOemSysConfigDetail")
    public ResultVo queryOemSysConfigDetail(@JsonParam Long id){
        if(null == id){
            return ResultVo.Fail("机构id不能为空");
        }
        OemSysConfigDetailVO vo = oemService.queryOemSysConfigDetail(id);
        return ResultVo.Success(vo);

    }
    /**
     * 系统配置
     * @param dto
     * @return
     */
    @PostMapping("/sysConfig")
    public ResultVo sysConfig(@RequestBody @Validated OemSysConfigDTO dto, BindingResult result)  {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        //带登陆验证
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OemEntity oemEntity = new OemEntity();
        oemEntity.setId(dto.getId());
        oemEntity.setOemCode(userEntity.getOemCode());
        oemEntity = oemService.selectOne(oemEntity);
        if (oemEntity == null) {
            return ResultVo.Fail("机构不存在");
        }
        // 当工单审核方选OEM机构客服提交时，需校验该OEM机构账号是否已创建客服坐席账号，否则不能提交
        if (dto.getWorkAuditWay() == 2 ){
            List<UserEntity> userList = userService.queryAccountByOemCodeAndAccountTypeAndPlatformType(oemEntity.getOemCode(),2,2,null);
            if (userList == null || userList.size()<1){
                return ResultVo.Fail("该oem机构下未配置客服坐席账号");
            }
        }
        // 当工单审核方选平台机构客服提交时，需校验该平台是否已创建客服坐席账号，否则不能提交
        if (dto.getWorkAuditWay() == 1 ){
            List<UserEntity> userList = userService.queryAccountByOemCodeAndAccountTypeAndPlatformType(null,2,1,null);
            if (userList == null || userList.size()<1){
                return ResultVo.Fail("平台未配置客服坐席账号");
            }
        }
        oemEntity.setWorkAuditWay(dto.getWorkAuditWay());
        oemEntity.setIsInviterCheck(dto.getIsInviterCheck());
        oemEntity.setIsOpenPromotion(dto.getIsOpenPromotion());
        oemEntity.setUpdateTime(new Date());
        oemEntity.setUpdateUser(userEntity.getUsername());
//        oemEntity.setEmployeesLimit(dto.getEmployeesLimit());
//        oemEntity.setDiamondCommissionServiceFeeRate(dto.getDiamondCommissionServiceFeeRate());
        oemEntity.setCommissionServiceFeeRate(dto.getCommissionServiceFeeRate());
        //保存接入国金助手参数
      //oemService.setChannelParams(dto,oemEntity.getOemCode(),userEntity.getUsername());
        oemService.editByIdSelective(oemEntity);

        //保存国金助手参数
        //oemService.setChannelParams(dto,oemEntity.getOemCode(),userEntity.getUsername());
        return ResultVo.Success();
    }

    /**
     * 开票信息配置详情
     * @return
     */
    @PostMapping("/invoiceInfoByOemDetail")
    public ResultVo invoiceInfoByOemDetail(@JsonParam String oemCode)  {
        //带登陆验证
        CurrUser currUser = getCurrUser();
        OemEntity oemEntity=oemService.getOem(oemCode);
        if(oemEntity==null){
            return ResultVo.Fail("机构不存在");
        }
        String oemName=oemEntity.getOemName();
        InvoiceInfoByOemEntity entity =invoiceInfoByOemService.queryInvoiceInfoByOemEntityByOemCode(oemCode);
        if(entity==null){
            InvoiceInfoByOemVO vo=new InvoiceInfoByOemVO();
            vo.setOemName(oemName);
            return ResultVo.Success(vo);
        }
        InvoiceInfoByOemVO invoiceInfoByOemVO=new InvoiceInfoByOemVO();
        BeanUtils.copyProperties(entity,invoiceInfoByOemVO);
        invoiceInfoByOemVO.setOemName(oemName);
        //获取开票类目
        OemInvoiceCategoryRelaEntity oemInvoiceCategoryRelaEntity=new OemInvoiceCategoryRelaEntity();
        oemInvoiceCategoryRelaEntity.setOemCode(entity.getOemCode());
        List<OemInvoiceCategoryRelaEntity> list=oemInvoiceCategoryRelaService.select(oemInvoiceCategoryRelaEntity);
        if(CollectionUtil.isNotEmpty(list)){
            invoiceInfoByOemVO.setCategoryList(list);
        }
        return ResultVo.Success(invoiceInfoByOemVO);
    }

    /**
     * 开票信息配置保存
     * @return
     */
    @PostMapping("/updateInvoiceInfoByOem")
    public ResultVo updateInvoiceInfoByOem(@RequestBody  @Validated InvoiceInfoByOemPO invoiceInfoByOemPO, BindingResult result)  {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        //带登陆验证
        CurrUser currUser = getCurrUser();
        InvoiceInfoByOemEntity invoiceInfoByOemEntity=invoiceInfoByOemService.queryInvoiceInfoByOemEntityByOemCode(invoiceInfoByOemPO.getOemCode());
        String msg = setHostingInfo(invoiceInfoByOemPO,invoiceInfoByOemEntity);
        if(StringUtils.isNotBlank(msg)){
            return ResultVo.Fail(msg);
        }
        if(invoiceInfoByOemEntity==null){
            invoiceInfoByOemEntity=new InvoiceInfoByOemEntity();
            BeanUtils.copyProperties(invoiceInfoByOemPO,invoiceInfoByOemEntity);
            if (StringUtil.isNotBlank(invoiceInfoByOemPO.getProvinceCode())){
                invoiceInfoByOemEntity.setProvinceName(provinceService.getByCode(invoiceInfoByOemPO.getProvinceCode()).getName());
                invoiceInfoByOemEntity.setCityName(cityService.getByCode(invoiceInfoByOemPO.getCityCode()).getName());
                invoiceInfoByOemEntity.setDistrictName(districtService.getByCode(invoiceInfoByOemPO.getDistrictCode()).getName());
            }
            invoiceInfoByOemEntity.setStatus(1);
            if (StringUtil.isNotBlank(invoiceInfoByOemPO.getEmail())){
                invoiceInfoByOemEntity.setEmail(invoiceInfoByOemPO.getEmail());
            }
            invoiceInfoByOemEntity.setAddTime(new Date());
            invoiceInfoByOemEntity.setAddUser(getCurrUseraccount());
            invoiceInfoByOemService.insertSelective(invoiceInfoByOemEntity);
        }else{
            BeanUtils.copyProperties(invoiceInfoByOemPO,invoiceInfoByOemEntity);
            if (StringUtil.isNotBlank(invoiceInfoByOemPO.getProvinceCode())){
                invoiceInfoByOemEntity.setProvinceName(provinceService.getByCode(invoiceInfoByOemPO.getProvinceCode()).getName());
                invoiceInfoByOemEntity.setCityName(cityService.getByCode(invoiceInfoByOemPO.getCityCode()).getName());
                invoiceInfoByOemEntity.setDistrictName(districtService.getByCode(invoiceInfoByOemPO.getDistrictCode()).getName());
            }
            invoiceInfoByOemEntity.setUpdateTime(new Date());
            invoiceInfoByOemEntity.setUpdateUser(getCurrUseraccount());
            invoiceInfoByOemService.editByIdSelective(invoiceInfoByOemEntity);
        }
        //批量增加oem机构开票类目关系表
        oemInvoiceCategoryRelaService.delByOemCode(invoiceInfoByOemEntity.getOemCode());
        List<com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO> list=new ArrayList<>();
        List<InvoiceCategoryBaseStringAgentVO> categoryList =new ArrayList<>();
        for (InvoiceCategoryBaseStringAgentVO vo:invoiceInfoByOemPO.getCategoryList()) {
            InvoiceCategoryBaseEntity entity=new InvoiceCategoryBaseEntity();
            entity.setTaxClassificationAbbreviation(vo.getTaxClassificationAbbreviation());
            entity.setGoodsName(vo.getGoodsName());
            if("*".equals(vo.getGoodsName())){
                return ResultVo.Fail("不能选择带*的商品名称");
            }
            List<InvoiceCategoryBaseEntity> invoiceCategoryBaseEntities=invoiceCategoryBaseService.select(entity);
            if(CollectionUtil.isEmpty(invoiceCategoryBaseEntities)){
                throw  new BusinessException(vo.getTaxClassificationAbbreviation()+"*"+vo.getGoodsName()+"类目不存在");
            }
            vo.setId(invoiceCategoryBaseEntities.get(0).getId());
            InvoiceCategoryBaseStringAgentVO invoiceCategoryBaseStringAgentVO= new  InvoiceCategoryBaseStringAgentVO();
            BeanUtils.copyProperties(invoiceCategoryBaseEntities.get(0),invoiceCategoryBaseStringAgentVO);
            categoryList.add(invoiceCategoryBaseStringAgentVO);
        }
        invoiceInfoByOemEntity.setAddUser(getCurrUseraccount());
        invoiceInfoByOemEntity.setAddTime(new Date());
        oemInvoiceCategoryRelaService.addBatch(invoiceInfoByOemEntity,categoryList);

        return ResultVo.Success();
    }

    /**
     * 设置托管信息
     * @param invoiceInfoByOemPO
     * @return
     */
    private String setHostingInfo(InvoiceInfoByOemPO invoiceInfoByOemPO,InvoiceInfoByOemEntity invoiceInfoByOemEntity){
        //已托管
        if(invoiceInfoByOemPO.getHostingStatus().intValue() == 1){
            if(invoiceInfoByOemPO.getTaxDiscType() == null){
                return "税务判断类型不能为空";
            }
            if(StringUtils.isBlank(invoiceInfoByOemPO.getDistrictCode())){
                return "税务盘编码不能为空";
            }
            if(invoiceInfoByOemPO.getFaceAmountType() == null){
                return "发票面额不能为空";
            }
            if(invoiceInfoByOemPO.getFaceAmountType() == null){
                invoiceInfoByOemPO.setFaceAmount(0L);
            }else {
                switch (invoiceInfoByOemPO.getFaceAmountType()) {
                    case 1:
                        invoiceInfoByOemPO.setFaceAmount(1000000L);
                        break;
                    case 2:
                        invoiceInfoByOemPO.setFaceAmount(10000000L);
                        break;
                    case 3:
                        invoiceInfoByOemPO.setFaceAmount(100000000L);
                        break;
                    default:
                        invoiceInfoByOemPO.setFaceAmount(0L);
                        break;
                }
            }
            //调用百旺领用存接口校验
            Map<String,String> oemParams = invoiceRecordService.getBWInvoiceParams(invoiceInfoByOemPO.getOemCode());
            //随便传一个发票类型
            Map<String,Object> map=invoiceRecordService.queryInventory(oemParams,invoiceInfoByOemPO.getEin(),invoiceInfoByOemPO.getTaxDiscCode(), InvoiceWayEnum.ELECTRON.getValue());
            if(!map.containsKey("totalSurplusNo")){
                return "税务盘信息配置错误";
            }
        }else{ //未托管
            if(invoiceInfoByOemEntity!=null && invoiceInfoByOemEntity.getHostingStatus().intValue()==1){ //判断oem机构之前是否已托管
                //判断是否存在出票中的消费订单
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderType(OrderTypeEnum.CONSUMPTION_INVOICE.getValue());
                orderEntity.setOrderStatus(1);
                orderEntity.setOemCode(invoiceInfoByOemPO.getOemCode());
                List list = orderSer.select(orderEntity);
                if(list!=null && list.size()>0){
                    return "存在出票中的订单，不能取消托管";
                }
            }
        }
        return "";
    }
    /**
     * 查询行业例外规则
     * @param oemCode
     * @param parkId
     * @return
     */
    @PostMapping("industry/black/list")
    public ResultVo industryBlackList(@JsonParam String oemCode, @JsonParam Long parkId) {
        getCurrUser();
        if (parkId == null) {
            return ResultVo.Fail("园区id不能为空");
        }
        return ResultVo.Success(industryService.queryBlackListByParkId(oemCode, parkId));
    }

    /**
     * 根据园区id和oem机构编码获取协议模板信息
     * @param oemCode
     * @param parkId
     * @return
     */
    @PostMapping("/getAgreementTemplateByParkOem")
    public ResultVo getAgreementTemplateByParkOem(@JsonParam String oemCode, @JsonParam Long parkId){
        if (StringUtil.isEmpty(oemCode)){
            return ResultVo.Fail("oem机构编码不能为空");
        }
        if (parkId == null){
            return ResultVo.Fail("园区id不能为空");
        }
        AgreementTemplateInfoVO vo = oemParkRelaService.getAgreementTemplateByOemCodeAndParkId(oemCode,parkId);
        return ResultVo.Success(vo);
    }

//    /**
//     * 保存行业例外规则
//     * @param dto
//     * @return
//     */
//    @PostMapping("industry/black/save")
//    public ResultVo saveIndustryBlack(@RequestBody @Validated OemParkIndustryBlacklistRelaDTO dto, BindingResult result) {
//        if(result.hasErrors()){
//            return ResultVo.Fail(result);
//        }
//        CurrUser currUser = getCurrUser();
//        UserEntity userEntity = userService.findById(currUser.getUserId());
//        if (userEntity == null) {
//            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
//        }
//        if (StringUtils.isNotBlank(userEntity.getOemCode()) && !StringUtils.equals(userEntity.getOemCode(), dto.getOemCode())) {
//            return ResultVo.Fail("操作机构不归属于当前登录用户");
//        }
//        if(oemService.getOem(dto.getOemCode()) == null) {
//            return ResultVo.Fail("机构不存在");
//        }
//        ParkEntity parkEntity = parkService.findById(dto.getParkId());
//        if(parkEntity == null) {
//            return ResultVo.Fail("园区不存在");
//        }
//        List<IndustryEntity> list = industryService.selectByIndustryIds(dto.getParkId(), dto.getIds());
//        if (CollectionUtil.isEmpty(list)) {
//            return ResultVo.Fail("行业不存在");
//        }
//        List<Long> ids = list.stream().map(IndustryEntity::getId).collect(Collectors.toList());
//        oemParkIndustryBlacklistRelaService.addBatch(dto.getOemCode(), parkEntity.getParkCode(), dto.getParkId(), ids, currUser.getUseraccount());
//        return ResultVo.Success();
//    }

}
