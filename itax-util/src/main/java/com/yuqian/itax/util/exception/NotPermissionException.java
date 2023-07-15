package com.yuqian.itax.util.exception;

/**
 * 未登录异常
 *
 * @date: 2018年5月29日 下午9:43:14 
 * @author LiuXianTing
 */
public class NotPermissionException extends RuntimeException {

	private static final long serialVersionUID = -1;

	private String code;

	public NotPermissionException(){
		super();
	}

	public NotPermissionException(ExceptionEnum exceptionEnum) {
		super(exceptionEnum.getMsg());
		this.code = exceptionEnum.getCode();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
