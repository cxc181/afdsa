package com.yuqian.itax.util.validator;

import com.yuqian.itax.util.validator.constraint.IdCardConstraint;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)  
@Constraint(validatedBy = {IdCardConstraint.class})
@Documented  
public @interface IdCard {
	
	/**
	 * 返回错误信息
	 * 
	 * @return
	 */
	String message() default "身份证号码输入错误";  
	  
	/**
	 * 验证组
	 * 
	 * @return
	 */
    Class<?>[]groups() default {};  
  
    Class<? extends Payload>[]payload() default {};  
}
