package com.abernathy.webinterface.controller;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.abernathy.webinterface.bean.NoteBean;
import com.abernathy.webinterface.bean.PatientBean;
import com.abernathy.webinterface.dto.PatientDto;
import com.abernathy.webinterface.dto.SearchPatientDto;
import com.abernathy.webinterface.proxy.MicroserviceNotesProxy;
import com.abernathy.webinterface.proxy.MicroservicePatientsProxy;

@Controller
public class PatientWebController {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	MicroservicePatientsProxy patientsProxy;

	@Autowired
	MicroserviceNotesProxy notesProxy;

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

		// First we check for the PatientNotFoundException, if caught we display the
		// search form again
		List<PatientBean> patients = patientsProxy.getPatients(firstName, lastName);

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
	public String showPatientView(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {

		PatientBean patient = patientsProxy.getPatientById(id);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message", String.format("Patient with id '%s' was not found", id));
			return "redirect:/";
		}

		// Adding patient's microservice information
		model.addAttribute("patient", patient);

		// Adding patient's notes history from Notes microservice
		List<NoteBean> notes = notesProxy.getPatientHistory(id);
		model.addAttribute("hasNotes", !notes.isEmpty());
		model.addAttribute("notesNumber", notes.size());
		model.addAttribute("notes", notes);

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

		PatientBean patient = patientsProxy.registerPatient(patientDto);
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
	public String showUpdatePatientForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {

		PatientBean patient = patientsProxy.getPatientById(id);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message", String.format("Patient with id '%s' was not found", id));
			return "redirect:/";
		}

		PatientDto patientDto = modelMapper.map(patient, PatientDto.class);
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

		// Updating the patient
		PatientBean patient = patientsProxy.updatePatient(patientDto.getId(), patientDto);
		redirectAttributes.addFlashAttribute("message",
				String.format("Patient with id '%s' was successfully updated", patient.getId()));
		return "redirect:/search/" + patient.getId();

	}

	/**
	 * Delete a patient given its ID
	 *
	 * @param patientId								String : The Patient ID
	 * @return										Redirect to the homepage and show appropriate message depending on case
	 */
	@GetMapping("/patient/delete/{patientId}")
	public String deletePatient(@PathVariable String patientId, Model model, RedirectAttributes redirectAttributes) {

		// Checking if patient exists
		PatientBean patient = patientsProxy.getPatientById(patientId);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message",
					String.format("Patient with id '%s' was not found", patientId));
			return "redirect:/";
		}

		// If patient is not null we proceed to deletion
		String deletionMessage = patientsProxy.deletePatient(patientId);
		redirectAttributes.addFlashAttribute("message", deletionMessage);
		return "redirect:/";
	}
}
