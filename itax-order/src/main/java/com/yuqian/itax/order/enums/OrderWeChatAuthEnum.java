package com.yuqian.itax.order.enums;


import org.apache.commons.lang3.StringUtils;

/**
 * 微信通知授权状态 0-未授权 1-已授权
 * @author：pengwei
 * @Date：2020/6/8 16:19
 * @version：1.0
 */
public enum OrderWeChatAuthEnum {

    NO(0, "未授权"),
    YES(1, "已授权"),
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
    private OrderWeChatAuthEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    /**
     * 根据枚举类型的value获取枚举变量
     * 
     * @param value
     * @return
     */
    public static OrderWeChatAuthEnum getByValue(String value) {
        
        value = StringUtils.trimToEmpty(value);
        
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (OrderWeChatAuthEnum availableEnum : values()) {
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

