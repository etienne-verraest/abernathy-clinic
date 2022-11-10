package com.abernathy.webinterface.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchPatientDto {

	@NotBlank(message = "First name must not be blank")
	private String firstName;

	@NotBlank(message = "First name must not be blank")
	private String lastName;

}
