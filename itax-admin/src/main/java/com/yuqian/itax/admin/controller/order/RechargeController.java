package com.yuqian.itax.admin.controller.order;

import com.yuqian.itax.admin.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 充值记录controller
 * @author：pengwei
 * @Date：2019/12/16 11:12
 * @version：1.0
 */
@Api(tags = "充值记录")
@RestController
@RequestMapping("recharge")
@Slf4j
public class RechargeController extends BaseController {

   /* @Autowired
    UserService userService;

    @Autowired
    PayWaterService payWaterService;

    @ApiOperation("我的充值记录")
    @PostMapping("mine/page")
    public ResultVo listPageMineRecharger(@RequestBody PayWaterQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        if (query.getOrderType() == null) {
            query.setOrderTypes(PayWaterOrderTypeEnum.RECHARGE.getValue() + "," + PayWaterOrderTypeEnum.AGENT_RECHARGE.getValue());
        } else if (query.getOrderType() > 2 || query.getOrderType() < 1) {
            return ResultVo.Fail("订单类型不正确");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        query.setMemberId(userEntity.getId());
        if (userEntity.getAccountType()==1&&userEntity.getPlatformType()==1) {
            query.setUserType(UserTypeEnum.PALTFORM.getValue());
        } else {
            query.setUserTypeNotIn(UserTypeEnum.MEMBER.getValue() + "," + UserTypeEnum.PALTFORM.getValue());
        }
        PageInfo<PayWaterEntity> page = payWaterService.listPagePayWater(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("我的充值记录导出")
    @PostMapping("mine/export")
    public ResultVo listOpenOrderExport(@RequestBody PayWaterQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        if (query.getOrderType() == null) {
            query.setOrderTypes(PayWaterOrderTypeEnum.RECHARGE.getValue() + "," + PayWaterOrderTypeEnum.AGENT_RECHARGE.getValue());
        } else if (query.getOrderType() > 2 || query.getOrderType() < 1) {
            return ResultVo.Fail("订单类型不正确");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        query.setMemberId(userEntity.getId());
        if (userEntity.getAccountType()==1&&userEntity.getPlatformType()==1) {
            query.setUserType(UserTypeEnum.PALTFORM.getValue());
        } else {
            query.setUserTypeNotIn(UserTypeEnum.MEMBER.getValue() + "," + UserTypeEnum.PALTFORM.getValue());
        }
        List<PayWaterEntity> list = payWaterService.listPayWater(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("我的充值记录", "我的充值记录", PayWaterEntity.class, list, getRequest(), getResponse());
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("开户订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }*/
}
