package com.yuqian.itax.api.controller.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.capital.entity.dto.UserRechargeDTO;
import com.yuqian.itax.capital.entity.dto.UserWithdrawDTO;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ErrorCodeConstants;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.dto.RegOrderFileDTO;
import com.yuqian.itax.order.entity.dto.RegisterOrderDTO;
import com.yuqian.itax.order.entity.dto.UpgradeOrderDTO;
import com.yuqian.itax.order.entity.query.ConsumptionRecordQuery;
import com.yuqian.itax.order.entity.query.RegOrderQuery;
import com.yuqian.itax.order.entity.vo.ConsumptionRecordVO;
import com.yuqian.itax.order.entity.vo.RegisterOrderVO;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.RegOrderStatusEnum;
import com.yuqian.itax.order.service.MemberConsumptionRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.profits.entity.query.MemberProfitsQuery;
import com.yuqian.itax.profits.entity.vo.MemberProfitsRecordVO;
import com.yuqian.itax.profits.entity.vo.MemberProfitsVO;
import com.yuqian.itax.profits.enums.ProfitsTypeEnum;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.ExtendRecordDetailVO;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: OrderController
 * @Description: 订单控制器
 * @Author: Kaven
 * @Date: Created in 2019/12/9
 * @Version: 1.0
 * @Modified By:
 */
@Api(tags = "订单控制器")
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProfitsDetailService profitsDetailService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MemberConsumptionRecordService memberConsumptionRecordService;

    /**
     * @Description 分页查询企业注册订单列表
     * @Author  Kaven
     * @Date   2019/12/6 16:28
     * @Param  query
     * @Return ResultVo
     */
    @ApiOperation("分页查询企业注册订单列表")
    @PostMapping("listOrderPage")
    public ResultVo<PageInfo<RegisterOrderVO>> listOrderPage(@RequestBody RegOrderQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        query.setUserId(getCurrUserId());
        query.setOemCode(this.getRequestHeadParams("oemCode"));
        PageInfo<RegisterOrderVO> pages = this.orderService.getOrderListPage(query);
        return ResultVo.Success(pages);
    }
    /**
     * @Description 创建工商注册订单
     * @Author  Kaven
     * @Date   2019/12/10 14:57
     * @Param  RegisterOrderEntity
     * @Return ResultVo
     */
    @ApiOperation("创建工商注册订单")
    @PostMapping("createRegOrder")
    public ResultVo createRegOrder(@RequestBody @Valid RegisterOrderDTO entity, BindingResult result){
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        if (null == entity) {
            return ResultVo.Fail("操作失败，订单对象不能为空！");
        }
        if (null == entity.getCompanyType()) {
            return ResultVo.Fail("企业类型不能为空");
        }
        String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
        ;// 请求来源:支付宝or微信or其他
        if (StringUtil.isEmpty(sourceType)) {
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if (!("1".equals(sourceType) || "2".equals(sourceType) || "3".equals(sourceType) || "4".equals(sourceType))) {
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        if ((MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(entity.getCompanyType())
                || MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(entity.getCompanyType())
                || MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(entity.getCompanyType()))
                && null == entity.getRegisteredCapital()) {
            return ResultVo.Fail("注册资本为空");
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try{
            entity.setSourceType(Integer.parseInt(sourceType));
            entity.setOemCode(this.getRequestHeadParams("oemCode"));
            String orderNo = this.orderService.createIndustryOrder(getCurrUser().getUserId(), entity);
            resultMap.put("orderNo",orderNo);
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(resultMap);
    }

    /**
     * @Description 开户订单取消
     * @Author  Kaven
     * @Date   2019/12/12 17:17
     * @Param  orderNo
     * @Return ResultVo
     */
    @ApiOperation("开户订单取消")
    @ApiImplicitParam(name="orderNo",value="订单号",dataType="String",required = true)
    @PostMapping("cancelRegOrder")
    public ResultVo cancelRegOrder(@JsonParam String orderNo){
        if (StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        JSONObject params = new JSONObject();
        params.put("orderNo", orderNo);

        String useraccount = getCurrUseraccount();
        OrderEntity entity = new OrderEntity();
        String oemCode = getRequestHeadParams("oemCode");
        entity.setOrderNo(orderNo);
        entity.setOemCode(oemCode);
        entity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        entity = orderService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("订单不存在");
        }
        if (entity.getOrderStatus() > RegOrderStatusEnum.TO_BE_CERTIFICATION.getValue() && !RegOrderStatusEnum.TO_CREATE.getValue().equals(entity.getOrderStatus())) {
            return ResultVo.Fail("订单"+ RegOrderStatusEnum.getByValue(entity.getOrderStatus()).getMessage() +"，不允许取消");
        }

        if(!entity.getUserId().equals(getCurrUserId())){
            return ResultVo.Fail("非法操作，您没有权限取消该订单");
        }
        String result = this.orderService.cancelOrder(entity, RegOrderStatusEnum.CANCELLED.getValue(), useraccount, true);
        // 订单已支付不允许取消
        if(ErrorCodeConstants.EXIST_PAYING_ERROR.toString().equals(result)){
            return ResultVo.Fail(ErrorCodeConstants.EXIST_PAYING_ERROR.toString(),"该订单已经支付成功，不允许取消");
        }
        return ResultVo.Success();
    }

    /**
     * @Description 工商注册订单：更新开户订单上传信息（签名/认证视频/补充资料）
     * @Author  Kaven
     * @Date   2019/12/11 10:50
     * @Param  signImgBase64  orderNo  step
     * @Return ResultVo
     */
    @ApiOperation("工商注册订单：更新开户订单上传信息（签名/认证视频/补充资料）")
    @PostMapping("updateRegOrderFile")
    public ResultVo updateRegOrderFile(@RequestBody @Validated RegOrderFileDTO uploadDto, BindingResult result){
        if(null == uploadDto){
            return ResultVo.Fail("参数不能为空！");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        if(StringUtils.isBlank(uploadDto.getFileUrl()) || StringUtils.isBlank(uploadDto.getOrderNo())
                || null == uploadDto.getStep()){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        Map<String,Object> resultMap = resultMap = this.orderService.updateRegOrderFile(getCurrUser().getUserId(),uploadDto.getFileUrl(),uploadDto.getOrderNo(),uploadDto.getStep(),uploadDto.getVersionCode());
        return ResultVo.Success(resultMap);
    }

    /**
     * @Description 用户提现
     * @Author  Kaven
     * @Date   2019/12/16 16:31
     * @Paramdto
     * @Return ResultVo
     * @Exception
     */
    @ApiOperation("用户提现")
    @PostMapping("withdraw")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.USER_WITHDRAW, lockTime = 10)
    public ResultVo withdraw(@RequestBody @Validated UserWithdrawDTO dto, BindingResult result) {
        if (null == dto) {
            return ResultVo.Fail("传入参数对象不能为空");
        }
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
        ;// 请求来源:支付宝or微信or其他
        if (StringUtil.isEmpty(sourceType)) {
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if (!("1".equals(sourceType) || "2".equals(sourceType)|| "3".equals(sourceType)|| "4".equals(sourceType))) {
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        dto.setSourceType(Integer.parseInt(sourceType));
        String oemCode = this.getRequestHeadParams("oemCode");
        dto.setOemCode(oemCode);
        dto.setUserId(getCurrUserId());
        String res = this.orderService.userWithdraw(dto);
        if (StringUtil.isNotEmpty(res) && res.contains("TX0001")) {
            String[] split = res.split(",");
            return ResultVo.Fail("提现失败，" + split[2]);
        }
        return ResultVo.Success(res);
    }

    /**
     * 查询消费提现免手续文案
     * @return
     */
    @PostMapping("queryConsumptionWithdrawExplain")
    public ResultVo queryConsumptionWithdrawExplain() {
        String withdrawExplain = orderService.queryConsumptionWithdrawExplain(getRequestHeadParams("oemCode"));
        Map<String, String> map = Maps.newHashMap();
        map.put("withdrawExplain", StringUtil.isEmpty(withdrawExplain) ? "" : withdrawExplain);
        return ResultVo.Success(map);
    }

    /**
     * @Description 计算提现到账金额
     * @Author  Kaven
     * @Date   2020/6/29 2:27 下午
     * @Param   orderAmount-提现金额 withdrawType：提现类型：1-消费钱包 2-佣金钱包
     * @Return  ResultVo<Map<String,Object>>
     * @Exception
     */
    @PostMapping("calWithdrawReachAmount")
    public ResultVo<Map<String,Object>> calWithdrawReachAmount(@JsonParam Long orderAmount,@JsonParam Integer withdrawType){
        if(null == orderAmount){
            return ResultVo.Fail("提现金额不能为空");
        }
        if(null == withdrawType){
            return ResultVo.Fail("提现类型不能为空");
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        Map<String,Object> dataMap = this.orderService.calWithdrawReachAmount(getCurrUserId(),oemCode,orderAmount,withdrawType);
        return ResultVo.Success(dataMap);
    }

    /**
     * @Description 用户充值
     * @Author  Kaven
     * @Date   2019/12/16 19:16
     * @Param UserRechargeDTO
     * @Return ResultVo
     * @Exception BusinessException
     */
    @ApiOperation("用户充值")
    @PostMapping("recharge")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.USER_RECHARGE, lockTime = 10)
    public ResultVo recharge(@RequestBody @Validated UserRechargeDTO dto, BindingResult result) {
        ResultVo resp = ResultVo.Success();
        if (null == dto) {
            return ResultVo.Fail("传入参数对象不能为空");
        }
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
        ;// 请求来源:支付宝or微信or其他
        if (StringUtil.isEmpty(sourceType)) {
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if (!("1".equals(sourceType) || "2".equals(sourceType) || "3".equals(sourceType) || "4".equals(sourceType))) {
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        dto.setSourceType(Integer.parseInt(sourceType));
        String oemCode = this.getRequestHeadParams("oemCode");
        dto.setCurrUserId(getCurrUserId());
        dto.setOemCode(oemCode);
        resp = this.orderService.userRecharge(dto);
        return resp;
    }

    /**
     * @Description 分页查询会员分润记录
     * @Author Kaven
     * @Date 2019/12/20 09:27
     * @param
     * @return ResultVo<PageResultVo<MemberProfitsVO>>
     */
    @ApiOperation("分页查询会员分润记录")
    @PostMapping("/queryProfitsPage")
    public ResultVo<PageInfo<MemberProfitsVO>> queryProfitsPage(@RequestBody MemberProfitsQuery query) {
        if(StringUtils.isBlank(query.getStartDate())){
            return ResultVo.Fail("查询开始时间不能为空");
        }
        if(StringUtils.isBlank(query.getEndDate())){
            return ResultVo.Fail("查询结束时间不能为空");
        }
        query.setUserId(getCurrUserId());
        query.setOemCode(this.getRequestHeadParams("oemCode"));
        PageInfo<MemberProfitsVO> pages = this.profitsDetailService.queryMemberProfitsList(query);
        return ResultVo.Success(pages);
    }

    /**
     * @Description 推广中心-会员分润记录查询（V1.0.7）
     * @Author  Kaven
     * @Date   2020/6/5 17:21
     * @Param MemberProfitsQuery
     * @Return ResultVo<PageInfo<MemberProfitsVO>>
     * @Exception
     */
    @ApiOperation("推广中心-会员分润记录查询")
    @PostMapping("/queryProfitsPageNew")
    public ResultVo<MemberProfitsRecordVO> queryProfitsPageNew(@RequestBody MemberProfitsQuery query) {
        query.setUserId(getCurrUserId());
        query.setOemCode(this.getRequestHeadParams("oemCode"));
        MemberProfitsRecordVO profitsRecord = this.profitsDetailService.queryMemberProfitsListNew(query);
        return ResultVo.Success(profitsRecord);
    }

    /**
     * @Description 分润补单
     * @Author ni.jiang
     * @Date 2020/05/08
     * @param
     * @return
     */
    @ApiOperation("分润补单")
    @PostMapping("/reStartProfitsDetail")
    public ResultVo reStartProfitsDetail(@JsonParam String orderNo) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setProfitStatus(3);
        if(StringUtils.isNotEmpty(orderNo)){
            orderEntity.setOrderNo(orderNo);
        }
        String oemCode = getRequestHeadParams("oemCode");
        orderEntity.setOemCode(oemCode);
        List<OrderEntity> orderList = orderService.select(orderEntity);
        orderList.forEach(vo -> {
            //添加分润明细数据 add ni.jiang
            try {
                profitsDetailService.saveProfitsDetailByOrderNo(vo.getOrderNo(), "admin");
            }catch (Exception e){
                //分润失败
                OrderEntity order = orderService.queryByOrderNo(vo.getOrderNo());
                order.setIsShareProfit(2);
                order.setProfitStatus(3);
                order.setUpdateTime(new Date());
                order.setUpdateUser("admin");
                order.setRemark("分润失败原因："+e.getMessage());
                orderService.editByIdSelective(order);

                // 短信通知紧急联系人String orderNo =
                DictionaryEntity dict = dictionaryService.getByCode("emergency_contact");
                if(null != dict){
                    String dicValue = dict.getDictValue();
                    String[] contacts = dicValue.split(",");
                    for(String contact : contacts){
                        Map<String,Object> map = new HashMap();
                        map.put("oemCode",order.getOemCode());
                        map.put("orderNo",orderEntity.getOrderNo());
                        smsService.sendTemplateSms(contact,order.getOemCode(), VerifyCodeTypeEnum.NOTICE.getValue(), map,1);
                        log.info("分润失败发送通知给【" + contact + "】成功");
                    }
                }
            }
        });
        return ResultVo.Success();
    }

    /**
     * @Description 分润类型查询接口
     * @Author Kaven
     * @Date 2020/5/18 11:21
     * @param
     * @return ResultVo<PageResultVo<MemberProfitsVO>>
     */
    @ApiOperation("分润类型查询")
    @PostMapping("/queryProfitsType")
    public ResultVo<List<Map<String,Object>>> queryProfitsType() {
        List<Map<String,Object>> list = Lists.newArrayList();
        for (ProfitsTypeEnum state : ProfitsTypeEnum.values()) {
            Map<String,Object> data = Maps.newHashMap();
            data.put("type",state.getValue());
            data.put("name",state.getMessage());
            list.add(data);
        }
        return ResultVo.Success(list);
    }

    /**
     * @Description 推广中心-推广记录明细查询
     * @Author  Kaven
     * @Date   2020/6/7 10:09 下午
     * @Param  MemberExtendQuery
     * @Return ResultVo<ExtendRecordDetailVO>
     * @Exception
     */
    @ApiOperation("推广中心-推广记录明细查询")
    @PostMapping("queryExtendRecordDetail")
    public ResultVo<ExtendRecordDetailVO> queryExtendRecordDetail(@RequestBody @Valid MemberExtendQuery query, BindingResult results) {
        log.info("收到查询推广记录明细请求：{}", JSON.toJSONString(query));
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        query.setUserId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        ExtendRecordDetailVO extendVO = this.orderService.queryExtendRecordDetail(query);
        return ResultVo.Success(extendVO);
    }

    /**
     * @param entity
     * @Description 创建会员升级订单
     * @Author yejian
     * @Date 2020/09/07 09:57
     * @Return ResultVo<String>
     */
    @ApiOperation("创建会员升级订单")
    @PostMapping("/createUpgradeOrder")
    public ResultVo<Map<String, Object>> createUpgradeOrder(@RequestBody @Valid UpgradeOrderDTO entity, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String orderNo = orderService.createUpgradeOrder(
                getCurrUserId(),
                getRequestHeadParams("oemCode"),
                entity,
                StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"));
        resultMap.put("orderNo", orderNo);
        return ResultVo.Success(resultMap);
    }

    /**
     * @Description 会员升级费用返还
     * @Author yejian
     * @Date 2020/09/07 13:57
     * @Return ResultVo
     */
    @ApiOperation("会员升级费用返还")
    @PostMapping("/upgradeReimbursement")
    @ApiImplicitParam(name = "upFlag", value = "会费返还达标标记（0-直推个体数达标 1-累计直推开票金额达标）", dataType = "Integer", required = true)
    public ResultVo upgradeReimbursement(@JsonParam Integer upFlag) {
        orderService.upgradeReimbursement(getCurrUserId(), getRequestHeadParams("oemCode"), upFlag);
        return ResultVo.Success();
    }

    /**
     * @Description 选择消费订单：根据用户ID获取可开票的订单列表（分页）
     * @Author  Kaven
     * @Date   2020/9/27 10:04
     * @Param WholeOrderQuery
     * @Return ResultVo<PageResultVo<WholeOrderVO>>
     * @Exception
     */
    @ApiOperation("选择消费订单：根据用户ID获取可开票的订单列表（分页）")
    @PostMapping("/listConsumptionRecordPage")
    public ResultVo<PageResultVo<ConsumptionRecordVO>> listConsumptionRecordPage(@RequestBody ConsumptionRecordQuery query) {
        List<ConsumptionRecordVO> orderList = this.memberConsumptionRecordService.listConsumptionRecord(getCurrUserId(), getRequestHeadParams("oemCode"), query);
        return ResultVo.Success(PageResultVo.restPage(orderList));
    }

    /**
     * 创建注册订单前置校验
     * @param idCardNumber
     * @return
     */
    @ApiOperation("创建注册订单前置校验")
    @PostMapping("/preCheckOfRegOrder")
    public ResultVo preCheckOfRegOrder(@JsonParam String idCardNumber) {
        orderService.preCheckOfRegOrder(idCardNumber);
        return ResultVo.Success();
    }

}