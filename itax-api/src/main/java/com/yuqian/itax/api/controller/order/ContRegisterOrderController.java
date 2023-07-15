package com.yuqian.itax.api.controller.order;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.service.ContRegisterOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: RenewOrderController
 * @Description: 续费订单控制器
 * @Author: LiuMenghao
 * @Date: 2021/02/05
 * @Version: 1.8
 */
@Api(tags = "订单控制器")
@RestController
@RequestMapping("/order/renewOrder")
@Slf4j
public class ContRegisterOrderController extends BaseController {

    @Autowired
    private ContRegisterOrderService contRegisterOrderService;

    /**
     * @param
     * @Description 创建托管费续费订单
     * @Author LiuMenghao
     * @Date 2021/02/03 11:14
     * @Return
     */
    @ApiOperation("创建托管费续费订单")
    @PostMapping("/createContRegOrder")
    public ResultVo createHostingRenewOrder(@JsonParam Long companyId) {
        if (null == companyId) {
            return ResultVo.Fail("企业id不能为空");
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        String orderNo = contRegisterOrderService.createContRegOrder(
                getCurrUserId(),
                getRequestHeadParams("oemCode"),
                companyId);
        resultMap.put("orderNo", orderNo);
        return ResultVo.Success(resultMap);
    }
}
