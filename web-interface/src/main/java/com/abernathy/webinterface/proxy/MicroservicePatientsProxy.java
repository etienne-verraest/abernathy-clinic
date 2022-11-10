package com.abernathy.webinterface.proxy;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.abernathy.webinterface.bean.PatientBean;
import com.abernathy.webinterface.dto.PatientDto;

@FeignClient(name = "microservice-patients", url = "localhost:9001")
public interface MicroservicePatientsProxy {

	@GetMapping("api/patients")
	List<PatientBean> getPatients(@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName);

	@GetMapping("api/patients/{id}")
	PatientBean getPatientById(@PathVariable String id);

	@PostMapping("api/patients")
	PatientBean registerPatient(@RequestBody PatientDto addPatientDto);

	@PutMapping("api/patients/{id}")
	PatientBean updatePatient(@PathVariable String id, @Valid @RequestBody PatientDto updatePatientDto);

	@DeleteMapping("api/patients/{id}")
	boolean deletePatient(@PathVariable String id);
}
