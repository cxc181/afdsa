package com.yuqian.itax.order.enums;

/**
 * 是否已退邮寄费 0-未退 1-已退
 * @author：pengwei
 * @Date：2020/9/25 14:55
 * @version：1.0
 */
public enum RefundPostageFeeEnum {
    NO_REFUND(0, "未退款"),
    REFUNDED(1, "已退款"),
    ;

    private Integer value;

    private String message = null;

    private RefundPostageFeeEnum(Integer value, String message) {
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
    public static RefundPostageFeeEnum getByValue(Integer value) {
        for (RefundPostageFeeEnum statusEnum : values()) {
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

