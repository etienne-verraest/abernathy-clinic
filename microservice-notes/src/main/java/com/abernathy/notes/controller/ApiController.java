package com.abernathy.notes.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.notes.dao.NoteDao;
import com.abernathy.notes.exception.IncorrectFieldValueException;
import com.abernathy.notes.exception.NoteNotFoundException;
import com.abernathy.notes.exception.PatientNotFoundException;
import com.abernathy.notes.model.Note;
import com.abernathy.notes.model.dto.NoteDto;
import com.abernathy.notes.util.ValidationErrorBuilderUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/")
public class ApiController {

	@Autowired
	NoteDao noteDao;

	/**
	 * Get a patient history, given its id
	 *
	 * @param id									String : The ID of the patient
	 * @return										A List<Note> concerning the patient
	 * @throws PatientNotFoundException				Thrown if Patient with given ID was not found
	 */
	@ApiOperation(value = "Get notes for a given patient")
	@GetMapping("notes/{patientId}")
	public List<Note> getPatientHistory(@PathVariable String patientId) throws PatientNotFoundException {
		return noteDao.getNotesByPatientId(patientId);
	}

	/**
	 * Get a note given its id
	 *
	 * @param noteId								String : The ID of the Note (MongoDB Object ID)
	 * @return										Returns the note
	 * @throws NoteNotFoundException				Thrown if the Note was not found
	 */
	@ApiOperation(value = "Get a note by its ID")
	@GetMapping("note/{noteId}")
	public Note getNote(@PathVariable String noteId) throws NoteNotFoundException {
		return noteDao.getNoteById(noteId);
	}

	/**
	 * Add a note to a given patient
	 *
	 * @param noteDto									NoteDto : Object that contains attributes for the note
	 * @return											Returns the Note if the operation is successful
	 * @throws PatientNotFoundException             	Thrown if Patient with given ID was not found
	 * @throws IncorrectFieldValueException				Thrown if one (or more) fields are incorrect
	 */
	@ApiOperation(value = "Add a note")
	@PostMapping("notes")
	public Note addNoteToPatientHistory(@Valid @RequestBody NoteDto noteDto, Errors errors)
			throws PatientNotFoundException, IncorrectFieldValueException {

		if (errors.hasErrors()) {
			ValidationErrorBuilderUtil.buildErrorMessage(errors);
		}

		Note note = noteDao.mapToEntity(noteDto);
		return noteDao.createNote(note);
	}

	/**
	 * Update a note given its ID
	 *
	 * @param noteId									String : the note ID (MongoDB Object ID)
	 * @param noteDto									NoteDto containing information for update
	 * @return											Returns the updated note
	 * @throws NoteNotFoundException					Thrown if the Note was not found
	 * @throws PatientNotFoundException					Thrown if the Patient was not found
	 * @throws IncorrectFieldValueException				Thrown if one (or more) fields are incorrect
	 */
	@ApiOperation(value = "Update a note by its ID")
	@PutMapping("notes/{noteId}")
	public Note updateNoteById(@PathVariable String noteId, @Valid @RequestBody NoteDto noteDto, Errors errors)
			throws NoteNotFoundException, PatientNotFoundException, IncorrectFieldValueException {

		if (errors.hasErrors()) {
			ValidationErrorBuilderUtil.buildErrorMessage(errors);
		}

		Note note = noteDao.mapToEntity(noteDto);
		return noteDao.updateNote(noteId, note);
	}

	/**
	 * Delete a note given its ID
	 *
	 * @param noteId								String : the note ID (MongoDB Object ID)
	 * @return										Returns true if the deletion was successful
	 * @throws NoteNotFoundException				Thrown if the Note was not found
	 */
	@ApiOperation(value = "Delete a note by its ID")
	@DeleteMapping("notes/{noteId}")
	public boolean deleteNoteById(@PathVariable String noteId) throws NoteNotFoundException {
		return noteDao.deleteNote(noteId);
	}

	/**
	 * Delete all notes for a patient
	 *
	 * @param patientId								String : The Patient ID
	 * @return										Returns true if the deletion was successful
	 * @throws PatientNotFoundException				Thrown if the Patient was not found
	 */
	@ApiOperation(value = "Delete all notes for a given patient")
	@DeleteMapping("notes/all/{patientId}")
	public boolean deleteAllNotesForPatientId(@PathVariable String patientId) throws PatientNotFoundException {
		return noteDao.deleteAllNotesForPatient(patientId);
	}
}
