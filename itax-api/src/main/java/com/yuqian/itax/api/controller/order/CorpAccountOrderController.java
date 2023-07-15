package com.yuqian.itax.api.controller.order;

import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.corporateaccount.service.CorporateAccountWithdrawalOrderService;
import com.yuqian.itax.order.entity.dto.CorpAccountWithdrawOrderDTO;
import com.yuqian.itax.order.entity.vo.CorpAccountWithdrawOrderDetailVO;
import com.yuqian.itax.order.entity.vo.CorpAccountWithdrawOrderVO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description 对公户提现订单控制器
 * @Author  Kaven
 * @Date   2020/9/8 15:24
*/
@Api(tags = "对公户提现订单控制器")
@RestController
@RequestMapping("/corporateaccountorder")
@Slf4j
public class CorpAccountOrderController extends BaseController {
    @Autowired
    private CorporateAccountWithdrawalOrderService corporateAccountWithdrawalOrderService;

    /**
     * @Description 分页查询对公户提现订单
     * @Author  Kaven
     * @Date   2020/9/9 15:16
     * @Param ComCorpAccQuery
     * @Return ResultVo<CorpAccountWithdrawOrderVO>
     * @Exception
    */
    @ApiOperation("分页查询对公户提现订单")
    @PostMapping("listWithdrawOrderPage")
    public ResultVo<CorpAccountWithdrawOrderVO> listWithdrawOrderPage(@RequestBody ComCorpAccQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        query.setCurrUserId(getCurrUserId());
        query.setOemCode(this.getRequestHeadParams("oemCode"));
        CorpAccountWithdrawOrderVO corpAccountVO = this.corporateAccountWithdrawalOrderService.listWithdrawOrderPage(query);
        return ResultVo.Success(corpAccountVO);
    }

    /**
     * @Description 对公户提现订单详情查询
     * @Author  Kaven
     * @Date   2020/9/9 16:23
     * @Param   id
     * @Return  ResultVo<CorpAccountWithdrawOrderDetailVO>
     * @Exception  
    */
    @ApiOperation("对公户提现订单详情查询")
    @PostMapping("getWithdrawOrderDetail")
    public ResultVo<CorpAccountWithdrawOrderDetailVO> getWithdrawOrderDetail(@JsonParam("orderNo") String orderNo){
        if(StringUtil.isEmpty(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        CorpAccountWithdrawOrderDetailVO corpAccountVO = this.corporateAccountWithdrawalOrderService.getWithdrawOrderDetail(orderNo);
        return ResultVo.Success(corpAccountVO);
    }

    /**
     * @Description 创建对公户提现订单
     * @Author  Kaven
     * @Date   2020/9/8 16:00
     * @Param   CorpAccountWithdrawOrderDTO
     * @Return  ResultVo
     * @Exception
    */
    @ApiOperation("创建对公户提现订单")
    @PostMapping("createWithdrawOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.CORP_ACCOUNT_WITHDRAW_CREATE_ORDER, lockTime = 10)
    public ResultVo createWithdrawOrder(@RequestBody @Validated CorpAccountWithdrawOrderDTO dto, BindingResult result){
        if (null == dto) {
            return ResultVo.Fail("传入参数对象不能为空");
        }
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        dto.setOemCode(oemCode);
        dto.setCurrUserId(getCurrUserId());
        String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
        ;// 请求来源:支付宝or微信or其他
        if (StringUtil.isEmpty(sourceType)) {
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if (!("1".equals(sourceType) || "2".equals(sourceType) || "3".equals(sourceType)|| "4".equals(sourceType))) {
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        dto.setSourceType(Integer.parseInt(sourceType));
        String orderNo = this.corporateAccountWithdrawalOrderService.createCorpAccountWithdrawOrder(dto);
        // 返回值
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("orderNo",orderNo);
        return ResultVo.Success(dataMap);
    }

    /**
     * @Description 对公户提现-订单确认
     * @Author  Kaven
     * @Date   2020/9/8 16:47
     * @Param  CorpAccountWithdrawOrderDTO
     * @Return
     * @Exception
    */
    @ApiOperation("对公户提现-订单确认")
    @PostMapping("confirmWithdrawOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.CORP_ACCOUNT_WITHDRAW_CONFIRM_ORDER, lockTime = 10)
    public ResultVo confirmWithdrawOrder(@JsonParam String orderNo,@JsonParam String verifyCode){
        if(StringUtil.isEmpty(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        if(StringUtil.isEmpty(verifyCode)){
            return ResultVo.Fail("验证码不能为空");
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        String result = this.corporateAccountWithdrawalOrderService.confirmWithdrawOrder(orderNo,verifyCode,oemCode,getCurrUserId());
        if(StringUtils.isNotBlank(result)){
            return ResultVo.Fail(result);
        }
        return ResultVo.Success();
    }
}
