package com.yuqian.itax.user.enums;


public enum UserLevelEnum {
    PLATFORM(1, "平台"),
    OEMANDPARK(2, "机构、园区"),
    PARTNER(3, "高级合伙人"),
    CITYPARTNER(4, "城市合伙人"),
    MEMBER(5, "会员"),
    OEMUSER(6, "机构用户"),
    PARKUSER(7, "园区用户"),
    PARTNERUSER(8, "高级合伙人用户"),
    CITYPARTNERUSER(9, "城市合伙人用户"),
    PLATFORMUSER(10, "平台用户");



    private Integer value;
    private String message = null;



    private UserLevelEnum(Integer value, String message) {
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
    public static UserLevelEnum getByValue(Integer value) {
        for (UserLevelEnum levelEnum : values()) {
            if (levelEnum.getValue().equals(value)) {
                return levelEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }

}
