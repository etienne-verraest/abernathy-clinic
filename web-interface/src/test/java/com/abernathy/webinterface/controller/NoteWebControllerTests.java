package com.abernathy.webinterface.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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

import com.abernathy.webinterface.bean.NoteBean;
import com.abernathy.webinterface.bean.PatientBean;
import com.abernathy.webinterface.dto.NoteDto;
import com.abernathy.webinterface.proxy.MicroserviceNotesProxy;
import com.abernathy.webinterface.proxy.MicroservicePatientsProxy;

@WebMvcTest(controllers = NoteWebController.class)
class NoteWebControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MicroservicePatientsProxy patientsProxyMock;

	@MockBean
	private ModelMapper modelMapper;

	@MockBean
	private MicroserviceNotesProxy notesProxyMock;

	private static PatientBean patientMock;
	private static NoteBean noteMock;
	private static NoteDto noteDtoMock;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		// Creating a mock patient
		patientMock = new PatientBean();
		patientMock.setFirstName("Alpha");
		patientMock.setLastName("Bravo");
		patientMock.setDateOfBirth("1998-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");
		patientMock.setId("AB10000");

		// Creating note for the patient
		noteMock = new NoteBean("Note_1", new Date(), "AB10000", "He feels sick");
		noteDtoMock = new NoteDto("AB10000", "He feels sick");
	}

	@Test
	void testShowNewNoteForm_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);

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

	@Test
	void testShowUpdateNoteForm_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(notesProxyMock.getNote("Note_1")).thenReturn(noteMock);
		when(modelMapper.map(noteMock, NoteDto.class)).thenReturn(noteDtoMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/{patientId}/notes/{noteId}/edit", "AB10000", "Note_1"))
				.andExpect(model().attributeExists("noteDto")).andExpect(status().isOk());
	}

	@Test
	void testShowUpdateNoteForm_PatientIsInvalid() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(null);

		// ACT AND ASSERT
		mockMvc.perform(get("/{patientId}/notes/{noteId}/edit", "AB10000", "Note_1"))
				.andExpect(flash().attributeExists("message")).andExpect(status().is3xxRedirection());
	}

	@Test
	void testShowUpdateNoteForm_NoteIsInvalid() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(notesProxyMock.getNote("Note_1")).thenReturn(null);

		// ACT AND ASSERT
		mockMvc.perform(get("/{patientId}/notes/{noteId}/edit", "AB10000", "Note_1"))
				.andExpect(flash().attributeExists("message")).andExpect(status().is3xxRedirection());
	}

	@Test
	void testSubmitUpdateForm_Successful() throws Exception {

		// ACT AND ASSERT
		mockMvc.perform(post("/{patientId}/notes/{noteId}/edit", "AB10000", "Note_1") //
				.param("patientId", "AB10000") //
				.param("content", "He feels dizzy")) //
				.andExpect(flash().attribute("message", "Note was successfully updated."))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void testSubmitUpdateForm_Invalid() throws Exception {

		// ACT AND ASSERT
		mockMvc.perform(post("/{patientId}/notes/{noteId}/edit", "AB10000", "Note_1") //
				.param("patientId", "AB10000") //
				.param("content", "")) //
				.andExpect(model().hasErrors());
	}

	@Test
	void testDeleteNote_Successful() throws Exception {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(notesProxyMock.getNote("Note_1")).thenReturn(noteMock);
		when(notesProxyMock.deleteNoteById("Note_1")).thenReturn(true);

		// ACT AND ASSERT
		mockMvc.perform(get("/{patientId}/notes/{noteId}/delete", "AB10000", "Note_1"))
				.andExpect(flash().attribute("message", "Note was successfully deleted."))
				.andExpect(status().is3xxRedirection());

	}
}
