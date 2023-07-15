package com.yuqian.itax.api.controller.system;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.coupons.entity.query.CouponsQuery;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVO;
import com.yuqian.itax.coupons.service.CouponExchangeCodeService;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


/**
 * @Author: liumenghao
 * @Date: 2021/4/8
 * @Description: 优惠券控制器
 */
@Api(tags = "优惠券控制器")
@Slf4j
@RestController
@RequestMapping("/system/coupons")
public class CouponsController extends BaseController {

    @Autowired
    private CouponsIssueRecordService couponsIssueRecordService;

    @Autowired
    private CouponsService couponsService;

    @ApiOperation("优惠券列表查询")
    @PostMapping("/list")
    public ResultVo<PageInfo<CouponsIssueVO>> list(@RequestBody @Valid CouponsQuery query, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String oemCode = getRequestHeadParams("oemCode");
        PageInfo<CouponsIssueVO> couponsVOPageInfo = couponsIssueRecordService.listByMemberId(getCurrUserId(), oemCode, query);
        return ResultVo.Success(couponsVOPageInfo);
    }

    @ApiOperation("可用优惠券张数")
    @PostMapping("/count")
    public ResultVo<Map> count(@JsonParam String usableRange) {
        Integer num = couponsIssueRecordService.countUsable(getCurrUserId(), getRequestHeadParams("oemCode"), usableRange);
        Map<String,Integer> dataMap = Maps.newHashMap();
        dataMap.put("number",num);
        return ResultVo.Success(dataMap);
    }

    @ApiOperation("优惠券兑换")
    @PostMapping("/exchange")
    public ResultVo exchange(@JsonParam String exchangeCode) {
        couponsService.exchange(getCurrUserId(), getRequestHeadParams("oemCode"), exchangeCode);
        return ResultVo.Success();
    }
}