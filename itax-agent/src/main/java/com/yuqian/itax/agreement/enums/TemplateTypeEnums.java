package com.yuqian.itax.agreement.enums;

/**
 *  @Author: lmh
 *  @Date: 2022/02/17
 *  @Description: 模板类型枚举
 *  1-收费标准 2-委托注册协议 3-园区办理协议
 */
public enum TemplateTypeEnums {
    RATES(1,"收费标准"),
    REGISTRATION_AGREEMENT(2, "个体户综合服务协议"),
    PARK_MANAGEMENT_AGREEMENT(3, "园区办理协议");

    private Integer value;

    private String message = null;

    private TemplateTypeEnums(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过枚举<code>value</code>获得枚举
     * 
     * @param value
     * @return
     */
    public static TemplateTypeEnums getByValue(Integer value) {
        for (TemplateTypeEnums statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 通过枚举<code>value</code>获得枚举
     *
     * @param value
     * @return
     */
    public static String getByMessage(Integer value) {
        for (TemplateTypeEnums statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum.getMessage();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }

}

