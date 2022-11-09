package com.abernathy.notes.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(PatientNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorMessage handlePatientNotFoundException(Exception ex, WebRequest request) {
		return new ErrorMessage(new Date(), HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(NoteNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorMessage handleNoteNotFoundException(Exception ex, WebRequest request) {
		return new ErrorMessage(new Date(), HttpStatus.NOT_FOUND, ex.getMessage());
	}

}
