package com.yuqian.itax.common.base.vo;

import com.yuqian.itax.common.constants.ResultConstants;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;

/**
 * @ClassName ResultVo
 * @Description app返回结果
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
public class ResultVo<T> {

	/**
	 * 返回code
	 */
	@ApiModelProperty(value = "错误编码")
	private String retCode;

	/**
	 * 返回信息
	 */
	@ApiModelProperty(value = "错误信息")
	private String retMsg;

	/**
	 * 返回数据
	 */
	@ApiModelProperty(value = "返回对象")
	private T data;

	public ResultVo() {
		super();
	}

	public ResultVo(ResultConstants constants) {
		super();
		this.retCode = constants.getRetCode();
		this.retMsg = constants.getRetMsg();
	}
	public ResultVo(ResultConstants constants,T data) {
		super();
		this.retCode = constants.getRetCode();
		this.retMsg = constants.getRetMsg();
		if(null != data){
			this.data = data;
		}
	}

	public ResultVo(String retCode, String retMsg) {
		super();
		this.retCode = retCode;
		this.retMsg = retMsg;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	/**
	 * 操作成功
	 * @return
	 */
	public static ResultVo Success() {
		return new ResultVo(ResultConstants.SUCCESS);
	}

	/**
	 * 操作成功
	 * @return
	 */
	public static <T> ResultVo<T> Success(T data) {
		return new ResultVo<T>(ResultConstants.SUCCESS,data);
	}

	/**
	 * 操作失败
	 * @return
	 */
	public static ResultVo Fail() {
		return new ResultVo(ResultConstants.FAIL);
	}

	/**
	 * 操作失败
	 * @return
	 */
	public static ResultVo Fail(String msg) {
		return new ResultVo(ResultConstants.FAIL.getRetCode(),msg);
	}

	/**
	 * 操作失败
	 * @return
	 */
	public static ResultVo Fail(String errCode,String msg) {
		if(StringUtils.isEmpty(errCode)){
			errCode = ResultConstants.FAIL.getRetCode();
		}
		return new ResultVo(errCode,msg);
	}

	/**
	 * 操作失败
	 * @param result
	 * @return
	 */
	public static ResultVo Fail(BindingResult result) {
		return Fail(ResultConstants.FAIL.getRetCode(), result);
	}

	/**
	 * 操作失败
	 * @param retCode
	 * @param result
	 * @return
	 */
	public static ResultVo Fail(String retCode, BindingResult result) {
		return new ResultVo(retCode, result.getFieldError().getDefaultMessage());
	}
}
