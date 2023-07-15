package com.yuqian.itax.enums;

/**
 * OSS前缀
 * @author：pengwei
 * @Date：2020/3/6 10:12
 * @version：1.0
 */
public enum OSSPrefixEnum {

	SYSTEM_ADMIN("ADMIN","系统管理员");

	private final String value;

    private final String message;

    /**
     * 私有构造方法
     *
     * @param value
     * @param message
     */
    private OSSPrefixEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }
    
    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
