package com.abernathy.patients.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.NoteDto;
import com.abernathy.patients.proxy.MicroserviceNotesProxy;

@Controller
public class NoteWebController {

	// TODO : Add delete and edit icons

	@Autowired
	PatientDao patientDao;

	@Autowired
	MicroserviceNotesProxy notesProxy;

	@GetMapping("/notes/{patientId}")
	public String showAddNoteToPatientForm(@PathVariable("patientId") String patientId, NoteDto noteDto,
			RedirectAttributes redirectAttributes) {

		// Checking if patient exists before showing form
		Patient patient = patientDao.getPatientById(patientId);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message",
					String.format("Patient with id '%s' was not found", patientId));
			return "redirect:/";
		}

		// Forcing hidden field in form to have the path variable
		noteDto.setPatientId(patientId);
		return "note/add";
	}

	@PostMapping("/notes/{patientId}/add")
	public String submitAddNoteForm(@PathVariable("patientId") String patientId, @Valid NoteDto noteDto,
			BindingResult result) {

		// If there are validations errors on the form, we display it again
		if (result.hasErrors()) {
			return "note/add";
		}

		// Otherwise we call our proxy method and add it to the database
		notesProxy.addNoteToPatientHistory(noteDto);
		return "redirect:/search/" + patientId;

	}

}
