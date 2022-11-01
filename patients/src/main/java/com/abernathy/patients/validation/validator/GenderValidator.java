package com.abernathy.patients.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.abernathy.patients.validation.Gender;

public class GenderValidator implements ConstraintValidator<Gender, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if (value != null) {
			value = value.toUpperCase();
			return ("M".equals(value) || "F".equals(value));
		}
		return false;
	}

}
