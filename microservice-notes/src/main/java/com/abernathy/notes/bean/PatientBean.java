package com.abernathy.notes.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PatientBean {

	private String id;

	private String firstName;

	private String lastName;

	private String dateOfBirth;

	private String gender;

	private String address;

	private String phone;
}
