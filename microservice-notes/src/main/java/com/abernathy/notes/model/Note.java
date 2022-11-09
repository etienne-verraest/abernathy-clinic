package com.abernathy.notes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "notes")
public class Note {

	@Id
	private String id;

	private String patientId;

	private String practitioner;

	private String content;

}