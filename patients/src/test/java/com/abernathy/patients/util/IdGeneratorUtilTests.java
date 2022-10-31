package com.abernathy.patients.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.abernathy.patients.model.Patient;
import com.abernathy.patients.util.IdGeneratorUtil;

class IdGeneratorUtilTests {

	@Test
	void testGenerateIdentifier_ShouldReturn_UniqueIdentifier() {

		// ARRANGE
		Patient patientMock = new Patient();
		patientMock.setFirstName("Alpha");
		patientMock.setLastName("Bravo");
		patientMock.setDateOfBirth("1998-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");
		patientMock.setId("AB10000");

		// ACT
		String response = IdGeneratorUtil.generateIdentifier(patientMock);

		// ASSERT
		assertThat(response.charAt(0)).isEqualTo('A');
		assertThat(response.charAt(1)).isEqualTo('B');
		assertThat(Integer.valueOf(response.substring(2, response.length()))).isGreaterThan(0);
	}

}
