package com.yuqian.itax.capital.enums;

/**
 * 是否管理员 0-否 1-是 2-平台管理员
 * @author：pengwei
 * @Date：2019/12/14 14:52
 * @version：1.0
 */
public enum SysUserAdminTypeEnum {
    NO(0, "否"),
    YES(1, "是"),
    ADMIN(2, "平台管理员");

    private Integer value;

    private String message = null;

    private SysUserAdminTypeEnum(Integer value, String message) {
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
    public static SysUserAdminTypeEnum getByValue(Integer value) {
        for (SysUserAdminTypeEnum statusEnum : values()) {
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

