package com.yuqian.itax.message.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/6 15:37
 *  @Description: CP发送返回结果枚举
 */
public enum SendResultCodeEnum {

	SUCCESS("SUCCESS", "成功"),

	LOGIN_FAILURE("LOGIN_FAILURE", "帐号或密码错误"),

	VALIDATE_SERVICE_FAILURE("VALIDATE_SERVICE_FAILURE", "验证服务调用失败"),
    
    CLIENT_IP_NOT_PERMISSION("CLIENT_IP_NOT_PERMISSION", "客户端ip地址被禁止"),
    
    EXCEED_MAX_CONNECT_NUMBER("EXCEED_MAX_CONNECT_NUMBER", "超过最大连接数"),
    
    PHONE_NUMBER_ERROR("PHONE_NUMBER_ERROR", "手机号码错误"),
    
    NOT_ENOUGH_BALANCE("NOT_ENOUGH_BALANCE", "短信余额不足"),
    
    ERROR("ERROR", "系统未知错误");
    
    private final String value;

    private final String message;

    /**
     * 私有构造方法
     * 
     * @param value
     * @param message
     */
    private SendResultCodeEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

}

