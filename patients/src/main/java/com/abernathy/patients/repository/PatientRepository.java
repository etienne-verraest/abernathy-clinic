package com.abernathy.patients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abernathy.patients.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

	List<Patient> findByFirstNameAndLastName(String firstName, String lastName);
}
