package com.abernathy.patients.model.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class NoteDto {

	@NotBlank(message = "You must specify a patient ID")
	private String patientId;

	@NotBlank(message = "Notes/recommandations must have at least 1 character")
	private String content;
}
