package com.yuqian.itax.util.validator;

import com.yuqian.itax.util.validator.constraint.PasswordConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)  
@Constraint(validatedBy = {PasswordConstraint.class})
@Documented  
public @interface Password {
	
	/**
	 * 返回错误信息
	 * 
	 * @return
	 */
	String message() default "密码长度低于6位";  
	  
	/**
	 * 验证组
	 * 
	 * @return
	 */
    Class<?>[]groups() default {};  
  
    Class<? extends Payload>[]payload() default {};  
}
