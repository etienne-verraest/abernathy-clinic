package com.abernathy.reports.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abernathy.reports.bean.NoteBean;
import com.abernathy.reports.bean.PatientBean;
import com.abernathy.reports.exception.MicroserviceNotStartedException;
import com.abernathy.reports.model.Report;
import com.abernathy.reports.proxy.MicroserviceNotesProxy;
import com.abernathy.reports.proxy.MicroservicePatientsProxy;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
class ReportsGeneratorTests {

	@InjectMocks
	ReportsGenerator reportsGenerator;

	@Mock
	MicroservicePatientsProxy patientsProxy;

	@Mock
	MicroserviceNotesProxy notesProxy;

	private static PatientBean patientOver30;
	private static PatientBean manUnder30;
	private static PatientBean womanUnder30;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		// Creating mock patients for our test reports
		patientOver30 = new PatientBean();
		patientOver30.setFirstName("Zulu");
		patientOver30.setLastName("Foxtrot");
		patientOver30.setDateOfBirth("1968-02-16");
		patientOver30.setAddress("526 Imaginary Street");
		patientOver30.setGender("M");
		patientOver30.setPhone("123-400-5000");
		patientOver30.setId("ZF11000");

		manUnder30 = new PatientBean();
		manUnder30.setFirstName("Hotel");
		manUnder30.setLastName("Charlie");
		manUnder30.setDateOfBirth("2000-02-16");
		manUnder30.setAddress("9456 Broadway Avenue");
		manUnder30.setGender("M");
		manUnder30.setPhone("123-400-5000");
		manUnder30.setId("HC11000");

		womanUnder30 = new PatientBean();
		womanUnder30.setFirstName("Emilie");
		womanUnder30.setLastName("Bravo");
		womanUnder30.setDateOfBirth("2000-02-16");
		womanUnder30.setAddress("9456 Broadway Avenue");
		womanUnder30.setGender("F");
		womanUnder30.setPhone("123-400-5000");
		womanUnder30.setId("EB11000");

	}

	@Test
	void testGenerateReports_PatientMicroserviceNotStartedException() {

		// ARRANGE
		when(patientsProxy.getPatientById(anyString())).thenThrow(FeignException.class);

		// ACT
		Executable executable = () -> reportsGenerator.generateReports("RandomPatientId");

		// ASSERT
		assertThrows(MicroserviceNotStartedException.class, executable);
	}

	@Test
	void testGenerateReports_NoteMicroserviceNotStartedException() {

		// ARRANGE
		when(patientsProxy.getPatientById(anyString())).thenReturn(patientOver30);
		when(notesProxy.getPatientHistory(anyString())).thenThrow(FeignException.class);

		// ACT
		Executable executable = () -> reportsGenerator.generateReports("RandomPatientId");

		// ASSERT
		assertThrows(MicroserviceNotStartedException.class, executable);
	}

	@Test
	void testGenerateReports_PatientOver30_NoTriggers_ShouldReturnNone() throws Exception {

		// ARRANGE
		when(patientsProxy.getPatientById("ZF11000")).thenReturn(patientOver30);
		when(notesProxy.getPatientHistory("ZF11000")).thenReturn(Collections.emptyList());

		// ACT
		Report response = reportsGenerator.generateReports("ZF11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("None");
		assertThat(response.getTriggers()).isEmpty();
	}

	@Test
	void testGenerateReports_PatientOver30_HasNotesButNoTriggers_ShouldReturnNone() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "ZF11000", "Random note");
		notesMock.add(note1);

		when(patientsProxy.getPatientById("ZF11000")).thenReturn(patientOver30);
		when(notesProxy.getPatientHistory("ZF11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("ZF11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("None");
		assertThat(response.getTriggers()).hasSize(0);
	}

	@Test
	void testGenerateReports_PatientOver30_2Triggers_ShouldReturnBorderline() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "ZF11000", "Hemoglobin A1C check required");
		NoteBean note2 = new NoteBean("Note_2", new Date(), "ZF11000", "Patient is a Smoker");
		notesMock.addAll(List.of(note1, note2));

		when(patientsProxy.getPatientById("ZF11000")).thenReturn(patientOver30);
		when(notesProxy.getPatientHistory("ZF11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("ZF11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("Borderline");
		assertThat(response.getTriggers()).hasSize(2);
	}

	@Test
	void testGenerateReports_ManUnder30_3Triggers_ShouldReturnInDanger() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "HC11000", "Hemoglobin A1C check required");
		NoteBean note2 = new NoteBean("Note_2", new Date(), "HC11000", "Patient is a Smoker");
		NoteBean note3 = new NoteBean("Note_3", new Date(), "HC11000",
				"Patient gained Weight by eating too much pizzas");
		notesMock.addAll(List.of(note1, note2, note3));

		when(patientsProxy.getPatientById("HC11000")).thenReturn(manUnder30);
		when(notesProxy.getPatientHistory("HC11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("HC11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("In Danger");
		assertThat(response.getTriggers()).hasSize(3);
	}

	@Test
	void testGenerateReports_WomanUnder30_4Triggers_ShouldReturnInDanger() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "EB11000", "Hemoglobin A1C check required");
		NoteBean note2 = new NoteBean("Note_2", new Date(), "EB11000", "Patient is a Smoker");
		NoteBean note3 = new NoteBean("Note_3", new Date(), "EB11000",
				"Patient gained Weight by eating too much pizzas");
		NoteBean note4 = new NoteBean("Note_4", new Date(), "EB11000", "Patient Relapse on Alcohool");
		notesMock.addAll(List.of(note1, note2, note3, note4));

		when(patientsProxy.getPatientById("EB11000")).thenReturn(womanUnder30);
		when(notesProxy.getPatientHistory("EB11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("EB11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("In Danger");
		assertThat(response.getTriggers()).hasSize(4);
	}

	@Test
	void testGenerateReports_PatientOver30_6Triggers_ShouldReturnInDanger() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "ZF11000", "Hemoglobin A1C check required");
		NoteBean note2 = new NoteBean("Note_2", new Date(), "ZF11000", "Patient is a Smoker");
		NoteBean note3 = new NoteBean("Note_3", new Date(), "ZF11000",
				"Patient gained Weight by eating too much pizzas");
		NoteBean note4 = new NoteBean("Note_4", new Date(), "ZF11000", "Patient Relapse on Alcohool");
		NoteBean note5 = new NoteBean("Note_5", new Date(), "ZF11000", "Patient has vertigo when he wakes up");
		NoteBean note6 = new NoteBean("Note_6", new Date(), "ZF11000", "Patient feeling abnormal");

		notesMock.addAll(List.of(note1, note2, note3, note4, note5, note6));

		when(patientsProxy.getPatientById("ZF11000")).thenReturn(patientOver30);
		when(notesProxy.getPatientHistory("ZF11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("ZF11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("In Danger");
		assertThat(response.getTriggers()).hasSize(6);
	}

	@Test
	void testGenerateReports_ManUnder30_6Triggers_ShouldReturnEarlyOnset() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "HC11000", "Hemoglobin A1C check required");
		NoteBean note2 = new NoteBean("Note_2", new Date(), "HC11000", "Patient is a Smoker");
		NoteBean note3 = new NoteBean("Note_3", new Date(), "HC11000",
				"Patient gained Weight by eating too much pizzas");
		NoteBean note4 = new NoteBean("Note_4", new Date(), "HC11000", "Patient Relapse on Alcohool");
		NoteBean note5 = new NoteBean("Note_5", new Date(), "HC11000", "Patient has vertigo when he wakes up");
		NoteBean note6 = new NoteBean("Note_6", new Date(), "HC11000", "Patient feeling abnormal");

		notesMock.addAll(List.of(note1, note2, note3, note4, note5, note6));

		when(patientsProxy.getPatientById("HC11000")).thenReturn(manUnder30);
		when(notesProxy.getPatientHistory("HC11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("HC11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("Early Onset");
		assertThat(response.getTriggers()).hasSize(6);
	}

	@Test
	void testGenerateReports_WomanUnder30_7Triggers_ShouldReturnEarlyOnset() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "EB11000", "Hemoglobin A1C check required");
		NoteBean note2 = new NoteBean("Note_2", new Date(), "EB11000", "Patient is a Smoker");
		NoteBean note3 = new NoteBean("Note_3", new Date(), "EB11000",
				"Patient gained Weight by eating too much pizzas");
		NoteBean note4 = new NoteBean("Note_4", new Date(), "EB11000", "Patient Relapse on Alcohool");
		NoteBean note5 = new NoteBean("Note_5", new Date(), "EB11000", "Patient has vertigo when he wakes up");
		NoteBean note6 = new NoteBean("Note_6", new Date(), "EB11000", "Patient feeling abnormal");
		NoteBean note7 = new NoteBean("Note_7", new Date(), "EB11000", "Cholesterol check required");

		notesMock.addAll(List.of(note1, note2, note3, note4, note5, note6, note7));

		when(patientsProxy.getPatientById("EB11000")).thenReturn(womanUnder30);
		when(notesProxy.getPatientHistory("EB11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("EB11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("Early Onset");
		assertThat(response.getTriggers()).hasSize(7);
	}

	@Test
	void testGenerateReports_PatientOver30_8Triggers_ShouldReturnEarlyOnset() throws Exception {

		// ARRANGE
		ArrayList<NoteBean> notesMock = new ArrayList<>();
		NoteBean note1 = new NoteBean("Note_1", new Date(), "ZF11000", "Hemoglobin A1C check required");
		NoteBean note2 = new NoteBean("Note_2", new Date(), "ZF11000", "Patient is a Smoker");
		NoteBean note3 = new NoteBean("Note_3", new Date(), "ZF11000",
				"Patient gained Weight by eating too much pizzas");
		NoteBean note4 = new NoteBean("Note_4", new Date(), "ZF11000", "Patient Relapse on Alcohool");
		NoteBean note5 = new NoteBean("Note_5", new Date(), "ZF11000", "Patient has vertigo when he wakes up");
		NoteBean note6 = new NoteBean("Note_6", new Date(), "ZF11000", "Patient feeling abnormal");
		NoteBean note7 = new NoteBean("Note_7", new Date(), "ZF11000", "Cholesterol check required");
		NoteBean note8 = new NoteBean("Note_8", new Date(), "ZF11000", "Weird reaction when taking magnesium");

		notesMock.addAll(List.of(note1, note2, note3, note4, note5, note6, note7, note8));

		when(patientsProxy.getPatientById("ZF11000")).thenReturn(patientOver30);
		when(notesProxy.getPatientHistory("ZF11000")).thenReturn(notesMock);

		// ACT
		Report response = reportsGenerator.generateReports("ZF11000");

		// ASSERT
		assertThat(response.getRisk()).isEqualTo("Early Onset");
		assertThat(response.getTriggers()).hasSize(8);
	}

}
