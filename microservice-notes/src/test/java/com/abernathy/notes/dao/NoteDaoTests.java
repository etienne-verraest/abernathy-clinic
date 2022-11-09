package com.abernathy.notes.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.abernathy.notes.bean.PatientBean;
import com.abernathy.notes.exception.PatientNotFoundException;
import com.abernathy.notes.model.Note;
import com.abernathy.notes.proxy.MicroservicePatientsProxy;
import com.abernathy.notes.repository.NoteRepository;

@ExtendWith(MockitoExtension.class)
class NoteDaoTests {

	@InjectMocks
	private NoteDao noteDaoMock;

	@Mock
	private NoteRepository noteRepositoryMock;

	@Mock
	MicroservicePatientsProxy patientsProxyMock;

	@Mock
	private ModelMapper modelMapperMock;

	static Note noteMock;
	static PatientBean patientMock;

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

		noteMock = new Note();
		noteMock.setPatientId("AB10000");
		noteMock.setPractitioner("Dr. Melrose");
		noteMock.setContent("He feels sick");
	}

	@Test
	void testGetNotesByPatientId_ShouldReturn_1Note() throws PatientNotFoundException {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(noteRepositoryMock.findByPatientId("AB10000")).thenReturn(List.of(noteMock));

		// ACT
		List<Note> response = noteDaoMock.getNotesByPatientId("AB10000");

		// ASSERT
		assertThat(response).hasSize(1);
		assertThat(response.get(0).getPatientId()).isEqualTo("AB10000");
	}

	@Test
	void testCreateNote_ShouldBe_Successful() throws PatientNotFoundException {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(noteRepositoryMock.insert(any(Note.class))).thenReturn(noteMock);

		// ACT
		Note response = noteDaoMock.createNote(noteMock);

		// ASSERT
		assertThat(response.getPractitioner()).isEqualTo("Dr. Melrose");
	}

}
