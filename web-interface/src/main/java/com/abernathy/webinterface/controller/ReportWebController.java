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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ReportWebController {

	@Autowired
	MicroservicePatientsProxy patientsProxy;

	@Autowired
	MicroserviceReportsProxy reportsProxy;

	/**
	 * Generate the report for a given patient
	 *
	 * @param patientId									String : The patient ID we want to generate the report
	 * @return											Shows the report
	 */
	@GetMapping("/{patientId}/report/generate")
	public String generateReport(@PathVariable String patientId, RedirectAttributes redirectAttributes, Model model) {

		// Checking if patient exists before showing report
		PatientBean patient = patientsProxy.getPatientById(patientId);
		if (patient == null) {
			redirectAttributes.addFlashAttribute("message",
					String.format("Patient with id '%s' was not found", patientId));
			return "redirect:/";
		}

		// Getting reports from the dedicated microservice
		ReportBean report = reportsProxy.generateReport(patientId);
		if (report != null) {
			model.addAttribute("patient", patient);
			model.addAttribute("report", report);
			model.addAttribute("triggersNumber", report.getTriggers().size());

			// Building keywords list as a string if the risk is not none
			if (!report.getRisk().equals("None")) {
				StringBuilder triggersBuilder = new StringBuilder();
				report.getTriggers().forEach(trigger -> triggersBuilder.append(trigger + ", "));
				String triggersString = triggersBuilder.toString().substring(0,
						triggersBuilder.toString().length() - 2); // Removing the last space and comma for last trigger
				model.addAttribute("triggersString", triggersString);
			}

			// Returning the report view, text is set directly in the HTML template and is
			// handled by Thymeleaf
			return "report/view";
		}

		redirectAttributes.addFlashAttribute("message", "Could not generate report for this patient");
		return "redirect:/" + patientId;
	}
}
