package com.abernathy.patients.model.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddPatientDto {

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	private Date dateOfBirth;

	private char gender;

	private String address;

	private String phone;
}
