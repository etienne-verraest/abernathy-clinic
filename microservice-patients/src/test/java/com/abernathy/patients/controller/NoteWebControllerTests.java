package com.abernathy.patients.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.abernathy.patients.bean.NoteBean;
import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.NoteDto;
import com.abernathy.patients.proxy.MicroserviceNotesProxy;

@WebMvcTest(controllers = NoteWebController.class)
class NoteWebControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PatientDao patientDaoMock;

	@MockBean
	private ModelMapper modelMapper;

	@MockBean
	private MicroserviceNotesProxy notesProxyMock;

	private static Patient patientMock;
	private static NoteBean noteMock;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		// Creating a mock patient
		patientMock = new Patient();
		patientMock.setFirstName("Alpha");
		patientMock.setLastName("Bravo");
		patientMock.setDateOfBirth("1998-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");
		patientMock.setId("AB10000");

		// Creating note for the patient
		noteMock = new NoteBean("Note_1", new Date(), "AB10000", "He feels sick");
	}

	@Test
	void testShowNewNoteForm_Successful() throws Exception {

		// ARRANGE
		when(patientDaoMock.getPatientById("AB10000")).thenReturn(patientMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/{patientId}/notes/add", "AB10000")).andExpect(status().isOk())
				.andExpect(model().attributeExists("noteDto"));
	}

	@Test
	void testSubmitNewNoteForm_Successful() throws Exception {

		// ARRANGE
		when(notesProxyMock.addNoteToPatientHistory(any(NoteDto.class))).thenReturn(noteMock);

		// ACT AND ASSERT
		mockMvc.perform(post("/{patientId}/notes/add", "AB10000").param("content", "He feels sick"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void testSubmitNewNoteForm_Invalid() throws Exception {

		// ARRANGE
		when(notesProxyMock.addNoteToPatientHistory(any(NoteDto.class))).thenReturn(null);

		// ACT AND ASSERT
		// Content is blank, so validation will fail
		mockMvc.perform(post("/{patientId}/notes/add", "AB10000").param("content", "")).andExpect(model().hasErrors());
	}

}
