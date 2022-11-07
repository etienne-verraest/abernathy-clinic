package com.abernathy.patients.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ApiController.class)
class ApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PatientDao patientDaoMock;

	private static List<Patient> patientsListMock = new ArrayList<Patient>();
	private static Patient patientMock;
	private static PatientDto patientDtoMock;

	@BeforeAll
	static void initialization() throws Exception {

		// Creating a mock patient for our operations
		patientMock = new Patient();
		patientMock.setFirstName("Alpha");
		patientMock.setLastName("Bravo");
		patientMock.setDateOfBirth("1998-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");
		patientMock.setId("AB10000");

		// Creating a DTO mock patient for our save and update operations
		patientDtoMock = new PatientDto();
		patientDtoMock.setFirstName("Alpha");
		patientDtoMock.setLastName("Bravo");
		patientDtoMock.setDateOfBirth("1998-02-16");
		patientDtoMock.setAddress("123 Imaginary Street");
		patientDtoMock.setGender("M");
		patientDtoMock.setPhone("123-400-5000");

		// Adding the mock patient to the mocked list
		patientsListMock.add(patientMock);
	}

	@Test
	void testGetPatientsWithId_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientById("AB10000")).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/api/patients").queryParam("id", "AB10000")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName").value("Alpha"));
	}

	@Test
	void testGetPatientsWithFirstAndLastName_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientsByFirstNameAndLastName("Alpha", "Bravo")).thenReturn(List.of(patientMock));

		// ACT AND ASSERT
		mockMvc.perform(get("/api/patients").queryParam("firstName", "Alpha").queryParam("lastName", "Bravo"))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].firstName").value("Alpha"));
	}

	@Test
	void testGetPatients_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatients()).thenReturn(List.of(patientMock));

		// ACT AND ASSERT
		mockMvc.perform(get("/api/patients")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName").value("Alpha"));

	}

	@Test
	void testRegisterPatient_ShouldReturn_StatusOk() throws Throwable {

		// ARRANGE
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(patientDtoMock);
		when(patientDaoMock.mapToEntity(any(PatientDto.class))).thenReturn(patientMock);
		when(patientDaoMock.savePatient(any(Patient.class))).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(post("/api/patients").content(json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Alpha"));
	}

	@Test
	void testRegisterPatient_ShouldReturn_StatusBadRequest() throws Exception {

		// ARRANGE
		PatientDto badMockedDto = new PatientDto();
		badMockedDto.setFirstName("Alpha");
		badMockedDto.setLastName("");
		badMockedDto.setDateOfBirth("16-02-1998");
		badMockedDto.setAddress("123 Imaginary Street");
		badMockedDto.setGender("A");
		badMockedDto.setPhone("000-000-");

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(badMockedDto);

		// ACT AND ASSERT
		mockMvc.perform(post("/api/patients").content(json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

		verify(patientDaoMock, never()).savePatient(any(Patient.class));
	}

	@Test
	void testUpdatePatient_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(patientDtoMock);

		when(patientDaoMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(patientDaoMock.mapToEntity(any(PatientDto.class))).thenReturn(patientMock);
		when(patientDaoMock.savePatient(any(Patient.class))).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(put("/api/patients/{id}", "AB10000").content(json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	void testDeletePatient_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(patientDaoMock.deletePatient("AB10000")).thenReturn(true);

		// ACT AND ASSERT
		mockMvc.perform(delete("/api/patients/{id}", "AB10000")).andExpect(status().isOk());

	}

}
