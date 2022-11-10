package com.abernathy.webinterface.dto;

import javax.validation.constraints.NotBlank;

import com.abernathy.webinterface.validation.DateOfBirth;
import com.abernathy.webinterface.validation.Gender;
import com.abernathy.webinterface.validation.Name;
import com.abernathy.webinterface.validation.Phone;
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
