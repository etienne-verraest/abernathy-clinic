package com.abernathy.patients.controller;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.AddPatientDto;

@RestController
@RequestMapping("api/")
public class PatientRestController {

	@Autowired
	PatientDao patientDao;

	@Autowired
	ModelMapper modelMapper;

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
	 */
	@PostMapping("/patients")
	public ResponseEntity<Patient> registerPatient(@Valid @RequestBody AddPatientDto addPatientDto) {
		Patient patient = modelMapper.map(addPatientDto, Patient.class);
		patient = patientDao.savePatient(patient);
		return new ResponseEntity<>(patient, HttpStatus.CREATED);
	}

}
