package com.abernathy.notes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.notes.dao.NoteDao;
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
	 * @param id								String : The ID of the patient
	 * @return									A List of notes concerning the patient
	 */
	@GetMapping("notes/{id}")
	public ResponseEntity<List<Note>> getPatientHistory(@PathVariable(required = true) String id) {
		List<Note> notes = noteDao.getNotesByPatientId(id);
		return new ResponseEntity<>(notes, HttpStatus.OK);
	}

	/**
	 * Add a note to the given patient
	 *
	 * @param noteDto							NoteDto : Object that contains attributes for the note
	 * @return									Return the note if the operation is successful
	 */
	@PostMapping("notes")
	public ResponseEntity<Note> addNoteToPatientHistory(@RequestBody NoteDto noteDto) {
		Note note = noteDao.mapToEntity(noteDto);
		noteDao.createNote(note);
		return new ResponseEntity<>(note, HttpStatus.CREATED);
	}
}
