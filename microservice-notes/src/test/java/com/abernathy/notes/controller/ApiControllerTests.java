package com.abernathy.notes.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.test.web.servlet.MvcResult;

import com.abernathy.notes.bean.PatientBean;
import com.abernathy.notes.dao.NoteDao;
import com.abernathy.notes.model.Note;
import com.abernathy.notes.model.dto.NoteDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ApiController.class)
class ApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	NoteDao noteDaoMock;

	static PatientBean patientMock;
	static NoteDto noteDtoMock;
	static Note noteMock;
	static List<Note> notesListMock = new ArrayList<Note>();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		// Creating a mock patient for our operations
		patientMock = new PatientBean();
		patientMock.setFirstName("Alpha");
		patientMock.setLastName("Bravo");
		patientMock.setDateOfBirth("1998-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");
		patientMock.setId("AB10000");

		String[] contents = { "He feels sleepy", "He feels sick" };
		for (int i = 0; i < 2; i++) {
			noteMock = new Note();
			noteMock.setId("Note_" + i);
			noteMock.setPatientId("AB10000");
			noteMock.setContent(contents[i]);
			notesListMock.add(noteMock);
		}

		noteDtoMock = new NoteDto();
		noteDtoMock.setPatientId("AB10000");
		noteDtoMock.setContent("He feels sick");

	}

	@Test
	void testGetPatientHistory_ShouldReturn_ListOfNotes() throws Exception {

		// ARRANGE
		when(noteDaoMock.getNotesByPatientId("AB10000")).thenReturn(notesListMock);

		// ACT AND ASSERT
		mockMvc.perform(get("/api/notes/{patientId}", "AB10000")) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$[0].id").value("Note_0")) //
				.andExpect(jsonPath("$[1].id").value("Note_1"));
	}

	@Test
	void testAddNoteToPatientHistory_ShouldBe_Successful() throws Exception {

		// ARRANGE
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(noteDtoMock);

		when(noteDaoMock.mapToEntity(any(NoteDto.class))).thenReturn(noteMock);
		when(noteDaoMock.createNote(any(Note.class))).thenReturn(noteMock);

		// ACT
		mockMvc.perform(post("/api/notes").content(json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.patientId").value("AB10000"));

	}

	@Test
	void testUpdateNoteById_ShouldBe_StatusOk() throws Exception {

		// ARRANGE
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(noteDtoMock);
		when(noteDaoMock.mapToEntity(any(NoteDto.class))).thenReturn(noteMock);
		when(noteDaoMock.updateNote(anyString(), any(Note.class))).thenReturn(noteMock);

		// ACT AND ASSERT
		mockMvc.perform(put("/api/notes/{noteId}", "Note_1").content(json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	void testDeleteNoteById_ShouldBe_StatusOk() throws Exception {

		// ARRANGE
		when(noteDaoMock.deleteNote(anyString())).thenReturn(true);

		// ACT AND ASSERT
		MvcResult result = mockMvc.perform(delete("/api/notes/{noteId}", "Note_1")).andExpect(status().isOk())
				.andReturn();
		String content = result.getResponse().getContentAsString();
		assertThat(content).isEqualTo("true");
	}

	@Test
	void testDeleteAllNotesForPatient_ShouldBe_StatusOk() throws Exception {

		// ARRANGE
		when(noteDaoMock.deleteAllNotesForPatient(anyString())).thenReturn(true);

		// ACT AND ASSERT
		MvcResult result = mockMvc.perform(delete("/api/notes/all/{patientId}", "AB10000")).andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();
		assertThat(content).isEqualTo("true");
	}

}
