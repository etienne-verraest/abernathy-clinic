package com.abernathy.notes.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abernathy.notes.model.Note;
import com.abernathy.notes.repository.NoteRepository;

@Component
public class NoteDao {

	@Autowired
	NoteRepository noteRepository;

	/**
	 * Get notes for a given patient
	 *
	 * @param patientId						String : The ID of the patient
	 * @return								List<Note> containing all practitioner's notes concerning the patient
	 */
	public List<Note> getNotesByPatientId(String patientId) {
		return noteRepository.findByPatientId(patientId);
	}

}
