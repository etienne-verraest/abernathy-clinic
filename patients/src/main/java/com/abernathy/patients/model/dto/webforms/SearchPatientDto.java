package com.abernathy.patients.model.dto.webforms;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchPatientDto {

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

}
