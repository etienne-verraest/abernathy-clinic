package com.abernathy.notes.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.abernathy.notes.model.Note;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

	List<Note> findByPatientId(String patientId);

}
