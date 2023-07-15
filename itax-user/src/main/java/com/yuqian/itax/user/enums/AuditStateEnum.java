package com.yuqian.itax.user.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/14 14:39
 *  @Description: 用章申请审批状态枚举类
 *  审核状态 0-待审核 1-已审核 2-审核不通过 3-已撤销
 */
public enum AuditStateEnum {

    TO_APPROVE(0, "待审核"),
    APPROVED(1, "已审核"),
    APPROVE_NO_PASS(2, "审核不通过"),
    WITHDRAW(3, "已撤销");

    private Integer value;

    private String message = null;

    private AuditStateEnum(Integer value, String message) {
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
    public static AuditStateEnum getByValue(Integer value) {
        for (AuditStateEnum statusEnum : values()) {
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

