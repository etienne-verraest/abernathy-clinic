package com.abernathy.notes.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.abernathy.notes.bean.PatientBean;

@FeignClient(name = "microservice-patients", url = "${patients.url}")
public interface MicroservicePatientsProxy {

	@GetMapping("api/patients/{id}")
	PatientBean getPatientById(@PathVariable("id") String id);
}
