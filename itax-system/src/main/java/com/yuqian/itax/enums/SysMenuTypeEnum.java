package com.yuqian.itax.enums;

public enum SysMenuTypeEnum {
	
	/**
	 * 目录
	 */
	CATALOG(0),
	/**
	 * 菜单
	 */
	MENU(1),
	/**
	 * 按钮
	 */
	BUTTON(2);
	
	private int value;

	private SysMenuTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
