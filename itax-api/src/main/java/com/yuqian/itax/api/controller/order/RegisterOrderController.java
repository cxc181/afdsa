package com.yuqian.itax.api.controller.order;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duob.encrypt.EncryptUtil;

import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.vo.CompanyInfoOfRegOrderVO;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.RegOrderStatusEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.enums.ParkProcessMarkEnum;
import com.yuqian.itax.park.service.ParkDisableWordService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.enums.PayWaterTypeEnum;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.util.util.BytedanceUtils;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.channel.DESUtils;
import com.yuqian.itax.util.util.channel.RSA2Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/12 10:10
 *  @Description: 工商注册订单支付控制器（支付）
 */
@Api(tags = "工商注册订单支付控制器（支付）")
@Slf4j
@RestController
@RequestMapping("order/registerorder")
public class RegisterOrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private PayWaterService payWaterService;
    @Autowired
    private ParkDisableWordService parkDisableWorkService;
    @Autowired
    private ProductParkRelaService productParkRelaService;
    @Autowired
    private OemService oemService;

    /**
     * @Description 重新提交企业注册订单
     * @Author  Kaven
     * @Date   2020/6/3 14:38
     * @Param
     * @Return
     * @Exception
    */
    @ApiOperation("重新提交企业注册订单")
    @PostMapping("resubmitRegOrder")
    public ResultVo resubmitRegOrder(@RequestBody @Valid ResubmitRegOrderDTO entity, BindingResult result){
        log.info("收到重新提交企业注册订单请求：{}",JSON.toJSONString(entity));

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if(null == entity){
            return ResultVo.Fail("操作失败，对象不能为空！");
        }
        entity.setOemCode(this.getRequestHeadParams("oemCode"));
        this.registerOrderService.resubmitRegOrder(getCurrUser().getUserId(),entity);
        return ResultVo.Success();
    }

    /**
     * @Description 长沙园区-确认已开启(根据订单号修改订单状态并进行自动派单)
     * @Author  Kaven
     * @Date   2020/6/12 10:56 上午
     * @Param orderNo
     * @Return ResultVo
     * @Exception
    */
    @ApiOperation("长沙园区-确认已开启(根据订单号修改订单状态并进行自动派单)")
    @PostMapping("ensureValidate")
    public ResultVo<Map<String,Object>> ensureValidate(@JsonParam String orderNo){
        log.info("收到长沙园区-确认已开启请求：{}",orderNo);

        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("操作失败，订单号不能为空！");
        }
        this.registerOrderService.ensureValidate(orderNo,getCurrUser().getUserId(),getRequestHeadParams("oemCode"));
        return ResultVo.Success();
    }

    /**
     * @Description 根据订单号更新是否微信授权标识
     * @Author  Kaven
     * @Date   2020/6/12 11:47 上午
     * @Param   orderNo-订单号 flag 0-未授权 1-已授权
     * @Return  ResultVo
     * @Exception
    */
    @ApiOperation("根据订单号更新是否微信授权标识")
    @PostMapping("updateWechatAuthFlag")
    public ResultVo updateWechatAuthFlag(@RequestBody List<OrderWechatAuthRelaDTO> lists) {
        //校验参数
        try {
            validateWeChatAuthDTOList(lists);
            Long userId = getCurrUserId();
            String oemCode = getRequestHeadParams("oemCode");
            registerOrderService.updateWechatAuthFlagByOrderNo(lists, userId, oemCode);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResultVo.Fail("更新微信订阅通知失败");
        }
        return ResultVo.Success();
    }

    /**
     * 校验微信模板
     * @param lists
     */
    public void validateWeChatAuthDTOList(List<OrderWechatAuthRelaDTO> lists) {
        if (CollectionUtil.isEmpty(lists)) {
            throw new BusinessException("请求参数不能为空");
        }
        for (OrderWechatAuthRelaDTO dto : lists) {
            if (StringUtils.isBlank(dto.getOrderNo())) {
                throw new BusinessException("订单编号不能为空");
            }
            if (dto.getWechatTmplType() == null) {
                throw new BusinessException("微信模板类型不能为空");
            }
            if (dto.getWechatTmplType() < 1 || dto.getWechatTmplType() > 3) {
                throw new BusinessException("微信模板类型有误");
            }
            if (dto.getAuthStatus() == null) {
                throw new BusinessException("授权状态不能为空");
            }
            if (dto.getAuthStatus() < 0 || dto.getAuthStatus() > 1) {
                throw new BusinessException("授权状态有误");
            }
        }
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
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(orderNo);
        orderEntity.setUserId(currUser.getUserId());
        orderEntity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        orderEntity = orderService.selectOne(orderEntity);
        if (orderEntity == null) {
            throw new BusinessException("订单不存在");
        }
        ParkEntity parkEntity = parkService.findById(orderEntity.getParkId());
        if (parkEntity == null) {
            throw new BusinessException("订单归属园区不存在");
        }
        // 查询注册订单
        RegisterOrderEntity regOrder = registerOrderService.queryByOrderNo(orderNo);
        if (null == regOrder) {
            return ResultVo.Fail("未查询到注册订单");
        }
        // 查询园区产品注册流程
        ProductParkRelaEntity productParkRelaEntity = new ProductParkRelaEntity();
        productParkRelaEntity.setProductId(orderEntity.getProductId());
        productParkRelaEntity.setCompanyType(regOrder.getCompanyType());
        productParkRelaEntity.setParkId(orderEntity.getParkId());
        ProductParkRelaEntity entity = productParkRelaService.selectOne(productParkRelaEntity);
        if (null == entity) {
            return ResultVo.Fail("未查询到园区产品注册流程");
        }
        if (!Objects.equals(entity.getProcessMark(), ParkProcessMarkEnum.SIGN.getValue())) {
            throw new BusinessException("当前园区不需要提交签名");
        }
        if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue())) {
            return ResultVo.Fail("当前订单状态不允许提交签名");
        }
        registerOrderService.openSubmitSign(orderEntity, RegOrderStatusEnum.SIGNATURE_CONFIRMATION.getValue(), currUser.getUseraccount());
        return ResultVo.Success();
    }
    /**
     * @Description 字号查询
     * @Author  Kaven
     * @Date   2020/6/3 16:20
     * @Param   orderNo
     * @Return  ResultVo
     * @Exception
    */
    @ApiOperation("字号查询")
    @PostMapping("queryShopName")
    public ResultVo<Map> queryShopName(@JsonParam String orderNo){
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        Map<String,Object> dataMap = this.registerOrderService.queryShopName(orderNo, getCurrUserId());
        return ResultVo.Success(dataMap);
    }

    /**
     * @Author Kaven
     * @Description 工商注册（会员升级）订单支付
     * @Date 17:53 2019/8/15
     * @Param [payDto, result]
     * @Param orderType 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-用户升级
     * @return ResultVo
     **/
    @ApiOperation("工商注册（会员升级）订单支付")
    @PostMapping("payOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.REGISTER_ORDER, lockTime = 10)
    public ResultVo payRegOrder(@RequestBody @Validated RegOrderPayDTO payDto, BindingResult result) throws UnknownHostException {
        log.info("收到支付请求：{}",JSON.toJSONString(payDto));

        ResultVo resultVo = ResultVo.Success();
        if(null == payDto){
            return ResultVo.Fail("订单支付失败，参数不能为空！");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        // 工商订单注册订单支付时，订单号必传
        if(OrderTypeEnum.REGISTER.getValue().equals(payDto.getOrderType()) && StringUtils.isBlank(payDto.getOrderNo())){
            return ResultVo.Fail("订单号不能为空");
        }
        payDto.setCurrUserId(getCurrUserId());
        String sourceType = org.apache.commons.lang.StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");// 请求来源:支付宝or微信or其他
        if(StringUtil.isBlank(sourceType)){
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if(!("1".equals(sourceType) || "2".equals(sourceType)|| "3".equals(sourceType)|| "4".equals(sourceType))){
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        payDto.setSourceType(Integer.parseInt(sourceType));
        if(StringUtils.isBlank(payDto.getPayChannel())){
            payDto.setPayChannel("1");// 向下兼容，默认微信支付
        }
        String oemCode = this.getRequestHeadParams("oemCode");

        // 跨主体收单机构无法通过本机构进行微信支付
        if (StringUtil.isNotBlank(payDto.getPayChannel()) && payDto.getPayChannel().equals("1")) {
            // 查询机构是否为跨主体收单
            OemEntity oem = oemService.getOem(oemCode);
            if (null == oem) {
                return ResultVo.Fail("未查询到机构信息");
            }
            if (null != oem.getIsOtherOemPay() && oem.getIsOtherOemPay() == 1) {
                return ResultVo.Fail("无法使用当前机构进行微信支付");
            }
        }

        payDto.setOemCode(oemCode);
        JSONObject data = this.registerOrderService.orderPay(payDto);
        resultVo.setData(data);
        return resultVo;
    }

    /**
     * @Description 工商注册/会员升级/用户充值订单取消 唤起微信又取消不支付时需要前端调用
     * @Author  Kaven
     * @Date   2020/1/9 9:56
     * @Param
     * @Return
     * @Exception
    */
    @ApiOperation("工商注册/会员升级/用户充值订单取消")
    @PostMapping("cancelOrder")
    public ResultVo cancelOrder(@JsonParam String orderNo){
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("取消订单失败，订单号不能为空！");
        }
        this.registerOrderService.cancelOrder(getCurrUser().getUserId(),orderNo);
        return ResultVo.Success();
    }

    /**
     * @Description 根据订单号查询订单状态信息
     * @Author  Kaven
     * @Date   2020/1/9 10:08
     * @Param  orderNo
     * @Return ResultVo
    */
    @ApiOperation("根据订单号查询订单状态信息")
    @PostMapping("queryByOrderNo")
    public ResultVo queryByOrderNo(@JsonParam String orderNo){
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("查询失败，订单号不能为空！");
        }
        Map<String,Object> respData = Maps.newHashMap();
        OrderEntity order = this.orderService.queryByOrderNo(orderNo);
        respData.put("id",order.getId());
        respData.put("orderStatus",order.getOrderStatus());
        respData.put("orderNo",order.getOrderNo());
        respData.put("orderType",order.getOrderType());
        // 如果是开户订单，补充返回接单客服电话
        if(OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType())){
            RegisterOrderEntity t = new RegisterOrderEntity();
            t.setOrderNo(orderNo);
            RegisterOrderEntity regOrder = this.registerOrderService.selectOne(t);
            respData.put("customerServicePhone",regOrder.getCustomerServicePhone());
        } else if(OrderTypeEnum.INVOICE.getValue().equals(order.getOrderType())){ // 开票订单
            InvoiceOrderEntity t = new InvoiceOrderEntity();
            t.setOrderNo(orderNo);
            InvoiceOrderEntity invOrder = this.invoiceOrderService.selectOne(t);
            respData.put("customerServicePhone",invOrder.getCustomerServicePhone());
        }
        return ResultVo.Success(respData);
    }

    /**
     * @Description 渠道端支付回调地址
     * @Author  Kaven
     * @Date   2019/12/28 14:19
     * @Param  orderNo orderStatus wxOrderNO
     * @Return String
     * @Exception
    */
    @PostMapping("wechatNotify")
    public String wechatNotify(@RequestParam Map<String,String> reqData) throws SQLException {
        if(null == reqData){
            log.error("未收到渠道微信/支付宝回调请求参数...");
            return null;
        }

        boolean flag = false;// 新旧渠道标识，默认旧渠道
        String payNo = null;// 交易流水号
        String orderStatus = null; // 订单状态

        //读取渠道支付相关配置，对数据进行解密
        OemParamsEntity t = new OemParamsEntity();
        if(StringUtils.isNotBlank(reqData.get("agentNo"))){ // 旧渠道返回
            t.setAccount(reqData.get("agentNo"));
        } else if(StringUtils.isNotBlank(reqData.get("merNo"))){ // 新渠道返回
            t.setAccount(reqData.get("merNo"));
            flag = true;// 新渠道
        } else {
            throw new BusinessException("微信/支付宝回调处理失败，未知的商户信息");
        }
//        t.setStatus(1);
        t.setParamsType(2);
        OemParamsEntity paramsEntity = this.oemParamsService.selectOne(t);
        if(null == paramsEntity){
            throw new BusinessException("微信/支付宝回调处理失败，未找到对应商户号的支付配置信息！");
        }

        // 数据解密
        String keyNumSrc = reqData.get("keyNum").replaceAll(" ","+");// keyNum密文（防止+号被替换为空格）
        String dataStr = reqData.get("data").replaceAll(" ","+"); // 数据密文
        String deckeyNum = null;
        Map<String, String> data = null;// 旧渠道解密后数据
        JSONObject jsonData = null;// 新渠道解密后数据
        try{
            // 解密keyNum和data数据
            if(flag){
                // 新渠道解密
                deckeyNum = new String(RSA2Util.decryptByPrivateKey(Base64.decode(keyNumSrc), paramsEntity.getPrivateKey().trim()));
                String decDataStr = new String(DESUtils.des3DecodeECB(deckeyNum.getBytes(), Base64.decode(dataStr)));
                jsonData = JSONObject.parseObject(decDataStr);
                log.info("解密渠道回调data数据：{}",JSON.toJSONString(jsonData));
                payNo = jsonData.getString("orderNo");
                orderStatus = jsonData.getString("status");
            } else {
                // 旧渠道解密
                deckeyNum = EncryptUtil.decSm2SecretKey(keyNumSrc, paramsEntity.getPrivateKey());
                data = EncryptUtil.decSm4Data(dataStr, deckeyNum);
                log.info("解密渠道回调data数据：{}",JSON.toJSONString(data));
                payNo = data.get("orderNo");
                orderStatus = data.get("status");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("参数解密错误：{}",e.getMessage());
            return "参数解密错误";
        }

        Date payDate = new Date();
        if("5".equals(orderStatus)) { // 订单支付成功
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SUCCESS.getValue(),orderStatus,MessageEnum.SUCCESS.getMessage());
        } else {//订单支付失败
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SYSTEM_ERROR.getValue() ,orderStatus, "支付失败");
        }
        log.info("渠道微信/支付宝支付回调成功...");
        return "SUCCESS";
    }

    /**
     * 字节跳动支付回调地址
     * @param reqData
     * @return
     * @throws SQLException
     */
    @PostMapping("bytedancePayNotify")
    public String bytedancePayNotify(@RequestBody Map<String,String> reqData) throws Exception {
        log.info("字节跳动异步回调参数：{}",JSON.toJSONString(reqData));
        if(null == reqData){
            log.error("未收到字节跳动回调请求参数...");
            return null;
        }
        if(!reqData.containsKey("type") && StringUtils.isBlank("type")){
            return null;
        }
        if(!reqData.containsKey("msg")){
            log.error("未收到字节跳动回调请求参数...");
            return null;
        }
        String payNo = null;// 交易流水号

        //读取渠道支付相关配置，对数据进行解密
        String bytedanceData = reqData.get("msg").toString();
        JSONObject paramsData = null;
        JSONObject cpExtra = null;
        try{
            paramsData = JSONObject.parseObject(bytedanceData);
            String cpExtraStr = paramsData.getString("cp_extra");
            if(StringUtils.isNotEmpty(cpExtraStr)){
                cpExtra = JSONObject.parseObject(cpExtraStr);
            }
        }catch (Exception e){
            log.error("字节跳动回调请求参数解析错误,原参数：{}",bytedanceData);
            return null;
        }

        OemParamsEntity t = new OemParamsEntity();
        if(paramsData.containsKey("appid") && StringUtils.isNotBlank(paramsData.getString("appid"))){ //字节跳动支付参数
            t.setAccount(paramsData.getString("appid"));
        }
        t.setParamsType(30);
        if(cpExtra != null && cpExtra.containsKey("oemCode")) {
            t.setOemCode(cpExtra.getString("oemCode"));
        }
        OemParamsEntity paramsEntity = this.oemParamsService.selectOne(t);
        if(null == paramsEntity){
            throw new BusinessException("字节跳动回调处理失败，未找到对应的支付配置信息！");
        }
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        String token = "";
        if(params.containsKey("token")){
            token = params.getString("token");
        }
        JSONObject signParamsData = JSONObject.parseObject(JSON.toJSONString(reqData));
        String newSign = BytedanceUtils.getNotifySign(signParamsData.toJavaObject(Map.class),token);// 正常请求支付接口
        if(reqData.containsKey("msg_signature") && newSign.equals(reqData.get("msg_signature"))){
            String status = paramsData.getString("status");
            if("payment".equals(reqData.get("type"))) {
                log.info("字节跳动支付回调开始");
                String message = "其他";
                String wayStr = paramsData.getString("way");
                if("1".equals(wayStr)){
                    message = "微信";
                }else if("2".equals(wayStr)){
                    message="支付宝";
                }
                payNo =  paramsData.getString("cp_orderno");
                if("SUCCESS".equals(status)) {
                    // 更新订单状态
                    this.orderService.updateOrderStatus(payNo, null, new Date(), MessageEnum.SUCCESS.getValue(), "0", "字节跳动支付成功，支付通道：" + message);
                }else  if("FAIL".equals(status) || "TIMEOUT".equals(status)){
                    // 更新订单状态
                    this.orderService.updateOrderStatus(payNo, null, new Date(), MessageEnum.SYSTEM_ERROR.getValue() ,status, "字节跳动支付失败，支付通道："+ message);
                }
                log.info("字节跳动支付回调结束");
            }else if("refund".equals(reqData.get("type"))){
                log.info("字节跳动退款回调开始");
                payNo =  paramsData.getString("cp_refundno");
                PayWaterEntity payWater = new PayWaterEntity();
                payWater.setPayNo(payNo);
                payWater.setOemCode(t.getOemCode());
                payWater.setPayWay(PayWayEnum.BYTEDANCE.getValue());
                payWater.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());
                payWater.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
                payWater =  payWaterService.selectOne(payWater);
                if(payWater == null){
                    return null;
                }
                if("SUCCESS".equals(status)) {
                    // 更新流水状态
                    payWater.setUpResultMsg("成功");
                    payWater.setExternalOrderNo( paramsData.getString("refund_no"));
                    payWater.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                    payWater.setUpdateTime(new Date());
                    payWater.setUpdateUser("admin异步回调");
                    this.payWaterService.updatePayStatus(payWater);
                }else  if("FAIL".equals(status) || "TIMEOUT".equals(status)){
                    // 更新流水状态
                    payWater.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
                    payWater.setUpResultMsg("字节跳动退款失败");
                    payWater.setUpStatusCode(status);
                    payWater.setUpdateTime(new Date());
                    payWater.setUpdateUser("admin异步回调");
                    this.payWaterService.updatePayStatus(payWater);
                }
                log.info("字节跳动退款回调结束");
            }
        }else{
            log.error("字节跳动回调请求验签失败");
            return null;
        }
        log.info("字节跳动异步回调成功...");
        Map<String,Object> resultMsg = new HashMap<>();
        resultMsg.put("err_no",0);
        resultMsg.put("err_tips","success");
        return JSON.toJSONString(resultMsg);
    }

    /**
     * @Description 渠道端支付手工回调地址（仅在渠道端回调失败时使用）
     * @Author  Kaven
     * @Date   2020/1/13 17:19
     * @Param  payNo 支付流水号  orderStatus 订单状态=5
     * @Return
     * @Exception
    */
    @PostMapping("wechatNotifyByHand")
    public String wechatNotifyByHand(@JsonParam String payNo, @JsonParam String orderStatus) throws SQLException {
        Date payDate = new Date();

        if("5".equals(orderStatus)) { // 订单支付成功
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SUCCESS.getValue() ,orderStatus,MessageEnum.SUCCESS.getMessage());
        } else {//订单支付失败
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SYSTEM_ERROR.getValue(), orderStatus, "支付失败");
        }
        log.info("渠道端微信手工回调成功...");
        return JSON.toJSONString(ResultVo.Success("手工回调成功"));
    }

    /**
     * @Description 查询待身份验证订单列表
     * @Author yejian
     * @Date 2020/06/11 17:20
     * @return ResultVo
     **/
    @ApiOperation("查询待身份验证订单列表")
    @PostMapping("queryTobeAuthOrder")
    public ResultVo queryTobeAuthOrder(){
        List<String> tobeAuthOrderList = registerOrderService.queryTobeAuthRegOrder(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(tobeAuthOrderList);
    }

    /**
     * @Description 更新待身份验证订单的通知次数
     * @Author yejian
     * @Date 2020/06/11 18:20
     * @return ResultVo
     **/
    @ApiOperation("更新待身份验证订单的通知次数")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping("setOrderAlertNum")
    public ResultVo setOrderAlertNum(@JsonParam String orderNo){
        registerOrderService.setOrderAlertNum(getCurrUserId(), orderNo);
        return ResultVo.Success();
    }

    @ApiOperation("校验字号")
    @ApiImplicitParam(name="parkId",value="园区id",dataType="Long",required = true)
    @PostMapping("/checkShopName")
    public ResultVo checkShopName(@RequestBody @Validated RegOrderCheckShopNameDTO dto, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }

        // 禁用字号校验
        parkDisableWorkService.checkoutDisableWord(dto.getParkId(), dto.getShopName(), dto.getShopNameOne(), dto.getShopNameTwo());
        return ResultVo.Success();
    }

    @ApiOperation("新增/编辑注册订单")
    @PostMapping("/addOrUpdateRegOrder")
    public ResultVo addOrUpdateRegOrder(@RequestBody @Validated AddOrUpdateRegOrderDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }

        String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
        // 请求来源:支付宝or微信or其他
        if (com.github.pagehelper.StringUtil.isEmpty(sourceType)) {
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if (!("1".equals(sourceType) || "2".equals(sourceType) || "3".equals(sourceType) || "4".equals(sourceType))) {
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        dto.setSourceType(Integer.parseInt(sourceType));

        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            dto.setOemCode(getRequestHeadParams("oemCode"));
            dto.setUserId(getCurrUserId());
            String orderNo = registerOrderService.addOrUpdateRegOrder(dto);
            resultMap.put("orderNo",orderNo);
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(resultMap);
    }

    @ApiOperation("确认注册订单")
    @PostMapping("/confirmRegOrder")
    public ResultVo confirmRegOrder(@JsonParam String orderNo) {
        if (StringUtil.isBlank(orderNo)) {
            ResultVo.Fail("订单编号为空");
        }
        orderService.updateOrderStatus(getCurrUseraccount(), orderNo, RegOrderStatusEnum.TO_BE_SIGN.getValue());
        return ResultVo.Success();
    }

    @ApiOperation("企业信息数据")
    @PostMapping("/getCompanyInfo")
    public ResultVo getCompanyInfo(@JsonParam String orderNo) {
        CompanyInfoOfRegOrderVO vo = registerOrderService.getCompanyInfo(orderNo);
        return ResultVo.Success(vo);
    }
}
