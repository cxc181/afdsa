package com.yuqian.itax.admin;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.util.exception.AppException;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.exception.NotLoginException;
import com.yuqian.itax.util.exception.NotPermissionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@ControllerAdvice
@SuppressWarnings("all")
public class ExceptionAdvic {

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    @ResponseBody
    public ResultVo exception(MaxUploadSizeExceededException exception) {
        String msg = exception.getMessage();
        log.error(msg);
        return new ResultVo(ExceptionEnum.MAXUPLOADSIZ_ERROR.getCode(),"上传文件大小超过最大限制");
    }

    /**
     * 业务需要自定义返回错误信息的，可以直接抛出该异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {IllegalArgumentException.class,AccountException.class})
    @ResponseBody
    public ResultVo exception(IllegalArgumentException exception) {
        String msg = exception.getMessage();
        log.error(msg);
        return new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),StringUtils.isEmpty(msg) ? "操作失败" : msg);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultVo exception(HttpServletRequest request,HttpRequestMethodNotSupportedException exception) {
        log.error(exception.getMessage() + "\t,URI:" + request.getRequestURI());
        return new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),"Request resource does not exist");
    }

    @ExceptionHandler(NotPermissionException.class)
    @ResponseBody
    public ResultVo exception(UnauthenticatedException exception) {
        log.error("鉴权异常："+exception.getMessage());
        return new ResultVo(ExceptionEnum.PERMISSIONS_ERROR.getCode(),"权限不足");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResultVo validationError(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder builder = new StringBuilder();
        for (FieldError error : fieldErrors) {
            builder.append(error.getDefaultMessage());
        }
        String errorMessage = "参数异常: " + builder.toString();
        log.error(errorMessage);
        return new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),errorMessage);
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultVo validationError(NotLoginException ex) {
//        ex.printStackTrace();
//        String errorMessage = "您尚未登录!";
        log.error(ex.getMessage());
        return new ResultVo(ex.getCode(),ex.getMessage());
    }

    @ExceptionHandler(value = {BusinessException.class,Exception.class})
    @ResponseBody
    public ResultVo exception(Exception exception) {
        ResultVo vo = null;
        if (exception instanceof AppException) {
            AppException appException = (AppException) exception;
            vo = new ResultVo(appException.getCode(),appException.getMessage());
            log.error(appException.getCode(), appException.getMessage());
        } else if (exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException) exception;
            String errorCode = org.apache.commons.lang3.StringUtils.isBlank(businessException.getErrorCode()) ? "0001" : businessException.getErrorCode();
            vo = new ResultVo(errorCode,businessException.getMessage());
            log.error(errorCode, businessException.getMessage());
        }else {
            vo = new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),ExceptionEnum.APPLY_FAIL.getMsg());
            log.error(ExceptionEnum.APPLY_FAIL.getMsg(), exception);
        }
        return vo;
    }
}
