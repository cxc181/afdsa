package com.yuqian.itax.message.enums;


import org.apache.commons.lang3.StringUtils;

/**
 * 微信通知模板
 * @author：pengwei
 * @Date：2020/6/8 16:19
 * @version：1.0
 */
public enum WeChatMessageTemplateTypeEnum {

    /*企业注册审核结果通知*/
    REGISTER_AUDIT_RESULT(1, "企业注册订单核名驳回"),
    INVITE_SIGN(2, "邀请签名"),
    SIGN_SURE_RESULT(3, "签名确认结果"),
    ;


    /**
     * 枚举值
     */
    private final Integer value;

    /**
     * 枚举值描述
     */
    private final String message;

    /**
     * 私有构造方法
     *
     * @param value
     * @param message
     */
    private WeChatMessageTemplateTypeEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    /**
     * 根据枚举类型的value获取枚举变量
     * 
     * @param value
     * @return
     */
    public static WeChatMessageTemplateTypeEnum getByValue(String value) {
        
        value = StringUtils.trimToEmpty(value);
        
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (WeChatMessageTemplateTypeEnum availableEnum : values()) {
            if (value.equals(availableEnum.getValue().toString())){
                return availableEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

}

