package com.abernathy.webinterface.controller;

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
import com.abernathy.webinterface.dto.NoteDto;
import com.abernathy.webinterface.proxy.MicroserviceNotesProxy;
import com.abernathy.webinterface.proxy.MicroservicePatientsProxy;

@Controller
public class NoteWebController {

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	MicroservicePatientsProxy patientsProxy;

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
		PatientBean patient = patientsProxy.getPatientById(patientId);
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
		return "redirect:/" + patientId;
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

		// Check if patient exists
		PatientBean patient = patientsProxy.getPatientById(patientId);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message",
					String.format("Patient with id '%s' was not found", patientId));
			return "redirect:/";
		}

		// Check if note exists
		NoteBean note = notesProxy.getNote(noteId);
		if (note == null) {
			redirectAttributes.addFlashAttribute("message", "Note was not found.");
			return "redirect:/search/" + patientId;
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

		redirectAttributes.addFlashAttribute("message", "Note was successfully updated.");
		notesProxy.updateNoteById(noteId, noteDto);
		return "redirect:/" + noteDto.getPatientId();
	}

	/**
	 * Delete a specific note from patient history
	 *
	 * @param patientId									String : The patient whom we will update the note
	 * @param noteId									String : The ID of the note
	 * @return											Returns the Patient view if the deletion was succesful
	 */
	@GetMapping("/{patientId}/notes/{noteId}/delete")
	public String deleteNoteFromPatientHistory(@PathVariable String patientId, @PathVariable String noteId,
			RedirectAttributes redirectAttributes) {

		// Check if patient exists
		PatientBean patient = patientsProxy.getPatientById(patientId);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message",
					String.format("Patient with id '%s' was not found", patientId));
			return "redirect:/";
		}

		// Check if note exists
		NoteBean note = notesProxy.getNote(noteId);
		if (note == null) {
			redirectAttributes.addFlashAttribute("message", "Note was not found");
			return "redirect:/" + patientId;
		}

		// Check if patient Id are the same (in path and in note)
		if (!note.getPatientId().equals(patientId)) {
			redirectAttributes.addFlashAttribute("message",
					"You are trying to delete a note that doesn't belong to the correct patient");
			return "redirect:/" + patientId;
		}

		// Delete the note if every checks have passed
		notesProxy.deleteNoteById(noteId);
		redirectAttributes.addFlashAttribute("message", "Note was successfully deleted.");
		return "redirect:/" + patientId;

	}
}
