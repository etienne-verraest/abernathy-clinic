package com.abernathy.webinterface.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportBean {

	private String patientId;
	private String firstName;
	private String lastName;
	private String gender;
	private String birthdate;
	private Integer age;
	private String risk;
	private List<String> triggers;

}
