package com.abernathy.patients.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class PatientDaoTests {

	@InjectMocks
	private PatientDao patientDaoMock;

	@Mock
	private PatientRepository patientRepositoryMock;

	@Mock
	private ModelMapper modelMapperMock;

	private static List<Patient> patientsListMock = new ArrayList<Patient>();
	private static Patient patientMock;

	@BeforeAll
	static void initalization() {

		// Creating a mock patient for our operations
		patientMock = new Patient();
		patientMock.setFirstName("Alpha");
		patientMock.setLastName("Bravo");
		patientMock.setDateOfBirth("1998-02-16");
		patientMock.setAddress("123 Imaginary Street");
		patientMock.setGender("M");
		patientMock.setPhone("123-400-5000");
		patientMock.setId("AB10000");

		// Adding the mock patient to the mocked list
		patientsListMock.add(patientMock);
	}

	@Test
	void testGetPatients_ShouldReturn_ListOfPatients() {

		// ARRANGE
		when(patientRepositoryMock.findAll()).thenReturn(patientsListMock);

		// ACT
		List<Patient> response = patientDaoMock.getPatients();

		// ASSERT
		assertThat(response).hasSize(1);
		assertThat(response.get(0).getFirstName()).isEqualTo("Alpha");
		verify(patientRepositoryMock, times(1)).findAll();
	}

	@Test
	void testGetPatientsByFirstNameAndLastName_ShouldReturn_MockedPatient() throws PatientNotFoundException {

		// ARRANGE
		when(patientRepositoryMock.findByFirstNameAndLastName("Alpha", "Bravo")).thenReturn(List.of(patientMock));

		// ACT
		List<Patient> response = patientDaoMock.getPatientsByFirstNameAndLastName("Alpha", "Bravo");

		// ASSERT
		assertThat(response).hasSize(1);
		assertThat(response.get(0).getFirstName()).isEqualTo("Alpha");
		verify(patientRepositoryMock, times(1)).findByFirstNameAndLastName("Alpha", "Bravo");
	}

	@Test
	void testGetPatientsByFirstNameAndLastName_ShouldThrow_PatientNotFoundException() {

		// ARRANGE
		when(patientRepositoryMock.findByFirstNameAndLastName(anyString(), anyString()))
				.thenReturn(new ArrayList<Patient>());

		// ACT
		Executable executable = () -> patientDaoMock.getPatientsByFirstNameAndLastName("Zulu", "Foxtrot");

		// ASSERT
		assertThrows(PatientNotFoundException.class, executable);
		verify(patientRepositoryMock, times(1)).findByFirstNameAndLastName("Zulu", "Foxtrot");
	}

	@Test
	void testGetPatientById_ShouldReturn_MockedPatient() throws PatientNotFoundException {

		// ARRANGE
		when(patientRepositoryMock.findById("AB10000")).thenReturn(Optional.of(patientMock));

		// ACT
		Patient response = patientDaoMock.getPatientById("AB10000");

		// ASSERT
		assertThat(response.getFirstName()).isEqualTo("Alpha");
		verify(patientRepositoryMock, times(1)).findById("AB10000");
	}

	@Test
	void testSavePatient_ShouldReturn_MockedPatient() {

		// ARRANGE
		when(patientRepositoryMock.save(any(Patient.class))).thenReturn(patientMock);

		// ACT
		Patient response = patientDaoMock.savePatient(patientMock);

		// ASSERT
		assertThat(response.getFirstName()).isEqualTo("Alpha");
		verify(patientRepositoryMock, times(1)).save(patientMock);

	}

	@Test
	void testUpdatePatient_ShouldReturn_UpdatedPatient() {

		// ARRANGE
		Patient patientUpdateMock = new Patient();
		patientUpdateMock.setFirstName("Alpha");
		patientUpdateMock.setLastName("Bravo");
		patientUpdateMock.setDateOfBirth("1998-02-16");
		patientUpdateMock.setAddress("456 New Street");
		patientUpdateMock.setGender("M");
		patientUpdateMock.setPhone("000-000-0000");
		patientUpdateMock.setId("AB10000");
		when(patientRepositoryMock.save(any(Patient.class))).thenReturn(patientUpdateMock);

		// ACT
		Patient response = patientDaoMock.updatePatient(patientUpdateMock, "AB10000");

		// ASSERT
		assertThat(response.getFirstName()).isEqualTo("Alpha");
		assertThat(response.getAddress()).isEqualTo("456 New Street");
		verify(patientRepositoryMock, times(1)).save(patientUpdateMock);

	}

}
