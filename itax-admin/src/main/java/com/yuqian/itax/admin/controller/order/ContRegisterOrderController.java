package com.yuqian.itax.admin.controller.order;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.query.ContRegisterOrderQuery;
import com.yuqian.itax.order.entity.vo.ContRegisterOrderVO;
import com.yuqian.itax.order.service.ContRegisterOrderService;
import com.yuqian.itax.user.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * 企业续费订单
 * @author：pengwei
 * @Date：2021/2/4 9:53
 * @version：1.0
 */
@RestController
@RequestMapping("cont/register/order")
@Slf4j
public class ContRegisterOrderController extends BaseController {

    @Autowired
    private ContRegisterOrderService contRegisterOrderService;

    @ApiOperation("企业续费订单列表页")
    @PostMapping("page")
    public ResultVo listPageContRegOrder(@RequestBody ContRegisterOrderQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (!Objects.equals(userEntity.getPlatformType(), 1)) {
            query.setOemCode(userEntity.getOemCode());
        }
        try {
            if (Objects.equals(userEntity.getPlatformType(), 3)) {
                query.setParkId(userEntity.getParkId());
            } else {
                query.setTree(getOrgTree());
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        query.setPlatformType(userEntity.getPlatformType());
        PageInfo<ContRegisterOrderVO> page = contRegisterOrderService.listPageContRegOrder(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("企业续费订单导出")
    @PostMapping("export")
    public ResultVo export(@RequestBody ContRegisterOrderQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (!Objects.equals(userEntity.getPlatformType(), 1)) {
            query.setOemCode(userEntity.getOemCode());
        }
        try {
            if (Objects.equals(userEntity.getPlatformType(), 3)) {
                query.setParkId(userEntity.getParkId());
            } else {
                query.setTree(getOrgTree());
            }
            query.setPlatformType(userEntity.getPlatformType());
            List<ContRegisterOrderVO> lists = contRegisterOrderService.listContRegOrder(query);
            if (CollectionUtil.isEmpty(lists)) {
                return ResultVo.Fail("暂无数据导出");
            }
            exportExcel("企业续费订单", "企业续费订单", ContRegisterOrderVO.class, lists);
            return null;
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error("企业续费订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

}
