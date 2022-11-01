package com.abernathy.patients.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.webforms.SearchPatientDto;

@Controller
public class WebController {

	@Autowired
	PatientDao patientDao;

	/**
	 * This method show the search patient form
	 *
	 * @param searchPatientDto					SearchPatientDto containing fields for the form
	 * @return									Show the search form
	 */
	@GetMapping("/")
	public String showSearchPatientForm(SearchPatientDto searchPatientDto) {
		return "index";
	}

	/**
	 * This method show the results of the search patient form.
	 * If nobody was found, we redirect to the search form again.
	 * If one patient is found, then we redirect to the patient view page.
	 * Otherwise, we display search results with additionnal informations.
	 *
	 * @param searchPatientDto						SearchPatientDto containing filled fields
	 * @return										Return different page based on the explanation
	 * @throws PatientNotFoundException				Thrown if nobody was found
	 */
	@PostMapping("/search")
	public String validateSearchPatientForm(@Valid SearchPatientDto searchPatientDto, BindingResult result,
			Model model) {

		// If there are validations errors on the search form, we display it again
		if (result.hasErrors()) {
			return "index";
		}

		String firstName = searchPatientDto.getFirstName();
		String lastName = searchPatientDto.getLastName();
		List<Patient> patients;

		// First we check for the PatientNotFoundException, if caught we display the
		// search form again
		try {
			patients = patientDao.getPatientsByFirstNameAndLastName(firstName, lastName);
		} catch (PatientNotFoundException e) {
			model.addAttribute("message", e.getMessage());
			return "index";
		}

		// If there is only one patient found, then we directly display the patient view
		// Otherwise we display search results of every patient found
		if (patients.size() == 1) {
			model.addAttribute("patient", patients.get(0));
			return "patientView";
		} else if (patients.size() >= 2) {
			model.addAttribute("patients", patients);
			return "search";
		}

		return "index";
	}

	/**
	 * Show the patient information with its ID
	 *
	 * @param id									String : the id of the patient
	 * @return										PatientView.html
	 * @throws PatientNotFoundException				Thrown if nobody was found
	 */
	@GetMapping("/search/{id}")
	public String showPatientView(@PathVariable("id") String id, Model model) throws PatientNotFoundException {
		Patient patient = patientDao.getPatientById(id);
		model.addAttribute("patient", patient);
		return "patientView";
	}
}
