package com.yuqian.itax.park.enums;

/**
 * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
 * @author：pengwei
 * @Date：2021/2/2 14:12
 * @version：1.0
 */
public enum ParkProcessMarkEnum {
    VIDEO(1, "标准视频认证流程"),
    IDENTITY(2, "确认身份验证开启流程"),
    SIGN(3, "工商局签名流程"),
    ;

    private Integer value;

    private String message = null;

    private ParkProcessMarkEnum(Integer value, String message) {
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
    public static ParkProcessMarkEnum getByValue(Integer value) {
        for (ParkProcessMarkEnum statusEnum : values()) {
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

