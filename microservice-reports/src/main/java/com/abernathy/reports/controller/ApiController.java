package com.abernathy.reports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.reports.exception.PatientNotFoundException;
import com.abernathy.reports.generator.ReportsGenerator;
import com.abernathy.reports.model.Report;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/")
public class ApiController {

	@Autowired
	ReportsGenerator reportsGenerator;

	/**
	 * Generate a diabete risk report given a patient ID
	 *
	 * @param patientId								String : The Patient ID
	 * @return										Returns a Report object containing datas if generation is successful
	 * @throws PatientNotFoundException				Thrown if patient was not found
	 */
	@ApiOperation(value = "Generate a diabete risk report given a patient ID")
	@GetMapping("/reports/generate/{patientId}")
	public Report generateReport(@PathVariable String patientId) throws PatientNotFoundException {

		if (reportsGenerator.generateReports(patientId) != null) {
			return reportsGenerator.generateReports(patientId);
		}

		return null;
	}

}
