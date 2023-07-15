package com.yuqian.itax.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.dto.UserWithdrawDTO;
import com.yuqian.itax.capital.enums.CapitalChangeTypeEnum;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.capital.service.UserCapitalChangeRecordService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.corporateaccount.entity.*;
import com.yuqian.itax.corporateaccount.enums.CorporateAccountApplyOrderStatusEnum;
import com.yuqian.itax.corporateaccount.service.*;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueRecordStatusEnum;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.group.dao.GroupPaymentAnalysisRecordMapper;
import com.yuqian.itax.group.dao.InvoiceOrderGroupMapper;
import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.enums.InvoiceOrderGroupStatusEnum;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.dao.InvoiceOrderChangeRecordMapper;
import com.yuqian.itax.order.dao.InvoiceOrderMapper;
import com.yuqian.itax.order.dao.OrderMapper;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.query.InvOrderQuery;
import com.yuqian.itax.order.entity.query.InvWaterOrderQuery;
import com.yuqian.itax.order.entity.query.InvoiceWaterOrderApiQuery;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.entity.*;
import com.yuqian.itax.park.entity.query.TaxRulesVatRateQuery;
import com.yuqian.itax.park.entity.vo.TaxRulesConfigVO;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.park.enums.*;
import com.yuqian.itax.park.service.*;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.entity.query.PayWaterQuery;
import com.yuqian.itax.pay.entity.vo.PaywaterVO;
import com.yuqian.itax.pay.enums.*;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ChargeStandardService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.tax.entity.CompanyTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillQuery;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillQueryAdmin;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.CompanyTaxBillListVO;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.entity.vo.TaxCalculationVO;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.service.CompanyTaxBillChangeService;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.dao.*;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.query.CompanyListApiQuery;
import com.yuqian.itax.user.entity.query.CompanyListQuery;
import com.yuqian.itax.user.entity.query.ExtendUserQuery;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.*;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.*;
import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.workorder.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 开票订单service impl
 *
 * @author yejian
 * @Date: 2019年12月07日 20:05:12
 */
@Slf4j
@Service("invoiceOrderService")
public class InvoiceOrderServiceImpl extends BaseServiceImpl<InvoiceOrderEntity, InvoiceOrderMapper> implements InvoiceOrderService {

    @Autowired
    InvoiceRecordService invoiceRecordService;
    @Resource
    private CompanyInvoiceRecordMapper companyInvoiceRecordMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserExtendMapper userExtendMapper;
    @Resource
    private MemberAccountMapper memberAccountMapper;
    @Resource
    private InvoiceOrderChangeRecordMapper invoiceOrderChangeRecordMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private InvoiceOrderChangeRecordService invoiceOrderChangeRecordService;
    @Autowired
    private PayWaterService payWaterService;
    @Autowired
    private MemberOrderRelaService memberOrderRelaService;
    @Autowired
    private CompanyInvoiceRecordService companyInvoiceRecordService;
    @Autowired
    private CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private ChargeStandardService chargeStandardService;
    @Autowired
    private TaxPolicyService taxPolicyService;
    @Autowired
    private TaxRulesConfigService taxRulesConfigService;
    @Autowired
    private OemService oemService;
    @Autowired
    private UserRelaService userRelaService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private UserCapitalChangeRecordService userCapitalChangeRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private AgentProfitsRulesService agentProfitsRulesService;
    @Autowired
    private ReceiveOrderService receiveOrderService;
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private ProfitsDetailService profitsDetailService;
    @Autowired
    private UserExtendService userExtendService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private LogisCompanyService logisCompanyService;
    @Autowired
    private DictionaryService sysDictionaryService;
    @Autowired
    private CompanyCancelOrderService companyCancelOrderService;
    @Autowired
    private CompanyCancelOrderChangeRecordService companyCancelOrderChangeRecordService;
    @Resource
    private MemberCompanyMapper memberCompanyMapper;
    @Resource
    private GroupPaymentAnalysisRecordMapper groupPaymentAnalysisRecordMapper;
    @Resource
    private InvoiceOrderGroupMapper invoiceOrderGroupMapper;
    @Autowired
    private CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private CityService cityService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private DistrictService districtService;
    @Autowired
    private OssService ossService;
    @Autowired
    private CorporateAccountApplyOrderService corporateAccountApplyOrderService;
    @Autowired
    private CorporateAccountApplyOrderChangeService corporateAccountApplyOrderChangeService;
    @Autowired
    private MemberConsumptionRecordService memberConsumptionRecordService;
    @Autowired
    private BusinessIncomeRuleService businessIncomeRuleService;
    @Autowired
    private CompanyTaxBillService companyTaxBillService;
    @Resource
    private CompanyTaxHostingMapper companyTaxHostingMapper;
    @Resource
    private ContRegisterOrderService contRegisterOrderService;
    @Resource
    private ContRegisterOrderChangeService contRegisterOrderChangeService;
    @Resource
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Resource
    private InvoiceServiceFeeDetailService invoiceServiceFeeDetailService;
    @Resource
    private ParkEndtimeConfigService parkEndtimeConfigService;
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private OemConfigService oemConfigService;
    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;
    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private RegisterOrderChangeRecordService registerOrderChangeRecordService;
    @Autowired
    private ParkBusinessAddressRulesService parkBusinessAddressRulesService;
    @Resource
    private CouponsIssueRecordService couponsIssueRecordService;
    @Resource
    private CouponsService couponsService;
    @Autowired
    private CorporateAccountContOrderService corporateAccountContOrderService;
    @Autowired
    private CorporateAccountContOrderChangeService corporateAccountContOrderChangeService;
    @Autowired
    private LogisticsInfoService logisticsInfoService;
    @Autowired
    private InvoiceorderGoodsdetailRelaService invoiceorderGoodsdetailRelaService;
    @Autowired
    private OemAccessPartyService oemAccessPartyService;
    @Autowired
    private CompanyTaxHostingService companyTaxHostingService;
    @Autowired
    private CorporateAccountWithdrawalOrderService corporateAccountWithdrawalOrderService;
    @Autowired
    private CompanyTaxBillChangeService companyTaxBillChangeService;
    @Autowired
    private ClassificationCodeVatService classificationCodeVatService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private ProductParkRelaService productParkRelaService;
    @Autowired
    private ParkCorporateAccountConfigService parkCorporateAccountConfigService;

    @Override
    public InvoiceOrderEntity queryByOrderNo(String orderNo) {
        return mapper.queryByOrderNo(orderNo);
    }

    @Override
    public InvCourierVo queryCourierByOrderNo(String orderNo) {
        return mapper.queryCourierByOrderNo(orderNo);
    }

    @Override
    public List<InvoiceOrderVO> listInvoiceOrder(Long memberId, String oemCode, InvOrderQuery query) {
        // 查询V4.0历史订单划定时间点配置
        String pointTime = dictionaryService.getValueByCode("historical_order_point_in_time");

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return mapper.listInvoiceOrder(memberId, oemCode, query.getCompanyId(), query.getMonth(), query.getType(), pointTime);
    }

    @Override
    @Transactional
    public void ticket(InvoiceOrderEntity invEntity, String userAccount, Long memberId) {
        Date date = new Date();
        Date confirmInvoiceTime = invEntity.getConfirmInvoiceTime();
        if(confirmInvoiceTime == null){
            confirmInvoiceTime = invEntity.getAddTime();
        }
        //修改开票实体
        int year = DateUtil.getYear(confirmInvoiceTime);
        invEntity.setTaxYear(year);
        invEntity.setTaxSeasonal(Integer.parseInt(DateUtil.getQuarter(confirmInvoiceTime)));
        invEntity.setUpdateTime(date);
        invEntity.setUpdateUser(userAccount);
        mapper.updateByPrimaryKey(invEntity);

        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invEntity, record);
        record.setId(null);
        record.setAddTime(date);
        record.setAddUser(userAccount);
        //集团开票，不修改开票订单状态
        if(Objects.equals(invEntity.getCreateWay(),InvoiceCreateWayEnum.GROUP.getValue())){
            record.setOrderStatus(InvoiceOrderStatusEnum.IN_TICKETING.getValue());
        }else{
            record.setOrderStatus(InvoiceOrderStatusEnum.TO_BE_SHIPPED.getValue());
            //电票，开票记录完成后，将状态改成已完成v3.1 add ni.jiang
            if(ObjectUtils.equals(invEntity.getInvoiceWay(),2)){
                MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invEntity.getCompanyId());
                confirmReceipt(memberCompanyEntity.getMemberId(), invEntity.getOemCode(), invEntity.getOrderNo(),userAccount);
            }else{
                orderMapper.updateOrderStatus(invEntity.getOrderNo(), InvoiceOrderStatusEnum.TO_BE_SHIPPED.getValue(), userAccount, date);
            }
        }
        invoiceOrderChangeRecordService.insertSelective(record);

        // 接入方需要推送数据
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(memberId)).orElseThrow(() -> new BusinessException("未查询到开票用户信息"));
        if (null != member.getAccessPartyId()) {
            // 回调参数
            HashMap<String, Object> map = new HashMap<>();
            map.put("callbackType", 2); //回调类型 1-取消 2-出票 3-发货 4-完成
            map.put("orderNo", invEntity.getOrderNo());
            // 获取图片并转为base64
            //  获取图片的base64
            String[] imgList = invEntity.getInvoiceImgs().split(",");
            List<String> invImgList = Lists.newArrayList();
            for (String s : imgList) {
                String img = ImageUtils.netImageToBase64(ossService.getPrivateImgUrl(s));
                if (img.contains("\r\n")) {
                    img = img.replace("\r\n", "");
                } else if (img.contains("\r")) {
                    img = img.replace("\r", "");
                } else if (img.contains("\n")) {
                    img = img.replace("\n", "");
                }
                String suffix = s.substring(s.lastIndexOf(".") + 1);
                invImgList.add("data:image/" + suffix + ";base64," + img);
            }
            map.put("invImgList", invImgList);
            // 发送推送消息
            invoiceOrderService.accessPartyPush(invEntity.getOrderNo(), invEntity.getOemCode(), member.getAccessPartyId(), map);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String createInvoiceOrder(Long memberId, String oemCode, CreateInvoiceOrderDTO entity, String sourceType, boolean isResubmit) throws BusinessException {

        // 发票类型为增值税专用发票，抬头信息完整性校验
        if (InvoiceTypeEnum.REGISTER.getValue().equals(entity.getInvoiceType()) &&
                (StringUtil.isBlank(entity.getCompanyAddress()) || StringUtil.isBlank(entity.getPhone())
                        || StringUtils.isBlank(entity.getBankName()) || StringUtils.isBlank(entity.getBankNumber()))
        ) {
            throw new BusinessException("发票抬头信息不完整");
        }

        int invoiceAmountLimit = Integer.parseInt(sysDictionaryService.getByCode("invoice_amount_limit").getDictValue());
        // 开票金额最低1000起
        if (entity.isApi() && entity.getInvoiceAmount() < invoiceAmountLimit) {
            throw new BusinessException("单次最小开票" + invoiceAmountLimit + "元哦");
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
        //校验企业托管费过期状态（1-正常 2-即将过期 3-已过期）
        if (Objects.equals(company.getOverdueStatus(),MemberCompanyOverdueStatusEnum.OVERDUE.getValue())){
            throw new BusinessException("企业已过期，无法开票");
        }
        // 校验公司状态(状态：1->正常；2->禁用；4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("公司状态异常，无法开票");
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException("公司开票已满额，无法开票");
        }
        // 校验未支付订单
        List<OrderNoVO> unpaidList = invoiceOrderService.getUnpaidList(memberId, oemCode, company.getId());
        if (null != unpaidList && !unpaidList.isEmpty()) {
            throw new BusinessException("存在未支付订单");
        }
        // 校验是否存在“待出款”状态订单
        List<String> invoiceOrderEntities = this.findByOrderStatus(company.getId(), InvoiceOrderStatusEnum.WAIT_FOR_PAYMENT.getValue());
        if (CollectionUtil.isNotEmpty(invoiceOrderEntities)) {
            throw new BusinessException("存在待出款的佣金开票订单");
        }
        // 校验是否存在非取消状态的注销订单
        List<ComCancelOrderVO> cancelList = companyCancelOrderService.queryByCompanyId(company.getId());
        if (CollectionUtil.isNotEmpty(cancelList)) {
            List<ComCancelOrderVO> collect = cancelList.stream().filter(x -> !CompCancelOrderStatusEnum.CANCELED.getValue().equals(x.getOrderStatus())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect) && 1 == park.getIncomeLevyType().intValue()) {
                throw new BusinessException("存在待付款的企业注销订单");
            }
        }
        // 校验是否存在超时未确认成本税单
        PendingTaxBillQuery pendingTaxBillQuery = new PendingTaxBillQuery();
        pendingTaxBillQuery.setEin(company.getEin());
        pendingTaxBillQuery.setCompanyId(company.getId());
        pendingTaxBillQuery.setStatusRange(1);
        List<PendingTaxBillVO> pendingTaxBillVOS = companyTaxBillService.pendingTaxBill(pendingTaxBillQuery);
        if (CollectionUtil.isNotEmpty(pendingTaxBillVOS)) {
            List<PendingTaxBillVO> collect = pendingTaxBillVOS.stream().filter(x -> x.getTimeDifference() < 0).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                throw new BusinessException("该企业存在超时未确认成本的税单");
            }
        }
        //校验税单
        String overtime = dictionaryService.getValueByCode("tax_bill_overtime");
        if (StringUtils.isNotBlank(overtime)) {
            Integer time = Integer.valueOf(overtime);
            if (time > 0) {
                CompanyTaxBillQuery query = new CompanyTaxBillQuery();
                query.setCompanyId(company.getId());
                query.setMemberId(company.getMemberId());
                query.setOemCode(company.getOemCode());
                query.setTaxBillStatus(TaxBillStatusEnum.TAX_TO_BE_PAID.getValue());
                query.setOverTime(time);
                List<CompanyTaxBillListVO> bills = companyTaxBillService.listCompanyTaxBill(query);
                long count = bills.stream().filter(u -> StringUtils.isNotBlank(u.getOverTimeDesc())).count();
                if (count > 0) {
                    throw new BusinessException(ResultConstants.TAX_BILL_OVER_TIME);
                }
            }
        }
        // 查询企业开票额度
        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(entity.getCompanyId());
        if (CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue().equals(company.getTaxpayerType())) { // 一般纳税人无限额配置，不进行校验
            if (null == companyInvRecord) {
                throw new BusinessException("未查询到企业开票记录");
            } else if (null == companyInvRecord.getRemainInvoiceAmount()) {
                throw new BusinessException("未查询到企业年度可开票金额");
            }
            InvoiceStatisticsViewVO view = Optional.ofNullable(mapper.queryCompanyInvoiceRecordStatisticsView(memberId, entity.getCompanyId())).orElse(new InvoiceStatisticsViewVO());
            TaxPolicyEntity taxPolicyEntity = Optional.ofNullable(taxPolicyService.queryTaxPolicyByParkId(company.getParkId(), company.getCompanyType(),company.getTaxpayerType())).orElseThrow(() -> new BusinessException("园区税费政策不存在"));
            Long totalInvoiceAmount = taxPolicyEntity.getTotalInvoiceAmount();
            if (totalInvoiceAmount == null) {
                throw new BusinessException("园区税费政策年度开票总额未配置");
            }
            if ((totalInvoiceAmount - view.getUseTotalInvoiceAmount()) < entity.getInvoiceAmount()) {
                throw new BusinessException("开票金额不能大于近12个月剩余可开票额度");
            }

            // 计算本月累计开票金额
            String monFirstDay = DateUtil.getMonFirstDay();
            Date start = DateTime.parse(monFirstDay).toDate();
            String monLastDay = DateUtil.getMonLastDay();
            Date end = DateTime.parse(monLastDay).toDate();
            List<CountPeriodInvoiceAmountVO> monthList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long monthInvoiceAmount = 0L;
            if (!monthList.isEmpty() && null != monthList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : monthList) {
                    monthInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getMonthInvoiceAmount() != null && monthInvoiceAmount + entity.getInvoiceAmount() > taxPolicyEntity.getMonthInvoiceAmount()){
                throw new BusinessException("本月累计开票金额不能超过"+taxPolicyEntity.getMonthInvoiceAmount()/100 + "元");
            }

            // 计算本周期累计开票金额
            int year =  DateUtil.getYear(new Date());
            int quarter = Integer.parseInt(DateUtil.getQuarter());
            String[]  s = DateUtil.getCurrQuarter(year, quarter);
            start = DateTime.parse(s[0]).toDate();
            end = DateTime.parse(s[1]).toDate();
            List<CountPeriodInvoiceAmountVO> perList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long periodInvoiceAmount = 0L;
            if (!perList.isEmpty() && null != perList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : perList) {
                    periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getQuarterInvoiceAmount() != null && periodInvoiceAmount + entity.getInvoiceAmount() > taxPolicyEntity.getQuarterInvoiceAmount()){
                throw new BusinessException("本季累计开票金额不能超过"+taxPolicyEntity.getQuarterInvoiceAmount()/100 + "元");
            }
        }
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }
        if (null != member.getAccessPartyId()) {
            throw new BusinessException("该通道不支持接入方用户开票");
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
        MemberOrderRelaEntity more = getUserTree(memberId, oemCode, 2);//获取一二级推广人和分润信息
        if (more != null) {
            more.setMemberId(memberId);
            more.setMemberLevel(memberLevel.getLevelNo());//会员等级
            more.setOemCode(oemCode);
            more.setOemName(oem.getOemName());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            memberOrderRelaService.insertSelective(more);
        }

        // 修改企业开票额度
        if (null == companyInvRecord.getUseInvoiceAmount()) {
            companyInvRecord.setUseInvoiceAmount(entity.getInvoiceAmount());//年度已开票金额
        } else {
            companyInvRecord.setUseInvoiceAmount(companyInvRecord.getUseInvoiceAmount() + entity.getInvoiceAmount());//年度已开票金额
        }
        companyInvRecord.setRemainInvoiceAmount(companyInvRecord.getRemainInvoiceAmount() - entity.getInvoiceAmount());//年度可开票金额
        companyInvoiceRecordService.editByIdSelective(companyInvRecord);

        // 查询产品id
        ProductEntity product = new ProductEntity();
        product.setOemCode(oemCode);
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(memberId);

        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setUserId(memberId);
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.INVOICE.getValue());
        mainOrder.setOrderStatus(InvoiceOrderStatusEnum.CREATED.getValue());
        mainOrder.setProductId(product.getId());
        mainOrder.setProductName(product.getProdName());
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setOemCode(oemCode);
        mainOrder.setParkId(company.getParkId());
        mainOrder.setOrderAmount(entity.getInvoiceAmount());
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(member.getMemberAccount());
        // 默认设置为消费钱包
        if (null == entity.getWalletType()) {
            mainOrder.setWalletType(1);
        } else {
            mainOrder.setWalletType(entity.getWalletType());
        }
        if (StringUtil.isNotBlank(sourceType)) {
            mainOrder.setSourceType(Integer.parseInt(sourceType));
        } else {
            mainOrder.setSourceType(0);
        }
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        //  添加用户映射id
        mainOrder.setChannelUserId(member.getChannelUserId());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(mainOrder.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }

        /**
         * 保存开票服务费阶段
         */
        MemberCompanyDetailVo memberCompanyDetail = memberCompanyService.getMemberCompanyDetail(memberId, oemCode, company.getId());
        // 计算服务费明细
        List<InvoiceServiceFeeDetailVO> list = invoiceServiceFeeDetailService.countServiceDetail(memberId,company.getIndustryId(),company.getParkId(),product.getProdType(), entity.getInvoiceAmount(), memberCompanyDetail.getUseInvoiceAmountYear(), oemCode);
        if(list == null || list.size()<1){
            list = invoiceServiceFeeDetailService.countServiceDetail(memberId, company.getId(), product.getId(), entity.getInvoiceAmount(), memberCompanyDetail.getUseInvoiceAmountYear(), oemCode);
        }
        if(list!=null &&list.size()>0){
            for (InvoiceServiceFeeDetailVO invoiceServiceFeeDetail : list) {
                // 保存服务费数据
                InvoiceServiceFeeDetailEntity serviceFeeDetailEntity = new InvoiceServiceFeeDetailEntity();
                serviceFeeDetailEntity.setOrderNo(orderNo);
                serviceFeeDetailEntity.setCompanyId(company.getId());
                serviceFeeDetailEntity.setOemCode(oemCode);
                serviceFeeDetailEntity.setAddTime(new Date());
                serviceFeeDetailEntity.setAddUser(member.getMemberAccount());
                serviceFeeDetailEntity.setPhaseAmount(invoiceServiceFeeDetail.getPhaseAmount());
                serviceFeeDetailEntity.setFeeRate(invoiceServiceFeeDetail.getFeeRate());
                serviceFeeDetailEntity.setFeeAmount(invoiceServiceFeeDetail.getFeeAmount());
                invoiceServiceFeeDetailService.insertSelective(serviceFeeDetailEntity);
            }
        }else{
            throw new BusinessException("未找到开票服务费配置");
        }
        Long feeAmount = list.stream().mapToLong(InvoiceServiceFeeDetailVO::getFeeAmount).sum();
        //设置会员优惠价
        Long serviceFeeDis = 0L;
        if (memberLevel != null && !Objects.equals(memberLevel.getLevelNo(),MemberLevelEnum.NORMAL.getValue()) && !Objects.equals(memberLevel.getLevelNo(),MemberLevelEnum.MEMBER.getValue())) {
            // 查询会员消费折扣
            MemberProfitsRulesEntity memberProfitsRules = new MemberProfitsRulesEntity();
            memberProfitsRules.setUserLevel(memberLevel.getLevelNo());
            memberProfitsRules.setOemCode(member.getOemCode());
            memberProfitsRules = memberProfitsRulesService.selectOne(memberProfitsRules);
            if (null == memberProfitsRules) {
                throw new BusinessException("未查询到会员分润规则");
            }
            // 计算服务费折扣
            serviceFeeDis = (new BigDecimal(feeAmount).multiply(memberProfitsRules.getConsumptionDiscount().divide(new BigDecimal(100)))).setScale(0, BigDecimal.ROUND_DOWN).longValue();
        }
        mainOrder.setDiscountAmount(serviceFeeDis);
        //保存人群标签id
        Long crowdLabelId = memberCrowdLabelRelaService.getCrowLabelIdByMemberId(member.getId(),member.getOemCode());
        if(crowdLabelId!=null){
            mainOrder.setCrowdLabelId(crowdLabelId);
        }

        try{
            orderService.insertSelective(mainOrder);
        }catch (Exception e){
            throw new BusinessException("数据格式错误");
        }

        //查询企业对公户
        CompanyCorporateAccountEntity companyCorporateAccountEntity = companyCorporateAccountService.queryCorpByCompanyId(company.getId());

        // 保存开票订单信息
        InvoiceOrderEntity invoice = transferInvoiceOrderDto2Entity(entity);
        invoice.setOemCode(oemCode);
        invoice.setOrderNo(orderNo);
        invoice.setAddTime(new Date());
        invoice.setAddUser(member.getMemberAccount());
        invoice.setTaxpayerType(company.getTaxpayerType());
        if (Objects.nonNull(companyCorporateAccountEntity)){
            invoice.setCorporateAccount(companyCorporateAccountEntity.getCorporateAccount());
            invoice.setCorporateAccountBankName(companyCorporateAccountEntity.getCorporateAccountBankName());
        }
        invoice.setServiceFee(feeAmount);
        invoice.setServiceFeeDiscount(serviceFeeDis);
        if (isResubmit) {
            // 重新提交的开票订单字段处理
            invoice.setIsRefundPostageFee(0); // 是否已退邮寄费 0-未退 1-已退
            invoice.setInvoiceMark(0); // 发票标识 0-正常 1-已作废/红冲 2-作废重开
        }
        try{
            invoiceOrderService.insertSelective(invoice);
        }catch (Exception e){
            throw new BusinessException("数据格式错误");
        }

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.CREATED.getValue());
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setRemark("创建开票订单");
        if (Objects.nonNull(companyCorporateAccountEntity)) {
            record.setCorporateAccount(companyCorporateAccountEntity.getCorporateAccount());
            record.setCorporateAccountBankName(companyCorporateAccountEntity.getCorporateAccountBankName());
        }
        invoiceOrderChangeRecordService.insertSelective(record);

        // 计算支付金额
        PayInformationVO payAmountVO = invoiceOrderService.getInvoicePayInfo(memberId, oemCode, orderNo, entity.getVatRate());
        TaxFeeDetailVO taxFeeDetail = payAmountVO.getTaxFeeDetail();
        //开票类目来自企业开票类目表对应的基础类目库id
        CompanyInvoiceCategoryEntity e = new CompanyInvoiceCategoryEntity();
        e.setCategoryBaseId(entity.getCategoryId());
        e.setCompanyId(company.getId());
        CompanyInvoiceCategoryEntity companyInvoiceCategoryEntity = companyInvoiceCategoryService.selectOne(e);
        if (companyInvoiceCategoryEntity == null) {
            throw new BusinessException("企业开票类目不存在");
        }
        if (!Objects.equals(companyInvoiceCategoryEntity.getCompanyId(), company.getId())) {
            throw new BusinessException("开票类目不属于当前企业");
        }
        invoice.setCategoryId(companyInvoiceCategoryEntity.getCategoryBaseId());
        //开票类目以**结尾替换最后一个*号为goodsName
        String categoryName = entity.getCategoryName();
        String goodsName = entity.getGoodsName();
        if (StringUtils.endsWith(categoryName,"**") && StringUtil.isNotBlank(goodsName)){
            categoryName = StringUtils.overlay(categoryName, goodsName, categoryName.length()-1, categoryName.length());
        }
        invoice.setCategoryName(categoryName);
        invoice.setVatFee(taxFeeDetail.getPayableVatFee());
        invoice.setVatFeeRate(taxFeeDetail.getVatFeeRate().divide(new BigDecimal(100)));
        invoice.setSurcharge(taxFeeDetail.getPayableSurcharge());
        invoice.setSurchargeRate(taxFeeDetail.getSurchargeRate().divide(new BigDecimal(100)));
        invoice.setPaidVatFee(taxFeeDetail.getPaidVatFee());
        invoice.setPaidSurcharge(taxFeeDetail.getPaidSurcharge());
        invoice.setPeriodInvoiceAmount(taxFeeDetail.getPeriodInvoiceAmount());
        invoice.setHistoricalInvoiceAmount(payAmountVO.getHistoricalInvoiceAmount());
        invoice.setTaxpayerType(taxFeeDetail.getTaxpayerType());
        if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            invoice.setPersonalIncomeTax(taxFeeDetail.getPayableIncomeTax());
            invoice.setPersonalIncomeTaxRate(taxFeeDetail.getIncomeTaxRate().divide(new BigDecimal(100)));
            invoice.setPaidIncomeTax(taxFeeDetail.getPaidIncomeTax());
            invoice.setPaidIncomeTaxYear(taxFeeDetail.getPaidIncomeTaxYear());
        } else {
            invoice.setStampDutyRate(taxFeeDetail.getStampDutyRate());
            invoice.setStampDutyAmount(taxFeeDetail.getStampDutyAmount());
            invoice.setWaterConservancyFundRate(taxFeeDetail.getWaterConservancyFundRate());
            invoice.setWaterConservancyFundAmount(taxFeeDetail.getWaterConservancyFundAmount());
            invoice.setVatBreaksCycle(taxFeeDetail.getVATBreaksCycle());
            invoice.setSurchargeBreaksCycle(taxFeeDetail.getSurchargeBreaksCycle());
            invoice.setStampDutyBreaksCycle(taxFeeDetail.getStampDutyBreaksCycle());
            invoice.setWaterConservancyFundBreaksCycle(taxFeeDetail.getWaterConservancyFundBreaksCycle());
            invoice.setIncomeTaxBreaksCycle(taxFeeDetail.getIncomeTaxBreaksCycle());
        }
        // 所得税为应税所得率征收方式时才有应税所得率
        TaxPolicyEntity policyEntity = taxPolicyService.queryTaxPolicyByParkId(park.getId(), company.getCompanyType(),company.getTaxpayerType());
        if (LevyWayEnum.TAXABLE_INCOME_RATE.getValue().equals(policyEntity.getLevyWay()) && MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            invoice.setTaxableIncomeRate(taxFeeDetail.getTaxableIncomeRate().divide(new BigDecimal(100)));
        }
        invoice.setPayAmount(payAmountVO.getPayAmount());
        invoice.setPostageFees(payAmountVO.getPostageFees());

        // 自助开票创建开票订单时无需收件信息或邮件
        if (null == entity.getCreateWay() || entity.getCreateWay() != 1) {
            //保存收件信息调整，根据发票类型
            if (Objects.equals(invoice.getInvoiceWay(), InvoiceWayEnum.PAPER.getValue())) {
                invoice.setRecipient(Optional.ofNullable(entity.getRecipient()).orElseThrow(() -> new BusinessException("收件人姓名不能为空")));
                invoice.setRecipientPhone(Optional.ofNullable(entity.getRecipientPhone()).orElseThrow(() -> new BusinessException("收件人联系电话不能为空")));
                invoice.setRecipientAddress(Optional.ofNullable(entity.getRecipientAddress()).orElseThrow(() -> new BusinessException("收件人详细地址不能为空")));
                invoice.setProvinceCode(Optional.ofNullable(entity.getProvinceCode()).orElseThrow(() -> new BusinessException("收件人省编码不能为空")));
                invoice.setProvinceName(Optional.ofNullable(entity.getProvinceName()).orElseThrow(() -> new BusinessException("收件人省名称不能为空")));
                invoice.setCityCode(Optional.ofNullable(entity.getCityCode()).orElseThrow(() -> new BusinessException("收件人市编码不能为空")));
                invoice.setCityName(Optional.ofNullable(entity.getCityName()).orElseThrow(() -> new BusinessException("收件人市名称不能为空")));
                invoice.setDistrictCode(Optional.ofNullable(entity.getDistrictCode()).orElseThrow(() -> new BusinessException("收件人区编码不能为空")));
                invoice.setDistrictName(Optional.ofNullable(entity.getDistrictName()).orElseThrow(() -> new BusinessException("收件人区名称不能为空")));
            } else {
                // 校验并保存收票邮箱 V2.7
                String email = Optional.ofNullable(entity.getEmail()).orElseThrow(() -> new BusinessException("收票邮箱不能为空"));
                Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
                if (!pattern.matcher(email).matches()) {
                    throw new BusinessException("收票邮箱格式有误");
                }
                invoice.setEmail(email);
                // 修改用户默认的收票邮箱 V2.7
                member.setEmail(email);
                member.setUpdateUser(member.getMemberAccount());
                member.setUpdateTime(new Date());
                member.setRemark("修改收票邮箱");
                memberAccountMapper.updateByPrimaryKeySelective(member);
            }
        }
        invoice.setGoodsName(entity.getGoodsName());
        invoice.setInvoiceRemark(entity.getInvoiceRemark());
        try{
            invoiceOrderService.editByIdSelective(invoice);
        }catch (Exception ee){
            throw new BusinessException("数据格式错误");
        }
        //修改主订单的支付金额
        mainOrder.setPayAmount(payAmountVO.getPayAmount());
        orderService.editByIdSelective(mainOrder);
        return orderNo;
    }

    /**
     *  查询个体户指定状态订单
     * @param companyId
     * @param orderStatus
     * @return
     */
    public List<String> findByOrderStatus(Long companyId, Integer orderStatus) {
        return mapper.queryByOrderStatus(companyId, orderStatus);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Map<String, Object> confirmInvoiceOrder(Long memberId, String oemCode, ConfirmInvoiceOrderDTO entity) throws BusinessException {
        Map<String, Object> result = new HashMap<String, Object>();

        // 查询机构
        OemEntity oem = Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询订单主表信息
        OrderEntity mainOrder = orderMapper.queryByOrderNo(entity.getOrderNo());
        if (!Objects.equals(mainOrder.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        // 查询开票订单信息
        InvoiceOrderEntity invoice = mapper.queryByOrderNo(entity.getOrderNo());
        // 校验开票金额是否大于一万元
        if (mainOrder.getOrderAmount() >= 1000000 && StringUtils.isBlank(invoice.getGroupOrderNo())) {
            // 校验是否开票后补流水
            if (null == entity.getIsAfterUploadBankWater()) {
                throw new BusinessException("请选择是否先开票后补流水");
            }
            // 校验成果类型
            if (null == entity.getIsAfterAchievement()) {
                throw new BusinessException("请选择是否先开票后补交易成果");
            }
            // 如果先上传流水再开票，校验银行流水
            if (1 == entity.getIsAfterUploadBankWater()) {
                if (StringUtils.isBlank(entity.getAccountStatement())) {
                    throw new BusinessException("开票金额一万元及以上请上传银行流水截图");
                }
            }
            // 如果先上传流水再开票，校验交易成果
            if (1 == entity.getIsAfterAchievement()) {
                if (StringUtils.isBlank(entity.getAchievementImgs())) {
                    throw new BusinessException("开票金额一万元及以上请上传交易成果");
                }
            }
            // 校验交易成果截图是否超过20张
            if (StringUtils.isNotBlank(entity.getAchievementImgs())) {
                String[] chievementImgsArray = entity.getAchievementImgs().split(",");
                if (chievementImgsArray.length > 20) {
                    throw new BusinessException("交易成果截图个数超过最大限制");
                }
            }

            // 校验银行流水截图是否超过20张
            if (StringUtils.isNotBlank(entity.getAccountStatement())) {
                String[] accountStatementArray = entity.getAccountStatement().split(",");
                if (accountStatementArray.length > 20) {
                    throw new BusinessException("银行流水截图个数超过最大限制");
                }
            }
            // 业务合同必传
            if (StringUtil.isBlank(entity.getBusinessContractImgs())) {
                throw new BusinessException("开票金额一万元及以上请上传业务合同");
            }
            // 校验业务合同截图是否超过20张
            String[] businessContractImgsArray = entity.getBusinessContractImgs().split(",");
            if (businessContractImgsArray.length > 20) {
                throw new BusinessException("业务合同截图个数超过最大限制");
            }
        } else {
            entity.setIsAfterUploadBankWater(1);
            entity.setAchievementStatus(0);
        }

        // 校验重算标识
        if (Objects.equals(0, invoice.getIsRecalculateServiceFee())) {
            throw new BusinessException("税费计算发生变化，请重新进入该页");
        }

        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(invoice.getCompanyId());
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }
        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("公司状态异常，无法开票");
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException("公司开票已满额，无法开票");
        }



        // 修改订单主表信息
        mainOrder.setOrderStatus(InvoiceOrderStatusEnum.UNPAID.getValue());
        if (mainOrder.getPayAmount() == 0L) {
            mainOrder.setOrderStatus(InvoiceOrderStatusEnum.TO_PAYMENT_REVIEW.getValue());
        }
        mainOrder.setUpdateUser(member.getMemberAccount());
        mainOrder.setUpdateTime(new Date());
        if (invoice.getCreateWay().equals(InvoiceCreateWayEnum.GROUP.getValue())) {
            mainOrder.setDiscountAmount(0L);
            mainOrder.setPayAmount(0L);
            mainOrder.setServiceFee(0L);
        }
        orderService.editByIdSelective(mainOrder);

        // 修改开票订单信息
        //invoice.setCategoryId(entity.getCategoryId());

        // 自助开票时，收件信息或邮箱必填
        if (null != entity.getCreateWay() && entity.getCreateWay() == 1) {
            //保存收件信息调整，根据发票类型
            if (Objects.equals(invoice.getInvoiceWay(), InvoiceWayEnum.PAPER.getValue())) {
                invoice.setRecipient(Optional.ofNullable(entity.getRecipient()).orElseThrow(() -> new BusinessException("收件人姓名不能为空")));
                invoice.setRecipientPhone(Optional.ofNullable(entity.getRecipientPhone()).orElseThrow(() -> new BusinessException("收件人联系电话不能为空")));
                invoice.setRecipientAddress(Optional.ofNullable(entity.getRecipientAddress()).orElseThrow(() -> new BusinessException("收件人详细地址不能为空")));
                invoice.setProvinceCode(Optional.ofNullable(entity.getProvinceCode()).orElseThrow(() -> new BusinessException("收件人省编码不能为空")));
                invoice.setProvinceName(Optional.ofNullable(entity.getProvinceName()).orElseThrow(() -> new BusinessException("收件人省名称不能为空")));
                invoice.setCityCode(Optional.ofNullable(entity.getCityCode()).orElseThrow(() -> new BusinessException("收件人市编码不能为空")));
                invoice.setCityName(Optional.ofNullable(entity.getCityName()).orElseThrow(() -> new BusinessException("收件人市名称不能为空")));
                invoice.setDistrictCode(Optional.ofNullable(entity.getDistrictCode()).orElseThrow(() -> new BusinessException("收件人区编码不能为空")));
                invoice.setDistrictName(Optional.ofNullable(entity.getDistrictName()).orElseThrow(() -> new BusinessException("收件人区名称不能为空")));
            } else {
                // 校验并保存收票邮箱 V2.7
                String email = Optional.ofNullable(entity.getEmail()).orElseThrow(() -> new BusinessException("收票邮箱不能为空"));
                Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
                if (!pattern.matcher(email).matches()) {
                    throw new BusinessException("收票邮箱格式有误");
                }
                invoice.setEmail(email);
                // 修改用户默认的收票邮箱 V2.7
                member.setEmail(email);
                member.setUpdateUser(member.getMemberAccount());
                member.setUpdateTime(new Date());
                member.setRemark("修改收票邮箱");
                memberAccountMapper.updateByPrimaryKeySelective(member);
            }
        }

        if(entity.getIsAfterAchievement()!=null&&entity.getIsAfterAchievement()==1){
            invoice.setAchievementImgs(entity.getAchievementImgs());
            invoice.setAchievementVideo(entity.getAchievementVideo());
        }
        if(entity.getIsAfterUploadBankWater()!=null&&entity.getIsAfterUploadBankWater()==1){
            invoice.setAccountStatement(entity.getAccountStatement());
        }
        invoice.setRemark(entity.getRemark());
        invoice.setUpdateUser(member.getMemberAccount());
        invoice.setUpdateTime(new Date());
        invoice.setBankWaterStatus(4);
        invoice.setAchievementStatus(1);
        if (null != entity.getIsAfterUploadBankWater()) {
            if (0 == entity.getIsAfterUploadBankWater()) {
                invoice.setBankWaterStatus(BankWaterStatusEnum.TO_BE_UPLOAD.getValue());
            }
        }
        if (null != entity.getIsAfterAchievement()) {
            if (0 == entity.getIsAfterAchievement()) {
                invoice.setAchievementStatus(2);
            }
        }

        invoice.setIsAfterUploadBankWater(entity.getIsAfterUploadBankWater());
        invoice.setBusinessContractImgs(entity.getBusinessContractImgs());
        if (invoice.getCreateWay().equals(InvoiceCreateWayEnum.GROUP.getValue())) {
            invoice.setPayAmount(0L);
            invoice.setServiceFeeDiscount(0L);
            invoice.setServiceFee(0L);
            invoice.setPostageFees(0L);
        }
        if(StringUtil.isNotBlank(entity.getSupplementExplain())){
            invoice.setSupplementExplain(entity.getSupplementExplain());
        }
        try{
            if(StringUtils.isNotBlank(invoice.getBusinessContractImgs())&&invoice.getBusinessContractImgs().indexOf("oss-cn-hangzhou.aliyuncs.com")>=0){
                invoice.setBusinessContractImgs(invoice.getBusinessContractImgs().replaceAll("http://itax-private.oss-cn-hangzhou.aliyuncs.com/",""));
            }
            if(StringUtils.isNotBlank(invoice.getAccountStatement())&&entity.getIsAfterUploadBankWater()==1
                    &&invoice.getAccountStatement().indexOf("oss-cn-hangzhou.aliyuncs.com")>=0){
                invoice.setAccountStatement(invoice.getAccountStatement().replaceAll("http://itax-private.oss-cn-hangzhou.aliyuncs.com/",""));
            }
            if(StringUtils.isNotBlank(invoice.getAchievementImgs())&&entity.getIsAfterAchievement()==1
                    &&invoice.getAchievementImgs().indexOf("oss-cn-hangzhou.aliyuncs.com")>=0){
                invoice.setAchievementImgs(invoice.getAchievementImgs().replaceAll("http://itax-private.oss-cn-hangzhou.aliyuncs.com/",""));
            }
            if(StringUtils.isNotBlank(invoice.getAchievementVideo())&&entity.getIsAfterAchievement()==1
                    &&invoice.getAchievementVideo().indexOf("oss-cn-hangzhou.aliyuncs.com")>=0){
                invoice.setAchievementVideo(invoice.getAchievementVideo().replaceAll("http://itax-private.oss-cn-hangzhou.aliyuncs.com/",""));
            }
            invoiceOrderService.editByIdSelective(invoice);
        }catch (Exception e){
            throw new BusinessException("数据格式错误");
        }

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtil.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.UNPAID.getValue());
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setRemark("确认开票订单");
        if (mainOrder.getPayAmount() == 0L && !invoice.getCreateWay().equals(InvoiceCreateWayEnum.GROUP.getValue())) {
            record.setOrderStatus(InvoiceOrderStatusEnum.TO_PAYMENT_REVIEW.getValue());
            record.setRemark("确认开票订单，订单支付金额为0");
        }
        invoiceOrderChangeRecordService.insertSelective(record);

        // 查询产品
        ProductEntity product = new ProductEntity();
        product.setOemCode(member.getOemCode());
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);//查询产品id
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        result.put("orderNo", entity.getOrderNo());
        result.put("payAmount", invoice.getPayAmount());
        result.put("goodsName", product.getProdName());

        // 非个体支付金额为0时，直接变为待审核
        if (!MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType()) && mainOrder.getPayAmount() == 0L) {
            InvOrderPayDTO payDTO = new InvOrderPayDTO();
            payDTO.setOrderNo(entity.getOrderNo());
            payDTO.setGoodsName(product.getProdName());
            payDTO.setUpdateUser(member.getMemberAccount());
            HashMap<String, String> map = Maps.newHashMap();
            balancePayOrderOfInvoice(map, mainOrder, member, null, oem, payDTO, null, new PayWaterEntity());
            result.put("customerServicePhone", map.get("customerServicePhone"));
        }
        return result;
    }

//    @Override
//    public InvoiceOrderPayAmountVO countPayAmount(Long memberId, String oemCode, String orderNo, BigDecimal vatRate) throws BusinessException {
//        if (StrUtil.isEmpty(orderNo)) {
//            throw new BusinessException("订单号不能为空");
//        }
//
//        // 查询会员账号
//        MemberAccountEntity member = memberAccountService.findById(memberId);
//        if (null == member) {
//            throw new BusinessException("未查询到会员账号");
//        }
//
//        // 查询主订单
//        OrderEntity mainOrder = orderService.queryByOrderNo(orderNo);
//        if (null == mainOrder) {
//            throw new BusinessException("未查询到订单");
//        }
//
//        if (!Objects.equals(mainOrder.getUserId(), memberId)) {
//            throw new BusinessException("不是会员的订单");
//        }
//
//        // 查询开票订单
//        InvoiceOrderEntity invOrder = invoiceOrderService.queryByOrderNo(orderNo);
//        if (null == invOrder) {
//            throw new BusinessException("未查询到开票订单");
//        }
//
//        // 查询企业类型
//        MemberCompanyEntity company = memberCompanyService.findById(invOrder.getCompanyId());
//        if (null == company) {
//            throw new BusinessException("未查询到企业");
//        }
//
//        // 查询园区
//        ParkEntity park = new ParkEntity();
//        park.setId(mainOrder.getParkId());
//        park.setStatus(1);//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
//        park = parkService.selectOne(park);
//        if (null == park) {
//            throw new BusinessException("园区不存在或未上架");
//        }
//
//        // 计算增值税和个人所得税
//        InvoiceOrderPayAmountVO payAmountVO = calcTaxsAndFees(memberId, oemCode, invOrder.getInvoiceType(), mainOrder, company, park, vatRate);
//        payAmountVO.setInvoiceAmount(invOrder.getInvoiceAmount());//开票金额
//
//        // 查询产品
//        ProductEntity product = new ProductEntity();
//        product.setOemCode(member.getOemCode());
//        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
//        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
//        product = productService.selectOne(product);//查询产品id
//        if (null == product) {
//            throw new BusinessException("产品不存在或未上架");
//        }
//
//        // 根据产品查询收费标准
//        ChargeStandardEntity chargeStandard = new ChargeStandardEntity();
//        chargeStandard.setProductId(product.getId());
//        chargeStandard.setOemCode(member.getOemCode());
//        chargeStandard.setChargeType(1);
//        List<ChargeStandardEntity> chargeLevelList = chargeStandardService.select(chargeStandard);//获取阶梯服务费率
//        if (CollectionUtil.isEmpty(chargeLevelList)) {
//            throw new BusinessException("未查询到收费标准");
//        }
//
//        // 初始化服务费阶梯区间和税率区间
//        List<BigDecimal> charge = new LinkedList<BigDecimal>();
//        List<BigDecimal> price = new LinkedList<BigDecimal>();
//
//        // 查询本年开票订单
////        String invDate = DateUtil.format(new Date(),"yyyy");
////        List<InvoiceOrderEntity> invOrderYearList = mapper.InvOrderListOfYear(memberId, oemCode, park.getId(), company.getId(), invDate);
//
//        // 累加本年开票金额
////        BigDecimal allInvAmount = new BigDecimal(invOrderYearList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum());
//        InvoiceStatisticsViewVO view = Optional.ofNullable(mapper.queryCompanyInvoiceRecordStatisticsView(memberId, invOrder.getCompanyId())).orElse(new InvoiceStatisticsViewVO());
//        BigDecimal allInvAmount = new BigDecimal(view.getUseInvoiceAmountYear());
//        allInvAmount = allInvAmount.subtract(new BigDecimal(invOrder.getInvoiceAmount())); //减掉本次开票金额
//        // 获取服务费阶梯区间和税率区间
//        for (ChargeStandardEntity level : chargeLevelList) {
//            charge.add(new BigDecimal(level.getChargeMax()));
//            price.add(MoneyUtil.moneydiv(level.getChargeRate(), new BigDecimal("100")));
//        }
//
//        // 计算服务费
//        BigDecimal serviceFee = IntervalUtil.getMoney(allInvAmount, new BigDecimal(invOrder.getInvoiceAmount()), charge, price);
//        if (Objects.equals(invOrder.getCreateWay(),InvoiceCreateWayEnum.GROUP.getValue())){
//            serviceFee = BigDecimal.valueOf(0);
//        }
//
//        // 服务费向上取整
//        payAmountVO.setServiceFee(serviceFee.setScale(0, BigDecimal.ROUND_UP).longValue());
//
//        // 查询会员等级
//        MemberLevelEntity level = new MemberLevelEntity();
//        level.setId(member.getMemberLevel());
//        level.setOemCode(member.getOemCode());
//        level = memberLevelService.selectOne(level);
//        if (null == level) {
//            throw new BusinessException("未查询到一级推广人等级");
//        }
//
//        // 查询会员消费折扣
//        MemberProfitsRulesEntity memberProfitsRules = new MemberProfitsRulesEntity();
//        memberProfitsRules.setUserLevel(level.getLevelNo());
//        memberProfitsRules.setOemCode(member.getOemCode());
//        memberProfitsRules = memberProfitsRulesService.selectOne(memberProfitsRules);
//        if (null == memberProfitsRules) {
//            throw new BusinessException("未查询到会员分润规则");
//        }
//
//        // 计算服务费折扣
//        BigDecimal serviceFeeDis = MoneyUtil.moneyMul(new BigDecimal(payAmountVO.getServiceFee()), MoneyUtil.moneydiv(memberProfitsRules.getConsumptionDiscount(), new BigDecimal("100")));
//        // 服务费折扣向下取整
//        payAmountVO.setServiceFeeDiscount(serviceFeeDis.setScale(0, BigDecimal.ROUND_DOWN).longValue());
//
//        // 计算邮寄费金额
//        if (Objects.equals(InvoiceWayEnum.PAPER.getValue(), invOrder.getInvoiceWay())) {
//            payAmountVO.setPostageFees(park.getPostageFees());
//        } else {
//            payAmountVO.setPostageFees(0L);
//        }
//
//        // 累加支付金额
//        payAmountVO.setPayAmount(payAmountVO.getAllTax() + payAmountVO.getPostageFees() + (payAmountVO.getServiceFee() - payAmountVO.getServiceFeeDiscount()));
//        return payAmountVO;
//    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void cancelInvOrder(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主表订单信息
        OrderEntity order = orderService.queryByOrderNo(orderNo);
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

        // 查询开票订单信息
        InvoiceOrderEntity invOrder = mapper.queryByOrderNo(orderNo);
        if (null == invOrder) {
            throw new BusinessException("未查询到开票订单");
        }
        if (InvoiceMarkEnum.REOPEN.getValue().equals(invOrder.getInvoiceMark())) {
            throw new BusinessException("作废重开开票订单不允许取消");
        }

        // 开票：0->待创建；1->待付款；2->待审核；3->出票中；4->待发货；5->出库中；6->待收货；7->已签收；8->已取消
        // 如果订单状态为3-出票中 4-待发货 5-出库中 6-待收货 7-已签收则不能取消
        if (IntervalUtil.isInTheInterval(order.getOrderStatus().toString(), "[" + InvoiceOrderStatusEnum.IN_TICKETING.getValue().toString() + "," + InvoiceOrderStatusEnum.SIGNED.getValue().toString() + "]")) {
            throw new BusinessException("已出票的订单无法取消");
        } else if (order.getOrderStatus().equals(InvoiceOrderStatusEnum.CANCELED.getValue())) {
            throw new BusinessException("已取消的订单无法再次取消");
        } else {
            // 判断用户是否支付，如果已支付将支付金额退回可用余额，添加资金变动记录
            if (order.getOrderStatus().equals(InvoiceOrderStatusEnum.UNCHECKED.getValue())) {

                // 查询会员资金信息
                UserCapitalAccountEntity userCapital = this.userCapitalAccountService.queryByUserIdAndType(memberId, 1, oemCode, 1);
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
                userCapitalIncomeChange.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
                userCapitalIncomeChange.setOemCode(order.getOemCode());
                userCapitalIncomeChange.setChangesAmount(order.getPayAmount());
                userCapitalIncomeChange.setChangesBeforeAmount(beforeTotalAmount);//变动前余额
                userCapitalIncomeChange.setChangesAfterAmount(beforeTotalAmount + order.getPayAmount());//变动后余额
                userCapitalIncomeChange.setChangesType(CapitalChangeTypeEnum.THAW.getValue());
                userCapitalIncomeChange.setDetailDesc("取消开票订单解冻金额");
                userCapitalIncomeChange.setOrderType(6);
                userCapitalIncomeChange.setOrderNo(order.getOrderNo());
                userCapitalIncomeChange.setAddTime(new Date());
                userCapitalIncomeChange.setAddUser(member.getMemberAccount());
                userCapitalIncomeChange.setRemark(order.getProductName());
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
                water.setDiscountAmount(invOrder.getServiceFeeDiscount());
                water.setPayAmount(order.getPayAmount());
                water.setOrderType(6);
                water.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
                water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
                water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                water.setPayAccount(userCapital.getCapitalAccount());
                water.setAddTime(new Date());
                water.setAddUser(member.getMemberAccount());
                water.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
                water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行
                water.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
                payWaterService.insertSelective(water);
            }

            //回滚企业开票额度
            companyInvoiceRecordMapper.refund(invOrder.getCompanyId(),invOrder.getAddTime(),invOrder.getInvoiceAmount(),member.getMemberAccount(),new Date());

            // 修改主表订单状态
            order.setOrderStatus(InvoiceOrderStatusEnum.CANCELED.getValue());
            order.setUpdateUser(member.getMemberAccount());
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            // 新增开票订单变更记录
            InvoiceOrderChangeRecordEntity invOrderChange = new InvoiceOrderChangeRecordEntity();
            BeanUtils.copyProperties(invOrder, invOrderChange);
            invOrderChange.setId(null);
            invOrderChange.setOrderStatus(InvoiceOrderStatusEnum.CANCELED.getValue());
            invOrderChange.setAddUser(member.getMemberAccount());
            invOrderChange.setAddTime(new Date());
            invOrderChange.setRemark("取消开票订单");
            invoiceOrderChangeRecordService.insertSelective(invOrderChange);

            // 取消工单
            workOrderService.cancelWorkOrder(member.getMemberAccount(), orderNo, oemCode);
        }
    }

    @Override
    public InvoiceOrderSubpackageVO queryDetailByOrderNo(long memberId, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(memberId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        InvoiceOrderEntity invoiceOrder = Optional.ofNullable(invoiceOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到开票订单"));
        // 是否重需要算服务费
        if (Objects.equals(0, invoiceOrder.getIsRecalculateServiceFee())) {
            // 重算服务费
            OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
            ProductEntity product = Optional.ofNullable(productService.findById(orderEntity.getProductId())).orElseThrow(() -> new BusinessException("未查询到产品信息"));
            MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(invoiceOrder.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业信息"));
            MemberCompanyDetailVo memberCompanyDetail = memberCompanyService.getMemberCompanyDetail(memberId, member.getOemCode(), invoiceOrder.getCompanyId());
            // 计算服务费明细
            List<InvoiceServiceFeeDetailVO> list = invoiceServiceFeeDetailService.countServiceDetail(memberId, company.getIndustryId(), company.getParkId(), product.getProdType(), invoiceOrder.getInvoiceAmount(), memberCompanyDetail.getUseInvoiceAmountYear(), member.getOemCode());
            if(list == null || list.size()<1){
                list = invoiceServiceFeeDetailService.countServiceDetail(memberId, company.getId(), product.getId(), invoiceOrder.getInvoiceAmount(), memberCompanyDetail.getUseInvoiceAmountYear(), member.getOemCode());
            }
            if(list!=null &&list.size()>0) {
                // 删除服务费明细历史数据
                Example example = new Example(InvoiceServiceFeeDetailEntity.class);
                example.createCriteria().andEqualTo("orderNo", orderNo);
                invoiceServiceFeeDetailService.delByExample(example);
                for (InvoiceServiceFeeDetailVO invoiceServiceFeeDetail : list) {
                    // 保存服务费数据
                    InvoiceServiceFeeDetailEntity serviceFeeDetailEntity = new InvoiceServiceFeeDetailEntity();
                    serviceFeeDetailEntity.setOrderNo(orderNo);
                    serviceFeeDetailEntity.setCompanyId(company.getId());
                    serviceFeeDetailEntity.setOemCode(member.getOemCode());
                    serviceFeeDetailEntity.setAddTime(new Date());
                    serviceFeeDetailEntity.setAddUser(member.getMemberAccount());
                    serviceFeeDetailEntity.setPhaseAmount(invoiceServiceFeeDetail.getPhaseAmount());
                    serviceFeeDetailEntity.setFeeRate(invoiceServiceFeeDetail.getFeeRate());
                    serviceFeeDetailEntity.setFeeAmount(invoiceServiceFeeDetail.getFeeAmount());
                    invoiceServiceFeeDetailService.insertSelective(serviceFeeDetailEntity);
                }
            }
            // 重算税费
            PayInformationVO invoicePayInfo = invoiceOrderService.getInvoicePayInfo(memberId, invoiceOrder.getOemCode(), orderNo, invoiceOrder.getVatFeeRate().multiply(new BigDecimal("100")));
            TaxFeeDetailVO taxFeeDetail = invoicePayInfo.getTaxFeeDetail();
            // 更新订单主表
            orderEntity.setPayAmount(invoicePayInfo.getPayAmount());
            orderEntity.setDiscountAmount(invoicePayInfo.getServiceFeeDiscount());
            orderService.editByIdSelective(orderEntity);
            // 更新开票订单
            invoiceOrder.setIsRecalculateServiceFee(1);
            invoiceOrder.setVatFee(taxFeeDetail.getPayableVatFee());
            if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
                invoiceOrder.setPersonalIncomeTax(taxFeeDetail.getPayableIncomeTax());
                invoiceOrder.setPersonalIncomeTaxRate(taxFeeDetail.getIncomeTaxRate().divide(new BigDecimal("100")));
            } else {
                invoiceOrder.setStampDutyAmount(taxFeeDetail.getStampDutyAmount());
                invoiceOrder.setStampDutyRate(taxFeeDetail.getStampDutyRate().divide(new BigDecimal("100")));
                invoiceOrder.setWaterConservancyFundAmount(taxFeeDetail.getWaterConservancyFundAmount());
                invoiceOrder.setWaterConservancyFundRate(taxFeeDetail.getWaterConservancyFundRate().divide(new BigDecimal("100")));
            }
            invoiceOrder.setSurcharge(taxFeeDetail.getPayableSurcharge());
            invoiceOrder.setSurchargeRate(taxFeeDetail.getSurchargeRate().divide(new BigDecimal("100")));
            // 各税种申报周期
            invoiceOrder.setVatBreaksCycle(taxFeeDetail.getVATBreaksCycle());
            invoiceOrder.setSurchargeBreaksCycle(taxFeeDetail.getSurchargeBreaksCycle());
            invoiceOrder.setIncomeTaxBreaksCycle(taxFeeDetail.getIncomeTaxBreaksCycle());
            invoiceOrder.setStampDutyBreaksCycle(taxFeeDetail.getStampDutyBreaksCycle());
            invoiceOrder.setWaterConservancyFundBreaksCycle(taxFeeDetail.getWaterConservancyFundBreaksCycle());
            invoiceOrder.setServiceFee(invoicePayInfo.getServiceFee());
            invoiceOrder.setServiceFeeDiscount(invoicePayInfo.getServiceFeeDiscount());
            invoiceOrder.setPayAmount(invoicePayInfo.getPayAmount());
            invoiceOrder.setPostageFees(invoicePayInfo.getPostageFees());
            invoiceOrder.setUpdateTime(new Date());
            invoiceOrder.setUpdateUser(member.getMemberAccount());
            invoiceOrder.setPaidIncomeTaxYear(taxFeeDetail.getPaidIncomeTaxYear());
            invoiceOrder.setPaidVatFee(taxFeeDetail.getPaidVatFee());
            invoiceOrder.setPaidSurcharge(taxFeeDetail.getPaidSurcharge());
            invoiceOrder.setPaidIncomeTax(taxFeeDetail.getPaidIncomeTax());
            invoiceOrder.setPeriodInvoiceAmount(taxFeeDetail.getPeriodInvoiceAmount());
            invoiceOrder.setHistoricalInvoiceAmount(invoicePayInfo.getHistoricalInvoiceAmount());
            if (null != taxFeeDetail.getTaxableIncomeRate()) {
                invoiceOrder.setTaxableIncomeRate(taxFeeDetail.getTaxableIncomeRate().divide(new BigDecimal("100")));
            }
            invoiceOrderService.editByIdSelective(invoiceOrder);
            // 添加变更记录
            InvoiceOrderChangeRecordEntity changeRecordEntity = new InvoiceOrderChangeRecordEntity();
            ObjectUtil.copyObject(invoiceOrder, changeRecordEntity);
            changeRecordEntity.setOrderStatus(orderEntity.getOrderStatus());
            changeRecordEntity.setId(null);
            changeRecordEntity.setAddTime(new Date());
            changeRecordEntity.setAddUser(member.getMemberAccount());
            changeRecordEntity.setUpdateTime(null);
            changeRecordEntity.setUpdateUser(null);
            changeRecordEntity.setRemark("重算服务费");
            invoiceOrderChangeRecordService.insertSelective(changeRecordEntity);
        }

        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(invoiceOrder.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业"));

        TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(company.getParkId(), company.getCompanyType(),company.getTaxpayerType());

        InvoiceOrderSubpackageVO vo = mapper.querySubpackageByOrderNo(memberId, orderNo);
        TaxFeeDetailVO taxFeeDetail = vo.getTaxFeeDetail();
        taxFeeDetail.setVatFeeRate(taxFeeDetail.getVatFeeRate().multiply(new BigDecimal(100)));
        taxFeeDetail.setSurchargeRate(taxFeeDetail.getSurchargeRate().multiply(new BigDecimal(100)));
        taxFeeDetail.setIncomeTaxRate(taxFeeDetail.getIncomeTaxRate().multiply(new BigDecimal(100)));
        taxFeeDetail.setTaxableIncomeRate(taxFeeDetail.getTaxableIncomeRate().multiply(new BigDecimal(100)));
        taxFeeDetail.setLevyWay(taxPolicyEntity.getLevyWay());
        taxFeeDetail.setIncomeLevyType(taxPolicyEntity.getIncomeLevyType());
        vo.setTaxFeeDetail(taxFeeDetail);
        vo.setSupplementExplain(invoiceOrder.getSupplementExplain());

        // 添加服务费信息
        vo.setInvoiceServiceFeeDetail(invoiceOrderService.getInvServiceFeeDetail(orderNo));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Map<String,String> newBalancePayOrder(Long memberId, String oemCode, InvOrderPayDTO entity) throws BusinessException {
        //1、查询校验    1.会员账号；2.验证码；3.订单主表；4.oem机构；5.会员资金账号；6.对应订单表；7.支付中的流水
        //1.1 会员账户查询校验
        MemberAccountEntity member = Optional.ofNullable(this.memberAccountService.findById(memberId)).orElseThrow(() -> new BusinessException("未查询到员账号"));

        if(entity.getAmount()!=0){
            //1.2 验证码校验
            String verficationCode = redisService.get(RedisKey.SMS_WALLET_BALANCE_PAY_KEY_SUFFER + member.getMemberAccount());
            // 校验验证码是否错误或过期
            if (verficationCode == null || "".equals(verficationCode) || !entity.getVerifyCode().equals(verficationCode)) {
                throw new BusinessException(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
            }
        }
        //1.3 查询校验————订单主表
        OrderEntity order = Optional.of(orderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到订单"));

        if (!Objects.equals(order.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        //1.4 oem机构
        OemEntity oem = oemService.getOem(oemCode);
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }

        //1.5 会员资金账号
        UserCapitalAccountEntity userCapital = new UserCapitalAccountEntity();
        userCapital.setUserType(MemberTypeEnum.MEMBER.getValue());//用户类型 1-会员 2 -系统用户
        userCapital.setUserId(memberId);
        userCapital.setStatus(CustomerStatusEnum.NORMAL.getValue());//状态 0-禁用 1-可用
        userCapital.setOemCode(oemCode);
        userCapital.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());//钱包类型 1-消费钱包 2-佣金钱包
        userCapital = userCapitalAccountService.selectOne(userCapital);
        //Optional.ofNullable(userCapital).orElseThrow(()->new BusinessException("会员资金账户不存在或被禁用"));
        if(userCapital==null){
            throw new BusinessException("会员资金账户不存在或被禁用");
        }

        //1.7 支付中的流水
        PayWaterQuery payWaterQuery = new PayWaterQuery();
        payWaterQuery.setOrderNo(order.getOrderNo());
        payWaterQuery.setPayStatus(1);
        List<PaywaterVO> paywaterVOS = payWaterService.listPayWater(payWaterQuery);
        if (null != paywaterVOS && !paywaterVOS.isEmpty()) {
            throw new BusinessException("该订单存在支付中的流水");
        }

        //2、数据准备
        UserCapitalChangeRecordEntity userCapitalBlockChange = new UserCapitalChangeRecordEntity();
        PayWaterEntity water = new PayWaterEntity();
        HashMap<String, String> map = Maps.newHashMap();

        //4、分类操作
        if (userCapital.getAvailableAmount() < order.getPayAmount()) {
            throw new BusinessException("可用余额不足，请先充值!!!");
        }

        //支付完成后续操作
        switch (order.getOrderType()){
            //订单类型 5-个体户注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 13-补税 15-托管费续费 16-对公户续费
            case 5 :
                balancePayOrderOfRegCompany(map,order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 6 :
                balancePayOrderOfInvoice(map,order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 7 :
                balancePayOrderOfUpgrade(order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 8 :
                balancePayOrderOfCancellation(order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 9 :
                balancePayOrderOfCompresapply(order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 10 :
                balancePayOrderOfCorporateApply(order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 13 :
                balancePayOrderOfRecoverableTax(order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 15 :
                balancePayOrderOfCustodyFeeRenewal(map,order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
            case 16 :
                balancePayOrderOfCorporateAccountRenew(map,order,member,userCapital,oem,entity,userCapitalBlockChange,water);
                break;
        }

        // 释放redis锁
        redisService.delete(RedisKey.SMS_WALLET_BALANCE_PAY_KEY_SUFFER + member.getMemberAccount());

            return map;
    }

    /**
     * 对公户续费订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private Map<String, String> balancePayOrderOfCorporateAccountRenew(HashMap<String, String> map, OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        String oemCode = order.getOemCode();
        Date date = new Date();

        //一、查询校验
        //1.1 参数校验——商品名称校验
        //Optional.ofNullable(entity.getGoodsName().isEmpty()).orElseThrow(() -> new BusinessException("商品名称不能为空"));
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        //1.2 对公户续费订单
        CorporateAccountContOrderEntity contOrderEntity = new CorporateAccountContOrderEntity();
        contOrderEntity.setOrderNo(entity.getOrderNo());
        contOrderEntity = Optional.ofNullable(corporateAccountContOrderService.selectOne(contOrderEntity)).orElseThrow(() -> new BusinessException("未查询到托管费续费订单"));
        //1.3 订单校验
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), ContOrderStatusEnum.TO_BE_PAY.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        }

        try {
            //二、资金操作（冻结）
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);

            //四、支付完成后操作
            //4.1 支付成功，更新主表状态为“已完成”
            order.setOrderStatus(ContOrderStatusEnum.COMPLETED.getValue());
            order.setUpdateUser(member.getMemberAccount());
            order.setUpdateTime(date);
            orderMapper.updateByPrimaryKeySelective(order);
            //4.2 添加续费订单变更记录
            CorporateAccountContOrderChangeEntity contOrderChangeEntity = new CorporateAccountContOrderChangeEntity();
            ObjectUtil.copyObject(contOrderEntity, contOrderChangeEntity);
            contOrderChangeEntity.setStatus(ContOrderStatusEnum.COMPLETED.getValue());
            contOrderChangeEntity.setId(null);
            corporateAccountContOrderChangeService.insertSelective(contOrderChangeEntity);
            //4.3 更新对公户过期时间
            CompanyCorporateAccountEntity companyCorpAcc = Optional.ofNullable(companyCorporateAccountService.findById(contOrderEntity.getCorporateAccountId())).orElseThrow(() -> new BusinessException("未查询到对公户信息"));
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(companyCorpAcc.getExpirationTime());
            calendar.add(Calendar.YEAR,1);
            companyCorpAcc.setExpirationTime(calendar.getTime());
            companyCorpAcc.setIsSendNotice(0);
            companyCorporateAccountService.editByIdSelective(companyCorpAcc);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            map.put("endTime",formatter.format(companyCorpAcc.getExpirationTime()));
            //4.4 更新对公户过期状态
            companyCorporateAccountService.updateOverdueStatus(companyCorpAcc.getId(), companyCorpAcc.getExpirationTime());
            //4.5 扣除自己资金
            userCapitalAccountService.addBalanceByProfits(order.getOemCode(), order.getOrderNo(), order.getOrderType(), order.getUserId(), 1, order.getPayAmount(), 0L, 0L, order.getPayAmount(), "企业托管费续费", member.getMemberAccount(), date, 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
            UserEntity oemUser = new UserEntity();
            oemUser.setOemCode(order.getOemCode());
            oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
            oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
            oemUser.setStatus(1);//状态 0-禁用 1-可用
            oemUser = userService.selectOne(oemUser);
            //4.6 给机构增加资金
            userCapitalAccountService.addBalanceByProfits(order.getOemCode(), order.getOrderNo(), order.getOrderType(), oemUser.getId(), 2, order.getPayAmount(), order.getPayAmount(), 0L, 0L, "企业托管费续费", member.getMemberAccount(), date, 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
            //4.7 添加消费记录
            memberConsumptionRecordService.insertSelective(oemCode, order.getOrderNo(), order.getOrderType(), order.getUserId(), order.getPayAmount(), member.getMemberAccount(), "开户订单已领证");
        } catch (BusinessException e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
        return map;
    }

    /**
     * 余额支付失败时操作
     * @param order
     */
    private void balancePaymentFailed(OrderEntity order, OemEntity oem, MemberAccountEntity member, UserCapitalAccountEntity userCapital, InvOrderPayDTO entity) {
        // 生成失败支付流水
        PayWaterEntity payWater = new PayWaterEntity();
        payWater.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        payWater.setOrderNo(order.getOrderNo());
        payWater.setMemberId(member.getId());
        payWater.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        payWater.setOemCode(oem.getOemCode());
        payWater.setOemName(oem.getOemName());
        payWater.setOrderAmount(order.getOrderAmount());
        payWater.setPayAmount(order.getPayAmount());
        // 订单类型 6-开票 8-注销 9-证件领用 7-会员升级 10-对公户申请 13-补税 15-托管费续费
        payWater.setOrderType(PayWaterOrderTypeEnum.getByValue(order.getOrderType()).getValue());
        payWater.setPayWaterType(PayWaterTypeEnum.BALANCE.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
        payWater.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        payWater.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
        payWater.setPayAccount(userCapital.getCapitalAccount());
        Date date = new Date();
        payWater.setAddTime(date);
        payWater.setAddUser(member.getMemberAccount());
        payWater.setUpdateUser(member.getMemberAccount());
        payWater.setUpdateTime(date);
        payWater.setPayTime(date);
        payWater.setRemark(entity.getGoodsName());// 备注字段暂用来存储商品名称
        payWater.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行
        payWater.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        payWaterService.insertSelective(payWater);
    }

    /**
     * 托管费续费订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private Map<String, String> balancePayOrderOfCustodyFeeRenewal(HashMap<String, String> map, OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        String oemCode = order.getOemCode();
        Date date = new Date();

        //一、查询校验
        //1.1 参数校验——商品名称校验
        //Optional.ofNullable(entity.getGoodsName().isEmpty()).orElseThrow(() -> new BusinessException("商品名称不能为空"));
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        //1.2 托管费续费订单
        ContRegisterOrderEntity contRegisterOrderEntity = new ContRegisterOrderEntity();
        contRegisterOrderEntity.setOrderNo(entity.getOrderNo());
        contRegisterOrderEntity = Optional.ofNullable(contRegisterOrderService.selectOne(contRegisterOrderEntity)).orElseThrow(() -> new BusinessException("未查询到托管费续费订单"));
        //1.3 订单校验
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), ContOrderStatusEnum.TO_BE_PAY.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        }

        try {
            //二、资金操作（冻结）
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);

            //四、支付完成后操作
            //4.1 支付成功，更新主表状态为“已完成”
            order.setOrderStatus(ContOrderStatusEnum.COMPLETED.getValue());
            order.setUpdateUser(member.getMemberAccount());
            order.setUpdateTime(date);
            orderMapper.updateByPrimaryKeySelective(order);
            //4.2 更新续费订单
            contRegisterOrderEntity.setOrderStatus(ContOrderStatusEnum.COMPLETED.getValue());
            contRegisterOrderEntity.setUpdateUser(member.getMemberAccount());
            contRegisterOrderEntity.setUpdateTime(date);
            contRegisterOrderService.editByIdSelective(contRegisterOrderEntity);
            //4.3 添加续费订单变更记录
            ContRegisterOrderChangeEntity contRegisterOrderChangeEntity = new ContRegisterOrderChangeEntity();
            contRegisterOrderChangeEntity.setOrderNo(contRegisterOrderEntity.getOrderNo());
            ContRegisterOrderChangeEntity changeEntity = contRegisterOrderChangeService.selectOne(contRegisterOrderChangeEntity);
            changeEntity.setOrderStatus(ContOrderStatusEnum.COMPLETED.getValue());
            changeEntity.setUpdateUser(member.getMemberAccount());
            changeEntity.setUpdateTime(date);
            changeEntity.setId(null);
            contRegisterOrderChangeService.insertSelective(changeEntity);
            //4.4 更新企业有效时间
            MemberCompanyEntity company = memberCompanyService.findById(contRegisterOrderEntity.getCompanyId());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(company.getEndTime());
            calendar.add(Calendar.YEAR,1);
            company.setEndTime(calendar.getTime());
            company.setIsSendNotice(0);
            memberCompanyService.editByIdSelective(company);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            map.put("endTime",formatter.format(company.getEndTime()));
            //4.5 更新企业过期状态
            memberCompanyService.updateOverdueStatus(company.getId(),company.getEndTime());
            //4.6 扣除自己资金
            userCapitalAccountService.addBalanceByProfits(order.getOemCode(), order.getOrderNo(), order.getOrderType(), order.getUserId(), 1, order.getPayAmount(), 0L, 0L, order.getPayAmount(), "企业托管费续费", member.getMemberAccount(), date, 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
            UserEntity oemUser = new UserEntity();
            oemUser.setOemCode(order.getOemCode());
            oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
            oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
            oemUser.setStatus(1);//状态 0-禁用 1-可用
            oemUser = userService.selectOne(oemUser);
            //4.7 给机构增加资金
            userCapitalAccountService.addBalanceByProfits(order.getOemCode(), order.getOrderNo(), order.getOrderType(), oemUser.getId(), 2, order.getPayAmount(), order.getPayAmount(), 0L, 0L, "企业托管费续费", member.getMemberAccount(), date, 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
            //4.8 添加分润明细数据
            try {
                profitsDetailService.saveProfitsDetailByOrderNo(order.getOrderNo(), member.getMemberAccount());
            } catch (Exception e) {
                log.error("分润失败：{}", e.getMessage());
                //分润失败
                order.setIsShareProfit(2);
                order.setProfitStatus(3);
                order.setUpdateTime(date);
                order.setUpdateUser("admin");
                order.setRemark("分润失败原因：" + e.getMessage());
                orderMapper.updateByPrimaryKeySelective(order);

                // 短信通知紧急联系人
                DictionaryEntity dict = this.dictionaryService.getByCode("emergency_contact");
                if (null != dict) {
                    String dicValue = dict.getDictValue();
                    String[] contacts = dicValue.split(",");
                    for (String contact : contacts) {
                        Map<String, Object> noticeMap = new HashMap();
                        noticeMap.put("oemCode", order.getOemCode());
                        noticeMap.put("orderNo", order.getOrderNo());
                        this.smsService.sendTemplateSms(contact, order.getOemCode(), VerifyCodeTypeEnum.NOTICE.getValue(), noticeMap, 1);
                        log.info("分润失败发送通知给【" + contact + "】成功");
                    }
                }
            }
            //4.9 添加企业年开票记录
            TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(company.getParkId(), company.getCompanyType(),company.getTaxpayerType());
            CompanyInvoiceRecordEntity compEntity = new CompanyInvoiceRecordEntity();
            compEntity.setCompanyId(company.getId());
            compEntity.setTotalInvoiceAmount(taxPolicyEntity.getTotalInvoiceAmount());
            compEntity.setRemainInvoiceAmount(taxPolicyEntity.getTotalInvoiceAmount());
            compEntity.setUseInvoiceAmount(0L);
            compEntity.setYear(calendar.get(Calendar.YEAR) + "");
            compEntity.setOemCode(oemCode);
            compEntity.setEndTime(calendar.getTime());
            compEntity.setAddTime(date);
            compEntity.setAddUser(member.getMemberAccount());
            companyInvoiceRecordMapper.insert(compEntity);
            //添加消费记录
            memberConsumptionRecordService.insertSelective(oemCode, order.getOrderNo(), order.getOrderType(), order.getUserId(), order.getPayAmount(), member.getMemberAccount(), "开户订单已领证");
            // 统计会员日推广数据
            this.orderService.statisticsMemberGeneralize(order,member.getMemberAccount(),1);
        } catch (BusinessException e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
        return map;
    }

    /**
     * 补税订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private void balancePayOrderOfRecoverableTax(OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        String memberAccount = member.getMemberAccount();
        String oemCode = order.getOemCode();
        String orderNo = order.getOrderNo();
        Integer orderType = order.getOrderType();
        Long payAmount = order.getPayAmount();

        //一、查询校验
        //1.1 参数校验——商品名称校验
        Optional.ofNullable(entity.getGoodsName().isEmpty()).orElseThrow(() -> new BusinessException("商品名称不能为空"));
        //1.2 补税订单
        CompanyTaxBillEntity companyTaxBillEntity = Optional.ofNullable(companyTaxBillService.findById(order.getProductId())).orElseThrow(() -> new BusinessException("未查询到需补缴税单"));
        if (!Objects.equals(companyTaxBillEntity.getTaxBillStatus(), TaxBillStatusEnum.TAX_TO_BE_PAID.getValue())) {
            throw new BusinessException("当前订单不是需补缴税单");
        }
        //1.3 订单校验
        if (!Objects.equals(orderType, OrderTypeEnum.RECOVERABLE_TAX.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), RACWStatusEnum.PAYING.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        }

        try {
            //二、资金操作（冻结）
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);

            //四、支付完成后操作
            //4.1 订单支付成功，更新主表状态为"支付完成"
            Date date = new Date();
            order.setOrderStatus(RACWStatusEnum.PAYED.getValue());
            order.setUpdateUser(memberAccount);
            order.setUpdateTime(date);
            orderMapper.updateByPrimaryKeySelective(order);

            UserEntity oemUser = new UserEntity();
            oemUser.setOemCode(oemCode);
            oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
            oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
            oemUser.setStatus(1);//状态 0-禁用 1-可用
            oemUser = userService.selectOne(oemUser);
            //4.2 扣除自己资金
            userCapitalAccountService.addBalanceByProfits(oemCode, orderNo, orderType, order.getUserId(), 1, payAmount, 0L, 0L, payAmount, "企业税费补缴", memberAccount, date, 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
            //4.3 给机构增加资金
            userCapitalAccountService.addBalanceByProfits(oemCode, orderNo, orderType, oemUser.getId(), 2, payAmount, payAmount, 0L, 0L, "企业税费补缴", memberAccount, date, 1, WalletTypeEnum.CONSUMER_WALLET.getValue());

            //4.4 修改企业税单记录
            if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TO_BE_DECLARE.getValue()); // V 3.11 查账征收补税后税单状态变为待申报
            } else {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TAX_PAID.getValue());
            }
            companyTaxBillEntity.setUpdateUser(memberAccount);
            companyTaxBillEntity.setOrderNo(orderNo);
            companyTaxBillEntity.setUpdateTime(date);
            companyTaxBillEntity.setCompleteTime(date);
            companyTaxBillService.editByIdSelective(companyTaxBillEntity);
            CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
            BeanUtils.copyProperties(companyTaxBillEntity,companyTaxBillChangeEntity);
            companyTaxBillChangeEntity.setId(null);
            companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
            companyTaxBillChangeEntity.setDescrip("用户补税成功");
            companyTaxBillChangeEntity.setAddTime(new Date());
            companyTaxBillChangeEntity.setAddUser(memberAccount);
            companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
        } catch (Exception e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
    }

    /**
     * 对公户申请订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private void balancePayOrderOfCorporateApply(OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        String memberAccount = member.getMemberAccount();
        Date date = new Date();

        //一、查询校验
        //1.1 参数校验——商品名称校验
        //Optional.ofNullable(entity.getGoodsName().isEmpty()).orElseThrow(() -> new BusinessException("商品名称不能为空"));
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        //1.2 查询对公户申请订单
        CorporateAccountApplyOrderEntity applyOrder = Optional.ofNullable(corporateAccountApplyOrderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到对公户申请订单"));
        //1.3 订单校验
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.CORPORATE_APPLY.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), CorporateAccountApplyOrderStatusEnum.TO_BE_PAY.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        }
        //1.4 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(order.getParkId());
        park.setStatus(ParkStatusEnum.ON_SHELF.getValue());//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("园区不存在或未上架");
        }
        //1.5 查询产品
        ProductEntity product = new ProductEntity();
        product.setOemCode(order.getOemCode());
        product.setProdType(ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue());
        product.setStatus(ParkStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }
        //1.6 查询产品园区关系
        ProductParkRelaEntity relaEntity = new ProductParkRelaEntity();
        relaEntity.setProductId(product.getId());
        relaEntity.setParkId(park.getId());
        relaEntity = productParkRelaService.selectOne(relaEntity);
        if (null == relaEntity) {
            throw new BusinessException("该园区不支持对公户申请");
        }
        //1.7 查询企业对公户配置
        List<ParkCorporateAccountConfigEntity> list = Optional.ofNullable(parkCorporateAccountConfigService.getConfigByParkId(park.getId())).orElseThrow(() -> new BusinessException("企业对公户配置不存在或不可用"));

        try {
            //二、资金操作（冻结）
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);

            //四、支付完成后操作
            //4.1 订单支付成功，更新主表状态为"待发货"
            order.setOrderStatus(CorporateAccountApplyOrderStatusEnum.TO_BE_SUBSCRIBE.getValue());
            order.setUpdateUser(memberAccount);
            order.setUpdateTime(date);
            orderMapper.updateByPrimaryKeySelective(order);
            //4.2 修改对公户申请订单记录
            applyOrder.setPayTime(date);
            applyOrder.setUpdateUser(memberAccount);
            applyOrder.setUpdateTime(date);
            corporateAccountApplyOrderService.editByIdSelective(applyOrder);

            //4.3 保存对公户申请订单变更记录
            CorporateAccountApplyOrderChangeEntity applyOrderChange = new CorporateAccountApplyOrderChangeEntity();
            BeanUtil.copyProperties(applyOrder, applyOrderChange);
            applyOrderChange.setId(null);
            applyOrderChange.setStatus(CorporateAccountApplyOrderStatusEnum.TO_BE_SUBSCRIBE.getValue());
            applyOrderChange.setAddTime(date);
            applyOrderChange.setAddUser(memberAccount);
            applyOrderChange.setUpdateTime(null);
            applyOrderChange.setUpdateUser(null);
            corporateAccountApplyOrderChangeService.insertSelective(applyOrderChange);
        } catch (Exception e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
    }

    /**
     * 证件申请订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private void balancePayOrderOfCompresapply(OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        String memberAccount = member.getMemberAccount();
        Date date = new Date();

        //一、查询校验
        // 1.1 证件领用订单
        CompanyResoucesApplyRecordEntity resApplyRecord = new CompanyResoucesApplyRecordEntity();
        resApplyRecord.setOrderNo(entity.getOrderNo());
        resApplyRecord = Optional.ofNullable(companyResoucesApplyRecordService.selectOne(resApplyRecord)).orElseThrow(() -> new BusinessException("未查询到证件领用订单"));
        //1.2 订单校验
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.COMPRESAPPLY.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), CompResApplyRecordOrderStatusEnum.TO_BE_PAY.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        }

        try {
            //二、资金操作（冻结）
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);

            //四、支付完成后操作
            //4.1 订单支付成功，更新主表状态为"待发货"
            order.setOrderStatus(CompResApplyRecordOrderStatusEnum.TO_BE_SHIPPED.getValue());
            order.setUpdateUser(memberAccount);
            order.setUpdateTime(date);
            orderMapper.updateByPrimaryKeySelective(order);

            //4.2 更新证件领用记录为“待发货”
            resApplyRecord.setStatus(CompResApplyRecordOrderStatusEnum.TO_BE_SHIPPED.getValue());
            resApplyRecord.setUpdateUser(memberAccount);
            resApplyRecord.setUpdateTime(date);
            companyResoucesApplyRecordService.editByIdSelective(resApplyRecord);
        } catch (Exception e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
    }


    /**
     * 工商注销订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private void balancePayOrderOfCancellation(OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        String memberAccount = member.getMemberAccount();
        Date date = new Date();

        //一、查询校验
        //1.1 参数校验——商品名称校验
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        //1.2 企业注销订单
        CompanyCancelOrderEntity companyCancel = new CompanyCancelOrderEntity();
        companyCancel.setOrderNo(entity.getOrderNo());
        companyCancel = Optional.ofNullable(companyCancelOrderService.selectOne(companyCancel)).orElseThrow(() -> new BusinessException("未查询到企业注销订单"));
        //1.3 订单校验
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.CANCELLATION.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), CompCancelOrderStatusEnum.TO_BE_PAY.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        }
        //1.4 待确认成本校验
        // 查询企业
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(companyCancel.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业信息"));
        PendingTaxBillQuery pendingTaxBillQuery = new PendingTaxBillQuery();
        pendingTaxBillQuery.setEin(company.getEin());
        pendingTaxBillQuery.setCompanyId(companyCancel.getCompanyId());
        pendingTaxBillQuery.setStatusRange(3);
        List<PendingTaxBillVO> pendingTaxBillVOS = companyTaxBillService.pendingTaxBill(pendingTaxBillQuery);
        // 待补税订单
        List<PendingTaxBillVO> taxToBePaid = pendingTaxBillVOS.stream().filter(x -> TaxBillStatusEnum.TAX_TO_BE_PAID.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(taxToBePaid)) {
            throw new BusinessException("存在待补税的税单");
        }
        // 待填报成本
        List<PendingTaxBillVO> toBeWriteCost = pendingTaxBillVOS.stream().filter(x -> TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(toBeWriteCost)) {
            throw new BusinessException("存在待填报成本的税单");
        }

        try {
            //二、资金操作（冻结）
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);

            //四、支付完成后操作
            //4.1 订单支付成功，更新主表状态为"注销处理中"和待分润金额
            order.setOrderStatus(CompCancelOrderStatusEnum.IN_PROCESSING.getValue());
            order.setUpdateUser(memberAccount);
            order.setUpdateTime(date);
            order.setProfitAmount(companyCancel.getCancelServiceCharge());
            orderMapper.updateByPrimaryKeySelective(order);

            //4.2 更新企业状态为“注销中”
            company = new MemberCompanyEntity();
            company.setId(companyCancel.getCompanyId());
            company.setStatus(MemberCompanyStatusEnum.CANCELLING.getValue());
            memberCompanyService.editByIdSelective(company);

            //4.3 添加注销订单变更记录
            CompanyCancelOrderChangeRecordEntity record = new CompanyCancelOrderChangeRecordEntity();
            BeanUtil.copyProperties(companyCancel, record);
            record.setId(null);
            record.setOrderStatus(CompCancelOrderStatusEnum.IN_PROCESSING.getValue());
            record.setAddTime(date);
            record.setAddUser(memberAccount);
            record.setRemark("企业注销订单余额支付");
            companyCancelOrderChangeRecordService.insertSelective(record);
        } catch (Exception e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
    }

    /**
     * 用户升级订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private void balancePayOrderOfUpgrade(OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        String oemCode = oem.getOemCode();
        Date date = new Date();

        //一、查询校验
        //1.1 参数校验——商品名称校验
        //Optional.ofNullable(entity.getGoodsName().isEmpty()).orElseThrow(() -> new BusinessException("商品名称不能为空"));
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        //1.2 会员升级订单
        OrderEntity memberUpgrade = Optional.ofNullable(orderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到会员升级订单"));
        //1.3 订单校验及会员等级校验
        MemberLevelEntity level = this.memberLevelService.findById(memberUpgrade.getProductId());
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.UPGRADE.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), MemberOrderStatusEnum.TO_BE_PAY.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        }else if (null == level){
            throw new BusinessException("会员等级套餐信息不存在");
        }else if (this.memberLevelService.findById(member.getMemberLevel()).getLevelNo() >= level.getLevelNo()){
            throw new BusinessException("您当前等级不符合该升级条件");
        }

        try {
            //二、资金操作(冻结)
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);

            //四、支付完成后操作
            //4.1 修改主表订单状态
            order.setOrderStatus(MemberOrderStatusEnum.COMPLETED.getValue());
            order.setUpdateUser(member.getMemberAccount());
            order.setUpdateTime(date);
            orderMapper.updateByPrimaryKeySelective(order);

            //4.2 更新会员等级
            member.setId(member.getId());
            member.setOemCode(oemCode);
            member.setLevelName(level.getLevelName());
            member.setMemberLevel(level.getId());
            // 支付金额大于0才标记是否付费升级
            if (order.getPayAmount() > 0) {
                member.setIsPayUpgrade(1); //是否付费升级 0-否 1-是
            } else {
                member.setIsPayUpgrade(0); //是否付费升级 0-否 1-是
            }
            member.setUpdateUser(member.getMemberAccount());
            member.setUpdateTime(date);
            memberAccountService.editByIdSelective(member);


            //4.3 资金操作————资金流动
            //查询oem机构账号
            UserEntity oemUser = new UserEntity();
            oemUser.setOemCode(oemCode);
            oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
            oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
            oemUser.setStatus(1);//状态 0-禁用 1-可用
            oemUser = userService.selectOne(oemUser);
            if (null == oemUser) {
                throw new BusinessException("oem机构账号不存在或被禁用");
            }
            //扣除自己资金
            userCapitalAccountService.addBalanceByProfits(order.getOemCode(), order.getOrderNo(), order.getOrderType(), order.getUserId(), 1, order.getPayAmount(), 0L, 0L, order.getPayAmount(), "企业税费补缴", member.getMemberAccount(), date, 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
            //给机构增加资金
            userCapitalAccountService.addBalanceByProfits(order.getOemCode(), order.getOrderNo(), order.getOrderType(), oemUser.getId(), 2, order.getPayAmount(), order.getPayAmount(), 0L, 0L, "企业税费补缴", member.getMemberAccount(), date, 1, WalletTypeEnum.CONSUMER_WALLET.getValue());

            //4.4 V2.3需求，添加会员消费记录
            MemberConsumptionRecordEntity consumptionRecord = new MemberConsumptionRecordEntity();
            consumptionRecord.setOrderNo(order.getOrderNo());
            consumptionRecord.setOrderType(OrderTypeEnum.UPGRADE.getValue());
            consumptionRecord.setMemberId(member.getId());
            consumptionRecord.setConsumptionAmount(order.getPayAmount()); // 消费金额(会费)
            consumptionRecord.setOemCode(oemCode);
            consumptionRecord.setIsOpenInvoice(0);// 是否已开票 0-未开 1-已开
            consumptionRecord.setAddTime(new Date());
            consumptionRecord.setAddUser(member.getMemberAccount());
            consumptionRecord.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
            memberConsumptionRecordService.insertSelective(consumptionRecord);

            //4.5 添加分润明细数据
            try {
                profitsDetailService.saveProfitsDetailByOrderNo(order.getOrderNo(), member.getMemberAccount());
            } catch (Exception e) {
                log.error("分润失败：{}", e.getMessage());
                //分润失败
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
                        map.put("orderNo", order.getOrderNo());
                        this.smsService.sendTemplateSms(contact, order.getOemCode(), VerifyCodeTypeEnum.NOTICE.getValue(), map, 1);
                        log.info("分润失败发送通知给【" + contact + "】成功");
                    }
                }
            }

            //4.6 统计会员日推广数据
            try {
                orderService.statisticsMemberGeneralize(order, member.getMemberAccount(), 1);
            } catch (BusinessException e) {
                log.error("统计会员日推广数据失败：{}", e.getMessage());
            }
        } catch (BusinessException e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
    }

    /**
     * 注册订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private Map<String,String> balancePayOrderOfRegCompany(HashMap<String, String> map, OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        Date date = new Date();
        String memberAccount = member.getMemberAccount();
        String customerServicePhone = null;// 接单客服电话
        Integer isOpenAuthentication = null;// 是否已开启身份验证 0-未开启 1-已开启
        //一、查询校验
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        //1.3 订单校验
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.REGISTER.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (!Objects.equals(order.getOrderStatus(), RegOrderStatusEnum.TO_BE_PAY.getValue())) {
            throw new BusinessException("订单状态错误，支付失败");
        }
        // 判断是否使用了优惠券
        Long payAmount = order.getPayAmount();
        CouponsIssueRecordEntity issueRecord = new CouponsIssueRecordEntity();
        RegisterOrderEntity registerOrderEntity = registerOrderService.queryByOrderNo(entity.getOrderNo());
        if (null == registerOrderEntity) {
            throw new BusinessException("未查询到注册订单");
        }
        RegisterOrderChangeRecordEntity recordEntity = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(registerOrderEntity, recordEntity);// 参数拷贝
        if (Objects.nonNull(entity.getCouponsIssueRecordId())) {
            // 查询优惠券金额
            issueRecord = Optional.ofNullable(couponsIssueRecordService.findById(entity.getCouponsIssueRecordId())).orElseThrow(() -> new BusinessException("未查询到优惠券发放记录"));
            CouponsEntity couponsEntity = Optional.ofNullable(couponsService.findById(issueRecord.getCouponsId())).orElseThrow(() -> new BusinessException("未查询到优惠券信息"));
            // 校验优惠券可用范围是否合法
            if (!couponsEntity.getUsableRange().equals(registerOrderEntity.getCompanyType().toString())) {
                throw new BusinessException("优惠券不可用于当前类型企业注册");
            }
            // 校验优惠券是否过期,过期则抛异常
            if (DateUtils.truncatedCompareTo(couponsEntity.getEndDate(),new Date(),Calendar.DATE) < 0) {
                throw new BusinessException("优惠券已过期，不可使用");
            }
            Long faceAmount = couponsEntity.getFaceAmount();

            if (Objects.nonNull(registerOrderEntity.getCouponsIssueId()) && !Objects.equals(entity.getCouponsIssueRecordId(),registerOrderEntity.getCouponsIssueId())) {
                throw new BusinessException("系统错误，请返回订单列表重新进入此页面");
            }
            // 校验订单金额
            if (null == registerOrderEntity.getCouponsIssueId()) {
                payAmount = Long.valueOf(new BigDecimal(payAmount).subtract(new BigDecimal(faceAmount)).longValue());
                if (payAmount < 0) {
                    payAmount = 0L;
                }
            }

        }
        if(!Objects.equals(payAmount,entity.getAmount())){
            throw new BusinessException("订单支付金额错误，无法进行支付");
        }
        order.setPayAmount(payAmount);

        try {
            //二、资金操作
            capitalOperation(order,member,userCapital,oem,entity,userCapitalBlockChange);
            //三、流水操作
            payWaterOperation(order,member,userCapital,oem,entity,water,null);
            //注册订单操作支付流水需设置优惠金额
            water.setDiscountAmount(order.getDiscountAmount());

            //四、支付完成后操作
            //4.1 订单支付成功，更新主表状态为"待审核"和待分润金额
            order.setOrderStatus(RegOrderStatusEnum.TO_BE_SURE.getValue());
            order.setUpdateUser(memberAccount);
            order.setUpdateTime(date);
            order.setPayAmount(payAmount);
            // 查询园区
            ParkEntity park = parkService.findById(order.getParkId());
            if (null == park) {
                throw new BusinessException("未查询到园区信息");
            }
            order.setProfitAmount(park.getIsRegisterProfit() == 1 ? payAmount : 0L);
            orderMapper.updateByPrimaryKeySelective(order);

            //4.2 修改优惠券使用状态及使用时间
            if (null != entity.getCouponsIssueRecordId()) {
                issueRecord.setUseTime(date);
                issueRecord.setStatus(CouponsIssueRecordStatusEnum.USED.getValue());
                issueRecord.setUpdateTime(date);
                issueRecord.setUpdateUser(member.getMemberAccount());
                issueRecord.setRemark("使用优惠券");
                couponsIssueRecordService.editByIdSelective(issueRecord);
                // 修改注册订单支付金额
                registerOrderEntity.setPayAmount(payAmount);
                registerOrderEntity.setCouponsIssueId(entity.getCouponsIssueRecordId());// 优惠券发放记录id
                registerOrderService.updateByPrimaryKeySelective(registerOrderEntity);
                // 修改注册订单变更记录支付金额
                recordEntity.setPayAmount(payAmount);
                recordEntity.setCouponsIssueId(entity.getCouponsIssueRecordId());
            }

            //4.3 支付成功，进行自动派单
            log.info("注册订单余额支付，进行自动派单...");
            OemEntity oemEntity=oemService.getOem(order.getOemCode());
            if(oemEntity==null){
                throw new BusinessException("oem机构不存在");
            }
            String oemCode=null;
            if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
                oemCode=order.getOemCode();
            }
            Long recvOrderUserId = receiveOrderService.getReceiveServer(oemCode, entity.getOrderNo(), 1, 1).getRecvOrderUserId();
            if (null != recvOrderUserId) {
                // 派单成功，返回客服信息
                UserExtendEntity userExtend = new UserExtendEntity();
                userExtend.setUserId(recvOrderUserId);
                userExtend = userExtendMapper.selectOne(userExtend);
                if (null == userExtend) {
                    throw new BusinessException("自动派单失败，未找到接单客服拓展信息");
                }
                customerServicePhone = userExtend.getPhone();
                map.put("customerServicePhone",customerServicePhone);
            }
            // 长沙园区特殊处理
            if(Objects.equals(park.getProcessMark(), ParkProcessMarkEnum.IDENTITY.getValue())){
                // 创建一条新的首页弹窗通知
                log.info("===长沙园区支付完成，创建首页弹窗通知===");
                this.messageNoticeService.addNoticeIndex(member.getId(),order.getOemCode(),order.getOrderNo());
                isOpenAuthentication = 0;
            }
            // 生成企业专属经营链接（需求变更）
            String businessAddr = parkBusinessAddressRulesService.builderBusinessAddressByPark(order.getParkId());
            // 更新客服和经营链接
            log.info("更新开户订单专属客服电话和经营链接地址：{},{},{}",order.getOrderNo(),customerServicePhone,businessAddr);
            registerOrderService.updateCusomerPhoneAndBusinessAddr(order.getOrderNo(), customerServicePhone, businessAddr,isOpenAuthentication);

            //保存工商注册订单变更记录
            recordEntity.setId(null);
            recordEntity.setCustomerServicePhone(customerServicePhone);
            recordEntity.setBusinessAddress(businessAddr);
            recordEntity.setAddTime(new Date());
            recordEntity.setAddUser(order.getAddUser());
            recordEntity.setOrderStatus(order.getOrderStatus());
            this.registerOrderChangeRecordService.insertSelective(recordEntity);
        } catch (BusinessException e) {
            log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
            balancePaymentFailed(order, oem, member, userCapital, entity);
            //开票操作支付流水需设置优惠金额
            water.setDiscountAmount(order.getDiscountAmount());
            throw new BusinessException("余额支付下单异常，" + e.getMessage());
        }
        return map;
    }

    /**
     * 开票订单余额支付完成后续操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param userCapitalBlockChange
     * @param water
     */
    private Map<String,String> balancePayOrderOfInvoice(HashMap<String, String> map, OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange, PayWaterEntity water) {
        Date date = new Date();
        String memberAccount = member.getMemberAccount();
        //一、查询校验
        //1.1 参数校验——商品名称校验
        //Optional.ofNullable(entity.getGoodsName().isEmpty()).orElseThrow(() -> new BusinessException("商品名称不能为空"));
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        //1.2 开票订单
        InvoiceOrderEntity invOrder = Optional.ofNullable(invoiceOrderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到开票订单"));
        // 校验重算标识
        if (Objects.equals(0, invOrder.getIsRecalculateServiceFee())) {
            throw new BusinessException("税费计算发生变化，请重新进入该页");
        }

        //1.4 企业校验
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(invOrder.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业"));
        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("公司状态异常，无法开票");
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException("公司开票已满额，无法开票");
        }
        if(company.getCompanyType()!=1){
            memberAccount = entity.getUpdateUser();
        }

        //1.3 订单校验
        if (!Objects.equals(order.getOrderType(), OrderTypeEnum.INVOICE.getValue())) {
            throw new BusinessException("无效的订单类型");
        } else if (company.getCompanyType() == 1 && !Objects.equals(order.getOrderStatus(), InvoiceOrderStatusEnum.UNPAID.getValue())) {
            throw new BusinessException("订单已支付或已取消");
        } else if (company.getCompanyType() != 1 && !Objects.equals(order.getOrderStatus(), InvoiceOrderStatusEnum.TO_PAYMENT_REVIEW.getValue()))
            throw new BusinessException("订单状态错误");
        try {
            //开票操作支付流水需设置优惠金额
            water.setDiscountAmount(invOrder.getServiceFeeDiscount());
            if(company.getCompanyType() == 1) {
                //二、资金操作
                capitalOperation(order, member, userCapital, oem, entity, userCapitalBlockChange);
                //三、流水操作
                payWaterOperation(order, member, userCapital, oem, entity, water,null);
            }else{
                //三、流水操作
                payWaterOperation(order, member, userCapital, oem, entity, water,PayChannelEnum.OFFLINE.getValue());
            }

            //四、支付完成后操作
            //4.1 订单支付成功，更新主表状态为"待审核"和待分润金额
            order.setOrderStatus(InvoiceOrderStatusEnum.UNCHECKED.getValue());
            order.setUpdateUser(memberAccount);
            order.setUpdateTime(date);
            order.setProfitAmount(invOrder.getServiceFee() - invOrder.getServiceFeeDiscount());
            orderMapper.updateByPrimaryKeySelective(order);

            //4.2 添加开票订单变更记录
            InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
            BeanUtil.copyProperties(invOrder, record);
            record.setId(null);
            if (order.getPayAmount() == 0L) { // 非个体开票，支付金额为0，为保证历史记录排序，操作时间增加1s以区别‘确认开票’操作记录
                date.setTime(date.getTime() + 1000);
                record.setAddTime(date);
            }
            record.setAddTime(date);
            record.setAddUser(memberAccount);
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setOrderStatus(InvoiceOrderStatusEnum.UNCHECKED.getValue());
            record.setRemark("开票订单余额支付");
            if (company.getCompanyType() != 1 && StringUtil.isNotBlank(entity.getReceiptPaymentVoucher())) {
                record.setRemark("收款凭证|receiptPaymentVoucher");
            }
            invoiceOrderChangeRecordService.insertSelective(record);

            //4.3 支付成功，进行自动派单
            log.info("开票订单余额支付，进行自动派单...");
            OemEntity oemEntity=oemService.getOem(order.getOemCode());
            if(oemEntity==null){
                throw new BusinessException("oem机构不存在");
            }
            String oemCode=null;
            if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
                oemCode=order.getOemCode();
            }
            Long recvOrderUserId = receiveOrderService.getReceiveServer(oemCode, entity.getOrderNo(), 2, 2).getRecvOrderUserId();
            if (null != recvOrderUserId) {
                // 派单成功，返回客服信息
                UserExtendEntity userExtend = new UserExtendEntity();
                userExtend.setUserId(recvOrderUserId);
                userExtend = userExtendMapper.selectOne(userExtend);
                if (null == userExtend) {
                    throw new BusinessException("自动派单失败，未找到接单客服拓展信息");
                }
                // 更新开票订单的专属客服电话
                invOrder.setCustomerServicePhone(userExtend.getPhone());
                invoiceOrderService.editByIdSelective(invOrder);
                map.put("customerServicePhone",invOrder.getCustomerServicePhone());
            }
        } catch (BusinessException e) {
            if(company.getCompanyType() == 1) {
                log.error(order.getOrderNo() + "余额支付下单异常：" + e.getMessage());
                balancePaymentFailed(order, oem, member, userCapital, entity);
                //开票操作支付流水需设置优惠金额
                water.setDiscountAmount(invOrder.getServiceFeeDiscount());
                throw new BusinessException("余额支付下单异常，" + e.getMessage());
            }else {
                log.error(order.getOrderNo() + "财务审核异常：" + e.getMessage());
//                balancePaymentFailed(order, oem, member, userCapital, entity);
                //开票操作支付流水需设置优惠金额
                water.setDiscountAmount(invOrder.getServiceFeeDiscount());
                throw new BusinessException("财务审核异常，" + e.getMessage());
            }
        }
        return map;
    }

    /**
     * 余额支付流水操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     * @param water
     */
    private void payWaterOperation(OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, PayWaterEntity water,Integer payChannel) {
        Date date = new Date();

        // 生成支付流水
        water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        water.setOrderNo(order.getOrderNo());
        water.setMemberId(member.getId());
        water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setOemCode(oem.getOemCode());
        water.setOemName(oem.getOemName());
        water.setOrderAmount(order.getOrderAmount());
        water.setPayAmount(order.getPayAmount());
        water.setOrderType(order.getOrderType());
        if(payChannel!=null && payChannel == 9){
            water.setPayWaterType(PayWaterTypeEnum.OFFLINE.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款 6-对公户提现 7-企业退税  8 - 线下支付
            water.setPayWay(PayWayEnum.OFFLINE.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付 5-字节跳动 6-线下转账
            water.setPayChannels(PayChannelEnum.OFFLINE.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付 9-线下
        }else{
            water.setPayWaterType(PayWaterTypeEnum.BALANCE.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款 6-对公户提现 7-企业退税  8 - 线下支付
            water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付 5-字节跳动 6-线下转账
            water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付 9-线下
        }
        water.setPayTime(date);
        water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        water.setPayAccount(null != userCapital ? userCapital.getCapitalAccount() : null);
        water.setAddTime(date);
        water.setAddUser(member.getMemberAccount());
        water.setRemark(entity.getGoodsName());// 备注字段暂用来存储商品名称
        water.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue()); //钱包类型 1-消费钱包 2-佣金钱包
        payWaterService.insertSelective(water);
    }

    /**
     * 余额支付资金操作
     * @param order
     * @param member
     * @param userCapital
     * @param oem
     * @param entity
     */
    private void capitalOperation(OrderEntity order, MemberAccountEntity member, UserCapitalAccountEntity userCapital, OemEntity oem, InvOrderPayDTO entity, UserCapitalChangeRecordEntity userCapitalBlockChange) {
        Long payAmount = order.getPayAmount();
        if (payAmount <= 0L) {
            return;
        }
        Integer orderType = order.getOrderType();
        String memberAccount = member.getMemberAccount();
        Long beforeTotalAmount = userCapital.getTotalAmount();
        Date date = new Date();

        // 冻结会员资金账户金额
        userCapital.setAvailableAmount(userCapital.getAvailableAmount() - payAmount);//修改可用金额
        userCapital.setBlockAmount(userCapital.getBlockAmount() + payAmount);//添加冻结金额
        userCapital.setUpdateTime(date);
        userCapital.setUpdateUser(memberAccount);
        userCapitalAccountService.editByIdSelective(userCapital);

        // 添加会员资金账号冻结记录
        userCapitalBlockChange.setCapitalAccountId(userCapital.getId());
        userCapitalBlockChange.setUserId(member.getId());
        userCapitalBlockChange.setUserType(MemberTypeEnum.MEMBER.getValue());//用户类型 1-会员 2 -系统用户
        userCapitalBlockChange.setOemCode(oem.getOemCode());
        userCapitalBlockChange.setChangesAmount(payAmount);
        userCapitalBlockChange.setChangesBeforeAmount(beforeTotalAmount);//变动前余额
        userCapitalBlockChange.setChangesAfterAmount(beforeTotalAmount);//变动后余额
        userCapitalBlockChange.setChangesType(CapitalChangeTypeEnum.FROZEN.getValue());
        userCapitalBlockChange.setDetailDesc(OrderTypeEnum.getByValue(orderType).getMessage()+"订单余额支付");
        userCapitalBlockChange.setOrderType(orderType);
        userCapitalBlockChange.setOrderNo(order.getOrderNo());
        userCapitalBlockChange.setAddTime(date);
        userCapitalBlockChange.setAddUser(memberAccount);
        userCapitalBlockChange.setRemark(entity.getGoodsName());// 备注字段暂用来存储商品名称
        userCapitalBlockChange.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());//钱包类型 1-消费钱包 2-佣金钱包
        userCapitalChangeRecordService.insertSelective(userCapitalBlockChange);
    }

    @Override
    public MemberOrderRelaEntity getUserTree(Long memberId, String oemCode, Integer type) throws BusinessException {
        // 设置平台、合伙人、城市合伙人数据
        MemberOrderRelaEntity memberOrderRelaEntity = setPlatformAndProviders(memberId, oemCode, type);

        // 查询会员账号
        MemberAccountLevelProfitsRuleVO accountLevelProfitVO = memberAccountService.queryMemberAccLevelProfit(memberId);
        if (null == accountLevelProfitVO) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员上级账号
        MemberAccountLevelProfitsRuleVO parentAccountLevelProfitVO = memberAccountService.queryMemberAccLevelProfit(accountLevelProfitVO.getParentMemberId());
        if (null != parentAccountLevelProfitVO) {
            memberOrderRelaEntity.setAccountFirstId(parentAccountLevelProfitVO.getId());
            // 一级推广人账号
            memberOrderRelaEntity.setAccountFirst(parentAccountLevelProfitVO.getMemberAccount());
            // 一级推广人手机号码
            memberOrderRelaEntity.setPhoneFirst(parentAccountLevelProfitVO.getMemberPhone());
            // 一级推广人名称
            memberOrderRelaEntity.setNameFirst(parentAccountLevelProfitVO.getMemberName());
            // 一级推广人等级
            memberOrderRelaEntity.setLevelFirst(parentAccountLevelProfitVO.getLevelNo());
            // 一级推广人分润比率(订单类型获取不同的分润比例：1-开户 2-开票 3-会员升级 4-企业注销 5-证件领用)
            if (type == 1) {
                memberOrderRelaEntity.setLevelFirstProfitsRate(parentAccountLevelProfitVO.getProfitsEntrustFeeRate());
            } else if (type == 2) {
                memberOrderRelaEntity.setLevelFirstProfitsRate(parentAccountLevelProfitVO.getServiceFeeRate());
            } else if (type == 3) {
                memberOrderRelaEntity.setLevelFirstProfitsRate(parentAccountLevelProfitVO.getMembershipFee());
            } else if (type == 4) {
                memberOrderRelaEntity.setLevelFirstProfitsRate(parentAccountLevelProfitVO.getServiceFeeRate());
            } else if (type == 5) {
                memberOrderRelaEntity.setLevelFirstProfitsRate(new BigDecimal(0));
            }
            memberOrderRelaEntity.setParentMemberLevelNo(parentAccountLevelProfitVO.getLevelNo()); //邀请人等级标识
        }

        // 设置其他值
        memberOrderRelaEntity.setParentMemberId(accountLevelProfitVO.getParentMemberId()); //邀请人id
        memberOrderRelaEntity.setParentMemberAccount(accountLevelProfitVO.getParentMemberAccount()); //邀请人账号
        return memberOrderRelaEntity;
    }

    //------------------------------------------------会员订单关系代码开始-------------------------------------------------

    /**
     * 设置平台、合伙人、城市合伙人数据
     *
     * @param memberId 会员ID
     * @param oemCode
     * @param type     1-开户 2-开票 3-会员升级 4-企业注销 5-证件领用
     * @return MemberOrderRelaEntity
     */
    private MemberOrderRelaEntity setPlatformAndProviders(Long memberId, String oemCode, Integer type) {
        MemberOrderRelaEntity memberOrderRelaEntity = new MemberOrderRelaEntity();

        // 查询会员的用户关系树
        UserRelaEntity userRela = userRelaService.queryUserRelaEntityByUserId(memberId, 5);
        if (null == userRela) {
            throw new BusinessException("未查询到会员关系");
        }

        // 分割用户树
        String[] split = userRela.getUserTree().split("/");//用户树
        if (split.length < 1) {
            throw new BusinessException("会员关系树为空");
        }

        // 遍历用户数查询用户关系
        List<UserRelaEntity> userRelaList = new LinkedList<UserRelaEntity>();
        for (int i = 0; i < split.length; i++) {
            if ("0".equals(split[i])) {
                userRela = new UserRelaEntity();
            } else {
                userRela = userRelaService.queryUserRelaEntityByUserId(Long.valueOf(split[i]), i <= 3 ? i + 1 : 5);
            }
            if (null == userRela) {
                throw new BusinessException("未查询到会员关系");
            } else {
                userRelaList.add(userRela);
            }
        }

        // 偏历用户数查询用户是否为会员，是则删除
        MemberAccountEntity member = new MemberAccountEntity();
        for (int i = 4; i < userRelaList.size(); i++) {
            member.setId(userRelaList.get(i).getUserId());
            member = memberAccountService.selectOne(member);
            if (member.getMemberType().equals(MemberTypeEnum.EMPLOYEE.getValue())) {
                userRelaList.remove(i);
            }
            member = new MemberAccountEntity();
        }

        //移除最后一个元素，会员自己
        userRelaList.remove(((LinkedList<UserRelaEntity>) userRelaList).getLast());

        // 判断第二级用户是否机构，如果是机构，则获取第三级和第四家用户作为用户订单表的合伙人和城市合伙人数据
        if (CollectionUtil.isNotEmpty(userRelaList)) {
            // 获取第一级为平台账号
            UserEntity sysUser = new UserEntity();
            sysUser.setId(userRelaList.get(0).getUserId());
            sysUser.setPlatformType(1);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
            sysUser = userService.selectOne(sysUser);//查询系统用户
            if (null == sysUser) {
                throw new BusinessException("未查询到平台账号");
            }
            memberOrderRelaEntity.setPlatformAccountId(sysUser.getId());//平台id
            memberOrderRelaEntity.setPlatformAccount(sysUser.getUsername());//平台账号
            AgentProfitsRulesEntity agentRule = new AgentProfitsRulesEntity();
            agentRule.setAgentId(userRelaList.get(0).getUserId());
            agentRule.setOemCode(oemCode);
            agentRule = agentProfitsRulesService.selectOne(agentRule);//查询代理商分润规则
            if (null == agentRule) {
                throw new BusinessException("未查询到代理商分润规则");
            }
            // 类型：1-开户 2-开票 3-会员升级 4-企业注销 5-证件领用
            if (type == 1) {
                memberOrderRelaEntity.setPlatformAccountProfitsRate(agentRule.getRegisterFee());// 平台开户分润比率
            } else if (type == 2) {
                memberOrderRelaEntity.setPlatformAccountProfitsRate(agentRule.getInvoiceFee());// 平台开票分润比率
            } else if (type == 3) {
                memberOrderRelaEntity.setPlatformAccountProfitsRate(agentRule.getMembershipFee());// 平台会员升级分润比率
            } else if (type == 4) {
                memberOrderRelaEntity.setPlatformAccountProfitsRate(agentRule.getCancelCompanyFee());// 平台企业注销分润比率
            } else if (type == 5) {
                memberOrderRelaEntity.setPlatformAccountProfitsRate(new BigDecimal(0));// 平台证件领用分润比率
            }
            //v3.0不在设置机构城市合伙人
           /* if (userRelaList.size() >= 2) {
                UserExtendEntity extend = new UserExtendEntity();
                UserRelaEntity organ = userRelaList.get(1);
                // 2-机构、园区
                if (organ.getUserClass().equals(2)) {
                    // 获取第三级作为高级合伙人
                    if (userRelaList.size() >= 3) {
                        if (null != userRelaList.get(2).getId()) {
                            sysUser = new UserEntity();
                            sysUser.setId(userRelaList.get(2).getUserId());
                            sysUser.setPlatformType(4);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
                            sysUser = userService.selectOne(sysUser);//查询系统用户
                            if (null == sysUser) {
                                throw new BusinessException("未查询到高级合伙人");
                            } else {
                                // 查询高级合伙人手机号码
                                extend.setUserId(sysUser.getId());
                                extend = userExtendService.selectOne(extend);
                                if (null == extend) {
                                    throw new BusinessException("未查询到高级合伙人扩展关系");
                                }
                                memberOrderRelaEntity.setCityPartnerId(sysUser.getId());//高级城市合伙人id
                                memberOrderRelaEntity.setCityPartner(sysUser.getUsername());//高级城市合伙人账号
                                memberOrderRelaEntity.setCityPartnerPhone(extend.getPhone());//高级城市合伙人手机号码
                                memberOrderRelaEntity.setCityPartnerName(sysUser.getNickname());//高级城市合伙人名称
                                agentRule = new AgentProfitsRulesEntity();
                                agentRule.setAgentId(userRelaList.get(2).getUserId());
                                agentRule.setOemCode(oemCode);
                                agentRule = agentProfitsRulesService.selectOne(agentRule);//查询代理商分润规则
                                if (null == agentRule) {
                                    throw new BusinessException("未查询到代理商分润规则");
                                }
                                // 类型：1-开户 2-开票 3-会员升级 4-企业注销 5-证件领用
                                if (type == 1) {
                                    memberOrderRelaEntity.setCityPartnerProfitsRate(agentRule.getRegisterFee());//高级城市合伙人开户分润比率
                                } else if (type == 2) {
                                    memberOrderRelaEntity.setCityPartnerProfitsRate(agentRule.getInvoiceFee());// 高级城市合伙人开票分润比率
                                } else if (type == 3) {
                                    memberOrderRelaEntity.setCityPartnerProfitsRate(agentRule.getMembershipFee());//高级城市合伙人会员升级分润比率
                                } else if (type == 4) {
                                    memberOrderRelaEntity.setCityPartnerProfitsRate(agentRule.getCancelCompanyFee());// 高级城市合伙人企业注销分润比率
                                } else if (type == 5) {
                                    memberOrderRelaEntity.setCityPartnerProfitsRate(new BigDecimal(0));// 高级城市合伙人证件领用分润比率
                                }
                            }
                        }
                    }

                    // 获取第四级作为城市合伙人
                    if (userRelaList.size() >= 4) {
                        if (null != userRelaList.get(3).getId()) {
                            sysUser = new UserEntity();
                            sysUser.setId(userRelaList.get(3).getUserId());
                            sysUser.setPlatformType(5);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
                            sysUser = userService.selectOne(sysUser);//查询系统用户
                            if (null == sysUser) {
                                throw new BusinessException("未查询到城市合伙人");
                            } else {
                                // 查询高级合伙人手机号码
                                extend = new UserExtendEntity();
                                extend.setUserId(sysUser.getId());
                                extend = userExtendService.selectOne(extend);
                                if (null == extend) {
                                    throw new BusinessException("未查询到城市合伙人扩展关系");
                                }
                                memberOrderRelaEntity.setCityProvidersId(sysUser.getId()); //城市合伙人id
                                memberOrderRelaEntity.setCityProviders(sysUser.getUsername());//城市合伙人账号
                                memberOrderRelaEntity.setCityProvidersPhone(extend.getPhone());//城市合伙人手机号码
                                memberOrderRelaEntity.setCityProvidersName(sysUser.getNickname());//城市城市合伙人名称
                                agentRule = new AgentProfitsRulesEntity();
                                agentRule.setAgentId(userRelaList.get(3).getUserId());
                                agentRule.setOemCode(oemCode);
                                agentRule = agentProfitsRulesService.selectOne(agentRule);//查询代理商分润规则
                                if (null == agentRule) {
                                    throw new BusinessException("未查询到代理商分润规则");
                                }
                                // 类型：1-开户 2-开票 3-会员升级 4-企业注销
                                if (type == 1) {
                                    memberOrderRelaEntity.setCityProvidersProfitsRate(agentRule.getRegisterFee());//城市合伙人开户分润比率
                                } else if (type == 2) {
                                    memberOrderRelaEntity.setCityProvidersProfitsRate(agentRule.getInvoiceFee());// 城市合伙人开票分润比率
                                } else if (type == 3) {
                                    memberOrderRelaEntity.setCityProvidersProfitsRate(agentRule.getMembershipFee());//城市合伙人会员升级分润比率
                                } else if (type == 4) {
                                    memberOrderRelaEntity.setCityProvidersProfitsRate(agentRule.getCancelCompanyFee());//城市合伙人企业注销分润比率
                                } else if (type == 5) {
                                    memberOrderRelaEntity.setCityProvidersProfitsRate(new BigDecimal(0));//城市合伙人证件领用分润比率
                                }
                            }
                        }
                    }
                }
            }*/
        }
        return memberOrderRelaEntity;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void confirmReceipt(Long memberId, String oemCode, String orderNo,String updateUser) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主表订单信息
        OrderEntity order = orderService.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }

        if (!Objects.equals(order.getUserId(), memberId)) {
           // throw new BusinessException("不是会员的订单");
        }

        // 查询开票订单信息
        InvoiceOrderEntity invOrder = mapper.queryByOrderNo(orderNo);
        if (null == invOrder) {
            throw new BusinessException("未查询到开票订单");
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

        // 开票：0->待创建；1->待付款；2->待审核；3->出票中；4->待发货；5->出库中；6->待收货；7->已签收；8->已取消
        // 如果订单状态为0->待创建；1->待付款；2->待审核；3->出票中；4->待发货；5->出库中则不能确认收货
        //如果是电票，且状态不为出票中，则不能签收
        if(Objects.equals(invOrder.getInvoiceWay(),2) && !Objects.equals(order.getOrderStatus(),InvoiceOrderStatusEnum.IN_TICKETING.getValue())&& !Objects.equals(order.getOrderStatus(),InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue())){
            throw new BusinessException("电子发票当前状态不能操作");
        }else if (Objects.equals(invOrder.getInvoiceWay(),1) && IntervalUtil.isInTheInterval(order.getOrderStatus().toString(), "[" + InvoiceOrderStatusEnum.CREATED.getValue().toString() + "," + InvoiceOrderStatusEnum.OUT_OF_STOCK.getValue().toString() + "]")) {
            throw new BusinessException("未发货的订单无法确认收货");
        } else if (Objects.equals(invOrder.getInvoiceWay(),1) && order.getOrderStatus().equals(InvoiceOrderStatusEnum.SIGNED.getValue())) {
            throw new BusinessException("已签收的订单无法确认收货");
        } else if (order.getOrderStatus().equals(InvoiceOrderStatusEnum.CANCELED.getValue())) {
            throw new BusinessException("已取消的订单无法确认收货");
        } else {

            // 修改主表订单状态
            order.setOrderStatus(InvoiceOrderStatusEnum.SIGNED.getValue());
            order.setUpdateUser(updateUser);
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            // 修改开票订单完成时间
            invOrder.setUpdateUser(member.getMemberAccount());
            invOrder.setUpdateTime(new Date());
            Date completeTime =  new Date();
            invOrder.setCompleteTime(completeTime);
            invOrder.setRemainingWithdrawalAmount(invOrder.getInvoiceAmount());
            invoiceOrderService.editByIdSelective(invOrder);

            // 新增开票订单变更记录
            InvoiceOrderChangeRecordEntity invOrderChange = new InvoiceOrderChangeRecordEntity();
            BeanUtils.copyProperties(invOrder, invOrderChange);
            invOrderChange.setAddTime(new Date());
            invOrderChange.setAddUser(updateUser);
            invOrderChange.setId(null);
            invOrderChange.setOrderStatus(InvoiceOrderStatusEnum.SIGNED.getValue());
            invOrderChange.setRemark("开票订单确认收货");
            invoiceOrderChangeRecordService.insertSelective(invOrderChange);

            // 修改会员资金账号总金额,减少冻结金额
            // 是否已退邮寄费 0-未退 1-已退
            Long changeAmount = 0L;// 变动金额
            if (0 == invOrder.getIsRefundPostageFee()) {
                changeAmount = order.getPayAmount();
                userCapital.setTotalAmount(userCapital.getTotalAmount() - order.getPayAmount());//减少总金额
                userCapital.setBlockAmount(userCapital.getBlockAmount() - order.getPayAmount());//减少冻结金额
            } else {
                changeAmount = order.getPayAmount() - invOrder.getPostageFees(); //变动金额为支付金额减已退邮寄费
                userCapital.setTotalAmount(userCapital.getTotalAmount() - changeAmount);//减少总金额
                userCapital.setBlockAmount(userCapital.getBlockAmount() - changeAmount);//减少冻结金额
            }
            userCapital.setUpdateTime(new Date());
            userCapital.setUpdateUser(member.getMemberAccount());
            userCapitalAccountService.editByIdSelective(userCapital);

            // 添加会员资金账号支出记录
            UserCapitalChangeRecordEntity userCapitalPayChange = new UserCapitalChangeRecordEntity();
            userCapitalPayChange.setCapitalAccountId(userCapital.getId());
            userCapitalPayChange.setUserId(memberId);
            userCapitalPayChange.setUserType(MemberTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-系统用户
            userCapitalPayChange.setOemCode(oemCode);
            userCapitalPayChange.setChangesAmount(changeAmount);//变动金额
            userCapitalPayChange.setChangesBeforeAmount(beforeTotalAmount);//变动前余额
            userCapitalPayChange.setChangesAfterAmount(userCapital.getTotalAmount());//变动后余额
            userCapitalPayChange.setChangesType(CapitalChangeTypeEnum.EXPENDITURE.getValue());
            userCapitalPayChange.setDetailDesc("开票订单余额支付");
            userCapitalPayChange.setOrderType(OrderTypeEnum.INVOICE.getValue());
            userCapitalPayChange.setOrderNo(order.getOrderNo());
            userCapitalPayChange.setAddTime(new Date());
            userCapitalPayChange.setAddUser(updateUser);
            userCapitalPayChange.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
            userCapitalPayChange.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());//钱包类型 1-消费钱包 2-佣金钱包
            userCapitalChangeRecordService.insertSelective(userCapitalPayChange);

            // 修改机构资金账号，增加总金额和可用金额

            // 是否已退邮寄费 0-未退 1-已退
            if (0 == invOrder.getIsRefundPostageFee()) {
                oemCapital.setTotalAmount(oemCapital.getTotalAmount() + order.getPayAmount());//增加总金额
                oemCapital.setAvailableAmount(oemCapital.getAvailableAmount() + order.getPayAmount());//增加可用金额
            } else {
                oemCapital.setTotalAmount(oemCapital.getTotalAmount() + changeAmount);//增加总金额
                oemCapital.setAvailableAmount(oemCapital.getAvailableAmount() + changeAmount);//增加可用金额
            }
            oemCapital.setUpdateTime(new Date());
            oemCapital.setUpdateUser(member.getMemberAccount());
            userCapitalAccountService.editByIdSelective(oemCapital);

            // 添加机构资金账号收入记录
            UserCapitalChangeRecordEntity oemCapitalPayChange = new UserCapitalChangeRecordEntity();
            oemCapitalPayChange.setCapitalAccountId(oemCapital.getId());
            oemCapitalPayChange.setUserId(oemCapital.getUserId());
            oemCapitalPayChange.setUserType(oemCapital.getUserType());// 用户类型 1-会员 2-系统用户
            oemCapitalPayChange.setOemCode(oemCode);
            oemCapitalPayChange.setChangesAmount(changeAmount);//变动金额
            oemCapitalPayChange.setChangesBeforeAmount(oemBeforeTotalAmount);//变动前余额
            oemCapitalPayChange.setChangesAfterAmount(oemCapital.getTotalAmount());//变动后余额
            oemCapitalPayChange.setChangesType(CapitalChangeTypeEnum.INCOME.getValue());
            oemCapitalPayChange.setDetailDesc("开票订单余额支付");
            oemCapitalPayChange.setOrderType(OrderTypeEnum.INVOICE.getValue());
            oemCapitalPayChange.setOrderNo(order.getOrderNo());
            oemCapitalPayChange.setAddTime(new Date());
            oemCapitalPayChange.setAddUser(updateUser);
            oemCapitalPayChange.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
            oemCapitalPayChange.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
            userCapitalChangeRecordService.insertSelective(oemCapitalPayChange);

            // V2.3需求，添加会员消费记录
            MemberConsumptionRecordEntity consumptionRecord = new MemberConsumptionRecordEntity();
            consumptionRecord.setOrderNo(order.getOrderNo());
            consumptionRecord.setOrderType(OrderTypeEnum.INVOICE.getValue());
            consumptionRecord.setMemberId(memberId);

            // 是否已退邮寄费 0-未退 1-已退
            if (0 == invOrder.getIsRefundPostageFee()) {
                consumptionRecord.setConsumptionAmount(invOrder.getServiceFee() - invOrder.getServiceFeeDiscount() + invOrder.getPostageFees()); // 消费金额(服务费 -服务费优惠 + 邮寄费)
            } else {
                consumptionRecord.setConsumptionAmount(invOrder.getServiceFee() - invOrder.getServiceFeeDiscount()); // 消费金额(服务费 - 服务费优惠)
            }
            consumptionRecord.setOemCode(oemCode);
            consumptionRecord.setIsOpenInvoice(0);// 是否已开票 0-未开 1-已开
            consumptionRecord.setAddTime(new Date());
            consumptionRecord.setAddUser(updateUser);
            consumptionRecord.setRemark(order.getProductName());// 备注字段暂用来存储商品名称
            memberConsumptionRecordService.insertSelective(consumptionRecord);

            //添加分润明细数据
            try {
                profitsDetailService.saveProfitsDetailByOrderNo(orderNo, member.getMemberAccount());
            } catch (Exception e) {
                log.error("分润失败：{}", e.getMessage());
                //分润失败
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

            //自助开票或佣金开开票且为电票已完成 发送短信通知用户
            if (invOrder.getInvoiceWay() != null && Objects.equals(invOrder.getInvoiceWay(),2)
                    && (Objects.equals(invOrder.getCreateWay(),1) || Objects.equals(invOrder.getCreateWay(),3))) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("orderNo",invOrder.getOrderNo());
                smsService.sendTemplateSms(member.getMemberPhone(), invOrder.getOemCode(), VerifyCodeTypeEnum.ELECTRONIC_INVOICE_SIGN.getValue(), map, 1);
            }

            // 先开票后补流水/成果，需发短信提醒用户
            if ((invOrder.getIsAfterUploadBankWater() != null && 0 == invOrder.getIsAfterUploadBankWater())||(invOrder.getAchievementStatus() != null && 2 == invOrder.getAchievementStatus())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("uploadInvoiceImgsTimeout", sysDictionaryService.getByCode("upload_invoice_imgs_timeout").getDictValue());
                smsService.sendTemplateSms(member.getMemberPhone(), invOrder.getOemCode(), VerifyCodeTypeEnum.INVOICE_UPLOAD_BANK_WATER.getValue(), map, 1);
            }

            // 统计会员日推广数据
            try {
                orderService.statisticsMemberGeneralize(order, member.getMemberAccount(), 1);
            } catch (Exception e) {
                log.error("统计会员日推广数据失败：{}", e.getMessage());
            }

            // 会员自动升级
            try {
                MemberLevelEntity levelEntity=memberLevelService.findById(member.getMemberLevel());
                if(levelEntity.getLevelNo()<=MemberLevelEnum.BRONZE.getValue()){
                    orderService.memberAutoUpdate(member.getParentMemberId());
                }
                //税务顾问自己也要升级
                if(levelEntity.getLevelNo().equals(MemberLevelEnum.GOLD.getValue())){
                    orderService.memberAutoUpdate(member.getId());
                }
            } catch (Exception e) {
                log.error("会员自动升级失败：{}", e.getMessage());
            }
            //V3.0 订单完成推送给国金
            //订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
            if (order.getChannelPushState().equals(ChannelPushStateEnum.TO_BE_PAY.getValue())){
                try {
                    List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
                    OpenOrderVO vo = new OpenOrderVO();
                    vo.setOrderNo(orderNo);
                    vo.setId(order.getUserId());
                    vo.setCompleteTime(completeTime);
                    vo.setOemCode(order.getOemCode());
                    vo.setOrderType(order.getOrderType());
                    listToBePush.add(vo);
                    rabbitTemplate.convertAndSend("orderPush", listToBePush);
                } catch (Exception e) {
                    log.error("推送失败：{}", e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void xxljobCancelInvOrder(String orderNo) {

        // 修改企业开票额度
        InvoiceOrderEntity invOrder = mapper.queryByOrderNo(orderNo);
        if (Objects.isNull(invOrder)){
            throw new BusinessException("开票订单不存在");
        }
        companyInvoiceRecordMapper.refund(invOrder.getCompanyId(),invOrder.getAddTime(),invOrder.getInvoiceAmount(),"xxljob",new Date());

        // 修改主表订单状态
        OrderEntity order = orderService.queryByOrderNo(orderNo);
        order.setOrderStatus(InvoiceOrderStatusEnum.CANCELED.getValue());
        order.setUpdateUser("xxljob");
        order.setUpdateTime(new Date());
        order.setRemark("24小时未支付自动取消");
        orderMapper.updateByPrimaryKeySelective(order);

        // 新增开票订单变更记录
        InvoiceOrderChangeRecordEntity invOrderChange = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invOrder, invOrderChange);
        invOrderChange.setId(null);
        invOrderChange.setOrderStatus(InvoiceOrderStatusEnum.CANCELED.getValue());
        invOrderChange.setAddUser("xxljob");
        invOrderChange.setAddTime(new Date());
        invOrderChange.setRemark("24小时未支付自动取消");
        invoiceOrderChangeRecordService.insertSelective(invOrderChange);
    }

    /**
     * @Description 创建开票订单实体转换
     * @Author yejian
     * @Date 2019/12/10 16:34
     * @Param CreateInvoiceOrderDTO
     * @Return InvoiceOrderEntity
     */
    public InvoiceOrderEntity transferInvoiceOrderDto2Entity(CreateInvoiceOrderDTO createInvoiceOrderDTO) {
        InvoiceOrderEntity invoiceOrder = new InvoiceOrderEntity();
        invoiceOrder.setCompanyId(createInvoiceOrderDTO.getCompanyId());
        invoiceOrder.setInvoiceAmount(createInvoiceOrderDTO.getInvoiceAmount());
        invoiceOrder.setInvoiceType(createInvoiceOrderDTO.getInvoiceType());
        invoiceOrder.setInvoiceTypeName(createInvoiceOrderDTO.getInvoiceTypeName());
        invoiceOrder.setCompanyName(createInvoiceOrderDTO.getCompanyName());
        invoiceOrder.setCompanyAddress(createInvoiceOrderDTO.getCompanyAddress());
        invoiceOrder.setEin(createInvoiceOrderDTO.getEin());
        invoiceOrder.setPhone(createInvoiceOrderDTO.getPhone());
        invoiceOrder.setBankName(createInvoiceOrderDTO.getBankName());
        invoiceOrder.setBankNumber(createInvoiceOrderDTO.getBankNumber());
        invoiceOrder.setRecipient(createInvoiceOrderDTO.getRecipient());
        invoiceOrder.setRecipientPhone(createInvoiceOrderDTO.getRecipientPhone());
        invoiceOrder.setRecipientAddress(createInvoiceOrderDTO.getRecipientAddress());
        invoiceOrder.setProvinceCode(createInvoiceOrderDTO.getProvinceCode());
        invoiceOrder.setProvinceName(createInvoiceOrderDTO.getProvinceName());
        invoiceOrder.setCityCode(createInvoiceOrderDTO.getCityCode());
        invoiceOrder.setCityName(createInvoiceOrderDTO.getCityName());
        invoiceOrder.setDistrictCode(createInvoiceOrderDTO.getDistrictCode());
        invoiceOrder.setDistrictName(createInvoiceOrderDTO.getDistrictName());
        invoiceOrder.setEmail(createInvoiceOrderDTO.getEmail());
        invoiceOrder.setAddTime(new Date());
        invoiceOrder.setAlertNumber("0");// 默认通知次数为0
        invoiceOrder.setPayType(createInvoiceOrderDTO.getPayType());
        // 钱包类型默认为消费钱包
        if (null == createInvoiceOrderDTO.getWalletType()) {
            createInvoiceOrderDTO.setWalletType(1);
        } else {
            invoiceOrder.setWalletType(createInvoiceOrderDTO.getWalletType());
        }
        // 开票方式默认为自助开票
        if (null == createInvoiceOrderDTO.getCreateWay()) {
            invoiceOrder.setCreateWay(1);
        } else {
            invoiceOrder.setCreateWay(createInvoiceOrderDTO.getCreateWay());
        }
        //保存发票类型 by云财2.7版本
        invoiceOrder.setInvoiceWay(createInvoiceOrderDTO.getInvoiceWay());
        return invoiceOrder;
    }

    /**
     * @Description 创建开票订单实体转换
     * @Author yejian
     * @Date 2019/12/10 16:34
     * @Param CreateInvoiceOrderDTO
     * @Return InvoiceOrderEntity
     */
    public InvoiceOrderEntity transferInvoiceOrderDto2Entity(CompanyInvoiceApiDTO companyInvoiceApiDTO) {
        InvoiceOrderEntity invoiceOrder = new InvoiceOrderEntity();
        invoiceOrder.setCompanyId(companyInvoiceApiDTO.getCompanyId());
        invoiceOrder.setInvoiceAmount(companyInvoiceApiDTO.getInvoiceAmount());
        invoiceOrder.setInvoiceType(companyInvoiceApiDTO.getInvoiceType());
        invoiceOrder.setInvoiceTypeName(InvoiceTypeEnum.getByValue(companyInvoiceApiDTO.getInvoiceType()).getMessage());
        invoiceOrder.setCompanyName(companyInvoiceApiDTO.getCompanyName());
        invoiceOrder.setCompanyAddress(companyInvoiceApiDTO.getCompanyAddress());
        invoiceOrder.setEin(companyInvoiceApiDTO.getEin());
        invoiceOrder.setPhone(companyInvoiceApiDTO.getCompanyPhone());
        invoiceOrder.setBankName(companyInvoiceApiDTO.getBankName());
        invoiceOrder.setBankNumber(companyInvoiceApiDTO.getBankNumber());
        invoiceOrder.setRecipient(companyInvoiceApiDTO.getRecipient());
        invoiceOrder.setRecipientPhone(companyInvoiceApiDTO.getRecipientPhone());
        invoiceOrder.setRecipientAddress(companyInvoiceApiDTO.getRecipientAddress());
        invoiceOrder.setProvinceCode(companyInvoiceApiDTO.getProvinceCode());
        invoiceOrder.setProvinceName(companyInvoiceApiDTO.getProvinceName());
        invoiceOrder.setCityCode(companyInvoiceApiDTO.getCityCode());
        invoiceOrder.setCityName(companyInvoiceApiDTO.getCityName());
        invoiceOrder.setDistrictCode(companyInvoiceApiDTO.getDistrictCode());
        invoiceOrder.setDistrictName(companyInvoiceApiDTO.getDistrictName());
        invoiceOrder.setAddTime(new Date());
        if (null == companyInvoiceApiDTO.getInvoiceWay()) {
            invoiceOrder.setInvoiceWay(1);
        } else {
            invoiceOrder.setInvoiceWay(companyInvoiceApiDTO.getInvoiceWay());
        }
        // 默认通知次数为0
        invoiceOrder.setAlertNumber("0");
        // 钱包类型默认为消费钱包
        invoiceOrder.setWalletType(1);
        // 开票方式默认为自助开票
        invoiceOrder.setCreateWay(1);
        return invoiceOrder;
    }

    /**
     * @Description 企业类型转换为产品类型
     * @Author yejian
     * @Date 2019/12/20 16:34
     * @Param companyType
     * @Param reg 服务费计算要取开户的费用比率
     * @Return Integer
     */
    public Integer companyTypeTransferProductType(Integer companyType) {
        //类型转换：1->个体开户；2->个独开户；3->有限合伙；4->有限责任 对应产品表类型为  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票
        if (companyType == 1) {
            return ProductTypeEnum.INDIVIDUAL_INVOICE.getValue();
        } else if (companyType == 2) {
            return ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue();
        } else if (companyType == 3) {
            return ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue();
        } else if (companyType == 4) {
            return ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue();
        } else {
            return null;
        }
    }

    @Override
    public List<InvoiceOrderDetailVO> listInvoiceOrderDetailByStatus() {
        return mapper.listInvoiceOrderDetailByStatus();
    }

    @Override
    public Map<String, Object> getOrderProgress(long memberId, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果
        //查询订单主表信息
        OrderEntity mainOrder = orderMapper.queryByOrderNo(orderNo);
        if (!Objects.equals(mainOrder.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        // 查询用户信息
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(memberId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        //查询订单明细
        InvoiceOrderDetailVO invOrder = mapper.queryDetailByOrderNo(memberId, orderNo);
        Integer orderStatus = invOrder.getOrderStatus();
        String courierCompanyName = invOrder.getCourierCompanyName();
        String courierNumber = invOrder.getCourierNumber();
        resultMap.put("orderStatus", orderStatus);

        if (!Objects.equals(orderStatus, InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue())
                && !Objects.equals(orderStatus, InvoiceOrderStatusEnum.SIGNED.getValue())) {
            return resultMap;
        }

        // 查询物流信息
        List<LogisticsInfoEntity> logisticsInfoEntities = logisticsInfoService.queryLogisticsInfoList(courierCompanyName, courierNumber, orderNo, member.getMemberAccount());
        // 封装为返回对象
        List<LogisticsInfoVO> list = Lists.newArrayList();
        for (LogisticsInfoEntity logisticsInfo : logisticsInfoEntities) {
            LogisticsInfoVO logisticsInfoVO = new LogisticsInfoVO();
            logisticsInfoVO.setContext(logisticsInfo.getLogisticsInfo());
            logisticsInfoVO.setTime(DateUtil.formatTimesTampDate(logisticsInfo.getLogisticsTime()));
            list.add(logisticsInfoVO);
        }
        resultMap.put("logistics", list);
        return resultMap;
    }


    /**
     * 快递100明细状态转换
     * @param status
     * 0-待发货 1-已揽货 2-运输中 3-派送中 4-待取件  5-已签收 6-已收货 7-退货
     */
    public Integer tranStatus(String status){
        if("在途".equals(status)){
            return 2;
        }else if("揽收".equals(status)){
            return 1;
        }else if("疑难".equals(status)){
            return 0;
        }else if("签收".equals(status)){
            return 5;
        }else if("退签".equals(status)){
            return 7;
        }else if("派件".equals(status)){
            return 3;
        }else if("退回".equals(status)){
            return 7;
        }else{
            return 1;
        }
    }
    @Override
    public int queryNotFinishOrderByCompanyId(Long companyId, String oemCode) {
        return mapper.queryNotFinishOrderByCompanyId(companyId, oemCode);
    }

    @Override
    public int querySomeStatusOrderByCompanyId(Long companyId, String oemCode) {
        return mapper.querySomeStatusOrderByCompanyId(companyId, oemCode);
    }

    @Override
    public int invoiceAmountByDate(Long memberId, String oemCode, Long companyId, String month) {
        return mapper.invoiceAmountByDate(memberId, oemCode, companyId, month);
    }

    @Override
    public InvoiceOrderCountVO getInvOrderCount(Long memberId, String oemCode, Long companyId, String month) {
        return mapper.getInvOrderCount(memberId, oemCode, companyId, month);
    }

    @Override
    public List<MemberCompanyVo> listMemberCompany(Long memberId, String oemCode, CompanyListQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        // --begin modify by Kaven 2020-06-23 兼容佣金钱包-提现-选择开票主体--
        List<MemberCompanyVo> companyList = null;
        if (1 == query.getType() || 2 == query.getType()) {
            companyList = memberCompanyMapper.listMemberCompany(memberId, oemCode, query.getType());
        } else if (3 == query.getType()) {
            // 2.2需求变更，需支持多个开票类目筛选
            List<String> list = Lists.newArrayList();
            String[] categoryBaseIds = query.getCategoryBaseId().split(",");
            for (int i = 0; i < categoryBaseIds.length; i++) {
                list.add(categoryBaseIds[i]);
            }
            query.setCategoryBaseIds(list);
            companyList = memberCompanyMapper.listMemberCompanyForCommission(memberId, oemCode, query.getCategoryBaseIds());
            query.setType(1L);// type重置为1，共用处理增值税个税逻辑
        }
        // --end modify--
        Long allInvAmount = 0L;
        List<InvoiceOrderEntity> invOrderList = new ArrayList<InvoiceOrderEntity>();

        // 查询类型：1->开票企业列表 ；2->我的企业列表 ; 3->选择开票主体
        if (1 == query.getType()) {
            //计算增值税减免剩余额度和个人所得税减免剩余额度
            for (MemberCompanyVo company : companyList) {

                // 增值税减免周期 1-按月 2-按季度
                if (Objects.equals(company.getVatBreaksCycle(), 1)) {
                    // 查询本月开票订单
                    String month = DateUtil.format(new Date(), "yyyy-MM");
                    invOrderList = mapper.InvOrderListOfDate(memberId, oemCode, company.getParkId(), company.getId(), month, null, null, null);
                } else if (Objects.equals(company.getVatBreaksCycle(), 2)) {
                    //查询本季度开票订单
                    int year = DateUtil.getYear(new Date());
                    String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
                    invOrderList = mapper.InvOrderListOfDate(memberId, oemCode, company.getParkId(), company.getId(), null, currQuarter[0], currQuarter[1], null);
                }
                // 累加开票金额
                allInvAmount = invOrderList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum();
                // 判断开票金额是否在优惠政策内
                Long vatBreaksAmount = null == company.getVatBreaksAmount() ? 0L : company.getVatBreaksAmount();
                if (MoneyUtil.moneyComp(new BigDecimal(allInvAmount), new BigDecimal(vatBreaksAmount))) {
                    // 如果累计开票金额大于减免额度
                    company.setVatBreaksRemainAmount(0L);
                } else {
                    // 如果累计开票金额小于减免额度
                    company.setVatBreaksRemainAmount(vatBreaksAmount - allInvAmount);
                }


                // 个人所得税减免周期 1-按月 2-按季度
                if (Objects.equals(company.getIncomeTaxBreaksCycle(), 1L)) {
                    //查询本月开票订单
                    String month = DateUtil.format(new Date(), "yyyy-MM");
                    invOrderList = mapper.InvOrderListOfDate(memberId, oemCode, company.getParkId(), company.getId(), month, null, null, null);
                } else if (Objects.equals(company.getIncomeTaxBreaksCycle(), 2L)) {
                    //查询本季度开票订单
                    int year = DateUtil.getYear(new Date());
                    String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
                    invOrderList = mapper.InvOrderListOfDate(memberId, oemCode, company.getParkId(), company.getId(), null, currQuarter[0], currQuarter[1], null);
                }

                // 累加本开票金额
                allInvAmount = invOrderList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum();

                // 判断开票金额是否在优惠政策内
                Long incomeTaxBreaksAmount = null == company.getIncomeTaxBreaksAmount() ? 0L : company.getIncomeTaxBreaksAmount();
                if (MoneyUtil.moneyComp(new BigDecimal(allInvAmount), new BigDecimal(incomeTaxBreaksAmount))) {
                    // 如果累计开票金额大于减免额度
                    company.setIncomeTaxBreaksRemainAmount(0L);
                } else {
                    // 如果累计开票金额小于减免额度
                    company.setIncomeTaxBreaksRemainAmount(incomeTaxBreaksAmount - allInvAmount);
                }
            }
        }
        return companyList;
    }

    @Override
    public PageInfo<InvoiceOrderByGroupOrderNoVO> pageListByGroupOrderNo(String groupOrderNo, String oemCode, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return new PageInfo(listByGroupOrderNo(groupOrderNo, oemCode));
    }

    @Override
    public List<InvoiceOrderByGroupOrderNoVO> listByGroupOrderNo(String groupOrderNo, String oemCode) {
        return mapper.listByGroupOrderNo(groupOrderNo, oemCode);
    }

    @Override
    @Transactional
    public InvoiceOrderEntity createInvoiceOrderByGroup(MemberCompanyVo company, GroupPaymentAnalysisRecordEntity analysisEntity, InvoiceOrderGroupEntity entity) {
        //创建订单
        CreateInvoiceOrderDTO createEntity = new CreateInvoiceOrderDTO();
        createEntity.setCompanyId(company.getId());
        createEntity.setInvoiceAmount(new BigDecimal(analysisEntity.getInvoiceAmount()).multiply(new BigDecimal("100")).longValue());
        createEntity.setInvoiceType(entity.getInvoiceType());
        createEntity.setInvoiceTypeName(entity.getInvoiceTypeName());
        createEntity.setCompanyName(entity.getCompanyName());
        createEntity.setCompanyAddress(entity.getCompanyAddress());
        createEntity.setEin(entity.getEin());
        createEntity.setPhone(entity.getPhone());
        createEntity.setBankName(entity.getBankName());
        createEntity.setBankNumber(entity.getBankNumber());
        createEntity.setRecipient(entity.getRecipient());
        createEntity.setRecipientPhone(entity.getRecipientPhone());
        createEntity.setRecipientAddress(entity.getRecipientAddress());
        createEntity.setProvinceCode(entity.getProvinceCode());
        createEntity.setProvinceName(entity.getProvinceName());
        createEntity.setCityCode(entity.getCityCode());
        createEntity.setCityName(entity.getCityName());
        createEntity.setDistrictCode(entity.getDistrictCode());
        createEntity.setDistrictName(entity.getDistrictName());
        if (company.getHostingStatus() != null && company.getHostingStatus() == 1) {
            createEntity.setInvoiceWay(entity.getInvoiceWay());
        } else {
            createEntity.setInvoiceWay(InvoiceWayEnum.PAPER.getValue());
        }
        createEntity.setCreateWay(InvoiceCreateWayEnum.GROUP.getValue());
        createEntity.setEmail(entity.getEmail());
        createEntity.setApi(false);
        createEntity.setVatRate(MoneyUtil.yuan2fen(entity.getVatFeeRate()));
        createEntity.setCategoryId(entity.getCategoryGroupId());
        createEntity.setCategoryName(entity.getCategoryGroupName());
        createEntity.setPayType(PayTypeInvoiceEnum.OFFLINE_PAY.getValue());
        String orderNo = createInvoiceOrder(company.getMemberId(), company.getOemCode(), createEntity, null, false);
        //更新集团订单号
        mapper.updateGroupOrderNo(entity.getOrderNo(), orderNo, company.getOemCode());
        //确认订单
        ConfirmInvoiceOrderDTO confirmEntity = new ConfirmInvoiceOrderDTO();
        confirmEntity.setOrderNo(orderNo);
        confirmEntity.setRemark("集团批量开票");
        // 校验并保存收票邮箱 V2.7
        confirmInvoiceOrder(company.getMemberId(), company.getOemCode(), confirmEntity);
        //修改订单状态为待审核(V3.1修改) 集团开票订单为待财务审核状态，子订单为待审核
        orderMapper.updateOrderStatus(orderNo, InvoiceOrderStatusEnum.UNCHECKED.getValue(), null, new Date());
        InvoiceOrderEntity invoiceOrderEntity = mapper.queryByOrderNo(orderNo);
        //保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtil.copyProperties(invoiceOrderEntity, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.UNCHECKED.getValue());
        record.setAddTime(new Date());
        record.setAddUser(record.getUpdateUser());
        invoiceOrderChangeRecordService.insertSelective(record);
        //更新解析记录
        analysisEntity.setAnalysisResult(1);
        analysisEntity.setUpdateTime(new Date());
        analysisEntity.setUpdateUser(analysisEntity.getAddUser());
        groupPaymentAnalysisRecordMapper.updateByPrimaryKeySelective(analysisEntity);

        //重新查一次集团开票状态看看是否改成了已取消
        entity = invoiceOrderGroupMapper.queryByOrderNo(analysisEntity.getGroupOrderNo(), analysisEntity.getOemCode());
        if (!Objects.equals(entity.getOrderStatus(), InvoiceOrderGroupStatusEnum.CANCELED.getValue())) {
            //非已取消结束流程
            return mapper.queryByOrderNo(orderNo);
        }
        //修改订单状态为已取消
        orderMapper.updateOrderStatus(orderNo, InvoiceOrderStatusEnum.CANCELED.getValue(), null, new Date());

       /* record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.CANCELED.getValue());
        record.setAddTime(new Date());
        record.setAddUser(record.getUpdateUser());
        record.setRemark(entity.getRemark());
        invoiceOrderChangeRecordService.insertSelective(record);*/
        //企业可用额度回滚
        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(invoiceOrderEntity.getCompanyId());
        companyInvRecord.setUseInvoiceAmount(companyInvRecord.getUseInvoiceAmount() - invoiceOrderEntity.getInvoiceAmount());//年度已开票金额
        companyInvRecord.setRemainInvoiceAmount(companyInvRecord.getRemainInvoiceAmount() + invoiceOrderEntity.getInvoiceAmount());//年度可开票金额
        companyInvoiceRecordMapper.updateByPrimaryKeySelective(companyInvRecord);

        return mapper.queryByOrderNo(orderNo);
    }

    @Override
    public PageResultVo<InvoiceOrdVO> queryInvoiceOrder(TZBOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        List<InvoiceOrdVO> list = this.mapper.queryInvoiceOrder(query);
        PageInfo<InvoiceOrdVO> pageInfo = new PageInfo<InvoiceOrdVO>(list);

        PageResultVo<InvoiceOrdVO> result = new PageResultVo<InvoiceOrdVO>();
        result.setList(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageSize(query.getPageSize());
        result.setPageNum(query.getPageNumber());
        result.setOrderBy("addTime DESC");
        return result;
    }

    @Override
    public List<OrderNoVO> getUnpaidList(Long memberId, String oemCode, Long companyId) {
        if (null == companyId) {
            throw new BusinessException("企业id不能为空");
        }
        List<OrderNoVO> unpaidList = mapper.getUnpaidList(memberId, oemCode, companyId);
        if (null == unpaidList || unpaidList.isEmpty()) {
            return unpaidList;
        }
        return unpaidList;
    }

    @Override
    public Map<String, Object> checkCrossTime(long memberId, String oemCode, String orderNo) throws BusinessException {
        Map<String, Object> resultMap = new HashMap<String, Object>();// 返回结果
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主表订单信息
        OrderEntity order = orderService.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }
        // 查询开票订单信息
        InvoiceOrderEntity invOrder = new InvoiceOrderEntity();

        // 查询开票订单
        invOrder = invoiceOrderService.queryByOrderNo(orderNo);
        if (null == invOrder) {
            throw new BusinessException("未查询到开票订单");
        }
        resultMap.put("status", order.getOrderStatus());

        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(invOrder.getCompanyId());
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }
        resultMap.put("companyId", company.getId());

        // 查询企业开票额度
//        Example example = new Example(CompanyInvoiceRecordEntity.class);
//        example.createCriteria().andEqualTo("companyId",company.getId()).
//                andEqualTo("oemCode",oemCode).
//                andGreaterThanOrEqualTo("endTime",new Date());
//        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.selectOneByExample(example);
        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(company.getId());
        if (null == companyInvRecord) {
            throw new BusinessException("未查询到企业开票记录");
        } else if (null == companyInvRecord.getRemainInvoiceAmount()) {
            throw new BusinessException("未查询到企业年度可开票金额");
        }
        resultMap.put("remainInvoiceAmount", companyInvRecord.getRemainInvoiceAmount());

        resultMap.put("flag", 0);
        // 查询园区税费政策
//        TaxPolicyEntity taxPolicy = new TaxPolicyEntity();
//        taxPolicy.setParkId(order.getParkId());
//        taxPolicy.setCompanyType(company.getCompanyType());
//        taxPolicy.setStatus(TaxPolicyStatusEnum.ON_SHELF.getValue());
//        taxPolicy = taxPolicyService.selectOne(taxPolicy); //查询园区税费政策
//        if (null == taxPolicy) {
//            throw new BusinessException("未查询到园区税费政策");
//        }
//
//        // 减免周期 1-按月 2-按季度(如果个税减免周期和增值税减免周期不一样，取最小的)
//        Integer breaksCycle = taxPolicy.getVatBreaksCycle() > taxPolicy.getIncomeTaxBreaksCycle().intValue() ? taxPolicy.getIncomeTaxBreaksCycle().intValue() : taxPolicy.getVatBreaksCycle();
//        if (breaksCycle == 1) {
//            // 判断订单创建时间和当前时间的月份是否一致，不一致则提示用户
//            boolean checkTime = DateUtil.sameMonth(order.getAddTime(), new Date());
//            if (checkTime) {
//                resultMap.put("flag", 0);
//            } else {
//                resultMap.put("flag", 1);
//            }
//        } else if (breaksCycle == 2) {
//            //获取当前时间的季度区间
//            int year = DateUtil.getYear(new Date());
//            String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
//
//            // 判断订单创建时间是否在当前季度的区间，不在则提示用户
//            boolean checkTime = DateUtil.isEffectiveDate(order.getAddTime(), DateUtil.parseDefaultDate(currQuarter[0]), DateUtil.parseDefaultDate(currQuarter[1]));
//            if (checkTime) {
//                resultMap.put("flag", 0);
//            } else {
//                resultMap.put("flag", 2);
//            }
//        }
        return resultMap;
    }

    @Override
    public InvoiceWaterCountVO countInvoiceWaterOrder(Long memberId, String oemCode) {
        InvoiceWaterCountVO invoiceWaterCountVO = new InvoiceWaterCountVO();
        //查询类型：1->全部；2->待补传；3->审核中；4->审核通过；5->审核不通过；6->流水前置
        int totalhCount = mapper.listInvoiceWaterOrder(memberId, oemCode, 1L).size();//总数量
        int toBePatchCount = mapper.listInvoiceWaterOrder(memberId, oemCode, 2L).size();//待补传数量
        int toBeAuditCount = mapper.listInvoiceWaterOrder(memberId, oemCode, 3L).size();//审核中数量

        int achievementTotalhCount = mapper.listInvoiceAchievementOrder(memberId, oemCode, 1L).size();//总数量
        int achievementToBePatchCount = mapper.listInvoiceAchievementOrder(memberId, oemCode, 2L).size();//待补传数量
        int achievementToBeAuditCount = mapper.listInvoiceAchievementOrder(memberId, oemCode, 3L).size();//审核中数量
        invoiceWaterCountVO.setWaterOrderCount(totalhCount);
        invoiceWaterCountVO.setWaterToBePatchCount(toBePatchCount);
        invoiceWaterCountVO.setWaterToBeAuditCount(toBeAuditCount);
        invoiceWaterCountVO.setAchievementOrderCount(achievementTotalhCount);
        invoiceWaterCountVO.setAchievementToBePatchCount(achievementToBePatchCount);
        invoiceWaterCountVO.setAchievementToBeAuditCount(achievementToBeAuditCount);
        return invoiceWaterCountVO;
    }

    @Override
    public List<InvoiceWaterOrderVO> listInvoiceWaterOrder(Long memberId, String oemCode, InvWaterOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return mapper.listInvoiceWaterOrder(memberId, oemCode, query.getType());
    }

    @Override
    public List<InvoiceAchievementOrderVO> listInvoiceAchievementOrder(Long memberId, String oemCode, InvWaterOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return mapper.listInvoiceAchievementOrder(memberId, oemCode, query.getType());
    }

    @Override
    public InvoiceWaterOrderVO getInvWaterDetail(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        return mapper.getInvWaterDetail(memberId, oemCode, orderNo);
    }

    @Override
    public InvoiceAchievementOrderVO getInvAchievementDetail(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        return mapper.getInvAchievementDetail(memberId, oemCode, orderNo);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void patchBankWater(Long memberId, String oemCode, InvOrderBankWaterDTO entity) {

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询开票订单信息
        InvoiceOrderEntity invoice = mapper.queryByOrderNo(entity.getOrderNo());
        if (null == invoice) {
            throw new BusinessException("未查询到开票订单");
        }

        if (!Objects.equals(invoice.getBankWaterStatus(), BankWaterStatusEnum.TO_BE_UPLOAD.getValue()) && !Objects.equals(invoice.getBankWaterStatus(), BankWaterStatusEnum.NOT_APPROVED.getValue())) {
            throw new BusinessException("订单已上传流水");
        }
        // 校验企业是否存在
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(invoice.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业"));
        if (MemberCompanyStatusEnum.TAX_CANCELLED.getValue().equals(company.getStatus()) || MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue().equals(company.getStatus())) {
            throw new BusinessException("该企业已注销");
        }

        // 修改开票订单信息
        invoice.setBankWaterStatus(1);
        invoice.setUpdateUser(member.getMemberAccount());
        invoice.setUpdateTime(new Date());
        invoiceOrderService.editByIdSelective(invoice);

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtil.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.SIGNED.getValue());
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setRemark("补传流水提交");
        invoiceOrderChangeRecordService.insertSelective(record);

        try {
            log.info("开票订单补传流水，进行自动派单...");
            OemEntity oemEntity=oemService.getOem(invoice.getOemCode());
            if(oemEntity==null){
                throw new BusinessException("oem机构不存在");
            }
            String oemCodeConfig=null;
            if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
                oemCodeConfig=invoice.getOemCode();
            }
            ReceiveServerVO receiveServer = receiveOrderService.getReceiveServer(oemCodeConfig, entity.getOrderNo(), 2, 3);
            if (null != receiveServer.getRecvOrderUserId()) {
                // 派单成功，返回客服信息
                UserExtendEntity userExtend = new UserExtendEntity();
                userExtend.setUserId(receiveServer.getRecvOrderUserId());
                userExtend = userExtendMapper.selectOne(userExtend);
                if (null == userExtend) {
                    throw new BusinessException("自动派单失败，未找到接单客服拓展信息");
                }
            }

            // 更新工单表的银行流水
            WorkOrderEntity workOrder = new WorkOrderEntity();
            workOrder.setWorkOrderNo(receiveServer.getWorkOrderNo());
            workOrder = workOrderService.selectOne(workOrder);
            if (null == workOrder) {
                throw new BusinessException("未查询到工单");
            }
            workOrder.setAccountStatement(entity.getAccountStatement());
            workOrderService.editByIdSelective(workOrder);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("开票订单补传流水自动派单失败：{}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public List<InvoiceWaterOrderVO> getWaterOrder(Map<String, Object> map) {
        return mapper.getWaterOrder(map);
    }

    @Override
    public List<InvoiceAchievementOrderInfoVO> getAchievementOrder(Map<String, Object> map) {
        return mapper.getAchievementOrder(map);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void patchBankAchievement(Long memberId, String oemCode, InvOrderBankAchievementDTO entity) {
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询开票订单信息
        InvoiceOrderEntity invoice = mapper.queryByOrderNo(entity.getOrderNo());
        if (null == invoice) {
            throw new BusinessException("未查询到开票订单");
        }

        if (!Objects.equals(invoice.getAchievementStatus(), BankAchievementStatusEnum.TO_BE_UPLOAD.getValue()) && !Objects.equals(invoice.getAchievementStatus(), BankAchievementStatusEnum.NOT_APPROVED.getValue())) {
            throw new BusinessException("订单已上传成果交易");
        }
        // 校验企业是否存在
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(invoice.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业"));
        if (MemberCompanyStatusEnum.TAX_CANCELLED.getValue().equals(company.getStatus()) || MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue().equals(company.getStatus())) {
            throw new BusinessException("该企业已注销");
        }

        // 修改开票订单信息
        invoice.setAchievementStatus(BankAchievementStatusEnum.TO_BE_AUDIT.getValue());
        invoice.setUpdateUser(member.getMemberAccount());
        invoice.setUpdateTime(new Date());
        invoiceOrderService.editByIdSelective(invoice);

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtil.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.SIGNED.getValue());
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setRemark("补传成果提交");
        invoiceOrderChangeRecordService.insertSelective(record);

        try {
            log.info("开票订单补传交易成果，进行自动派单...");
            OemEntity oemEntity=oemService.getOem(invoice.getOemCode());
            if(oemEntity==null){
                throw new BusinessException("oem机构不存在");
            }
            String oemCodeConfig=null;
            if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
                oemCodeConfig=invoice.getOemCode();
            }
            ReceiveServerVO receiveServer = receiveOrderService.getReceiveServer(oemCodeConfig, entity.getOrderNo(), 2, 4);
            if (null != receiveServer.getRecvOrderUserId()) {
                // 派单成功，返回客服信息
                UserExtendEntity userExtend = new UserExtendEntity();
                userExtend.setUserId(receiveServer.getRecvOrderUserId());
                userExtend = userExtendMapper.selectOne(userExtend);
                if (null == userExtend) {
                    throw new BusinessException("自动派单失败，未找到接单客服拓展信息");
                }
            }

            // 更新工单表的银行流水
            WorkOrderEntity workOrder = new WorkOrderEntity();
            workOrder.setWorkOrderNo(receiveServer.getWorkOrderNo());
            workOrder = workOrderService.selectOne(workOrder);
            if (null == workOrder) {
                throw new BusinessException("未查询到工单");
            }
            workOrder.setAchievementImgs(entity.getAchievementImgs());
            if(StringUtils.isNotBlank(entity.getAchievementVideo())){
                workOrder.setAchievementVideo(entity.getAchievementVideo());
            }
            workOrderService.editByIdSelective(workOrder);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("开票订单补传交易成果自动派单失败：{}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public List<InvoiceNoticeVO> selectUploadFlowTimeoutOrder(Integer overDays) {
        return this.mapper.selectUploadFlowTimeoutOrder(overDays);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void handleUploadFlowTimeoutNotice(InvoiceNoticeVO invocieOrder, Integer overDays) {
        log.info("----收到处理超时未补传流水的开票订单请求：{}", JSON.toJSONString(invocieOrder));

        // 若新增未通知条数不为0，删除原有通知表记录
        if (invocieOrder.getUnNoticeCount() > 0) {
            Example example = new Example(MessageNoticeEntity.class);
            example.createCriteria().andEqualTo("userId", invocieOrder.getUserId())
                    .andEqualTo("oemCode", invocieOrder.getOemCode())
                    .andEqualTo("businessType", 5)
                    .andEqualTo("isAlert", 0);
            this.messageNoticeService.delByExample(example);

            // 新增新的通知记录
            MessageNoticeEntity entity = new MessageNoticeEntity();
            entity.setIsAlert(0);//是否已弹窗 0-未弹窗 1-已弹窗
            entity.setNoticeTitle("补传流水/成果超时提醒");
            entity.setNoticeContent("您有" + invocieOrder.getTotalCount() + "笔需补传流水/成果的开票订单已签收超过" + overDays + "天还未补传流水/成果，为避免税务风险，请您尽快处理。");
            entity.setUserId(invocieOrder.getUserId());
            entity.setUserType(1);// 用户类型 1-会员 2-后端用户
            entity.setNoticeType(2);// 通知类型  1-短信通知 2-站内通知
            entity.setNoticePosition("2");//通知位置(多个通知位置之间用逗号分割)  1-消息中心 2-首页弹窗
            entity.setOpenMode(3);// 打开方式 1-通知详情 2-h5地址链接 3-小程序功能
            entity.setAddUser("admin");
            entity.setAddTime(new Date());
            entity.setBusinessType(5);
            entity.setStatus(0);// 状态 0-未读 1-已读 2-已下线 3-已取消
            entity.setOemCode(invocieOrder.getOemCode());
            this.messageNoticeService.insertSelective(entity);

            // 更新开票订单表通知状态为“1-已通知”
            this.invoiceOrderService.updateAlertStatus(invocieOrder.getUserId(), invocieOrder.getOemCode(), overDays);
        }


        log.info("----超时未补传流水的开票订单通知任务处理结束。----");
    }

    @Override
    public void updateAlertStatus(Long userId, String oemCode, Integer overDays) {
        this.mapper.updateAlertNumber(userId, oemCode, overDays);
    }

    @Override
    public PageResultVo<ExtendUserVO> listDirectUsers(ExtendUserQuery query) throws BusinessException {
        log.info("分页查询直推用户列表：{}", JSON.toJSONString(query));

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        MemberAccountEntity member = this.memberAccountService.findById(query.getCurrUserId());
        if (null == member) {
            throw new BusinessException("查询失败，当前登录" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setExtendType(member.getExtendType());
        if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
            query.setExtendType(-1);// 标识员工
        }
        List<ExtendUserVO> list = this.mapper.listDirectUsers(query);

        //遍历集合，查询用户最近一次开票信息
        list.stream().forEach(extendUser -> {
            Example example = new Example(OrderEntity.class);
            // 条件使用的是属性名
            example.createCriteria().andEqualTo("oemCode", query.getOemCode()).
                    andEqualTo("userId", extendUser.getUserId()).
                    andEqualTo("orderType", OrderTypeEnum.INVOICE.getValue());
            // 注意：排序使用的是列名
            example.setOrderByClause("ADD_TIME DESC");
            List<OrderEntity> orderList = this.orderService.selectByExample(example);
            if (CollectionUtil.isNotEmpty(orderList)) {
                extendUser.setLastInvoiceAmount(orderList.get(0).getOrderAmount());
                extendUser.setLastInvoiceTime(orderList.get(0).getAddTime());
            }

            // 如果当前用户是城市服务商，查询其推广用户的直推用户数
            if (ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue().equals(member.getExtendType())) {
                MemberExtendQuery eQuery = new MemberExtendQuery();
                eQuery.setUserId(extendUser.getUserId());
                eQuery.setOemCode(query.getOemCode());
                Long extendUserCount = this.memberAccountMapper.queryExtendUserCountNew(eQuery);
                extendUser.setExtendUserCount(extendUserCount);
            }
        });
        return PageResultVo.restPage(list);
    }

    @Override
    public void upgradeToDirect(Long memberId, Long selectUserId) throws BusinessException {

        // 查询选择的会员账号
        MemberAccountEntity member = memberAccountService.findById(selectUserId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 判断操作用户是不是他的上级或者上级城市服务商用户
        if (Objects.equals(memberId, member.getParentMemberId()) || Objects.equals(memberId, member.getUpDiamondId())) {
            if (Objects.equals(member.getMemberType(), 2)) {
                throw new BusinessException("员工无法升级为直客");
            }

            if (Objects.equals(member.getExtendType(), 1)) {
                member.setExtendType(2);
                memberAccountService.editByIdSelective(member);

                // 会员自动升级
                try {
                    MemberLevelEntity levelEntity=memberLevelService.findById(member.getMemberLevel());
                    if(levelEntity.getLevelNo()<=MemberLevelEnum.BRONZE.getValue()){
                        orderService.memberAutoUpdate(member.getParentMemberId());
                    }
                    //税务顾问自己也要升级
                    if(levelEntity.getLevelNo().equals(MemberLevelEnum.GOLD.getValue())){
                        orderService.memberAutoUpdate(member.getId());
                    }
                } catch (BusinessException e) {
                    log.error("会员自动升级失败：{}", e.getMessage());
                }
            } else {
                throw new BusinessException("已升级为直客");
            }
        } else {
            throw new BusinessException("您无权限操作");
        }
    }

    @Override
    public Long sumOrderAmountByCompanyId(Long companyId) {
        return mapper.sumOrderAmountByCompanyId(companyId);
    }

    @Override
    public List<Long> queryInvoiceReach(Long userId, String oemCode, Long minInvoiceAmount) {
        return mapper.queryInvoiceReach(userId, oemCode, minInvoiceAmount);
    }

    @Override
    public List<Long> queryCompanyRegistReach(Long userId, String oemCode) {
        return mapper.queryCompanyRegistReach(userId, oemCode);
    }

    @Override
    @Transactional
    public void editAndSaveHistory(InvoiceOrderEntity invEntity, Integer orderStatus, String userAccount, String hisRemark) {
        Date updateTime = new Date();
        invEntity.setUpdateTime(updateTime);
        invEntity.setUpdateUser(userAccount);
        mapper.updateByPrimaryKeySelective(invEntity);
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invEntity, record);
        record.setId(null);
        record.setAddTime(updateTime);
        record.setAddUser(userAccount);
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setRemark(hisRemark);
        record.setOrderStatus(orderStatus);
        invoiceOrderChangeRecordMapper.insertSelective(record);
    }

    @Override
    @Transactional
    public void editAndSaveHistory(InvoiceOrderEntity invEntity, InvoiceOrderEntity histCopyEntity, Integer orderStatus, String userAccount, String hisRemark) {
        Date updateTime = new Date();
        invEntity.setUpdateTime(updateTime);
        invEntity.setUpdateUser(userAccount);
        mapper.updateByPrimaryKeySelective(invEntity);
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invEntity, record);
        record.setId(null);
        record.setAddTime(updateTime);
        record.setAddUser(userAccount);
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setRemark(hisRemark);
        record.setOrderStatus(orderStatus);
        invoiceOrderChangeRecordMapper.insertSelective(record);
    }

    @Override
    @Transactional
    public void updateInvoiceStatus(InvoiceOrderEntity invEntity, OrderEntity orderEntity, String userAccount, String hisRemark) {
        Date updateTime = new Date();
        //更新订单状态
        orderMapper.updateOrderStatus(orderEntity.getOrderNo(), orderEntity.getOrderStatus(), userAccount, updateTime);
        //保存历史记录
        editAndSaveHistory(invEntity, orderEntity.getOrderStatus(), userAccount, hisRemark);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String createInvoiceOrderForCommission(UserWithdrawDTO userWithdrawDto, String memberAccount) throws BusinessException {
        log.info("城市服务商佣金提现，创建开票订单开始：{},{}", JSON.toJSONString(userWithdrawDto), memberAccount);
        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(userWithdrawDto.getCompanyId());
        //company.setOemCode(userWithdrawDto.getOemCode());
        //company.setMemberId(userWithdrawDto.getUserId());
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException("佣金开票订单创建失败，未查询到企业");
        }
        if (!MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            throw new BusinessException("该类型企业暂不支持开票");
        }
        if(!userWithdrawDto.getUserId().equals(company.getMemberId())){
            throw new BusinessException("佣金开票订单创建失败，企业和用户不匹配");
        }
        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("佣金开票订单创建失败：公司状态异常，无法开票");
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException("佣金开票订单创建失败：公司开票已满额，无法开票");
        }
        // 校验企业托管费是否到期
        Date date = new Date();
        if (MemberCompanyOverdueStatusEnum.OVERDUE.getValue().equals(company.getOverdueStatus()) || company.getEndTime().before(date)) {
            throw new BusinessException("佣金开票订单创建失败：该企业托管费已过期");
        }

        // 查询企业超过15天的企业税单
        List<CompanyTaxBillEntity> companyTaxBillEntities = companyTaxBillService.queryCompanyTaxByOverTime(15, company.getId());
        if (null != companyTaxBillEntities && !companyTaxBillEntities.isEmpty()) {
            throw new BusinessException("佣金开票订单创建失败：该企业存在超时未补缴税单");
        }

        // 查询企业开票额度
//        Example example = new Example(CompanyInvoiceRecordEntity.class);
//        example.createCriteria().andEqualTo("companyId",userWithdrawDto.getCompanyId()).
//                andEqualTo("oemCode",userWithdrawDto.getOemCode()).
//                andGreaterThanOrEqualTo("endTime",new Date());
//        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.selectOneByExample(example);
        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(userWithdrawDto.getCompanyId());

        if (null == companyInvRecord) {
            throw new BusinessException("佣金开票订单创建失败：未查询到企业开票记录");
        }
        TaxPolicyEntity taxPolicyEntity = new TaxPolicyEntity();
        taxPolicyEntity.setParkId(company.getParkId());
        taxPolicyEntity.setCompanyType(company.getCompanyType());
        List<TaxPolicyEntity> taxPolicyList = taxPolicyService.select(taxPolicyEntity);
        if(taxPolicyList == null || taxPolicyList.size()!= 1){
            throw new BusinessException("佣金开票订单创建失败：未找到园区配置");
        }
        taxPolicyEntity = taxPolicyList.get(0);
//        Long useInvoiceAmount = companyInvRecord.getUseInvoiceAmount() == null ? 0L : companyInvRecord.getUseInvoiceAmount();
        MemberCompanyDetailVo memberCompanyDetail = memberCompanyService.getMemberCompanyDetail(company.getMemberId(), company.getOemCode(), company.getId());
        if (null == taxPolicyEntity.getTotalInvoiceAmount()) {
            throw new BusinessException("佣金开票订单创建失败：未查询到园区配置的开票金额");
        } else if (memberCompanyDetail.getRemainInvoiceAmount() < userWithdrawDto.getAmount()) {
            throw new BusinessException("佣金开票订单创建失败：开票金额已超过企业可开票限额");
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("佣金开票订单创建失败：未查询到园区");
        }
        //状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        if (park.getStatus() == 2) {
            throw new BusinessException("企业所在园区已停止使用，如有疑问，请联系客服！");
        }
        if (park.getStatus() == 3) {
            throw new BusinessException("企业所在园区已暂停使用，如有疑问，请联系客服！");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(company.getMemberId());
        if(member==null){
            throw  new BusinessException("会员账号不存在");
        }
        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if(memberLevel==null){
            throw  new BusinessException("会员等级不存在");
        }
        // 查询oem机构信息
        OemEntity oem = oemService.getOem(userWithdrawDto.getOemCode());
        if(oem==null){
            throw  new BusinessException("em机构信息不存在");
        }

        if (CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue().equals(company.getTaxpayerType())) { // 一般纳税人无限额配置，不进行校验
            // 近12个月可开票金额校验
            if (null == companyInvRecord) {
                throw new BusinessException("未查询到企业开票记录");
            } else if (null == companyInvRecord.getRemainInvoiceAmount()) {
                throw new BusinessException("未查询到企业年度可开票金额");
            }
            InvoiceStatisticsViewVO view = Optional.ofNullable(mapper.queryCompanyInvoiceRecordStatisticsView(member.getParentMemberId(), company.getId())).orElse(new InvoiceStatisticsViewVO());
            Long totalInvoiceAmount = taxPolicyEntity.getTotalInvoiceAmount();
            if (totalInvoiceAmount == null) {
                throw new BusinessException("园区税费政策年度开票总额未配置");
            }
            if ((totalInvoiceAmount - view.getUseTotalInvoiceAmount()) < userWithdrawDto.getAmount()) {
                throw new BusinessException("开票金额不能大于近12个月剩余可开票额度");
            }

            // 计算本月累计开票金额
            String monFirstDay = DateUtil.getMonFirstDay();
            Date start = DateTime.parse(monFirstDay).toDate();
            String monLastDay = DateUtil.getMonLastDay();
            Date end = DateTime.parse(monLastDay).toDate();
            List<CountPeriodInvoiceAmountVO> monthList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long monthInvoiceAmount = 0L;
            if (!monthList.isEmpty() && null != monthList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : monthList) {
                    monthInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getMonthInvoiceAmount() != null && monthInvoiceAmount + userWithdrawDto.getAmount() > taxPolicyEntity.getMonthInvoiceAmount()){
                throw new BusinessException("本月累计开票金额不能超过"+taxPolicyEntity.getMonthInvoiceAmount()/100 + "元");
            }
            // 计算本周期累计开票金额
            int year =  DateUtil.getYear(new Date());
            int quarter = Integer.parseInt(DateUtil.getQuarter());
            String[]  s = DateUtil.getCurrQuarter(year, quarter);
            start = DateTime.parse(s[0]).toDate();
            end = DateTime.parse(s[1]).toDate();
            List<CountPeriodInvoiceAmountVO> perList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long periodInvoiceAmount = 0L;
            if (!perList.isEmpty() && null != perList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : perList) {
                    periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getQuarterInvoiceAmount() != null && periodInvoiceAmount + userWithdrawDto.getAmount() > taxPolicyEntity.getQuarterInvoiceAmount()){
                throw new BusinessException("本季累计开票金额不能超过"+taxPolicyEntity.getQuarterInvoiceAmount()/100 + "元");
            }
        }
        // 税务监控
        Map<String, Object> map = invoiceOrderService.taxMonitoringQuery(company.getId(), userWithdrawDto.getAmount());
        if (null != map.get("quotaWarn")) {
            throw new BusinessException("该园区季开票金额超过" + map.get("quotaWarnAmount") + "万会有税务监控风险,建议下季度再继续开票");
        }
        // 保存会员订单关系
        MemberOrderRelaEntity more = getUserTree(member.getId(), userWithdrawDto.getOemCode(), 2);//获取一二级推广人和分润信息
        if (more != null) {
            more.setMemberId(member.getId());
            more.setMemberLevel(memberLevel.getLevelNo());//会员等级
            more.setOemCode(member.getOemCode());
            more.setOemName(oem.getOemName());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            memberOrderRelaService.insertSelective(more);
        }

        // 修改企业开票额度
        if (null == companyInvRecord.getUseInvoiceAmount()) {
            companyInvRecord.setUseInvoiceAmount(userWithdrawDto.getAmount());//年度已开票金额
        } else {
            companyInvRecord.setUseInvoiceAmount(companyInvRecord.getUseInvoiceAmount() + userWithdrawDto.getAmount());//年度已开票金额
        }
        companyInvRecord.setRemainInvoiceAmount(companyInvRecord.getRemainInvoiceAmount() - userWithdrawDto.getAmount());//年度可开票金额
        companyInvoiceRecordService.editByIdSelective(companyInvRecord);

        // 查询产品id
        ProductEntity product = new ProductEntity();
        product.setOemCode(userWithdrawDto.getOemCode());
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(member.getId());

        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setUserId(member.getId());
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.INVOICE.getValue());
        mainOrder.setOrderStatus(InvoiceOrderStatusEnum.WAIT_FOR_PAYMENT.getValue());// 订单状态为"待出款"
        mainOrder.setProductId(product.getId());
        mainOrder.setProductName(product.getProdName());
        mainOrder.setChannelUserId(member.getChannelUserId());
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setOemCode(userWithdrawDto.getOemCode());
        mainOrder.setParkId(company.getParkId());
        mainOrder.setOrderAmount(userWithdrawDto.getAmount());
        mainOrder.setPayAmount(0L);// 支付金额为0
        mainOrder.setServiceFee(0L);// 服务费为0
        mainOrder.setDiscountAmount(0L);// 优惠金额
        mainOrder.setServiceFeeRate(oem.getDiamondCommissionServiceFeeRate());// 服务费率
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(memberAccount);
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());        // 默认设置为消费钱包
        mainOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
       /* OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(mainOrder.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }*/
        //  佣金开票无需推送
        mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        log.info("保存佣金开票订单主表信息：{}", JSON.toJSONString(mainOrder));
        orderService.insertSelective(mainOrder);

        // 保存开票订单信息
        InvoiceOrderEntity invoice = buildInvoiceOrderEntity(userWithdrawDto, company,member);
        invoice.setOemCode(userWithdrawDto.getOemCode());
        invoice.setOrderNo(orderNo);
        invoice.setAddTime(new Date());
        invoice.setAddUser(member.getMemberAccount());
        BigDecimal vatRate = userWithdrawDto.getVatRate();
        // 增值税税率处理
        if (vatRate == null) {
//            List<TaxRulesVatRateVO> vatRateList = taxRulesConfigService.queryTaxRulesVatRate(park.getId(), company.getCompanyType(), );
//            if (CollectionUtils.isEmpty(vatRateList)) {
//                throw new BusinessException("未查询到园区增值税税费规则");
//            }
//            List<TaxRulesVatRateVO> collect = vatRateList.stream().sorted(Comparator.comparing(TaxRulesVatRateVO::getVatRate)).collect(Collectors.toList());
//            vatRate = collect.get(0).getVatRate();
            // v3.6增值税率区分专普票，佣金开票增值税率必传
            throw new BusinessException("增值税率为空");
        }
        invoice.setVatFeeRate(vatRate.divide(BigDecimal.valueOf(100)));

        log.info("保存佣金开票订单信息：{}", JSON.toJSONString(invoice));
        invoiceOrderService.insertSelective(invoice);

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.WAIT_FOR_PAYMENT.getValue());
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setRemark("创建开票订单");

        log.info("保存佣金开票订单变更记录：{}", JSON.toJSONString(record));
        invoiceOrderChangeRecordService.insertSelective(record);

        log.info("佣金开票订单数据保存成功，计算支付/税费等");

        // 计算支付金额
        PayInformationVO payInfo = invoiceOrderService.getInvoicePayInfo(member.getId(), userWithdrawDto.getOemCode(), orderNo, vatRate);
        TaxFeeDetailVO taxFeeDetail = payInfo.getTaxFeeDetail();

        // 修改开票订单信息
        invoice.setVatFee(taxFeeDetail.getPayableVatFee());
        invoice.setVatFeeRate(taxFeeDetail.getVatFeeRate().divide(new BigDecimal(100)));
        invoice.setPersonalIncomeTax(taxFeeDetail.getPayableIncomeTax());
        invoice.setPersonalIncomeTaxRate(taxFeeDetail.getIncomeTaxRate().divide(new BigDecimal(100)));
        invoice.setSurcharge(taxFeeDetail.getPayableSurcharge());
        invoice.setSurchargeRate(taxFeeDetail.getSurchargeRate().divide(new BigDecimal(100)));
        //开票类型由是否已托管决定 V2.7
        CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingMapper.queryByCompanyId(company.getId());
        if(invoice.getInvoiceType().equals(InvoiceTypeEnum.REGISTER.getValue())){
            invoice.setInvoiceWay(InvoiceWayEnum.PAPER.getValue());
        } else{
            Integer status = Optional.ofNullable(companyTaxHostingEntity).map(CompanyTaxHostingEntity::getStatus).orElse(0);
            //status 0-未托管：纸质发票 1-已托管:电子发票
            invoice.setInvoiceWay(Objects.equals(status, 0) ? InvoiceWayEnum.PAPER.getValue() : InvoiceWayEnum.ELECTRON.getValue());
        }
          invoice.setUpdateUser(member.getMemberAccount());
        invoice.setUpdateTime(new Date());
        invoice.setBankWaterStatus(4);
        invoiceOrderService.editByIdSelective(invoice);

        //保存佣金开票的开票记录
        //invoiceRecordService.createInvoiceRecord(invoice,companyTaxHostingEntity,company.getEin(), company.getParkId(), member.getId(), member.getMemberAccount(),true);
        log.info("城市服务商佣金提现，创建开票订单结束。");
        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceOrderEntity createInvoiceOrderByThirdParty(ThirdPartyCreateInoiveIDTO dto, String memberAccount) throws BusinessException {
        log.info("接入方开票，创建开票订单开始：{},{}", JSON.toJSONString(dto), memberAccount);

        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(dto.getCompanyId());
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException("接入方开票开票订单创建失败，未查询到企业");
        }
        if (!MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            throw new BusinessException("该类型企业暂不支持开票");
        }
        if(!dto.getUserId().equals(company.getMemberId())){
            throw new BusinessException("接入方开票订单创建失败，企业和用户不匹配");
        }
        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("第三发开票订单创建失败：公司状态异常，无法开票");
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException("接入方开票订单创建失败：公司开票已满额，无法开票");
        }
        // 校验企业托管费是否到期
        Date date = new Date();
        if (MemberCompanyOverdueStatusEnum.OVERDUE.getValue().equals(company.getOverdueStatus()) || company.getEndTime().before(date)) {
            throw new BusinessException("接入方开票订单创建失败：该企业托管费已过期");
        }

        // 查询企业超过15天的企业税单
        List<CompanyTaxBillEntity> companyTaxBillEntities = companyTaxBillService.queryCompanyTaxByOverTime(15, company.getId());
        if (null != companyTaxBillEntities && !companyTaxBillEntities.isEmpty()) {
            throw new BusinessException("接入方开票订单创建失败：该企业存在超时未补缴税单");
        }
        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(dto.getCompanyId());
        if (null == companyInvRecord) {
            throw new BusinessException("接入方开票订单创建失败：未查询到企业开票记录");
        }
        TaxPolicyEntity taxPolicyEntity = new TaxPolicyEntity();
        taxPolicyEntity.setParkId(company.getParkId());
        taxPolicyEntity.setCompanyType(company.getCompanyType());
        List<TaxPolicyEntity> taxPolicyList = taxPolicyService.select(taxPolicyEntity);
        if(taxPolicyList == null || taxPolicyList.size()!= 1){
            throw new BusinessException("接入方开票订单创建失败：未找到园区配置");
        }
        taxPolicyEntity = taxPolicyList.get(0);
        MemberCompanyDetailVo memberCompanyDetail = memberCompanyService.getMemberCompanyDetail(company.getMemberId(), company.getOemCode(), company.getId());
        if (null == taxPolicyEntity.getTotalInvoiceAmount()) {
            throw new BusinessException("接入方开票订单创建失败：未查询到园区配置的开票金额");
        } else if (memberCompanyDetail.getRemainInvoiceAmount() < dto.getAmount()) {
            throw new BusinessException("接入方开票订单创建失败：开票金额已超过企业可开票限额");
        }
        if (CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue().equals(company.getTaxpayerType())) { // 一般纳税人无限额配置，不进行校验
            // 近12个月可开票金额校验
            if (null == companyInvRecord.getRemainInvoiceAmount()) {
                throw new BusinessException("未查询到企业年度可开票金额");
            }
            MemberAccountEntity member = memberAccountService.findById(company.getMemberId());
            if (null == member) {
                throw new BusinessException("未查询到用户信息");
            }
            InvoiceStatisticsViewVO view = Optional.ofNullable(mapper.queryCompanyInvoiceRecordStatisticsView(member.getParentMemberId(), company.getId())).orElse(new InvoiceStatisticsViewVO());
            Long totalInvoiceAmount = taxPolicyEntity.getTotalInvoiceAmount();
            if (totalInvoiceAmount == null) {
                throw new BusinessException("园区税费政策年度开票总额未配置");
            }
            if ((totalInvoiceAmount - view.getUseTotalInvoiceAmount()) < dto.getAmount()) {
                throw new BusinessException("开票金额不能大于近12个月剩余可开票额度");
            }

            // 计算本月累计开票金额
            String monFirstDay = DateUtil.getMonFirstDay();
            Date start = DateTime.parse(monFirstDay).toDate();
            String monLastDay = DateUtil.getMonLastDay();
            Date end = DateTime.parse(monLastDay).toDate();
            List<CountPeriodInvoiceAmountVO> monthList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long monthInvoiceAmount = 0L;
            if (!monthList.isEmpty() && null != monthList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : monthList) {
                    monthInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getMonthInvoiceAmount() != null && monthInvoiceAmount + dto.getAmount() > taxPolicyEntity.getMonthInvoiceAmount()) {
                throw new BusinessException("本月累计开票金额不能超过" + taxPolicyEntity.getMonthInvoiceAmount() / 100 + "元");
            }
            // 计算本周期累计开票金额
            int year = DateUtil.getYear(new Date());
            int quarter = Integer.parseInt(DateUtil.getQuarter());
            String[] s = DateUtil.getCurrQuarter(year, quarter);
            start = DateTime.parse(s[0]).toDate();
            end = DateTime.parse(s[1]).toDate();
            List<CountPeriodInvoiceAmountVO> perList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long periodInvoiceAmount = 0L;
            if (!perList.isEmpty() && null != perList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : perList) {
                    periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getQuarterInvoiceAmount() != null && (periodInvoiceAmount + dto.getAmount()) > taxPolicyEntity.getQuarterInvoiceAmount()) {
                throw new BusinessException("本季累计开票金额不能超过" + taxPolicyEntity.getQuarterInvoiceAmount() / 100 + "元");
            }
        }
        if(StringUtil.isBlank(company.getEin())) {
            throw new BusinessException("企业税号不存在");
        }
        // 税务监控
        Map<String, Object> map = invoiceOrderService.taxMonitoringQuery(company.getId(), dto.getAmount());
        if (null != map.get("quotaWarn")) {
            throw new BusinessException("该园区季开票金额超过" + map.get("quotaWarnAmount") + "万会有税务监控风险,建议下季度再继续开票");
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("接入方开票订单创建失败：未查询到园区");
        }
        //状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        if (park.getStatus() == 2) {
            throw new BusinessException("企业所在园区已停止使用，如有疑问，请联系客服！");
        }
        if (park.getStatus() == 3) {
            throw new BusinessException("企业所在园区已暂停使用，如有疑问，请联系客服！");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(company.getMemberId());
        if(member==null){
            throw  new BusinessException("会员账号不存在");
        }
        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if(memberLevel==null){
            throw  new BusinessException("会员等级不存在");
        }
        // 查询oem机构信息
        OemEntity oem = oemService.getOem(dto.getOemCode());
        if(oem==null){
            throw  new BusinessException("em机构信息不存在");
        }
        // 保存会员订单关系
        MemberOrderRelaEntity more = getUserTree(member.getId(), dto.getOemCode(), 2);//获取一二级推广人和分润信息
        if (more != null) {
            more.setMemberId(member.getId());
            more.setMemberLevel(memberLevel.getLevelNo());//会员等级
            more.setOemCode(member.getOemCode());
            more.setOemName(oem.getOemName());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            memberOrderRelaService.insertSelective(more);
        }

        // 修改企业开票额度
        if (null == companyInvRecord.getUseInvoiceAmount()) {
            companyInvRecord.setUseInvoiceAmount(dto.getAmount());//年度已开票金额
        } else {
            companyInvRecord.setUseInvoiceAmount(companyInvRecord.getUseInvoiceAmount() + dto.getAmount());//年度已开票金额
        }
        companyInvRecord.setRemainInvoiceAmount(companyInvRecord.getRemainInvoiceAmount() - dto.getAmount());//年度可开票金额
        companyInvoiceRecordService.editByIdSelective(companyInvRecord);

        // 查询产品id
        ProductEntity product = new ProductEntity();
        product.setOemCode(dto.getOemCode());
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(member.getId());
        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setUserId(member.getId());
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.INVOICE.getValue());
        mainOrder.setOrderStatus(InvoiceOrderStatusEnum.UNPAID.getValue());// 订单状态为"待付款"
        mainOrder.setProductId(product.getId());
        mainOrder.setProductName(product.getProdName());
        mainOrder.setChannelUserId(member.getChannelUserId());
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setOemCode(dto.getOemCode());
        mainOrder.setParkId(company.getParkId());
        mainOrder.setOrderAmount(dto.getAmount());
        mainOrder.setPayAmount(0L);// 支付金额为0
        mainOrder.setServiceFee(0L);// 服务费为0
        mainOrder.setDiscountAmount(0L);// 优惠金额
        mainOrder.setServiceFeeRate(new BigDecimal(0));// 服务费率
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(memberAccount);
        mainOrder.setChannelServiceId(member.getChannelServiceId());        // 默认设置为消费钱包
        mainOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        mainOrder.setExternalOrderNo(dto.getExternalOrderNo()); // 业务来源单号
        log.info("保存接入方开票开票订单主表信息：{}", JSON.toJSONString(mainOrder));
        orderService.insertSelective(mainOrder);

        // 保存开票订单信息
        InvoiceOrderEntity invoice = buildInvoiceOrderEntity(dto, company,member);
        invoice.setOemCode(dto.getOemCode());
        invoice.setOrderNo(orderNo);
        invoice.setAddTime(new Date());
        invoice.setAddUser(member.getMemberAccount());
        invoice.setRemark(dto.getRemark());
        invoice.setInvoiceRemark(dto.getInvoiceRemark());
        invoice.setGoodsDetails(dto.getGoodsDetails());
        BigDecimal vatRate = dto.getVatRate();
        // 增值税税率处理
        if (vatRate == null) {
//            List<TaxRulesVatRateVO> vatRateList = taxRulesConfigService.queryTaxRulesVatRate(park.getId(), company.getCompanyType());
//            if (CollectionUtils.isEmpty(vatRateList)) {
//                throw new BusinessException("未查询到园区增值税税费规则");
//            }
//            List<TaxRulesVatRateVO> collect = vatRateList.stream().sorted(Comparator.comparing(TaxRulesVatRateVO::getVatRate)).collect(Collectors.toList());
//            vatRate = collect.get(0).getVatRate();
            // V3.6 增值税率支持专普票，增值税率必传
            throw new BusinessException("增值税率不能为空");
        }
        invoice.setVatFeeRate(vatRate.divide(BigDecimal.valueOf(100)));

        log.info("保存接入方开票订单信息：{}", JSON.toJSONString(invoice));
        invoiceOrderService.insertSelective(invoice);

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.UNPAID.getValue());
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setRemark("创建开票订单");

        log.info("保存接入方开票订单变更记录：{}", JSON.toJSONString(record));
        invoiceOrderChangeRecordService.insertSelective(record);

        //保存订单与商品明细的关系
        if(StringUtils.isNotBlank(dto.getGoodsDetails())){
            List<InvoiceorderGoodsdetailRelaEntity> list = JSONObject.parseArray(dto.getGoodsDetails(),InvoiceorderGoodsdetailRelaEntity.class);
            Long totalAmount = 0L;
            for(InvoiceorderGoodsdetailRelaEntity vo: list){
                totalAmount += vo.getGoodsTotalPrice();
                vo.setOrderNo(mainOrder.getOrderNo());
                vo.setAddTime(mainOrder.getAddTime());
                vo.setAddUser(mainOrder.getAddUser());
                vo.setGoodsTaxRate(invoice.getVatFeeRate());
                BigDecimal goodsPrice = new BigDecimal(vo.getGoodsTotalPrice()).divide(vo.getGoodsQuantity(),4, BigDecimal.ROUND_HALF_UP);
                vo.setGoodsPrice(goodsPrice);
                vo.setGoodsTotalTax(new BigDecimal(vo.getGoodsTotalPrice()).multiply(vo.getGoodsTaxRate()).setScale(0, BigDecimal.ROUND_UP).longValue());
                invoiceorderGoodsdetailRelaService.insertSelective(vo);
            }
            if(!ObjectUtils.equals(totalAmount,invoice.getInvoiceAmount())){
                throw new BusinessException("商品总金额与开票金额不一致，创建订单失败");
            }
        }
        log.info("接入方开票订单数据保存成功，计算支付/税费等");

        // 计算支付金额
        PayInformationVO payInfo = invoiceOrderService.getInvoicePayInfo(member.getId(), dto.getOemCode(), orderNo, vatRate);
        TaxFeeDetailVO taxFeeDetail = payInfo.getTaxFeeDetail();

        // 修改开票订单信息
        invoice.setVatFee(taxFeeDetail.getPayableVatFee());
        invoice.setVatFeeRate(taxFeeDetail.getVatFeeRate().divide(new BigDecimal(100)));
        invoice.setPersonalIncomeTax(taxFeeDetail.getPayableIncomeTax());
        invoice.setPersonalIncomeTaxRate(taxFeeDetail.getIncomeTaxRate().divide(new BigDecimal(100)));
        invoice.setSurcharge(taxFeeDetail.getPayableSurcharge());
        invoice.setSurchargeRate(taxFeeDetail.getSurchargeRate().divide(new BigDecimal(100)));
        CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingMapper.queryByCompanyId(company.getId());
        /*if(invoice.getInvoiceType().equals(InvoiceTypeEnum.REGISTER.getValue())){
            invoice.setInvoiceWay(InvoiceWayEnum.PAPER.getValue());
        } else{
            Integer status = Optional.ofNullable(companyTaxHostingEntity).map(CompanyTaxHostingEntity::getStatus).orElse(0);
            //status 0-未托管：纸质发票 1-已托管:电子发票
            invoice.setInvoiceWay(Objects.equals(status, 0) ? InvoiceWayEnum.PAPER.getValue() : InvoiceWayEnum.ELECTRON.getValue());
        }*/
        invoice.setUpdateUser(member.getMemberAccount());
        invoice.setUpdateTime(new Date());
        invoice.setBankWaterStatus(4);
        invoiceOrderService.editByIdSelective(invoice);

        log.info("第三方创建开票订单结束。");
        return invoice;
    }

    @Override
    public List<TaxRulesVatRateVO> getVatRateList(TaxRulesVatRateQuery query) throws BusinessException {
        if (null == query.getCompanyId()) {
            throw new BusinessException("企业id为空");
        }
        // 查询企业类型
        MemberCompanyEntity company = memberCompanyService.findById(query.getCompanyId());
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }

        List<TaxRulesVatRateVO> vatRateList;
        // 查询增值税税率列表
        if (CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue().equals(company.getTaxpayerType())) {
            // 小规模纳税人
            // 开票类型不能为空
            if (null == query.getInvoiceType()) {
                throw new BusinessException("发票类型为空");
            }
            vatRateList = taxRulesConfigService.queryTaxRulesVatRate(company.getParkId(), company.getCompanyType(), query.getInvoiceType());
            if (null == vatRateList || vatRateList.isEmpty()) {
                throw new BusinessException("该开票类型没有可选税率");
            }
        } else {
            // 一般纳税人，获取开票类目对应的增值税率
            // 开票类目不能为空
            if (null == query.getCategoryBaseId()) {
                throw new BusinessException("开票类目为空");
            }
            List<BigDecimal> vatRates = classificationCodeVatService.queryVatRateByCompanyCategoryId(query.getCategoryBaseId());
            if (CollectionUtil.isEmpty(vatRates)) {
                throw new BusinessException("该开票类目没有可选税率");
            }
            vatRateList = TaxRulesVatRateVO.getTaxRulesVatRateVO(vatRates);
        }
        return vatRateList;
    }

    /**
     * 查询增值税税率列表
     * @param orderNo 订单号
     * @param images 图片地址
     * @param type 1：银行流水，2：业务合同 3：发票图片
     * @param remark 备注
     * @param updateUser 操作人
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public void editInvoiceOrderPic(String orderNo, String images,Integer type,String remark,String updateUser) throws BusinessException{
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if(orderEntity == null){
            throw new BusinessException("订单不存在");
        }
        if(orderEntity.getOrderType().intValue() != 6){
            throw new BusinessException("订单类型不正确");
        }
        InvoiceOrderEntity invoiceOrderEntity =  invoiceOrderService.queryByOrderNo(orderNo);
        if(invoiceOrderEntity == null){
            throw new BusinessException("开票订单不存在");
        }
        //修改 银行流水和业务合同
        if((orderEntity.getOrderStatus().intValue() >= 3 && orderEntity.getOrderStatus().intValue()<= 7)){
           if(type == 1){ //修改银行流水
               if(StringUtils.isNotBlank(remark)){
                   invoiceOrderEntity.setRemark(remark+",");
               }else{
                   invoiceOrderEntity.setRemark("");
               }
               invoiceOrderEntity.setRemark(invoiceOrderEntity.getRemark()+"换前流水|accountStatement");
               //保存原有图片历史记录
               editAndSaveHistory(invoiceOrderEntity,orderEntity.getOrderStatus(),updateUser,invoiceOrderEntity.getRemark());
               //更新开票订单图片
               invoiceOrderEntity.setAccountStatement(images);
               Date updateTime = new Date();
               invoiceOrderEntity.setUpdateTime(updateTime);
               invoiceOrderEntity.setUpdateUser(updateUser);
               mapper.updateByPrimaryKeySelective(invoiceOrderEntity);
           }else if(type == 2){
               if(StringUtils.isNotBlank(remark)){
                   invoiceOrderEntity.setRemark(remark+",");
               }else{
                   invoiceOrderEntity.setRemark("");
               }
               invoiceOrderEntity.setRemark(invoiceOrderEntity.getRemark()+"换前合同|businessContractImgs");
               //保存原有图片历史记录
               editAndSaveHistory(invoiceOrderEntity,orderEntity.getOrderStatus(),updateUser,invoiceOrderEntity.getRemark());
               //更新开票订单图片
               invoiceOrderEntity.setBusinessContractImgs(images);
               Date updateTime = new Date();
               invoiceOrderEntity.setUpdateTime(updateTime);
               invoiceOrderEntity.setUpdateUser(updateUser);
               mapper.updateByPrimaryKeySelective(invoiceOrderEntity);
           }else if(type == 3 && orderEntity.getOrderStatus().intValue() >= 4){
               if(StringUtils.isNotBlank(remark)){
                   invoiceOrderEntity.setRemark(remark+",");
               }else{
                   invoiceOrderEntity.setRemark("");
               }
               invoiceOrderEntity.setRemark(invoiceOrderEntity.getRemark()+"换前发票|invoiceImgs");
               editAndSaveHistory(invoiceOrderEntity,orderEntity.getOrderStatus(),updateUser,invoiceOrderEntity.getRemark());
               //更新开票订单图片
               invoiceOrderEntity.setInvoiceImgs(images);
               Date updateTime = new Date();
               invoiceOrderEntity.setUpdateTime(updateTime);
               invoiceOrderEntity.setUpdateUser(updateUser);
               mapper.updateByPrimaryKeySelective(invoiceOrderEntity);
           }else{
               throw new BusinessException("此开票订单不允许该操作");
           }
        }else{
            throw new BusinessException("此开票订单不允许该操作");
        }
    }

    /**
     * 上传风险承诺函
     * @param orderNo 订单号
     * @param images 图片地址
     * @param remark 备注
     * @param updateUser 操作人
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public void uploadRiskCommitment(String orderNo, String images,String remark,String updateUser) throws BusinessException{
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if(orderEntity == null){
            throw new BusinessException("订单不存在");
        }
        if(orderEntity.getOrderType().intValue() != 6){
            throw new BusinessException("订单类型不正确");
        }
        InvoiceOrderEntity invoiceOrderEntity =  invoiceOrderService.queryByOrderNo(orderNo);
        if(invoiceOrderEntity == null){
            throw new BusinessException("开票订单不存在");
        }
        //修改 银行流水和业务合同
        if(invoiceOrderEntity.getIsAfterUploadBankWater().intValue() == 0 &&
                (invoiceOrderEntity.getBankWaterStatus().intValue() == 0 || invoiceOrderEntity.getBankWaterStatus().intValue() == 3)){
            invoiceOrderEntity.setRemark(remark);
            //更新开票订单图片
            invoiceOrderEntity.setRiskCommitment(images);
            Date updateTime = new Date();
            invoiceOrderEntity.setUpdateTime(updateTime);
            invoiceOrderEntity.setUpdateUser(updateUser);
            mapper.updateByPrimaryKeySelective(invoiceOrderEntity);
            //保存原有图片历史记录
            editAndSaveHistory(invoiceOrderEntity,orderEntity.getOrderStatus(),updateUser,invoiceOrderEntity.getRemark());
        }else{
            throw new BusinessException("此开票订单不允许该操作");
        }
    }

    @Override
    public List<CompanyListApitVO> getCompanyListByQuery(String oemCode, CompanyListApiQuery query) throws BusinessException {
        List<CompanyListApitVO> lists = new ArrayList<CompanyListApitVO>();

        // 查询企业列表
        List<CompanyListApiVO> companyList = memberCompanyMapper.getCompanyListByQuery(oemCode, query.getRegPhone(), query.getCompanyName(), query.getIdCard(), query.getStatus());

        //计算增值税减免剩余额度和个人所得税减免剩余额度
        Long allInvAmount = 0L;
        List<InvoiceOrderEntity> invOrderList = new ArrayList<InvoiceOrderEntity>();
        for (CompanyListApiVO company : companyList) {

            // 增值税减免周期 1-按月 2-按季度
            if (Objects.equals(company.getVatBreaksCycle(), 1)) {
                // 查询本月开票订单
                String month = DateUtil.format(new Date(), "yyyy-MM");
                invOrderList = mapper.InvOrderListOfDate(company.getUserId(), oemCode, company.getParkId(), company.getCompanyId(), month, null, null, null);
            } else if (Objects.equals(company.getVatBreaksCycle(), 2)) {
                //查询本季度开票订单
                int year = DateUtil.getYear(new Date());
                String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
                invOrderList = mapper.InvOrderListOfDate(company.getUserId(), oemCode, company.getParkId(), company.getCompanyId(), null, currQuarter[0], currQuarter[1], null);
            }
            // 累加开票金额
            allInvAmount = invOrderList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum();

            // 判断开票金额是否在优惠政策内
            if (MoneyUtil.moneyComp(new BigDecimal(allInvAmount), new BigDecimal(company.getVatBreaksAmount()))) {
                // 如果累计开票金额大于减免额度
                company.setVatBreaksRemainAmount(0L);
            } else {
                // 如果累计开票金额小于减免额度
                company.setVatBreaksRemainAmount(company.getVatBreaksAmount() - allInvAmount);
            }


            // 个人所得税减免周期 1-按月 2-按季度
            if (Objects.equals(company.getIncomeTaxBreaksCycle(), 1L)) {
                //查询本月开票订单
                String month = DateUtil.format(new Date(), "yyyy-MM");
                invOrderList = mapper.InvOrderListOfDate(company.getUserId(), oemCode, company.getParkId(), company.getCompanyId(), month, null, null, null);
            } else if (Objects.equals(company.getIncomeTaxBreaksCycle(), 2L)) {
                //查询本季度开票订单
                int year = DateUtil.getYear(new Date());
                String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
                invOrderList = mapper.InvOrderListOfDate(company.getUserId(), oemCode, company.getParkId(), company.getCompanyId(), null, currQuarter[0], currQuarter[1], null);
            }

            // 累加本开票金额
            allInvAmount = invOrderList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum();

            // 判断开票金额是否在优惠政策内
            if (MoneyUtil.moneyComp(new BigDecimal(allInvAmount), new BigDecimal(company.getIncomeTaxBreaksAmount()))) {
                // 如果累计开票金额大于减免额度
                company.setIncomeTaxBreaksRemainAmount(0L);
            } else {
                // 如果累计开票金额小于减免额度
                company.setIncomeTaxBreaksRemainAmount(company.getIncomeTaxBreaksAmount() - allInvAmount);
            }

            // 复制一个实体，去除parkId和userId
            CompanyListApitVO companyCopy = new CompanyListApitVO();
            BeanUtils.copyProperties(company, companyCopy);
            lists.add(companyCopy);
        }
        return lists;
    }

    @Override
    @Transactional
    public CompanyInvoiceApiVO companyInvoice(String oemCode, CompanyInvoiceApiDTO entity) throws BusinessException {
        CompanyInvoiceApiVO companyInvoiceApiVO = new CompanyInvoiceApiVO();
        log.info("对外API企业开票开始：{},{}", oemCode, JSON.toJSONString(entity));

        // 判断外部订单号是否全局唯一
        OrderEntity exterOrder = new OrderEntity();
        exterOrder.setExternalOrderNo(entity.getExternalOrderNo());
        List<OrderEntity> exterOrderList = orderService.select(exterOrder);
        if (CollectionUtil.isNotEmpty(exterOrderList)) {
            throw new BusinessException(ErrorCodeEnum.EXTERNAL_ORDER_IS_EXIST);
        }

        // 开票金额最低1000起
        int invoiceAmountLimit = Integer.parseInt(sysDictionaryService.getByCode("invoice_amount_limit").getDictValue());
        if (entity.getInvoiceAmount() < invoiceAmountLimit) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_AMOUNT_MIN_LIMIT);
        }

        // 发票类型为增值税专用发票，校验开户行和银行账号
        if (Objects.equals(entity.getInvoiceType(), 2)) {
            if (StringUtils.isBlank(entity.getBankName()) && StringUtils.isBlank(entity.getBankNumber())) {
                throw new BusinessException(ErrorCodeEnum.INVOICE_MISS_BANKINFO);
            }
        }

        // 校验开票金额和银行流水
        if (entity.getInvoiceAmount() >= 1000000) {
            if (1 == entity.getIsAfterUploadBankWater()) {
                if (StringUtils.isBlank(entity.getAccountStatement())) {
                    throw new BusinessException(ErrorCodeEnum.INVOICE_NEED_BANK_WATER);
                }

                // 判断银行流水截图是否超过上限和文件地址是否存在
                String[] accountStatementArray = entity.getAccountStatement().split(",");
                if (accountStatementArray.length > 9) {
                    throw new BusinessException(ErrorCodeEnum.BANK_STATEMENT_NUM_LIMIT);
                }
                log.info("银行流水截图地址：{}", entity.getAccountStatement());
                for (String url : accountStatementArray) {
                    String bucketName = this.dictionaryService.getByCode("oss_privateBucketName").getDictValue();
                    boolean exists = ossService.doesObjectExist(url, bucketName);
                    if (!exists) {
                        throw new BusinessException(ErrorCodeEnum.BANK_WATER_IMG_EXIST);
                    }
                }
            }

            // 校验业务合同截图
            if (StringUtils.isBlank(entity.getBusinessContractImgs())) {
                throw new BusinessException(ErrorCodeEnum.INVOICE_NEED_BUSINESS_CONTRACT_IMG);
            }

            // 判断业务合同截图是否超过上限和文件地址是否存在
            String[] businessContractImgsArray = entity.getBusinessContractImgs().split(",");
            if (businessContractImgsArray.length > 9) {
                throw new BusinessException(ErrorCodeEnum.BUSINESS_CONTRACT_IMG_NUM_LIMIT);
            }
            log.info("业务合同截图地址：{}", entity.getBusinessContractImgs());
            for (String url : businessContractImgsArray) {
                String bucketName = this.dictionaryService.getByCode("oss_privateBucketName").getDictValue();
                boolean exists = ossService.doesObjectExist(url, bucketName);
                if (!exists) {
                    throw new BusinessException(ErrorCodeEnum.BUSINESS_CONTRACT_IMG_EXIST);
                }
            }
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.queryByAccount(entity.getRegPhone(), oemCode);
        if (null == member) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXISTS);
        }

        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(entity.getCompanyId());
        company.setOemCode(oemCode);
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_EXIST);
        }

        // 校验会员id是否一致
        if (!Objects.equals(member.getId(), company.getMemberId())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_BELONG_REG);
        }

        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_STATUS_ERROR);
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException(ErrorCodeEnum.INVOICE_COMPANY_TOP_UP);
        }

        // 查询企业开票额度
//        Example example = new Example(CompanyInvoiceRecordEntity.class);
//        example.createCriteria().andEqualTo("companyId",entity.getCompanyId()).
//                andEqualTo("oemCode",oemCode).
//                andGreaterThanOrEqualTo("endTime",new Date());
//        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.selectOneByExample(example);
        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(entity.getCompanyId());
        if (null == companyInvRecord) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_COMPANY_NOT_CONFIGURED);
        } else if (null == companyInvRecord.getRemainInvoiceAmount()) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_COMPANY_NOT_CONFIGURED);
        } else if (companyInvRecord.getRemainInvoiceAmount() <= entity.getInvoiceAmount()) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_REMAIN_AMOUNT_ERROR);
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException(ErrorCodeEnum.PARK_NOT_EXIST);
        }
        //状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        if (park.getStatus() == 2) {
            throw new BusinessException(ErrorCodeEnum.PARK_STAUS_ERROR);
        }
        if (park.getStatus() == 3) {
            throw new BusinessException(ErrorCodeEnum.PARK_STAUS_ERROR);
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());

        // 查询oem机构信息
        OemEntity oem = oemService.getOem(oemCode);

        // 查询产品
        ProductEntity product = new ProductEntity();
        product.setOemCode(oemCode);
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException(ErrorCodeEnum.PRODUCT_NOT_CONFIGURED);
        }

        // 修改企业开票额度
        if (null == companyInvRecord.getUseInvoiceAmount()) {
            companyInvRecord.setUseInvoiceAmount(entity.getInvoiceAmount());//年度已开票金额
        } else {
            companyInvRecord.setUseInvoiceAmount(companyInvRecord.getUseInvoiceAmount() + entity.getInvoiceAmount());//年度已开票金额
        }
        companyInvRecord.setRemainInvoiceAmount(companyInvRecord.getRemainInvoiceAmount() - entity.getInvoiceAmount());//年度可开票金额
        companyInvoiceRecordService.editByIdSelective(companyInvRecord);

        //校验开票类目
        CompanyInvoiceCategoryEntity companyInvoiceCategoryEntity = new CompanyInvoiceCategoryEntity();
        companyInvoiceCategoryEntity.setCompanyId(entity.getCompanyId());
        companyInvoiceCategoryEntity.setCategoryName(entity.getCategoryName());
        List list = companyInvoiceCategoryService.select(companyInvoiceCategoryEntity);
        if (list == null || list.size() < 1) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_FIND_CATEGORY);
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(company.getMemberId());

        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setExternalOrderNo(entity.getExternalOrderNo());
        mainOrder.setUserId(company.getMemberId());
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.INVOICE.getValue());
        mainOrder.setOrderStatus(InvoiceOrderStatusEnum.CREATED.getValue());
        mainOrder.setProductId(product.getId());
        mainOrder.setProductName(product.getProdName());
        mainOrder.setOemCode(oemCode);
        mainOrder.setParkId(company.getParkId());
        mainOrder.setOrderAmount(entity.getInvoiceAmount());
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(member.getMemberAccount());
        // 默认设置为消费钱包
        mainOrder.setWalletType(1);
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(mainOrder.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }
        orderService.insertSelective(mainOrder);

        // 保存开票订单信息
        // 处理省市区信息
        String provinceName = Optional.ofNullable(provinceService.getByCode(entity.getProvinceCode())).map(ProvinceEntity::getName).orElseThrow(() -> new BusinessException(ErrorCodeEnum.PROVINCE_CODE_ERROR));
        ;
        entity.setProvinceName(provinceName);
        String cityName = Optional.ofNullable(cityService.getByCode(entity.getCityCode())).map(CityEntity::getName).orElseThrow(() -> new BusinessException(ErrorCodeEnum.CITY_CODE_ERROR));
        ;
        entity.setCityName(cityName);
        String districtName = Optional.ofNullable(districtService.getByCode(entity.getDistrictCode())).map(DistrictEntity::getName).orElseThrow(() -> new BusinessException(ErrorCodeEnum.DISTRICT_CODE_ERROR));
        ;
        entity.setDistrictName(districtName);
        InvoiceOrderEntity invoice = transferInvoiceOrderDto2Entity(entity);
        invoice.setOemCode(oemCode);
        invoice.setOrderNo(orderNo);
        invoice.setAddTime(new Date());
        invoice.setAddUser(member.getMemberAccount());
        invoice.setCategoryName(entity.getCategoryName());
        invoiceOrderService.insertSelective(invoice);

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity createRecord = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invoice, createRecord);
        createRecord.setId(null);
        createRecord.setOrderStatus(InvoiceOrderStatusEnum.CREATED.getValue());
        createRecord.setAddTime(new Date());
        createRecord.setAddUser(member.getMemberAccount());
        invoice.setCategoryName(entity.getCategoryName());
        createRecord.setRemark("对外API创建开票订单");
        invoiceOrderChangeRecordService.insertSelective(createRecord);


        log.info("对外API企业开票订单数据保存成功，计算支付/税费等");
        // 计算支付金额
        CompanyInvoiceTaxCalcApiDTO invoiceTaxCalcDTO = new CompanyInvoiceTaxCalcApiDTO();
        invoiceTaxCalcDTO.setCompanyId(entity.getCompanyId());
        invoiceTaxCalcDTO.setVatFeeRate(entity.getVatFeeRate());
        invoiceTaxCalcDTO.setInvoiceAmount(entity.getInvoiceAmount());
        invoiceTaxCalcDTO.setInvoiceType(entity.getInvoiceType());
        invoiceTaxCalcDTO.setRegPhone(entity.getRegPhone());
        CompanyInvoiceTaxCalcApiVO invoiceTaxCalcVO = invoiceOrderService.invoiceTaxCalc(oemCode, invoiceTaxCalcDTO);

        // 修改订单主表信息
        mainOrder.setOrderStatus(InvoiceOrderStatusEnum.UNCHECKED.getValue());
        mainOrder.setUpdateUser(member.getMemberAccount());
        mainOrder.setUpdateTime(new Date());
        orderService.editByIdSelective(mainOrder);

        // 修改开票订单信息
        //invoice.setCategoryId(entity.getCategoryId());
        invoice.setCategoryName(entity.getCategoryName());
        invoice.setVatFee(invoiceTaxCalcVO.getVatFee());
        invoice.setVatFeeRate(MoneyUtil.moneydiv(invoiceTaxCalcVO.getVatFeeRate(), new BigDecimal("100")));
        invoice.setVatPayment(invoiceTaxCalcVO.getVatPayment());
        invoice.setVatFeeQuota(invoiceTaxCalcVO.getVatFeeQuota());
        invoice.setPersonalIncomeTax(invoiceTaxCalcVO.getPersonalIncomeTax());
        invoice.setPersonalIncomeTaxRate(MoneyUtil.moneydiv(invoiceTaxCalcVO.getPersonalIncomeTaxRate(), new BigDecimal("100")));
        invoice.setIncomeTaxPayment(invoiceTaxCalcVO.getIncomeTaxPayment());
        invoice.setPersonalIncomeTaxQuota(invoiceTaxCalcVO.getPersonalIncomeTaxQuota());
        invoice.setSurcharge(invoiceTaxCalcVO.getSurcharge());
        invoice.setSurchargeRate(MoneyUtil.moneydiv(invoiceTaxCalcVO.getSurchargeRate(), new BigDecimal("100")));
        invoice.setSurchargePayment(invoiceTaxCalcVO.getSurchargePayment());
//        invoice.setServiceFee(invoiceTaxCalcVO.getServiceFee());
        invoice.setInvoiceWay(entity.getInvoiceWay());
        invoice.setAccountStatement(entity.getAccountStatement());
        //invoice.setGoodsName(entity.getGoodsName());
        //invoice.setRemark(entity.getRemark());
        invoice.setUpdateUser(member.getMemberAccount());
        invoice.setUpdateTime(new Date());
        invoice.setBankWaterStatus(4);
        if (null != entity.getIsAfterUploadBankWater()) {
            if (0 == entity.getIsAfterUploadBankWater()) {
                invoice.setBankWaterStatus(0);
            }
        }
        invoice.setIsAfterUploadBankWater(entity.getIsAfterUploadBankWater());
        invoice.setBusinessContractImgs(entity.getBusinessContractImgs());
        invoiceOrderService.editByIdSelective(invoice);

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity confirmRecord = new InvoiceOrderChangeRecordEntity();
        BeanUtil.copyProperties(invoice, confirmRecord);
        confirmRecord.setId(null);
        confirmRecord.setOrderStatus(InvoiceOrderStatusEnum.UNCHECKED.getValue());
        confirmRecord.setAddTime(new Date());
        confirmRecord.setAddUser(member.getMemberAccount());
        confirmRecord.setRemark("对外API确认开票订单");
        invoiceOrderChangeRecordService.insertSelective(confirmRecord);

        companyInvoiceApiVO.setOrderNo(orderNo);
        companyInvoiceApiVO.setOrderStatus(mainOrder.getOrderStatus());
        companyInvoiceApiVO.setAddTime(invoice.getAddTime());
        log.info("对外API企业开票结束。");

        // 订单创建成功进行自动派单
        log.info("订单创建成功进行自动派单...");
        OemEntity oemEntity=oemService.getOem(invoice.getOemCode());
        if(oemEntity==null){
            throw new BusinessException("oem机构不存在");
        }
        String oemCodeConfig=null;
        if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
            oemCodeConfig=invoice.getOemCode();
        }
        Long recvOrderUserId = receiveOrderService.getReceiveServer(oemCodeConfig, orderNo, 2, 2).getRecvOrderUserId();
        if (null != recvOrderUserId) {
            // 派单成功，返回客服信息
            UserExtendEntity userExtend = new UserExtendEntity();
            userExtend.setUserId(recvOrderUserId);
            userExtend = userExtendMapper.selectOne(userExtend);
            if (null == userExtend) {
                throw new BusinessException("自动派单失败，未找到接单客服拓展信息");
            }

            // 更新开票订单的专属客服电话
            invoice.setCustomerServicePhone(userExtend.getPhone());
            invoiceOrderService.editByIdSelective(invoice);
        }
        return companyInvoiceApiVO;
    }

    @Override
    public CompanyInvoiceTaxCalcApiVO invoiceTaxCalc(String oemCode, CompanyInvoiceTaxCalcApiDTO entity) throws BusinessException {
        CompanyInvoiceTaxCalcApiVO invoiceTaxCalcVo = new CompanyInvoiceTaxCalcApiVO();
        invoiceTaxCalcVo.setInvoiceAmount(entity.getInvoiceAmount());

        // 开票金额最低1000起
        int invoiceAmountLimit = Integer.parseInt(sysDictionaryService.getByCode("invoice_amount_limit").getDictValue());
        if (entity.getInvoiceAmount() < invoiceAmountLimit) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_AMOUNT_MIN_LIMIT);
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.queryByAccount(entity.getRegPhone(), oemCode);
        if (null == member) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXISTS);
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
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_STATUS_ERROR);
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException(ErrorCodeEnum.INVOICE_COMPANY_TOP_UP);
        }

        // 校验会员id是否一致
        if (!Objects.equals(member.getId(), company.getMemberId())) {
            throw new BusinessException(ErrorCodeEnum.COMPANY_NOT_BELONG_REG);
        }

        // 查询企业开票额度
//        Example example = new Example(CompanyInvoiceRecordEntity.class);
//        example.createCriteria().andEqualTo("companyId",entity.getCompanyId()).
//                andEqualTo("oemCode",oemCode).
//                andGreaterThanOrEqualTo("endTime",new Date());
//        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.selectOneByExample(example);
        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(entity.getCompanyId());
        if (null == companyInvRecord) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_COMPANY_NOT_CONFIGURED);
        } else if (null == companyInvRecord.getRemainInvoiceAmount()) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_COMPANY_NOT_CONFIGURED);
        } else if (companyInvRecord.getRemainInvoiceAmount() < entity.getInvoiceAmount()) {
            throw new BusinessException(ErrorCodeEnum.INVOICE_REMAIN_AMOUNT_ERROR);
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park.setStatus(1);//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException(ErrorCodeEnum.PARK_STAUS_ERROR);
        }

        // 查询园区税费政策
        TaxPolicyEntity taxPolicy = new TaxPolicyEntity();
        taxPolicy.setParkId(park.getId());
        taxPolicy.setCompanyType(company.getCompanyType());
        taxPolicy.setTaxpayerType(company.getTaxpayerType());
        taxPolicy.setStatus(TaxPolicyStatusEnum.ON_SHELF.getValue());
        taxPolicy.setTaxpayerType(company.getTaxpayerType());
        taxPolicy = taxPolicyService.selectOne(taxPolicy); //查询园区税费政策
        if (null == taxPolicy) {
            throw new BusinessException(ErrorCodeEnum.PARK_RULE_NOT_CONFIGURED);
        }

        // 查询园区增值税税率列表
        List<TaxRulesVatRateVO> vatRateList = taxRulesConfigService.queryTaxRulesVatRate(park.getId(), company.getCompanyType(), entity.getInvoiceType());
        if (CollectionUtils.isEmpty(vatRateList)) {
            throw new BusinessException(ErrorCodeEnum.PARK_RULE_NOT_CONFIGURED);
        }

        // 判断增值税税率是否为空,不为空直接处理，为空则默认取list第一条
        if (null != entity.getVatFeeRate()) {
            boolean flag = false;
            for (TaxRulesVatRateVO taxRule : vatRateList) {
                if (MoneyUtil.moneyCompEquals(taxRule.getVatRate(), entity.getVatFeeRate())) {
                    flag = true;
                }
            }
            if (flag) {
                invoiceTaxCalcVo.setVatFeeRate(entity.getVatFeeRate().setScale(2, BigDecimal.ROUND_DOWN));
                // 增值税费率
                entity.setVatFeeRate(MoneyUtil.moneydiv(entity.getVatFeeRate(), new BigDecimal("100")));
            } else {
                throw new BusinessException(ErrorCodeEnum.INVOICE_VAT_RATE_ERROR);
            }
        } else {
            invoiceTaxCalcVo.setVatFeeRate(vatRateList.get(0).getVatRate().setScale(2, BigDecimal.ROUND_DOWN));
            // 增值税费率
            entity.setVatFeeRate(MoneyUtil.moneydiv(vatRateList.get(0).getVatRate(), new BigDecimal("100")));
        }

        // 查询园区个人所得税税费规则
        TaxRulesConfigEntity taxRule = new TaxRulesConfigEntity();
        taxRule.setParkId(park.getId());
        taxRule.setTaxType(1);
        taxRule.setCompanyType(company.getCompanyType());//企业类型 1-个体工商 2-个体独资 3-有限合伙 4-有限责任
        List<TaxRulesConfigEntity> incomeTaxRuleList = taxRulesConfigService.select(taxRule);
        if (CollectionUtil.isEmpty(incomeTaxRuleList)) {
            throw new BusinessException(ErrorCodeEnum.PARK_RULE_NOT_CONFIGURED);
        }
        // 初始化个人所得税金额阶梯区间和税率区间
        List<BigDecimal> charge = new LinkedList<BigDecimal>();
        List<BigDecimal> price = new LinkedList<BigDecimal>();
        //设置减免政策为第一区间
        charge.add(new BigDecimal(taxPolicy.getIncomeTaxBreaksAmount()));
        price.add(new BigDecimal(0));
        for (TaxRulesConfigEntity level : incomeTaxRuleList) {
            charge.add(MoneyUtil.moneyAdd(new BigDecimal(level.getMaxAmount()), new BigDecimal(taxPolicy.getIncomeTaxBreaksAmount())));
            price.add(MoneyUtil.moneydiv(level.getRate(), new BigDecimal("100")));
        }


        // 查询园区附加税税费规则
        taxRule = new TaxRulesConfigEntity();
        taxRule.setParkId(park.getId());
        taxRule.setTaxType(3);
        taxRule.setCompanyType(company.getCompanyType());//企业类型 1-个体工商 2-个体独资 3-有限合伙 4-有限责任
        TaxRulesConfigEntity surchargeTaxRule = taxRulesConfigService.selectOne(taxRule);
        if (null == surchargeTaxRule) {
            throw new BusinessException(ErrorCodeEnum.PARK_RULE_NOT_CONFIGURED);
        }
        // 附加税税率换算
        invoiceTaxCalcVo.setSurchargeRate(surchargeTaxRule.getRate().setScale(2, BigDecimal.ROUND_DOWN));
        BigDecimal surchargeRate = MoneyUtil.moneydiv(surchargeTaxRule.getRate(), new BigDecimal("100"));

        // -------------------------------------------------------计算增值税---------------------------------------------
        // 增值税减免周期 1-按月 2-按季度
        BigDecimal vatAllInvAmount = new BigDecimal(0);
        int vatCycInvAmount = 0;
        List<InvoiceOrderEntity> vatCycPuinvOrderList = new ArrayList<InvoiceOrderEntity>();
        if (Objects.equals(taxPolicy.getVatBreaksCycle(), 1)) {

            // 查询本月开票订单（本月历史存量+本次开票订单）
            String invDate = DateUtil.format(new Date(), "yyyy-MM");
            List<InvoiceOrderEntity> invOrderMonthList = mapper.InvOrderListOfDate(company.getMemberId(), oemCode, park.getId(), company.getId(), invDate, null, null, null);

            // 累加本月开票金额
            vatAllInvAmount = new BigDecimal(invOrderMonthList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum());
            vatAllInvAmount = MoneyUtil.moneyAdd(vatAllInvAmount, new BigDecimal(entity.getInvoiceAmount()));

            // 累加本月历史存量开票金额
            vatCycInvAmount = mapper.cycInvoiceOrderAmountByDate(company.getMemberId(), oemCode, park.getId(), company.getId(), invDate, null, null, null, null);

            // 查询本月历史存量普票开票订单列表
            vatCycPuinvOrderList = mapper.cycInvoiceOrderListByDate(company.getMemberId(), oemCode, park.getId(), company.getId(), invDate, null, null, null);

        } else if (Objects.equals(taxPolicy.getVatBreaksCycle(), 2)) {
            int year = DateUtil.getYear(new Date());
            //查询本季度开票订单（本季度历史存量+本次开票订单）
            String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
            List<InvoiceOrderEntity> invOrderQuarterList = mapper.InvOrderListOfDate(company.getMemberId(), oemCode, park.getId(), company.getId(), null, currQuarter[0], currQuarter[1], null);

            // 累加本季度开票金额
            vatAllInvAmount = new BigDecimal(invOrderQuarterList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum());
            vatAllInvAmount = MoneyUtil.moneyAdd(vatAllInvAmount, new BigDecimal(entity.getInvoiceAmount()));

            // 累加本季度历史存量开票金额
            vatCycInvAmount = mapper.cycInvoiceOrderAmountByDate(company.getMemberId(), oemCode, park.getId(), company.getId(), null, currQuarter[0], currQuarter[1], null, null);

            // 查询本季度历史存量普票开票订单列表
            vatCycPuinvOrderList = mapper.cycInvoiceOrderListByDate(company.getMemberId(), oemCode, park.getId(), company.getId(), null, currQuarter[0], currQuarter[1], null);
        }

        // 判断历史存量开票金额是否在优惠政策内(大于则用本次开票金额乘以增值税费)
        if (MoneyUtil.moneyComp(new BigDecimal(vatCycInvAmount), new BigDecimal(taxPolicy.getVatBreaksAmount()))) {

            //本次增值税计税金额
            invoiceTaxCalcVo.setVatFeeQuota(entity.getInvoiceAmount());

            // 计算增值税费
            BigDecimal vatfee = MoneyUtil.moneyMul(MoneyUtil.moneydiv(new BigDecimal(entity.getInvoiceAmount()), MoneyUtil.moneyAdd(new BigDecimal(1), entity.getVatFeeRate())), entity.getVatFeeRate());

            // 增值税费向上取整
            invoiceTaxCalcVo.setVatFee(vatfee.setScale(0, BigDecimal.ROUND_UP).longValue());
            invoiceTaxCalcVo.setVatPayment(0L);
        } else {

            // 判断累计开票金额（历史存量+本次开票订单）是否在优惠政策内(大于则计算需补缴的税费,小于等于则按开票类型进行计算)
            if (MoneyUtil.moneyComp(vatAllInvAmount, new BigDecimal(taxPolicy.getVatBreaksAmount()))) {

                //本次增值税计税金额
                invoiceTaxCalcVo.setVatFeeQuota(vatAllInvAmount.longValue());

                // 计算增值税费
                BigDecimal vatfee = MoneyUtil.moneyMul(MoneyUtil.moneydiv(new BigDecimal(entity.getInvoiceAmount()), MoneyUtil.moneyAdd(new BigDecimal(1), entity.getVatFeeRate())), entity.getVatFeeRate());

                // 增值税费向上取整
                invoiceTaxCalcVo.setVatFee(vatfee.setScale(0, BigDecimal.ROUND_UP).longValue());

                //循环存量普票开票订单列表,计算补缴增值税税费
                BigDecimal vatPayment = new BigDecimal(0);
                if (CollectionUtil.isNotEmpty(vatCycPuinvOrderList)) {
                    for (InvoiceOrderEntity cycInvOrder : vatCycPuinvOrderList) {
                        vatPayment = MoneyUtil.moneyAdd(vatPayment, MoneyUtil.moneyMul(MoneyUtil.moneydiv(new BigDecimal(cycInvOrder.getInvoiceAmount()), MoneyUtil.moneyAdd(new BigDecimal(1), cycInvOrder.getVatFeeRate())), cycInvOrder.getVatFeeRate()));
                    }
                }

                // 补缴增值税税费向上取整
                invoiceTaxCalcVo.setVatPayment(vatPayment.setScale(0, BigDecimal.ROUND_UP).longValue());
            } else {

                //发票类型：1->普通发票；2-增值税发票(增值税发票无减免)
                if (1 == entity.getInvoiceType()) {
                    invoiceTaxCalcVo.setVatFeeQuota(0L);
                    invoiceTaxCalcVo.setVatFee(0L);
                    invoiceTaxCalcVo.setVatPayment(0L);
                } else {

                    //本次增值税计税金额
                    invoiceTaxCalcVo.setVatFeeQuota(entity.getInvoiceAmount());

                    // 计算增值税费
                    BigDecimal vatfee = MoneyUtil.moneyMul(MoneyUtil.moneydiv(new BigDecimal(entity.getInvoiceAmount()), MoneyUtil.moneyAdd(new BigDecimal(1), entity.getVatFeeRate())), entity.getVatFeeRate());

                    // 增值税费向上取整
                    invoiceTaxCalcVo.setVatFee(vatfee.setScale(0, BigDecimal.ROUND_UP).longValue());
                    invoiceTaxCalcVo.setVatPayment(0L);
                }
            }
        }

        // 合计增值税税费（增值税税费+补缴增值税税费）
        invoiceTaxCalcVo.setAllVatTax(invoiceTaxCalcVo.getVatFee() + invoiceTaxCalcVo.getVatPayment());


        // -----------------------------------------------------计算附加税-----------------------------------------------
        invoiceTaxCalcVo.setSurcharge(MoneyUtil.moneyMul(new BigDecimal(invoiceTaxCalcVo.getVatFee()), surchargeRate).setScale(0, BigDecimal.ROUND_UP).longValue());
        invoiceTaxCalcVo.setSurchargePayment(MoneyUtil.moneyMul(new BigDecimal(invoiceTaxCalcVo.getVatPayment()), surchargeRate).setScale(0, BigDecimal.ROUND_UP).longValue());
        invoiceTaxCalcVo.setAllSurchargeTax(invoiceTaxCalcVo.getSurcharge() + invoiceTaxCalcVo.getSurchargePayment());


        // -----------------------------------------------------计算个人所得税--------------------------------------------
        // 个人所得税减免周期 1-按月 2-按季度
        BigDecimal incomeAllInvAmount = new BigDecimal(0);
        int incomeCycInvAmount = 0;
        if (Objects.equals(taxPolicy.getIncomeTaxBreaksCycle(), 1L)) {

            // 查询本月开票订单（本月历史存量+本次开票订单）
            String invDate = DateUtil.format(new Date(), "yyyy-MM");
            List<InvoiceOrderEntity> invOrderMonthList = mapper.InvOrderListOfDate(company.getMemberId(), oemCode, park.getId(), company.getId(), invDate, null, null, null);

            // 累加本月开票金额
            incomeAllInvAmount = new BigDecimal(invOrderMonthList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum());
            incomeAllInvAmount = MoneyUtil.moneyAdd(incomeAllInvAmount, new BigDecimal(entity.getInvoiceAmount()));

            // 累加本月历史存量开票金额
            incomeCycInvAmount = mapper.cycInvoiceOrderAmountByDate(company.getMemberId(), oemCode, park.getId(), company.getId(), invDate, null, null, null, null);

        } else if (Objects.equals(taxPolicy.getIncomeTaxBreaksCycle(), 2L)) {
            int year = DateUtil.getYear(new Date());
            // 查询本季度开票订单（本季度历史存量+本次开票订单）
            String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
            List<InvoiceOrderEntity> invOrderQuarterList = mapper.InvOrderListOfDate(company.getMemberId(), oemCode, park.getId(), company.getId(), null, currQuarter[0], currQuarter[1], null);

            // 累加本季度开票金额
            incomeAllInvAmount = new BigDecimal(invOrderQuarterList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum());
            incomeAllInvAmount = MoneyUtil.moneyAdd(incomeAllInvAmount, new BigDecimal(entity.getInvoiceAmount()));

            // 累加本季度历史存量开票金额
            incomeCycInvAmount = mapper.cycInvoiceOrderAmountByDate(company.getMemberId(), oemCode, park.getId(), company.getId(), null, currQuarter[0], currQuarter[1], null, null);
        }

        // 查询累计开票金额的费率区间
        BigDecimal allRate = IntervalUtil.getMoneyRate(incomeAllInvAmount, charge, price);
        BigDecimal personalIncomeTaxRate = MoneyUtil.moneyMul(allRate, new BigDecimal("100")).setScale(2, BigDecimal.ROUND_DOWN);

        // 查询历史存量开票金额的费率区间
        BigDecimal cycRate = IntervalUtil.getMoneyRate(new BigDecimal(incomeCycInvAmount), charge, price);

        // 比较两次费率是否一致
        if (MoneyUtil.moneyCompEquals(allRate, cycRate)) {

            // 设置个人所得税计税金额
            invoiceTaxCalcVo.setPersonalIncomeTaxQuota(entity.getInvoiceAmount());

            // 计算个人所得税费
            BigDecimal personalIncomeTax = MoneyUtil.moneyMul(new BigDecimal(entity.getInvoiceAmount()), allRate);

            // 个人所得税费向上取整
            invoiceTaxCalcVo.setPersonalIncomeTax(personalIncomeTax.setScale(0, BigDecimal.ROUND_UP).longValue());
            invoiceTaxCalcVo.setPersonalIncomeTaxRate(personalIncomeTaxRate);
            invoiceTaxCalcVo.setIncomeTaxPayment(0L);
        } else {
            // 设置个人所得税计税金额
            invoiceTaxCalcVo.setPersonalIncomeTaxQuota(incomeAllInvAmount.longValue());

            // 计算个人所得税费
            BigDecimal personalIncomeTax = MoneyUtil.moneyMul(new BigDecimal(entity.getInvoiceAmount()), allRate);

            // 个人所得税费向上取整
            invoiceTaxCalcVo.setPersonalIncomeTax(personalIncomeTax.setScale(0, BigDecimal.ROUND_UP).longValue());
            invoiceTaxCalcVo.setPersonalIncomeTaxRate(personalIncomeTaxRate);

            // 计算补缴个人所得税税费
            //当月/季累计开票所得税费差额=当月/季累计开票金额*（本次开票适用所得税税率-当月/季累计开票金额适用所得税税率）
            BigDecimal incomeTaxPayment = MoneyUtil.moneyMul(new BigDecimal(incomeCycInvAmount), MoneyUtil.moneySub(allRate, cycRate));

            // 补缴个人所得税税费向上取整
            invoiceTaxCalcVo.setIncomeTaxPayment(incomeTaxPayment.setScale(0, BigDecimal.ROUND_UP).longValue());
        }

        // 合计个人所得税税费（个人所得税税费+补缴个人所得税税费）
        invoiceTaxCalcVo.setAllIncomeTax(invoiceTaxCalcVo.getPersonalIncomeTax() + invoiceTaxCalcVo.getIncomeTaxPayment());

        // 合计所有税费（合计增值税税费+合计附加税税费+合计个人所得税税费）
        invoiceTaxCalcVo.setAllTax(invoiceTaxCalcVo.getAllVatTax() + invoiceTaxCalcVo.getAllSurchargeTax() + invoiceTaxCalcVo.getAllIncomeTax());
        return invoiceTaxCalcVo;
    }

    @Override
    @Transactional
    public Integer patchBankWater(String oemCode, InvOrderBankWaterApiDTO entity) {

        // 查询订单主表信息
        OrderEntity mainOrder = orderMapper.queryByOrderNo(entity.getOrderNo());
        if (null == mainOrder) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NO_NOT_EXIST);
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.queryByAccount(entity.getRegPhone(), oemCode);
        if (null == member) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXISTS);
        }

        // 校验会员id是否一致
        if (!Objects.equals(member.getId(), mainOrder.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_BELONG_REG);
        }

        // 未签收的订单无法申请补传
        if (!mainOrder.getOrderStatus().equals(InvoiceOrderStatusEnum.SIGNED.getValue())) {
            throw new BusinessException(ErrorCodeEnum.BANK_STATEMENT_NOT_SIGNED);
        }

        // 查询开票订单信息
        InvoiceOrderEntity invoice = mapper.queryByOrderNo(entity.getOrderNo());
        if (null == invoice) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NO_NOT_EXIST);
        }

        if (!Objects.equals(invoice.getBankWaterStatus(), BankWaterStatusEnum.TO_BE_UPLOAD.getValue()) && !Objects.equals(invoice.getBankWaterStatus(), BankWaterStatusEnum.NOT_APPROVED.getValue())) {
            throw new BusinessException(ErrorCodeEnum.BANK_STATEMENT_IS_UPLOAD);
        }

        // 判断文件是否超过上限和文件地址是否存在
        String[] accountStatementArray = entity.getAccountStatement().split(",");
        if (accountStatementArray.length > 9) {
            throw new BusinessException(ErrorCodeEnum.BANK_STATEMENT_NUM_LIMIT);
        }
        log.info("银行流水截图地址：{}", entity.getAccountStatement());
        for (String url : accountStatementArray) {
            String bucketName = this.dictionaryService.getByCode("oss_privateBucketName").getDictValue();
            boolean exists = ossService.doesObjectExist(url, bucketName);
            if (!exists) {
                throw new BusinessException(ErrorCodeEnum.OSS_FILE_EXIST);
            }
        }

        // 修改开票订单信息
        invoice.setBankWaterStatus(1);
        invoice.setUpdateUser(member.getMemberAccount());
        invoice.setUpdateTime(new Date());
        invoiceOrderService.editByIdSelective(invoice);

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtil.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.SIGNED.getValue());
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setRemark("对外API补传流水提交");
        invoiceOrderChangeRecordService.insertSelective(record);

        try {
            log.info("开票订单补传流水，进行自动派单...");
            OemEntity oemEntity=oemService.getOem(invoice.getOemCode());
            if(oemEntity==null){
                throw new BusinessException("oem机构不存在");
            }
            String oemCodeConfig=null;
            if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
                oemCodeConfig=invoice.getOemCode();
            }
            ReceiveServerVO receiveServer = receiveOrderService.getReceiveServer(oemCodeConfig, entity.getOrderNo(), 2, 3);
            if (null != receiveServer.getRecvOrderUserId()) {
                // 派单成功，返回客服信息
                UserExtendEntity userExtend = new UserExtendEntity();
                userExtend.setUserId(receiveServer.getRecvOrderUserId());
                userExtend = userExtendMapper.selectOne(userExtend);
                if (null == userExtend) {
                    throw new BusinessException(ErrorCodeEnum.WORK_ORDER_ERROR);
                }
            }

            // 更新工单表的银行流水
            WorkOrderEntity workOrder = new WorkOrderEntity();
            workOrder.setWorkOrderNo(receiveServer.getWorkOrderNo());
            workOrder = workOrderService.selectOne(workOrder);
            workOrder.setAccountStatement(entity.getAccountStatement());
            workOrderService.editByIdSelective(workOrder);
        } catch (Exception e) {
            log.error("开票订单补传流水自动派单失败：{}", e.getMessage());
        }

        return invoice.getBankWaterStatus();
    }

    @Override
    public List<InvoiceWaterOrderApiVO> getInvWaterOrderListByQuery(String oemCode, InvoiceWaterOrderApiQuery query) {
        return mapper.getInvWaterOrderListByQuery(oemCode, query.getOrderNo(), query.getCompanyName(), query.getBankWaterStatus(), query.getRegPhone());
    }

    @Override
    public void updateRemainingWithdrawAmount(String invoiceOrderNo, Long withdrawalAmount, int flag) throws BusinessException {
        log.info("更新开票订单剩余可提现额度:{},{},{}", invoiceOrderNo, withdrawalAmount, flag);
        if (null == withdrawalAmount) {
            withdrawalAmount = 0L;
        }
        this.mapper.updateRemainWithdrawAmount(invoiceOrderNo, withdrawalAmount, flag);
    }

    /**
     * @Description 构建接入方开票订单从表信息
     * @Author HZ
     * @Date 2021/9/7 10:15 上午
     * @Return InvoiceOrderEntity
     * @Exception
     */
    public InvoiceOrderEntity buildInvoiceOrderEntity(ThirdPartyCreateInoiveIDTO dto, MemberCompanyEntity company, MemberAccountEntity memberAccountEntity) throws BusinessException {
        InvoiceOrderEntity invoiceOrder = new InvoiceOrderEntity();
        invoiceOrder.setCompanyId(dto.getCompanyId());
        invoiceOrder.setInvoiceAmount(dto.getAmount());
        invoiceOrder.setInvoiceType(dto.getInvoiceType());
        invoiceOrder.setInvoiceTypeName(InvoiceTypeEnum.getByValue(dto.getInvoiceType()).getMessage());
        if(StringUtil.isNotBlank(dto.getCategoryName()) && StringUtil.isBlank(dto.getGoodsDetails())) {
            CompanyInvoiceCategoryEntity companyInvoiceCategoryEntity = null;
            if (StringUtil.isNotBlank(dto.getCategoryName())) {
                CompanyInvoiceCategoryEntity categoryEntity = new CompanyInvoiceCategoryEntity();
                categoryEntity.setCompanyId(company.getId());
                List<CompanyInvoiceCategoryEntity> entities = companyInvoiceCategoryService.select(categoryEntity);
                String[] categoryNames = dto.getCategoryName().split(",");
                for (String name : categoryNames) {
                    for (CompanyInvoiceCategoryEntity entity : entities) {
                        if (Objects.equals(entity.getCategoryName(), name)) {
                            companyInvoiceCategoryEntity = entity;
                            break;
                        }
                    }
                }
            } else {
                companyInvoiceCategoryEntity = companyInvoiceCategoryService.queryOemInvoiceCategory(company.getId());
            }
            if (companyInvoiceCategoryEntity == null) {
                throw new BusinessException("创建接入方开票订单失败：未找到oem机构开票类目信息");
            }
            invoiceOrder.setCategoryId(companyInvoiceCategoryEntity.getCategoryBaseId());
            invoiceOrder.setCategoryName(companyInvoiceCategoryEntity.getCategoryName());
        }
        invoiceOrder.setCompanyName(dto.getCompanyName());
        invoiceOrder.setCompanyAddress(dto.getCompanyAddress());
        invoiceOrder.setEin(dto.getEin());
        invoiceOrder.setPhone(dto.getPhone());
        invoiceOrder.setBankName(dto.getBankName());
        invoiceOrder.setBankNumber(dto.getBankNumber());
        invoiceOrder.setRecipient(dto.getRecipient());
        invoiceOrder.setRecipientPhone(dto.getRecipientPhone());
        invoiceOrder.setRecipientAddress(dto.getRecipientAddress());
        ProvinceEntity provinceEntity = null;
        if(StringUtil.isNotBlank(dto.getProvinceName())){
            provinceEntity=provinceService.getByName(dto.getProvinceName().trim());
            if(provinceEntity==null){
                throw new BusinessException("创建接入方开票订单失败：省不存在");
            }
            invoiceOrder.setProvinceCode(provinceEntity.getCode());
            invoiceOrder.setProvinceName(dto.getProvinceName());
        }
        CityEntity cityEntity = null;
        if(null != provinceEntity && StringUtil.isNotBlank(dto.getCityName())) {
            cityEntity=cityService.getByName(dto.getCityName().trim(), provinceEntity.getCode());
            if(cityEntity==null){
                throw new BusinessException("创建接入方开票订单失败：市不存在");
            }
            invoiceOrder.setCityCode(cityEntity.getCode());
            invoiceOrder.setCityName(dto.getCityName());
        }
        if(null != cityEntity && StringUtil.isNotBlank(dto.getDistrictName())) {
            DistrictEntity districtEntity=districtService.getByName(dto.getDistrictName().trim(), cityEntity.getCode());
            if(districtEntity==null){
                throw new BusinessException("创建第三方订单失败：区不存在");
            }
            invoiceOrder.setDistrictCode(districtEntity.getCode());
            invoiceOrder.setDistrictName(dto.getDistrictName());
        }

        invoiceOrder.setAddTime(new Date());
        invoiceOrder.setAlertNumber("0");// 默认通知次数为0
        invoiceOrder.setEmail(dto.getEmail());
        // 钱包类型设置为消费钱包
        invoiceOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        // 开票方式设置为接入方开票
        invoiceOrder.setCreateWay(InvoiceCreateWayEnum.THIRDPRATY.getValue());
        // 服务费、支付金额、邮寄费等全设置为0
        invoiceOrder.setServiceFee(0L);
        invoiceOrder.setServiceFeeDiscount(0L);
        invoiceOrder.setPayAmount(0L);
        if (Objects.equals(dto.getInvoiceWay(), 4)) {
            dto.setInvoiceWay(1);
        } else if (Objects.equals(dto.getInvoiceWay(), 3)) {
            CompanyTaxHostingEntity companyTaxHostingEntity=companyTaxHostingService.getCompanyTaxHostingByCompanyId(dto.getCompanyId(),null);
            dto.setInvoiceWay(null == companyTaxHostingEntity ? 1 :2);
        }
        invoiceOrder.setInvoiceWay(dto.getInvoiceWay());
        Long postageFees=0L;
        if (Objects.equals(InvoiceWayEnum.PAPER.getValue(), invoiceOrder.getInvoiceWay())) {
            postageFees = parkService.findById(memberCompanyService.findById(dto.getCompanyId()).getParkId()).getPostageFees();
        }
        invoiceOrder.setPostageFees(postageFees);
        invoiceOrder.setPayType(PayTypeInvoiceEnum.OFFLINE_PAY.getValue());
        return invoiceOrder;
    }
    /**
     * @Description 构建佣金开票订单从表信息
     * @Author Kaven
     * @Date 2020/6/30 10:15 上午
     * @Param userWithdrawDto
     * @Return InvoiceOrderEntity
     * @Exception
     */
    public InvoiceOrderEntity buildInvoiceOrderEntity(UserWithdrawDTO userWithdrawDto, MemberCompanyEntity company, MemberAccountEntity memberAccountEntity) throws BusinessException {
        InvoiceOrderEntity invoiceOrder = new InvoiceOrderEntity();
        invoiceOrder.setCompanyId(userWithdrawDto.getCompanyId());
        invoiceOrder.setInvoiceAmount(userWithdrawDto.getAmount());
        invoiceOrder.setInvoiceType(userWithdrawDto.getInvoiceType());
        invoiceOrder.setInvoiceTypeName(InvoiceTypeEnum.getByValue(userWithdrawDto.getInvoiceType()).getMessage());

//        invoiceOrder.setCategoryName(iiboe.getCategoryName());
        //查询开票类目 V2.7
        //查询开票类目 V3.0
        CompanyInvoiceCategoryEntity companyInvoiceCategoryEntity=null;
        if(StringUtil.isNotBlank(userWithdrawDto.getCategoryName())){
            CompanyInvoiceCategoryEntity categoryEntity= new CompanyInvoiceCategoryEntity();
            categoryEntity.setCompanyId(company.getId());
            List<CompanyInvoiceCategoryEntity> entities = companyInvoiceCategoryService.select(categoryEntity);
            String [] categoryNames=userWithdrawDto.getCategoryName().split(",");
            for (String name:categoryNames) {
                for (CompanyInvoiceCategoryEntity entity :entities) {
                    if(Objects.equals(entity.getCategoryName(),name)){
                        companyInvoiceCategoryEntity=entity;
                        break;
                    }
                }
            }
        }else{
            companyInvoiceCategoryEntity=companyInvoiceCategoryService.queryOemInvoiceCategory(company.getId());
        }
        if (companyInvoiceCategoryEntity == null) {
            throw new BusinessException("创建佣金提现订单失败：未找到oem机构开票类目信息");
        }
        invoiceOrder.setCategoryId(companyInvoiceCategoryEntity.getCategoryBaseId());
        invoiceOrder.setCategoryName(companyInvoiceCategoryEntity.getCategoryName());
        invoiceOrder.setCompanyName(userWithdrawDto.getCompanyName());
        invoiceOrder.setCompanyAddress(userWithdrawDto.getCompanyAddress());
        invoiceOrder.setEin(userWithdrawDto.getEin());
        invoiceOrder.setPhone(userWithdrawDto.getPhone());
        invoiceOrder.setBankName(userWithdrawDto.getBankName());
        invoiceOrder.setBankNumber(userWithdrawDto.getBankNumber());
        invoiceOrder.setRecipient(userWithdrawDto.getRecipient());
        invoiceOrder.setRecipientPhone(userWithdrawDto.getRecipientPhone());
        invoiceOrder.setRecipientAddress(userWithdrawDto.getRecipientAddress());
        ProvinceEntity provinceEntity=provinceService.getByName(userWithdrawDto.getProvinceName().trim());
        if(provinceEntity==null){
            throw new BusinessException("创建佣金提现订单失败：省不存在");
        }
        invoiceOrder.setProvinceCode(provinceEntity.getCode());
        invoiceOrder.setProvinceName(userWithdrawDto.getProvinceName());
        CityEntity cityEntity=new CityEntity();
        cityEntity.setProvinceCode(provinceEntity.getCode());
        cityEntity.setName(userWithdrawDto.getCityName());
        cityEntity = cityService.selectOne(cityEntity);
        if(cityEntity==null){
            throw new BusinessException("创建佣金提现订单失败：市不存在");
        }
        invoiceOrder.setCityCode(cityEntity.getCode());
        invoiceOrder.setCityName(userWithdrawDto.getCityName());
        DistrictEntity districtEntity=new DistrictEntity();
        districtEntity.setCityCode(cityEntity.getCode());
        districtEntity.setName(userWithdrawDto.getDistrictName());
        districtEntity = districtService.selectOne(districtEntity);
        if(districtEntity==null){
            throw new BusinessException("创建佣金提现订单失败：区不存在");
        }
        invoiceOrder.setDistrictCode(districtEntity.getCode());
        invoiceOrder.setDistrictName(userWithdrawDto.getDistrictName());
        //v3.0
        invoiceOrder.setChannelCode(memberAccountEntity.getChannelCode());
        invoiceOrder.setChannelServiceId(memberAccountEntity.getChannelServiceId());
        invoiceOrder.setAddTime(new Date());
        invoiceOrder.setAlertNumber("0");// 默认通知次数为0
        invoiceOrder.setEmail(userWithdrawDto.getEmail());
        // 钱包类型设置为消费钱包
        invoiceOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        // 开票方式设置为佣金开票
        invoiceOrder.setCreateWay(InvoiceCreateWayEnum.COMMISSION.getValue());
        invoiceOrder.setPayType(PayTypeInvoiceEnum.OFFLINE_PAY.getValue());
        // 服务费、支付金额、邮寄费等全设置为0
        invoiceOrder.setServiceFee(0L);
        invoiceOrder.setServiceFeeDiscount(0L);
        invoiceOrder.setPayAmount(0L);
        invoiceOrder.setPostageFees(0L);
        return invoiceOrder;
    }

    @Override
    @Transactional
    public void refundPostageFee(InvoiceOrderEntity invEntity, OrderEntity orderEntity, String useraccount) {
        String remark = "退快递费";
        editAndSaveHistory(invEntity, orderEntity.getOrderStatus(), useraccount, remark);
        UserCapitalAccountEntity userCapital = userCapitalAccountService.queryByUserIdAndType(orderEntity.getUserId(), UserTypeEnum.MEMBER.getValue(), orderEntity.getOemCode(), WalletTypeEnum.CONSUMER_WALLET.getValue());
        //添加支付流水
        PayWaterEntity water = new PayWaterEntity();
        water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
        water.setOrderNo(orderEntity.getOrderNo());
        water.setMemberId(orderEntity.getUserId());
        water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setOemCode(orderEntity.getOemCode());
        water.setOemName(Optional.ofNullable(oemService.getOem(orderEntity.getOemCode())).map(OemEntity::getOemName).orElse(null));
        water.setOrderAmount(invEntity.getPostageFees());
        water.setPayAmount(invEntity.getPostageFees());
        water.setOrderType(PayWaterOrderTypeEnum.INVOICE.getValue());
        water.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
        water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
        water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        water.setPayAccount(userCapital.getCapitalAccount());
        water.setAddTime(new Date());
        water.setAddUser(useraccount);
        water.setRemark(remark);
        water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行
        water.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        payWaterService.insertSelective(water);
        //增加资金变动
        userCapitalAccountService.unfreezeBalance(userCapital, orderEntity.getOrderNo(), orderEntity.getOrderType(), invEntity.getPostageFees(), useraccount);
    }

    @Override
    public InvoiceServiceFeeVO calcServiceFee(Long userId, String oemCode, Long companyId, Long invoiceAmount) {

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }
        // 查询企业类型
        MemberCompanyEntity company = memberCompanyService.findById(companyId);
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }
        if (!Objects.equals(company.getMemberId(), userId)) {
            throw new BusinessException("当前企业不归属于登录用户");
        }
        // 查询产品
        ProductEntity product = new ProductEntity();
        product.setOemCode(oemCode);
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);//查询产品id
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        // 根据产品查询收费标准
        //List<ChargeStandardEntity> chargeLevelList = chargeStandardService.select(chargeStandard);//获取阶梯服务费率
        Map<String, Object> map = chargeStandardService.queryChargeStandards(product.getId(), company.getParkId(), userId, company.getIndustryId());
        List<ChargeStandardEntity> chargeLevelList = JSONArray.parseArray(JSONObject.toJSONString(map.get("systemUsageCharge")), ChargeStandardEntity.class);

        if (CollectionUtil.isEmpty(chargeLevelList)) {
            throw new BusinessException("未查询到收费标准");
        }

        // 累加本年开票金额
        InvoiceStatisticsViewVO vo = mapper.queryCompanyInvoiceRecordStatisticsView(userId, companyId);
        Long allInvAmount = Optional.ofNullable(vo).map(InvoiceStatisticsViewVO::getUseInvoiceAmountYear).orElse(0L);
        ChargeStandardEntity entity;
        InvoiceServiceFeeVO result = new InvoiceServiceFeeVO();
        result.setServiceRate(chargeLevelList.get(chargeLevelList.size() - 1).getChargeRate());
        for (int i = 0; i < chargeLevelList.size(); i++) {
            entity = chargeLevelList.get(i);
            if (allInvAmount == 0 || (allInvAmount > entity.getChargeMin() && allInvAmount <= entity.getChargeMax())) {
                result.setServiceRate(entity.getChargeRate());
                if (i < chargeLevelList.size() - 1) {
                    entity = chargeLevelList.get(i + 1);
                    result.setHasNext(1);
                    result.setNextServiceRate(entity.getChargeRate());
                    long amount = entity.getChargeMin() - allInvAmount;
                    if (amount == 0) {
                        amount = 1L;
                    }
                    result.setAmount(amount);
                }
                break;
            }
        }
        return result;
    }

    /**
     * 查看电子发票
     *
     * @param orderNo
     * @return
     */
    @Override
    public List<InvoiceDetailVO> getEleInvByOrderNo(String orderNo) throws BusinessException {
        //参数校验
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主订单
        OrderEntity mainOrder = orderService.queryByOrderNo(orderNo);

        if (Objects.isNull(mainOrder)){
            return null;
        }
        //订单类型是否为开票 订单类型  6-开票
        if (!Objects.equals(mainOrder.getOrderType(), OrderTypeEnum.INVOICE.getValue())) {
            throw new BusinessException("订单类型不为开票");
        }

        //状态是否已签收   开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过)
        if (mainOrder.getOrderStatus()!=null && mainOrder.getOrderStatus() <= 3) {
            throw new BusinessException("当前订单还没出票，无法进行发票查看");
        }

        //判断是否为电票
        InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
        if (null == invoiceOrderEntity){
            return null;
        }
//        if (!Objects.equals(invoiceOrderEntity.getInvoiceWay(),InvoiceWayEnum.ELECTRON.getValue())){
//            throw new BusinessException("非电票");
//        }

       /*
        List<InvoiceDetailVO> list = invoiceRecordDetailService.querryByOrderNo(orderNo);

        if (list.isEmpty()){
            return null;
        }
        for (InvoiceDetailVO invoiceDetailVO : list) {
            invoiceDetailVO.setEInvoiceOssImgUrl(ossService.getPrivateVideoUrl(invoiceDetailVO.getEInvoiceOssImgUrl()));
        }*/
       List<InvoiceDetailVO> list=new ArrayList<>();
       if(StringUtils.isBlank(invoiceOrderEntity.getInvoiceImgs())){
           return list;
       }
       String s[]=invoiceOrderEntity.getInvoiceImgs().split(",");
       for(String url:s){
           InvoiceDetailVO vo= new InvoiceDetailVO();
           vo.setEInvoiceOssImgUrl(ossService.getPrivateVideoUrl(url));
           list.add(vo);
       }
        return list;
    }

    @Override
    public List<Map<String,Object>> sumByGroupOrderNo(String groupOrderNo) {
        return mapper.sumByGroupOrderNo(groupOrderNo);
    }

    @Override
    public List<Map<String,Object>> queryTotaLInvoiceAmountByVat(String start, String end,Long parkId,Integer invoiceType,Long companyId) {
        return mapper.queryTotaLInvoiceAmountByVat(start,end,parkId,invoiceType,companyId);
    }

    @Override
    public List<Map<String,Object>> queryTotaLInvoiceAmountByVatByTaxYear(Integer year,Integer seasonal,Long parkId,Integer invoiceType,Long companyId) {
        return mapper.queryTotaLInvoiceAmountByVatByTaxYear(year,seasonal,parkId,invoiceType,companyId);
    }

    @Override
    public Long queryTotaLInvoiceAmountByIit(Integer year , Integer seasonal,Long parkId,Integer invoiceType,Long companyId) {
        return mapper.queryTotaLInvoiceAmountByIit(year,seasonal,parkId,invoiceType,companyId);
    }

    @Override
    public Long queryTotaLInvoiceAmountByFj(String start, String end,Long parkId,Integer invoiceType,Long companyId) {
        return mapper.queryTotaLInvoiceAmountByFj(start,end,parkId,invoiceType,companyId);
    }

    @Override
    public Long queryVatByTotaLInvoiceAmount(int year,int seasonal ,List<Map<String,Object>>  mapList, Long parkId,Integer companyType,Long companyId) {
        MemberCompanyEntity company = memberCompanyService.findById(companyId);
        if(company == null){
            throw  new BusinessException("未查到企业信息！");
        }
        Long vatShouldAmount=0L;
        Long totaLInvoiceAmount=0L;
        //查询园区税费政策
        TaxPolicyEntity taxPolicyEntity=taxPolicyService.queryTaxPolicyByParkId(parkId,companyType,company.getTaxpayerType());
        if(taxPolicyEntity==null){
            throw  new BusinessException("园区税费政策不存在！");
        }
        if(CollectionUtil.isEmpty(mapList)){
            Map<String,Object> map =new HashMap<>();
            map.put("invoice_amount",new BigDecimal(0));
            map.put("rate",new BigDecimal(0));
            mapList.add(map);
        }
        for (Map<String,Object> map:mapList) {
            try{
                totaLInvoiceAmount+=((BigDecimal)map.get("invoice_amount")).longValue();
            }catch (Exception e){
                e.getMessage();
            }
        }
        //判断本周起累计开票金额是否>减免额度
        if(totaLInvoiceAmount>taxPolicyEntity.getVatBreaksAmount()){
                for (Map<String,Object> map:mapList) {
                vatShouldAmount+=(((BigDecimal) map.get("invoice_amount")).multiply((BigDecimal) map.get("rate"))).divide(new BigDecimal(1).add((BigDecimal) map.get("rate")),0,BigDecimal.ROUND_UP).longValue();
            }
        }else{
            List<Map<String,Object>>  mapZpList =queryTotaLInvoiceAmountByVatByTaxYear(year,seasonal,parkId,InvoiceTypeEnum.REGISTER.getValue(),companyId);
            if(CollectionUtil.isEmpty(mapZpList)){
                Map<String,Object> map =new HashMap<>();
                map.put("invoice_amount",new BigDecimal(0));
                map.put("rate",new BigDecimal(0));
                mapZpList.add(map);
            }
            for (Map<String,Object> map:mapZpList) {
                vatShouldAmount+=(((BigDecimal) map.get("invoice_amount")).multiply((BigDecimal) map.get("rate"))).divide(new BigDecimal(1).add((BigDecimal) map.get("rate")) ,0,BigDecimal.ROUND_UP).longValue();
            }
        }
        return vatShouldAmount;
    }

    @Override
    public Map<String ,Object> queryIitByTotaLInvoiceAmount(Long totaLInvoiceAmount, Long parkId, Integer companyType,Long vatShouldAmount,int year,Long companyId) {
        //应缴所得税
        Long iitShouldAmount=0L;
        //计算周期期累计应纳税所得额
        Long iitShouldNsAmount=0L;
        //本年累计应纳税所得额
        Long iitShouldNsAmountYear=0L;
        BigDecimal taxableIncomeRate=new BigDecimal(0);
        BigDecimal incomeRate=new BigDecimal(0);
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(companyId);
        if(memberCompanyEntity==null){
            throw  new BusinessException("未查询到企业信息！");
        }
        //累计开票金额
        //Long totaLInvoiceAmount=0L;
        //查询园区税费政策
        TaxPolicyEntity taxPolicyEntity=taxPolicyService.queryTaxPolicyByParkId(parkId,companyType,memberCompanyEntity.getTaxpayerType());
        if(taxPolicyEntity==null){
            throw  new BusinessException("园区税费政策不存在！");
        }

        TaxRulesConfigEntity tax=null;

        if(Objects.equals(taxPolicyEntity.getLevyWay(),1)){
            //判断本周起累计开票金额是否>减免额度
            iitShouldNsAmount+=totaLInvoiceAmount-vatShouldAmount;
            if(totaLInvoiceAmount>taxPolicyEntity.getIncomeTaxBreaksAmount()){
                //查询是否拥有列外行业
                if(memberCompanyEntity.getIndustryId()!=null){
                    tax=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,totaLInvoiceAmount-taxPolicyEntity.getIncomeTaxBreaksAmount(),memberCompanyEntity.getParkId(),memberCompanyEntity.getCompanyType(),memberCompanyEntity.getIndustryId());
                    if(tax==null){
                        //throw  new BusinessException("未找到例外行业配置");
                        tax=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,totaLInvoiceAmount-taxPolicyEntity.getIncomeTaxBreaksAmount(),memberCompanyEntity.getParkId(),memberCompanyEntity.getCompanyType(),null);
                        if(tax==null){
                            throw  new BusinessException("未找到所得税税费配置");
                        }
                    }
                }else{
                    tax=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,totaLInvoiceAmount-taxPolicyEntity.getIncomeTaxBreaksAmount(),memberCompanyEntity.getParkId(),memberCompanyEntity.getCompanyType(),null);
                    if(tax==null){
                        throw  new BusinessException("未找到所得税税费配置");
                    }
                }
                //计算周期期累计应纳税所得额
                iitShouldAmount=new BigDecimal(iitShouldNsAmount).multiply(tax.getRate().divide(new BigDecimal(100))).setScale(0,BigDecimal.ROUND_UP).longValue();
                incomeRate=tax.getRate();
            }else{
                iitShouldAmount=0L;
            }
        }else if(Objects.equals(taxPolicyEntity.getLevyWay(),2)){
            //查询是否拥有列外行业
            if(memberCompanyEntity.getIndustryId()!=null){
                tax=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,totaLInvoiceAmount,memberCompanyEntity.getParkId(),memberCompanyEntity.getCompanyType(),memberCompanyEntity.getIndustryId());
                if(tax==null){
                    //throw  new BusinessException("未找到例外行业配置");
                    tax=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,totaLInvoiceAmount,memberCompanyEntity.getParkId(),memberCompanyEntity.getCompanyType(),null);
                    if(tax==null){
                        throw  new BusinessException("未找到所得税税费配置");
                    }
                }
            }else{
                tax=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,totaLInvoiceAmount,memberCompanyEntity.getParkId(),memberCompanyEntity.getCompanyType(),null);
                if(tax==null){
                    throw  new BusinessException("未找到所得税税费配置");
                }
            }
            //本年累计开票金额
            Long totaLYearInvoiceAmount =queryTotaLInvoiceAmountByIit(year,null,parkId,null,companyId);
            //本年累计应缴增值税
            Long totalYearVatShouldAmount=0L;
            CompanyTaxBillQueryAdmin queryAdmin=new CompanyTaxBillQueryAdmin();
            queryAdmin.setTaxBillYear(year);
            queryAdmin.setCompanyId(companyId);
            Map<String,Object> map=companyTaxBillService.queryCompanyTaxBillTotalVatIiTfJByTime(queryAdmin);
            totalYearVatShouldAmount= new BigDecimal(vatShouldAmount).add((BigDecimal) map.get("vat_total_should_amount")).longValue() ;
            //计算本年度纳税所得额
            iitShouldNsAmountYear=new BigDecimal(totaLYearInvoiceAmount-totalYearVatShouldAmount).multiply(tax.getRate().divide(new BigDecimal(100))).setScale(0,BigDecimal.ROUND_UP).longValue();
            iitShouldNsAmount=new BigDecimal(totaLInvoiceAmount-vatShouldAmount).multiply(tax.getRate().divide(new BigDecimal(100))).setScale(0,BigDecimal.ROUND_UP).longValue();
            //本年度历史周期已缴所得税
            Long totalYearIitAlreadyAmount=((BigDecimal)map.get("iit_total_should_amount")).longValue();

            //查询适用税率
            BusinessIncomeRuleEntity businessIncomeRuleEntity = businessIncomeRuleService.queryBusinessIncomeRuleByAmount(iitShouldNsAmountYear);
            if (businessIncomeRuleEntity==null) {
                throw new BusinessException("未查询到经营所得适用个人所得税税率规则");
            }
            iitShouldAmount=new BigDecimal(iitShouldNsAmountYear).multiply(businessIncomeRuleEntity.getRate()).subtract(new BigDecimal(businessIncomeRuleEntity.getQuick())).setScale(0,BigDecimal.ROUND_UP).longValue()-totalYearIitAlreadyAmount;
            taxableIncomeRate=tax.getRate();
            incomeRate=businessIncomeRuleEntity.getRate().multiply(new BigDecimal(100));
        }
        Map<String,Object> map =new HashMap<>();
        map.put("iitShouldAmount",iitShouldAmount);
        map.put("iitShouldNsAmount",iitShouldNsAmount);
        map.put("levyWay",taxPolicyEntity.getLevyWay());
        map.put("taxableIncomeRate",taxableIncomeRate);
        map.put("incomeRate",incomeRate);

        return map;
    }

    @Override
    public Long queryAdditionalByTotaLInvoiceAmount(Long totaLInvoiceAmount, Long parkId, Integer companyType,Integer taxpayerType,Long vatShouldAmount) {
        //累计开票金额
        Long fjShouldAmount=0L;
        //查询园区税费政策
        TaxPolicyEntity taxPolicyEntity=taxPolicyService.queryTaxPolicyByParkId(parkId,companyType,taxpayerType);
        if(taxPolicyEntity==null){
            throw  new BusinessException("园区税费政策不存在！");
        }
        List<TaxRulesConfigVO> taxRulesConfigVOS=taxRulesConfigService.queryTaxRules(parkId,TaxTypeEnum.SURCHARGE.getValue(),companyType,taxPolicyEntity.getVatBreaksCycle(), CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
        if(CollectionUtil.isEmpty(taxRulesConfigVOS)){
            throw  new BusinessException("税费规则不存在");
        }
        //判断本周起累计开票金额是否>减免额度（无需判断，因为如果小于减免额度传过来得增值税应缴就是专票得应缴增值税）
        //计算周期期累计应纳税所得额
        if(totaLInvoiceAmount>taxPolicyEntity.getSurchargeBreaksAmount()){
            fjShouldAmount=new BigDecimal(vatShouldAmount).multiply(MoneyUtil.moneydiv(taxRulesConfigVOS.get(0).getRate(),new BigDecimal(100))).setScale(0,BigDecimal.ROUND_UP).longValue();
        }else{
            fjShouldAmount=new BigDecimal(vatShouldAmount).multiply(MoneyUtil.moneydiv(taxRulesConfigVOS.get(0).getUrbanConstructionTaxRate(),new BigDecimal(100))).setScale(0,BigDecimal.ROUND_UP).longValue();
        }
        return fjShouldAmount;
    }

    @Override
    public List<CountPeriodInvoiceAmountVO> countPeriodInvoiceAmount(Date start, Date end, Long companyId, String oemCode, Integer invoiceType) {
       return this.mapper.countPeriodInvoiceAmount(start, end, companyId, oemCode, invoiceType);
    }

    @Override
    public PayInformationVO getInvoicePayInfo(Long memberId, String oemCode, String orderNo, BigDecimal vatRate) {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询主订单
        OrderEntity mainOrder = orderService.queryByOrderNo(orderNo);
        if (null == mainOrder) {
            throw new BusinessException("未查询到订单");
        }

        if (!Objects.equals(mainOrder.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        // 查询开票订单
        InvoiceOrderEntity invOrder = invoiceOrderService.queryByOrderNo(orderNo);
        if (null == invOrder) {
            throw new BusinessException("未查询到开票订单");
        }

        // 查询企业类型
        MemberCompanyEntity company = memberCompanyService.findById(invOrder.getCompanyId());
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }

        // 查询产品id
        ProductEntity product = new ProductEntity();
        product.setOemCode(oemCode);
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(mainOrder.getParkId());
        park.setStatus(1);//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("园区不存在或未上架");
        }

        // 校验个体户开票税率与开票类型是否相符 V3.6
        if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            TaxRulesConfigEntity taxRulesConfigEntity = new TaxRulesConfigEntity();
            taxRulesConfigEntity.setCompanyType(company.getCompanyType());
            taxRulesConfigEntity.setTaxType(TaxTypeEnum.VAT.getValue());
            taxRulesConfigEntity.setParkId(park.getId());
            taxRulesConfigEntity.setRate(vatRate);
            if (InvoiceTypeEnum.UPGRADE.getValue().equals(invOrder.getInvoiceType())) {
                taxRulesConfigEntity.setIsOpenPp(1);
            } else {
                taxRulesConfigEntity.setIsOpenZp(1);
            }
            List<TaxRulesConfigEntity> taxRulesConfigEntities = taxRulesConfigService.select(taxRulesConfigEntity);
            if (null == taxRulesConfigEntities || taxRulesConfigEntities.isEmpty()) {
                throw new BusinessException("税率不可用");
            }
        }

        if (null == vatRate) {
            throw new BusinessException("增值税率为空");
        }
        vatRate = MoneyUtil.moneydiv(vatRate, new BigDecimal("100"));

        // 构造支付信息
        PayInformationVO payInformationVO = new PayInformationVO();
        payInformationVO.setInvoiceAmount(invOrder.getInvoiceAmount());

        // 计算税费明细
//        Map<String, Object> taxMap = invoiceOrderService.taxCalculation(company.getId(), 1, orderNo, vatRate, 0, 0, 0);
        TaxCalculationVO entity = new TaxCalculationVO();
        entity.setCompanyId(company.getId());
        entity.setType(1);
        entity.setOrderNo(orderNo);
        entity.setVatRate(vatRate);
        entity.setSeason(0);
        entity.setYear(0);
        entity.setCalculationType(0);
        Map<String, Object> taxMap = invoiceOrderService.taxCalculation(entity);
        TaxFeeDetailVO taxFeeDetailVO = new TaxFeeDetailVO();
        BeanUtil.copyProperties(taxMap, taxFeeDetailVO);
        payInformationVO.setTaxFeeDetail(taxFeeDetailVO);
        Long allTax = (Long) taxMap.get("allTax");
        payInformationVO.setAllTax(allTax);

        // 获取服务费明细
        List<InvoiceServiceFeeDetailVO> list = invoiceServiceFeeDetailService.findDetailByOrderNo(orderNo);
        payInformationVO.setInvoiceServiceFeeDetail(list);
        Long feeAmount = list.stream().mapToLong(InvoiceServiceFeeDetailVO::getFeeAmount).sum();
        // 获取本周期（自然年）历史开票金额
        MemberCompanyDetailVo memberCompanyDetail = memberCompanyService.getMemberCompanyDetail(memberId, oemCode, company.getId());
        payInformationVO.setHistoricalInvoiceAmount(memberCompanyDetail.getUseInvoiceAmountYear() - (InvoiceMarkEnum.REOPEN.getValue().equals(invOrder.getInvoiceMark()) ? 0L : invOrder.getInvoiceAmount()));
        payInformationVO.setServiceFee(feeAmount);

        //设置会员优惠价
        Long serviceFeeDis = 0L;
        MemberLevelEntity memberLevel = this.memberLevelService.findById(member.getMemberLevel());
        if (memberLevel != null && !Objects.equals(memberLevel.getLevelNo(),MemberLevelEnum.NORMAL.getValue()) && !Objects.equals(memberLevel.getLevelNo(),MemberLevelEnum.MEMBER.getValue())) {
            // 查询会员消费折扣
            MemberProfitsRulesEntity memberProfitsRules = new MemberProfitsRulesEntity();
            memberProfitsRules.setUserLevel(memberLevel.getLevelNo());
            memberProfitsRules.setOemCode(member.getOemCode());
            memberProfitsRules = memberProfitsRulesService.selectOne(memberProfitsRules);
            if (null == memberProfitsRules) {
                throw new BusinessException("未查询到会员分润规则");
            }

            // 计算服务费折扣
            serviceFeeDis = (new BigDecimal(feeAmount).multiply(memberProfitsRules.getConsumptionDiscount().divide(new BigDecimal(100)))).setScale(0, BigDecimal.ROUND_DOWN).longValue();
        }
        payInformationVO.setServiceFeeDiscount(serviceFeeDis);
        // 计算邮寄费金额
        Long postageFees = 0L;
        if (Objects.equals(InvoiceWayEnum.PAPER.getValue(), invOrder.getInvoiceWay())) {
            postageFees = park.getPostageFees();
        }
        payInformationVO.setPostageFees(postageFees);
        // 计算支付金额
        if (!MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            allTax = 0L;
        }
        payInformationVO.setPayAmount(allTax + feeAmount - serviceFeeDis + postageFees);
        return payInformationVO;
    }

    @Override
    public Long periodPaidTax(Date start, Date end, Long companyId, String oemCode, Integer type) {
        return this.mapper.periodPaidTax(start, end, companyId, oemCode, type);
    }

    @Override
    public Long periodPayableVatFee(InvoiceOrderEntity invOrder, List<CountPeriodInvoiceAmountVO> list, BigDecimal vatRate, MemberCompanyEntity company, int isOverage) {
        Long periodPayableVatFee = 0L;
        BigDecimal multiply = BigDecimal.valueOf(0.00);
        //flag：改为false表示本次开票税率已经在历史订单中开票过，需要把本次开票金额合并到历史订单进行分组统计
        boolean flag = true;
        for (int i = 0; i < list.size(); i++) {
            CountPeriodInvoiceAmountVO vo = list.get(i);
            if ((isOverage == 1 && vatRate.equals(vo.getVATFeeRate())) ||
                    (isOverage == 0 && vatRate.equals(vo.getVATFeeRate()) && invOrder.getInvoiceType().equals(InvoiceTypeEnum.REGISTER.getValue()))) {
                vo.setCountAmountInvoiced(vo.getCountAmountInvoiced() + invOrder.getInvoiceAmount());
                flag = false;
            }
            multiply = multiply.add(new BigDecimal(vo.getCountAmountInvoiced()).multiply(vo.getVATFeeRate()).divide(new BigDecimal(1).add(vo.getVATFeeRate()),4,BigDecimal.ROUND_UP));
        }
        if ((flag && isOverage == 1) ||
                (flag && isOverage == 0 && invOrder.getInvoiceType().equals(InvoiceTypeEnum.REGISTER.getValue()))) {
            multiply = multiply.add((new BigDecimal(invOrder.getInvoiceAmount())).multiply(vatRate).divide(new BigDecimal(1).add(vatRate),4,BigDecimal.ROUND_UP));
        }
        periodPayableVatFee = multiply.setScale(0,BigDecimal.ROUND_UP).longValue();
        return periodPayableVatFee;
    }

    @Override
    public InvoiceEndtimeVO getInvoiceEndtime(Long companyId, Integer invoiceWay) {
        Integer hostingStatus = 0;
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(companyId)).orElseThrow(() -> new BusinessException("未查询到企业"));
        CompanyTaxHostingEntity taxHostingEntity = companyTaxHostingMapper.queryByCompanyId(companyId);
        if (Objects.nonNull(taxHostingEntity)) {
            hostingStatus = taxHostingEntity.getStatus();
        }

        // 佣金提现开票根据企业托管状态确定开票方式
        if (null == invoiceWay) {
            if (hostingStatus.equals(0)) {
                invoiceWay = 1;
            } else {
                invoiceWay = 2;
            }
        }

        InvoiceEndtimeVO invoiceEndtimeVO = new InvoiceEndtimeVO();
        ParkEndtimeConfigEntity endtimeConfig = parkEndtimeConfigService.findEndtimeConfig(company.getParkId(), OperTypeEnum.CTEAT.getValue(), invoiceWay);

        Date date = new Date();
        if (Objects.nonNull(endtimeConfig)) {
            if (endtimeConfig.getStartTime().before(date) && endtimeConfig.getEndTime().after(date)) {
                invoiceEndtimeVO.setAtEndtime(1);
            }
        }
        return invoiceEndtimeVO;
    }

    @Override
    public List<InvoiceServiceFeeDetailVO> getInvServiceFeeDetail(String orderNo) {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        InvoiceOrderEntity invOrder = Optional.ofNullable(invoiceOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到开票订单"));

        // 查询企业
        MemberCompanyDetailAdminVO company = Optional.ofNullable(memberCompanyService.queryDetailById(invOrder.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业"));

        List<InvoiceServiceFeeDetailVO> serviceList = Lists.newArrayList();

        InvoiceServiceFeeDetailEntity entity = new InvoiceServiceFeeDetailEntity();
        entity.setOemCode(company.getOemCode());
        entity.setCompanyId(invOrder.getCompanyId());
        entity.setOrderNo(orderNo);
        List<InvoiceServiceFeeDetailEntity> list = invoiceServiceFeeDetailService.select(entity);
        for (int i = 0; i < list.size(); i++) {
            InvoiceServiceFeeDetailVO detailVO = new InvoiceServiceFeeDetailVO();
            detailVO.setFeeAmount(list.get(i).getFeeAmount());
            detailVO.setFeeRate(list.get(i).getFeeRate());
            detailVO.setPhaseAmount(list.get(i).getPhaseAmount());
            serviceList.add(detailVO);
        }

        return serviceList;
    }

    @Override
    public Map<String, Object> taxCalculation(Long companyId, int type, String orderNo, BigDecimal vatRate, int season ,int year, int calculationType) {
        if (null == companyId) {
            throw new BusinessException("企业id不能为空");
        }
        HashMap<String, Object> map = Maps.newHashMap();

        // 获取周期（企业开票与预税单周期取当前季度，税单取上季度）
        year = 0 == year ? DateUtil.getYear(new Date()) : year;
        String[] s = {};
        int quarter = Integer.parseInt(DateUtil.getQuarter());
        if (season != 0) { //定时任务可通过该参数生成当前季度税单
            quarter = season;
        } else if (type == 2 && (quarter - 1) <= 0) {
            year -= 1;
            quarter += 3;
        } else if (type == 2 && (quarter - 1) > 0) {
            quarter -= 1;
        }
        s = DateUtil.getCurrQuarter(year, quarter);
        Date start = DateTime.parse(s[0]).toDate();
        Date end = DateTime.parse(s[1]).toDate();
        map.put("taxBillYear", year);
        map.put("taxBillSeasonal", quarter);

        // 查询企业
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(companyId)).orElseThrow(() -> new BusinessException("未查询到企业信息"));
        map.put("companyId", companyId);
        map.put("companyName", company.getCompanyName());
        map.put("operatorName", company.getOperatorName());

        // 查询企业园区
        ParkEntity park = Optional.ofNullable(parkService.findById(company.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区信息"));

        // 查询税费政策
        TaxPolicyEntity taxPolicy = Optional.ofNullable(taxPolicyService.queryTaxPolicyByParkId(park.getId(), company.getCompanyType(),company.getTaxpayerType())).orElseThrow(() -> new BusinessException("园区税费政策不存在"));
        map.put("vatBreaksCycle", taxPolicy.getVatBreaksCycle());
        map.put("incomeTaxBreaksCycle", taxPolicy.getIncomeTaxBreaksCycle());

        // 计算本周期累计开票金额
        List<CountPeriodInvoiceAmountVO> list = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, start, end, null, 1, 0);
        Long periodInvoiceAmount = 0L;
        if (!list.isEmpty() && null != list) {
            for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
            }
        }
        map.put("totalInvoiceAmount", periodInvoiceAmount);
        InvoiceOrderEntity invoiceOrder = null;
        if (type == 1) { // 企业开票需要加上本次开票金额
            if (StringUtil.isBlank(orderNo)) {
                throw new BusinessException("订单编号不能为空");
            }
            // 查询开票订单
            invoiceOrder = Optional.ofNullable(invoiceOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到开票订单"));
            map.put("periodInvoiceAmount", periodInvoiceAmount);
        }

        // 查询税单
        CompanyTaxBillEntity companyTaxBillEntity = null;
        if (type == 2) {
            // 查询企业税单
            companyTaxBillEntity = companyTaxBillService.queryCompanyTaxBillByEin(company.getEin(), quarter, year, companyId);
            if (null == companyTaxBillEntity) {
                throw new BusinessException("未查询到企业税单信息");
            }
            if (IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
                calculationType = 0;
            }
        }

        Long payableVatFee = 0L; // 本次开票应缴增值税
        Long periodPayableVat = 0L; // 本周期应缴增值税
        Long payableSurcharge = 0L; // 本次开票应缴附加税
        Long periodPayableSurcharge = 0L; // 本周期应缴附加税
        // 本周期历史已缴税费
        PeriodPaidTaxVo paidTaxVo = mapper.paidTax(company.getId(), type, start, end);
        // 计算类型 0-所有 1-只计算已缴税费以及增值附加税 2-只计算所得税
        if (calculationType == 0 || calculationType == 1) {
            // 增值税已缴税费
            Long paidVatFee = paidTaxVo.getVatFee();
            map.put("paidVatFee", paidVatFee);
            map.put("vatAlreadyTaxMoney",paidVatFee);
            // 附加税已缴税费
            Long paidSurcharge = paidTaxVo.getSurcharge();
            map.put("paidSurcharge", paidSurcharge);
            map.put("additionalAlreadyTaxMoney", paidSurcharge);
            // 所得税已缴税费
            Long paidIncomeTax = paidTaxVo.getIncomeTax();
            map.put("paidIncomeTax", paidIncomeTax);
            map.put("incomeAlreadyTaxMoney", paidIncomeTax);
            // 总已缴税费
            Long alreadyTaxMoney = paidVatFee + paidSurcharge + paidIncomeTax;
            map.put("alreadyTaxMoney", alreadyTaxMoney);

            if (type == 2 || type == 3) {
                // 跨季出票金额
                List<CountPeriodInvoiceAmountVO> moreQuarterInvoiceList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, start, end, null, 1, 1);
                Long moreQuarterInvoiceAmount = 0L;
                if (!moreQuarterInvoiceList.isEmpty() && null != moreQuarterInvoiceList) {
                    for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : moreQuarterInvoiceList) {
                        moreQuarterInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                    }
                }
                map.put("moreQuarterInvoiceAmount", moreQuarterInvoiceAmount);

                // 增值税普通发票开票金额
                List<CountPeriodInvoiceAmountVO> ppInvoiceList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, start, end, InvoiceTypeEnum.UPGRADE.getValue(), 1, 0);
                Long ppInvoiceAmount = 0L;
                if (!ppInvoiceList.isEmpty() && null != ppInvoiceList) {
                    for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : ppInvoiceList) {
                        ppInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                    }
                }
                map.put("ppInvoiceAmount", ppInvoiceAmount);

                // 增值税专用发票开票金额
                List<CountPeriodInvoiceAmountVO> zpInvoiceList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, start, end, InvoiceTypeEnum.REGISTER.getValue(), 1, 0);
                Long zpInvoiceAmount = 0L;
                if (!zpInvoiceList.isEmpty() && null != zpInvoiceList) {
                    for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : zpInvoiceList) {
                        zpInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                    }
                }
                map.put("zpInvoiceAmount", zpInvoiceAmount);
                map.put("invoiceMoney", ppInvoiceAmount + zpInvoiceAmount);

            }

            if (type == 2) {
                // 作废/红冲金额
                Long cancellationAmount = mapper.queryCancellationAmount(company.getId(), start, end);
                map.put("cancellationAmount", cancellationAmount);
            }
        // =============================================== 计算增值税 ===================================================
            // 本周期增值税是否超过减免额度
            int moreThan = periodInvoiceAmount.compareTo(taxPolicy.getVatBreaksAmount());
            // 计算本周期应缴增值税
            if (moreThan < 1) { // 未超过减免时，仅统计累计专票金额
                list = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, start, end, InvoiceTypeEnum.REGISTER.getValue(), 1, 0);
            }
            periodPayableVat = invoiceOrderService.payableVatFee(list, type, invoiceOrder, vatRate, moreThan);
            if (periodPayableVat < 0L) {
                periodPayableVat = 0L;
            }
            map.put("periodPayableVatFee", periodPayableVat);
            map.put("vatShouldTaxMoney", periodPayableVat);
            // 增值税适用税率
            if (type == 1) {
                map.put("vatFeeRate", vatRate.multiply(new BigDecimal("100")));
            } else {
                // 税单、预税单使用最大税率(应缴为0时税率为0)
                map.put("vatRate", periodPayableVat > 0 ? list.get(0).getVATFeeRate().multiply(new BigDecimal("100")) : BigDecimal.ZERO);
            }
            // 增值税应纳税所得额
            Long vatTaxableIncomeAmount = periodInvoiceAmount - periodPayableVat; //V3.11 税单计算，查账征收应纳税所得额需减去扣除金额
            if (vatTaxableIncomeAmount < 0L) {
                vatTaxableIncomeAmount = 0L;
            }
            map.put("vatTaxableIncomeAmount", vatTaxableIncomeAmount);
            // 本次开票应缴增值税
            payableVatFee = BigDecimal.valueOf(periodPayableVat).subtract(BigDecimal.valueOf(paidVatFee)).setScale(2, BigDecimal.ROUND_UP).longValue();
            // 增值税应退或应补税费
            if (type == 2 || type == 3) {
                if (payableVatFee > 0L) {
                    map.put("vatSupplementTaxMoney", payableVatFee);
                    map.put("vatRecoverableTaxMoney", 0L);
                } else if (payableVatFee < 0L) {
                    map.put("vatSupplementTaxMoney", 0L);
                    map.put("vatRecoverableTaxMoney", -payableVatFee);
                } else {
                    map.put("vatSupplementTaxMoney", 0L);
                    map.put("vatRecoverableTaxMoney", 0L);
                }
            }
            if (type == 1) {
                payableVatFee = (moreThan < 1 && InvoiceTypeEnum.UPGRADE.getValue().equals(invoiceOrder.getInvoiceType())) ? 0 : payableVatFee < 0 ? 0 : payableVatFee;
                map.put("payableVatFee", payableVatFee);
            }

        // =============================================== 计算附加税 ===================================================
            // 附加税应纳税所得额
            Long additionalTaxableIncomeAmount = periodPayableVat;
            map.put("additionalTaxableIncomeAmount", additionalTaxableIncomeAmount < 0L ? 0L : additionalTaxableIncomeAmount); //V3.11 税单计算，查账征收应纳税所得额需减去扣除金额
            // 附加税税率
            List<TaxRulesConfigVO> taxRulesConfigVOS = taxRulesConfigService.queryTaxRules(park.getId(), 3, company.getCompanyType(), taxPolicy.getSurchargeBreaksCycle(), CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
            TaxRulesConfigVO taxRulesConfigVO = taxRulesConfigVOS.get(0);
            BigDecimal surchargeRate = taxRulesConfigVO.getRate().divide(new BigDecimal(100));
            if (periodInvoiceAmount <= taxPolicy.getSurchargeBreaksAmount()) {
                surchargeRate = taxRulesConfigVO.getUrbanConstructionTaxRate().divide(new BigDecimal(100));
                ;
            }
            map.put("surchargeRate", surchargeRate.multiply(new BigDecimal("100")));
            // 本周期应缴附加税
            periodPayableSurcharge = BigDecimal.valueOf(periodPayableVat).multiply(surchargeRate).setScale(0, BigDecimal.ROUND_UP).longValue();
            if (periodPayableSurcharge < 0L) {
                periodPayableSurcharge = 0L;
            }
            map.put("additionalShouldTaxMoney", periodPayableSurcharge);
            payableSurcharge = BigDecimal.valueOf(periodPayableSurcharge).subtract(BigDecimal.valueOf(paidSurcharge)).setScale(2, BigDecimal.ROUND_UP).longValue();
            map.put("additionalRate", periodPayableSurcharge > 0 ? surchargeRate.multiply(new BigDecimal("100")) : BigDecimal.ZERO);
            // 附加税应退或应补税费
            if (type == 2 || type == 3) {
                if (payableSurcharge > 0L) {
                    map.put("additionalRecoverableTaxMoney", 0L);
                    map.put("additionalSupplementTaxMoney", payableSurcharge);
                } else if (payableSurcharge < 0L) {
                    map.put("additionalRecoverableTaxMoney", -payableSurcharge);
                    map.put("additionalSupplementTaxMoney", 0L);
                } else {
                    map.put("additionalRecoverableTaxMoney", 0L);
                    map.put("additionalSupplementTaxMoney", 0L);
                }
            }
            if (type == 1) {
                payableSurcharge = (periodInvoiceAmount <= taxPolicy.getSurchargeBreaksAmount() && InvoiceTypeEnum.UPGRADE.getValue().equals(invoiceOrder.getInvoiceType()) ? 0L : payableSurcharge < 0L ? 0L : payableSurcharge);
                map.put("payableSurcharge", (periodInvoiceAmount <= taxPolicy.getSurchargeBreaksAmount() && InvoiceTypeEnum.UPGRADE.getValue().equals(invoiceOrder.getInvoiceType()) ? 0L : payableSurcharge < 0L ? 0L : payableSurcharge));
            }

            // 查账方式的税单生成时计算出季度不含税收入和年不含税收入
            if (type == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
                Long quarterIncomeAmount = periodInvoiceAmount - periodPayableVat;
                // 本周期累计收入（不含税）
                map.put("quarterIncomeAmount", quarterIncomeAmount);
                // 本年累计收入（不含税）
                if (quarter == 1) {
                    map.put("yearIncomeAmount", quarterIncomeAmount);
                } else {
                    // 本年前一个季度年累计收入
                    Long historyYearIncomeAmount = 0L;
                    CompanyTaxBillEntity companyTaxBillByEin = companyTaxBillService.queryCompanyTaxBillByEin(company.getEin(), quarter - 1, year, companyId);
                    if (null != companyTaxBillByEin && null != companyTaxBillByEin.getYearIncomeAmount()) {
                        historyYearIncomeAmount = companyTaxBillByEin.getYearIncomeAmount();
                    }
                    map.put("yearIncomeAmount", quarterIncomeAmount + historyYearIncomeAmount);
                }
            }

            // 只计算到增值税、附加税
            if (calculationType == 1) {
                return map;
            }
        }
        // =============================================== 计算所得税 ===================================================
        Long payableIncomeTax = 0L;
        // 税率
        BigDecimal rate = BigDecimal.ZERO;
        // 过滤例外行业税率
        TaxRulesConfigEntity taxRulesConfigEntity = new TaxRulesConfigEntity();
        taxRulesConfigEntity.setParkId(park.getId());
        taxRulesConfigEntity.setIndustryId(company.getIndustryId());
        taxRulesConfigEntity.setTaxType(TaxTypeEnum.IIT.getValue());
        taxRulesConfigEntity.setCompanyType(company.getCompanyType());
        List<TaxRulesConfigEntity> taxConfigList = taxRulesConfigService.select(taxRulesConfigEntity);
        if (taxConfigList != null && taxConfigList.size() > 0) {
            for (TaxRulesConfigEntity vo : taxConfigList) {
                if (taxPolicy.getLevyWay().equals(1) && (periodInvoiceAmount - periodPayableVat) > taxPolicy.getIncomeTaxBreaksAmount()
                        && (periodInvoiceAmount - periodPayableVat) > (vo.getMinAmount() + taxPolicy.getIncomeTaxBreaksAmount())
                        && (periodInvoiceAmount - periodPayableVat) <= (vo.getMaxAmount() < Integer.MAX_VALUE ? vo.getMaxAmount() + taxPolicy.getIncomeTaxBreaksAmount() : vo.getMaxAmount())) {
                    if (null != vo.getRate()) {
                        rate = vo.getRate().divide(new BigDecimal(100));
                        break;
                    }
                } else if (taxPolicy.getLevyWay().equals(2)) {
                    rate = vo.getRate().divide(new BigDecimal(100));
                    break;
                }
            }
        }else{
            // 查询所得税税率
            List<TaxRulesConfigVO> taxRulesList = taxRulesConfigService.queryTaxRules(company.getParkId(), TaxTypeEnum.IIT.getValue(), company.getCompanyType(), taxPolicy.getVatBreaksCycle(), CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
            if(CollectionUtil.isEmpty(taxRulesList)){
                throw  new BusinessException("税费规则不存在");
            }
            // 本周期累计开票金额决定所得税使用税率
            for(TaxRulesConfigVO vo:taxRulesList){
                if (taxPolicy.getLevyWay().equals(1) && (periodInvoiceAmount-periodPayableVat) > taxPolicy.getIncomeTaxBreaksAmount()
                        && (periodInvoiceAmount-periodPayableVat) > vo.getMinAmount() && (periodInvoiceAmount-periodPayableVat)<= vo.getMaxAmount()) {
                    if(null != vo.getRate()){
                        rate = vo.getRate().divide(new BigDecimal(100));
                        break;
                    }
                }else if(taxPolicy.getLevyWay().equals(2) && null != vo.getRate()){
                    rate = vo.getRate().divide(new BigDecimal(100));
                    break;
                }
            }
        }

        // 本年历史周期累计开票金额
        Long historyPeriodAmount = 0L;

        if (quarter != 1) {
            s = DateUtil.getCurrQuarter(year,quarter == 1 ? 1 : quarter -1);
            // 历史季度第一天与最后一天
            Date start1 = DateUtil.parseDefaultDate(year + "-01-01");
            Date end1 = DateTime.parse(s[1]).toDate();
            List<CountPeriodInvoiceAmountVO> list2 = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, start1, end1, null, 0, 0);
            if (!list2.isEmpty() && null != list2) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list2) {
                    historyPeriodAmount = BigDecimal.valueOf(historyPeriodAmount).add(BigDecimal.valueOf(companyInvoiceAmountVO.getCountAmountInvoiced())).longValue();
                }
            }
        }
        // 本年累计开票金额
        Long yearTotalAmount = historyPeriodAmount + periodInvoiceAmount;

        // 累计收入 V3.11
        if (type == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            periodPayableVat = companyTaxBillEntity.getVatShouldTaxMoney();
            periodPayableSurcharge = companyTaxBillEntity.getAdditionalShouldTaxMoney();
            Long quarterIncomeAmount = periodInvoiceAmount - periodPayableVat;
            // 本周期累计收入（不含税）
            map.put("quarterIncomeAmount", quarterIncomeAmount);
            // 本年累计收入（不含税）
            if (quarter == 1) {
                map.put("yearIncomeAmount", quarterIncomeAmount);
            } else {
                // 本年前一个季度年累计收入
                Long historyYearIncomeAmount = 0L;
                CompanyTaxBillEntity companyTaxBillByEin = companyTaxBillService.queryCompanyTaxBillByEin(company.getEin(), quarter - 1, year, companyId);
                if (null != companyTaxBillByEin && null != companyTaxBillByEin.getYearIncomeAmount()) {
                    historyYearIncomeAmount = companyTaxBillByEin.getYearIncomeAmount();
                }
                map.put("yearIncomeAmount", quarterIncomeAmount + historyYearIncomeAmount);
            }
        }

        // 1.计算本年/本周期应纳税所得额
        Long incomeTaxableIncomeAmount = 0L;
        int flag = 0; // 0-计算全年 1-计算周期
        if (type == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) { //1.1 税单，查账征收
            // 本年应纳税所得额 = 本年累计开票 - 本年应缴增值税 - 本年累计成本 -所得税本年累计扣除金额
            // 1.1.1 本年累计应缴增值税
            Long yearPayableVatFee = 0L;
            // 1.1.2 本年累计成本
            Long yearCostAmount = 0L;
            // 1.1.3 本年累计个税扣除金额
            Long yearIitDeductionAmount = 0L;
            PeriodPaidTaxVo yearPaidTaxVo = companyTaxBillService.queryPayableTaxFee(company.getId(), year, quarter);
            if (null != yearPaidTaxVo) {
                yearPayableVatFee = yearPaidTaxVo.getVatFee() - yearPaidTaxVo.getCurrentVatTax() + periodPayableVat;
                yearCostAmount = yearPaidTaxVo.getYearCostAmount();
                yearIitDeductionAmount = yearPaidTaxVo.getIitDeductionAmount();
                map.put("yearCostAmount", yearCostAmount);
            }
            incomeTaxableIncomeAmount = yearTotalAmount - yearPayableVatFee - yearCostAmount - yearIitDeductionAmount;
        } else if (LevyWayEnum.TAXABLE_INCOME_RATE.getValue().equals(taxPolicy.getLevyWay())) { //1.2 开票/税单， 应税所得率计税
            // 本年应纳税所得额 = （本年累计开票 - 本年应缴增值税）* 应税所得率
            // 1.2.1 本年累计应缴增值税
            Long yearPayableVatFee = 0L;
            PeriodPaidTaxVo yearPaidTaxVo = companyTaxBillService.queryPayableTaxFee(company.getId(), year, quarter);
            if (null != yearPaidTaxVo) {
                yearPayableVatFee = yearPaidTaxVo.getVatFee() - yearPaidTaxVo.getCurrentVatTax() + periodPayableVat;
            }
            incomeTaxableIncomeAmount = BigDecimal.valueOf(yearTotalAmount - yearPayableVatFee).multiply(rate).longValue();
        } else if (LevyWayEnum.LEVY_RATE.getValue().equals(taxPolicy.getLevyWay())) { //1.3 开票/税单，固定征收率计税
            // 1.3.1 本周期应纳税所得额 = 本周期累计开票 - 本周期应缴增值税
            incomeTaxableIncomeAmount = periodInvoiceAmount - periodPayableVat;
            flag = 1;
        }
        if (incomeTaxableIncomeAmount < 0L) { // 负营利时，应纳税所得额为0
            incomeTaxableIncomeAmount = 0L;
        }
        map.put("incomeTaxableIncomeAmount", incomeTaxableIncomeAmount);

        // 2.计算本周期应缴所得税
        Long periodPayableIncomeTax = 0L;
        if (flag == 0) { //计算全年
            // 本周期应缴所得税 = （本年应纳税所得额*适用税率 - 速算扣除） - 本年历史已缴所得税
            // 所得税适用全国统一个税税率
            BusinessIncomeRuleEntity businessIncomeRuleEntity = businessIncomeRuleService.queryBusinessIncomeRuleByAmount(incomeTaxableIncomeAmount);
            BigDecimal incomeTaxRate = businessIncomeRuleEntity.getRate().multiply(new BigDecimal("100"));
            // 本年历史已缴所得税
            Long historyPaidIncome = 0L;
            PeriodPaidTaxVo yearPaidTaxVo = companyTaxBillService.queryPayableTaxFee(company.getId(), year, quarter);
            if (null != yearPaidTaxVo) {
                if (type == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
                    historyPaidIncome = yearPaidTaxVo.getYearHistoryIncomeTax();
                } else {
                    historyPaidIncome = yearPaidTaxVo.getIncomeTax();
                }
            }
            // 本年应缴所得税
            Long yearPayableIncomeTax = BigDecimal.valueOf(incomeTaxableIncomeAmount)
                    .multiply(businessIncomeRuleEntity.getRate()).setScale(0, BigDecimal.ROUND_UP).longValue()
                    - businessIncomeRuleEntity.getQuick();
            Long limit = 100000000L;
            String yearIncomeTaxHalveAmount = dictionaryService.getValueByCode("year_income_tax_halve_amount"); // 所得税减半应纳税所得额限额
            if (StringUtil.isNotBlank(yearIncomeTaxHalveAmount)) {
                limit = Long.parseLong(yearIncomeTaxHalveAmount);
            }
            if (type == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType()) && incomeTaxableIncomeAmount <= limit) {
                yearPayableIncomeTax = BigDecimal.valueOf(yearPayableIncomeTax).divide(new BigDecimal("2").setScale(2, BigDecimal.ROUND_UP)).longValue();
            }
            map.put("yearPayableIncomeTax", yearPayableIncomeTax); // 本年应缴所得税【查账、税单需要】
            periodPayableIncomeTax = yearPayableIncomeTax - historyPaidIncome;
            // 所得税应税所得率【开票、应税所得率计税需要】
            if ((type == 1 || (type == 2 && IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())))
                    && LevyWayEnum.TAXABLE_INCOME_RATE.getValue().equals(taxPolicy.getLevyWay())) {
                map.put("taxableIncomeRate", rate.multiply(new BigDecimal("100")));
            }
            // 所得税适用税率
            map.put("incomeTaxRate", incomeTaxRate); // 开票
            map.put("incomeRate", incomeTaxRate); // 税单
            // 本年历史已缴所得税【开票需要】
            map.put("paidIncomeTaxYear", paidTaxVo.getIncomeTax() + historyPaidIncome);
        } else { //计算周期
            // 本周期应缴所得税 = 本周期应纳税所得额*适用税率
            periodPayableIncomeTax = BigDecimal.valueOf(incomeTaxableIncomeAmount).multiply(rate).longValue();
            // 所得税适用税率
            map.put("incomeTaxRate", rate.multiply(new BigDecimal("100")));
            map.put("incomeRate", periodPayableIncomeTax > 0 ? rate.multiply(new BigDecimal("100")) : BigDecimal.ZERO);
        }
        // 所得税应缴税费
        map.put("incomeShouldTaxMoney", periodPayableIncomeTax > 0L ? periodPayableIncomeTax : 0L);

        // 3.计算本次应缴所得税
        // 本次开票应缴所得税 = 本周期应缴所得税 - 本周期已缴所得税
        payableIncomeTax = periodPayableIncomeTax - paidTaxVo.getIncomeTax();
        if (type == 1) {
            payableIncomeTax = periodInvoiceAmount <= taxPolicy.getIncomeTaxBreaksAmount() ? 0L : (payableIncomeTax < 0L ? 0L : payableIncomeTax);
            map.put("payableIncomeTax", periodInvoiceAmount <= taxPolicy.getIncomeTaxBreaksAmount() ? 0L : (payableIncomeTax < 0L ? 0L : payableIncomeTax));
        }

        if (type == 2 || type == 3) {
            if (payableIncomeTax > 0L) {
                map.put("incomeRecoverableTaxMoney", 0L);
                map.put("incomeSupplementTaxMoney", payableIncomeTax);
            } else if (payableIncomeTax < 0L) {
                map.put("incomeRecoverableTaxMoney", -payableIncomeTax);
                map.put("incomeSupplementTaxMoney", 0L);
                map.put("incomeTaxYearFreezeAmount", -payableIncomeTax);
            } else {
                map.put("incomeRecoverableTaxMoney", 0L);
                map.put("incomeSupplementTaxMoney", 0L);
            }
        }

        // 本次开票总税费
        Long allTax = (payableVatFee < 0L ? 0L : payableVatFee) + (payableSurcharge < 0L ? 0L : payableSurcharge) + (payableIncomeTax < 0L ? 0L : payableIncomeTax);
        map.put("allTax", allTax);
        // 本周期总税费
        Long shouldTaxMoney = (periodPayableVat < 0L ? 0L : periodPayableVat) + (periodPayableSurcharge < 0L ? 0L : periodPayableSurcharge) + (periodPayableIncomeTax < 0L ? 0L : periodPayableIncomeTax);
        map.put("shouldTaxMoney", shouldTaxMoney);
        // 总应退或应补税费
        Long taxFee = shouldTaxMoney - (paidTaxVo.getVatFee() + paidTaxVo.getSurcharge() + paidTaxVo.getIncomeTax());
        if (taxFee > 0L) {
            map.put("recoverableTaxMoney", 0L);
            map.put("supplementTaxMoney" , taxFee);
        } else if (taxFee < 0L) {
            map.put("recoverableTaxMoney", -taxFee);
            map.put("supplementTaxMoney" , 0L);
        } else {
            map.put("recoverableTaxMoney", 0L);
            map.put("supplementTaxMoney" , 0L);
        }
        return map;
    }

    @Override
    public Map<String, Object> taxCalculation(TaxCalculationVO entity) {
        Map<String, Object> map = Maps.newHashMap();
        // 数据校验与准备
        this.taxCalculationDataPreparation(entity, map);

        // 计算类型 0-所有 1-只计算已缴税费以及增值附加税 2-只计算所得税
        int calculationType = entity.getCalculationType();
        if (calculationType == 0) { // 计算所有税种【开票、重新开票等】
            // 计算增值税
            this.countVatFee(entity, map);
            // 计算附加税
            this.countSurcharge(entity, map);
            if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(entity.getCompanyType())) { // 个体户计算所得税并计算总税费
                // 计算所得税
                this.countIncomeTax(entity, map);
            } else { // 非个体计算印花税及水利建设基金
                // 计算印花税
                this.countStampDuty(entity, map);
                // 计算水利建设基金
                this.countWaterConservancyFund(entity, map);
            }
            // 计算总税费
            this.countAllTax(entity, map);
        } else if (calculationType == 1) { // 只计算已缴税费以及增值附加税 【税单生成、个体户注销】
            // 计算增值税
            this.countVatFee(entity, map);
            // 计算附加税
            this.countSurcharge(entity, map);
            // 计算所得税[所得税仅计算已缴]
            this.countIncomeTax(entity, map);
            // 计算总税费
            this.countAllTax(entity, map);
        } else { // 只计算所得税【税单填报成本】（当前仅个体户有税单）
            // 增值附加税应缴已缴数据准备
            CompanyTaxBillEntity taxBillEntity = entity.getCompanyTaxBillEntity();
            entity.setPeriodPayableVat(taxBillEntity.getVatShouldTaxMoney());
            entity.setPaidVatFee(taxBillEntity.getVatAlreadyTaxMoney());
            entity.setPeriodPayableSurcharge(taxBillEntity.getAdditionalShouldTaxMoney());
            entity.setPaidSurcharge(taxBillEntity.getAdditionalAlreadyTaxMoney());
            // 计算所得税
            this.countIncomeTax(entity, map);
            // 计算总税费
            this.countAllTax(entity, map);
        }

        return map;
    }

    /**
     * 税费计算————数据校验与准备
     * @param entity
     * @return
     */
    private void taxCalculationDataPreparation(TaxCalculationVO entity, Map<String, Object> map) {
        int type = entity.getType(); // 适用类型 1-企业开票 2-企业税单 3-企业预税单

        if (null == entity.getCompanyId()) {
            throw new BusinessException("企业id不能为空");
        }

        // 查询企业
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(entity.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业信息"));
        entity.setCompany(company);
        entity.setCompanyType(company.getCompanyType());
        entity.setTaxpayerType(company.getTaxpayerType());
        map.put("companyId", company.getId());
        map.put("companyName", company.getCompanyName());
        map.put("operatorName", company.getOperatorName());
        map.put("taxpayerType", company.getTaxpayerType());

        // 查询企业园区
        ParkEntity park = Optional.ofNullable(parkService.findById(company.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区信息"));
        entity.setParkId(park.getId());

        // 查询税费政策
        TaxPolicyEntity taxPolicy = taxPolicyService.queryTaxPolicyByParkId(park.getId(), company.getCompanyType(),company.getTaxpayerType());
        if (null == taxPolicy) {
            if (type == 2) {
                // 税单计算找不到税费政策时发送紧急邮件
                Map<String, Object> param = Maps.newHashMap();
                param.put("parkId", park.getId());
                param.put("companyType", company.getCompanyType());
                param.put("taxPayerType", company.getTaxpayerType());
                param.put("oemCode", entity.getOemCode());
                param.put("calculationType", entity.getCalculationType());
                this.sendUrgentMail(param);
            }
        }
        entity.setTaxPolicy(taxPolicy);
        map.put("vATBreaksCycle", taxPolicy.getVatBreaksCycle());
        map.put("incomeTaxBreaksCycle", taxPolicy.getIncomeTaxBreaksCycle());
        map.put("surchargeBreaksCycle", taxPolicy.getVatBreaksCycle());
        map.put("stampDutyBreaksCycle", taxPolicy.getStampDutyBreaksCycle());
        map.put("waterConservancyFundBreaksCycle", taxPolicy.getWaterConservancyFundBreaksCycle());

        // 开票税费计算相关数据准备
        if (type == 1) { // 企业开票需要加上本次开票金额
            if (StringUtil.isBlank(entity.getOrderNo())) {
                throw new BusinessException("订单编号不能为空");
            }
            // 查询开票订单
            InvoiceOrderEntity invoiceOrder = Optional.ofNullable(invoiceOrderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到开票订单"));
            entity.setInvoiceOrder(invoiceOrder);
            // 个体户开票，需要统计本周期开票金额
            if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(entity.getCompanyType())) {
                // 获取开票统计周期
                entity.setTaxType(TaxTypeEnum.VAT.getValue()); // 默认使用增值税配置的
                this.countTaxCalendar(entity, map);
                // 计算开票订单所在申报周期累计开票金额
                List<CountPeriodInvoiceAmountVO> list = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, entity.getStart(), entity.getEnd(), null, 1, 0);
                Long periodInvoiceAmount = 0L;
                if (!list.isEmpty() && null != list) {
                    for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                        periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                    }
                }
                map.put("periodInvoiceAmount", periodInvoiceAmount);
                entity.setPeriodInvoiceAmount(periodInvoiceAmount);
            }
        }

        // 税单税费计算相关数据准备
        if (type == 2) {
            // 税单所属税期
            Map<String, Object> dataMap = this.countTaxCalendar(entity, map);
            int taxBillYear = (int)dataMap.get("taxBillYear");
            int taxBillSeasonal = (int)dataMap.get("taxBillSeasonal");
            map.put("taxBillYear", taxBillYear);
            map.put("taxBillSeasonal", taxBillSeasonal);
            // 计算税单所在季度累计开票金额
            List<CountPeriodInvoiceAmountVO> list = mapper.queryCompanyInvoiceAmountByEin(company.getId(), entity.getType(), entity.getStart(), entity.getEnd(), null, 1, 0);
            Long periodInvoiceAmount = 0L;
            if (!list.isEmpty() && null != list) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                    periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            entity.setPeriodInvoiceAmount(periodInvoiceAmount);
            map.put("totalInvoiceAmount", periodInvoiceAmount);
            // 查询企业税单
            CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.queryCompanyTaxBillByEin(company.getEin(), taxBillSeasonal, taxBillYear, entity.getCompanyId());
            if (null == companyTaxBillEntity) {
                throw new BusinessException("未查询到企业税单信息");
            }
            entity.setCompanyTaxBillEntity(companyTaxBillEntity);
            if (IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
                entity.setCalculationType(0);
            }
            // 作废/红冲金额
            Long cancellationAmount = mapper.queryCancellationAmount(company.getId(), entity.getStart(), entity.getEnd());
            map.put("cancellationAmount", cancellationAmount);
        }

        // 税单/预税单税费计算相关数据准备
        if (type == 2 || type == 3) {
            // 跨季出票金额
            List<CountPeriodInvoiceAmountVO> moreQuarterInvoiceList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, entity.getStart(), entity.getEnd(), null, 1, 1);
            Long moreQuarterInvoiceAmount = 0L;
            if (!moreQuarterInvoiceList.isEmpty() && null != moreQuarterInvoiceList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : moreQuarterInvoiceList) {
                    moreQuarterInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            map.put("moreQuarterInvoiceAmount", moreQuarterInvoiceAmount);

            // 增值税普通发票开票金额
            List<CountPeriodInvoiceAmountVO> ppInvoiceList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, entity.getStart(), entity.getEnd(), InvoiceTypeEnum.UPGRADE.getValue(), 1, 0);
            Long ppInvoiceAmount = 0L;
            if (!ppInvoiceList.isEmpty() && null != ppInvoiceList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : ppInvoiceList) {
                    ppInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            map.put("ppInvoiceAmount", ppInvoiceAmount);

            // 增值税专用发票开票金额
            List<CountPeriodInvoiceAmountVO> zpInvoiceList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), type, entity.getStart(), entity.getEnd(), InvoiceTypeEnum.REGISTER.getValue(), 1, 0);
            Long zpInvoiceAmount = 0L;
            if (!zpInvoiceList.isEmpty() && null != zpInvoiceList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : zpInvoiceList) {
                    zpInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            map.put("zpInvoiceAmount", zpInvoiceAmount);
            map.put("invoiceMoney", ppInvoiceAmount + zpInvoiceAmount);
        }
    }

    /**
     * 税费计算————税期确认
     * @return
     */
    private Map<String, Object> countTaxCalendar(TaxCalculationVO entity, Map<String, Object> map) {
        Map<String, Object> dataMap = Maps.newHashMap();
        int period = 0; // 申报周期 0-按季 1-按月

        // 税单/预税单按季度统计，开票按政策配置统计
        if (entity.getType() == 1) {
            if (null == entity.getTaxpayerType()) {
                log.error("【税费计算失败】税期确认——纳税人类型为空");
                throw new BusinessException("纳税人类型为空");
            }
            if (null == entity.getParkId()) {
                log.error("【税费计算失败】税期确认——园区id为空");
                throw new BusinessException("园区id为空");
            }
            if (null == entity.getCompanyType()) {
                log.error("【税费计算失败】税期确认——企业类型为空");
                throw new BusinessException("企业类型为空");
            }

            // 查询税费政策
            TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(entity.getParkId(), entity.getCompanyType(), entity.getTaxpayerType());
            if (null == taxPolicyEntity) {
                log.error("【税费计算失败】税期确认——未查询到税费政策");
                throw new BusinessException("未查询到税费政策");
            }

            // 不同税种取其对应的申报周期配置
            if (TaxTypeEnum.IIT.getValue().equals(entity.getTaxType())) { // 所得税
                period = null != taxPolicyEntity.getIncomeTaxBreaksCycle() && taxPolicyEntity.getIncomeTaxBreaksCycle() == 2 ? 0 : 1;
            } else if (TaxTypeEnum.STAMP_DUTY.getValue().equals(entity.getTaxType())) { // 印花税
                period = null != taxPolicyEntity.getStampDutyBreaksCycle() && taxPolicyEntity.getStampDutyBreaksCycle() == 2 ? 0 : 1;
            } else if (TaxTypeEnum.FOUNDATION_FOR_WATER_WORKS.getValue().equals(entity.getTaxType())) { // 水利建设基金
                period = null != taxPolicyEntity.getWaterConservancyFundBreaksCycle() && taxPolicyEntity.getWaterConservancyFundBreaksCycle() == 2 ? 0 : 1;
            } else { // 增值、附加或不确定税种（即小规模纳税人开票需要统计周期开票金额时）
                period = null != taxPolicyEntity.getVatBreaksCycle() && taxPolicyEntity.getVatBreaksCycle() == 2 ? 0 : 1;
            }
        }

        // 获取时间区间
        if (period == 0) { // 获取季度开始时间和结束时间
            // 年
            int year = entity.getYear();
            if (year == 0) {
                year = DateUtil.getYear(new Date());
                entity.setYear(year);
            }
            // 季度
            int quarter = entity.getSeason();
            if (quarter == 0) {
                quarter = Integer.parseInt(DateUtil.getQuarter());
                if (entity.getType() == 2 && (quarter - 1) <= 0) {
                    entity.setYear(entity.getYear() - 1);
                    quarter += 3;
                } else if (entity.getType() == 2 && (quarter - 1) > 0) {
                    quarter -= 1;
                }
            }
            String[] s = DateUtil.getCurrQuarter(year, quarter);
            Date start = DateTime.parse(s[0]).toDate();
            Date end = DateTime.parse(s[1]).toDate();
            map.put("start", start);
            map.put("end", end);
            entity.setStart(start);
            entity.setEnd(end);
            dataMap.put("taxBillYear", year);
            dataMap.put("taxBillSeasonal", quarter);
            entity.setYear(year);
            entity.setSeason(quarter);
        } else { // 获取月度开始时间和结束时间（默认只有开票会使用到按月配置）
            String monFirstDay = DateUtil.getMonFirstDay();
            String monLastDay = DateUtil.getMonLastDay();
            map.put("start", DateTime.parse(monFirstDay).toDate());
            map.put("end", DateTime.parse(monLastDay).toDate());
            entity.setStart(DateTime.parse(monFirstDay).toDate());
            entity.setEnd(DateTime.parse(monLastDay).toDate());
        }
        return dataMap;
    }

    /**
     * 税费计算————增值税计算
     * @param entity
     * @return
     */
    private void countVatFee(TaxCalculationVO entity, Map<String, Object> map) {
        // 确定申报周期
        entity.setTaxType(TaxTypeEnum.VAT.getValue());
        this.countTaxCalendar(entity, map);
        // 申报周期已缴增值税
        PeriodPaidTaxVo paidTaxVo = mapper.paidTax(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd());
        entity.setPaidVatFee(paidTaxVo.getVatFee());
        map.put("paidVatFee", paidTaxVo.getVatFee());
        map.put("vatAlreadyTaxMoney",paidTaxVo.getVatFee());
        // 计算增值税申报周期累计开票金额
        List<CountPeriodInvoiceAmountVO> list = mapper.queryCompanyInvoiceAmountByEin(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd(), null, 1, 0);
        Long periodInvoiceAmount = 0L;
        if (!list.isEmpty() && null != list) {
            for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
            }
        }
        entity.setPeriodInvoiceAmount(periodInvoiceAmount);
        // 本周期增值税是否超过减免额度
        int moreThan = periodInvoiceAmount.compareTo(entity.getTaxPolicy().getVatBreaksAmount());
        // 未超过减免时，仅统计累计专票金额
        if (moreThan < 1) {
            list = mapper.queryCompanyInvoiceAmountByEin(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd(), InvoiceTypeEnum.REGISTER.getValue(), 1, 0);
        }
        // 计算本周期应缴增值税
        Long periodPayableVat = invoiceOrderService.payableVatFee(list, entity.getType(), entity.getInvoiceOrder(), entity.getVatRate(), moreThan);
        if (periodPayableVat < 0L) {
            periodPayableVat = 0L;
        }
        entity.setPeriodPayableVat(periodPayableVat);
        map.put("periodPayableVatFee", periodPayableVat);
        map.put("vatShouldTaxMoney", periodPayableVat);
        // 增值税适用税率
        if (entity.getType() == 1) {
            if (null == entity.getVatRate()) {
                throw new BusinessException("增值税率为空");
            }
            map.put("vatFeeRate", entity.getVatRate().multiply(new BigDecimal("100")));
        } else {
            // 税单、预税单使用最大税率(应缴为0时税率为0)
            map.put("vatRate", periodPayableVat > 0 ? list.get(0).getVATFeeRate().multiply(new BigDecimal("100")) : BigDecimal.ZERO);
        }
        // 增值税应纳税所得额
        Long vatTaxableIncomeAmount = periodInvoiceAmount - periodPayableVat; //V3.11 税单计算，查账征收应纳税所得额需减去扣除金额
        if (vatTaxableIncomeAmount < 0L) {
            vatTaxableIncomeAmount = 0L;
        }
        map.put("vatTaxableIncomeAmount", vatTaxableIncomeAmount);
        // 本次开票应缴增值税
        Long payableVatFee = BigDecimal.valueOf(periodPayableVat).subtract(BigDecimal.valueOf(entity.getPaidVatFee())).setScale(2, BigDecimal.ROUND_UP).longValue();
        // 增值税应退或应补税费
        if (entity.getType() == 2 || entity.getType() == 3) {
            if (payableVatFee > 0L) {
                map.put("vatSupplementTaxMoney", payableVatFee);
                map.put("vatRecoverableTaxMoney", 0L);
            } else if (payableVatFee < 0L) {
                map.put("vatSupplementTaxMoney", 0L);
                map.put("vatRecoverableTaxMoney", -payableVatFee);
            } else {
                map.put("vatSupplementTaxMoney", 0L);
                map.put("vatRecoverableTaxMoney", 0L);
            }
        }
        // 本次开票应缴增值税
        if (entity.getType() == 1) {
            if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(entity.getCompanyType())) {
                payableVatFee = (moreThan < 1 && InvoiceTypeEnum.UPGRADE.getValue().equals(entity.getInvoiceOrder().getInvoiceType())) ? 0 : payableVatFee < 0 ? 0 : payableVatFee;
                map.put("payableVatFee", payableVatFee);
                entity.setPayableVatFee(payableVatFee);
            } else { // 非个体企业本次开票应缴等于本申报周期应缴
                map.put("payableVatFee", periodPayableVat);
                entity.setPayableVatFee(periodPayableVat);
            }
        }

    }

    /**
     * 税费计算————附加税计算
     * @param entity
     * @return
     */
    private void countSurcharge(TaxCalculationVO entity, Map<String, Object> map) {
        // 确认申报周期，附加税申报周期与增值税一致
        entity.setTaxType(TaxTypeEnum.SURCHARGE.getValue());
        this.countTaxCalendar(entity, map);
        // 申报周期已缴附加税
        PeriodPaidTaxVo paidTaxVo = mapper.paidTax(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd());
        entity.setPaidSurcharge(paidTaxVo.getSurcharge());
        map.put("paidSurcharge", paidTaxVo.getSurcharge());
        map.put("additionalAlreadyTaxMoney", paidTaxVo.getSurcharge());
        // 本申报周期累计开票金额，附加税申报周期与增值税一致
        List<CountPeriodInvoiceAmountVO> list = mapper.queryCompanyInvoiceAmountByEin(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd(), null, 1, 0);
        Long periodInvoiceAmount = 0L;
        if (!list.isEmpty() && null != list) {
            for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
            }
        }
        entity.setPeriodInvoiceAmount(periodInvoiceAmount);
        // 附加税应纳税所得额
        Long additionalTaxableIncomeAmount = entity.getPeriodPayableVat();
        map.put("additionalTaxableIncomeAmount", additionalTaxableIncomeAmount < 0L ? 0L : additionalTaxableIncomeAmount); //V3.11 税单计算，查账征收应纳税所得额需减去扣除金额
        // 附加税税率
        List<TaxRulesConfigVO> taxRulesConfigVOS = taxRulesConfigService.queryTaxRules(entity.getParkId(), 3, entity.getCompanyType(), entity.getTaxPolicy().getSurchargeBreaksCycle(), entity.getTaxpayerType());
        TaxRulesConfigVO taxRulesConfigVO = taxRulesConfigVOS.get(0);
        BigDecimal surchargeRate = taxRulesConfigVO.getRate().divide(new BigDecimal(100));
        if (entity.getPeriodInvoiceAmount() <= entity.getTaxPolicy().getSurchargeBreaksAmount()) {
            surchargeRate = taxRulesConfigVO.getUrbanConstructionTaxRate().divide(new BigDecimal(100));
        }
        map.put("surchargeRate", surchargeRate.multiply(new BigDecimal("100")));
        // 本周期应缴附加税
        Long periodPayableSurcharge = BigDecimal.valueOf(entity.getPeriodPayableVat()).multiply(surchargeRate).setScale(0, BigDecimal.ROUND_UP).longValue();
        if (periodPayableSurcharge < 0L) {
            periodPayableSurcharge = 0L;
        }
        entity.setPeriodPayableSurcharge(periodPayableSurcharge);
        map.put("additionalShouldTaxMoney", periodPayableSurcharge);
        // 本次应缴附加税
        Long payableSurcharge = BigDecimal.valueOf(periodPayableSurcharge).subtract(BigDecimal.valueOf(entity.getPaidSurcharge())).setScale(2, BigDecimal.ROUND_UP).longValue();
        map.put("additionalRate", periodPayableSurcharge > 0 ? surchargeRate.multiply(new BigDecimal("100")) : BigDecimal.ZERO);
        // 附加税应退或应补税费
        if (entity.getType() == 2 || entity.getType() == 3) {
            if (payableSurcharge > 0L) {
                map.put("additionalRecoverableTaxMoney", 0L);
                map.put("additionalSupplementTaxMoney", payableSurcharge);
            } else if (payableSurcharge < 0L) {
                map.put("additionalRecoverableTaxMoney", -payableSurcharge);
                map.put("additionalSupplementTaxMoney", 0L);
            } else {
                map.put("additionalRecoverableTaxMoney", 0L);
                map.put("additionalSupplementTaxMoney", 0L);
            }
        }
        // 本次开票应缴附加税
        if (entity.getType() == 1) {
            if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(entity.getCompanyType())) {
                payableSurcharge = (entity.getPeriodInvoiceAmount() <= entity.getTaxPolicy().getSurchargeBreaksAmount() && InvoiceTypeEnum.UPGRADE.getValue().equals(entity.getInvoiceOrder().getInvoiceType()) ? 0L : payableSurcharge < 0L ? 0L : payableSurcharge);
                map.put("payableSurcharge", payableSurcharge);
                entity.setPayableSurcharge(payableSurcharge);
            } else { // 非个体企业本次开票应缴等于本申报周期应缴
                map.put("payableSurcharge", periodPayableSurcharge);
                entity.setPayableSurcharge(periodPayableSurcharge);
            }
        }
    }

    /**
     * 税费计算————所得税计算
     * @param entity
     * @return
     */
    private void countIncomeTax(TaxCalculationVO entity, Map<String, Object> map) {
        // 所得税申报周期(当前版本限制所得税申报周期必须与增值税配置一致，暂不用另外确认申报周期V4.0)
        // entity.setTaxType(TaxTypeEnum.IIT.getValue());
        // this.countTaxCalendar(entity, map);
        // 申报周期所得税已缴
        PeriodPaidTaxVo paidTaxVo = mapper.paidTax(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd());
        entity.setPaidIncomeTax(paidTaxVo.getIncomeTax());
        map.put("paidIncomeTax", paidTaxVo.getIncomeTax());
        map.put("incomeAlreadyTaxMoney", paidTaxVo.getIncomeTax());
        // 所得税申报周期累计开票金额(当前版本限制所得税申报周期必须与增值税配置一致，暂不用另外统计累计开票V4.0)



        Long payableIncomeTax = 0L;
        // 税率
        BigDecimal rate = BigDecimal.ZERO;
        // 过滤例外行业税率
        TaxRulesConfigEntity taxRulesConfigEntity = new TaxRulesConfigEntity();
        taxRulesConfigEntity.setParkId(entity.getParkId());
        taxRulesConfigEntity.setIndustryId(entity.getCompany().getIndustryId());
        taxRulesConfigEntity.setTaxType(TaxTypeEnum.IIT.getValue());
        taxRulesConfigEntity.setCompanyType(entity.getCompanyType());
        List<TaxRulesConfigEntity> taxConfigList = taxRulesConfigService.select(taxRulesConfigEntity);
        if (taxConfigList != null && taxConfigList.size() > 0) {
            for (TaxRulesConfigEntity vo : taxConfigList) {
                if (entity.getTaxPolicy().getLevyWay().equals(1) && (entity.getPeriodInvoiceAmount() - entity.getPeriodPayableVat()) > entity.getTaxPolicy().getIncomeTaxBreaksAmount()
                        && (entity.getPeriodInvoiceAmount() - entity.getPeriodPayableVat()) > (vo.getMinAmount() + entity.getTaxPolicy().getIncomeTaxBreaksAmount())
                        && (entity.getPeriodInvoiceAmount() - entity.getPeriodPayableVat()) <= (vo.getMaxAmount() < Integer.MAX_VALUE ? vo.getMaxAmount() + entity.getTaxPolicy().getIncomeTaxBreaksAmount() : vo.getMaxAmount())) {
                    if (null != vo.getRate()) {
                        rate = vo.getRate().divide(new BigDecimal(100));
                        break;
                    }
                } else if (entity.getTaxPolicy().getLevyWay().equals(2)) {
                    rate = vo.getRate().divide(new BigDecimal(100));
                    break;
                }
            }
        }else{
            // 查询所得税税率
            List<TaxRulesConfigVO> taxRulesList = taxRulesConfigService.queryTaxRules(entity.getParkId(), TaxTypeEnum.IIT.getValue(), entity.getCompanyType(), entity.getTaxPolicy().getVatBreaksCycle(), entity.getTaxpayerType());
            if(CollectionUtil.isEmpty(taxRulesList)){
                throw  new BusinessException("税费规则不存在");
            }
            // 本周期累计开票金额决定所得税使用税率
            for(TaxRulesConfigVO vo:taxRulesList){
                if (entity.getTaxPolicy().getLevyWay().equals(1) && (entity.getPeriodInvoiceAmount()-entity.getPeriodPayableVat()) > entity.getTaxPolicy().getIncomeTaxBreaksAmount()
                        && (entity.getPeriodInvoiceAmount()-entity.getPeriodPayableVat()) > vo.getMinAmount() && (entity.getPeriodInvoiceAmount()-entity.getPeriodPayableVat())<= vo.getMaxAmount()) {
                    if(null != vo.getRate()){
                        rate = vo.getRate().divide(new BigDecimal(100));
                        break;
                    }
                }else if(entity.getTaxPolicy().getLevyWay().equals(2) && null != vo.getRate()){
                    rate = vo.getRate().divide(new BigDecimal(100));
                    break;
                }
            }
        }

        // 本年历史周期累计开票金额
        Long historyPeriodAmount = 0L;
        // 确定历史周期开始结束时间
        if (entity.getSeason() > 1) {
            String[] s = DateUtil.getCurrQuarter(entity.getYear(), entity.getSeason() == 1 ? 1 : entity.getSeason()-1);
            // 历史季度第一天与最后一天
            Date start1 = DateUtil.parseDefaultDate(entity.getYear() + "-01-01");
            Date end1 = DateTime.parse(s[1]).toDate();
            List<CountPeriodInvoiceAmountVO> list2 = mapper.queryCompanyInvoiceAmountByEin(entity.getCompanyId(), entity.getType(), start1, end1, null, 0, 0);
            if (!list2.isEmpty() && null != list2) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list2) {
                    historyPeriodAmount = BigDecimal.valueOf(historyPeriodAmount).add(BigDecimal.valueOf(companyInvoiceAmountVO.getCountAmountInvoiced())).longValue();
                }
            }
        }
        // 本年累计开票金额
        Long yearTotalAmount = historyPeriodAmount + entity.getPeriodInvoiceAmount();

        // 1.计算本年/本周期应纳税所得额
        Long incomeTaxableIncomeAmount = 0L;
        int flag = 0; // 0-计算全年 1-计算周期
        if (entity.getType() == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(entity.getCompanyTaxBillEntity().getIncomeLevyType())) { //1.1 税单，查账征收
            // 本年应纳税所得额 = 本年累计开票 - 本年应缴增值税 - 本年累计成本 -所得税本年累计扣除金额
            // 1.1.1 本年累计应缴增值税
            Long yearPayableVatFee = 0L;
            // 1.1.2 本年累计成本
            Long yearCostAmount = 0L;
            // 1.1.3 本年累计个税扣除金额
            Long yearIitDeductionAmount = 0L;
            PeriodPaidTaxVo yearPaidTaxVo = companyTaxBillService.queryPayableTaxFee(entity.getCompanyId(), entity.getYear(), entity.getSeason());
            if (null != yearPaidTaxVo) {
                yearPayableVatFee = yearPaidTaxVo.getVatFee() - yearPaidTaxVo.getCurrentVatTax() + entity.getPeriodPayableVat();
                yearCostAmount = yearPaidTaxVo.getYearCostAmount();
                yearIitDeductionAmount = yearPaidTaxVo.getIitDeductionAmount();
                map.put("yearCostAmount", yearCostAmount);
            }
            incomeTaxableIncomeAmount = yearTotalAmount - yearPayableVatFee - yearCostAmount - yearIitDeductionAmount;
            // 本周期累计收入（不含税）
            map.put("quarterIncomeAmount", entity.getPeriodInvoiceAmount() - entity.getPeriodPayableVat());
            // 本年累计收入（不含税）
            map.put("yearIncomeAmount", yearTotalAmount - yearPayableVatFee);

            if (entity.getCalculationType() == 1) {
                // 查账征收生成税单时，仅计算增值附加税以及已缴所得税，同时统计出不含税收入
                return;
            }
        } else if (LevyWayEnum.TAXABLE_INCOME_RATE.getValue().equals(entity.getTaxPolicy().getLevyWay())) { //1.2 开票/税单， 应税所得率计税
            // 本年应纳税所得额 = （本年累计开票 - 本年应缴增值税）* 应税所得率
            // 1.2.1 本年累计应缴增值税
            Long yearPayableVatFee = 0L;
            PeriodPaidTaxVo yearPaidTaxVo = companyTaxBillService.queryPayableTaxFee(entity.getCompanyId(), entity.getYear(), entity.getSeason());
            if (null != yearPaidTaxVo) {
                yearPayableVatFee = yearPaidTaxVo.getVatFee() - yearPaidTaxVo.getCurrentVatTax() + entity.getPeriodPayableVat();
            }
            incomeTaxableIncomeAmount = BigDecimal.valueOf(yearTotalAmount - yearPayableVatFee).multiply(rate).longValue();
        } else if (LevyWayEnum.LEVY_RATE.getValue().equals(entity.getTaxPolicy().getLevyWay())) { //1.3 开票/税单，固定征收率计税
            // 1.3.1 本周期应纳税所得额 = 本周期累计开票 - 本周期应缴增值税
            incomeTaxableIncomeAmount = entity.getPeriodInvoiceAmount() - entity.getPeriodPayableVat();
            flag = 1;
        }
        if (incomeTaxableIncomeAmount < 0L) { // 负营利时，应纳税所得额为0
            incomeTaxableIncomeAmount = 0L;
        }
        map.put("incomeTaxableIncomeAmount", incomeTaxableIncomeAmount);

        // 2.计算本周期应缴所得税
        Long periodPayableIncomeTax = 0L;
        if (flag == 0) { //计算全年
            // 本周期应缴所得税 = （本年应纳税所得额*适用税率 - 速算扣除） - 本年历史已缴所得税
            // 所得税适用全国统一个税税率
            BusinessIncomeRuleEntity businessIncomeRuleEntity = businessIncomeRuleService.queryBusinessIncomeRuleByAmount(incomeTaxableIncomeAmount);
            BigDecimal incomeTaxRate = businessIncomeRuleEntity.getRate().multiply(new BigDecimal("100"));
            // 本年历史已缴所得税
            Long historyPaidIncome = 0L;
            PeriodPaidTaxVo yearPaidTaxVo = companyTaxBillService.queryPayableTaxFee(entity.getCompanyId(), entity.getYear(), entity.getSeason());
            if (null != yearPaidTaxVo) {
                if (entity.getType() == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(entity.getCompanyTaxBillEntity().getIncomeLevyType())) {
                    historyPaidIncome = yearPaidTaxVo.getYearHistoryIncomeTax();
                } else {
                    historyPaidIncome = yearPaidTaxVo.getIncomeTax();
                }
            }

            // 本年应缴所得税
            Long yearPayableIncomeTax = 0L;
            BigDecimal payableIncomeBreak; // 减免年应缴所得税
            TaxPolicyEntity taxPolicy = entity.getTaxPolicy();
            // 计算本年应缴所得税
            payableIncomeBreak = BigDecimal.valueOf(incomeTaxableIncomeAmount).multiply(businessIncomeRuleEntity.getRate()).subtract(BigDecimal.valueOf(businessIncomeRuleEntity.getQuick()));
            // 核定应税所得率计税方式的开票/核定征收方式税单，税额减免(税额减免百分比配置不为空时减免)
            if (LevyWayEnum.TAXABLE_INCOME_RATE.getValue().equals(taxPolicy.getLevyWay())
                    && (entity.getType() == 1 || (entity.getType() == 2 && IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(taxPolicy.getIncomeLevyType())))
                    && null != taxPolicy.getIncomeTaxReliefRatio() && taxPolicy.getIncomeTaxReliefRatio().compareTo(BigDecimal.ZERO) > 0) {
                payableIncomeBreak = payableIncomeBreak.multiply(BigDecimal.ONE.subtract(taxPolicy.getIncomeTaxReliefRatio().divide(new BigDecimal("100"))));
            }
            yearPayableIncomeTax = payableIncomeBreak.setScale(0, BigDecimal.ROUND_UP).longValue();

            Long limit = 100000000L;
            String yearIncomeTaxHalveAmount = dictionaryService.getValueByCode("year_income_tax_halve_amount"); // 所得税减半应纳税所得额限额
            if (StringUtil.isNotBlank(yearIncomeTaxHalveAmount)) {
                limit = Long.parseLong(yearIncomeTaxHalveAmount);
            }
            // 查账征收税单计算，年应纳税所得额小于100W时应纳税所得额减半(与以上核定应税所得计税的税额减免不冲突)
            if (entity.getType() == 2 && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(entity.getCompanyTaxBillEntity().getIncomeLevyType()) && incomeTaxableIncomeAmount <= limit) {
                yearPayableIncomeTax = BigDecimal.valueOf(yearPayableIncomeTax).divide(new BigDecimal("2").setScale(2, BigDecimal.ROUND_UP)).longValue();
            }
            map.put("yearPayableIncomeTax", yearPayableIncomeTax); // 本年应缴所得税【查账、税单需要】
            periodPayableIncomeTax = yearPayableIncomeTax;
            // 所得税应税所得率【开票、应税所得率计税需要】
            if ((entity.getType() == 1 || (entity.getType() == 2 && IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue().equals(entity.getCompanyTaxBillEntity().getIncomeLevyType())))
                    && LevyWayEnum.TAXABLE_INCOME_RATE.getValue().equals(entity.getTaxPolicy().getLevyWay())) {
                map.put("taxableIncomeRate", rate.multiply(new BigDecimal("100")));
            }
            // 所得税适用税率
            map.put("incomeTaxRate", incomeTaxRate); // 开票
            map.put("incomeRate", incomeTaxRate); // 税单
            // 本年已缴所得税【开票需要】
            map.put("paidIncomeTaxYear", entity.getPaidIncomeTax() + historyPaidIncome);
            entity.setPaidIncomeTax(entity.getPaidIncomeTax() + historyPaidIncome); // 按年计税时，应缴已缴都使用全年的值计算，但是税单中保存为周期值（即当季值）
        } else { //计算周期
            // 本周期应缴所得税 = 本周期应纳税所得额*适用税率
            periodPayableIncomeTax = BigDecimal.valueOf(incomeTaxableIncomeAmount).multiply(rate).setScale(0, BigDecimal.ROUND_UP).longValue();
            // 所得税适用税率
            map.put("incomeTaxRate", rate.multiply(new BigDecimal("100")));
            map.put("incomeRate", periodPayableIncomeTax > 0 ? rate.multiply(new BigDecimal("100")) : BigDecimal.ZERO);
        }
        // 所得税应缴税费（按年计税时，税单中保存为周期值即当季值）
        map.put("incomeShouldTaxMoney", periodPayableIncomeTax > 0L ? periodPayableIncomeTax : 0L);
        entity.setPeriodPayableIncomeTax(periodPayableIncomeTax > 0L ? periodPayableIncomeTax : 0L);

        // 3.计算本次应缴所得税
        // 本次开票应缴所得税 = 本周期应缴所得税 - 本周期已缴所得税
        payableIncomeTax = periodPayableIncomeTax - entity.getPaidIncomeTax();
        if (entity.getType() == 1) {
            payableIncomeTax = entity.getPeriodInvoiceAmount() <= entity.getTaxPolicy().getIncomeTaxBreaksAmount() ? 0L : (payableIncomeTax < 0L ? 0L : payableIncomeTax);
            map.put("payableIncomeTax", payableIncomeTax);
            entity.setPayableIncomeTax(payableIncomeTax);
        }

        if (entity.getType() == 2 || entity.getType() == 3) {
            if (payableIncomeTax > 0L) {
                map.put("incomeRecoverableTaxMoney", 0L);
                map.put("incomeSupplementTaxMoney", payableIncomeTax);
            } else if (payableIncomeTax < 0L) {
                map.put("incomeRecoverableTaxMoney", -payableIncomeTax);
                map.put("incomeSupplementTaxMoney", 0L);
                map.put("incomeTaxYearFreezeAmount", -payableIncomeTax);
            } else {
                map.put("incomeRecoverableTaxMoney", 0L);
                map.put("incomeSupplementTaxMoney", 0L);
            }
        }
    }

    /**
     * 税费计算————计算印花税
     * @param entity
     * @param map
     */
    private void countStampDuty(TaxCalculationVO entity, Map<String, Object> map) {
        // 印花税申报周期
        entity.setTaxType(TaxTypeEnum.STAMP_DUTY.getValue());
        this.countTaxCalendar(entity, map);
        // 计算印花税申报周期累计开票金额
        List<CountPeriodInvoiceAmountVO> list = mapper.queryCompanyInvoiceAmountByEin(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd(), null, 1, 0);
        Long periodInvoiceAmount = 0L;
        if (!list.isEmpty() && null != list) {
            for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
            }
        }
        // 获取印花税税率
        List<TaxRulesConfigVO> taxRulesConfigVOS = taxRulesConfigService.queryTaxRules(entity.getParkId(), TaxTypeEnum.STAMP_DUTY.getValue(), entity.getCompanyType(), entity.getTaxPolicy().getStampDutyBreaksCycle(), entity.getTaxpayerType());
        TaxRulesConfigVO taxRulesConfigVO = taxRulesConfigVOS.get(0);
        BigDecimal stampDutyRate = taxRulesConfigVO.getRate().divide(new BigDecimal(100));
        map.put("stampDutyRate", stampDutyRate);
        // 计算印花税
        BigDecimal stampDutyAmount = BigDecimal.valueOf(periodInvoiceAmount).multiply(stampDutyRate).setScale(2, BigDecimal.ROUND_UP);
        if (entity.getTaxPolicy().getIsStampDutyHalved() == 1) {
            stampDutyAmount = stampDutyAmount.divide(new BigDecimal("2"));
        }
        map.put("stampDutyAmount", stampDutyAmount.setScale(2, BigDecimal.ROUND_UP).longValue());
        entity.setStampDutyAmount(stampDutyAmount.setScale(2, BigDecimal.ROUND_UP).longValue());
    }

    /**
     * 税费计算————计算水利建设基金
     * @param entity
     * @param map
     */
    private void countWaterConservancyFund(TaxCalculationVO entity, Map<String, Object> map) {
        // 水利建设基金申报周期
        entity.setTaxType(TaxTypeEnum.FOUNDATION_FOR_WATER_WORKS.getValue());
        this.countTaxCalendar(entity, map);
        // 计算水利建设基金申报周期累计开票金额
        List<CountPeriodInvoiceAmountVO> list = mapper.queryCompanyInvoiceAmountByEin(entity.getCompanyId(), entity.getType(), entity.getStart(), entity.getEnd(), null, 1, 0);
        Long periodInvoiceAmount = 0L;
        if (!list.isEmpty() && null != list) {
            for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
            }
        }
        // 获取水利建设基金税率
        List<TaxRulesConfigVO> taxRulesConfigVOS = taxRulesConfigService.queryTaxRules(entity.getParkId(), TaxTypeEnum.FOUNDATION_FOR_WATER_WORKS.getValue(), entity.getCompanyType(), entity.getTaxPolicy().getWaterConservancyFundBreaksCycle(), entity.getTaxpayerType());
        TaxRulesConfigVO taxRulesConfigVO = taxRulesConfigVOS.get(0);
        BigDecimal waterConservancyFundRate = taxRulesConfigVO.getRate().divide(new BigDecimal(100));
        map.put("waterConservancyFundRate", waterConservancyFundRate);
        // 计算水利建设基金
        BigDecimal waterConservancyFundAmount = BigDecimal.valueOf(periodInvoiceAmount).multiply(waterConservancyFundRate);
        if (entity.getTaxPolicy().getIsWaterConservancyFundHalved() == 1) {
            waterConservancyFundAmount = waterConservancyFundAmount.divide(new BigDecimal("2"));
        }
        map.put("waterConservancyFundAmount", waterConservancyFundAmount.setScale(2, BigDecimal.ROUND_UP).longValue());
        entity.setWaterConservancyFundAmount(waterConservancyFundAmount.setScale(2, BigDecimal.ROUND_UP).longValue());
    }

    /**
     * 税费计算————计算总税费
     * @param entity
     * @param map
     */
    private void countAllTax(TaxCalculationVO entity, Map<String, Object> map) {
        // 本周期开票已缴税
        Long paidVatFee = entity.getPaidVatFee();
        Long paidSurcharge = entity.getPaidSurcharge();
        Long paidIncomeTax = null == entity.getPaidIncomeTax() ? 0L : entity.getPaidIncomeTax();
        // 总已缴税费
        Long alreadyTaxMoney = paidVatFee + paidSurcharge + paidIncomeTax;
        if (null != map.get("incomeAlreadyTaxMoney")) {
            paidIncomeTax = (Long) map.get("incomeAlreadyTaxMoney");
        }
        map.put("alreadyTaxMoney", paidVatFee + paidSurcharge + paidIncomeTax); // 税单中存的是周期已缴所得税
        if (entity.getCalculationType() == 1) {
            return;
        }

        // 本次开票应缴税
        Long payableVatFee = entity.getPayableVatFee();
        Long payableSurcharge = entity.getPayableSurcharge();
        Long payableIncomeTax = null == entity.getPayableIncomeTax() ? 0L : entity.getPayableIncomeTax();
        // 本周期开票缴税
        Long periodPayableVat = entity.getPeriodPayableVat();
        Long periodPayableSurcharge = entity.getPeriodPayableSurcharge();
        Long periodPayableIncomeTax = entity.getPeriodPayableIncomeTax();

        // 应缴印花税
        Long stampDutyAmount = null == entity.getStampDutyAmount() ? 0L : entity.getStampDutyAmount();
        // 应缴水利建设基金
        Long waterConservancyFundAmount = null == entity.getWaterConservancyFundAmount() ? 0L : entity.getWaterConservancyFundAmount();

        // 本次开票总税费
        Long allTax = 0L;
        // 本周期总税费
        Long shouldTaxMoney = 0L;
        // 总应退或应补税费
        Long taxFee = 0L;

        // 开票计税需要计算本次开票总税费
        if (entity.getType() == 1) {
            allTax = payableVatFee + payableSurcharge + payableIncomeTax + stampDutyAmount + waterConservancyFundAmount;
            map.put("allTax", allTax);
            return;
        }

        // 总应缴税费
        shouldTaxMoney = periodPayableVat + periodPayableSurcharge + periodPayableIncomeTax;
        map.put("shouldTaxMoney", shouldTaxMoney);

        taxFee = shouldTaxMoney - alreadyTaxMoney;
        if (taxFee > 0L) {
            map.put("recoverableTaxMoney", 0L);
            map.put("supplementTaxMoney" , taxFee);
        } else if (taxFee < 0L) {
            map.put("recoverableTaxMoney", -taxFee);
            map.put("supplementTaxMoney" , 0L);
        } else {
            map.put("recoverableTaxMoney", 0L);
            map.put("supplementTaxMoney" , 0L);
        }
    }

    @Override
    public Long payableVatFee(List<CountPeriodInvoiceAmountVO> list, int type, InvoiceOrderEntity invoiceOrder, BigDecimal vatRate, int moreThan) {

        BigDecimal payableVatFee = BigDecimal.ZERO;
        if (null == list || list.isEmpty()) {
            return 0L;
        }
        if (type == 1 && null == invoiceOrder) {
            throw new BusinessException("开票订单不存在");
        }

        for (int i = 0; i < list.size(); i++) {
            CountPeriodInvoiceAmountVO vo = list.get(i);
            if (null != vo.getVATFeeRate()) {
                payableVatFee = payableVatFee.add(new BigDecimal(vo.getCountAmountInvoiced()).multiply(vo.getVATFeeRate()).divide(new BigDecimal(1).add(vo.getVATFeeRate()),4,BigDecimal.ROUND_UP));
            } else { // 待创建订单无增值税税率
                payableVatFee = payableVatFee.add(new BigDecimal(invoiceOrder.getInvoiceAmount()).multiply(vatRate).divide(new BigDecimal(1).add(vatRate),4,BigDecimal.ROUND_UP));
            }

        }

        return payableVatFee.setScale(0, BigDecimal.ROUND_UP).longValue();
    }

    /**
     * 发送紧急邮件
     */
    private void sendUrgentMail(Map<String, Object> param) {
        DictionaryEntity dictionaryEntity = dictionaryService.getByCode("emergency_contact_email");
        if(dictionaryEntity!=null){
            String emails = dictionaryEntity.getDictValue();
            if(StringUtils.isBlank(emails)){
                return ;
            }
            String oemCode = param.get("oemCode").toString();
            OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode, OemParamsTypeEnum.EMAIL_CONFIG.getValue());
            Map<String,String> params = new HashMap<>();
            params.put("account",oemParamsEntity.getAccount());
            params.put("password",oemParamsEntity.getSecKey());
            params.put("emailHost",oemParamsEntity.getUrl());
            JSONObject jsonObject = JSONObject.parseObject(oemParamsEntity.getParamsValues());
            params.put("port",jsonObject.getString("port"));

            String subject = "税单生成失败";
            if (param.get("calculationType") == "2") {
                subject = "填报成本失败";
            }

            param.remove("calculationType");
            String content = JSON.toJSONString(param);
            content = subject + "，原因：[未配置税费政策]。相关参数：" + content;

            String[] arrs = emails.split(",");
            for (String arr : arrs) {
                try {
                    EmailUtils.send(params, "", subject, content, arr, null);
                }catch (Exception e){
                    log.error(content);
                }
            }
        }
    }

    @Override
    @Transactional
    public void invoiceConfirmGateway(String orderNo,String remark,String updateUser) {
        orderService.updateOrderStatus(null,orderNo,8);
        InvoiceOrderEntity invoiceOrder =  invoiceOrderService.queryByOrderNo(orderNo);
        if(invoiceOrder!=null){
            // 保存开票订单变更记录
            InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
            BeanUtils.copyProperties(invoiceOrder, record);
            record.setId(null);
            record.setOrderStatus(8);
            record.setAddTime(new Date());
            record.setAddUser(updateUser);
            record.setRemark(remark);
            record.setUpdateUser(null);
            record.setUpdateTime(null);
            invoiceOrderChangeRecordService.insertSelective(record);
            //开票金额回滚
            companyInvoiceRecordMapper.refund(invoiceOrder.getCompanyId(), invoiceOrder.getAddTime(), invoiceOrder.getInvoiceAmount(), updateUser, new Date());
        }
    }
    @Override
    @Transactional
    public void invoiceConfirmGatewaySuccess(String orderNo,String paymentVoucher,String remark,String updateUser) {
        orderService.updateOrderStatus(null,orderNo,3);
        //保存开票的开票记录
        InvoiceOrderEntity invoiceOrder =  invoiceOrderService.queryByOrderNo(orderNo);
        if(invoiceOrder!=null){
            invoiceOrder.setPaymentVoucher(paymentVoucher);
            invoiceOrder.setUpdateTime(new Date());
            invoiceOrder.setUpdateUser(updateUser);
            this.editByIdSelective(invoiceOrder);

            InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
            BeanUtils.copyProperties(invoiceOrder, record);
            record.setId(null);
            record.setOrderStatus(3);
            record.setAddTime(new Date());
            record.setAddUser(updateUser);
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setRemark(remark);
            invoiceOrderChangeRecordService.insertSelective(record);

            CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingMapper.queryByCompanyId(invoiceOrder.getCompanyId());
            MemberCompanyEntity com =  new MemberCompanyEntity();
            com.setId(invoiceOrder.getCompanyId());
            MemberCompanyEntity company  = memberCompanyService.selectOne(com);
            MemberBaseInfoVO member =  memberAccountService.getMemberBaseInfo(company.getMemberId());
            invoiceRecordService.createInvoiceRecord(invoiceOrder,companyTaxHostingEntity,company.getEin(), company.getParkId(), member.getId(), updateUser,true);
        }
    }

    @Override
    @Transactional
    public void thirdPartyInvoiceComplete(OrderEntity entity,String updateUser, Long accessPartyId) {
        if(entity == null){
            throw new BusinessException("订单不存在");
        }
        InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
        invEntity.setOrderNo(entity.getOrderNo());
        invEntity.setOemCode(entity.getOemCode());
        invEntity = invoiceOrderService.selectOne(invEntity);
        if (invEntity == null) {
            throw new BusinessException("开票订单不存在");
        }
        if (!Objects.equals(invEntity.getCreateWay(), InvoiceCreateWayEnum.THIRDPRATY.getValue())) {
            throw new BusinessException("开票方式不是" + InvoiceCreateWayEnum.THIRDPRATY.getValue());
        }
        //更新开票完成时间
        invEntity.setCompleteTime(new Date());
        invEntity.setUpdateTime(new Date());
        invEntity.setUpdateUser(updateUser);
        invoiceOrderService.editByIdSelective(invEntity);
        //更新开票状态
        entity.setOrderStatus(InvoiceOrderStatusEnum.SIGNED.getValue());
        invoiceOrderService.updateInvoiceStatus(invEntity, entity, updateUser, "接入方开票签收");

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
        logEntity.setLogisticsInfo("第三方接口签收");
        logEntity.setAddTime(new Date());
        logEntity.setAddUser(entity.getUpdateUser());
        logisticsInfoService.insertSelective(logEntity);
    }

    @Override
    public InvoicePayInfo queryInvoicePayInfo(String orderNo) {
        return mapper.queryInvoicePayInfo(orderNo);
    }

    @Override
    public ThirdPartyQueryInoiveInfoVO thirdPartyQueryInvoiceDetail(String orderNo, String externalOrderNo, String accessPartyCode) {
        List<ThirdPartyQueryInoiveInfoVO> list = mapper.thirdPartyQueryInvoiceList(orderNo, externalOrderNo, accessPartyCode);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        if (list.size() > 1) {
            throw new BusinessException("存在多笔相同业务来源单号订单");
        }
        ThirdPartyQueryInoiveInfoVO vo = Objects.requireNonNull(list).get(0);
        if (InvoiceOrderStatusEnum.AUDIT_FAILED.getValue().equals(vo.getOrderStatus())) {
            // 查询审核不通过原因
            Example example = new Example(InvoiceOrderChangeRecordEntity.class);
            example.createCriteria().andEqualTo("orderNo", vo.getOrderNo()).andEqualTo("orderStatus", vo.getOrderStatus());
            List<InvoiceOrderChangeRecordEntity> changeRecordEntities = invoiceOrderChangeRecordService.selectByExample(example);
            if (CollectionUtil.isNotEmpty(changeRecordEntities)) {
                vo.setAuditFailReason(changeRecordEntities.get(0).getRemark());
            }
        }
        return vo;
    }

    @Override
    public void accessPartyPush(String orderNo, String oemCode, Long accessPartyId, HashMap<String, Object> map) {
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("发送推送消息失败，订单编号不能为空");
        }

        // 查询回调地址
        OemAccessPartyEntity accessParty = Optional.ofNullable(oemAccessPartyService.findById(accessPartyId)).orElseThrow(() -> new BusinessException("未查询到接入方信息"));
        if (StringUtil.isBlank(accessParty.getCallbackUrl())) {
            return;
        }
        String callbackUrl = accessParty.getCallbackUrl();
        // 必要数据
        ThirdPartyPushVO pushVO = new ThirdPartyPushVO();
        pushVO.setCallbackUrl(callbackUrl);
        pushVO.setNum(3);
        pushVO.setOemCode(oemCode);
        // 查询第三方机构秘钥
        OemEntity oem = Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));
        pushVO.setSecretKey(oem.getOemSecret());
        pushVO.setParam(map);
        // 推送订单取消信息
        rabbitTemplate.convertAndSend("thirdPartyPush", pushVO);
    }

    @Override
    public List<String> getCancellationVoucher(String orderNo, String oemCode, Long currUserId) {
        // 查询开票订单
        InvoiceOrderEntity invOrder = Optional.ofNullable(mapper.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到开票订单"));

        // 查询企业
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(invOrder.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到企业信息"));
        if (!Objects.equals(currUserId, company.getMemberId())) {
            throw new BusinessException("所查订单不属于当前登录用户");
        }

        List<String> list = Lists.newArrayList();
        String cancellationVouchers = mapper.queryCancellationVoucher(orderNo, oemCode);
        if (StringUtil.isBlank(cancellationVouchers)) {
            return list;
        }
        String[] split = cancellationVouchers.split(",");
        for (String s : split) {
            String privateImgUrl = ossService.getPrivateImgUrl(s);
            list.add(privateImgUrl);
        }
        return list;
    }

    @Override
    @Transactional
    public  Map<String,Object> cancellation(OrderVoidInfo orderVoidInfo,String userName) {
        Map<String,Object> contentMap = new HashMap<>();
        List<CorporateAccountWithdrawalOrderEntity> corporateList = corporateAccountWithdrawalOrderService.queryWithdrawOrderByInvoiceOrderNo(orderVoidInfo.getOrderNo());
        if (CollectionUtil.isNotEmpty(corporateList)){
            throw new BusinessException("该订单已提现");
        }
        OrderEntity orderEntity = orderMapper.queryByOrderNo(orderVoidInfo.getOrderNo());
        InvoiceOrderEntity invoiceOrderEntity =  invoiceOrderService.queryByOrderNo(orderVoidInfo.getOrderNo());
        MemberAccountEntity memberAccountEntity = memberAccountService.findById(orderEntity.getUserId());
        //  作废不重开
        if (InvoiceMarkEnum.CANCELLATION.getValue().equals(orderVoidInfo.getInvoiceMark())){
            List<MessageNoticeEntity> messageList = messageNoticeService.findAllHomeNotAlertMessageByUserIdAndBusinessType(orderEntity.getUserId(),orderEntity.getOemCode(),11);
            invoiceOrderEntity.setCancellationTime(new Date());
            invoiceOrderEntity.setUpdateUser(userName);
            invoiceOrderEntity.setUpdateTime(new Date());
            invoiceOrderEntity.setInvoiceMark(InvoiceMarkEnum.CANCELLATION.getValue());
            invoiceOrderEntity.setCancellationRemark(orderVoidInfo.getCancellationRemark());
            invoiceOrderEntity.setCancellationVoucher(orderVoidInfo.getCancellationVoucher());
            invoiceOrderService.editByIdSelective(invoiceOrderEntity);
            // 保存开票订单变更记录
            InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
            BeanUtils.copyProperties(invoiceOrderEntity, record);
            record.setId(null);
            record.setOrderStatus(orderEntity.getOrderStatus());
            record.setAddTime(new Date());
            record.setAddUser(userName);
            record.setRemark("作废/红冲");
            invoiceOrderChangeRecordService.insertSelective(record);
            //  用户未存在未读通知才发送通知
            if (CollectionUtil.isEmpty(messageList)){
                //  发送通知
                DictionaryEntity dictionaryEntity = dictionaryService.getByCode("to_void_message");
                MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
                messageNoticeEntity.setOemCode(invoiceOrderEntity.getOemCode());
                messageNoticeEntity.setNoticeType(2);
                messageNoticeEntity.setIsAlert(0);
                messageNoticeEntity.setNoticePosition("1,2");
                messageNoticeEntity.setOpenMode(3);
                messageNoticeEntity.setBusinessType(11);
                messageNoticeEntity.setNoticeTitle("开票订单已作废/红冲");
                messageNoticeEntity.setNoticeContent(dictionaryEntity.getDictValue());
                messageNoticeEntity.setUserPhones(memberAccountEntity.getMemberAccount());
                messageNoticeEntity.setStatus(0);
                messageNoticeEntity.setUserId(orderEntity.getUserId());
                messageNoticeEntity.setUserType(1);
                messageNoticeEntity.setAddTime(new Date());
                messageNoticeEntity.setAddUser("admin");
                messageNoticeService.saveMessageNotice(messageNoticeEntity);
            }
            return contentMap;
        }
        if (InvoiceMarkEnum.REOPEN.getValue().equals(invoiceOrderEntity.getInvoiceMark())){
            throw new BusinessException("重开订单不支持再次重开！");
        }
        //  存在未支付的订单
        List<OrderNoVO> unpaidList = invoiceOrderService.getUnpaidList(orderEntity.getUserId(), orderEntity.getOemCode(), invoiceOrderEntity.getCompanyId());
        if (CollectionUtil.isNotEmpty(unpaidList)){
            throw new BusinessException("存在未支付订单！");
        }
       //  流水状态
        if (BankWaterStatusEnum.TO_BE_AUDIT.getValue().equals(invoiceOrderEntity.getBankWaterStatus()) ){
            throw new BusinessException("当前流水状态不允许重开！");
        }
        //  成果状态
        if (BankAchievementStatusEnum.TO_BE_AUDIT.getValue().equals(invoiceOrderEntity.getAchievementStatus()) ){
            throw new BusinessException("当前成果状态不允许重开！");
        }
        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(orderVoidInfo.getCompanyId());
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }

        //校验企业托管费过期状态（1-正常 2-即将过期 3-已过期）
        if (Objects.equals(company.getOverdueStatus(),MemberCompanyOverdueStatusEnum.OVERDUE.getValue())){
            throw new BusinessException("企业已过期，无法开票");
        }
        // 校验公司状态(状态：1->正常；2->禁用；4->已注销 5->注销中)
        if (!Objects.equals(company.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("公司状态异常，无法开票");
        } else if (Objects.equals(company.getIsTopUp(), 1)) {//校验是否满额(是否满额 0->否 1->是)
            throw new BusinessException("公司开票已满额，无法开票");
        }

        TaxPolicyEntity taxPolicyEntity = Optional.ofNullable(taxPolicyService.queryTaxPolicyByParkId(company.getParkId(), company.getCompanyType(),company.getTaxpayerType())).orElseThrow(() -> new BusinessException("园区税费政策不存在"));
        if (CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue().equals(company.getTaxpayerType())) { // 一般纳税人无限额配置，不进行校验
            // 近12个月可开票金额校验
            CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.findByCompanyId(company.getId());
            if (null == companyInvRecord) {
                    throw new BusinessException("未查询到企业开票记录");
                } else if (null == companyInvRecord.getRemainInvoiceAmount()) {
                    throw new BusinessException("未查询到企业年度可开票金额");
                }
            MemberAccountEntity member = memberAccountService.findById(company.getMemberId());
            if (null == member) {
                throw new BusinessException("未查询到用户信息");
            }
            InvoiceStatisticsViewVO view = Optional.ofNullable(mapper.queryCompanyInvoiceRecordStatisticsView(member.getParentMemberId(), company.getId())).orElse(new InvoiceStatisticsViewVO());
                Long totalInvoiceAmount = taxPolicyEntity.getTotalInvoiceAmount();
                if (totalInvoiceAmount == null) {
                    throw new BusinessException("园区税费政策年度开票总额未配置");
                }
                if ((totalInvoiceAmount - view.getUseTotalInvoiceAmount()) < orderVoidInfo.getInvoiceAmount()) {
                    throw new BusinessException("开票金额不能大于近12个月剩余可开票额度");
                }
            // 计算本月累计开票金额
            String monFirstDay = DateUtil.getMonFirstDay();
            Date start = DateTime.parse(monFirstDay).toDate();
            String monLastDay = DateUtil.getMonLastDay();
            Date end = DateTime.parse(monLastDay).toDate();
            List<CountPeriodInvoiceAmountVO> monthList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long monthInvoiceAmount = 0L;
            if (!monthList.isEmpty() && null != monthList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : monthList) {
                    monthInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getMonthInvoiceAmount() != null && monthInvoiceAmount + orderVoidInfo.getInvoiceAmount() > taxPolicyEntity.getMonthInvoiceAmount()){
                throw new BusinessException("本月累计开票金额不能超过"+taxPolicyEntity.getMonthInvoiceAmount()/100 + "元");
            }

            // 计算本周期累计开票金额
            int year =  DateUtil.getYear(new Date());
            int quarter = Integer.parseInt(DateUtil.getQuarter());
            String[]  s = DateUtil.getCurrQuarter(year, quarter);
            start = DateTime.parse(s[0]).toDate();
            end = DateTime.parse(s[1]).toDate();
            List<CountPeriodInvoiceAmountVO> perList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
            Long periodInvoiceAmount = 0L;
            if (!perList.isEmpty() && null != perList) {
                for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : perList) {
                    periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
                }
            }
            if (taxPolicyEntity.getQuarterInvoiceAmount() != null && (periodInvoiceAmount+orderVoidInfo.getInvoiceAmount()) > taxPolicyEntity.getQuarterInvoiceAmount()){
                throw new BusinessException("本季累计开票金额不能超过"+taxPolicyEntity.getQuarterInvoiceAmount()/100 + "元");
            }
        }
        //校验税单
        String overtime = dictionaryService.getValueByCode("tax_bill_overtime");
        if (StringUtils.isNotBlank(overtime)) {
            Integer time = Integer.valueOf(overtime);
            if (time > 0) {
                CompanyTaxBillQuery query = new CompanyTaxBillQuery();
                query.setCompanyId(company.getId());
                query.setMemberId(company.getMemberId());
                query.setOemCode(company.getOemCode());
                query.setTaxBillStatus(TaxBillStatusEnum.TAX_TO_BE_PAID.getValue());
                query.setOverTime(time);
                List<CompanyTaxBillListVO> bills = companyTaxBillService.listCompanyTaxBill(query);
                long count = bills.stream().filter(u -> StringUtils.isNotBlank(u.getOverTimeDesc())).count();
                if (count > 0) {
                    throw new BusinessException(ResultConstants.TAX_BILL_OVER_TIME);
                }
            }
        }
        // 若该企业存在税单待处理、待付款的注销订单，则弹窗提示
//        List<String> orderNoList =  orderService.getOderNoByCompanyAndOrderStatus(company.getId());
        List<String> orderNoList = orderService.getOderNoByCompany(company.getId());
        if (CollectionUtil.isNotEmpty(orderNoList)){
            contentMap.put("type",1);
            contentMap.put("content","该企业存在注销订单，请先取消注销订单后继续操作~");
            return contentMap;
        }
        // 校验是否存在超时未确认成本税单
        PendingTaxBillQuery pendingTaxBillQuery = new PendingTaxBillQuery();
        pendingTaxBillQuery.setEin(company.getEin());
        pendingTaxBillQuery.setCompanyId(company.getId());
        pendingTaxBillQuery.setStatusRange(1);
        List<PendingTaxBillVO> pendingTaxBillVOS = companyTaxBillService.pendingTaxBill(pendingTaxBillQuery);
        if (CollectionUtil.isNotEmpty(pendingTaxBillVOS)) {
            List<PendingTaxBillVO> collect = pendingTaxBillVOS.stream().filter(x -> x.getTimeDifference() < 0).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                contentMap.put("type",1);
                contentMap.put("content","该企业存在超时未确认成本的税单,无法继续开票！");
                return contentMap;
            }
        }
        // 查询企业开票额度
//        Example example = new Example(CompanyInvoiceRecordEntity.class);
//        example.createCriteria().andEqualTo("companyId",entity.getCompanyId()).
//                andEqualTo("oemCode",oemCode).
//                andGreaterThanOrEqualTo("endTime",new Date());
//        CompanyInvoiceRecordEntity companyInvRecord = companyInvoiceRecordMapper.selectOneByExample(example);
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
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(orderEntity.getUserId());
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if (null == memberLevel) {
            throw new BusinessException("未查询到会员等级");
        }
        // 查询oem机构信息
        OemEntity oem = oemService.getOem(orderEntity.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }
        // 保存会员订单关系
        MemberOrderRelaEntity more = getUserTree(orderEntity.getUserId(), orderEntity.getOemCode(), 2);//获取一二级推广人和分润信息
        if (more != null) {
            more.setMemberId(orderEntity.getUserId());
            more.setMemberLevel(memberLevel.getLevelNo());//会员等级
            more.setOemCode(orderEntity.getOemCode());
            more.setOemName(oem.getOemName());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            memberOrderRelaService.insertSelective(more);
        }

        // 查询产品id
        ProductEntity product = new ProductEntity();
        product.setOemCode(orderEntity.getOemCode());
        product.setProdType(companyTypeTransferProductType(company.getCompanyType()));
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(orderEntity.getUserId());

        // TODO 作废重新开直接将状态变成待客服审核并生成工单
        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setUserId(orderEntity.getUserId());
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.INVOICE.getValue());
        mainOrder.setOrderStatus(InvoiceOrderStatusEnum.UNCHECKED.getValue());
        mainOrder.setProductId(product.getId());
        mainOrder.setProductName(product.getProdName());
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setOemCode(orderEntity.getOemCode());
        mainOrder.setParkId(company.getParkId());
        mainOrder.setOrderAmount(orderEntity.getOrderAmount());
        mainOrder.setPayAmount(0L);
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(userName);
        // 默认设置为消费钱包
        if (null != invoiceOrderEntity.getWalletType()) {
            mainOrder.setWalletType(invoiceOrderEntity.getWalletType());
        }
        if (orderEntity.getSourceType() != null) {
            mainOrder.setSourceType(orderEntity.getSourceType());
        }
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        //  添加用户映射id
        mainOrder.setChannelUserId(orderEntity.getChannelUserId());
        // 新生成的订单无需推送
        mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());

        /**
         * 保存开票服务费阶段
         */
        MemberCompanyDetailVo memberCompanyDetail = memberCompanyService.getMemberCompanyDetail(orderEntity.getUserId(), orderEntity.getOemCode(), company.getId());
        // 计算服务费明细
        List<InvoiceServiceFeeDetailVO> list = invoiceServiceFeeDetailService.countServiceDetail(orderEntity.getUserId(),company.getIndustryId(),company.getParkId(),product.getProdType(), 0L, memberCompanyDetail.getUseInvoiceAmountYear(), orderEntity.getOemCode());
        if(list == null || list.size()<1){
            list = invoiceServiceFeeDetailService.countServiceDetail(orderEntity.getUserId(), company.getId(), product.getId(), 0L, memberCompanyDetail.getUseInvoiceAmountYear(), orderEntity.getOemCode());
        }
        if(list!=null &&list.size()>0){
            for (InvoiceServiceFeeDetailVO invoiceServiceFeeDetail : list) {
                // 保存服务费数据
                InvoiceServiceFeeDetailEntity serviceFeeDetailEntity = new InvoiceServiceFeeDetailEntity();
                serviceFeeDetailEntity.setOrderNo(orderNo);
                serviceFeeDetailEntity.setCompanyId(company.getId());
                serviceFeeDetailEntity.setOemCode(orderEntity.getOemCode());
                serviceFeeDetailEntity.setAddTime(new Date());
                serviceFeeDetailEntity.setAddUser(member.getMemberAccount());
                serviceFeeDetailEntity.setPhaseAmount(invoiceServiceFeeDetail.getPhaseAmount());
                serviceFeeDetailEntity.setFeeRate(invoiceServiceFeeDetail.getFeeRate());
                serviceFeeDetailEntity.setFeeAmount(invoiceServiceFeeDetail.getFeeAmount());
                invoiceServiceFeeDetailService.insertSelective(serviceFeeDetailEntity);
            }
        }else{
            throw new BusinessException("未找到开票服务费配置");
        }
        mainOrder.setDiscountAmount(0L);
        if(orderEntity.getCrowdLabelId()!=null){
            mainOrder.setCrowdLabelId(orderEntity.getCrowdLabelId());
        }
        try{
            orderService.insertSelective(mainOrder);
        }catch (Exception e){
            throw new BusinessException("数据格式错误");
        }

        //查询企业对公户
        CompanyCorporateAccountEntity companyCorporateAccountEntity = companyCorporateAccountService.queryCorpByCompanyId(company.getId());

        // 保存开票订单信息
        InvoiceOrderEntity invoice = new InvoiceOrderEntity();
        BeanUtils.copyProperties(invoiceOrderEntity,invoice);
        invoice.setOrderNo(orderNo);
        invoice.setId(null);
        invoice.setCategoryName(orderVoidInfo.getCategoryName());
        invoice.setCategoryId(orderVoidInfo.getCategoryId());
        invoice.setInvoiceType(orderVoidInfo.getInvoiceType());
        invoice.setVatFeeRate(orderVoidInfo.getVATFeeRate().divide(new BigDecimal(100)));
        invoiceOrderEntity.setCancellationVoucher(orderVoidInfo.getCancellationVoucher());
        if (StringUtil.isNotBlank(orderVoidInfo.getCancellationRemark())){
            invoiceOrderEntity.setCancellationRemark(orderVoidInfo.getCancellationRemark());
        }
        if (StringUtil.isNotBlank(orderVoidInfo.getInvoiceRemark())){
            invoice.setInvoiceRemark(orderVoidInfo.getInvoiceRemark());
        }
        invoice.setInvoiceMark(InvoiceMarkEnum.REOPEN.getValue());
        invoice.setIsReopen(1);
        invoiceOrderEntity.setRelevanceOrderNo(orderNo);
        invoiceOrderEntity.setCancellationTime(new Date());
        invoiceOrderEntity.setInvoiceMark(InvoiceMarkEnum.CANCELLATION.getValue());
        invoice.setRelevanceOrderNo(invoiceOrderEntity.getOrderNo());
        invoice.setAddTime(new Date());
        invoice.setAddUser(userName);
        invoice.setServiceFee(0L);
        invoice.setVatFee(null);
        invoice.setVatPayment(null);
        invoice.setSurcharge(null);
        invoice.setSurchargePayment(null);
        invoice.setPersonalIncomeTax(null);
        invoice.setIncomeTaxPayment(null);
        invoice.setRefundTaxFee(null);
        invoice.setServiceFeeDiscount(0L);
        invoice.setCompleteTime(null);
        invoice.setConfirmInvoiceTime(null);
        invoice.setCourierNumber(null);
        invoice.setCourierCompanyName(null);
        invoice.setUpdateUser(null);
        invoice.setUpdateTime(null);
        invoice.setTaxYear(null);
        invoice.setTaxSeasonal(null);
        invoice.setPayAmount(0L);
        invoiceOrderEntity.setUpdateTime(new Date());
        invoiceOrderEntity.setUpdateUser(userName);
        if (Objects.nonNull(companyCorporateAccountEntity)) {
            invoice.setCorporateAccount(companyCorporateAccountEntity.getCorporateAccount());
            invoice.setCorporateAccountBankName(companyCorporateAccountEntity.getCorporateAccountBankName());
        }
        invoice.setRemark("发票作废重开");
        //修改发票抬头
        invoice.setCompanyName(orderVoidInfo.getHeadCompanyName());
        invoice.setCompanyAddress(orderVoidInfo.getCompanyAddress());
        invoice.setEin(orderVoidInfo.getEin());
        invoice.setPhone(orderVoidInfo.getPhone());
        invoice.setBankName(orderVoidInfo.getBankName());
        invoice.setBankNumber(orderVoidInfo.getBankNumber());
        try{
            invoiceOrderService.insertSelective(invoice);
            invoiceOrderService.editByIdSelective(invoiceOrderEntity);
        }catch (Exception e){
            throw new BusinessException("数据格式错误");
        }

        // 保存开票订单变更记录
        InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invoice, record);
        record.setId(null);
        record.setOrderStatus(InvoiceOrderStatusEnum.UNPAID.getValue());
        record.setAddTime(new Date());
        record.setAddUser(userName);
        if (Objects.nonNull(companyCorporateAccountEntity)) {
            record.setCorporateAccount(companyCorporateAccountEntity.getCorporateAccount());
            record.setCorporateAccountBankName(companyCorporateAccountEntity.getCorporateAccountBankName());
        }
        invoiceOrderChangeRecordService.insertSelective(record);

        // 保存作废红冲订单变更记录
        record = new InvoiceOrderChangeRecordEntity();
        BeanUtils.copyProperties(invoiceOrderEntity, record);
        record.setId(null);
        record.setOrderStatus(orderEntity.getOrderStatus());
        record.setAddTime(new Date());
        record.setAddUser(userName);
        record.setRemark("作废/红冲");
        if (Objects.nonNull(companyCorporateAccountEntity)) {
            record.setCorporateAccount(companyCorporateAccountEntity.getCorporateAccount());
            record.setCorporateAccountBankName(companyCorporateAccountEntity.getCorporateAccountBankName());
        }
        invoiceOrderChangeRecordService.insertSelective(record);

        // 计算支付金额
        PayInformationVO payAmountVO = invoiceOrderService.getInvoicePayInfo(orderEntity.getUserId(), orderEntity.getOemCode(), invoice.getOrderNo(), orderVoidInfo.getVATFeeRate());
        TaxFeeDetailVO taxFeeDetail = payAmountVO.getTaxFeeDetail();
        //开票类目来自企业开票类目表对应的基础类目库id
        CompanyInvoiceCategoryEntity e = new CompanyInvoiceCategoryEntity();
        e.setCategoryBaseId(invoice.getCategoryId());
        e.setCompanyId(company.getId());
        CompanyInvoiceCategoryEntity companyInvoiceCategoryEntity = companyInvoiceCategoryService.selectOne(e);
        if (companyInvoiceCategoryEntity == null) {
            throw new BusinessException("企业开票类目不存在");
        }
        if (!Objects.equals(companyInvoiceCategoryEntity.getCompanyId(), company.getId())) {
            throw new BusinessException("开票类目不属于当前企业");
        }
        invoice.setCategoryId(companyInvoiceCategoryEntity.getCategoryBaseId());
        //开票类目以**结尾替换最后一个*号为goodsName
        String categoryName = invoice.getCategoryName();
        String goodsName = invoice.getGoodsName();
        if (StringUtils.endsWith(categoryName,"**") && StringUtil.isNotBlank(goodsName)){
            categoryName = StringUtils.overlay(categoryName, goodsName, categoryName.length()-1, categoryName.length());
        }
        invoice.setCategoryName(categoryName);
        invoice.setVatFee(taxFeeDetail.getPayableVatFee());
        invoice.setVatFeeRate(taxFeeDetail.getVatFeeRate().divide(new BigDecimal(100)));
        invoice.setSurcharge(taxFeeDetail.getPayableSurcharge());
        invoice.setSurchargeRate(taxFeeDetail.getSurchargeRate().divide(new BigDecimal(100)));
        invoice.setPaidVatFee(taxFeeDetail.getPaidVatFee());
        invoice.setPaidSurcharge(taxFeeDetail.getPaidSurcharge());
        invoice.setPeriodInvoiceAmount(taxFeeDetail.getPeriodInvoiceAmount());
        invoice.setHistoricalInvoiceAmount(payAmountVO.getHistoricalInvoiceAmount());
        invoice.setTaxpayerType(taxFeeDetail.getTaxpayerType());
        if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            invoice.setPersonalIncomeTax(taxFeeDetail.getPayableIncomeTax());
            invoice.setPersonalIncomeTaxRate(taxFeeDetail.getIncomeTaxRate().divide(new BigDecimal(100)));
            invoice.setPaidIncomeTax(taxFeeDetail.getPaidIncomeTax());
            invoice.setPaidIncomeTaxYear(taxFeeDetail.getPaidIncomeTaxYear());
        } else {
            invoice.setStampDutyRate(taxFeeDetail.getStampDutyRate());
            invoice.setStampDutyAmount(taxFeeDetail.getStampDutyAmount());
            invoice.setWaterConservancyFundRate(taxFeeDetail.getWaterConservancyFundRate());
            invoice.setWaterConservancyFundAmount(taxFeeDetail.getWaterConservancyFundAmount());
            invoice.setVatBreaksCycle(taxFeeDetail.getVATBreaksCycle());
            invoice.setSurchargeBreaksCycle(taxFeeDetail.getSurchargeBreaksCycle());
            invoice.setStampDutyBreaksCycle(taxFeeDetail.getStampDutyBreaksCycle());
            invoice.setWaterConservancyFundBreaksCycle(taxFeeDetail.getWaterConservancyFundBreaksCycle());
            invoice.setIncomeTaxBreaksCycle(taxFeeDetail.getIncomeTaxBreaksCycle());
        }
        // 所得税为应税所得率征收方式时才有应税所得率
        if (taxFeeDetail.getLevyWay() != null && taxFeeDetail.getLevyWay().equals(LevyWayEnum.TAXABLE_INCOME_RATE.getValue())
                && MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
            invoice.setTaxableIncomeRate(taxFeeDetail.getTaxableIncomeRate().divide(new BigDecimal(100)));
        }
        invoice.setPayAmount(0L);
        invoice.setPostageFees(0L);
        try{
            invoiceOrderService.editByIdSelective(invoice);
        }catch (Exception ee){
            throw new BusinessException("数据格式错误");
        }

        //生成支付流水并将生成工单
        PayWaterEntity water = new PayWaterEntity();
        HashMap<String, String> invoiceMap = Maps.newHashMap();
        //组装参数
        InvOrderPayDTO entity = new InvOrderPayDTO();
        entity.setAmount(mainOrder.getPayAmount());
        entity.setOrderType(6);
        entity.setOrderNo(orderNo);
        entity.setUpdateUser(userName);
        entity.setGoodsName("开票订单作废重开");
        //将状态重新设值，方便调用余额支付的通用方法
        if(company.getCompanyType()==1){
            mainOrder.setOrderStatus(1);
        }else{
            mainOrder.setOrderStatus(11);
        }
        balancePayOrderOfInvoice(invoiceMap,mainOrder,member,null,oem,entity,null,water);

//        List<MessageNoticeEntity> messageList = messageNoticeService.findAllHomeNotAlertMessageByUserIdAndBusinessType(orderEntity.getUserId(),orderEntity.getOemCode(),12);
//        if (CollectionUtil.isEmpty(messageList)) {
//            //  发送通知
//            DictionaryEntity dictionaryEntity = dictionaryService.getByCode("cancellation");
//            MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
//            messageNoticeEntity.setOemCode(invoiceOrderEntity.getOemCode());
//            messageNoticeEntity.setNoticeType(2);
//            messageNoticeEntity.setIsAlert(0);
//            messageNoticeEntity.setNoticePosition("1,2");
//            messageNoticeEntity.setOpenMode(3);
//            messageNoticeEntity.setBusinessType(12);
//            messageNoticeEntity.setNoticeTitle("作废重开订单待处理");
//            messageNoticeEntity.setNoticeContent(dictionaryEntity.getDictValue());
//            messageNoticeEntity.setUserPhones(memberAccountEntity.getMemberAccount());
//            messageNoticeEntity.setStatus(0);
//            messageNoticeEntity.setUserId(orderEntity.getUserId());
//            messageNoticeEntity.setUserType(1);
//            messageNoticeEntity.setAddTime(new Date());
//            messageNoticeEntity.setAddUser("admin");
//            messageNoticeService.saveMessageNotice(messageNoticeEntity);
//        }
//        // 短信通知
//        Map<String, Object> map = new HashMap<>();
//        OemEntity oemEntity = oemService.getOem(orderEntity.getOemCode());
//        map.put("oemName",oemEntity.getOemName());
//        smsService.sendTemplateSms(memberAccountEntity.getMemberAccount(),orderEntity.getOemCode(), VerifyCodeTypeEnum.CANCELLATION.getValue(),map,1);
        return contentMap;
    }

    @Override
    public List<String> findExistPendingInvOrder(Long companyId, Date afterTheTime, Date beforeTheTime, BigDecimal serviceFeeRate, String orderNo) {
        return mapper.queryExistPendingInvOrder(companyId, afterTheTime, beforeTheTime, serviceFeeRate, orderNo);
    }

    @Override
    public List<InvoiceOrderEntity> findExistPendingOrder(List<Long> companyId, Date addTime,String groupOrderNo) {
        return mapper.findExistPendingOrder(companyId,addTime,groupOrderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resubmit(String orderNo, Long currUserId) {
        // 校验当前登录用户与订单所属用户是否一致
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(currUserId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        if (!Objects.equals(member.getId(), order.getUserId())) {
            throw new BusinessException("订单所属用户与当前登录用户不一致");
        }
        // 校验订单状态是否为审核不通过
        if (!Objects.equals(10, order.getOrderStatus()) && !Objects.equals(8, order.getOrderStatus())) {
            throw new BusinessException("当前订单不允许重新提交");
        }
        // 查询旧的开票订单
        InvoiceOrderEntity invOrder = Optional.ofNullable(invoiceOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到开票订单"));
        // 发票类型校验（未托管或者开专票(重开不会改变开票类型)，只能开纸票不能开电票）
        if (InvoiceWayEnum.ELECTRON.getValue().equals(invOrder.getInvoiceWay())) {
            // 查询企业托管状态
            Optional.ofNullable(companyTaxHostingService.getCompanyTaxHostingByCompanyId(invOrder.getCompanyId(), 1)).orElseThrow(() -> new BusinessException("未托管企业无法开电票"));
        }
        // 创建新的开票订单
        CreateInvoiceOrderDTO invoiceOrderDTO = new CreateInvoiceOrderDTO();
        ObjectUtil.copyObject(invOrder, invoiceOrderDTO);
        invoiceOrderDTO.setVatRate(invOrder.getVatFeeRate().multiply(new BigDecimal("100")));
        String newOrderNo = invoiceOrderService.createInvoiceOrder(member.getId(), member.getOemCode(), invoiceOrderDTO, "1", true);
        // 为新的开票订单保存合同流水等数据
        if (invOrder.getInvoiceAmount() <= 1000000L) {
            return newOrderNo;
        }
        InvoiceOrderEntity invoiceOrder = Optional.ofNullable(invoiceOrderService.queryByOrderNo(newOrderNo)).orElseThrow(() -> new BusinessException("未查询到重开开票订单"));
        invoiceOrder.setAccountStatement(invOrder.getAccountStatement()); // 银行流水截图
        invoiceOrder.setBusinessContractImgs(invOrder.getBusinessContractImgs()); // 业务合同
        invoiceOrder.setAchievementImgs(invOrder.getAchievementImgs());// 成功图片
        invoiceOrder.setAchievementVideo(invOrder.getAchievementVideo()); // 成功视频
        invoiceOrderService.editByIdSelective(invoiceOrder);
        return newOrderNo;
    }

    @Override
    public List<String> getOrderNoByCreateWayAndCompanyId(Long company) {
        return mapper.getOrderNoByCreateWayAndCompanyId(company);
    }

    @Override
    public Long getquarterAmountByCompanyTypeAndParkId(Long companyId, Long parkId) {
        return mapper.getquarterAmountByCompanyTypeAndParkId(companyId,parkId);
    }

    @Override
    public Map<String, Object> taxMonitoringQuery(Long companyId, Long invoiceAmount) {
        if (null == companyId) {
            throw new BusinessException("企业id为空");
        }
        if (null == invoiceAmount) {
            throw new BusinessException("开票金额为空");
        }

        Map<String, Object> map = Maps.newHashMap();
        // 查询企业
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(companyId)).orElseThrow(() -> new BusinessException("未查询到企业信息"));
        // 查询企业类型对应的税务监控配置
        String value = dictionaryService.getValueByCode(company.getTaxMonitoringDictCode());
        if (StringUtil.isBlank(value)) {
            return map;
        }
        // 校验企业所在园区是否与配置园区一致
        Long quotaWarnAmount = null;
        JSONArray config = JSONObject.parseArray(value);
        for (Object o : config) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (company.getParkId().toString().equals(jsonObject.getString("parkId"))) {
                quotaWarnAmount = jsonObject.getLong("quotaWarnAmount");
            }
        }
        if (null == quotaWarnAmount) {
            return map;
        }
        // 计算本周期累计开票金额
        int year =  DateUtil.getYear(new Date());
        int quarter = Integer.parseInt(DateUtil.getQuarter());
        String[]  s = DateUtil.getCurrQuarter(year, quarter);
        Date start = DateTime.parse(s[0]).toDate();
        Date end = DateTime.parse(s[1]).toDate();
        List<CountPeriodInvoiceAmountVO> perList = mapper.queryCompanyInvoiceAmountByEin(company.getId(), 1, start, end, null, 1, 0);
        Long periodInvoiceAmount = 0L;
        if (!perList.isEmpty() && null != perList) {
            for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : perList) {
                periodInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
            }
        }
        // 校验企业季开票是否超过限额
        if (periodInvoiceAmount + invoiceAmount <= quotaWarnAmount*100) {
            return map;
        }
        map.put("quotaWarnAmount", quotaWarnAmount/10000);
        map.put("quotaWarn", "本季累计开票金额已超过" + quotaWarnAmount/10000 + "万，可能会被税务机构进行监控，请确定是否继续开票？");
        return map;
    }

    @Override
   public List<CountPeriodInvoiceAmountVO> queryCompanyInvoiceAmountByEin(Long companyId,  int type, Date start,
                                                                    Date end, Integer invoiceType,
                                                                    Integer isCurrentCycle, Integer isAcrossQuarter){
        return this.mapper.queryCompanyInvoiceAmountByEin(companyId,type,start,end,invoiceType,isCurrentCycle,isAcrossQuarter);
   }

    /**
     * 开票订单财务审核
     * @param orderNo 订单号
     * @param auditResult 审核结果 1-通过 2-审核不通过
     * @param receiptPaymentVoucher 收款凭证
     * @param errorRemark 失败原因
     */
    @Override
    @Transactional
    public void invoicePaymentReview(String orderNo,Integer auditResult,String receiptPaymentVoucher,String errorRemark,String userName){
        InvoiceOrderEntity invoiceOrderEntity = queryByOrderNo(orderNo);
        if(invoiceOrderEntity == null){
            throw new BusinessException("未查询到开票订单数据");
        }
        if(auditResult == 1){ //审核通过，将订单状态改成待审核，并生成工单和支付流水
            //校验是否存在更早的待财务审核订单
            Integer count = mapper.checkInvoicePaymentReview(invoiceOrderEntity.getCompanyId(),invoiceOrderEntity.getAddTime(),1,invoiceOrderEntity.getOrderNo());
            if(count!=null && count>0){
                throw new BusinessException("为了避免服务费计算错误，请先审核时间更早的开票订单");
            }
            //根据开票订单表的收款凭证
            invoiceOrderEntity.setReceiptPaymentVoucher(receiptPaymentVoucher);
            invoiceOrderEntity.setRemark("更新收款凭证");
            invoiceOrderEntity.setUpdateTime(new Date());
            invoiceOrderEntity.setUpdateUser(userName);
            editByIdSelective(invoiceOrderEntity);

            //生成支付流水并生成工单
            PayWaterEntity water = new PayWaterEntity();
            HashMap<String, String> map = Maps.newHashMap();

            OrderEntity order = Optional.of(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单"));
            MemberAccountEntity member = Optional.ofNullable(this.memberAccountService.findById(order.getUserId())).orElseThrow(() -> new BusinessException("未查询到员账号"));
            OemEntity oemEntity = oemService.getOem(invoiceOrderEntity.getOemCode());
            if(oemEntity==null){
                throw new BusinessException("未找到oem机构信息");
            }
            //组装参数
            InvOrderPayDTO entity = new InvOrderPayDTO();
            entity.setAmount(order.getPayAmount());
            entity.setOrderType(6);
            entity.setOrderNo(orderNo);
            entity.setReceiptPaymentVoucher(receiptPaymentVoucher);
            entity.setUpdateUser(userName);
            entity.setGoodsName("非个体开票，线下打款");
            balancePayOrderOfInvoice(map,order,member,null,oemEntity,entity,null,water);
        }else if(auditResult == 2){ //审核不通过，需要校验是否有后续创建的待审核订单，将订单状态改成 审核不通过
            //校验是否存在之后的待财务审核订单
            Integer count = checkInvoicePaymentReview(invoiceOrderEntity.getCompanyId(),invoiceOrderEntity.getAddTime(),2,invoiceOrderEntity.getOrderNo());
            if(count!=null && count>0){
                throw new BusinessException("为了避免服务费计算错误，请先取消/审核不通过企业后续创建的开票订单");
            }
            //将后续创建的开票订单未付款订单标记为税费重新计算

            //修改订单状态
            OrderEntity order = Optional.of(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单"));
            order.setOrderStatus(InvoiceOrderStatusEnum.AUDIT_FAILED.getValue());
            order.setUpdateUser(userName);
            order.setUpdateTime(new Date());
            order.setRemark("财务审核未通过，"+errorRemark);
            order.setProfitAmount(invoiceOrderEntity.getServiceFee() - invoiceOrderEntity.getServiceFeeDiscount());
            orderMapper.updateByPrimaryKeySelective(order);

            //根据开票订单表的收款凭证
            invoiceOrderEntity.setRemark("财务审核未通过，"+errorRemark);
            invoiceOrderEntity.setUpdateTime(new Date());
            invoiceOrderEntity.setUpdateUser(userName);
            editByIdSelective(invoiceOrderEntity);

            // 添加开票订单变更记录
            InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
            BeanUtil.copyProperties(invoiceOrderEntity, record);
            record.setId(null);
            record.setOrderStatus(InvoiceOrderStatusEnum.TO_PAYMENT_REVIEW.getValue());
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setAddTime(new Date());
            record.setAddUser(userName);
            record.setRemark("财务审核未通过，"+errorRemark);
            invoiceOrderChangeRecordService.insertSelective(record);

            //小规模纳税人 开票金额回滚(重开订单不回滚年开票记录)
//            if (invoiceOrderEntity.getTaxpayerType().equals(1)) {
            companyInvoiceRecordMapper.refund(invoiceOrderEntity.getCompanyId(), invoiceOrderEntity.getAddTime(), invoiceOrderEntity.getInvoiceAmount(),userName, new Date());
//            }

            // 查询是否有其他待付款的的自助开票订单
            List<OrderNoVO> unpaidList = invoiceOrderService.getUnpaidList(order.getUserId(), order.getOemCode(), invoiceOrderEntity.getCompanyId());
            if (null != unpaidList && !unpaidList.isEmpty()) {
                for (int i = 0; i < unpaidList.size(); i++) {
                    if (!Objects.equals(unpaidList.get(i).getInvoiceMark(), 0)) {
                        // 跳过作废标识非正常状态的代付款订单
                        continue;
                    }
                    invoiceOrderEntity = Optional.ofNullable(invoiceOrderService.queryByOrderNo(unpaidList.get(i).getOrderNo())).orElseThrow(() -> new BusinessException("未查询到未付款开票订单信息"));
                    invoiceOrderEntity.setIsRecalculateServiceFee(0);
                    invoiceOrderEntity.setUpdateTime(new Date());
                    invoiceOrderEntity.setUpdateUser(userName);
                    invoiceOrderService.editByIdSelective(invoiceOrderEntity);
                    InvoiceOrderChangeRecordEntity invoiceOrderChangeRecordEntity = new InvoiceOrderChangeRecordEntity();
                    ObjectUtil.copyObject(invoiceOrderEntity, invoiceOrderChangeRecordEntity);
                    invoiceOrderChangeRecordEntity.setId(null);
                    invoiceOrderChangeRecordEntity.setOrderStatus(order.getOrderStatus());
                    invoiceOrderChangeRecordEntity.setAddTime(new Date());
                    invoiceOrderChangeRecordEntity.setAddUser(userName);
                    invoiceOrderChangeRecordEntity.setUpdateUser(null);
                    invoiceOrderChangeRecordEntity.setUpdateTime(null);
                    invoiceOrderChangeRecordEntity.setRemark("添加费用重算标识");
                    invoiceOrderChangeRecordService.insertSelective(invoiceOrderChangeRecordEntity);
                }
            }


        }
    }

    /**
     * 校验待财务审核开票订单数据
     * @param companyId 企业id
     * @param updateTime 更新时间
     * @param isBefore 是否当前待财务审核之前的数据  1-当前订单之前 2-当前订单之后
     * @param orderNo 订单号
     * @return
     */
    @Override
    public Integer checkInvoicePaymentReview(Long companyId,Date updateTime,Integer isBefore,String orderNo){
        return mapper.checkInvoicePaymentReview(companyId,updateTime,isBefore,orderNo);
    }

    @Override
    public Map<String, String> offlinePaymentDetail(String oemCode) {
        if (StringUtil.isBlank(oemCode)) {
            throw new BusinessException("机构编码不能为空");
        }
        Map<String, String> map = Maps.newHashMap();

        // 查询机构信息
        OemEntity oemEntity = Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));

        map.put("customerServiceTel", oemEntity.getCustomerServiceTel());
        map.put("companyName", oemEntity.getCompanyName());
        map.put("bankNumber", oemEntity.getReceivingBankAccount());
        map.put("bankName", oemEntity.getReceivingBankAccountBranch());
        return map;
    }

    @Override
    @Transactional
    public void uploadPaymentVoucher(String orderNo, String vouchers) {
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        if (StringUtil.isBlank(vouchers)) {
            throw new BusinessException("上传凭证不能为空");
        }

        String[] paymentVouchers = vouchers.split(",");
        if (paymentVouchers.length > 9) {
            throw new BusinessException("最多上传9张打款凭证");
        }

        // 查询订单
        OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        if (!InvoiceOrderStatusEnum.UNPAID.getValue().equals(orderEntity.getOrderStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(orderEntity.getUserId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        for (String paymentVoucher : paymentVouchers) {
            // 校验图片是否存在
            if (!ossService.doesObjectExistPrivate(paymentVoucher)) {
                log.error("【orderNo：" + orderNo + "】上传线下打款凭证【" + paymentVoucher + "】不存在");
                throw new BusinessException("图片不存在");
            }
        }

        // 查询开票订单
        InvoiceOrderEntity invoiceOrder = Optional.ofNullable(this.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到开票订单信息"));
        invoiceOrder.setPaymentVoucher(vouchers);
        this.editByIdSelective(invoiceOrder);

        // 添加开票订单变更记录
        InvoiceOrderChangeRecordEntity changeRecordEntity = new InvoiceOrderChangeRecordEntity();
        ObjectUtil.copyObject(invoiceOrder, changeRecordEntity);
        changeRecordEntity.setId(null);
        changeRecordEntity.setAddUser(member.getMemberAccount());
        changeRecordEntity.setAddTime(new Date());
        changeRecordEntity.setOrderStatus(InvoiceOrderStatusEnum.TO_PAYMENT_REVIEW.getValue());
        changeRecordEntity.setRemark("用户上传线下打款凭证");
        invoiceOrderChangeRecordService.insertSelective(changeRecordEntity);

        // 更新订单状态为“待财务审核”
        orderEntity.setOrderStatus(InvoiceOrderStatusEnum.TO_PAYMENT_REVIEW.getValue());
        orderService.editByIdSelective(orderEntity);
    }
}