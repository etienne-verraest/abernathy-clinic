package com.abernathy.patients.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.abernathy.patients.bean.NoteBean;
import com.abernathy.patients.model.dto.NoteDto;

@FeignClient(name = "microservice-notes", url = "localhost:9002")
public interface MicroserviceNotesProxy {

	@GetMapping("api/notes/{patientId}")
	List<NoteBean> getPatientHistory(@PathVariable String patientId);

	@PostMapping("api/notes")
	NoteBean addNoteToPatientHistory(@RequestBody NoteDto noteDto);

	@PutMapping("api/notes/{noteId}")
	NoteBean updateNoteById(@PathVariable("noteId") String noteId, @RequestBody NoteDto noteDto);

	@DeleteMapping("api/notes/{noteId}")
	boolean deleteNoteById(@PathVariable("noteId") String noteId);
}