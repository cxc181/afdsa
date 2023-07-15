package com.yuqian.itax.error.entity;

import java.io.Serializable;
import java.util.Date;

import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * 错误代码管理
 * 
 * @Date: 2019年12月08日 20:41:09 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_error_code")
public class ErrorCodeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(message="主键不能为空", groups={Update.class})
	private Long id;
	
	/**
	 * 错误代码
	 */
	@NotBlank(message="错误编码不能为空", groups={Add.class, Update.class})
	@Size(max = 16, message = "错误编码不能超过16位", groups={Add.class, Update.class})
	private String errorCode;
	
	/**
	 * 错别描述
	 */
	@Size(max = 128, message = "错别描述不能超过128位", groups={Add.class, Update.class})
	private String errorDesc;
	
	/**
	 * 是否转译 0-否 1-是
	 */
	@Min(value = 0, message = "是否转译有误", groups={Add.class, Update.class})
	@Max(value = 1, message = "是否转译有误", groups={Add.class, Update.class})
	private Integer isTranslation;
	
	/**
	 * 转译后文案
	 */
	@Size(max = 32, message = "转译后文案不能超过32位", groups={Add.class, Update.class})
	private String translationContent;
	
	/**
	 * 错误类型  1-参数错误 2-业务错误 3-数据库错误
	 */
	@Min(value = 1, message = "错误类型有误", groups={Add.class, Update.class})
	@Max(value = 3, message = "错误类型有误", groups={Add.class, Update.class})
	private Integer errorType;
	
	/**
	 * 是否内部错误码 1-内部错误码 2-外包错误码
	 */
	@Min(value = 1, message = "是否内部错误码有误", groups={Add.class, Update.class})
	@Max(value = 2, message = "是否内部错误码有误", groups={Add.class, Update.class})
	private Integer isInternalError;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	@Size(max = 64, message = "备注不能超过64位", groups={Add.class, Update.class})
	private String remark;
	
	
}
