package com.abernathy.patients.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.abernathy.patients.bean.NoteBean;

@FeignClient(name = "microservice-notes", url = "${notes.url}")
public interface MicroserviceNotesProxy {

	@DeleteMapping("api/notes/all/{patientId}")
	boolean deleteAllNotesForPatientId(@PathVariable String patientId);

	@GetMapping("api/notes/{patientId}")
	List<NoteBean> getPatientHistory(@PathVariable String patientId);

}
