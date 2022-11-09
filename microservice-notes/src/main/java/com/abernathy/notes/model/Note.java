package com.abernathy.notes.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@Document(collection = "notes")
public class Note {

	// TODO : Check if all attributes are present based on tests cases
	// TODO : Validate fields in DTO

	@Id
	private String id;

	private Date date;

	private String practitioner;

	private String patientId;

	private String content;

}
