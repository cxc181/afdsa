package com.yuqian.itax.agreement.enums;

/**
 *  @Author: lmh
 *  @Date: 2022/02/17
 *  @Description: 模板状态枚举
 *  1-启用 2-禁用
 */
public enum TemplateStatusEnums {
    ENABLE(1,"启用"),
    FORBIDDEN(2, "禁用");

    private Integer value;

    private String message = null;

    private TemplateStatusEnums(Integer value, String message) {
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
    public static TemplateStatusEnums getByValue(Integer value) {
        for (TemplateStatusEnums statusEnum : values()) {
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
        for (TemplateStatusEnums statusEnum : values()) {
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

