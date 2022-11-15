package com.abernathy.patients.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.abernathy.patients.exceptions.MicroserviceNotStartedException;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.abernathy.patients.proxy.MicroserviceNotesProxy;
import com.abernathy.patients.util.ValidationErrorBuilderUtil;

import feign.FeignException;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/")
public class ApiController {

	@Autowired
	PatientDao patientDao;

	@Autowired
	MicroserviceNotesProxy notesProxy;

	/**
	 * Returns every patient if Optional parameters are NOT filled.
	 * Otherwise this method returns possible patients for a given first name and last name.
	 * Because the method can return duplicates, we use a List instead of a single Patient object.
	 *
	 * @param firstName									String (optional) : the first name of the patient
	 * @param lastName									String (optional) : the last name of the patient
	 * @return											List<Patient> containing possible patients
	 * @throws PatientNotFoundException					Thrown if nobody was found
	 */
	@ApiOperation(value = "Get a list of patients (optional : given their first and last name)")
	@GetMapping("/patients")
	public List<Patient> getPatients(@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName) throws PatientNotFoundException {

		// If first name and last name is set then we fetch patients by their names
		if (firstName != null && lastName != null) {
			List<Patient> possiblePatients = patientDao.getPatientsByFirstNameAndLastName(firstName, lastName);
			return possiblePatients;
		}

		// Otherwise we return a list of every patients
		List<Patient> patients = patientDao.getPatients();
		return patients;
	}

	/**
	 * Return a single patient based on its ID
	 *
	 * @param id										String (required) : the ID of the patient
	 * @return											ResponseEntity containg patient information if found
	 * @throws PatientNotFoundException					Thrown if no patients were found
	 */
	@ApiOperation(value = "Get a patient given its ID")
	@GetMapping("/patients/{id}")
	public Patient getPatientById(@PathVariable String id) throws PatientNotFoundException {

		// Checking if ID is set
		if (id != null) {
			return patientDao.getPatientById(id);
		}
		return null;
	}

	/**
	 * Creates a patient in database.
	 * Fields must be validated in order to be saved
	 *
	 * @param addPatientDto								PatientDto containing fields to add Patient
	 * @return											Patient if fields are valid
	 * @throws IncorrectFieldValueException				Thrown if a field in the request body is incorrect
	 */
	@ApiOperation(value = "Create a new Patient")
	@PostMapping("/patients")
	public Patient registerPatient(@Valid @RequestBody PatientDto addPatientDto, Errors errors)
			throws IncorrectFieldValueException {

		// Check if validation contains errors, if it's the case an error message is
		// returned
		if (errors.hasErrors()) {
			ValidationErrorBuilderUtil.buildErrorMessage(errors);
		}

		// If there is no error, then we create the patient in database
		Patient patient = patientDao.mapToEntity(addPatientDto);
		patient = patientDao.savePatient(patient);
		return patient;
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
	@ApiOperation(value = "Update a patient given its ID")
	@PutMapping("/patients/{id}")
	public Patient updatePatient(@PathVariable String id, @Valid @RequestBody PatientDto updatePatientDto,
			Errors errors) throws IncorrectFieldValueException, PatientNotFoundException {

		// If Dto has errors, then we return an error
		if (errors.hasErrors()) {
			ValidationErrorBuilderUtil.buildErrorMessage(errors);
		}

		if (getPatientById(id) == null) {
			throw new PatientNotFoundException("Patient with given ID was not found");
		}

		// Fetching patient from database, if nobody was found an exception is thrown
		// Update patient with new information, the id generated is saved and not
		// re-generated on update
		Patient patient = patientDao.mapToEntity(updatePatientDto);
		patient = patientDao.updatePatient(patient, patientDao.getPatientById(id).getId());
		return patient;
	}

	/**
	 * Deletes a patient from database, given its id.
	 *
	 * @param id										String : The id of the patient to deleted
	 * @return											A message if the operation was successfull
	 * @throws PatientNotFoundException					Thrown if nobody was found given
	 * @throws MicroserviceNotStartedException
	 */
	@ApiOperation(value = "Delete a patient given its ID")
	@DeleteMapping("/patients/{id}")
	public String deletePatient(@PathVariable String id)
			throws PatientNotFoundException, MicroserviceNotStartedException {

		// Checking if patient with given id exists
		if (id != null && patientDao.getPatientById(id) != null) {

			int notesNumber;
			try {
				notesNumber = notesProxy.getPatientHistory(id).size();
			} catch (FeignException e) {
				throw new MicroserviceNotStartedException("Notes microservice is not started. Cannot delete patient.");
			}

			// Depending on if the patient has note, we do not return the same String
			if (notesNumber > 0) {
				boolean notesDeleted = notesProxy.deleteAllNotesForPatientId(id);
				if (notesDeleted) {
					patientDao.deletePatient(id);
					return String.format(
							"Patient with id '%s' was successfully deleted. Notes attached to him were also deleted.",
							id);
				}
			} else {
				patientDao.deletePatient(id);
				return String.format("Patient with id '%s' was successfully deleted.", id);
			}

		}

		throw new PatientNotFoundException("Patient with given ID was not found.");
	}

}
