package com.abernathy.patients.validation.validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.abernathy.patients.validation.DateOfBirth;

public class DateOfBirthValidator implements ConstraintValidator<DateOfBirth, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if ((value != null) && !value.isEmpty()) {
			Pattern datePattern = Pattern.compile("^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$");
			return datePattern.matcher(value).matches();
		}
		return false;
	}

}
