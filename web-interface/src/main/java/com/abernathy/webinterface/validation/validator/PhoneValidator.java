package com.abernathy.webinterface.validation.validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.abernathy.webinterface.validation.Phone;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if ((value != null) && !value.isEmpty()) {
			Pattern phoneNumberPattern = Pattern.compile("^[0-9]{3}-[0-9]{3}-[0-9]{4}$");
			return phoneNumberPattern.matcher(value).matches();
		}
		return false;
	}

}
