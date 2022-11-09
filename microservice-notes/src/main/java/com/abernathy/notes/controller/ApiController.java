package com.abernathy.notes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.notes.dao.NoteDao;
import com.abernathy.notes.exception.NoteNotFoundException;
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
	@GetMapping("notes/{patientId}")
	public List<Note> getPatientHistory(@PathVariable String patientId) throws PatientNotFoundException {
		return noteDao.getNotesByPatientId(patientId);
	}

	/**
	 * Add a note to a given patient
	 *
	 * @param noteDto								NoteDto : Object that contains attributes for the note
	 * @return										Returns the note if the operation is successful
	 * @throws PatientNotFoundException             Thrown if patient with given ID was not found
	 */
	@PostMapping("notes")
	public Note addNoteToPatientHistory(@RequestBody NoteDto noteDto) throws PatientNotFoundException {
		Note note = noteDao.mapToEntity(noteDto);
		return noteDao.createNote(note);
	}

	/**
	 * Update a note given its ID
	 *
	 * @param noteId								String : the note ID (MongoDB Object ID)
	 * @param noteDto								NoteDto containing information for update
	 * @return										Returns the updated note
	 * @throws NoteNotFoundException				Thrown if the note was not found
	 * @throws PatientNotFoundException				Thrown if the patient was not found
	 */
	@PutMapping("notes/{noteId}")
	public Note updateNoteById(@PathVariable("noteId") String noteId, @RequestBody NoteDto noteDto)
			throws NoteNotFoundException, PatientNotFoundException {
		Note note = noteDao.mapToEntity(noteDto);
		return noteDao.updateNote(noteId, note);
	}
}
