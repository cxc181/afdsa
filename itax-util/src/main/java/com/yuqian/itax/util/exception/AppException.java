package com.yuqian.itax.util.exception;

/**
 * @ClassName AppException
 * @Description 自定义异常
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
public class AppException extends RuntimeException {

    private static final long serialVersionUID = -9005356351742384779L;

    private String code;

    public AppException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
    }

    public AppException(String msg){
        super(msg);
        this.code = "-1";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
