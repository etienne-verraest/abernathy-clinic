package com.abernathy.reports.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.abernathy.reports.bean.NoteBean;

@FeignClient(name = "microservice-notes", url = "localhost:9002")
public interface MicroserviceNotesProxy {

	@GetMapping("api/notes/{patientId}")
	List<NoteBean> getPatientHistory(@PathVariable String patientId);

}
