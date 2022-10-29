package com.abernathy.patients.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.abernathy.patients.util.ErrorBuilder;

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
	 * @param firstName								String (optional) : the first name of the patient
	 * @param lastName								String (optional) : the last name of the patient
	 * @return										ResponseEntity of possible patients found
	 * @throws PatientNotFoundException				Thrown if nobody was found
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
	 * @param addPatientDto							AddPatientDto containing fields to add Patient
	 * @return										Patient if fields are valid
	 * @throws IncorrectFieldValueException
	 */
	@PostMapping("/patients")
	public ResponseEntity<String> registerPatient(@Valid @RequestBody PatientDto addPatientDto, Errors errors) {

		// Check if validation contains errors, if it's the case an error message is
		// returned
		if (errors.hasErrors()) {
			return ErrorBuilder.buildErrorMessage(errors);
		}

		// If there is no error, then we create the patient in database
		Patient patient = patientDao.mapToEntity(addPatientDto);
		patient = patientDao.savePatient(patient);
		return new ResponseEntity<>(patient.toString(), HttpStatus.CREATED);
	}

}
