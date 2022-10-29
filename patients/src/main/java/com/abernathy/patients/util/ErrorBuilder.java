package com.abernathy.patients.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public class ErrorBuilder {

	public static ResponseEntity<String> buildErrorMessage(Errors errors) {
		// Create a StringBuilder that will contain incorrect fields values
		StringBuilder errorString = new StringBuilder();

		// For each found error, we get the field name and add it to the builder
		errors.getFieldErrors().stream().forEach(error -> errorString.append(error.getField() + ", "));

		// When process is finished we delete the last space & comma
		String finalString = errorString.substring(0, errorString.length() - 2);

		// Returning the error message with fields
		return new ResponseEntity<>("The following fields are incorrect : " + finalString + "\n"
				+ "Please check the documentation for more information", HttpStatus.BAD_REQUEST);
	}
}
