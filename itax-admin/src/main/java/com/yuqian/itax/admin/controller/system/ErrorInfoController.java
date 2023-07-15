package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.error.entity.ErrorInfoEntity;
import com.yuqian.itax.error.entity.query.ErrorInfoQuery;
import com.yuqian.itax.error.service.ErrorInfoService;
import com.yuqian.itax.user.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误信息controller
 * @author：pengwei
 * @Date：2020/2/12 9:45
 * @version：1.0
 */
@RestController
@RequestMapping("error/info")
@Slf4j
public class ErrorInfoController extends BaseController {

    @Autowired
    private ErrorInfoService errorInfoService;

    @ApiOperation("列表页")
    @PostMapping("page")
    public ResultVo page(@RequestBody ErrorInfoQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PageInfo<ErrorInfoEntity> page = errorInfoService.page(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("详情")
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long id) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ErrorInfoEntity errorInfoEntity = errorInfoService.findById(id);
        if (errorInfoEntity == null) {
            return ResultVo.Fail("错误信息不存在");
        }
        return ResultVo.Success(errorInfoEntity);
    }

}
