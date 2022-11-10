package com.abernathy.webinterface.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {

	@NotBlank(message = "You must specify a patient ID")
	private String patientId;

	@NotBlank(message = "Notes/recommandations must not be blank")
	private String content;
}
