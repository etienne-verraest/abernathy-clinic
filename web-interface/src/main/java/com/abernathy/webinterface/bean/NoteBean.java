package com.abernathy.webinterface.bean;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NoteBean {

	private String id;
	private Date date;
	private String patientId;
	private String content;

}
