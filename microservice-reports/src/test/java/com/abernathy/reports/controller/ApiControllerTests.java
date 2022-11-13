package com.abernathy.reports.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.abernathy.reports.generator.ReportsGenerator;
import com.abernathy.reports.model.Report;

@WebMvcTest(controllers = ApiController.class)
class ApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReportsGenerator reportsGenerator;

	static Report mockReport;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ArrayList<String> triggers = new ArrayList<String>(List.of("smoker", "weight", "height"));
		mockReport = new Report("AB10000", "Alpha", "Bravo", "M", "1998-02-16", 24, "In Danger", triggers);
	}

	@Test
	void testGenerateReport_ShouldReturn_ReportAndStatusOk() throws Exception {

		// ARRANGE
		when(reportsGenerator.generateReports("AB10000")).thenReturn(mockReport);

		// ACT AND ASSERT
		mockMvc.perform(get("/api/reports/generate/{patientId}", "AB10000")).andExpect(status().isOk())
				.andExpect(jsonPath("$.patientId").value("AB10000"));
	}

}
