package com.abernathy.patients.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectFieldValueException extends Exception {

	public IncorrectFieldValueException(String message) {
		super(message);
	}
}