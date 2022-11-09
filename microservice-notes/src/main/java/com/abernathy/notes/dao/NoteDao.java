package com.abernathy.notes.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	 * @param patientId							String : The ID of the patient
	 * @return									List<Note> containing all practitioner's notes concerning the patient
	 * @throws PatientNotFoundException			Thrown if no patient were found with given ID
	 */
	public List<Note> getNotesByPatientId(String patientId) throws PatientNotFoundException {
		if (patientExists(patientId)) {
			return noteRepository.findByPatientId(patientId);
		}
		throw new PatientNotFoundException("Patient with given ID was not found");
	}

	/**
	 * Add a note to the given patient history
	 *
	 * @param note								A Note object practitioner's notes
	 * @return									Returns a Note object if the operation is successful
	 * @throws PatientNotFoundException         Thrown if no patient were found with given ID
	 */
	public Note createNote(Note note) throws PatientNotFoundException {
		if (patientExists(note.getId())) {
			// Here we are forcing the generation of the ObjectID for the note
			note.setId(new ObjectId().toString());
			return noteRepository.insert(note);
		}
		throw new PatientNotFoundException("Patient with given ID was not found");
	}

	/**
	 * Map DTO to Entity
	 *
	 * @param noteDto							The NoteDto to map to entity
	 * @return									Note entity which can be saved in MongoDB database
	 */
	public Note mapToEntity(NoteDto noteDto) {
		return modelMapper.map(noteDto, Note.class);
	}

	/**
	 * Helper method that is used to check if a patient exists
	 * The patients are handled by the "Patients" microservice.
	 *
	 * @param patientId							String : The ID of the patient to check
	 * @return									Returns true if patient with ID exists
	 */
	private boolean patientExists(String patientId) {
		return patientsProxy.getPatientById(patientId) != null;
	}

}
