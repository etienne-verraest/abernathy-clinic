package com.abernathy.patients.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;

@WebMvcTest(controllers = WebController.class)
class WebControllerTests {

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
	void testShowSearchPatientForm_Successful() throws Exception {

		// ACT AND ASSERT
		mockMvc.perform(get("/")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("searchPatientDto"));
	}

	@Test
	void testSubmitSearchPatienForm_NoPatientFound_Successful() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientsByFirstNameAndLastName("Zulu", "Foxtrot"))
				.thenThrow(PatientNotFoundException.class);

		// ACT AND ASSERT
		mockMvc.perform(post("/search") //
				.param("firstName", "Zulu") //
				.param("lastName", "Foxtrot")) //
				.andExpect(status().isOk());

	}

	@Test
	void testSubmitSearchPatienForm_FoundOnePatient_Successful() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientsByFirstNameAndLastName("Alpha", "Bravo")).thenReturn(List.of(patientMock));

		// ACT AND ASSERT
		mockMvc.perform(post("/search") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo")) //
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/search/AB10000"));

	}

	@Test
	void testSubmitSearchPatienForm_FoundTwoPatients_Successful() throws Exception {

		// ARRANGE
		Patient patientMock2 = new Patient();
		patientMock2.setFirstName("Alpha");
		patientMock2.setLastName("Bravo");
		patientMock2.setDateOfBirth("1998-02-16");
		patientMock2.setAddress("456 Imaginary Street");
		patientMock2.setGender("M");
		patientMock2.setPhone("123-400-5000");
		patientMock2.setId("AB20000");
		when(patientDaoMock.getPatientsByFirstNameAndLastName("Alpha", "Bravo"))
				.thenReturn(List.of(patientMock, patientMock2));

		// ACT AND ASSERT
		mockMvc.perform(post("/search") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("patients"));

	}

	@Test
	void testShowPatientView_Successful() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientById("AB10000")).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/search/{id}", "AB10000")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("patient"));

	}

	@Test
	void testShowAddPatientForm_Successful() throws Exception {

		// ACT AND ASSERT
		mockMvc.perform(get("/patient/add")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("patientDto"))
				.andExpect(view().name("patient/add"));
	}

	@Test
	void testSubmitAddPatientForm_Successful() throws Exception {

		// ARRANGE
		when(patientDaoMock.mapToEntity(patientDtoMock)).thenReturn(patientMock);
		when(patientDaoMock.savePatient(patientMock)).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(post("/patient/add") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo") //
				.param("dateOfBirth", "1998-02-16") //
				.param("address", "123 Imaginary Street") //
				.param("gender", "M") //
				.param("phone", "123-400-5000")) //
				.andExpect(status().is3xxRedirection());

	}

	@Test
	void testShowUpdatePatientForm_Successful() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(patientDaoMock.mapToDto(patientMock)).thenReturn(patientDtoMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/patient/update/{id}", "AB10000")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("patientDto"));
	}

}
