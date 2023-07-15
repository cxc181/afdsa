package com.yuqian.itax.order.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yuqian.itax.agent.entity.*;
import com.yuqian.itax.agent.service.*;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.constants.ErrorCodeConstants;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.coupons.dao.CouponsIssueRecordMapper;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueRecordStatusEnum;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.enums.WeChatMessageTemplateTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.dao.RegisterOrderChangeRecordMapper;
import com.yuqian.itax.order.dao.RegisterOrderMapper;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.query.AccessPartyOrderQuery;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.dao.ParkBusinessAddressRulesMapper;
import com.yuqian.itax.park.entity.ParkBusinessAddressRulesEntity;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeWithTaxCodeVO;
import com.yuqian.itax.park.enums.ParkProcessMarkEnum;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkBusinessAddressRulesService;
import com.yuqian.itax.park.service.ParkDisableWordService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.*;
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
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.dao.MemberCompanyMapper;
import com.yuqian.itax.user.dao.UserExtendMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.dto.UserAuthDTO;
import com.yuqian.itax.user.enums.*;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.entity.*;
import com.yuqian.itax.util.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Decoder;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service("registerOrderService")
public class RegisterOrderServiceImpl extends BaseServiceImpl<RegisterOrderEntity,RegisterOrderMapper> implements RegisterOrderService {
    @Resource
    private RegisterOrderMapper registerOrderMapper;
    @Autowired
    private PayWaterService payWaterService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private MemberOrderRelaService memberOrderRelaService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private OemService oemService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private ProfitsDetailService profitsDetailService;
    @Resource
    private RegisterOrderChangeRecordMapper registerOrderChangeRecordMapper;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private ReceiveOrderService receiveOrderService;
    @Autowired
    private UserService userService;
    @Resource
    private UserExtendMapper userExtendMapper;
    @Resource
    private MemberCompanyMapper memberCompanyMapper;
    @Resource
    private ParkBusinessAddressRulesMapper parkBusinessAddressRulesMapper;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private ParkBusinessAddressRulesService parkBusinessAddressRulesService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private OemParkRelaService oemParkRelaService;
    @Autowired
    private ProductParkRelaService productParkRelaService;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private OssService ossService;
    @Autowired
    private OrderAttachmentService orderAttachmentService;
    @Autowired
    private BusinessScopeService businessScopeService;
    @Autowired
    private OrderWechatAuthRelaService orderWechatAuthRelaService;
    @Autowired
    private CouponsService couponsService;
    @Autowired
    private CouponsIssueRecordService couponsIssueRecordService;
    @Autowired RegisterOrderChangeRecordService registerOrderChangeRecordService;
    @Resource
    private CouponsIssueRecordMapper couponsIssueRecordMapper;
    @Autowired
    private OemConfigService oemConfigService;
    @Autowired
    private ParkDisableWordService parkDisableWorkService;
    @Autowired
    private OemAccessPartyService oemAccessPartyService;
    @Autowired
    private RegisterOrderGoodsDetailRelaService registerOrderGoodsDetailService;
    @Autowired
    private ParkBusinessscopeService parkBusinessScopeService;
    @Autowired
    private RegisterOrderGoodsDetailRelaService registerOrderGoodsDetailRelaService;
    @Autowired
    private ParkDisableWordService parkDisableWordService;
    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;
    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;
    @Autowired
    private CompanyCorePersonnelService companyCorePersonnelService;

    @Override
    public void updateSignOrVideoAddr(String orderNo, String url, int type) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("orderNo",orderNo);
        params.put("updateTime",new Date());
        if(type == 1){
            params.put("signImg",url);
        }else if(type == 2){
            params.put("videoAddr",url);
        }
        this.registerOrderMapper.updateSignOrVideoAddr(params);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject orderPay(RegOrderPayDTO payDto) throws BusinessException, UnknownHostException{
        log.info("支付请求处理开始：{}",JSON.toJSONString(payDto));
        String orderNo = payDto.getOrderNo();

        // 支付预处理：支付参数校验
        Map<String,Object> returnData = assertBeforePay(payDto);

        MemberLevelEntity levelData = null;// 会员等级信息
        ProductEntity product = (ProductEntity) returnData.get("product");// 产品信息
        MemberAccountEntity member = (MemberAccountEntity) returnData.get("member");// 会员信息

        JSONObject dataObj  = new JSONObject();// 返回值

        OrderEntity order = null;// 订单信息
        if(null != returnData.get("order")){
            order = (OrderEntity) returnData.get("order");
        }

        String retCode = null;// 临时变量，支付接口返回码
        String retMsg = null;// 临时变量，支付接口返回信息

        // 企业开户订单在支付前需要请求渠道实时订单查询接口判断是否可以继续支付（防重复支付）add by Kaven 2020-03-05
        if(OrderTypeEnum.REGISTER.getValue().equals(payDto.getOrderType())){
            // 订单支付预处理，查询实时订单状态，并给出相应处理
            dataObj = this.orderCheckBeforePay(order,member);
            if(null != dataObj){
                return dataObj;
            }
        }

        // 判断是否使用了优惠券
        if (Objects.nonNull(payDto.getCouponsIssueRecordId())) {
            // 查询优惠券金额
            CouponsIssueRecordEntity issueRecord = Optional.ofNullable(couponsIssueRecordService.findById(payDto.getCouponsIssueRecordId())).orElseThrow(() -> new BusinessException("未查询到优惠券发放记录"));
            CouponsEntity couponsEntity = Optional.ofNullable(couponsService.findById(issueRecord.getCouponsId())).orElseThrow(() -> new BusinessException("未查询到优惠券信息"));
            // 校验优惠券是否过期,过期则抛异常
            if (DateUtils.truncatedCompareTo(couponsEntity.getEndDate(),new Date(),Calendar.DATE) < 0) {
                throw new BusinessException("优惠券已过期，不可使用");
            }
            Long faceAmount = couponsEntity.getFaceAmount();
            // 查询注册订单
            RegisterOrderEntity registerOrderEntity = registerOrderMapper.queryByOrderNo(orderNo);
            if (null == registerOrderEntity) {
                throw new BusinessException("未查询到注册订单");
            }
            // 校验优惠券可用范围是否合法
            if (!couponsEntity.getUsableRange().equals(registerOrderEntity.getCompanyType().toString())) {
                throw new BusinessException("优惠券不可用于当前类型企业注册");
            }
            if (Objects.nonNull(registerOrderEntity.getCouponsIssueId()) && !Objects.equals(payDto.getCouponsIssueRecordId(),registerOrderEntity.getCouponsIssueId())) {
                throw new BusinessException("系统错误，请返回订单列表重新进入此页面");
            }
            // 校验订单金额
            OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("订单不存在"));
            Long payAmount = orderEntity.getPayAmount();
            if (null == registerOrderEntity.getCouponsIssueId()) {
                payAmount = Long.valueOf(new BigDecimal(payAmount).subtract(new BigDecimal(faceAmount)).longValue());
                if (payAmount < 0) {
                    payAmount = 0L;
                }
            }
            if (!payAmount.equals(payDto.getAmount())) {
                throw new BusinessException("支付失败，订单支付金额不匹配，请核对");
            }
            payDto.setAmount(payAmount);
            // 修改优惠券使用状态及使用时间
            Date date = new Date();
            issueRecord.setUseTime(date);
            issueRecord.setStatus(CouponsIssueRecordStatusEnum.USED.getValue());
            issueRecord.setUpdateTime(date);
            issueRecord.setUpdateUser(member.getMemberAccount());
            issueRecord.setRemark("使用优惠券");
            couponsIssueRecordService.editByIdSelective(issueRecord);
            // 修改注册订单支付金额
            registerOrderEntity.setPayAmount(payAmount);
            registerOrderEntity.setCouponsIssueId(payDto.getCouponsIssueRecordId());// 优惠券发放记录id
            registerOrderMapper.updateByPrimaryKeySelective(registerOrderEntity);
            // 修改注册订单变更记录支付金额
            RegisterOrderChangeRecordEntity recordEntity = new RegisterOrderChangeRecordEntity();
            BeanUtils.copyProperties(registerOrderEntity, recordEntity);// 参数拷贝
            recordEntity.setId(null);
            recordEntity.setAddTime(new Date());
            recordEntity.setAddUser(member.getMemberAccount());
            recordEntity.setOrderStatus(RegOrderStatusEnum.TO_BE_SURE.getValue());
            recordEntity.setCouponsIssueId(payDto.getCouponsIssueRecordId());
            this.registerOrderChangeRecordMapper.insertSelective(recordEntity);
        }

        // 开户订单支付或者会员升级支付时，若支付金额等于0，不走支付，直接成功
        if(payDto.getAmount() == 0 && (OrderTypeEnum.REGISTER.getValue().equals(payDto.getOrderType()) || OrderTypeEnum.UPGRADE.getValue().equals(payDto.getOrderType()))){
            log.info("订单支付金额为0，不用调起支付，直接通过...");
            return this.payZeroAmountOrder(payDto,product.getProdName());
        }

        // 根据支付方式封装参数，请求渠道端支付接口完成支付
        payDto.setBuyerId(member.getAlipayUserId());// 设置买家支付宝用户号
        //调用支付通道
        Map<String,Object> orderMap = this.routeAndPayOrder(payDto);

        log.info("会员升级/工商注册请求支付返回：{}",JSONObject.toJSONString(orderMap));

        retCode = orderMap.get("code").toString();// 返回码
        retMsg = orderMap.get("msg").toString();// 返回信息

        // 如果是会员升级，需要先生成订单号并入库
        if(OrderTypeEnum.UPGRADE.getValue().equals(payDto.getOrderType())){
            // 保存订单主表信息
            order = new OrderEntity();
            orderNo = OrderNoFactory.getOrderCode(payDto.getCurrUserId());
            order.setOemCode(payDto.getOemCode());
            order.setOrderNo(orderNo);
            order.setOrderAmount(payDto.getAmount());
            order.setPayAmount(payDto.getAmount());
            order.setProfitAmount(payDto.getAmount());
            order.setAuditStatus(0);// 默认审核状态为0 待审核
            // 取会员等级套餐信息
            levelData = (MemberLevelEntity) returnData.get("levelData");
            order.setProductName(levelData.getLevelName());
            order.setProductId(levelData.getId());
            order.setOrderType(payDto.getOrderType());
            order.setSourceType(payDto.getSourceType());
            order.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
            // 保存会员订单关系，补全订单参数
            completionParameter(payDto.getCurrUserId(),order);

            if (!(StringUtils.equals(retCode, "00"))) {
                order.setOrderStatus(RACWStatusEnum.PAY_FAILURE.getValue());
            }
            this.orderService.insertSelective(order);
        }

        // 生成支付流水
        if (null != order) {
            // 使用订单金额作为产品金额，以防订单未支付时产品金额发生变化，或存在园区单独定价的情况
            product.setProdAmount(order.getOrderAmount());
        }
        generatePayWater(member,payDto,levelData,product,orderMap,retCode,orderNo);

        // 如果是开户订单，需要更新订单支付金额
        if(OrderTypeEnum.REGISTER.getValue().equals(payDto.getOrderType())){
            OrderEntity mainOrder = new OrderEntity();
            mainOrder.setId(order.getId());
            mainOrder.setOrderNo(orderNo);
            mainOrder.setPayAmount(payDto.getAmount());
            mainOrder.setUpdateTime(new Date());
            this.orderService.editByIdSelective(mainOrder);
        }

        if (!(StringUtils.equals(retCode, "00"))) {
            log.error("支付失败，支付下单失败:" + orderNo);
            throw new BusinessException("支付失败，支付下单失败:" + retMsg);
        }
        dataObj = (JSONObject) orderMap.get("data");
        // 返回订单号
        dataObj.put("orderNo",orderNo);
        dataObj.put("isPaid","0");// 是否已支付标识，0 未支付 1已支付

        log.info("支付请求处理结束，返回数据：{}", JSON.toJSONString(dataObj));
        return dataObj;
    }

    /**
     * @Description 路由支付通道，发起支付请求
     * @Author  Kaven
     * @Date   2020/10/21 11:24
     * @Param payDto
     * @Return Map<String, Object>
     * @Exception UnknownHostException
    */
    public Map<String, Object> routeAndPayOrder(RegOrderPayDTO payDto) throws UnknownHostException,BusinessException {
        log.info("路由支付通道，发送支付请求：{}",JSON.toJSONString(payDto));

        Map<String,Object> orderMap = Maps.newHashMap();// 接收返回
        String payNo = UniqueNumGenerator.generatePayNo();// 生成24位流水号
        if("1".equals(payDto.getPayChannel())){ // 微信支付
            log.info("会员升级/工商注册请求微信支付开始：{}",JSONObject.toJSONString(payDto));
            // 根据挡板开关判断流程走向
            DictionaryEntity dict = this.dictionaryService.getByCode("itax_wechatpay_switch");
            if(null != dict && "1".equals(dict.getDictValue())){
                orderMap = simulateWechatPay(payNo);// 挡板模拟返回
            }else{
                // 参数封装
                WechatPayDto wechatPayDto = this.orderService.buildWechatParams(payDto.getCurrUserId(),payDto.getOemCode(),payNo,payDto.getAmount());
                orderMap = WechatPayUtils.wechatPay(wechatPayDto);// 正常请求支付接口
            }
            orderMap.put("payWay",PayWayEnum.WECHATPAY.getValue());
            orderMap.put("payChanel",PayChannelEnum.WECHATPAY.getValue());
        } else if ("3".equals(payDto.getPayChannel())){ // 支付宝支付
            // 参数封装
            AliPayDto aliPayDto = this.orderService.buildAliPayParams(payDto.getOemCode(),payNo,payDto.getAmount(),payDto.getBuyerId(),payDto.getBuyerLogonId());
            log.info("会员升级/工商注册请求支付宝支付开始：{}",JSONObject.toJSONString(payDto));
            // 根据挡板开关判断流程走向
            DictionaryEntity dict = this.dictionaryService.getByCode("itax_alipay_switch");
            if(null != dict && "1".equals(dict.getDictValue())){
                orderMap = simulateAliPay(payNo);// 挡板模拟返回
            }else{
                orderMap = AliPayUtils.aliPay(aliPayDto);// 正常请求支付接口
            }
            orderMap.put("payWay",PayWayEnum.ALIPAY.getValue());
            orderMap.put("payChanel",PayChannelEnum.ALIPAY.getValue());
        }else if ("8".equals(payDto.getPayChannel())){ // 字节跳动支付
            // 参数封装
            BytedancePayDto bytedancePayDto = this.orderService.buildBytedanceParams(payDto.getCurrUserId(),payDto.getOemCode(),payNo,payDto.getAmount(),payDto.getOrderNo());
            log.info("会员升级/工商注册请求字节跳动支付开始：{}",JSONObject.toJSONString(payDto));
            // 根据挡板开关判断流程走向
            DictionaryEntity dict = this.dictionaryService.getByCode("itax_bytedance_switch");
            if(null != dict && "1".equals(dict.getDictValue())){
                orderMap = simulateBytedancePay(payNo);// 挡板模拟返回
            }else{
                orderMap = BytedanceUtils.bytedancePay(bytedancePayDto);// 正常请求支付接口
            }
            orderMap.put("payWay",PayWayEnum.BYTEDANCE.getValue());
            orderMap.put("payChanel",PayChannelEnum.BYTEDANCE.getValue());
        } else {
            throw new BusinessException("不支持的支付方式，请联系管理员");
        }
        orderMap.put("payNo",payNo);
        return orderMap;
    }

    /**
     * @Description 生成（会员升级、开户支付订单）支付流水
     * @Author  Kaven
     * @Date   2020/10/21  11:14
     * @Param   payDto levelData product orderMap retCode payNo orderNo payChanel payWay
     * @Return
     * @Exception  BusinessException
    */
    public void generatePayWater(MemberAccountEntity member,RegOrderPayDTO payDto,MemberLevelEntity levelData,ProductEntity product,Map<String,Object> orderMap,String retCode,String orderNo) throws BusinessException {
        log.info("生成订单支付流水...");
        // 查询oem机构信息
        OemEntity oem = oemService.getOem(payDto.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }
        // 生成支付流水
        PayWaterEntity water = new PayWaterEntity();
        water.setOemCode(payDto.getOemCode());
        water.setOemName(oem.getOemName());
        if(OrderTypeEnum.UPGRADE.getValue().equals(payDto.getOrderType())){
            if(levelData == null){
                throw new BusinessException("支付失败，等级套餐信息不存在");
            }
            water.setOrderAmount(levelData.getPayMoney());
            water.setRemark(levelData.getLevelName());// 备注字段暂用来存储商品名称
        }else{
            water.setOrderAmount(product.getProdAmount());
            water.setRemark(product.getProdName());// 备注字段暂用来存储商品名称
        }
        water.setPayAmount(payDto.getAmount());
        water.setPayNo(orderMap.get("payNo").toString());
        water.setOrderNo(orderNo);
        water.setMemberId(payDto.getCurrUserId());
        water.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());

        // 解析返回data数据，拿到外部订单号
        if("00".equals(retCode)){
            JSONObject data = (JSONObject) orderMap.get("data");
            water.setExternalOrderNo(data.getString("tradeNo"));
        }
        water.setAddTime(new Date());
        water.setAddUser(member.getMemberAccount());
        water.setPayChannels(Integer.parseInt(orderMap.get("payChanel").toString()));
        water.setOrderType(payDto.getOrderType());// 支付通道
        water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
        water.setPayWay(Integer.parseInt(orderMap.get("payWay").toString()));// 支付方式
        water.setPayWaterType(PayWaterTypeEnum.THIRD.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
        water.setPayStatus("00".equals(retCode) ? PayWaterStatusEnum.PAYING.getValue() : PayWaterStatusEnum.PAY_FAILURE.getValue());
        this.payWaterService.insertSelective(water);
    }

    /**
     * @Description 构建微信支付挡板模拟返回参数
     * @Author  Kaven
     * @Date   2020/3/18 10:28
     * @Param   payNo
     * @Return  Map<String, Object>
     * @Exception
    */
    private Map<String, Object> simulateWechatPay(String payNo) {
        Map<String, Object> orderMap = new HashMap<String, Object>();
        JSONObject data = new JSONObject();
        data.put("payNo",payNo);
        data.put("tradeNo",System.currentTimeMillis());
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
        data.put("returnData",JSONObject.parse(retunData));
        data.put("merNo","M000000164");
        orderMap.put("data", data);
        orderMap.put("code","00");
        orderMap.put("msg","支付成功");
        return orderMap;
    }

    /**
     * @Description 构建支付宝支付挡板模拟返回参数
     * @Author  Kaven
     * @Date   2020/10/21 11:41
     * @Param   payNo
     * @Return  Map<String, Object>
     * @Exception
     */
    private Map<String, Object> simulateAliPay(String payNo) {
        Map<String, Object> orderMap = new HashMap<String, Object>();
        JSONObject data = new JSONObject();
        data.put("payNo",payNo);
        data.put("tradeNo",System.currentTimeMillis());
        data.put("merNo","M000000164");
        orderMap.put("data", data);
        orderMap.put("code","00");
        orderMap.put("msg","支付成功");
        return orderMap;
    }

    /**
     * 构建字节跳动支付挡板模拟返回参数
     * @param payNo
     * @return
     */
    private Map<String, Object> simulateBytedancePay(String payNo) {
        Map<String, Object> orderMap = new HashMap<String, Object>();
        JSONObject data = new JSONObject();
        data.put("payNo",payNo);
        data.put("order_id",payNo);
        data.put("order_token",System.currentTimeMillis());
        orderMap.put("data", data);
        orderMap.put("code","00");
        orderMap.put("msg","支付成功");
        return orderMap;
    }

    /**
     * @Description 支付预处理：支付参数校验
     * @Author  Kaven
     * @Date   2020/3/18 9:56
     * @Param  payDto
     * @Return Map<String,Object>
     * @Exception BusinessException
    */
    private Map<String,Object> assertBeforePay(RegOrderPayDTO payDto) {
        log.info("支付订单基本校验开始……");

        Map<String,Object> returnData = Maps.newHashMap();

        if(!("1".equals(payDto.getPayChannel()) || "3".equals(payDto.getPayChannel()) || "8".equals(payDto.getPayChannel()))){
            throw new BusinessException("不支持的支付方式，请联系管理员");
        }

        if(null == payDto.getCurrUserId()){
            throw new BusinessException("支付失败，用户ID不能为空");
        }

        if(payDto.getAmount() < 0){
            throw new BusinessException("支付失败，支付金额必须大于或等于0");
        }

        if(payDto.getOrderType() <= 0 || payDto.getOrderType() > 7){
            throw new BusinessException("支付失败，无效的订单类型");
        }

        // 查询产品信息是否存在
        MemberLevelEntity level = null;
        if(OrderTypeEnum.UPGRADE.getValue().equals(payDto.getOrderType())){
            // 查询会员等级套餐信息是否存在
            level = this.memberLevelService.findById(payDto.getProductId());
            if(null == level){
                throw new BusinessException("支付失败，等级套餐信息不存在");
            }
            returnData.put("levelData",level);
        } else {
            ProductEntity product = this.productService.findById(payDto.getProductId());
            if(null == product){
                throw new BusinessException("支付失败，产品信息不存在");
            }

            // 判断产品是否是上架状态
            if( !ProductStatusEnum.ON_SHELF.getValue().equals(product.getStatus())){
                throw new BusinessException("支付失败，产品【" + ProductStatusEnum.getByValue(product.getStatus()) + "】");
            }
            returnData.put("product",product);
        }

        // 判断用户信息是否存在
        MemberAccountEntity member = this.memberAccountService.findById(payDto.getCurrUserId());
        if(null == member){
            throw new BusinessException("支付失败，用户信息不存在");
        }

        returnData.put("member",member);

        // 判断会员等级
        MemberLevelEntity memberLevel = this.memberLevelService.findById(member.getMemberLevel());
        if(OrderTypeEnum.UPGRADE.getValue().equals(payDto.getOrderType())){
            // 如果是会员升级需要判断当前会员等级
            if(MemberLevelEnum.DIAMOND.getValue().equals(memberLevel.getLevelNo())){
                throw new BusinessException("支付失败，当前会员已是城市服务商不允许升级");
            }

            if(MemberLevelEnum.GOLD.getValue().equals(memberLevel.getLevelNo())){
                throw new BusinessException("支付失败，当前会员已是税务顾问不允许升级");
            }

            // 判断会员是否已实名
            if(!MemberAuthStatusEnum.AUTH_SUCCESS.getValue().equals(member.getAuthStatus())){
                throw new BusinessException("支付失败，您尚未实名，请先绑卡或上传身份证完成实名认证");
            }
        }

        // 开户订单支付需先判断订单状态
        OrderEntity order = null;
        if(OrderTypeEnum.REGISTER.getValue().equals(payDto.getOrderType())){
            order = this.orderService.queryByOrderNo(payDto.getOrderNo());// 开户订单
            if(null == order){
                throw new BusinessException("支付失败，开户订单不存在");
            }

            // 判断订单状态
            if(!RegOrderStatusEnum.TO_BE_PAY.getValue().equals(order.getOrderStatus())){
                throw new BusinessException("当前订单状态为【" + RegOrderStatusEnum.getByValue(order.getOrderStatus()).getMessage() + "】，不允许支付");
            }
            returnData.put("order",order);
        }

        // 会员升级时，验证支付金额的正确性
        if(null == order && null != level && !Objects.equals(payDto.getAmount(),level.getPayMoney())){
            throw new BusinessException("支付失败，订单支付金额不匹配，请核对");
        }
        // 开户订单支付时(未使用优惠券)
        if(null != order && !Objects.equals(payDto.getAmount(),order.getPayAmount()) && Objects.isNull(payDto.getCouponsIssueRecordId())){
                throw new BusinessException("支付失败，订单支付金额不匹配，请核对");
        }

        log.info("支付订单基本校验结束");
        return returnData;
    }

    /**
     * @Description 处理支付金额为0的订单
     * @Author  Kaven
     * @Date   2020/1/11 14:58
     * @Param  userId oemCode orderType productId orderNo
     * @Return ResultVo
     * @Exception
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject payZeroAmountOrder(RegOrderPayDTO payDto,String prodName) throws BusinessException {
        log.info("处理支付金额为0的订单，orderNo:{}",payDto.getOrderNo());
        JSONObject jsonObj = new JSONObject();
        try {
            OrderEntity order = null;
            if(OrderTypeEnum.REGISTER.getValue().equals(payDto.getOrderType())){// 开户订单支付
                order = this.orderService.queryByOrderNo(payDto.getOrderNo());
                if(null == order){
                    throw new BusinessException("支付失败，开户订单信息不存在");
                }
                // 根据不同园区编码更新订单主表状态
                Integer orderStatus = null;
//                ParkEntity park = this.parkService.findById(order.getParkId());
//                if(StringUtils.isNotBlank(park.getParkCode()) && park.getParkCode().startsWith("CSYQ")){
//                    orderStatus = RegOrderStatusEnum.TO_BE_VALIDATE.getValue();// 长沙园区直接置为"待身份验证"
//                } else {
//
//                }
                orderStatus = RegOrderStatusEnum.TO_BE_SURE.getValue();
                this.orderService.updateOrderStatus(null, payDto.getOrderNo(), orderStatus);

                // 若使用优惠券，需更新订单支付金额及分润金额
                if (Objects.nonNull(payDto.getCouponsIssueRecordId())) {
                    order.setPayAmount(payDto.getAmount());
                    order.setProfitAmount(payDto.getAmount());
                }

                // 开户订单后续处理
                this.successPayHandle(order,payDto.getCurrUserId(),orderStatus);

                // 接入方用户注册，费用承担方非本人时无需生成支付流水
                if (Objects.equals(2, order.getIsSelfPaying())) {
                    // 返回订单号
                    jsonObj.put("orderNo",order.getOrderNo());
                    jsonObj.put("isPaid","1");// 是否已支付标识，0 未支付 1已支付
                    return jsonObj;
                }
            }else {// 会员升级订单支付
                order = this.memberUpgradePaySuccess(payDto.getProductId(),prodName,payDto.getOemCode(),payDto.getOrderNo(),payDto.getCurrUserId(),1);

                // 分润处理，添加分润明细数据 add ni.jiang
                try {
                    profitsDetailService.saveProfitsDetailByOrderNo(order.getOrderNo(), "admin");
                }catch (Exception e){
                    //分润失败
                    log.error("分润失败：{}",e.getMessage());
                    order.setIsShareProfit(2);
                    order.setProfitStatus(3);
                    order.setUpdateTime(new Date());
                    order.setUpdateUser("admin");
                    order.setRemark("分润失败原因："+e.getMessage());
                    orderService.editByIdSelective(order);

                    // 短信通知紧急联系人
                    DictionaryEntity dict = this.dictionaryService.getByCode("emergency_contact");
                    if(null != dict){
                        String dicValue = dict.getDictValue();
                        String[] contacts = dicValue.split(",");
                        for(String contact : contacts){
                            Map<String,Object> map = new HashMap();
                            map.put("oemCode",order.getOemCode());
                            map.put("orderNo",payDto.getOrderNo());
                            this.smsService.sendTemplateSms(contact,order.getOemCode(), VerifyCodeTypeEnum.NOTICE.getValue(), map,1);
                            log.info("分润失败发送通知给【" + contact + "】成功");
                        }
                    }
                }
            }

            // 生成支付流水
            PayWaterEntity water = new PayWaterEntity();
            water.setOemCode(payDto.getOemCode());
            water.setOrderAmount(0L);
            water.setPayAmount(0L);
            water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成支付流水号
            water.setOrderNo(order.getOrderNo());
            water.setMemberId(payDto.getCurrUserId());
            water.setAddTime(new Date());
            water.setPayChannels(PayChannelEnum.WECHATPAY.getValue());
            water.setRemark(prodName);// 备注字段暂用来存储商品名称
            water.setOrderType(payDto.getOrderType());
            water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
            if("1".equals(payDto.getPayChannel())){
                water.setPayWay(PayWayEnum.WECHATPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
            } else if ("3".equals(payDto.getPayChannel())){
                water.setPayWay(PayWayEnum.ALIPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
            } else if ("8".equals(payDto.getPayChannel())){
                water.setPayWay(PayWayEnum.BYTEDANCE.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付 5-字节跳动
            }  else {
                throw new BusinessException("不支持的支付方式，请联系管理员");
            }
            water.setPayWaterType(PayWaterTypeEnum.THIRD.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
            water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            this.payWaterService.insertSelective(water);

            // 返回订单号
            jsonObj.put("orderNo",order.getOrderNo());
            jsonObj.put("isPaid","1");// 是否已支付标识，0 未支付 1已支付
        }catch (Exception e) {
            log.error("支付失败，" + e.getMessage());
            throw new BusinessException("支付失败，" + e.getMessage());
        }
        log.info("支付金额为0的订单处理结束...");
        return jsonObj;
    }

    @Override
    public void updateCusomerPhoneAndBusinessAddr(String orderNo, String customerServicePhone, String businessAddr,Integer isOpenAuthentication) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("orderNo",orderNo);
        params.put("updateTime",new Date());
        params.put("businessAddress",businessAddr);
        params.put("customerServicePhone",customerServicePhone);
        // 添加修改是否已开起标识
        if(null != isOpenAuthentication){
            params.put("isOpenAuthentication",isOpenAuthentication);
        }
        this.registerOrderMapper.updateSignOrVideoAddr(params);
    }

    @Override
    public WechatPayDto buildWechatParams(PayWaterEntity payWater) throws BusinessException{
        //读取渠道支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(payWater.getOemCode(),3);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道支付相关信息！");
        }
        // 组装参数对象
        WechatPayDto payDto = new WechatPayDto();
        payDto.setTradeNo(payWater.getExternalOrderNo());
        payDto.setAgentNo(paramsEntity.getAccount());
        // payDto.setAppSecret(paramsEntity.getSecKey());
        payDto.setServicePubKey(paramsEntity.getPublicKey());
        payDto.setPostUrl(paramsEntity.getUrl());

        // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        payDto.setKeyNum(params.getString("keyNum"));
        payDto.setSignKey(params.getString("signKey"));
        // 加解密方式设置
        if(StringUtil.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().trim().indexOf("\"channel\":\"new\"") > -1){
            payDto.setChannel("1");
        }
        return payDto;
    }
    @Override
    public WechatRefundDto buildRefundWechatParams(PayWaterEntity payWater) throws BusinessException{
        //读取渠道支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(payWater.getOemCode(),29);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道支付相关信息！");
        }
        // 组装参数对象
        WechatRefundDto wechatRefundDto = new WechatRefundDto();
        wechatRefundDto.setTradeNo(payWater.getPayNo());
        wechatRefundDto.setRefundOrderNo(payWater.getPayNo());
        wechatRefundDto.setServicePubKey(paramsEntity.getPublicKey());
        wechatRefundDto.setPostUrl(paramsEntity.getUrl());
        wechatRefundDto.setAgentNo(paramsEntity.getAccount());

        // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        wechatRefundDto.setKeyNum(params.getString("keyNum"));
        wechatRefundDto.setSignKey(params.getString("signKey"));
        // 加解密方式设置
        if(StringUtil.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().trim().indexOf("\"channel\":\"new\"") > -1){
            wechatRefundDto.setChannel("1");
        }
        return wechatRefundDto;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void successPayHandle(OrderEntity order, Long userId,Integer orderStatus) throws BusinessException {
        log.info("开户订单支付成功，开始处理订单后续业务……");

        String customerServicePhone = null;// 接单客服电话
        Integer isOpenAuthentication = null;// 是否已开启身份验证 0-未开启 1-已开启
        log.info("开户订单支付完成，进行自动派单和经营地址的生成:{}",order.getOrderNo());

        // 支付完成，进行自动派单和经营地址的生成
        ParkEntity park = this.parkService.findById(order.getParkId());
        // 长沙园区特殊处理
        if(Objects.equals(park.getProcessMark(), ParkProcessMarkEnum.IDENTITY.getValue())){
            // 创建一条新的首页弹窗通知
            log.info("===长沙园区支付完成，创建首页弹窗通知===");
            this.messageNoticeService.addNoticeIndex(userId,order.getOemCode(),order.getOrderNo());
            isOpenAuthentication = 0;
        }
        // 自动派单
        OemEntity oemEntity=oemService.getOem(order.getOemCode());
        if(oemEntity==null){
            throw new BusinessException("oem机构不存在");
        }
        String oemCodeConfig=null;
        if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
            oemCodeConfig=order.getOemCode();
        }
        Long recvOrderUserId = this.receiveOrderService.getReceiveServer(oemCodeConfig, order.getOrderNo(), 1,1).getRecvOrderUserId();
        if (null != recvOrderUserId) {
            // 派单成功，返回客服信息
            UserExtendEntity ext = new UserExtendEntity();
            ext.setUserId(recvOrderUserId);
            UserExtendEntity userExtend = this.userExtendMapper.selectOne(ext);
            if (null == userExtend) {
                throw new BusinessException("自动派单失败，未找到接单客服拓展信息");
            }
            customerServicePhone = userExtend.getPhone();
        }

        // 生成企业专属经营链接（需求变更）
        String businessAddr = this.parkBusinessAddressRulesService.builderBusinessAddressByPark(order.getParkId());

        // 更新客服和经营链接
        log.info("更新开户订单专属客服电话和经营链接地址：{},{},{}",order.getOrderNo(),customerServicePhone,businessAddr);

        this.updateCusomerPhoneAndBusinessAddr(order.getOrderNo(), customerServicePhone, businessAddr,isOpenAuthentication);

        // 更新订单主表待分润金额
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setId(order.getId());
        mainOrder.setUpdateTime(new Date());
        mainOrder.setPayAmount(order.getPayAmount());
        mainOrder.setProfitAmount(park.getIsRegisterProfit() == 1 ? order.getPayAmount() : 0L);
        mainOrder.setOrderStatus(orderStatus);
        this.orderService.editByIdSelective(mainOrder);

        //保存工商注册订单变更记录
        RegisterOrderEntity roe = new RegisterOrderEntity();
        roe.setOrderNo(order.getOrderNo());
        // 查找原始订单
        RegisterOrderEntity regOrder = this.selectOne(roe);
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(regOrder, record);// 参数拷贝
        record.setId(null);
        record.setCustomerServicePhone(customerServicePhone);
        record.setBusinessAddress(businessAddr);
        record.setAddTime(new Date());
        record.setAddUser(order.getAddUser());
        record.setOrderStatus(orderStatus);
        this.registerOrderChangeRecordMapper.insertSelective(record);

        /**
         *  工商注册和会员升级时，需添加资金变动流水出账和入账两条变动记录
         */

        log.info("开户订单支付成功，添加资金变动流水出账和入账两条变动记录");
        //会员账号添加可用资金
        this.userCapitalAccountService.addBalanceByProfits(order.getOemCode(),order.getOrderNo(),order.getOrderType(),userId,1,
                order.getPayAmount(),order.getPayAmount(),0L,0L,"订单[" + order.getOrderNo() + "]成功入账资金账户","admin",new Date(),1,WalletTypeEnum.CONSUMER_WALLET.getValue());

        //会员账号扣除资金
        this.userCapitalAccountService.addBalanceByProfits(order.getOemCode(),order.getOrderNo(),order.getOrderType(),userId,1,
                0L,order.getPayAmount(),0L,order.getPayAmount(),"订单[" + order.getOrderNo() + "]成功冻结资金账户","admin",new Date(),0,WalletTypeEnum.CONSUMER_WALLET.getValue());

        log.info("订单处理结束.");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject orderCheckBeforePay(OrderEntity order, MemberAccountEntity member) throws BusinessException {
        log.info("开户订单预处理，实时查询渠道订单查询接口，根据返回状态做出不同响应：{},{}",JSON.toJSONString(order),JSON.toJSONString(member));

        JSONObject dataObj = null;

        // 查询当前订单下状态为“支付中”的流水，正常情况下至多有一条记录
        PayWaterEntity t = new PayWaterEntity();
        t.setOrderNo(order.getOrderNo());
        t.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
        List<PayWaterEntity> waterList = this.payWaterService.select(t);

        // 若存在支付中的流水，调用渠道实时订单查询接口查询订单状态
        if(!CollectionUtils.isEmpty(waterList)){
            Map<String,Object> dataMap = Maps.newHashMap();// 接收渠道订单查询结果
            PayWaterEntity water = waterList.get(0);// 正常情况下最多有且只有一条记录
            // 根据支付订单类型查询渠道订单
            String isNewChannel = "";
            if(PayChannelEnum.WECHATPAY.getValue().equals(water.getPayChannels())){ // 微信订单
                WechatPayDto payDto = this.buildWechatParams(water);
                dataMap = WechatPayUtils.queryWxOrder(payDto);
                isNewChannel = payDto.getChannel();
            } else if(PayChannelEnum.ALIPAY.getValue().equals(water.getPayChannels())) { // 支付宝订单
                AliPayDto payDto = this.buildAliPayParams(water);
                dataMap = AliPayUtils.queryAliOrder(payDto);
                //支付宝只有新渠道
                isNewChannel = "1";
            }else if(PayChannelEnum.BYTEDANCE.getValue().equals(water.getPayChannels())) { // 字节跳动订单
                AliPayDto payDto = this.buildAliPayParams(water);
                dataMap = AliPayUtils.queryAliOrder(payDto);
                //支付宝只有新渠道
                isNewChannel = "1";
            } else {
                throw new BusinessException(ErrorCodeConstants.UN_SUPPORT_PAY_CHANNEL,"不支持的支付订单类型，请联系管理员！！");
            }

            // 解析返回结果
            String respCode = dataMap.get("code").toString();// 返回码
            String retMsg = dataMap.get("msg").toString();// 返回信息

            if (StringUtils.equals(respCode, "00")) {// 返回成功
                JSONObject data = new JSONObject();
                if("1".equals(isNewChannel)){
                    data = (JSONObject)dataMap.get("data");
                }else {
                    JSONArray dataArrays = (JSONArray) dataMap.get("data");
                    if (dataArrays.size() > 0) {
                        data = dataArrays.getJSONObject(0);
                    }
                }
                String orderStatus = data.getString("status");

                // 根据实时返回的订单状态（-1-交易失败", 0-交易初始化, 1-交易处理中, 3-交易超时,4-交易关闭,5-交易成功）做相应订单处理
                if("5".equals(orderStatus)) {
                    // 订单已支付成功，更新订单状态为“审核中”
                    this.orderService.updateOrderStatus(member.getMemberAccount(),order.getOrderNo(),RegOrderStatusEnum.TO_BE_SURE.getValue());

                    // 更新流水状态为“支付成功”
                    this.payWaterService.updatePayStatusByPayNo(water.getPayNo(),member.getMemberAccount(),PayWaterStatusEnum.PAY_SUCCESS.getValue(),orderStatus,retMsg);

                    // 处理订单后续业务并返回，如果是长沙园区，状态改为"待身份验证"
                    int regOrderStatus = RegOrderStatusEnum.TO_BE_SURE.getValue();// 默认取"审核中"状态
//                    ParkEntity park = this.parkService.findById(order.getParkId());
//                    if(StringUtils.isNotBlank(park.getParkCode()) && park.getParkCode().startsWith("CSYQ")){
//                        regOrderStatus = RegOrderStatusEnum.TO_BE_VALIDATE.getValue();
//                    }
                    this.successPayHandle(order,member.getId(),regOrderStatus);

                    dataObj = new JSONObject();
                    dataObj.put("isPaid","1");// 是否已支付标识，0 未支付 1已支付

                    log.info("开户订单支付成功后续逻辑处理完成，请求结束……");
                    return dataObj;
                }else if("1".equals(orderStatus) || "0".equals(orderStatus)){
                    // 流水状态不变，响应订单支付中
                    log.info("订单：{}已支付成功，支付成功流水号为：{}，不能继续支付",order.getOrderNo(),water.getPayNo());
                    throw new BusinessException(ErrorCodeConstants.EXIST_PAYING_ERROR,"该订单存在处理中的支付，请稍后再试！若长时间如此请联系客服！！");
                }else{
                    // 修改流水状态为“支付失败”
                    this.payWaterService.updatePayStatusByPayNo(water.getPayNo(),member.getMemberAccount(),PayWaterStatusEnum.PAY_FAILURE.getValue(),orderStatus,retMsg);
                }
            }else if (StringUtils.equals(respCode, "4018")){// 渠道返回交易不存在
                // 修改流水状态为“支付失败”
                this.payWaterService.updatePayStatusByPayNo(water.getPayNo(),member.getMemberAccount(),PayWaterStatusEnum.PAY_FAILURE.getValue(),respCode,retMsg);
            }else{
                // 返回失败，流水状态不变，响应订单支付中
                throw new BusinessException(ErrorCodeConstants.EXIST_PAYING_ERROR,"该订单存在处理中的支付，请稍后再试！若长时间如此请联系客服！！");
            }
        }
        return dataObj;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderEntity memberUpgradePaySuccess(Long productId, String prodName,String oemCode, String orderNo,Long userId,Integer operateType) throws BusinessException {
        log.info("会员升级订单支付成功处理开始：{}，{}，{}",productId,prodName,userId);

        OrderEntity order = null;

        if(operateType == 1){
            // 会员升级订单支付需要先生成订单号并入库
            orderNo = OrderNoFactory.getOrderCode(userId);// 订单号
            // 保存订单主表信息
            order = new OrderEntity();
            order.setOemCode(oemCode);
            order.setOrderNo(orderNo);
            order.setOrderAmount(0L);
            order.setPayAmount(0L);
            order.setProfitAmount(0L);
            order.setAuditStatus(0);// 默认审核状态为0 待审核
            order.setProductName(prodName);
            order.setProductId(productId);
            order.setOrderType(OrderTypeEnum.UPGRADE.getValue());
            // 保存会员订单关系，补全订单参数
            completionParameter(userId,order);
            order.setOrderStatus(MemberOrderStatusEnum.COMPLETED.getValue());// 直接置为“已完成”
            this.orderService.insertSelective(order);
        }else{
            order = this.orderService.queryByOrderNo(orderNo);
        }

        // 更新会员等级
        MemberLevelEntity level = this.memberLevelService.findById(order.getProductId());// 这里的productId实际上保存的就是会员等级表ID
        if(null != level){
            log.info("会员升级订单回调成功，更新会员相应等级：用户{},升级为{}",userId,level.getLevelName());
            MemberAccountEntity t = new MemberAccountEntity();
            t.setId(userId);
            t.setLevelName(level.getLevelName());
            t.setMemberLevel(level.getId());
            t.setUpdateTime(new Date());
            this.memberAccountService.editByIdSelective(t);
        } else {
            log.info("会员升级订单回调成功，但未找到会员升级套餐信息，升级失败……");
        }

        // 更新订单主表待分润金额
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setId(order.getId());
        mainOrder.setUpdateTime(new Date());
        mainOrder.setProfitAmount(order.getPayAmount());
        this.orderService.editByIdSelective(mainOrder);

        /**
         *  工商注册和会员升级时，需添加资金变动流水出账和入账两条变动记录
         */
        log.info("会员升级回调成功，往OEM机构账户入账");

        //会员账号添加可用资金
        this.userCapitalAccountService.addBalanceByProfits(order.getOemCode(),orderNo,order.getOrderType(),userId,1,
                order.getPayAmount(),order.getPayAmount(),0L,0L,"订单["+orderNo+"]成功入账资金账户","admin",new Date(),1,WalletTypeEnum.CONSUMER_WALLET.getValue());

        //会员账号扣除资金
        this.userCapitalAccountService.addBalanceByProfits(order.getOemCode(),orderNo,order.getOrderType(),userId,1,
                order.getPayAmount(),order.getPayAmount(),0L,0L,"订单["+orderNo+"]成功扣除资金账户","admin",new Date(),0,WalletTypeEnum.CONSUMER_WALLET.getValue());

        //oem机构资金账号入账
        UserEntity entity = new UserEntity();
        entity.setPlatformType(2);
        entity.setAccountType(1);
        entity.setOemCode(order.getOemCode());
        entity = userService.selectOne(entity);
        this.userCapitalAccountService.addBalanceByProfits(order.getOemCode(),orderNo,order.getOrderType(),entity.getId(),2,
                order.getPayAmount(),order.getPayAmount(),0L,0L,"会员升级成功入账oem资金账户","admin",new Date(),1,WalletTypeEnum.CONSUMER_WALLET.getValue());

        // 会员升级成功，统计会员日推广数据
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        this.orderService.statisticsMemberGeneralize(order,member.getMemberAccount(),1);
        log.info("会员升级订单支付成功处理结束");
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, String orderNo) throws BusinessException {
        // 查询用户是否存在
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("取消失败，用户信息不存在");
        }
        // 查询订单信息
        OrderEntity order = this.orderService.queryByOrderNo(orderNo);
        if(null == order){
            throw new BusinessException("取消失败，订单不存在");
        }
        OrderEntity t = new OrderEntity();
        t.setId(order.getId());
        t.setUpdateTime(new Date());

        // 根据订单类型更新不同状态：订单类型 1-充值 5-工商开户 7-用户升级
        if(OrderTypeEnum.UPGRADE.getValue().equals(order.getOrderType())){
            t.setOrderStatus(MemberOrderStatusEnum.CANCELLED.getValue());
            this.orderService.editByIdSelective(t);
        }else if(OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType())){
            // 开户订单特殊处理，不用修改订单状态
        }else if(OrderTypeEnum.ENCHARGE.getValue().equals(order.getOrderType())){
            t.setOrderStatus(RACWStatusEnum.CANCELED.getValue());
            this.orderService.editByIdSelective(t);
        }

        // 修改支付流水状态为“支付失败”
        PayWaterEntity water = new PayWaterEntity();
        water.setOrderNo(orderNo);
        water.setUpdateTime(new Date());
        water.setUpdateUser(member.getMemberAccount());
        water.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
        this.payWaterService.updatePayStatus(water);

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
                issueRecord.setRemark("取消支付，回滚优惠券");
                if (couponsEntity.getEndDate().before(new Date())) {
                    issueRecord.setStatus(CouponsIssueRecordStatusEnum.STALE.getValue());
                } else {
                    issueRecord.setStatus(CouponsIssueRecordStatusEnum.UNUSED.getValue());
                }
                couponsIssueRecordMapper.updateByPrimaryKey(issueRecord);
                // 回滚订单支付金额
                Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(member.getId(), order.getProductId(), member.getOemCode(), order.getParkId());
                order.setPayAmount(payAmount);
                order.setProfitAmount(payAmount);
                orderService.editByIdSelective(order);
                // 回滚注册订单支付金额
                registerOrder.setPayAmount(payAmount);
                registerOrder.setCouponsIssueId(null);
                registerOrderMapper.updateByPrimaryKey(registerOrder);
                // 回滚注册订单变更记录支付金额
                RegisterOrderChangeRecordEntity recordEntity = new RegisterOrderChangeRecordEntity();
                BeanUtils.copyProperties(registerOrder, recordEntity);// 参数拷贝
                recordEntity.setId(null);
                recordEntity.setAddTime(new Date());
                recordEntity.setAddUser(member.getMemberAccount());
                recordEntity.setOrderStatus(RegOrderStatusEnum.TO_BE_PAY.getValue());
                this.registerOrderChangeRecordMapper.insertSelective(recordEntity);
            }
        }
    }

    /**
     * @Description 补全订单参数
     * @Author  Kaven
     * @Date   2019/12/13 16:07
     * @Param userId order
    */
    public void completionParameter(Long userId,OrderEntity order) {
        // 查询oem机构信息
        OemEntity oem = this.oemService.getOem(order.getOemCode());
        if(null == oem){
            throw new BusinessException("未查询到oem机构");
        }
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(member==null){
            throw new BusinessException("会员信息不存在");
        }
        // 保存会员订单关系
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(userId, order.getOemCode(),3);//获取一二级推广人和分润信息
        if(more != null){
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
        if(more != null){
            order.setRelaId(more.getId());
        }
        order.setUserId(userId);
        order.setUserType(member.getMemberType());
        order.setAddUser(member.getMemberAccount());
        order.setAddTime(new Date());
        order.setOrderStatus(MemberOrderStatusEnum.TO_BE_PAY.getValue());
        order.setChannelProductCode(member.getChannelProductCode());
        order.setChannelCode(member.getChannelCode());
        order.setChannelEmployeesId(member.getChannelEmployeesId());
        order.setChannelServiceId(member.getChannelServiceId());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(order.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            order.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            order.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }
    }

    @Override
    public PageResultVo<RegOrderVO> queryRegOrderPage(TZBOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        List<RegOrderVO> list = this.registerOrderMapper.queryRegistOrderList(query);

        // 遍历集合，返回已完成状态订单的企业ID
        list.stream().forEach(regOrder -> {
            if(RegOrderStatusEnum.COMPLETED.getValue().intValue() == regOrder.getOrderStatus().intValue()){
                // 查询企业
                MemberCompanyEntity t = new MemberCompanyEntity();
                t.setOrderNo(regOrder.getOrderNo());
                MemberCompanyEntity company = this.memberCompanyMapper.selectOne(t);
                regOrder.setCompanyId(null == company ? null : company.getId());
            }
        });
        PageInfo<RegOrderVO> pageInfo = new PageInfo<RegOrderVO>(list);

        PageResultVo<RegOrderVO> result = new PageResultVo<RegOrderVO>();
        result.setList(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageSize(query.getPageSize());
        result.setPageNum(query.getPageNumber());
        result.setOrderBy("createTime DESC");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resubmitRegOrder(Long userId, ResubmitRegOrderDTO entity) throws BusinessException {
        log.info("开始处理重新提交企业注册订单的请求：{}，{},{}",userId,JSON.toJSONString(entity));

        // 校验订单是否存在
        OrderEntity order = this.orderService.queryByOrderNo(entity.getOrderNo());
        if(null == order){
            throw new BusinessException("提交失败，原订单信息不存在");
        }

        // 订单状态校验
        if(!RegOrderStatusEnum.REJECTED.getValue().equals(order.getOrderStatus())){
            throw new BusinessException("当前订单状态为【" + RegOrderStatusEnum.getByValue(order.getOrderStatus()).getMessage() + "】，不允许重新提交！");
        }

        // 校验会员账号
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("提交失败，会员信息不存在");
        }
        if(!member.getId().equals(order.getUserId())){
            throw new BusinessException("非法操作【订单所属会员不是当前操作人】");
        }

        // 查询校验注册订单
        RegisterOrderEntity regOrder = Optional.ofNullable(registerOrderMapper.queryByOrderNo(entity.getOrderNo())).orElseThrow(() -> new BusinessException("注册订单不存在"));

        // 校验驳回项
        String rejectedItems = regOrder.getRejectedItem();
        // 驳回项为空(不包括空字符串)时，为旧版本数据，默认核名驳回
        if (null == rejectedItems) {
            rejectedItems = "1";
        }
        String[] rejectedItem = rejectedItems.split(",");

        // 根据驳回项校验参数(1-字号 2-身份证 3-视频)
        String bucketName = this.dictionaryService.getByCode("oss_privateBucketName").getDictValue();
        for (int i = 0; i < rejectedItem.length; i++) {
            if (rejectedItem[i].equals(RejectedItemEnum.SHOP.getValue().toString())) {
                // 校验字号
                checkShopName(entity,regOrder,order.getParkId());
                // 更新字号
                log.info("字号校验通过，更新字号字段……");
                regOrder.setShopName(entity.getShopName());
                regOrder.setShopNameOne(entity.getShopNameOne());
                regOrder.setShopNameTwo(entity.getShopNameTwo());
            } else if (rejectedItem[i].equals(RejectedItemEnum.ID_CARD.getValue().toString())) {
                // 校验身份证（参数必要性校验、oss文件是否存在校验）
                if (StringUtil.isBlank(entity.getIdCardFront()) || StringUtil.isBlank(entity.getIdCardReverse())) {
                    throw new BusinessException("未获取到身份证照地址");
                }
                boolean exists_front = this.ossService.doesObjectExist(entity.getIdCardFront(),bucketName);
                if(!exists_front){
                    throw new BusinessException(ErrorCodeEnum.OSS_IDCARDFRONT_NOT_EXIST);
                }
                boolean exists_back = this.ossService.doesObjectExist(entity.getIdCardReverse(),bucketName);
                if(!exists_back){
                    throw new BusinessException(ErrorCodeEnum.OSS_IDCARDBACK_NOT_EXIST);
                }
                // 更新身份证
                log.info("身份证校验通过，更新身份证字段……");
                regOrder.setIdCardFront(entity.getIdCardFront());
                regOrder.setIdCardReverse(entity.getIdCardReverse());
            } else if (rejectedItem[i].equals(RejectedItemEnum.VIDEO_ADDR.getValue().toString())) {
                // 校验视频(参数必要性校验、oss文件是否存在校验)
                //Optional.ofNullable(entity.getVideoAddr()).orElseThrow(() -> new BusinessException("未获取到视频地址"));
                if(StringUtils.isBlank(entity.getVideoAddr())){
                    throw new BusinessException("未获取到视频地址");
                }
                boolean exists = this.ossService.doesObjectExist(entity.getVideoAddr(),bucketName);
                if(!exists){
                    throw new BusinessException(ErrorCodeEnum.OSS_VIDEO_NOT_EXIST);
                }
                // 更新视频
                log.info("视频校验通过，更新视频字段……");
                regOrder.setVideoAddr(entity.getVideoAddr());
            }
        }

        // 补充注册订单数据
        regOrder.setUpdateTime(new Date());
        regOrder.setUpdateUser(member.getMemberAccount());

        // 更新订单主表状态
        entity.setOrderStatus(RegOrderStatusEnum.TO_BE_SURE.getValue());// 赋值订单状态，外部调用需要返回
        this.orderService.updateOrderStatus(member.getMemberAccount(),order.getOrderNo(),RegOrderStatusEnum.TO_BE_SURE.getValue());
        // 自动派单
        OemEntity oemEntity=oemService.getOem(order.getOemCode());
        if(oemEntity==null){
            throw new BusinessException("oem机构不存在");
        }
        String oemCodeConfig=null;
        if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
            oemCodeConfig=order.getOemCode();
        }
        Long recvOrderUserId = this.receiveOrderService.getReceiveServer(oemCodeConfig, order.getOrderNo(), 1,1).getRecvOrderUserId();

        String customerServicePhone = null;// 接单客服电话
        if (null != recvOrderUserId) {
            // 派单成功，返回客服信息
            UserExtendEntity ext = new UserExtendEntity();
            ext.setUserId(recvOrderUserId);
            UserExtendEntity userExtend = this.userExtendMapper.selectOne(ext);
            if (null == userExtend) {
                throw new BusinessException("自动派单失败，未找到接单客服拓展信息");
            }
            customerServicePhone = userExtend.getPhone();
            // 更新接单客服电话
            regOrder.setCustomerServicePhone(customerServicePhone);
        }
        this.updateOrderById(regOrder);

        log.info("重新提交企业注册订单的请求处理结束。");
    }

    @Override
    public Map<String, Object> queryShopName(String orderNo, Long userId) throws BusinessException {
        // 查询订单是否存在
        OrderEntity order = this.orderService.queryByOrderNo(orderNo);
        if(null == order){
            throw new BusinessException("字号查询失败，订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException("该订单不属于当前登录用户");
        }
        RegisterOrderEntity t = new RegisterOrderEntity();
        t.setOrderNo(orderNo);
        RegisterOrderEntity regOrder = this.selectOne(t);
        Map<String,Object> dataMap = Maps.newHashMap();
        dataMap.put("shopName",regOrder.getShopName());
        dataMap.put("shopNameOne",regOrder.getShopNameOne());
        dataMap.put("shopNameTwo",regOrder.getShopNameTwo());
        return dataMap;
    }

    @Override
    public void updateOrderById(RegisterOrderEntity tt) {
        this.registerOrderMapper.updateOrderById(tt);
    }

    @Override
    public List<String> queryTobeAuthRegOrder(Long memberId, String oemCode) {
        List<String> orders = new ArrayList<String>();
        List<RegisterOrderEntity> lists =  this.registerOrderMapper.queryTobeAuthRegOrder(memberId, oemCode);
        if(CollectionUtil.isNotEmpty(lists)){
            orders = lists.stream().map(RegisterOrderEntity::getOrderNo).collect(Collectors.toList());
        }
        return orders;
    }

    @Override
    @Transactional
    public void setOrderAlertNum(Long memberId, String orderNo) throws BusinessException {
        if(StrUtil.isEmpty(orderNo)){
            throw new BusinessException("订单号不能为空");
        }

        // 查询会员账号
        MemberAccountEntity member = this.memberAccountService.findById(memberId);
        if(null == member){
            throw new BusinessException("未查询到员账号");
        }

        RegisterOrderEntity regOrder = registerOrderMapper.queryByOrderNo(orderNo);
        if(null == regOrder){
            throw new BusinessException("未查询到订单");
        }
        if(regOrder.getAlertNumber() == 0){
            regOrder.setAlertNumber(1);
            regOrder.setUpdateTime(new Date());
            regOrder.setUpdateUser(member.getMemberAccount());
            registerOrderMapper.updateByPrimaryKeySelective(regOrder);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureValidate(String orderNo, Long userId, String oemCode) throws BusinessException {
        log.info("确认已开启，修改开户订单状态并自动派单：{},{},{}",orderNo,userId,oemCode);

        // 用户校验
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("操作失败，当前登录" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        // 订单验证
        OrderEntity order = this.orderService.queryByOrderNo(orderNo);
        if(null == order){
            throw new BusinessException("操作失败，订单不存在");
        }
        RegisterOrderEntity t = new RegisterOrderEntity();
        t.setOrderNo(orderNo);
        RegisterOrderEntity regOrder = this.selectOne(t);
        if(regOrder.getIsOpenAuthentication().intValue() == 1){
            throw new BusinessException("当前订单已开启身份验证，请勿重复操作");
        }

        log.info("更新订单确认已开启状态:{}",orderNo);
        t.setId(regOrder.getId());
        t.setUpdateTime(new Date());
        t.setUpdateUser(member.getMemberAccount());
        t.setIsOpenAuthentication(1);
        this.editByIdSelective(t);

        // 根据订单号和是否已弹窗字段（未弹窗）删除当前通知数据
        log.info("根据订单号和是否已弹窗字段删除当前通知数据");
        this.messageNoticeService.deleteByOrderNo(order.getOrderNo(),0);
    }

    @Override
    @Transactional
    public void updateWechatAuthFlagByOrderNo(List<OrderWechatAuthRelaDTO> lists, Long userId, String oemCode) throws BusinessException {
        // 用户校验
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("操作失败，当前登录" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        for (OrderWechatAuthRelaDTO dto : lists) {
            // 订单验证
            RegisterOrderEntity order = this.registerOrderMapper.queryByOrderNo(dto.getOrderNo());
            if (null == order){
                throw new BusinessException("操作失败，注册订单不存在");
            }
            OrderEntity orderEntity = orderService.queryByOrderNo(dto.getOrderNo());
            if (null == orderEntity){
                throw new BusinessException("操作失败，订单不存在");
            }
            if (!Objects.equals(orderEntity.getUserId(), userId)) {
                throw new BusinessException("操作失败，订单不属于当前用户");
            }
            orderWechatAuthRelaService.addOrUpdate(dto.getOrderNo(), dto.getWechatTmplType(), dto.getAuthStatus(), member, oemCode);
        }
    }

    public InvPayInfoVo queryPayInfoByOrderNo(String orderNo){
        return this.mapper.queryPayInfoByOrderNo(orderNo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegOrderReturnVO createRegOrderForOuter(OuterRegOrderDTO entity) throws BusinessException, IOException {
        log.info("开始处理工商注册请求：{}",JSON.toJSONString(entity));

        // 外部订单号校验
        OrderEntity t = new OrderEntity();
        t.setExternalOrderNo(entity.getExternalOrderNo());
        List<OrderEntity> list = this.orderService.select(t);
        if(CollectionUtil.isNotEmpty(list)){
            throw new BusinessException(ErrorCodeEnum.EXTERNAL_ORDER_IS_EXIST);
        }

        // 查询oem机构信息
        OemEntity oem = oemService.getOem(entity.getOemCode());
        if (null == oem) {
            throw new BusinessException(ErrorCodeEnum.OEMCODE_ERROR);
        }
        if(StringUtils.isBlank(oem.getInviterAccount())){
            throw new BusinessException(ErrorCodeEnum.OEM_INVITOR_ERROR);
        }
        entity.setOemName(oem.getOemName());// 赋值机构名称，保存订单数据时需要

        // 校验身份证正反面、签名图片或视频地址是否存在
        checkImgUrlExists(entity);

        // 判断手机号是否已注册，若未注册则先注册用户
        MemberAccountEntity member = this.memberAccountService.queryByAccount(entity.getRegPhone(),entity.getOemCode());
        entity.setIsOther(0); // 默认0-本人办理
        if (member == null) {
            // 用户未注册，则先注册用户
            member = this.memberAccountService.registerAccount(entity.getRegPhone(),entity.getOemCode(),oem.getInviterAccount(), MemberLevelEnum.NORMAL.getValue(),null,entity.getOperatorName(),0,null,null);
        } else if(!entity.getIdCardNumber().equals(member.getIdCardNo())){ // 如果手机号已注册且身份证号码与已保存的数据不一致，则设置为"为他人办理"
            entity.setIsOther(1);
        }
        // 实名认证
        UserAuthDTO userAuthDto = buildUserAuthParams(entity);
        this.memberAccountService.userAuth(member.getId(),member.getOemCode(),userAuthDto,1);

        // 创建企业注册订单：默认为自己办理，订单状态为"待客服确认"，并进行自动派单
        String orderNo = this.createRegOrder(member,entity);

        // 自动派单
        String customerServicePhone = null;// 接单客服电话
        OemEntity oemEntity=oemService.getOem(entity.getOemCode());
        if(oemEntity==null){
            throw new BusinessException("oem机构不存在");
        }
        String oemCodeConfig=null;
        if(oemEntity.getWorkAuditWay()!=null&&oemEntity.getWorkAuditWay()==2){
            oemCodeConfig=entity.getOemCode();
        }
        Long recvOrderUserId = this.receiveOrderService.getReceiveServer(oemCodeConfig, orderNo, 1,1).getRecvOrderUserId();
        if (null != recvOrderUserId) {
            // 派单成功，返回客服信息
            UserExtendEntity ext = new UserExtendEntity();
            ext.setUserId(recvOrderUserId);
            UserExtendEntity userExtend = this.userExtendMapper.selectOne(ext);
            if (null == userExtend) {
                throw new BusinessException(ErrorCodeEnum.WORK_ORDER_ERROR);
            }
            customerServicePhone = userExtend.getPhone();
        }

        // 更新接单客服
        log.info("更新接单客服：{},{}",orderNo,customerServicePhone);
        this.updateCusomerPhoneAndBusinessAddr(orderNo,customerServicePhone,null,1);
        // 封装返回信息
        RegOrderReturnVO regOrderReturn = new RegOrderReturnVO();
        regOrderReturn.setOrderNo(orderNo);
        regOrderReturn.setCustomerServicePhone(customerServicePhone);
        regOrderReturn.setOrderStatus(RegOrderStatusEnum.TO_BE_SURE.getValue());
        regOrderReturn.setAddTime(entity.getAddTime());

        log.info("工商注册请求处理结束。");
        return regOrderReturn;
    }

    /**
     * @Description 构建用户实名认证参数
     * @Author  Kaven
     * @Date   2020/7/30 16:43
     * @Param   OuterRegOrderDTO
     * @Return  UserAuthDTO
     * @Exception
    */
    private UserAuthDTO buildUserAuthParams(OuterRegOrderDTO entity) {
        UserAuthDTO authDTO = new UserAuthDTO();
        authDTO.setUserName(entity.getOperatorName());
        authDTO.setExpireDate(entity.getExpireDate());
        authDTO.setIdCardAddr(entity.getIdCardAddr());
        authDTO.setIdCardNo(entity.getIdCardNumber());
        authDTO.setIdCardFront(entity.getIdCardFront());
        authDTO.setIdCardBack(entity.getIdCardReverse());
        authDTO.setIsOther(entity.getIsOther());
        return authDTO;
    }

    /**
     * @Description 校验文件地址是否存在于oss服务器
     * @Author  Kaven
     * @Date   2020/7/29 17:21
     * @Param   OuterRegOrderDTO
     * @Return
     * @Exception
    */
    private void checkImgUrlExists(OuterRegOrderDTO entity) {
        String bucketName = this.dictionaryService.getByCode("oss_privateBucketName").getDictValue();
        if(StringUtil.isNotBlank(entity.getVideoAddr())){
            // 判断文件地址是否存在
            boolean exists = this.ossService.doesObjectExist(entity.getVideoAddr(),bucketName);
            if(!exists){
                throw new BusinessException(ErrorCodeEnum.OSS_VIDEO_NOT_EXIST);
            }
        }
        boolean exists_front = this.ossService.doesObjectExist(entity.getIdCardFront(),bucketName);
        if(!exists_front){
            throw new BusinessException(ErrorCodeEnum.OSS_IDCARDFRONT_NOT_EXIST);
        }
        boolean exists_back = this.ossService.doesObjectExist(entity.getIdCardReverse(),bucketName);
        if(!exists_back){
            throw new BusinessException(ErrorCodeEnum.OSS_IDCARDBACK_NOT_EXIST);
        }
        boolean exists_signimg = this.ossService.doesObjectExist(entity.getSignImg(),bucketName);
        if(!exists_signimg){
            throw new BusinessException(ErrorCodeEnum.OSS_SIGNIMG_NOT_EXIST);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createRegOrder(MemberAccountEntity member, OuterRegOrderDTO orderDto) throws BusinessException {
        log.info("开始创建工商注册订单：{},{}",JSON.toJSONString(member),JSON.toJSONString(orderDto));
        // 开户订单前置校验
        assertRegOrder(member,orderDto);

        // 示例名称生成
        IndustryEntity industry = this.industryService.findById(orderDto.getIndustryId());
        String exmpleName = orderDto.getParkCity() + industry.getExampleName();
        orderDto.setRegisteredName(exmpleName);

        // 查询会员等级
        MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());

        // 保存会员订单关系
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(member.getId(), orderDto.getOemCode(), 1);
        if (more != null) {
            more.setMemberId(member.getId());
            // 设置会员等级
            more.setMemberLevel(level.getLevelNo());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            more.setOemCode(orderDto.getOemCode());
            more.setOemName(orderDto.getOemName());
            this.memberOrderRelaService.insertSelective(more);
        }

        // 生成订单号，订单信息入库
        String orderNo = OrderNoFactory.getOrderCode(member.getId());
        // 保存订单主表信息
        orderDto.setOrderAmount(orderDto.getProdAmount());// 订单金额取产品金额
        OrderEntity mainOrder = transferMainOrder2Entity(member.getId(), orderDto);
        mainOrder.setUserType(member.getMemberType());
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setAddUser(member.getMemberAccount());
        mainOrder.setOrderNo(orderNo);

        log.info("订单主表入库：{}",JSON.toJSONString(mainOrder));
        orderDto.setAddTime(mainOrder.getAddTime());// 设置订单创建时间
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(orderDto.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }
        this.orderService.insertSelective(mainOrder);

        // 保存工商订单信息
        RegisterOrderEntity regOrder = new RegisterOrderEntity();
        BeanUtils.copyProperties(orderDto,regOrder);// copy参数
        regOrder.setAddTime(new Date());
        regOrder.setAlertNumber(0);// 默认通知次数为0
        regOrder.setOrderNo(orderNo);
        regOrder.setCompanyType(orderDto.getProdType());
        regOrder.setAddUser(member.getMemberAccount());
        regOrder.setContactPhone(orderDto.getRegPhone());
        // 生成企业专属经营链接地址
        String businessAddr = this.parkBusinessAddressRulesService.builderBusinessAddressByPark(orderDto.getParkId());
        regOrder.setBusinessAddress(businessAddr);
        // 经营范围
        List<BusinessScopeEntity> scopes = this.businessScopeService.listBusinessScope(orderDto.getIndustryId());
        regOrder.setBusinessScope(CollectionUtil.isEmpty(scopes) ? null : scopes.get(0).getBusinessContent());

        log.info("工商注册订单表入库：{}",JSON.toJSONString(regOrder));
        this.insertSelective(regOrder);

        //保存工商注册订单变更记录
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(regOrder, record);
        record.setId(null);
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setOrderStatus(RegOrderStatusEnum.TO_BE_SURE.getValue());
        this.registerOrderChangeRecordMapper.insertSelective(record);

        // 保存企业开票类目
        this.companyInvoiceCategoryService.addByIndustryId(orderDto.getOemCode(), orderNo, orderDto.getIndustryId(), member.getMemberAccount());

        return orderNo;
    }

    @Override
    public Map<String, Object> fileUpload(FileUploadDTO uploadDto, String oemCode) throws BusinessException, IOException {
        log.info("开始文件上传处理：{},{}",JSON.toJSONString(uploadDto),oemCode);

        // 文件格式和大小校验
        // 获取文件大小
        int fileSize = ImageUtils.obtainImgSize(uploadDto.getFile());
        // 获取最后一个.的位置
        int lastIndexOf = uploadDto.getFileName().lastIndexOf(".");
        if(lastIndexOf == -1){
            throw new BusinessException(ErrorCodeEnum.OSS_IMG_FILENAME_ERROR);
        }
        //获取文件的后缀名
        String suffix = uploadDto.getFileName().substring(lastIndexOf);
        // 校验文件类型 1-图片 2-视频
        if(uploadDto.getFileType().intValue() == 1) {
            if(!(".jpg".equalsIgnoreCase(suffix) || ".jpeg".equalsIgnoreCase(suffix) || ".png".equalsIgnoreCase(suffix) || ".bmp".equalsIgnoreCase(suffix))){
                throw new BusinessException(ErrorCodeEnum.OSS_IMG_FORMAT_ERROR);
            }
            long maxImgSize = Integer.valueOf(dictionaryService.getByCode("oss_maxFileSize").getDictValue()).longValue() * 1024L * 1024L;// 支持最大图片大小
            if(fileSize > maxImgSize){
                throw new BusinessException(ErrorCodeEnum.OSS_IMG_SIZE_ERROR);
            }
        } else if(uploadDto.getFileType() == 2){
            // 文件格式校验
            if(!".mp4".equalsIgnoreCase(suffix)){
                throw new BusinessException(ErrorCodeEnum.OSS_VIDEO_FORMAT_ERROR);
            }
            long maxVideoSize = Integer.valueOf(dictionaryService.getByCode("oss_maxVideoSize").getDictValue()).longValue() * 1024L * 1024L;// 支持最大视频大小
            if(fileSize > maxVideoSize){
                throw new BusinessException(ErrorCodeEnum.OSS_VIDEO_SIZE_ERROR);
            }
        } else {
            throw new BusinessException(ErrorCodeEnum.OSS_FILE_TYPE_ERROR);
        }

        // oss存储目录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dir = oemCode + "/" + sdf.format(new Date()) + "/";
        String fileUrl = dir + System.currentTimeMillis() + "_" + uploadDto.getFileName();

        log.info("上传文件至oss服务器：{}",fileUrl);

        //上传文件至oss私域
        byte[] file_bytes = new BASE64Decoder().decodeBuffer(uploadDto.getFile());
        this.ossService.upload(fileUrl,file_bytes);

        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("fileUrl",fileUrl);

        log.info("文件上传处理结束。");
        return dataMap;
    }

    /**
     * @Description 订单主表实体转换
     * @Author  Kaven
     * @Date   2020/7/17 16:38
     * @Param   userId  OuterRegOrderDTO
     * @Return  OrderEntity
     * @Exception
    */
    private OrderEntity transferMainOrder2Entity(Long userId, OuterRegOrderDTO orderDto) {
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setAddTime(new Date());
        mainOrder.setUserId(userId);
        mainOrder.setExternalOrderNo(orderDto.getExternalOrderNo());// 外部订单号
        mainOrder.setOrderType(OrderTypeEnum.REGISTER.getValue());
        mainOrder.setOrderStatus(RegOrderStatusEnum.TO_BE_SURE.getValue());
        mainOrder.setProductId(orderDto.getProductId());
        mainOrder.setProductName(orderDto.getProductName());

        mainOrder.setOemCode(orderDto.getOemCode());
        mainOrder.setParkId(orderDto.getParkId());
        mainOrder.setOrderAmount(orderDto.getOrderAmount());
        mainOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());

        // 计算订单支付金额，如有折扣
        Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(userId, orderDto.getProductId(), orderDto.getOemCode(), orderDto.getParkId());
        mainOrder.setPayAmount(payAmount);
        mainOrder.setDiscountAmount(orderDto.getOrderAmount() - payAmount);//优惠金额
        return mainOrder;
    }

    /**
     * @Description 开户订单前置校验
     * @Author  Kaven
     * @Date   2020/7/17 15:00
     * @Param   MemberAccountEntity OuterRegOrderDTO
     * @Return
     * @Exception
    */
    private void assertRegOrder(MemberAccountEntity member, OuterRegOrderDTO orderDto) {
        log.info("开户订单校验开始：{}", JSON.toJSONString(orderDto));

        // 企业类型默认取1-个体开户
        if(null == orderDto.getCompanyType()){
            orderDto.setCompanyType(1);
        }
        // 查询产品信息
        ProductEntity product = this.productService.queryProductByProdType(orderDto.getCompanyType(),orderDto.getOemCode(), null);
        if (null == product) {
            throw new BusinessException(ErrorCodeEnum.INVALID_COMPANY_TYPE);
        }

        // 判断产品类型
        if (product.getProdType() > ProductTypeEnum.LIMITED_LIABILITY.getValue()) {
            throw new BusinessException(ErrorCodeEnum.INVALID_COMPANY_TYPE);
        }

        // 产品参数设置
        orderDto.setProductId(product.getId());
        orderDto.setProductName(product.getProdName());
        orderDto.setProdAmount(product.getProdAmount());
        orderDto.setProdType(product.getProdType());

        // 查询园区信息
        List<ParkEntity> parkList = this.parkService.getParkByParkCode(orderDto.getParkCode(),null);
        if(CollectionUtil.isEmpty(parkList)){
            throw new BusinessException(ErrorCodeEnum.PARK_NOT_EXIST);
        }
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(parkList.get(0).getStatus())) {
            throw new BusinessException(ErrorCodeEnum.PARK_STATUS_ERROR);
        }
        orderDto.setParkId(parkList.get(0).getId());// 设置园区ID
        orderDto.setParkCity(parkList.get(0).getParkCity());

        // 查询园区是否属于当前OEM机构
        OemParkRelaEntity t = new OemParkRelaEntity();
        t.setParkId(orderDto.getParkId());
        t.setOemCode(orderDto.getOemCode());
        List<OemParkRelaEntity> opreList = this.oemParkRelaService.select(t);
        if (null == opreList || opreList.size() == 0) {
            throw new BusinessException(ErrorCodeEnum.PARK_OEM_ERROR);
        }

        // 园区经营地址规则配置校验  add by Kaven 2020-05-22
        ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity = new ParkBusinessAddressRulesEntity();
        parkBusinessAddressRulesEntity.setParkId(orderDto.getParkId());
        List<ParkBusinessAddressRulesEntity> list = this.parkBusinessAddressRulesMapper.select(parkBusinessAddressRulesEntity);
        if (null == list || list.size() != 1) {
            throw new BusinessException(ErrorCodeEnum.PARK_BUSINESSADDR_ERROR);
        }

        // 判断产品和园区关系是否正确
        ProductParkRelaEntity ppre = new ProductParkRelaEntity();
        ppre.setParkId(orderDto.getParkId());
        ppre.setProductId(orderDto.getProductId());
        ProductParkRelaEntity rela = this.productParkRelaService.selectOne(ppre);
        if (null == rela) {
            throw new BusinessException(ErrorCodeEnum.PARK_PROD_RELA_NOT_EXISTS);
        }

        // 判断当前园区是否支持所选行业
        IndustryEntity ie = new IndustryEntity();
        ie.setParkId(orderDto.getParkId());
        ie.setId(orderDto.getIndustryId());
        ie.setCompanyType(orderDto.getCompanyType());
        IndustryEntity industry = this.industryService.selectOne(ie);
        if (null == industry) {
            throw new BusinessException(ErrorCodeEnum.INDUSTRY_NOT_SUPPORT);
        }
        orderDto.setExampleName(industry.getExampleName());// 设值示例名称

        // 字号检查：排查字号、备选字号对应的注册名称是否在会员企业表存在
        String result = this.memberCompanyMapper.checkCompanyNameByShopName(orderDto.getOemCode(), orderDto.getShopName(), orderDto.getShopNameOne(), orderDto.getShopNameTwo());
        if (StringUtils.isNotBlank(result)) {
            throw new BusinessException(result);
        }
        // 验证字号是否合法正则
        String pattern = "^[\u4e00-\u9fa5]{2,6}";
        if(StringUtils.isNotBlank(orderDto.getShopNameOne())){
            if(!Pattern.matches(pattern,orderDto.getShopNameOne())){
                throw new BusinessException(ErrorCodeEnum.SHOP_NAME_ONE_INVALID);
            }
        }
        if(StringUtils.isNotBlank(orderDto.getShopNameTwo())){
            if(!Pattern.matches(pattern,orderDto.getShopNameTwo())){
                throw new BusinessException(ErrorCodeEnum.SHOP_NAME_TWO_INVALID);
            }
        }

        // 校验新提交的字号是否有重名
        ArrayList<String> newNames = Lists.newArrayList();
        newNames.add(orderDto.getShopName());
        if(StringUtils.isNotBlank(orderDto.getShopNameOne())){
            newNames.add(orderDto.getShopNameOne());
        }
        if(StringUtils.isNotBlank(orderDto.getShopNameTwo())){
            newNames.add(orderDto.getShopNameTwo());
        }
        boolean isRepeat = false;// 是否有重复标识，默认无重名字号
        // 提交了一个备选字号
        if(newNames.size() == 2){
            if(newNames.get(0).equals(newNames.get(1))){
                isRepeat = true;
            }
        } else if(newNames.size() == 3){ // 提交了两个备选字号
            HashSet<String> namesSet = new HashSet<String>();
            newNames.stream().forEach( shopName -> {
                namesSet.add(shopName);
            });
            if(namesSet.size() != newNames.size()){
                isRepeat = true;
            }
        }
        if (isRepeat) {
            throw new BusinessException(ErrorCodeEnum.SHOP_NAME_REPEATED);
        }

        log.info("开户订单校验完成...");
    }

    /**
     * @Description 字号校验
     * @Author  Kaven
     * @Date   2020/6/3 15:22
     * @Param   ResubmitRegOrderDTO RegisterOrderEntity
     * @Return  boolean
     * @Exception
    */
    private void checkShopName(ResubmitRegOrderDTO entity, RegisterOrderEntity regOrder, Long parkId) {
        // 验证字号格式是否合法正则
        String regex = "^[\u4e00-\u9fa5]{2,6}";
        if (StringUtils.isBlank(entity.getShopName()) || !Pattern.matches(regex,entity.getShopName())) {
            throw new BusinessException("字号请输入2-6个汉字");
        }
        if(StringUtils.isNotBlank(entity.getShopNameOne())){
            if(!Pattern.matches(regex,entity.getShopNameOne())){
                throw new BusinessException(ResultConstants.SHOP_NAME_ONE_INVALID);
            }
        }
        if(StringUtils.isNotBlank(entity.getShopNameTwo())){
            if(!Pattern.matches(regex,entity.getShopNameTwo())){
                throw new BusinessException(ResultConstants.SHOP_NAME_TWO_INVALID);
            }
        }

        // 校验新提交的字号是否有重名
        ArrayList<String> newNames = Lists.newArrayList();
        newNames.add(entity.getShopName());
        if(StringUtils.isNotBlank(entity.getShopNameOne())){
            newNames.add(entity.getShopNameOne());
        }
        if(StringUtils.isNotBlank(entity.getShopNameTwo())){
            newNames.add(entity.getShopNameTwo());
        }
        boolean isRepeat = false;// 是否有重复标识，默认无重名字号
        // 提交了一个备选字号
        if(newNames.size() == 2){
            if(newNames.get(0).equals(newNames.get(1))){
                isRepeat = true;
            }
        } else if(newNames.size() == 3){ // 提交了两个备选字号
            HashSet<String> namesSet = new HashSet<String>();
            newNames.stream().forEach( shopName -> {
                namesSet.add(shopName);
            });
            if(namesSet.size() != newNames.size()){
                isRepeat = true;
            }
        }
        if (isRepeat) {
            throw new BusinessException("新的字号不能相同，请检查！");
        }


        // 新的字号与现有字号进行比对
        String[] oldNames = new String[3];
        oldNames[0] = regOrder.getShopName();

        if(StringUtils.isNotBlank(regOrder.getShopNameOne())){
            oldNames[1] = regOrder.getShopNameOne();
        }
        if(StringUtils.isNotBlank(regOrder.getShopNameTwo())){
            oldNames[2] = regOrder.getShopNameTwo();
        }

        for(int i = 0; i < oldNames.length; i++){
            if(entity.getShopName().equals(oldNames[i]) ||
                    (StringUtils.isNotBlank(entity.getShopNameOne()) && entity.getShopNameOne().equals(oldNames[i])) ||
                    (StringUtils.isNotBlank(entity.getShopNameTwo()) && entity.getShopNameTwo().equals(oldNames[i]))){
                throw new BusinessException("新的字号不能与已驳回的一样，请修改后再提交！");
            }
        }

        // 禁用字号校验
        parkDisableWorkService.checkoutDisableWord(parkId, entity.getShopName(), entity.getShopNameOne(), entity.getShopNameTwo());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editAndSaveHistory(RegisterOrderEntity entity, Integer orderStatus, String userAccount, String hisRemark) {
        Date updateTime = new Date();
        entity.setUpdateTime(updateTime);
        entity.setUpdateUser(userAccount);
        mapper.updateByPrimaryKeySelective(entity);
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(entity, record);
        record.setId(null);
        record.setAddTime(updateTime);
        record.setAddUser(userAccount);
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setRemark(hisRemark);
        record.setOrderStatus(orderStatus);
        registerOrderChangeRecordMapper.insert(record);
    }

    @Override
    public void supplyMaterials(SupplyMeterialDTO smDto) throws BusinessException {
        log.info("开始处理工商注册订单资料补充请求：{}",JSON.toJSONString(smDto));

        // 查询用户是否存在
        MemberAccountEntity member = this.memberAccountService.queryByAccount(smDto.getRegPhone(),smDto.getOemCode());
        if(null == member){
            throw new BusinessException(ErrorCodeEnum.USER_NOT_EXISTS);
        }

        // 查询订单号是否存在
        OrderEntity t = new OrderEntity();
        t.setOrderNo(smDto.getOrderNo());
        OrderEntity order = this.orderService.selectOne(t);
        if(null == order){
            throw new BusinessException(ErrorCodeEnum.ORDER_NO_NOT_EXIST);
        }
        // 订单归属人判断
        OrderEntity tt = new OrderEntity();
        tt.setAddUser(smDto.getRegPhone());
        tt.setOrderNo(smDto.getOrderNo());
        order = this.orderService.selectOne(tt);
        if(null == order){
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_BELONG_REG);
        }

        // 判断订单状态是否合法：已取消或审核未通过的订单不允许补充资料
        if(RegOrderStatusEnum.CANCELLED.getValue().intValue() == order.getOrderStatus().intValue() ||
                RegOrderStatusEnum.FAILED.getValue().intValue() == order.getOrderStatus().intValue()){
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        // 判断是否达到上传文件上限 add by Kaven 2020-04-15
        OrderAttachmentEntity oae = new OrderAttachmentEntity();
        oae.setOrderNo(smDto.getOrderNo());
        List<OrderAttachmentEntity> list = this.orderAttachmentService.select(oae);
        if (list.size() > 100) {
            throw new BusinessException(ErrorCodeEnum.OSS_FILE_NUM_LIMIT);
        }

        // 前端上传补充资料成功后，添加订单附件上传记录
        String[] fileUrlArray = smDto.getFileUrls().split(",");
        log.info("补充资料附件地址：{}", smDto.getFileUrls());

        // 单次最多上传个数判断
        if(fileUrlArray.length > 12){
            throw new BusinessException(ErrorCodeEnum.OSS_FILE_SINGLE_NUM_LIMIT);
        }

        for (String url : fileUrlArray) {
            // 判断文件地址是否存在
            String bucketName = this.dictionaryService.getByCode("oss_privateBucketName").getDictValue();
            boolean exists = this.ossService.doesObjectExist(url,bucketName);
            if(!exists){
                throw new BusinessException(ErrorCodeEnum.OSS_FILE_EXIST);
            }
            OrderAttachmentEntity attachment = new OrderAttachmentEntity();
            attachment.setAddTime(new Date());
            attachment.setAddUser(member.getMemberAccount());
            attachment.setMemberId(member.getId());
            attachment.setAttachmentAddr(url);
            attachment.setOrderNo(smDto.getOrderNo());
            attachment.setOemCode(order.getOemCode());
            attachment.setOrderType(2);
            this.orderAttachmentService.insertSelective(attachment);
        }

        log.info("工商注册订单资料补充请求处理完成。");
    }

    @Override
    public AliPayDto buildAliPayParams(PayWaterEntity payWater) throws BusinessException {
        //读取渠道支付宝支付相关配置
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(payWater.getOemCode(),21);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道支付宝支付相关信息！");
        }
        // 组装参数对象
        AliPayDto payDto = new AliPayDto();
        payDto.setTradeNo(payWater.getPayNo());
        payDto.setAgentNo(paramsEntity.getAccount());
        payDto.setServicePubKey(paramsEntity.getPublicKey());
        payDto.setPostUrl(paramsEntity.getUrl());

        // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        payDto.setKeyNum(params.getString("keyNum"));
        payDto.setSignKey(params.getString("signKey"));
        return payDto;
    }

    @Override
    @Transactional
    public void openRegisterConfirm(OrderEntity orderEntity, String useraccount) {
        RegisterOrderEntity registerOrderEntity = mapper.queryByOrderNo(orderEntity.getOrderNo());
        if (registerOrderEntity == null) {
            throw new BusinessException("开户订单不存在");
        }
        MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
        if (accEntity == null) {
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        //编辑企业注册订单和历史记录
        orderService.updateOrderStatus(useraccount, orderEntity.getOrderNo(), RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue());
        editAndSaveHistory(registerOrderEntity, RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue(), useraccount, null);
        //去掉备注，小程序不好展示
        registerOrderEntity.setRemark(null);
        mapper.updateByPrimaryKey(registerOrderEntity);
        //保存消息通知
        messageNoticeService.addNotice("register_confirm_register_tmpl", orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getUserId(), "温馨提示", 2, "2", 3, 8, useraccount);
        //发送通知短信
        Map<String, Object> map = Maps.newHashMap();
        map.put("regName", registerOrderEntity.getRegisteredName());
        smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.REGISTER_ORDER_CONFIRM_REGISTER.getValue(), map, 2);
        //微信通知
        map.put("registeredName", registerOrderEntity.getRegisteredName());
        map.put("registerStatus", RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getMessage());
        map.put("registerRemark", "需确认提交签名才能完成注册哦~");

        orderWechatAuthRelaService.sendNotice(orderEntity.getOrderNo(), WeChatMessageTemplateTypeEnum.INVITE_SIGN.getValue(), accEntity, "wechat_page_company_register", map);

    }

    @Override
    @Transactional
    public void openSubmitSign(OrderEntity orderEntity, Integer orderStatus, String useraccount) {
        RegisterOrderEntity registerOrderEntity = mapper.queryByOrderNo(orderEntity.getOrderNo());
        if (registerOrderEntity == null) {
            throw new BusinessException("开户订单不存在");
        }

        //编辑企业注册订单和历史记录
        orderService.updateOrderStatus(useraccount, orderEntity.getOrderNo(), orderStatus);
        editAndSaveHistory(registerOrderEntity, orderStatus, useraccount, null);
        //去掉备注，小程序不好展示
        registerOrderEntity.setRemark(null);
        mapper.updateByPrimaryKey(registerOrderEntity);
    }

    @Override
    @Transactional
    public void openDeclareConfirm(OrderEntity orderEntity, Integer orderStatus, String remark, String useraccount) {
        RegisterOrderEntity registerOrderEntity = mapper.queryByOrderNo(orderEntity.getOrderNo());
        if (registerOrderEntity == null) {
            throw new BusinessException("订单不存在");
        }
        MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
        if (accEntity == null) {
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        //编辑企业注册订单和历史记录
        orderService.updateOrderStatus(useraccount, orderEntity.getOrderNo(), orderStatus);
        registerOrderEntity.setRemark(remark);
        editAndSaveHistory(registerOrderEntity, orderStatus, useraccount, remark);
        //判断是不是状态变回待提交签名
        if (!Objects.equals(orderStatus, RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue())) {
            return;
        }
        //保存消息通知
        messageNoticeService.addNotice("register_user_not_sign_tmpl", orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getUserId(), "温馨提示", 2, "2", 3, 9, useraccount);
        //发送通知短信
        Map<String, Object> map = Maps.newHashMap();
        map.put("regName", registerOrderEntity.getRegisteredName());
        smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.REGISTER_ORDER_USER_NOT_SIGN.getValue(), map, 2);
        //微信通知
        map.put("registeredName", registerOrderEntity.getRegisteredName());
        map.put("signResult", "未提交签名");
        map.put("signRemark", "您未提交签名，请重新确认提交~");
        orderWechatAuthRelaService.sendNotice(orderEntity.getOrderNo(), WeChatMessageTemplateTypeEnum.SIGN_SURE_RESULT.getValue(), accEntity, "wechat_page_company_register", map);

    }

    @Override
    public List<RegisterOrderOfAccessPartyVO> listByMemberId(AccessPartyOrderQuery query) {
        return mapper.listByMemberId(query);
    }


    /**
     * @Description 路由支付通道，发起退款请求
     * @Author  HZ
     * @Date   2021/8/11 11:24
     * @Return Map<String, Object>
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> routeAndRefundOrder(String oemCode ,String orderNo,String payNo,String account) throws UnknownHostException,BusinessException {

        log.info("路由支付通道，发送退款请求：{}",oemCode);
        Map<String,Object> orderMap = Maps.newHashMap();// 接收返回
        String refundOrderNo = UniqueNumGenerator.generatePayNo();// 生成24位流水号

        OrderEntity orderEntity=orderService.queryByOrderNo(orderNo);
        if(orderEntity==null){
            throw new BusinessException("订单不存在");
        }
        PayWaterEntity payEntity = new PayWaterEntity();
        payEntity.setOemCode(oemCode);
        payEntity.setOrderNo(orderNo);
        payEntity.setOrderType(orderService.queryByOrderNo(orderNo).getOrderType());
        payEntity.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());
        List<PayWaterEntity> list = payWaterService.select(payEntity);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BusinessException("已存在退款订单");
        }

        payEntity.setPayWaterType(null);
        payEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        payEntity = payWaterService.selectOne(payEntity);
        if (payEntity == null) {
            log.info("取消订单支付流水不存在：{}", orderNo);
            throw new BusinessException("支付流水不存在");
        }
        // 修改支付流水退款状态为退款中
        payEntity.setRefundStatus(RefundWaterStatusEnum.REFUNDING.getValue());
        payWaterService.editByIdSelective(payEntity);

        // 退款流水
        PayWaterEntity refundWater = new PayWaterEntity();
        ObjectUtil.copyObject(payEntity, refundWater);
        refundWater.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
        refundWater.setPayNo(refundOrderNo);
        refundWater.setId(null);
        refundWater.setAddTime(new Date());
        refundWater.setAddUser(account);
        refundWater.setPayWaterType(5);
        refundWater.setUpdateTime(new Date());
        refundWater.setPayTime(new Date());
        refundWater.setUpdateUser(account);
        refundWater.setRefundStatus(null);
        //增加支付流水
        payWaterService.insertSelective(refundWater);

        UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(orderEntity.getUserId(), payEntity.getUserType(), oemCode,1);
        if (accEntity == null) {
            throw new BusinessException("资金账户不存在");
        }
        if (accEntity.getBlockAmount() < orderEntity.getPayAmount()) {
            throw new BusinessException("冻结金额不足，无法退款");
        }
        if(Objects.equals(PayWayEnum.WECHATPAY.getValue(),payEntity.getPayWay())) {
            WechatRefundDto wechatRefundDto = this.orderService.buildWechatRefundParams(oemCode, payNo, refundOrderNo);
            orderMap = WechatPayUtils.wechatRefund(wechatRefundDto);// 正常请求退款接口
            //更新外部流水号
            if (orderMap.get("code").equals("00")) {
                //解冻余额
                userCapitalAccountService.unfreezeBalanceWetchat(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(), account);
                JSONObject jsonObject = (JSONObject) orderMap.get("data");
                String refundTradeNo = jsonObject.getString("refundTradeNo");
                payEntity.setExternalOrderNo(refundTradeNo);
                orderMap.put("payWay",PayWayEnum.WECHATPAY.getValue());
                orderMap.put("payChanel",PayChannelEnum.WECHATPAY.getValue());
            }
        }else if(Objects.equals(PayWayEnum.BYTEDANCE.getValue(),payEntity.getPayWay())) {
            BytedanceRefundDto bytedanceRefundDto = this.orderService.buildBytedanceRefundParams(payEntity, payNo);
            orderMap = BytedanceUtils.bytedanceRefund(bytedanceRefundDto);// 正常请求退款接口
            //更新外部流水号
            if (orderMap.get("code").equals("00")) {
            JSONObject jsonObject = (JSONObject) orderMap.get("data");
            String refundTradeNo = jsonObject.getString("refund_no");
            payEntity.setExternalOrderNo(refundTradeNo);
            orderMap.put("payWay",PayWayEnum.BYTEDANCE.getValue());
            orderMap.put("payChanel",PayChannelEnum.BYTEDANCE.getValue());
            }
        }
        // 更新支付流水
        payEntity.setUpdateUser(account);
        payEntity.setUpdateTime(new Date());
        String msg = orderMap.get("msg").toString();
        if (orderMap.get("code").equals("00")) {
            payEntity.setRemark("更新外部流水号");
            payEntity.setRefundStatus(RefundWaterStatusEnum.REFUND_SUCCESS.getValue());
            refundWater.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            orderMap.put("refundOrderNo",refundOrderNo);
        } else {
            payEntity.setRefundStatus(RefundWaterStatusEnum.REFUND_FAILURE.getValue());
            refundWater.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
            orderMap.put("refundResult", StringUtil.isNotBlank(msg) ? "退款失败！" + msg + "，可前往支付流水进行处理~" : "该笔订单可能退款失败，请前往支付流水进行查看！");
            if(StringUtil.isNotBlank(msg)){
                orderMap.put("refundResultCode", "error");
            }
        }
        payWaterService.editByIdSelective(payEntity);
        // 更新退款流水
        payWaterService.editByIdSelective(refundWater);
        return orderMap;
    }


    /**
     * @Description 路由支付通道，发起退款请求(重新付款)
     * @Author  HZ
     * @Date   2021/8/11 11:24
     * @Return Map<String, Object>
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void routeAndRefundOrderReplay(String oemCode ,String orderNo,String payNo,String account,int payWay) {

        log.info("路由支付通道，发送退款请求：{}", oemCode);
        Map<String, Object> orderMap = Maps.newHashMap();// 接收返回
        String refundOrderNo = UniqueNumGenerator.generatePayNo();// 生成24位流水号

        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if (orderEntity == null) {
            throw new BusinessException("订单不存在");
        }

        // 查询支付流水
        PayWaterEntity payEntity = new PayWaterEntity();
        payEntity.setPayNo(payNo);
        payEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        payEntity = payWaterService.selectOne(payEntity);
        //更新支付流水
        payEntity.setRefundStatus(RefundWaterStatusEnum.REFUNDING.getValue());
        payWaterService.editByIdSelective(payEntity);

        // 查询退款流水
        PayWaterEntity refundEntity = new PayWaterEntity();
        refundEntity.setOemCode(oemCode);
        refundEntity.setOrderNo(orderNo);
        refundEntity.setOrderType(orderService.queryByOrderNo(orderNo).getOrderType());
        refundEntity.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());
        List<PayWaterEntity> list = payWaterService.select(refundEntity);
        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException("未找到订单[" + orderNo + "]的退款流水信息");
        }
        refundEntity =list.get(0);
        refundEntity.setId(null);
        refundEntity.setPayStatus(PayWaterStatusEnum.PAYING.getValue());
        refundEntity.setPayNo(refundOrderNo);
        refundEntity.setUpResultMsg(null);
        refundEntity.setUpStatusCode(null);
        refundEntity.setUpdateTime(null);
        refundEntity.setUpdateUser(null);
        refundEntity.setAddTime(new Date());
        refundEntity.setAddUser(account);
        refundEntity.setPayTime(new Date());
        payWaterService.insertSelective(refundEntity);

        if (Objects.equals(payWay, PayWayEnum.WECHATPAY.getValue())) {
            WechatRefundDto wechatRefundDto = null;
            try {
                wechatRefundDto = this.orderService.buildWechatRefundParams(oemCode, payNo, refundOrderNo);
            } catch (UnknownHostException e) {
                log.error(e.getMessage());
                throw new BusinessException("微信退款下单系统异常");
            }
            orderMap = WechatPayUtils.wechatRefund(wechatRefundDto);// 正常请求退款接口
            if (!orderMap.get("code").equals("00")) {
                throw new BusinessException("微信退款下单失败");
            }
        } else if (Objects.equals(payWay, PayWayEnum.BYTEDANCE.getValue())) {
            BytedanceRefundDto bytedanceRefundDto = null;
            try {
                bytedanceRefundDto = this.orderService.buildBytedanceRefundParams(refundEntity, payNo);
            } catch (UnknownHostException e) {
                log.error(e.getMessage());
                throw new BusinessException("微信退款下单系统异常");
            }
            orderMap = BytedanceUtils.bytedanceRefund(bytedanceRefundDto);// 正常请求退款接口
            if (!orderMap.get("code").equals("00")) {
                throw new BusinessException("字节跳动退款下单失败");
            }
        }
        // 更新支付流水退款状态为“退款成功”
        payEntity.setRefundStatus(RefundWaterStatusEnum.REFUND_SUCCESS.getValue());

    }

    @Override
    public RegisterOrderEntity queryByOrderNo(String orderNo) {
        return mapper.queryByOrderNo(orderNo);
    }

    @Override
    public void updateByPrimaryKeySelective(RegisterOrderEntity registerOrderEntity) {
        mapper.updateByPrimaryKeySelective(registerOrderEntity);
    }

    @Override
    public String getBusinessAddress(Long parkId) {
        return mapper.getBusinessAddress(parkId);
    }

    @Override
    public void addPaymentVoucher(ThirdPartyAddPaymentVoucherDTO dto) {
        log.info("注册订单添加支付凭证开始：{}", dto);
        // 查询订单
        OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(dto.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到主订单信息"));
        if (InvoiceOrderStatusEnum.UNCHECKED.getValue() < orderEntity.getOrderStatus()) {
            throw new BusinessException("订单状态不正确");
        }
        if (Objects.equals(orderEntity.getIsSelfPaying(), 1)) {
            throw new BusinessException("费用承担方不为企业");
        }
        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(orderEntity.getUserId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        // 查询接入方
        OemAccessPartyEntity oemAccessParty = Optional.ofNullable(oemAccessPartyService.queryByAccessPartyCode(dto.getAccessPartyCode())).orElseThrow(() -> new BusinessException("未查询到接入方信息"));
        if (!Objects.equals(oemAccessParty.getId(), member.getAccessPartyId())) {
            throw new BusinessException("订单不属于当前接入方");
        }
        // 查询注册订单
        RegisterOrderEntity regOrder = Optional.ofNullable(this.queryByOrderNo(dto.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到注册订单信息"));
        if (StringUtil.isNotBlank(regOrder.getPaymentVoucher())) {
            throw new BusinessException(ErrorCodeEnum.HAVE_VOUCHER);
        }

        // 更新注册订单
        regOrder.setPaymentVoucher(dto.getPaymentVoucher());
        regOrder.setUpdateTime(new Date());
        regOrder.setUpdateUser(oemAccessParty.getAccessPartyName());
        this.updateByPrimaryKeySelective(regOrder);
        // 更新变更记录
        RegisterOrderChangeRecordEntity changeRecordEntity = new RegisterOrderChangeRecordEntity();
        ObjectUtil.copyObject(regOrder, changeRecordEntity);
        changeRecordEntity.setAddTime(new Date());
        changeRecordEntity.setAddUser(oemAccessParty.getAccessPartyName());
        changeRecordEntity.setUpdateTime(null);
        changeRecordEntity.setUpdateUser(null);
        changeRecordEntity.setId(null);
        changeRecordEntity.setOrderStatus(orderEntity.getOrderStatus());
        registerOrderChangeRecordService.insertSelective(changeRecordEntity);
    }

    @Override
    public List<RegisterOrderEntity> getRegisterOrderByBusinessContent(String content, Long parkId) {
        return mapper.getRegisterOrderByBusinessContent(content,parkId);
    }

    @Override
    @Transactional
    public void synchronousTaxCode(SynchronousTaxCodeDTO dto, String accessPartyCode) {
        // 查询接入方信息
        OemAccessPartyEntity accessPartyEntity = Optional.ofNullable(oemAccessPartyService.queryByAccessPartyCode(accessPartyCode)).orElseThrow(() -> new BusinessException("未查询到接入方信息"));

        // 查询注册订单
        RegisterOrderEntity regOrder = Optional.ofNullable(this.queryByOrderNo(dto.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到注册订单信息"));
        // 校验订单是否全部赋码
        if (Objects.equals(regOrder.getIsAllCodes(), 1)) {
            throw new BusinessException(ErrorCodeEnum.HAVE_SYNCHRONOUS);
        }
        // 查询订单
        OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(dto.getOrderNo())).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        // 校验订单是否还未审核
        if (3 < order.getOrderStatus() && order.getOrderStatus() < 9) {
            throw new BusinessException("订单状态不正确");
        }
        // 校验所传编码及名称是否有空值
        Set<RegisterOrderGoodsDetailRelaEntity> collect = dto.getMerchandises().stream().filter(x -> null == x.getTaxClassificationCode() || StringUtil.isBlank(x.getTaxClassificationCode())
                || null == x.getGoodsName() || StringUtil.isBlank(x.getGoodsName())).collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            throw new BusinessException("税收分类编码或名称为空");
        }
        // 赋码后新增经营范围
        String newBusinessScope = "";
        // 需同步的税收分类编码
        List<RegisterOrderGoodsDetailRelaEntity> needSynchronizedList = dto.getMerchandises();
        // 本次匹配结果
        List<ParkBusinessScopeWithTaxCodeVO> matchingResults = null;

        // 查询订单商品关系数据
        List<RegisterOrderGoodsDetailRelaEntity> list = registerOrderGoodsDetailService.findByOrderNo(order.getOrderNo());
        if (null != list && !list.isEmpty()) {
            // 获取已赋码编码
            Set<String> matchedCode = list.stream().filter(x -> null != x.getBusinessscopeBaseId() && null != x.getParkBusinessscopeId())
                    .map(RegisterOrderGoodsDetailRelaEntity::getTaxClassificationCode).collect(Collectors.toSet());
            // 去除已赋码编码
            needSynchronizedList = dto.getMerchandises().stream().filter(x -> !matchedCode.contains(x.getTaxClassificationCode())).collect(Collectors.toList());
        }
        Set<String> needSynchronizedCode = needSynchronizedList.stream().map(RegisterOrderGoodsDetailRelaEntity::getTaxClassificationCode).collect(Collectors.toSet());
        // 对剩余部分编码进行匹配
        if (CollectionUtil.isNotEmpty(needSynchronizedCode)) {
            matchingResults = parkBusinessScopeService.queryByTaxCode(needSynchronizedCode, order.getParkId());
            newBusinessScope = matchingResults.stream().map(ParkBusinessScopeWithTaxCodeVO::getBusinessscopeName).collect(Collectors.joining(";"));
            Set<String> matchingResultCode = matchingResults.stream().map(ParkBusinessScopeWithTaxCodeVO::getTaxClassificationCode).collect(Collectors.toSet());
            if (!matchingResults.isEmpty()) {
                // 未匹配结果加上对应的税收分类名称
                for (int i = 0; i < matchingResults.size(); i++) {
                    ParkBusinessScopeWithTaxCodeVO vo = matchingResults.get(i);
                    String goodsName = needSynchronizedList.stream().filter(x -> x.getTaxClassificationCode().equals(vo.getTaxClassificationCode())).map(RegisterOrderGoodsDetailRelaEntity::getGoodsName).collect(Collectors.toList()).get(0);
                    vo.setGoodsName(goodsName);
                }
                // 合并该次需匹配列表及匹配结果列表
                needSynchronizedList = needSynchronizedList.stream().filter(x -> !matchingResultCode.contains(x.getTaxClassificationCode())).collect(Collectors.toList());
                ObjectUtil.copyListObject(matchingResults, needSynchronizedList, RegisterOrderGoodsDetailRelaEntity.class);
            }
            // 为本次同步商品添加完整数据
            Date date = new Date();
            for (int i = 0; i < needSynchronizedList.size(); i++) {
                RegisterOrderGoodsDetailRelaEntity needSynchronized = needSynchronizedList.get(i);
                needSynchronized.setAddTime(date);
                needSynchronized.setAddUser(accessPartyEntity.getAccessPartyName());
                needSynchronized.setUpdateTime(date); // 用于查找赋码后新增经营范围
                needSynchronized.setUpdateUser(accessPartyEntity.getAccessPartyName());
                needSynchronized.setOrderNo(order.getOrderNo());
            }
            // 保存本次注册订单与商品关系数据
            registerOrderGoodsDetailRelaService.batchAdd(needSynchronizedList);
            // 查找注册时编码为空的数据，并对比本次匹配结果中有无相同税收分类名称数据
            List<RegisterOrderGoodsDetailRelaEntity> taxCodeIsBlank = list.stream().filter(x -> StringUtil.isBlank(x.getTaxClassificationCode())).collect(Collectors.toList());
            if (!taxCodeIsBlank.isEmpty()) {
                // 获取本次匹配结果税收分类名称集
                List<String> goodsNames = needSynchronizedList.stream().map(RegisterOrderGoodsDetailRelaEntity::getGoodsName).collect(Collectors.toList());
                List<RegisterOrderGoodsDetailRelaEntity> needAddTaxCodeList = taxCodeIsBlank.stream().filter(x -> goodsNames.contains(x.getGoodsName())).collect(Collectors.toList());
                if (!needAddTaxCodeList.isEmpty()) {
                    for (int i = 0; i < needAddTaxCodeList.size(); i++) {
                        RegisterOrderGoodsDetailRelaEntity entity = needAddTaxCodeList.get(i);
                        RegisterOrderGoodsDetailRelaEntity data = needSynchronizedList.stream().filter(x -> x.getGoodsName().endsWith(entity.getGoodsName())).collect(Collectors.toList()).get(0);
                        entity.setUpdateTime(date);
                        entity.setUpdateUser(accessPartyEntity.getAccessPartyName());
                        entity.setTaxClassificationCode(data.getTaxClassificationCode());
                        entity.setBusinessscopeBaseId(data.getBusinessscopeBaseId());
                        entity.setParkBusinessscopeId(data.getParkBusinessscopeId());
                        registerOrderGoodsDetailRelaService.editByIdSelective(entity);
                    }
                }
            }
        }

        // 修改注册订单是否全部赋码
        regOrder.setIsAllCodes(1);
        if (null != matchingResults) {
            String businessScope = regOrder.getBusinessScope();
            Set<String> businessScopes = Sets.newHashSet(businessScope.split(";"));
            businessScopes.addAll(matchingResults.stream().map(ParkBusinessScopeWithTaxCodeVO::getBusinessscopeName).collect(Collectors.toSet()));
            regOrder.setBusinessScope(String.join(";", businessScopes));
            regOrder.setTaxcodeBusinessScope(regOrder.getTaxcodeBusinessScope() + (StringUtil.isNotBlank(regOrder.getTaxcodeBusinessScope()) ? ";" : "") + newBusinessScope);
        }
        regOrder.setUpdateTime(new Date());
        regOrder.setUpdateUser(accessPartyEntity.getAccessPartyName());
        this.editByIdSelective(regOrder);
        // 添加变更记录
        RegisterOrderChangeRecordEntity changeRecordEntity = new RegisterOrderChangeRecordEntity();
        ObjectUtil.copyObject(regOrder, changeRecordEntity);
        changeRecordEntity.setId(null);
        changeRecordEntity.setOrderStatus(order.getOrderStatus());
        changeRecordEntity.setRemark("商品同步赋码接口修改全部赋码标识");
        registerOrderChangeRecordService.insertSelective(changeRecordEntity);
    }

    @Override
    public void cancelUnpaidOrder(String orderNo, String oemCode, Long memberId) {
        ArrayList<String> orders = Lists.newArrayList();

        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(memberId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        // 订单号为空时取消所有未支付订单
        if (StringUtil.isBlank(orderNo)) {
            // 查询用户是否存在未支付注册订单
            AccessPartyOrderQuery query = new AccessPartyOrderQuery();
            query.setOemCode(oemCode);
            query.setUserId(memberId);
            query.setOrderStatisticsStatus(2);
            List<RegisterOrderOfAccessPartyVO> registerOrderUnpaid = this.listByMemberId(query);
            // 过滤有支付凭证的未支付订单
            registerOrderUnpaid = registerOrderUnpaid.stream().filter(x -> StringUtil.isBlank(x.getPaymentVoucher())).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(registerOrderUnpaid)) {
                // 不存在未支付订单，无需操作
                throw new BusinessException("用户不存在未支付注册订单或已取消");
            }
            for (RegisterOrderOfAccessPartyVO order : registerOrderUnpaid) {
                orders.add(order.getOrderNo());
            }
        } else {
            // 查询注册订单
            RegisterOrderEntity regOrder = Optional.ofNullable(this.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到注册订单信息"));
            if (StringUtil.isNotBlank(regOrder.getPaymentVoucher())) {
                throw new BusinessException("已上传支付凭证订单不允许取消");
            }
            orders.add(orderNo);
        }

        for (String no : orders) {
            OrderEntity entity = new OrderEntity();
            entity.setOrderNo(no);
            entity.setOemCode(oemCode);
            entity.setOrderType(OrderTypeEnum.REGISTER.getValue());
            entity = orderService.selectOne(entity);
            if (entity == null) {
                throw new BusinessException("订单不存在");
            }
            // 查询注册订单
            RegisterOrderEntity registerOrderEntity = this.queryByOrderNo(no);
            if (null == registerOrderEntity) {
                throw new BusinessException("未查询到注册订单信息");
            }
            if (entity.getOrderStatus() > RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue() || (RegOrderStatusEnum.TO_BE_SURE.getValue().equals(entity.getOrderStatus())
                    && (IsSelfPayingEnum.SELF_PLAYING.getValue().equals(entity.getIsSelfPaying())
                        || (IsSelfPayingEnum.BEARER.getValue().equals(entity.getIsSelfPaying()) && StringUtil.isNotBlank(registerOrderEntity.getPaymentVoucher()))))
            ) {
                throw new BusinessException("订单"+ Objects.requireNonNull(RegOrderStatusEnum.getByValue(entity.getOrderStatus())).getMessage() +"，不允许取消");
            } else if (IsSelfPayingEnum.BEARER.getValue().equals(entity.getIsSelfPaying()) && StringUtil.isNotBlank(registerOrderEntity.getPaymentVoucher())) {
                throw new BusinessException("承担方已付费，不允许取消");
            }

            if(!entity.getUserId().equals(memberId)){
                throw new BusinessException("非法操作，您没有权限取消该订单");
            }
            String result = this.orderService.cancelOrder(entity, RegOrderStatusEnum.CANCELLED.getValue(), member.getMemberAccount(), true);
            // 订单已支付不允许取消
            if(ErrorCodeConstants.EXIST_PAYING_ERROR.toString().equals(result)){
                throw new BusinessException(ErrorCodeConstants.EXIST_PAYING_ERROR,"该订单已经支付成功，不允许取消");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addOrUpdateRegOrder(AddOrUpdateRegOrderDTO dto) {
        log.info("新增/编辑注册订单服务开始：{}", JSON.toJSONString(dto));

        // 1.机构相关校验
        // 查询oem机构信息
        OemEntity oem = oemService.getOem(dto.getOemCode());
        if (null == oem) {
            throw new BusinessException("未查询到oem机构");
        }

        // 2.园区相关校验
        // 查询园区信息
        ParkEntity park = this.parkService.findById(dto.getParkId());
        if (null == park) {
            throw new BusinessException("开户订单创建失败，园区不存在");
        }
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            throw new BusinessException("开户订单创建失败，园区状态不正确，只有已上架的园区才允许开户");
        }
        // 查询园区是否属于当前OEM机构
        OemParkRelaEntity t = new OemParkRelaEntity();
        t.setParkId(dto.getParkId());
        t.setOemCode(dto.getOemCode());
        List<OemParkRelaEntity> opreList = this.oemParkRelaService.select(t);
        if (null == opreList || opreList.size() == 0) {
            throw new BusinessException("开户订单创建失败，所选园区不属于当前OEM机构");
        }
        // 园区经营地址规则配置校验
        ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity = new ParkBusinessAddressRulesEntity();
        parkBusinessAddressRulesEntity.setParkId(dto.getParkId());
        List<ParkBusinessAddressRulesEntity> list = this.parkBusinessAddressRulesMapper.select(parkBusinessAddressRulesEntity);
        if (null == list || list.size() != 1) {
            throw new BusinessException("开户订单创建失败，园区经营地址规则配置错误！");
        }

        // 3.产品相关校验
        // 查询产品信息
        ProductEntity product = this.productService.findById(dto.getProductId());
        if (null == product) {
            throw new BusinessException("开户订单创建失败，产品信息不存在");
        }
        // 判断产品类型
        if (product.getProdType() > ProductTypeEnum.LIMITED_LIABILITY.getValue()) {
            throw new BusinessException("所选产品类型为【" + ProductTypeEnum.getByValue(product.getProdType()) + "】不允许开户");
        }
        // 判断产品和园区关系是否正确
        ProductParkRelaEntity ppre = new ProductParkRelaEntity();
        ppre.setParkId(dto.getParkId());
        ppre.setProductId(dto.getProductId());
        ProductParkRelaEntity rela = this.productParkRelaService.selectOne(ppre);
        if (null == rela) {
            throw new BusinessException("开户订单创建失败，未找到所选产品和园区对应关系");
        }

        // 4.行业校验
        IndustryEntity industry = this.industryService.findById(dto.getIndustryId());
        if (null == industry) {
            throw new BusinessException("开户订单创建失败，行业类型不存在");
        }
        dto.setExampleName(industry.getExampleName());// 设值示例名称

        // 5.字号相关校验
        if (Objects.equals(dto.getIsAutoCreate(), 0)) {
            if (StringUtil.isBlank(dto.getShopNameOne())) {
                throw new BusinessException("请输入备选字号1");
            }
            String regexp = "[\\u4e00-\\u9fa5]{2,6}";
            Pattern pattern = Pattern.compile(regexp);
            if (!pattern.matcher(dto.getShopNameOne()).matches()) {
                throw new BusinessException("备选字号1格式不正确，请输入2-6个汉字");
            }
            if (StringUtil.isBlank(dto.getShopNameTwo())) {
                throw new BusinessException("请输入备选字号2");
            }
            if (!pattern.matcher(dto.getShopNameTwo()).matches()) {
                throw new BusinessException("备选字号2格式不正确，请输入2-6个汉字");
            }
        }
        // 字号检查：排查字号、备选字号对应的注册名称是否在会员企业表存在
        String shopName = park.getParkCity() + dto.getExampleName().replace("***", dto.getShopName());
        String shopNameOne = "";
        if (StringUtil.isNotBlank(dto.getShopNameOne())) {
            shopNameOne = park.getParkCity() + dto.getExampleName().replace("***", dto.getShopNameOne());
        }
        String shopNameTwo = "";
        if (StringUtil.isNotBlank(dto.getShopNameOne())) {
            shopNameTwo = park.getParkCity() + dto.getExampleName().replace("***", dto.getShopName());
        }
        String result = this.memberCompanyMapper.checkCompanyNameByShopName(dto.getOemCode(), shopName, shopNameOne, shopNameTwo);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(result)) {
            throw new BusinessException(result);
        }
        // 禁用字号校验
        parkDisableWordService.checkoutDisableWord(park.getId(), dto.getShopName(), dto.getShopNameOne(), dto.getShopNameTwo());

        // 6.用户相关校验
        MemberAccountEntity member = this.memberAccountService.findById(dto.getUserId());
        if (null == member) {
            throw new BusinessException("开户订单创建失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        // 校验用户是否已过期
        if (member.getAuthStatus() == 1 && StringUtil.isNotBlank(member.getExpireDate())) {
            String[] split = member.getExpireDate().split("-");
            if (split.length == 2 && !"长期".equals(split[1])
                    && DateUtil.parseDefaultDate(split[1].replace(".","-")).before(DateUtil.parseDefaultDate(DateUtil.formatDefaultDate(new Date())))) {
                throw new BusinessException("登录用户认证身份证已过期");
            }
        }

        // 7.判断是否为员工，员工账号不允许注册企业
        if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
            throw new BusinessException("员工账号不允许注册企业！");
        }

        // 8.非个体户注册注册资本校验
        if (!MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(dto.getCompanyType()) && null == dto.getRegisteredCapital()) {
            throw new BusinessException("注册资本不能为空");
        }
        log.info("开户订单校验完成...");

        // 携带订单编号时为编辑订单，否则为新增订单
        boolean isAddRegOrder = true; // 是否新增注册订单
        String orderNo; // 订单号
        OrderEntity mainOrder; // 主订单
        RegisterOrderEntity regOrder; // 注册订单
        if (StringUtil.isNotBlank(dto.getOrderNo())) {
            orderNo = dto.getOrderNo();
            mainOrder = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单"));
            if (!RegOrderStatusEnum.TO_CREATE.getValue().equals(mainOrder.getOrderStatus())
                    && !RegOrderStatusEnum.TO_BE_SIGN.getValue().equals(mainOrder.getOrderStatus())) {
                throw new BusinessException("订单当前状态为【" + RegOrderStatusEnum.getByValue(mainOrder.getOrderStatus()).getMessage() + "】不允许修改！");
            }
            regOrder = Optional.ofNullable(this.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到注册订单"));
            isAddRegOrder = false;
        } else {
            // 生成订单号，订单信息入库
            orderNo = OrderNoFactory.getOrderCode(dto.getUserId());
            // 新建订单
            mainOrder = new OrderEntity();
            // 新建注册订单
            regOrder = new RegisterOrderEntity();
        }

        MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
        // 二要素验证通过，保存会员订单关系
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(dto.getUserId(), dto.getOemCode(), 1);
        if (more != null && isAddRegOrder) {
            more.setMemberId(dto.getUserId());
            // 设置会员等级
            more.setMemberLevel(level.getLevelNo());
            more.setAddTime(new Date());
            more.setAddUser(member.getMemberAccount());
            more.setOemCode(oem.getOemCode());
            more.setOemName(oem.getOemName());

            this.memberOrderRelaService.insertSelective(more);
        }

        // 保存订单主表信息
        mainOrder.setUserId(dto.getUserId());
        mainOrder.setSourceType(dto.getSourceType());
        mainOrder.setOrderType(OrderTypeEnum.REGISTER.getValue());
        mainOrder.setOrderStatus(RegOrderStatusEnum.TO_CREATE.getValue());
        mainOrder.setProductId(dto.getProductId());
        mainOrder.setProductName(product.getProdName());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(dto.getOemCode(),"is_open_channel");
        if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
            mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
        }else{
            mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        }
        mainOrder.setChannelUserId(member.getChannelUserId());
        mainOrder.setOemCode(dto.getOemCode());
        mainOrder.setParkId(dto.getParkId());
        mainOrder.setOrderAmount(product.getProdAmount());
        mainOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        mainOrder.setIsSelfPaying(dto.getIsSelfPaying());
        mainOrder.setPayerName(dto.getPayerName());

        // 计算订单支付金额，如有折扣
        Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(dto.getUserId(), dto.getProductId(), dto.getOemCode(), dto.getParkId());
        mainOrder.setPayAmount(payAmount);
        mainOrder.setDiscountAmount(product.getProdAmount() - payAmount);//优惠金额

        /**
         *  判断是否存在特价活动，
         *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
         */
        ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
        productDiscountActivityAPIDTO.setOemCode(dto.getOemCode());
        productDiscountActivityAPIDTO.setMemberId(dto.getUserId());
        productDiscountActivityAPIDTO.setIndustryId(dto.getIndustryId());
        productDiscountActivityAPIDTO.setParkId(dto.getParkId());
        productDiscountActivityAPIDTO.setProductType(product.getProdType());
        ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        if(productDiscountActivityVO!=null) {
            mainOrder.setOrderAmount(productDiscountActivityVO.getSpecialPriceAmount());
            mainOrder.setPayAmount(productDiscountActivityVO.getPayAmount());
            mainOrder.setDiscountAmount(productDiscountActivityVO.getSpecialPriceAmount() - productDiscountActivityVO.getPayAmount());
            mainOrder.setDiscountActivityId(productDiscountActivityVO.getDiscountActivityId());
        }
        //保存人群标签id
        Long crowdLabelId = memberCrowdLabelRelaService.getCrowLabelIdByMemberId(member.getId(), oem.getOemCode());
        if(crowdLabelId!=null){
            mainOrder.setCrowdLabelId(crowdLabelId);
        }

        // 接入方用户注册，费用承担方为非本人时，支付金额为0
        if (null != dto.getIsSelfPaying() && IsSelfPayingEnum.BEARER.getValue().equals(dto.getIsSelfPaying())) {
            mainOrder.setPayAmount(0L);
        }

        mainOrder.setUserType(member.getMemberType());
        if (more != null) {
            mainOrder.setRelaId(more.getId());
        }
        mainOrder.setOrderNo(orderNo);
        mainOrder.setSourceType(dto.getSourceType());
        if (isAddRegOrder) {
            mainOrder.setAddUser(member.getMemberAccount());
            mainOrder.setAddTime(new Date());
            orderService.insertSelective(mainOrder);
        } else {
            mainOrder.setUpdateUser(member.getMemberAccount());
            mainOrder.setUpdateTime(new Date());
            orderService.editByIdSelective(mainOrder);
        }

        // 更新股东/合伙人投资金额
        if (!isAddRegOrder && null != regOrder.getRegisteredCapital() && null != dto.getRegisteredCapital()
                && regOrder.getRegisteredCapital().compareTo(dto.getRegisteredCapital()) != 0) {
            // 查询股东/合伙人
            Example example = new Example(CompanyCorePersonnelEntity.class);
            example.createCriteria().andEqualTo("isShareholder", "1").andEqualTo("orderNo", orderNo);
            List<CompanyCorePersonnelEntity> corePersonnelEntities = companyCorePersonnelService.selectByExample(example);
            if (CollectionUtil.isNotEmpty(corePersonnelEntities)) {
                for (CompanyCorePersonnelEntity corePersonnelEntity : corePersonnelEntities) {
                    if (null != corePersonnelEntity.getShareProportion()) {
                        corePersonnelEntity.setInvestmentAmount(dto.getRegisteredCapital().multiply(corePersonnelEntity.getShareProportion()));
                        companyCorePersonnelService.editByIdSelective(corePersonnelEntity);
                    }
                }
            }
        }

        // 保存工商订单信息
        regOrder.setOemCode(dto.getOemCode());
        regOrder.setShopName(shopName);
        regOrder.setShopNameOne(shopNameOne);
        regOrder.setShopNameTwo(shopNameTwo);
        regOrder.setIndustryId(dto.getIndustryId());
        String businessScope = dto.getIndustryBusinessScope();
        Set<String> set = Sets.newHashSet(dto.getIndustryBusinessScope().split(";"));
        if (StringUtil.isNotBlank(dto.getOwnBusinessScope())) {
            String[] split = dto.getOwnBusinessScope().split(";");
            set.addAll(Arrays.asList(split));
            businessScope = set.stream().collect(Collectors.joining(";"));
        }
        regOrder.setBusinessScope(businessScope);
        regOrder.setRegisteredName(dto.getRegisteredName());
        regOrder.setAlertNumber(0);// 默认通知次数为0
        regOrder.setRemark(dto.getRemark());
        regOrder.setExampleName(dto.getExampleName());
        regOrder.setIndustryBusinessScope(dto.getIndustryBusinessScope());
        regOrder.setOwnBusinessScope(dto.getOwnBusinessScope());
        regOrder.setTaxpayerType(dto.getTaxpayerType());
        regOrder.setOrderNo(orderNo);
        regOrder.setCompanyType(product.getProdType());
        regOrder.setOrderAmount(mainOrder.getOrderAmount());
        regOrder.setPayAmount(mainOrder.getPayAmount());
        regOrder.setDiscountAmount(mainOrder.getDiscountAmount());
        regOrder.setPayType(null == dto.getPayType() ? 1 : dto.getPayType()); // 默认在线支付
        regOrder.setRegisteredCapital(dto.getRegisteredCapital());
        regOrder.setIsAutoCreate(dto.getIsAutoCreate());
        if (isAddRegOrder) {
            regOrder.setAddTime(new Date());
            regOrder.setAddUser(member.getMemberAccount());
            this.insertSelective(regOrder);
        } else {
            regOrder.setUpdateUser(member.getMemberAccount());
            regOrder.setUpdateTime(new Date());
            this.editByIdSelective(regOrder);
        }

        //保存工商注册订单变更记录
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(regOrder, record);
        record.setId(null);
        record.setAddTime(new Date());
        record.setAddUser(member.getMemberAccount());
        record.setOrderStatus(RegOrderStatusEnum.TO_CREATE.getValue());
        if (!isAddRegOrder) {
            record.setRemark("编辑注册订单");
        }
        this.registerOrderChangeRecordMapper.insertSelective(record);

        // 编辑订单时先删除之前保存的开票类目，根据订单号删除开票类目
        if (!isAddRegOrder) {
            Example example = new Example(CompanyInvoiceCategoryEntity.class);
            example.createCriteria().andEqualTo("orderNo", orderNo);
            companyInvoiceCategoryService.delByExample(example);
        }
        // 保存企业开票类目
        this.companyInvoiceCategoryService.addByIndustryId(dto.getOemCode(), orderNo, dto.getIndustryId(), member.getMemberAccount());


        return orderNo;
    }

    @Override
    public CompanyInfoOfRegOrderVO getCompanyInfo(String orderNo) {
        CompanyInfoOfRegOrderVO vo = new CompanyInfoOfRegOrderVO();
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }
        // 查询注册订单
        RegisterOrderEntity regOrder = Optional.ofNullable(this.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到注册订单"));
        ObjectUtil.copyObject(regOrder, vo);
        vo.setExampleName(vo.getExampleName().replace("***", ""));

        // 查询订单
        OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单"));

        // 查询园区
        ParkEntity park = Optional.ofNullable(parkService.findById(orderEntity.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区"));
        vo.setAffiliatingArea(park.getAffiliatingArea());
        String parkCity = park.getParkCity();
        vo.setParkCity(parkCity);
        vo.setShopName(vo.getShopName().replace(parkCity, ""));
        if (StringUtil.isNotBlank(vo.getShopNameOne())) {
            vo.setShopNameOne(vo.getShopNameOne().replace(parkCity, ""));
        }
        if (StringUtil.isNotBlank(vo.getShopNameTwo())) {
            vo.setShopNameTwo(vo.getShopNameTwo().replace(parkCity, ""));
        }

        // 查询行业
        IndustryEntity industryEntity = industryService.findById(regOrder.getIndustryId());
        if (null != industryEntity) {
            vo.setIndustryName(industryEntity.getIndustryName());
            String exampleName = vo.getExampleName();
            vo.setShopName(vo.getShopName().replace(exampleName, ""));
            if (StringUtil.isNotBlank(vo.getShopNameOne())) {
                vo.setShopNameOne(vo.getShopNameOne().replace(exampleName, ""));
            }
            if (StringUtil.isNotBlank(vo.getShopNameTwo())) {
                vo.setShopNameTwo(vo.getShopNameTwo().replace(exampleName, ""));
            }
        }

        return vo;
    }
}

