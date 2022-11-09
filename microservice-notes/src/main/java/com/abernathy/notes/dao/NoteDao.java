package com.abernathy.notes.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abernathy.notes.model.Note;
import com.abernathy.notes.model.dto.NoteDto;
import com.abernathy.notes.repository.NoteRepository;

@Component
public class NoteDao {

	@Autowired
	NoteRepository noteRepository;

	@Autowired
	ModelMapper modelMapper;

	/**
	 * Get notes for a given patient
	 *
	 * @param patientId						String : The ID of the patient
	 * @return								List<Note> containing all practitioner's notes concerning the patient
	 */
	public List<Note> getNotesByPatientId(String patientId) {
		return noteRepository.findByPatientId(patientId);
	}

	/**
	 * Add a note to the given patient history
	 *
	 * @param note							A Note object practitioner's notes
	 * @return								Returns a Note object if the operation is successful
	 */
	public Note createNote(Note note) {
		// Here we are forcing the generation of the ObjectID for each notes
		note.setId(new ObjectId().toString());
		return noteRepository.insert(note);
	}

	/**
	 * Map DTO to Entity
	 *
	 * @param noteDto						The NoteDto to map to entity
	 * @return								Note entity which can be saved in MongoDB database
	 */
	public Note mapToEntity(NoteDto noteDto) {
		return modelMapper.map(noteDto, Note.class);
	}

}
