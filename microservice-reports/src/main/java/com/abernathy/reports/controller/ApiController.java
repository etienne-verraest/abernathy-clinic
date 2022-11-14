package com.abernathy.reports.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abernathy.reports.generator.ReportsGenerator;
import com.abernathy.reports.model.Report;

@RestController
@RequestMapping("api/")
public class ApiController {

	@Autowired
	ReportsGenerator reportsGenerator;

	@GetMapping("/reports/generate/{patientId}")
	public Report generateReport(@PathVariable String patientId) throws Exception {

		if (reportsGenerator.generateReports(patientId) != null) {
			return reportsGenerator.generateReports(patientId);
		}

		return null;
	}

}
