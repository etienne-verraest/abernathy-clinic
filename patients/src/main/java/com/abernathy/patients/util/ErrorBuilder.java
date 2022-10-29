package com.abernathy.patients.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public class ErrorBuilder {

	public static ResponseEntity<String> buildErrorMessage(Errors errors) {
		// Create a StringBuilder that will contain incorrect fields values
		StringBuilder errorString = new StringBuilder();

		// For each found error, we get the field name and add it to the builder
		errors.getFieldErrors().stream().forEach(error -> errorString.append(error.getField() + ","));

		// When process is finished we delete the last comma
		errorString.deleteCharAt(errorString.length() - 1);

		// Returning the error message with fields
		return new ResponseEntity<>("The following fields are incorrect : " + errorString, HttpStatus.BAD_REQUEST);
	}
}
