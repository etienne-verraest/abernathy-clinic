package com.abernathy.patients.util;

import org.springframework.validation.Errors;

import com.abernathy.patients.exceptions.IncorrectFieldValueException;

public class ErrorBuilderUtil {

	public static void buildErrorMessage(Errors errors) throws IncorrectFieldValueException {
		// Create a StringBuilder that will contain incorrect fields values
		StringBuilder errorString = new StringBuilder();

		// For each found error, we get the field name and add it to the builder
		errors.getFieldErrors().stream().forEach(error -> errorString.append(error.getField() + ", "));

		// When process is finished we delete the last space & comma
		String finalString = errorString.substring(0, errorString.length() - 2);

		// Returning the error message with fields
		throw new IncorrectFieldValueException("The following fields are incorrect : " + finalString);
	}
}
