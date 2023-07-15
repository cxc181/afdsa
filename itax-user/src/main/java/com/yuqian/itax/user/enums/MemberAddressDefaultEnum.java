package com.yuqian.itax.user.enums;

/**
 * 会员收件地址是否默认
 * @author：pengwei
 * @Date：2020/12/25 16:57
 * @version：1.0
 * 是否默认 0-不默认 1-默认
 */
public enum MemberAddressDefaultEnum {

    NO(0, "不默认"),
    YES(1, "默认"),
    ;

    private Integer value;

    private String message = null;

    private MemberAddressDefaultEnum(Integer value, String message) {
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
    public static MemberAddressDefaultEnum getByValue(Integer value) {
        for (MemberAddressDefaultEnum statusEnum : values()) {
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

