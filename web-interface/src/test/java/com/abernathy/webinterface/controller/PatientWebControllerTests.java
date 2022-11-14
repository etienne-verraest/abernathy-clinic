package com.abernathy.webinterface.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.abernathy.webinterface.bean.NoteBean;
import com.abernathy.webinterface.bean.PatientBean;
import com.abernathy.webinterface.dto.PatientDto;
import com.abernathy.webinterface.proxy.MicroserviceNotesProxy;
import com.abernathy.webinterface.proxy.MicroservicePatientsProxy;

@WebMvcTest(controllers = PatientWebController.class)
class PatientWebControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MicroservicePatientsProxy patientsProxyMock;

	@MockBean
	private MicroserviceNotesProxy notesProxyMock;

	@MockBean
	private ModelMapper modelMapper;

	private static List<PatientBean> patientsListMock = new ArrayList<PatientBean>();
	private static PatientBean patientMock;
	private static PatientDto patientDtoMock;
	private static NoteBean noteMock;

	@BeforeAll
	static void initialization() throws Exception {

		// Creating a mock patient for our operations
		patientMock = new PatientBean();
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
		patientDtoMock.setId("AB10000");

		// Adding the mock patient to the mocked list
		patientsListMock.add(patientMock);

		// Creating note for the patient
		noteMock = new NoteBean("Note_1", new Date(), "AB10000", "He feels sick");
	}

	@Test
	void testShowSearchPatientForm_Successful() throws Exception {

		// ACT AND ASSERT
		mockMvc.perform(get("/")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("searchPatientDto"));
	}

	@Test
	void testSubmitSearchPatienForm_FoundOnePatient_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatients("Alpha", "Bravo")).thenReturn(List.of(patientMock));

		// ACT AND ASSERT
		mockMvc.perform(post("/search") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo")) //
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/AB10000/"));

	}

	@Test
	void testSubmitSearchPatientForm_FoundTwoPatients_Successful() throws Exception {

		// ARRANGE
		PatientBean patientMock2 = new PatientBean();
		patientMock2.setFirstName("Alpha");
		patientMock2.setLastName("Bravo");
		patientMock2.setDateOfBirth("1998-02-16");
		patientMock2.setAddress("456 Imaginary Street");
		patientMock2.setGender("M");
		patientMock2.setPhone("123-400-5000");
		patientMock2.setId("AB20000");
		when(patientsProxyMock.getPatients("Alpha", "Bravo")).thenReturn(List.of(patientMock, patientMock2));

		// ACT AND ASSERT
		mockMvc.perform(post("/search") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("patients"));

	}

	@Test
	void testShowPatientView_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(notesProxyMock.getPatientHistory("AB10000")).thenReturn(List.of(noteMock));

		// ACT AND ASSERT
		mockMvc.perform(get("/{id}/", "AB10000")) //
				.andExpect(status().isOk()).andExpect(model().attributeExists("patient"))
				.andExpect(model().attributeExists("notes"));

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
		when(patientsProxyMock.registerPatient(any(PatientDto.class))).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(post("/patient/add/") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo") //
				.param("dateOfBirth", "1998-02-16") //
				.param("address", "123 Imaginary Street") //
				.param("gender", "M") //
				.param("phone", "123-400-5000")) //
				.andExpect(status().is3xxRedirection());

	}

	@Test
	void testShowUpdatePatient_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(modelMapper.map(patientMock, PatientDto.class)).thenReturn(patientDtoMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/{id}/patient/update/", "AB10000")).andExpect(status().isOk());
	}

	@Test
	void testSubmitUpdatePatient_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.updatePatient(anyString(), any(PatientDto.class))).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(post("/patient/update") //
				.param("id", "AB10000") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo") //
				.param("dateOfBirth", "1998-02-16") //
				.param("gender", "M") //
				.param("address", "526 Race Track") //
				.param("phone", "123-400-5000") //
		).andExpect(status().is3xxRedirection())
				.andExpect(flash().attribute("message", "Patient with id 'AB10000' was successfully updated"));
	}

	@Test
	void testSubmitUpdatePatient_ContentIsInvalid() throws Exception {

		// ARRANGE
		when(patientsProxyMock.updatePatient("AB10000", patientDtoMock)).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(post("/patient/update") //
				.param("id", "AB10000") //
				.param("firstName", "Alpha") //
				.param("lastName", "Bravo") //
				.param("dateOfBirth", "1998-02-16") //
				.param("gender", "M") //
				.param("address", "526 Race Track") //
				.param("phone", "123444-40550-555000") //
		).andExpect(model().hasErrors());
	}

	@Test
	void testDeletePatient_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(patientsProxyMock.deletePatient("AB10000")).thenReturn(
				"Patient with id 'AB10000' was successfully deleted. Notes attached to him were also deleted.");

		// ACT AND ASSERT
		mockMvc.perform(get("/{id}/patient/delete/", "AB10000")) //
				.andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("message"));
	}

	@Test
	void testDeletePatient_Invalid() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById(anyString())).thenReturn(null);

		// ACT AND ASSERT
		mockMvc.perform(get("/{id}/patient/delete/", "AB10000")) //
				.andExpect(flash().attribute("message", "Patient with id 'AB10000' was not found"));
	}
}
