package com.abernathy.notes.model.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class NoteDto {

	@NotBlank(message = "You must specify a patient ID")
	private String patientId;

	@NotBlank(message = "Notes/recommandations must not be blank")
	private String content;
}
