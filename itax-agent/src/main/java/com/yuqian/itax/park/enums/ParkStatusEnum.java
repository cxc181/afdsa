package com.yuqian.itax.park.enums;

/**
 *  园区状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
 * @author：pengwei
 * @Date：2019/12/17 14:12
 * @version：1.0
 */
public enum ParkStatusEnum {
    STAY_SHELF(0, "待上线"),
    ON_SHELF(1, "已上架"),
    OFF_SHELF(2, "已下架"),
    PAUSED(3, "已暂停"),
    DELETE(4, "已删除");

    private Integer value;

    private String message = null;

    private ParkStatusEnum(Integer value, String message) {
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
    public static ParkStatusEnum getByValue(Integer value) {
        for (ParkStatusEnum statusEnum : values()) {
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

