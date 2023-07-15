package com.yuqian.itax.user.enums;


/**
 *  账号类型枚举  1-管理员  2-坐席客服 3-普通用户
 * @author：pengwei
 * @Date：2019/12/24 11:17
 * @version：1.0
 */
public enum UserAccountTypeEnum {

    ADMIN(1, "管理员"),
    SERVER(2, "坐席客服"),
    PEOPLE(3, "普通用户");

    private Integer value;

    private String message = null;

    private UserAccountTypeEnum(Integer value, String message) {
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
    public static UserAccountTypeEnum getByValue(Integer value) {
        for (UserAccountTypeEnum statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }

}

