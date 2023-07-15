package com.yuqian.itax.util.validator;

import com.yuqian.itax.util.validator.constraint.PhoneConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)  
@Constraint(validatedBy = {PhoneConstraint.class})
@Documented  
public @interface Phone {
	
	/**
	 * 返回错误信息
	 * 
	 * @return
	 */
	String message() default "手机号码格式错误";  
	  
	/**
	 * 验证组
	 * 
	 * @return
	 */
    Class<?>[]groups() default {};  
  
    Class<? extends Payload>[]payload() default {};  
}
