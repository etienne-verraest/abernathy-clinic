package com.abernathy.patients.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservice-notes", url = "localhost:9002")
public interface MicroserviceNotesProxy {

	@DeleteMapping("api/notes/all/{patientId}")
	boolean deleteAllNotesForPatientId(@PathVariable String patientId);
}
