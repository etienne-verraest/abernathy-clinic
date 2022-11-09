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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.exceptions.PatientNotFoundException;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.PatientDto;
import com.abernathy.patients.model.dto.webforms.SearchPatientDto;

@Controller
public class WebController {

	// TODO : Set up Feign client
	// TODO : Set up Notes Proxy
	// TODO : Set up Notes Web Controller with UI

	@Autowired
	PatientDao patientDao;

	/**
	 * This method show the "Search Patientt" Form
	 *
	 * @param searchPatientDto						SearchPatientDto containing fields for the form
	 * @return										search.html
	 */
	@GetMapping("/")
	public String showSearchPatientForm(SearchPatientDto searchPatientDto, RedirectAttributes redirectAttributes) {
		return "index";
	}

	/**
	 * This method show the results of the search patient form.
	 * If nobody was found, we redirect to the search form again.
	 * If one patient is found, then we redirect to the patient view page.
	 * Otherwise, we display search results with additionnal informations.
	 *
	 * @param searchPatientDto						SearchPatientDto containing user defined datas
	 * @throws PatientNotFoundException				Thrown if nobody was found
	 */
	@PostMapping("/search")
	public String validateSearchPatientForm(@Valid SearchPatientDto searchPatientDto, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {

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
			return "redirect:/search/" + patients.get(0).getId();
		} else if (patients.size() > 1) {
			model.addAttribute("patients", patients);
			return "search";
		}

		return "index";
	}

	/**
	 * Show patient's information, searched with ID
	 *
	 * @param id									String : the id of the patient
	 * @return										patient/view.html
	 * @throws PatientNotFoundException				Thrown if nobody was found
	 */
	@GetMapping("/search/{id}")
	public String showPatientView(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {

		Patient patient = patientDao.getPatientById(id);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message", String.format("Patient with id '%s' was not found", id));
			return "redirect:/";
		}

		model.addAttribute("patient", patient);
		return "patient/view";
	}

	/**
	 * Show the form to add a patient. The form uses the same DTO as the api.
	 * Hence, form will be validated with the same requirements as the api.
	 *
	 */
	@GetMapping("/patient/add")
	public String showAddPatientForm(Model model) {
		model.addAttribute("patientDto", new PatientDto());
		return "patient/add";
	}

	/**
	 * Validate the "Add Patient" form.
	 * If validation is correct, the user is redirected to the new user profile
	 * Otherwise validation error messages are shown
	 *
	 * @param patientDto							PatientDto containing user defined datas
	 *
	 */
	@PostMapping("/patient/add")
	public String validateAddPatientForm(@Valid PatientDto patientDto, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "patient/add";
		}

		Patient patient = patientDao.mapToEntity(patientDto);
		patient = patientDao.savePatient(patient);
		return "redirect:/search/" + patient.getId();

	}

	/**
	 * Show the form to edit a patient
	 * Form will be filled with the fields corresponding to the datas of the patient
	 *
	 * @param id									String : the id of the patient
	 * @throws PatientNotFoundException				Thrown if id was not found in database
	 */
	@GetMapping("/patient/update/{id}")
	public String showUpdatePatientForm(@PathVariable("id") String id, Model model,
			RedirectAttributes redirectAttributes) {

		Patient patient = patientDao.getPatientById(id);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message", String.format("Patient with id '%s' was not found", id));
			return "redirect:/";
		}

		PatientDto patientDto = patientDao.mapToDto(patient);
		model.addAttribute("patientDto", patientDto);
		return "patient/edit";
	}

	/**
	 * Validate the "Edit Patient" form.
	 *
	 * @param patientDto							PatientDto containing patient data
	 * @return										User profile if validation is correct, otherwise throws error
	 */
	@PostMapping("/patient/update")
	public String validateUpdatePatientForm(@Valid PatientDto patientDto, BindingResult result, Model model,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "patient/edit";
		}

		// Mapping and updating the patient
		Patient patient = patientDao.mapToEntity(patientDto);
		patient = patientDao.updatePatient(patient, patientDto.getId());

		redirectAttributes.addFlashAttribute("message",
				String.format("Patient with id '%s' was successfully updated", patientDto.getId()));
		return "redirect:/search/" + patient.getId();

	}

	@GetMapping("/patient/delete/{id}")
	public String deletePatient(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes)
			throws PatientNotFoundException {

		Patient patient = patientDao.getPatientById(id);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message", String.format("Patient with id '%s' was not found", id));
			return "redirect:/";
		}

		// If there is no exception caught we delete the patient
		if (patientDao.deletePatient(id)) {
			redirectAttributes.addFlashAttribute("message",
					String.format("Patient with id '%s' was successfully deleted", id));
			return "redirect:/";
		}

		return "redirect:/search/" + id;
	}
}
