package com.abernathy.patients.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.abernathy.patients.model.dto.webforms.SearchPatientDto;

@Controller
public class WebController {

	@GetMapping("/")
	public String showSearchPatientForm(SearchPatientDto searchPatientDto) {
		return "index";
	}

	@PostMapping("/search")
	public String validateSearchPatientForm(@Valid SearchPatientDto searchPatientDto, BindingResult result,
			Model model) {

		// If there are errors when searching for result we return the search form
		if (result.hasErrors()) {
			return "index";
		}

		// Otherwise we display search results
		return "search";
	}
}
