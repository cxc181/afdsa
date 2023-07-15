package com.yuqian.itax.gateway;

import com.alibaba.fastjson.JSONException;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.util.exception.AppException;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.exception.NotLoginException;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.XssAndSqlUtils;
import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@SuppressWarnings("all")
@Slf4j
@ControllerAdvice
public class ExceptionAdvic {

    /**
     * 业务需要自定义返回错误信息的，可以直接抛出该异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {IllegalArgumentException.class, XssAndSqlUtils.XssAndSqlException.class})
    @ResponseBody
    public ResultVo exception(IllegalArgumentException exception) {
        String msg = exception.getMessage();
        log.error(msg);
        exception.printStackTrace();
        return new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),StringUtils.isEmpty(msg) ? "操作失败" : msg);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultVo exception(HttpServletRequest request,HttpRequestMethodNotSupportedException exception) {
        log.debug(exception.getMessage() + "\t,URI:" + request.getRequestURI());
        return new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),"Request resource does not exist");
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public ResultVo exception(NotLoginException ex) {
//        ex.printStackTrace();
        String errorMessage = "您尚未登录!";
        log.error(errorMessage+ex.getMessage());
        return new ResultVo(ExceptionEnum.NO_LOGIN.getCode(),errorMessage);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResultVo exception(BindException ex) {
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

    @ExceptionHandler(value = {BusinessException.class,Exception.class})
    @ResponseBody
    public ResultVo exception(Exception exception) {
        ResultVo vo = null;
        if (exception instanceof AppException) {
            AppException appException = (AppException) exception;
            vo = new ResultVo(appException.getCode(),appException.getMessage());
            log.error(appException.getCode(), appException.getMessage());
        }else if (exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException) exception;
            if(StringUtil.isNotBlank(businessException.getErrorCode())){
                vo = new ResultVo(businessException.getErrorCode(),businessException.getMessage());
            } else {
                vo = new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(), businessException.getMessage());
            }
            log.error(businessException.getErrorCode(), businessException.getMessage());
        }else if (exception instanceof JSONException){
            vo = new ResultVo(ErrorCodeEnum.PARAM_FORMAT_ERROR.getCode(),ErrorCodeEnum.PARAM_FORMAT_ERROR.getText());
            log.error(ErrorCodeEnum.PARAM_FORMAT_ERROR.getText(), exception);
        }else {
            vo = new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),ExceptionEnum.APPLY_FAIL.getMsg());
            log.error(ExceptionEnum.APPLY_FAIL.getMsg(), exception);
        }
        return vo;
    }
}
