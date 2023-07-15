package com.yuqian.itax.workorder.enums;

/**
 * 工单处理人类型 0-本人 1-客服 2-经办人 3-管理员 4-工号
 * @author：pengwei
 * @Date：2019/12/23 9:12
 * @version：1.0
 */
public enum WorkProcessorTypeEnum {

    ONESELF(0, "本人"),
    CUSTOMER_SERVICE(1, "客服"),
    AGENT_ACCOUNT(2, "经办人"),
    ADMIN(3, "管理员"),
    WORK_NUMBER(4, "工号");

    private Integer value;

    private String message = null;

    private WorkProcessorTypeEnum(Integer value, String message) {
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
    public static WorkProcessorTypeEnum getByValue(Integer value) {
        for (WorkProcessorTypeEnum statusEnum : values()) {
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

