package com.abernathy.patients.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
	 * @param searchPatientDto					SearchPatientDto containing filled fields
	 * @return									Return different page based on the explanation
	 * @throws PatientNotFoundException
	 */
	@PostMapping("/search")
	public String validateSearchPatientForm(@Valid SearchPatientDto searchPatientDto, BindingResult result, Model model)
			throws PatientNotFoundException {

		// If there are errors when searching for result we return the search form
		if (result.hasErrors()) {
			return "index";
		}

		// Otherwise we display search results
		String firstName = searchPatientDto.getFirstName();
		String lastName = searchPatientDto.getLastName();
		List<Patient> patients = patientDao.getPatientsByFirstNameAndLastName(firstName, lastName);

		// If Patient list is empty, then we redirect to the index page
		if (patients.isEmpty()) {
			result.rejectValue("firstName", "No patient were found");
			return "index";
		} // If Patient list size is equal to 1 we redirect directly to the view patient
			// page
		else if (patients.size() == 1) {
			model.addAttribute("patient", patients.get(0));
			return "patientView";
		} // Otherwise we display search results of every occurrences found
		else if (patients.size() >= 2) {
			model.addAttribute("patients", patients);
			return "search";
		}
		return "index";
	}
}
