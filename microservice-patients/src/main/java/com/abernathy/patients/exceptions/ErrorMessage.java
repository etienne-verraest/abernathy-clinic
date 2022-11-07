package com.abernathy.patients.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {

	private Date time;
	private HttpStatus status;
	private String message;
}
