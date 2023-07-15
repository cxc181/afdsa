package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.error.entity.ErrorCodeEntity;
import com.yuqian.itax.error.entity.query.ErrorCodeQuery;
import com.yuqian.itax.error.service.ErrorCodeService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 错误编码controller
 * @author：pengwei
 * @Date：2020/2/12 9:45
 * @version：1.0
 */
@RestController
@RequestMapping("error/code")
@Slf4j
public class ErrorCodeController extends BaseController {

    @Autowired
    private ErrorCodeService errorCodeService;

    @ApiOperation("列表页")
    @PostMapping("page")
    public ResultVo page(@RequestBody ErrorCodeQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PageInfo<ErrorCodeEntity> page = errorCodeService.page(query);
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
        ErrorCodeEntity codeEntity = errorCodeService.findById(id);
        if (codeEntity == null) {
            return ResultVo.Fail("错误编码不存在");
        }
        return ResultVo.Success(codeEntity);
    }

    @ApiOperation("新增")
    @PostMapping("add")
    public ResultVo add(@RequestBody @Validated(Add.class) ErrorCodeEntity entity, BindingResult result) {
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ErrorCodeEntity errorCodeEntity = errorCodeService.selectByCode(entity.getErrorCode(), null);
        if (errorCodeEntity != null) {
            return ResultVo.Fail("错误编码已经存在，请修改");
        }
        entity.setAddTime(new Date());
        entity.setAddUser(currUser.getUseraccount());
        entity.setId(null);
        errorCodeService.insertSelective(entity);
        return ResultVo.Success();
    }

    @ApiOperation("编辑")
    @PostMapping("edit")
    public ResultVo edit(@RequestBody @Validated(Update.class) ErrorCodeEntity entity, BindingResult result) {
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        ErrorCodeEntity entity1 = errorCodeService.findById(entity.getId());
        if (entity1 == null) {
            return ResultVo.Fail("当前错误编码id不存在");
        }
        ErrorCodeEntity errorCodeEntity = errorCodeService.selectByCode(entity.getErrorCode(), entity.getId());
        if (errorCodeEntity != null) {
            return ResultVo.Fail("错误编码已经存在，请修改");
        }
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        entity.setAddUser(null);
        entity.setAddTime(null);
        errorCodeService.editByIdSelective(entity);
        return ResultVo.Success();
    }
}
