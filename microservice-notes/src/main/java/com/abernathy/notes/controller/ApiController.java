package com.abernathy.notes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.notes.dao.NoteDao;
import com.abernathy.notes.exception.PatientNotFoundException;
import com.abernathy.notes.model.Note;
import com.abernathy.notes.model.dto.NoteDto;

@RestController
@RequestMapping("api/")
public class ApiController {

	@Autowired
	NoteDao noteDao;

	/**
	 * Get a patient history, given its id
	 *
	 * @param id									String : The ID of the patient
	 * @return										A List of notes concerning the patient
	 * @throws PatientNotFoundException				Thrown if patient with given ID was not found
	 */
	@GetMapping("notes/{id}")
	public List<Note> getPatientHistory(@PathVariable("id") String patientId) throws PatientNotFoundException {
		return noteDao.getNotesByPatientId(patientId);
	}

	/**
	 * Add a note to the given patient
	 *
	 * @param noteDto								NoteDto : Object that contains attributes for the note
	 * @return										Return the note if the operation is successful
	 * @throws PatientNotFoundException             Thrown if patient with given ID was not found
	 */
	@PostMapping("notes")
	public Note addNoteToPatientHistory(@RequestBody NoteDto noteDto) throws PatientNotFoundException {
		Note note = noteDao.mapToEntity(noteDto);
		return noteDao.createNote(note);
	}
}
