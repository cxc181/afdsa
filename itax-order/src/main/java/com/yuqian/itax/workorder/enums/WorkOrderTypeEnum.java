package com.yuqian.itax.workorder.enums;

/**
 * 工单类型 1- 办理核名 2-开票审核
 * @author：pengwei
 * @Date：2019/12/23 9:12
 * @version：1.0
 */
public enum WorkOrderTypeEnum {

    REGISTER(1, "办理核名"),
    INVOICE(2, "开票审核"),
    WATER(3, "流水审核"),
    ACHIEVEMENT(4, "成果审核");

    private Integer value;

    private String message = null;

    private WorkOrderTypeEnum(Integer value, String message) {
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
    public static WorkOrderTypeEnum getByValue(Integer value) {
        for (WorkOrderTypeEnum statusEnum : values()) {
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

