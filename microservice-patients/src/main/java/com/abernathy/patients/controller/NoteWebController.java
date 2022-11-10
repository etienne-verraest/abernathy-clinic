package com.abernathy.patients.controller;

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

import com.abernathy.patients.bean.NoteBean;
import com.abernathy.patients.dao.PatientDao;
import com.abernathy.patients.model.Patient;
import com.abernathy.patients.model.dto.NoteDto;
import com.abernathy.patients.proxy.MicroserviceNotesProxy;

@Controller
public class NoteWebController {

	@Autowired
	PatientDao patientDao;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	MicroserviceNotesProxy notesProxy;

	/**
	 * Show "New Note" Form
	 *
	 * @param patientId									String : The patient whom we will add the note
	 * @param noteDto									NoteDto containing note's fields
	 * @return											Returns the "New Note" Form
	 */
	@GetMapping("/{patientId}/notes/add")
	public String showNewForm(@PathVariable String patientId, NoteDto noteDto, RedirectAttributes redirectAttributes) {

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

	/**
	 * Validate the new note form
	 *
	 * @param patientId									String : The patient whom we will add the note
	 * @param noteDto									NoteDto containing note's fields
	 * @return											Returns to the patient view if insert was succesful
	 */
	@PostMapping("/{patientId}/notes/add")
	public String submitNewForm(@PathVariable String patientId, @Valid NoteDto noteDto, BindingResult result) {

		// If there are validations errors on the form, we display it again
		if (result.hasErrors()) {
			return "note/add";
		}

		// Otherwise we call our proxy method and add it to the database
		notesProxy.addNoteToPatientHistory(noteDto);
		return "redirect:/search/" + patientId;
	}

	/**
	 * Show the "Update Note" Form
	 *
	 * @param patientId									String : The patient whom we will update the note
	 * @param noteId									String : The ID of the note
	 * @return											Returns the form to update Note
	 */
	@GetMapping("/{patientId}/notes/{noteId}/edit")
	public String showUpdateForm(@PathVariable String patientId, @PathVariable String noteId, Model model,
			RedirectAttributes redirectAttributes) {

		NoteBean note = notesProxy.getNote(noteId);
		if (note == null) {
			redirectAttributes.addFlashAttribute("message", "Note was not found");
			return "redirect:/search/";
		}

		NoteDto noteDto = modelMapper.map(note, NoteDto.class);
		model.addAttribute("noteDto", noteDto);
		return "note/edit";
	}

	/**
	 * Validate the "Update Fom" form
	 *
	 * @param patientId									String : The patient whom we will update the note
	 * @param noteId									String : The ID of the note
	 * @param noteDto									NoteDto containing note's fields
	 * @return											Returns the Patient view if the edit was successful
	 */
	@PostMapping("/{patientId}/notes/{noteId}/edit")
	public String submitUpdateForm(@PathVariable String patientId, @PathVariable String noteId, @Valid NoteDto noteDto,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "note/edit";
		}

		redirectAttributes.addFlashAttribute("message", "Note with id '" + noteId + "' was successfully updated");
		notesProxy.updateNoteById(noteId, noteDto);
		return "redirect:/search/" + noteDto.getPatientId();
	}

}
