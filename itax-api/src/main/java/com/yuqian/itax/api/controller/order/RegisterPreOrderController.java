package com.yuqian.itax.api.controller.order;

import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.order.entity.dto.RegisterPreOrderDTO;
import com.yuqian.itax.order.service.RegisterPreOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 *  @Author: lmh
 *  @Date: 2022/6/28
 *  @Description: 工商注册预订单控制器
 */
@Api(tags = "工商注册预订单控制器")
@Slf4j
@RestController
@RequestMapping("order/reg/pre")
public class RegisterPreOrderController extends BaseController {

    @Autowired
    private RegisterPreOrderService registerPreOrderService;

    @ApiOperation("新增/编辑注册预订单")
    @PostMapping("/addOrUpdatePreOrder")
    public ResultVo addOrUpdate(@RequestBody @Validated RegisterPreOrderDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVo.Fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        dto.setMemberId(getCurrUserId());
        dto.setAddUser(getCurrUseraccount());
        String preOrderNo = null;
        String regRedisTime = (System.currentTimeMillis() + 10000) + ""; // redis标识值
        boolean flag = true;
        try {
            // 防止重复点击
            boolean lockResult = redisService.lock(RedisKey.LOCK_REG_PRE_ORDER_KEY + getCurrUserId(), regRedisTime, 60);
            if (!lockResult) {
                flag = false;
                throw new BusinessException("请勿重复点击！");
            }
            preOrderNo = registerPreOrderService.addOrUpdate(dto);
        } catch (Exception e) {
            return ResultVo.Fail(e.getMessage());
        } finally {
            if (flag) {
                redisService.unlock(RedisKey.LOCK_REG_PRE_ORDER_KEY + getCurrUserId(), regRedisTime);
            }
        }
        return ResultVo.Success(preOrderNo);
    }

    @ApiOperation("删除注册预订单")
    @PostMapping("/deletePreOrder")
    public ResultVo deletePreOrder() {
        registerPreOrderService.deletePreOrder(getCurrUserId());
        return ResultVo.Success();
    }
}
