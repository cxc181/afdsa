package com.yuqian.itax.capital.enums;

/**
 * 资金账户状态枚举
 * 0-禁用 1-可用
 * @author：pengwei
 * @Date：2020/7/14 20:50
 * @version：1.0
 */
public enum UserCapitalAccountStatusEnum {

    FORBIDDEN(0, "禁用"),
    AVAILABLE(1, "可用"),
    ;

    private Integer value;

    private String message;

    private UserCapitalAccountStatusEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static UserCapitalAccountStatusEnum getByValue(Integer value) {
        for (UserCapitalAccountStatusEnum statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}

