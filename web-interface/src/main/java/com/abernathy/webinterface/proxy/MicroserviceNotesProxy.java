package com.abernathy.webinterface.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.abernathy.webinterface.bean.NoteBean;
import com.abernathy.webinterface.dto.NoteDto;

@FeignClient(name = "microservice-notes", url = "${notes.url}")
public interface MicroserviceNotesProxy {

	@GetMapping("api/notes/{patientId}")
	List<NoteBean> getPatientHistory(@PathVariable String patientId);

	@GetMapping("api/note/{noteId}")
	NoteBean getNote(@PathVariable String noteId);

	@PostMapping("api/notes")
	NoteBean addNoteToPatientHistory(@RequestBody NoteDto noteDto);

	@PutMapping("api/notes/{noteId}")
	NoteBean updateNoteById(@PathVariable String noteId, @RequestBody NoteDto noteDto);

	@DeleteMapping("api/notes/{noteId}")
	boolean deleteNoteById(@PathVariable String noteId);

	@DeleteMapping("api/notes/all/{patientId}")
	boolean deleteAllNotesForPatientId(@PathVariable String patientId);
}
