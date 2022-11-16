package com.abernathy.reports.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class MicroserviceNotStartedException extends Exception {

	public MicroserviceNotStartedException(String message) {
		super(message);
	}
}
