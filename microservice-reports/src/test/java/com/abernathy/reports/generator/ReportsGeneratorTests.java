package com.abernathy.reports.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abernathy.reports.bean.PatientBean;
import com.abernathy.reports.model.Report;
import com.abernathy.reports.proxy.MicroserviceNotesProxy;
import com.abernathy.reports.proxy.MicroservicePatientsProxy;

@ExtendWith(MockitoExtension.class)
class ReportsGeneratorTests {

	@InjectMocks
	ReportsGenerator reportsGenerator;

	@Mock
	MicroservicePatientsProxy patientsProxy;

	@Mock
	MicroserviceNotesProxy notesProxy;

	private static PatientBean patientOver30;
	private static PatientBean patientUnder30;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		// Creating mock patients for our test reports
		patientOver30 = new PatientBean();
		patientOver30.setFirstName("Zulu");
		patientOver30.setLastName("Foxtrot");
		patientOver30.setDateOfBirth("1968-02-16");
		patientOver30.setAddress("526 Imaginary Street");
		patientOver30.setGender("M");
		patientOver30.setPhone("123-400-5000");
		patientOver30.setId("ZF11000");

		patientUnder30 = new PatientBean();
		patientUnder30.setFirstName("Hotel");
		patientUnder30.setLastName("Charlie");
		patientUnder30.setDateOfBirth("2000-02-16");
		patientUnder30.setAddress("9456 Broadway Avenue");
		patientUnder30.setGender("M");
		patientUnder30.setPhone("123-400-5000");
		patientUnder30.setId("HC11000");

	}

	@Test
	void testGenerateReports_PatientOver30_NoTriggers_ShouldReturnNone() {

		// ARRANGE
		when(patientsProxy.getPatientById("ZF11000")).thenReturn(patientOver30);
		when(notesProxy.getPatientHistory("ZF11000")).thenReturn(Collections.emptyList());

		// ACT
		Report response = reportsGenerator.generateReports("ZF11000");

		assertThat(response.getRisk()).isEqualTo("None");
		assertThat(response.getTriggers()).hasSize(0);
	}

}
