package com.yuqian.itax.admin.controller.workOrder;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.CustomerWorker;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueRecordStatusEnum;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.enums.LevyWayEnum;
import com.yuqian.itax.park.enums.ParkProcessMarkEnum;
import com.yuqian.itax.park.service.ParkDisableWordService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.entity.vo.UnmatchedBusinessScopeVO;
import com.yuqian.itax.system.enums.IndustryStatusEnum;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.tax.entity.vo.TaxCalculationVO;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.dto.CompanyCorePersonnelDTO;
import com.yuqian.itax.user.entity.vo.CompanyCorePersonnelVO;
import com.yuqian.itax.user.enums.CustomerStatusEnum;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import com.yuqian.itax.user.enums.UserAccountTypeEnum;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringHandleUtil;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.workorder.entity.WorkOrderChangeRecordEntity;
import com.yuqian.itax.workorder.entity.WorkOrderDescEntity;
import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.workorder.entity.dto.WorkInvOrderDTO;
import com.yuqian.itax.workorder.entity.dto.WorkOrderDTO;
import com.yuqian.itax.workorder.entity.query.WordOrderDescQuery;
import com.yuqian.itax.workorder.entity.query.WorkOrderQuery;
import com.yuqian.itax.workorder.entity.vo.WorkOrderDescVO;
import com.yuqian.itax.workorder.entity.vo.WorkOrderListVO;
import com.yuqian.itax.workorder.entity.vo.WorkOrderVO;
import com.yuqian.itax.workorder.enums.WorkOrderStatusEnum;
import com.yuqian.itax.workorder.enums.WorkOrderTypeEnum;
import com.yuqian.itax.workorder.enums.WorkProcessorTypeEnum;
import com.yuqian.itax.workorder.service.WorkOrderChangeRecordService;
import com.yuqian.itax.workorder.service.WorkOrderDescService;
import com.yuqian.itax.workorder.service.WorkOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工单Controll
 * auth: HZ
 * time: 2019/12/09
 */
@RestController
@RequestMapping("/workOrder")
@Slf4j
public class WorkOrderController extends BaseController {
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private CustomerServiceWorkNumberService customerServiceWorkNumberService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private CityService cityService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Autowired
    private OssService ossService;
    @Autowired
    private OrderAttachmentService orderAttachmentService;
    @Autowired
    private WorkOrderChangeRecordService workOrderChangeRecordService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private InvoiceRecordService invoiceRecordService;
    @Autowired
    private CompanyTaxHostingService companyTaxHostingService;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private WorkOrderDescService workOrderDescService;
    @Autowired
    private OemAccessPartyService oemAccessPartyService;
    @Autowired
    private CouponsIssueRecordService couponsIssueRecordService;
    @Autowired
    private CouponsService couponsService;
    @Autowired
    CrowdLabelService crowdLabelService;
    @Autowired
    private ParkDisableWordService parkDisableWorkService;
    @Autowired
    private InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Autowired
    private RegisterOrderGoodsDetailRelaService registerOrderGoodsDetailRelaService;
    @Autowired
    private InvoiceOrderChangeRecordService invoiceOrderChangeRecordService;
    @Autowired
    private InvoiceServiceFeeDetailService invoiceServiceFeeDetailService;
    @Autowired
    private CompanyCorePersonnelService companyCorePersonnelService;
    @Autowired
    private ProductParkRelaService productParkRelaService;
    @Autowired
    private TaxPolicyService taxPolicyService;

    private static final String AUDIT_FAIL_REMARK = "存在一笔审核不通过订单造成后续部分订单税费和服务费计算错误，需重新提交";

    /**
     * 工单列表查询
     * auth: HZ
     * time: 2019/12/09
     */
    @PostMapping("/workOrderPageInfo")
    public ResultVo workOrderPageInfo(@RequestBody WorkOrderQuery workOrderQuery){
        //验证登陆
        getCurrUser();
        UserEntity userEntity = userService.findById(getCurrUser().getUserId());
        workOrderQuery.setOemCode(getRequestHeadParams("oemCode"));
        if (userEntity.getPlatformType() == 2){
            workOrderQuery.setOemCode(userEntity.getOemCode());
        }
        if(workOrderQuery.getType()!=null && workOrderQuery.getType()==1){
            workOrderQuery.setCustomerServiceId(getCurrUserId());
            workOrderQuery.setWorkOrderStatuss("0,1");
        }
        //分页查询
        PageInfo<WorkOrderListVO> memberList=workOrderService.queryWorkOrderPageInfo(workOrderQuery);
        return ResultVo.Success(memberList);
    }

    /**
     * 工单备注分页
     * @param wordOrderDescQuery
     * @return
     */
    @PostMapping("/workOrderDescPageInfo")
    public ResultVo workOrderDesc(@RequestBody WordOrderDescQuery wordOrderDescQuery){
        //验证登陆
        getCurrUser();
        if (StringUtil.isBlank(wordOrderDescQuery.getWorkOrderNo())){
            return ResultVo.Fail("工单id不能为空");
        }
        PageInfo<WorkOrderDescVO> page = workOrderDescService.queryByWorkOrderNoPageInfo(wordOrderDescQuery);
        return ResultVo.Success(page);
    }

    /**
     * 插入工单记录
     * @param orderNo
     * @param workOrderNo
     * @param descContent
     * @return
     */
    @PostMapping("/insertWorkDesc")
    public ResultVo insertWorkDesc(@JsonParam String orderNo,@JsonParam String workOrderNo,@JsonParam String descContent){
        //验证登陆
        getCurrUser();
        UserEntity userEntity = userService.findById(getCurrUser().getUserId());
        if (StringUtil .isBlank(workOrderNo)){
            return ResultVo.Fail("工单id不能为空");
        }
        if (StringUtil .isBlank(orderNo)){
            return ResultVo.Fail("订单id不能为空");
        }
        if (descContent.length() >100){
            return ResultVo.Fail("备注太长！");
        }
        WorkOrderDescEntity entity = new WorkOrderDescEntity();
        entity.setOrderNo(orderNo);
        entity.setWorkOrderNo(workOrderNo);
        entity.setDescContent(descContent);
        entity.setCustomerServiceAccount(userEntity.getUsername());
        entity.setCustomerServiceName(userEntity.getNickname());
        entity.setAddTime(new Date());
        entity.setAddUser(userEntity.getUsername());
        workOrderDescService.insertSelective(entity);
        return ResultVo.Success();
    }

    @ApiOperation("接单")
    @PostMapping("accept")
    public ResultVo accept(@JsonParam Long id){
        //校验客服状态
        CustomerServiceWorkNumberEntity serEntity;
        String lockKey = RedisKey.ADMIN_WORK_ORDER_ACCEPT_REDIS_KEY;
        String registRedisTime = (System.currentTimeMillis() + 300000) + "";
        try {
            WorkOrderEntity entity = workOrderService.findById(id);
            if (entity == null) {
                return ResultVo.Fail("工单不存在");
            }
            if (!Objects.equals(entity.getWorkOrderStatus(), WorkOrderStatusEnum.WAITING_FOR_ORDERS.getValue())) {
                return ResultVo.Fail("工单状态不正确");
            }
            OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            serEntity = ValidCustomer(orderEntity.getOemCode());
            Integer status;
            Object obj;
            if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())
                    || Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.WATER.getValue())
                        ||Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())) {
                InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
                invEntity.setOrderNo(entity.getOrderNo());
                invEntity.setOemCode(entity.getOemCode());
                invEntity = invoiceOrderService.selectOne(invEntity);
                if (invEntity == null) {
                    return ResultVo.Fail("开票审核记录不存在");
                }
                if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())) {
                    entity.setWorkOrderDesc("开票工单接单");
                    if (!Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.UNCHECKED.getValue())) {
                        return ResultVo.Fail("订单状态不正确");
                    }
                    // 添加开票工单审核时间校验  个体户在此之前还有工单未审核则不允许接单
                    List<String> existPendingWorkOrder = invoiceOrderService.findExistPendingInvOrder(invEntity.getCompanyId(), null, invEntity.getAddTime(), null, invEntity.getOrderNo());
                    if (null != existPendingWorkOrder && !existPendingWorkOrder.isEmpty()) {
                        throw new BusinessException("请先处理该企业时间更早的开票工单！");
                    }
                } else if(Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())) {
                    entity.setWorkOrderDesc("开票成果工单接单");
                    if (!Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.SIGNED.getValue())) {
                        return ResultVo.Fail("订单状态不正确");
                    }
                    if (!Objects.equals(invEntity.getAchievementStatus(), BankAchievementStatusEnum.TO_BE_AUDIT.getValue())) {
                        return ResultVo.Fail("开票订单成果审核状态不正确");
                    }
                }  else {
                    entity.setWorkOrderDesc("开票流水工单接单");
                    if (!Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.SIGNED.getValue())) {
                        return ResultVo.Fail("订单状态不正确");
                    }
                    if (!Objects.equals(invEntity.getIsAfterUploadBankWater(), 0)) {
                        return ResultVo.Fail("开票订单不是后补流水");
                    }
                    if (!Objects.equals(invEntity.getBankWaterStatus(), BankWaterStatusEnum.TO_BE_AUDIT.getValue())) {
                        return ResultVo.Fail("开票订单流水审核状态不正确");
                    }
                }
                obj = invEntity;
                status = WorkOrderStatusEnum.AUDITING.getValue();
            } else{
                entity.setWorkOrderDesc("开户工单接单");
                if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.TO_BE_SURE.getValue())) {
                    return ResultVo.Fail("订单状态不正确");
                }
                //办理核名
                RegisterOrderEntity regEntity = new RegisterOrderEntity();
                regEntity.setOrderNo(entity.getOrderNo());
                regEntity.setOemCode(entity.getOemCode());
                regEntity = registerOrderService.selectOne(regEntity);
                if (regEntity == null) {
                    return ResultVo.Fail("办理核名记录不存在");
                }
                if (orderEntity.getIsSelfPaying() != null && orderEntity.getIsSelfPaying() == 2){
                    if(StringUtil.isEmpty(regEntity.getPaymentVoucher())){
                        return ResultVo.Fail("该订单第三方还未支付");
                    }
                }
                obj = regEntity;
                status = WorkOrderStatusEnum.AUDITING.getValue();
            }
            //校验工单归属客服信息
            validBelongCustomer(serEntity, entity.getCustomerServiceId());
            //更新工单状态
            entity.setProcessorType(WorkProcessorTypeEnum.WORK_NUMBER.getValue());
            lockKey += id;
            boolean lockResult = redisService.lock(lockKey, registRedisTime, 60);
            if(!lockResult){
                return ResultVo.Fail("当前工单处理中，请稍后再试");
            }
            workOrderService.updateStatus(entity, serEntity, status, null, obj);
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } finally {
            redisService.unlock(lockKey, registRedisTime);
        }
    }


    @ApiOperation("详情")
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long id){
        if (id == null) {
            return ResultVo.Fail("请求参数有误");
        }
        //校验客服登录状态
        getCurrUser();
//        getCustomerWorker();
        HashMap<String, Object> map = Maps.newHashMap();
        WorkOrderEntity entity = workOrderService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("工单不存在");
        }
        WorkOrderVO vo = new WorkOrderVO(entity);
        OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }
//        MemberOrderRelaEntity relaEntity = memberOrderRelaService.findById(orderEntity.getRelaId());
//        if (relaEntity == null) {
//            return ResultVo.Fail("订单关系表不存在");
//        }
        MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
        if (accEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (accEntity.getAccessPartyId() != null){
            OemAccessPartyEntity accessEntity = oemAccessPartyService.findById(accEntity.getAccessPartyId());
           vo.setAccessPartyName(accessEntity.getAccessPartyName());
        }

        vo.setParkId(orderEntity.getParkId());
        vo.setPhone(accEntity.getMemberPhone());
        vo.setUserName(accEntity.getRealName());
        vo.setProvinceName(Optional.ofNullable(provinceService.getByCode(accEntity.getProvinceCode())).map(ProvinceEntity::getName).orElse(null));
        vo.setCityName(Optional.ofNullable(cityService.getByCode(accEntity.getCityCode())).map(CityEntity::getName).orElse(null));
        if(orderEntity.getCrowdLabelId()!=null){
            vo.setCrowdLabelName(crowdLabelService.findById(orderEntity.getCrowdLabelId()).getCrowdLabelName());
        }
        if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())) {
            //开票
            InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
            invEntity.setOrderNo(entity.getOrderNo());
            invEntity.setOemCode(entity.getOemCode());
            invEntity = invoiceOrderService.selectOne(invEntity);
            if (invEntity == null) {
                return ResultVo.Fail("开票审核记录不存在");
            }
            //获取公司信息
            MemberCompanyEntity company = memberCompanyService.findById(invEntity.getCompanyId());
            if (company == null) {
                return ResultVo.Fail("开票审核记录公司信息不存在");
            }
            vo.setCompanyType(company.getCompanyType());
            InvoiceDetailOrderVO invOrder = new InvoiceDetailOrderVO(invEntity, company);
            if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.WATER.getValue())) {
                invEntity.setAccountStatement(entity.getAccountStatement());
                invOrder.setAccountStatement(invEntity.getAccountStatement());
            }
            //业务合同
            invOrder.setBusinessContractImgList(getOssImages(invEntity.getBusinessContractImgs()));
            //银行流水
            invOrder.setAccountStatementList(getOssImages(invEntity.getAccountStatement()));
            //成果照片
            invOrder.setAchievementImgList(getOssImages(invEntity.getAchievementImgs()));
            invOrder.setAchievementImgs(invEntity.getAchievementImgs());
            //成果视频
            invOrder.setAchievementVideoLeng(ossService.getPrivateImgUrl(invEntity.getAchievementVideo()));
            invOrder.setAchievementVideo(invEntity.getAchievementVideo());
            // 开票类目
            invOrder.setCategoryName(invEntity.getCategoryName());
            // 成果状态
            invOrder.setAchievementStatus(invEntity.getAchievementStatus());
            // 营业执照
            invOrder.setBusinessLicense(getOssImages(company.getBusinessLicense()).get(0));
            // 是否需要审核不通过风险提示 0-否 1-是
            BigDecimal serviceFeeRate = invoiceServiceFeeDetailService.findLeastServiceFeeRate(invEntity.getOrderNo());
            if (null != serviceFeeRate && !BigDecimal.ZERO.equals(serviceFeeRate)) {
                List<String> existPendingWorkOrder = invoiceOrderService.findExistPendingInvOrder(company.getId(), null, null, serviceFeeRate, orderEntity.getOrderNo());
                invOrder.setIsRiskHint(null == existPendingWorkOrder || existPendingWorkOrder.isEmpty() ? 0 : 1);
            }
            map.put("invOrder", invOrder);
            InvoicePayInfo invoicePayInfo = invoiceOrderService.queryInvoicePayInfo(invEntity.getOrderNo());
            map.put("invoicePayInfo",invoicePayInfo);
            //是否已托管
            CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingService.getCompanyTaxHostingByCompanyId(invEntity.getCompanyId(),1);
            if(companyTaxHostingEntity!=null && companyTaxHostingEntity.getStatus().intValue() == 1){
                map.put("hostingStatus", 1);
            }else{
                map.put("hostingStatus", 0);
            }

            // 如果为作废/重开订单则没有待补传流水成果
            if (!InvoiceMarkEnum.CANCELLATION.getValue().equals(invOrder.getInvoiceMark())){
                //获取开票流水
                getWaterOrder(orderEntity.getUserId(), entity.getWorkOrderType(), map);
                //  获取补传成果
                getAchievementOrder(orderEntity.getUserId(), entity.getWorkOrderType(), map);
            }
            // 如果是作废/重开订单，去取原订单的作废/红冲说明
            if (invOrder.getInvoiceMark().equals(InvoiceMarkEnum.REOPEN.getValue())){
                InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(invOrder.getRelevanceOrderNo());
                invOrder.setCancellationRemark(invoiceOrderEntity.getCancellationRemark());
            }
            // 获取本月第一天
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date firstDay=cal.getTime();
            //最后一天
            cal=Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date lastDay=cal.getTime();
            List<CountPeriodInvoiceAmountVO> invoiceAmout = invoiceOrderService.queryCompanyInvoiceAmountByEin(invOrder.getInvCompanyId(),1,firstDay,lastDay,null,1,0);
            Long MonthInvoiceAmount = 0L;
            for (CountPeriodInvoiceAmountVO invoiceAmountVO:invoiceAmout){
                MonthInvoiceAmount += invoiceAmountVO.getCountAmountInvoiced();
            }
            invOrder.setMonthInvoiceAmount(MonthInvoiceAmount);
            InvoiceCategoryBaseEntity invoiceCategoryBaseEntity = invoiceCategoryBaseService.findById(invEntity.getCategoryId());
            if (invoiceCategoryBaseEntity!= null && StringUtil.isNotBlank(invoiceCategoryBaseEntity.getGoodsName()) && invoiceCategoryBaseEntity.getGoodsName().equals("*")){
                //  用户自定义开票类目
                invOrder.setCategoryType(2);
            }else{
                // 系统开票类目
                invOrder.setCategoryType(1);
            }
        } else if( Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.WATER.getValue())){
            InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
            invEntity.setOrderNo(entity.getOrderNo());
            invEntity.setOemCode(entity.getOemCode());
            invEntity = invoiceOrderService.selectOne(invEntity);
            if (invEntity == null) {
                return ResultVo.Fail("开票审核记录不存在");
            }
            //获取公司信息
            MemberCompanyEntity company = memberCompanyService.findById(invEntity.getCompanyId());
            if (company == null) {
                return ResultVo.Fail("开票审核记录公司信息不存在");
            }
            vo.setCompanyType(company.getCompanyType());
            InvoiceDetailOrderVO invOrder = new InvoiceDetailOrderVO(invEntity, company);
            invEntity.setAccountStatement(entity.getAccountStatement());
            invOrder.setAccountStatement(invEntity.getAccountStatement());
            //业务合同
            invOrder.setBusinessContractImgList(getOssImages(invEntity.getBusinessContractImgs()));
            //银行流水
            invOrder.setAccountStatementList(getOssImages(invEntity.getAccountStatement()));
            //成果照片
            invOrder.setAchievementImgList(getOssImages(invEntity.getAchievementImgs()));
            invOrder.setAchievementImgs(invEntity.getAchievementImgs());
            //成果视频
            invOrder.setAchievementVideoLeng(ossService.getPrivateImgUrl(invEntity.getAchievementVideo()));
            invOrder.setAchievementVideo(invEntity.getAchievementVideo());
            // 营业执照
            invOrder.setBusinessLicense(getOssImages(company.getBusinessLicense()).get(0));
            map.put("invOrder", invOrder);
        }else if( Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())){
            InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
            invEntity.setOrderNo(entity.getOrderNo());
            invEntity.setOemCode(entity.getOemCode());
            invEntity = invoiceOrderService.selectOne(invEntity);
            if (invEntity == null) {
                return ResultVo.Fail("开票审核记录不存在");
            }
            //获取公司信息
            MemberCompanyEntity company = memberCompanyService.findById(invEntity.getCompanyId());
            if (company == null) {
                return ResultVo.Fail("开票审核记录公司信息不存在");
            }
            vo.setCompanyType(company.getCompanyType());
            //开票基础信息
            InvoiceDetailOrderVO invOrder = new InvoiceDetailOrderVO(invEntity, company);
            //业务合同
            invOrder.setBusinessContractImgList(getOssImages(invEntity.getBusinessContractImgs()));
            //成果照片
            invOrder.setAchievementImgList(getOssImages(entity.getAchievementImgs()));
            invOrder.setAchievementImgs(entity.getAchievementImgs());
            //成果视频
            invOrder.setAchievementVideoLeng(ossService.getPrivateImgUrl(entity.getAchievementVideo()));
            invOrder.setAchievementVideo(entity.getAchievementVideo());
            // 营业执照
            invOrder.setBusinessLicense(getOssImages(company.getBusinessLicense()).get(0));
            map.put("invOrder", invOrder);
        } else {
            //办理核名
            RegisterOrderEntity regEntity = new RegisterOrderEntity();
            regEntity.setOrderNo(entity.getOrderNo());
            regEntity.setOemCode(entity.getOemCode());
            regEntity = registerOrderService.selectOne(regEntity);
            if (regEntity == null) {
                return ResultVo.Fail("办理核名记录不存在");
            }
            vo.setCompanyType(regEntity.getCompanyType());
            RegisterDetailOrderVO regOrder = new RegisterDetailOrderVO(regEntity);
            regOrder.setOrderStatus(orderEntity.getOrderStatus());
            if (accEntity.getAccessPartyId() != null){
                regOrder.setAccessPartyId(accEntity.getAccessPartyId());
            }
            //获取企业核心成员数据
            List<CompanyCorePersonnelVO>  list =  companyCorePersonnelService.getCompanyCorePersonnelByCompanyIdOrOrderNo(null,entity.getOrderNo());
            regOrder.setShareholderInfoList(list);
            //赋码后新增经营范围
            List<String>  codeBusinessScope = registerOrderGoodsDetailRelaService.getAddedBusinessScope(entity.getOrderNo(),regEntity.getAddTime());
            String str = String.join(";", codeBusinessScope);
            regOrder.setCodeBusinessScope(str);
            //获取行业类型
            Optional<IndustryEntity> industryEntity = Optional.ofNullable(industryService.findById(regEntity.getIndustryId()));
            regOrder.setIndustryName(industryEntity.map(IndustryEntity::getIndustryName).orElse(null));
            if (StringUtils.isBlank(regOrder.getExampleName())) {
                regOrder.setExampleName(StringHandleUtil.removeStar(industryEntity.map(IndustryEntity::getExampleName).orElse(null)));
            }
            //获取园区名字
            Optional<ParkEntity> parkEntity = Optional.ofNullable(parkService.findById(orderEntity.getParkId()));
            regOrder.setParkName(parkEntity.map(ParkEntity::getParkName).orElse(null));
            regOrder.setParkCity(parkEntity.map(ParkEntity::getParkCity).orElse(null));
            regOrder.setParkCode(parkEntity.map(ParkEntity::getParkCode).orElse(null));
            //获取注册流程标记
            ProductParkRelaEntity productParkRelaEntity = new ProductParkRelaEntity();
            productParkRelaEntity.setProductId(orderEntity.getProductId());
            productParkRelaEntity.setParkId(orderEntity.getParkId());
            productParkRelaEntity.setCompanyType(regEntity.getCompanyType());
            productParkRelaEntity = productParkRelaService.selectOne(productParkRelaEntity);
            if(productParkRelaEntity==null){
                return ResultVo.Fail("当前园区不支持注册办理");
            }
            regOrder.setProcessMark(productParkRelaEntity.getProcessMark());
            //开票类目
            regOrder.setCategoryNames(companyInvoiceCategoryService.getCategoryNames(entity.getOrderNo(), entity.getOemCode(), regEntity.getIndustryId()));
            regOrder.setIdCardFrontFileName(regOrder.getIdCardFront());
            regOrder.setIdCardReverseFileName(regOrder.getIdCardReverse());
            regOrder.setIdCardFront(ossService.getPrivateImgUrl(regOrder.getIdCardFront()));
            regOrder.setIdCardReverse(ossService.getPrivateImgUrl(regOrder.getIdCardReverse()));

            regOrder.setSignImg(ossService.getPrivateImgUrl(regOrder.getSignImg()));
            regOrder.setVideoAddr(ossService.getPrivateVideoUrl(regOrder.getVideoAddr()));
            map.put("regOrder", regOrder);
            //订单附件表
            OrderAttachmentEntity attEntity = new OrderAttachmentEntity();
            attEntity.setOrderNo(entity.getOrderNo());
            attEntity.setOrderType(2);
            attEntity.setOemCode(entity.getOemCode());
            List<OrderAttachmentEntity> attachmentEntities = orderAttachmentService.select(attEntity);
            List<Map<String, Object>> attachments = Lists.newArrayList();
            Map<String, Object> attachmentMap;
            if (CollectionUtil.isNotEmpty(attachmentEntities)) {
                for (OrderAttachmentEntity attachment : attachmentEntities) {
                    attachmentMap = Maps.newHashMap();
                    if (StringUtils.isBlank(attachment.getAttachmentAddr())) {
                        continue;
                    }
                    if (attachment.getAttachmentAddr().endsWith("mp4")) {
                        attachmentMap.put("url", ossService.getPrivateVideoUrl(attachment.getAttachmentAddr()));
                        attachmentMap.put("type", 2);
                    } else {
                        attachmentMap.put("url", ossService.getPrivateImgUrl(attachment.getAttachmentAddr()));
                        attachmentMap.put("type", 1);
                    }
                    attachments.add(attachmentMap);
                }
            }
            map.put("attachment", attachments);
            //获取支付信息
            InvPayInfoVo invPayInfoVo = registerOrderService.queryPayInfoByOrderNo(entity.getOrderNo());
            map.put("invPayInfo", invPayInfoVo);
        }
        map.put("workOrder", vo);
        //工单操作历史记录
        WorkOrderChangeRecordEntity recordEntity = new WorkOrderChangeRecordEntity();
        recordEntity.setWorkOrderNo(entity.getWorkOrderNo());
        recordEntity.setWorkOrderType(entity.getWorkOrderType());
        List<WorkOrderChangeRecordEntity> records = workOrderChangeRecordService.select(recordEntity);
        map.put("history", records);
        return ResultVo.Success(map);
    }

    /**
     * 获取oss上图片集合
     * @param url
     * @return
     */
    private List<String> getOssImages(String url) {
        List<String> list = Lists.newArrayList();
        if (StringUtils.isBlank(url)) {
            return list;
        }
        //oss获取银行流水
        String[] split = url.split(",");
        for (String s : split) {
            list.add(ossService.getPrivateImgUrl(s));
        }
        return list;
    }

    /**
     * 获取补传成果订单
     * @param memberId
     * @param workOrderType
     * @param map
     */
    private void getAchievementOrder(Long memberId, Integer workOrderType, Map<String, Object> map){
        if (!Objects.equals(workOrderType, WorkOrderTypeEnum.INVOICE.getValue())) {
            return;
        }
        DictionaryEntity dictionaryEntity = dictionaryService.getByCode("achievement_timeout");
        String timeOut = Optional.ofNullable(dictionaryEntity).map(DictionaryEntity::getDictValue).orElse("30");
        HashMap<String, Object> queryMap = Maps.newHashMap();
        queryMap.put("memberId", memberId);
        queryMap.put("timeOut", timeOut);
        queryMap.put("orderStatus", InvoiceOrderStatusEnum.SIGNED.getValue());
        List<InvoiceAchievementOrderInfoVO> list = invoiceOrderService.getAchievementOrder(queryMap);
        map.put("achievementOrder", list);
    }

    /**
     * 获取待补传流水订单
     * @param memberId
     * @param workOrderType
     * @param map
     */
    private void getWaterOrder(Long memberId, Integer workOrderType, Map<String, Object> map) {
        if (!Objects.equals(workOrderType, WorkOrderTypeEnum.INVOICE.getValue())) {
            return;
        }
        DictionaryEntity dictionaryEntity = dictionaryService.getByCode("upload_invoice_imgs_timeout");
        String timeOut = Optional.ofNullable(dictionaryEntity).map(DictionaryEntity::getDictValue).orElse("30");
        HashMap<String, Object> queryMap = Maps.newHashMap();
        queryMap.put("memberId", memberId);
        queryMap.put("timeOut", timeOut);
        queryMap.put("orderStatus", InvoiceOrderStatusEnum.SIGNED.getValue());
        queryMap.put("notBankWaterStatus", BankWaterStatusEnum.APPROVED.getValue());
        queryMap.put("isAfterUploadBankWater", 0);
        List<InvoiceWaterOrderVO> list = invoiceOrderService.getWaterOrder(queryMap);
        list.forEach(vo ->{
            if(StringUtils.isNotBlank(vo.getRiskCommitment())){
                vo.setRiskCommitmentList(getOssImages(vo.getRiskCommitment()));
            }
        });
        map.put("waterOrder", list);
    }

    /**
     * 校验登录信息
     * @return
     * @throws BusinessException
     * @param oemCode
     */
    private CustomerServiceWorkNumberEntity ValidCustomer(String oemCode) throws BusinessException {
        CustomerWorker customerWorker = getCustomerWorker();
        CustomerServiceWorkNumberEntity serEntity = customerServiceWorkNumberService.findById(customerWorker.getUserId());
        if (serEntity == null) {
            throw new BusinessException("当前登录客服不存在");
        }
        if (!Objects.equals(serEntity.getStatus(), CustomerStatusEnum.NORMAL.getValue())){
            throw new BusinessException("当前登录客服已被禁用");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(serEntity.getUserId());
        userEntity = userService.selectOne(userEntity);
        if (userEntity == null) {
            throw new BusinessException("坐席客服记录不存在");
        }
        if (!Objects.equals(userEntity.getAccountType(), UserAccountTypeEnum.SERVER.getValue())){
            throw new BusinessException("当前登录客服不归属于坐席客服");
        }
        if (!Objects.equals(userEntity.getStatus(), CustomerStatusEnum.NORMAL.getValue())){
            throw new BusinessException("当前坐席已被禁用");
        }
       /* if (StringUtils.isNotBlank(userEntity.getOemCode())) {
            throw new BusinessException("非平台坐席客服不允许操作工单");
        }*/
        return serEntity;
    }

    /**
     * 校验归属客服信息信息
     * @param serEntity
     * @throws BusinessException
     */
    private void validBelongCustomer(CustomerServiceWorkNumberEntity serEntity, Long id) throws BusinessException {
        if (id==null) {
            return;
        }
        UserEntity userEntity = userService.findById(id);
        if (null == userEntity) {
            throw new BusinessException("工单归属客服坐席账号不存在");
        }
        if (!Objects.equals(userEntity.getId(), serEntity.getUserId())) {
            throw new BusinessException("当前操作的工单不属于当前登录客服的坐席");
        }
    }

    @ApiOperation("审核")
    @PostMapping("audit")
    public ResultVo audit(@RequestBody @Validated WorkOrderDTO dto, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        Map<String, Object> map = Maps.newHashMap();
        //校验客服状态
        CustomerServiceWorkNumberEntity serEntity;
        try {
            WorkOrderEntity entity = workOrderService.findById(dto.getId());
            if (entity == null) {
                return ResultVo.Fail("工单不存在");
            }
            if (!Objects.equals(entity.getWorkOrderStatus(), WorkOrderStatusEnum.AUDITING.getValue())) {
                return ResultVo.Fail("工单状态不正确");
            }
            serEntity = ValidCustomer(entity.getOemCode());
            //校验工单归属客服信息
            validBelongCustomer(serEntity, entity.getCustomerServiceId());
            OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
            if (accEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())
                || Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.WATER.getValue())
                    || Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())) {
                if (Objects.equals(dto.getStatus(), 3)) {
                    return ResultVo.Fail("操作状态有误");
                }
                //开票
                InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
                invEntity.setOrderNo(entity.getOrderNo());
                invEntity.setOemCode(entity.getOemCode());
                invEntity = invoiceOrderService.selectOne(invEntity);
                if (invEntity == null) {
                    return ResultVo.Fail("开票审核记录不存在");
                }
                MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(invEntity.getCompanyId());
                if(StringUtils.isBlank(memberCompanyEntity.getEin())){
                    return ResultVo.Fail("该企业税号为空，请联系运营添加税号后再审核！");
                }
                if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())) {
                    entity.setWorkOrderDesc("开票工单审核");
                    if (!Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.UNCHECKED.getValue())) {
                        return ResultVo.Fail("订单状态不正确");
                    }
                    // 查询企业托管信息
                    CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingService.getCompanyTaxHostingByCompanyId(memberCompanyEntity.getId(),2);
//                    if (dto.getIsImmediatelyInvoiceStatus() == null && (null != companyTaxHostingEntity && Objects.equals(companyTaxHostingEntity.getStatus(),0))) {
//                        return ResultVo.Fail("是否立即开票不能为空！");
//                    }
                    // 查询企业是否有早于该笔开票订单创建时间的待审核订单
                    List<String> existPendingWorkOrder = invoiceOrderService.findExistPendingInvOrder(invEntity.getCompanyId(), null, invEntity.getAddTime(), null, invEntity.getOrderNo());
                    if (null != existPendingWorkOrder && !existPendingWorkOrder.isEmpty()) {
                        throw new BusinessException("请先处理该企业时间更早的开票工单！");
                    }
                    if (Objects.equals(dto.getStatus(), 1)) {
                        if (invEntity.getCategoryName().startsWith("*")) {
                            //如果开票类目是以*开头，那么直接替换*
                            invEntity.setCategoryName(invEntity.getCategoryName().replaceFirst("\\*", invEntity.getGoodsName()));
                        }
                        entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_SUCCESS.getValue());
                        dto.setOrderStatus(InvoiceOrderStatusEnum.IN_TICKETING.getValue());
                        invEntity.setRemark("开票工单审核通过");
                        //更新工单信息
                        workOrderService.updateInvOrderStatus(entity, invEntity, orderEntity, dto, serEntity, accEntity, true);
                        //生成开票记录 ni.jiang
                        boolean isImmediatelyInvoice = false;
//                        if(null != dto.getIsImmediatelyInvoiceStatus() && dto.getIsImmediatelyInvoiceStatus().intValue() == 1){
//                            isImmediatelyInvoice = true;
//                        }
                        invoiceRecordService.createInvoiceRecord(invEntity,companyTaxHostingEntity,memberCompanyEntity.getEin(),orderEntity.getParkId(),orderEntity.getUserId(),serEntity.getWorkNumber(),isImmediatelyInvoice);
                        return ResultVo.Success();
                    } else {
                        if (StringUtils.isBlank(dto.getRemark())) {
                            return ResultVo.Fail("备注必填，请填写后再提交！");
                        }
                        //非个体校验是否存在后创建的待财务审核订单数据
                        if(memberCompanyEntity.getCompanyType()!=1){
                            Integer count = invoiceOrderService.checkInvoicePaymentReview(memberCompanyEntity.getId(),invEntity.getUpdateTime(),2,invEntity.getOrderNo());
                            if(count != null && count > 0){
                                return ResultVo.Fail("为了避免服务费计算错误，请审核不通过企业后续创建的待财务审核开票订单！");
                            }
                        }
                        entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_FAILED.getValue());
                        dto.setOrderStatus(InvoiceOrderStatusEnum.AUDIT_FAILED.getValue());
                        invEntity.setRemark("开票工单审核不通过");

                        // 查询是否有其他待付款的的自助开票订单
                        List<OrderNoVO> unpaidList = invoiceOrderService.getUnpaidList(orderEntity.getUserId(), orderEntity.getOemCode(), invEntity.getCompanyId());
                        if (null != unpaidList && !unpaidList.isEmpty()) {
                            for (int i = 0; i < unpaidList.size(); i++) {
                                if (!Objects.equals(unpaidList.get(i).getInvoiceMark(), 0)) {
                                    // 跳过作废标识非正常状态的代付款订单
                                    continue;
                                }
                                OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(unpaidList.get(i).getOrderNo())).orElseThrow(() -> new BusinessException("未查询到未付款订单信息"));
                                InvoiceOrderEntity invoiceOrderEntity = Optional.ofNullable(invoiceOrderService.queryByOrderNo(unpaidList.get(i).getOrderNo())).orElseThrow(() -> new BusinessException("未查询到未付款开票订单信息"));
                                invoiceOrderEntity.setIsRecalculateServiceFee(0);
                                invoiceOrderEntity.setUpdateTime(new Date());
                                invoiceOrderEntity.setUpdateUser(serEntity.getWorkNumber());
                                invoiceOrderService.editByIdSelective(invoiceOrderEntity);
                                InvoiceOrderChangeRecordEntity invoiceOrderChangeRecordEntity = new InvoiceOrderChangeRecordEntity();
                                ObjectUtil.copyObject(invoiceOrderEntity, invoiceOrderChangeRecordEntity);
                                invoiceOrderChangeRecordEntity.setId(null);
                                invoiceOrderChangeRecordEntity.setOrderStatus(order.getOrderStatus());
                                invoiceOrderChangeRecordEntity.setAddTime(new Date());
                                invoiceOrderChangeRecordEntity.setAddUser(serEntity.getWorkNumber());
                                invoiceOrderChangeRecordEntity.setUpdateUser(null);
                                invoiceOrderChangeRecordEntity.setUpdateTime(null);
                                invoiceOrderChangeRecordEntity.setRemark("添加费用重算标识");
                                invoiceOrderChangeRecordService.insertSelective(invoiceOrderChangeRecordEntity);
                            }
                        }
                        // 对后续未审核的已支付订单进行处理
                        if (!InvoiceMarkEnum.REOPEN.getValue().equals(invEntity.getInvoiceMark())) {
                            // 查询该笔订单开票的最小服务费费率
                            BigDecimal serviceFeeRate = invoiceServiceFeeDetailService.findLeastServiceFeeRate(invEntity.getOrderNo());
                            if (null != serviceFeeRate && !BigDecimal.ZERO.equals(serviceFeeRate)) {
                                // 校验是否有其他待审核订单
                                List<String> existPendingOrder = invoiceOrderService.findExistPendingInvOrder(invEntity.getCompanyId(), null, null, serviceFeeRate, invEntity.getOrderNo());
                                if (null != existPendingOrder && !existPendingOrder.isEmpty()) {
                                    String remark = dto.getRemark();
                                    for (String orderNo : existPendingOrder) {
                                        // 查询工单
                                        WorkOrderEntity workOrderEntity = new WorkOrderEntity();
                                        workOrderEntity.setOrderNo(orderNo);
                                        workOrderEntity.setOemCode(orderEntity.getOemCode());
                                        workOrderEntity.setWorkOrderType(WorkOrderTypeEnum.INVOICE.getValue());
                                        workOrderEntity = workOrderService.selectOne(workOrderEntity);
                                        if (null == workOrderEntity) {
                                            throw new BusinessException("未查询到其他待审订单工单信息");
                                        }
                                        workOrderEntity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_FAILED.getValue());
                                        // 查询开票订单
                                        InvoiceOrderEntity invoiceOrderEntity = Optional.ofNullable(invoiceOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到其他待审核开票订单信息"));
                                        invoiceOrderEntity.setRemark("开票工单审核不通过");
                                        // 审核不通过原因
                                        String reason = AUDIT_FAIL_REMARK;
                                        DictionaryEntity dictionaryEntity = dictionaryService.getByCode("audit_fail_remark");
                                        if (null != dictionaryEntity && StringUtil.isNotBlank(dictionaryEntity.getDictValue())) {
                                            reason = dictionaryEntity.getDictValue();
                                        }
                                        dto.setRemark(reason);
                                        // 查询订单
                                        OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到其他待审核订单信息"));
                                        // 审核不通过操作
                                        workOrderService.updateInvOrderStatus(workOrderEntity, invoiceOrderEntity, order, dto, serEntity, accEntity, false);
                                    }
                                    dto.setRemark(remark);
                                }
                            }
                        }
                    }
                } else  if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())) {
                    entity.setWorkOrderDesc("开票成果工单审核");
                    if (!Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.SIGNED.getValue())) {
                        return ResultVo.Fail("订单状态不正确");
                    }
                    if (!Objects.equals(invEntity.getAchievementStatus(), BankAchievementStatusEnum.TO_BE_AUDIT.getValue())) {
                        return ResultVo.Fail("开票订单成果审核状态不正确");
                    }
                    if (Objects.equals(dto.getStatus(), 1)) {
                        entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_SUCCESS.getValue());
                        invEntity.setAchievementStatus(BankAchievementStatusEnum.APPROVED.getValue());
                        invEntity.setAchievementImgs(entity.getAchievementImgs());
                        invEntity.setAchievementVideo(entity.getAchievementVideo());
                        invEntity.setRemark("成果流水审核通过");
                    } else {
                        if (StringUtils.isBlank(dto.getRemark())) {
                            return ResultVo.Fail("备注必填，请填写后再提交！");
                        }
                        entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_FAILED.getValue());
                        invEntity.setAchievementStatus(BankAchievementStatusEnum.NOT_APPROVED.getValue());
                        invEntity.setAchievementErrorRemark(dto.getRemark());
                        invEntity.setRemark("成果流水审核不通过");
                    }
                } else {
                    entity.setWorkOrderDesc("开票流水工单审核");
                    if (!Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.SIGNED.getValue())) {
                        return ResultVo.Fail("订单状态不正确");
                    }
                    if (!Objects.equals(invEntity.getIsAfterUploadBankWater(), 0)) {
                        return ResultVo.Fail("开票订单不是后补流水");
                    }
                    if (!Objects.equals(invEntity.getBankWaterStatus(), BankWaterStatusEnum.TO_BE_AUDIT.getValue())) {
                        return ResultVo.Fail("开票订单流水审核状态不正确");
                    }
                    if (Objects.equals(dto.getStatus(), 1)) {
                        entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_SUCCESS.getValue());
                        invEntity.setBankWaterStatus(BankWaterStatusEnum.APPROVED.getValue());
                        invEntity.setAccountStatement(entity.getAccountStatement());
                        invEntity.setRemark("开票流水审核通过");
                    } else {
                        if (StringUtils.isBlank(dto.getRemark())) {
                            return ResultVo.Fail("备注必填，请填写后再提交！");
                        }
                        entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_FAILED.getValue());
                        invEntity.setBankWaterStatus(BankWaterStatusEnum.NOT_APPROVED.getValue());
                        invEntity.setAuditErrorRemark(dto.getRemark());
                        invEntity.setRemark("开票流水审核不通过");
                    }
                }
                //更新工单信息
                workOrderService.updateInvOrderStatus(entity, invEntity, orderEntity, dto, serEntity, accEntity, true);
            } else {
                RegisterOrderEntity regEntity = new RegisterOrderEntity();
                regEntity.setOrderNo(entity.getOrderNo());
                regEntity.setOemCode(entity.getOemCode());
                regEntity = registerOrderService.selectOne(regEntity);
                if (regEntity == null) {
                    return ResultVo.Fail("办理核名记录不存在");
                }

                //办理核名
                entity.setWorkOrderDesc("开户工单审核");
                validateRegDTO(dto, entity.getOemCode());
                // 如果为通过 校验字号是否禁用
                if (dto.getStatus() == 1){
                    if(StringUtil.isBlank(dto.getRegisteredName())){
                        return ResultVo.Fail("企业确认名称不能为空");
                    }

                    //禁用字去除前缀和后缀
                    ParkEntity parkEntity = parkService.findById(orderEntity.getParkId());
                    if(parkEntity==null){
                        return ResultVo.Fail("未找到园区信息");
                    }
                    String regName = dto.getRegisteredName().replace(parkEntity.getParkCity(),"");
                    String exampleName = regEntity.getExampleName();
                    if(StringUtil.isBlank(exampleName)){
                        exampleName = "";
                    }
                    exampleName = exampleName.replace("***","");
                    regName = regName.replace(exampleName,"");
                    parkDisableWorkService.checkoutDisableWord(orderEntity.getParkId(),regName,null,null);
                }
                if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.TO_BE_SURE.getValue())) {
                    return ResultVo.Fail("订单状态不正确");
                }
                if (Objects.equals(dto.getStatus(), 3) && StringUtils.isBlank(dto.getRejectedItem())) {
                    return ResultVo.Fail("驳回项不能为空");
                }

                // 接入方费用承担方为企业时需校验注册订单支付凭证
                if (null != accEntity.getAccessPartyId() && Objects.equals(orderEntity.getIsSelfPaying(),2)
                        && StringUtil.isBlank(regEntity.getPaymentVoucher())) {
                    return ResultVo.Fail("注册订单支付凭证为空");
                }
                if (Objects.equals(dto.getStatus(), 1)) {
                    if (regEntity.getIsAllCodes().equals(0)){
                        return ResultVo.Fail("注册订单未全部赋码");
                    }
                    //校验身份验证状态
                    Integer orderStatus = validOpenAuthentication(orderEntity.getParkId(), regEntity.getIsOpenAuthentication(), orderEntity.getProductId(), regEntity.getCompanyType());
                    //非个体需要校验注册资本和投资比例与企业成员占有的总和相等
                    if(regEntity.getCompanyType()!=1) {
                        List<CompanyCorePersonnelVO> corePersonnelList = companyCorePersonnelService.getCompanyCorePersonnelByCompanyIdOrOrderNo(null, regEntity.getOrderNo());
                        if (corePersonnelList == null){
                            return ResultVo.Fail("未找到企业成员信息");
                        }
                        //获取到股东列表
                        List<CompanyCorePersonnelVO> shareholderList = corePersonnelList.stream().filter(u -> u.getIsShareholder() == 1).collect(Collectors.toList());
//                        BigDecimal totalInvestmentAmount=  shareholderList.stream().map(u->u.getInvestmentAmount()).reduce(BigDecimal.ZERO,BigDecimal::add);
                        BigDecimal totalShareProportion=  shareholderList.stream().map(u->u.getShareProportion()).reduce(BigDecimal.ZERO,BigDecimal::add);
//                        if(regEntity.getRegisteredCapital().compareTo(totalInvestmentAmount) != 0){
//                            return ResultVo.Fail("成员投资金额与注册资金不一致");
//                        }
                        if(new BigDecimal("1").compareTo(totalShareProportion) != 0){
                            return ResultVo.Fail("成员占股比例不等于100%");
                        }
                        //重新设置法人的联系电话
                        CompanyCorePersonnelEntity companyCorePersonnelEntity = new CompanyCorePersonnelEntity();
                        companyCorePersonnelEntity.setOrderNo(regEntity.getOrderNo());
                        companyCorePersonnelEntity.setIsLegalPerson(1);
                        companyCorePersonnelEntity.setCompanyType(regEntity.getCompanyType());
                        List<CompanyCorePersonnelEntity> coreList = companyCorePersonnelService.select(companyCorePersonnelEntity);
                        if(coreList!=null && coreList.size()>0){
                            regEntity.setContactPhone(coreList.get(0).getContactPhone());
                        }
                    }
                    //通过
//                    if (!dto.getRegisteredName().equals(regEntity.getRegisteredName())
//                            && !dto.getRegisteredName().equals(regEntity.getShopNameOne())
//                            && !dto.getRegisteredName().equals(regEntity.getShopNameTwo())) {
//                        return ResultVo.Fail("个体名称确认有误");
//                    }
                    regEntity.setRegisteredName(dto.getRegisteredName());
                    entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_SUCCESS.getValue());
                    dto.setOrderStatus(orderStatus);
                } else if (Objects.equals(dto.getStatus(), 2)) {
                    //不通过
                    entity.setWorkOrderStatus(WorkOrderStatusEnum.AUDIT_FAILED.getValue());
                    dto.setOrderStatus(RegOrderStatusEnum.FAILED.getValue());
                } else {
                    //校验身份验证状态
                    validOpenAuthentication(orderEntity.getParkId(), regEntity.getIsOpenAuthentication(), orderEntity.getProductId(), regEntity.getCompanyType());
                    //核名驳回
                    entity.setWorkOrderStatus(WorkOrderStatusEnum.REJECTED.getValue());
                    regEntity.setRejectedItem(dto.getRejectedItem());
                    dto.setOrderStatus(RegOrderStatusEnum.REJECTED.getValue());
                }
                map = workOrderService.updateRegOrderStatus(entity, regEntity, orderEntity, dto, serEntity, accEntity);
                // 查询注册订单，判断该订单是否使用了优惠券
                if (Objects.equals(dto.getStatus(), 2) && Objects.nonNull(regEntity.getCouponsIssueId())) { // 该订单使用了优惠券
                    // 回滚优惠券使用状态，清空使用时间
                    CouponsIssueRecordEntity issueRecord = Optional.ofNullable(couponsIssueRecordService.findById(regEntity.getCouponsIssueId())).orElseThrow(() -> new BusinessException("未查询到优惠券发放记录"));
                    CouponsEntity couponsEntity = Optional.ofNullable(couponsService.findById(issueRecord.getCouponsId())).orElseThrow(() -> new BusinessException("未查询到优惠券信息"));
                    issueRecord.setUseTime(null);
                    issueRecord.setUpdateTime(new Date());
                    issueRecord.setUpdateUser(serEntity.getWorkNumberName());
                    issueRecord.setRemark("取消工单审核，回滚优惠券");
                    if (couponsEntity.getEndDate().after(new Date())) {
                        issueRecord.setStatus(CouponsIssueRecordStatusEnum.UNUSED.getValue());
                    } else {
                        issueRecord.setStatus(CouponsIssueRecordStatusEnum.STALE.getValue());
                    }
                    couponsIssueRecordService.editByIdSelective(issueRecord);
                    // 查询注册订单
                    OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("订单不存在"));
                    Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(order.getUserId(), order.getProductId(), order.getOemCode(), order.getParkId());
                    order.setPayAmount(payAmount);
                    order.setProfitAmount(payAmount);
                    orderService.editByIdSelective(order);
                    // 回滚注册订单支付金额
                    regEntity.setPayAmount(payAmount);
                    regEntity.setCouponsIssueId(null);
                    registerOrderService.updateByPrimaryKeySelective(regEntity);
                    // 回滚注册订单变更记录支付金额
                    RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
                    BeanUtils.copyProperties(regEntity, record);
                    record.setId(null);
                    record.setAddTime(entity.getUpdateTime());
                    record.setAddUser(serEntity.getWorkNumber());
                    record.setOrderStatus(dto.getOrderStatus());
                    registerOrderChangeRecordService.insertSelective(record);
                }
            }
            return ResultVo.Success(map);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 校验身份验证状态
     * @param parkId
     * @param isOpenAuthentication
     */
    private Integer validOpenAuthentication(Long parkId, Integer isOpenAuthentication, Long productId, Integer companyType) throws BusinessException{
        //V2.8 根据园区流程标识判断是否需要校验身份验证
        ProductParkRelaEntity productParkRelaEntity = new ProductParkRelaEntity();
        productParkRelaEntity.setParkId(parkId);
        productParkRelaEntity.setProductId(productId);
        productParkRelaEntity.setCompanyType(companyType);
        ProductParkRelaEntity entity = productParkRelaService.selectOne(productParkRelaEntity);
        if (null == entity) {
            throw new BusinessException("未查询到园区产品关系");
        }
        if (null == entity.getProcessMark()) {
            throw new BusinessException("未配置注册流程");
        }
        Integer processMark = entity.getProcessMark();
        if (Objects.equals(processMark, ParkProcessMarkEnum.IDENTITY.getValue())) {
            if (Objects.equals(isOpenAuthentication, OpenAuthenticationEnum.CLOSED.getValue())) {
                throw new BusinessException("用户还未开启身份验证，请确认已开启后继续。");
            }
        }
        //V2.9 订单状态根据园区流程处理
        if (Objects.equals(processMark, ParkProcessMarkEnum.SIGN.getValue())) {
            return RegOrderStatusEnum.REGISTRATION_UNDER_WAY.getValue();
        }
        return RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue();
    }

    /**
     * 校验办理核名编辑数据
     * @param dto
     * @throws BusinessException
     */
    private void validateRegDTO(WorkOrderDTO dto, String oemCode) throws BusinessException {
        if (Objects.equals(dto.getStatus(), 1)) {
//            if (StringUtils.isBlank(dto.getAgentAccount())) {
//                throw new BusinessException("经办人账号不能为空");
//            }
            if (StringUtils.isBlank(dto.getRegisteredName())) {
                throw new BusinessException("请选择个体名称");
            }
            //校验是否重名
            memberCompanyService.checkCompanyName(oemCode, dto.getRegisteredName());
        }else {
            if (StringUtils.isBlank(dto.getRemark())) {
                throw new BusinessException("备注必填，请填写后再提交！");
            }
        }
    }

    @ApiOperation("编辑办理核名工单")
    @PostMapping("edit/open/order")
    public ResultVo editOpenOrder(@RequestBody WorkOrderDTO dto) {
        if (dto.getId() == null) {
            return ResultVo.Fail("主键不能为空");
        }
        if (StringUtils.isBlank(dto.getPhone())
                && StringUtils.isBlank(dto.getEmail()) && dto.getIndustryId() == null
                && CollectionUtil.isEmpty(dto.getCategoryList()) && StringUtils.isBlank(dto.getBusinessScope())
                && StringUtils.isBlank(dto.getExampleName()) && StringUtils.isBlank(dto.getShopName())
                && StringUtils.isBlank(dto.getShopNameOne()) && StringUtils.isBlank(dto.getShopNameTwo())&& StringUtils.isBlank(dto.getOwnBusinessScope())) {
            return ResultVo.Fail("编辑信息必须有一条不能为空");
        }
        if(dto.getOperType()==null){

        }else if(dto.getOperType()==1 && dto.getIndustryId() == null && CollectionUtil.isEmpty(dto.getCategoryList())){
            return ResultVo.Fail("基本信息编辑至少有一个不能为空");
        }else if(dto.getOperType()==2 && StringUtils.isBlank(dto.getShopName())
                && StringUtils.isBlank(dto.getShopNameOne()) && StringUtils.isBlank(dto.getShopNameTwo())){
            return ResultVo.Fail("名称确认编辑至少有一个不能为空");
        }else if(dto.getOperType()==3  && StringUtils.isBlank(dto.getBusinessScope())&& StringUtils.isBlank(dto.getOwnBusinessScope())){
            return ResultVo.Fail("经营范围编辑至少有一个不能为空");
        }

        //校验客服状态
        CustomerServiceWorkNumberEntity serEntity;
        try {
            //校验行业类型
            if (dto.getIndustryId() != null) {
                IndustryEntity industryEntity = industryService.findById(dto.getIndustryId());
                if (industryEntity == null) {
                    throw new BusinessException("行业类型不存在");
                }
                if (!Objects.equals(industryEntity.getStatus(), IndustryStatusEnum.YES.getValue())) {
                    throw new BusinessException("行业类型不可用");
                }
            }
            WorkOrderEntity entity = workOrderService.findById(dto.getId());
            if (entity == null) {
                return ResultVo.Fail("工单不存在");
            }
            serEntity = ValidCustomer(entity.getOemCode());

            //校验工单归属客服信息
            validBelongCustomer(serEntity, entity.getCustomerServiceId());
            OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            if (!Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.REGISTER.getValue())) {
                return ResultVo.Fail("当前工单不是办理核名工单");
            }
            if (orderEntity.getOrderStatus() > RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue()) {
                return ResultVo.Fail("订单"+ RegOrderStatusEnum.getByValue(orderEntity.getOrderStatus()).getMessage() +"，不允许编辑信息");
            }
            //办理核名
            RegisterOrderEntity regEntity = new RegisterOrderEntity();
            regEntity.setOrderNo(entity.getOrderNo());
            regEntity.setOemCode(entity.getOemCode());
            regEntity = registerOrderService.selectOne(regEntity);
            if (regEntity == null) {
                return ResultVo.Fail("办理核名记录不存在");
            }
            Optional<ParkEntity> parkEntity = Optional.ofNullable(parkService.findById(orderEntity.getParkId()));
            String parkCity = parkEntity.map(ParkEntity::getParkCity).orElse(null);
            dto.setParkCity(parkCity);
            if (StringUtils.isBlank(regEntity.getExampleName())) {
                regEntity.setExampleName(Optional.ofNullable(industryService.findById(regEntity.getIndustryId())).map(IndustryEntity::getExampleName).orElse(null));
            }
            dto.setOrderStatus(orderEntity.getOrderStatus());
            workOrderService.updateRegOrder(regEntity, dto, serEntity);

            String remark = "办理核名工单信息编辑";
            if(dto.getOperType()==null){

            }else if(dto.getOperType()==1){
                remark = "基本信息编辑";
            }else if(dto.getOperType()==2){
                remark = "名称确认信息编辑";
            }else if(dto.getOperType()==3){
                remark = "经营范围确认信息编辑";
            }
            // 保存工单变更记录
            WorkOrderChangeRecordEntity changeRecord = new WorkOrderChangeRecordEntity();
            BeanUtils.copyProperties(entity,changeRecord);
            changeRecord.setWorkOrderDesc(remark);
            changeRecord.setId(null);
            changeRecord.setWorkOrderStatus(entity.getWorkOrderStatus());
            changeRecord.setAddTime(new Date());
            changeRecord.setAddUser(serEntity.getWorkNumber());
            changeRecord.setUpdateTime(new Date());
            changeRecord.setUpdateUser(serEntity.getWorkNumber());
            changeRecord.setRemark(null);
            this.workOrderChangeRecordService.insertSelective(changeRecord);
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
    }

    @ApiOperation("编辑开票工单")
    @PostMapping("edit/inv/order")
    public ResultVo editInvOrder(@RequestBody @Validated WorkInvOrderDTO dto, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //校验客服状态
        CustomerServiceWorkNumberEntity serEntity;
        try {
            WorkOrderEntity entity = workOrderService.findById(dto.getId());
            if (entity == null) {
                return ResultVo.Fail("工单不存在");
            }
            serEntity = ValidCustomer(entity.getOemCode());

            //校验工单归属客服信息
            validBelongCustomer(serEntity, entity.getCustomerServiceId());
            OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            if (!Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())) {
                return ResultVo.Fail("当前工单不是开票审核工单");
            }
            if (orderEntity.getOrderStatus() > InvoiceOrderStatusEnum.UNCHECKED.getValue()) {
                return ResultVo.Fail("订单"+ RegOrderStatusEnum.getByValue(orderEntity.getOrderStatus()).getMessage() +"，不允许编辑信息");
            }
            //开票审核工单
            InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
            invEntity.setOrderNo(entity.getOrderNo());
            invEntity.setOemCode(entity.getOemCode());
            invEntity = invoiceOrderService.selectOne(invEntity);
            if (invEntity == null) {
                return ResultVo.Fail("开票记录不存在");
            }
            if (StringUtils.isBlank(invEntity.getCategoryName())) {
                return ResultVo.Fail("开票类目不能为空！");
            }
            if(dto.getTaxClassificationAbbreviation()!=null&&dto.getTaxGoodsName()!=null){
                invEntity.setCategoryName(dto.getTaxClassificationAbbreviation()+"*"+dto.getTaxGoodsName());
                if(null == dto.getCategoryId()){
                    return ResultVo.Fail("开票类目id不能为空！");
                }
                invEntity.setCategoryId(dto.getCategoryId());
            }else{
                invEntity.setCategoryName(null);
            }
            if(invEntity.getTaxpayerType()==2 ){
                if(dto.getVatRate() == null) {
                    return ResultVo.Fail("增税率不能为空！");
                }
                //重新计算税费
                TaxCalculationVO taxCalculationVO = new TaxCalculationVO();
                taxCalculationVO.setCompanyId(invEntity.getCompanyId());
                taxCalculationVO.setType(1);
                taxCalculationVO.setOrderNo(invEntity.getOrderNo());
                taxCalculationVO.setVatRate(dto.getVatRate().divide(new BigDecimal(100)));
                taxCalculationVO.setSeason(0);
                taxCalculationVO.setYear(0);
                taxCalculationVO.setCalculationType(0);
                Map<String, Object> taxMap = invoiceOrderService.taxCalculation(taxCalculationVO);
                TaxFeeDetailVO taxFeeDetail = new TaxFeeDetailVO();
                BeanUtil.copyProperties(taxMap, taxFeeDetail);
                invEntity.setVatFee(taxFeeDetail.getPayableVatFee());
                invEntity.setVatFeeRate(taxFeeDetail.getVatFeeRate().divide(new BigDecimal(100)));
                invEntity.setSurcharge(taxFeeDetail.getPayableSurcharge());
                invEntity.setSurchargeRate(taxFeeDetail.getSurchargeRate().divide(new BigDecimal(100)));
                invEntity.setPeriodInvoiceAmount(taxFeeDetail.getPeriodInvoiceAmount());
                invEntity.setTaxpayerType(taxFeeDetail.getTaxpayerType());
                MemberCompanyEntity companyEntity = memberCompanyService.findById(invEntity.getCompanyId());
                if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(companyEntity.getCompanyType())) {
                    invEntity.setPersonalIncomeTax(taxFeeDetail.getPayableIncomeTax());
                    invEntity.setPersonalIncomeTaxRate(taxFeeDetail.getIncomeTaxRate().divide(new BigDecimal(100)));
                } else {
                    invEntity.setStampDutyRate(taxFeeDetail.getStampDutyRate());
                    invEntity.setStampDutyAmount(taxFeeDetail.getStampDutyAmount());
                    invEntity.setWaterConservancyFundRate(taxFeeDetail.getWaterConservancyFundRate());
                    invEntity.setWaterConservancyFundAmount(taxFeeDetail.getWaterConservancyFundAmount());
                }
                // 所得税为应税所得率征收方式时才有应税所得率
                TaxPolicyEntity policyEntity = taxPolicyService.queryTaxPolicyByParkId(companyEntity.getParkId(), companyEntity.getCompanyType(),companyEntity.getTaxpayerType());
                if (LevyWayEnum.TAXABLE_INCOME_RATE.getValue().equals(policyEntity.getLevyWay())) {
                    invEntity.setTaxableIncomeRate(taxFeeDetail.getTaxableIncomeRate().divide(new BigDecimal(100)));
                }
            }
            String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            if(dto.getEmail()!=null && !dto.getEmail().matches(regex)){
                return ResultVo.Fail("邮箱格式不对");
            }
            invEntity.setEmail(dto.getEmail());
            invEntity.setGoodsName(dto.getGoodsName());
            invEntity.setInvoiceRemark(dto.getInvoiceRemark());
            invoiceOrderService.editAndSaveHistory(invEntity, orderEntity.getOrderStatus(), serEntity.getWorkNumber(), "开票工单编辑");
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 编辑开票订单照片
     * @param id 工单id
     * @param image 图片地址
     * @param type 1：银行流水，2：业务合同
     * @return
     */
    @PostMapping("edit/inv/pic")
    public ResultVo editInvPic(@JsonParam Long id, @JsonParam String image, @JsonParam Integer type) {
        getCurrUser();
        if (id == null) {
            return ResultVo.Fail("请选择工单");
        }
        if (StringUtils.isBlank(image)) {
            return ResultVo.Fail("图片地址不能为空");
        }
        if (type == null || (!Objects.equals(type, 1) && !Objects.equals(type, 2) && !Objects.equals(type, 3) && !Objects.equals(type, 4))) {
            return ResultVo.Fail("上传操作有误");
        }
        String[] split = image.split(",");
        for (String s : split) {
            if (!ossService.doesObjectExistPrivate(s)) {
                return ResultVo.Fail("图片" + s + "在oss文件服务器不存在");
            }
        }
        WorkOrderEntity entity = workOrderService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("工单不存在");
        }
        if (!Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())
                && !Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.WATER.getValue())
                     && !Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())) {
            return ResultVo.Fail("当前工单类型不是开票审核工单也不是流水审核工单也不是成果审核工单");
        }
        //校验订单主表
        OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }

        //校验客服信息
        CustomerServiceWorkNumberEntity serEntity = ValidCustomer(orderEntity.getOemCode());

        //办理核名
        InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
        invEntity.setOrderNo(orderEntity.getOrderNo());
        invEntity.setOemCode(orderEntity.getOemCode());
        invEntity = invoiceOrderService.selectOne(invEntity);
        if (invEntity == null) {
            return ResultVo.Fail("开票记录不存在");
        }
        if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())) {
            if (orderEntity.getOrderStatus() > InvoiceOrderStatusEnum.UNCHECKED.getValue()) {
                return ResultVo.Fail("订单"+ RegOrderStatusEnum.getByValue(orderEntity.getOrderStatus()).getMessage() +"，不允许编辑信息");
            }
            if (Objects.equals(invEntity.getIsAfterUploadBankWater(), 0) && Objects.equals(1, type)) {
                return ResultVo.Fail("先开票后补传流水的开票工单不允许上传银行流水图片");
            }
        }else if(Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())) {
            if (!Objects.equals(invEntity.getAchievementStatus(), BankAchievementStatusEnum.TO_BE_AUDIT.getValue())) {
                return ResultVo.Fail("成果工单状态不是审核中，不允许编辑信息");
            }
        }else {
            if (!Objects.equals(invEntity.getBankWaterStatus(), BankWaterStatusEnum.TO_BE_AUDIT.getValue())) {
                return ResultVo.Fail("流水工单状态不是审核中，不允许编辑信息");
            }
        }
        //构建历史记录保存实体
        InvoiceOrderEntity histCopyEntity = new InvoiceOrderEntity();
        BeanUtils.copyProperties(invEntity,histCopyEntity);
        String remark;
        if (Objects.equals(1, type)) {
            //银行流水
            remark = "修改了流水照片，查看修改前图片|accountStatement";
            invEntity.setAccountStatement(image);
        } else if(Objects.equals(2, type)) {
            remark = "修改了业务合同图片，查看修改前图片|businessContractImgs";
            //业务合同
            invEntity.setBusinessContractImgs(image);
        } else if(Objects.equals(3, type)) {
            remark = "修改了成果图片，查看修改前图片|achievementImgs";
            //成果照片
            invEntity.setAchievementImgs(image);
        }else {
            remark = "修改了成果视频，查看修改前图片|achievementVideo";
            //成果视频
            invEntity.setAchievementVideo(image);
        }
        workOrderService.editAndSaveInvOrderHistory(entity, invEntity, histCopyEntity, orderEntity.getOrderStatus(), serEntity.getWorkNumber(), remark);
        return ResultVo.Success();

    }

    /**
     * 编辑办理核名订单身份证照片
     * @param id 工单id
     * @param idCardFront 身份证前面
     * @param idCardReverse 身份证后面
     * @return
     */
    @PostMapping("edit/register/idcard/pic")
    public ResultVo editInvPic(@JsonParam Long id, @JsonParam String idCardFront,@JsonParam String idCardReverse) {
        getCurrUser();
        if (id == null) {
            return ResultVo.Fail("请选择工单");
        }
        if (StringUtils.isBlank(idCardFront)) {
            return ResultVo.Fail("图片地址不能为空");
        }
        if (!ossService.doesObjectExistPrivate(idCardFront)) {
            return ResultVo.Fail("图片在oss文件服务器不存在");
        }
        WorkOrderEntity entity = workOrderService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("工单不存在");
        }
        if (!Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.REGISTER.getValue())) {
            return ResultVo.Fail("当前工单类型不是办理核名");
        }
        if (!Objects.equals(entity.getWorkOrderStatus(), WorkOrderStatusEnum.AUDITING.getValue())) {
            return ResultVo.Fail("当前工单类型不是审核中，不允许修改");
        }
        //校验订单主表
        OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }

        //校验客服信息
        CustomerServiceWorkNumberEntity serEntity = ValidCustomer(orderEntity.getOemCode());

        //办理核名
        RegisterOrderEntity registerOrderEntity = new RegisterOrderEntity();
        registerOrderEntity.setOrderNo(orderEntity.getOrderNo());
        registerOrderEntity.setOemCode(orderEntity.getOemCode());
        registerOrderEntity = registerOrderService.selectOne(registerOrderEntity);
        if (registerOrderEntity == null) {
            return ResultVo.Fail("办理核名订单不存在");
        }
        String remark = "修改了身份证照片，查看修改前图片|"+registerOrderEntity.getIdCardFront()+","+registerOrderEntity.getIdCardReverse();
        //新增历史记录
        RegisterOrderChangeRecordEntity registerOrderChangeRecordEntity=new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(registerOrderEntity,registerOrderChangeRecordEntity);
        registerOrderChangeRecordEntity.setId(null);
        registerOrderChangeRecordEntity.setAddTime(new Date());
        registerOrderChangeRecordEntity.setAddUser(getCurrUseraccount());
        registerOrderChangeRecordEntity.setUpdateTime(null);
        registerOrderChangeRecordEntity.setUpdateUser(null);
        registerOrderChangeRecordEntity.setRemark(remark);
        registerOrderChangeRecordEntity.setOrderStatus(orderEntity.getOrderStatus());
        registerOrderChangeRecordService.insertSelective(registerOrderChangeRecordEntity);
        //构建历史记录保存实体
        registerOrderEntity.setIdCardFront(idCardFront);
        registerOrderEntity.setIdCardReverse(idCardReverse);
        registerOrderEntity.setUpdateTime(new Date());
        registerOrderEntity.setUpdateUser(getCurrUseraccount());
        registerOrderEntity.setRemark(remark);
        registerOrderService.editByIdSelective(registerOrderEntity);
        return ResultVo.Success();

    }

    /**
     * 编辑办理核名订单上传视频
     * @param id 工单id
     * @param videoUrl 视频地址
     * @return
     */
    @PostMapping("edit/register/video")
    public ResultVo editRegistVideo(@JsonParam Long id, @JsonParam String videoUrl) {
        getCurrUser();
        if (id == null) {
            return ResultVo.Fail("请选择工单");
        }
        if (StringUtils.isBlank(videoUrl)) {
            return ResultVo.Fail("视频地址不能为空");
        }
        if (!ossService.doesObjectExistPrivate(videoUrl)) {
            return ResultVo.Fail("图片在oss文件服务器不存在");
        }
        WorkOrderEntity entity = workOrderService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("工单不存在");
        }
        if (!Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.REGISTER.getValue())) {
            return ResultVo.Fail("当前工单类型不是办理核名");
        }
        if (!Objects.equals(entity.getWorkOrderStatus(), WorkOrderStatusEnum.AUDITING.getValue())) {
            return ResultVo.Fail("当前工单类型不是审核中，不允许修改");
        }
        //校验订单主表
        OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }

        //校验客服信息
        CustomerServiceWorkNumberEntity serEntity = ValidCustomer(orderEntity.getOemCode());

        //办理核名
        RegisterOrderEntity registerOrderEntity = new RegisterOrderEntity();
        registerOrderEntity.setOrderNo(orderEntity.getOrderNo());
        registerOrderEntity.setOemCode(orderEntity.getOemCode());
        registerOrderEntity = registerOrderService.selectOne(registerOrderEntity);
        if (registerOrderEntity == null) {
            return ResultVo.Fail("办理核名订单不存在");
        }
        String remark = "修改了上传视频";
        //新增历史记录
        RegisterOrderChangeRecordEntity registerOrderChangeRecordEntity=new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(registerOrderEntity,registerOrderChangeRecordEntity);
        registerOrderChangeRecordEntity.setId(null);
        registerOrderChangeRecordEntity.setAddTime(new Date());
        registerOrderChangeRecordEntity.setAddUser(getCurrUseraccount());
        registerOrderChangeRecordEntity.setUpdateTime(null);
        registerOrderChangeRecordEntity.setUpdateUser(null);
        registerOrderChangeRecordEntity.setRemark(remark);
        registerOrderChangeRecordEntity.setOrderStatus(orderEntity.getOrderStatus());
        registerOrderChangeRecordService.insertSelective(registerOrderChangeRecordEntity);
        //构建历史记录保存实体
        registerOrderEntity.setVideoAddr(videoUrl);
        registerOrderEntity.setUpdateTime(new Date());
        registerOrderEntity.setUpdateUser(getCurrUseraccount());
        registerOrderEntity.setRemark(remark);
        registerOrderService.editByIdSelective(registerOrderEntity);

        //添加工单变更记录
        WorkOrderChangeRecordEntity record = new WorkOrderChangeRecordEntity();
        BeanUtils.copyProperties(entity, record);
        record.setId(null);
        record.setAddTime(new Date());
        record.setAddUser(getCurrUseraccount());
        record.setWorkOrderDesc("用户视频编辑");
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setRemark(null);
        workOrderChangeRecordService.insertSelective(record);
        return ResultVo.Success();

    }

    /**
     * 修改核心成员信息
     * @return
     */
    @PostMapping("edit/corePersonnelInfo")
    public ResultVo editCorePersonnelInfo(@RequestBody CompanyCorePersonnelDTO companyCorePersonnelDTO){
        CurrUser currUser = getCurrUser();
        if(companyCorePersonnelDTO==null){
            return ResultVo.Fail("企业成员信息不能为空");
        }
        if (companyCorePersonnelDTO.getId() == null) {
            return ResultVo.Fail("企业成员id不能为空");
        }
        if (StringUtil.isBlank(companyCorePersonnelDTO.getContactPhone())){
            return ResultVo.Fail("联系电话不能为空");
        }
        if (companyCorePersonnelDTO.getWorkOrderId() == null){
            return ResultVo.Fail("工单id不能为空");
        }
        CompanyCorePersonnelEntity entity = companyCorePersonnelService.findById(companyCorePersonnelDTO.getId());
        if(entity.getIdentityType()==1){
            if (StringUtil.isBlank(companyCorePersonnelDTO.getIdCardFront()) || StringUtil.isBlank(companyCorePersonnelDTO.getIdCardReverse())) {
                return ResultVo.Fail("身份证照片不能为空");
            }
        }
        if(entity.getIdentityType()==2){
            if (StringUtil.isBlank(companyCorePersonnelDTO.getBusinessLicense())) {
                return ResultVo.Fail("企业营业执照照片不能为空");
            }
        }
        if(entity.getIsShareholder()==1 && (companyCorePersonnelDTO.getShareProportion() == null
                || BigDecimal.ZERO.compareTo(companyCorePersonnelDTO.getShareProportion()) > 0
                || new BigDecimal("100").compareTo(companyCorePersonnelDTO.getShareProportion()) < 0)){
            return ResultVo.Fail("股东占股比例不能为空且占股比例只能在0~100的区间");
        }
        if (StringUtil.isNotBlank(companyCorePersonnelDTO.getContactPhone())){
            entity.setContactPhone(companyCorePersonnelDTO.getContactPhone());
        }
        if(entity.getIdentityType()==1) {
            if (StringUtil.isNotBlank(companyCorePersonnelDTO.getIdCardFront())) {
                entity.setIdCardFront(companyCorePersonnelDTO.getIdCardFront());
            }
            if (StringUtil.isNotBlank(companyCorePersonnelDTO.getIdCardReverse())) {
                entity.setIdCardReverse(companyCorePersonnelDTO.getIdCardReverse());
            }
        }else if(entity.getIdentityType()==2){
            if (StringUtil.isNotBlank(companyCorePersonnelDTO.getBusinessLicense())) {
                entity.setBusinessLicense(companyCorePersonnelDTO.getBusinessLicense());
            }
        }
        if(entity.getIsShareholder()==1){
            RegisterOrderEntity registerOrderEntity = registerOrderService.queryByOrderNo(entity.getOrderNo());
            if(registerOrderEntity==null){
                return ResultVo.Fail("未找到相应的订单数据");
            }
            entity.setInvestmentAmount(registerOrderEntity.getRegisteredCapital().multiply(companyCorePersonnelDTO.getShareProportion()).setScale(2, BigDecimal.ROUND_UP));
            entity.setShareProportion(companyCorePersonnelDTO.getShareProportion());
        }
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        companyCorePersonnelService.editByIdSelective(entity);

        WorkOrderEntity workOrderEntity = workOrderService.findById(companyCorePersonnelDTO.getWorkOrderId());
        // 保存工单变更记录
        WorkOrderChangeRecordEntity changeRecord = new WorkOrderChangeRecordEntity();
        BeanUtils.copyProperties(workOrderEntity,changeRecord);
        changeRecord.setWorkOrderDesc("企业成员信息编辑");
        changeRecord.setId(null);
        changeRecord.setAddTime(new Date());
        changeRecord.setAddUser(currUser.getUseraccount());
        changeRecord.setWorkOrderStatus(workOrderEntity.getWorkOrderStatus());
        changeRecord.setUpdateTime(new Date());
        changeRecord.setUpdateUser(currUser.getUseraccount());
        changeRecord.setRemark(null);
        this.workOrderChangeRecordService.insertSelective(changeRecord);
        return ResultVo.Success();
    }

    @Resource
    RegisterOrderChangeRecordService registerOrderChangeRecordService;
    @ApiOperation("开启身份验证")
    @PostMapping("turn/auth/open/order")
    public ResultVo turnAuthOpenOrder(@JsonParam Long id) {
        if (id == null) {
            return ResultVo.Fail("主键不能为空");
        }
        //校验客服状态
        CustomerServiceWorkNumberEntity serEntity;
        try {
            WorkOrderEntity entity = workOrderService.findById(id);
            if (entity == null) {
                return ResultVo.Fail("工单不存在");
            }
            serEntity = ValidCustomer(entity.getOemCode());

            //校验工单归属客服信息
            validBelongCustomer(serEntity, entity.getCustomerServiceId());
            OrderEntity orderEntity = orderService.queryByOrderNo(entity.getOrderNo());
            if (orderEntity == null) {
                return ResultVo.Fail("订单不存在");
            }
            if (!Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.REGISTER.getValue())) {
                return ResultVo.Fail("当前工单不是办理核名工单");
            }
            if (orderEntity.getOrderStatus() > RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue()) {
                return ResultVo.Fail("订单"+ RegOrderStatusEnum.getByValue(orderEntity.getOrderStatus()).getMessage() +"，不允许操作");
            }
            //办理核名
            RegisterOrderEntity regEntity = new RegisterOrderEntity();
            regEntity.setOrderNo(entity.getOrderNo());
            regEntity.setOemCode(entity.getOemCode());
            regEntity = registerOrderService.selectOne(regEntity);
            if (regEntity == null) {
                return ResultVo.Fail("办理核名记录不存在");
            }
            regEntity.setIsOpenAuthentication(OpenAuthenticationEnum.OPENED.getValue());
            String remark = serEntity.getWorkNumber() + "开启了身份验证";
            registerOrderService.editAndSaveHistory(regEntity, orderEntity.getOrderStatus(), serEntity.getWorkNumber(), remark);
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
    }

    @ApiOperation("工单转审")
    @PostMapping("forward")
    public ResultVo forward(@JsonParam Long id,@JsonParam Long customerServiceId,@JsonParam String remark) {
        CurrUser user = getCurrUser();
        if (id == null) {
            return ResultVo.Fail("主键不能为空");
        }
        if(StringUtils.isBlank(remark) && remark.trim().length()>100){
            return ResultVo.Fail("备注不能超过100个字符");
        }
        workOrderService.forward(id,customerServiceId,remark,user.getUserId(),user.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 下载未匹配到的经营范围
     * @param id
     * @return
     */
    @PostMapping("/downloadUnmatchedBusinessScope")
    public ResultVo downloadUnmatchedBusinessScope(@JsonParam Long id){
        if (id == null) {
            return ResultVo.Fail("请求参数有误");
        }
        //校验客服登录状态
        getCurrUser();
//        getCustomerWorker();
        WorkOrderEntity entity = workOrderService.findById(id);
        List<UnmatchedBusinessScopeVO> list = registerOrderGoodsDetailRelaService.getUnmatchedBusinessScopeByOrderNo(entity.getOrderNo());
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("未匹配到的经营范围", "未匹配到的经营范围"+ DateUtil.format(new Date(),"yyyy-MM-dd"), UnmatchedBusinessScopeVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("未匹配到的经营范围导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 校验审核弹窗
     * @param id
     * @return
     */
    @PostMapping("/auditNotice")
    public ResultVo auditNotice(@JsonParam Long id){
        if (id == null) {
            return ResultVo.Fail("请求参数有误");
        }
        WorkOrderEntity entity = workOrderService.findById(id);
        List<UnmatchedBusinessScopeVO> list = registerOrderGoodsDetailRelaService.getUnmatchedBusinessScopeByOrderNo(entity.getOrderNo());
        Map<String,Object> map = new HashMap<>();
        if(!CollectionUtils.isEmpty(list)){
            map.put("type",2);
            map.put("content","存在未匹配经营范围的商品，可能会影响客户开票，是否继续？");
        }
        return ResultVo.Success(map);
    }
}
