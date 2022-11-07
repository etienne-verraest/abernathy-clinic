package com.abernathy.patients.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.abernathy.patients.model.Patient;

class IdGeneratorUtilTests {

	@Test
	void testGenerateIdentifier_ShouldReturn_UniqueIdentifier() {

		final String FIRSTNAME = "Alpha";
		final String LASTNAME = "Bravo";

		// ARRANGE
		Patient patientMock = new Patient();
		patientMock.setFirstName(FIRSTNAME);
		patientMock.setLastName(LASTNAME);
		patientMock.setDateOfBirth("1998-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");

		// ACT
		String response = IdGeneratorUtil.generateIdentifier(patientMock);

		// ASSERT
		assertThat(response.charAt(0)).isEqualTo(FIRSTNAME.charAt(0));
		assertThat(response.charAt(1)).isEqualTo(LASTNAME.charAt(0));
		assertThat(Integer.valueOf(response.substring(2, response.length()))).isGreaterThan(0);
	}

}
