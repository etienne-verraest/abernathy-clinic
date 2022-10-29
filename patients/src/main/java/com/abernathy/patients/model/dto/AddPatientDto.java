package com.abernathy.patients.model.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.abernathy.patients.validation.Phone;

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

	@NotBlank
	private String gender;

	@NotBlank
	private String address;

	@Phone
	private String phone;
}
