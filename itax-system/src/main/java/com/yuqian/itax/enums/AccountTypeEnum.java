package com.yuqian.itax.enums;

/**
 * 系统用户类型
 * @author Karen
 *
 */
public enum AccountTypeEnum {
	
	SYSTEM_ADMIN("1","系统管理员"),
    SYSTEM_PARTNER("2","城市合伙人"),
    SYSTEM_AGENT("3","城市合伙人"),
    SYSTEM_SERVICER("4","坐席客服"),
    SYSTEM_FINANCE("5","财务"),
    SYSTEM_OPERATOR("6","经办人"),
    SYSTEM_OPERATE("7","运营");
	

	private final String value;

    private final String message;
    
    /**
     * 私有构造方法
     * 
     * @param value
     * @param message
     */
    private AccountTypeEnum(String value, String message) {
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
