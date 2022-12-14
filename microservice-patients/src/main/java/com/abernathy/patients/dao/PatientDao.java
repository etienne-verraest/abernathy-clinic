package com.abernathy.patients.dao;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.abernathy.patients.repository.PatientRepository;
import com.abernathy.patients.util.IdGeneratorUtil;

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

		if (patients.isEmpty()) {
			throw new PatientNotFoundException("The patient with given first name and last name was not found");
		}

		return patients;
	}

	/**
	 * Return a patient found by its id
	 * The method can't return duplicate because ID is a unique generated key
	 *
	 * @param id								String : the id of the patient to fetch
	 * @return									Patient : A patient object containing patient informatoion
	 */
	public Patient getPatientById(String id) {
		Optional<Patient> patient = patientRepository.findById(id);
		if (patient.isPresent()) {
			return patient.get();
		}
		return null;
	}

	/**
	 * Creates a patient in database
	 * Based on the informations we are given we generate a unique identifier (i.e : AB35678)
	 *
	 * @param patient							Patient : the object to save in the database
	 * @return									Returns the created patient
	 */
	public Patient savePatient(Patient patient) {
		patient.setId(IdGeneratorUtil.generateIdentifier(patient));
		patient.setFirstName(patient.getFirstName().toUpperCase());
		patient.setLastName(patient.getLastName().toUpperCase());
		return patientRepository.save(patient);
	}

	/**
	 * Updates a patient in database, given its id.
	 * We do not regenerate the unique identifier, even if first and last name change
	 *
	 * @param patient							Patient : the object to update in the database
	 * @param id								String : the id of the patient
	 * @return									Returns the updated patient
	 */
	public Patient updatePatient(Patient patient, String id) {
		patient.setId(id);
		patient.setFirstName(patient.getFirstName().toUpperCase());
		patient.setLastName(patient.getLastName().toUpperCase());
		return patientRepository.save(patient);
	}

	/**
	 * Deletes a patient in database, given its id.
	 *
	 * @param id								String : The id of the patient to delete
	 * @return									Returns true if the deletion was successfull
	 * @throws PatientNotFoundException			Thrown if the patient with given ID is not found.
	 */
	public boolean deletePatient(String id) throws PatientNotFoundException {

		if (patientRepository.findById(id).isPresent()) {
			patientRepository.deleteById(id);
			return true;
		}

		throw new PatientNotFoundException("The patient with given ID was not found.");
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

	/**
	 * Map Entity to DTO
	 *
	 * @param patient							Patient : The Entity to transform
	 * @return									A Patient DTO object
	 */
	public PatientDto mapToDto(Patient patient) {
		return modelMapper.map(patient, PatientDto.class);
	}

}
