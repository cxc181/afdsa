package com.yuqian.itax.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yuqian.itax.agent.entity.*;
import com.yuqian.itax.agent.entity.query.OemAccessPartyQuery;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.service.*;
import com.yuqian.itax.capital.dao.UserBankCardMapper;
import com.yuqian.itax.capital.dao.UserCapitalAccountMapper;
import com.yuqian.itax.capital.dao.UserCapitalChangeRecordMapper;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.dto.UserCapitalAccountDTO;
import com.yuqian.itax.capital.entity.dto.UserRechargeDTO;
import com.yuqian.itax.capital.entity.dto.UserWithdrawDTO;
import com.yuqian.itax.capital.enums.CapitalChangeTypeEnum;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.capital.service.UserCapitalChangeRecordService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ErrorCodeConstants;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountWithdrawalOrderEntity;
import com.yuqian.itax.corporateaccount.service.CorporateAccountWithdrawalOrderService;
import com.yuqian.itax.coupons.dao.CouponsIssueRecordMapper;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueRecordStatusEnum;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.nabei.entity.APISignQueryRespVo;
import com.yuqian.itax.nabei.entity.SinglePayRespVo;
import com.yuqian.itax.nabei.service.NabeiApiService;
import com.yuqian.itax.order.dao.*;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.dto.AccessPartyRegisterOrderDTO;
import com.yuqian.itax.order.entity.dto.RegisterOrderDTO;
import com.yuqian.itax.order.entity.dto.ThirdPartyQueryInoiveInfoDTO;
import com.yuqian.itax.order.entity.dto.UpgradeOrderDTO;
import com.yuqian.itax.order.entity.query.*;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.dao.ParkBusinessAddressRulesMapper;
import com.yuqian.itax.park.entity.ParkBusinessAddressRulesEntity;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeWithTaxCodeVO;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.park.enums.ParkProcessMarkEnum;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkDisableWordService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.entity.RepayDetailVO;
import com.yuqian.itax.pay.enums.*;
import com.yuqian.itax.pay.service.CCBRepayService;
import com.yuqian.itax.pay.service.PartiallyRepayService;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.profits.entity.ProfitsDetailEntity;
import com.yuqian.itax.profits.enums.ProfitsDetailStatusEnum;
import com.yuqian.itax.profits.enums.ProfitsTypeEnum;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.snapshot.service.MemberSnapshotService;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.entity.query.ParkBusinessScopeQuery;
import com.yuqian.itax.system.entity.vo.IndustryInfoVO;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.tax.entity.CompanyTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.CompanyTaxBillVO;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.entity.vo.TaxCalculationVO;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.service.CompanyTaxBillChangeService;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.dao.*;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.dto.CompResApplyRecordDTO;
import com.yuqian.itax.user.entity.dto.CompanyCancelApiDTO;
import com.yuqian.itax.user.entity.dto.CompanyCertReturnApiDTO;
import com.yuqian.itax.user.entity.dto.CompanyCertUseApiDTO;
import com.yuqian.itax.user.entity.po.UpdateMemberPhonePO;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.*;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.entity.*;
import com.yuqian.itax.util.util.*;
import com.yuqian.itax.util.util.guojin.GuoJinUtil;
import com.yuqian.itax.workorder.service.WorkOrderService;
import com.yuqian.itax.yishui.entity.FastIssuingQueryResp;
import com.yuqian.itax.yishui.service.YiShuiService;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends BaseServiceImpl<OrderEntity, OrderMapper> implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RegisterOrderMapper registerOrderMapper;
    @Resource
    private InvoiceOrderMapper invoiceOrderMapper;
    @Resource
    private MemberCompanyMapper memberCompanyMapper;
    @Resource
    private RegisterOrderChangeRecordMapper registerOrderChangeRecordMapper;
    @Resource
    private InvoiceOrderChangeRecordMapper invoiceOrderChangeRecordMapper;
    @Resource
    private ParkBusinessAddressRulesMapper parkBusinessAddressRulesMapper;
    @Resource
    private CompanyCancelOrderMapper companyCancelOrderMapper;
    @Resource
    private ContRegisterOrderMapper contRegisterOrderMapper;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private RedisService redisService;
    @Autowired
    LogisticsInfoService logisticsInfoService;
    @Autowired
    private MemberOrderRelaService memberOrderRelaService;
    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private PayWaterService payWaterService;
    @Resource
    private CompanyInvoiceRecordMapper companyInvoiceRecordMapper;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private ProductService productService;
    @Resource
    private UserBankCardMapper userBankCardMapper;
    @Autowired
    private OrderAttachmentService orderAttachmentService;
    @Resource
    private WorkOrderService workOrderService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private OemParkRelaService oemParkRelaService;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private ProductParkRelaService productParkRelaService;
    @Autowired
    private ProfitsDetailService profitsDetailService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private OemService oemService;
    @Autowired
    private PartiallyRepayService partiallyRepayService;
    @Autowired
    private CompanyResourcesAddressService companyResourcesAddressService;
    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private CompanyInvoiceRecordService companyInvoiceRecordService;
    @Autowired
    private CompanyCancelOrderService companyCancelOrderService;
    @Autowired
    private CompanyCancelOrderChangeRecordService companyCancelOrderChangeRecordService;
    @Resource(name = "ccbRepayService")
    private CCBRepayService ccbRepayService;
    @Resource(name = "ccbZXRepayService")
    private CCBRepayService ccbZXRepayService;
    @Autowired
    private CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
    @Resource
    private CompanyResoucesApplyRecordMapper companyResoucesApplyRecordMapper;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private UserCapitalChangeRecordService userCapitalChangeRecordService;
    @Autowired
    private LogisCompanyService logisCompanyService;
    @Autowired
    private DictionaryService sysDictionaryService;
    @Autowired
    private CompanyResourcesAddressHistoryService companyResourcesAddressHistoryService;
    @Autowired
    private CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Autowired
    private InvoiceCategoryService invoiceCategoryService;
    @Resource
    private MemberAccountMapper memberAccountMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private NabeiApiService nabeiApiService;
    @Autowired
    private UserBankCardService userBankCardService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RegisterOrderChangeRecordService registerOrderChangeRecordService;
    @Resource
    private MemberLevelMapper memberLevelMapper;
    @Autowired
    private MemberConsumptionRecordService memberConsumptionRecordService;
    @Autowired
    private CompanyTaxBillService companyTaxBillService;
    @Autowired
    private InvoiceRecordService invoiceRecordService;
    @Autowired
    private InvoiceRecordChangeService invoiceRecordChangeService;
    @Autowired
    MemberAccountChangeService memberAccountChangeService;
    @Autowired
    MemberSnapshotService memberSnapshotService;
    @Autowired
    private CouponsIssueRecordService couponsIssueRecordService;
    @Autowired
    private CouponsService couponsService;
    @Resource
    private CouponsIssueRecordMapper couponsIssueRecordMapper;
    @Autowired
    private InvoiceInfoByOemService invoiceInfoByOemService;
    @Autowired
    private OemConfigService oemConfigService;
    @Autowired
    private MemberCompanyChangeService memberCompanyChangeService;
    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;
    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;
    @Resource
    private OemAccessPartyService oemAccessPartyService;
    @Resource
    TaxRulesConfigService taxRulesConfigService;
    @Resource
    CompanyTaxHostingService companyTaxHostingService;
    @Autowired
    private ParkDisableWordService parkDisableWordService;
    @Autowired
    private BusinessScopeService businessScopeService;
    @Autowired
    private RatifyTaxService ratifyTaxService;
    @Autowired
    private OssService ossService;
    @Autowired
    private CorporateAccountWithdrawalOrderService corporateAccountWithdrawalOrderService;
    @Autowired
    private InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Autowired
    private ParkBusinessscopeService parkBusinessScopeService;
    @Autowired
    private RegisterOrderGoodsDetailRelaService registerOrderGoodsDetailRelaService;
    @Autowired
    private BusinessscopeTaxcodeService businessScopeTaxCodeService;
    @Resource
    private UserCapitalChangeRecordMapper userCapitalChangeRecordMapper;
    @Resource
    private UserCapitalAccountMapper userCapitalAccountMapper;
    @Autowired
    private CompanyTaxBillChangeService companyTaxBillChangeService;
    @Autowired
    private RegisterPreOrderService registerPreOrderService;
    @Autowired
    private CompanyCorePersonnelService companyCorePersonnelService;
    @Autowired
    private MemberToSignYishuiService memberToSignYishuiService;
    @Autowired
    private YiShuiService yishuiService;

    private static final int SHAREHOLDER_NUMBER = 49;

    @Override
    public PageInfo<RegisterOrderVO> getOrderListPage(RegOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<RegisterOrderVO> list = this.orderMapper.listPages(query);

        for (int i = 0; i < list.size(); i++) {
            // 使用过优惠券的待付款订单携带优惠券信息
            if (list.get(i).getCouponsIssueId() != null) {
                if (list.get(i).getOrderStatus().equals("3")) {
                    CouponsIssueRecordEntity recordEntity = Optional.ofNullable(couponsIssueRecordService.findById(list.get(i).getCouponsIssueId())).orElseThrow(() -> new BusinessException("未查询到优惠券发放记录"));
                    CouponsEntity coupons = Optional.ofNullable(couponsService.findById(recordEntity.getCouponsId())).orElseThrow(() -> new BusinessException("未查询到优惠券信息"));
                    list.get(i).setCouponsAmount(coupons.getFaceAmount());
                } else {
                    list.get(i).setCouponsIssueId(null);
                }
            }
            // 历史核名驳回补充驳回项
            if (list.get(i).getOrderStatus().equals("8") && StringUtils.isBlank(list.get(i).getRejectedItem())) {
                list.get(i).setRejectedItem("1");
            }
        }
        return new PageInfo<RegisterOrderVO>(list);
    }

    @Override
    public List<OpenOrderVO> listOpenOrder(OrderQuery query) {
        query.setOrderType(OrderTypeEnum.REGISTER.getValue());
        return mapper.listOpenOrder(query);
    }

    @Override
    public List<InvOrderVO> listInvOrder(OrderQuery query) {
        query.setOrderType(OrderTypeEnum.INVOICE.getValue());
        return mapper.listInvOrder(query);
    }

    @Override
    public List<MemberLvUpOrderVO> listMemberLvUpOrder(OrderQuery query) {
        query.setOrderType(OrderTypeEnum.UPGRADE.getValue());
        return mapper.listMemberLvUpOrder(query);
    }

    @Override
    public PageInfo<OpenOrderVO> listPageOpenOrder(OrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listOpenOrder(query));
    }

    @Override
    public PageInfo<InvOrderVO> listPageInvOrder(OrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listInvOrder(query));
    }

    @Override
    public List<BatchInvOrderExportVO> invBatchExportOrderList(OrderQuery query) {
        return mapper.invBatchExportOrderList(query);
    }

    @Override
    public PageInfo<MemberLvUpOrderVO> listPageMemberLvUpOrder(OrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listMemberLvUpOrder(query));
    }

    @Override
    public List<OrderVO> queryNoticeOrderList(Long userId) {
        // 查询待通知的工商注册订单列表
        List<OrderVO> regOrderList = this.registerOrderMapper.queryNoticeRegOrderList(userId);
        // 查询待通知的开票订单列表
        List<OrderVO> invOrderList = this.invoiceOrderMapper.queryNoticeInvOrderList(userId);
        // 合并结果集
        regOrderList.addAll(invOrderList);
        return regOrderList;
    }

    @Override
    public OrderEntity queryByOrderNo(String orderNo) {
        return this.orderMapper.queryByOrderNo(orderNo);
    }

    @Transactional
    public MemberCompanyEntity saveCompanyInfo(OrderEntity orderEntity, RegisterOrderEntity regEntity, TaxPolicyEntity taxPolicyVO, String license, String businessLicenseCopy, String useraccount, Date date,String ein) {
        //保存我的企业
        MemberCompanyEntity entity = new MemberCompanyEntity();
        entity.setMemberId(orderEntity.getUserId());
        entity.setParkId(orderEntity.getParkId());
        entity.setEin(ein);
        entity.setOperatorName(regEntity.getOperatorName());
        entity.setCompanyType(regEntity.getCompanyType());
        entity.setCompanyName(regEntity.getRegisteredName());
        entity.setBusinessScope(regEntity.getBusinessScope());
        entity.setBusinessAddress(regEntity.getBusinessAddress());
        entity.setIndustryId(regEntity.getIndustryId());
        IndustryEntity industryEntity = industryService.findById(regEntity.getIndustryId());
        entity.setIndustry(Optional.ofNullable(industryEntity).map(IndustryEntity::getIndustryName).orElse(null));
        entity.setBusinessLicense(license);
        entity.setBusinessLicenseCopy(businessLicenseCopy);
        entity.setStatus(MemberCompanyStatusEnum.NORMAL.getValue());
        entity.setIsTopUp(0);
        entity.setOemCode(regEntity.getOemCode());
        entity.setEndTime(new DateTime(date).plusYears(1).toDate());
        entity.setAddUser(useraccount);
        entity.setAddTime(date);
        entity.setOperatorTel(regEntity.getContactPhone());
        entity.setOperatorEmail(regEntity.getEmail());
        entity.setAgentAccount(regEntity.getAgentAccount());
        entity.setIdCardFront(regEntity.getIdCardFront());
        entity.setIdCardNumber(regEntity.getIdCardNumber());
        entity.setIdCardReverse(regEntity.getIdCardReverse());
        entity.setIsOther(regEntity.getIsOther());
        entity.setOrderNo(regEntity.getOrderNo());
        entity.setRegisteredCapital(regEntity.getRegisteredCapital());
        entity.setTaxpayerType(regEntity.getTaxpayerType());
        memberCompanyMapper.insertSelective(entity);

        //公司年开票记录
        CompanyInvoiceRecordEntity compEntity = new CompanyInvoiceRecordEntity();
        compEntity.setCompanyId(entity.getId());
        compEntity.setTotalInvoiceAmount(taxPolicyVO.getTotalInvoiceAmount());
        compEntity.setRemainInvoiceAmount(taxPolicyVO.getTotalInvoiceAmount());
        compEntity.setUseInvoiceAmount(0L);
        compEntity.setYear(new SimpleDateFormat("yyyy").format(date));
        compEntity.setOemCode(entity.getOemCode());
        compEntity.setEndTime(entity.getEndTime());
        compEntity.setAddTime(date);
        compEntity.setAddUser(useraccount);
        companyInvoiceRecordMapper.insertSelective(compEntity);

        //保存/更新企业开票类目
        CompanyInvoiceCategoryEntity category = new CompanyInvoiceCategoryEntity();
        category.setOemCode(orderEntity.getOemCode());
        category.setOrderNo(orderEntity.getOrderNo());
        List<CompanyInvoiceCategoryEntity> cateLists = companyInvoiceCategoryService.select(category);
        if (CollectionUtil.isNotEmpty(cateLists)) {
            companyInvoiceCategoryService.updateCompanyIdByOrderNo(entity.getId(), orderEntity.getOrderNo(), orderEntity.getOemCode());
            return entity;
        }
        //获取开票类目
        InvoiceCategoryEntity t = new InvoiceCategoryEntity();
        t.setIndustryId(regEntity.getIndustryId());
        List<InvoiceCategoryEntity> invoiceCategoryList = invoiceCategoryService.select(t);
        if (CollectionUtil.isNotEmpty(invoiceCategoryList)) {
            category = new CompanyInvoiceCategoryEntity();
            category.setOemCode(orderEntity.getOemCode());
            category.setOrderNo(orderEntity.getOrderNo());
            category.setCompanyId(entity.getId());
            category.setIndustryId(regEntity.getIndustryId());
            category.setAddTime(date);
            category.setAddUser(useraccount);
            category.setAddUser("已领证批量添加开票类目");
            companyInvoiceCategoryService.addBatch(category, invoiceCategoryList.stream().map(InvoiceCategoryEntity::getCategoryName).collect(Collectors.toList()));
        }
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmCertificate(OrderEntity orderEntity, RegisterOrderEntity regEntity, TaxPolicyEntity taxPolicyVO, String license, String businessLicenseCopy, String useraccount,String ein) {
        Date date = new Date();
        //保存我的企业
        MemberCompanyEntity entity = saveCompanyInfo(orderEntity, regEntity, taxPolicyVO, license, businessLicenseCopy, useraccount, date,ein);
        //添加企业变更记录
        memberCompanyChangeService.insertChangeInfo(entity,useraccount,"企业新增");
        //修改订单状态
        mapper.updateOrderStatus(regEntity.getOrderNo(), RegOrderStatusEnum.COMPLETED.getValue(), useraccount, date);
        //保存工商注册订单变更记录
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(regEntity, record);
        record.setId(null);
        record.setAddTime(date);
        record.setAddUser(useraccount);
        record.setOrderStatus(RegOrderStatusEnum.COMPLETED.getValue());
        registerOrderChangeRecordMapper.insertSelective(record);

        //修改核心成员的企业id
        companyCorePersonnelService.updateCompanyCorePersonnelCompanyIdByOrderNo(entity.getId(),regEntity.getOrderNo());

        //保存通知消息 add ni.jiang
        saveNoticeMessage(orderEntity.getUserId(), regEntity.getId(), regEntity.getOrderNo(), useraccount, regEntity.getRegisteredName(), regEntity.getCompanyType(), orderEntity.getOemCode());
        //扣除自己资金
        userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getUserId(), 1, orderEntity.getPayAmount(), 0L, 0L, orderEntity.getPayAmount(), "开户订单已领证", useraccount, date, 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
        UserEntity oemUser = new UserEntity();
        oemUser.setOemCode(orderEntity.getOemCode());
        oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
        oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
        oemUser.setStatus(1);//状态 0-禁用 1-可用
        oemUser = userService.selectOne(oemUser);
        //给机构增加资金
        userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), oemUser.getId(), 2, orderEntity.getPayAmount(), orderEntity.getPayAmount(), 0L, 0L, "开户订单已领证", useraccount, date, 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
        //添加消费记录
        memberConsumptionRecordService.insertSelective(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getUserId(), orderEntity.getPayAmount(), useraccount, "开户订单已领证");
        //添加分润明细数据 add ni.jiang
        try {
            profitsDetailService.saveProfitsDetailByOrderNo(regEntity.getOrderNo(), useraccount);
        } catch (Exception e) {
            //分润失败
            OrderEntity order = this.queryByOrderNo(orderEntity.getOrderNo());
            order.setIsShareProfit(2);
            order.setProfitStatus(3);
            order.setUpdateTime(new Date());
            order.setUpdateUser("admin");
            order.setRemark("分润失败原因：" + e.getMessage());
            orderMapper.updateByPrimaryKeySelective(order);

            // 短信通知紧急联系人
            DictionaryEntity dict = this.dictionaryService.getByCode("emergency_contact");
            if (null != dict) {
                String dicValue = dict.getDictValue();
                String[] contacts = dicValue.split(",");
                for (String contact : contacts) {
                    Map<String, Object> map = new HashMap();
                    map.put("oemCode", order.getOemCode());
                    map.put("orderNo", orderEntity.getOrderNo());
                    this.smsService.sendTemplateSms(contact, order.getOemCode(), VerifyCodeTypeEnum.NOTICE.getValue(), map, 1);
                    log.info("分润失败发送通知给【" + contact + "】成功");
                }
            }
        }

        try {
            //保存企业资源数据 add ni.jiang
            ParkEntity parkEntity = parkService.findById(entity.getParkId());
            saveCompanyResourcesAddress(entity, 1, parkEntity.getParkName(), useraccount);
            saveCompanyResourcesAddress(entity, 2, parkEntity.getParkName(), useraccount);
            saveCompanyResourcesAddress(entity, 3, parkEntity.getParkName(), useraccount);
            saveCompanyResourcesAddress(entity, 4, parkEntity.getParkName(), useraccount);
            saveCompanyResourcesAddress(entity, 5, parkEntity.getParkName(), useraccount);
            saveCompanyResourcesAddress(entity, 6, parkEntity.getParkName(), useraccount);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("企业资源保存失败，企业id为：" + entity.getId());
        }
//        MemberOrderRelaEntity relaEntity = memberOrderRelaService.findById(orderEntity.getRelaId());
//        if (relaEntity == null) {
//            throw new BusinessException("订单关系表不存在");
//        }
        MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
        if (accEntity == null) {
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("regName", regEntity.getRegisteredName());
        smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.REGISTER_SUCCESS.getValue(), map, 2);
        if (Objects.equals(entity.getIsOther(), 1)) {
            //为他人办理给经营者发短信通知
            map.put("name", accEntity.getRealName());
            OemEntity oem = oemService.getOem(entity.getOemCode());
            map.put("serviceTel", Optional.ofNullable(oem).map(OemEntity::getCustomerServiceTel).orElse(""));
            smsService.sendTemplateSms(entity.getOperatorTel(), entity.getOemCode(), VerifyCodeTypeEnum.REGISTER_SUCCESS_OPERATOR.getValue(), map, 2);
        }
        try{
            //日统计
            statisticsMemberGeneralize(orderEntity, useraccount, 1);
            MemberAccountEntity memberAccountEntity=memberAccountMapper.selectByPrimaryKey(orderEntity.getUserId());
            //自动升级
            MemberLevelEntity levelEntity=memberLevelService.findById(memberAccountEntity.getMemberLevel());
            if(levelEntity.getLevelNo()<=MemberLevelEnum.BRONZE.getValue()){
                memberAutoUpdate(memberAccountEntity.getParentMemberId());
            }
            //税务顾问自己也要升级
            if(levelEntity.getLevelNo().equals(MemberLevelEnum.GOLD.getValue())){
                memberAutoUpdate(memberAccountEntity.getId());
            }

        }catch (BusinessException e){
            log.info(e.getErrorMsg());
        }

    }

    /**
     * add ni.jiang
     * 开户订单已领证后进行通知
     *
     * @param registeredName
     * @return
     */
    private int saveNoticeMessage(Long userId, Long regOrderId, String orderNo, String useraccount, String registeredName, Integer companyType, String oemCode) {
        DictionaryEntity entity = dictionaryService.getByCode("register_complete_notice_tmpl");
        String value = entity.getDictValue();
        value = value.replace("#companyName#", registeredName);
        String typeName = "企业";
//        if (companyType == 2) {
//            typeName = "个独企业";
//        } else if (companyType == 3) {
//            typeName = "有限合伙企业";
//        } else if (companyType == 4) {
//            typeName = "有限责任企业";
//        }
        value = value.replace("#companyType#", typeName);
        MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
        messageNoticeEntity.setOemCode(oemCode);
        messageNoticeEntity.setOpenMode(3);
        messageNoticeEntity.setBusinessType(2);
        messageNoticeEntity.setNoticeType(2);
        messageNoticeEntity.setNoticePosition("1,2");
        messageNoticeEntity.setNoticeTitle("企业注册进度通知");
        messageNoticeEntity.setNoticeContent(value);
        messageNoticeEntity.setStatus(0);
        messageNoticeEntity.setUserId(userId);
        messageNoticeEntity.setUserType(1);
        messageNoticeEntity.setSourceId(regOrderId);
        messageNoticeEntity.setOrderNo(orderNo);
        messageNoticeEntity.setAddUser(useraccount);
        messageNoticeEntity.setAddTime(new Date());
        return messageNoticeService.saveMessageNotice(messageNoticeEntity);
    }

    /**
     * 保存企业资源数据
     *
     * @param entity
     * @param resourcesType 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照
     * @param addr          资源所在地 默认都是在园区
     * @param updateUser    add ni.jiang
     */
    private void saveCompanyResourcesAddress(MemberCompanyEntity entity, Integer resourcesType, String addr, String updateUser) {
        CompanyResourcesAddressEntity companyResourcesAddressEntity = new CompanyResourcesAddressEntity();
        companyResourcesAddressEntity.setCompanyId(entity.getId());
        companyResourcesAddressEntity.setResourcesType(resourcesType);
        companyResourcesAddressEntity.setAddress(addr);
        companyResourcesAddressEntity.setOemCode(entity.getOemCode());
        companyResourcesAddressEntity.setAddTime(new Date());
        companyResourcesAddressEntity.setAddUser(updateUser);
        companyResourcesAddressService.insertSelective(companyResourcesAddressEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createIndustryOrder(Long userId, RegisterOrderDTO orderDto) throws BusinessException {
        log.info("创建开户订单service服务开始：{}，{}", userId, JSON.toJSONString(orderDto));

        // 开户订单逻辑校验
        registerOrderPreHandler(orderDto, userId, 0);

        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("开户订单创建失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        // 校验用户是否已过期
        if (member.getAuthStatus() == 1 && StringUtil.isNotBlank(member.getExpireDate())) {
            String[] split = member.getExpireDate().split("-");
            if (split.length == 2 && !"长期".equals(split[1])
                    && DateUtil.parseDefaultDate(split[1].replace(".","-")).before(DateUtil.parseDefaultDate(DateUtil.formatDefaultDate(new Date())))) {
                throw new BusinessException("认证身份证已过期");
            }
        }

        // 判断是否为员工，员工账号不允许注册企业
        if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
            throw new BusinessException("员工账号不允许注册企业！");
        }
        // 判断是否为企业身份，企业不允许为自己办理
        if (MemberAuthTypeEnum.COMPANY.getValue().equals(member.getMemberAuthType()) && orderDto.getIsOther().equals(0)) {
            throw new BusinessException("企业账号不能为自己办理个体户！");
        }

        MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());

        // 二要素验证通过，保存会员订单关系
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(userId, orderDto.getOemCode(), 1);
        if (more != null) {
            more.setMemberId(userId);
            // 设置会员等级
            more.setMemberLevel(level.getLevelNo());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            more.setOemCode(orderDto.getOemCode());
            more.setOemName(orderDto.getOemName());
            this.memberOrderRelaService.insertSelective(more);
        }

        // 补充更新用户身份证信息，需要判断是否是为自己办理
        if (orderDto.getIsOther() == 0) {
            // 判断用户上传的身份信息是否与用户实名的一致
//            if (!orderDto.getIdCardNumber().equals(member.getIdCardNo())) {
//                throw new BusinessException("上传身份证信息与实名信息不一致，请核对后再试");
//            }
            log.info("补充更新用户身份证信息:{},{},{}", userId, orderDto.getIdCardFront(), orderDto.getIdCardReverse());
            MemberAccountEntity updateUser = new MemberAccountEntity();
            updateUser.setId(userId);
            updateUser.setUpdateTime(new Date());
            updateUser.setUpdateUser(member.getMemberAccount());
            updateUser.setIdCardFront(orderDto.getIdCardFront());
            updateUser.setIdCardBack(orderDto.getIdCardReverse());
            updateUser.setExpireDate(orderDto.getExpireDate());
            updateUser.setIdCardAddr(orderDto.getIdCardAddr());
            this.memberAccountService.editByIdSelective(updateUser);
        }

        // 生成订单号，订单信息入库
        String orderNo = OrderNoFactory.getOrderCode(userId);
        if (!MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(orderDto.getCompanyType())) {
            // 查询注册预订单
            RegisterPreOrderEntity preOrder = registerPreOrderService.queryByMemberId(userId);
            if (null == preOrder) {
                throw new BusinessException("未查询到企业注册预订单信息");
            }
            orderNo = preOrder.getOrderNo();
            // 删除注册预订单
            registerPreOrderService.delById(preOrder.getId());
        }

        //校验占股比例总和为100%，注册资本等于股东投资金额总和。
        if(orderDto.getCompanyType()!=1){
            Example example = new Example(CompanyCorePersonnelEntity.class);
            example.createCriteria().andEqualTo("memberId", userId).andEqualTo("orderNo", orderNo);
            List<CompanyCorePersonnelEntity> l = companyCorePersonnelService.selectByExample(example);
            BigDecimal total = new BigDecimal(0);
            BigDecimal shareProportion = new BigDecimal(0);
            for(CompanyCorePersonnelEntity c : l){
                if(c.getIsShareholder()!=null&&c.getIsShareholder()==1){
                    total = total.add(c.getInvestmentAmount());
                    shareProportion = shareProportion.add(c.getShareProportion());
                }
            }
            if(total.compareTo(orderDto.getRegisteredCapital())!=0){
                throw new BusinessException("注册资本和股东投资金额总和不一致！");
            }
            if(shareProportion.compareTo(new BigDecimal(1))!=0){
                throw new BusinessException("占股比例总和不为100%！");
            }
        }

        // 保存订单主表信息
        orderDto.setOrderAmount(orderDto.getProdAmount());// 订单金额取产品金额
        OrderEntity mainOrder = transferMainOrder2Entity(userId, orderDto);
        mainOrder.setUserType(member.getMemberType());
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setAddUser(member.getMemberAccount());
        mainOrder.setOrderNo(orderNo);
        mainOrder.setSourceType(orderDto.getSourceType());
        this.insertSelective(mainOrder);

        // 保存注册订单与商品明细的关系
        List<RegisterOrderGoodsDetailRelaEntity> merchandises = orderDto.getMerchandises();
        List<RegisterOrderGoodsDetailRelaEntity> list = Lists.newArrayList();
        if (null != orderDto.getMerchandises() && !orderDto.getMerchandises().isEmpty()) {
            // 匹配税收分类编码
            Set<String> taxCodeSet = orderDto.getMerchandises().stream().map(RegisterOrderGoodsDetailRelaEntity::getTaxClassificationCode).collect(Collectors.toSet());
            List<ParkBusinessScopeWithTaxCodeVO> vos = parkBusinessScopeService.queryByTaxCode(taxCodeSet, orderDto.getParkId());
            if (null != vos && !vos.isEmpty()) {
                for (ParkBusinessScopeWithTaxCodeVO vo : vos) {
                    String goodsName = merchandises.stream().filter(merchandise -> merchandise.getTaxClassificationCode().equals(vo.getTaxClassificationCode())).map(RegisterOrderGoodsDetailRelaEntity::getGoodsName).collect(Collectors.toList()).get(0);
                    merchandises.removeIf(x -> x.getTaxClassificationCode().equals(vo.getTaxClassificationCode()));
                    RegisterOrderGoodsDetailRelaEntity goodsDetailEntity = new RegisterOrderGoodsDetailRelaEntity();
                    BeanUtil.copyProperties(vo, goodsDetailEntity);
                    goodsDetailEntity.setAddTime(orderDto.getAddTime());
                    goodsDetailEntity.setAddUser(orderDto.getAddUser());
                    goodsDetailEntity.setUpdateTime(orderDto.getAddTime());// 用于区分同步赋码后新增经营范围
                    goodsDetailEntity.setUpdateUser(orderDto.getAddUser());
                    goodsDetailEntity.setOrderNo(orderNo);
                    goodsDetailEntity.setGoodsName(goodsName);
                    list.add(goodsDetailEntity);
                }
                // 获取匹配到的经营范围
                Set<String> collect = list.stream().map(RegisterOrderGoodsDetailRelaEntity::getBusinessscopeName).collect(Collectors.toSet());
                orderDto.setTaxCodeBusinessScope(String.join(";", collect));
                collect.addAll(Sets.newHashSet(orderDto.getBusinessScope().split(";")));
                orderDto.setBusinessScope(String.join(";", collect));
            }
            for (RegisterOrderGoodsDetailRelaEntity merchandise : merchandises) merchandise.setOrderNo(orderNo);
            if (!list.isEmpty()) {
                ObjectUtil.copyListObject(list, merchandises, RegisterOrderGoodsDetailRelaEntity.class);
            }
            registerOrderGoodsDetailRelaService.batchAdd(merchandises);
        }

        // 保存工商订单信息
        RegisterOrderEntity regOrder = transferRegOrderDto2Entity(orderDto);
        regOrder.setOrderNo(orderNo);
        regOrder.setCompanyType(orderDto.getProdType());
        regOrder.setAddUser(member.getMemberAccount());
        regOrder.setOrderAmount(mainOrder.getOrderAmount());
        regOrder.setPayAmount(mainOrder.getPayAmount());
        regOrder.setDiscountAmount(mainOrder.getDiscountAmount());
        regOrder.setPayType(null == orderDto.getPayType() ? 1 : orderDto.getPayType()); // 默认在线支付
        regOrder.setRegisteredCapital(orderDto.getRegisteredCapital());
        this.registerOrderService.insertSelective(regOrder);

        //保存工商注册订单变更记录
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(regOrder, record);
        record.setId(null);
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setOrderStatus(RegOrderStatusEnum.TO_BE_SIGN.getValue());
        this.registerOrderChangeRecordMapper.insertSelective(record);

        // 保存企业开票类目
        this.companyInvoiceCategoryService.addByIndustryId(orderDto.getOemCode(), orderNo, orderDto.getIndustryId(), member.getMemberAccount());

        return orderNo;
    }

    /**
     * @Description 开户订单逻辑校验
     * @Author Kaven
     * @Date 2020/3/13 14:08
     * @Param orderDto
     * @Return
     * @Exception
     */
    public void registerOrderPreHandler(RegisterOrderDTO orderDto, Long userId, int type) {
        log.info("开户订单校验开始：{}", JSON.toJSONString(orderDto));

        // 查询产品信息
        ProductEntity product = this.productService.findById(orderDto.getProductId());
        if (null == product) {
            throw new BusinessException("开户订单创建失败，产品信息不存在");
        }

        // 判断产品类型
        if (product.getProdType() > ProductTypeEnum.LIMITED_LIABILITY.getValue()) {
            throw new BusinessException("所选产品类型为【" + ProductTypeEnum.getByValue(product.getProdType()) + "】不允许开户");
        }
        // 产品参数设置
        orderDto.setProductName(product.getProdName());
        orderDto.setProdAmount(product.getProdAmount());
        orderDto.setProdType(product.getProdType());

        // 查询园区信息
        ParkEntity park = this.parkService.findById(orderDto.getParkId());
        if (null == park) {
            throw new BusinessException("开户订单创建失败，园区不存在");
        }
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            throw new BusinessException("开户订单创建失败，园区状态不正确，只有已上架的园区才允许开户");
        }
        // 查询园区是否属于当前OEM机构
        OemParkRelaEntity t = new OemParkRelaEntity();
        t.setParkId(orderDto.getParkId());
        t.setOemCode(orderDto.getOemCode());
        List<OemParkRelaEntity> opreList = this.oemParkRelaService.select(t);
        if (null == opreList || opreList.size() == 0) {
            throw new BusinessException("开户订单创建失败，所选园区不属于当前OEM机构");
        }

        // 园区经营地址规则配置校验  add by Kaven 2020-05-22
        ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity = new ParkBusinessAddressRulesEntity();
        parkBusinessAddressRulesEntity.setParkId(orderDto.getParkId());
        List<ParkBusinessAddressRulesEntity> list = this.parkBusinessAddressRulesMapper.select(parkBusinessAddressRulesEntity);
        if (null == list || list.size() != 1) {
            throw new BusinessException("开户订单创建失败，园区经营地址规则配置错误！");
        }

        // 判断产品和园区关系是否正确
        ProductParkRelaEntity ppre = new ProductParkRelaEntity();
        ppre.setParkId(orderDto.getParkId());
        ppre.setProductId(orderDto.getProductId());
        ProductParkRelaEntity rela = this.productParkRelaService.selectOne(ppre);
        if (null == rela) {
            throw new BusinessException("开户订单创建失败，未找到所选产品和园区对应关系");
        }

        // 判断行业是否存在
        IndustryEntity industry = this.industryService.findById(orderDto.getIndustryId());
        if (null == industry) {
            throw new BusinessException("开户订单创建失败，行业类型不存在");
        }
        orderDto.setExampleName(industry.getExampleName());// 设值示例名称

        // 查询oem机构信息
        OemEntity oem = oemService.getOem(orderDto.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }
        orderDto.setOemName(oem.getOemName());

        if (StringUtils.isBlank(orderDto.getExpireDate())) {
            throw new BusinessException("身份证有效期不能为空");
        }

        // 校验经营者年龄
        String year = orderDto.getIdCardNumber().substring(6, 10);
        if (!StringUtils.isNumeric(year)) {
            throw new BusinessException("身份证号格式不正确");
        }
        String age = new BigDecimal(DateUtil.getYear(new Date())).subtract(new BigDecimal(year)).toString();
        DictionaryEntity rangeOfOperator = dictionaryService.getByCode("age_range_of_operator");
        if (null == rangeOfOperator) {
            throw new BusinessException("未配置经营者年龄范围");
        }
        boolean inTheInterval = IntervalUtil.isInTheInterval(age, rangeOfOperator.getDictValue());
        if (!inTheInterval) {
            throw new BusinessException("经营者年龄不符合注册要求");
        }

        // 验证身份证有效期格式
        String regex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))-((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))";
        if (!(orderDto.getExpireDate().contains("长期") || Pattern.matches(regex, orderDto.getExpireDate()))) {
            throw new BusinessException("身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd");
        }

        // 验证用户身份证有效期是否在有效范围内
        String[] dateArr = orderDto.getExpireDate().split("-");
        if (dateArr.length == 2 && !dateArr[1].contains("长期")) {
            String endDate = dateArr[1].replace(".", "").replace(".", "");
            String nowDate = DateUtil.formatDefaultDate(new Date()).replace("-", "").replace("-", "");
            if (Long.parseLong(endDate) < Long.parseLong(nowDate)) {
                throw new BusinessException("经营者身份证已过期，请退出后重新上传");
            }
        }

        // 字号检查：排查字号、备选字号对应的注册名称是否在会员企业表存在
        String result = this.memberCompanyMapper.checkCompanyNameByShopName(orderDto.getOemCode(), orderDto.getShopName(), orderDto.getShopNameOne(), orderDto.getShopNameTwo());
        if (StringUtils.isNotBlank(result)) {
            throw new BusinessException(result);
        }

        // 验证字号是否合法正则
        String pattern = "^[\u4e00-\u9fa5]{2,6}";
        if(StringUtils.isNotBlank(orderDto.getShopNameOne())){
            if(!Pattern.matches(pattern,orderDto.getShopNameOne())){
                throw new BusinessException(ResultConstants.SHOP_NAME_ONE_INVALID);
            }
        }
        if(StringUtils.isNotBlank(orderDto.getShopNameTwo())){
            if(!Pattern.matches(pattern,orderDto.getShopNameTwo())){
                throw new BusinessException(ResultConstants.SHOP_NAME_TWO_INVALID);
            }
        }

        // 禁用字号校验
        parkDisableWordService.checkoutDisableWord(park.getId(), orderDto.getShopName(), orderDto.getShopNameOne(), orderDto.getShopNameTwo());

        // 如果是为他人办理，需进行二要素认证
        if (orderDto.getIsOther() == 1) {
            /**
             * 身份证/姓名的二要素验证
             */
            //读取要素认证相关配置 paramsType=5
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(orderDto.getOemCode(), 5);
            if (null == paramsEntity) {
                throw new BusinessException("未配置身份二要素相关信息！");
            }
            // agentNo
            String agentNo = paramsEntity.getAccount();
            // signKey
            String signKey = paramsEntity.getSecKey();
            // authUrl
            String authUrl = paramsEntity.getUrl();
            String authResult = AuthKeyUtils.auth2Key(agentNo, signKey, authUrl, orderDto.getOperatorName(), orderDto.getIdCardNumber(),paramsEntity.getParamsValues());

            if (StringUtils.isBlank(authResult)) {
                throw new BusinessException("二要素认证失败");
            }
            JSONObject resultObj = JSONObject.parseObject(authResult);

            if (!"00".equals(resultObj.getString("code"))) {
                throw new BusinessException("二要素认证失败：" + resultObj.getString("msg"));
            }
        }

        // 非个体户企业注册股东数校验
        /*if (type == 0 && !MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(orderDto.getCompanyType())) {
            // 股东查询
            List<CompanyCorePersonnelEntity> persons = companyCorePersonnelService.querylist(userId, CompanyCorePersonnelTypeEnum.SHAREHOLDER.getValue());
            if (CollectionUtil.isEmpty(persons)) {
                throw new BusinessException("未添加股东信息");
            }
            // 个人独资企业股东数限1人
            if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(orderDto.getCompanyType()) && persons.size() != 1) {
                throw new BusinessException("个人独资企业股东数限1人");
            } else if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(orderDto.getCompanyType())
                    || MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(orderDto.getCompanyType())) {
                // 查询股东数限制
                int number = SHAREHOLDER_NUMBER;
                // 获取公司股东数配置
                String shareholderNumber = dictionaryService.getValueByCode("shareholder_number");
                if (StringUtil.isNotBlank(shareholderNumber)) {
                    number = Integer.parseInt(shareholderNumber);
                }
                if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(orderDto.getCompanyType()) && persons.size() < 2) {
                    throw new BusinessException("股东数少于2人");
                } else if (persons.size() > number) {
                    throw new BusinessException("股东数超过限制");
                }
            }
        }*/

        log.info("开户订单校验完成...");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> updateRegOrderFile(Long userId, String fileUrl, String orderNo, Integer step, String versionCode) throws BusinessException {
        log.info("补充更新签名图片地址、认证视频地址或者其他资料地址service开始...");

        MemberAccountEntity member =   member = this.memberAccountService.findById(userId);;
        if (null == member) {
            throw new BusinessException("操作失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        // 判断订单是否存在
        OrderEntity order = this.orderMapper.queryByOrderNo(orderNo);
        if (null == order || (null != userId && !order.getUserId().equals(userId))) {
            throw new BusinessException("操作失败，用户订单不存在！");
        }
        // 查询园区
        ParkEntity park = this.parkService.findById(order.getParkId());

        if (order.getOrderStatus() > RegOrderStatusEnum.TO_BE_VIDEO.getValue() && step != 3
                && !RegOrderStatusEnum.TO_CREATE.getValue().equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态为【" + RegOrderStatusEnum.getByValue(order.getOrderStatus()) + "】不允许上传");
        }

        Integer orderStatus = null;// 临时变量，存储订单状态
        Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果

        if (step == 1) {
            /*// 获取OEM系统版本号，进行比对，进而判断是否需要走视频认证
            OemEntity oem = this.oemService.getOem(order.getOemCode());
            if (null != oem && versionCode.equals(oem.getVersionCode())) {
                resultMap.put("checkFlag", 1);// 过审标识，1表示过审 0表示非过审
                log.info("过审版本，跳过视频认证，直接到付款状态");
                orderStatus = RegOrderStatusEnum.TO_BE_PAY.getValue();
            } else if(StringUtils.isNotBlank(park.getParkCode()) && park.getParkCode().startsWith("CSYQ")){ // 如果是长沙园区，无需视频认证，订单状态直接置为"待付款"
                log.info("长沙园区，无需视频认证，直接到待付款状态");
                orderStatus = RegOrderStatusEnum.TO_BE_PAY.getValue();
                resultMap.put("checkFlag", 0);// 过审标识，1表示过审 0表示非过审
            }else {
                // 非过审版本，走正常逻辑
                orderStatus = RegOrderStatusEnum.TO_BE_VIDEO.getValue();
                resultMap.put("checkFlag", 0);// 过审标识，1表示过审 0表示非过审
            }*/
            // 获取流程标记
            ProductParkRelaEntity productParkRelaEntity = new ProductParkRelaEntity();
            productParkRelaEntity.setProductId(order.getProductId());
            productParkRelaEntity.setParkId(park.getId());
            productParkRelaEntity = productParkRelaService.selectOne(productParkRelaEntity);
            if (null != productParkRelaEntity.getProcessMark() && ParkProcessMarkEnum.VIDEO.getValue().equals(productParkRelaEntity.getProcessMark())) {
                orderStatus = RegOrderStatusEnum.TO_BE_VIDEO.getValue();
            } else {
                orderStatus = RegOrderStatusEnum.TO_BE_PAY.getValue();
            }
            // 更新订单状态
            this.updateOrderStatus(member.getMemberAccount(), orderNo, orderStatus);

            // 更新工商注册订单表签名单信息
            this.registerOrderService.updateSignOrVideoAddr(orderNo, fileUrl, 1);

        } else if (step == 2) {
            if (RegOrderStatusEnum.TO_BE_SIGN.getValue().equals(order.getOrderStatus())) {
                throw new BusinessException("请先上传签名信息");
            }
            // 更新订单状态，需求变更：状态更新为“待付款”
            orderStatus = RegOrderStatusEnum.TO_BE_PAY.getValue();
            this.updateOrderStatus(member.getMemberAccount(), orderNo, orderStatus);

            // 更新工商注册订单表短视频地址
            this.registerOrderService.updateSignOrVideoAddr(orderNo, fileUrl, 2);

        } else if (step == 3) {
            // 已取消的订单不允许上传资料
            if (RegOrderStatusEnum.CANCELLED.getValue().equals(order.getOrderStatus())) {
                throw new BusinessException("已取消的订单不允许上传资料");
            }

            // 判断是否达到上传文件上限 add by Kaven 2020-04-15
            OrderAttachmentEntity t = new OrderAttachmentEntity();
            t.setOrderNo(orderNo);
            List<OrderAttachmentEntity> list = this.orderAttachmentService.select(t);
            if (list.size() > 100) {
                throw new BusinessException("上传附件数超过系统上限，最多只能上传100张");
            }

            // 前端上传补充资料成功后，添加订单附件上传记录
            String[] fileUrlArray = fileUrl.split(",");
            log.info("补充资料附件地址：{}", fileUrl);

            for (String url : fileUrlArray) {
                OrderAttachmentEntity attachment = new OrderAttachmentEntity();
                attachment.setAddTime(new Date());
                attachment.setAddUser(member.getMemberAccount());
                attachment.setMemberId(userId);
                attachment.setAttachmentAddr(url);
                attachment.setOrderNo(orderNo);
                attachment.setOemCode(order.getOemCode());
                attachment.setOrderType(2);
                this.orderAttachmentService.insertSelective(attachment);
            }

        } else {
            throw new BusinessException("操作失败，未知操作类型！");
        }

        // 补充资料不需要保存订单变更记录
        if (step == 1 || step == 2) {
            // 保存工商注册订单变更记录
            RegisterOrderEntity t = new RegisterOrderEntity();
            t.setOrderNo(orderNo);
            RegisterOrderEntity regOrder = this.registerOrderService.selectOne(t);
            RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();

            BeanUtils.copyProperties(regOrder, record);
            record.setOrderStatus(orderStatus);
            if (step == 1) {
                record.setSignImg(fileUrl);
            } else {
                record.setVideoAddr(fileUrl);
            }

            record.setId(null);
            record.setAddTime(new Date());
            record.setAddUser(member.getMemberAccount());
            record.setOrderStatus(orderStatus);
            this.registerOrderChangeRecordMapper.insertSelective(record);
        }
        log.info("补充更新签名图片地址、认证视频地址或者其他资料地址service处理结束。");
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(String payNo, String upOrderNo, Date payDate, String code, String upStatusCode, String message) {
        String redisTime = (System.currentTimeMillis() + 10000) + "";
        boolean lockResult = redisService.lock(RedisKey.REGISTER_ORDER_PAY_LOCK_KEY + payNo, redisTime, 60);
        if(!lockResult){
            throw new BusinessException("请勿重复提交！");
        }
        try {
            // 根据返回编码判断待更新的订单状态
            PayWaterEntity orderPay = new PayWaterEntity();
            orderPay.setPayNo(payNo);
            orderPay.setExternalOrderNo(upOrderNo);
            orderPay.setPayTime(payDate);
            orderPay.setUpdateTime(payDate);
            orderPay.setUpStatusCode(upStatusCode);
            orderPay.setUpResultMsg(message);
            // 根据流水号和流水状态查询订单信息
            PayWaterEntity pwe = new PayWaterEntity();
            pwe.setPayNo(payNo);
            pwe.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
            pwe = this.payWaterService.selectOne(pwe);

            // 添加订单状态的逻辑判断，防止重复回调 add by Kaven 2020-03-03
            if (pwe != null) {
                String orderNo = pwe.getOrderNo();
                // 查询订单
                OrderEntity order = this.orderMapper.queryByOrderNo(orderNo);
                // 查询会员订单关系
                MemberOrderRelaEntity more = this.memberOrderRelaService.findById(order.getRelaId());

                // 查询会员信息
                MemberAccountEntity member = null;
                if (more != null) {
                    member = this.memberAccountService.findById(more.getMemberId());
                } else {
                    member = this.memberAccountService.findById(order.getUserId());
                }

                if (MessageEnum.SUCCESS.getValue().equals(code)) {
                    // 订单支付成功，根据订单类型更新不同状态
                    if (OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType())) {// 开户订单回调
                        log.info("开户订单支付回调成功，处理订单后续逻辑……");

                        // 支付成功，处理订单后续业务并返回，如果是长沙园区，状态改为"待身份验证"
                        int regOrderStatus = RegOrderStatusEnum.TO_BE_SURE.getValue();// 默认取"审核中"状态

                        // 2.3需求变更：所有开户订单支付完成后状态统一置为"审核中"
                        log.info("开户订单支付成功，修改订单主表状态：{}，{}", orderNo, RegOrderStatusEnum.TO_BE_SURE.getValue());
                        this.updateOrderStatus(null, orderNo, regOrderStatus);

                        this.registerOrderService.successPayHandle(order, member.getId(), regOrderStatus);
                    } else if (OrderTypeEnum.UPGRADE.getValue().equals(order.getOrderType())) {// 会员升级订单回调
                        // 升级成功，更新订单主表状态
                        this.updateOrderStatus(null, orderNo, MemberOrderStatusEnum.COMPLETED.getValue());
                        log.info("升级成功，修改订单主表状态：{}，{}", orderNo, MemberOrderStatusEnum.COMPLETED.getValue());

                        // 升级成功订单处理
                        this.registerOrderService.memberUpgradePaySuccess(null, null, order.getOemCode(), orderNo, member.getId(), 2);

                        // 会员升级订单分润，添加分润明细数据 add ni.jiang
                        try {
                            profitsDetailService.saveProfitsDetailByOrderNo(order.getOrderNo(), "admin");
                        } catch (Exception e) {
                            //分润失败
                            log.error("分润失败：{}", e.getMessage());
                            order.setIsShareProfit(2);
                            order.setProfitStatus(3);
                            order.setUpdateTime(new Date());
                            order.setUpdateUser("admin");
                            order.setRemark("分润失败原因：" + e.getMessage());
                            orderMapper.updateByPrimaryKeySelective(order);

                            // 短信通知紧急联系人
                            DictionaryEntity dict = this.dictionaryService.getByCode("emergency_contact");
                            if (null != dict) {
                                String dicValue = dict.getDictValue();
                                String[] contacts = dicValue.split(",");
                                for (String contact : contacts) {
                                    Map<String, Object> map = new HashMap();
                                    map.put("oemCode", order.getOemCode());
                                    map.put("orderNo", orderNo);
                                    this.smsService.sendTemplateSms(contact, order.getOemCode(), VerifyCodeTypeEnum.NOTICE.getValue(), map, 1);
                                    log.info("分润失败发送通知给【" + contact + "】成功");
                                }
                            }
                        }

                    } else if (OrderTypeEnum.ENCHARGE.getValue().equals(order.getOrderType())) {// 用户充值订单回调
                        // 充值成功，更新订单主表状态
                        this.updateOrderStatus(null, orderNo, RACWStatusEnum.PAYED.getValue());
                        log.info("充值成功，修改订单主表状态：{}，{}", orderNo, RACWStatusEnum.PAYED.getValue());

                        // 往用户资金账户入账并保存资金变更记录
                        this.userCapitalAccountService.addBalance(order.getOemCode(), orderNo, order.getOrderType(), order.getUserId(), 1, order.getPayAmount(), "", "", new Date(), order.getWalletType());
                    }
                    // 设置支付流水状态
                    orderPay.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                    // 更新支付流水
                    this.payWaterService.updatePayWater(orderPay);

                } else {
                    // 支付失败，更新订单主表状态
                    Integer orderStatus = null;
                    if (OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType())
                            && !RegOrderStatusEnum.TO_BE_SURE.getValue().equals(order.getOrderStatus()) && order.getOrderStatus() < 4) {
                        orderStatus = RegOrderStatusEnum.TO_BE_PAY.getValue();
                    } else if (OrderTypeEnum.UPGRADE.getValue().equals(order.getOrderType())) {
                        orderStatus = MemberOrderStatusEnum.PAYING.getValue();
                    } else if (OrderTypeEnum.ENCHARGE.getValue().equals(order.getOrderType())) {
                        orderStatus = RACWStatusEnum.PAY_FAILURE.getValue();
                    }
                    // 设置支付流水状态
                    orderPay.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
                    // 更新订单主表状态
                    if (null != orderStatus) {
                        this.updateOrderStatus(null, orderNo, orderStatus);
                    }
                    // 更新支付流水
                    this.payWaterService.updatePayWater(orderPay);
                    // 如果是企业注册，判断该订单是否使用过优惠券
                    if (OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType())) {
                        RegisterOrderEntity registerOrder = Optional.ofNullable(registerOrderMapper.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到注册订单"));
                        if (Objects.nonNull(registerOrder.getCouponsIssueId())) {
                            CouponsIssueRecordEntity issueRecord = Optional.ofNullable(couponsIssueRecordService.findById(registerOrder.getCouponsIssueId())).orElseThrow(() -> new BusinessException("未查询到优惠券发放记录"));
                            CouponsEntity couponsEntity = Optional.ofNullable(couponsService.findById(issueRecord.getCouponsId())).orElseThrow(() -> new BusinessException("未查询到优惠券信息"));
                            // 回滚优惠券发放记录
                            issueRecord.setUseTime(null);
                            issueRecord.setUpdateTime(new Date());
                            issueRecord.setUpdateUser(member.getMemberAccount());
                            issueRecord.setRemark("支付失败，回滚优惠券");
                            if (couponsEntity.getEndDate().before(new Date())) {
                                issueRecord.setStatus(CouponsIssueRecordStatusEnum.STALE.getValue());
                            } else {
                                issueRecord.setStatus(CouponsIssueRecordStatusEnum.UNUSED.getValue());
                            }
                            couponsIssueRecordMapper.updateByPrimaryKey(issueRecord);
                            // 回滚订单支付金额
                            Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(member.getId(), order.getProductId(), member.getOemCode(),order.getParkId());
                            order.setPayAmount(payAmount);
                            order.setProfitAmount(payAmount);
                            orderService.editByIdSelective(order);
                            // 回滚注册订单支付金额
                            registerOrder.setPayAmount(payAmount);
                            registerOrder.setCouponsIssueId(null);
                            registerOrderMapper.updateByPrimaryKey(registerOrder);
                        }
                        // 回滚注册订单变更记录支付金额
                        RegisterOrderChangeRecordEntity recordEntity = new RegisterOrderChangeRecordEntity();
                        BeanUtils.copyProperties(registerOrder, recordEntity);// 参数拷贝
                        recordEntity.setId(null);
                        recordEntity.setAddTime(new Date());
                        recordEntity.setAddUser(member.getMemberAccount());
                        recordEntity.setOrderStatus(null == orderStatus ? order.getOrderStatus() : RegOrderStatusEnum.TO_BE_PAY.getValue());
                        this.registerOrderChangeRecordMapper.insertSelective(recordEntity);
                    }
                }
            }
        }catch (Exception e){
            throw new BusinessException(e.getMessage());
        }finally {
            redisService.unlock(RedisKey.REGISTER_ORDER_PAY_LOCK_KEY + payNo, redisTime);
        }
        log.info("微信支付回调业务处理结束...");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(String account, String orderNo, Integer orderStatus) {
        // 判断订单类型
        OrderEntity order = this.queryByOrderNo(orderNo);
        if(OrderTypeEnum.CORPORATE_WITHDRAW.getValue().intValue() == order.getOrderType().intValue()){
            // 更新流水状态
            PayWaterEntity payWater = new PayWaterEntity();
            payWater.setOrderNo(orderNo);
            payWater.setUpdateTime(new Date());
            payWater.setUpdateUser("admin");
            payWater.setPayStatus(RACWStatusEnum.PAYED.getValue().intValue() == orderStatus.intValue() ? PayWaterStatusEnum.PAY_SUCCESS.getValue() : PayWaterStatusEnum.PAY_FAILURE.getValue());
            this.payWaterService.updatePayStatus(payWater);
        }
        this.orderMapper.updateOrderStatus(orderNo, orderStatus, account, new Date());
    }


    @Override
    public void batchUpdateOrderChannelPushState() {
        this.orderMapper.batchUpdateOrderChannelPushState();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatusAndExternalOrderNo(String externalOrderNo, String orderNo, Integer orderStatus) {
        log.info("更新订单主表状态和外部订单号：{},{},{}",externalOrderNo,orderNo,orderStatus);
        // 判断订单类型
        OrderEntity order = this.queryByOrderNo(orderNo);
        if(OrderTypeEnum.CORPORATE_WITHDRAW.getValue().intValue() == order.getOrderType().intValue()){
            // 更新流水状态
            PayWaterEntity payWater = new PayWaterEntity();
            payWater.setOrderNo(orderNo);
            payWater.setUpdateTime(new Date());
            payWater.setUpdateUser("admin");
            payWater.setPayStatus(RACWStatusEnum.PAYED.getValue().intValue() == orderStatus.intValue() ? PayWaterStatusEnum.PAY_SUCCESS.getValue() : PayWaterStatusEnum.PAY_FAILURE.getValue());
            this.payWaterService.updatePayStatus(payWater);
        }
        this.orderMapper.updateOrderStatusAndExternalOrderNo(externalOrderNo, orderStatus, orderNo, new Date());
    }

    /**
     * @Description 构建开户订单主表数据
     * @Author Kaven
     * @Date 2019/12/10 16:51
     * @Param orderDto
     * @Return OrderEntity
     * @Exception BusinessException
     */
    private OrderEntity transferMainOrder2Entity(Long userId, RegisterOrderDTO orderDto) throws BusinessException {
        OrderEntity mainOrder = new OrderEntity();
        MemberAccountEntity memberAccountEntity=memberAccountMapper.selectByPrimaryKey(userId);
        mainOrder.setAddTime(new Date());
        mainOrder.setUserId(userId);
        mainOrder.setSourceType(orderDto.getSourceType());
        mainOrder.setOrderType(OrderTypeEnum.REGISTER.getValue());
        mainOrder.setOrderStatus(RegOrderStatusEnum.TO_BE_SIGN.getValue());
        mainOrder.setProductId(orderDto.getProductId());
        mainOrder.setProductName(orderDto.getProductName());
        mainOrder.setChannelServiceId(memberAccountEntity.getChannelServiceId());
        mainOrder.setChannelEmployeesId(memberAccountEntity.getChannelEmployeesId());
        mainOrder.setChannelCode(memberAccountEntity.getChannelCode());
        mainOrder.setChannelProductCode(memberAccountEntity.getChannelProductCode());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(orderDto.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }
        mainOrder.setChannelUserId(memberAccountEntity.getChannelUserId());
        mainOrder.setOemCode(orderDto.getOemCode());
        mainOrder.setParkId(orderDto.getParkId());
        mainOrder.setOrderAmount(orderDto.getOrderAmount());
        mainOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        mainOrder.setIsSelfPaying(orderDto.getIsSelfPaying());
        mainOrder.setPayerName(orderDto.getPayerName());
        mainOrder.setExternalOrderNo(orderDto.getExternalOrderNo());

        // 计算订单支付金额，如有折扣
        Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(userId, orderDto.getProductId(), orderDto.getOemCode(), orderDto.getParkId());
        mainOrder.setPayAmount(payAmount);
        mainOrder.setDiscountAmount(orderDto.getOrderAmount() - payAmount);//优惠金额

        /**
         *  判断是否存在特价活动，
         *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
         */
        ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
        productDiscountActivityAPIDTO.setOemCode(orderDto.getOemCode());
        productDiscountActivityAPIDTO.setMemberId(userId);
        productDiscountActivityAPIDTO.setIndustryId(orderDto.getIndustryId());
        productDiscountActivityAPIDTO.setParkId(orderDto.getParkId());
        productDiscountActivityAPIDTO.setProductType(orderDto.getProdType());
        ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        if(productDiscountActivityVO!=null) {
            mainOrder.setOrderAmount(productDiscountActivityVO.getSpecialPriceAmount());
            mainOrder.setPayAmount(productDiscountActivityVO.getPayAmount());
            mainOrder.setDiscountAmount(productDiscountActivityVO.getSpecialPriceAmount() - productDiscountActivityVO.getPayAmount());
            mainOrder.setDiscountActivityId(productDiscountActivityVO.getDiscountActivityId());
        }
        //保存人群标签id
        Long crowdLabelId = memberCrowdLabelRelaService.getCrowLabelIdByMemberId(memberAccountEntity.getId(),memberAccountEntity.getOemCode());
        if(crowdLabelId!=null){
            mainOrder.setCrowdLabelId(crowdLabelId);
        }

        // 接入方用户注册，费用承担方为非本人时，支付金额为0
        if (null != orderDto.getIsSelfPaying() && IsSelfPayingEnum.BEARER.getValue().equals(orderDto.getIsSelfPaying())) {
            mainOrder.setPayAmount(0L);
        }
        return mainOrder;
    }

    /**
     * @Description 构建企业注销订单主表数据
     * @Author Kaven
     * @Date 2020/2/14 10:41
     * @Param userId oemCode productId productName parkId orderAmount orderStatus
     * @Return OrderEntity
     * @Exception
     */
    public OrderEntity buildComCancelMainOrder(Long userId, String oemCode, Long productId, String productName, Long parkId, Long orderAmount, Integer orderStatus, String externalOrderNo) throws BusinessException {
        OrderEntity mainOrder = new OrderEntity();
        MemberAccountEntity memberAccountEntity=memberAccountMapper.selectByPrimaryKey(userId);

        // 生成订单号，订单信息入库
        String orderNo = OrderNoFactory.getOrderCode(userId);
        mainOrder.setOrderNo(orderNo);
        mainOrder.setExternalOrderNo(externalOrderNo);
        mainOrder.setAddTime(new Date());
        mainOrder.setUpdateTime(new Date());
        mainOrder.setUserId(userId);
        mainOrder.setOrderType(OrderTypeEnum.CANCELLATION.getValue());
        mainOrder.setOrderStatus(orderStatus);
        mainOrder.setProductId(productId);
        mainOrder.setProductName(productName);
        mainOrder.setOemCode(oemCode);
        mainOrder.setParkId(parkId);
        mainOrder.setOrderAmount(orderAmount);
        mainOrder.setPayAmount(orderAmount);
        mainOrder.setChannelServiceId(memberAccountEntity.getChannelServiceId());
        mainOrder.setChannelEmployeesId(memberAccountEntity.getChannelEmployeesId());
        mainOrder.setChannelCode(memberAccountEntity.getChannelCode());
        mainOrder.setChannelProductCode(memberAccountEntity.getChannelProductCode());
        mainOrder.setChannelUserId(memberAccountEntity.getChannelUserId());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(memberAccountEntity.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }
        // 查询oem机构信息
        OemEntity oem = this.oemService.getOem(oemCode);
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }
        MemberAccountEntity member = this.memberAccountService.findById(userId);

        // 补全订单会员关系
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(userId, oemCode, 4);//获取一二级推广人和分润信息
        if (more != null) {
            more.setMemberId(userId);
            more.setOemCode(oemCode);
            more.setOemName(oem.getOemName());
            more.setAddTime(new Date());
            // 设置会员等级
            more.setAddUser(member.getMemberAccount());
            MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
            more.setMemberLevel(level.getLevelNo());
            this.memberOrderRelaService.insertSelective(more);
        }

        // 补全订单主表信息
        mainOrder.setUserType(1);
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setAddUser(member.getMemberAccount());
        mainOrder.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
        return mainOrder;
    }

    /**
     * @Description 对象实体转换
     * @Author Kaven
     * @Date 2019/12/10 16:34
     * @Param orderDto
     * @Return RegisterOrderEntity
     */
    private RegisterOrderEntity transferRegOrderDto2Entity(RegisterOrderDTO orderDto) {
        RegisterOrderEntity regOrder = new RegisterOrderEntity();
        regOrder.setAddTime(new Date());
        regOrder.setOemCode(orderDto.getOemCode());
        regOrder.setOperatorName(orderDto.getOperatorName());
        if (orderDto.getIsSupplyShopName() == 1) {
            ParkEntity park = parkService.findById(orderDto.getParkId());
            regOrder.setShopName(park.getParkCity() + orderDto.getExampleName().replace("***", orderDto.getShopName()));
            regOrder.setShopNameOne(park.getParkCity() + orderDto.getExampleName().replace("***", orderDto.getShopNameOne()));
            regOrder.setShopNameTwo(park.getParkCity() + orderDto.getExampleName().replace("***", orderDto.getShopNameTwo()));
        } else {
            regOrder.setShopName(orderDto.getShopName());
            regOrder.setShopNameOne(orderDto.getShopNameOne());
            regOrder.setShopNameTwo(orderDto.getShopNameTwo());
        }
        regOrder.setIndustryId(orderDto.getIndustryId());
        regOrder.setBusinessAddress(orderDto.getBusinessAddress());
        regOrder.setContactPhone(orderDto.getContactPhone());
        regOrder.setEmail(orderDto.getEmail());
        regOrder.setIdCardFront(orderDto.getIdCardFront());
        regOrder.setIdCardReverse(orderDto.getIdCardReverse());
        regOrder.setIdCardNumber(orderDto.getIdCardNumber());
        regOrder.setIdCardAddr(orderDto.getIdCardAddr());
        regOrder.setExpireDate(orderDto.getExpireDate());
        regOrder.setRatifyTax(orderDto.getRatifyTax());
        regOrder.setBusinessScope(orderDto.getBusinessScope());
        regOrder.setRegisteredName(orderDto.getRegisteredName());
        regOrder.setOrderAmount(orderDto.getOrderAmount());
        regOrder.setDiscountAmount(orderDto.getDiscountAmount());
        regOrder.setPayAmount(orderDto.getPayAmount());
        regOrder.setAlertNumber(0);// 默认通知次数为0
        regOrder.setAgentAccount(orderDto.getAgentAccount());
        regOrder.setCompanyType(orderDto.getCompanyType());
        regOrder.setRemark(orderDto.getRemark());
        regOrder.setIsOther(orderDto.getIsOther());
        regOrder.setExampleName(orderDto.getExampleName());
        regOrder.setIsAllCodes(orderDto.getIsAllCodes());
        regOrder.setIndustryBusinessScope(orderDto.getIndustryBusinessScope());
        regOrder.setOwnBusinessScope(orderDto.getOwnBusinessScope());
        regOrder.setTaxcodeBusinessScope(orderDto.getTaxCodeBusinessScope());
        regOrder.setTaxpayerType(orderDto.getTaxpayerType());
        return regOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cancelOrder(OrderEntity entity, Integer status, String userAccount, boolean isApi) throws BusinessException {
        Date date = new Date();
        Map<String, Object> resultMap = Maps.newHashMap();
                // 查询订单
        OrderEntity order = Optional.ofNullable(this.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        if (Objects.equals(OrderTypeEnum.REGISTER.getValue(), entity.getOrderType())) {
            //工商注册
            RegisterOrderEntity regEntity = new RegisterOrderEntity();
            regEntity.setOemCode(entity.getOemCode());
            regEntity.setOrderNo(entity.getOrderNo());
            regEntity = registerOrderMapper.selectOne(regEntity);
            if (regEntity == null) {
                throw new BusinessException("工商注册取消异常，订单不存在");
            }
            if (isApi) {
                // 订单预处理：查询实时订单状态 add by Kaven 2020-04-14
                MemberAccountEntity member = this.memberAccountService.queryByAccount(userAccount, entity.getOemCode());
                JSONObject jsonObj = this.registerOrderService.orderCheckBeforePay(entity, member);
                if (null != jsonObj && "1".equals(jsonObj.getString("isPaid"))) {
                    // 订单已经支付成功不允许取消
                    return ErrorCodeConstants.EXIST_PAYING_ERROR.toString();
                }
            }

            RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
            BeanUtils.copyProperties(regEntity, record);
            record.setId(null);
            record.setAddTime(date);
            record.setAddUser(userAccount);
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setOrderStatus(status);

            if ((Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.TO_BE_SURE.getValue())
                    || Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue())
                    || Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.FAILED.getValue())
                    || Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.REJECTED.getValue())
                    || Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.TO_BE_VALIDATE.getValue())
                    || Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.REGISTRATION_UNDER_WAY.getValue())
                    || Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue())
                    || Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.SIGNATURE_CONFIRMATION.getValue()))
                    && IsSelfPayingEnum.SELF_PLAYING.getValue().equals(order.getIsSelfPaying()) // 接入方承担费用无需退款
            ) {
                //微信退款
                List<PayWaterEntity> list=payWaterService.queryPayWaterListByOrderNoAndStaus(entity.getOrderNo());
                if(CollectionUtils.isEmpty(list)){
                    throw  new BusinessException("支付流水查询失败");
                }
                PayWaterEntity payWaterEntity=list.get(0);
                if(ObjectUtils.equals(PayWayEnum.WECHATPAY.getValue(),payWaterEntity.getPayWay())
                        || ObjectUtils.equals(PayWayEnum.BYTEDANCE.getValue(),payWaterEntity.getPayWay())){
                    if(regEntity.getPayAmount()>0L) {
                        try {
                            resultMap = registerOrderService.routeAndRefundOrder(entity.getOemCode(), list.get(0).getOrderNo(), list.get(0).getPayNo(), userAccount);

                        } catch (UnknownHostException e) {
                            log.error(e.getMessage());
                            throw  new BusinessException("支付金额原路退退回失败");
                        }
                    }
                }else{
                    //退款到消费钱包
                    refund(entity, userAccount, date,2, false);
                }
            }

            // 判断该订单是否使用了优惠券
            if (Objects.nonNull(regEntity.getCouponsIssueId())) { // 该订单使用了优惠券
                // 回滚优惠券使用状态，清空使用时间
                CouponsIssueRecordEntity issueRecord = Optional.ofNullable(couponsIssueRecordService.findById(regEntity.getCouponsIssueId())).orElseThrow(() -> new BusinessException("未查询到优惠券发放记录"));
                CouponsEntity couponsEntity = Optional.ofNullable(couponsService.findById(issueRecord.getCouponsId())).orElseThrow(() -> new BusinessException("未查询到优惠券信息"));
                issueRecord.setUseTime(null);
                issueRecord.setUpdateTime(new Date());
                issueRecord.setUpdateUser(userAccount);
                issueRecord.setRemark("取消工单，回滚优惠券");
                if (couponsEntity.getEndDate().after(new Date())) {
                    issueRecord.setStatus(CouponsIssueRecordStatusEnum.UNUSED.getValue());
                } else {
                    issueRecord.setStatus(CouponsIssueRecordStatusEnum.STALE.getValue());
                }
                couponsIssueRecordMapper.updateByPrimaryKey(issueRecord);
                // 查询注册订单
                Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(entity.getUserId(), entity.getProductId(), entity.getOemCode(), order.getParkId());
                entity.setPayAmount(payAmount);
                entity.setProfitAmount(payAmount);
                orderService.editByIdSelective(entity);
                // 回滚注册订单支付金额
                regEntity.setPayAmount(payAmount);
                regEntity.setCouponsIssueId(null);
                registerOrderMapper.updateByPrimaryKey(regEntity);
                // 回滚注册订单变更记录支付金额
                record.setPayAmount(payAmount);
                record.setCouponsIssueId(null);
            }
            registerOrderChangeRecordMapper.insertSelective(record);
        } else if (Objects.equals(OrderTypeEnum.INVOICE.getValue(), entity.getOrderType())) {
            //开票
            InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
            invEntity.setOemCode(entity.getOemCode());
            invEntity.setOrderNo(entity.getOrderNo());
            invEntity = invoiceOrderMapper.selectOne(invEntity);
            if (invEntity == null) {
                throw new BusinessException("开票订单取消异常，订单不存在");
            }
            if (StringUtils.isNotBlank(invEntity.getGroupOrderNo())) {
                throw new BusinessException("集团开票订单不允许取消");
            }
            InvoiceRecordEntity q=null;
            q=new InvoiceRecordEntity();
            q.setOrderNo(invEntity.getOrderNo());
            List<InvoiceRecordEntity> invoiceRecordEntities=invoiceRecordService.select(q);
            if(CollectionUtil.isNotEmpty(invoiceRecordEntities)){
                for (InvoiceRecordEntity invoiceRecord :invoiceRecordEntities) {
                    if(invoiceRecord.getStatus().equals(InvoiceRecordStatusEnum.CANCELED.getValue())){
                        continue;
                    }
                    if(!invoiceRecord.getStatus().equals(InvoiceRecordStatusEnum.TO_BE_CONFIRMED.getValue())
                        &&!invoiceRecord.getStatus().equals(InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue())
                        &&!invoiceRecord.getStatus().equals(InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue())){
                        throw new BusinessException("该订单不能取消！");
                    }
                    //更新开票记录
                    invoiceRecord.setStatus(InvoiceRecordStatusEnum.CANCELED.getValue());
                    invoiceRecord.setUpdateTime(new Date());
                    invoiceRecord.setUpdateUser(userAccount);
                    invoiceRecord.setRemark(entity.getRemark());
                    invoiceRecordService.editByIdSelective(invoiceRecord);
                    //新增开票记录变更表
                    InvoiceRecordChangeEntity invoiceRecordChangeEntity=new InvoiceRecordChangeEntity();
                    BeanUtils.copyProperties(invoiceRecord,invoiceRecordChangeEntity);
                    invoiceRecordChangeEntity.setId(null);
                    invoiceRecordChangeEntity.setAddTime(new Date());
                    invoiceRecordChangeEntity.setAddUser(userAccount);
                    invoiceRecordChangeService.insertSelective(invoiceRecordChangeEntity);
                }
            }
            invEntity.setRemark(entity.getRemark());
            invEntity.setUpdateTime(new Date());
            invEntity.setUpdateUser(userAccount);
            invoiceOrderService.editByIdSelective(invEntity);

            InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
            BeanUtils.copyProperties(invEntity, record);
            record.setId(null);
            record.setAddTime(date);
            record.setAddUser(userAccount);
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setOrderStatus(status);
            invoiceOrderChangeRecordMapper.insertSelective(record);

            //开票金额回滚（作废重开订单不回滚开票记录）
            if (!InvoiceMarkEnum.REOPEN.getValue().equals(invEntity.getInvoiceMark())) {
                companyInvoiceRecordMapper.refund(invEntity.getCompanyId(), invEntity.getAddTime(), invEntity.getInvoiceAmount(), userAccount, date);
            }
            if (entity.getOrderStatus() > InvoiceOrderStatusEnum.UNPAID.getValue()
                    && entity.getOrderStatus() < InvoiceOrderStatusEnum.TO_BE_SHIPPED.getValue()) {
                //退款
                refund(entity, userAccount, date,2, false);
            }
            // 向接入方推送消息
            MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(entity.getUserId())).orElseThrow(() -> new BusinessException("未查询到订单所属用户信息"));
            if (null != member.getAccessPartyId()) {
                // 回调参数
                HashMap<String, Object> map = new HashMap<>();
                map.put("callbackType", 1); //回调类型 1-取消 2-出票 3-发货 4-完成
                map.put("orderNo", invEntity.getOrderNo());
                map.put("orderStatus", 8);
                // 发送推送消息
                invoiceOrderService.accessPartyPush(invEntity.getOrderNo(), entity.getOemCode(), member.getAccessPartyId(), map);
            }
            if (InvoiceMarkEnum.REOPEN.getValue().equals(invEntity.getInvoiceMark())) {
                // 取消未读“作废重开”通知
                MessageNoticeEntity noticeEntity = new MessageNoticeEntity();
                noticeEntity.setOemCode(order.getOemCode());
                noticeEntity.setUserId(order.getUserId());
                noticeEntity.setBusinessType(12);
                noticeEntity.setStatus(0);
                noticeEntity.setIsAlert(0);
                List<MessageNoticeEntity> list = messageNoticeService.select(noticeEntity);
                if (null != list && !list.isEmpty()) {
                    for (MessageNoticeEntity messageNoticeEntity : list) {
                        messageNoticeService.updateStatusById(messageNoticeEntity.getId(), 3);
                    }
                }
            }
        } else {
            return null;
        }
        //更新订单主表状态
        mapper.updateOrderStatus(entity.getOrderNo(), status, userAccount, date);

        // 取消工单
        this.workOrderService.cancelWorkOrder(userAccount, entity.getOrderNo(), entity.getOemCode());

        if (null != resultMap.get("refundResult") && StringUtil.isNotBlank(resultMap.get("refundResult").toString())) {
            return resultMap.get("refundResult").toString();
        }

        return null;
    }

    /**
     * 退款
     *
     * @param entity
     * @throws BusinessException
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refund(OrderEntity entity, String userAccount, Date date,Integer payChannels, boolean allowRefunds) throws BusinessException {
//        MemberOrderRelaEntity relaEntity = memberOrderRelaService.findById(entity.getRelaId());
//        if (relaEntity == null) {
//            throw new BusinessException("会员关系订单不存在");
//        }

        PayWaterEntity payEntity = new PayWaterEntity();
        payEntity.setOemCode(entity.getOemCode());
        payEntity.setOrderNo(entity.getOrderNo());
        payEntity.setOrderType(entity.getOrderType());
        payEntity.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());
        List<PayWaterEntity> list = payWaterService.select(payEntity);
        if (!allowRefunds &&CollectionUtil.isNotEmpty(list)) {
            throw new BusinessException("已存在退款订单");
        }

        payEntity.setPayWaterType(null);
//        if (Objects.equals(entity.getOrderType(), OrderTypeEnum.REGISTER.getValue())) {
//            payEntity.setOrderType(PayWaterOrderTypeEnum.REGISTER.getValue());
//        } else if (Objects.equals(entity.getOrderType(), OrderTypeEnum.INVOICE.getValue())) {
//            payEntity.setOrderType(PayWaterOrderTypeEnum.INVOICE.getValue());
//        }
        payEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        payEntity = payWaterService.selectOne(payEntity);
        if (payEntity == null) {
            log.info("取消订单支付流水不存在：{}", entity.getOrderNo());
            return;
//            throw new BusinessException("支付流水不存在");
        }
        payEntity.setPayNo(UniqueNumGenerator.generatePayNo());
        payEntity.setId(null);
        payEntity.setAddTime(date);
        payEntity.setAddUser(userAccount);
        payEntity.setPayWaterType(5);
        payEntity.setUpdateTime(date);
        payEntity.setPayTime(date);
        payEntity.setUpdateUser(userAccount);
        payEntity.setRefundStatus(null);
        if(payChannels!=null&&payChannels==2) {
            payEntity.setPayChannels(payChannels);
            payEntity.setPayWay(payChannels);
        }
        MemberCompanyEntity company = null;
        if(entity.getOrderType() == 6){ //开票订单
            InvoiceOrderEntity invoiceOrderEntity = Optional.of(invoiceOrderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到开票订单"));
            company = Optional.ofNullable(memberCompanyService.findById(invoiceOrderEntity.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业"));
            if(company.getCompanyType()!=1){
                payEntity.setPayChannels(PayChannelEnum.OFFLINE.getValue());
                payEntity.setPayWay(PayWayEnum.OFFLINE.getValue());
                if(payEntity.getPayAmount()>0) {
                    payEntity.setPayStatus(PayWaterStatusEnum.WAIT_FINANCIAL_PAY.getValue());
                }
            }
        }
        //增加支付流水
        payWaterService.insertSelective(payEntity);
        if(entity.getOrderType() == 6 && company != null && company.getCompanyType() != 1){
            return ;
        }
        UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(entity.getUserId(), payEntity.getUserType(), entity.getOemCode(),1);
        if (accEntity == null) {
            throw new BusinessException("资金账户不存在");
        }
        if (accEntity.getBlockAmount() < entity.getPayAmount()) {
            throw new BusinessException("冻结金额不足，无法退款");
        }
        //解冻余额
        userCapitalAccountService.unfreezeBalance(accEntity, entity.getOrderNo(), entity.getOrderType(), entity.getPayAmount(), userAccount);
    }

    @Override
    public PageResultVo<MemberUpgradeOrderVO> queryUpgradeOrder(TZBOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        List<MemberUpgradeOrderVO> list = this.orderMapper.queryMemberLvUpOrderList(query);
        PageInfo<MemberUpgradeOrderVO> pageInfo = new PageInfo<MemberUpgradeOrderVO>(list);

        PageResultVo<MemberUpgradeOrderVO> result = new PageResultVo<MemberUpgradeOrderVO>();
        result.setList(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageSize(query.getPageSize());
        result.setPageNum(query.getPageNumber());
        result.setOrderBy("createTime DESC");
        return result;
    }

    @Override
    @Transactional
    public void memberAudit(OrderEntity entity, MemberAccountEntity memEntity) {
        if (Objects.equals(MemberOrderStatusEnum.COMPLETED.getValue(), entity.getOrderStatus())) {
            MemberLevelEntity level = memberLevelService.findById(entity.getProductId());
            if (level == null) {
                throw new BusinessException("等级配置不存在");
            }
            if (Objects.equals(level.getLevelNo(), MemberLevelEnum.DIAMOND.getValue())) {
                //设置城市服务商会默认的员工上线
                OemEntity oem = this.oemService.getOem(entity.getOemCode());
                if (null == oem) {
                    throw new BusinessException("操作失败，OEM机构不存在");
                }
                memEntity.setEmployeesLimit(oem.getEmployeesLimit());
                //更新下级信息
                memberAccountMapper.updateSubordinate(memEntity);
                //判断所属员工上级城市服务商是不是当前会员上上级城市服务商，是清除员工信息
                MemberAccountEntity employeeEntity = memberAccountMapper.selectByPrimaryKey(memEntity.getAttributionEmployeesId());
                if (employeeEntity != null && Objects.equals(employeeEntity.getUpDiamondId(), memEntity.getSuperDiamondId())) {
                    memEntity.setAttributionEmployeesId(null);
                    memEntity.setAttributionEmployeesAccount(null);
                    memEntity.setSuperEmployeesId(null);
                    memEntity.setSuperEmployeesAccount(null);
                }
                memEntity.setExtendType(ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue());
                memEntity.setSuperDiamondId(null);
                memEntity.setSuperDiamondAccount(null);
            }
            memEntity.setLevelName(level.getLevelName());
            memEntity.setIsPayUpgrade(MemberPayUpgradeEnum.NO.getValue());
            memEntity.setMemberLevel(level.getId());
            memEntity.setIsPayUpgrade(0);//不是付费升级
            memEntity.setUpdateTime(entity.getUpdateTime());
            memEntity.setUpdateUser(entity.getUpdateUser());
            memberAccountMapper.updateByPrimaryKey(memEntity);
        }
        mapper.updateByPrimaryKey(entity);

    }

    @Override
    public Map<String, Integer> sumInvOrder(OrderQuery query) {
        query.setOrderType(OrderTypeEnum.INVOICE.getValue());
        return mapper.sumInvOrder(query);
    }

    @Override
    @Transactional
    public List<InvOrderBatchShipmentsVO> listInvBatchStock(OrderQuery query, String userAccount) {
        List<InvOrderBatchShipmentsVO> lists = mapper.listInvBatchStock(query);
        if (CollectionUtil.isEmpty(lists)) {
            return lists;
        }
        List<Long> ids = lists.stream().map(InvOrderBatchShipmentsVO::getId).collect(Collectors.toList());
        invoiceOrderChangeRecordMapper.batchAdd(ids, InvoiceOrderStatusEnum.OUT_OF_STOCK.getValue(), userAccount, new Date(), "批量导出");
        List<String> orders = lists.stream().map(InvOrderBatchShipmentsVO::getOrderNo).collect(Collectors.toList());
        mapper.updateBatchOrderStatus(orders, InvoiceOrderStatusEnum.OUT_OF_STOCK.getValue(), userAccount, new Date());
        return lists;
    }

    @Override
    @Transactional
    public void updateInvOrderStatus(InvoiceOrderEntity invEntity, OrderEntity entity, String remark) {
        mapper.updateOrderStatus(entity.getOrderNo(), entity.getOrderStatus(), entity.getUpdateUser(), entity.getUpdateTime());
        invoiceOrderMapper.updateByPrimaryKeySelective(invEntity);
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invEntity, record);
        record.setId(null);
        record.setAddTime(entity.getUpdateTime());
        record.setAddUser(entity.getUpdateUser());
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setOrderStatus(entity.getOrderStatus());
        record.setRemark(remark);
        invoiceOrderChangeRecordMapper.insertSelective(record);
    }

    @Override
    @Transactional
    public void updateInvOrderStatusAndlogisticsInfo(InvoiceOrderEntity invEntity, OrderEntity entity, String remark,String logisticsInfo,Long userId,String updateUser) {
        //updateInvOrderStatus(invEntity,entity,remark);
        invoiceOrderService.confirmReceipt(userId, invEntity.getOemCode(), entity.getOrderNo(),updateUser);
        // 代签收删除之前的快递信息
        logisticsInfoService.deleteByOrderNo(entity.getOrderNo());
        LogisticsInfoEntity logEntity = new LogisticsInfoEntity();
        logEntity.setOrderNo(entity.getOrderNo());
        //  代签收
        logEntity.setLogisticsStatus(7);
        if (StringUtil.isNotBlank(invEntity.getCourierNumber())){
            logEntity.setCourierNumber(invEntity.getCourierNumber());
        }
        if (StringUtil.isNotBlank(invEntity.getCourierCompanyName())){
            logEntity.setCourierCompanyName(invEntity.getCourierCompanyName());
        }
        logEntity.setLogisticsInfo("代签收说明:"+logisticsInfo);
        logEntity.setAddTime(new Date());
        logEntity.setAddUser(entity.getUpdateUser());
        logisticsInfoService.insertSelective(logEntity);

        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(userId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        if (null != member.getAccessPartyId()) {
            // 回调参数
            HashMap<String, Object> map = new HashMap<>();
            map.put("callbackType", 4); //回调类型 1-取消 2-出票 3-发货 4-完成
            map.put("orderNo", entity.getOrderNo());
            map.put("orderStatus", 7);
            // 发送推送消息
            invoiceOrderService.accessPartyPush(entity.getOrderNo(), member.getOemCode(), member.getAccessPartyId(), map);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompangApplyStatusBatch(List<CompanyResoucesApplyRecordExportVO> list, String userAccount) {
        List<Long> ids = list.stream().map(CompanyResoucesApplyRecordExportVO::getId).collect(Collectors.toList());
        companyResoucesApplyRecordMapper.batchUpdateStatus(ids, CompanyResoucesApplyRecordStatusEnum.OUT_OF_STOCK.getValue(), userAccount, new Date());
        List<String> orders = list.stream().map(CompanyResoucesApplyRecordExportVO::getOrderNo).collect(Collectors.toList());
        mapper.updateBatchOrderStatus(orders, CompanyResoucesApplyRecordStatusEnum.OUT_OF_STOCK.getValue(), userAccount, new Date());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompangApplyStatus(CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity, OrderEntity orderEntity) {
        //修改申请记录状态
        companyResoucesApplyRecordMapper.updateByPrimaryKey(companyResoucesApplyRecordEntity);
        //修改订单状态
        mapper.updateByPrimaryKey(orderEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String userWithdraw(UserWithdrawDTO dto) throws BusinessException {
        log.info("开始处理用户提现请求：{}", JSON.toJSONString(dto));

        String result = null;

        // 判断提现金额是否大于等于最小限额（接收金额单位：分）
        OemEntity oem = this.oemService.getOem(dto.getOemCode());
        if(null == oem){
            throw new BusinessException("提现失败，OEM机构不存在");
        }
        if((dto.getWithdrawType().intValue() == 2 && dto.getAmount() < oem.getMinCommissionWalletLimit()) // 佣金钱包提现
                || (dto.getWithdrawType().intValue() == 1 && dto.getAmount() < oem.getMinConsumptionWalletLimit())) { // 消费钱包提现
            throw new BusinessException("提现金额必须大于等于最小限额");
        }

        // 判断消费钱包提现是否超过可提现金额
        if (dto.getWithdrawType().intValue() == 1) {
            Long usableWithdrawAmount = this.usableWithdrawAmount(dto.getUserId(), oem.getOemCode());
            if (dto.getAmount() > usableWithdrawAmount) {
                throw new BusinessException("提现金额不能大于用户可提现金额");
            }
        }

        // 查询用户账号
        MemberAccountEntity member = this.memberAccountService.findById(dto.getUserId());
        if (null == member) {
            throw new BusinessException("操作失败，用户信息不存在");
        }

        // 验证码校验
        String registRedisTime = System.currentTimeMillis() + 300000 + "";

        //验证码验证
        String verficationCode = redisService.get(RedisKey.SMS_WALLET_WITHDRAW_KEY_SUFFER + member.getMemberAccount());
        //验证码错误或过期
        if (verficationCode == null || "".equals(verficationCode) || !dto.getVerifyCode().equals(verficationCode)) {
            //释放redis锁
            redisService.unlock(RedisKey.SMS_WALLET_WITHDRAW_KEY_SUFFER + member.getMemberAccount(), registRedisTime);
            throw new BusinessException(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
        }

        // 实名为企业的用户只支持代理提现
        if (MemberAuthTypeEnum.COMPANY.getValue().equals(member.getMemberAuthType())) {
            throw new BusinessException("企业账号当前只支持代理提现");
        }

        // 查询用户资金账户信息
        UserCapitalAccountEntity userCapitalAccount = this.userCapitalAccountService.queryByUserIdAndType(dto.getUserId(), 1, dto.getOemCode(),dto.getWithdrawType());
        if (null == userCapitalAccount) {
            throw new BusinessException("提现失败，用户资金账户不存在");
        }
        if (userCapitalAccount.getStatus().intValue() != 1) {
            throw new BusinessException("资金账户已被冻结，如有疑问请联系客服");
        }
        // 判断可用余额是否充足
        if(userCapitalAccount.getAvailableAmount() - dto.getAmount() < 0){
            throw new BusinessException("提现失败，账户可用余额不足");
        }

        // 查询用户绑定银行卡信息
        UserBankCardEntity userBankCard = this.userBankCardMapper.getBankCardInfoByUserIdAndUserType(dto.getUserId(), 1, dto.getOemCode());
        if (null == userBankCard) {
            throw new BusinessException("提现失败，未找到用户绑定银行卡信息");
        }
        // 校验银行卡号
        if (!dto.getBankNumber().equals(userBankCard.getBankNumber())) {
            throw new BusinessException("提现失败，银行卡号不匹配");
        }

        if(dto.getWithdrawType().intValue() == 2){ // 佣金钱包提现
            log.info("处理佣金钱包提现请求。。。");
            result = this.userWithdrawForCommission(userCapitalAccount,userBankCard,dto,member.getMemberAccount());
        } else {
            log.info("处理消费钱包提现请求。。。");
            // 调用北京银行代付接口
            this.adminWithdraw(userCapitalAccount, userBankCard, dto.getAmount(), member, WalletTypeEnum.CONSUMER_WALLET.getValue(),
                    null,dto.getSourceType());
        }

        // 释放redis锁
        redisService.unlock(RedisKey.USER_WITHDRAW_REDIS_KEY + member.getMemberAccount(), registRedisTime);
        log.info("用户提现请求处理结束...");

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo userRecharge(UserRechargeDTO dto) throws BusinessException {
        if (StringUtil.isBlank(dto.getOemCode())) {
            throw new BusinessException("机构编码为空");
        }

        // 查询机构
        OemEntity oem = Optional.ofNullable(oemService.getOem(dto.getOemCode())).orElseThrow(() -> new BusinessException("未查询到机构信息"));

        // 查询用户账号
        MemberAccountEntity member = this.memberAccountService.findById(dto.getCurrUserId());
        if (null == member) {
            throw new BusinessException("操作失败，用户信息不存在");
        }
        dto.setMemberAccount(member.getMemberAccount());

        if (dto.getAmount() <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        if (PayTypeEnum.WECHATPAY.getValue().equals(dto.getPayType())) {
            // 充值限额校验
            int rechargeAmountMinLimit = parseInt(sysDictionaryService.getByCode("recharge_amount_min_limit").getDictValue());
            if (dto.getAmount() < rechargeAmountMinLimit) {
                throw new BusinessException("充值金额小于微信单笔最小限额，请修改后重试！");
            }

            // 单笔充值最大限额
            int rechargeAmountMaxLimit = parseInt(sysDictionaryService.getByCode("recharge_amount_max_limit").getDictValue());
            if (dto.getAmount()  > rechargeAmountMaxLimit) {
                throw new BusinessException("充值金额超过微信单笔最大限额，请修改后重试！");
            }

            // 当前机构是否为跨主体收单机构
            if (null != oem.getIsOtherOemPay() && oem.getIsOtherOemPay() == 1) {
                // 使用代收单机构
                dto.setOtherPayOemCode(oem.getOtherPayOemcode());
            } else {
                dto.setOtherPayOemCode(dto.getOemCode());
            }

            log.info("调用微信支付充值接口...");
            return this.wechatPayRecharge(dto);
        } else if (PayTypeEnum.ALIPAY.getValue().equals(dto.getPayType())) {
            // 充值限额校验
            int rechargeAmountMinLimit = parseInt(sysDictionaryService.getByCode("ali_recharge_amount_min_limit").getDictValue());
            if (dto.getAmount() < rechargeAmountMinLimit) {
                throw new BusinessException("充值金额小于支付宝单笔最小限额，请修改后重试！");
            }

            // 单笔充值最大限额
            int rechargeAmountMaxLimit = parseInt(sysDictionaryService.getByCode("ali_recharge_amount_max_limit").getDictValue());
            if (dto.getAmount()  > rechargeAmountMaxLimit) {
                throw new BusinessException("充值金额超过支付宝单笔最大限额，请修改后重试！");
            }
            // 调用支付宝支付接口
            log.info("调用支付宝充值接口...");
            dto.setBuyerId(member.getAlipayUserId());// 设置买家支付用户号
            return this.aliPayRecharge(dto);
        } else if (PayTypeEnum.BYTEDANCEPAY.getValue().equals(dto.getPayType())) { //字节跳动
            // 充值限额校验
            int rechargeAmountMinLimit = parseInt(sysDictionaryService.getByCode("recharge_amount_min_limit").getDictValue());
            if (dto.getAmount() < rechargeAmountMinLimit) {
                throw new BusinessException("充值金额小于微信单笔最小限额，请修改后重试！");
            }

            // 单笔充值最大限额
            int rechargeAmountMaxLimit = parseInt(sysDictionaryService.getByCode("recharge_amount_max_limit").getDictValue());
            if (dto.getAmount()  > rechargeAmountMaxLimit) {
                throw new BusinessException("充值金额超过微信单笔最大限额，请修改后重试！");
            }
            log.info("调用字节跳动支付充值接口...");
            return this.bytedancePayRecharge(dto);
        } else {
            throw new BusinessException("不支持的支付方式，请更换");
        }
    }

    /**
     * @Description 补全充值(提现)订单参数
     * @Author Kaven
     * @Date 2019/12/13 16:07
     * @Param userId order
     */
    private void completionParameter(Long userId, OrderEntity order) {
//        // 保存会员订单关系
//        MemberOrderRelaEntity more = new MemberOrderRelaEntity();
//        more.setMemberId(userId);
//        // 设置会员等级
//        MemberAccountEntity member = this.memberAccountService.findById(userId);
//        MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
//        more.setMemberLevel(level.getLevelNo());
//        more.setAddTime(new Date());
//        this.memberOrderRelaService.insertSelective(more);

        // 查询oem机构信息
        OemEntity oem = this.oemService.getOem(order.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(member==null){
            throw new BusinessException("会员信息不存在");
        }
        //获取一二级推广人和分润信息
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(userId, order.getOemCode(), 3);
        if (more != null) {
            more.setMemberId(userId);
            more.setOemCode(order.getOemCode());
            more.setOemName(oem.getOemName());
            more.setAddTime(new Date());
            // 设置会员等级
            more.setAddUser(member.getMemberAccount());
            MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
            more.setMemberLevel(level.getLevelNo());
            this.memberOrderRelaService.insertSelective(more);
        }

        // 补全订单主表信息
        order.setUserId(userId);
        order.setUserType(MemberTypeEnum.MEMBER.getValue());
        if (more != null) {
            order.setRelaId(more.getId());
        }
        order.setAddUser(member.getMemberAccount());
        order.setAddTime(new Date());
        if (Objects.equals(OrderTypeEnum.ENCHARGE.getValue(), order.getOrderType()) ||
                Objects.equals(OrderTypeEnum.SUBSTITUTE_CHARGE.getValue(), order.getOrderType()) ||
                Objects.equals(OrderTypeEnum.WITHDRAW.getValue(), order.getOrderType()) ||
                Objects.equals(OrderTypeEnum.SUBSTITUTE_WITHDRAW.getValue(), order.getOrderType())) {
            order.setOrderStatus(RACWStatusEnum.PAYING.getValue());
        } else {
            order.setOrderStatus(MemberOrderStatusEnum.TO_BE_PAY.getValue());
        }
        order.setChannelProductCode(member.getChannelProductCode());
        order.setChannelCode(member.getChannelCode());
        order.setChannelEmployeesId(member.getChannelEmployeesId());
        order.setChannelServiceId(member.getChannelServiceId());
        order.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        order.setChannelUserId(member.getChannelUserId());
    }

    /**
     * @Description 微信支付充值接口（创建用户充值订单）
     * @Author Kaven
     * @Date 2020/1/7 9:41
     * @Param userId oemCode orderType amount
     * @Return ResultVo
     * @Exception
     */
    public ResultVo wechatPayRecharge(UserRechargeDTO dto) {
        log.info("微信充值请求开始：");
        ResultVo wechatPayResult = new ResultVo();
        log.info("开始处理微信充值相关业务...");
        // 创建订单订单号
        String orderNo = OrderNoFactory.getOrderCode(dto.getCurrUserId());

        Map<String, Object> orderMap = null;
        String retCode = null;// 临时变量，支付接口返回码
        String retMsg = null;// 临时变量，支付接口返回信息
        try {
            // 封装参数，请求渠道端微信支付接口完成充值
            String payNo = UniqueNumGenerator.generatePayNo();// 生成24位流水号
            WechatPayDto payDto = buildWechatParams(dto.getCurrUserId(), dto.getOtherPayOemCode(), payNo, dto.getAmount());

            log.info("请求渠道端微信支付开始：{}", JSONObject.toJSONString(payDto));

            // 根据挡板开关判断流程走向
            DictionaryEntity dict = this.dictionaryService.getByCode("itax_wechatpay_switch");
            if (null != dict && "1".equals(dict.getDictValue())) {
                orderMap = new HashMap<String, Object>();
                JSONObject data = new JSONObject();
                data.put("payNo", payNo);
                data.put("tradeNo", System.currentTimeMillis());
                String retunData = "{\n" +
                        "\t\t\t\"timeStamp\": \"1578451169\",\n" +
                        "\t\t\t\"package\": \"prepay_id=wx0810392909322481d601b9da1641342800\",\n" +
                        "\t\t\t\"paySign\": \"eU7wy0hXpO1aHeKb+zEcHX6vefJ32/dJjgkKgTtdU8MYFWBYxlNpw8cBsnNBWEVO+PYpp/wBHHj+x/ew2Du6ytFbq/ZmOWOgIxBeFKek0h/6d6pfSvjBbjRJeo5lWKav8Pf3oKicWJPOh0n2SSb6PQ3fNT6kPM6upH25qcjVhSAwrrGRhaOZV9Qc9qQXvwZp6BioLf52ppDMDs/eTll/ociNnz8IZDucpS2sauNZ5MXHVWON3Kw/eEr2EYalkzHM1jZdOBSU/MjXvSt0r3P+dWjSCYaCvegqz7OmDtV1yTWibAt4fM7Rk+rsUG1S5yYMt11R+m8uis27H3AhEZYYtg==\",\n" +
                        "\t\t\t\"mweb_url\": \"\",\n" +
                        "\t\t\t\"appId\": \"wxb884fccbb878f5b8\",\n" +
                        "\t\t\t\"signType\": \"RSA\",\n" +
                        "\t\t\t\"partnerid\": \"340838482\",\n" +
                        "\t\t\t\"prepayid\": \"\",\n" +
                        "\t\t\t\"nonceStr\": \"Z4EJ94t7KqOJ0NYsr676GgcszLgrmkgB\"\n" +
                        "\t\t}";
                data.put("returnData", JSONObject.parse(retunData));
                data.put("merNo", "M000000164");
                orderMap.put("data", data);
                orderMap.put("code", "00");
                orderMap.put("msg", "支付成功");
            } else {
                orderMap = WechatPayUtils.wechatPay(payDto);// 请求支付接口
            }

            log.info("渠道微信支付返回结果：{}", JSONObject.toJSONString(orderMap));

            retCode = orderMap.get("code").toString();// 返回码
            retMsg = orderMap.get("msg").toString();// 返回信息

            // 保存订单主表信息
            OrderEntity order = new OrderEntity();
            order.setOemCode(dto.getOemCode());
            order.setOrderNo(orderNo);
            order.setOrderAmount(dto.getAmount());
            order.setPayAmount(dto.getAmount());
            order.setProfitAmount(dto.getAmount());
            order.setOrderType(OrderTypeEnum.ENCHARGE.getValue());
            order.setSourceType(dto.getSourceType());
            // 查询用户渠道映射id
            MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(dto.getCurrUserId())).orElseThrow(() -> new BusinessException("未查询到当前登录用户信息"));
            order.setChannelUserId(member.getChannelUserId());
            // 保存会员订单关系，补全订单参数
            completionParameter(dto.getCurrUserId(), order);

            if (!(StringUtils.equals(retCode, "00"))) {
                order.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
            }
            this.insertSelective(order);

            log.info("微信充值订单创建成功：{}", orderNo);

            // 查询oem机构信息
            OemEntity oem = oemService.getOem(dto.getOemCode());
            if (null == oem) {
                throw new BusinessException("未查询到oem机构");
            }
            // 生成支付流水
            PayWaterEntity water = new PayWaterEntity();
            water.setOemCode(dto.getOemCode());
            water.setOemName(oem.getOemName());
            water.setOrderAmount(dto.getAmount());
            water.setPayAmount(dto.getAmount());
            water.setPayNo(payNo);
            water.setOrderNo(orderNo);
            water.setMemberId(dto.getCurrUserId());

            // 解析返回data数据，拿到外部订单号
            if ("00".equals(retCode)) {
                JSONObject dataObj = (JSONObject) orderMap.get("data");
                water.setExternalOrderNo(dataObj.getString("tradeNo"));
            }
            water.setAddUser(dto.getMemberAccount());
            water.setAddTime(new Date());
            water.setPayChannels(PayChannelEnum.WECHATPAY.getValue());
            water.setOrderType(OrderTypeEnum.ENCHARGE.getValue());
            water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
            water.setPayWay(PayWayEnum.WECHATPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
            water.setPayWaterType(PayWaterTypeEnum.RECHARGE.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
            water.setPayStatus("00".equals(retCode) ? PayWaterStatusEnum.PAYING.getValue() : PayWaterStatusEnum.PAY_FAILURE.getValue());
            this.payWaterService.insertSelective(water);
        } catch (Exception e) {
            log.error(orderNo + "充值失败，调用微信充值接口发生异常：" + e.getMessage());
            wechatPayResult.setRetCode(MessageEnum.SYSTEM_ERROR.getValue());
            wechatPayResult.setRetMsg(e.getMessage());
            throw new BusinessException("充值失败，调用微信充值接口发生异常，" + e.getMessage());
        }
        if (!(StringUtils.equals(retCode, "00"))) {
            log.error("充值失败：" + retMsg);
            wechatPayResult.setRetCode(MessageEnum.SYSTEM_ERROR.getValue());
            wechatPayResult.setRetMsg(retMsg);
            throw new BusinessException("充值失败：" + retMsg);
        }
        log.info("微信充值请求处理结束...");
        wechatPayResult.setRetCode(MessageEnum.SUCCESS.getValue());
        wechatPayResult.setRetMsg(MessageEnum.SUCCESS.getMessage());
        // 返回订单号
        JSONObject dataObj = (JSONObject) orderMap.get("data");
        dataObj.put("orderNo", orderNo);
        wechatPayResult.setData(dataObj);
        return wechatPayResult;
    }

    /**
     * @Description 支付宝支付充值接口
     * @Author  Kaven
     * @Date   2020/10/23 09:41
     * @Param   UserRechargeDTO
     * @Return  ResultVo
     * @Exception
    */
    public ResultVo aliPayRecharge(UserRechargeDTO dto) {
        log.info("收到支付宝充值请求，开始处理支付宝充值相关业务...");

        ResultVo wechatPayResult = new ResultVo();
        // 创建订单订单号
        String orderNo = OrderNoFactory.getOrderCode(dto.getCurrUserId());

        Map<String, Object> orderMap = null;
        String retCode = null;// 临时变量，支付接口返回码
        String retMsg = null;// 临时变量，支付接口返回信息
        try {
            // 封装参数，请求渠道端微信支付接口完成充值
            String payNo = UniqueNumGenerator.generatePayNo();// 生成24位流水号
            AliPayDto payDto = buildAliPayParams(dto.getOemCode(), payNo, dto.getAmount(),dto.getBuyerId(),dto.getBuyerLogonId());

            log.info("请求渠道端支付宝支付开始：{}", JSONObject.toJSONString(payDto));

            // 根据挡板开关判断流程走向
            DictionaryEntity dict = this.dictionaryService.getByCode("itax_alipay_switch");
            if (null != dict && "1".equals(dict.getDictValue())) {
                orderMap = new HashMap<String, Object>();
                JSONObject data = new JSONObject();
                data.put("payNo", payNo);
                data.put("tradeNo", System.currentTimeMillis());
                data.put("merNo", "M000000164");
                orderMap.put("data", data);
                orderMap.put("code", "00");
                orderMap.put("msg", "支付成功");
            } else {
                orderMap = AliPayUtils.aliPay(payDto);// 请求支付接口
            }

            log.info("渠道支付宝支付返回结果：{}", JSONObject.toJSONString(orderMap));

            retCode = orderMap.get("code").toString();// 返回码
            retMsg = orderMap.get("msg").toString();// 返回信息

            // 保存订单主表信息
            OrderEntity order = new OrderEntity();
            order.setOemCode(dto.getOemCode());
            order.setOrderNo(orderNo);
            order.setOrderAmount(dto.getAmount());
            order.setPayAmount(dto.getAmount());
            order.setProfitAmount(dto.getAmount());
            order.setOrderType(OrderTypeEnum.ENCHARGE.getValue());
            order.setSourceType(dto.getSourceType());
            // 查询用户渠道映射id
            MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(dto.getCurrUserId())).orElseThrow(() -> new BusinessException("未查询到当前登录用户信息"));
            order.setChannelUserId(member.getChannelUserId());
            // 保存会员订单关系，补全订单参数
            completionParameter(dto.getCurrUserId(), order);

            if (!(StringUtils.equals(retCode, "00"))) {
                order.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
            }
            this.insertSelective(order);

            log.info("支付宝充值订单创建成功：{}", orderNo);

            // 查询oem机构信息
            OemEntity oem = oemService.getOem(dto.getOemCode());
            if (null == oem) {
                throw new BusinessException("未查询到oem机构");
            }
            // 生成支付流水
            PayWaterEntity water = new PayWaterEntity();
            water.setOemCode(dto.getOemCode());
            water.setOemName(oem.getOemName());
            water.setOrderAmount(dto.getAmount());
            water.setPayAmount(dto.getAmount());
            water.setPayNo(payNo);
            water.setOrderNo(orderNo);
            water.setMemberId(dto.getCurrUserId());

            // 解析返回data数据，拿到外部订单号
            if ("00".equals(retCode)) {
                JSONObject dataObj = (JSONObject) orderMap.get("data");
                water.setExternalOrderNo(dataObj.getString("tradeNo"));
            }
            water.setAddUser(dto.getMemberAccount());
            water.setAddTime(new Date());
            water.setPayChannels(PayChannelEnum.ALIPAY.getValue());
            water.setOrderType(OrderTypeEnum.ENCHARGE.getValue());
            water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
            water.setPayWay(PayWayEnum.ALIPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
            water.setPayWaterType(PayWaterTypeEnum.RECHARGE.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
            water.setPayStatus("00".equals(retCode) ? PayWaterStatusEnum.PAYING.getValue() : PayWaterStatusEnum.PAY_FAILURE.getValue());
            this.payWaterService.insertSelective(water);
        } catch (Exception e) {
            log.error(orderNo + "充值失败，调用支付宝充值接口发生异常：" + e.getMessage());
            wechatPayResult.setRetCode(MessageEnum.SYSTEM_ERROR.getValue());
            wechatPayResult.setRetMsg(e.getMessage());
            throw new BusinessException("充值失败，调用支付宝充值接口发生异常，" + e.getMessage());
        }
        if (!(StringUtils.equals(retCode, "00"))) {
            log.error("充值失败：" + retMsg);
            wechatPayResult.setRetCode(MessageEnum.SYSTEM_ERROR.getValue());
            wechatPayResult.setRetMsg(retMsg);
            throw new BusinessException("充值失败：" + retMsg);
        }
        log.info("支付宝充值请求处理结束...");
        wechatPayResult.setRetCode(MessageEnum.SUCCESS.getValue());
        wechatPayResult.setRetMsg(MessageEnum.SUCCESS.getMessage());
        // 返回订单号
        JSONObject dataObj = (JSONObject) orderMap.get("data");
        dataObj.put("orderNo", orderNo);
        wechatPayResult.setData(dataObj);
        return wechatPayResult;
    }

    /**
     * @Description 字节跳动支付充值接口（创建用户充值订单）
     * @Author 蒋匿
     * @Date 2021/9/26
     * @Param dto
     * @Return ResultVo
     * @Exception
     */
    public ResultVo bytedancePayRecharge(UserRechargeDTO dto) {
        log.info("字节跳动充值请求开始：");
        ResultVo bytedancePayResult = new ResultVo();
        log.info("开始处理字节跳动充值相关业务...");
        // 创建订单订单号
        String orderNo = OrderNoFactory.getOrderCode(dto.getCurrUserId());

        Map<String, Object> orderMap = null;
        String retCode = null;// 临时变量，支付接口返回码
        String retMsg = null;// 临时变量，支付接口返回信息
        try {
            // 保存订单主表信息
            OrderEntity order = new OrderEntity();
            order.setOemCode(dto.getOemCode());
            order.setOrderNo(orderNo);
            order.setOrderAmount(dto.getAmount());
            order.setPayAmount(dto.getAmount());
            order.setProfitAmount(dto.getAmount());
            order.setOrderType(OrderTypeEnum.ENCHARGE.getValue());
            order.setSourceType(dto.getSourceType());
            // 查询用户渠道映射id
            MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(dto.getCurrUserId())).orElseThrow(() -> new BusinessException("未查询到当前登录用户信息"));
            order.setChannelUserId(member.getChannelUserId());
            // 保存会员订单关系，补全订单参数
            completionParameter(dto.getCurrUserId(), order);
            this.insertSelective(order);

            // 封装参数，请求渠道端微信支付接口完成充值
            String payNo = UniqueNumGenerator.generatePayNo();// 生成24位流水号
            BytedancePayDto bytedancePayDto = buildBytedanceParams(dto.getCurrUserId(), dto.getOemCode(), payNo, dto.getAmount(),orderNo);

            log.info("请求字节跳动支付开始：{}", JSONObject.toJSONString(bytedancePayDto));
            // 根据挡板开关判断流程走向
            DictionaryEntity dict = this.dictionaryService.getByCode("itax_bytedance_switch");
            if(null != dict && "1".equals(dict.getDictValue())){
                orderMap = new HashMap<String, Object>();
                JSONObject data = new JSONObject();
                data.put("payNo",payNo);
                data.put("order_id",payNo);
                data.put("order_token",System.currentTimeMillis());
                orderMap.put("data", data);
                orderMap.put("code","00");
                orderMap.put("msg","支付成功");
            }else{
                orderMap = BytedanceUtils.bytedancePay(bytedancePayDto);// 正常请求支付接口
            }
            log.info("字节跳动支付返回结果：{}", JSONObject.toJSONString(orderMap));

            retCode = orderMap.get("code").toString();// 返回码
            retMsg = orderMap.get("msg").toString();// 返回信息

            if (!(StringUtils.equals(retCode, "00"))) {
                order.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
            }
            this.editByIdSelective(order);

            log.info("字节跳动充值订单创建成功：{}", orderNo);

            // 查询oem机构信息
            OemEntity oem = oemService.getOem(dto.getOemCode());
            if (null == oem) {
                throw new BusinessException("未查询到oem机构");
            }
            // 生成支付流水
            PayWaterEntity water = new PayWaterEntity();
            water.setOemCode(dto.getOemCode());
            water.setOemName(oem.getOemName());
            water.setOrderAmount(dto.getAmount());
            water.setPayAmount(dto.getAmount());
            water.setPayNo(payNo);
            water.setOrderNo(orderNo);
            water.setMemberId(dto.getCurrUserId());

            // 解析返回data数据，拿到外部订单号
            if ("00".equals(retCode)) {
                JSONObject dataObj = (JSONObject) orderMap.get("data");
                water.setExternalOrderNo(dataObj.getString("tradeNo"));
            }
            water.setAddUser(dto.getMemberAccount());
            water.setAddTime(new Date());
            water.setPayChannels(PayChannelEnum.BYTEDANCE.getValue());
            water.setOrderType(OrderTypeEnum.ENCHARGE.getValue());
            water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
            water.setPayWay(PayWayEnum.BYTEDANCE.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
            water.setPayWaterType(PayWaterTypeEnum.RECHARGE.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
            water.setPayStatus("00".equals(retCode) ? PayWaterStatusEnum.PAYING.getValue() : PayWaterStatusEnum.PAY_FAILURE.getValue());
            this.payWaterService.insertSelective(water);
        } catch (Exception e) {
            log.error(orderNo + "充值失败，调用字节跳动充值接口发生异常：" + e.getMessage());
            bytedancePayResult.setRetCode(MessageEnum.SYSTEM_ERROR.getValue());
            bytedancePayResult.setRetMsg(e.getMessage());
            throw new BusinessException("充值失败，调用字节跳动充值接口发生异常，" + e.getMessage());
        }
        if (!(StringUtils.equals(retCode, "00"))) {
            log.error("充值失败：" + retMsg);
            bytedancePayResult.setRetCode(MessageEnum.SYSTEM_ERROR.getValue());
            bytedancePayResult.setRetMsg(retMsg);
            throw new BusinessException("充值失败：" + retMsg);
        }
        log.info("字节跳动充值请求处理结束...");
        bytedancePayResult.setRetCode(MessageEnum.SUCCESS.getValue());
        bytedancePayResult.setRetMsg(MessageEnum.SUCCESS.getMessage());
        // 返回订单号
        JSONObject dataObj = (JSONObject) orderMap.get("data");
        dataObj.put("orderNo", orderNo);
        bytedancePayResult.setData(dataObj);
        return bytedancePayResult;
    }

    /**
     * @Description 组装微信支付参数
     * @Author Kaven
     * @Date 2020/1/7 17:17
     * @Param userId orderNo  amount
     * @Return WechatPayDto
     * @Exception
     */
    @Override
    public WechatPayDto buildWechatParams(Long userId, String oemCode, String payNo, Long amount) throws BusinessException, UnknownHostException {
        //读取渠道微信支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 2);
        if (null == paramsEntity) {
            throw new BusinessException("未配置渠道微信支付相关信息！");
        }
        // 组装参数对象
        WechatPayDto payDto = new WechatPayDto();
        payDto.setTradeNo(payNo);
        payDto.setAmount(String.valueOf(amount));
        payDto.setAgentNo(paramsEntity.getAccount());
        payDto.setAppSecret(paramsEntity.getSecKey());
        payDto.setIpAddr(InetAddress.getLocalHost().getHostAddress());
        payDto.setServicePubKey(paramsEntity.getPublicKey());
        payDto.setPostUrl(paramsEntity.getUrl());

        // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        payDto.setKeyNum(params.getString("keyNum"));
        payDto.setAppId(params.getString("appId"));
        payDto.setSignKey(params.getString("signKey"));

        // callbackUrl回调地址
        DictionaryEntity dic = this.dictionaryService.getByCode("wechatPayNotifyUrl");
        if (null == dic) {
            throw new BusinessException("未配置渠道微信回调通知地址!");
        }
        payDto.setCallbackUrl(dic.getDictValue());

        // 获取微信openId
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if (StringUtils.isBlank(member.getOpenId())) {
            throw new BusinessException("支付失败，用户openId为空");
        }
        payDto.setOpenId(member.getOpenId());

        // 加解密方式设置
        if(StringUtil.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().trim().indexOf("\"channel\":\"new\"") > -1){
            payDto.setChannel("1");
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            if(StringUtils.isBlank(productCode)){
                throw new BusinessException("渠道产品编码配置错误！");
            }
            payDto.setProductCode(productCode);
        }
        return payDto;
    }

    @Override
    public AccessPartyPayDto buildAccessPartyParams(Long userId, String oemCode, String orderNo, Long amount, Integer sourceType, String appId) throws BusinessException, UnknownHostException {
        if (Objects.isNull(sourceType)) {
            throw new BusinessException("操作来源不能为空！");
        }

        //读取渠道微信支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 2);
        if (null == paramsEntity) {
            throw new BusinessException("未配置渠道微信支付相关信息！");
        }
        // 组装参数对象
        AccessPartyPayDto payDto = new AccessPartyPayDto();
        payDto.setTradeNo(orderNo);
        payDto.setAmount(String.valueOf(amount));
        payDto.setAgentNo(paramsEntity.getAccount());
        payDto.setAppSecret(paramsEntity.getSecKey());
        payDto.setUserIp(InetAddress.getLocalHost().getHostAddress());
        payDto.setServicePubKey(paramsEntity.getPublicKey());
        payDto.setPostUrl(paramsEntity.getUrl());

        // 解析paramValues，配置样例：{"keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        payDto.setKeyNum(params.getString("keyNum"));
        payDto.setSignKey(params.getString("signKey"));
        if (1 == sourceType) {
            payDto.setAppId(params.getString("appId"));
            // 获取微信openId
            MemberAccountEntity member = this.memberAccountService.findById(userId);
            if (StringUtils.isBlank(member.getOpenId())) {
                throw new BusinessException("支付失败，用户openId为空");
            }
            payDto.setOpenId(member.getOpenId());
        } else if (2 == sourceType) {
            payDto.setAppId(params.getString("appId"));
            // 获取支付宝用户id
            MemberAccountEntity member = this.memberAccountService.findById(userId);
            if (StringUtils.isBlank(member.getAlipayUserId())) {
                throw new BusinessException("支付失败，用户alipayUserId为空");
            }
            payDto.setBuyerId(member.getAlipayUserId());
        } else if (4 == sourceType) {
            // appId由前端获取
            if (StringUtil.isBlank(appId)) {
                throw new BusinessException("公众号appId为空");
            }
            // 获取微信openId
            MemberAccountEntity member = this.memberAccountService.findById(userId);
            if (StringUtils.isBlank(member.getOpenId())) {
                throw new BusinessException("支付失败，用户openId为空");
            }
            payDto.setOpenId(member.getOpenId());
        }

        // callbackUrl回调地址
        DictionaryEntity dic = this.dictionaryService.getByCode("wechatPayNotifyUrl");
        if (null == dic) {
            throw new BusinessException("未配置渠道微信回调通知地址!");
        }
        payDto.setCallbackUrl(dic.getDictValue());

        // 加解密方式设置
        if(StringUtil.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().trim().indexOf("\"channel\":\"new\"") > -1){
            payDto.setChannel("1");
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            if(StringUtils.isBlank(productCode)){
                throw new BusinessException("渠道产品编码配置错误！");
            }
            payDto.setProductCode(productCode);
        }
        return payDto;
    }


    /**
     * @Description 组装微信退款参数
     * @Author Kaven
     * @Date 2020/1/7 17:17
     * @Param oemCode payNo  refundOrderNo
     * @Return WechatPayDto
     * @Exception
     */
    @Override
    public WechatRefundDto buildWechatRefundParams( String oemCode,String payNo, String refundOrderNo ) throws BusinessException, UnknownHostException {
        //读取渠道微信支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 27);
        if (null == paramsEntity) {
            throw new BusinessException("未配置渠道微信退款相关信息！");
        }
        // 组装参数对象
        WechatRefundDto refundDto = new WechatRefundDto();
        refundDto.setTradeNo(payNo);
        refundDto.setRefundOrderNo(refundOrderNo);
        refundDto.setAgentNo(paramsEntity.getAccount());
        refundDto.setAppSecret(paramsEntity.getSecKey());
        refundDto.setIpAddr(InetAddress.getLocalHost().getHostAddress());
        refundDto.setServicePubKey(paramsEntity.getPublicKey());
        refundDto.setPostUrl(paramsEntity.getUrl());

        // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        refundDto.setKeyNum(params.getString("keyNum"));
        refundDto.setAppId(params.getString("appId"));
        refundDto.setSignKey(params.getString("signKey"));

        // 加解密方式设置
        if(StringUtil.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().trim().indexOf("\"channel\":\"new\"") > -1){
            refundDto.setChannel("1");
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            if(StringUtils.isBlank(productCode)){
                throw new BusinessException("渠道产品编码配置错误！");
            }
            refundDto.setProductCode(productCode);
        }
        return refundDto;
    }

    /**
     * @Description 构建字节跳动退款参数
     * @Author 蒋匿
     * @Date 2021/9/26
     * @Param oemCode payNo refundOrderNo refundAmount
     * @Return BytedanceRefundDto
     * @Exception
     */
    @Override
    public BytedanceRefundDto buildBytedanceRefundParams( PayWaterEntity payWaterEntity, String payNo ) throws BusinessException, UnknownHostException {
        //读取字节跳动支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(payWaterEntity.getOemCode(), 30);
        if (null == paramsEntity) {
            throw new BusinessException("未配置字节跳动退款相关信息！");
        }
        // 组装参数对象
        BytedanceRefundDto refundDto = new BytedanceRefundDto();
        refundDto.setAppId(paramsEntity.getAccount());
        refundDto.setAppSecret(paramsEntity.getSecKey());
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        refundDto.setPaySalt(params.getString("paySalt"));
        refundDto.setTradeNo(payNo); //支付流水号
        refundDto.setRefundOrderNo(payWaterEntity.getPayNo());
        refundDto.setReason("订单取消资金原路退回");
        refundDto.setRefundAmount(payWaterEntity.getPayAmount());
        // callbackUrl回调地址
        DictionaryEntity dic = this.dictionaryService.getByCode("bytedancePayNotifyUrl");
        if (null == dic) {
            throw new BusinessException("未配置字节跳动支付回调通知地址!");
        }
        refundDto.setCallbackUrl(dic.getDictValue());
        refundDto.setOrderNo(payWaterEntity.getOrderNo());
        refundDto.setUserId(payWaterEntity.getMemberId());
        refundDto.setOrderType(payWaterEntity.getOrderType());
        refundDto.setOemCode(payWaterEntity.getOemCode());
        return refundDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> createComCancelOrder(Long userId, String oemCode, Long companyId, String sourceType) throws BusinessException {
        log.info("开始处理创建企业注销订单的请求：{},{},{}", userId, oemCode, companyId);
        Map<String, Object> map = Maps.newHashMap();
        map.put("isConfirmCost", 0); // 是否存在未处理成本税单 0-否 1-是

        MemberCompanyEntity company = this.memberCompanyMapper.selectByPrimaryKey(companyId);
        if (null == company) {
            throw new BusinessException("操作失败，企业信息不存在");
        }
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("操作失败，用户信息不存在");
        }
        ParkEntity park = Optional.ofNullable(parkService.findById(company.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区信息"));

        //已过期企业不允许注销 V2.8
        if (MemberCompanyOverdueStatusEnum.OVERDUE.getValue().equals(company.getOverdueStatus())){
            throw new BusinessException("已过期企业不允许注销");
        }

        // 判断当前企业状态是否非法，只有正常状态的企业才可以注销 V2.8
        if (!(MemberCompanyStatusEnum.NORMAL.getValue().equals(company.getStatus()))) {
            throw new BusinessException("当前企业状态为【" + MemberCompanyStatusEnum.getByValue(company.getStatus()).getMessage() + "】，不允许注销");
        }

        // 判断当前注销企业是否属于当前用户
        if (!userId.equals(company.getMemberId())) {
            throw new BusinessException("操作失败，待注销企业不属于当前用户");
        }

        // 查询该企业是否还有未完成的开票订单
        int count = this.invoiceOrderService.queryNotFinishOrderByCompanyId(companyId, oemCode);
        if (count > 0) {
            throw new BusinessException("该企业还有进行中的开票订单，请完成或取消订单后再试");
        }

        // 校验企业是否有未补缴的税单（核定征收模式）
        if (IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(park.getIncomeLevyType())) {
            List<CompanyTaxBillEntity> list = companyTaxBillService.queryCompanyTaxByCompanyId(companyId);
            if (list != null && list.size()>0){
                throw new BusinessException(ErrorCodeEnum.BILL_TAX_NO_PAYMENT);
            }
        }

        // 园区为查账征收模式
        CompanyTaxBillEntity companyTaxBillEntity = null;
        if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(park.getIncomeLevyType())) {
            // 查询是否有历史未确认成本税单
            PendingTaxBillQuery pendingTaxBillQuery = new PendingTaxBillQuery();
            pendingTaxBillQuery.setEin(company.getEin());
            pendingTaxBillQuery.setCompanyId(companyId);
            pendingTaxBillQuery.setStatusRange(3);
            pendingTaxBillQuery.setRange(2);
            List<PendingTaxBillVO> pendingTaxBillVOS = companyTaxBillService.pendingTaxBill(pendingTaxBillQuery);
            if (CollectionUtil.isNotEmpty(pendingTaxBillVOS)) {
                List<PendingTaxBillVO> collect = pendingTaxBillVOS.stream().filter(x -> TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    throw new BusinessException("存在未确认成本的历史税单");
                }
            }

            // 查询是否有当期税单
            companyTaxBillEntity = companyTaxBillService.queryCompanyTaxBillByEin(company.getEin(), parseInt(DateUtil.getQuarter()), DateUtil.getYear(new Date()), companyId);
            if (null == companyTaxBillEntity || TaxBillStatusEnum.CANCELLED.getValue().equals(companyTaxBillEntity.getTaxBillStatus())) {
                // 不存在当前税期税单时需要生成一条“待填报成本”的税单
                companyTaxBillEntity=new CompanyTaxBillEntity();
                companyTaxBillEntity.setParkTaxBillId(null);
                companyTaxBillEntity.setParkId(park.getId());
                int year = DateUtil.getYear(new Date());
                int quarter = parseInt(DateUtil.getQuarter());
                companyTaxBillEntity.setTaxBillYear(year);
                companyTaxBillEntity.setTaxBillSeasonal(quarter);
                companyTaxBillEntity.setCompanyId(companyId);
                companyTaxBillEntity.setIncomeLevyType(park.getIncomeLevyType()); // V3.11 企业税单保存征收方式
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TO_BE_WRITE_COST.getValue());
                companyTaxBillEntity.setAddTime(new Date());
                companyTaxBillEntity.setAddUser(member.getMemberAccount());
                companyTaxBillEntity.setGenerateType(2);
                companyTaxBillService.insertSelective(companyTaxBillEntity);
                // 获取税费信息
                TaxCalculationVO entity = new TaxCalculationVO();
                entity.setCompanyId(company.getId());
                entity.setType(2);
                entity.setOrderNo(null);
                entity.setVatRate(null);
                entity.setSeason(quarter);
                entity.setYear(year);
                entity.setCalculationType(1);
                Map<String, Object> taxMap = invoiceOrderService.taxCalculation(entity);
                BeanUtil.copyProperties(taxMap, companyTaxBillEntity);
                companyTaxBillService.editByIdSelective(companyTaxBillEntity);
                map.put("isConfirmCost", 1);
                // 生成变更记录
                CompanyTaxBillChangeEntity changeEntity = new CompanyTaxBillChangeEntity();
                BeanUtil.copyProperties(companyTaxBillEntity, changeEntity);
                changeEntity.setId(null);
                changeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
                changeEntity.setAddUser(member.getMemberAccount());
                changeEntity.setAddTime(new Date());
                changeEntity.setUpdateUser(null);
                changeEntity.setUpdateTime(null);
                changeEntity.setDescrip("注销生成税单");
                companyTaxBillChangeService.insertSelective(changeEntity);
            }
            if (Objects.equals(0, map.get("isConfirmCost")) && (TaxBillStatusEnum.TAX_TO_BE_PAID.getValue().equals(companyTaxBillEntity.getTaxBillStatus())
                    || TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(companyTaxBillEntity.getTaxBillStatus()))) {
                throw new BusinessException("税单未确认成本");
            }

        }
        // 查询是否已经存在当前企业的注销订单，若存在全部置为“已取消”状态
        List<OrderEntity> orderList = this.queryComCancelOrder(userId, oemCode, companyId, null);
        for (OrderEntity order : orderList) {
            OrderEntity t = new OrderEntity();
            t.setId(order.getId());
            t.setUpdateTime(new Date());
            t.setUpdateUser(member.getMemberAccount());
            t.setOrderStatus(QYZXOrderStatusEnum.CANCELED.getValue());
            this.editByIdSelective(t);

            // 保存企业注销订单变更记录
            log.info("企业注销订单取消成功，保存订单变更记录：");
            CompanyCancelOrderEntity query = new CompanyCancelOrderEntity();
            query.setOrderNo(order.getOrderNo());
            query.setOemCode(order.getOemCode());
            CompanyCancelOrderEntity cancelOrder = this.companyCancelOrderService.selectOne(query);
            CompanyCancelOrderChangeRecordEntity record = new CompanyCancelOrderChangeRecordEntity();
            BeanUtils.copyProperties(cancelOrder, record);
            record.setId(null);
            record.setOrderStatus(QYZXOrderStatusEnum.CANCELED.getValue());
            record.setAddTime(new Date());
            record.setAddUser(member.getMemberAccount());
            this.companyCancelOrderChangeRecordService.insertSelective(record);
        }

        log.info("开始创建新的企业注销订单：");

        // 查询企业累计开票金额
        Long totalAmount = companyInvoiceRecordService.sumUseInvoiceAmount(companyId);
        log.info("企业累计开票金额：{}", totalAmount);

        // 订单金额
        Long orderAmount = null;
        // 订单状态
        Integer orderStatus = null;

        // 查询企业累积开票额度阈值
        ProductEntity t = new ProductEntity();
        t.setStatus(1);
        t.setOemCode(oemCode);
        // 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任',
        if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.INDIVIDUAL_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.INDEPENDENTLY_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.LIMITED_PARTNER_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue());
        }
        ProductEntity product = this.productService.selectOne(t);

        if (null == product) {
            throw new BusinessException("操作失败，未配置企业注销相关产品信息");
        }

        // 1.累积开票额度大于阈值可免注销服务费 2.若产品金额为0，也视为免注销服务费
        if (totalAmount >= product.getCancelTotalLimit() || product.getProdAmount() == 0) {
            orderAmount = 0L;
            orderStatus = QYZXOrderStatusEnum.CANCEL_HANDLEING.getValue();// 订单状态置为“注销处理中”
        } else {
            orderAmount = product.getProdAmount();
            orderStatus = QYZXOrderStatusEnum.TO_BE_PAID.getValue();// 订单状态置为“待付款”
        }

        /**
         *  判断是否存在特价活动，
         *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
         */
        ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
        productDiscountActivityAPIDTO.setOemCode(oemCode);
        productDiscountActivityAPIDTO.setMemberId(userId);
        productDiscountActivityAPIDTO.setIndustryId(company.getIndustryId());
        productDiscountActivityAPIDTO.setParkId(company.getParkId());
        productDiscountActivityAPIDTO.setProductType(t.getProdType());
        ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        if(productDiscountActivityVO!=null) {
            // 1.累积开票额度大于阈值可免注销服务费 2.若产品金额为0，也视为免注销服务费
            if (totalAmount >= productDiscountActivityVO.getCancelTotalLimit() || productDiscountActivityVO.getSpecialPriceAmount() == 0) {
                orderAmount = 0L;
                orderStatus = QYZXOrderStatusEnum.CANCEL_HANDLEING.getValue();// 订单状态置为“注销处理中”
            } else {
                orderAmount = productDiscountActivityVO.getSpecialPriceAmount();
                orderStatus = QYZXOrderStatusEnum.TO_BE_PAID.getValue();// 订单状态置为“待付款”
            }
            if (null != companyTaxBillEntity && Objects.equals(1, map.get("isConfirmCost"))) { // 园区征收模式为查账征收，且税单未确认成本
                orderStatus = QYZXOrderStatusEnum.TO_BE_TAX_BILL.getValue();// 订单状态置为“税单待处理”
            }
        }
        // 构建企业注销订单主表对象
        OrderEntity mainOrder = this.buildComCancelMainOrder(userId, oemCode, product.getId(), product.getProdName(),
                company.getParkId(), orderAmount, orderStatus, null);
        mainOrder.setSourceType(parseInt(sourceType));
        //保存人群标签id
        Long crowdLabelId = memberCrowdLabelRelaService.getCrowLabelIdByMemberId(member.getId(),member.getOemCode());
        if(crowdLabelId!=null){
            mainOrder.setCrowdLabelId(crowdLabelId);
        }
        if(productDiscountActivityVO!=null){
            mainOrder.setDiscountActivityId(productDiscountActivityVO.getDiscountActivityId());
        }
        // 保存订单主表信息
        this.insertSelective(mainOrder);

        // 生成企业注销订单
        CompanyCancelOrderEntity companyCancelOrder = this.buildComCancelOrder(mainOrder, companyId, company.getCompanyType(), company.getCompanyName(), totalAmount);
        this.companyCancelOrderService.insertSelective(companyCancelOrder);

        // 保存企业注销订单变更记录
        CompanyCancelOrderChangeRecordEntity changeRecord = new CompanyCancelOrderChangeRecordEntity();
        BeanUtils.copyProperties(companyCancelOrder, changeRecord);
        changeRecord.setId(null);
        changeRecord.setOrderStatus(mainOrder.getOrderStatus());
        this.companyCancelOrderChangeRecordService.insertSelective(changeRecord);

        log.info("企业注销订单创建成功：{}", JSON.toJSONString(mainOrder));

        //如果注销企业为免手续费则需要将企业表数据的状态改成注销中 add ni.jiang
        if (QYZXOrderStatusEnum.CANCEL_HANDLEING.getValue().equals(orderStatus)) {
            MemberCompanyEntity companyEntity = new MemberCompanyEntity();
            companyEntity.setId(company.getId());
            companyEntity.setStatus(MemberCompanyStatusEnum.CANCELLING.getValue());
            companyEntity.setUpdateTime(new Date());
            companyEntity.setUpdateUser(member.getMemberAccount());
            memberCompanyMapper.updateByPrimaryKeySelective(companyEntity);
        }

        log.info("企业注销请求处理完毕");
        map.put("orderNo", mainOrder.getOrderNo());
        return map;
    }

    /**
     * @Description 构建企业注销订单表数据
     * @Author Kaven
     * @Date 2020/2/19 15:53
     * @Param mainOrder
     * @Return CompanyCancelOrderEntity
     * @Exception
     */
    private CompanyCancelOrderEntity buildComCancelOrder(OrderEntity mainOrder, Long companyId, Integer companyType, String companyName, Long cancelTotalLimit) {
        CompanyCancelOrderEntity companyCancelOrder = new CompanyCancelOrderEntity();
        // 参数拷贝
        BeanUtils.copyProperties(mainOrder, companyCancelOrder);
        companyCancelOrder.setId(null);
        companyCancelOrder.setCompanyId(companyId);
        companyCancelOrder.setCancelServiceCharge(mainOrder.getPayAmount());
        companyCancelOrder.setCancelTotalLimit(cancelTotalLimit);
        companyCancelOrder.setCompanyType(companyType);
        companyCancelOrder.setCompanyName(companyName);
        companyCancelOrder.setOperUserType(1);// 操作人类型  0-用户本人 1-系统  2-系统用户
        return companyCancelOrder;
    }

    @Override
    public List<OrderEntity> queryMemberLvUpOrder(Long memberId, Integer productType) {
        return mapper.queryMemberLvUpOrder(memberId, productType,
                OrderTypeEnum.UPGRADE.getValue(), UserTypeEnum.MEMBER.getValue(),
                AuditStateEnum.TO_APPROVE.getValue(), MemberOrderStatusEnum.TO_BE_AUDIT.getValue());
    }

    @Override
    @Transactional
    public OrderEntity memberUpgrade(MemberAccountEntity entity, MemberLevelEntity memberLevelEntity, UserBankCardEntity userBankCardEntity, String useraccount, String remark) {
        Date date = new Date();
        OrderEntity order = new OrderEntity();
        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(entity.getId());
        // 保存订单主表信息
        order.setOemCode(entity.getOemCode());
        order.setUserId(entity.getId());
        order.setUserType(UserTypeEnum.MEMBER.getValue());
        order.setIsShareProfit(0);
        order.setDiscountAmount(0L);
        order.setOrderNo(orderNo);
        order.setOrderAmount(0L);
        order.setPayAmount(0L);
        order.setProfitAmount(0L);
        order.setProfitStatus(0);
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setProductName(memberLevelEntity.getLevelName());
        order.setProductId(memberLevelEntity.getId());
        order.setRemark(remark);
        // 补全订单参数，保存关系表
        completionParameter(entity.getId(), order);
        order.setAddTime(date);
        order.setAddUser(useraccount);
        order.setOrderStatus(MemberOrderStatusEnum.TO_BE_AUDIT.getValue());
        order.setOrderType(OrderTypeEnum.UPGRADE.getValue());
        order.setChannelUserId(entity.getChannelUserId());
        order.setChannelProductCode(entity.getChannelProductCode());
        order.setChannelCode(entity.getChannelCode());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(order.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            order.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }
        mapper.insertSelective(order);
        //保存实名银行卡信息
        if (userBankCardEntity != null) {
            userBankCardMapper.insertSelective(userBankCardEntity);
            //保存用户实名
            memberAccountService.updateUserAuth(userBankCardEntity.getUserId(), userBankCardEntity.getUserName(), userBankCardEntity.getIdCard(), entity.getIdCardFront(), entity.getIdCardBack(), entity.getExpireDate(), entity.getIdCardAddr(), MemberAuthStatusEnum.AUTH_SUCCESS.getValue(),"会员升级实名");
        }
        return  order;
    }

    @Override
    public List<BillDetailVO> listBillDetail(Long memberId, String oemCode, BillDetailQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        if(Objects.equals(query.getWalletType(), 1)){
            if(Objects.equals(query.getType(), 4)){
                throw new BusinessException("不支持此类型");
            }
            List<BillDetailVO> billDetailVOS = orderMapper.listConsumerBillDetail(memberId, oemCode, query.getType(), query.getMonth(), query.getWalletType(), null);
            // 代理充值只展示支付流水状态“支付完成”的
//            return billDetailVOS.stream().filter(vo -> !PayWaterOrderTypeEnum.AGENT_RECHARGE.getValue().equals(vo.getOrderType()) || "1".equals(vo.getPayStatus())).collect(Collectors.toList());
            return  billDetailVOS;
        }else{
            if(Objects.equals(query.getType(), 1) || Objects.equals(query.getType(), 3) || Objects.equals(query.getType(), 4) || Objects.equals(query.getType(), 5)){
                return orderMapper.listCommissionBillDetail(memberId, oemCode, query.getType(), query.getMonth(), query.getWalletType(), null);
            }else{
                throw new BusinessException("不支持此类型");
            }
        }
    }

    @Override
    public BillIncomePayVO statisBillIncomePay(Long memberId, String oemCode, BillDetailQuery query) {
        BillIncomePayVO billIncomePay = new BillIncomePayVO();
        //查询类型type：1->全部；2->充值；3->提现；4->推广分润；5->企业注册；6->企业开票；7->会员升级；8->企业注销；9->证件领用申请 ；10->对公户申请
        //订单类型orderType：1-充值 2-代理充值 3-提现 4-代理提现 5-工商开户 6-开票 7-用户升级 8-工商注销 9->证件领用申请 19-推广分润 20-退款

        // 钱包类型 1-消费钱包 2-佣金钱包
        if(Objects.equals(query.getWalletType(), 1)){
            // 根据查询类型统计
            if(Objects.equals(query.getType(), 4)){
                throw new BusinessException("不支持此类型");
            }
            if (Objects.equals(query.getType(), 1)) {
                List<BillDetailVO> billList = orderMapper.listConsumerBillDetail(memberId, oemCode, query.getType(), query.getMonth(), query.getWalletType(), query.getType());
//                Long incomeAmount = 0L;
                Long payAmount = 0L;
                Long refundAmount = 0L;
                payAmount = billList.stream().filter((b) -> b.getOrderType() == 5 || b.getOrderType() == 6 || b.getOrderType() == 7
                        || b.getOrderType() == 8 || b.getOrderType() == 9 || b.getOrderType() == 10 || b.getOrderType() == 13).mapToLong(BillDetailVO::getAmount).sum();
                refundAmount = billList.stream().filter((b) -> b.getOrderType() == 14 || b.getOrderType() == 20).mapToLong(BillDetailVO::getAmount).sum();
                billIncomePay.setIncomeAmount(refundAmount);
                billIncomePay.setPayAmount(payAmount);
            } else if (Objects.equals(query.getType(), 5) || Objects.equals(query.getType(), 6) || Objects.equals(query.getType(), 7)
                    || Objects.equals(query.getType(), 8) || Objects.equals(query.getType(), 9) || Objects.equals(query.getType(), 10)
                    || Objects.equals(query.getType(), 13) || Objects.equals(query.getType(), 14)) {
                List<BillDetailVO> billList = orderMapper.listConsumerBillDetail(memberId, oemCode, query.getType(), query.getMonth(), query.getWalletType(), query.getType());
                Long payAmount = billList.stream().mapToLong(BillDetailVO::getAmount).sum();

                // 查询退款
                Long refundAmount = orderMapper.billRefundAmount(memberId, oemCode, query.getType(), query.getMonth(), query.getWalletType());
                if (null == refundAmount) {
                    refundAmount = 0L;
                }
                billIncomePay.setIncomeAmount(refundAmount);
                billIncomePay.setPayAmount(payAmount);
            } else {
                billIncomePay.setIncomeAmount(0L);
                billIncomePay.setPayAmount(0L);
            }
        }else{
            // 根据查询类型统计
            if(Objects.equals(query.getType(), 1) || Objects.equals(query.getType(), 3) || Objects.equals(query.getType(), 4)){
                if (Objects.equals(query.getType(), 1)) {
                    List<BillDetailVO> billList = orderMapper.listCommissionBillDetail(memberId, oemCode, query.getType(), query.getMonth(), query.getWalletType(), query.getType());
                    Long incomeAmount = 0L;
                    Long payAmount = 0L;
                    incomeAmount = billList.stream().filter((b) -> b.getOrderType() == 19).mapToLong(BillDetailVO::getAmount).sum();
                    billIncomePay.setIncomeAmount(incomeAmount);
                    billIncomePay.setPayAmount(payAmount);
                } else if (Objects.equals(query.getType(), 4)) {
                    List<BillDetailVO> billList = orderMapper.listCommissionBillDetail(memberId, oemCode, query.getType(), query.getMonth(), query.getWalletType(), query.getType());
                    Long incomeAmount = billList.stream().mapToLong(BillDetailVO::getAmount).sum();
                    billIncomePay.setIncomeAmount(incomeAmount);
                    billIncomePay.setPayAmount(0L);
                }else {
                    billIncomePay.setIncomeAmount(0L);
                    billIncomePay.setPayAmount(0L);
                }
            }else{
                throw new BusinessException("不支持此类型");
            }
        }
        return billIncomePay;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminWithdraw(UserCapitalAccountEntity entity, UserBankCardEntity cardEntity, Long amount, MemberAccountEntity member, Integer withdrawType,
                              String commissionInvoiceOrderNo, Integer sourceType) throws BusinessException {
        OemEntity oem = oemService.getOem(entity.getOemCode());
        if (oem == null) {
            throw new BusinessException("OEM机构不存在");
        }
        String message = isWithdrawalLimit(cardEntity.getUserId(), cardEntity.getUserType(), amount, withdrawType);
        if (StringUtils.isNoneBlank(message)) {
            throw new BusinessException(message);
        }
        // 生成提现订单记录
        String orderNo = OrderNoFactory.getOrderCode(entity.getUserId()); // 生成订单号
        Date date = new Date();
        String addUser = member.getMemberAccount();
        // 保存订单主表信息
        OrderEntity order = new OrderEntity();
        order.setOemCode(entity.getOemCode());
        order.setOrderNo(orderNo);
        order.setCommissionInvoiceOrderNo(commissionInvoiceOrderNo);// 佣金开票订单号
        order.setOrderAmount(amount);
        order.setPayAmount(amount);
        order.setProfitAmount(0L);
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setOrderType(OrderTypeEnum.WITHDRAW.getValue());
        order.setSourceType(sourceType);
        order.setChannelUserId(member.getChannelUserId());
        if(WalletTypeEnum.CONSUMER_WALLET.getValue().equals(withdrawType)){
            order.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        } else {
            order.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
        }
        order.setAddUser(addUser);

        //设置到账金额
        Long withdrawAmount = amount;

        // 佣金提现需计算手续费，根据会员等级取手续费率
        if(withdrawType.intValue() != 1){
            MemberLevelEntity memberLevel = this.memberLevelService.findById(member.getMemberLevel());
            if(MemberLevelEnum.DIAMOND.getValue().intValue() == memberLevel.getLevelNo()){
                order.setServiceFeeRate(oem.getDiamondCommissionServiceFeeRate());
            } else {
                order.setServiceFeeRate(oem.getCommissionServiceFeeRate());
            }
            // 计算服务费
            BigDecimal serviceFee = (order.getServiceFeeRate().divide(new BigDecimal(100))).multiply(new BigDecimal(order.getPayAmount()));
            order.setServiceFee(serviceFee.setScale(0, BigDecimal.ROUND_UP).longValue());// 舍弃小数位，向上取整
            withdrawAmount = amount - order.getServiceFee();
            if (withdrawAmount <= 0L) {
                throw new BusinessException("到账金额大于0时方可提现");
            }
        } else { // 消费钱包提现超过免费额度部分收取手续费V4.1.3
            Long chargePartyAmount = 0L; // 超过部分金额
            // 获取月累计提现金额
            Long monthlyWithdrawalAmount = orderService.monthlyWithdrawalAmount(member.getId(), DateUtil.getYear(new Date()), DateUtil.getMonth(new Date()));
            if ((monthlyWithdrawalAmount - oem.getConsumptionWithdrawFreeCredit()) >= 0L) {
                chargePartyAmount = amount;
            } else {
                monthlyWithdrawalAmount += amount;
                if ((monthlyWithdrawalAmount - oem.getConsumptionWithdrawFreeCredit()) > 0L) {
                    chargePartyAmount = monthlyWithdrawalAmount - oem.getConsumptionWithdrawFreeCredit();
                } else {
                    chargePartyAmount = 0L;
                }
            }
            Long serviceFee = 0L;
            if (chargePartyAmount < 0L) {
                chargePartyAmount = 0L;
            }
            if (chargePartyAmount > 0L && oem.getConsumptionWithdrawRate().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = oem.getConsumptionWithdrawRate().divide(new BigDecimal("100"));
                serviceFee = new BigDecimal(chargePartyAmount).multiply(rate).setScale(0, BigDecimal.ROUND_UP).longValue();
            }
            order.setServiceFee(serviceFee);
            order.setServiceFeeRate(oem.getConsumptionWithdrawRate());
            withdrawAmount = amount - serviceFee;
            if (withdrawAmount <= 0L) {
                throw new BusinessException("到账金额大于0时方可提现");
            }
        }
        // 补全订单参数
        completionParameter(entity.getUserId(), order);
        order.setAddTime(date);
        order.setAddUser(addUser);
        this.insertSelective(order);

        PayWaterEntity water = new PayWaterEntity();

        RepayDetailVO repayDetailVO = new RepayDetailVO(cardEntity, withdrawAmount, date);
        water.setExternalOrderNo(repayDetailVO.getOrderNo());
        // 生成支付流水
        water.setOemCode(entity.getOemCode());
        water.setOemName(oem.getOemName());
        water.setPayAccount(cardEntity.getBankNumber());
        water.setPayBank(cardEntity.getBankName());
        water.setPayTime(date);
        water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        water.setOrderNo(orderNo);
        water.setMemberId(entity.getUserId());
        water.setUserType(entity.getUserType());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setAddTime(date);
        water.setAddUser(addUser);
        water.setPayChannels(PayChannelEnum.PROXYPAY.getValue());
        water.setServiceFeeRate(order.getServiceFeeRate());
        water.setServiceFee(order.getServiceFee());

        //设置提现通道
        OemParamsEntity params = oemParamsService.getParams(entity.getOemCode(), OemParamsTypeEnum.REPAY.getValue());
        if (params == null) {
            throw new BusinessException("机构参数未配置");
        }
        if (!Objects.equals(params.getStatus(), OemStatusEnum.YES.getValue())) {
            throw new BusinessException("机构参数不可用");
        }
        if (StringUtils.isNotBlank(params.getParamsValues()) && params.getParamsValues().indexOf("\"channel\":\"ccb\"") > -1) {
            water.setPayChannels(PayChannelEnum.CCBPAY.getValue());
        }else{
            water.setPayChannels(PayChannelEnum.PROXYPAY.getValue());
        }
        water.setOrderType(OrderTypeEnum.WITHDRAW.getValue());
        water.setPayWay(PayWayEnum.QUICKPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        water.setPayWaterType(PayWaterTypeEnum.WITHDRAW.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
        water.setPayAmount(amount);// 支付金额
        water.setOrderAmount(amount);// 订单金额
        water.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
        water.setWalletType(withdrawType.intValue() == 1 ? WalletTypeEnum.CONSUMER_WALLET.getValue() : WalletTypeEnum.COMMISSION_WALLET.getValue()); //钱包类型
        this.payWaterService.insertSelective(water);

        //资金账户冻结
        userCapitalAccountService.freezeBalance(entity, order.getOrderNo(), order.getOrderType(), amount, addUser);
        // 根据挡板开关判断流程走向
        DictionaryEntity dict = this.dictionaryService.getByCode("itax_withdrawal_switch");
        if(null != dict && "1".equals(dict.getDictValue())){ // 挡板模拟返回
            //订单状态
            order.setOrderStatus(RACWStatusEnum.PAYED.getValue());
            order.setUpdateTime(new Date());
            order.setUpdateUser(addUser);
            //流水状态
            water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            water.setUpdateTime(new Date());
            water.setUpdateUser(addUser);
//            //订单状态
//            order.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
//            //流水状态
//            water.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
            handelWithdrawResult(order, water, entity);
            return;
        }
        //代付
        String repay = repay(entity.getOemCode(), order.getOrderNo(), "", repayDetailVO);
        if (StringUtils.isBlank(repay)) {
            return;
        }
        //代付失败
        throw new BusinessException(repay);
    }


    /**
     * 是否已达提现限额
     * @param userId  用户id
     * @param userType 用户类型
     * @param amount
     * @return 0-数据错误 1-超过单笔限额 2-超过单日限额 -1 正常
     */
    private String isWithdrawalLimit(Long userId,Integer userType,Long amount,Integer withdrawType){
        if(WalletTypeEnum.CONSUMER_WALLET.getValue().equals(withdrawType)){
            withdrawType = WalletTypeEnum.CONSUMER_WALLET.getValue();
        } else {
            withdrawType = WalletTypeEnum.COMMISSION_WALLET.getValue();
        }
        if(userId == null || amount == 0){
            return "提现金额错误或用户信息错误";
        }
        DictionaryEntity dictionaryEntityBySingle = sysDictionaryService.getByCode("single_withdrawal_limit"); //单笔限额
        if(dictionaryEntityBySingle != null && StringUtils.isNotBlank(dictionaryEntityBySingle.getDictValue())){
            Long singleLimitCash = Long.parseLong(dictionaryEntityBySingle.getDictValue())*100;
            if(singleLimitCash < amount){
                return "提现金额已超过单笔限额，请重试！";
            }
        }
        DictionaryEntity dictionaryEntityByDaily =  sysDictionaryService.getByCode("daily_withdrawal_limit"); //单日限额
        if(dictionaryEntityByDaily != null && StringUtils.isNotBlank(dictionaryEntityByDaily.getDictValue())){
            Long dailyLimitCash = Long.parseLong(dictionaryEntityByDaily.getDictValue())*100;
            Long  currentDayWithdrawalAmount = mapper.queryCurrentDayWithdrawalAmountByUserId(userId,userType,1,withdrawType);
            if(currentDayWithdrawalAmount!=null && dailyLimitCash < (currentDayWithdrawalAmount + amount)){
                return "今天提现金额已达上限，请明天再提！";
            }
        }
        return "";
    }

    /**
     * 代付处理
     *
     * @param oemCode   机构编码
     * @param batchNo   批次号
     * @param notifyUrl 回调地址
     * @param vo        代付详情对象
     * @return
     */
    public String repay(String oemCode, String batchNo, String notifyUrl, RepayDetailVO vo) {
        OemParamsEntity params = oemParamsService.getParams(oemCode, OemParamsTypeEnum.REPAY.getValue());
        if (params == null) {
            throw new BusinessException("机构参数未配置");
        }
        if (!Objects.equals(params.getStatus(), OemStatusEnum.YES.getValue())) {
            throw new BusinessException("机构参数不可用");
        }
        JSONObject json = null;
        if (StringUtils.isNotBlank(params.getParamsValues()) && params.getParamsValues().indexOf("\"channel\":\"ccb\"") > -1) { //建行网金
            json = ccbRepayService.merchantBalance(oemCode, params);
            if (json != null) {
                BigDecimal balance = json.getBigDecimal("balance1"); //账号可用余额
                if (balance == null || balance.compareTo(new BigDecimal(vo.getRepayAmount())) < 0) {
                    throw new BusinessException("今日额度已用尽，请下个工作日再试!");
                }
            }
            json = ccbRepayService.repay(oemCode, batchNo, vo.getRepayAmount(), vo, params);
            if (json == null) {
                //没有同步返回受理结果，需要查询处理
                return null;
            } else if ("000000".equals(json.getString("returnCode"))) {
                return null;
            } else {
                return json.getString("returnMsg");
            }
        }else if (StringUtils.isNotBlank(params.getParamsValues()) && params.getParamsValues().indexOf("\"channel\":\"ccbzx\"") > -1) { //建行专线
            OemParamsEntity ccbzxParams = oemParamsService.getParams(oemCode, OemParamsTypeEnum.REPAY_QUERY_AMOUNT.getValue());
            if (ccbzxParams == null) {
                throw new BusinessException("机构参数未配置");
            }
            if (!Objects.equals(ccbzxParams.getStatus(), OemStatusEnum.YES.getValue())) {
                throw new BusinessException("机构参数不可用");
            }
            json = ccbZXRepayService.merchantBalance(oemCode, ccbzxParams);
            if (json != null && "00".equals(json.getString("bizCode"))) {
                BigDecimal balance = json.getBigDecimal("accAvlBal"); //账号可用余额
                if (balance == null || balance.compareTo(new BigDecimal(vo.getRepayAmount())) < 0) {
                    throw new BusinessException("今日额度已用尽，请下个工作日再试!");
                }
            }else{
                if(json !=null) {
                    log.error("=====建行专线余额查询失败：" + json.get("bizCodeMsg"));
                }
                throw new BusinessException("余额查询失败");
            }

            json = ccbZXRepayService.repay(oemCode, batchNo, vo.getRepayAmount(), vo, params);
            if (json == null) {
                //没有同步返回受理结果，需要查询处理
                return null;
            } else if ("00".equals(json.getString("bizCode"))) {
                return null;
            } else {
                return json.getString("bizCodeMsg");
            }
        } else { //北京银行
            List<RepayDetailVO> list = Lists.newArrayList(vo);
            json = partiallyRepayService.repay(oemCode, batchNo, vo.getRepayAmount(), list.size() + "", vo.getOrderTime(), list, notifyUrl, params);
            if (json == null) {
                //没有同步返回受理结果，需要查询处理
                return null;
            }
            String resCode = json.getString("p1_resCode");
            if (StringUtils.equals(RepayMessageEnum.RPY0000.getValue(), resCode)) {
                //成功
                return null;
            }
            if (StringUtils.equals(RepayMessageEnum.RPY3110.getValue(), resCode)
                    || StringUtils.equals(RepayMessageEnum.RPY3111.getValue(), resCode)
                    || StringUtils.equals(RepayMessageEnum.RPY3112.getValue(), resCode)
                    || StringUtils.equals(RepayMessageEnum.RPY3113.getValue(), resCode)
                    || StringUtils.equals(RepayMessageEnum.RPY3114.getValue(), resCode)) {
                //结果未知，需要查询处理
                return null;
            }

            return json.getString("p2_resMsg");
        }
    }


    @Override
    @Transactional
    public void withdrawQuery(OrderEntity orderEntity) throws BusinessException {
        PayWaterEntity payWaterEntity = new PayWaterEntity();
        payWaterEntity.setOrderNo(orderEntity.getOrderNo());
        payWaterEntity.setOemCode(orderEntity.getOemCode());
        payWaterEntity = payWaterService.selectOne(payWaterEntity);
        if (payWaterEntity == null) {
            throw new BusinessException("提现流水不存在");
        }
        //判断如果是纳呗通道 则直接退出本次查询
        if(payWaterEntity.getPayChannels().intValue() == PayChannelEnum.NABEIPAY.getValue()){
            return ;
        }
        UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), orderEntity.getOemCode(), orderEntity.getWalletType());
        if (accEntity == null) {
            throw new BusinessException("资金账户不存在");
        }
        // 结果查询
        if(payWaterEntity.getPayChannels().intValue() == PayChannelEnum.YISHUIPAY.getValue()){
            handleYiShuiPayResult(orderEntity, payWaterEntity);
        } else {
            //代付结果查询
            repayQuery(orderEntity, payWaterEntity);
        }
        //处理提现结果
        handelWithdrawResult(orderEntity, payWaterEntity, accEntity);
    }

    @Override
    public List<OrderEntity> invOrderListByType() {
        return orderMapper.invOrderListByType();
    }

    @Override
    public List<OrderEntity> invOrderListByOrderType() {
        return orderMapper.invOrderListByOrderType();
    }

    @Transactional
    public void handelWithdrawResult(OrderEntity orderEntity, PayWaterEntity payWaterEntity, UserCapitalAccountEntity accEntity) {
        if (Objects.equals(orderEntity.getOrderStatus(), RACWStatusEnum.PAYED.getValue())) {
            //提现成功
            mapper.updateByPrimaryKeySelective(orderEntity);
            payWaterService.editByIdSelective(payWaterEntity);
            //删除冻结款
            userCapitalAccountService.delFreezeBalance(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(), orderEntity.getAddUser());

            if(orderEntity.getWalletType().intValue() == WalletTypeEnum.COMMISSION_WALLET.getValue()){ //佣金钱包提现，需要将提现手续费添加到oem机构的资金账号
                UserEntity userEntity = new UserEntity();
                userEntity.setPlatformType(2);
                userEntity.setAccountType(1);
                userEntity.setOemCode(orderEntity.getOemCode());
                userEntity = userService.selectOne(userEntity);
                userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), userEntity.getId(), 2, orderEntity.getServiceFee(),
                        orderEntity.getServiceFee(), 0L, 0L, "订单[" + orderEntity.getOrderNo() + "佣金提现手续费]", orderEntity.getUpdateUser(), new Date(), 1,WalletTypeEnum.CONSUMER_WALLET.getValue());

                //修改佣金开票订单状态
                InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderEntity.getCommissionInvoiceOrderNo());
                OrderEntity commissionInvoiceOrder = queryByOrderNo(orderEntity.getCommissionInvoiceOrderNo());
                if(commissionInvoiceOrder!=null && invoiceOrderEntity != null) {
                    commissionInvoiceOrder.setOrderStatus(InvoiceOrderStatusEnum.IN_TICKETING.getValue());
                    invoiceOrderService.updateInvoiceStatus(invoiceOrderEntity, commissionInvoiceOrder, "admin", "佣金提现开票订单状态修改");
                }
            } else { //消费钱包提现，需要将提现手续费添加到oem机构的资金账号
                UserEntity userEntity = new UserEntity();
                userEntity.setPlatformType(2);
                userEntity.setAccountType(1);
                userEntity.setOemCode(orderEntity.getOemCode());
                userEntity = userService.selectOne(userEntity);
                userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), userEntity.getId(), 2, orderEntity.getServiceFee(),
                        orderEntity.getServiceFee(), 0L, 0L, "订单[" + orderEntity.getOrderNo() + "消费钱包提现手续费]", orderEntity.getUpdateUser(), new Date(), 1,WalletTypeEnum.CONSUMER_WALLET.getValue());
            }
            return;
        }
        if (Objects.equals(orderEntity.getOrderStatus(), RACWStatusEnum.PAY_FAILURE.getValue())) {
            //提现失败
            mapper.updateByPrimaryKeySelective(orderEntity);
            payWaterService.editByIdSelective(payWaterEntity);
            //资金解冻
            userCapitalAccountService.unfreezeBalance(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(), orderEntity.getAddUser());
            //修改佣金开票订单状态
            InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderEntity.getCommissionInvoiceOrderNo());
            OrderEntity commissionInvoiceOrder = queryByOrderNo(orderEntity.getCommissionInvoiceOrderNo());
            if(commissionInvoiceOrder!=null && invoiceOrderEntity != null) {
                //修改订单状态
                commissionInvoiceOrder.setOrderStatus(InvoiceOrderStatusEnum.CANCELED.getValue());
                invoiceOrderService.updateInvoiceStatus(invoiceOrderEntity, commissionInvoiceOrder, "admin", "佣金提现开票订单状态修改");
                // 修改企业开票额度
                CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(invoiceOrderEntity.getCompanyId());
                if(companyInvRecord != null) {
                    // 查询会员账号
                    MemberAccountEntity member = memberAccountService.findById(accEntity.getUserId());
                    if (null == member) {
                        throw new BusinessException("未查询到会员账号");
                    }
                    companyInvoiceRecordMapper.refund(invoiceOrderEntity.getCompanyId(),invoiceOrderEntity.getAddTime(),invoiceOrderEntity.getInvoiceAmount(),member.getMemberAccount(),new Date());
                }

                // 取消工单
                workOrderService.cancelWorkOrder("admin", invoiceOrderEntity.getOrderNo(), invoiceOrderEntity.getOemCode());
            }
            return;
        }
        //其他结果不处理订单
        return;
    }

    /**
     * 代付订单查询
     *
     * @param orderEntity
     * @param payWaterEntity
     * @return
     */
    public void repayQuery(OrderEntity orderEntity, PayWaterEntity payWaterEntity) {
        OemParamsEntity params = oemParamsService.getParams(orderEntity.getOemCode(), OemParamsTypeEnum.REPAY.getValue());
        if (params == null) {
            throw new BusinessException("机构参数未配置");
        }
        if (!Objects.equals(params.getStatus(), OemStatusEnum.YES.getValue())) {
            throw new BusinessException("机构参数不可用");
        }
        JSONObject json = null;
        if (StringUtils.isNotBlank(params.getParamsValues()) && params.getParamsValues().indexOf("\"channel\":\"ccb\"") > -1) { //建行网金
            json = ccbRepayService.repayQuery(orderEntity.getOemCode(), orderEntity.getOrderNo(), params);
            if (json == null) {
                //没有结果，重新查询处理
                return;
            }

            if ("000000".equals(json.getString("returnCode"))) {
                // 处理结果状态：0-处理中，1-成功，2-失败
                if ("1".equals(json.getString("status"))) {
                    //订单状态
                    orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
                    orderEntity.setUpdateTime(new Date());
                    orderEntity.setUpdateUser("xxljob");
                    //流水状态
                    payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                    payWaterEntity.setUpdateUser("xxljob");
                    payWaterEntity.setUpdateTime(new Date());
                    return;
                }
            }
            if ("2".equals(json.getString("status"))) {
                //订单状态
                orderEntity.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
                orderEntity.setRemark(json.getString("returnMsg"));
                orderEntity.setUpdateTime(new Date());
                orderEntity.setUpdateUser("xxljob");
                //流水状态
                payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
                payWaterEntity.setUpStatusCode("9999");
                payWaterEntity.setUpResultMsg(json.getString("returnMsg"));
                payWaterEntity.setUpdateUser("xxljob");
                payWaterEntity.setUpdateTime(new Date());
                return;
            }
        }else if (StringUtils.isNotBlank(params.getParamsValues()) && params.getParamsValues().indexOf("\"channel\":\"ccbzx\"") > -1) { //建行专线
            params = oemParamsService.getParams(orderEntity.getOemCode(), OemParamsTypeEnum.REPAY_QUERY_ORDER.getValue());
            if (params == null) {
                throw new BusinessException("机构参数未配置");
            }
            if (!Objects.equals(params.getStatus(), OemStatusEnum.YES.getValue())) {
                throw new BusinessException("机构参数不可用");
            }
            json = ccbZXRepayService.repayQuery(orderEntity.getOemCode(), orderEntity.getOrderNo(), params);
            if (json == null) {
                //没有结果，重新查询处理
                return;
            }

            if ("00".equals(json.getString("bizCode"))) {
                //处理订单状态
                if("4".equals(json.getString("status")) || "-1".equals(json.getString("status"))) {
                    //订单失败
                    //订单状态
                    orderEntity.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
                    orderEntity.setRemark(json.getString("bizCodeMsg"));
                    orderEntity.setUpdateTime(new Date());
                    orderEntity.setUpdateUser("xxljob");
                    //流水状态
                    payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
                    payWaterEntity.setUpStatusCode("9999");
                    payWaterEntity.setUpResultMsg(json.getString("bizCodeMsg"));
                    payWaterEntity.setUpdateUser("xxljob");
                    payWaterEntity.setUpdateTime(new Date());
                }else if("5".equals(json.getString("status"))) {
                    //订单状态
                    orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
                    orderEntity.setUpdateTime(new Date());
                    orderEntity.setUpdateUser("xxljob");
                    //流水状态
                    payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                    payWaterEntity.setUpdateUser("xxljob");
                    payWaterEntity.setUpdateTime(new Date());
                    return;
                }else {
                    //其他非终态，等待下次查询
                    return;
                }
            }else if ("2022".equals(json.getString("bizCode"))){ //订单不存在，直接关单
                //订单失败
                //订单状态
                orderEntity.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
                orderEntity.setRemark(json.getString("bizCodeMsg"));
                orderEntity.setUpdateTime(new Date());
                orderEntity.setUpdateUser("xxljob");
                //流水状态
                payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
                payWaterEntity.setUpStatusCode("9999");
                payWaterEntity.setUpResultMsg(json.getString("bizCodeMsg"));
                payWaterEntity.setUpdateUser("xxljob");
                payWaterEntity.setUpdateTime(new Date());
            }
        } else { //北京银行
            json = partiallyRepayService.repayQuery(orderEntity.getOemCode(), orderEntity.getOrderNo(), null, DateUtil.formatTimesTampDate(orderEntity.getAddTime()));
            if (json == null) {
                //没有结果，重新查询处理
                return;
            }
            String resCode = json.getString("p1_resCode");
            if (!StringUtils.equals(RepayMessageEnum.RPY0000.getValue(), resCode)) {
                //查询不成功，重新查询处理
                return;
            }
            JSONArray array = json.getJSONArray("p6_detailJson");
            if (CollectionUtil.isEmpty(array)) {
                //打款明细不存在，重新查询处理
                return;
            }
            JSONObject result = array.getJSONObject(0);
            if (RepayQueryResultEnum.REPAY_SUCCESS.getValue().equals(result.getString("status"))) {
                //订单状态
                orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
                orderEntity.setUpdateTime(new Date());
                orderEntity.setUpdateUser("xxljob");
                //流水状态
                payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                payWaterEntity.setUpdateUser("xxljob");
                payWaterEntity.setUpdateTime(new Date());
                return;
            } else if (RepayQueryResultEnum.REPAY_FAILURE.getValue().equals(result.getString("status"))) {
                //订单状态
                orderEntity.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
                orderEntity.setRemark(result.getString("resMsg"));
                orderEntity.setUpdateTime(new Date());
                orderEntity.setUpdateUser("xxljob");
                //流水状态
                payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
                payWaterEntity.setUpStatusCode("9999");
                payWaterEntity.setUpResultMsg(result.getString("resMsg"));
                payWaterEntity.setUpdateUser("xxljob");
                payWaterEntity.setUpdateTime(new Date());
                return;
            }
        }
        //其他结果，重新查询
        return;
    }

    @Override
    public PageInfo<CompanyCancelOrderVO> listPageCancelOrder(OrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(listCancelOrder(query));
    }

    @Override
    public List<CompanyCancelOrderVO> listCancelOrder(OrderQuery query) {
        query.setOrderType(OrderTypeEnum.CANCELLATION.getValue());
        return mapper.listCancelOrder(query);
    }

    @Override
    public List<OrderEntity> queryComCancelOrder(Long userId, String oemCode, Long companyId, Integer orderStatus) {
        return this.orderMapper.queryComCancelOrder(userId, oemCode, companyId, orderStatus);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String certUseOrReturnOrder(Long memberId, String oemCode, CompResApplyRecordDTO entity, String sourceType) throws BusinessException {
        // 申请类型 1-领用 2-归还
        if (entity.getApplyType() == 2) {
            if (StringUtils.isBlank(entity.getCourierNumber())) {
                throw new BusinessException("快递单号不能为空");
            }

            // 校验快递单号
            String regex = "^[A-Za-z0-9]{4,40}$";
            boolean isMatch = entity.getCourierNumber().matches(regex);
            if (!isMatch) {
                throw new BusinessException("快递单号有误");
            }

            if (StringUtils.isBlank(entity.getCourierCompanyName())) {
                throw new BusinessException("快递公司名称不能为空");
            }
        }

        // 查询证件是否在申请中
        int orderCount = companyResoucesApplyRecordMapper.checkCertOrder(memberId, oemCode, entity.getCompanyId(), entity.getApplyType(), entity.getApplyResouces());
        if (orderCount > 0) {
            throw new BusinessException("申请的资源正在使用中");
        }

        // 解析资源类型
        String[] applyResouces = entity.getApplyResouces().split(",");

        // 申请类型 1-领用 2-归还
        if (entity.getApplyType() == 1) {
            for (String resource : applyResouces) {
                // 查询证件是否在园区（0-不在园区 1-在园区）
                int isInPark = companyResoucesApplyRecordMapper.checkCertIsInPark(memberId, oemCode, entity.getCompanyId(), resource);
                if (isInPark == 0) {
                    throw new BusinessException("申请的资源不在园区，无法领用");
                }
            }
        } else if (entity.getApplyType() == 2) {
            for (String resource : applyResouces) {
                // 查询证件是否在园区（0-不在园区 1-在园区）
                int isInPark = companyResoucesApplyRecordMapper.checkCertIsInPark(memberId, oemCode, entity.getCompanyId(), resource);
                if (isInPark == 1) {
                    throw new BusinessException("申请的资源已在园区，无需归还");
                }
            }
        }

        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(entity.getCompanyId());
        company.setOemCode(oemCode);
        company.setMemberId(memberId);
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }

        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.PROHIBIT.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.CANCELLING.getValue())) {
            throw new BusinessException("公司状态异常，无法申请证件领用");
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park.setStatus(1);//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("园区不存在或未上架");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if (null == memberLevel) {
            throw new BusinessException("未查询到会员等级");
        }

        // 查询oem机构信息
        OemEntity oem = oemService.getOem(oemCode);
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }

        // 保存会员订单关系
        MemberOrderRelaEntity more = invoiceOrderService.getUserTree(memberId, oemCode, 5);//获取一二级推广人和分润信息
        if (more != null) {
            more.setMemberId(memberId);
            more.setMemberLevel(memberLevel.getLevelNo());//会员等级
            more.setOemCode(oemCode);
            more.setOemName(oem.getOemName());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            memberOrderRelaService.insertSelective(more);
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(memberId);

        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setUserId(memberId);
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.COMPRESAPPLY.getValue());
        // 申请类型 1-领用 2-归还
        if (entity.getApplyType() == 1) {
            mainOrder.setOrderStatus(CompResApplyRecordOrderStatusEnum.TO_BE_PAY.getValue());
        } else {
            mainOrder.setOrderStatus(CompResApplyRecordOrderStatusEnum.TO_BE_RECEIVED.getValue());
        }
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setOemCode(oemCode);
        mainOrder.setParkId(company.getParkId());
        // 申请类型 1-领用 2-归还
        if (entity.getApplyType() == 1) {
            mainOrder.setOrderAmount(park.getPostageFees());
            mainOrder.setPayAmount(park.getPostageFees());
            mainOrder.setWalletType(1); //钱包类型 1-消费钱包 2-佣金钱包
        }
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(member.getMemberAccount());
        mainOrder.setSourceType(parseInt(sourceType));

        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        orderMapper.insertSelective(mainOrder);

        // 保存企业资源申请信息
        CompanyResoucesApplyRecordEntity resApplyRecord = new CompanyResoucesApplyRecordEntity();
        resApplyRecord.setOrderNo(orderNo);
        resApplyRecord.setCompanyId(entity.getCompanyId());
        resApplyRecord.setOemCode(oemCode);
        resApplyRecord.setApplyType(entity.getApplyType());
        resApplyRecord.setApplyResouces(entity.getApplyResouces().substring(0, entity.getApplyResouces().length() - 1));
        // 申请类型 1-领用 2-归还
        if (entity.getApplyType() == 1) {
            resApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.TO_BE_PAY.getValue());
        } else {
            resApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.TO_BE_RECEIVED.getValue());
        }
        // 申请类型 1-领用 2-归还
        if (entity.getApplyType() == 1) {
            resApplyRecord.setPostageFees(park.getPostageFees());
        }
        resApplyRecord.setRecipient(entity.getRecipient());
        resApplyRecord.setRecipientPhone(entity.getRecipientPhone());
        resApplyRecord.setProvinceCode(entity.getProvinceCode());
        resApplyRecord.setProvinceName(entity.getProvinceName());
        resApplyRecord.setCityCode(entity.getCityCode());
        resApplyRecord.setCityName(entity.getCityName());
        resApplyRecord.setDistrictCode(entity.getDistrictCode());
        resApplyRecord.setDistrictName(entity.getDistrictName());
        resApplyRecord.setRecipientAddress(entity.getRecipientAddress());
        // 申请类型 1-领用 2-归还
        if (entity.getApplyType() == 2) {
            resApplyRecord.setCourierCompanyName(entity.getCourierCompanyName());
            resApplyRecord.setCourierNumber(entity.getCourierNumber());
        }
        resApplyRecord.setAddTime(new Date());
        resApplyRecord.setAddUser(member.getMemberAccount());
        companyResoucesApplyRecordService.insertSelective(resApplyRecord);
        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void cancelCertOrder(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主表订单信息
        OrderEntity order = orderMapper.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }

        if (!Objects.equals(order.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询oem机构
        OemEntity oem = oemService.getOem(order.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }

        //获取企业资源申请信息
        CompanyResoucesApplyRecordEntity comResApplyRecord = companyResoucesApplyRecordMapper.queryByOrderNo(orderNo);
        if (null == comResApplyRecord) {
            throw new BusinessException("未查询到企业资源申请订单");
        }

        // 申请类型 1-领用 2-归还
        if (comResApplyRecord.getApplyType() == 1) {
            // 企业资源申请状态：0-待付款 1-待发货 2-出库中 3-待签收 4-已签收 5-已取消
            // 如果订单状态为2-出库中 3-待签收 4-已签收则不能取消
            // 如果订单状态为5-已取消则不能取消
            if (IntervalUtil.isInTheInterval(order.getOrderStatus().toString(), "[" + CompResApplyRecordOrderStatusEnum.OUT_OF_STOCK.getValue().toString() + "," + CompResApplyRecordOrderStatusEnum.SIGNED.getValue().toString() + "]")) {
                throw new BusinessException("已出库中的订单无法取消");
            } else if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.CANCELED.getValue())) {
                throw new BusinessException("已取消的订单无法再次取消");
            } else {
                // 判断用户是否支付，如果已支付将支付金额退回可用余额，添加资金变动记录
                if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.TO_BE_SHIPPED.getValue())) {

                    // 查询会员资金信息
                    UserCapitalAccountEntity userCapital = this.userCapitalAccountService.queryByUserIdAndType(memberId, 1, oemCode,1);
                    if (null == userCapital) {
                        throw new BusinessException("未查询到会员资金账户");
                    }
                    Long beforeTotalAmount = userCapital.getTotalAmount();

                    // 退回资金账号冻结金额
                    userCapital.setAvailableAmount(userCapital.getAvailableAmount() + order.getPayAmount());//增加可用金额
                    userCapital.setBlockAmount(userCapital.getBlockAmount() - order.getPayAmount());//修改冻结金额
                    userCapital.setUpdateTime(new Date());
                    userCapital.setUpdateUser(member.getMemberAccount());
                    userCapitalAccountService.editByIdSelective(userCapital);

                    // 添加会员退回记录
                    UserCapitalChangeRecordEntity userCapitalIncomeChange = new UserCapitalChangeRecordEntity();
                    userCapitalIncomeChange.setCapitalAccountId(userCapital.getId());
                    userCapitalIncomeChange.setUserId(memberId);
                    userCapitalIncomeChange.setUserType(1);// 用户类型 1-会员 2-系统用户
                    userCapitalIncomeChange.setWalletType(1);// 钱包类型 1-消费钱包 2-佣金钱包
                    userCapitalIncomeChange.setOemCode(order.getOemCode());
                    userCapitalIncomeChange.setChangesAmount(order.getPayAmount());
                    userCapitalIncomeChange.setChangesBeforeAmount(beforeTotalAmount);//变动前余额
                    userCapitalIncomeChange.setChangesAfterAmount(beforeTotalAmount + order.getPayAmount());//变动后余额
                    userCapitalIncomeChange.setChangesType(CapitalChangeTypeEnum.THAW.getValue());
                    userCapitalIncomeChange.setDetailDesc("企业资源申请解冻金额");
                    userCapitalIncomeChange.setOrderType(9);
                    userCapitalIncomeChange.setOrderNo(order.getOrderNo());
                    userCapitalIncomeChange.setAddTime(new Date());
                    userCapitalIncomeChange.setAddUser(member.getMemberAccount());
                    userCapitalChangeRecordService.insertSelective(userCapitalIncomeChange);

                    // 生成支付流水
                    PayWaterEntity water = new PayWaterEntity();
                    water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
                    water.setOrderNo(orderNo);
                    water.setMemberId(memberId);
                    water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
                    water.setOemCode(order.getOemCode());
                    water.setOemName(oem.getOemName());
                    water.setOrderAmount(order.getOrderAmount());
                    water.setPayAmount(order.getPayAmount());
                    water.setOrderType(9);
                    water.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
                    water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
                    water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                    water.setPayAccount(userCapital.getCapitalAccount());
                    water.setWalletType(1);// 钱包类型 1-消费钱包 2-佣金钱包
                    water.setAddTime(new Date());
                    water.setAddUser(member.getMemberAccount());
                    water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行
                    payWaterService.insertSelective(water);
                }
            }
        } else {
            // 企业资源申请状态：0-待付款 1-待发货 2-出库中 3-待签收 4-已签收 5-已取消
            // 如果订单状态为5-已取消则不能取消
            if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.CANCELED.getValue())) {
                throw new BusinessException("已取消的订单无法再次取消");
            }
        }

        // 修改主表订单状态
        order.setOrderStatus(CompResApplyRecordOrderStatusEnum.CANCELED.getValue());
        order.setUpdateUser(member.getMemberAccount());
        order.setUpdateTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        // 修改企业资源申请表状态
        comResApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.CANCELED.getValue());
        comResApplyRecord.setUpdateUser(member.getMemberAccount());
        comResApplyRecord.setUpdateTime(new Date());
        companyResoucesApplyRecordMapper.updateByPrimaryKeySelective(comResApplyRecord);
    }


    @Override
    public void cancelCertOrderAdmin(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主表订单信息
        OrderEntity order = orderMapper.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }

        if (!Objects.equals(order.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询oem机构
        OemEntity oem = oemService.getOem(order.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }

        //获取企业资源申请信息
        CompanyResoucesApplyRecordEntity comResApplyRecord = companyResoucesApplyRecordMapper.queryByOrderNo(orderNo);
        if (null == comResApplyRecord) {
            throw new BusinessException("未查询到企业资源申请订单");
        }

        // 申请类型 1-领用 2-归还
        if (comResApplyRecord.getApplyType() == 1) {
            // 企业资源申请状态：0-待付款 1-待发货 2-出库中 3-待签收 4-已签收 5-已取消
            // 如果订单状态为2-出库中 3-待签收 4-已签收则不能取消
            // 如果订单状态为5-已取消则不能取消
            if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.CANCELED.getValue())) {
                throw new BusinessException("已取消的订单无法再次取消");
            } else if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.TO_BE_PAY.getValue())) {
                //待付款状态不做资金变更
            } else {
                // 查询会员资金信息
                UserCapitalAccountEntity userCapital = this.userCapitalAccountService.queryByUserIdAndType(memberId, 1, oemCode,1);
                if (null == userCapital) {
                    throw new BusinessException("未查询到会员资金账户");
                }
                Long beforeTotalAmount = userCapital.getTotalAmount();

                // 退回资金账号冻结金额
                userCapital.setAvailableAmount(userCapital.getAvailableAmount() + order.getPayAmount());//增加可用金额
                userCapital.setBlockAmount(userCapital.getBlockAmount() - order.getPayAmount());//修改冻结金额
                userCapital.setUpdateTime(new Date());
                userCapital.setUpdateUser(member.getMemberAccount());
                userCapitalAccountService.editByIdSelective(userCapital);

                // 添加会员退回记录
                UserCapitalChangeRecordEntity userCapitalIncomeChange = new UserCapitalChangeRecordEntity();
                userCapitalIncomeChange.setCapitalAccountId(userCapital.getId());
                userCapitalIncomeChange.setUserId(memberId);
                userCapitalIncomeChange.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
                userCapitalIncomeChange.setOemCode(order.getOemCode());
                userCapitalIncomeChange.setChangesAmount(order.getPayAmount());
                userCapitalIncomeChange.setChangesBeforeAmount(beforeTotalAmount);//变动前余额
                userCapitalIncomeChange.setChangesAfterAmount(beforeTotalAmount + order.getPayAmount());//变动后余额
                userCapitalIncomeChange.setChangesType(CapitalChangeTypeEnum.THAW.getValue());
                userCapitalIncomeChange.setDetailDesc("企业资源申请解冻金额");
                userCapitalIncomeChange.setOrderType(9);
                userCapitalIncomeChange.setOrderNo(order.getOrderNo());
                userCapitalIncomeChange.setAddTime(new Date());
                userCapitalIncomeChange.setAddUser(member.getMemberAccount());
                userCapitalChangeRecordService.insertSelective(userCapitalIncomeChange);

                // 生成支付流水
                PayWaterEntity water = new PayWaterEntity();
                water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
                water.setOrderNo(orderNo);
                water.setMemberId(memberId);
                water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
                water.setOemCode(order.getOemCode());
                water.setOemName(oem.getOemName());
                water.setOrderAmount(order.getOrderAmount());
                water.setPayAmount(order.getPayAmount());
                water.setOrderType(9);
                water.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
                water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
                water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                water.setPayAccount(userCapital.getCapitalAccount());
                water.setAddTime(new Date());
                water.setAddUser(member.getMemberAccount());
                water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行
                payWaterService.insertSelective(water);
            }
        } else {
            // 企业资源申请状态：0-待付款 1-待发货 2-出库中 3-待签收 4-已签收 5-已取消
            // 如果订单状态为5-已取消则不能取消
            if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.CANCELED.getValue())) {
                throw new BusinessException("已取消的订单无法再次取消");
            }
        }

        // 修改主表订单状态
        order.setOrderStatus(CompResApplyRecordOrderStatusEnum.CANCELED.getValue());
        order.setUpdateUser(member.getMemberAccount());
        order.setUpdateTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        // 修改企业资源申请表状态
        comResApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.CANCELED.getValue());
        comResApplyRecord.setUpdateUser(member.getMemberAccount());
        comResApplyRecord.setUpdateTime(new Date());
        companyResoucesApplyRecordMapper.updateByPrimaryKeySelective(comResApplyRecord);
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void certUseConfirm(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主表订单信息
        OrderEntity order = orderMapper.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }

        if (!Objects.equals(order.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        //获取企业资源申请信息
        CompanyResoucesApplyRecordEntity comResApplyRecord = companyResoucesApplyRecordMapper.queryByOrderNo(orderNo);
        if (null == comResApplyRecord) {
            throw new BusinessException("未查询到企业资源申请订单");
        }
        // 解析资源类型
        String[] applyResouces = comResApplyRecord.getApplyResouces().split(",");

        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(comResApplyRecord.getCompanyId());
        company.setOemCode(oemCode);
        company.setMemberId(memberId);
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }

        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.PROHIBIT.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.CANCELLING.getValue())) {
            throw new BusinessException("公司状态异常，无法证件领用确认收货");
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park.setStatus(1);//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("园区不存在或未上架");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员资金账号信息
        UserCapitalAccountEntity userCapital = new UserCapitalAccountEntity();
        userCapital.setUserType(1);//用户类型 1-会员 2 -系统用户
        userCapital.setUserId(memberId);
        userCapital.setStatus(1);//状态 0-禁用 1-可用
        userCapital.setOemCode(oemCode);
        userCapital.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
        userCapital = userCapitalAccountService.selectOne(userCapital);
        if (null == userCapital) {
            throw new BusinessException("会员资金账户不存在或被禁用");
        }
        Long beforeTotalAmount = userCapital.getTotalAmount();

        // 查询oem机构账号
        UserEntity oemUser = new UserEntity();
        oemUser.setOemCode(oemCode);
        oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
        oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
        oemUser.setStatus(1);//状态 0-禁用 1-可用
        oemUser = userService.selectOne(oemUser);
        if (null == oemUser) {
            throw new BusinessException("oem机构账号不存在或被禁用");
        }

        // 查询oem机构资金账号信息
        UserCapitalAccountEntity oemCapital = new UserCapitalAccountEntity();
        oemCapital.setUserType(2);//用户类型 1-会员 2 -系统用户
        oemCapital.setUserId(oemUser.getId());
        oemCapital.setStatus(1);//状态 0-禁用 1-可用
        oemCapital.setOemCode(oemCode);
        oemCapital.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
        oemCapital = userCapitalAccountService.selectOne(oemCapital);
        if (null == oemCapital) {
            throw new BusinessException("oem机构资金账户不存在或被禁用");
        }
        Long oemBeforeTotalAmount = oemCapital.getTotalAmount();

        // 企业资源申请类型  1-领用  2-归还
        // 企业资源申请状态：0-待付款 1-待发货 2-出库中 3-待签收 4-已签收 5-已取消
        // 如果订单状态为0-待付款 1-待发货 2-出库中则不能确认收货
        if (!Objects.equals(comResApplyRecord.getApplyType(), 1)) {
            throw new BusinessException("该资源申请类型无法确认收货");
        } else if (IntervalUtil.isInTheInterval(order.getOrderStatus().toString(), "[" + CompResApplyRecordOrderStatusEnum.TO_BE_PAY.getValue().toString() + "," + CompResApplyRecordOrderStatusEnum.OUT_OF_STOCK.getValue().toString() + "]")) {
            throw new BusinessException("未发货的订单无法确认收货");
        } else if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.SIGNED.getValue())) {
            throw new BusinessException("已签收的订单无法确认收货");
        } else if (order.getOrderStatus().equals(CompResApplyRecordOrderStatusEnum.CANCELED.getValue())) {
            throw new BusinessException("已取消的订单无法确认收货");
        } else {

            // 修改主表订单状态
            order.setOrderStatus(CompResApplyRecordOrderStatusEnum.SIGNED.getValue());
            order.setUpdateUser(member.getMemberAccount());
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            // 修改企业资源申请表状态
            comResApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.SIGNED.getValue());
            comResApplyRecord.setUpdateUser(member.getMemberAccount());
            comResApplyRecord.setUpdateTime(new Date());
            companyResoucesApplyRecordMapper.updateByPrimaryKeySelective(comResApplyRecord);

            // 修改企业资源所在地
            for (String resource : applyResouces) {
                CompanyResourcesAddressEntity comResAddress = new CompanyResourcesAddressEntity();
                comResAddress.setCompanyId(comResApplyRecord.getCompanyId());
                comResAddress.setOemCode(oemCode);
                comResAddress.setResourcesType(parseInt(resource));
                comResAddress = companyResourcesAddressService.selectOne(comResAddress);
                comResAddress.setAddress("已移交给用户");
                comResAddress.setIsInPark(0);
                companyResourcesAddressService.editByIdSelective(comResAddress);
            }

            // 添加企业资源所在地历史记录
            for (String resource : applyResouces) {
                CompanyResourcesAddressHistoryEntity companyResourcesAddressHistoryEntity = new CompanyResourcesAddressHistoryEntity();
                companyResourcesAddressHistoryEntity.setCompanyId(comResApplyRecord.getCompanyId());
                companyResourcesAddressHistoryEntity.setResourcesType(parseInt(resource));
                companyResourcesAddressHistoryEntity.setUpdateBefore(park.getParkName());
                companyResourcesAddressHistoryEntity.setUpdateAfter("已移交给用户");
                companyResourcesAddressHistoryEntity.setOemCode(oemCode);
                companyResourcesAddressHistoryEntity.setAddTime(new Date());
                companyResourcesAddressHistoryEntity.setAddUser(member.getMemberAccount());
                companyResourcesAddressHistoryService.insertSelective(companyResourcesAddressHistoryEntity);
            }

            // 修改会员资金账号总金额,减少冻结金额
            userCapital.setTotalAmount(userCapital.getTotalAmount() - order.getPayAmount());//减少总金额
            userCapital.setBlockAmount(userCapital.getBlockAmount() - order.getPayAmount());//减少冻结金额
            userCapital.setUpdateTime(new Date());
            userCapital.setUpdateUser(member.getMemberAccount());
            userCapitalAccountService.editByIdSelective(userCapital);

            // 添加会员资金账号支出记录
            UserCapitalChangeRecordEntity userCapitalPayChange = new UserCapitalChangeRecordEntity();
            userCapitalPayChange.setCapitalAccountId(userCapital.getId());
            userCapitalPayChange.setUserId(memberId);
            userCapitalPayChange.setUserType(1);// 用户类型 1-会员 2-系统用户
            userCapitalPayChange.setOemCode(oemCode);
            userCapitalPayChange.setChangesAmount(order.getPayAmount());
            userCapitalPayChange.setChangesBeforeAmount(beforeTotalAmount);//变动前余额
            userCapitalPayChange.setChangesAfterAmount(userCapital.getTotalAmount());//变动后余额
            userCapitalPayChange.setChangesType(CapitalChangeTypeEnum.EXPENDITURE.getValue());
            userCapitalPayChange.setDetailDesc("证件领用订单余额支付");
            userCapitalPayChange.setOrderType(9);
            userCapitalPayChange.setOrderNo(order.getOrderNo());
            userCapitalPayChange.setAddTime(new Date());
            userCapitalPayChange.setAddUser(member.getMemberAccount());
            userCapitalPayChange.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
            userCapitalChangeRecordService.insertSelective(userCapitalPayChange);

            // 修改机构资金账号，增加总金额和可用金额
            oemCapital.setTotalAmount(oemCapital.getTotalAmount() + order.getPayAmount());//增加总金额
            oemCapital.setAvailableAmount(oemCapital.getAvailableAmount() + order.getPayAmount());//增加可用金额
            oemCapital.setUpdateTime(new Date());
            oemCapital.setUpdateUser(member.getMemberAccount());
            userCapitalAccountService.editByIdSelective(oemCapital);

            // 添加机构资金账号收入记录
            UserCapitalChangeRecordEntity oemCapitalPayChange = new UserCapitalChangeRecordEntity();
            oemCapitalPayChange.setCapitalAccountId(oemCapital.getId());
            oemCapitalPayChange.setUserId(oemCapital.getUserId());
            oemCapitalPayChange.setUserType(2);// 用户类型 1-会员 2-系统用户
            oemCapitalPayChange.setOemCode(oemCode);
            oemCapitalPayChange.setChangesAmount(order.getPayAmount());
            oemCapitalPayChange.setChangesBeforeAmount(oemBeforeTotalAmount);//变动前余额
            oemCapitalPayChange.setChangesAfterAmount(oemCapital.getTotalAmount());//变动后余额
            oemCapitalPayChange.setChangesType(CapitalChangeTypeEnum.INCOME.getValue());
            oemCapitalPayChange.setDetailDesc("证件领用订单余额支付");
            oemCapitalPayChange.setOrderType(9);
            oemCapitalPayChange.setOrderNo(order.getOrderNo());
            oemCapitalPayChange.setAddTime(new Date());
            oemCapitalPayChange.setAddUser(member.getMemberAccount());
            oemCapitalPayChange.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
            userCapitalChangeRecordService.insertSelective(oemCapitalPayChange);

            // V2.3需求，添加会员消费记录
            MemberConsumptionRecordEntity consumptionRecord = new MemberConsumptionRecordEntity();
            consumptionRecord.setOrderNo(order.getOrderNo());
            consumptionRecord.setOrderType(OrderTypeEnum.COMPRESAPPLY.getValue());
            consumptionRecord.setMemberId(member.getId());
            consumptionRecord.setConsumptionAmount(order.getPayAmount()); // 消费金额(邮寄费)
            consumptionRecord.setOemCode(oemCode);
            consumptionRecord.setIsOpenInvoice(0);// 是否已开票 0-未开 1-已开
            consumptionRecord.setAddTime(new Date());
            consumptionRecord.setAddUser(member.getMemberAccount());
            consumptionRecord.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
            memberConsumptionRecordService.insertSelective(consumptionRecord);
        }
    }

    @Override
    public Map<String, Object> certOrderProgress(long memberId, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果

        //查询订单主表信息
        OrderEntity mainOrder = orderMapper.queryByOrderNo(orderNo);
        if (!Objects.equals(mainOrder.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        //获取企业资源申请信息
        CompanyResoucesApplyRecordEntity comResApplyRecord = companyResoucesApplyRecordMapper.queryByOrderNo(orderNo);
        resultMap.put("orderStatus", comResApplyRecord.getStatus());
        if (Objects.equals(comResApplyRecord.getStatus(), CompResApplyRecordOrderStatusEnum.TO_BE_RECEIVED.getValue()) || Objects.equals(comResApplyRecord.getStatus(), CompResApplyRecordOrderStatusEnum.SIGNED.getValue())) {
            if (StringUtils.isNotBlank(comResApplyRecord.getCourierCompanyName())) {
                //获取快递编码
                LogisCompanyEntity logis = new LogisCompanyEntity();
                logis.setCompanyName(comResApplyRecord.getCourierCompanyName());
                logis = logisCompanyService.selectOne(logis);
                if (null == logis) {
                    throw new BusinessException("快递公司名称有误");
                }

                //快递100查询快递信息
                String result = DeliveryUtils.synQueryData(sysDictionaryService.getByCode("kuaidi100_key").getDictValue(),
                        sysDictionaryService.getByCode("kuaidi100_companyno").getDictValue(), logis.getCompanyCode(),
                        comResApplyRecord.getCourierNumber(), null, null, null, 0);

                //解析快递100返回结果
                JSONObject resultObj = JSONObject.parseObject(result);
                if ("ok".equals(resultObj.getString("message")) && "200".equals(resultObj.getString("status"))) {
                    JSONArray array = resultObj.getJSONArray("data");
                    resultMap.put("logistics", array);
                } else {
                    resultMap.put("logistics", "快递查询暂无结果");
                }
            }
        }
        return resultMap;
    }

    @Override
    public OrderEntity queryByMemberId(Long memberId, String oemCode, Integer orderType, Integer orderStatus) {
        return mapper.queryByMemberId(memberId, oemCode, orderType, orderStatus);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void xxljobCancelCertOrder(String orderNo) {

        // 修改主表订单状态
        OrderEntity order = orderMapper.queryByOrderNo(orderNo);
        order.setOrderStatus(CompResApplyRecordOrderStatusEnum.CANCELED.getValue());
        order.setUpdateUser("xxljob");
        order.setUpdateTime(new Date());
        order.setRemark("10分钟未支付自动取消");
        orderMapper.updateByPrimaryKeySelective(order);

        // 修改企业资源申请表状态
        CompanyResoucesApplyRecordEntity comResApplyRecord = companyResoucesApplyRecordMapper.queryByOrderNo(orderNo);
        comResApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.CANCELED.getValue());
        comResApplyRecord.setUpdateUser("xxljob");
        comResApplyRecord.setUpdateTime(new Date());
        comResApplyRecord.setRemark("10分钟未支付自动取消");
        companyResoucesApplyRecordMapper.updateByPrimaryKeySelective(comResApplyRecord);
    }

    @Override
    public List<OpenOrderExportVO> getopenOrderExportList(OrderQuery query) {
        return mapper.getopenOrderExportList(query);
    }


    @Override
    public ExtendRecordDetailVO queryExtendRecordDetail(MemberExtendQuery query) throws BusinessException {
        log.info("查询推广记录明细信息：{}", JSON.toJSONString(query));

        // --------------------------------------------处理查询条件，时间格式化处理 YYYY-MM-DD HH:mm:ss ------------------------------------------------------
        if(StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){// 开始时间不为空，结束时间为空
            query.setDay(query.getStartDate());
        }else if(StringUtils.isNotBlank(query.getEndDate()) && StringUtils.isBlank(query.getStartDate())){// 结束时间不为空，开始时间为空
            query.setDay(query.getEndDate());
        } else if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())) { // 开始时间结束时间均不为空
            query.setStartDate(query.getStartDate() + " 00:00:00");
            query.setEndDate(query.getEndDate() + " 23:59:59");
        }

        // 用户检查
        MemberAccountEntity member = this.memberAccountService.findById(query.getUserId());
        if (null == member) {
            throw new BusinessException("查询失败，登录" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if (null == memberLevel) {
            throw new BusinessException("查询失败，会员等级不存在！");
        }
        query.setLevelNo(memberLevel.getLevelNo());
        query.setExtendType(member.getExtendType());

        ExtendRecordDetailVO extendDetail = new ExtendRecordDetailVO();// 返回结果

        // 企业注册推广明细
        if (OrderTypeEnum.REGISTER.getValue().equals(query.getOrderType())) {
            log.info(">>>查询企业注册推广明细>>>");
            ComRegExtendDetailVO registerDetail = queryRegOrderExtendDetail(query);
            // 数据填充
            extendDetail.setComRegistDetail(registerDetail);
        } else if (OrderTypeEnum.INVOICE.getValue().equals(query.getOrderType())) { // 企业开票推广明细
            log.info(">>>查询企业开票推广明细>>>");
            ComInvExtendDetailVO invoiceDetail = queryInvoiceExtendDetail(query);
            // 数据填充
            extendDetail.setComInvoiceDetail(invoiceDetail);
        } else if (OrderTypeEnum.UPGRADE.getValue().equals(query.getOrderType())) { // 会员升级推广明细
            log.info(">>>查询会员升级推广明细>>>");
            MemberUpExtendDetailVO upgradeDetail = queryMemberUpExtendDetail(query);
            // 数据填充
            extendDetail.setMemberUpgradeDetail(upgradeDetail);
        } else if (OrderTypeEnum.CANCELLATION.getValue().equals(query.getOrderType())) { // 企业注销推广明细
            log.info(">>>查询企业注销推广明细>>>");
            ComCancelExtendDetailVO cancelDetail = queryComCancelExtendDetail(query);
            // 数据填充
            extendDetail.setComCancelDetail(cancelDetail);
        } else{
            throw new BusinessException("订单类型不支持，请检查");
        }

        return extendDetail;
    }


    @Override
    public ExtendRecordDetailVO queryExtendRecordDetailByChannelServiceId(MemberExtendQuery query) throws BusinessException {
        log.info("查询推广记录明细信息：{}", JSON.toJSONString(query));

        // --------------------------------------------处理查询条件，时间格式化处理 YYYY-MM-DD HH:mm:ss ------------------------------------------------------
        if(StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){// 开始时间不为空，结束时间为空
            query.setDay(query.getStartDate());
        }else if(StringUtils.isNotBlank(query.getEndDate()) && StringUtils.isBlank(query.getStartDate())){// 结束时间不为空，开始时间为空
            query.setDay(query.getEndDate());
        } else if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())) { // 开始时间结束时间均不为空
            query.setStartDate(query.getStartDate() + " 00:00:00");
            query.setEndDate(query.getEndDate() + " 23:59:59");
        }

        ExtendRecordDetailVO extendDetail = new ExtendRecordDetailVO();// 返回结果

        // 企业注册推广明细
        if (OrderTypeEnum.REGISTER.getValue().equals(query.getOrderType())) {
            log.info(">>>查询企业注册推广明细>>>");
            ComRegExtendDetailVO registerDetail = queryRegOrderExtendDetailByChannelServiceId(query);
            // 数据填充
            extendDetail.setComRegistDetail(registerDetail);
        } else if (OrderTypeEnum.INVOICE.getValue().equals(query.getOrderType())) { // 企业开票推广明细
            log.info(">>>查询企业开票推广明细>>>");
            ComInvExtendDetailVO invoiceDetail = queryInvoiceExtendDetailByChannelServiceId(query);
            // 数据填充
            extendDetail.setComInvoiceDetail(invoiceDetail);
        } else if (OrderTypeEnum.UPGRADE.getValue().equals(query.getOrderType())) { // 会员升级推广明细
            log.info(">>>查询会员升级推广明细>>>");
            MemberUpExtendDetailVO upgradeDetail = queryMemberUpExtendDetailByChannelServiceId(query);
            // 数据填充
            extendDetail.setMemberUpgradeDetail(upgradeDetail);
        } else if (OrderTypeEnum.CANCELLATION.getValue().equals(query.getOrderType())) { // 企业注销推广明细
            log.info(">>>查询企业注销推广明细>>>");
            ComCancelExtendDetailVO cancelDetail = queryComCancelExtendDetailByChannelServiceId(query);
            // 数据填充
            extendDetail.setComCancelDetail(cancelDetail);
        } else if (OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue().equals(query.getOrderType())) { // 企业注销推广明细
            log.info(">>>查询企业推广费续费推广明细>>>");
            ContComRegExtendDetailVO contComRegExtendDetailVO = queryContComRegExtendDetailByChannelServiceId(query);
            // 数据填充
            extendDetail.setContComRegExtendDetailVO(contComRegExtendDetailVO);
        } else{
            throw new BusinessException("订单类型不支持，请检查");
        }

        return extendDetail;
    }
    /**
     * @Description 查询企业注销推广明细
     * @Author  Kaven
     * @Date   2020/6/8 12:48 下午
     * @Param   MemberExtendQuery
     * @Return  ComCancelExtendDetailVO
     * @Exception
     */
    private ComCancelExtendDetailVO queryComCancelExtendDetail(MemberExtendQuery query) {
        // 查询企业注销统计信息
        ComCancelExtendDetailVO ccedv = this.companyCancelOrderService.queryComCancelStat(query);
        // 查询注销订单的分页信息
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        // 如果orderStatus为空，默认查待付款tab
        if(null == query.getOrderStatus()){
            query.setOrderStatus(0);
        }
        List<ExtendMemberVO> list = this.companyCancelOrderService.queryCancelOrderListByStatus(query);

        // 查询总注销服务费
        Long cancelFee = this.companyCancelOrderService.queryTotalCancelFee(query);
        ccedv.setTotalCancelFee(cancelFee);

        PageInfo pageInfo = new PageInfo<ExtendMemberVO>(list);
        ccedv.setOrderPageList(pageInfo);
        return ccedv;
    }
    /**
     * @Description 查询企业注销推广明细
     * @Param   MemberExtendQuery
     * @Return  ComCancelExtendDetailVO
     * @Exception
     */
    private ComCancelExtendDetailVO queryComCancelExtendDetailByChannelServiceId(MemberExtendQuery query) {
        // 查询企业注销统计信息
        ComCancelExtendDetailVO ccedv = this.companyCancelOrderService.queryComCancelStatByChannelServiceId(query);
        // 查询注销订单的分页信息
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        // 如果orderStatus为空，默认查待付款tab
        if(null == query.getOrderStatus()){
            query.setOrderStatus(0);
        }
        List<ExtendMemberVO> list = this.companyCancelOrderService.queryCancelOrderListByStatusByChannelServiceId(query);

        // 查询总注销服务费
        Long cancelFee = this.companyCancelOrderService.queryTotalCancelFeeByChannelServiceId(query);
        ccedv.setTotalCancelFee(cancelFee);

        PageInfo pageInfo = new PageInfo<>(list);
        ccedv.setOrderPageList(pageInfo);
        return ccedv;
    }
    /**
     * @Description 查询会员升级推广明细
     * @Author  Kaven
     * @Date   2020/6/8 12:48 下午
     * @Param   MemberExtendQuery
     * @Return  MemberUpExtendDetailVO
     * @Exception
     */
    private MemberUpExtendDetailVO queryMemberUpExtendDetail(MemberExtendQuery query) {
        MemberUpExtendDetailVO memberUpgradeDetail = new MemberUpExtendDetailVO();
        // 分页查询会员升级订单列表
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        query.setOrderStatus(MemberOrderStatusEnum.COMPLETED.getValue());// 会员升级只统计已完成状态的数据
        List<ExtendMemberVO> list = this.orderMapper.queryMemberUpgradeOrderList(query);
        // 查询总会费
        Long totalMemberFee = this.orderMapper.queryTotalMemberFee(query);
        memberUpgradeDetail.setTotalMemberFee(totalMemberFee);

        PageInfo pageInfo = new PageInfo<ExtendMemberVO>(list);
        memberUpgradeDetail.setOrderPageList(pageInfo);
        return memberUpgradeDetail;
    }
    /**
     * @Description 查询会员升级推广明细
     * @Author  HZ
     * @Date   2021/4/30 12:48 下午
     * @Param   MemberExtendQuery
     * @Return  MemberUpExtendDetailVO
     * @Exception
     */
    private MemberUpExtendDetailVO queryMemberUpExtendDetailByChannelServiceId(MemberExtendQuery query) {
        MemberUpExtendDetailVO memberUpgradeDetail = new MemberUpExtendDetailVO();
        // 分页查询会员升级订单列表
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        query.setOrderStatus(MemberOrderStatusEnum.COMPLETED.getValue());// 会员升级只统计已完成状态的数据
        List<ExtendMemberVO> list = this.orderMapper.queryMemberUpgradeOrderListByChannelServiceId(query);
        // 查询总会费
        Long totalMemberFee = this.orderMapper.queryTotalMemberFeeByChannelServiceId(query);
        memberUpgradeDetail.setTotalMemberFee(totalMemberFee);

        PageInfo pageInfo = new PageInfo<>(list);
        memberUpgradeDetail.setOrderPageList(pageInfo);
        return memberUpgradeDetail;
    }

    /**
     * @Description 查询托管费续费推广明细
     * @Author  HZ
     * @Date   2021/4/30 12:48 下午
     * @Param   MemberExtendQuery
     * @Return  MemberUpExtendDetailVO
     * @Exception
     */
    private ContComRegExtendDetailVO queryContComRegExtendDetailByChannelServiceId(MemberExtendQuery query) {
        ContComRegExtendDetailVO contComRegExtendDetailVO = new ContComRegExtendDetailVO();
        // 分页查询会员升级订单列表
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        query.setOrderStatus(2);// 托管费续费只统计已完成状态的数据
        List<ExtendMemberVO> list = this.orderMapper.queryContComRegExtendDetailByChannelServiceId(query);
        // 查询总会费
        Long totalMemberFee = this.orderMapper.queryTotalContRegFeeByChannelServiceId(query);
        contComRegExtendDetailVO.setTotalServiceFee(totalMemberFee);

        PageInfo pageInfo = new PageInfo<>(list);
        contComRegExtendDetailVO.setOrderPageList(pageInfo);
        return contComRegExtendDetailVO;
    }

    /**
     * @Description 查询企业开票推广明细
     * @Author  Kaven
     * @Date   2020/6/8 12:45 下午
     * @Param   MemberExtendQuery
     * @Return  ComInvExtendDetailVO
     * @Exception
     */
    private ComInvExtendDetailVO queryInvoiceExtendDetail(MemberExtendQuery query) {
        ComInvExtendDetailVO invoiceDetail = new ComInvExtendDetailVO();
        // 查询企业开票推广统计信息
        CompanyInvoiceProgressVO cipv = this.memberAccountService.queryCompanyInvoiceProgress(query);
        BeanUtils.copyProperties(cipv,invoiceDetail);
        // 查询推广开票订单的分页信息
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        // 如果orderStatus为空，默认查待提交tab
        if(null == query.getOrderStatus()){
            query.setOrderStatus(0);
        }
        List<ExtendMemberVO> list = this.invoiceOrderMapper.queryInvOrderListByStatus(query);

        // 查询总开票金额和总服务费
        ComInvExtendDetailVO feeDetail = this.invoiceOrderMapper.queryTotalInvServiceFee(query);
        invoiceDetail.setTotalInvoiceAmount(feeDetail.getTotalInvoiceAmount());
        invoiceDetail.setTotalServiceFee(feeDetail.getTotalServiceFee());

        PageInfo pageInfo = new PageInfo<ExtendMemberVO>(list);
        invoiceDetail.setOrderPageList(pageInfo);
        return invoiceDetail;
    }

    /**
     * @Description 查询企业开票推广明细
     * @Author  Kaven
     * @Date   2020/6/8 12:45 下午
     * @Param   MemberExtendQuery
     * @Return  ComInvExtendDetailVO
     * @Exception
     */
    private ComInvExtendDetailVO queryInvoiceExtendDetailByChannelServiceId(MemberExtendQuery query) {
        ComInvExtendDetailVO invoiceDetail = new ComInvExtendDetailVO();
        // 查询企业开票推广统计信息
        CompanyInvoiceProgressVO cipv = this.memberAccountService.queryCompanyInvoiceProgressByChannelServiceId(query);
        BeanUtils.copyProperties(cipv,invoiceDetail);
        // 查询推广开票订单的分页信息
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        // 如果orderStatus为空，默认查待提交tab
        if(null == query.getOrderStatus()){
            query.setOrderStatus(0);
        }
        List<ExtendMemberVO> list = this.invoiceOrderMapper.queryInvOrderListByStatusByChannelServiceId(query);

        // 查询总开票金额和总服务费
        ComInvExtendDetailVO feeDetail = this.invoiceOrderMapper.queryTotalInvServiceFeeByChannelServiceId(query);
        invoiceDetail.setTotalInvoiceAmount(feeDetail.getTotalInvoiceAmount());
        invoiceDetail.setTotalServiceFee(feeDetail.getTotalServiceFee());

        PageInfo pageInfo = new PageInfo<>(list);
        invoiceDetail.setOrderPageList(pageInfo);
        return invoiceDetail;
    }

    /**
     * @Description 查询企业注册推广明细
     * @Author  Kaven
     * @Date   2020/6/8 12:43 下午
     * @Param   MemberExtendQuery
     * @Return  ComRegExtendDetailVO
     * @Exception
     */
    private ComRegExtendDetailVO queryRegOrderExtendDetail(MemberExtendQuery query) {
        ComRegExtendDetailVO registerDetail = new ComRegExtendDetailVO();
        // 查询企业注册推广统计信息
        CompanyRegProgressVO crpv = this.memberAccountService.queryCompanyRegProgress(query);
        BeanUtils.copyProperties(crpv,registerDetail);

        // 查询推广用户/订单列表的分页信息
        List<ExtendMemberVO> list = null;
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        // 如果orderStatus为空，默认查未注册企业tab
        if(null == query.getOrderStatus()){
            query.setOrderStatus(0);
            list = this.memberAccountMapper.queryUnRegMemberList(query);
        } else if ( query.getOrderStatus() == 0){
            list = this.memberAccountMapper.queryUnRegMemberList(query);
        } else {
            // 否则查询各状态下的订单列表
            list = this.memberAccountMapper.queryRegOrderListByStatus(query);
            // 查询总托管费
            Long totalRegistFee = this.memberAccountMapper.queryTotalRegistFee(query);
            registerDetail.setTotalRegistFee(totalRegistFee);
        }

        PageInfo pageInfo = new PageInfo<ExtendMemberVO>(list);
        registerDetail.setOrderPageList(pageInfo);
        return  registerDetail;
    }

    /**
     * @Description 查询企业注册推广明细
     * @Author  Kaven
     * @Date   2020/6/8 12:43 下午
     * @Param   MemberExtendQuery
     * @Return  ComRegExtendDetailVO
     * @Exception
     */
    private ComRegExtendDetailVO queryRegOrderExtendDetailByChannelServiceId(MemberExtendQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());

        ComRegExtendDetailVO registerDetail = new ComRegExtendDetailVO();
        // 查询企业注册推广统计信息
        CompanyRegProgressVO crpv = this.memberAccountService.queryCompanyRegProgressByChannelServiceId(query);
        BeanUtils.copyProperties(crpv,registerDetail);

        // 查询推广用户/订单列表的分页信息
        List<ExtendMemberVO> list = null;
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        // 如果orderStatus为空，默认查未注册企业tab
        if (null == query.getOrderStatus() || query.getOrderStatus() == 0){
            list = this.memberAccountMapper.queryUnRegMemberListByChannelServiceId(query);
        } else {
            // 否则查询各状态下的订单列表
            list = this.memberAccountMapper.queryRegOrderListByStatusByChannelServiceId(query);
            // 查询总托管费
            Long totalRegistFee = this.memberAccountMapper.queryTotalRegistFeeByChannelSreviceId(query);
            registerDetail.setTotalRegistFee(totalRegistFee);
        }
        PageInfo pageInfo = new PageInfo<>(list);
        registerDetail.setOrderPageList(pageInfo);
        return  registerDetail;
    }

    @Override
    public void memberAutoUpdate(Long userId) {
        JSONObject json=new JSONObject();
        json.put("userId",userId);
        rabbitTemplate.convertAndSend("memberAutoUpdate", json);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String userWithdrawForCommission(UserCapitalAccountEntity userCapitalAccount, UserBankCardEntity userBankCard, UserWithdrawDTO dto, String memberAccount) throws BusinessException {
        log.info("开始处理佣金钱包提现请求：{},{},{}",JSON.toJSONString(userBankCard),JSON.toJSONString(dto),memberAccount);

        // 查询当前会员等级
        MemberAccountEntity member = this.memberAccountService.queryByAccount(memberAccount,dto.getOemCode());
        MemberLevelEntity memberLevel = this.memberLevelService.findById(member.getMemberLevel());

        // 查询机构信息
        OemEntity oem = oemService.getOem(dto.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到机构信息");
        }

        // 判断当前会员是否是城市服务商
        if(MemberLevelEnum.DIAMOND.getValue().equals(memberLevel.getLevelNo())){
            // 判断是否有选择指定开票类目的开票主体
            if(null == dto.getCompanyId()){
                throw new BusinessException("未选择指定开票类目的开票主体！");
            }

            // 判断开票主体的可开票额度是否大于等于提交订单金额
            CompanyInvoiceRecordEntity record = this.companyInvoiceRecordMapper.findByCompanyId(dto.getCompanyId());
            if(null == record){
                throw new BusinessException("提现失败，未查询到该开票主体的可开票额度！");
            }

            if(record.getRemainInvoiceAmount() < dto.getAmount()){
                throw new BusinessException("当前开票主体的可开票额度小于待提现金额！");
            }

            // 创建佣金开票开票订单
            log.info("城市服务商佣金钱包提现，创建开票订单");
            // 查询发票抬头和收货地址
            InvoiceInfoByOemEntity t = new InvoiceInfoByOemEntity();
            t.setOemCode(dto.getOemCode());
            InvoiceInfoByOemEntity iiboe = this.invoiceInfoByOemService.selectOne(t);
            if (null == iiboe) {
                throw new BusinessException("创建佣金提现订单失败：未找到oem机构开票配置信息");
            }
            dto.setCompanyName(iiboe.getCompanyName());
            dto.setCompanyAddress(iiboe.getCompanyAddress());
            dto.setEin(iiboe.getEin());
            dto.setPhone(iiboe.getPhone());
            dto.setBankName(iiboe.getBankName());
            dto.setBankNumber(iiboe.getBankNumber());
            dto.setRecipient(iiboe.getRecipient());
            dto.setRecipientPhone(iiboe.getRecipientPhone());
            dto.setRecipientAddress(iiboe.getRecipientAddress());
            dto.setProvinceCode(iiboe.getProvinceCode());
            dto.setProvinceName(iiboe.getProvinceName());
            dto.setCityCode(iiboe.getCityCode());
            dto.setCityName(iiboe.getCityName());
            dto.setDistrictCode(iiboe.getDistrictCode());
            dto.setDistrictName(iiboe.getDistrictName());
            dto.setInvoiceType(InvoiceTypeEnum.UPGRADE.getValue());
            dto.setEmail(iiboe.getEmail());

            String commissionInvoiceOrderNo = this.invoiceOrderService.createInvoiceOrderForCommission(dto,memberAccount);

            // 调用北京银行代付接口，创建佣金提现订单并生成支付流水
            log.info("调用北京银行代付接口，创建佣金提现订单并生成支付流水");
            this.adminWithdraw(userCapitalAccount, userBankCard, dto.getAmount(), member, 3, commissionInvoiceOrderNo,dto.getSourceType());

        } else { // 非城市服务商佣金提现
            // 根据数据字典配置的提现方案，决定流程走向
            DictionaryEntity dict = this.sysDictionaryService.getByCode("withdraw_scheme_switch");
            if (null != dict && "1".equals(dict.getDictValue())) {
                // 北京代发渠道
                log.info("非城市服务商佣金提现：调用北京银行代付接口：");
                this.adminWithdraw(userCapitalAccount, userBankCard, dto.getAmount(), member, WalletTypeEnum.COMMISSION_WALLET.getValue(), null,dto.getSourceType());
            } else if (null != dict && "2".equals(dict.getDictValue())) {// 纳呗渠道
                // 校验金额是否一致
                if (null == dto.getIsCheckAllProfitDetail()) {
                    throw new BusinessException("是否全选分润记录为空");
                }
                Long allProfitsDetail = 0L;
                if (dto.getIsCheckAllProfitDetail() == 0) {
                    if (StringUtil.isBlank(dto.getProfitDetailIds())) {
                        throw new BusinessException("未选择分润记录");
                    }
                    String[] split = dto.getProfitDetailIds().split(",");
                    // 根据分润记录id列表计算总分润金额
                    allProfitsDetail = profitsDetailService.countProfitDetailAmount(member.getId(), oem.getOemCode(), null, Lists.newArrayList(split));
                } else {
                    if (null == dto.getMaximalProfitDetailId()) {
                        throw new BusinessException("最大分润记录id为空");
                    }
                    // 根据最大分润记录id统计总分润金额
                    allProfitsDetail = profitsDetailService.countProfitDetailAmount(member.getId(), oem.getOemCode(), dto.getMaximalProfitDetailId(), null);
                }
                if (allProfitsDetail.compareTo(dto.getAmount()) != 0) {
                    throw new BusinessException("提现金额与所选的分润记录总分润金额不一致");
                }

                log.info("非城市服务商佣金提现：调用纳呗相关接口：");
                // 读取纳呗平台API相关配置 paramsType=10
                OemParamsEntity paramsEntity = this.oemParamsService.getParams(dto.getOemCode(), 10);
                if (null == paramsEntity) {
                    throw new BusinessException("未配置纳呗平台API相关信息！");
                }
                // 调用纳呗平台签约注册查询
                APISignQueryRespVo signQueryRespVo = nabeiApiService.signQuery(paramsEntity, member.getIdCardNo(), null);
                if(null != signQueryRespVo){
                    if(Objects.equals(signQueryRespVo.getP1_resCode(), "0000")) {
                        // 未签约纳呗，中断
                        if(!"1".equals(signQueryRespVo.getP3_status())){ //处理状态：0-未签约，1-已签约，2-已解约
                            throw new BusinessException("签约状态异常，请退出重试！");
                        }
                        // 已签约纳呗，创建佣金提现订单并生成支付流水
                        log.info("创建佣金提现订单并生成支付流水");
                        OrderEntity order = this.createCommissionWithdrawOrder(dto,userBankCard,member,memberLevel.getLevelNo(),PayChannelEnum.NABEIPAY.getValue());

                        // 批量修改分润记录，保存提现订单号
                        if (dto.getIsCheckAllProfitDetail() == 0) {
                            profitsDetailService.batchUpdateProfitsDetail(member.getId(), oem.getOemCode(), null, Lists.newArrayList(dto.getProfitDetailIds().split(",")), order.getOrderNo());
                        } else {
                            profitsDetailService.batchUpdateProfitsDetail(member.getId(), oem.getOemCode(), dto.getMaximalProfitDetailId(), null, order.getOrderNo());
                        }

                        // 纳呗单笔出款申请：根据挡板开关判断流程走向
                        DictionaryEntity dictionary = this.dictionaryService.getByCode("itax_withdraw_nabei_switch");
                        if(dto.getAmount() == 0L || (null != dictionary && "1".equals(dictionary.getDictValue()))){
                            // 纳呗单笔出款申请挡板处理
                            this.handleCommissionWithdrawResult(order,userCapitalAccount,0, "");// 0-成功 1-失败
                            log.info("纳呗单笔出款申请挡板处理成功，佣金提现请求处理结束。");
                            return null;
                        } else {
                            // 走正常纳呗出款申请
                            log.info("调用纳呗单笔出款申请接口");
                            Long reachAmount = order.getPayAmount() - order.getServiceFee();// 到账金额
                            SinglePayRespVo singlePayRespVo = nabeiApiService.singlePay(paramsEntity,order.getOrderNo(),userBankCard.getUserName(),userBankCard.getIdCard(),
                                    userBankCard.getBankNumber(),userBankCard.getPhone(),reachAmount.toString(),"佣金钱包提现出款申请");
                            if(null != singlePayRespVo){
                                if(Objects.equals(singlePayRespVo.getP1_resCode(), "0000")){ // 单笔出款申请接口响应成功
                                    // 代发处理状态 0-等待业务系统处理，1-业务系统出款中，2-出款成功，3-出款失败，9-待签约
                                    if("3".equals(singlePayRespVo.getP4_status())){
                                        log.info("提现失败，纳呗出款申请出款失败");
                                        // 提现失败订单处理，并返回错误信息（不能抛异常）
                                        this.handleCommissionWithdrawResult(order,userCapitalAccount,1, singlePayRespVo.getP5_message());// 0-成功 1-失败
                                        return "TX0001,提现失败,纳呗出款申请出款失败";
                                    } else {
                                        log.info("单笔出款申请返回状态：{}，提现申请成功，等待处理结果。",singlePayRespVo.getP4_status());
                                    }
                                } else if (Objects.equals(singlePayRespVo.getP1_resCode(), "0011")){ // 0011 订单号已存在
                                    log.info("单笔出款申请失败，resCode：{}, resMsg：{}", singlePayRespVo.getP1_resCode(), singlePayRespVo.getP2_resMsg());
                                    throw new BusinessException("订单号已存在，请勿重复提交");
                                } else {
                                    log.info("单笔出款申请失败，resCode：{}, resMsg：{}", singlePayRespVo.getP1_resCode(), singlePayRespVo.getP2_resMsg());
                                    // 渠道返回确定异常，提现申请不成功，处理订单
                                    this.handleCommissionWithdrawResult(order,userCapitalAccount,1, singlePayRespVo.getP2_resMsg());// 0-成功 1-失败
                                    return "TX0001,提现失败," + "渠道出款接口请求失败";
                                }
                            }else{ // 接口请求异常，默认为已单笔申请成功，不做处理
                                log.error("纳呗单笔出款申请接口异常");
                            }
                        }
                    } else {
                        log.info("调用纳呗平台签约结果查询失败：{}", JSON.toJSONString(signQueryRespVo));
                        throw new BusinessException("签约状态查询失败，请稍候再试");
                    }
                }else{
                    throw new BusinessException("纳呗签约注册查询接口异常");
                }

            } else if (null != dict && "4".equals(dict.getDictValue())) { //易税
                // 校验是否已签约易税
                MemberToSignYishuiEntity userYishuiEntity = new MemberToSignYishuiEntity();
                userYishuiEntity.setUserId(member.getId());
                MemberToSignYishuiEntity yishuiEntity = memberToSignYishuiService.selectOne(userYishuiEntity);
                if (null == yishuiEntity) {
                    throw new BusinessException("未签约易税，请签约后再试");
                }
                log.info("易税通道提现处理start");
                // 易税银行卡签约校验
                memberToSignYishuiService.verifyAndSetBankYiShuiId(userBankCard, yishuiEntity.getProfessionalId());
                // 已签约易税，创建佣金提现订单并生成支付流水
                log.info("创建佣金提现订单并生成支付流水");
                OrderEntity order = this.createCommissionWithdrawOrder(dto,userBankCard,member,memberLevel.getLevelNo(),PayChannelEnum.YISHUIPAY.getValue());
                // 是否挡板提现
                String ySBaffle = dictionaryService.getValueByCode("itax_withdraw_yishui_switch");
                if (StringUtil.isNotBlank(ySBaffle) && "1".equals(ySBaffle)) {
                    // 易税挡板付款，无需调用易税接口
                    this.handleCommissionWithdrawResult(order,userCapitalAccount,0, "易税挡板提现成功");// 0-成功 1-失败
                    log.info("易税提现挡板处理成功，佣金提现请求处理结束。");
                    return null;
                }
                // 调用易税付款下单接口
                Long reachAmount = order.getPayAmount() - order.getServiceFee();// 到账金额
                YiShuiBaseResp ySResponse = yishuiService.pay(dto.getOemCode(), order.getOrderNo(), MoneyUtil.moneydiv(new BigDecimal(reachAmount), new BigDecimal("100")), userBankCard, yishuiEntity.getProfessionalId());
                if (!"200".equals(ySResponse.getCode())) {
                    log.info("易税提现付款下单失败：" + ySResponse.getMsg());
                    throw new BusinessException("提现失败，通道调用失败");
                }
                log.info("易税提现下单成功，提现处理end");
            }else {
                throw new BusinessException("提现失败，提现渠道未配置或当前不支持该提现渠道");
            }
        }
        log.info("佣金钱包提现请求处理结束。");

        return null;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public OrderEntity createCommissionWithdrawOrder(UserWithdrawDTO dto, UserBankCardEntity cardEntity, MemberAccountEntity member,Integer levelNo, Integer payChannels) {
        log.info("创建佣金钱包提现订单开始：{},{}",JSONObject.toJSONString(dto),JSONObject.toJSONString(cardEntity));

        OemEntity oem = oemService.getOem(dto.getOemCode());
        if (oem == null) {
            throw new BusinessException("OEM机构不存在");
        }

        // 生成提现订单记录
        String orderNo = OrderNoFactory.getOrderCode(dto.getUserId()); // 生成订单号
        Date date = new Date();
        // 保存订单主表信息
        OrderEntity order = new OrderEntity();
        order.setOemCode(dto.getOemCode());
        order.setOrderNo(orderNo);
        order.setOrderAmount(dto.getAmount());
        order.setPayAmount(dto.getAmount());
        order.setProfitAmount(0L);
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setOrderType(OrderTypeEnum.WITHDRAW.getValue());
        order.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
        order.setSourceType(dto.getSourceType());
        order.setChannelUserId(member.getChannelUserId());
        // 根据会员等级取手续费率
        if(MemberLevelEnum.DIAMOND.getValue().intValue() == levelNo){
            order.setServiceFeeRate(oem.getDiamondCommissionServiceFeeRate());
        } else {
            order.setServiceFeeRate(oem.getCommissionServiceFeeRate());
        }
        // 计算服务费
        BigDecimal serviceFee = (order.getServiceFeeRate().divide(new BigDecimal(100))).multiply(new BigDecimal(order.getPayAmount()));
        order.setServiceFee(serviceFee.setScale(0, BigDecimal.ROUND_UP).longValue());// 舍弃小数位，向上取整

        // 到账金额为0不允许提现
        if (dto.getAmount() - order.getServiceFee() <= 0L) {
            throw new BusinessException("到账金额大于0时方可提现");
        }
        // 单笔最高提现校验
        if (null == oem.getMaxCommissionWithdrawSingleLimit()) {
            throw new BusinessException("未配置提现单笔最高限额");
        }
        if (dto.getAmount() - serviceFee.setScale(0, BigDecimal.ROUND_UP).longValue()  > oem.getMaxCommissionWithdrawSingleLimit()) {
            throw new BusinessException("提现金额已超过单笔限额，请重试！");
        }
        // 单月最高提现校验
        if (null == oem.getCommissionWithdrawMonthLimit()) {
            throw new BusinessException("未配置提现单月最高限额");
        }
        Long  currentMonthWithdrawalAmount = mapper.queryCurrentDayWithdrawalAmountByUserId(member.getId(),cardEntity.getUserType(),2,WalletTypeEnum.COMMISSION_WALLET.getValue());
        if(currentMonthWithdrawalAmount!=null && oem.getCommissionWithdrawMonthLimit() < (currentMonthWithdrawalAmount + dto.getAmount() - serviceFee.setScale(0, BigDecimal.ROUND_UP).longValue())){
            throw new BusinessException("本月提现金额已达上限，请下月再提！");
        }

        String addUser = member.getMemberAccount();
        order.setAddUser(addUser);
        // 补全订单参数
        completionParameter(dto.getUserId(), order);
        order.setAddTime(date);
        order.setAddUser(addUser);
        if (null != payChannels && PayChannelEnum.NABEIPAY.getValue().equals(payChannels)) {
            order.setIsSubmitPerformanceVoucher(1); // 使用呐呗渠道，需要提交业绩证明
        }
        this.insertSelective(order);

        // 生成支付流水
        PayWaterEntity water = new PayWaterEntity();
        water.setOemCode(dto.getOemCode());
        water.setOemName(oem.getOemName());
        water.setPayAccount(cardEntity.getBankNumber());
        water.setPayBank(cardEntity.getBankName());
        water.setPayTime(date);
        // water.setExternalOrderNo(repayDetailVO.getOrderNo());
        water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        water.setOrderNo(orderNo);
        water.setMemberId(dto.getUserId());
        water.setUserType(cardEntity.getUserType());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setAddTime(date);
        water.setAddUser(addUser);
        water.setPayChannels(payChannels);
        water.setOrderType(OrderTypeEnum.WITHDRAW.getValue());
        water.setPayWay(PayWayEnum.QUICKPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        water.setPayWaterType(PayWaterTypeEnum.WITHDRAW.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
        water.setPayAmount(dto.getAmount());
        water.setOrderAmount(dto.getAmount());
        water.setServiceFee(order.getServiceFee());
        water.setServiceFeeRate(order.getServiceFeeRate());
        water.setPayStatus(PayWaterStatusEnum.PAY_INIT.getValue());
        water.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
        this.payWaterService.insertSelective(water);

        // 保存资金账户变动记录：冻结提现金额
        log.info("保存资金账户变动记录：冻结提现金额");
        this.userCapitalAccountService.addBalanceByProfits(dto.getOemCode(),order.getOrderNo(),order.getOrderType(),dto.getUserId(),1,
                0L,order.getPayAmount(),0L,order.getPayAmount(),"订单[" + order.getOrderNo() + "]成功冻结资金账户","admin",new Date(),0,WalletTypeEnum.COMMISSION_WALLET.getValue());

        log.info("创建佣金钱包提现订单结束。");
        return order;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void handleCommissionWithdrawResult(OrderEntity order,UserCapitalAccountEntity userCapitalAccount, int status, String resMsg) throws BusinessException {
        log.info("处理佣金钱包提现结果:{},{}",JSON.toJSONString(order),status);
        // 订单主表
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUpdateTime(new Date());
        orderEntity.setId(order.getId());

        // 支付流水
        PayWaterEntity payWater = new PayWaterEntity();
        payWater.setUpdateTime(new Date());
        payWater.setOrderNo(order.getOrderNo());
        if(status == 0){ // 成功处理
            // 修改订单状态为"提现成功"
            orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
            // 修改订单流水状态为"支付成功"
            payWater.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            //删除冻结款
            userCapitalAccountService.delFreezeBalance(userCapitalAccount, order.getOrderNo(), order.getOrderType(), order.getPayAmount(), order.getAddUser());
            // 提现订单对应手续费金额累加到oem机构管理员的资金账号
            UserEntity entity = new UserEntity();
            entity.setPlatformType(2);
            entity.setAccountType(1);
            entity.setOemCode(order.getOemCode());
            entity = userService.selectOne(entity);
            this.userCapitalAccountService.addBalanceByProfits(order.getOemCode(),order.getOrderNo(),order.getOrderType(),entity.getId(),2,
                    order.getServiceFee(),order.getServiceFee(),0L,0L,"佣金钱包提现成功入账oem资金账户","admin",new Date(),1,WalletTypeEnum.CONSUMER_WALLET.getValue());
        } else { // 失败处理
            // 修改订单状态为"提现失败"
            orderEntity.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
            orderEntity.setIsSubmitPerformanceVoucher(0);
            // 修改支付流水状态为"支付失败"
            payWater.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
            payWater.setUpResultMsg(resMsg);
            // 解冻用户资金账户
            userCapitalAccountService.unfreezeBalance(userCapitalAccount, order.getOrderNo(), order.getOrderType(), order.getPayAmount(), order.getAddUser());
            // 批量修改分润记录(去掉提现选择的分润记录中绑定的提现订单号)
            profitsDetailService.batchUpdateProfitsDetail(order.getUserId(), order.getOemCode(), null, null, order.getOrderNo());
        }
        this.editByIdSelective(orderEntity);
        this.payWaterService.updatePayStatus(payWater);

        log.info("佣金钱包提现结果处理完成。");
    }

    @Override
    public Map<String, Object> calWithdrawReachAmount(Long currUserId, String oemCode, Long orderAmount,Integer withdrawType) throws BusinessException {
        log.info("计算提现到账金额：{},{},{},{}",currUserId,oemCode,orderAmount,withdrawType);

        // 判断提现金额是否大于等于最小限额（接收金额单位：分）
        OemEntity oem = this.oemService.getOem(oemCode);
        if(null == oem){
            throw new BusinessException("提现失败，OEM机构不存在");
        }
        if((withdrawType.intValue() == 2 && orderAmount < oem.getMinCommissionWalletLimit()) // 佣金钱包提现
                || (withdrawType.intValue() == 1 && orderAmount < oem.getMinConsumptionWalletLimit())) { // 消费钱包提现
            throw new BusinessException("提现金额必须大于等于最小限额");
        }
        // 判断消费钱包提现是否超过可提现金额
        if (withdrawType.intValue() == 1) {
            Long usableWithdrawAmount = this.usableWithdrawAmount(currUserId, oemCode);
            if (orderAmount > usableWithdrawAmount) {
                throw new BusinessException("提现金额不能大于用户可提现金额");
            }
        }

        if(!(withdrawType.intValue() == 1 || withdrawType.intValue() == 2)){
            throw new BusinessException("非法的提现类型");
        }
        // 查询用户资金账户信息
        UserCapitalAccountEntity userCapitalAccount = this.userCapitalAccountService.queryByUserIdAndType(currUserId, 1, oemCode,withdrawType);
        if (null == userCapitalAccount) {
            throw new BusinessException("提现失败，用户资金账户不存在");
        }
        if (userCapitalAccount.getStatus().intValue() != 1) {
            throw new BusinessException("资金账户已被冻结，如有疑问请联系客服");
        }
        // 判断可用余额是否充足
        if(userCapitalAccount.getAvailableAmount() - orderAmount < 0){
            throw new BusinessException("提现失败，账户可用余额不足");
        }
        // 返回结果
        Map<String,Object> dataMap = Maps.newHashMap();

        // 查询当前会员等级
        MemberAccountEntity member = this.memberAccountService.findById(currUserId);
        if(null == member){
            throw new BusinessException("当前用户不存在");
        }
        // 消费钱包提现
        if(withdrawType.intValue() == 1){
            String message = isWithdrawalLimit(currUserId,1,orderAmount,WalletTypeEnum.CONSUMER_WALLET.getValue()); //消费钱包
            if(StringUtils.isNoneBlank(message)){
                throw new BusinessException(message);
            }
            // 计算服务费
            Long chargePartyAmount = 0L; // 超过部分金额
            // 获取月累计提现金额
            Long monthlyWithdrawalAmount = orderService.monthlyWithdrawalAmount(member.getId(), DateUtil.getYear(new Date()), DateUtil.getMonth(new Date()));
            if ((monthlyWithdrawalAmount - oem.getConsumptionWithdrawFreeCredit()) >= 0L) {
                chargePartyAmount = orderAmount;
            } else {
                monthlyWithdrawalAmount += orderAmount;
                if ((monthlyWithdrawalAmount - oem.getConsumptionWithdrawFreeCredit()) > 0L) {
                    chargePartyAmount = monthlyWithdrawalAmount - oem.getConsumptionWithdrawFreeCredit();
                } else {
                    chargePartyAmount = 0L;
                }
            }
            Long serviceFee = 0L;
            if (chargePartyAmount < 0L) {
                chargePartyAmount = 0L;
            }
            if (chargePartyAmount > 0L && oem.getConsumptionWithdrawRate().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = oem.getConsumptionWithdrawRate().divide(new BigDecimal("100"));
                serviceFee = new BigDecimal(chargePartyAmount).multiply(rate).setScale(0, BigDecimal.ROUND_UP).longValue();
            }
            dataMap.put("serviceFee", serviceFee);
            dataMap.put("serviceFeeRate", oem.getConsumptionWithdrawRate());
            // 计算预计到账金额
            Long reachAmount = orderAmount - serviceFee;
            if (reachAmount <= 0L) {
                throw new BusinessException("到账金额大于0时才能提现");
            }
            dataMap.put("reachAmount", reachAmount);
        } else {
            // 佣金钱包提现
            MemberLevelEntity memberLevel = this.memberLevelService.findById(member.getMemberLevel());
            // 根据会员等级取手续费率
            BigDecimal serviceFeeRate = new BigDecimal("0");
            if(MemberLevelEnum.DIAMOND.getValue().intValue() == memberLevel.getLevelNo()){
                serviceFeeRate = oem.getDiamondCommissionServiceFeeRate().divide(new BigDecimal(100));
            } else {
                serviceFeeRate = oem.getCommissionServiceFeeRate().divide(new BigDecimal(100));
            }
            // 计算服务费
            BigDecimal serviceFee = serviceFeeRate.multiply(new BigDecimal(orderAmount)).setScale(0, BigDecimal.ROUND_UP);// 舍弃小数位，向上取整
            dataMap.put("serviceFee",serviceFee.longValue());
            dataMap.put("serviceFeeRate",serviceFeeRate.multiply(new BigDecimal(100)));// 前端展示会拼接%，所以这里要乘以100
            // 计算预计到账金额
            dataMap.put("reachAmount",orderAmount - serviceFee.longValue());
            if (orderAmount - serviceFee.longValue() <= 0L) {
                throw new BusinessException("到账金额大于0时才能提现");
            }
            if(MemberLevelEnum.DIAMOND.getValue().intValue() == memberLevel.getLevelNo()) { //建行或北京银行代发限额判断
                String message = isWithdrawalLimit(currUserId, 1, orderAmount - serviceFee.longValue(),WalletTypeEnum.COMMISSION_WALLET.getValue());
                if (StringUtils.isNoneBlank(message)) {
                    throw new BusinessException(message);
                }
            }else{ //呐呗代发限额判断
                DictionaryEntity dictionaryEntityBySwitch = sysDictionaryService.getByCode("withdraw_scheme_switch"); //用户提现方案开关：1-北京、建行代付 2-纳呗 3-其他
                if(dictionaryEntityBySwitch!= null && dictionaryEntityBySwitch.getDictValue()!= null){
                    if("2".equals(dictionaryEntityBySwitch.getDictValue())){ //纳呗限额
                        if (null == oem.getMaxCommissionWithdrawSingleLimit()) {
                            throw new BusinessException("未配置机构佣金提现单笔提现限额");
                        }
                        if (null == oem.getCommissionWithdrawMonthLimit()) {
                            throw new BusinessException("未配置机构佣金提现单月提现限额");
                        }
                        if(oem.getMaxCommissionWithdrawSingleLimit() < (orderAmount - serviceFee.longValue())){
                            throw new BusinessException("提现金额已超过单笔限额，请重试！");
                        }
                        Long  currentMonthWithdrawalAmount = mapper.queryCurrentDayWithdrawalAmountByUserId(currUserId,1,2,WalletTypeEnum.COMMISSION_WALLET.getValue());//佣金钱包
                        if(currentMonthWithdrawalAmount == null ){
                            currentMonthWithdrawalAmount = 0L ;
                        }
                        Long userWithdrwawalAmount = currentMonthWithdrawalAmount + orderAmount - serviceFee.longValue();
                        if(oem.getCommissionWithdrawMonthLimit() < userWithdrwawalAmount){
                            throw new BusinessException("本月提现金额已达上限，请下个月再提！");
                        }
                    }else{
                        String message = isWithdrawalLimit(currUserId, 1, orderAmount - serviceFee.longValue(),WalletTypeEnum.COMMISSION_WALLET.getValue());
                        if (StringUtils.isNoneBlank(message)) {
                            throw new BusinessException(message);
                        }
                    }
                }
            }
        }

        return dataMap;
    }

    @Override
    public void statisticsMemberGeneralize(OrderEntity orderEntity, String account, int type) {
        String oemCode = "";
        if(StringUtils.isNotBlank(orderEntity.getOemCode())){
            oemCode = orderEntity.getOemCode();
        }else{
            OrderEntity entity = orderService.queryByOrderNo(orderEntity.getOrderNo());
            oemCode = entity.getOemCode();
        }
        OemEntity oemEntity = new OemEntity();
        oemEntity.setOemCode(oemCode);
        oemEntity = oemService.selectOne(oemEntity);

        //oem机构 使用方式为纯api方式的请求 不统计
        if (oemEntity != null && "4".equals(oemEntity.getUseWay())) {
            return;
        }

        JSONObject json = new JSONObject();
        json.put("orderNo", orderEntity.getOrderNo());
        json.put("type", type);
        json.put("userId", orderEntity.getUserId());
        //发MQ
        rabbitTemplate.convertAndSend("statisticsMemberGeneralize", json);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agentAdminWithdraw(UserCapitalAccountEntity entity, UserBankCardEntity cardEntity, Long amount, String addUser, String remark) throws BusinessException {
        OemEntity oem = oemService.getOem(entity.getOemCode());
        if (oem == null) {
            throw new BusinessException("OEM机构不存在");
        }

        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(entity.getUserId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        // 生成提现订单记录
        String orderNo = OrderNoFactory.getOrderCode(entity.getUserId()); // 生成订单号
        Date date = new Date();
        // 保存订单主表信息
        OrderEntity order = new OrderEntity();
        order.setOemCode(entity.getOemCode());
        order.setUserId(entity.getUserId());
        order.setUserType(entity.getUserType());
        order.setOrderStatus(RACWStatusEnum.WAIT_FOR_AUDIT.getValue());
        order.setOrderNo(orderNo);
        order.setOrderAmount(amount);
        order.setPayAmount(amount);
        order.setProfitAmount(0L);
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setOrderType(OrderTypeEnum.SUBSTITUTE_WITHDRAW.getValue());
        order.setWalletType(entity.getWalletType());
        order.setServiceFee(0L);
        order.setServiceFeeRate(BigDecimal.ZERO);
        order.setRemark(remark);
        // 补全订单参数
//        completionParameter(entity.getUserId(), order);
        order.setAddUser(addUser);
        order.setAddTime(date);
        order.setChannelUserId(member.getChannelUserId());
        order.setChannelProductCode(member.getChannelProductCode());
        order.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        insertSelective(order);

        // 生成支付流水
        PayWaterEntity water = new PayWaterEntity();
        water.setOemCode(entity.getOemCode());
        water.setOemName(oem.getOemName());
        water.setPayAccount(cardEntity.getBankNumber());
        water.setPayBank(cardEntity.getBankName());
//        water.setPayTime(date);
        water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        water.setOrderNo(orderNo);
        water.setMemberId(entity.getUserId());
        water.setUserType(entity.getUserType());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setAddTime(date);
        water.setAddUser(addUser);
        water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());
        water.setServiceFeeRate(BigDecimal.ZERO);
        water.setServiceFee(0L);
        water.setOrderType(OrderTypeEnum.SUBSTITUTE_WITHDRAW.getValue());
        water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        water.setPayWaterType(PayWaterTypeEnum.WITHDRAW.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
        water.setPayAmount(amount);// 支付金额
        water.setOrderAmount(amount);// 订单金额
        water.setPayStatus(PayWaterStatusEnum.WAIT_FOR_AUDIT.getValue());
        water.setWalletType(entity.getWalletType());
        water.setRemark(remark);
        this.payWaterService.insertSelective(water);

        //资金账户冻结(防脏数据重新查一次资金账户)
        entity = userCapitalAccountService.findById(entity.getId());
        userCapitalAccountService.freezeBalance(entity, order.getOrderNo(), order.getOrderType(), amount, addUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agentAdminRecharge(UserCapitalAccountEntity entity, UserBankCardEntity cardEntity, Long amount, String addUser, String remark) throws BusinessException {
        OemEntity oem = oemService.getOem(entity.getOemCode());
        if (oem == null) {
            throw new BusinessException("OEM机构不存在");
        }

        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(entity.getUserId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        // 生成提现订单记录
        String orderNo = OrderNoFactory.getOrderCode(entity.getUserId()); // 生成订单号
        Date date = new Date();
        // 保存订单主表信息
        OrderEntity order = new OrderEntity();
        order.setOemCode(entity.getOemCode());
        order.setUserId(entity.getUserId());
        order.setUserType(entity.getUserType());
        order.setOrderStatus(RACWStatusEnum.WAIT_FOR_AUDIT.getValue());
        order.setOrderNo(orderNo);
        order.setOrderAmount(amount);
        order.setPayAmount(amount);
        order.setProfitAmount(0L);
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setOrderType(OrderTypeEnum.SUBSTITUTE_CHARGE.getValue());
        order.setWalletType(entity.getWalletType());
        order.setServiceFee(0L);
        order.setServiceFeeRate(BigDecimal.ZERO);
        order.setRemark(remark);
        // 补全订单参数
//        completionParameter(entity.getUserId(), order);
        order.setAddUser(addUser);
        order.setAddTime(date);
        order.setChannelUserId(member.getChannelUserId());
        order.setChannelProductCode(member.getChannelProductCode());
        order.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        insertSelective(order);

        // 生成支付流水
        PayWaterEntity water = new PayWaterEntity();
        water.setOemCode(entity.getOemCode());
        water.setOemName(oem.getOemName());
//        water.setPayTime(date);
        water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        water.setOrderNo(orderNo);
        water.setMemberId(entity.getUserId());
        water.setUserType(entity.getUserType());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setAddTime(date);
        water.setAddUser(addUser);
        water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());
        water.setServiceFeeRate(BigDecimal.ZERO);
        water.setServiceFee(0L);
        water.setOrderType(OrderTypeEnum.SUBSTITUTE_CHARGE.getValue());
        water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        water.setPayWaterType(PayWaterTypeEnum.RECHARGE.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
        water.setPayAmount(amount);// 支付金额
        water.setOrderAmount(amount);// 订单金额
        water.setPayStatus(PayWaterStatusEnum.WAIT_FOR_AUDIT.getValue());
        water.setWalletType(entity.getWalletType());
        water.setRemark(remark);
        this.payWaterService.insertSelective(water);
    }

    @Override
    public void userLogOff(Long userId, String oemCode) throws BusinessException {
        log.info("用户注销请求处理开始：{},{}",userId,oemCode);

        // 判断用户是否存在
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("用户不存在");
        }
        if (Objects.equals(MemberStateEnum.STATE_OFF.getValue(), member.getStatus())) {
            throw new BusinessException("当前操作账户已经被注销");
        }
        //校验是否满足注销条件
        String result = canCancel(member, MemberStateEnum.STATE_OFF.getValue());
        if (StringUtils.isNotBlank(result)) {
            throw new BusinessException(result);
        }
        this.memberAccountService.updateStatus(userId, MemberStateEnum.STATE_OFF.getValue(), member.getMemberAccount());
        //清除会员token
        String token = redisService.get(RedisKey.LOGIN_TOKEN_KEY + member.getOemCode() + "_" + "userId_1_" + member.getId());
        if(!StringUtils.isBlank(token)){
            redisService.delete(RedisKey.LOGIN_TOKEN_KEY + member.getOemCode() + "_" + token);
        }

        log.info("用户注销请求处理结束");
    }

    @Override
    @Transactional
    public String certUseOrder(String oemCode, CompanyCertUseApiDTO entity) throws BusinessException {

        // 判断外部订单号是否全局唯一
        OrderEntity exterOrder = new OrderEntity();
        exterOrder.setExternalOrderNo(entity.getExternalOrderNo());
        List< OrderEntity > exterOrderList = orderService.select(exterOrder);
        if(CollectionUtil.isNotEmpty(exterOrderList)) {
            throw new BusinessException(ErrorCodeEnum.EXTERNAL_ORDER_IS_EXIST);
        }

        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(entity.getCompanyId());
        company.setOemCode(oemCode);
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_EXIST);
        }

        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.PROHIBIT.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.CANCELLING.getValue())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_STATUS_ERROR);
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.queryByAccount(entity.getRegPhone(), oemCode);
        if (null == member) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXISTS);
        }

        // 判断当前注销企业是否属于当前用户
        if (!member.getId().equals(company.getMemberId())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_BELONG_REG);
        }

        // 查询证件是否在申请中
        int orderCount = companyResoucesApplyRecordMapper.checkCertOrder(member.getId(), oemCode, entity.getCompanyId(), 1, entity.getApplyResouces());
        if (orderCount > 0) {
            throw new BusinessException(ErrorCodeEnum.CERT_IN_USE);
        }

        // 解析资源类型
        String[] applyResouces = entity.getApplyResouces().split(",");
        for (String resource : applyResouces) {
            if(Objects.equals(resource, 2) || Objects.equals(resource, 3)){
                throw new BusinessException(ErrorCodeEnum.CERT_USE_TYPE_ERROR);
            }
            // 查询证件是否在园区（0-不在园区 1-在园区）
            int isInPark = companyResoucesApplyRecordMapper.checkCertIsInPark(member.getId(), oemCode, entity.getCompanyId(), resource);
            if (isInPark == 0) {
                throw new BusinessException(ErrorCodeEnum.CERT_NOT_IN_PARK);
            }
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(member.getId());

        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setExternalOrderNo(entity.getExternalOrderNo());
        mainOrder.setUserId(member.getId());
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.COMPRESAPPLY.getValue());
        mainOrder.setOrderStatus(CompResApplyRecordOrderStatusEnum.TO_BE_SHIPPED.getValue());
        mainOrder.setOemCode(oemCode);
        mainOrder.setParkId(company.getParkId());
        mainOrder.setOrderAmount(0L);
        mainOrder.setPayAmount(0L);
        mainOrder.setWalletType(1); //钱包类型 1-消费钱包 2-佣金钱包
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(member.getMemberAccount());
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        orderMapper.insertSelective(mainOrder);

        // 保存企业资源申请信息
        CompanyResoucesApplyRecordEntity resApplyRecord = new CompanyResoucesApplyRecordEntity();
        resApplyRecord.setOrderNo(orderNo);
        resApplyRecord.setCompanyId(entity.getCompanyId());
        resApplyRecord.setOemCode(oemCode);
        resApplyRecord.setApplyType(1);
        resApplyRecord.setApplyResouces(entity.getApplyResouces().substring(0, entity.getApplyResouces().length() - 1));
        resApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.TO_BE_PAY.getValue());
        resApplyRecord.setPostageFees(0L);
        resApplyRecord.setRecipient(entity.getRecipient());
        resApplyRecord.setRecipientPhone(entity.getRecipientPhone());
        resApplyRecord.setProvinceCode(entity.getProvinceCode());
        resApplyRecord.setProvinceName(entity.getProvinceName());
        resApplyRecord.setCityCode(entity.getCityCode());
        resApplyRecord.setCityName(entity.getCityName());
        resApplyRecord.setDistrictCode(entity.getDistrictCode());
        resApplyRecord.setDistrictName(entity.getDistrictName());
        resApplyRecord.setRecipientAddress(entity.getRecipientAddress());
        resApplyRecord.setAddTime(new Date());
        resApplyRecord.setAddUser(member.getMemberAccount());
        companyResoucesApplyRecordService.insertSelective(resApplyRecord);
        return orderNo;
    }

    @Override
    @Transactional
    public String certReturnOrder(String oemCode, CompanyCertReturnApiDTO entity) throws BusinessException {

        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(entity.getCompanyId());
        company.setOemCode(oemCode);
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_EXIST);
        }

        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.PROHIBIT.getValue())
                && !Objects.equals(company.getStatus(), MemberCompanyStatusEnum.CANCELLING.getValue())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_STATUS_ERROR);
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.queryByAccount(entity.getRegPhone(), oemCode);
        if (null == member) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXISTS);
        }

        // 判断当前注销企业是否属于当前用户
        if (!member.getId().equals(company.getMemberId())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_BELONG_REG);
        }

        // 查询证件是否在申请中
        int orderCount = companyResoucesApplyRecordMapper.checkCertOrder(member.getId(), oemCode, entity.getCompanyId(), 2, entity.getApplyResouces());
        if (orderCount > 0) {
            throw new BusinessException(ErrorCodeEnum.CERT_IN_USE);
        }

        // 解析资源类型
        String[] applyResouces = entity.getApplyResouces().split(",");
        for (String resource : applyResouces) {
            if(Objects.equals(resource, 2) || Objects.equals(resource, 3)){
                throw new BusinessException(ErrorCodeEnum.CERT_USE_TYPE_ERROR);
            }

            // 查询证件是否在园区（0-不在园区 1-在园区）
            int isInPark = companyResoucesApplyRecordMapper.checkCertIsInPark(member.getId(), oemCode, entity.getCompanyId(), resource);
            if (isInPark == 1) {
                throw new BusinessException(ErrorCodeEnum.CERT_IN_PARK);
            }
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(member.getId());

        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setExternalOrderNo(entity.getExternalOrderNo());
        mainOrder.setUserId(member.getId());
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.COMPRESAPPLY.getValue());
        mainOrder.setOrderStatus(CompResApplyRecordOrderStatusEnum.TO_BE_RECEIVED.getValue());
        mainOrder.setOemCode(oemCode);
        mainOrder.setParkId(company.getParkId());
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(member.getMemberAccount());
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        orderMapper.insertSelective(mainOrder);

        // 保存企业资源申请信息
        CompanyResoucesApplyRecordEntity resApplyRecord = new CompanyResoucesApplyRecordEntity();
        resApplyRecord.setOrderNo(orderNo);
        resApplyRecord.setCompanyId(entity.getCompanyId());
        resApplyRecord.setOemCode(oemCode);
        resApplyRecord.setApplyType(2);
        resApplyRecord.setApplyResouces(entity.getApplyResouces().substring(0, entity.getApplyResouces().length() - 1));
        resApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.TO_BE_RECEIVED.getValue());
        resApplyRecord.setCourierCompanyName(entity.getCourierCompanyName());
        resApplyRecord.setCourierNumber(entity.getCourierNumber());
        resApplyRecord.setAddTime(new Date());
        resApplyRecord.setAddUser(member.getMemberAccount());
        companyResoucesApplyRecordService.insertSelective(resApplyRecord);
        return orderNo;
    }

    @Override
    public String createUpgradeOrder(Long memberId, String oemCode, UpgradeOrderDTO entity, String sourceType) throws BusinessException {
        log.info("创建会员升级订单开始：{}，{}，{}，{}，{}", memberId, oemCode, entity.getAmount(), entity.getProductId(), entity.getProductName());
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员等级套餐信息
        MemberLevelEntity level = this.memberLevelService.findById(entity.getProductId());
        if (null == level) {
            throw new BusinessException("会员等级套餐信息不存在");
        }

        if (!Objects.equals(level.getIsPayUpgrade(), 1)) {
            throw new BusinessException("会员等级套餐不支持付费升级");
        }

        //会员升级等级检验
        MemberLevelEntity levelEntity = this.memberLevelService.findById(member.getMemberLevel());
        if (levelEntity.getLevelNo() >= level.getLevelNo()){
            throw new BusinessException("您当前等级不符合该升级条件");
        }

        // 查询会员升级订单
        OrderEntity order = new OrderEntity();
        order.setOemCode(oemCode);
        order.setUserId(memberId);
//        order.setProductId(level.getId());
        order.setOrderType(OrderTypeEnum.UPGRADE.getValue());
        order.setOrderStatus(MemberOrderStatusEnum.TO_BE_PAY.getValue());
        List<OrderEntity> upgradeOrderList = orderService.select(order);
        if (CollectionUtil.isNotEmpty(upgradeOrderList)) {
            upgradeOrderList.stream().forEach(upgradeOrder -> {
                upgradeOrder.setOrderStatus(MemberOrderStatusEnum.CANCELLED.getValue());
                upgradeOrder.setUpdateUser(member.getMemberAccount());
                upgradeOrder.setUpdateTime(new Date());
                orderMapper.updateByPrimaryKeySelective(upgradeOrder);
            });
        }
        // 保存订单主表信息
        order = new OrderEntity();
        String orderNo = OrderNoFactory.getOrderCode(memberId);
        order.setOemCode(oemCode);
        order.setOrderNo(orderNo);
        order.setOrderAmount(entity.getAmount());
        order.setPayAmount(entity.getAmount());
        order.setProfitAmount(entity.getAmount());
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setProductName(level.getLevelName());
        order.setProductId(level.getId());
        order.setOrderType(OrderTypeEnum.UPGRADE.getValue());
        order.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        order.setSourceType(parseInt(sourceType));
        order.setChannelProductCode(member.getChannelProductCode());
        order.setChannelCode(member.getChannelCode());
        order.setChannelEmployeesId(member.getChannelEmployeesId());
        order.setChannelServiceId(member.getChannelServiceId());
        order.setChannelUserId(member.getChannelUserId());

        // 保存会员订单关系，补全订单参数
        completionParameter(memberId, order);
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(order.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            order.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }
        orderService.insertSelective(order);
        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void upgradeReimbursement(Long memberId, String oemCode, Integer upFlag) throws BusinessException {
        if (null == upFlag) {
            throw new BusinessException("会费返还达标标记不能为空");
        }

        // 查询oem机构信息
        OemEntity oem = oemService.getOem(oemCode);
        if (null == oem) {
            throw new BusinessException("未查询到oem机构信息");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if (null == memberLevel) {
            throw new BusinessException("未查询到会员等级");
        }

        // 查询会费返还分润记录
        ProfitsDetailEntity profit = new ProfitsDetailEntity();
        profit.setUserId(memberId);
        profit.setUserLevel(memberLevel.getLevelNo());
        profit.setOemCode(oemCode);
        profit.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
        profit.setOrderType(1);//订单类型 1-会员升级 2-工商注册 3-开票
        profit.setProfitsType(ProfitsTypeEnum.MEMBER_UPGRADE_RETURN_FEE.getValue());//分润类型 1-会费 2-托管费 3-开票服务费 4-注销服务费 5-会费返还
        profit = profitsDetailService.selectOne(profit);
        if (null != profit) {
            throw new BusinessException("会费已返还！");
        }

        // 查询该会员的会员升级订单
        OrderEntity order = orderMapper.queryUpgradeOrder(memberId, oemCode);
        if (null == order) {
            throw new BusinessException("未查询到会员升级订单");
        }
        // 校验会员等级id和购买订单的产品等级id一致
        if (!Objects.equals(memberLevel.getId(), order.getProductId())) {
            throw new BusinessException("会员等级和升级订单不匹配");
        }

        // 是否达标标识
        boolean accord = false;

        // 查询会员中心推广业绩
        List<MemberUpgradeRulesVO> upgradeRulesList = selectUpgradeInfo(oemCode, memberId);
        for (MemberUpgradeRulesVO rule : upgradeRulesList) {
            // 获取对应等级的推广业绩
            if (Objects.equals(memberLevel.getLevelNo(), rule.getLevelNo())) {
                if (Objects.equals(memberLevel.getLevelNo(), MemberLevelEnum.BRONZE.getValue())) {
                    if (rule.getCompleteRegistCompanyNum() >= rule.getRegistCompanyNum()) {
                        accord = true;
                        break;
                    }
                } else if (Objects.equals(memberLevel.getLevelNo(), MemberLevelEnum.GOLD.getValue())) {
                    // 会费返还达标标记(0-直推个体数达标 1-累计直推开票金额达标)
                    if (0 == upFlag) {
                        if (rule.getCompleteInvoiceNum() >= rule.getCompleteInvoiceCompanyNum()) {
                            accord = true;
                            break;
                        }
                    } else if (1 == upFlag) {
                        if (rule.getExtendInvoiceAmount() >= rule.getTotalInvoiceMoney()) {
                            accord = true;
                            break;
                        }
                    }
                }
            }
        }

        // 查询会员资金账号信息
        UserCapitalAccountEntity userCapital = new UserCapitalAccountEntity();
        userCapital.setUserType(MemberTypeEnum.MEMBER.getValue());//用户类型 1-会员 2 -系统用户
        userCapital.setUserId(memberId);
        userCapital.setStatus(1);//状态 0-禁用 1-可用
        userCapital.setOemCode(oemCode);
        userCapital.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());//钱包类型 1-消费钱包 2-佣金钱包
        userCapital = userCapitalAccountService.selectOne(userCapital);
        if (null == userCapital) {
            throw new BusinessException("会员资金账户不存在或被禁用");
        }
        Long beforeTotalAmount = userCapital.getTotalAmount();

        // 查询oem机构账号
        UserEntity oemUser = new UserEntity();
        oemUser.setOemCode(oemCode);
        oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市服务商
        oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市服务商 4-坐席客服 5-财务 6-经办人 7-运营
        oemUser.setStatus(1);//状态 0-禁用 1-可用
        oemUser = userService.selectOne(oemUser);
        if (null == oemUser) {
            throw new BusinessException("oem机构账号不存在或被禁用");
        }

        // 查询oem机构资金账号信息
        UserCapitalAccountEntity oemCapital = new UserCapitalAccountEntity();
        oemCapital.setUserType(2);//用户类型 1-会员 2 -系统用户
        oemCapital.setUserId(oemUser.getId());
        oemCapital.setStatus(1);//状态 0-禁用 1-可用
        oemCapital.setOemCode(oemCode);
        oemCapital.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
        oemCapital = userCapitalAccountService.selectOne(oemCapital);
        if (null == oemCapital) {
            throw new BusinessException("oem机构资金账户不存在或被禁用");
        }
        Long oemBeforeTotalAmount = oemCapital.getTotalAmount();

        // 如果达标，将会费返还至会员佣金钱包，并创建相对应的资金变动记录、分润记录
        if (accord) {
            upgradeReimbursementSuccess(beforeTotalAmount, oemBeforeTotalAmount, oem, userCapital, oemCapital, order, member, memberLevel, upFlag);
        } else {
            throw new BusinessException("推广业绩未达标，无法返还");
        }

        // 统计会员日推广数据
        try {
            orderService.statisticsMemberGeneralize(order, member.getMemberAccount(), 1);
        } catch (BusinessException e) {
            log.error("统计会员日推广数据失败：{}", e.getMessage());
        }
    }

    /**
     * 会员升级费用返还达标后处理
     *
     * @param beforeTotalAmount 变动前余额
     * @param oem               机构信息
     * @param userCapital       用户资金账号
     * @param order             订单
     * @param member            会员
     * @param memberLevel       会员等级
     * @param upFlag            会费返还达标标记（0-直推个体数达标 1-累计直推开票金额达标）
     */
    @Transactional(rollbackFor = {Exception.class})
    public void upgradeReimbursementSuccess(Long beforeTotalAmount, Long oemBeforeTotalAmount, OemEntity oem, UserCapitalAccountEntity userCapital,
                                     UserCapitalAccountEntity oemCapital, OrderEntity order, MemberAccountEntity member,
                                     MemberLevelEntity memberLevel, Integer upFlag) {
        // 增加会员资金账号总金额和可用金额
        userCapital.setAvailableAmount(userCapital.getAvailableAmount() + order.getPayAmount());//增加可用金额
        userCapital.setTotalAmount(userCapital.getTotalAmount() + order.getPayAmount());//增加总金额
        userCapital.setUpdateTime(new Date());
        userCapital.setUpdateUser(member.getMemberAccount());
        userCapitalAccountService.editByIdSelective(userCapital);

        // 添加会员资金账号返还记录
        UserCapitalChangeRecordEntity userCapitalReturnChange = new UserCapitalChangeRecordEntity();
        userCapitalReturnChange.setCapitalAccountId(userCapital.getId());
        userCapitalReturnChange.setUserId(member.getId());
        userCapitalReturnChange.setUserType(MemberTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-系统用户
        userCapitalReturnChange.setOemCode(oem.getOemCode());
        userCapitalReturnChange.setChangesAmount(order.getPayAmount());
        userCapitalReturnChange.setChangesBeforeAmount(beforeTotalAmount);//变动前余额
        userCapitalReturnChange.setChangesAfterAmount(userCapital.getTotalAmount());//变动后余额
        userCapitalReturnChange.setChangesType(CapitalChangeTypeEnum.INCOME.getValue());
        userCapitalReturnChange.setDetailDesc("会员升级费用返还");
        userCapitalReturnChange.setOrderType(OrderTypeEnum.UPGRADE.getValue());
        userCapitalReturnChange.setOrderNo(order.getOrderNo());
        userCapitalReturnChange.setAddTime(new Date());
        userCapitalReturnChange.setAddUser(member.getMemberAccount());
        userCapitalReturnChange.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
        userCapitalReturnChange.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());//钱包类型 1-消费钱包 2-佣金钱包
        userCapitalChangeRecordService.insertSelective(userCapitalReturnChange);

        // 减少机构资金账号总金额和可用金额
        oemCapital.setAvailableAmount(oemCapital.getAvailableAmount() - order.getPayAmount());//减少可用金额
        oemCapital.setTotalAmount(oemCapital.getTotalAmount() - order.getPayAmount());//减少总金额
        oemCapital.setUpdateTime(new Date());
        oemCapital.setUpdateUser(member.getMemberAccount());
        userCapitalAccountService.editByIdSelective(oemCapital);

        // 添加机构资金账号支出记录
        UserCapitalChangeRecordEntity oemCapitalPayChange = new UserCapitalChangeRecordEntity();
        oemCapitalPayChange.setCapitalAccountId(oemCapital.getId());
        oemCapitalPayChange.setUserId(oemCapital.getUserId());
        oemCapitalPayChange.setUserType(2);// 用户类型 1-会员 2-系统用户
        oemCapitalPayChange.setOemCode(oem.getOemCode());
        oemCapitalPayChange.setChangesAmount(order.getPayAmount());
        oemCapitalPayChange.setChangesBeforeAmount(oemBeforeTotalAmount);//变动前余额
        oemCapitalPayChange.setChangesAfterAmount(oemCapital.getTotalAmount());//变动后余额
        oemCapitalPayChange.setChangesType(CapitalChangeTypeEnum.EXPENDITURE.getValue());
        oemCapitalPayChange.setDetailDesc("会员升级费用返还");
        oemCapitalPayChange.setOrderType(OrderTypeEnum.UPGRADE.getValue());
        oemCapitalPayChange.setOrderNo(order.getOrderNo());
        oemCapitalPayChange.setAddTime(new Date());
        oemCapitalPayChange.setAddUser(member.getMemberAccount());
        oemCapitalPayChange.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
        oemCapitalPayChange.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        userCapitalChangeRecordService.insertSelective(oemCapitalPayChange);

        // 添加分润记录
        ProfitsDetailEntity profitsDetailEntity = new ProfitsDetailEntity();
        profitsDetailEntity.setOrderNo(order.getOrderNo());
        profitsDetailEntity.setOrderAmount(order.getOrderAmount());
        profitsDetailEntity.setPayAmount(order.getPayAmount());
        profitsDetailEntity.setAvailableProfitsAmount(order.getProfitAmount());
        profitsDetailEntity.setUserId(member.getId());
        profitsDetailEntity.setUserAccount(member.getMemberAccount());
        profitsDetailEntity.setOemCode(oem.getOemCode());
        profitsDetailEntity.setOemName(oem.getOemName());
        profitsDetailEntity.setUserLevel(memberLevel.getLevelNo());
        profitsDetailEntity.setUserType(MemberTypeEnum.MEMBER.getValue());
        profitsDetailEntity.setProfitsRate(new BigDecimal("100.00"));
        profitsDetailEntity.setProfitsAmount(order.getProfitAmount());
        profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
        profitsDetailEntity.setProfitsTime(new Date());
        profitsDetailEntity.setAddTime(new Date());
        profitsDetailEntity.setAddUser(member.getMemberAccount());
        // 会费返还达标标记(0-直推个体数达标 1-累计直推开票金额达标)
        if (0 == upFlag) {
            profitsDetailEntity.setRemark("直推个体数达标");
        } else if (1 == upFlag) {
            profitsDetailEntity.setRemark("累计直推开票金额达标");
        }
        profitsDetailEntity.setProfitsNo(OrderNoFactory.getProfitsNo(member.getId()));
        profitsDetailEntity.setOrderType(1); //订单类型 1-会员升级 2-工商注册 3-开票
        profitsDetailEntity.setAttributionEmployeesAccount(member.getAttributionEmployeesAccount());
        profitsDetailEntity.setAttributionEmployeesId(member.getAttributionEmployeesId());
        profitsDetailEntity.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());//钱包类型 1-消费钱包 2-佣金钱包
        profitsDetailEntity.setProfitsType(ProfitsTypeEnum.MEMBER_UPGRADE_RETURN_FEE.getValue());//分润类型 1-会费 2-托管费 3-开票服务费 4-注销服务费 5-会费返还
        profitsDetailService.insertSelective(profitsDetailEntity);
    }


    /**
     * 校验是否满足注销条件
     *
     * @param entity
     * @param status
     * @return
     */
    public String canCancel(MemberAccountEntity entity, Integer status) {
        if (!Objects.equals(MemberStateEnum.STATE_OFF.getValue(), status)) {
            return null;
        }
        //查询未注销企业
        Integer size = memberCompanyService.countMemberCompany(entity.getId(), entity.getOemCode(), MemberCompanyStatusEnum.TAX_CANCELLED.getValue());
        if (size > 0) {
            return "当前会员存在未注销企业，不允许注销";
        }
        UserCapitalAccountEntity userCapitalAccountEntity = userCapitalAccountService.queryByUserIdAndType(entity.getId(), MemberTypeEnum.MEMBER.getValue(), entity.getOemCode(),1);
        if (userCapitalAccountEntity != null && userCapitalAccountEntity.getTotalAmount() > 0) {
            return "当前会员账户总额不为0，不允许注销";
        }
        // 工商注册   0-待电子签字 1-待视频认证 2-审核中  3-待付款 4-待领证 5-已完成 6-已取消
        // 开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
        OrderEntity orderEntity = this.queryByMemberId(entity.getId(), entity.getOemCode(), OrderTypeEnum.REGISTER.getValue(), RegOrderStatusEnum.COMPLETED.getValue());
        if (orderEntity != null) {
            return "当前会员存在进行中的企业注册订单，不允许注销";
        }
        orderEntity = this.queryByMemberId(entity.getId(), entity.getOemCode(), OrderTypeEnum.INVOICE.getValue(), InvoiceOrderStatusEnum.SIGNED.getValue());
        if (orderEntity != null) {
            return "当前会员存在进行中的企业开票订单，不允许注销";
        }
        return null;
    }

    @Override
    @Transactional
    public CompanyCancelApiVO createComCancelOrder(String oemCode, CompanyCancelApiDTO entity) throws BusinessException {
        CompanyCancelApiVO companyCancelApiVO = new CompanyCancelApiVO();
        log.info("开始处理创建企业注销订单的请求：{},{},{},{}", oemCode, entity.getCompanyId(), entity.getRegPhone(), entity.getExternalOrderNo());

        // 判断外部订单号是否全局唯一
        OrderEntity exterOrder = new OrderEntity();
        exterOrder.setExternalOrderNo(entity.getExternalOrderNo());
        List< OrderEntity > exterOrderList = orderService.select(exterOrder);
        if(CollectionUtil.isNotEmpty(exterOrderList)) {
            throw new BusinessException(ErrorCodeEnum.EXTERNAL_ORDER_IS_EXIST);
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.queryByAccount(entity.getRegPhone(), oemCode);
        if (null == member) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXISTS);
        }

        // 查询企业
        MemberCompanyEntity company = this.memberCompanyMapper.selectByPrimaryKey(entity.getCompanyId());
        if (null == company) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_EXIST);
        }

        // 判断当前企业状态是否非法，只有正常或过期状态的企业才可以注销
        if (!(MemberCompanyStatusEnum.NORMAL.getValue().equals(company.getStatus()) || MemberCompanyOverdueStatusEnum.OVERDUE.getValue().equals(company.getOverdueStatus()))) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_STATUS_ERROR);
        }

        // 判断当前注销企业是否属于当前用户
        if (!member.getId().equals(company.getMemberId())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_BELONG_REG);
        }

        // 查询该企业是否还有未完成的开票订单
        int count = this.invoiceOrderService.queryNotFinishOrderByCompanyId(entity.getCompanyId(), oemCode);
        if (count > 0) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_COMPLETED);
        }

        // 查询是否已经存在当前企业的注销订单，若存在全部置为“已取消”状态
        List<OrderEntity> orderList = this.queryComCancelOrder(member.getId(), oemCode, entity.getCompanyId(), null);
        for (OrderEntity order : orderList) {
            OrderEntity t = new OrderEntity();
            t.setId(order.getId());
            t.setUpdateTime(new Date());
            t.setUpdateUser(member.getMemberAccount());
            t.setOrderStatus(QYZXOrderStatusEnum.CANCELED.getValue());
            this.editByIdSelective(t);

            // 保存企业注销订单变更记录
            log.info("企业注销订单取消成功，保存订单变更记录：");
            CompanyCancelOrderEntity query = new CompanyCancelOrderEntity();
            query.setOrderNo(order.getOrderNo());
            query.setOemCode(order.getOemCode());
            CompanyCancelOrderEntity cancelOrder = this.companyCancelOrderService.selectOne(query);
            CompanyCancelOrderChangeRecordEntity record = new CompanyCancelOrderChangeRecordEntity();
            BeanUtils.copyProperties(cancelOrder, record);
            record.setId(null);
            record.setOrderStatus(QYZXOrderStatusEnum.CANCELED.getValue());
            record.setAddTime(new Date());
            record.setAddUser(member.getMemberAccount());
            this.companyCancelOrderChangeRecordService.insertSelective(record);
        }

        log.info("开始创建新的企业注销订单：");

        // 查询企业累计开票金额
        Long totalAmount = companyInvoiceRecordService.sumUseInvoiceAmount(entity.getCompanyId());
        log.info("企业累计开票金额：{}", totalAmount);

        // 订单金额
        Long orderAmount = null;
        // 订单状态
        Integer orderStatus = null;

        // 查询企业累积开票额度阈值
        ProductEntity t = new ProductEntity();
        t.setStatus(1);
        t.setOemCode(oemCode);
        // 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任',
        if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.INDIVIDUAL_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.INDEPENDENTLY_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.LIMITED_PARTNER_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue());
        }
        ProductEntity product = this.productService.selectOne(t);

        if (null == product) {
            throw new BusinessException(ErrorCodeEnum.PRODUCT_NOT_CONFIGURED);
        }

        // 1.累积开票额度大于阈值可免注销服务费 2.若产品金额为0，也视为免注销服务费
    /*    if (totalAmount >= product.getCancelTotalLimit() || product.getProdAmount() == 0) {
            orderAmount = 0L;
            orderStatus = QYZXOrderStatusEnum.CANCEL_HANDLEING.getValue();// 订单状态置为“注销处理中”
        } else {
            orderAmount = 0L;
            orderStatus = QYZXOrderStatusEnum.CANCEL_HANDLEING.getValue();// 订单状态置为“注销处理中”
        }*/
        orderAmount = 0L;
        orderStatus = QYZXOrderStatusEnum.CANCEL_HANDLEING.getValue();// 订单状态置为“注销处理中”
        // 构建企业注销订单主表对象
        OrderEntity mainOrder = this.buildComCancelMainOrder(member.getId(), oemCode, product.getId(), product.getProdName(),
                company.getParkId(), orderAmount, orderStatus, entity.getExternalOrderNo());
        // 保存订单主表信息
        this.insertSelective(mainOrder);

        // 生成企业注销订单
        CompanyCancelOrderEntity companyCancelOrder = this.buildComCancelOrder(mainOrder, entity.getCompanyId(), company.getCompanyType(), company.getCompanyName(), totalAmount);
        this.companyCancelOrderService.insertSelective(companyCancelOrder);

        // 保存企业注销订单变更记录
        CompanyCancelOrderChangeRecordEntity changeRecord = new CompanyCancelOrderChangeRecordEntity();
        BeanUtils.copyProperties(companyCancelOrder, changeRecord);
        changeRecord.setId(null);
        changeRecord.setOrderStatus(mainOrder.getOrderStatus());
        this.companyCancelOrderChangeRecordService.insertSelective(changeRecord);

        log.info("企业注销订单创建成功：{}", JSON.toJSONString(mainOrder));

        //如果注销企业为免手续费则需要将企业表数据的状态改成注销中 add ni.jiang
        if (QYZXOrderStatusEnum.CANCEL_HANDLEING.getValue().equals(orderStatus)) {
            MemberCompanyEntity companyEntity = new MemberCompanyEntity();
            companyEntity.setId(company.getId());
            companyEntity.setStatus(MemberCompanyStatusEnum.CANCELLING.getValue());
            companyEntity.setUpdateTime(new Date());
            companyEntity.setUpdateUser(member.getMemberAccount());
            memberCompanyMapper.updateByPrimaryKeySelective(companyEntity);
        }

        log.info("企业注销请求处理完毕");
        companyCancelApiVO.setOrderNo(mainOrder.getOrderNo());
        companyCancelApiVO.setOrderStatus(mainOrder.getOrderStatus());
        return companyCancelApiVO;
    }

    /**
     * 获取签名时间
     * @param orderEntity
     * @return
     */
    @Override
    public void getSignDate(OrderEntity orderEntity, JSONObject json) {
        if(orderEntity==null){
            throw  new BusinessException("开户订单不存在");
        }
        Integer orderStatus;
        //V2.8 根据园区流程标识判断是否需要校验身份验证
        Integer processMark = Optional.ofNullable(parkService.findById(orderEntity.getParkId())).map(ParkEntity::getProcessMark).orElseThrow(()->new BusinessException("园区信息不存在"));
        if (Objects.equals(processMark, ParkProcessMarkEnum.VIDEO.getValue())) {
            orderStatus = RegOrderStatusEnum.TO_BE_VIDEO.getValue();
        } else {
            orderStatus = RegOrderStatusEnum.TO_BE_PAY.getValue();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        RegisterOrderChangeRecordEntity registerOrderChangeRecordEntity = new RegisterOrderChangeRecordEntity();
        registerOrderChangeRecordEntity.setOrderStatus(orderStatus);
        registerOrderChangeRecordEntity.setOrderNo(orderEntity.getOrderNo());
        registerOrderChangeRecordEntity.setOemCode(orderEntity.getOemCode());
        List<RegisterOrderChangeRecordEntity> records = registerOrderChangeRecordService.select(registerOrderChangeRecordEntity);
        try {
            if (CollectionUtil.isNotEmpty(records)) {
                Date addTime = records.get(0).getAddTime();
                json.put("signDate", PDFUtil.getJson(sdf.format(addTime), "1"));
                DateTime dateTime = new DateTime(addTime);
                dateTime = dateTime.plusYears(1);
                json.put("signEndDate", PDFUtil.getJson(sdf.format(dateTime.toDate()), "1"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return;
    }

    @Override
    public List<MemberUpgradeRulesVO> selectUpgradeInfo(String oemCode, Long currUserId) throws BusinessException {
        log.info("查询会员中心展示数据：{}，{}", oemCode, currUserId);

        MemberAccountEntity member = this.memberAccountService.findById(currUserId);
        if (null == member) {
            throw new BusinessException("获取会员升级信息失败，会员信息不存在！");
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if (null == memberLevel) {
            throw new BusinessException("获取会员升级信息失败，会员等级不存在！");
        }

        // 查询会员VIP配置信息
        List<MemberUpgradeRulesVO> list = memberLevelMapper.selectUpgradeInfo(oemCode);

        List<MemberUpgradeRulesVO> dataList = com.google.common.collect.Lists.newArrayList();// 返回结果集

        // 遍历结果集进行业务处理
        list.stream().forEach(upgradeRule -> {
            /*// 查询该会员的会员升级订单
            Example example = new Example(OrderEntity.class);
            example.createCriteria().andEqualTo("userId", currUserId).andEqualTo("oemCode", oemCode).andEqualTo("orderType", OrderTypeEnum.UPGRADE.getValue());
            example.orderBy("addTime").desc();
            List<OrderEntity> upgradeOrderList = orderService.selectByExample(example);
            if (CollectionUtil.isNotEmpty(upgradeOrderList)) {
                // 遍历会员升级订单
                upgradeOrderList.stream().forEach(order -> {
                    // 校验会员等级id和购买订单的产品等级id一致
                    if (Objects.equals(upgradeRule.getId(), order.getProductId())) {
                        upgradeRule.setIsPayUpgrade(1);
                    }
                });
            }*/

            // 查询会员等级是否付费升级
            if (Objects.equals(memberLevel.getLevelNo(), upgradeRule.getLevelNo())) {
                upgradeRule.setIsPayUpgrade(member.getIsPayUpgrade());
            }

            // 查询会费返还分润记录
            ProfitsDetailEntity profit = new ProfitsDetailEntity();
            profit.setUserId(currUserId);
            profit.setUserLevel(upgradeRule.getLevelNo());
            profit.setOemCode(oemCode);
            profit.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
            profit.setOrderType(1);//订单类型 1-会员升级 2-工商注册 3-开票
            profit.setProfitsType(ProfitsTypeEnum.MEMBER_UPGRADE_RETURN_FEE.getValue());//分润类型 1-会费 2-托管费 3-开票服务费 4-注销服务费 5-会费返还
            profit = profitsDetailService.selectOne(profit);
            if (null != profit) {
                upgradeRule.setIsReturnUpgrade(1);
            }

            // 查询用户推广个体统计信息，设置当前达标个体数
            MemberExtendQuery query = new MemberExtendQuery();
            query.setOemCode(oemCode);
            query.setExtendType(member.getExtendType());
            query.setLevelNo(memberLevel.getLevelNo());
            query.setUserId(currUserId);
            CompanyRegProgressVO companyRegStat = this.memberAccountService.queryCompanyRegProgress(query);
            upgradeRule.setCompleteRegistCompanyNum(companyRegStat.getFinishedCount());

            // 查询用户推广开票统计信息，设置当前达标开票数
            List<Long> countList = this.memberAccountService.queryCompanyInvoiceCount(query);
            upgradeRule.setCompleteInvoiceNum(countList.size());

            // 2.2新增需求：税务顾问会费返还规则，查询直推达标个体数和累计直推开票数
            if (MemberLevelEnum.GOLD.getValue().intValue() == upgradeRule.getLevelNo().intValue()) {
                AchievementStatVO statData = this.memberAccountMapper.queryAchievementStatistic(currUserId, oemCode, member.getExtendType(), null, memberLevel.getInvoiceMinMoney());
                upgradeRule.setExtendInvoiceAmount(statData.getAddInvoiceAmount());// 累计直推开票数
                upgradeRule.setExtendStdComCount(statData.getExtendStdComCount()); // 直推达标个体数
            }

            // 2.2需求：不区分直客散客，都可以查看所有
            /*// 如果是散客，最多只能看VIP信息
            if (ExtendTypeEnum.INDEPENDENT_CUSTOMER.getValue().equals(member.getExtendType())) {
                if (MemberLevelEnum.NORMAL.getValue().equals(upgradeRule.getLevelNo()) || MemberLevelEnum.BRONZE.getValue().equals(upgradeRule.getLevelNo())) {
                    dataList.add(upgradeRule);
                }
            } else {
                // 直客可以查看所有
                dataList.add(upgradeRule);
            }*/
            dataList.add(upgradeRule);
        });
        return dataList;
    }

    @Override
    public void autoCancelWithdrawOrder(OrderEntity order) throws BusinessException {
        log.info("超时自动取消对公户提现订单：{}",JSON.toJSONString(order));
        // 修改订单主表状态为已取消
        this.updateOrderStatus("admin",order.getOrderNo(),RACWStatusEnum.CANCELED.getValue());
    }

    @Override
    public List<OrderEntity> selectToSubmitWithdrawOrder(Integer minutes) throws BusinessException {
        log.info("查询{}分钟前待提交状态的对公户提现订单列表:" + minutes);
        if(null == minutes){
            throw new BusinessException("未配置对公户提现订单超时取消时间");
        }
        return this.orderMapper.selectToSubmitWithdrawOrder(minutes);
    }

    @Override
    public AliPayDto buildAliPayParams(String oemCode, String payNo, Long amount,String buyerId,String buyerLogonId) throws BusinessException, UnknownHostException {
        // 读取渠道支付宝支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 20);
        if (null == paramsEntity) {
            throw new BusinessException("未配置渠道支付宝支付相关信息！");
        }
        // 组装参数对象
        AliPayDto payDto = new AliPayDto();
        payDto.setTradeNo(payNo);
        payDto.setAmount(String.valueOf(amount));
        payDto.setAgentNo(paramsEntity.getAccount());
        payDto.setAppSecret(paramsEntity.getSecKey());
        payDto.setIpAddr(InetAddress.getLocalHost().getHostAddress());
        payDto.setServicePubKey(paramsEntity.getPublicKey());
        payDto.setPostUrl(paramsEntity.getUrl());
        payDto.setBuyerId(buyerId);
        payDto.setBuyerLogonId(buyerLogonId);

        // 解析paramValues，配置样例：{"productCode":"PRD00095","appId": "2021001199602334","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "0a474834-ab3a-4ada-a33d-3c48b1f28c86","pubKey":"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsO2nz7Ipy/CnlOWoBWk72SYYJFwcExiop13xEo1JDe/sRfoaDv8OTFkU+3gryGkz0bYYKFwls76PCGdNMAUsT6ZAfpMP1EuPVJElGFsRUR5r38KfuUI8I8hBOpccpvvmQe5cRwVSdsiMrbxeoC7FTWeyYQyFrFBRwK177E4hpyOAucGDjmBlxElNxQkX2tVXlRXcuwcWbL68SVV5wZjxLp345oy6bLvFs6rtntY+L0cAmkLk5cqr3Tk2f96bXa5/cJ/D1BferXmGS2fCLeCfszs13NFp7Sau8VYdzMpib08P03wtr7mqbYpwG/Qbt3Rhn+zDjlCwkb9gFuRNXIDvfwIDAQAB"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        payDto.setKeyNum(params.getString("keyNum"));
        payDto.setAppId(params.getString("appId"));
        payDto.setSignKey(params.getString("signKey"));
        payDto.setAlipayPublicKey(params.getString("pubKey"));// 支付宝公钥
        payDto.setAppPrivateKey(paramsEntity.getPrivateKey());// 小程序私钥
        String productCode = params.getString("productCode");// 渠道产品编码
        if(StringUtils.isBlank(productCode)){
            throw new BusinessException("渠道产品编码配置错误！");
        }
        payDto.setProductCode(productCode);

        // callbackUrl回调地址
        DictionaryEntity dic = this.dictionaryService.getByCode("wechatPayNotifyUrl"); // 与微信回调地址一致
        if (null == dic) {
            throw new BusinessException("未配置渠道支付回调通知地址!");
        }
        payDto.setCallbackUrl(dic.getDictValue());
        return payDto;
    }

    @Override
    public BillRecordVO listBillDetailApi(BillDetailApiQuery query) throws BusinessException {
        BillRecordVO billRecord = new BillRecordVO();
        Integer walletType = 1;// 钱包类型 1-消费钱包 2-佣金钱包

        // 查询账单明细列表
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<BillDetailVO> billDetailList = new ArrayList<BillDetailVO>();

        // 会员等级 0-税务顾问 1-城市服务商（查询佣金钱包）
        if (Objects.equals(query.getLevelNo(), 0) || Objects.equals(query.getLevelNo(), 1)) {
            billDetailList = orderMapper.listBillDetailApi(query.getUserId(), query.getOemCode(), query.getType(),
                    query.getMonth(), WalletTypeEnum.COMMISSION_WALLET.getValue(), null);
            walletType = WalletTypeEnum.COMMISSION_WALLET.getValue();

            // 会员等级 2-城市合伙人 3-高级城市合伙人（查询消费钱包）
        } else if (Objects.equals(query.getLevelNo(), 2) || Objects.equals(query.getLevelNo(), 3)) {
            billDetailList = orderMapper.listBillDetailApi(query.getUserId(), query.getOemCode(), query.getType(),
                    query.getMonth(), WalletTypeEnum.CONSUMER_WALLET.getValue(), null);
            walletType = WalletTypeEnum.CONSUMER_WALLET.getValue();
        }
        billRecord.setBillPageData(new PageInfo<BillDetailVO>(billDetailList));

        // 统计收入和支出
        Long incomeAmount = 0L;
        Long payAmount = 0L;
        if (Objects.equals(query.getType(), 1)) {
            List<BillDetailVO> billList = orderMapper.listBillDetailApi(query.getUserId(), query.getOemCode(), query.getType(),
                    query.getMonth(), walletType, query.getType());
            incomeAmount = billList.stream().filter((b) -> b.getOrderType() == 19).mapToLong(BillDetailVO::getAmount).sum();
        } else if (Objects.equals(query.getType(), 4)) {
            List<BillDetailVO> billList = orderMapper.listBillDetailApi(query.getUserId(), query.getOemCode(), query.getType(),
                    query.getMonth(), walletType, query.getType());
            incomeAmount = billList.stream().mapToLong(BillDetailVO::getAmount).sum();
        }
        billRecord.setIncomeAmount(incomeAmount);
        billRecord.setPayAmount(payAmount);
        return billRecord;
    }

    @Override
    public void cancelAdmin(Long companyId,String cancelCredentials,String account, Integer cancelType) {
        MemberCompanyEntity company = memberCompanyService.findById(companyId);
        if (null == company) {
            throw new BusinessException("操作失败，企业信息不存在");
        }
        // 已税务注销的企业不能再次进行税务注销
        if (MemberCompanyStatusEnum.TAX_CANCELLED.getValue().equals(company.getStatus()) && cancelType == 1) {
            throw new BusinessException("企业已税务注销，请勿重复操作");
        }
        // 查询该企业是否还有未完成的开票订单
        int count = this.invoiceOrderService.queryNotFinishOrderByCompanyId(companyId, company.getOemCode());
        if (count > 0) {
            throw new BusinessException("该企业还有进行中的开票订单，请完成或取消订单后再试");
        }
        // 查询是否已经存在当前企业的注销订单，若存在全部置为“已取消”状态
        List<OrderEntity> orderList = orderService.queryComCancelOrder(company.getMemberId(), company.getOemCode(), companyId, null);
        if(cn.hutool.core.collection.CollectionUtil.isNotEmpty(orderList)){
            throw new BusinessException("该企业还有进行中的企业注销订单，请完成或取消订单后再试");
        }
        if (MemberCompanyStatusEnum.TAX_CANCELLED.getValue().equals(company.getStatus())) {
            // 查询是否有已税务注销状态的注销订单
            List<OrderEntity> orderEntities = orderService.queryComCancelOrder(company.getMemberId(), company.getOemCode(), companyId, CompCancelOrderStatusEnum.TAX_CANCEL_COMPLETED.getValue());
            if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(orderEntities)) {
                throw new BusinessException("该企业还有进行中的企业注销订单，请完成或取消订单后再试");
            }
        }

        if (cancelType == 1) {
            MemberCompanyEntity companyEntity = new MemberCompanyEntity();
            companyEntity.setId(company.getId());
            companyEntity.setCancelCredentials(cancelCredentials);
            companyEntity.setStatus(MemberCompanyStatusEnum.TAX_CANCELLED.getValue());
            companyEntity.setUpdateTime(new Date());
            companyEntity.setUpdateUser(account);
            memberCompanyService.editByIdSelective(companyEntity);
            //添加企业变更记录
            company = memberCompanyService.findById(companyId);
            memberCompanyChangeService.insertChangeInfo(company,account,"企业税务注销");
            return;
        }

        // 查询企业累计开票金额
        Long totalAmount = companyInvoiceRecordService.sumUseInvoiceAmount(companyId);
        log.info("企业累计开票金额：{}", totalAmount);

        // 查询企业累积开票额度阈值
        ProductEntity t = new ProductEntity();
        t.setStatus(1);
        t.setOemCode(company.getOemCode());
        // 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任',
        if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.INDIVIDUAL_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.INDEPENDENTLY_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.LIMITED_PARTNER_CANCEL.getValue());
        } else if (MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(company.getCompanyType())) {
            t.setProdType(ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue());
        }
        ProductEntity product = productService.selectOne(t);
        if(product == null){
            throw new BusinessException("未找到相应的注销产品");
        }
        // 构建企业注销订单主表对象
        OrderEntity mainOrder = buildComCancelMainOrder(company.getMemberId(), company.getOemCode(), product.getId(), product.getProdName(),
                company.getParkId(), 0L, 5, null);
        mainOrder.setSourceType(0);

        // 保存订单主表信息
        orderService.insertSelective(mainOrder);
        //V3.0 订单完成推送给国金  订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
        //  企业注销
        if (ChannelPushStateEnum.TO_BE_PAY.getValue().equals(mainOrder.getChannelPushState())) {
            try {
                List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
                OpenOrderVO vo = new OpenOrderVO();
                vo.setOrderNo(mainOrder.getOrderNo());
                vo.setId(mainOrder.getUserId());
                vo.setOemCode(mainOrder.getOemCode());
                vo.setOrderType(mainOrder.getOrderType());
                listToBePush.add(vo);
                rabbitTemplate.convertAndSend("orderPush", listToBePush);
            }catch (BusinessException e){
                log.error("推送失败：{}",e.getMessage());
            }
        }

        // 生成企业注销订单
        CompanyCancelOrderEntity companyCancelOrder = buildComCancelOrder(mainOrder, companyId, company.getCompanyType(), company.getCompanyName(), totalAmount);
        companyCancelOrder.setAttachmentAddr(cancelCredentials);
        companyCancelOrderService.insertSelective(companyCancelOrder);

        // 保存企业注销订单变更记录
        CompanyCancelOrderChangeRecordEntity changeRecord = new CompanyCancelOrderChangeRecordEntity();
        BeanUtils.copyProperties(companyCancelOrder, changeRecord);
        changeRecord.setId(null);
        changeRecord.setOrderStatus(mainOrder.getOrderStatus());
        changeRecord.setAddUser(account);
        changeRecord.setAddTime( new Date());
        companyCancelOrderChangeRecordService.insertSelective(changeRecord);


        MemberCompanyEntity companyEntity = new MemberCompanyEntity();
        companyEntity.setId(company.getId());
        if (StringUtil.isNotBlank(company.getCancelCredentials())) {
            cancelCredentials = company.getCancelCredentials() + "," + cancelCredentials;
        }
        companyEntity.setCancelCredentials(cancelCredentials);
        companyEntity.setStatus(MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue());
        companyEntity.setUpdateTime(new Date());
        companyEntity.setUpdateUser(account);
        memberCompanyService.editByIdSelective(companyEntity);
        log.info("企业注销订单创建成功：{}", JSON.toJSONString(mainOrder));
        //添加企业变更记录
        company = memberCompanyService.findById(companyId);
        memberCompanyChangeService.insertChangeInfo(company,account,"企业工商注销");

        //  未确认成本的税单改成已作废
        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(companyEntity.getEin());
        query.setCompanyId(companyEntity.getId());
        query.setStatusRange(1);
        List<PendingTaxBillVO> pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            for (PendingTaxBillVO vo:pendingTaxBillList){
                CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(vo.getCompanyTaxBillId());
                companyTaxBillEntity.setTaxBillStatus(9);
                companyTaxBillEntity.setUpdateTime(new Date());
                companyTaxBillEntity.setUpdateUser(account);
                companyTaxBillService.editByIdSelective(companyTaxBillEntity);
                CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
                BeanUtils.copyProperties(companyTaxBillEntity,companyTaxBillChangeEntity);
                companyTaxBillChangeEntity.setId(null);
                companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
                companyTaxBillChangeEntity.setDescrip("企业注销，税单作废");
                companyTaxBillChangeEntity.setAddTime(new Date());
                companyTaxBillChangeEntity.setAddUser(account);
                companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
            }
        }
    }

    @Override
    public List<OrderEntity> queryByProductId(Long productId, String oemCode, Integer orderType, Integer orderStatus) {
        return mapper.queryByProductId(productId, oemCode, orderType, orderStatus);
    }

    @Override
    @Transactional
    public OrderEntity createBaseOrder(String oemCode, Long userId, String sourceType) {
        OemEntity oem = oemService.getOem(oemCode);
        if (oem == null) {
            throw new BusinessException("OEM机构不存在");
        }
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 生成提现订单记录
        String orderNo = OrderNoFactory.getOrderCode(userId); // 生成订单号
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOemCode(oemCode);
        orderEntity.setRemark(oem.getOemName());
        orderEntity.setOrderNo(orderNo);
        orderEntity.setUserId(userId);
        orderEntity.setUserType(member.getMemberType());
        orderEntity.setAddTime(new Date());
        orderEntity.setAddUser(member.getMemberAccount());
        orderEntity.setUserType(UserTypeEnum.MEMBER.getValue());
        orderEntity.setIsShareProfit(0);
        orderEntity.setDiscountAmount(0L);
        orderEntity.setProfitAmount(0L);
        orderEntity.setProfitStatus(0);
        orderEntity.setAuditStatus(0);// 默认审核状态为0 待审核
        orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
        orderEntity.setOrderType(OrderTypeEnum.RECOVERABLE_TAX.getValue());
        orderEntity.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        if (StringUtils.isNotBlank(sourceType)) {
            orderEntity.setSourceType(parseInt(sourceType));
        }
        // 保存会员订单关系，补全订单参数
        completionParameter(userId, orderEntity);
        return orderEntity;
    }

    @Override
    @Transactional
    public OrderEntity saveTaxSupplement(String oemCode, Long userId, String sourceType, CompanyTaxBillEntity companyTaxBillEntity) {
        //创建基础订单
        OrderEntity orderEntity = createBaseOrder(oemCode, userId, sourceType);
        //存量订单改为已取消状态
        OrderEntity order = new OrderEntity();
        order.setOemCode(oemCode);
        order.setUserId(userId);
        order.setOrderType(OrderTypeEnum.RECOVERABLE_TAX.getValue());
        order.setOrderStatus(RACWStatusEnum.PAYING.getValue());
        List<OrderEntity> upgradeOrderList = orderService.select(order);
        if (CollectionUtil.isNotEmpty(upgradeOrderList)) {
            upgradeOrderList.stream().forEach(upgradeOrder -> {
                upgradeOrder.setOrderStatus(RACWStatusEnum.CANCELED.getValue());
                upgradeOrder.setUpdateUser(orderEntity.getAddUser());
                upgradeOrder.setUpdateTime(new Date());
                orderMapper.updateByPrimaryKeySelective(upgradeOrder);
            });
        }
        //创建税费补缴订单
        String remark = "企业税费补缴";
        long supplementTaxMoney = companyTaxBillEntity.getSupplementTaxMoney();
        // 查账征收方式的税单应补税需要将所得税换算成年度数据
        if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            CompanyTaxBillVO taxBillVO = new CompanyTaxBillVO();
            taxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
            companyTaxBillService.detailOfAuditCollection(taxBillVO);
            supplementTaxMoney = taxBillVO.getSupplementTaxMoney();
        }
        orderEntity.setOrderAmount(supplementTaxMoney);
        orderEntity.setPayAmount(supplementTaxMoney);
        orderEntity.setProductName(remark);
        orderEntity.setProductId(companyTaxBillEntity.getId());
        orderEntity.setRemark(remark);
        orderEntity.setOrderStatus(RACWStatusEnum.PAYING.getValue());
        orderEntity.setOrderType(OrderTypeEnum.RECOVERABLE_TAX.getValue());
        orderEntity.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        if (StringUtils.isNotBlank(sourceType)) {
            orderEntity.setSourceType(parseInt(sourceType));
        }
        companyTaxBillEntity.setOrderNo(orderEntity.getOrderNo());
        companyTaxBillEntity.setUpdateUser(orderEntity.getAddUser());
        companyTaxBillEntity.setUpdateTime(new Date());
        companyTaxBillService.editByIdSelective(companyTaxBillEntity);
        mapper.insertSelective(orderEntity);
        return orderEntity;
    }

    @Override
    @Transactional
    public void saveTaxRefund(String oemCode, Long userId, String sourceType, CompanyTaxBillEntity companyTaxBillEntity) {
        //创建基础订单
        OrderEntity orderEntity = createBaseOrder(oemCode, userId, sourceType);
        String oemName = orderEntity.getRemark();
        //创建退税费订单
        String remark = "退税费";
        if (IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            orderEntity.setOrderAmount(companyTaxBillEntity.getRecoverableTaxMoney());
            orderEntity.setPayAmount(companyTaxBillEntity.getRecoverableTaxMoney());
        } else if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            CompanyTaxBillVO companyTaxBillVO = new CompanyTaxBillVO();
            companyTaxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
            companyTaxBillService.detailOfAuditCollection(companyTaxBillVO);
            orderEntity.setOrderAmount(companyTaxBillVO.getRecoverableTaxMoney());
            orderEntity.setPayAmount(companyTaxBillVO.getRecoverableTaxMoney());
        }
        orderEntity.setProductName(remark);
        orderEntity.setProductId(companyTaxBillEntity.getId());
        orderEntity.setRemark(remark);
        orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
        orderEntity.setOrderType(OrderTypeEnum.SUPPLEMENT_TAX.getValue());
        orderEntity.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        if (StringUtils.isNotBlank(sourceType)) {
            orderEntity.setSourceType(parseInt(sourceType));
        }
        mapper.insertSelective(orderEntity);
        UserEntity oemUser = new UserEntity();
        oemUser.setOemCode(orderEntity.getOemCode());
        oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
        oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
        oemUser.setStatus(1);//状态 0-禁用 1-可用
        oemUser = userService.selectOne(oemUser);
        //查询机构资金账号信息
        UserCapitalAccountEntity userCapital = new UserCapitalAccountEntity();
        userCapital.setUserType(MemberTypeEnum.EMPLOYEE.getValue());//用户类型 1-会员 2 -系统用户
        userCapital.setUserId(oemUser.getId());
        userCapital.setStatus(CustomerStatusEnum.NORMAL.getValue());//状态 0-禁用 1-可用
        userCapital.setOemCode(oemCode);
        userCapital.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());//钱包类型 1-消费钱包 2-佣金钱包
        userCapital = userCapitalAccountService.selectOne(userCapital);
        if(null == userCapital){
            throw new BusinessException("机构资金账户不存在或被禁用");
        }
        // 生成支付流水
        PayWaterEntity water = new PayWaterEntity();
        water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        water.setOrderNo(orderEntity.getOrderNo());
        water.setMemberId(userId);
        water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setOemCode(oemCode);
        water.setOemName(oemName);
        if (IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            water.setOrderAmount(companyTaxBillEntity.getRecoverableTaxMoney());
            water.setPayAmount(companyTaxBillEntity.getRecoverableTaxMoney());
        } else if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            CompanyTaxBillVO companyTaxBillVO = new CompanyTaxBillVO();
            companyTaxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
            companyTaxBillService.detailOfAuditCollection(companyTaxBillVO);
            water.setOrderAmount(companyTaxBillVO.getRecoverableTaxMoney());
            water.setPayAmount(companyTaxBillVO.getRecoverableTaxMoney());
        }
        water.setOrderType(PayWaterOrderTypeEnum.SUPPLEMENT_TAX.getValue());
        water.setPayWaterType(PayWaterTypeEnum.TAX_REFUND.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款，6-对公户提现，7-企业退税
        water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        water.setPayAccount(userCapital.getCapitalAccount());
        water.setAddTime(orderEntity.getAddTime());
        water.setAddUser(orderEntity.getAddUser());
        water.setUpdateTime(orderEntity.getAddTime());
        water.setUpdateUser(orderEntity.getAddUser());
        water.setPayTime(orderEntity.getAddTime());
        water.setRemark(remark);// 备注字段暂用来存储商品名称
        water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗
        water.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue()); //钱包类型 1-消费钱包 2-佣金钱包
        payWaterService.insertSelective(water);

        //增加自己资金
        userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getUserId(), 1, orderEntity.getPayAmount(), orderEntity.getPayAmount(), 0L, 0L, remark, orderEntity.getAddUser(), orderEntity.getAddTime(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());

        //给机构扣减资金
        userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), oemUser.getId(), 2, orderEntity.getPayAmount(), 0L, 0L, 0L, remark, orderEntity.getAddUser(), orderEntity.getAddTime(), 0, WalletTypeEnum.CONSUMER_WALLET.getValue());

        //更新企业税单表
        companyTaxBillEntity.setUpdateTime(orderEntity.getAddTime());
        companyTaxBillEntity.setUpdateUser(orderEntity.getAddUser());
        companyTaxBillEntity.setCompleteTime(orderEntity.getAddTime());
        companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TAX_REFUNDED.getValue());
        companyTaxBillService.editByIdSelective(companyTaxBillEntity);
        //企业税单历史记录
        CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
        BeanUtils.copyProperties(companyTaxBillEntity,companyTaxBillChangeEntity);
        companyTaxBillChangeEntity.setId(null);
        companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
        companyTaxBillChangeEntity.setDescrip("用户领取退税");
        companyTaxBillChangeEntity.setAddTime(new Date());
        companyTaxBillChangeEntity.setAddUser(orderEntity.getAddUser());
        companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
    }

    @Override
    public Integer corporateAmoutRepeatCheck(Integer orderType, Integer exceptOrderStatus, Long payAmount, String repeatTime,Long corporateAccountId) {
        return mapper.corporateAmoutRepeatCheck(orderType, exceptOrderStatus, payAmount, repeatTime,corporateAccountId);
    }

    @Override
    @Transactional(rollbackFor =  {Exception.class})
    public void updateMemberPhone(MemberAccountEntity entity, UpdateMemberPhonePO po,String account) {
        String oldAccount = entity.getMemberAccount();
        //拷贝变动记录
        MemberAccountChangeEntity memberAccountChangeEntity=new MemberAccountChangeEntity();
        BeanUtils.copyProperties(entity,memberAccountChangeEntity);
        //修改会员信息
        entity.setMemberAccount(po.getPhone());
        entity.setMemberPhone(po.getPhone());
        entity.setRemark(po.getRemark());
        entity.setUpdateUser(account);
        entity.setUpdateTime(new Date());
        memberAccountService.editByIdSelective(entity);
        //增加比会员变动记录
        memberAccountChangeEntity.setPhoneNew(po.getPhone());
        memberAccountChangeEntity.setAccountId(entity.getId());
        memberAccountChangeEntity.setId(null);
        if(StringUtils.isNotBlank(po.getFileUrl())) {
            memberAccountChangeEntity.setFileUrl(po.getFileUrl());
        }
        memberAccountChangeEntity.setAddTime(new Date());
        memberAccountChangeEntity.setAddUser(account);
        memberAccountChangeEntity.setUpdateTime(null);
        memberAccountChangeEntity.setUpdateUser(null);
        memberAccountChangeEntity.setRemark(po.getRemark());
        memberAccountChangeService.insertSelective(memberAccountChangeEntity);

        MemberLevelEntity levelEntity=memberLevelService.findById(entity.getMemberLevel());
        if(levelEntity==null){
            throw  new BusinessException("会员等级不存在");
        }
        //修改上级账号
        memberAccountService.updateMemberAccount(entity,account);
        //钻石会员，修改所属城市合伙人对应的推广手机号
        if(levelEntity.getLevelNo().equals(MemberLevelEnum.DIAMOND.getValue())){
            Example example = new Example(UserEntity.class);
            example.createCriteria().andEqualTo("oemCode",entity.getOemCode())
                    .andEqualTo("bindingAccount",oldAccount)
                    .andGreaterThan("platformType","3")
                    .andLessThan("status","2");
            List<UserEntity> userEntityList = userService.selectByExample(example);
            if(userEntityList!= null && userEntityList.size()==1){
                UserEntity userEntity = userEntityList.get(0);
                userEntity.setBindingAccount(entity.getMemberAccount());
                userEntity.setUpdateTime(new Date());
                userEntity.setRemark("修改推广人手机号");
                userEntity.setUpdateUser(account);
                userService.editByIdSelective(userEntity);
            }
            memberOrderRelaService.updateOrderRelaUpDiamondAccountByUpDiamondAccountId(entity.getMemberAccount(),po.getRemark(),entity.getId());
            memberOrderRelaService.updateOrderRelaSuperDiamondAccountBySuperDiamondId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        }else if(levelEntity.getLevelNo().equals(MemberLevelEnum.MEMBER.getValue())){
            memberOrderRelaService.updateOrderRelaEmployeesAccountByEmployeesId(entity.getMemberAccount(),po.getRemark(),entity.getId());
            memberOrderRelaService.updateOrderRelaSuperEmployeesAccountBySuperDiamondId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        }
        //#会员订单关系表
        memberOrderRelaService.updateMemberAccountByFirstMemberId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        memberOrderRelaService.updateMemberAccountByTwoMemberId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        memberOrderRelaService.updateMemberAccountByThreeMemberId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        memberOrderRelaService.updateMemberAccountByFourMemberId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        memberOrderRelaService.updateOrderRelaParentMemberAccountByParentMemberId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        //#会员快照表
        memberSnapshotService.updateMemberAccountByMemberId(entity.getMemberAccount(),po.getRemark(),entity.getId());
        //#分润表
        profitsDetailService.updateMemberAccountByMemberId(entity.getMemberAccount(),po.getRemark(),entity.getId());
    }

    @Override
    public synchronized  void goWJOrderPush(String orderNo,Integer tradeType,Long memberId,String oemCode,Date completeTime){
        Thread t = Thread.currentThread();
        log.info("============================当前线程为："+t.getId());
        OrderEntity order = orderService.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }
        if (order.getChannelServiceId() == null || order.getChannelCode() == null){
            log.info("渠道编码或服务商id为空无需推送");
            orderMapper.updateOrderChannelPushState(order.getOrderNo(),ChannelPushStateEnum.CANCELLED.getValue());
        }else {
            // 订单推送状态改为推送中
/*            order.setChannelPushState(ChannelPushStateEnum.PAYING.getValue());
            orderMapper.updateOrderChannelPushState(order.getOrderNo(),order.getChannelPushState());*/
            Map<String,Object> dataParams = WJOrderPustParams(orderNo,tradeType,memberId,completeTime);
            OemParamsEntity oemParamsEntity =  oemParamsService.getParams(oemCode,26);
            String result = GuoJinUtil.gotoWJOrderPush(dataParams,order.getChannelCode(),oemParamsEntity.getSecKey(), oemParamsEntity.getUrl(),3);
            if ( result.equals("0000")){
                //  设置推送状态为2已推送
                order.setChannelPushState(ChannelPushStateEnum.TO_BE_AUDIT.getValue());
            }else{
                // 推送失败
                order.setChannelPushState(ChannelPushStateEnum.COMPLETED.getValue());
            }
            orderMapper.updateOrderChannelPushState(order.getOrderNo(),order.getChannelPushState());
        }
    }

    @Override
    public void orderPush(OrderQuery query) {
        // 查询判断是否有正在推送中的数据
        query.setChannelPushStates("1");
        List<OpenOrderVO> list =  mapper.listPushOrder(query);
        if (list !=null && list.size()>0 ){
            throw  new BusinessException("订单正在推送，请勿重复操作！");
        }
        // 查询推送失败的数据
        query.setChannelPushStates("3");
        List<OpenOrderVO> listToBePush =  mapper.listPushOrder(query);
        if (listToBePush == null || listToBePush.size()<=0 ){
            throw  new BusinessException("无需要推送的订单");
        }
        //  批量改成推送中
        orderMapper.batchUpdateOrderChannelPushStateByOrderNo(listToBePush,ChannelPushStateEnum.PAYING.getValue());
        //  推送国金
        rabbitTemplate.convertAndSend("orderPush", listToBePush);
    }

    //  调用国金订单推送接口需要参数组装
    public Map<String,Object> WJOrderPustParams(String orderNo,Integer tradeType,Long memberId, Date completeTime){
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }
        OrderEntity order = orderService.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }
        MemberOrderRelaEntity memberOrderRelaEntity = memberOrderRelaService.findById(order.getRelaId());
        Map<String,Object> dataParams = new HashMap<String,Object>();
        dataParams.put("busOrderNo",orderNo);
        dataParams.put("productCode",order.getChannelProductCode());
        dataParams.put("tradeType",tradeType);
        if (StringUtil.isNotBlank(member.getRealName())){
            dataParams.put("custName",member.getRealName());
        }else{
            dataParams.put("custName",member.getMemberName());
        }
        dataParams.put("custAccountPhone",member.getMemberPhone());
        dataParams.put("custAccountNo",member.getId());
        if (memberOrderRelaEntity.getLevelFirst() != null && memberOrderRelaEntity.getLevelFirst() == 1){
            dataParams.put("isVip",1);
            dataParams.put("vipFee",memberOrderRelaEntity.getLevelFirstProfitsRate().toString());
        }else{
            dataParams.put("isVip",0);
        }
        if (member.getParentMemberId() != null){
            dataParams.put("isDirect",0);
        }else{
            dataParams.put("isDirect",1);

        }
        //  开票订单取开票完成时间
        if(tradeType == 2 || tradeType == 5){
            // 查询开票订单信息
            InvoiceOrderEntity invOrder = invoiceOrderMapper.queryByOrderNo(orderNo);
            if (null == invOrder) {
                throw new BusinessException("未查询到开票订单");
            }
            //  云财数据库为分  转为元
            if (order.getOrderAmount() != null){
                double tradeAmt=order.getOrderAmount().doubleValue()/100;
                // 转string  国金不支持double
                dataParams.put("tradeAmt",String.valueOf(tradeAmt));
            }
        }
        if( completeTime == null){
             completeTime = order.getUpdateTime();
        }
        //  云财数据库为分  转为元
        if (order.getProfitAmount() != null){
            double achieveTradeAmt=order.getProfitAmount().doubleValue()/100;
            dataParams.put("achieveTradeAmt",String.valueOf(achieveTradeAmt));
        }
        dataParams.put("completeTime",DateUtil.formatTimesTampDate(completeTime));
        if (order.getChannelEmployeesId()!= null){
            dataParams.put("empolyeeId",order.getChannelEmployeesId());
        }
        if (order.getChannelServiceId()!= null){
            dataParams.put("agentUserId",order.getChannelServiceId());
        }
        if (order.getChannelUserId()!= null){
            dataParams.put("userId",order.getChannelUserId());
        }
        // V3.3新增
        //tradeType 为5   注册
        if (tradeType == 1){
            MemberCompanyEntity memberCompanyEntity = memberCompanyMapper.queryByOrderNo(orderNo);
            if (memberCompanyEntity != null){
                dataParams.put("companyName",memberCompanyEntity.getCompanyName());
                dataParams.put("companyId",memberCompanyEntity.getId());
            }
        }
        //  开票
        if (tradeType == 2 || tradeType == 5){
            InvoiceOrderEntity invOrder = invoiceOrderMapper.queryByOrderNo(orderNo);
            MemberCompanyEntity memberCompanyEntity = new MemberCompanyEntity();
            memberCompanyEntity.setId(invOrder.getCompanyId());
            memberCompanyEntity = memberCompanyMapper.selectOne(memberCompanyEntity);
            if (memberCompanyEntity != null){
                dataParams.put("companyName",memberCompanyEntity.getCompanyName());
                dataParams.put("companyId",memberCompanyEntity.getId());
            }
        }
        //  注销
        if (tradeType == 3){
            CompanyCancelOrderEntity companyCancelOrderEntity = companyCancelOrderMapper.queryByOrderNo(orderNo);
            MemberCompanyEntity memberCompanyEntity = new MemberCompanyEntity();
            memberCompanyEntity.setId(companyCancelOrderEntity.getCompanyId());
            memberCompanyEntity = memberCompanyMapper.selectOne(memberCompanyEntity);
            if (memberCompanyEntity != null){
                dataParams.put("companyName",memberCompanyEntity.getCompanyName());
                dataParams.put("companyId",memberCompanyEntity.getId());
            }
        }
        // 续费
        if (tradeType == 4){
            ContRegisterOrderEntity contRegisterOrderEntity = contRegisterOrderMapper.queryByOrderNo(orderNo);
            MemberCompanyEntity memberCompanyEntity = new MemberCompanyEntity();
            memberCompanyEntity.setId(contRegisterOrderEntity.getCompanyId());
            memberCompanyEntity = memberCompanyMapper.selectOne(memberCompanyEntity);
            if (memberCompanyEntity != null){
                dataParams.put("companyName",memberCompanyEntity.getCompanyName());
                dataParams.put("companyId",memberCompanyEntity.getId());
            }
        }
        return dataParams;
    }

    @Override
    public ThirdPartyQueryInoiveInfoDTO queryThirdPartyInvoiceInfo(ThirdPartyQueryInoiveInfoQuery query) {
        OemAccessPartyQuery oemAccessPartyQuery=new OemAccessPartyQuery();
        oemAccessPartyQuery.setAccessPartyCode(query.getAccessPartyCode());
        OemAccessPartyEntity oemAccessPartyEntity=oemAccessPartyService.findByCode(oemAccessPartyQuery);
        if(oemAccessPartyEntity==null){
            throw new BusinessException("接入方不存在");
        }
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(query.getUserId());
        if(memberAccountEntity==null){
            throw new BusinessException("用户不存在");
        }
        if(!Objects.equals(memberAccountEntity.getAccessPartyId(),oemAccessPartyEntity.getId())){
            throw new BusinessException("操作用户不属于该接入方");
        }

        ThirdPartyQueryInoiveInfoDTO thirdPartyQueryInoiveInfoDTO=new ThirdPartyQueryInoiveInfoDTO();
        thirdPartyQueryInoiveInfoDTO.setOemCode(query.getOemCode());
        thirdPartyQueryInoiveInfoDTO.setCompanyId(query.getCompanyId());
        thirdPartyQueryInoiveInfoDTO.setUserId(query.getUserId());
        thirdPartyQueryInoiveInfoDTO.setAccessPartyCode(query.getAccessPartyCode());
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(query.getCompanyId());
        if(memberCompanyEntity==null){
            throw new BusinessException("企业不存在");
        }
        if(!Objects.equals(memberAccountEntity.getId(),memberCompanyEntity.getMemberId()) ){
            throw new BusinessException("操作用户不属于该企业");
        }
        //增值税税率
        List<TaxRulesVatRateVO> taxRulesVatRateVOList =taxRulesConfigService.queryTaxRulesVatRate(memberCompanyEntity.getParkId(),memberCompanyEntity.getCompanyType(),null);
        thirdPartyQueryInoiveInfoDTO.setRateList(taxRulesVatRateVOList);
        //发票类型
        List<Integer> invoiceWayList=new ArrayList<>();
        CompanyTaxHostingEntity companyTaxHostingEntity=companyTaxHostingService.getCompanyTaxHostingByCompanyId(query.getCompanyId(),null);
        if(companyTaxHostingEntity!=null){
            invoiceWayList.add(2);
        }
        invoiceWayList.add(3);
        invoiceWayList.add(1);
        invoiceWayList.add(4);
        thirdPartyQueryInoiveInfoDTO.setInvoiceWayList(invoiceWayList);
        //开票类型
        List<Integer> invoiceTypeList=new ArrayList<>();
        invoiceTypeList.add(1);
        invoiceTypeList.add(2);
        thirdPartyQueryInoiveInfoDTO.setInvoiceTypeList(invoiceTypeList);
        //剩余开票限额
        MemberCompanyDetailVo companyDetail = memberCompanyService.getMemberCompanyDetail(query.getUserId(), query.getOemCode(), query.getCompanyId());
        if(null == companyDetail){
            throw new BusinessException("企业不存在");
        }
        //近12个月可开票金额
        thirdPartyQueryInoiveInfoDTO.setRemainInvoiceAmount(companyDetail.getRemainInvoiceAmount());
        //开票类目
        List<CompanyInvoiceCategoryJdVO> companyInvoiceCategoryEntities= companyInvoiceCategoryService.queryCompanyInvoiceCategoryJd(query.getCompanyId());
        thirdPartyQueryInoiveInfoDTO.setCategoryList(companyInvoiceCategoryEntities);
        //企业支持的税收分类编码
        List<String> taxCodes = memberCompanyService.findCompanyTaxCodeByCompanyId(query.getCompanyId());
        thirdPartyQueryInoiveInfoDTO.setTaxCodes(taxCodes);
        //最小开票金额
        String rechargeAmountMinLimit=dictionaryService.getByCode("recharge_amount_min_limit").getDictValue();
        thirdPartyQueryInoiveInfoDTO.setRechargeAmountMinLimit(Long.parseLong(rechargeAmountMinLimit));
        thirdPartyQueryInoiveInfoDTO.setEin(companyDetail.getEin());
        return thirdPartyQueryInoiveInfoDTO;
    }

    @Override
    public void preCheckOfRegOrder(String idCardNumber) {
        if (StringUtil.isBlank(idCardNumber)) {
            throw new BusinessException("身份证号码不能为空");
        }
        // 校验经营者年龄
        String year = idCardNumber.substring(6, 10);
        boolean numeric = StringUtils.isNumeric(year);
        if (!numeric) {
            throw new BusinessException("身份证号码格式有误");
        }
        String age = null;
        age = new BigDecimal(DateUtil.getYear(new Date())).subtract(new BigDecimal(year)).toString();
        DictionaryEntity rangeOfOperator = dictionaryService.getByCode("age_range_of_operator");
        if (null == rangeOfOperator) {
            throw new BusinessException("未配置经营者年龄范围");
        }
        boolean inTheInterval = IntervalUtil.isInTheInterval(age, rangeOfOperator.getDictValue());
        if (!inTheInterval) {
            throw new BusinessException("经营者年龄不符合注册要求");
        }
    }

    /**
     * 组装字节跳动支付参数
     * @param userId
     * @param oemCode
     * @param payNo
     * @param amount
     * @param orderNo
     * @return
     * @throws BusinessException
     * @throws UnknownHostException
     */
    @Override
    public BytedancePayDto buildBytedanceParams(Long userId, String oemCode, String payNo, Long amount,String orderNo) throws BusinessException {
        //读取字节跳动支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 30);
        if (null == paramsEntity) {
            throw new BusinessException("未配置渠道微信支付相关信息！");
        }
        // 组装参数对象
        BytedancePayDto payDto = new BytedancePayDto();
        payDto.setAppId(paramsEntity.getAccount());
        payDto.setAppSecret(paramsEntity.getSecKey());
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        payDto.setPaySalt(params.getString("paySalt"));
        payDto.setAmount(amount);
        payDto.setTradeNo(payNo);
        payDto.setOemCode(oemCode);
        // callbackUrl回调地址
        DictionaryEntity dic = this.dictionaryService.getByCode("bytedancePayNotifyUrl");
        if (null == dic) {
            throw new BusinessException("未配置字节跳动支付回调通知地址!");
        }
        payDto.setNotifyUrl(dic.getDictValue());
        // 获取微信openId
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if (StringUtils.isBlank(member.getOpenId())) {
            throw new BusinessException("支付失败，用户openId为空");
        }
        payDto.setOpenId(member.getOpenId());
        payDto.setOrderNo(orderNo);
        OrderEntity orderEntity = this.queryByOrderNo(orderNo);
        if(orderEntity == null){
            throw new BusinessException("支付失败，未找到订单信息");
        }
        if(StringUtils.isNotBlank(orderEntity.getProductName())) {
            payDto.setSubject(orderEntity.getProductName());
            payDto.setBody(orderEntity.getProductName());
        }else{
            payDto.setSubject("充值");
            payDto.setBody("充值");
        }
        payDto.setUserId(userId);
        payDto.setOrderType(orderEntity.getOrderType());
        return payDto;
    }

    @Override
    public Map<String,Object> createRegisterOrder(Long userId, AccessPartyRegisterOrderDTO orderDto) throws BusinessException {
        // 费用承担方为接入方时，承担方标识必填
        if (Objects.equals(2, orderDto.getIsSelfPaying()) && StringUtil.isBlank(orderDto.getPayerName())) {
            throw new BusinessException("费用承担方不能为空");
        }

        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(userId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        if (!MemberAuthStatusEnum.AUTH_SUCCESS.getValue().equals(member.getAuthStatus())) {
            throw new BusinessException("用户未实名");
        }

        // 查询园区
        ParkEntity park = Optional.ofNullable(parkService.findById(orderDto.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区信息"));

        // 查询行业
        IndustryInfoVO industry = Optional.ofNullable(industryService.getById(orderDto.getIndustryId(), orderDto.getParkId())).orElseThrow(() -> new BusinessException("未查询到行业信息"));
        // 校验行业经营范围
        if (!orderDto.getIndustryBusinessScope().equals(industry.getBusinessContent())) {
            throw new BusinessException("行业经营范围不匹配");
        }
        // 注册订单经营范围
        String[] industryBusinessScope = industry.getBusinessContent().split(";");
        Set<String> businessScopes = Sets.newHashSet(industryBusinessScope);
        // 校验用户自选经营范围
        if (!StringUtil.isBlank(orderDto.getOwnBusinessScope())) {
            this.checkParkBusinessScope(orderDto.getOwnBusinessScope(), park.getId(), 1);
            String[] split = orderDto.getOwnBusinessScope().split(";");
            businessScopes.addAll(Sets.newHashSet(split));
        }

        // 完善注册参数
        // 默认为自己办理
        orderDto.setIsOther(0);
        // 注册名称拼接
        orderDto.setRegisteredName(industry.getExampleName().replace("***", orderDto.getShopName()));

        // 经营范围
        orderDto.setBusinessScope(String.join(";", businessScopes));
        // 核定税种
        List<RatifyTaxEntity> ratifyTaxs = Optional.ofNullable(ratifyTaxService.listRatifyTax(orderDto.getIndustryId())).orElseThrow(() -> new BusinessException("未查询到核定税种"));
        orderDto.setRatifyTax(ratifyTaxs.get(0).getTaxName());
        // 产品id
        ProductEntity product = Optional.ofNullable(productService.queryProductByProdType(ProductTypeEnum.INDIVIDUAL.getValue(), orderDto.getOemCode(), null)).orElseThrow(() -> new BusinessException("未查询到产品信息"));
        if (!ProductStatusEnum.ON_SHELF.getValue().equals(product.getStatus())) {
            throw new BusinessException("产品不可用");
        }
        orderDto.setProductId(product.getId());

        RegisterOrderDTO registerOrderDTO = new RegisterOrderDTO();
        ObjectUtil.copyObject(orderDto, registerOrderDTO);
        registerOrderDTO.setIsSupplyShopName(1);
        registerOrderDTO.setCompanyType(MemberCompanyTypeEnum.INDIVIDUAL.getValue());
        registerOrderDTO.setOperatorName(member.getRealName());
        registerOrderDTO.setIdCardNumber(member.getIdCardNo());
        registerOrderDTO.setIdCardFront(member.getIdCardFront());
        registerOrderDTO.setIdCardReverse(member.getIdCardBack());
        registerOrderDTO.setIdCardAddr(member.getIdCardAddr());
        registerOrderDTO.setExpireDate(member.getExpireDate());
        registerOrderDTO.setContactPhone(member.getMemberPhone());
        registerOrderDTO.setIsAllCodes(orderDto.getIsAllCodes());
        registerOrderDTO.setIndustryBusinessScope(orderDto.getIndustryBusinessScope());
        registerOrderDTO.setOwnBusinessScope(orderDto.getOwnBusinessScope());
        registerOrderDTO.setTaxCodeBusinessScope(orderDto.getTaxCodeBusinessScope());
        if (orderDto.getIsSelfPaying() == 2) {
            registerOrderDTO.setPayType(2);
        } else if (orderDto.getIsSelfPaying() != 1) {
            throw new BusinessException("“是否自费”数据非法");
        }
        // 为商品明细列表添加数据
        if (null != registerOrderDTO.getMerchandises() && !registerOrderDTO.getMerchandises().isEmpty()) {
            Date date = new Date();
            registerOrderDTO.setAddTime(date);
            registerOrderDTO.setAddUser(member.getMemberAccount());
            for (int i = 0; i < registerOrderDTO.getMerchandises().size(); i++) {
                RegisterOrderGoodsDetailRelaEntity goodsDetailEntity = registerOrderDTO.getMerchandises().get(i);
                goodsDetailEntity.setAddTime(date);
                goodsDetailEntity.setAddUser(member.getMemberAccount());
                goodsDetailEntity.setUpdateTime(date); // 用于区分同步赋码后新增经营范围
                goodsDetailEntity.setUpdateUser(member.getMemberAccount());
            }
        }

        // 创建订单
        registerOrderDTO.setTaxpayerType(CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
        String orderNo = this.createIndustryOrder(userId, registerOrderDTO);
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderNo);
        // 获取订单服务费
        OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单"));
        map.put("payAmount", orderEntity.getPayAmount());
        map.put("orderAmount", orderEntity.getOrderAmount());
        map.put("orderStatus", orderEntity.getOrderStatus());
        return map;
    }

    /**
     * 校验经营范围在园区是否存在
     * @param businessScopes
     * @param parkId
     * @param businessScopeType 1-用户自选经营范围 2-商品匹配经营范围
     */
    private void checkParkBusinessScope(String businessScopes, Long parkId, int businessScopeType) {
        HashSet<String> set = Sets.newHashSet(businessScopes.split(";"));

        List<String> list;
        if (businessScopeType == 1) {
            ParkBusinessScopeQuery query = new ParkBusinessScopeQuery();
            query.setParkId(parkId);
            query.setBusinessScopes(set);
            PageResultVo<String> parkBusinessScope = parkBusinessScopeService.findByParkId(query);
            list = parkBusinessScope.getList();
        } else {
            list = businessScopeTaxCodeService.checkByBusiness(businessScopes);
        }
        HashSet<String> set1 = Sets.newHashSet(list);
        set.removeAll(set1);
        if (set.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : set) {
                stringBuilder.append(";").append(s);
            }
            stringBuilder.delete(0,1);
            throw new BusinessException((businessScopeType == 1 ? "自选经营范围【" : "商品匹配经营范围【") + stringBuilder + "】不存在");
        }
    }

    @Override
    public OrderVoidInfo checkToVoid(String orderNo) {
        InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
        List<CorporateAccountWithdrawalOrderEntity> corporateList = corporateAccountWithdrawalOrderService.queryWithdrawOrderByInvoiceOrderNo(orderNo);

        if (CollectionUtil.isNotEmpty(corporateList)){
            throw new BusinessException("该订单已提现");
        }
        if (invoiceOrderEntity == null){
            throw new BusinessException("未查询到开票信息，无法作废/红冲");
        }
        if (!invoiceOrderEntity.getCreateWay().equals(InvoiceCreateWayEnum.ONESELF.getValue())){
            throw new BusinessException("非自助开票，无法作废/红冲");
        }
        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(invoiceOrderEntity.getCompanyId());
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException("未查询到企业，无法作废/红冲");
        }
        //校验企业托管费过期状态（1-正常 2-即将过期 3-已过期）
        if (Objects.equals(company.getOverdueStatus(), MemberCompanyOverdueStatusEnum.OVERDUE.getValue())){
            throw new BusinessException("企业已过期，无法作废/红冲");
        }
        // 校验公司状态(状态：1->正常；2->禁用；4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("公司状态异常，无法作废/红冲");
        }
        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("未查询到园区");
        }
        //状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        if (park.getStatus() == 2) {
            throw new BusinessException("企业所在园区已停止使用，如有疑问，请联系客服！");
        }
        if (park.getStatus() == 3) {
            throw new BusinessException("企业所在园区已暂停使用，如有疑问，请联系客服！");
        }
        // 查询oem机构信息
        OemEntity oem = oemService.getOem(company.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }
        OrderVoidInfo orderVoidInfo = orderMapper.getToVoidInfo(orderNo);
        if(orderVoidInfo!=null){
            orderVoidInfo.setOemCode(oem.getOemCode());
            orderVoidInfo.setTaxpayerType(invoiceOrderEntity.getTaxpayerType());
        }
        InvoiceCategoryBaseEntity invoiceCategoryBaseEntity = invoiceCategoryBaseService.findById(invoiceOrderEntity.getCategoryId());
        if (invoiceCategoryBaseEntity.getGoodsName().equals("*")){
            //  用户自定义开票类目
            orderVoidInfo.setCategoryType(2);
        }else{
            // 系统开票类目
            orderVoidInfo.setCategoryType(1);
        }
        return orderVoidInfo;
    }

    @Override
    public void addFreezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser) {
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        OemEntity oem = oemService.getOem(entity.getOemCode());
        record.setAddTime(new Date());
        record.setAddUser(updateUser);
        record.setCapitalAccountId(entity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.INCOME.getValue());
        record.setChangesBeforeAmount(entity.getTotalAmount());
        record.setChangesAfterAmount(entity.getTotalAmount() + amount);
        record.setOemCode(entity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(entity.getUserId());
        record.setUserType(entity.getUserType());
        record.setWalletType(entity.getWalletType());
        userCapitalChangeRecordMapper.insertSelective(record);

        UserCapitalAccountDTO dto = new UserCapitalAccountDTO();
        dto.setId(entity.getId());
        dto.setAddTotalAmount(amount);
        dto.setAddAvailableAmount(amount);
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(updateUser);
        userCapitalAccountMapper.updateAmount(dto);
    }

    @Override
    public List<String> getOderNoByCompanyAndOrderStatus(Long companyId) {
        return mapper.getOderNoByCompanyAndOrderStatus(companyId);
    }

    @Override
    public List<String> getOderNoByCompany(Long companyId) {
        return mapper.getOderNoByCompany(companyId);
    }

    @Override
    public String queryConsumptionWithdrawExplain(String oemCode) {
        // 查询机构
        OemEntity oemEntity = Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));
        return oemEntity.getConsumptionWithdrawExplain();
    }

    @Override
    public Long monthlyWithdrawalAmount(Long memberId, int year, int month) {
        return mapper.monthlyWithdrawalAmount(memberId, year, month);
    }

    @Override
    public Long usableWithdrawAmount(Long userId, String oemCode) {
        // 查询用户信息
        MemberAccountEntity member = memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("未查询到用户信息");
        }

        // 查询机构消费钱包提现充值天数限制
        OemEntity oem = oemService.getOem(oemCode);
        if (null == oem) {
            throw new BusinessException("未查询到机构信息");
        }
        if (null == oem.getRechargeDays()) {
            throw new BusinessException("未配置机构消费钱包提现相关配置");
        }

        // 查询用户消费钱包
        UserCapitalAccountEntity capital = userCapitalAccountService.queryByUserIdAndType(userId, 1, member.getOemCode(), WalletTypeEnum.CONSUMER_WALLET.getValue());
        if (null == capital) {
            throw new BusinessException("未查询到用户消费钱包");
        }

        // 查询未达充值天数的充值金额
        Long amount = orderService.queryRechargeAmountOfDays(userId, oem.getRechargeDays());
        Long usableWithdrawAmount = capital.getAvailableAmount() - amount;
        if (usableWithdrawAmount < 0L) {
            usableWithdrawAmount = 0L;
        }
        return usableWithdrawAmount;
    }

    @Override
    public Long queryRechargeAmountOfDays(Long userId, Integer rechargeDays) {
        return mapper.queryRechargeAmountOfDays(userId, rechargeDays);
    }

    private void handleYiShuiPayResult(OrderEntity orderEntity, PayWaterEntity payWaterEntity) {
        FastIssuingQueryResp resp = null;
        try {
            resp = yishuiService.payResultQuery(orderEntity.getOemCode(), orderEntity.getOrderNo());
        } catch (BusinessException e) {
            log.info("调用易税查询支付结果失败" + e.getMessage());
            return;
        }

        if (null == resp || !YiShuiBaseResp.SUCCESS.equals(resp.getCode())) {
            log.info("查询易税支付结果异常" + resp.getMsg());
            return;
        }

        // 支付状态 0 待审核 1 打款中 2打款失败 3拒绝打款 4打款成功
        if (Objects.equals(resp.getPayment_status(), 4)) {
            //订单状态
            orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
            orderEntity.setUpdateTime(new Date());
            orderEntity.setUpdateUser("xxljob");
            //流水状态
            payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            payWaterEntity.setUpdateUser("xxljob");
            payWaterEntity.setUpdateTime(new Date());
            return;
        } else if (Objects.equals(resp.getPayment_status(), 2) || Objects.equals(resp.getPayment_status(), 3)) {
            //订单状态
            orderEntity.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
            orderEntity.setRemark(resp.getPay_err_msg());
            orderEntity.setUpdateTime(new Date());
            orderEntity.setUpdateUser("xxljob");
            //流水状态
            payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
            payWaterEntity.setUpStatusCode("9999");
            payWaterEntity.setUpResultMsg(resp.getPay_err_msg());
            payWaterEntity.setUpdateUser("xxljob");
            payWaterEntity.setUpdateTime(new Date());
        } else {
            return;
        }
    }
}