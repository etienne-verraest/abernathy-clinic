package com.abernathy.notes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.notes.dao.NoteDao;
import com.abernathy.notes.model.Note;

@RestController
@RequestMapping("api/")
public class ApiController {

	@Autowired
	NoteDao noteDao;

	/**
	 * Get notes for a given patient, based on its ID
	 *
	 * @param id								String : The ID of the patient
	 * @return									A List of notes concerning the patient
	 */
	@GetMapping("notes/{id}")
	public ResponseEntity<List<Note>> getNotes(@PathVariable(required = true) String id) {

		List<Note> notes = noteDao.getNotesByPatientId(id);
		return new ResponseEntity<>(notes, HttpStatus.OK);
	}
}
