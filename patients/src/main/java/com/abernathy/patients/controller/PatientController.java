package com.abernathy.patients.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.model.Patient;

@RestController
public class PatientController {

	@Autowired
	PatientDao patientDao;

	/**
	 * This method returns every patients registered in the clinic
	 *
	 * @return							ResponseEntity : List<Patient> containing every patient
	 */
	@GetMapping("/patients")
	public ResponseEntity<List<Patient>> getPatients() {
		List<Patient> patients = patientDao.getPatients();
		return new ResponseEntity<>(patients, HttpStatus.OK);
	}

	@GetMapping("/patient")
	public ResponseEntity<Patient> getPatient(@RequestParam String firstName, @RequestParam String lastName) {
		Patient patient = patientDao.getPatientByFirstNameAndLastName(firstName, lastName);
		return new ResponseEntity<>(patient, HttpStatus.OK);
	}

}
