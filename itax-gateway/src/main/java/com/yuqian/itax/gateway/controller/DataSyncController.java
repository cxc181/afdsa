package com.yuqian.itax.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.error.service.ErrorInfoService;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.ComCancelOrderVO;
import com.yuqian.itax.order.entity.vo.InvoiceOrdVO;
import com.yuqian.itax.order.entity.vo.MemberUpgradeOrderVO;
import com.yuqian.itax.order.entity.vo.RegOrderVO;
import com.yuqian.itax.order.service.CompanyCancelOrderService;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.user.entity.dto.GenExtQrcodeDTO;
import com.yuqian.itax.user.entity.query.MemberRegisterQuery;
import com.yuqian.itax.user.entity.vo.MemberRegisterVO;
import com.yuqian.itax.user.service.MemberAccountService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Pattern;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 9:43
 *  @Description: 拓展宝数据同步（查询）控制器
 */
@Api(tags = "拓展宝信息同步服务API")
@RestController
@RequestMapping("/datasync")
@Slf4j
public class DataSyncController extends BaseController {
    @Autowired
    private ErrorInfoService errorInfoService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private CompanyCancelOrderService companyCancelOrderService;
    @Autowired
    private RegisterOrderService registerOrderService;

    /**
     * @Description 用户注册信息查询-分页
     * @Author  Kaven
     * @Date   2020/3/25 9:48
     * @Param MemberRegisterQuery result
     * @Return ResultVo<PageResultVo<MemberRegisterVO>>
     * @Exception
     */
    @PostMapping("queryRegisterData")
    public ResultVo<PageResultVo<MemberRegisterVO>> queryRegisterData(@RequestBody @Validated MemberRegisterQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        String  regex = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]";
        if(StringUtils.isNotEmpty(query.getRegisterStartTime()) && !Pattern.matches(regex,query.getRegisterStartTime())){
            return ResultVo.Fail("注册开始时间格式错误");
        }

        if(StringUtils.isNotEmpty(query.getRegisterEndTime())&& !Pattern.matches(regex,query.getRegisterEndTime())){
            return ResultVo.Fail("注册结束时间格式错误");
        }

        // 比较时间先后
        if(StringUtils.isNotEmpty(query.getRegisterStartTime()) && StringUtils.isNotEmpty(query.getRegisterEndTime())
                && query.getRegisterStartTime().compareTo(query.getRegisterEndTime()) > 0){
            return ResultVo.Fail("查询开始时间不能大于结束时间！");
        }

        Map<String,Object> dataMap = Maps.newHashMap();
        try{
            String oemCode = this.getRequestHeadParams("oemCode");
            query.setOemCode(oemCode);
            PageResultVo<MemberRegisterVO> pages = this.memberAccountService.queryRegisterData(query);
            return ResultVo.Success(pages);
        } catch (Exception e){
            log.error("系统未知异常{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "拓展宝数据同步（查询）控制器", "用户注册信息查询-分页", e.toString(), JSONObject.toJSONString(query), query.getOemCode(), "admin");
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * @Description 企业注册订单查询-分页
     * @Author  Kaven
     * @Date   2020/3/25 14:16
     * @Param   RegOrderQuery BindingResult
     * @Return ResultVo<PageResultVo<RegisterOrderVO>>
     * @Exception
     */
    @PostMapping("queryRegOrder")
    public ResultVo<PageResultVo<RegOrderVO>> queryRegOrder(@RequestBody @Validated TZBOrderQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        String  regex = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]";
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && !Pattern.matches(regex,query.getCreateTimeStart())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        if(StringUtils.isNotEmpty(query.getCreateTimeEnd())&& !Pattern.matches(regex,query.getCreateTimeEnd())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        // 比较时间先后
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && StringUtils.isNotEmpty(query.getCreateTimeEnd())
                && query.getCreateTimeEnd().compareTo(query.getCreateTimeEnd()) > 0){
            return ResultVo.Fail("查询开始时间不能大于结束时间！");
        }

        try {
            String oemCode = this.getRequestHeadParams("oemCode");
            query.setOemCode(oemCode);
            PageResultVo<RegOrderVO> pages = this.registerOrderService.queryRegOrderPage(query);
            return ResultVo.Success(pages);
        } catch (Exception e) {
            log.error("系统未知异常：{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "拓展宝数据同步（查询）控制器", "企业注册订单查询-分页", e.toString(), JSONObject.toJSONString(query), query.getOemCode(), "admin");
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * @Description 企业开票订单查询-分页
     * @Author  Kaven
     * @Date   2020/3/25 15:42
     * @Param   TZBOrderQuery BindingResult
     * @Return  ResultVo<PageResultVo<InvoiceOrdVO>>
     * @Exception
     */
    @PostMapping("queryInvoiceOrder")
    public ResultVo<PageResultVo<InvoiceOrdVO>> queryInvoiceOrder(@RequestBody @Validated TZBOrderQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        String  regex = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]";
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && !Pattern.matches(regex,query.getCreateTimeStart())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        if(StringUtils.isNotEmpty(query.getCreateTimeEnd())&& !Pattern.matches(regex,query.getCreateTimeEnd())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        // 比较时间先后
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && StringUtils.isNotEmpty(query.getCreateTimeEnd())
                && query.getCreateTimeEnd().compareTo(query.getCreateTimeEnd()) > 0){
            return ResultVo.Fail("查询开始时间不能大于结束时间！");
        }

        try {
            String oemCode = this.getRequestHeadParams("oemCode");
            query.setOemCode(oemCode);
            PageResultVo<InvoiceOrdVO> pages = this.invoiceOrderService.queryInvoiceOrder(query);
            return ResultVo.Success(pages);
        } catch (Exception e) {
            log.error("系统未知异常：{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "拓展宝数据同步（查询）控制器", "企业开票订单查询-分页", e.toString(), JSONObject.toJSONString(query), query.getOemCode(), "admin");
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * @Description 企业注销订单查询-分页
     * @Author  Kaven
     * @Date   2020/3/25 15:43
     * @Param   TZBOrderQuery BindingResult
     * @Return ResultVo<PageResultVo<InvoiceOrdVO>>
     * @Exception
     */
    @PostMapping("queryComCancelOrder")
    public ResultVo<PageResultVo<ComCancelOrderVO>> queryComCancelOrder(@RequestBody @Validated TZBOrderQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        String  regex = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]";
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && !Pattern.matches(regex,query.getCreateTimeStart())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        if(StringUtils.isNotEmpty(query.getCreateTimeEnd())&& !Pattern.matches(regex,query.getCreateTimeEnd())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        // 比较时间先后
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && StringUtils.isNotEmpty(query.getCreateTimeEnd())
                && query.getCreateTimeEnd().compareTo(query.getCreateTimeEnd()) > 0){
            return ResultVo.Fail("查询开始时间不能大于结束时间！");
        }

        try {
            String oemCode = this.getRequestHeadParams("oemCode");
            query.setOemCode(oemCode);
            PageResultVo<ComCancelOrderVO> pages = this.companyCancelOrderService.queryComCancelOrder(query);
            return ResultVo.Success(pages);
        } catch (Exception e) {
            log.error("系统未知异常：{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "拓展宝数据同步（查询）控制器", "企业注销订单查询-分页", e.toString(), JSONObject.toJSONString(query), query.getOemCode(), "admin");
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * @Description 会员升级订单查询-分页
     * @Author  Kaven
     * @Date   2020/3/25 15:47
     * @Param TZBOrderQuery BindingResult
     * @Return ResultVo<PageResultVo<MemberUpgradeOrderVO>>
     * @Exception
     */
    @PostMapping("queryUpgradeOrder")
    public ResultVo<PageResultVo<MemberUpgradeOrderVO>> queryUpgradeOrder(@RequestBody @Validated TZBOrderQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        String  regex = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]";
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && !Pattern.matches(regex,query.getCreateTimeStart())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        if(StringUtils.isNotEmpty(query.getCreateTimeEnd())&& !Pattern.matches(regex,query.getCreateTimeEnd())){
            return ResultVo.Fail("订单创建开始时间格式错误");
        }

        // 比较时间先后
        if(StringUtils.isNotEmpty(query.getCreateTimeStart()) && StringUtils.isNotEmpty(query.getCreateTimeEnd())
                && query.getCreateTimeEnd().compareTo(query.getCreateTimeEnd()) > 0){
            return ResultVo.Fail("查询开始时间不能大于结束时间！");
        }

        try {
            String oemCode = this.getRequestHeadParams("oemCode");
            query.setOemCode(oemCode);
            PageResultVo<MemberUpgradeOrderVO> pages = this.orderService.queryUpgradeOrder(query);
            return ResultVo.Success(pages);
        } catch (Exception e) {
            log.error("系统未知异常：{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "拓展宝数据同步（查询）控制器", "会员升级订单查询-分页", e.toString(), JSONObject.toJSONString(query), query.getOemCode(), "admin");
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * @Description 小程序推广二维码获取-拓展宝
     * @Author  Kaven
     * @Date   2020/3/26 11:13
     * @Param   GenExtQrcodeDTO BindingResult
     * @Return  ResultVo<Map>
     * @Exception
     */
    @PostMapping("generalizedQrCode")
    public ResultVo<Map> generalizedQrCode(@RequestBody @Validated GenExtQrcodeDTO dto, BindingResult result){
        if(null == dto){
            return ResultVo.Fail("参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        try {
            String oemCode = this.getRequestHeadParams("oemCode");
            String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
            dto.setOemCode(oemCode);
            dto.setSourceType(sourceType);
            String qrCode = this.memberAccountService.generalizedQrCode(dto);
            Map<String,String> dataMap = Maps.newHashMap();
            dataMap.put("qrCode",qrCode);
            return ResultVo.Success(dataMap);
        } catch (BusinessException e) {
            log.error("获取二维码异常：{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "拓展宝数据同步（查询）控制器", "小程序推广二维码获取", e.toString(), JSONObject.toJSONString(dto), dto.getOemCode(), "admin");
            return ResultVo.Fail(e.getMessage());
        }catch (Exception e) {
            log.error("系统未知异常：{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "拓展宝数据同步（查询）控制器", "小程序推广二维码获取", e.toString(), JSONObject.toJSONString(dto), dto.getOemCode(), "admin");
            return ResultVo.Fail(e.getMessage());
        }
    }
}