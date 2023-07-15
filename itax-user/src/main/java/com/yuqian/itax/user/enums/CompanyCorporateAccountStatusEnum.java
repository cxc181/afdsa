package com.yuqian.itax.user.enums;

/**
 * 企业对公户状态枚举
 *
 * @author：yejian
 * @Date：2020/09/09 11:07
 * @version：1.0
 */
public enum CompanyCorporateAccountStatusEnum {

    NORMAL(1, "正常"),
    PROHIBIT(2, "冻结"),
    CANCELLED(3, "注销");
    private Integer value;
    private String message;

    private CompanyCorporateAccountStatusEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static CompanyCorporateAccountStatusEnum getByValue(Integer value) {
        for (CompanyCorporateAccountStatusEnum state : values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        return null;
    }
}
