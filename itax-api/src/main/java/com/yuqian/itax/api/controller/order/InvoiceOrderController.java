package com.yuqian.itax.api.controller.order;

import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.query.InvOrderQuery;
import com.yuqian.itax.order.entity.query.InvWaterOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.ChannelPushStateEnum;
import com.yuqian.itax.order.enums.InvoiceMarkEnum;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.service.CompanyCancelOrderService;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.entity.query.TaxRulesVatRateQuery;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

/**
 *  @Description: 开票订单控制器
 *  @Author: yejian
 *  @Date: 2019/12/12 10:10
 */
@Api(tags = "开票订单控制器")
@Slf4j
@RestController
@RequestMapping("order/invoiceOrder")
public class InvoiceOrderController extends BaseController {

    @Autowired
    private InvoiceOrderService invoiceOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private OssService ossService;

    @Autowired
    private CompanyCancelOrderService companyCancelOrderService;

    /**
     * @Description 创建开票订单
     * @Author  yejian
     * @Date   2019/12/10 11:57
     * @param  entity
     * @Return ResultVo<String>
     */
    @ApiOperation("创建开票订单")
    @PostMapping("/createInvOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.INVOICE_CREATE_ORDER, lockTime = 10)
    public ResultVo<Map<String,Object>> createInvoiceOrder(@RequestBody @Valid CreateInvoiceOrderDTO entity, BindingResult results){
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        if (null == entity.getCreateWay()) {
            return ResultVo.Fail("创建开票方式不能为空");
        }
        Long userId = getCurrUserId();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String registRedisTime = (System.currentTimeMillis() + 10000) + ""; // redis标识值
        boolean flag = true;
        try{
            //创建开票订单，10秒只能创建一个订单 加入redis锁机制，防止重复生成工单
            boolean lockResult = redisService.lock(RedisKey.LOCK_INVOICE_CREATER_MEMBER_KEY + userId, registRedisTime, 60);
            if(!lockResult){
                flag = false;
                throw new BusinessException("请勿重复提交！");
            }
            String orderNo = invoiceOrderService.createInvoiceOrder(
                    userId,
                    getRequestHeadParams("oemCode"),
                    entity,
                    StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"), false);
            resultMap.put("orderNo", orderNo);
            //获取最近开票电子邮箱（收票邮箱） by 云财2.7
            MemberAccountEntity memberAccountEntity = memberAccountService.findById(userId);
            resultMap.put("email", memberAccountEntity.getEmail());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }finally {
            if(flag) {
                redisService.unlock(RedisKey.LOCK_INVOICE_CREATER_MEMBER_KEY + userId, registRedisTime);
            }
        }
        return ResultVo.Success(resultMap);
    }

    /**
     * @Description 确认开票订单
     * @Author  yejian
     * @Date   2019/12/10 11:57
     * @param  entity
     * @Return ResultVo<String>
     */
    @ApiOperation("确认开票订单")
    @PostMapping("/confirmInvOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.INVOICE_CONFIRM_ORDER, lockTime = 10)
    public ResultVo<Map<String,Object>> confirmInvOrder(@RequestBody @Valid ConfirmInvoiceOrderDTO entity, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        if (null == entity.getCreateWay()) {
            return ResultVo.Fail("创建开票方式不能为空");
        }
        try{
            Map<String,Object> resultMap = invoiceOrderService.confirmInvoiceOrder(getCurrUserId(), getRequestHeadParams("oemCode"), entity);
            return ResultVo.Success(resultMap);
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 获取支付信息
     * @param orderNo
     * @param vatRate
     * @return
     */
    @ApiOperation("获取支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="orderNo",value="订单号",dataType="String",required = true),
            @ApiImplicitParam(name="vatRate",value="增值税税率",dataType="BigDecimal")
    })
    @PostMapping("/payInformation")
    public ResultVo payInformation(@JsonParam String orderNo, @JsonParam BigDecimal vatRate){
        PayInformationVO invoicePayInfo = invoiceOrderService.getInvoicePayInfo(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo, vatRate);
        return ResultVo.Success(invoicePayInfo);
    }

    /**
     * 计算服务费
     * @param companyId 企业id
     * @param invoiceAmount 开票金额
     * @return
     */
    @PostMapping("calc/service/fee")
    public ResultVo calcServiceFee(@JsonParam Long companyId, @JsonParam Long invoiceAmount){
        if (companyId == null) {
            ResultVo.Fail("企业主键不能为空");
        }
        if (invoiceAmount == null) {
            ResultVo.Fail("开票金额不能为空");
        }
        if (invoiceAmount < 0) {
            ResultVo.Fail("开票金额不能小于0");
        }
        InvoiceServiceFeeVO vo = invoiceOrderService.calcServiceFee(getCurrUserId(), getRequestHeadParams("oemCode"), companyId, invoiceAmount);
        return ResultVo.Success(vo);
    }

    /**
     * @Description 余额支付
     * @Author yejian
     * @Date 2019/12/12 16:20
     * @param entity
     * @return ResultVo<String>
     **/
    @ApiOperation("余额支付")
    @PostMapping("/balancePayInvOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.BALANCE_PAY, lockTime = 10)
    public ResultVo<Map<String,String>> balancePayInvOrder(@RequestBody @Valid InvOrderPayDTO entity, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        Map<String,String> map = invoiceOrderService.newBalancePayOrder(getCurrUserId(), getRequestHeadParams("oemCode"), entity);
        //V3.0 订单完成推送给国金  订单类型 1-企业注册，2-企业开票，3-企业注销，4-托管费续费，5-个人开票，6-企业付款，7-月度交易结算，8-VIP费分润
        //  会员升级
        //1.3 查询校验————订单主表
        OrderEntity order = Optional.of(orderService.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到订单"));
        // 7-用户升级 15-托管费续费  才进行推送
        if ((order.getOrderType() == 7 || order.getOrderType() == 15) && ChannelPushStateEnum.TO_BE_PAY.getValue().equals(order.getChannelPushState())){
            try {
                List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
                OpenOrderVO vo = new OpenOrderVO();
                vo.setOrderNo(order.getOrderNo());
                vo.setId(order.getUserId());
                vo.setOemCode(order.getOemCode());
                vo.setOrderType(order.getOrderType());
                listToBePush.add(vo);
                rabbitTemplate.convertAndSend("orderPush", listToBePush);
            }catch (BusinessException e){
                log.error("推送失败：{}",e.getMessage());
            }
        }
        return ResultVo.Success(map);
    }

    /**
     * @Author yejian
     * @Date 2019/12/12 10:09
     * @param query
     * @return ResultVo<PageResultVo<InvoiceOrderVO>>
     */
    @ApiOperation("分页查询开票订单列表")
    @PostMapping("/findInvPage")
    public ResultVo<PageResultVo<InvoiceOrderVO>> findInvPage(@RequestBody InvOrderQuery query) {
        List<InvoiceOrderVO> invOrderList = invoiceOrderService.listInvoiceOrder(getCurrUserId(), getRequestHeadParams("oemCode"), query);
        return ResultVo.Success(PageResultVo.restPage(invOrderList));
    }

    /**
     * @Author yejian
     * @Date 2019/12/12 10:40
     * @param orderNo
     * @return ResultVo
     */
    @ApiOperation("取消开票订单")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping(value = "/cancelInvOrder")
    public ResultVo cancelInvOrder(@JsonParam String orderNo){
        invoiceOrderService.cancelInvOrder(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        return ResultVo.Success();
    }

    /**
     * @Author yejian
     * @Date 2019/12/12 15:44
     * @param orderNo
     * @return ResultVo<InvoiceOrderDetailVO>
     */
    @ApiOperation("查询开票订单详情")
    @ApiImplicitParam(name="orderNo",value="订单号",dataType="String",required = true)
    @PostMapping("/getInvDetailByNo")
    public ResultVo<InvoiceOrderSubpackageVO> getInvDetailByNo(@JsonParam String orderNo) {
        InvoiceOrderSubpackageVO invoiceOrderSubpackageVO = invoiceOrderService.queryDetailByOrderNo(getCurrUserId(), orderNo);
        if(StringUtils.isNotBlank(invoiceOrderSubpackageVO.getAccountStatement())){
            String[] imgs = invoiceOrderSubpackageVO.getAccountStatement().split(",");
            for(int a = 0;a <imgs.length;a++){
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            invoiceOrderSubpackageVO.setAccountStatementList(imgs);
        }
        if(StringUtils.isNotBlank(invoiceOrderSubpackageVO.getBusinessContractImgs())){
            String[] imgs = invoiceOrderSubpackageVO.getBusinessContractImgs().split(",");
            for(int a = 0;a <imgs.length;a++){
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            invoiceOrderSubpackageVO.setBusinessContractImgsList(imgs);
        }
        if(StringUtils.isNotBlank(invoiceOrderSubpackageVO.getAchievementImgs())){
            String[] imgs = invoiceOrderSubpackageVO.getAchievementImgs().split(",");
            for(int a = 0;a <imgs.length;a++){
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            invoiceOrderSubpackageVO.setAchievementImgsList(imgs);
        }
        if(StringUtils.isNotBlank(invoiceOrderSubpackageVO.getAchievementVideo())){
            String[] imgs = invoiceOrderSubpackageVO.getAchievementVideo().split(",");
            for(int a = 0;a <imgs.length;a++){
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            invoiceOrderSubpackageVO.setAchievementVideoList(imgs);
        }
        return ResultVo.Success(invoiceOrderSubpackageVO);
    }

    /**
     * @Author yejian
     * @Date 2019/12/21 14:40
     * @param orderNo
     * @return ResultVo
     */
    @ApiOperation("开票订单确认收货")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping(value = "/confirmReceipt")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.INVOICE_CONFIRM_RECEIPT, lockTime = 10)
    public ResultVo confirmReceipt(@JsonParam String orderNo){
        String time = (System.currentTimeMillis() + 300000 )+ ""; // redis标识值
        boolean flag = redisService.lock(RedisKey.LOCK_INVOICE_CONFIRM_RECEIPT + orderNo, time,60);
        String useraccount = getCurrUseraccount();
        if(!flag){
            return ResultVo.Fail("请勿重复提交！");
        }
        invoiceOrderService.confirmReceipt(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo,useraccount);
        return ResultVo.Success();
    }

    /**
     * @Author yejian
     * @Date 2020/2/17 14:40
     * @param orderNo
     * @return ResultVo<Map<String,Object>>
     */
    @ApiOperation("查询开票订单进度")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping(value = "/orderProgress")
    public ResultVo<Map<String,Object>> orderProgress(@JsonParam String orderNo){
        Map<String,Object> resultMap = invoiceOrderService.getOrderProgress(getCurrUserId(), orderNo);
        return ResultVo.Success(resultMap);
    }

    /**
     * @Author yejian
     * @Date 2020/3/04 10:40
     * @param companyId
     * @param month
     * @return ResultVo
     */
    @ApiOperation("查询企业累计开票额度")
    @ApiImplicitParams({
            @ApiImplicitParam(name="companyId",value="企业id",dataType="Long"),
            @ApiImplicitParam(name="month",value="月份",dataType="String")
    })
    @PostMapping(value = "/invoiceAmountByDate")
    public ResultVo orderProgress(@JsonParam Long companyId, @JsonParam String month){
        int invoiceAmount = invoiceOrderService.invoiceAmountByDate(getCurrUserId(), getRequestHeadParams("oemCode"), companyId, month);
        return ResultVo.Success(invoiceAmount);
    }

    /**
     * @Author yejian
     * @Date 2020/03/05 09:16
     * @param companyId
     * @param month
     * @return ResultVo<InvoiceOrderCountVO>
     */
    @ApiOperation("查询开票订单数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name="companyId",value="企业id",dataType="Long"),
            @ApiImplicitParam(name="month",value="月份",dataType="String")
    })
    @PostMapping("/getInvOrderCount")
    public ResultVo<InvoiceOrderCountVO> getInvOrderCount(@JsonParam Long companyId, @JsonParam String month) {
        InvoiceOrderCountVO invOrderCount = invoiceOrderService.getInvOrderCount(getCurrUserId(), getRequestHeadParams("oemCode"), companyId, month);
        return ResultVo.Success(invOrderCount);
    }

    /**
     * @Author yejian
     * @Date 2020/04/20 11:16
     * @param companyId
     * @return ResultVo<List<OrderNoVO>>
     */
    @ApiOperation("查询开票订单未付款列表")
    @ApiImplicitParam(name="companyId",value="企业id",dataType="Long")
    @PostMapping("/getUnpaidList")
    public ResultVo<List<OrderNoVO>> getUnpaidList(@JsonParam Long companyId) {
        List<OrderNoVO> unpaidList = invoiceOrderService.getUnpaidList(getCurrUserId(), getRequestHeadParams("oemCode"), companyId);
        for (OrderNoVO orderNoVO : unpaidList) {
            if (InvoiceMarkEnum.REOPEN.getValue().equals(orderNoVO.getInvoiceMark())) {
                throw new BusinessException("存在待处理的作废/红冲重开订单");
            }
        }
        return ResultVo.Success(unpaidList);
    }

    /**
     * @Author yejian
     * @Date 2020/04/22 15:16
     * @param orderNo
     * @return ResultVo<Map<String,Object>>
     */
    @ApiOperation("校验订单是否跨月/季处理")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping("/checkCrossTime")
    public ResultVo<Map<String,Object>> checkCrossTime(@JsonParam String orderNo) {
        Map<String,Object> resultMap = invoiceOrderService.checkCrossTime(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        return ResultVo.Success(resultMap);
    }

    /**
     * @Author yejian
     * @Date 2020/05/19 09:39
     * @param
     * @return ResultVo
     */
    @ApiOperation("查询开票证据补传订单数量")
    @PostMapping("/countInvWater")
    public ResultVo<InvoiceWaterCountVO> countInvWater() {
        InvoiceWaterCountVO invoiceWaterCountVO = invoiceOrderService.countInvoiceWaterOrder(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(invoiceWaterCountVO);
    }

    /**
     * @Author yejian
     * @Date 2020/05/15 11:09
     * @param query
     * @return ResultVo<PageResultVo<InvoiceWaterOrderVO>>
     */
    @ApiOperation("分页查询开票订单补传流水列表")
    @PostMapping("/findInvWaterPage")
    public ResultVo<PageResultVo<InvoiceWaterOrderVO>> findInvWaterPage(@RequestBody InvWaterOrderQuery query) {
        List<InvoiceWaterOrderVO> invWaterOrderList = invoiceOrderService.listInvoiceWaterOrder(getCurrUserId(), getRequestHeadParams("oemCode"), query);
        return ResultVo.Success(PageResultVo.restPage(invWaterOrderList));
    }

    /**
     * @Author HZ
     * @Date 2021/08/06 11:09
     * @param query
     * @return ResultVo<PageResultVo<InvoiceWaterOrderVO>>
     */
    @ApiOperation("分页查询开票订单补传成果列表")
    @PostMapping("/findInvAchievementPage")
    public ResultVo<PageResultVo<InvoiceAchievementOrderVO>> findInvAchievementPage(@RequestBody InvWaterOrderQuery query) {
        List<InvoiceAchievementOrderVO> invAchievementOrderList = invoiceOrderService.listInvoiceAchievementOrder(getCurrUserId(), getRequestHeadParams("oemCode"), query);
        return ResultVo.Success(PageResultVo.restPage(invAchievementOrderList));
    }

    /**
     * @Author yejian
     * @Date 2020/05/18 10:09
     * @param orderNo
     * @return ResultVo<InvoiceWaterOrderVO>
     */
    @ApiOperation("查询开票补传流水订单详情")
    @PostMapping("/getInvWaterDetail")
    public ResultVo<InvoiceWaterOrderVO> getInvWaterDetail(@JsonParam String orderNo) {
        InvoiceWaterOrderVO invoiceWaterOrderVO = invoiceOrderService.getInvWaterDetail(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        if(StringUtils.isNotBlank(invoiceWaterOrderVO.getAccountStatement())){
            String[] imgs = invoiceWaterOrderVO.getAccountStatement().split(",");
            for(int a = 0;a <imgs.length;a++){
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            invoiceWaterOrderVO.setAccountStatementList(imgs);
        }
        return ResultVo.Success(invoiceWaterOrderVO);
    }
    /**
     * @Author HZ
     * @Date 2021/08/06 10:09
     * @param orderNo
     * @return ResultVo<InvoiceWaterOrderVO>
     */
    @ApiOperation("查询开票补传成果订单详情")
    @PostMapping("/getInvAchievementDetail")
    public ResultVo<InvoiceAchievementOrderVO> getInvAchievementDetail(@JsonParam String orderNo) {
        InvoiceAchievementOrderVO invoiceAchievementOrderVO = invoiceOrderService.getInvAchievementDetail(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        if(StringUtils.isNotBlank(invoiceAchievementOrderVO.getAchievementImgs())){
            String[] imgs = invoiceAchievementOrderVO.getAchievementImgs().split(",");
            for(int a = 0;a <imgs.length;a++){
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            invoiceAchievementOrderVO.setAchievementImgList(imgs);
        }
        invoiceAchievementOrderVO.setAchievementVideo(ossService.getPrivateImgUrl(invoiceAchievementOrderVO.getAchievementVideo()));
        return ResultVo.Success(invoiceAchievementOrderVO);
    }
    /**
     * @Author yejian
     * @Date 2020/05/18 09:09
     * @param entity
     * @return ResultVo
     */
    @ApiOperation("开票订单补传流水")
    @PostMapping("/patchBankWater")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.INVOICE_PATCH_BANK_WATER, lockTime = 10)
    public ResultVo patchBankWater(@RequestBody @Valid InvOrderBankWaterDTO entity, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        invoiceOrderService.patchBankWater(getCurrUserId(), getRequestHeadParams("oemCode"), entity);
        return ResultVo.Success();
    }

    /**
     * @Author yejian
     * @Date 2020/05/18 09:09
     * @param entity
     * @return ResultVo
     */
    @ApiOperation("开票订单补传成果")
    @PostMapping("/patchBankAchievement")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.INVOICE_PATCH_BANK_WATER, lockTime = 10)
    public ResultVo patchBankAchievement(@RequestBody @Valid InvOrderBankAchievementDTO entity, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        invoiceOrderService.patchBankAchievement(getCurrUserId(), getRequestHeadParams("oemCode"), entity);
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
        query.setMemberId(getCurrUserId());
        List<TaxRulesVatRateVO> vatRateList = invoiceOrderService.getVatRateList(query);
        return ResultVo.Success(vatRateList);
    }

    /**
     * @Author
     * @Date
     * @param orderNo
     * @return ResultVo
     */
    @ApiOperation("查看发票")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping("find/electronic/invoice")
    public ResultVo findElectronicInvoice(@JsonParam String orderNo) {
        List<InvoiceDetailVO> list = invoiceOrderService.getEleInvByOrderNo(orderNo);
        return ResultVo.Success(list);
    }

    @ApiOperation("查询开票截止时间")
    @PostMapping("find/endtime")
    public ResultVo findEndtime(@JsonParam Long companyId, @JsonParam Integer invoiceWay) {
        InvoiceEndtimeVO invoiceEndtimeVO = invoiceOrderService.getInvoiceEndtime(companyId, invoiceWay);
        return ResultVo.Success(invoiceEndtimeVO);
    }

    @ApiOperation("查看开票服务费明细")
    @PostMapping("service/fee/detail")
    public ResultVo serviceFeeDetail(@JsonParam Long companyId, @JsonParam String orderNo) {
        List<InvoiceServiceFeeDetailVO> list = invoiceOrderService.getInvServiceFeeDetail(orderNo);
        return ResultVo.Success(list);
    }

    @ApiOperation("查看作废凭证")
    @PostMapping("/getCancellationVoucher")
    public ResultVo getCancellationVoucher(@JsonParam String orderNo) {
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }
        List<String> list = invoiceOrderService.getCancellationVoucher(orderNo, getRequestHeadParams("oemCode"), getCurrUserId());
        return ResultVo.Success(list);
    }

    @ApiOperation("重新提交")
    @PostMapping("/resubmit")
    public ResultVo resubmit(@JsonParam String orderNo) {
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }
        Long userId = getCurrUserId();
        String newOrderNo;
        String regRedisTime = (System.currentTimeMillis() + 10000) + ""; // redis标识值
        boolean flag = true;
        try {
            //创建开票订单，10秒只能创建一个订单 加入redis锁机制，防止重复生成工单
            boolean lockResult = redisService.lock(RedisKey.LOCK_INVOICE_CREATER_MEMBER_KEY + userId, regRedisTime, 60);
            if (!lockResult) {
                flag = false;
                throw new BusinessException("请勿重复提交！");
            }
            newOrderNo = invoiceOrderService.resubmit(orderNo, getCurrUserId());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }finally {
            if(flag) {
                redisService.unlock(RedisKey.LOCK_INVOICE_CREATER_MEMBER_KEY + userId, regRedisTime);
            }
        }
            return ResultVo.Success(newOrderNo);
        }

    @ApiOperation("开票前置校验")
    @ApiImplicitParam(name="companyId",value="企业id",dataType="Long")
    @PostMapping("/preCheckOfInvOrder")
    public ResultVo getObligationList(@JsonParam Long companyId) {
        if (null == companyId) {
            ResultVo.Fail("企业id为空");
        }

        Map<String, Integer> map = Maps.newHashMap();
        // 是否存在待出款订单
        List<String> obligationList = invoiceOrderService.findByOrderStatus(companyId, InvoiceOrderStatusEnum.WAIT_FOR_PAYMENT.getValue());
        if (CollectionUtil.isNotEmpty(obligationList)) {
            map.put("isObligation", 1);
        } else {
            map.put("isObligation", 0);
        }
        // 是否存在非已取消状态的注销订单
        map.put("isBePaid", 0);
//        List<ComCancelOrderVO> cancelList = companyCancelOrderService.queryByCompanyId(companyId);
//        if (CollectionUtil.isNotEmpty(cancelList)) {
//            List<ComCancelOrderVO> collect = cancelList.stream().filter(x -> !CompCancelOrderStatusEnum.CANCELED.getValue().equals(x.getOrderStatus())).collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(collect)) {
//                map.put("isBePaid", 1);
//            }
//        }
        List<String> cacelOrderList = orderService.getOderNoByCompany(companyId);
        if(CollectionUtil.isNotEmpty(cacelOrderList)){
            map.put("isBePaid", 1);
        }

        return ResultVo.Success(map);
    }

    @ApiOperation("税务监控查询")
    @PostMapping("taxMonitoringQuery")
    public ResultVo taxMonitoringQuery(@JsonParam Long companyId, @JsonParam Long invoiceAmount) {
        Map<String, Object> map = invoiceOrderService.taxMonitoringQuery(companyId, invoiceAmount);
        return ResultVo.Success(map);
    }

    @ApiOperation("开票订单线下支付详情")
    @PostMapping("offlinePaymentDetail")
    public ResultVo offlinePaymentDetail() {
        Map<String, String> map = invoiceOrderService.offlinePaymentDetail(getRequestHeadParams("oemCode"));
        return ResultVo.Success(map);
    }

    @ApiOperation("上传线下打款凭证")
    @PostMapping("uploadPaymentVoucher")
    public ResultVo uploadPaymentVoucher(@JsonParam String orderNo, @JsonParam String vouchers) {
        if (StringUtil.isBlank(orderNo) || StringUtil.isBlank(vouchers)) {
            throw new BusinessException("缺少请求参数");
        }
        invoiceOrderService.uploadPaymentVoucher(orderNo, vouchers);
        return ResultVo.Success();
    }
}
