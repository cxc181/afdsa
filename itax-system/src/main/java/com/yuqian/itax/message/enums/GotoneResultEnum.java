package com.yuqian.itax.message.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/6 15:48
 *  @Description: 消息发送结果枚举
 */
public enum GotoneResultEnum {

    SUCCESS("SUCCESS", "成功"),
    
    GOTONE_MESSAGE_TYPE_NULL("GOTONE_MESSAGE_TYPE_NULL", "消息类型为空"),

    GOTONE_SYSTEM_FAILURE("GOTONE_SYSTEM_FAILURE", "系统异常"),

    GOTONE_VALIDATE_ERROR("GOTONE_VALIDATE_ERROR", "校验异常"),

    GOTONE_ILLEGAL_ARGUMENT("GOTONE_ILLEGAL_ARGUMENT", "非法参数"),

    GOTONE_CONTENT_LENGTH_INVALID("GOTONE_CONTENT_LENGTH_INVALID", "消息内容超长"),

    GOTONE_MSG_CONTENT_IS_NULL("GOTONE_MSG_CONTENT_IS_NULL", "消息体不可为空"),

    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "参数不正确"),

    SERVICE_CODE_NOT_EXIST("SERVICE_CODE_NOT_EXIST", "业务代码不存在或已被删除"),
    
    AGENCY_NO_IS_ERROR("AGENCY_NO_IS_ERROR", "参数agencyNo错误"),

    RECEIVER_IS_BLACKLIST("RECEIVER_IS_BLACKLIST", "接收者存在于黑名单"),

    MESSAGE_TEMPLATE_NOT_EXIST("MESSAGE_TEMPLATE_NOT_EXIST", "消息模板不存在"),

    SENDER_IS_BLACKLIST("SENDER_IS_BLACKLIST", "发送者存在于黑名单"),

    REQUEST_PARAMETER_ERROR("REQUEST_PARAMETER_ERROR", "请求参数错误"),

	PICTURE_CODE_SEND_SUCCESS("PICTURE_CODE_SEND_SUCCESS", "图片验证码发送成功"),

	PICTURE_CODE_SEND_FAIL("PICTURE_CODE_SEND_FAIL", "图片验证码发送失败"),

	PICTURE_CODE_VAIL_SUCCESS("PICTURE_CODE_VAIL_SUCCESS", "图片验证码校验成功"),

	PICTURE_CODE_VAIL_FAIL("PICTURE_CODE_VAIL_FAIL", "图片验证码校验失败"),

    //极光推送
    ADD_JPUSH_SUCCESS("ADD_JPUSH_SUCCESS", "添加极光推送对象成功"),

    ADD_JPUSH_FAIL("ADD_JPUSH_FAIL", "添加极光推送对象失败"),

    UPDATE_JPUSH_SUCCESS("UPDATE_JPUSH_SUCCESS", "更新极光推送对象成功"),

    UPDATE_JPUSH_FAIL("UPDATE_JPUSH_FAIL", "更新极光推送对象失败"),
    
    SEND_JPUSH_SUCCESS("SEND_JPUSH_SUCCESS", "极光推送发送成功"),

    SEND_JPUSH_FAIL("SEND_JPUSH_FAIL", "极光推送发送失败");

    private final String value;

    private final String message;

    /**
     * 私有构造方法
     * @param value
     * @param message
     */
    private GotoneResultEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    /**
     * @see Enum#toString()
     */
    @Override
    public String toString() {
        return value + "|" + message;
    }
    
}

