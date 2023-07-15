package com.yuqian.itax.common.base.exception;

import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.ResultConstants;

/**
 * @Author Kaven
 * @Description 自定义业务异常
 * @Date 14:12 2019/8/12
 * @Param
 * @return
 **/
public class BusinessException extends RuntimeException {
    private Integer errCode;// 自定义异常编码
    private String errorCode;// 自定义异常编码
    private String errorMsg;// 自定义异常错误信息

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code,String message){
        super(message);
        this.errCode = code;
    }

    public BusinessException(ResultConstants enums){
        super(enums.getRetMsg());
        this.errorCode = enums.getRetCode();
        this.errorMsg = enums.getRetMsg();
    }

    public BusinessException(ErrorCodeEnum enums){
        super(enums.getText());
        this.errorCode = enums.getCode();
        this.errorMsg = enums.getText();
    }

    public Integer getErrCode() {
        return errCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
