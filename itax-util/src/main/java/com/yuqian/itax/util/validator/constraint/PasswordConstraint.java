package com.yuqian.itax.util.validator.constraint;

import com.yuqian.itax.util.validator.Password;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class PasswordConstraint implements ConstraintValidator<Password, String>{

	@Override
	public void initialize(Password constraintAnnotation) {
		log.info("PasswordConstraint initialize method invoke...");
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return StringUtils.isBlank(value) || value.length() >= 6;
	}

}
