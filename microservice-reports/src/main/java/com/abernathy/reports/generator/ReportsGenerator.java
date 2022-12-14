package com.abernathy.reports.generator;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abernathy.reports.bean.NoteBean;
import com.abernathy.reports.bean.PatientBean;
import com.abernathy.reports.exception.MicroserviceNotStartedException;
import com.abernathy.reports.exception.PatientNotFoundException;
import com.abernathy.reports.model.Report;
import com.abernathy.reports.proxy.MicroserviceNotesProxy;
import com.abernathy.reports.proxy.MicroservicePatientsProxy;
import com.abernathy.reports.util.TriggersList;

import feign.FeignException;

@Component
public class ReportsGenerator {

	@Autowired
	MicroservicePatientsProxy patientsProxy;

	@Autowired
	MicroserviceNotesProxy notesProxy;

	/**
	 * Generate a full report based on different criterias
	 * We are not checking for any nullity because Feign will throw an exception if the patient is not found
	 *
	 * @param patientId									String : The patient's ID
	 * @throws PatientNotFoundException					Thrown if the patient was not found
	 * @throws MicroserviceNotStartedException
	 */
	public Report generateReports(String patientId) throws PatientNotFoundException, MicroserviceNotStartedException {

		if (patientId != null) {

			// Getting the patient and its informations
			PatientBean patient;
			try {
				patient = patientsProxy.getPatientById(patientId);
			} catch (FeignException e) {
				throw new MicroserviceNotStartedException(
						"Patient microservice is not started. Cannot generate report.");
			}

			// Checking if patient is null
			if (patient == null) {
				throw new PatientNotFoundException("Patient with given ID was not found");
			}

			// Calculating the age for the patient
			int age = calculateAge(patient.getDateOfBirth());

			// Getting notes related to the patients
			List<NoteBean> notes;
			try {
				notes = notesProxy.getPatientHistory(patientId);
			} catch (FeignException e) {
				throw new MicroserviceNotStartedException("Note microservice is not started. Cannot generate report.");
			}

			if (notes != null && !notes.isEmpty()) {
				// Find triggers from notes
				List<String> triggersList = findTriggersFromHistory(notes);

				// Calculate the risk
				String risk = calculateRisk(triggersList.size(), age, patient.getGender());

				// Returning a "Report" object
				return new Report(patientId, patient.getFirstName(), patient.getLastName(), patient.getGender(),
						patient.getDateOfBirth(), age, risk, triggersList);
			} else {
				return new Report(patientId, patient.getFirstName(), patient.getLastName(), patient.getGender(),
						patient.getDateOfBirth(), age, "None", Collections.emptyList());
			}
		}

		throw new PatientNotFoundException("Patient with given ID was not found");
	}

	/**
	 * Calculate Diabetes Risk based on number of Triggers, patient age and gender
	 *
	 * @param numberOfTriggers								Integer : The number of triggers
	 * @param patientAge									Integer : The patient's age
	 * @param patientGender									String : The patient's gender
	 * @return												Returns a String that states the diabete risk level
	 */
	private static String calculateRisk(int numberOfTriggers, int patientAge, String patientGender) {

		// Getting gender as a boolean
		boolean isAMan = "M".equals(patientGender);
		boolean isAWoman = !isAMan;

		// Patient history does not contains notes with triggers
		if (numberOfTriggers == 0) {
			return "None";
		}

		// If 2 triggers are in patient history and patient is more than 30 years old
		if (numberOfTriggers == 2 && patientAge > 30) {
			return "Borderline";
		}

		// If patient is a man and is under 30 years old, then 3 triggers must be
		// present
		// If patient is a woman and is under 30 years old, then 4 triggers must be
		// present
		// If patient is over 30 years old, then 6 triggers must be present
		if ((isAMan && patientAge < 30 && (numberOfTriggers >= 3 && numberOfTriggers < 5))
				|| (isAWoman && patientAge < 30 && (numberOfTriggers >= 4 && numberOfTriggers < 7))
				|| (patientAge > 30 && (numberOfTriggers >= 6 && numberOfTriggers < 8))) {
			return "In Danger";
		}

		// If patient is a man and is under 30 years old, then at least 5 triggers must
		// be present.
		// If patient is a woman and is under 30 years old, then at least 7 triggers
		// must be present.
		// If patient is over 30 years old, then at least 8 triggers must be present
		if ((isAMan && patientAge < 30 && numberOfTriggers >= 5)
				|| (isAWoman && patientAge < 30 && numberOfTriggers >= 7)
				|| (patientAge > 30 && numberOfTriggers >= 8)) {
			return "Early Onset";
		}

		return "None";
	}

	/**
	 * Finds distinct triggers words from patient's notes
	 *
	 * @param notes									List<NoteBean> containing patient's notes
	 * @return										List of distinct triggers keywords
	 */
	private static List<String> findTriggersFromHistory(List<NoteBean> notes) {

		// Load the triggers list
		List<String> triggers = TriggersList.generateTriggersList();

		// Searching for triggers on each note, without distinction
		List<String> triggersFound = new ArrayList<>();
		notes.forEach(note -> {
			List<String> results = triggers.stream()
					.filter(trigger -> note.getContent().toLowerCase().matches(".*\\b" + trigger + "\\b.*"))
					.collect(Collectors.toList());
			triggersFound.addAll(results);
		});

		// Counting distinct keywords to output final result
		return triggersFound.stream().distinct().collect(Collectors.toList());

	}

	/**
	 * Calculate age from birthdate
	 *
	 * @param birthdate								String : Birthdate in format yyyy-MM-dd
	 * @return										Integer : Age extracted from the birthdate
	 */
	private static Integer calculateAge(String birthdate) {

		// Parse birthdate to the correct formatting
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate from = LocalDate.parse(birthdate, formatter);

		// Calculates the time elapsed between 2 dates
		Period age = Period.between(from, LocalDate.now());
		return age.getYears();
	}
}
