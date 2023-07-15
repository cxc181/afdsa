package com.yuqian.itax.admin.controller.user;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.vo.UserCapitalAccountDetailVO;
import com.yuqian.itax.capital.entity.vo.WithdrawDetailVO;
import com.yuqian.itax.capital.enums.BankCardTypeEnum;
import com.yuqian.itax.capital.enums.CardStatusEnum;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.capital.service.UserCapitalChangeRecordService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawWaterQuery;
import com.yuqian.itax.corporateaccount.service.CorporateAccountWithdrawalOrderService;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawWaterVO;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.entity.PaymentVo;
import com.yuqian.itax.pay.entity.query.PayWaterQuery;
import com.yuqian.itax.pay.entity.query.WthdrawQuery;
import com.yuqian.itax.pay.entity.vo.*;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.enums.RefundWaterStatusEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.profits.entity.query.ProfitsDetailQuery;
import com.yuqian.itax.profits.entity.vo.MyProfitsDetailVO;
import com.yuqian.itax.profits.entity.vo.ProfitsDetailVO;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.system.service.BankInfoService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.user.enums.AuditStateEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.UserExtendService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.ImageDownloadUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * 提现controller
 * @author HZ
 * @date 2019/12/11
 */
@Slf4j
@RestController
@RequestMapping("withdraw")
public class WithdrawController extends BaseController {
    @Autowired
    UserCapitalChangeRecordService userCapitalChangeRecordService;
    @Autowired
    PayWaterService payWaterService;
    @Autowired
    ProfitsDetailService profitsDetailService;
    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    UserBankCardService userBankCardService;
    @Autowired
    BankInfoService bankInfoService;
    @Autowired
    SmsService smsService;
    @Autowired
    OrderService orderService;

    @Autowired
    private UserExtendService userExtendService;

    @Autowired
    private OssService ossService;

    @Autowired
    private CorporateAccountWithdrawalOrderService corporateAccountWithdrawalOrderService;

    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private RegisterOrderService registerOrderService;
    /**
     * 提现记录列表（分页）
     * @author HZ
     * @date 2019/12/11
     */
    @PostMapping("/withdrawPageInfo")
    public ResultVo withdrawPageInfo(@RequestBody  WthdrawQuery wthdrawQuery){
        //带登陆验证
        getCurrUser();

        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            wthdrawQuery.setOemCode(oemCode);
        }
        if(wthdrawQuery.getType()!=null&&wthdrawQuery.getType()==1){
            wthdrawQuery.setMemberId(getCurrUserId());
            wthdrawQuery.setUserType(2);
        }

        wthdrawQuery.setTree(getOrgTree());

        PageInfo<WithdrawVO> userPageInfo=payWaterService.withdrawPageInfo(wthdrawQuery);

        return ResultVo.Success(userPageInfo);
    }

    /**
     * 提现记录导出
     * @author HZ
     * @date 2019/12/11
     */
    @PostMapping("/exportWithdrawAllList")
    public ResultVo exportWithdrawAllList(@RequestBody  WthdrawQuery wthdrawQuery){
        //带登陆验证
        getCurrUser();
        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            wthdrawQuery.setOemCode(oemCode);
        }
        List payWaterList=new ArrayList();
        wthdrawQuery.setTree(getOrgTree());
        if(wthdrawQuery.getType()!=null&&wthdrawQuery.getType()==1){
            wthdrawQuery.setMemberId(getCurrUserId());
            wthdrawQuery.setUserType(2);
        }
        payWaterList=payWaterService.withdrawList(wthdrawQuery);

        if (CollectionUtil.isEmpty(payWaterList)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            if(wthdrawQuery.getType()!=null&&wthdrawQuery.getType()==1&&wthdrawQuery.getPayWaterType()!=null&&wthdrawQuery.getPayWaterType()==2){
                String oldOb = JSON.toJSONString(payWaterList);
                List<MyWithdrawVO> list=JSON.parseArray(oldOb, MyWithdrawVO.class);
                exportExcel("我的提现记录", "我的提现记录", MyWithdrawVO.class, list);
            }else if (wthdrawQuery.getType()!=null&&wthdrawQuery.getType()==0&&wthdrawQuery.getPayWaterType()!=null&&wthdrawQuery.getPayWaterType()==2){
                exportExcel("提现记录", "提现记录", WithdrawVO.class, payWaterList);
            }else if (wthdrawQuery.getType()!=null&&wthdrawQuery.getType()==1&&wthdrawQuery.getPayWaterType()!=null&&wthdrawQuery.getPayWaterType()==1){
                String oldOb = JSON.toJSONString(payWaterList);
                List<MyRechargeVO> list=JSON.parseArray(oldOb, MyRechargeVO.class);
                exportExcel("我的充值记录", "我的充值记录", MyRechargeVO.class, list);
            }else if (wthdrawQuery.getType()!=null&&wthdrawQuery.getType()==0&&wthdrawQuery.getPayWaterType()!=null&&wthdrawQuery.getPayWaterType()==1){
                String oldOb = JSON.toJSONString(payWaterList);
                List<RechargeVO> list=JSON.parseArray(oldOb, RechargeVO.class);
                exportExcel("充值记录", "充值记录", RechargeVO.class, list);
            }
        } catch (Exception e) {
            log.error("提现/充值记录记录导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
        return ResultVo.Success();
    }



    /**
     * 分润记录列表（分页）
     * @author HZ
     * @date 2019/12/11
     */
    @PostMapping("/fenRunPageInfo")
    public ResultVo fenRunPageInfo(@RequestBody ProfitsDetailQuery profitsDetailQuery){
        //带登陆验证
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            profitsDetailQuery.setOemCode(getRequestHeadParams("oemCode"));
        }
        if(profitsDetailQuery.getType()!=null&&profitsDetailQuery.getType()==1){
            profitsDetailQuery.setUserId(getCurrUserId());
            profitsDetailQuery.setUserType(2);
        }
       profitsDetailQuery.setTree(getOrgTree());
        //查询分润记录表数据
        PageInfo<ProfitsDetailVO> profitsDetailVO=profitsDetailService.profitsDetailPageInfo(profitsDetailQuery);

        return ResultVo.Success(profitsDetailVO);
    }
    /**
     * 分润记录列表导出
     * @author HZ
     * @date 2019/12/11
     */
    @PostMapping("/exportFenRunAllPageInfo")
    public ResultVo exportFenRunAllPageInfo( @RequestBody ProfitsDetailQuery profitsDetailQuery){
        //带登陆验证
        getCurrUser();
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            profitsDetailQuery.setOemCode(getRequestHeadParams("oemCode"));
        }
        if(profitsDetailQuery.getType()!=null&&profitsDetailQuery.getType()==1){
            profitsDetailQuery.setUserId(getCurrUserId());
            profitsDetailQuery.setUserType(2);
        }
        profitsDetailQuery.setTree(getOrgTree());

        //查询分润记录表数据
        List<ProfitsDetailVO> ProfitsDetailVOList=profitsDetailService.profitsDetailList(profitsDetailQuery);
        if (CollectionUtil.isEmpty(ProfitsDetailVOList)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            if(profitsDetailQuery.getType()!=null&&profitsDetailQuery.getType()==1) {
                String oldOb = JSON.toJSONString(ProfitsDetailVOList);
                List<MyProfitsDetailVO> list=JSON.parseArray(oldOb, MyProfitsDetailVO.class);
                exportExcel("我的分润记录", "我的分润记录", MyProfitsDetailVO.class, list);
            }else{
                exportExcel("分润记录", "分润记录", ProfitsDetailVO.class, ProfitsDetailVOList);
            }
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("分润记录导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 支付流水列表（分页）
     * @author HZ
     * @date 2019/12/11
     */
    @PostMapping("/payWaterPageInfo")
    public ResultVo payWaterPageInfo(@RequestBody  PayWaterQuery payWaterQuery){

        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            payWaterQuery.setOemCode(oemCode);
        }
        payWaterQuery.setTree(getOrgTree());
        //查询支付流水记录表数据
        PageInfo<PaywaterVO> payWaterEntityPageInfo=payWaterService.payWaterPageInfo(payWaterQuery);

        return ResultVo.Success(payWaterEntityPageInfo);
    }
    /**
     * 支付流水列表导出
     * @author HZ
     * @date 2019/12/11
     */
    @PostMapping("/exportPayWater")
    public ResultVo exportPayWater(@RequestBody  PayWaterQuery payWaterQuery){

        String oemCode=getRequestHeadParams("oemCode");

        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            payWaterQuery.setOemCode(oemCode);
        }
        payWaterQuery.setTree(getOrgTree());
        //查询支付流水记录表数据
        List<PaywaterVO> payWaterEntityPageInfo=payWaterService.payWaterList(payWaterQuery);

        if (CollectionUtil.isEmpty(payWaterEntityPageInfo)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("支付流水", "支付流水", PaywaterVO.class, payWaterEntityPageInfo);
        } catch (Exception e) {
            log.error("支付流水导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
        return ResultVo.Success();
    }

    @ApiOperation("提现确认页")
    @PostMapping("detail")
    public ResultVo withdrawDetail(@JsonParam Long id) {
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            //获取资金账户信息
            UserCapitalAccountEntity entity = getUserCapitalAccountEntity(id, userEntity);
            //获取银行卡信息
            UserBankCardEntity cardEntity = getUserBankCardEntity(entity);
            //获取银行信息
            BankInfoEntity bankInfo = new BankInfoEntity();
            bankInfo.setBankCode(cardEntity.getBankCode());
            bankInfo.setStatus(1);
            bankInfo = bankInfoService.selectOne(bankInfo);
            return ResultVo.Success(new WithdrawDetailVO(userEntity, entity, cardEntity, bankInfo));
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
    }

    @ApiOperation("提现")
    @PostMapping("confirm")
    public ResultVo withdrawConfirm(@JsonParam Long id, @JsonParam Long amount, @JsonParam String verifyCode) {
//        //带登陆验证
//        if (id  == null) {
//            return ResultVo.Fail("主键不能为空");
//        }
//        if (amount  == null) {
//            return ResultVo.Fail("提现金额不能为空");
//        }
//        if (amount < 10000) {
//            return ResultVo.Fail("提现金额必须大于100元");
//        }
//        //元转分
//        String smsKey = RedisKey.SMS_WALLET_WITHDRAW_ADMIN_KEY_SUFFER;
//        String lockKey = RedisKey.USER_WITHDRAW_ADMIN_REDIS_KEY;
//        String registRedisTime = System.currentTimeMillis() + 300000 + "";
//        try {
//            UserEntity userEntity = userService.findById(getCurrUserId());
//            if (userEntity == null) {
//                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
//            }
//            //获取资金账户信息
//            UserCapitalAccountEntity entity = getUserCapitalAccountEntity(id, userEntity);
//            if (entity.getAvailableAmount() < amount) {
//                return ResultVo.Fail("可用余额不足");
//            }
//            //获取银行卡信息
//            UserBankCardEntity cardEntity = getUserBankCardEntity(entity);
//            smsKey +=  cardEntity.getOemCode() + "_" + cardEntity.getPhone();
//            //校验短信验证码
//            smsService.verifyCode(smsKey, verifyCode);
//            //提现操作锁
//            lockKey +=  cardEntity.getOemCode() + "_" + cardEntity.getPhone();
//            boolean lockResult = redisService.lock(lockKey, registRedisTime, 60);
//            if(!lockResult){
//                return ResultVo.Fail("提现处理中，请稍后再试");
//            }
//            orderService.adminWithdraw(entity, cardEntity, amount, userEntity.getNickname(),1,null);
//            redisService.delete(smsKey);
//            return ResultVo.Success();
//        } catch (BusinessException e) {
//            return ResultVo.Fail(e.getMessage());
//        } finally {
//            redisService.unlock(lockKey, registRedisTime);
//        }
        return ResultVo.Fail("暂不支持该功能");
    }

    /**
     * 获取资金账户
     * @param id
     * @return
     * @throws BusinessException
     */
    private UserCapitalAccountEntity getUserCapitalAccountEntity(Long id, UserEntity userEntity) throws BusinessException {
        UserCapitalAccountEntity entity = userCapitalAccountService.findById(id);
        if (entity == null) {
            throw new BusinessException("资金账户不存在");
        }
        if (!Objects.equals(entity.getUserType(), 2)) {
            throw new BusinessException("用户类型有误");
        }
        if (!Objects.equals(entity.getUserId(), userEntity.getId())) {
            throw new BusinessException("资金账户归属有误");
        }
        return entity;
    }

    /**
     * 获取银行卡信息
     * @param entity
     * @return
     * @throws BusinessException
     */
    public UserBankCardEntity getUserBankCardEntity(UserCapitalAccountEntity entity) throws BusinessException {
        UserBankCardEntity cardEntity = new UserBankCardEntity();
        cardEntity.setUserId(entity.getUserId());
        cardEntity.setUserType(entity.getUserType());
        cardEntity.setStatus(CardStatusEnum.BIND.getValue());
        cardEntity.setOemCode(entity.getOemCode());
        cardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
        cardEntity = userBankCardService.selectOne(cardEntity);
        if (cardEntity == null) {
            throw new BusinessException("暂无绑定提现银行账户，请联系上级机构添加！");
        }
        if (StringUtils.isBlank(cardEntity.getBankNumber())) {
            throw new BusinessException("银行卡号为空");
        }
        if (StringUtils.isBlank(cardEntity.getPhone())) {
            throw new BusinessException("银行账户手机号为空");
        }
        return cardEntity;
    }

    @ApiOperation("代理提现详情")
    @PostMapping("agent/detail")
    public ResultVo agentWithdrawDetail(@JsonParam String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PayWaterEntity payWaterEntity = new PayWaterEntity();
        payWaterEntity.setOrderNo(orderNo);
        payWaterEntity.setOrderType(OrderTypeEnum.SUBSTITUTE_WITHDRAW.getValue());
        payWaterEntity.setOemCode(userEntity.getOemCode());
        List<PayWaterEntity> list = payWaterService.select(payWaterEntity);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("提现记录不存在");
        }
        payWaterEntity = list.get(0);
//        boolean belongSystem = belongSystem(payWaterEntity.getMemberId(), getOrgTree());
//        if (belongSystem) {
//            return ResultVo.Fail("订单不属于当前登录用户组织");
//        }

        //判断是否为系统用户
        if(ObjectUtils.equals(payWaterEntity.getUserType(), 1)){ //会员
            MemberAccountEntity memberAccountEntity = memberAccountService.findById(payWaterEntity.getMemberId());
            if (memberAccountEntity == null) {
                return ResultVo.Fail("会员用户不存在");
            }

            UserBankCardEntity userBankCardEntity = new UserBankCardEntity();
            userBankCardEntity.setBankNumber(payWaterEntity.getPayAccount());
            userBankCardEntity.setStatus(CardStatusEnum.BIND.getValue());
            userBankCardEntity.setOemCode(payWaterEntity.getOemCode());
            userBankCardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
            List<UserBankCardEntity> banks = userBankCardService.select(userBankCardEntity);
            if (CollectionUtil.isEmpty(banks)) {
                return ResultVo.Fail("银行卡不存在");
            }
            userBankCardEntity = banks.get(0);
            UserCapitalAccountEntity userCapitalAccountEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (userCapitalAccountEntity == null) {
                return ResultVo.Fail("资金账户不存在");
            }
            OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            UserCapitalAccountDetailVO vo = new UserCapitalAccountDetailVO(userCapitalAccountEntity, null, null, null, userBankCardEntity);
            vo.setOrderNo(payWaterEntity.getOrderNo());
            vo.setOemName(payWaterEntity.getOemName());
            vo.setPayAmount(payWaterEntity.getPayAmount());
            vo.setWalletType(payWaterEntity.getWalletType());
            vo.setPayStatus(payWaterEntity.getPayStatus());
            vo.setRemark(orderEntity.getRemark());
            vo.setAuditStatus(orderEntity.getAuditStatus());
            vo.setAuditRemark(orderEntity.getAuditRemark());
            vo.setUsername(memberAccountEntity.getMemberAccount());
            vo.setNickname(StringUtils.isBlank(memberAccountEntity.getRealName())? memberAccountEntity.getMemberName():memberAccountEntity.getRealName() );
            vo.setPhone(memberAccountEntity.getMemberAccount());
            if (StringUtils.isNotBlank(orderEntity.getPayWaterImgs())) {
                String[] split = orderEntity.getPayWaterImgs().split(",");
                List<String> imags = Lists.newArrayList();
                for (String s : split) {
                    imags.add(ossService.getPrivateImgUrl(s));
                }
                vo.setPayWaterImgs(imags);
            }
            return ResultVo.Success(vo);
        }else { //系统用户
            userEntity = userService.findById(payWaterEntity.getMemberId());
            if (userEntity == null) {
                return ResultVo.Fail("系统用户不存在");
            }
            UserExtendEntity userExtendEntity = userExtendService.getUserExtendByUserId(userEntity.getId());
            if (userExtendEntity == null) {
                return ResultVo.Fail("系统用户扩展信息不存在");
            }
            UserBankCardEntity userBankCardEntity = new UserBankCardEntity();
            userBankCardEntity.setBankNumber(payWaterEntity.getPayAccount());
            userBankCardEntity.setStatus(CardStatusEnum.BIND.getValue());
            userBankCardEntity.setOemCode(payWaterEntity.getOemCode());
            userBankCardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
            List<UserBankCardEntity> banks = userBankCardService.select(userBankCardEntity);
            if (CollectionUtil.isEmpty(banks)) {
                return ResultVo.Fail("银行卡不存在");
            }
            userBankCardEntity = banks.get(0);
            UserCapitalAccountEntity userCapitalAccountEntity = userCapitalAccountService.queryByUserIdAndType(userEntity.getId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (userCapitalAccountEntity == null) {
                return ResultVo.Fail("资金账户不存在");
            }
            OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            UserCapitalAccountDetailVO vo = new UserCapitalAccountDetailVO(userCapitalAccountEntity, userEntity, userExtendEntity, null, userBankCardEntity);
            vo.setOrderNo(payWaterEntity.getOrderNo());
            vo.setOemName(payWaterEntity.getOemName());
            vo.setPayAmount(payWaterEntity.getPayAmount());
            vo.setWalletType(payWaterEntity.getWalletType());
            vo.setPayStatus(payWaterEntity.getPayStatus());
            vo.setRemark(orderEntity.getRemark());
            vo.setAuditStatus(orderEntity.getAuditStatus());
            vo.setAuditRemark(orderEntity.getAuditRemark());
            if (StringUtils.isNotBlank(orderEntity.getPayWaterImgs())) {
                String[] split = orderEntity.getPayWaterImgs().split(",");
                List<String> imags = Lists.newArrayList();
                for (String s : split) {
                    imags.add(ossService.getPrivateImgUrl(s));
                }
                vo.setPayWaterImgs(imags);
            }
            return ResultVo.Success(vo);
        }
    }

    @ApiOperation("代理充值详情")
    @PostMapping("agent/recharge/detail")
    public ResultVo agentRechargeDetail(@JsonParam String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PayWaterEntity payWaterEntity = new PayWaterEntity();
        payWaterEntity.setOrderNo(orderNo);
        payWaterEntity.setOrderType(OrderTypeEnum.SUBSTITUTE_CHARGE.getValue());
        payWaterEntity.setOemCode(userEntity.getOemCode());
        List<PayWaterEntity> list = payWaterService.select(payWaterEntity);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("充值记录不存在");
        }
        payWaterEntity = list.get(0);
//        boolean belongSystem = belongSystem(payWaterEntity.getMemberId(), getOrgTree());
//        if (belongSystem) {
//            return ResultVo.Fail("订单不属于当前登录用户组织");
//        }

        //判断是否为系统用户
        if(ObjectUtils.equals(payWaterEntity.getUserType(), 1)){ //会员
            MemberAccountEntity memberAccountEntity = memberAccountService.findById(payWaterEntity.getMemberId());
            if (memberAccountEntity == null) {
                return ResultVo.Fail("会员用户不存在");
            }

/*            UserBankCardEntity userBankCardEntity = new UserBankCardEntity();
            userBankCardEntity.setBankNumber(payWaterEntity.getPayAccount());
            userBankCardEntity.setStatus(CardStatusEnum.BIND.getValue());
            userBankCardEntity.setOemCode(payWaterEntity.getOemCode());
            userBankCardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
            List<UserBankCardEntity> banks = userBankCardService.select(userBankCardEntity);
            if (CollectionUtil.isEmpty(banks)) {
                return ResultVo.Fail("银行卡不存在");
            }
            userBankCardEntity = banks.get(0);*/
            UserCapitalAccountEntity userCapitalAccountEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (userCapitalAccountEntity == null) {
                return ResultVo.Fail("资金账户不存在");
            }
            OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            UserCapitalAccountDetailVO vo = new UserCapitalAccountDetailVO(userCapitalAccountEntity, null, null, null, null);
            vo.setOrderNo(payWaterEntity.getOrderNo());
            vo.setOemName(payWaterEntity.getOemName());
            vo.setPayAmount(payWaterEntity.getPayAmount());
            vo.setWalletType(payWaterEntity.getWalletType());
            vo.setPayStatus(payWaterEntity.getPayStatus());
            vo.setRemark(orderEntity.getRemark());
            vo.setAuditStatus(orderEntity.getAuditStatus());
            vo.setAuditRemark(orderEntity.getAuditRemark());
            vo.setUsername(memberAccountEntity.getMemberAccount());
            vo.setNickname(StringUtils.isBlank(memberAccountEntity.getRealName())? memberAccountEntity.getMemberName():memberAccountEntity.getRealName() );
            vo.setPhone(memberAccountEntity.getMemberAccount());
            if (StringUtils.isNotBlank(orderEntity.getPayWaterImgs())) {
                String[] split = orderEntity.getPayWaterImgs().split(",");
                List<String> imags = Lists.newArrayList();
                for (String s : split) {
                    imags.add(ossService.getPrivateImgUrl(s));
                }
                vo.setPayWaterImgs(imags);
            }
            return ResultVo.Success(vo);
        }else { //系统用户
            userEntity = userService.findById(payWaterEntity.getMemberId());
            if (userEntity == null) {
                return ResultVo.Fail("系统用户不存在");
            }
            UserExtendEntity userExtendEntity = userExtendService.getUserExtendByUserId(userEntity.getId());
            if (userExtendEntity == null) {
                return ResultVo.Fail("系统用户扩展信息不存在");
            }
          /*  UserBankCardEntity userBankCardEntity = new UserBankCardEntity();
            userBankCardEntity.setBankNumber(payWaterEntity.getPayAccount());
            userBankCardEntity.setStatus(CardStatusEnum.BIND.getValue());
            userBankCardEntity.setOemCode(payWaterEntity.getOemCode());
            userBankCardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
            List<UserBankCardEntity> banks = userBankCardService.select(userBankCardEntity);
            if (CollectionUtil.isEmpty(banks)) {
                return ResultVo.Fail("银行卡不存在");
            }
            userBankCardEntity = banks.get(0);*/
            UserCapitalAccountEntity userCapitalAccountEntity = userCapitalAccountService.queryByUserIdAndType(userEntity.getId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (userCapitalAccountEntity == null) {
                return ResultVo.Fail("资金账户不存在");
            }
            OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            UserCapitalAccountDetailVO vo = new UserCapitalAccountDetailVO(userCapitalAccountEntity, userEntity, userExtendEntity, null, null);
            vo.setOrderNo(payWaterEntity.getOrderNo());
            vo.setOemName(payWaterEntity.getOemName());
            vo.setPayAmount(payWaterEntity.getPayAmount());
            vo.setWalletType(payWaterEntity.getWalletType());
            vo.setPayStatus(payWaterEntity.getPayStatus());
            vo.setRemark(orderEntity.getRemark());
            vo.setAuditStatus(orderEntity.getAuditStatus());
            vo.setAuditRemark(orderEntity.getAuditRemark());
            if (StringUtils.isNotBlank(orderEntity.getPayWaterImgs())) {
                String[] split = orderEntity.getPayWaterImgs().split(",");
                List<String> imags = Lists.newArrayList();
                for (String s : split) {
                    imags.add(ossService.getPrivateImgUrl(s));
                }
                vo.setPayWaterImgs(imags);
            }
            return ResultVo.Success(vo);
        }
    }


    @ApiOperation("代理提现审核")
    @PostMapping("agent/audit")
    public ResultVo agentWithdrawAudit(@JsonParam String orderNo, @JsonParam Integer auditStatus, @JsonParam String payWaterImgs, @JsonParam String auditRemark) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (auditStatus == null || (!Objects.equals(auditStatus, 1) && !Objects.equals(auditStatus, 2))) {
            return ResultVo.Fail("审核操作有误");
        }
        if (Objects.equals(auditStatus, 1) && StringUtils.isBlank(payWaterImgs)) {
            return ResultVo.Fail("请上传打款流水");
        }
        if (Objects.equals(auditStatus, 2) && StringUtils.isBlank(auditRemark)) {
            return ResultVo.Fail("请填写备注");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PayWaterEntity payWaterEntity = new PayWaterEntity();
        payWaterEntity.setOrderNo(orderNo);
//        payWaterEntity.setUserType(2);
        payWaterEntity.setOrderType(OrderTypeEnum.SUBSTITUTE_WITHDRAW.getValue());
        payWaterEntity.setOemCode(userEntity.getOemCode());
        List<PayWaterEntity> list = payWaterService.select(payWaterEntity);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("提现记录不存在");
        }
        payWaterEntity = list.get(0);
        if (!Objects.equals(payWaterEntity.getPayStatus(), PayWaterStatusEnum.WAIT_FOR_AUDIT.getValue())) {
            return ResultVo.Fail("不是待审核提现记录");
        }
      /*  boolean belongSystem = belongSystem(payWaterEntity.getMemberId(), getOrgTree());
        if (belongSystem) {
            return ResultVo.Fail("订单不属于当前登录用户组织");
        }*/
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!Objects.equals(orderEntity.getAuditStatus(), AuditStateEnum.TO_APPROVE.getValue())) {
            return ResultVo.Fail("不是待审核订单");
        }
        UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
        if (accEntity == null) {
            return ResultVo.Fail("资金账户不存在");
        }
        String lockKey = RedisKey.USER_WITHDRAW_AGENT_ADMIN_REDIS_KEY + accEntity.getId();
        String lockTime = System.currentTimeMillis() + 300000 + "";
        try {
            boolean lockResult = redisService.lock(lockKey, lockTime, 60);
            if(!lockResult){
                return ResultVo.Fail("用户资金处理中，请稍后再试");
            }
            payWaterService.updateWaterAndOrder(payWaterEntity, orderEntity, auditStatus, payWaterImgs, auditRemark, currUser.getUseraccount());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            return ResultVo.Fail("代理提现审核失败");
        } finally {
            redisService.unlock(lockKey, lockTime);
        }
        return ResultVo.Success();
    }

    @ApiOperation("代理充值审核")
    @PostMapping("agent/recharge/audit")
    public ResultVo agentRechargeAudit(@JsonParam String orderNo, @JsonParam Integer auditStatus, @JsonParam String payWaterImgs, @JsonParam String auditRemark) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (auditStatus == null || (!Objects.equals(auditStatus, 1) && !Objects.equals(auditStatus, 2))) {
            return ResultVo.Fail("审核操作有误");
        }
        if (Objects.equals(auditStatus, 1) && StringUtils.isBlank(payWaterImgs)) {
            return ResultVo.Fail("请上传收款凭证");
        }
        if (Objects.equals(auditStatus, 2) && StringUtils.isBlank(auditRemark)) {
            return ResultVo.Fail("请填写备注");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PayWaterEntity payWaterEntity = new PayWaterEntity();
        payWaterEntity.setOrderNo(orderNo);
//        payWaterEntity.setUserType(2);
        payWaterEntity.setOrderType(OrderTypeEnum.SUBSTITUTE_CHARGE.getValue());
        payWaterEntity.setOemCode(userEntity.getOemCode());
        List<PayWaterEntity> list = payWaterService.select(payWaterEntity);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("充值记录不存在");
        }
        payWaterEntity = list.get(0);
        if (!Objects.equals(payWaterEntity.getPayStatus(), PayWaterStatusEnum.WAIT_FOR_AUDIT.getValue())) {
            return ResultVo.Fail("不是待审核充值记录");
        }
      /*  boolean belongSystem = belongSystem(payWaterEntity.getMemberId(), getOrgTree());
        if (belongSystem) {
            return ResultVo.Fail("订单不属于当前登录用户组织");
        }*/
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!Objects.equals(orderEntity.getAuditStatus(), AuditStateEnum.TO_APPROVE.getValue())) {
            return ResultVo.Fail("不是待审核订单");
        }
        UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
        if (accEntity == null) {
            return ResultVo.Fail("资金账户不存在");
        }
        String lockKey = RedisKey.USER_RECHARGE_AGENT_ADMIN_REDIS_KEY + accEntity.getId();
        String lockTime = System.currentTimeMillis() + 300000 + "";
        try {
            boolean lockResult = redisService.lock(lockKey, lockTime, 60);
            if(!lockResult){
                return ResultVo.Fail("用户资金处理中，请稍后再试");
            }
            payWaterService.updateWaterAndOrderByRecharge(payWaterEntity, orderEntity, auditStatus, payWaterImgs, auditRemark, currUser.getUseraccount());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            return ResultVo.Fail("代理提现审核失败");
        } finally {
            redisService.unlock(lockKey, lockTime);
        }
        return ResultVo.Success();
    }

    /**
     * 公户提现流水分页查询
     * @param query
     * @return
     */
    @PostMapping("corporate/water/page")
    public ResultVo corporateWaterPage(@RequestBody CorporateAccountWithdrawWaterQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                query.setParkId(userEntity.getParkId());
            } else {
                query.setTree(getOrgTree());
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        query.setOemCode(userEntity.getOemCode());
        PageInfo<CorporateAccountWithdrawWaterVO> page = corporateAccountWithdrawalOrderService.listPageCorporateWithdrawWater(query);
        return ResultVo.Success(page);
    }

    /**
     * 公户提现流水导出
     * @param query
     * @return
     */
    @PostMapping("corporate/water/export")
    public ResultVo corporateWaterExport(@RequestBody CorporateAccountWithdrawWaterQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        // 当前登录用户身份是否为园区
        if (Objects.equals(userEntity.getPlatformType(),3)) {
            query.setParkId(userEntity.getParkId());
        }
        List<CorporateAccountWithdrawWaterVO> lists = corporateAccountWithdrawalOrderService.listCorporateWithdrawWater(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("公户提现流水", "公户提现流水", CorporateAccountWithdrawWaterVO.class, lists);
            return null;
        } catch (Exception e) {
            log.error("公户提现流水导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }


    /**
     * 批量下载身份信息
     * @param query
     * @return
     */
    @PostMapping("batch/down/idCard")
    public ResultVo batchDownIdCard(@RequestBody WthdrawQuery  query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            query.setOemCode(oemCode);
        }
        if(query.getType()!=null&&query.getType()==1){
            query.setMemberId(getCurrUserId());
            query.setUserType(2);
        }
        query.setTree(getOrgTree());
        //查询
        List<Map<String,Object>> maps=payWaterService.batchDownIdCard(query);
        if(CollectionUtil.isEmpty(maps)){
            return ResultVo.Fail("暂无数据导出");
        }
        //装身份证得容器
        ArrayList<String> urlList = new ArrayList<>();
        for (Map<String,Object> map:maps) {
            urlList.add(ossService.getPrivateImgUrl((String)map.get("id_card_front"))+"||"+map.get("id_card_no")+"_正面");
            urlList.add(ossService.getPrivateImgUrl((String)map.get("id_card_back"))+"||"+map.get("id_card_no")+"_反面");
        }
        try {
            ImageDownloadUtil.downloadImagesZip(getRequest(),getResponse(),urlList,DateUtil.format(new Date(),"yyyy-MM-dd"));
        } catch (Exception e) {
            log.error("系统未知异常：{}",e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 退款接口
     */
    @ApiOperation("退款")
    @PostMapping("routeAndRefundOrder")
    public ResultVo routeAndRefundOrder(@JsonParam Long  id ,@JsonParam Integer  type,@JsonParam String payPic) {
        if(id==null){
            return ResultVo.Fail("请选择要退款的支付流水");
        }
        if(type==null||(type!=1&&type!=2&&type!=3)){
            return ResultVo.Fail("请选择正确的退款类型");
        }
        if(type==1 &&StringUtils.isBlank(payPic)){
            return ResultVo.Fail("请选择上传打款凭证");
        }
        PayWaterEntity payWaterEntity=payWaterService.findById(id);
        OrderEntity orderEntity = orderService.queryByOrderNo(payWaterEntity.getOrderNo());
        if (null == orderEntity) {
            return ResultVo.Fail("未查询到订单信息");
        }
        if(type==1){
            payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            payWaterEntity.setPayPic(payPic);
            payWaterEntity.setUpdateTime(new Date());
            payWaterEntity.setUpdateUser(getCurrUseraccount());
            payWaterEntity.setRefundStatus(RefundWaterStatusEnum.REFUND_SUCCESS.getValue());
            payWaterService.updatePayStatus(payWaterEntity);
        }else if (type == 2){
            if(ObjectUtils.equals(PayWayEnum.WECHATPAY.getValue(),payWaterEntity.getPayWay())) { //微信支付退款
                try {
                    PayWaterEntity payEntity = new PayWaterEntity();
                    payEntity.setOrderNo(payWaterEntity.getOrderNo());
                    payEntity.setPayWay(PayWayEnum.WECHATPAY.getValue());
                    payEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                    PayWaterEntity entity = payWaterService.selectOne(payEntity);
                    registerOrderService.routeAndRefundOrderReplay(payWaterEntity.getOemCode(), payWaterEntity.getOrderNo(), entity.getPayNo(), getCurrUseraccount(),PayWayEnum.WECHATPAY.getValue());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return ResultVo.Fail("微信退款失败");
                }
            }else if(ObjectUtils.equals(PayWayEnum.BYTEDANCE.getValue(),payWaterEntity.getPayWay())) { //字节跳动退款
                try {
                    PayWaterEntity payEntity = new PayWaterEntity();
                    payEntity.setOrderNo(payWaterEntity.getOrderNo());
                    payEntity.setPayWay(PayWayEnum.BYTEDANCE.getValue());
                    payEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                    PayWaterEntity entity = payWaterService.selectOne(payEntity);
                    registerOrderService.routeAndRefundOrderReplay(payWaterEntity.getOemCode(), payWaterEntity.getOrderNo(), entity.getPayNo(), getCurrUseraccount(),PayWayEnum.BYTEDANCE.getValue());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return ResultVo.Fail("字节跳动退款失败");
                }
            }
        } else { // 退回余额
            orderService.refund(orderEntity, getCurrUseraccount(), new Date(),2, true);
            // 修改支付流水退款状态
            payWaterEntity.setRefundStatus(RefundWaterStatusEnum.REFUND_SUCCESS.getValue());
            payWaterService.editByIdSelective(payWaterEntity);
        }

        return ResultVo.Success();
    }
    /**
     * 查看打款凭证
     */
    @PostMapping("getPayPic")
    public ResultVo getPayPic(@JsonParam Long  id ) {
        if(id==null){
            return ResultVo.Fail("请选择要退款的支付流水");
        }
        PayWaterEntity payWaterEntity=payWaterService.findById(id);
        if(payWaterEntity==null){
            return ResultVo.Fail("支付流水不存在");
        }
        if(StringUtils.isBlank(payWaterEntity.getPayPic())){
            return ResultVo.Fail("打款凭证不存在");
        }
        return ResultVo.Success(ossService.getPrivateImgUrl(payWaterEntity.getPayPic()));
    }


    @ApiOperation(value="线下打款", notes="线下打款")
    @PostMapping("payment")
    public ResultVo payment(@Valid @RequestBody PaymentVo vo){
        PayWaterEntity payWater = payWaterService.findById(vo.getId());
        if(payWater == null){
            return ResultVo.Fail("支付流水不存在");
        }
        if(StringUtils.isNotBlank(vo.getPayPic())){
            payWater.setPayPic(vo.getPayPic());
        }
        if(StringUtils.isNotBlank(vo.getRemark())){
            payWater.setRemark(vo.getRemark());
        }
        // 支付状态变成 支付成功
        payWater.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        // 执行修改操作
        payWaterService.editByIdSelective(payWater);
        return ResultVo.Success();
    }

}
