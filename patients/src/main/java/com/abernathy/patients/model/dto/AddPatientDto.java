package com.abernathy.patients.model.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddPatientDto {

	private String firstName;

	private String lastName;

	private Date dateOfBirth;

	private char gender;

	private String address;

	private String phone;
}
