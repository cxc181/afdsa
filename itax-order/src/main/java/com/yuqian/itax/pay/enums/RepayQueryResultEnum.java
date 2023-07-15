package com.yuqian.itax.pay.enums;

/**
 * 代付结果查询枚举
 * 0-待处理，1-银行处理中，2-代付成功，3-代付失败
 * @author：pengwei
 * @Date：2020/01/02 10:12
 * @version：1.0
 */
public enum RepayQueryResultEnum {

    REPAY_INIT("0", "待处理"),
    REPAYING("1", "银行处理中"),
    REPAY_SUCCESS("2", "代付成功"),
    REPAY_FAILURE("3", "代付失败");

    private String value;

    private String message = null;

    private RepayQueryResultEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
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
    public static RepayQueryResultEnum getByValue(String value) {
        for (RepayQueryResultEnum statusEnum : values()) {
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

