package com.abernathy.patients.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abernathy.patients.model.Patient;
import com.abernathy.patients.repository.PatientRepository;

@Component
public class PatientDao {

	@Autowired
	PatientRepository patientRepository;

	public List<Patient> getPatients() {
		return patientRepository.findAll();
	}

}
