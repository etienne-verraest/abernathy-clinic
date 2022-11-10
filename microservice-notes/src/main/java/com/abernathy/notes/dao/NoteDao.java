package com.abernathy.notes.dao;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abernathy.notes.exception.NoteNotFoundException;
import com.abernathy.notes.exception.PatientNotFoundException;
import com.abernathy.notes.model.Note;
import com.abernathy.notes.model.dto.NoteDto;
import com.abernathy.notes.proxy.MicroservicePatientsProxy;
import com.abernathy.notes.repository.NoteRepository;

@Component
public class NoteDao {

	@Autowired
	NoteRepository noteRepository;

	@Autowired
	MicroservicePatientsProxy patientsProxy;

	@Autowired
	ModelMapper modelMapper;

	/**
	 * Get notes for a given patient
	 *
	 * @param patientId								String : The ID of the patient
	 * @return										List<Note> containing all practitioner's notes concerning the patient
	 * @throws PatientNotFoundException				Thrown if no patient were found with given ID
	 */
	public List<Note> getNotesByPatientId(String patientId) throws PatientNotFoundException {
		if (patientExists(patientId)) {
			return noteRepository.findByPatientIdOrderByDateDesc(patientId);
		}
		throw new PatientNotFoundException("Patient with given ID was not found");
	}

	/**
	 * Add a note to the given patient history
	 *
	 * @param note									A Note object containing practitioner's notes
	 * @return										Returns a Note object if the operation is successful
	 * @throws PatientNotFoundException         	Thrown if no patient were found with given ID
	 */
	public Note createNote(Note note) throws PatientNotFoundException {
		if (patientExists(note.getPatientId())) {
			// Here we are forcing the generation of the ObjectID for the note
			// Otherwise ModelMapper will set the id as the patient ID.
			note.setId(new ObjectId().toString());
			note.setDate(new Date());
			return noteRepository.insert(note);
		}
		throw new PatientNotFoundException("Patient with given ID was not found");
	}

	/**
	 * Update a note in patient history. First the method check if
	 * the note exists in database. Then it checks if the patient
	 * exists.
	 * If both criterias are met, the update will be made.
	 *
	 * @param noteId								String : The note ID (MongoDB Object ID)
	 * @param note									Note : The note with new information to update
	 * @return										Returns the updated note
	 * @throws NoteNotFoundException				Thrown if the Note was not found
	 * @throws PatientNotFoundException				Thrown if the Patient was not found
	 */
	public Note updateNote(String noteId, Note note) throws NoteNotFoundException, PatientNotFoundException {
		if (noteRepository.findById(noteId).isPresent()) {
			if (patientExists(note.getPatientId())) {
				note.setId(noteId);
				note.setDate(new Date());
				return noteRepository.save(note);
			}

			throw new PatientNotFoundException("Patient with given ID was not found");
		}
		throw new NoteNotFoundException("Note with given ObjectID was not found");
	}

	/**
	 * Delete a note given its ID
	 *
	 * @param noteId								String : The note ID (MongoDB Object ID)
	 * @return										Returns true if the deletion was successful
	 * @throws NoteNotFoundException				Thrown if the Note was not found
	 */
	public boolean deleteNote(String noteId) throws NoteNotFoundException {
		if (noteRepository.findById(noteId).isPresent()) {
			noteRepository.deleteById(noteId);
			return true;
		}

		throw new NoteNotFoundException("Note with given ObjectID was not found");
	}

	/**
	 * Map DTO to Entity
	 *
	 * @param noteDto								The NoteDto to map to entity
	 * @return										Note entity which can be saved in MongoDB database
	 */
	public Note mapToEntity(NoteDto noteDto) {
		return modelMapper.map(noteDto, Note.class);
	}

	/**
	 * Helper method that is used to check if a patient exists
	 * The patients are handled by the "Patients" microservice.
	 *
	 * @param patientId								String : The ID of the patient to check
	 * @return										Returns true if patient with ID exists
	 */
	private boolean patientExists(String patientId) {
		return patientsProxy.getPatientById(patientId) != null;
	}

}
