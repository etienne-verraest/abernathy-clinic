package com.abernathy.patients.model.dto;

import javax.validation.constraints.NotBlank;

import com.abernathy.patients.validation.DateOfBirth;
import com.abernathy.patients.validation.Gender;
import com.abernathy.patients.validation.Name;
import com.abernathy.patients.validation.Phone;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientDto {

	@JsonIgnore
	private String id;

	@Name
	private String firstName;

	@Name
	private String lastName;

	@DateOfBirth
	private String dateOfBirth;

	@Gender
	private String gender;

	@NotBlank(message = "Address must not be blank")
	private String address;

	@Phone
	private String phone;
}
