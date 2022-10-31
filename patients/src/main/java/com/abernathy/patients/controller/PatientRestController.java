package com.abernathy.patients.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.exceptions.IncorrectFieldValueException;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.exceptions.RequiredParamNotSetException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.abernathy.patients.util.IdGeneratorUtil;
import com.abernathy.patients.util.ValidationErrorBuilderUtil;

@RestController
@RequestMapping("api/")
public class PatientRestController {

	@Autowired
	PatientDao patientDao;

	/**
	 * This method returns every patient if optional params are not filled.
	 * Otherwise this method returns possible patients for a given first name and last name.
	 * Because the method can return duplicates, we use a List instead of a single Patient object.
	 *
	 * @param firstName									String (optional) : the first name of the patient
	 * @param lastName									String (optional) : the last name of the patient
	 * @return											ResponseEntity of possible patients found
	 * @throws PatientNotFoundException					Thrown if nobody was found
	 */
	@GetMapping("/patients")
	public ResponseEntity<List<Patient>> getPatients(@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName) throws PatientNotFoundException {

		// If one or both params are null, then we return every patients
		if (firstName == null || lastName == null) {
			List<Patient> patients = patientDao.getPatients();
			return new ResponseEntity<>(patients, HttpStatus.OK);
		}

		// Otherwise, we get a list of possible patients based on their first and last
		// name
		List<Patient> possiblePatients = patientDao.getPatientsByFirstNameAndLastName(firstName, lastName);
		return new ResponseEntity<>(possiblePatients, HttpStatus.OK);
	}

	/**
	 * This method creates a patient in database. Fields must be validated in order to be saved
	 *
	 * @param addPatientDto								PatientDto containing fields to add Patient
	 * @return											Patient if fields are valid
	 * @throws IncorrectFieldValueException
	 */
	@PostMapping("/patients")
	public ResponseEntity<Patient> registerPatient(@Valid @RequestBody PatientDto addPatientDto, Errors errors)
			throws IncorrectFieldValueException {

		// Check if validation contains errors, if it's the case an error message is
		// returned
		if (errors.hasErrors()) {
			ValidationErrorBuilderUtil.buildErrorMessage(errors);
		}

		// If there is no error, then we create the patient in database
		Patient patient = patientDao.mapToEntity(addPatientDto);

		// Based on the informations we are given we generate a unique identifier
		patient.setId(IdGeneratorUtil.generateIdentifier(patient));

		patient = patientDao.savePatient(patient);
		return new ResponseEntity<>(patient, HttpStatus.CREATED);
	}

	/**
	 * This method updates a patient in database, based on it's unique ID. Fields must be validated in order to be saved
	 *
	 * @param id										String (required) : the unique ID of the patient
	 * @param updatePatientDto							PatientDto containing fields to update a patient
	 * @return											Patient object if fields are correctly populated
	 * @throws RequiredParamNotSetException				Thrown if param "id" is no set
	 * @throws IncorrectFieldValueException				Thrown if a field in the request body is incorrect
	 * @throws PatientNotFoundException					Thrown if the patient was not found
	 */
	@PutMapping("/patients")
	public ResponseEntity<Patient> updatePatient(@RequestParam(required = false) String id,
			@Valid @RequestBody PatientDto updatePatientDto, Errors errors)
			throws RequiredParamNotSetException, IncorrectFieldValueException, PatientNotFoundException {

		// If RequestParam is not defined, then we throw a RequiredParamNotSetException
		if (id == null) {
			throw new RequiredParamNotSetException("Param 'id' should be set to update person.");
		}

		// If Dto has errors, then we return an error
		if (errors.hasErrors()) {
			ValidationErrorBuilderUtil.buildErrorMessage(errors);
		}

		// Fetching patient from database, if nobody was found an exception is thrown
		Patient patientEntity = patientDao.getPatientById(id);

		// Update patient with new information, the id generated is saved and not
		// re-generated on update
		updatePatientDto.setId(patientEntity.getId());
		Patient patient = patientDao.mapToEntity(updatePatientDto);
		patient = patientDao.savePatient(patient);

		return new ResponseEntity<>(patient, HttpStatus.OK);
	}

}