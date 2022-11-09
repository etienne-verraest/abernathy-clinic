package com.abernathy.notes.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.abernathy.notes.bean.PatientBean;
import com.abernathy.notes.exception.NoteNotFoundException;
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
		noteMock.setId("Note_1");
		noteMock.setPatientId("AB10000");
		noteMock.setPractitioner("Dr. Melrose");
		noteMock.setContent("He feels sick");
	}

	@Test
	void testGetNotesByPatientId_ShouldReturn_ListOfNotes() throws PatientNotFoundException {

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
	void testGetNotesByPatientId_ShouldThrowPatientNotFoundException() {

		// ARRANGE
		when(patientsProxyMock.getPatientById(anyString())).thenReturn(null);

		// ACT
		Executable executable = () -> noteDaoMock.getNotesByPatientId("XY30300");

		// ASSERT
		assertThrows(PatientNotFoundException.class, executable);
		verify(noteRepositoryMock, never()).findByPatientId("XY30300");
	}

	@Test
	void testCreateNote_ShouldBe_SuccessfulOperation() throws PatientNotFoundException {

		// ARRANGE
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(noteRepositoryMock.insert(any(Note.class))).thenReturn(noteMock);

		// ACT
		Note response = noteDaoMock.createNote(noteMock);

		// ASSERT
		assertThat(response.getPractitioner()).isEqualTo("Dr. Melrose");
	}

	@Test
	void testUpdateNote_ShouldBe_SuccessfulOperation() throws NoteNotFoundException, PatientNotFoundException {

		// ARRANGE
		when(noteRepositoryMock.findById(anyString())).thenReturn(Optional.of(noteMock));
		when(patientsProxyMock.getPatientById("AB10000")).thenReturn(patientMock);
		when(noteRepositoryMock.save(any(Note.class))).thenReturn(noteMock);

		// ACT
		Note response = noteDaoMock.updateNote("Note_1", noteMock);

		// ASSERT
		assertThat(response.getPractitioner()).isEqualTo("Dr. Melrose");
		verify(noteRepositoryMock, times(1)).save(any(Note.class));
	}

	@Test
	void testUpdateNote_ShouldThrow_NoteNotFoundException() {

		// ARRANGE
		when(noteRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

		// ACT
		Executable executable = () -> noteDaoMock.updateNote("Note_30303030", noteMock);

		// ASSERT
		assertThrows(NoteNotFoundException.class, executable);

	}

	@Test
	void testUpdateNote_ShouldThrow_PatientNotFoundException() {

		// ARRANGE
		when(noteRepositoryMock.findById(anyString())).thenReturn(Optional.of(noteMock));
		when(patientsProxyMock.getPatientById(anyString())).thenReturn(null);

		// ACT
		Executable executable = () -> noteDaoMock.updateNote("Note_30303030", noteMock);

		// ASSERT
		assertThrows(PatientNotFoundException.class, executable);

	}

	@Test
	void testDeleteNote_ShouldReturn_BooleanTrue() throws NoteNotFoundException {

		// ARRANGE
		when(noteRepositoryMock.findById(anyString())).thenReturn(Optional.of(noteMock));

		// ACT
		boolean response = noteDaoMock.deleteNote("Note_1");

		// ASSERT
		assertThat(response).isTrue();
	}

	@Test
	void testDeleteNote_ShouldThrow_NoteNotFoundException() {

		// ARRANGE
		when(noteRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

		// ACT
		Executable executable = () -> noteDaoMock.deleteNote("Note_IENGIZESIENFSINSDIN");

		// ASSERT
		assertThrows(NoteNotFoundException.class, executable);
	}

}
