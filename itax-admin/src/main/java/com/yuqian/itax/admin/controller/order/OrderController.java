package com.yuqian.itax.admin.controller.order;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateSqlVO;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.agreement.service.ParkAgreementTemplateRelaService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawOrderQuery;
import com.yuqian.itax.corporateaccount.service.CorporateAccountWithdrawalOrderService;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawOrderVO;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.dto.ConfirmInvoiceRecordDTO;
import com.yuqian.itax.order.entity.query.OrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.entity.*;
import com.yuqian.itax.park.entity.query.TaxRulesVatRateQuery;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.park.enums.ParkProcessMarkEnum;
import com.yuqian.itax.park.enums.TaxPolicyStatusEnum;
import com.yuqian.itax.park.service.ParkAgentAccountService;
import com.yuqian.itax.park.service.ParkBusinessAddressRulesService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.entity.query.PayWaterQuery;
import com.yuqian.itax.pay.entity.vo.PaywaterVO;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.enums.PayWaterTypeEnum;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.enums.RefundWaterStatusEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.system.entity.ChannelInfoEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.vo.CompanyCorePersonnelExportVO;
import com.yuqian.itax.user.entity.vo.CompanyCorePersonnelVO;
import com.yuqian.itax.user.enums.AuditStateEnum;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.*;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.constraint.IdCardConstraint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.joda.time.DateTime;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单controller
 * @author：pengwei
 * @Date：2019/12/6 11:12
 * @version：1.0
 */
@Api(tags = "订单Controller")
@RestController
@RequestMapping("order")
@Slf4j
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RegisterOrderService registerOrderService;

    @Autowired
    private InvoiceOrderService invoiceOrderService;

    @Autowired
    private RegisterOrderChangeRecordService registerOrderChangeRecordService;

    @Autowired
    private InvoiceOrderChangeRecordService invoiceOrderChangeRecordService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private MemberOrderRelaService memberOrderRelaService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private TaxPolicyService taxPolicyService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private CompanyCancelOrderService companyCancelOrderService;

    @Autowired
    private CompanyCancelOrderChangeRecordService companyCancelOrderChangeRecordService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private OssService ossService;

    @Autowired
    private PayWaterService payWaterService;

    @Autowired
    private IndustryService industryService;

    @Autowired
    private OrderAttachmentService orderAttachmentService;

    @Autowired
    private OemService oemService;

    @Autowired
    private LogisCompanyService logisCompanyService;
    @Autowired
    private CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Autowired
    private MemberLevelService memberLevelService;

    @Autowired
    private ParkAgentAccountService parkAgentAccountService;

    @Autowired
    private ParkBusinessAddressRulesService parkBusinessAddressRulesService;

    @Autowired
    private CorporateAccountWithdrawalOrderService corporateAccountWithdrawalOrderService;

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @Autowired
    private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;

    @Autowired
    ConsumptionInvoiceOrderChangeService consumptionInvoiceOrderChangeService;

    @Autowired
    private ChannelInfoService channelInfoService;

    @Autowired
    private CrowdLabelService crowdLabelService;

    @Autowired
    OemAccessPartyService oemAccessPartyService;

    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;

    @Autowired
    private CompanyTaxBillService companyTaxBillService;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private InvoiceRecordChangeService invoiceRecordChangeService;

    @Autowired
    private InvoiceorderGoodsdetailRelaService invoiceorderGoodsdetailRelaService;

    @Autowired
    private ParkAgreementTemplateRelaService parkAgreementTemplateRelaService;
    @Autowired
    private AgreementTemplateService agreementTemplateService;
    @Autowired
    private CompanyCorePersonnelService companyCorePersonnelService;
    @Autowired
    private ProductParkRelaService productParkRelaService;

    @ApiOperation(value="开户订单列表页",notes="企业注册订单查询")
    @PostMapping("open/page")
    public ResultVo listPageOpenOrder(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
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
        query.setPlatformType(userEntity.getPlatformType());
        PageInfo<OpenOrderVO> page = orderService.listPageOpenOrder(query);
        return ResultVo.Success(page);
    }
    @ApiOperation("查询所有渠道来源")
    @PostMapping("getChannelCode")
    public  ResultVo<List> listChannelInfo(){
        List<ChannelInfoEntity> list = channelInfoService.selectAll();
        return ResultVo.Success(list);
    }

    @ApiOperation("批量推送订单")
    @PostMapping("open/batchPushOrder")
    public ResultVo batchOrderPushGuoJin(@RequestBody OrderQuery query){
        try {
            orderService.orderPush(query);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    @ApiOperation("开户订单列表导出")
    @PostMapping("open/batchOpenOrder")
    public ResultVo batchOpenOrder(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());if (userEntity == null) {
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
        List<OpenOrderVO> list = orderService.listOpenOrder(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("企业注册订单列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), "企业注册订单列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), OpenOrderVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("批量下载企业信息导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("开票订单列表页")
    @PostMapping("inv/page")
    public ResultVo listPageInvOrder(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
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
        query.setPlatformType(userEntity.getPlatformType());
        PageInfo<InvOrderVO> page = orderService.listPageInvOrder(query);
        return ResultVo.Success(page);
    }
    @ApiOperation("修改快递单号页")
    @PostMapping("update/courierNumber")
    public ResultVo updateCourierNumber(@JsonParam String orderNo){
        if (StringUtil.isEmpty(orderNo)){
            return ResultVo.Fail("订单错误");
        }
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if (orderEntity == null || orderEntity.getOrderType() != 6 || orderEntity.getOrderStatus() != 6){
            return ResultVo.Fail("该订单不允许修改快递单号");
        }
        InvCourierVo vo = invoiceOrderService.queryCourierByOrderNo(orderNo);
        return ResultVo.Success(vo);
    }

    @ApiOperation("修改快递单号")
    @PostMapping("update/courierInfo")
    public ResultVo updateCourierInfo(@JsonParam String orderNo,@JsonParam String courierNumber,@JsonParam String courierCompanyName){
        CurrUser currUser = getCurrUser();
        if (StringUtil.isEmpty(courierNumber)){
            return ResultVo.Fail("快递单号不能为空");
        }
        if (StringUtil.isEmpty(courierCompanyName)){
            return ResultVo.Fail("快递公司不能为空");
        }
        LogisCompanyEntity logisCompanyEntity = logisCompanyService.queryByCompanyName(courierCompanyName);
        if (logisCompanyEntity == null){
            return ResultVo.Fail("不支持该快递公司");
        }
        InvoiceOrderEntity entity = invoiceOrderService.queryByOrderNo(orderNo);
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if (entity == null){
            return ResultVo.Fail("订单号错误");
        }
        if (entity.getCourierNumber() .equals(courierNumber)){
            return ResultVo.Fail("不能输入重复的快递单号");
        }
        entity.setCourierNumber(courierNumber);
        entity.setCourierCompanyName(courierCompanyName);
        invoiceOrderService.editByIdSelective(entity);
        //保存历史记录
        invoiceOrderService.editAndSaveHistory(entity,orderEntity.getOrderStatus(),currUser.getUseraccount(),"修改快递单号");
        return ResultVo.Success();
    }


    @ApiOperation("会员购买列表页")
    @PostMapping("member/page")
    public ResultVo listPageMemberLvUpOrder(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            query.setTree(getOrgTree());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        query.setOemCode(userEntity.getOemCode());
        PageInfo<MemberLvUpOrderVO> page = orderService.listPageMemberLvUpOrder(query);
        return ResultVo.Success(page);
    }

    /**
     * 订单领证详情页
     * @param orderNo
     * @return
     *核准通过是从3变成4？
     *用户已签名是5->6?
     *经办人已签名6->7?
     *订单已领证7->8?
     *登记通过是 4-5
     */
    @ApiOperation("订单领证详情页")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("open/certificate")
    public ResultVo certificate(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        OrderEntity entity = new OrderEntity();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue())) {
            return ResultVo.Fail("订单状态不正确");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }

        RegisterOrderEntity regEntity = new RegisterOrderEntity();
        regEntity.setOrderNo(orderNo);
        regEntity.setOemCode(entity.getOemCode());
        regEntity = registerOrderService.selectOne(regEntity);
        if (regEntity == null) {
            return ResultVo.Fail("工商注册订单不存在");
        }
        return ResultVo.Success(new RegOrderAdminVO(regEntity));
    }

    @ApiOperation("订单领证确认")
    @ApiImplicitParams({
        @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true),
        @ApiImplicitParam(name="businessLicense",value="营业执照图片",dataType="String",paramType="query",required = true)
    })
    @PostMapping("open/certificate/confirm")
    public ResultVo confirmCertificate(@RequestBody String data){
        CurrUser currUser = getCurrUser();
        String orderNo = getParameter(data, "orderNo");
        String businessLicense = getParameter(data, "businessLicense");
        String businessLicenseCopy = getParameter(data, "businessLicenseCopy");
        String companyName = getParameter(data, "companyName");//企业名称
        String ein = getParameter(data, "creditCode");//税号
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (StringUtils.isBlank(businessLicense)) {
            return ResultVo.Fail("营业执照图片不能为空");
        }
        if (StringUtils.isBlank(companyName)) {
            return ResultVo.Fail("公司名称不能为空");
        }
        if (StringUtil.isBlank(ein)){
            return ResultVo.Fail("税号不能为空");
        }
        List<MemberCompanyEntity> memberCompanyEntityList = memberCompanyService.queryMemberCompanyByEinStatusNotCancellation(ein);
        if (CollectionUtil.isNotEmpty(memberCompanyEntityList)){
            return ResultVo.Fail("税号已存在");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!Objects.equals(entity.getOrderStatus(), RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue())) {
            return ResultVo.Fail("订单状态不正确");
        }
        RegisterOrderEntity regEntity = new RegisterOrderEntity();
        regEntity.setOrderNo(orderNo);
        regEntity.setOemCode(entity.getOemCode());
        regEntity = registerOrderService.selectOne(regEntity);
        if (regEntity == null) {
            return ResultVo.Fail("工商注册订单不存在");
        }
        TaxPolicyEntity taxPolicyEntity = new TaxPolicyEntity();
        taxPolicyEntity.setCompanyType(regEntity.getCompanyType());
        taxPolicyEntity.setParkId(entity.getParkId());
        taxPolicyEntity.setTaxpayerType(regEntity.getTaxpayerType());
        taxPolicyEntity = taxPolicyService.selectOne(taxPolicyEntity);
        if (taxPolicyEntity == null) {
            return ResultVo.Fail("园区税费政策不存在");
        }
        if (!Objects.equals(taxPolicyEntity.getStatus(), TaxPolicyStatusEnum.ON_SHELF.getValue())) {
            return ResultVo.Fail("园区税费政策未上架");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        //校验是否有重名企业
        memberCompanyService.checkCompanyName(companyName);
        //修改企业名称
        regEntity.setRegisteredName(companyName);
        registerOrderService.editByIdSelective(regEntity);

        orderService.confirmCertificate(entity, regEntity, taxPolicyEntity, businessLicense, businessLicenseCopy, currUser.getUseraccount(),ein);
        //订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
        try {
            List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
            OpenOrderVO vo = new OpenOrderVO();
            vo.setOrderNo(orderNo);
            vo.setId(entity.getUserId());
            vo.setOemCode(entity.getOemCode());
            vo.setOrderType(entity.getOrderType());
            listToBePush.add(vo);
            rabbitTemplate.convertAndSend("orderPush", listToBePush);
        }catch (BusinessException e){
            log.error("推送失败：{}",e.getMessage());
        }
        return ResultVo.Success();
    }

    @ApiOperation("开票订单出票")
    @ApiImplicitParams({
        @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true),
    })
    @PostMapping("inv/ticket")
    public ResultVo invTicket(@RequestBody @Validated(Add.class) ConfirmInvoiceRecordDTO confirmInvoiceRecordDTO, BindingResult result){
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(confirmInvoiceRecordDTO.getOrderNo());
        entity.setOemCode(userEntity.getOemCode());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        //查询开票记录
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(confirmInvoiceRecordDTO.getInvoiceRecordNo());
        invoiceRecordEntity.setOrderNo(confirmInvoiceRecordDTO.getOrderNo());
        invoiceRecordEntity = invoiceRecordService.selectOne(invoiceRecordEntity);
        if(invoiceRecordEntity == null){
            return ResultVo.Fail("未找到相对应的开票记录");
        }

        //出票日期判断 1、出票日期不能晚于当天 2、出票日期不能早于创建时间！3、出票日期和订单其它发票不在同一税期！
        Date ticketTime = DateUtil.parseDefaultDate(confirmInvoiceRecordDTO.getTicketTime());
        int days = DateUtil.diffDate(new Date(), ticketTime);
        if (days < 0) {
            throw new BusinessException("出票日期不能晚于当天");
        }
        days = DateUtil.diffDate(ticketTime, entity.getAddTime());
        if (days < 0) {
            throw new BusinessException("出票日期不能早于订单创建时间！");
        }
        int num = invoiceRecordService.queryLastQuarterRecordNumByOrderNo(entity.getOrderNo(),ticketTime);
        if (num > 0) {
            throw new BusinessException("出票日期和订单其它发票不在同一税期！");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        //只有人工出票 和待出票 状态可以确认出票
        if (!ObjectUtils.equals(invoiceRecordEntity.getStatus(), InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue())
                && !ObjectUtils.equals(invoiceRecordEntity.getStatus(), InvoiceRecordStatusEnum.TO_BE_CONFIRMED.getValue())) {
            return ResultVo.Fail("当前开票记录不能进行确认出票操作");
        }

        if(entity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) { //开票订单
            if (!Objects.equals(entity.getOrderStatus(), InvoiceOrderStatusEnum.IN_TICKETING.getValue())) {
                return ResultVo.Fail("订单状态不正确");
            }
            InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
            invEntity.setOrderNo(confirmInvoiceRecordDTO.getOrderNo());
            invEntity.setOemCode(entity.getOemCode());
            invEntity = invoiceOrderService.selectOne(invEntity);
            if (invEntity == null) {
                return ResultVo.Fail("开票订单不存在");
            }
            //线下开票，保存开票记录对应的出票日期
            if (ObjectUtils.equals(invoiceRecordEntity.getHandlingWay(), 1)) {
                invoiceRecordEntity.setTicketTime(ticketTime);
                invoiceRecordService.editByIdSelective(invoiceRecordEntity);
            }
            //更新开票记录状态
            invoiceRecordService.updateInvoiceRecordStatus(confirmInvoiceRecordDTO.getInvoiceRecordNo(), confirmInvoiceRecordDTO.getOrderNo(),
                    InvoiceRecordStatusEnum.COMPLETED.getValue(), "确认出票", null, userEntity.getUsername());
            if (StringUtils.isNotBlank(invEntity.getInvoiceImgs())) {
                invEntity.setInvoiceImgs(invEntity.getInvoiceImgs() + "," + confirmInvoiceRecordDTO.getInvoiceImgs());
            } else {
                invEntity.setInvoiceImgs(confirmInvoiceRecordDTO.getInvoiceImgs());
            }
            invEntity.setRemark("确认出票");
            invoiceOrderService.editByIdSelective(invEntity);
            //判断是否存在未完成的开票记录，如果存在则不能修改订单状态
            int count = invoiceRecordService.findUnfinishedInvoiceRecordByOrderNo(invoiceRecordEntity.getOrderNo(), invoiceRecordEntity.getInvoiceRecordNo());
            if (count == 0) {
                //将发票图片、修改开票订单税期更新到开票订单表
                String urls = invoiceRecordService.getInvoiceDetailImgUrlsByOrder(invEntity.getOrderNo());
                log.debug("查询电子发票地址结果-----------："+urls);
                if (StringUtils.isNotBlank(urls)) {
                    invEntity.setInvoiceImgs(invEntity.getInvoiceImgs() + ","+urls);
                    invoiceOrderService.editByIdSelective(invEntity);
                }

                invEntity.setTaxYear(confirmInvoiceRecordDTO.getTaxYear());
                invEntity.setTaxSeasonal(confirmInvoiceRecordDTO.getTaxQuarter());
                Date date = invoiceRecordService.getMaxTicketTimeByOrderNo(confirmInvoiceRecordDTO.getOrderNo());
                invEntity.setConfirmInvoiceTime(date);
                invoiceOrderService.ticket(invEntity, currUser.getUseraccount(), entity.getUserId());
            }
        }else if(entity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){
            if (!Objects.equals(entity.getOrderStatus(), 1)) {
                return ResultVo.Fail("订单状态不正确");
            }
            ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
            consumptionInvoiceOrderEntity.setOrderNo(entity.getOrderNo());
            consumptionInvoiceOrderEntity.setOemCode(entity.getOemCode());
            consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
            if(consumptionInvoiceOrderEntity == null){
                return ResultVo.Fail("消费开票订单不存在");
            }
            // 电票
            if (consumptionInvoiceOrderEntity.getInvoiceWay() == 2){
                //线下开票，保存开票记录对应的出票日期
                if (ObjectUtils.equals(invoiceRecordEntity.getHandlingWay(), 1)) {
                    invoiceRecordEntity.setTicketTime(ticketTime);
                    invoiceRecordService.editByIdSelective(invoiceRecordEntity);
                }
                //更新开票记录状态
                invoiceRecordService.updateInvoiceRecordStatus(confirmInvoiceRecordDTO.getInvoiceRecordNo(), confirmInvoiceRecordDTO.getOrderNo(),
                        InvoiceRecordStatusEnum.COMPLETED.getValue(), "确认出票", null, userEntity.getUsername());
                if (StringUtils.isNotBlank(consumptionInvoiceOrderEntity.getInvoiceImgs())) {
                    consumptionInvoiceOrderEntity.setInvoiceImgs(consumptionInvoiceOrderEntity.getInvoiceImgs() + "," + confirmInvoiceRecordDTO.getInvoiceImgs());
                } else {
                    consumptionInvoiceOrderEntity.setInvoiceImgs(confirmInvoiceRecordDTO.getInvoiceImgs());
                }
                consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
                //更新开票记录状态
                invoiceRecordService.updateInvoiceRecordStatus(confirmInvoiceRecordDTO.getInvoiceRecordNo(), confirmInvoiceRecordDTO.getOrderNo(),
                        InvoiceRecordStatusEnum.COMPLETED.getValue(), "确认出票", null, userEntity.getUsername());
                //判断是否存在未完成的开票记录，如果存在则不能修改订单状态
                int count = invoiceRecordService.findUnfinishedInvoiceRecordByOrderNo(invoiceRecordEntity.getOrderNo(), invoiceRecordEntity.getInvoiceRecordNo());
                if (count == 0) {
                    consumptionInvoiceOrderEntity.setCompleteTime(new Date());
                    consumptionInvoiceOrderEntity.setRemark("消费发票出票完成");
                    consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                    consumptionInvoiceOrderEntity.setUpdateUser(currUser.getUseraccount());
                    consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);

                    ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
                    BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
                    consumptionInvoiceOrderChangeEntity.setStatus(2);
                    consumptionInvoiceOrderChangeEntity.setId(null);
                    consumptionInvoiceOrderChangeEntity.setAddUser(currUser.getUseraccount());
                    consumptionInvoiceOrderChangeEntity.setAddTime(new Date());
                    consumptionInvoiceOrderChangeEntity.setUpdateTime(null);
                    consumptionInvoiceOrderChangeEntity.setUpdateUser(null);
                    consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
                    //修改订单状态为已出票
                    orderService.updateOrderStatus(currUser.getUseraccount(), consumptionInvoiceOrderEntity.getOrderNo(), 2);
                }
                // 纸票
            }else if(consumptionInvoiceOrderEntity.getInvoiceWay() == 1){
                //查询开票记录
                InvoiceRecordEntity invoiceRecord = new InvoiceRecordEntity();
                invoiceRecord.setInvoiceRecordNo(confirmInvoiceRecordDTO.getInvoiceRecordNo());
                invoiceRecord.setOrderNo(confirmInvoiceRecordDTO.getOrderNo());
                invoiceRecord = invoiceRecordService.selectOne(invoiceRecord);
                if(invoiceRecord==null){
                    throw new BusinessException("未找到开票记录");
                }
                invoiceRecord.setStatus(InvoiceRecordStatusEnum.COMPLETED.getValue());
                invoiceRecord.setUpdateTime(new Date());
                invoiceRecord.setUpdateUser(currUser.getUseraccount());
                invoiceRecord.setInvoiceDesc("确认出票");
                invoiceRecordService.editByIdSelective(invoiceRecord);
                consumptionInvoiceOrderEntity.setInvoiceImgs(confirmInvoiceRecordDTO.getInvoiceImgs());
                consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                consumptionInvoiceOrderEntity.setUpdateUser(userEntity.getUsername());
                consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
                ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
                BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
                consumptionInvoiceOrderChangeEntity.setStatus(4);
                consumptionInvoiceOrderChangeEntity.setInvoiceImgs(confirmInvoiceRecordDTO.getInvoiceImgs());
                consumptionInvoiceOrderChangeEntity.setId(null);
                consumptionInvoiceOrderChangeEntity.setAddUser(userEntity.getUsername());
                consumptionInvoiceOrderChangeEntity.setAddTime(new Date());
                consumptionInvoiceOrderChangeEntity.setUpdateTime(null);
                consumptionInvoiceOrderChangeEntity.setUpdateUser(null);
                consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
                //修改订单状态为待发货
                orderService.updateOrderStatus(userEntity.getUsername(), consumptionInvoiceOrderEntity.getOrderNo(), 4);
                //  添加开票记录变更表
                InvoiceRecordChangeEntity invoiceRecordChangeEntity = new InvoiceRecordChangeEntity();
                invoiceRecordChangeEntity.setOrderNo(confirmInvoiceRecordDTO.getOrderNo());
                invoiceRecordChangeEntity.setInvoiceRecordNo(invoiceRecord.getInvoiceRecordNo());
                invoiceRecordChangeEntity.setOemCode(invoiceRecord.getOemCode());
                invoiceRecordChangeEntity.setInvoiceAmount(invoiceRecord.getInvoiceAmount());
                invoiceRecordChangeEntity.setStatus(7);
                invoiceRecordChangeEntity.setHandlingWay(invoiceRecord.getHandlingWay());
                invoiceRecordChangeEntity.setInvoiceDesc("出票完成");
                invoiceRecordChangeEntity.setParkId(invoiceRecord.getParkId());
                invoiceRecordChangeEntity.setInvoiceTotalPrice(invoiceRecord.getInvoiceTotalPrice());
                invoiceRecordChangeEntity.setInvoiceTotalTax(invoiceRecord.getInvoiceTotalTax());
                invoiceRecordChangeEntity.setInvoiceTotalPriceTax(invoiceRecord.getInvoiceTotalPriceTax());
                invoiceRecordChangeEntity.setCompleteTime(new Date());
                invoiceRecordChangeEntity.setAddTime(new Date());
                invoiceRecordChangeEntity.setAddUser(userEntity.getUsername());
                invoiceRecordChangeService.insertSelective(invoiceRecordChangeEntity);
            }

        }
        return ResultVo.Success();
    }

    @ApiOperation("会员升级财务审核")
    @ApiImplicitParams({
        @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true),
        @ApiImplicitParam(name="status",value="审核状态:（1：通过，2：不通过）",dataType="Integer",paramType="query",required = true),
        @ApiImplicitParam(name="payWaterImgs",value="收款流水",dataType="String",paramType="query",required = true),
        @ApiImplicitParam(name="remark",value="备注",dataType="String",paramType="query",required = true)
    })
    @PostMapping("member/audit")
    public ResultVo memberAudit(@RequestBody String data){
        CurrUser currUser = getCurrUser();
        String orderNo = getParameter(data, "orderNo");
        Integer status = getParameter(data, "status", Integer.class);
        String payWaterImgs = getParameter(data, "payWaterImgs");
        String remark = getParameter(data, "remark");
        if (StringUtils.isBlank(orderNo) || status == null || status < 1 || status > 2){
            return ResultVo.Fail("请求参数有误");
        }
        if (Objects.equals(1, status) && StringUtils.isBlank(payWaterImgs)) {
            return ResultVo.Fail("请上传收款流水");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.UPGRADE.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!Objects.equals(entity.getOrderStatus(), MemberOrderStatusEnum.TO_BE_AUDIT.getValue())) {
            return ResultVo.Fail("订单状态不正确");
        }
//        ProductEntity proEntity = productService.findById(entity.getProductId());
//        if (proEntity == null) {
//            return ResultVo.Fail(ResultConstants.PRODUCT_NOT_EXISTS.getRetMsg());
//        }
//        if (!Objects.equals(proEntity.getProdType(), ProductTypeEnum.DIAMOND.getValue())) {
//            return ResultVo.Fail("只有升级城市服务商需要财务审核");
//        }
        if (!Objects.equals(entity.getAuditStatus(), AuditStateEnum.TO_APPROVE.getValue())) {
            return ResultVo.Fail("升级状态不对");
        }

        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        MemberAccountEntity memEntity = memberAccountService.findById(entity.getUserId());
        if (memEntity == null) {
            return ResultVo.Fail("会员不存在");
        }
        if (Objects.equals(1, status)) {
            //当前用户等级
            MemberLevelEntity memberLevelEntity = memberLevelService.findById(memEntity.getMemberLevel());
            //订单用户等级
            MemberLevelEntity targetLevel = memberLevelService.findById(entity.getProductId());
            if (memberLevelEntity.getLevelNo() > targetLevel.getLevelNo()) {
                return ResultVo.Fail("当前订单升级等级小于会员等级");
            }
            entity.setOrderStatus(MemberOrderStatusEnum.COMPLETED.getValue());
            entity.setPayWaterImgs(payWaterImgs);
            entity.setAuditStatus(AuditStateEnum.APPROVED.getValue());
        } else {
            if (StringUtils.isBlank(remark)) {
                return ResultVo.Fail("请填写审核不通过原因");
            }
            entity.setAuditStatus(AuditStateEnum.APPROVE_NO_PASS.getValue());
            entity.setOrderStatus(MemberOrderStatusEnum.CANCELLED.getValue());
        }
        Date updateTime = new Date();
        entity.setAuditRemark(remark);
        entity.setAuditUserId(currUser.getUserId().intValue());
        entity.setAuditUserAccount(currUser.getUseraccount());
        entity.setUpdateTime(updateTime);
        entity.setUpdateUser(currUser.getUseraccount());
        orderService.memberAudit(entity, memEntity);
        //  审核通过才发短信
        //V3.0  订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
        if (Objects.equals(1, status)) {
            try {
                List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
                OpenOrderVO vo = new OpenOrderVO();
                vo.setOrderNo(orderNo);
                vo.setId(entity.getUserId());
                vo.setOemCode(entity.getOemCode());
                vo.setCompleteTime(updateTime);
                vo.setOrderType(entity.getOrderType());
                listToBePush.add(vo);
                rabbitTemplate.convertAndSend("orderPush", listToBePush);
            }catch (BusinessException e){
                log.error("推送失败：{}",e.getMessage());
            }
        }
        return ResultVo.Success();
    }

    @ApiOperation(value = "开户订单列表页导出",notes = "企业注册订单导出")
    @PostMapping("open/export")
    public ResultVo listOpenOrderExport(@RequestBody OrderQuery query){
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
        query.setPlatformType(userEntity.getPlatformType());
        List<OpenOrderVO> lists = orderService.listOpenOrder(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("开户订单记录", "开户订单", OpenOrderVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("开户订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }
    @ApiOperation("开票订单列表页导出")
    @PostMapping("inv/export")
    public ResultVo listInvOrderExport(@RequestBody OrderQuery query){
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
        query.setPlatformType(userEntity.getPlatformType());
        List<InvOrderVO> lists = orderService.listInvOrder(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("开票订单记录", "开票订单", InvOrderVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("开票订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }
    @ApiOperation("会员购买订单列表页导出")
    @PostMapping("member/export")
    public ResultVo listMemberLvUpOrderExport(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            query.setTree(getOrgTree());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        query.setOemCode(userEntity.getOemCode());
        List<MemberLvUpOrderVO> lists = orderService.listMemberLvUpOrder(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("会员购买订单记录", "会员购买订单", MemberLvUpOrderVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("会员购买订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }
    @ApiOperation("开户历史记录")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("open/history")
    public ResultVo listOpenOrderHistory(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        Example example = new Example(RegisterOrderChangeRecordEntity.class);
        example.createCriteria().andEqualTo("orderNo", orderNo).andEqualTo("oemCode", entity.getOemCode());
        example.orderBy("addTime").desc();
        List<RegisterOrderChangeRecordEntity> list = registerOrderChangeRecordService.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Success(list);
        }
        List<RegisterOrderChangeRecordVO> vos = Lists.newArrayList();
        RegisterOrderChangeRecordVO vo;
        for (RegisterOrderChangeRecordEntity recordEntity : list) {
            vo = new RegisterOrderChangeRecordVO();
            BeanUtils.copyProperties(recordEntity, vo);
            if (StringUtils.isNotBlank(vo.getRemark()) && vo.getRemark().contains("查看修改前图片")) {
                String[] remarks = vo.getRemark().split("\\|");
                vo.setRemark(remarks[0]);
                if (remarks.length > 1) {
                    String[] urls=remarks[1].split(",");
                    List<String> urlList=new ArrayList<>();
                    for (String url: urls) {
                        urlList.add(ossService.getPrivateImgUrl(url));
                    }
                    vo.setImgUrl(urlList);
                }
            }
            vos.add(vo);
        }
        return ResultVo.Success(vos);
    }
    @ApiOperation("代签收")
    @PostMapping("inv/signed")
    public ResultVo beSigned(@JsonParam String orderNo,@JsonParam String remark){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (StringUtils.isBlank(remark)) {
            return ResultVo.Fail("待签收说明不能为空");
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.INVOICE.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue().equals(entity.getOrderStatus())){
            return ResultVo.Fail("订单状态不正确");
        }
        InvoiceOrderEntity inv = new InvoiceOrderEntity();
        inv.setOrderNo(orderNo);
        inv.setOemCode(userEntity.getOemCode());
        inv = invoiceOrderService.selectOne(inv);
        if (inv == null){
            return ResultVo.Fail("开票订单不存在");
        }
        orderService.updateInvOrderStatusAndlogisticsInfo(inv,entity,"代客户签收",remark,entity.getUserId(),currUser.getUseraccount());
        return ResultVo.Success();
    }

    @ApiOperation("开票历史记录")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("inv/history")
    public ResultVo listInvOrderHistory(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.INVOICE.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        Example example = new Example(InvoiceOrderChangeRecordEntity.class);
        example.createCriteria().andEqualTo("orderNo", orderNo).andEqualTo("oemCode", entity.getOemCode());
        example.orderBy("addTime").desc();
        List<InvoiceOrderChangeRecordEntity> list = invoiceOrderChangeRecordService.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Success(list);
        }
        List<InvoiceOrderChangeRecordVO> vos = Lists.newArrayList();
        List<String> imgUrls;
        InvoiceOrderChangeRecordVO vo;
        for (InvoiceOrderChangeRecordEntity invEntity : list) {
            vo = new InvoiceOrderChangeRecordVO();
            BeanUtils.copyProperties(invEntity, vo);
            if (StringUtils.isNotBlank(vo.getRemark()) && (vo.getRemark().contains("查看修改前图片")
            ||vo.getRemark().contains("换前合同")||vo.getRemark().contains("换前发票")||vo.getRemark().contains("换前流水")
            ||vo.getRemark().contains("收款凭证") )) {
                String[] remarks = vo.getRemark().split("\\|");
                vo.setRemark(remarks[0]);
                if (remarks.length > 1) {
                    imgUrls = Lists.newArrayList();
                    if ("businessContractImgs".equals(remarks[1]) && StringUtils.isNotBlank(vo.getBusinessContractImgs())) {
                        String[] images = vo.getBusinessContractImgs().split(",");
                        for (String img : images) {
                            imgUrls.add(ossService.getPrivateImgUrl(img));
                        }
                    } else if ("accountStatement".equals(remarks[1]) && StringUtils.isNotBlank(vo.getAccountStatement())) {
                        String[] images = vo.getAccountStatement().split(",");
                        for (String img : images) {
                            imgUrls.add(ossService.getPrivateImgUrl(img));
                        }
                    }else if ("invoiceImgs".equals(remarks[1]) && StringUtils.isNotBlank(vo.getInvoiceImgs())) {
                        String[] images = vo.getInvoiceImgs().split(",");
                        for (String img : images) {
                            imgUrls.add(ossService.getPrivateImgUrl(img));
                        }
                    }else if ("receiptPaymentVoucher".equals(remarks[1]) && StringUtils.isNotBlank(vo.getReceiptPaymentVoucher())) {
                        String[] images = vo.getReceiptPaymentVoucher().split(",");
                        for (String img : images) {
                            imgUrls.add(ossService.getPrivateImgUrl(img));
                        }
                    }
                    vo.setImgUrl(imgUrls);
                }
            }
            vos.add(vo);
        }
        return ResultVo.Success(vos);
    }

    @ApiOperation("开票订单取消")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("inv/cancel")
    public ResultVo invCancel(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.INVOICE.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (entity.getOrderStatus() > InvoiceOrderStatusEnum.IN_TICKETING.getValue() && entity.getOrderStatus() != InvoiceOrderStatusEnum.TO_PAYMENT_REVIEW.getValue()) {
            return ResultVo.Fail("当前订单状态不允许取消");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        orderService.cancelOrder(entity, InvoiceOrderStatusEnum.CANCELED.getValue(), currUser.getUseraccount(), false);

        return ResultVo.Success();
    }

    @ApiOperation("开户订单取消")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("open/cancel")
    public ResultVo openCancel(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (entity.getOrderStatus() >= RegOrderStatusEnum.COMPLETED.getValue()
                && entity.getOrderStatus() <= RegOrderStatusEnum.FAILED.getValue()) {
            return ResultVo.Fail("当前订单状态不允许取消");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        MemberAccountEntity memberAccountEntity = memberAccountService.findById(entity.getUserId());
        if (memberAccountEntity != null && memberAccountEntity.getAccessPartyId() != null && !entity.getOrderStatus().equals(RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue())){
            return ResultVo.Fail("接入方订单当前不能取消！");
        }
        HashMap<String, String> map = Maps.newHashMap();
        String refundResult = orderService.cancelOrder(entity, RegOrderStatusEnum.CANCELLED.getValue(), currUser.getUseraccount(), false);
        if (StringUtil.isNotBlank(refundResult)) {
            map.put("refundResult", refundResult);
            if(refundResult.indexOf("退款失败！")>-1){
                map.put("refundResultCode", "error");
            }
            return ResultVo.Success(map);
        }
        return ResultVo.Success(map);
    }
    /**
     * 开票批量出库弹框
     */
    @ApiOperation("开票批量出库弹框")
    @PostMapping("inv/batch/info")
    public ResultVo invBatchExport(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        try {
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                query.setParkId(userEntity.getParkId());
            } else {
                query.setTree(getOrgTree());
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        query.setOrderStatuses(InvoiceOrderStatusEnum.TO_BE_SHIPPED.getValue() + "," + InvoiceOrderStatusEnum.OUT_OF_STOCK.getValue());
        Map<String, Integer> map = orderService.sumInvOrder(query);
        if (map == null) {
            return ResultVo.Fail("暂无开票订单导出");
        }
        return ResultVo.Success(map);
    }

    @ApiOperation("开票订单批量出库")
    @PostMapping("inv/batch/stock")
    public ResultVo invBatchStock(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        try {
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                query.setParkId(userEntity.getParkId());
            } else {
                query.setTree(getOrgTree());
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        query.setOrderStatuses(InvoiceOrderStatusEnum.TO_BE_SHIPPED.getValue() + "," + InvoiceOrderStatusEnum.OUT_OF_STOCK.getValue());
        List<InvOrderBatchShipmentsVO> lists = orderService.listInvBatchStock(query, currUser.getUseraccount());
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无出库订单导出");
        }
        try {
            exportExcel("开票订单记录", "开票订单", InvOrderBatchShipmentsVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("出库订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }

    }

    @ApiOperation("开票订单批量发货")
    @PostMapping("inv/batch/send")
    public ResultVo invBatchSend(@RequestParam("file") MultipartFile file) {
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<InvOrderBatchStockVO> list;
        List<InvOrderBatchStockVO> failed = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            list = ExcelImportUtil.importExcel(file.getInputStream(), InvOrderBatchStockVO.class, params);
        } catch (Exception e) {
            log.error("批量发货异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("文件内容为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        Date updateTime = new Date();
        String orgTree = getOrgTree();
        Map<String, Object> map = Maps.newHashMap();
        for (InvOrderBatchStockVO inv : list) {
            if (StringUtils.isBlank(inv.getCourierNumber())) {
                inv.setFailed("快递单号不能为空");
                failed.add(inv);
                continue;
            }
            if (StringUtils.isBlank(inv.getCourierCompanyName())) {
                inv.setFailed("快递公司编号不能为空");
                failed.add(inv);
                continue;
            }
            LogisCompanyEntity logisCompanyEntity = logisCompanyService.queryByCompanyName(inv.getCourierCompanyName());
            if (logisCompanyEntity == null) {
                inv.setFailed("快递公司不存在");
                failed.add(inv);
                continue;
            }
            OrderEntity entity = orderService.queryByOrderNo(inv.getOrderNo());
            if (entity == null) {
                inv.setFailed("订单不存在");
                failed.add(inv);
                continue;
            }
            if (userEntity.getPlatformType() != 1 &&  userEntity.getPlatformType() != 3) {
                if (!StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
                    inv.setFailed("订单不属于当前OEM机构");
                    failed.add(inv);
                    continue;
                }
            }
            if (!Objects.equals(entity.getOrderStatus(), InvoiceOrderStatusEnum.OUT_OF_STOCK.getValue())) {
                inv.setFailed("订单状态不是出库中");
                failed.add(inv);
                continue;
            }
            MemberAccountEntity accEntity = memberAccountService.findById(entity.getUserId());
            if (accEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                    return ResultVo.Fail("订单不属于当前园区");
                }
            } else {
                boolean belongAdmin = belongAdmin(entity.getUserId(), orgTree, 5);
                if (belongAdmin) {
                    return ResultVo.Fail("订单不属于当前登录用户组织");
                }
            }
            InvoiceOrderEntity invEntity = invoiceOrderService.queryByOrderNo(inv.getOrderNo());
            if (invEntity == null) {
                inv.setFailed("开票记录不存在");
                failed.add(inv);
                continue;
            }
            invEntity.setCourierCompanyName(inv.getCourierCompanyName());
            invEntity.setCourierNumber(inv.getCourierNumber());
            invEntity.setUpdateTime(updateTime);
            invEntity.setUpdateUser(currUser.getUseraccount());
            entity.setOrderStatus(InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue());
            entity.setUpdateUser(currUser.getUseraccount());
            entity.setUpdateTime(updateTime);
            try {
                orderService.updateInvOrderStatus(invEntity, entity, "批量发货");
                map.put("name", inv.getCourierCompanyName());
                map.put("number", inv.getCourierNumber());
                if (!InvoiceCreateWayEnum.COMMISSION.getValue().equals(invEntity.getCreateWay())) {
                    smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.INVOICE_SEND.getValue(), map, 2);
                }
            } catch (Exception e) {
                log.error("批量发货修改状态异常：" + e.getMessage(), e);
                inv.setFailed("批量发货修改状态失败");
                failed.add(inv);
                continue;
            }

            // 发货成功订单向第三方机构发送回调
            if (null != accEntity.getAccessPartyId()) {
                HashMap<String, Object> param = new HashMap<>();
                param.put("callbackType", 3); //回调类型 1-取消 2-出票 3-发货 4-完成
                param.put("courierNumber", inv.getCourierNumber());
                param.put("courierCompanyName", inv.getCourierCompanyName());
                param.put("orderNo", invEntity.getOrderNo());
                // 发送回调数据
                invoiceOrderService.accessPartyPush(invEntity.getOrderNo(), accEntity.getOemCode(), accEntity.getAccessPartyId(), param);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", list.size() - failed.size());
        result.put("failed", failed.size());
        //如果无失败则不生成下载文件
        if (CollectionUtil.isEmpty(failed)){
            result.put("downLoadUrl", "");
            return ResultVo.Success(result);
        }
        DictionaryEntity dicEntity = dictionaryService.getByCode("file_download_path");
        if (dicEntity == null) {
            return ResultVo.Fail("字典数据未配置");
        }
        File bag = new File(dicEntity.getDictValue() + "/" + currUser.getUseraccount());
        if(!bag.exists()){
            bag.mkdirs();//如果路径不存在就先创建路径
        }
        String fileName = System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), InvOrderBatchStockVO.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("批量发货失败记录，保存服务器异常：" + e.getMessage(), e);
            return ResultVo.Fail("批量发货失败记录，保存异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    @ApiOperation("佣金开票签收")
    @PostMapping("inv/commission/sign")
    public ResultVo invCommissionSign(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(orderNo);
        orderEntity.setOemCode(userEntity.getOemCode());
        orderEntity.setOrderType(OrderTypeEnum.INVOICE.getValue());
        orderEntity = orderService.selectOne(orderEntity);
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue())) {
            return ResultVo.Fail("订单状态不是" + InvoiceOrderStatusEnum.TO_BE_RECEIVED.getMessage());
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), orderEntity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(orderEntity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
        invEntity.setOrderNo(orderNo);
        invEntity.setOemCode(orderEntity.getOemCode());
        invEntity = invoiceOrderService.selectOne(invEntity);
        if (invEntity == null) {
            return ResultVo.Fail("开票订单不存在");
        }
        if (!Objects.equals(invEntity.getCreateWay(), InvoiceCreateWayEnum.COMMISSION.getValue())) {
            return ResultVo.Fail("开票方式不是" + InvoiceCreateWayEnum.COMMISSION.getMessage());
        }
        //更新开票完成时间
        invEntity.setCompleteTime(new Date());
        invEntity.setUpdateTime(new Date());
        invEntity.setUpdateUser(currUser.getUseraccount());
        invoiceOrderService.editByIdSelective(invEntity);
        //更新开票状态
        orderEntity.setOrderStatus(InvoiceOrderStatusEnum.SIGNED.getValue());
        invoiceOrderService.updateInvoiceStatus(invEntity, orderEntity, currUser.getUseraccount(), "佣金开票签收");

        // 查询企业
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(invEntity.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到个体户信息"));
        // 查询用户信息
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(company.getMemberId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        // 订单完成，发送短信
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", invEntity.getCourierCompanyName());
        map.put("number", invEntity.getCourierNumber());
        smsService.sendTemplateSms(member.getMemberPhone(), member.getOemCode(), VerifyCodeTypeEnum.INVOICE_SIGN.getValue(), map, 1);

        //V3.0 订单完成推送给国金  订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
        if (ChannelPushStateEnum.TO_BE_PAY.getValue().equals(orderEntity.getChannelPushState())) {
            try {
                List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
                OpenOrderVO vo = new OpenOrderVO();
                vo.setOrderNo(orderEntity.getOrderNo());
                vo.setId(orderEntity.getUserId());
                vo.setOemCode(orderEntity.getOemCode());
                vo.setOrderType(orderEntity.getOrderType());
                listToBePush.add(vo);
                rabbitTemplate.convertAndSend("orderPush", listToBePush);
            }catch (BusinessException e){
                log.error("推送失败：{}",e.getMessage());
            }
        }

        return ResultVo.Success();
    }

    @ApiOperation("企业注销订单列表页")
    @PostMapping("cancel/page")
    public ResultVo listPageCancelOrder(@RequestBody OrderQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
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
            query.setOemCode(userEntity.getOemCode());
            query.setPlatformType(userEntity.getPlatformType());
            PageInfo<CompanyCancelOrderVO> page = orderService.listPageCancelOrder(query);
            return ResultVo.Success(page);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
    }

    @ApiOperation("企业注销订单导出")
    @PostMapping("cancel/export")
    public ResultVo listCancelOrderExport(@RequestBody OrderQuery query){
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
        query.setPlatformType(userEntity.getPlatformType());
        List<CompanyCancelOrderVO> lists = orderService.listCancelOrder(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("企业注销订单记录", "企业注销订单", CompanyCancelOrderVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("企业注销订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("企业注销订单历史记录")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("cancel/history")
    public ResultVo listCancelOrderHistory(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.CANCELLATION.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        Example example = new Example(CompanyCancelOrderChangeRecordEntity.class);
        example.createCriteria().andEqualTo("orderNo", orderNo).andEqualTo("oemCode", entity.getOemCode());
        example.orderBy("addTime").desc();
        return ResultVo.Success(CompanyCancelOrderChangeRecordVO.getList(companyCancelOrderChangeRecordService.selectByExample(example)));
    }

    @ApiOperation("企业注销订单取消")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("company/cancel")
    public ResultVo companyOrderCancel(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.CANCELLATION.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!(entity.getOrderStatus().equals(CompCancelOrderStatusEnum.TO_BE_PAY.getValue()) || entity.getOrderStatus().equals(CompCancelOrderStatusEnum.BILL_HANDLE.getValue()) || entity.getOrderStatus().equals(CompCancelOrderStatusEnum.IN_PROCESSING.getValue()))) {
            return ResultVo.Fail("当前订单状态不允许取消");
        }
        PayWaterEntity payEntity = new PayWaterEntity();
        payEntity.setOemCode(entity.getOemCode());
        payEntity.setOrderNo(entity.getOrderNo());
        payEntity.setOrderType(entity.getOrderType());
        payEntity.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
        payEntity = payWaterService.selectOne(payEntity);
        if (payEntity != null) {
            throw new BusinessException("该订单存在未获得结果的支付，请稍后再试！");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        CompanyCancelOrderEntity companyCancelOrderEntity = new CompanyCancelOrderEntity();
        companyCancelOrderEntity.setOrderNo(orderNo);
        companyCancelOrderEntity.setOemCode(entity.getOemCode());
        companyCancelOrderEntity = companyCancelOrderService.selectOne(companyCancelOrderEntity);
        if (companyCancelOrderEntity == null) {
            return ResultVo.Fail("企业注销订单不存在");
        }
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(companyCancelOrderEntity.getCompanyId());
        if (null == company) {
            return ResultVo.Fail("未查询到企业信息");
        }
        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(company.getEin());
        query.setCompanyId(companyCancelOrderEntity.getCompanyId());
        query.setRange(1);
        query.setStatusRange(4);
        List<PendingTaxBillVO> pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            return ResultVo.Fail("存在已申报的税单，不允许取消订单");
        }
        query.setStatusRange(2);
        pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        // 如果存在当前税期为未申报的税单，则把税单状态变成已作废已支付的税费退回至余额
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(pendingTaxBillList.get(0).getCompanyTaxBillId());
            if (companyTaxBillEntity != null){
                companyTaxBillEntity.setTaxBillStatus(9);
                companyTaxBillService.rechargeCompanyBill(companyTaxBillEntity,currUser.getUseraccount(),"取消注销订单");
            }
        }
        try {
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(currUser.getUseraccount());
            entity.setOrderStatus(CompCancelOrderStatusEnum.CANCELED.getValue());
            companyCancelOrderService.cancelOrder(entity, companyCancelOrderEntity);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 校验企业是否存在未补缴的税单
     * @param orderNo
     * @return
     */
    @PostMapping("checkTaxBill")
    public ResultVo checkTaxBill(@JsonParam String orderNo){
        //验证登陆
        getCurrUser();
        CompanyCancelOrderEntity companyCancelEntity = new CompanyCancelOrderEntity();
        companyCancelEntity.setOrderNo(orderNo);
        companyCancelEntity = companyCancelOrderService.selectOne(companyCancelEntity);
        if(companyCancelEntity.getCompanyId()==null){
            return ResultVo.Fail("请选择注销订单");
        }
        // 企业税单
        List<CompanyTaxBillEntity> list = companyTaxBillService.queryCompanyTaxByCompanyId(companyCancelEntity.getCompanyId());
        Map<String,Object> map = new HashMap<>();
        if (list != null && list.size()>0){
            map.put("type",2);
            map.put("content","该企业存在未补缴的税单，注销后将无法追缴，请确定是否继续注销？");
        }
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(companyCancelEntity.getCompanyId());
        if (null == company) {
            return ResultVo.Fail("未查询到企业信息");
        }
        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(company.getEin());
        query.setCompanyId(companyCancelEntity.getCompanyId());
        query.setStatusRange(5);
        List<PendingTaxBillVO> pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            map.put("type",1);
            map.put("content","该企业存在未处理的税单，请处理完后再注销！");
            return ResultVo.Success(map);
        }
        query.setStatusRange(3);
        pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            map.put("type",2);
            map.put("content","该企业存在未确认成本的税单，注销后税单将变成已作废，请确定是否继续注销？");
        }
        return ResultVo.Success(map);
    }

    @ApiOperation("企业注销注销成功确认页")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true)
    @PostMapping("cancel/confirm/page")
    public ResultVo cancelConfirmPage(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        OrderEntity entity = new OrderEntity();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.CANCELLATION.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!(CompCancelOrderStatusEnum.IN_PROCESSING.getValue().equals(entity.getOrderStatus())
                || CompCancelOrderStatusEnum.TO_BE_PAY.getValue().equals(entity.getOrderStatus())
                || CompCancelOrderStatusEnum.TAX_CANCEL_COMPLETED.getValue().equals(entity.getOrderStatus()))) {
            return ResultVo.Fail("订单状态不正确");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        CompanyCancelOrderEntity companyCancelEntity = new CompanyCancelOrderEntity();
        companyCancelEntity.setOrderNo(orderNo);
        companyCancelEntity.setOemCode(entity.getOemCode());
        companyCancelEntity = companyCancelOrderService.selectOne(companyCancelEntity);
        if (companyCancelEntity == null) {
            return ResultVo.Fail("企业注销订单不存在");
        }
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyCancelEntity.getCompanyId());
        if (memberCompanyEntity == null) {
            return ResultVo.Fail("我的企业信息不存在");
        }
        return ResultVo.Success(new CompanyCancelConformVO(companyCancelEntity, memberCompanyEntity));
    }

    @ApiOperation("企业税务注销成功确认")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true),
            @ApiImplicitParam(name="attachmentAddr",value="上传附件",dataType="String",paramType="query",required = true)
    })
    @PostMapping("taxCancel/confirm")
    public ResultVo taxCancel(@JsonParam String orderNo, @JsonParam String attachmentAddr,@JsonParam String remark){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("订单编号不能为空");
        }

        OrderEntity entity = new OrderEntity();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.CANCELLATION.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!CompCancelOrderStatusEnum.IN_PROCESSING.getValue().equals(entity.getOrderStatus())
                && !CompCancelOrderStatusEnum.TO_BE_PAY.getValue().equals(entity.getOrderStatus()) ) {
            return ResultVo.Fail("订单状态不正确");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        CompanyCancelOrderEntity companyCancelEntity = new CompanyCancelOrderEntity();
        companyCancelEntity.setOrderNo(orderNo);
        companyCancelEntity.setOemCode(entity.getOemCode());
        companyCancelEntity = companyCancelOrderService.selectOne(companyCancelEntity);
        if (companyCancelEntity == null) {
            return ResultVo.Fail("企业注销订单不存在");
        }
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyCancelEntity.getCompanyId());
        if (memberCompanyEntity == null) {
            return ResultVo.Fail("我的企业信息不存在");
        }
        //企业实体上传
        memberCompanyEntity.setCancelCredentials(attachmentAddr);
        //
        companyCancelEntity.setAttachmentAddr(attachmentAddr);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        if (CompCancelOrderStatusEnum.TO_BE_PAY.getValue().equals(entity.getOrderStatus())) { // 待付款的注销订单支付金额改为0
            entity.setPayAmount(0L);
        }
        entity.setOrderStatus(CompCancelOrderStatusEnum.TAX_CANCEL_COMPLETED.getValue());
        memberCompanyEntity.setCancelRemark(remark);
        //保存企业注销订单
        companyCancelEntity.setUpdateTime(entity.getUpdateTime());
        companyCancelEntity.setUpdateUser(entity.getUpdateUser());
        companyCancelOrderService.editByIdSelective(companyCancelEntity);
        //保存历史记录
        CompanyCancelOrderChangeRecordEntity record = new CompanyCancelOrderChangeRecordEntity();
        BeanUtils.copyProperties(companyCancelEntity, record);
        record.setId(null);
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setOrderStatus(entity.getOrderStatus());
        record.setAddTime(new Date());
        record.setAddUser(currUser.getUseraccount());
        companyCancelOrderChangeRecordService.insertSelective(record);
        //修改订单
        orderService.editByIdSelective(entity);
        //修改我的企业状态
        memberCompanyEntity.setStatus(MemberCompanyStatusEnum.TAX_CANCELLED.getValue());
        memberCompanyEntity.setUpdateTime(new Date());
        memberCompanyEntity.setUpdateUser(currUser.getUseraccount());
        memberCompanyService.editByIdSelective(memberCompanyEntity);
        return ResultVo.Success();
    }

    @ApiOperation("企业工商注销成功确认")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",paramType="query",required = true),
            @ApiImplicitParam(name="attachmentAddr",value="上传附件",dataType="String",paramType="query",required = true)
    })
    @PostMapping("cancel/confirm")
    public ResultVo cancelConfirm(@JsonParam String orderNo, @JsonParam String attachmentAddr,@JsonParam String remark){
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("订单编号不能为空");
        }
        if (StringUtils.isBlank(attachmentAddr)){
            return ResultVo.Fail("上传附件不能为空");
        }
        if (StringUtils.isBlank(remark)){
            return ResultVo.Fail("注销说明不能为空");
        }
        if (remark.length()>50){
            return ResultVo.Fail("注销说明不能超过50个字符");
        }
        OrderEntity entity = new OrderEntity();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.CANCELLATION.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (!CompCancelOrderStatusEnum.TAX_CANCEL_COMPLETED.getValue().equals(entity.getOrderStatus())) {
            return ResultVo.Fail("订单状态不正确");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        CompanyCancelOrderEntity companyCancelEntity = new CompanyCancelOrderEntity();
        companyCancelEntity.setOrderNo(orderNo);
        companyCancelEntity.setOemCode(entity.getOemCode());
        companyCancelEntity = companyCancelOrderService.selectOne(companyCancelEntity);
        if (companyCancelEntity == null) {
            return ResultVo.Fail("企业注销订单不存在");
        }
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyCancelEntity.getCompanyId());
        if (memberCompanyEntity == null) {
            return ResultVo.Fail("我的企业信息不存在");
        }
        //企业实体上传
        if (StringUtil.isNotBlank(memberCompanyEntity.getCancelCredentials())) {
            attachmentAddr = memberCompanyEntity.getCancelCredentials() + "," + attachmentAddr;
        }
        memberCompanyEntity.setCancelCredentials(attachmentAddr);
        //
        companyCancelEntity.setAttachmentAddr(attachmentAddr);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        if (CompCancelOrderStatusEnum.TO_BE_PAY.getValue().equals(entity.getOrderStatus())) { // 待付款的注销订单支付金额改为0
            entity.setPayAmount(0L);
        }
//        entity.setOrderStatus(CompCancelOrderStatusEnum.COMPLETED.getValue());  V4.1.6 拆分注销成功为税务注销成功和工商注销成功
        entity.setOrderStatus(CompCancelOrderStatusEnum.COMPANY_CANCEL_COMPLETED.getValue());
        memberCompanyEntity.setCancelRemark(remark);
        companyCancelOrderService.cancelConfirm(entity, companyCancelEntity, memberCompanyEntity,getCurrUseraccount());
        //V3.0 订单完成推送给国金  订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
        //  企业注销
        if (ChannelPushStateEnum.TO_BE_PAY.getValue().equals(entity.getChannelPushState())) {
            try {
                List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
                OpenOrderVO vo = new OpenOrderVO();
                vo.setOrderNo(entity.getOrderNo());
                vo.setId(entity.getUserId());
                vo.setOemCode(entity.getOemCode());
                vo.setOrderType(entity.getOrderType());
                listToBePush.add(vo);
                rabbitTemplate.convertAndSend("orderPush", listToBePush);
            } catch (BusinessException e) {
                log.error("推送失败：{}", e.getMessage());
            }
        }
        CompanyCorporateAccountEntity cor = companyCorporateAccountService.queryCorpByCompanyId(companyCancelEntity.getCompanyId());
        if (cor != null){
            //  存在对公户账户
            return ResultVo.Success("该企业存在对公户，请记得提醒客户去银行注销！");
        }else{
            return ResultVo.Success();
        }
    }

    @ApiOperation(value = "开户详情",notes = "开户订单详情")
    @PostMapping("open/detail")
    public ResultVo openDetail(@JsonParam String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(orderNo);
        orderEntity.setOemCode(userEntity.getOemCode());
        orderEntity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        orderEntity = orderService.selectOne(orderEntity);
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }

        RegisterOrderEntity regEntity = new RegisterOrderEntity();
        regEntity.setOrderNo(orderNo);
        regEntity.setOemCode(orderEntity.getOemCode());
        regEntity = registerOrderService.selectOne(regEntity);
        if (regEntity == null) {
            return ResultVo.Fail("开户订单不存在");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), orderEntity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(orderEntity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
        if (accEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OemEntity oemEntity = oemService.getOem(orderEntity.getOemCode());
        if (oemEntity == null) {
            return ResultVo.Fail("机构不存在");
        }
        Map<String, Object> map = Maps.newHashMap();
        //基础信息
        OrderVO orderVO = new OrderVO(orderEntity, memberOrderRelaService.findById(orderEntity.getRelaId()), accEntity, regEntity.getCompanyType());
        //  人群标签名称
        if (orderEntity.getCrowdLabelId() != null){
            CrowdLabelEntity crowdLabelEntity = crowdLabelService.findById(orderEntity.getCrowdLabelId());
            orderVO.setCrowdLabelName(crowdLabelEntity.getCrowdLabelName());
        }
        //  H5接入方名称
        if (accEntity.getAccessPartyId() != null){
            OemAccessPartyEntity oemAccessPartyEntity =  oemAccessPartyService.findById(accEntity.getAccessPartyId());
            orderVO.setAccessPartyName(oemAccessPartyEntity.getAccessPartyName());
        }
        orderVO.setOemName(oemEntity.getOemName());
        //获取渠道名称
        if (accEntity.getChannelCode() != null){
            ChannelInfoEntity channelInfoEntity = new ChannelInfoEntity();
            channelInfoEntity.setChannelCode(accEntity.getChannelCode());
            channelInfoEntity = channelInfoService.selectOne(channelInfoEntity);
            if (channelInfoEntity != null && channelInfoEntity.getChannelName() != null){
                orderVO.setChannelName(channelInfoEntity.getChannelName());
            }
        }
        //  获取邀请人id
        if (accEntity.getParentMemberId() != null){
            orderVO.setParentMemberId(accEntity.getParentMemberId());
            orderVO.setParentMemberAccount(accEntity.getParentMemberAccount());
        }
        //获取园区名称
        orderVO.setParkName(Optional.ofNullable(parkService.findById(orderEntity.getParkId())).map(ParkEntity::getParkName).orElse(null));
        map.put("orderInfo", orderVO);
        //开户信息
        RegisterDetailOrderVO regOrder = new RegisterDetailOrderVO(regEntity);
        //获取行业类型
        regOrder.setIndustryName(Optional.ofNullable(industryService.findById(regEntity.getIndustryId())).map(IndustryEntity::getIndustryName).orElse(null));
        //企业开票类目
        CompanyInvoiceCategoryEntity companyInvoiceCategoryEntity =new CompanyInvoiceCategoryEntity();
        companyInvoiceCategoryEntity.setOrderNo(orderNo);
        companyInvoiceCategoryEntity.setOemCode(orderEntity.getOemCode());
        List<CompanyInvoiceCategoryEntity> categoryList=companyInvoiceCategoryService.select(companyInvoiceCategoryEntity);

        regOrder.setInvoiceCategoryList(categoryList);
        //经营范围
        regOrder.setIdCardFront(ossService.getPrivateImgUrl(regOrder.getIdCardFront()));
        regOrder.setIdCardReverse(ossService.getPrivateImgUrl(regOrder.getIdCardReverse()));
        regOrder.setSignImg(ossService.getPrivateImgUrl(regOrder.getSignImg()));
        regOrder.setVideoAddr(ossService.getPrivateVideoUrl(regOrder.getVideoAddr()));
        regOrder.setIdCardAddr(regEntity.getIdCardAddr());

        if(regEntity.getCompanyType()!=1) {
            regOrder.setRegisteredCapital(regEntity.getRegisteredCapital());
        }
        //获取企业成员信息
        List<CompanyCorePersonnelVO> corePersonnelList = companyCorePersonnelService.getCompanyCorePersonnelByCompanyIdOrOrderNo(null,regOrder.getOrderNo());
        if(corePersonnelList!=null){
            regOrder.setShareholderInfoList(corePersonnelList);
        }else{
            regOrder.setShareholderInfoList(null);
        }
        map.put("regOrder", regOrder);
        //订单附件表
        getAttachment(orderEntity, 2, map);

        // 获取优惠券抵扣
        OrderQuery query  = new OrderQuery();
        query.setLikeOrderNo(orderNo);
        List<OpenOrderVO> list = orderService.listOpenOrder(query);
        //支付信息
        PaywaterVO paywaterVO = getPayWaterEntity(orderEntity);
        if(paywaterVO!=null) {
            // 打款凭证转换成https链接
            paywaterVO.setPayPic(ossService.getPrivateImgUrl(paywaterVO.getPayPic()));
        }
        PayOrderVO payOrderVO = new PayOrderVO(orderEntity, paywaterVO);
        if (orderEntity.getDiscountActivityId() != null){
            payOrderVO.setDiscountActivityId(orderEntity.getDiscountActivityId());
        }
        // 优惠券抵扣金额
        if (list != null && list.size()>0){
            if(list.get(0).getFaceAmount() != null){
                payOrderVO.setFaceAmount(list.get(0).getFaceAmount());
            }
        }
        map.put("payInfo", payOrderVO);

        //查询公司信息（获取营业执照）
        getBusinessInfo(orderEntity, regEntity, map);
        return ResultVo.Success(map);
    }

    /**
     * 获取营业执照
     * @param orderEntity
     * @param regEntity
     * @param map
     */
    private void getBusinessInfo(OrderEntity orderEntity, RegisterOrderEntity regEntity, Map<String, Object> map){
        Map<String, String> business = Maps.newHashMap();
        MemberCompanyEntity memberCompanyEntity = new MemberCompanyEntity();
        memberCompanyEntity.setOrderNo(orderEntity.getOrderNo());
        memberCompanyEntity.setOemCode(orderEntity.getOemCode());
        memberCompanyEntity = memberCompanyService.selectOne(memberCompanyEntity);
        //企业信息为空则通过用户id和注册名称查询
        if (memberCompanyEntity == null) {
            memberCompanyEntity = new MemberCompanyEntity();
            memberCompanyEntity.setMemberId(orderEntity.getUserId());
            memberCompanyEntity.setCompanyName(regEntity.getRegisteredName());
            List<MemberCompanyEntity> lists = memberCompanyService.select(memberCompanyEntity);
            if (CollectionUtil.isEmpty(lists)) {
                map.put("business", business);
                return;
            }
            memberCompanyEntity = lists.get(0);
        }
        if (StringUtils.isNotBlank(memberCompanyEntity.getBusinessLicense())) {
            business.put("businessLicense", ossService.getPrivateVideoUrl(memberCompanyEntity.getBusinessLicense()));
        }
        if (StringUtils.isNotBlank(memberCompanyEntity.getBusinessLicenseCopy())) {
            business.put("businessLicenseCopy", ossService.getPrivateVideoUrl(memberCompanyEntity.getBusinessLicenseCopy()));

        }
        map.put("business", business);
    }

    @ApiOperation(value="开票详情", notes="开票订单详情")
    @PostMapping("inv/detail")
    public ResultVo invDetail(@JsonParam String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(orderNo);
        orderEntity.setOemCode(userEntity.getOemCode());
        orderEntity.setOrderType(OrderTypeEnum.INVOICE.getValue());
        orderEntity = orderService.selectOne(orderEntity);
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }
        InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
        invEntity.setOrderNo(orderNo);
        invEntity.setOemCode(orderEntity.getOemCode());
        invEntity = invoiceOrderService.selectOne(invEntity);
        if (invEntity == null) {
            return ResultVo.Fail("开票订单不存在");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), orderEntity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(orderEntity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
        if (accEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        MemberCompanyEntity companyEntity = memberCompanyService.findById(invEntity.getCompanyId());
        if (companyEntity == null) {
            return ResultVo.Fail("我的企业不存在");
        }
        OemEntity oemEntity = oemService.getOem(orderEntity.getOemCode());
        if (oemEntity == null) {
            return ResultVo.Fail("机构不存在");
        }
        Map<String, Object> map = Maps.newHashMap();
        //基础信息
        OrderVO orderVO = new OrderVO(orderEntity, memberOrderRelaService.findById(orderEntity.getRelaId()), accEntity, companyEntity.getCompanyType());
        orderVO.setOemName(oemEntity.getOemName());
        //获取渠道名称
        if (accEntity.getChannelCode() != null){
            ChannelInfoEntity channelInfoEntity = new ChannelInfoEntity();
            channelInfoEntity.setChannelCode(accEntity.getChannelCode());
            channelInfoEntity = channelInfoService.selectOne(channelInfoEntity);
            if (channelInfoEntity.getChannelName() != null){
                orderVO.setChannelName(channelInfoEntity.getChannelName());
            }
        }
        //  获取邀请人id
        if (accEntity.getParentMemberId() != null){
            orderVO.setParentMemberId(accEntity.getParentMemberId());
            orderVO.setParentMemberAccount(accEntity.getParentMemberAccount());
        }
        //获取园区名称
        orderVO.setParkName(Optional.ofNullable(parkService.findById(orderEntity.getParkId())).map(ParkEntity::getParkName).orElse(null));
        map.put("orderInfo", orderVO);
        //开票信息
        InvoiceDetailOrderVO invOrder = new InvoiceDetailOrderVO(invEntity, companyEntity);
        //业务合同
        invOrder.setBusinessContractImgList(getOssImages(invEntity.getBusinessContractImgs()));
        //银行流水
        invOrder.setAccountStatementList(getOssImages(invEntity.getAccountStatement()));
        // 添加成果照片
        invOrder.setAchievementImgList(getOssImages(invEntity.getAchievementImgs()));
        // 添加成果视频
        if (StringUtil.isNotBlank(invEntity.getAchievementVideo())){
            invOrder.setAchievementVideo(ossService.getPrivateVideoUrl(invEntity.getAchievementVideo()));
        }
        map.put("invoice", invOrder);
        //支付信息
        PaywaterVO paywaterVO = getPayWaterEntity(orderEntity);
        if(paywaterVO!=null) {
            if(StringUtils.isNotBlank(paywaterVO.getPayPic())) {
                // 打款凭证转换成https链接
                paywaterVO.setPayPic(paywaterVO.getPayPic());
            }else{
                paywaterVO.setPayPic(null);
            }
            PayOrderVO payOrderVO = new PayOrderVO(orderEntity, paywaterVO, invEntity);
            if(companyEntity.getCompanyType()!=null && companyEntity.getCompanyType().intValue() !=1 && StringUtil.isNotBlank(invEntity.getPaymentVoucher())){
                // 打款凭证转换成https链接
                payOrderVO.setPayPic(invEntity.getPaymentVoucher());
            }
            if(StringUtils.isNotBlank(payOrderVO.getPayPic())){
                payOrderVO.setPayPicList(getOssImages(payOrderVO.getPayPic()));
            }
            map.put("payInfo", payOrderVO);
        }
        invOrder.setAttachmentImgList(getOssImages(invEntity.getInvoiceImgs()));
        return ResultVo.Success(map);
    }
    /**
     * 导出商品明细
     * @param orderNo
     * @return
     */
    @PostMapping("inv/detail/exportGoods")
    public ResultVo getGoodsDetail(@JsonParam String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        List<InvoiceOrderGoodsDetailVO> list = invoiceorderGoodsdetailRelaService.queryGoodsDetailByOrderNo(orderNo);
        if (CollectionUtil.isEmpty(list)){
            return ResultVo.Fail("无商品明细");
        }
        try {
            exportExcel(orderNo+"商品明细", "商品明细", InvoiceOrderGoodsDetailVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("商品明细导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
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
            list.add(ossService.getPrivateVideoUrl(s));
        }
        return list;
    }

    /**
     * 获取订单附件信息
     * @param orderEntity
     * @param orderType
     * @param map
     */
    public void getAttachment(OrderEntity orderEntity, Integer orderType, Map<String, Object> map) {
        OrderAttachmentEntity attEntity = new OrderAttachmentEntity();
        attEntity.setOrderNo(orderEntity.getOrderNo());
        attEntity.setOrderType(orderType);
        attEntity.setOemCode(orderEntity.getOemCode());
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
    }

    /**
     * 获取支付流水信息
     * @param orderEntity
     * @return
     */
    public PaywaterVO getPayWaterEntity(OrderEntity orderEntity) {
        PayWaterQuery payWaterQuery = new PayWaterQuery();
        payWaterQuery.setOemCode(orderEntity.getOemCode());
        payWaterQuery.setOrderNo(orderEntity.getOrderNo());
        payWaterQuery.setOrderType(orderEntity.getOrderType());
        payWaterQuery.setNotPayWaterType(PayWaterTypeEnum.REFUND.getValue());
        List<PaywaterVO> list = payWaterService.listPayWater(payWaterQuery);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
    /**
     * 批量下载开票信息弹框接口
     */
    @PostMapping("batch/invoice/popout")
    public ResultVo popout(@RequestBody OrderQuery query){
        UserEntity userEntity = userService.findById(getCurrUserId());
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
        List<BatchInvOrderExportVO> list = orderService.invBatchExportOrderList(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无订单导出");
        }
        Long parkId=list.get(0).getParkId();
        for (BatchInvOrderExportVO batchInvOrderExportVO :list ) {
            if(!parkId.equals(batchInvOrderExportVO.getParkId())){
                return ResultVo.Fail("包含不同园区订单不可下载，请调整查询条件后再试！");
            }
        }
        Map map=new HashMap();
        map.put("total",list.size());
        return ResultVo.Success(map);
    }

    /**
     * 批量下载开票信息接口
     */
    @PostMapping("batch/invoice")
    public ResultVo batchInvoice(@RequestBody OrderQuery query){
        UserEntity userEntity = userService.findById(getCurrUserId());
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
        List<BatchInvOrderExportVO> list = orderService.invBatchExportOrderList(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        Long parkId=list.get(0).getParkId();
        for (BatchInvOrderExportVO batchInvOrderExportVO :list ) {
            if(!parkId.equals(batchInvOrderExportVO.getParkId())){
                return ResultVo.Fail("包含不同园区订单不可下载，请调整查询条件后再试！");
            }
        }
        List<String> orderNo = new ArrayList<>();
        for (BatchInvOrderExportVO vo :list){
            orderNo.add(vo.getOrderNo());
        }
        List<InvoiceOrderGoodsDetailVO> goodsList = invoiceorderGoodsdetailRelaService.queryGoodsDetailByOrderNoList(orderNo);
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        ExportParams params = new ExportParams();
        params.setTitle("订单数据");
        params.setSheetName("订单数据");
        map.put("title",params);
        map.put("entity",BatchInvOrderExportVO.class);
        map.put("data",list);
        listMap.add(map);
        map = new HashMap<>();
        params = new ExportParams();
        params.setTitle("商品明细");
        params.setSheetName("商品明细");
        map.put("title",params);
        map.put("entity",InvoiceOrderGoodsDetailVO.class);
        map.put("data",goodsList);
        listMap.add(map);
        ParkEntity parkEntity=parkService.findById(parkId);
        try {
            //exportExcel(parkEntity.getParkName()+ DateUtil.format(new Date(),"yyyy-MM-dd"), parkEntity.getParkName()+ DateUtil.format(new Date(),"yyyy-MM-dd"), BatchInvOrderExportVO.class, list);
            exportExcel(parkEntity.getParkName() + DateUtil.format(new Date(), "yyyy-MM-dd"),listMap);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("批量下载开票信息导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     *
     * 批量下载办理信息弹框接口
     */
    @ApiOperation(value="批量下载办理信息", notes="批量下载办理信息")
    @PostMapping("batch/open/popout")
    public ResultVo openPopout(@RequestBody OrderQuery query){
        UserEntity userEntity = userService.findById(getCurrUserId());
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
        List<OpenOrderExportVO> list = orderService.getopenOrderExportList(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        Long parkId=list.get(0).getParkId();
        for (OpenOrderExportVO openOrderExportVO :list ) {
            if(!parkId.equals(openOrderExportVO.getParkId())){
                return ResultVo.Fail("包含不同园区订单不可下载，请调整查询条件后再试！");
            }
        }
        Map map=new HashMap();
        map.put("total",list.size());
        return ResultVo.Success(map);
    }

    /**
     * 批量下载办理信息接口
     */
    @PostMapping("batch/open/export")
    public ResultVo batchOpenExport(@RequestBody OrderQuery query){
        UserEntity userEntity = userService.findById(getCurrUserId());
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
        List<OpenOrderExportVO> list = orderService.getopenOrderExportList(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        Long parkId=list.get(0).getParkId();
         for (OpenOrderExportVO openOrderExportVO :list ) {
            if(!parkId.equals(openOrderExportVO.getParkId())){
                return ResultVo.Fail("包含不同园区订单不可下载，请调整查询条件后再试！");
            }
        }
        ParkEntity parkEntity=parkService.findById(parkId);
        if(parkEntity==null){
            return ResultVo.Fail("所属园区不存在");
        }
        List<String> orderNoList = new ArrayList<>();
        for (OpenOrderExportVO vo:list){
            orderNoList.add(vo.getOrderNo());
        }
        List<CompanyCorePersonnelExportVO>  companyCorePersonnelExportVOS = companyCorePersonnelService.getCompanyCorePersonnelByOrderNo(orderNoList);
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        ExportParams params = new ExportParams();
        params.setTitle("订单数据");
        params.setSheetName("订单数据");
        map.put("title",params);
        map.put("entity",OpenOrderExportVO.class);
        map.put("data",list);
        listMap.add(map);
        map = new HashMap<>();
        params = new ExportParams();
        params.setTitle("成员信息");
        params.setSheetName("成员信息");
        map.put("title",params);
        map.put("entity",CompanyCorePersonnelExportVO.class);
        map.put("data",companyCorePersonnelExportVOS);
        listMap.add(map);
        try {
            exportExcel(parkEntity.getParkName() + DateUtil.format(new Date(), "yyyy-MM-dd"),listMap);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("批量下载办理信息导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 下载办理协议
     * @return
     */
    @PostMapping("open/agreement/download")
    public ResultVo openAgreementDownload(@JsonParam String orderNo, @JsonParam String agentAccount) {
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (StringUtils.isBlank(agentAccount)) {
            return ResultVo.Fail("经办人账号不能为空");
        }
        CurrUser currUser = getCurrUser();
        OrderEntity orderEntity;
        try {
            orderEntity = getOrderEntityByAgreement(orderNo, currUser.getUserId());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        ParkAgentAccountEntity parkAgentAccountEntity = parkAgentAccountService.queryParkAgentAccountByAgentAccount(orderEntity.getParkId(), agentAccount, 1);
        if (parkAgentAccountEntity == null) {
            return ResultVo.Fail("经办人信息不存在");
        }
/*        ParkAgreementEntity parkAgreementEntity = new ParkAgreementEntity();
        parkAgreementEntity.setParkId(orderEntity.getParkId());
        parkAgreementEntity.setStatus(1);
        List<ParkAgreementEntity> agreements = this.parkAgreementService.select(parkAgreementEntity);*/
        List<ParkAgreementEntity> agreements = new ArrayList<>();
        List<AgreementTemplateInfoVO> list = parkAgreementTemplateRelaService.getTemplateInfo(orderEntity.getParkId(),3, orderEntity.getProductId());
        if (CollectionUtil.isNotEmpty(list)){
            for (AgreementTemplateInfoVO vo:list){
                ParkAgreementEntity entity = new ParkAgreementEntity();
                entity.setParkCode(vo.getParkCode());
                entity.setAgreementCode("park_agreement");
                entity.setAgreementName(vo.getTemplateName());
                entity.setAgreementTmplUrl(vo.getTemplateHtmlUrl());
                entity.setParkId(vo.getParkId());
                agreements.add(entity);
            }
        }
        if (CollectionUtil.isEmpty(agreements)) {
            return ResultVo.Fail("该园区暂不支持！");
        }
        RegisterOrderEntity regEntity = new RegisterOrderEntity();
        regEntity.setOrderNo(orderNo);
        regEntity.setOemCode(orderEntity.getOemCode());
        regEntity = registerOrderService.selectOne(regEntity);
        if (regEntity == null) {
            return ResultVo.Fail("开户订单不存在");
        }
        regEntity.setAgentAccount(agentAccount);
        regEntity.setUpdateTime(new Date());
        regEntity.setUpdateUser(currUser.getUseraccount());
        registerOrderService.editByIdSelective(regEntity);
        try {
            downloadZip(regEntity, orderEntity, agreements);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        return null;
    }

    /**
     * 下载zip压缩包
     * @param regEntity
     * @param orderEntity
     * @param agreements
     */
    private void downloadZip(RegisterOrderEntity regEntity, OrderEntity orderEntity, List<ParkAgreementEntity> agreements) {
        String baseDir = System.getProperty("java.io.tmpdir") + "agreement/" + regEntity.getOrderNo() + "_" + System.currentTimeMillis() + "/";
        String fileDir = baseDir + "file/";
        try {
            File tmpFilePath = new File(fileDir);
            if(!tmpFilePath.exists()) {
                tmpFilePath.mkdirs();
            }
            for (ParkAgreementEntity agreement : agreements) {
                //生成pdf文件
                downHtmlAndImg(regEntity, orderEntity, agreement, fileDir);
            }

            if (tmpFilePath.listFiles().length <= 0) {
                throw new BusinessException("暂无协议生成");
            }
            //生成zip压缩包
            HttpServletResponse response = getResponse();
            String zipTmp = baseDir + "zip/";
            tmpFilePath = new File(zipTmp);
            if(!tmpFilePath.exists()) {
                tmpFilePath.mkdirs();
            }
            String fileName = regEntity.getOperatorName() + "_" + DateUtil.formatDefaultDate(new Date()) +".zip";
            String zipFilePath = zipTmp + fileName;
            ZipUtil.zip(fileDir, zipFilePath);
            byte[] zipFileData = FileUtil.readBytes(zipFilePath);
            response.addHeader("filename", fileName);
            response.setContentType("application/zip");
            response.addHeader("Access-Control-Expose-Headers", "filename");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            OutputStream os = response.getOutputStream();
            os.write(zipFileData);
            os.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("PDF生成失败");
        } finally {
            File file = new File(baseDir);
            delFile(file);
        }
    }

    /**
     * 下载协议
     * @param regEntity
     * @param orderEntity
     * @param agreement
     * @param fileDir
     * @throws MalformedURLException
     */
    private void createPdf(RegisterOrderEntity regEntity, OrderEntity orderEntity, ParkAgreementEntity agreement, String fileDir) throws MalformedURLException {
        String privateBucket = Optional.ofNullable(dictionaryService.getByCode("oss_privateBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
        JSONObject json =  json = agreementTemplateByPark(regEntity, orderEntity);
/*        switch(agreement.getAgreementCode()){
            case "cluster_regist_agreement":
                //集群注册住所托管服务合同
                json = trusteeship(regEntity, orderEntity, privateBucket);
                break;
            case "acknowledgement":
                //承诺函
                json = letterOfCommitment(regEntity, orderEntity, privateBucket);
                break;
            case "company_regist_agent_certificate":
                //个体户开业登记申请书和委托代理人证明
                json = certificateOfEntrustedAgent(regEntity, orderEntity, privateBucket);
                break;
            case "company_name_pre_approval":
                //个体户名称预先核准申请书
                json = prePetition(regEntity, orderEntity, privateBucket);
                break;
            case "park_agreement":
                //园区办理协议
                json = agreementTemplateByPark(regEntity, orderEntity);
                break;
            default:
                return;
        }*/
        //获取签名时间
        getSignDate(orderEntity, json);
        String http = dictionaryService.getByCode("oss_req_head").getDictValue();
        String host = http + dictionaryService.getByCode("oss_access_public_host").getDictValue();
        byte[] temple = null;
       /* if (agreement.getAgreementCode().equals("park_agreement")){
            temple = HttpClientUtil.urlToByte( http+agreement.getAgreementTmplUrl());
        }else{
            temple = HttpClientUtil.urlToByte(host+ agreement.getAgreementTmplUrl());
        }*/
        PDFUtil.getPdfByHtmlContent(host+agreement.getAgreementTmplUrl(),json,fileDir,agreement.getAgreementName());

    }

    /**
     * 下载协议网页和图片
     * @param regEntity
     * @param orderEntity
     * @param agreement
     * @param fileDir
     * @throws MalformedURLException
     */
    private void downHtmlAndImg(RegisterOrderEntity regEntity, OrderEntity orderEntity, ParkAgreementEntity agreement, String fileDir) throws Exception {
        String privateBucket = Optional.ofNullable(dictionaryService.getByCode("oss_privateBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
        JSONObject json =  agreementTemplateByPark(regEntity, orderEntity);
        //获取签名时间
        getSignDate(orderEntity, json);
        String http = dictionaryService.getByCode("oss_req_head").getDictValue();
        String host = http + dictionaryService.getByCode("oss_access_public_host").getDictValue();

        //获取html的内容
        String htmlContent = HttpClientUtil.doGet(host+agreement.getAgreementTmplUrl(), null);
        //内容替换
        if(json!=null){
            String imgUrl = "";
            String localImgUrl = "";
            for(String key:json.keySet()){
                if(key == "idCardFront" || key == "idCardBack" || key == "signImg"
                        || key == "legalIdCardFront" || key == "legalIdCardReverse"
                        || key == "agentIdCardFront" || key == "agentIdCardBack" || key == "parkOfficialSealImg" || key == "oemOfficialSealImg"){
                    if("".equals(json.getString(key)) || null == json.getString(key)){
                        htmlContent = htmlContent.replaceAll("#"+key+"#","");
                    }else{
                        imgUrl = json.getString(key);
                        localImgUrl = imgUrl.substring(imgUrl.lastIndexOf("/")+1,imgUrl.lastIndexOf("?"));
                        OssUtil.downloadImgToLocal(imgUrl,fileDir+"images/", null);
                        htmlContent = htmlContent.replaceAll("#"+key+"#","<img src='images/"+localImgUrl+"' class='"+key+"'/>");
                    }
                }else{
                    if(json.containsKey(key) && json.get(key)==null){
                        htmlContent = htmlContent.replaceAll("#"+key+"#","");
                    }else {
                        htmlContent = htmlContent.replaceAll("#" + key + "#", json.getString(key));
                    }
                }
            }
        }
        File file = new File(fileDir+"/"+agreement.getAgreementName()+".html" );
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(htmlContent);
        bw.flush();
        bw.close();
        fw.close();
    }

    /**
     * 删除文件
     * @param file
     */
    private void delFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                delFile(file1);
            }
        }
        if (!file.delete()){
            throw new BusinessException("删除失败");
        }
    }

    /**
     * 个体户名称预先核准申请书
     * @param regEntity
     * @param orderEntity
     * @param privateBucket
     */
    private JSONObject prePetition(RegisterOrderEntity regEntity, OrderEntity orderEntity, String privateBucket) throws MalformedURLException {
        JSONObject json = new JSONObject();
        //获取行业类型
        if (StringUtils.isBlank(regEntity.getExampleName())) {
            Optional<IndustryEntity> industryEntity = Optional.ofNullable(industryService.findById(regEntity.getIndustryId()));
            regEntity.setExampleName(StringHandleUtil.removeStar(industryEntity.map(IndustryEntity::getExampleName).orElse(null)));
        }
        regEntity.setExampleName(StringHandleUtil.removeStar(regEntity.getExampleName()));
        //获取园区名字
        Optional<ParkEntity> parkEntity = Optional.ofNullable(parkService.findById(orderEntity.getParkId()));
        String parkCity = parkEntity.map(ParkEntity::getParkCity).orElse(null);

        json.put("registeredName", PDFUtil.getJson(regEntity.getRegisteredName(), "1"));
        if (StringUtils.isNotBlank(regEntity.getShopNameOne())) {
            json.put("registeredName1", PDFUtil.getJson(parkCity + regEntity.getShopNameOne() + regEntity.getExampleName(), "1"));
        }
        if (StringUtils.isNotBlank(regEntity.getShopNameTwo())) {
            json.put("registeredName2", PDFUtil.getJson(parkCity + regEntity.getShopNameTwo() + regEntity.getExampleName(), "1"));
        }
        json.put("operatorName", PDFUtil.getJson(regEntity.getOperatorName(), "1"));
        json.put("businessScope", PDFUtil.getJson(regEntity.getBusinessScope(), "1"));
        json.put("businessAddress",PDFUtil.getJson(regEntity.getBusinessAddress(), "1"));
        if (ossService.doesObjectExist(regEntity.getSignImg(), privateBucket)) {
            json.put("signImg", PDFUtil.getJson(ossService.getPrivateVideoUrl(regEntity.getSignImg()), "2"));
        }
        if (ossService.doesObjectExist(regEntity.getIdCardFront(), privateBucket)) {
            json.put("idCardFront", PDFUtil.getJson(ossService.getPrivateVideoUrl(regEntity.getIdCardFront()), "2"));
        }
        if (ossService.doesObjectExist(regEntity.getIdCardReverse(), privateBucket)) {
            json.put("idCardBack",  PDFUtil.getJson(ossService.getPrivateVideoUrl(regEntity.getIdCardReverse()), "2"));
        }
        return json;
    }

    private JSONObject agreementTemplateByPark(RegisterOrderEntity regEntity, OrderEntity orderEntity){
        AgreementTemplateSqlVO vo = agreementTemplateService.getTableInfo(orderEntity.getOrderNo(),orderEntity.getUserId(),null,orderEntity.getParkId(),orderEntity.getOemCode(),regEntity.getCompanyType());
        if(vo==null){
            return null;
        }
        if(StringUtil.isNotBlank(vo.getIdCardFront())){
            vo.setIdCardFront(ossService.getPrivateImgUrl(vo.getIdCardFront()));
        }
        if(StringUtil.isNotBlank(vo.getIdCardBack())){
            vo.setIdCardBack(ossService.getPrivateImgUrl(vo.getIdCardBack()));
        }
        if(StringUtil.isNotBlank(vo.getLegalIdCardFront())){
            vo.setLegalIdCardFront(ossService.getPrivateImgUrl(vo.getLegalIdCardFront()));
        }
        if(StringUtil.isNotBlank(vo.getLegalIdCardReverse())){
            vo.setLegalIdCardReverse(ossService.getPrivateImgUrl(vo.getLegalIdCardReverse()));
        }
        if(StringUtil.isNotBlank(vo.getAgentIdCardFront())){
            vo.setAgentIdCardFront(ossService.getPrivateImgUrl(vo.getAgentIdCardFront()));
        }
        if(StringUtil.isNotBlank(vo.getAgentIdCardBack())){
            vo.setAgentIdCardBack(ossService.getPrivateImgUrl(vo.getAgentIdCardBack()));
        }
        if(StringUtil.isNotBlank(vo.getSignImg())){
            vo.setSignImg(ossService.getPrivateImgUrl(vo.getSignImg()));
        }
        if(StringUtil.isNotBlank(vo.getOemOfficialSealImg())){
            vo.setOemOfficialSealImg(ossService.getPrivateImgUrl(vo.getOemOfficialSealImg()));
        }
        if(StringUtil.isNotBlank(vo.getParkOfficialSealImg())){
            vo.setParkOfficialSealImg(ossService.getPrivateImgUrl(vo.getParkOfficialSealImg()));
        }
        vo.setAgentStartTime(vo.getSignTime());
        if(StringUtil.isNotBlank(vo.getSignTime())) {
            Date signDate = DateUtil.parseDate(vo.getSignTime(), "yyyy年MM月dd日");
            Calendar date = Calendar.getInstance();
            date.setTime(signDate);
            date.add(Calendar.YEAR, 1);
            vo.setAgentEndTime(DateUtil.format(date.getTime(), "yyyy年MM月dd日"));
        }
        if (StringUtil.isNotBlank(vo.getLegalIdCardNumber())){
            vo.setLegalSex(IdCardConstraint.getChineseSex(vo.getLegalIdCardNumber()));
        }
        if (StringUtil.isNotBlank(vo.getIdCardNo())){
            vo.setUserSex(IdCardConstraint.getChineseSex(vo.getIdCardNo()));
        }
        JSONObject json = (JSONObject) JSONObject.toJSON(vo);
        return json;
    }

    /**
     * 承诺函
     * @param regEntity
     * @param orderEntity
     * @param privateBucket
     * @throws MalformedURLException
     */
    private JSONObject letterOfCommitment(RegisterOrderEntity regEntity, OrderEntity orderEntity, String privateBucket) throws MalformedURLException {
        JSONObject json = new JSONObject();
        json.put("registeredName", PDFUtil.getJson(regEntity.getRegisteredName(), "1"));
        if (ossService.doesObjectExist(regEntity.getSignImg(), privateBucket)) {
            json.put("signImg", PDFUtil.getJson(ossService.getPrivateVideoUrl(regEntity.getSignImg()), "2"));
        }
        return json;

    }

    /**
     * 个体户开业登记申请书和委托代理人证明
     * @param regEntity
     * @param orderEntity
     * @throws MalformedURLException
     */
    private JSONObject certificateOfEntrustedAgent(RegisterOrderEntity regEntity, OrderEntity orderEntity, String privateBucket) throws MalformedURLException{
        JSONObject json = new JSONObject();
        if (StringUtils.isBlank(regEntity.getExampleName())) {
            Optional<IndustryEntity> industryEntity = Optional.ofNullable(industryService.findById(regEntity.getIndustryId()));
            regEntity.setExampleName(StringHandleUtil.removeStar(industryEntity.map(IndustryEntity::getExampleName).orElse(null)));
        }
        regEntity.setExampleName(StringHandleUtil.removeStar(regEntity.getExampleName()));
        //获取园区名字
        Optional<ParkEntity> parkEntity = Optional.ofNullable(parkService.findById(orderEntity.getParkId()));
        String parkCity = parkEntity.map(ParkEntity::getParkCity).orElse(null);

        json.put("registeredName", PDFUtil.getJson(regEntity.getRegisteredName(), "1"));
        if (StringUtils.isNotBlank(regEntity.getShopNameOne())) {
            json.put("registeredName1", PDFUtil.getJson(parkCity + regEntity.getShopNameOne() + regEntity.getExampleName(), "1"));
        }
        if (StringUtils.isNotBlank(regEntity.getShopNameTwo())) {
            json.put("registeredName2", PDFUtil.getJson(parkCity + regEntity.getShopNameTwo() + regEntity.getExampleName(), "1"));
        }
        json.put("operatorName", PDFUtil.getJson(regEntity.getOperatorName(), "1"));
        json.put("businessScope", PDFUtil.getJson(regEntity.getBusinessScope(), "1"));
        json.put("idCardNumber", PDFUtil.getJson(regEntity.getIdCardNumber(), "1"));
        json.put("sex", PDFUtil.getJson(IdCardConstraint.getChineseSex(regEntity.getIdCardNumber()), "1"));
        if (ossService.doesObjectExist(regEntity.getSignImg(), privateBucket)) {
            json.put("signImg", PDFUtil.getJson(ossService.getPrivateVideoUrl(regEntity.getSignImg()), "2"));
        }
        json.put("idCardAddr", PDFUtil.getJson(regEntity.getIdCardAddr(), "1"));
        ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity = new ParkBusinessAddressRulesEntity();
        parkBusinessAddressRulesEntity.setParkId(orderEntity.getParkId());
        List<ParkBusinessAddressRulesEntity> rules = parkBusinessAddressRulesService.select(parkBusinessAddressRulesEntity);
        if (CollectionUtil.isNotEmpty(rules)) {
            String detailAddress = Optional.ofNullable(rules.get(0)).map(ParkBusinessAddressRulesEntity::getRegistPrefix).orElse("");
            detailAddress = regEntity.getBusinessAddress().replace(detailAddress, "");
            json.put("detailAddress", PDFUtil.getJson(detailAddress, "1"));
        }
        ParkAgentAccountEntity parkAgentAccountEntity = parkAgentAccountService.queryParkAgentAccountByAgentAccount(orderEntity.getParkId(), regEntity.getAgentAccount(), 1);
        if (parkAgentAccountEntity != null) {
            json.put("agentName", PDFUtil.getJson(parkAgentAccountEntity.getAgentName(), "1"));
            json.put("agentAccount", PDFUtil.getJson(parkAgentAccountEntity.getAgentAccount(), "1"));
            if (ossService.doesObjectExist(parkAgentAccountEntity.getIdCardFront(), privateBucket)) {
                json.put("agentIdCardFront", PDFUtil.getJson(ossService.getPrivateVideoUrl(parkAgentAccountEntity.getIdCardFront()), "2"));
            }
            if (ossService.doesObjectExist(parkAgentAccountEntity.getIdCardBack(), privateBucket)) {
                json.put("agentIdCardBack", PDFUtil.getJson(ossService.getPrivateVideoUrl(parkAgentAccountEntity.getIdCardBack()), "2"));
            }
        }
        return json;
    }

    /**
     * 集群注册住所托管服务合同
     * @param regEntity
     * @param orderEntity
     * @param privateBucket
     * @throws MalformedURLException
     */
    private JSONObject trusteeship(RegisterOrderEntity regEntity, OrderEntity orderEntity, String privateBucket) throws MalformedURLException{
        JSONObject json = new JSONObject();
        json.put("operatorName", PDFUtil.getJson(regEntity.getOperatorName(), "1"));
        json.put("idCardNumber", PDFUtil.getJson(regEntity.getIdCardNumber(), "1"));
        json.put("contactPhone", PDFUtil.getJson(regEntity.getContactPhone(), "1"));
        json.put("businessAddress", PDFUtil.getJson(regEntity.getBusinessAddress(), "1"));
        BigDecimal orderAmount = MoneyUtil.fen2yuan(new BigDecimal(orderEntity.getOrderAmount()));
        json.put("registerFeeUpper", PDFUtil.getJson(MoneyUtil.convertToChineseAmt(orderAmount), "1"));
        json.put("registerFee", PDFUtil.getJson(orderAmount.toString(), "1"));
        if (ossService.doesObjectExist(regEntity.getSignImg(), privateBucket)) {
            json.put("signImg", PDFUtil.getJson(ossService.getPrivateVideoUrl(regEntity.getSignImg()), "2"));
        }
        return json;
    }

    /**
     * 获取签名时间
     * @param orderEntity
     * @return
     */
    private void getSignDate(OrderEntity orderEntity, JSONObject json) {
        Integer orderStatus;
        //V2.8 根据园区流程标识判断是否需要校验身份验证
        Integer processMark = Optional.ofNullable(parkService.findById(orderEntity.getParkId())).map(ParkEntity::getProcessMark).orElseThrow(()->new BusinessException("园区信息不存在"));
        if (!Objects.equals(processMark, ParkProcessMarkEnum.VIDEO.getValue())) {
            orderStatus = RegOrderStatusEnum.TO_BE_PAY.getValue();
        } else {
            orderStatus = RegOrderStatusEnum.TO_BE_VIDEO.getValue();
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

    /**
     * 下载办理协议详情
     * @return
     */
    @PostMapping("open/agreement/detail")
    public ResultVo openAgreementDetail(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        OrderEntity orderEntity;
        try {
            orderEntity = getOrderEntityByAgreement(orderNo, currUser.getUserId());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        RegisterOrderEntity regEntity = new RegisterOrderEntity();
        regEntity.setOrderNo(orderNo);
        regEntity.setOemCode(orderEntity.getOemCode());
        regEntity = registerOrderService.selectOne(regEntity);
        if (regEntity == null) {
            return ResultVo.Fail("开户订单不存在");
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderNo);
        map.put("idCardFront", ossService.getPrivateImgUrl(regEntity.getIdCardFront()));
        map.put("idCardReverse", ossService.getPrivateImgUrl(regEntity.getIdCardReverse()));
        return ResultVo.Success(map);
    }

    /**
     * 保存经营者身份证照片
     * @param orderNo
     * @param image
     * @param type 1：身份证正面照，2：身份证反面照
     * @return
     */
    @PostMapping("open/save/idCardPic")
    public ResultVo openSaveIdCardPic(@JsonParam String orderNo, @JsonParam String image, @JsonParam Integer type) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (StringUtils.isBlank(image)) {
            return ResultVo.Fail("图片地址不能为空");
        }
        if (type == null || (!Objects.equals(type, 1) && !Objects.equals(type, 2))) {
            return ResultVo.Fail("类型有误");
        }
        String bucket = Optional.ofNullable(dictionaryService.getByCode("oss_privateBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
        if (!ossService.doesObjectExistPrivate(image)) {
            return ResultVo.Fail("照片在oss服务器不存在");
        }
        OrderEntity orderEntity;
        try {
            orderEntity = getOrderEntityByAgreement(orderNo, currUser.getUserId());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        RegisterOrderEntity regEntity = new RegisterOrderEntity();
        regEntity.setOrderNo(orderNo);
        regEntity.setOemCode(orderEntity.getOemCode());
        regEntity = registerOrderService.selectOne(regEntity);
        if (regEntity == null) {
            return ResultVo.Fail("开户订单不存在");
        }
        String remark;
        if (Objects.equals(type, 1)) {
            remark = "修改了经营者身份证正面图片，查看修改前图片|" + regEntity.getIdCardFront();
            regEntity.setIdCardFront(image);
        } else {
            remark = "修改了经营者身份证反面面图片，查看修改前图片|" + regEntity.getIdCardReverse();
            regEntity.setIdCardReverse(image);
        }
        registerOrderService.editAndSaveHistory(regEntity, orderEntity.getOrderStatus(), currUser.getUseraccount(), remark);
        return ResultVo.Success();
    }

    /**
     * 获取下载协议订单实体
     * @param orderNo
     * @param userId
     * @return
     * @throws BusinessException
     */
    private OrderEntity getOrderEntityByAgreement(String orderNo, Long userId) throws BusinessException {
        UserEntity userEntity = userService.findById(userId);
        if (userEntity == null) {
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(orderNo);
        orderEntity.setOemCode(userEntity.getOemCode());
        orderEntity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        orderEntity = orderService.selectOne(orderEntity);
        if (orderEntity == null) {
            throw new BusinessException("订单不存在");
        }
        if (orderEntity.getParkId() == null) {
            throw new BusinessException("当前订单没有归属园区");
        }
        ParkEntity parkEntity = parkService.findById(orderEntity.getParkId());
        if (parkEntity == null) {
            throw new BusinessException("当前订单归属园区不存在");
        }
        // 查询注册订单
        RegisterOrderEntity regOrder = Optional.ofNullable(registerOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到注册订单"));
        // 查询园区产品注册流程
        ProductParkRelaEntity productParkRelaEntity = new ProductParkRelaEntity();
        productParkRelaEntity.setParkId(orderEntity.getParkId());
        productParkRelaEntity.setProductId(orderEntity.getProductId());
        productParkRelaEntity.setCompanyType(regOrder.getCompanyType());
        ProductParkRelaEntity relaEntity = productParkRelaService.selectOne(productParkRelaEntity);
        if (null == relaEntity) {
            throw new BusinessException("未查询到园区产品注册流程");
        }
        if (Objects.equals(ParkProcessMarkEnum.SIGN.getValue(), relaEntity.getProcessMark())) {
            if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.REGISTRATION_UNDER_WAY.getValue())
                    &&!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue())
            ) {
                throw new BusinessException("只有待设立登记和待领证状态可以操作");
            }
        } else {
            if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.TO_BE_SURE.getValue())
                    &&!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue())
            ) {
                throw new BusinessException("只有待客服确认和待领证状态可以操作");
            }
        }

        /*ParkAgreementEntity parkAgreementEntity = new ParkAgreementEntity();
        parkAgreementEntity.setParkId(orderEntity.getParkId());
        parkAgreementEntity.setStatus(1);
        List<ParkAgreementEntity> agreements = this.parkAgreementService.select(parkAgreementEntity);*/
        List<AgreementTemplateInfoVO> agreements = parkAgreementTemplateRelaService.getTemplateInfo(orderEntity.getParkId(),3, orderEntity.getProductId());
        if (CollectionUtil.isEmpty(agreements)) {
            throw new BusinessException("该园区暂不支持！");
        }
        return orderEntity;
    }

    /**
     * 公户提现订单分页查询
     * @param query
     * @return
     */
    @PostMapping("corporate/withdraw/page")
    public ResultVo corporateWithdrawPage(@RequestBody CorporateAccountWithdrawOrderQuery query){
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
        PageInfo<CorporateAccountWithdrawOrderVO> page = corporateAccountWithdrawalOrderService.listPageCorporateWithdrawOrder(query);
        return ResultVo.Success(page);
    }

    /**
     * 公户提现订单导出
     * @param query
     * @return
     */
    @PostMapping("corporate/withdraw/export")
    public ResultVo corporateWithdrawExport(@RequestBody CorporateAccountWithdrawOrderQuery query) {
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
        List<CorporateAccountWithdrawOrderVO> lists = corporateAccountWithdrawalOrderService.listCorporateWithdrawOrder(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("公户提现订单", "公户提现订单", CorporateAccountWithdrawOrderVO.class, lists);
            return null;
        } catch (Exception e) {
            log.error("公户提现订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 退快递费
     * @param orderNo
     * @return
     */
    @PostMapping("inv/refund/postage/fee")
    public ResultVo invRefundPostageFee(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(OrderTypeEnum.INVOICE.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        //待发货、出库中、已发货三种状态的订单显示“退快递费”操作。
        if (!Objects.equals(entity.getOrderStatus(), InvoiceOrderStatusEnum.TO_BE_SHIPPED.getValue())
            && !Objects.equals(entity.getOrderStatus(), InvoiceOrderStatusEnum.OUT_OF_STOCK.getValue())
            && !Objects.equals(entity.getOrderStatus(), InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue())) {
            return ResultVo.Fail("当前订单状态不允许退快递费");
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                return ResultVo.Fail("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                return ResultVo.Fail("订单不属于当前登录用户组织");
            }
        }
        //开票
        InvoiceOrderEntity invEntity = new InvoiceOrderEntity();
        invEntity.setOemCode(entity.getOemCode());
        invEntity.setOrderNo(entity.getOrderNo());
        invEntity = invoiceOrderService.selectOne(invEntity);
        if (invEntity == null) {
            return ResultVo.Fail("开票订单不存在");
        }
        if (invEntity.getPostageFees() <= 0) {
            return ResultVo.Fail("快递费不大于0无需退款");
        }
        String time = (System.currentTimeMillis() + 300000 )+ ""; // redis标识值
        boolean flag = redisService.lock(RedisKey.INVOICE_ORDER_REFUND_POSTAGE_FEE_REDIS_KEY + orderNo, time,60);
        if(!flag){
            return ResultVo.Fail("请勿重复提交！");
        }
        try {
            invEntity.setIsRefundPostageFee(RefundPostageFeeEnum.REFUNDED.getValue());
            invoiceOrderService.refundPostageFee(invEntity, entity, currUser.getUseraccount());
        } catch (Exception e) {
            log.error("退邮寄费是吧");
            log.error(e.getMessage(), e);
        } finally {
            redisService.unlock(RedisKey.INVOICE_ORDER_REFUND_POSTAGE_FEE_REDIS_KEY + orderNo, time);
        }
        return ResultVo.Success();
    }

    /**
     * 确认登记
     * @param orderNo
     * @return
     */
    @PostMapping("open/register/confirm")
    public ResultVo openRegisterConfirm(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        OrderEntity orderEntity = validateOrder(currUser.getUserId(), orderNo, OrderTypeEnum.REGISTER.getValue());
        //待设立登记允许确认登记操作
        if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.REGISTRATION_UNDER_WAY.getValue())) {
            return ResultVo.Fail("当前订单状态不允许确认登记");
        }
        registerOrderService.openRegisterConfirm(orderEntity, currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 提交签名
     * @param orderNo
     * @return
     */
    @PostMapping("open/submit/sign")
    public ResultVo openSubmitSign(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        OrderEntity orderEntity = validateOrder(currUser.getUserId(), orderNo, OrderTypeEnum.REGISTER.getValue());
        //待提交签名允许提交签名操作
        if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue())) {
            return ResultVo.Fail("当前订单状态不允许提交签名");
        }

        //编辑企业注册订单和历史记录
        registerOrderService.openSubmitSign(orderEntity, RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue(), currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 确认已申报
     * @param orderNo
     * @param type 1:是，2：否
     * @param remark
     * @return
     */
    @PostMapping("open/declare/confirm")
    public ResultVo openDeclareConfirm(@JsonParam String orderNo, @JsonParam Integer type, @JsonParam String remark) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (type == null) {
            return ResultVo.Fail("请选择用户是否已签名");
        }
        if (type < 1 || type > 2) {
            return ResultVo.Fail("操作有误");
        }
        Integer orderStatus;
        if (Objects.equals(type, 2)) {
            if (StringUtils.isBlank(remark)) {
                return ResultVo.Fail("请填写备注说明");
            }
            if (remark.length() > 50) {
                return ResultVo.Fail("备注说明最多输入50个汉字");
            }
            orderStatus = RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue();
        } else {
            orderStatus = RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue();
        }
        OrderEntity orderEntity = validateOrder(currUser.getUserId(), orderNo, OrderTypeEnum.REGISTER.getValue());
        //签名待确认允许确认已申报
        if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.SIGNATURE_CONFIRMATION.getValue())) {
            return ResultVo.Fail("当前订单状态不允许确认已申报");
        }
        registerOrderService.openDeclareConfirm(orderEntity, orderStatus, remark, currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 校验订单
     * @param userId
     * @param orderNo
     * @param orderType
     * @return
     */
    public OrderEntity validateOrder(Long userId, String orderNo, Integer orderType) {
        UserEntity userEntity = userService.findById(userId);
        if (userEntity == null) {
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setOemCode(userEntity.getOemCode());
        entity.setOrderType(orderType);
        entity = orderService.selectOne(entity);
        if (entity == null) {
            throw new BusinessException("订单不存在");
        }

        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                throw new BusinessException("订单不属于当前园区");
            }
        } else {
            boolean belongAdmin = belongAdmin(entity.getUserId(), getOrgTree(), 5);
            if (belongAdmin) {
                throw new BusinessException("订单不属于当前登录用户组织");
            }
        }
        return entity;
    }

    /**
     * 查看开票订单照片
     * @param orderNo 订单号
     * @returnorder
     */
    @PostMapping("invoiceOrderPicView")
    public ResultVo invoiceOrderPicView(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
        if(invoiceOrderEntity == null){
            return ResultVo.Fail("未找到开票订单数据");
        }
        //开票信息
        InvoiceDetailOrderVO invOrder = new InvoiceDetailOrderVO();
        invOrder.setOrderNo(invoiceOrderEntity.getOrderNo());
        //业务合同
        invOrder.setBusinessContractImgs(invoiceOrderEntity.getBusinessContractImgs());
        invOrder.setBusinessContractImgList(getOssImages(invoiceOrderEntity.getBusinessContractImgs()));
        //银行流水
        invOrder.setAccountStatement(invoiceOrderEntity.getAccountStatement());
        invOrder.setAccountStatementList(getOssImages(invoiceOrderEntity.getAccountStatement()));
        //发票图片
        invOrder.setInvoiceImgs(invoiceOrderEntity.getInvoiceImgs());
        invOrder.setInvoiceImgsList(getOssImages(invoiceOrderEntity.getInvoiceImgs()));
        return ResultVo.Success(invOrder);
    }

    /**
     * 编辑开票订单照片
     * @param orderNo 订单号
     * @param images 图片地址
     * @param type 1：银行流水，2：业务合同 3：发票图片
     * @param remark 备注
     * @returnorder
     */
    @PostMapping("edit/invoiceOrderPic")
    public ResultVo editInvoiceOrderPic(@JsonParam String orderNo, @JsonParam String images, @JsonParam Integer type, @JsonParam String remark) {
        CurrUser currUser = getCurrUser();
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        if(StringUtil.isBlank(images)){
            return ResultVo.Fail("编辑的图片不能为空");
        }
        if(type == null){
            return ResultVo.Fail("操作类型不能为空");
        }
        invoiceOrderService.editInvoiceOrderPic(orderNo,images,type,remark,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 上传风险承诺函
     * @param orderNo 订单号
     * @param images 图片地址
     * @param remark 备注
     * @returnorder
     */
    @PostMapping("uploadRiskCommitment")
    public ResultVo uploadRiskCommitment(@JsonParam String orderNo, @JsonParam String images, @JsonParam String remark) {
        CurrUser currUser = getCurrUser();
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        if(StringUtil.isBlank(images)){
            return ResultVo.Fail("编辑的图片不能为空");
        }
        invoiceOrderService.uploadRiskCommitment(orderNo,images,remark,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 查询增值税率列表
     * @return ResultVo<List<TaxRulesVatRateVO>>
     */
    @ApiOperation("查询增值税率列表")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping("/getVatRateList")
    public ResultVo<List<TaxRulesVatRateVO>> getVatRateList(@RequestBody TaxRulesVatRateQuery query) {

            List<TaxRulesVatRateVO> vatRateList = invoiceOrderService.getVatRateList(query);
        return ResultVo.Success(vatRateList);
    }

    /**
     * 作废红冲信息
     */
    @PostMapping("toVoidInfo")
    public ResultVo toVoidInfo(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if (orderNo == null){
            return ResultVo.Fail("订单号不能为空");
        }
        OrderVoidInfo orderVoidInfo =  orderService.checkToVoid(orderNo);
        if (orderVoidInfo == null){
            return ResultVo.Fail("订单信息错误");
        }
        return ResultVo.Success(orderVoidInfo);
    }

    /**
     *作废/红冲
     * @param orderVoidInfo
     * @return
     */
    @PostMapping("cancellation")
    public ResultVo cancellation(@RequestBody @Validated OrderVoidInfo orderVoidInfo, BindingResult results){
        CurrUser currUser = getCurrUser();
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        if (orderVoidInfo.getOrderNo() == null){
            return ResultVo.Fail("订单号不能为空");
        }
        Map<String,Object> map = invoiceOrderService.cancellation(orderVoidInfo,currUser.getUseraccount());
        return ResultVo.Success(map);
    }

    /**
     * 开票取消弹窗校验
     * @param orderNo
     * @return
     */
    @PostMapping("cancelNotice")
    public ResultVo cancelNotice(@JsonParam String orderNo){
        InvoiceOrderEntity entity = invoiceOrderService.queryByOrderNo(orderNo);
        if (entity == null){
            return ResultVo.Fail("订单编号错误");
        }
        List<Long> companyIds = new ArrayList<>();
        companyIds.add(entity.getCompanyId());
        List<InvoiceOrderEntity> list = invoiceOrderService.findExistPendingOrder(companyIds, entity.getAddTime(),null);
        Map<String,Object> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(list)){
            map.put("type",2);
            map.put("content","该个体户存在后续创建的开票订单,取消当前订单可能会造成服务费少收,请联系客户补缴，或让客户取消未付款订单后再操作。");
        }
        return ResultVo.Success(map);
    }

    /**
     * 获取风险承诺函或作废/红冲凭证
     * @param orderNo
     * @param type
     * @return
     */
    @PostMapping("getCancellationVoucherOrRiskCommitment")
    public ResultVo getCancellationVoucherOrRiskCommitment(@JsonParam String orderNo,@JsonParam Integer type){
        InvoiceOrderEntity entity = invoiceOrderService.queryByOrderNo(orderNo);
        List<String> imgUrl = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        if (type != null && type == 1){
            imgUrl = getOssImages(entity.getRiskCommitment());
            map.put("longUrl",imgUrl);
            map.put("shortUrl",entity.getRiskCommitment());
        }else if (type != null && type == 2){
            imgUrl = getOssImages(entity.getCancellationVoucher());
            map.put("longUrl",imgUrl);
            if (StringUtil.isEmpty(entity.getCancellationVoucher())){
                map.put("shortUrl","");
            }else{
                map.put("shortUrl",entity.getCancellationVoucher());
            }
        }else{
            return ResultVo.Fail("参数错误");
        }
        return ResultVo.Success(map);
    }

    /**
     * 取消注销订单校验
     * @param orderNo
     * @return
     */
    @PostMapping("cancelOrderCheck")
    public ResultVo cancelOrderCheck(@JsonParam String orderNo){
        if (StringUtil.isEmpty(orderNo)){
            return ResultVo.Fail("注销订单号不能为空");
        }
        CompanyCancelOrderEntity companyCancelEntity = new CompanyCancelOrderEntity();
        companyCancelEntity.setOrderNo(orderNo);
        companyCancelEntity = companyCancelOrderService.selectOne(companyCancelEntity);
        if(companyCancelEntity.getCompanyId()==null){
            return ResultVo.Fail("请选择注销订单");
        }
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(companyCancelEntity.getCompanyId());
        if (null == company) {
            return ResultVo.Fail("未查询到企业信息");
        }
        Map<String,Object> map = new HashMap<>();
        ParkEntity parkEntity = parkService.findById(company.getParkId());

        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(company.getEin());
        query.setCompanyId(companyCancelEntity.getCompanyId());
        query.setRange(1);
        query.setStatusRange(4);
        List<PendingTaxBillVO> pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            map.put("type",1);
            map.put("content","该企业当前所在税期的税单已申报完成，不能取消订单！");
            return  ResultVo.Success(map);
        }
        if (parkEntity != null && parkEntity.getIncomeLevyType() != null && parkEntity.getIncomeLevyType().equals(1)){
            query = new PendingTaxBillQuery();
            query.setEin(company.getEin());
            query.setCompanyId(companyCancelEntity.getCompanyId());
            query.setRange(1);
            pendingTaxBillList = new ArrayList<>();
            pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
            if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
                map.put("type",2);
                map.put("content","该企业存在当前税期的税单，若取消订单，税单将作废，请确定是否继续？");
            }
        }
       return ResultVo.Success(map);
    }

    /**
     * 下载成员证件
     * @return
     */
    @PostMapping("/downPersonnelCertificate")
    public ResultVo downPersonnelCertificate(@JsonParam String orderNo) {
        if (StringUtil.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }

        // 查询订单
        OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        // 校验订单状态
        if (!(OrderTypeEnum.REGISTER.getValue()==order.getOrderType()&&RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue().equals(order.getOrderStatus()))) {
            return ResultVo.Fail("订单状态不正确");
        }

        // 查询注册订单
        RegisterOrderEntity regOrder = Optional.ofNullable(registerOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到注册订单信息"));

        // 获取下载对象数据
        Map<String, String> map = Maps.newHashMap();
        if (StringUtil.isNotBlank(regOrder.getIdCardFront())) {
            map.put("法人_" + regOrder.getOperatorName() + "_1" + regOrder.getIdCardFront().substring(regOrder.getIdCardFront().lastIndexOf(".")), ossService.getPrivateImgUrl(regOrder.getIdCardFront()));
        }
        if (StringUtil.isNotBlank(regOrder.getIdCardReverse())) {
            map.put("法人_" + regOrder.getOperatorName() + "_2" + regOrder.getIdCardReverse().substring(regOrder.getIdCardReverse().lastIndexOf(".")), ossService.getPrivateImgUrl(regOrder.getIdCardReverse()));
        }

        // 查询核心成员信息
        CompanyCorePersonnelEntity companyCorePersonnelEntity = new CompanyCorePersonnelEntity();
        companyCorePersonnelEntity.setOrderNo(regOrder.getOrderNo());
        List<CompanyCorePersonnelEntity> list = companyCorePersonnelService.select(companyCorePersonnelEntity);
        if (CollectionUtil.isNotEmpty(list)) {
            for (CompanyCorePersonnelEntity entity : list) {
                if (StringUtil.isNotBlank(entity.getBusinessLicense())) {
                    map.put(entity.getPersonnelName() + "_1" + entity.getBusinessLicense().substring(entity.getBusinessLicense().lastIndexOf(".")),
                            ossService.getPrivateImgUrl(entity.getBusinessLicense()));
                }
                if (StringUtil.isNotBlank(entity.getIdCardFront())) {
                    map.put(entity.getPersonnelName() + "_1" + entity.getIdCardFront().substring(entity.getIdCardFront().lastIndexOf(".")),
                            ossService.getPrivateImgUrl(entity.getIdCardFront()));
                }
                if (StringUtil.isNotBlank(entity.getIdCardReverse())) {
                    map.put(entity.getPersonnelName() + "_2" + entity.getIdCardReverse().substring(entity.getIdCardReverse().lastIndexOf(".")),
                            ossService.getPrivateImgUrl(entity.getIdCardReverse()));
                }
            }
        }

        // 下载图片
        String baseDir = System.getProperty("java.io.tmpdir") + "personnelCertificate/" + orderNo + "_" + System.currentTimeMillis() + "/";
        String fileDir = baseDir + "file/";
        try {
            File tmpFilePath = new File(fileDir);
            if(!tmpFilePath.exists()) {
                tmpFilePath.mkdirs();
            }

            //生成pdf文件
            JSONObject json = (JSONObject) JSONObject.toJSON(map);

            String imgUrl = "";
            for(String key:json.keySet()){
                imgUrl = json.getString(key);
                OssUtil.downloadImgToLocal(imgUrl,fileDir, key);
            }

            if (tmpFilePath.listFiles().length <= 0) {
                return ResultVo.Fail("无数据");
            }
            //生成zip压缩包
            HttpServletResponse response = getResponse();
            String zipTmp = baseDir + "zip/";
            tmpFilePath = new File(zipTmp);
            if(!tmpFilePath.exists()) {
                tmpFilePath.mkdirs();
            }
            String fileName = regOrder.getOperatorName() + "_" + DateUtil.formatDefaultDate(new Date()) +".zip";
            String zipFilePath = zipTmp + fileName;
            ZipUtil.zip(fileDir, zipFilePath);
            byte[] zipFileData = FileUtil.readBytes(zipFilePath);
            response.addHeader("filename", fileName);
            response.setContentType("application/zip");
            response.addHeader("Access-Control-Expose-Headers", "filename");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            OutputStream os = response.getOutputStream();
            os.write(zipFileData);
            os.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("下载失败");
        } finally {
            File file = new File(baseDir);
            delFile(file);
        }
        return ResultVo.Success();
    }

    /**
     * 开票订单证据下载
     * @return
     */
    @PostMapping("/downInvoiceOrderEvidence")
    public ResultVo downInvoiceOrderEvidence(@JsonParam String orderNo) {
        if (StringUtil.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        // 查询订单
        OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        // 校验订单状态
        if (!(OrderTypeEnum.INVOICE.getValue()==order.getOrderType()&&InvoiceOrderStatusEnum.SIGNED.getValue().equals(order.getOrderStatus()))) {
            return ResultVo.Fail("订单状态不正确");
        }
        // 查询开票订单
        InvoiceOrderEntity invoiceOrderEntity = Optional.ofNullable(invoiceOrderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到开票订单信息"));

        // 获取下载对象数据
        Map<String, String> map = Maps.newHashMap();
        //业务合同
        if(StringUtils.isNotBlank(invoiceOrderEntity.getBusinessContractImgs())){
           String[] imgs = invoiceOrderEntity.getBusinessContractImgs().split(",");
           for (int i=0;i<imgs.length;i++){
               map.put("业务合同_"+(i+1)+imgs[i].substring(imgs[i].lastIndexOf(".")),ossService.getPrivateImgUrl(imgs[i]));
           }
        }
        //银行流水
        if(StringUtils.isNotBlank(invoiceOrderEntity.getAccountStatement())){
            String[] imgs = invoiceOrderEntity.getAccountStatement().split(",");
            for (int i=0;i<imgs.length;i++){
                map.put("银行流水_"+(i+1)+imgs[i].substring(imgs[i].lastIndexOf(".")),ossService.getPrivateImgUrl(imgs[i]));
            }
        }
        //成果图片
        if(StringUtils.isNotBlank(invoiceOrderEntity.getAchievementImgs())){
            String[] imgs = invoiceOrderEntity.getAchievementImgs().split(",");
            for (int i=0;i<imgs.length;i++){
                map.put("成果图片_"+(i+1)+imgs[i].substring(imgs[i].lastIndexOf(".")),ossService.getPrivateImgUrl(imgs[i]));
            }
        }
        //发票
        if(StringUtils.isNotBlank(invoiceOrderEntity.getInvoiceImgs())){
            String[] imgs = invoiceOrderEntity.getInvoiceImgs().split(",");
            for (int i=0;i<imgs.length;i++){
                map.put("发票_"+(i+1)+imgs[i].substring(imgs[i].lastIndexOf(".")),ossService.getPrivateImgUrl(imgs[i]));
            }
        }

        // 下载图片
        String baseDir = System.getProperty("java.io.tmpdir") + "invoiceorder/" + orderNo + "_" + System.currentTimeMillis() + "/";
        String fileDir = baseDir + "file/";
        try {
            File tmpFilePath = new File(fileDir);
            if(!tmpFilePath.exists()) {
                tmpFilePath.mkdirs();
            }
            //下载图片
            JSONObject json = (JSONObject) JSONObject.toJSON(map);
            String imgUrl = "";
            for(String key:json.keySet()){
                imgUrl = json.getString(key);
                OssUtil.downloadImgToLocal(imgUrl,fileDir, key);
            }
            if (tmpFilePath.listFiles().length <= 0) {
                return ResultVo.Fail("无数据");
            }
            //生成zip压缩包
            HttpServletResponse response = getResponse();
            String zipTmp = baseDir + "zip/";
            tmpFilePath = new File(zipTmp);
            if(!tmpFilePath.exists()) {
                tmpFilePath.mkdirs();
            }
            String fileName = invoiceOrderEntity.getOrderNo() + "_" + DateUtil.formatDefaultDate(new Date()) +".zip";
            String zipFilePath = zipTmp + fileName;
            ZipUtil.zip(fileDir, zipFilePath);
            byte[] zipFileData = FileUtil.readBytes(zipFilePath);
            response.addHeader("filename", fileName);
            response.setContentType("application/zip");
            response.addHeader("Access-Control-Expose-Headers", "filename");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            OutputStream os = response.getOutputStream();
            os.write(zipFileData);
            os.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("下载失败");
        } finally {
            File file = new File(baseDir);
            delFile(file);
        }
        return ResultVo.Success();
    }

    /**
     * 注册订单退款到消费钱包
     */
    @ApiOperation("注册订单退款到消费钱包")
    @PostMapping("regOrderRefund2Wallet")
    public ResultVo regOrderRefund2Wallet(@JsonParam String orderNo) {
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空！");
        }
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if (null == orderEntity) {
            return ResultVo.Fail("未查询到订单信息");
        }
        if(orderEntity.getOrderType()!=5){
            return ResultVo.Fail("订单类型错误，不支持退回至消费钱包");
        }else if(!(2==orderEntity.getOrderStatus() || 6==orderEntity.getOrderStatus() || 7==orderEntity.getOrderStatus())){
            return ResultVo.Fail("注册订单状态错误，退款失败");
        }
        PayWaterEntity payEntity = new PayWaterEntity();
        payEntity.setOrderNo(orderNo);
        payEntity.setPayWay(PayWayEnum.WECHATPAY.getValue());
        payEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        PayWaterEntity entity = payWaterService.selectOne(payEntity);
        orderService.refund(orderEntity, getCurrUseraccount(), new Date(),2, true);
        // 修改支付流水退款状态
        entity.setRefundStatus(RefundWaterStatusEnum.REFUND_SUCCESS.getValue());
        payWaterService.editByIdSelective(entity);

        return ResultVo.Success();
    }

    /**
     * 开票订单待财务审核详情
     */
    @ApiOperation("开票订单待财务审核详情")
    @PostMapping("gotoInvoicePaymentReviewInfo")
    public ResultVo gotoInvoicePaymentReviewInfo(@JsonParam String orderNo) {
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空！");
        }
        InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
        if (null == invoiceOrderEntity) {
            return ResultVo.Fail("未查询到订单信息");
        }
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if(orderEntity==null){
            return ResultVo.Fail("未查询到订单信息");
        }else if(orderEntity.getOrderStatus()!=11){
            return ResultVo.Fail("订单状态错误，只有待财务审核状态才可以进行操作");
        }
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceOrderEntity.getCompanyId());
        if(memberCompanyEntity==null){
            return ResultVo.Fail("未查询到企业信息信息");
        }
        InvoiceOrderPaymentReviewInfoVO vo = new InvoiceOrderPaymentReviewInfoVO();
        vo.setCompanyName(memberCompanyEntity.getCompanyName());
        vo.setOrderNo(invoiceOrderEntity.getOrderNo());
        vo.setInvoiceAmount(orderEntity.getOrderAmount());
        vo.setPayAmount(orderEntity.getPayAmount());
        vo.setPaymentVoucher(invoiceOrderEntity.getPaymentVoucher());
        if(StringUtil.isNotBlank(invoiceOrderEntity.getPaymentVoucher())){
            List<String> paymentVoucherList = new ArrayList<>();
            String[] images = invoiceOrderEntity.getPaymentVoucher().split(",");
            for (String img : images) {
                paymentVoucherList.add(ossService.getPrivateImgUrl(img));
            }
            vo.setPaymentVoucherList(paymentVoucherList);
        }
        return ResultVo.Success(vo);
    }

    /**
     * 开票订单待财务审核
     */
    @ApiOperation("开票订单待财务审核")
    @PostMapping("invoicePaymentReview")
    public ResultVo invoicePaymentReview(@RequestBody JSONObject jsonObject) {
        CurrUser user = getCurrUser();
        if (jsonObject == null) {
            return ResultVo.Fail("参数不能为空！");
        }
        String orderNo = jsonObject.getString("orderNo");
        Integer auditResult = jsonObject.getInteger("auditResult");
        String receiptPaymentVoucher = jsonObject.getString("receiptPaymentVoucher");
        String errorRemark = jsonObject.getString("errorRemark");
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空！");
        }
        if(auditResult == null){
            return ResultVo.Fail("审核结果不能为空！");
        }
        if(auditResult != 1 && auditResult != 2){
            return ResultVo.Fail("审核结果错误！");
        } if(auditResult == 1 && StringUtil.isBlank(receiptPaymentVoucher)){
             return ResultVo.Fail("收款凭证不能为空！");
        }else if(auditResult == 2 && StringUtil.isBlank(errorRemark)){
            return ResultVo.Fail("不通过原因不能为空！");
        }
        invoiceOrderService.invoicePaymentReview(orderNo,auditResult,receiptPaymentVoucher,errorRemark,user.getUseraccount());
        return ResultVo.Success();
    }
}
