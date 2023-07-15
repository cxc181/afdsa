package com.yuqian.itax.user.enums;


public enum UserSmsLockEnum {
    UNLOCK(0, "未锁定"),
    LOCK(1, "锁定"),

    ;



    private Integer value;
    private String message = null;



    private UserSmsLockEnum(Integer value, String message) {
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
    public static UserSmsLockEnum getByValue(Integer value) {
        for (UserSmsLockEnum levelEnum : values()) {
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
