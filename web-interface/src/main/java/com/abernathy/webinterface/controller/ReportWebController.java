package com.abernathy.webinterface.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.abernathy.webinterface.bean.PatientBean;
import com.abernathy.webinterface.bean.ReportBean;
import com.abernathy.webinterface.proxy.MicroservicePatientsProxy;
import com.abernathy.webinterface.proxy.MicroserviceReportsProxy;

@Controller
public class ReportWebController {

	@Autowired
	MicroservicePatientsProxy patientsProxy;

	@Autowired
	MicroserviceReportsProxy reportsProxy;

	@GetMapping("/{patientId}/report/generate")
	public String generateReport(@PathVariable String patientId, RedirectAttributes redirectAttributes, Model model) {

		// Checking if patient exists before showing report
		PatientBean patient = patientsProxy.getPatientById(patientId);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message",
					String.format("Patient with id '%s' was not found", patientId));
			return "redirect:/";
		}

		ReportBean report = reportsProxy.generateReport(patientId);
		model.addAttribute("report", report);
		return "report/view";
	}
}
