package com.abernathy.patients.dao;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.abernathy.patients.repository.PatientRepository;

@Component
public class PatientDao {

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	ModelMapper modelMapper;

	/**
	 * Find all patients registered in the clinic
	 *
	 * @return									List : Returns a List of patient
	 */
	public List<Patient> getPatients() {
		return patientRepository.findAll();
	}

	/**
	 * Return patient(s) found by first name and last name.
	 * The method can return duplicates, hence the use of List<Patient> and not a Single object
	 *
	 * @param firstName							String : the first name of the patient
	 * @param lastName							String : the last name of the patient
	 * @return									List<Patient> : List of patients containing patient information
	 * @throws PatientNotFoundException			Thrown if nobody was found
	 */
	public List<Patient> getPatientsByFirstNameAndLastName(String firstName, String lastName)
			throws PatientNotFoundException {

		List<Patient> patients = patientRepository.findByFirstNameAndLastName(firstName, lastName);

		// Guard clause that check if the list is empty before returning anything
		if (patients.isEmpty()) {
			throw new PatientNotFoundException("The patient with given first name and last name was not found");
		}

		return patients;
	}

	/**
	 * Creates a patient in database
	 *
	 * @param patient							Patient : the object to save in the database
	 * @return									Return the populated object
	 */
	public Patient savePatient(Patient patient) {
		patientRepository.save(patient);
		return patient;
	}

	/**
	 * Map Data Transfer Object to entity
	 *
	 * @param patientDto						PatientDto : The DTO to transform
	 * @return									An Entity object
	 */
	public Patient mapToEntity(PatientDto patientDto) {
		return modelMapper.map(patientDto, Patient.class);
	}

}
