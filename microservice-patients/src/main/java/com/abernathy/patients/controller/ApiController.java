package com.abernathy.patients.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.exceptions.IncorrectFieldValueException;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.abernathy.patients.util.ValidationErrorBuilderUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/")
@Api("API that allows CRUD operations on patients")
public class ApiController {

	@Autowired
	PatientDao patientDao;

	/**
	 * Returns every patient if Optional parameters are NOT filled.
	 * Otherwise this method returns possible patients for a given first name and last name.
	 * Because the method can return duplicates, we use a List instead of a single Patient object.
	 *
	 * @param firstName									String (optional) : the first name of the patient
	 * @param lastName									String (optional) : the last name of the patient
	 * @return											ResponseEntity of possible patients found
	 * @throws PatientNotFoundException					Thrown if nobody was found
	 */
	@ApiOperation(value = "Find a patient either by first and last name or by ID")
	@GetMapping("/patients")
	public ResponseEntity<List<Patient>> getPatients(@RequestParam(required = false) String id,
			@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName)
			throws PatientNotFoundException {

		// If ID is set, then we return the concerned patient
		if (id != null) {
			List<Patient> patient = List.of(patientDao.getPatientById(id));
			return new ResponseEntity<>(patient, HttpStatus.OK);
		}

		// If first name and last name is set then we fetch patients by their names
		if (firstName != null && lastName != null) {
			List<Patient> possiblePatients = patientDao.getPatientsByFirstNameAndLastName(firstName, lastName);
			return new ResponseEntity<>(possiblePatients, HttpStatus.OK);
		}

		// Otherwise we return a list of every patients
		List<Patient> patients = patientDao.getPatients();
		return new ResponseEntity<>(patients, HttpStatus.OK);
	}

	/**
	 * Creates a patient in database.
	 * Fields must be validated in order to be saved
	 *
	 * @param addPatientDto								PatientDto containing fields to add Patient
	 * @return											Patient if fields are valid
	 * @throws IncorrectFieldValueException				Thrown if a field in the request body is incorrect
	 */
	@ApiOperation(value = "Create a patient")
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
		patient = patientDao.savePatient(patient);
		return new ResponseEntity<>(patient, HttpStatus.CREATED);
	}

	/**
	 * Updates a patient in database, based on its ID.
	 * Fields must be validated in order to be saved
	 *
	 * @param id										String (required) : the unique ID of the patient
	 * @param updatePatientDto							PatientDto containing fields to update a patient
	 * @return											Patient object if fields are correctly populated
	 * @throws IncorrectFieldValueException				Thrown if a field in the request body is incorrect
	 * @throws PatientNotFoundException					Thrown if the patient was not found
	 */
	@ApiOperation(value = "Update a patient for a givn ID")
	@PutMapping("/patients/{id}")
	public ResponseEntity<Patient> updatePatient(@PathVariable String id,
			@Valid @RequestBody PatientDto updatePatientDto, Errors errors)
			throws IncorrectFieldValueException, PatientNotFoundException {

		// If Dto has errors, then we return an error
		if (errors.hasErrors()) {
			ValidationErrorBuilderUtil.buildErrorMessage(errors);
		}

		// Fetching patient from database, if nobody was found an exception is thrown
		// Update patient with new information, the id generated is saved and not
		// re-generated on update
		Patient patient = patientDao.mapToEntity(updatePatientDto);
		patient = patientDao.updatePatient(patient, patientDao.getPatientById(id).getId());
		return new ResponseEntity<>(patient, HttpStatus.OK);
	}

	/**
	 * Deletes a patient from database, given its id.
	 *
	 * @param id										String : The id of the patient to deleted
	 * @return											A message if the operation was successfull
	 * @throws PatientNotFoundException					Thrown if nobody was found given
	 */
	@ApiOperation(value = "Delete a patient for a given ID")
	@DeleteMapping("/patients/{id}")
	public ResponseEntity<String> deletePatient(@PathVariable(required = true) String id)
			throws PatientNotFoundException {

		// Checking if patient with given id exists
		if (id != null && patientDao.getPatientById(id) != null) {
			if (patientDao.deletePatient(id)) {
				return new ResponseEntity<>("Patient with id : " + id + " was successfully deleted", HttpStatus.OK);
			}
		}

		throw new PatientNotFoundException("Patient with given ID was not found.");
	}

}
