package com.yuqian.itax.workorder.enums;

/**
 * 工单状态枚举类
 * 类型为办理核名：0-待接单 1-处理中 2-已完成 3-已取消
 * @author：pengwei
 * @Date：2019/12/23 9:12
 * @version：1.0
 */
public enum WorkOrderStatusEnum {

    WAITING_FOR_ORDERS(0, "待接单"),
    AUDITING(1, "审核中"),
    AUDIT_SUCCESS(2, "审核通过"),
    AUDIT_FAILED(3, "审核未通过"),
    CANCELED(4, "已取消"),
    REJECTED(5, "核名驳回"),
    ;

    private Integer value;

    private String message = null;

    private WorkOrderStatusEnum(Integer value, String message) {
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
    public static WorkOrderStatusEnum getByValue(Integer value) {
        for (WorkOrderStatusEnum statusEnum : values()) {
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

