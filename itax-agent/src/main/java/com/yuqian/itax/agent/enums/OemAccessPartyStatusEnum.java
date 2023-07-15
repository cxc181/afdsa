package com.yuqian.itax.agent.enums;

/**
 * 机构管理状态 0-不可用 1-可用
 * @author：pengwei
 * @Date：2019/12/17 14:12
 * @version：1.0
 */
public enum OemAccessPartyStatusEnum {

    NO(2, "下架"),
    YES(1, "上架");

    private Integer value;

    private String message = null;

    private OemAccessPartyStatusEnum(Integer value, String message) {
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
    public static OemAccessPartyStatusEnum getByValue(Integer value) {
        for (OemAccessPartyStatusEnum statusEnum : values()) {
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

