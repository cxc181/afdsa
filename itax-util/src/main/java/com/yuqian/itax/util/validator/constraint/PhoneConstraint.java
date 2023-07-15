package com.yuqian.itax.util.validator.constraint;

import com.yuqian.itax.util.util.MatcherUtil;
import com.yuqian.itax.util.validator.Phone;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class PhoneConstraint implements ConstraintValidator<Phone, String> {

	@Override
	public void initialize(Phone constraintAnnotation) {
		log.info("PhoneConstraint initialize method invoke...");
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return StringUtils.isBlank(value) || MatcherUtil.isPhone(value);
	}

}
