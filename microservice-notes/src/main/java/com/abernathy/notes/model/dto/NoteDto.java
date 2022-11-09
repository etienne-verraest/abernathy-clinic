package com.abernathy.notes.model.dto;

import lombok.Data;

@Data
public class NoteDto {

	private String patientId;

	private String practitioner;

	private String content;
}
