package com.yuqian.itax.user.enums;

/**
 * 会员收件地址状态
 * @author：pengwei
 * @Date：2020/12/25 16:57
 * @version：1.0
 * 状态  1-可用 0-不可用
 */
public enum MemberAddressStatusEnum {

    NO(0, "不可用"),
    YES(1, "可用"),
    ;

    private Integer value;

    private String message = null;

    private MemberAddressStatusEnum(Integer value, String message) {
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
    public static MemberAddressStatusEnum getByValue(Integer value) {
        for (MemberAddressStatusEnum statusEnum : values()) {
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

