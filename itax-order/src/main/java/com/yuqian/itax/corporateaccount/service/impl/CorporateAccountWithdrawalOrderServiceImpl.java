package com.yuqian.itax.corporateaccount.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountWithdrawalOrderMapper;
import com.yuqian.itax.corporateaccount.entity.CollectionWithdrawalAmountChangeRecordEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountCollectionRecordEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountWithdrawalOrderEntity;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawOrderQuery;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawWaterQuery;
import com.yuqian.itax.corporateaccount.service.CollectionWithdrawalAmountChangeRecordService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountCollectionRecordService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountWithdrawalOrderService;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawOrderVO;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawWaterVO;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.dto.CorpAccountWithdrawOrderDTO;
import com.yuqian.itax.order.entity.vo.CorpAccWithdrawOrderVO;
import com.yuqian.itax.order.entity.vo.CorpAccountColsRecordsVO;
import com.yuqian.itax.order.entity.vo.CorpAccountWithdrawOrderDetailVO;
import com.yuqian.itax.order.entity.vo.CorpAccountWithdrawOrderVO;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.MemberOrderRelaService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.service.ParkCorporateAccountConfigService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayChannelEnum;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.enums.PayWaterTypeEnum;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.dto.PaidOrderDTO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.enums.CompanyCorpAccOverdueStatusEnum;
import com.yuqian.itax.user.enums.CompanyCorporateAccountStatusEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.MoneyUtil;
import com.yuqian.itax.util.util.OrderNoFactory;
import com.yuqian.itax.util.util.UniqueNumGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service("corporateAccountWithdrawalOrderService")
public class CorporateAccountWithdrawalOrderServiceImpl extends BaseServiceImpl<CorporateAccountWithdrawalOrderEntity,CorporateAccountWithdrawalOrderMapper> implements CorporateAccountWithdrawalOrderService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private CorporateAccountCollectionRecordService corporateAccountCollectionRecordService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private ParkCorporateAccountConfigService parkCorporateAccountConfigService;
    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private MemberOrderRelaService memberOrderRelaService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private DaifuApiService daifuApiService;
    @Autowired
    private OemService oemService;
    @Autowired
    private PayWaterService payWaterService;
    @Autowired
    private CollectionWithdrawalAmountChangeRecordService collectionWithdrawalAmountChangeRecordService;
    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public PageInfo<CorporateAccountWithdrawOrderVO> listPageCorporateWithdrawOrder(CorporateAccountWithdrawOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listCorporateWithdrawOrder(query));
    }

    @Override
    public List<CorporateAccountWithdrawOrderVO> listCorporateWithdrawOrder(CorporateAccountWithdrawOrderQuery query) {
        query.setOrderType(OrderTypeEnum.CORPORATE_WITHDRAW.getValue());
        return mapper.listCorporateWithdrawOrder(query);
    }

    @Override
    public PageInfo<CorporateAccountWithdrawWaterVO> listPageCorporateWithdrawWater(CorporateAccountWithdrawWaterQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listCorporateWithdrawWater(query));
    }

    @Override
    public List<CorporateAccountWithdrawWaterVO> listCorporateWithdrawWater(CorporateAccountWithdrawWaterQuery query) {
        query.setOrderType(OrderTypeEnum.CORPORATE_WITHDRAW.getValue());
        return mapper.listCorporateWithdrawWater(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createCorpAccountWithdrawOrder(CorpAccountWithdrawOrderDTO dto) {
        log.info("开始处理用户提现请求：{}", JSON.toJSONString(dto));

        // 查询用户账号
        MemberAccountEntity member = this.memberAccountService.findById(dto.getCurrUserId());
        if (null == member) {
            throw new BusinessException("当前登录用户不存在");
        }

        // 查询OEM机构
        OemEntity oem = this.oemService.getOem(dto.getOemCode());
        if(null == oem){
            throw new BusinessException("OEM机构不存在");
        }
        dto.setOemName(oem.getOemName());

        // 校验对公户
        CompanyCorporateAccountEntity corporateAccount = this.companyCorporateAccountService.findById(dto.getCorporateAccountId());
        if(null == corporateAccount){
            throw new BusinessException("对公户信息不存在");
        }
        if(StringUtil.isEmpty(corporateAccount.getBindBankCardNumber())){
            throw new BusinessException("该对公户尚未绑定银行卡");
        }
        if (!CompanyCorporateAccountStatusEnum.NORMAL.getValue().equals(corporateAccount.getStatus())) {
            throw new BusinessException("对公户已冻结或已注销");
        }
        if (CompanyCorpAccOverdueStatusEnum.OVERDUE.getValue().equals(corporateAccount.getOverdueStatus()) ||
                LocalDate.now().isAfter(corporateAccount.getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
            throw new BusinessException("对公户已过期");
        }
        dto.setCorporateAccount(corporateAccount.getCorporateAccount());
        dto.setVoucherMemberCode(corporateAccount.getVoucherMemberCode());
        dto.setCorporateAccountConfigId(corporateAccount.getCorporateAccountConfigId());
        // 提现金额校验（接收金额单位：分）
        checkWithdrawAmount(dto);

        // 创建对公户提现订单
        String orderNo = this.createWithdrawOrder(dto,member,corporateAccount);
        log.info("用户提现请求处理结束...");

        return orderNo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createWithdrawOrder(CorpAccountWithdrawOrderDTO dto,MemberAccountEntity member,CompanyCorporateAccountEntity corporateAccount) {
        log.info("开始创建对公户提现订单：{}",JSON.toJSONString(dto));

        String orderNo = OrderNoFactory.getOrderCode(dto.getCurrUserId()); // 生成订单号
        // 保存订单主表信息
        OrderEntity order = new OrderEntity();
        order.setOemCode(dto.getOemCode());
        order.setOrderNo(orderNo);
        order.setOrderAmount(dto.getAmount());
        order.setPayAmount(dto.getAmount());
        order.setProfitAmount(0L);
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setOrderType(OrderTypeEnum.CORPORATE_WITHDRAW.getValue());
        order.setWalletType(WalletTypeEnum.OTHER.getValue());
        order.setAddUser(member.getMemberAccount());
        order.setSourceType(dto.getSourceType());

        // 补全订单参数
        completionParameter(member, order);
        order.setUpdateTime(order.getAddTime());
        order.setUpdateUser(order.getAddUser());
        this.orderService.insertSelective(order);

        // 生成对公户提现订单
        CorporateAccountWithdrawalOrderEntity withdrawalOrder = new CorporateAccountWithdrawalOrderEntity();
        withdrawalOrder.setOrderNo(orderNo);
        withdrawalOrder.setParkId(dto.getParkId());
        withdrawalOrder.setCorporateAccountId(dto.getCorporateAccountId());
        withdrawalOrder.setCollectionRecordIds(dto.getCollectionRecordIds());
        withdrawalOrder.setAddTime(new Date());
        withdrawalOrder.setAddUser(member.getMemberAccount());
        withdrawalOrder.setArriveBankAccount(corporateAccount.getBindBankCardNumber());
        withdrawalOrder.setArriveBankName(corporateAccount.getBindBankName());
        MemberCompanyEntity company = this.memberCompanyService.findById(corporateAccount.getCompanyId());
        withdrawalOrder.setArriveUserName(company.getOperatorName());// 收款方姓名，取经营者名称
        withdrawalOrder.setInvoiceOrderNo(dto.getInvoiceOrderNo());
        withdrawalOrder.setWithdrawalAmount(dto.getAmount());
        this.insertSelective(withdrawalOrder);

        // 生成对公户提现订单流水
        PayWaterEntity water = new PayWaterEntity();
        String payNo = UniqueNumGenerator.generatePayNo();// 生成24位流水号
        water.setOemCode(dto.getOemCode());
        water.setOemName(dto.getOemName());
        water.setOrderAmount(dto.getAmount());
        water.setPayAmount(dto.getAmount());
        water.setPayNo(payNo);
        water.setOrderNo(orderNo);
        water.setMemberId(dto.getCurrUserId());
        water.setWalletType(WalletTypeEnum.OTHER.getValue());
        water.setAddTime(new Date());
        water.setPayChannels(PayChannelEnum.CCBPAY.getValue());
        water.setOrderType(OrderTypeEnum.CORPORATE_WITHDRAW.getValue());
        water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        water.setPayWaterType(PayWaterTypeEnum.CORP_WITHDRAW.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款，6-对公户提现
        water.setPayStatus(PayWaterStatusEnum.PAY_INIT.getValue());
        this.payWaterService.insertSelective(water);

        log.info("对公户提现订单创建完成");
        return orderNo;
    }

    /**
     * @Description 对公户提现订单参数补全
     * @Author  Kaven
     * @Date   2020/9/9 09:58
     * @Param   MemberAccountEntity OrderEntity
     * @Return
     * @Exception
    */
    private void completionParameter(MemberAccountEntity member, OrderEntity order) {
        // 充值提现不需要保存会员订单关系
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(member.getId(), order.getOemCode(), 6);// 对公户提现无分润，暂定6
        if (more != null) {
            more.setMemberId(member.getId());
            more.setOemCode(order.getOemCode());
            more.setAddTime(new Date());
            // 设置会员等级
            more.setAddUser(member.getMemberAccount());
            MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
            more.setMemberLevel(level.getLevelNo());
            this.memberOrderRelaService.insertSelective(more);
        }

        // 补全订单主表信息
        order.setUserId(member.getId());
        order.setUserType(1);
        if (more != null) {
            order.setRelaId(more.getId());
        }
        order.setAddUser(member.getMemberAccount());
        order.setAddTime(new Date());
        order.setOrderStatus(RACWStatusEnum.TO_SUBMIT.getValue());// 待提交
        order.setChannelProductCode(member.getChannelProductCode());
        order.setChannelCode(member.getChannelCode());
        order.setChannelEmployeesId(member.getChannelEmployeesId());
        order.setChannelServiceId(member.getChannelServiceId());
        order.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        order.setChannelUserId(member.getChannelUserId());
    }

    /**
     * @Description 提现金额校验
     * @Author  Kaven
     * @Date   2020/9/8 17:08
     * @Param   CorpAccountWithdrawOrderDTO
     * @Return
     * @Exception
    */
    private void checkWithdrawAmount(CorpAccountWithdrawOrderDTO dto) throws BusinessException{
        log.info("提现金额校验开始：");
        if (dto.getAmount() <= 0) {
            throw new BusinessException("提现金额必须大于0");
        }
        // 查询开票金额
        InvoiceOrderEntity invoiceOrder = this.invoiceOrderService.queryByOrderNo(dto.getInvoiceOrderNo());
        if(null == invoiceOrder){
            throw new BusinessException("开票订单不存在");
        }

        // 开票订单发票标识校验
        if (InvoiceMarkEnum.CANCELLATION.getValue().equals(invoiceOrder.getInvoiceMark())) {
            throw new BusinessException("提现失败，开票订单已作废。");
        }

        // 累计提现额度
        Long totalAmount = 0L;
        String[] ids = dto.getCollectionRecordIds().split(",");
        if(ids.length > 0){
            // 累计开票金额
            for(int i = 0; i < ids.length; i++){
                CorporateAccountCollectionRecordEntity cacre = this.corporateAccountCollectionRecordService.findById(Long.valueOf(ids[i]));
                if(null == cacre){
                    throw new BusinessException("收款记录不存在");
                }
                totalAmount += cacre.getRemainingWithdrawalAmount();
            }
        }

        if(dto.getAmount() > invoiceOrder.getRemainingWithdrawalAmount()){
            throw new BusinessException("提现金额不能超过开票记录的剩余可提现金额！");
        }
        if(dto.getAmount() > totalAmount){
            throw new BusinessException("提现金额不能超过选择的收款记录的提现额度！");
        }
        /*
         * 查询账户可用余额，判断提现金额是否超出
        */
        Long availableAmount = 0L;// 账户可用余额
        // 读取渠道代付相关配置 paramsType=13
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(dto.getOemCode(),13);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付相关信息！");
        }
        // 调用渠道接口实时查询银行卡可用余额
        ComCorpAccQuery query = new ComCorpAccQuery();
        query.setDraweeAccountNo(dto.getCorporateAccount());
        query.setTxnStffId(dto.getVoucherMemberCode());
        JSONObject jsonObj = this.daifuApiService.queryCardBalance(paramsEntity,query);
        if(null != jsonObj){
            String balance = jsonObj.getString("accAvlBal"); // 账号可用余额
            availableAmount = null == balance ? 0L : Long.parseLong(balance);
        }
//        if(dto.getAmount() > availableAmount){
//            throw new BusinessException("提现金额不能超过账户可用余额！");
//        }
        //获取对公户提现账户需预留金额 V2.7
        String amtMinLimit = dictionaryService.getValueByCode("corporate_withdraw_amt_min_limit");
        if(dto.getAmount() > (availableAmount - Long.parseLong(amtMinLimit))) {
            throw new BusinessException("为保证账户正常使用，账户需预留" + MoneyUtil.moneydiv(amtMinLimit, "100") + "元，请修改金额后重试！");
        }
        // 校验是否超出银行卡限额
        MemberCompanyEntity company = this.memberCompanyService.findById(invoiceOrder.getCompanyId());// 查询单笔（日）限额信息
        if(null == company){
            throw new BusinessException("企业信息不存在");
        }
        dto.setParkId(company.getParkId());// 赋值园区ID，订单表需要保存

        // 查询对公户
        CompanyCorporateAccountEntity corporateAccountEntity = Optional.ofNullable(companyCorporateAccountService.queryCorpByCompanyId(invoiceOrder.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到对公户信息"));
        if(dto.getAmount() > corporateAccountEntity.getSingleWithdrawalLimit()){
            throw new BusinessException("提现金额超出银行卡单笔限额");
        }
        // 查询当日累计提现金额，校验是否超出银行卡单日限额
        Long dailyTotalAmount = this.mapper.countDailyTotalWithdrawAmount(dto.getCorporateAccountId());
        if((dailyTotalAmount + dto.getAmount()) > corporateAccountEntity.getDailyWithdrawalLimit()){
            throw new BusinessException("当天累计提现金额已超出银行卡单日限额");
        }
        //多少小时内提现金额不允许重复
        String repeatTime = dictionaryService.getValueByCode("corporate_withdraw_repeat_time");
        Integer repeatSize = orderService.corporateAmoutRepeatCheck(OrderTypeEnum.CORPORATE_WITHDRAW.getValue(), RACWStatusEnum.CANCELED.getValue(), dto.getAmount(), repeatTime,dto.getCorporateAccountId());
        if (repeatSize > 0) {
            throw new BusinessException(repeatTime + "小时内提现金额不允许重复，请修改金额后重试！");
        }
        log.info("提现金额校验完成。");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String confirmWithdrawOrder(String orderNo, String verifyCode, String oemCode, Long currUserId) throws BusinessException {
        log.info("对公户提现订单确认:{},{},{},{}",orderNo,verifyCode,oemCode,currUserId);

        // 查询用户账号
        MemberAccountEntity member = this.memberAccountService.findById(currUserId);
        if (null == member) {
            throw new BusinessException("操作失败，当前登录用户信息不存在");
        }

        // 验证码校验
        String registRedisTime = System.currentTimeMillis() + 300000 + "";
        //验证码验证
        String verficationCode = redisService.get(RedisKey.SMS_CORP_ACCOUNT_WITHDRAW_KEY_SUFFER + member.getMemberAccount());
        //验证码错误或过期
        if (verficationCode == null || "".equals(verficationCode) || !verifyCode.equals(verficationCode)) {
            // 释放redis锁
            redisService.unlock(RedisKey.SMS_CORP_ACCOUNT_WITHDRAW_KEY_SUFFER + member.getMemberAccount(), registRedisTime);
            throw new BusinessException(ErrorCodeEnum.PASSWORD_RESET_CODE_IS_EXPIRED);
        }

        // 订单检查
        OrderEntity mainOrder = this.orderService.queryByOrderNo(orderNo);
        CorporateAccountWithdrawalOrderEntity t = new CorporateAccountWithdrawalOrderEntity();
        t.setOrderNo(orderNo);
        CorporateAccountWithdrawalOrderEntity order = this.selectOne(t);
        if(null == order || null == mainOrder){
            throw new BusinessException("订单不存在，请检查订单号");
        }
        if(!RACWStatusEnum.TO_SUBMIT.getValue().equals(mainOrder.getOrderStatus())){
            throw new BusinessException("非法订单状态");
        }

        // 查询对公户信息
        CompanyCorporateAccountEntity corporateAccount = this.companyCorporateAccountService.findById(order.getCorporateAccountId());
        if(null == corporateAccount){
            throw new BusinessException("对公户不存在");
        }

        // 读取渠道代付相关配置 paramsType=17
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode,17);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付银行卡相关信息！");
        }

        // 查询对公户实时余额，判断提现余额是否充足
        OemParamsEntity params = this.oemParamsService.getParams(oemCode,13);// 读取渠道代付余额查询相关配置 paramsType=13
        if(null == params){
            throw new BusinessException("未配置渠道代付余额查询相关信息！");
        }

        // 调用渠道接口实时查询可用余额
        ComCorpAccQuery query = new ComCorpAccQuery();
        query.setDraweeAccountNo(corporateAccount.getCorporateAccount());
        query.setTxnStffId(corporateAccount.getVoucherMemberCode());
        JSONObject jsonObj = this.daifuApiService.queryCardBalance(params,query);
        if(null != jsonObj){
            String balance = jsonObj.getString("accAvlBal"); // 账号可用余额：分
            if(null != balance){
                Long balanceLongValue = Long.parseLong(balance);
                if(balanceLongValue < order.getWithdrawalAmount()){
                    // 余额不足，提现失败，修改订单和流水状态
                    this.orderService.updateOrderStatusAndExternalOrderNo(null,orderNo,RACWStatusEnum.PAY_FAILURE.getValue());
                    return "提现失败：对公户余额不足";
                }
            } else {
                // 未查到余额，提现失败，修改订单和流水状态
                this.orderService.updateOrderStatusAndExternalOrderNo(null,orderNo,RACWStatusEnum.PAY_FAILURE.getValue());
                return "提现失败：查询对公户余额失败";
            }
        } else {
            // 未查到余额，提现失败，修改订单和流水状态
            this.orderService.updateOrderStatusAndExternalOrderNo(null,orderNo,RACWStatusEnum.PAY_FAILURE.getValue());
            return "提现失败：查询对公户余额失败";
        }

        // 组装业务参数
        PaidOrderDTO paidOrderDto = buildPaidOrderParams(paramsEntity,mainOrder,corporateAccount,order);

        // 调用代付银行卡接口，发起提现申请
        JSONObject resultObj = this.daifuApiService.paidOrder(paramsEntity,paidOrderDto);

        String externalOrderNo = null,upStatusCode = null,upResultMsg = null;
        if(null != resultObj){
            externalOrderNo = resultObj.getString("tradeNo");
            upStatusCode = resultObj.getString("bizCode");
            upResultMsg = resultObj.getString("bizCodeMsg");
        }
        // 更新订单主表信息
        this.orderService.updateOrderStatusAndExternalOrderNo(externalOrderNo,orderNo,RACWStatusEnum.PAYING.getValue());
        // 更新流水状态为"支付中"
        PayWaterEntity payWater = new PayWaterEntity();
        payWater.setOrderNo(orderNo);
        payWater.setUpdateTime(new Date());
        payWater.setUpdateUser(member.getMemberAccount());
        payWater.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
        payWater.setExternalOrderNo(externalOrderNo);
        payWater.setUpStatusCode(upStatusCode);
        payWater.setUpResultMsg(upResultMsg);
        this.payWaterService.updatePayStatus(payWater);

        // 开票订单可提现额度扣减
        this.invoiceOrderService.updateRemainingWithdrawAmount(order.getInvoiceOrderNo(),order.getWithdrawalAmount(),0);
        //保存历史记录
        InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(order.getInvoiceOrderNo());
        OrderEntity orderEntity = orderService.queryByOrderNo(order.getInvoiceOrderNo());
        if(invoiceOrderEntity!=null && orderEntity!=null) {
            this.invoiceOrderService.editAndSaveHistory(invoiceOrderEntity, mainOrder.getOrderStatus(), member.getMemberAccount(), "对公户提现额度核销");
        }
        // 添加对公户银行收款核销记录
        String[] ids = order.getCollectionRecordIds().split(",");
        Long changeAmount = order.getWithdrawalAmount();
        Long afterAmount = 0L;
        for(int i = 0 ; i < ids.length; i++) {
            CorporateAccountCollectionRecordEntity record = this.corporateAccountCollectionRecordService.findById(Long.valueOf(ids[i]));
            if(null != record){
                afterAmount = record.getRemainingWithdrawalAmount();
                if(changeAmount - afterAmount > 0){
                    changeAmount = changeAmount - afterAmount;
                }else{
                    afterAmount = changeAmount;
                    changeAmount = 0l;
                }
                this.collectionWithdrawalAmountChangeRecordService.addCollectionWithdrawalAmountChangeRecordEntity(order.getCorporateAccountId(),record.getBankCollectionRecordNo(),
                        afterAmount,record.getRemainingWithdrawalAmount(),afterAmount,orderNo,member.getMemberAccount(),"对公户提现收款记录核销",order.getInvoiceOrderNo());
                // 收款记录对应可提现金额扣减
                this.corporateAccountCollectionRecordService.updateRemainingWithdrawAmount(record.getId(),afterAmount,0);
            }
        }

        log.info("对公户提现申请处理成功：{}",JSON.toJSONString(resultObj));
        return null;
    }

    /**
     * @Description 组装代付银行卡相关参数
     * @Author  Kaven
     * @Date   2020/10/13 10:35
     * @Param
     * @Return
     * @Exception
    */
    private PaidOrderDTO buildPaidOrderParams(OemParamsEntity paramsEntity,OrderEntity mainOrder, CompanyCorporateAccountEntity corporateAccount, CorporateAccountWithdrawalOrderEntity order) {
        PaidOrderDTO paidOrderDto = new PaidOrderDTO();
        JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
        String productCode = jsonObject.getString("productCode");
        if(StringUtils.isBlank(productCode)){
            throw new BusinessException("未配置渠道代付产品编码！");
        } else if(DaifuApiService.PRODUCT_CODE_WJ.equals(productCode)){ // 根据productCode判断通道走向
            paidOrderDto.setEntrstPrjId(corporateAccount.getEntrustProjectCodeWj());
            paidOrderDto.setPrjUserId(corporateAccount.getProjectUseCodeWj());
        } else if(DaifuApiService.PRODUCT_CODE_ZX.equals(productCode)){
            paidOrderDto.setEntrstPrjId(corporateAccount.getEntrustProjectCode());
            paidOrderDto.setTxnStffId(corporateAccount.getVoucherMemberCode());
            paidOrderDto.setPrjUserId(corporateAccount.getProjectUseCode());
        } else {
            throw new BusinessException("不支持的代付通道，请联系管理员！");
        }
        paidOrderDto.setAmount(String.valueOf(mainOrder.getOrderAmount()));
        paidOrderDto.setOrderNo(mainOrder.getOrderNo());
        paidOrderDto.setDraweeAccountNo(corporateAccount.getCorporateAccount());
        paidOrderDto.setPayeeBankName(order.getArriveBankName());
        paidOrderDto.setPayeeCardNo(order.getArriveBankAccount());
        paidOrderDto.setPayeeName(order.getArriveUserName());
        return paidOrderDto;
    }

    @Override
    public CorpAccountWithdrawOrderVO listWithdrawOrderPage(ComCorpAccQuery query) throws BusinessException {
        log.info("查询对公户提现订单列表：{}",JSON.toJSONString(query));

        // 处理查询条件，时间格式化处理 YYYY-MM-DD HH:mm:ss
        if(StringUtils.isBlank(query.getMonth()) && StringUtils.isBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){
            // 默认查本月
            query.setStartDate(DateUtil.getMonFirstDay() + " 00:00:00");
            query.setEndDate(DateUtil.getMonLastDay() + " 23:59:59");
        }else if(StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){// 开始时间不为空，结束时间为空
            query.setDay(query.getStartDate());
        } else if(StringUtils.isNotBlank(query.getEndDate()) && StringUtils.isBlank(query.getStartDate())){// 结束时间不为空，开始时间为空
            query.setDay(query.getEndDate());
        } else if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())){ // 开始时间结束时间均不为空
            query.setStartDate(query.getStartDate() + " 00:00:00");
            query.setEndDate(query.getEndDate() + " 23:59:59");
        }

        CorpAccountWithdrawOrderVO orderVO = new CorpAccountWithdrawOrderVO();
        // 统计总提现金额
        Long totalAmount = this.mapper.countTotalWithdrawAmount(query);
        orderVO.setTotalAmount(totalAmount);

        // 分页查询提现订单列表
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CorpAccWithdrawOrderVO> list = this.mapper.listWithdrawOrder(query);
        // 处理订单类型
        list.stream().forEach(order -> {
            order.setOrderType(OrderTypeEnum.getByValue(Integer.parseInt(order.getOrderType())).getMessage());
        });

        PageInfo pageInfo = new PageInfo<CorpAccWithdrawOrderVO>(list);
        orderVO.setOrderList(pageInfo);
        return orderVO;
    }

    @Override
    public CorpAccountWithdrawOrderDetailVO getWithdrawOrderDetail(String orderNo) throws BusinessException {
        log.info("查询对公户提现订单详情：{}", orderNo);

        CorpAccountWithdrawOrderDetailVO detailVO = new CorpAccountWithdrawOrderDetailVO();// 返回值

        // 验证订单是否存在
        OrderEntity order = this.orderService.queryByOrderNo(orderNo);
        if(null == order){
            throw new BusinessException("订单不存在");
        }

        // 查询提现订单信息
        CorporateAccountWithdrawalOrderEntity t = new CorporateAccountWithdrawalOrderEntity();
        t.setOrderNo(orderNo);
        CorporateAccountWithdrawalOrderEntity withdrawOrder = this.selectOne(t);

        // 查询对公户信息
        CompanyCorporateAccountEntity corporateAccount = this.companyCorporateAccountService.findById(withdrawOrder.getCorporateAccountId());
        if(null == corporateAccount){
            throw new BusinessException("对公户不存在");
        }

        // 查询企业信息
        MemberCompanyEntity company = this.memberCompanyService.findById(corporateAccount.getCompanyId());
        if(null == company){
            throw new BusinessException("企业信息不存在");
        }

        detailVO.setOrderType(order.getOrderType());
        detailVO.setOrderStatus(order.getOrderStatus());
        detailVO.setOrderAmount(order.getOrderAmount());
        detailVO.setOrderNo(orderNo);
        detailVO.setRegisterName(company.getCompanyName());
        detailVO.setAddTime(order.getAddTime());
        detailVO.setBindBankCardNumber(com.yuqian.itax.util.util.StringUtil.mark(corporateAccount.getCorporateAccount(), '*', 4, -4));
        detailVO.setArriveUserName(com.yuqian.itax.util.util.StringUtil.mark(withdrawOrder.getArriveUserName(), '*', 1, 0));
        detailVO.setArriveBankAccount(com.yuqian.itax.util.util.StringUtil.mark(withdrawOrder.getArriveBankAccount(), '*', 4, -4));

        // 查询开票记录
        InvoiceOrderEntity invoiceOrder = this.invoiceOrderService.queryByOrderNo(withdrawOrder.getInvoiceOrderNo());
        if(null == invoiceOrder){
            throw new BusinessException("开票订单记录不存在");
        }
        detailVO.setInvoiceOrderNo(withdrawOrder.getInvoiceOrderNo());
        detailVO.setCompleteTime(invoiceOrder.getCompleteTime());
        detailVO.setCompanyName(invoiceOrder.getCompanyName());
        detailVO.setUseAmount(withdrawOrder.getWithdrawalAmount());
        detailVO.setTotalAmount(invoiceOrder.getInvoiceAmount());

        // 查询收款记录
        List<CorpAccountColsRecordsVO> collectionList = Lists.newArrayList();
        CollectionWithdrawalAmountChangeRecordEntity tt = new CollectionWithdrawalAmountChangeRecordEntity();
        tt.setOrderNo(orderNo);
        tt.setChangeType(2);
        List<CollectionWithdrawalAmountChangeRecordEntity> changeRecords = this.collectionWithdrawalAmountChangeRecordService.select(tt);
        changeRecords.stream().forEach(changeRecord -> {
            CorpAccountColsRecordsVO cacrv = new CorpAccountColsRecordsVO();
            cacrv.setBankCollectionRecordNo(changeRecord.getBankCollectionRecordNo());
            cacrv.setTotalAmount(changeRecord.getBeforeAmount());
            cacrv.setUseAmount(changeRecord.getChangeAmount());
            cacrv.setTradingTime(changeRecord.getChangeTime());
            collectionList.add(cacrv);
        });
        detailVO.setCollectionList(collectionList);

        return detailVO;
    }

    @Override
    public List<CorporateAccountWithdrawalOrderEntity> queryWithdrawOrderByInvoiceOrderNo(String invoiceOrderNO) {
        return mapper.queryWithdrawOrderByInvoiceOrderNo(invoiceOrderNO);
    }

}

