package com.abernathy.patients.bean;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class NoteBean {

	private String id;
	private Date date;
	private String patientId;
	private String content;

}
