package com.abernathy.webinterface.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.abernathy.webinterface.validation.Name;

public class NameValidator implements ConstraintValidator<Name, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		if ((value != null) && value.length() > 0) {

			// Value must contain letters only and hyphen for composed name
			char[] nameCharacters = value.toCharArray();
			for (char ch : nameCharacters) {

				if (Character.isSpaceChar(ch)) {
					return false;
				}

				if (Character.isDigit(ch)) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

}
