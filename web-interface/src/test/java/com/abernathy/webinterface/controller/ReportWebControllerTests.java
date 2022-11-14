package com.abernathy.webinterface.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.abernathy.webinterface.bean.PatientBean;
import com.abernathy.webinterface.bean.ReportBean;
import com.abernathy.webinterface.proxy.MicroservicePatientsProxy;
import com.abernathy.webinterface.proxy.MicroserviceReportsProxy;

@WebMvcTest(controllers = ReportWebController.class)
class ReportWebControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MicroserviceReportsProxy reportsProxyMock;

	@MockBean
	private MicroservicePatientsProxy patientsProxyMock;

	private static PatientBean patientMock;
	private static ReportBean reportMock;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		// Creating a mock patient
		patientMock = new PatientBean();
		patientMock.setFirstName("Alpha");
		patientMock.setLastName("Bravo");
		patientMock.setDateOfBirth("1970-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");
		patientMock.setId("AB10000");

		// Creating a report mock
		reportMock = new ReportBean("AB10000", "Alpha", "Bravo", "M", "1970-02-16", 62, "Borderline",
				List.of("smoker", "weight"));
	}

	@Test
	void testGenerateReport_ShouldReturn_BorderlineRisk() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(reportsProxyMock.generateReport("AB10000")).thenReturn(reportMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/{patientId}/report/generate", "AB10000"))
				.andExpect(model().attribute("triggersNumber", 2)).andExpect(model().attributeExists("triggersString"))
				.andExpect(status().isOk());
	}

	@Test
	void testGenerateReport_ShouldReturn_NoneRisk() throws Exception {

		// ARRANGE
		// Creating a report mock
		ReportBean reportMock2 = new ReportBean("AB10000", "Alpha", "Bravo", "M", "1970-02-16", 62, "None",
				Collections.emptyList());
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(reportsProxyMock.generateReport("AB10000")).thenReturn(reportMock2);

		// ACT AND ASSERT
		mockMvc.perform(get("/{patientId}/report/generate", "AB10000"))
				.andExpect(model().attribute("triggersNumber", 0)).andExpect(status().isOk());
	}

}
