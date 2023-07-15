package com.yuqian.itax.api.controller.agent;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.product.service.ChargeStandardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 18:45
 *  @Description: 收费标准控制器
 */
@Api(tags = "收费标准控制器")
@Slf4j
@RestController
@RequestMapping("/agent/chargestandard")
public class ChargeStandardController extends BaseController {
    @Autowired
    private ChargeStandardService chargeStandardService;

    /**
     * @Description 获取平台收费标准（根据园区的政策，显示不同的收费标准）
     * @Author  Kaven
     * @Date   2019/12/10 17:17
     * @Param prodcutId
     * @Return ResultVo
    */
    @ApiOperation("获取平台收费标准")
    @ApiImplicitParam(name="prodcutId",value="产品id",dataType="Long",required = true)
    @PostMapping("getChargeStandard")
    public ResultVo getChargeStandard(@JsonParam Long productId, @JsonParam Long parkId){
        if(null == productId){
            return ResultVo.Fail("产品ID不能为空！");
        }
        JSONObject params = new JSONObject();
        params.put("productId", productId);
        Map<String,Object> dataMap = this.chargeStandardService.queryChargeStandards(productId, parkId, getCurrUserId(), null);
        return ResultVo.Success(dataMap);
    }
}