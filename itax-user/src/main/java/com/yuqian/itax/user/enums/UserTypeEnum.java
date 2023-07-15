package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/12 11:10
 *  @Description: 用户类型枚举类
 *  用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
 */
public enum UserTypeEnum {

    MEMBER(1, "会员"),
    SERVER(2, "城市合伙人"),
    PARTENER(3, "合伙人"),
    PALTFORM(4, "平台"),
    ADMIN(5, "管理员"),
    OTHER(6, "其他"),;

    private Integer value;

    private String message = null;

    private UserTypeEnum(Integer value, String message) {
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
    public static UserTypeEnum getByValue(Integer value) {
        for (UserTypeEnum statusEnum : values()) {
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

