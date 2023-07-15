package com.yuqian.itax.api;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.error.service.ErrorInfoService;
import com.yuqian.itax.util.exception.AppException;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.XssAndSqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.List;


@SuppressWarnings("all")
@Slf4j
@ControllerAdvice
public class ExceptionAdvic implements RequestBodyAdvice{

    /**
     * 异常请求参数
     */
    private String params;

    @Autowired
    private ErrorInfoService errorInfoService;

    @Autowired
    protected RedisService redisService;

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

//    @ExceptionHandler(NotLoginException.class)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public ResultVo exception(NotLoginException ex) {
////        ex.printStackTrace();
//        String errorMessage = "您尚未登录!";
//        log.error(errorMessage+ex.getMessage());
//        return new ResultVo(ExceptionEnum.NO_LOGIN.getCode(),errorMessage);
//    }

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
    public ResultVo exception(Exception e) {
        ResultVo vo = null;
        if (e instanceof AppException) {
            AppException appException = (AppException) e;
            //vo = new ResultVo(appException.getCode(),appException.getMessage());
            vo = ResultVo.Fail(appException.getMessage());
            log.error(appException.getCode(), appException.getMessage());
        }else if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            //vo = new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),businessException.getMessage());
            if(StringUtil.isNotBlank(businessException.getErrorCode())){
                vo = ResultVo.Fail(businessException.getErrorCode(),businessException.getMessage());
            } else {
                vo = ResultVo.Fail(businessException.getMessage());
            }
            log.error(ResultConstants.FAIL.getRetCode(), businessException.getMessage());
        }else {
            //vo = new ResultVo(ExceptionEnum.APPLY_FAIL.getCode(),ExceptionEnum.APPLY_FAIL.getMsg());
            vo = ResultVo.Fail(ExceptionEnum.APPLY_FAIL.getMsg());
            log.error(ExceptionEnum.APPLY_FAIL.getMsg(), e);
        }

        //获取header
        String token = "";
        String oemCode = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            if ("token".endsWith(key)) {
                token = request.getHeader(key);
            }
            if ("oemcode".endsWith(key)) {
                oemCode = request.getHeader(key);
            }
        }

        // 解析token获取登陆用户
        String currUserJson = redisService.get(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token);
        String userAccount = "";
        if(org.apache.commons.lang3.StringUtils.isNotBlank(currUserJson)) {
            JSONObject currUserObject = JSONObject.parseObject(currUserJson);
            userAccount = currUserObject.getString("useraccount");
        }

        // 保存错误信息
        errorInfoService.addErrorInfo(1, request.getServletPath(), request.getServletPath().substring(request.getServletPath().lastIndexOf("/") + 1), e.getMessage(), params, oemCode, userAccount);
        return vo;
    }

    /** 此处如果返回false , 则不执行当前Advice的业务 */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /** 读取参数前执行,在此做些编码/解密/封装参数为对象的操作 */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        //获取请求参数
        try {
            MyInputMessage him = new MyInputMessage(inputMessage);
            this.params = him.getParamsString();
            return him;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputMessage;
    }

    /** 读取参数后执行 */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /** 无请求时的处理 */
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
