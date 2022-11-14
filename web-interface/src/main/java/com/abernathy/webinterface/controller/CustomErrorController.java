package com.abernathy.webinterface.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

	public String getErrorPath() {
		return "/error";
	}

	@GetMapping("/error")
	public String handleError(HttpServletResponse response, Model model) {

		// Here we want to avoid going on the error page by giving the id
		if (response.getStatus() != 200) {
			String errorMessage = String.format("Oops ! A %d error happened.", response.getStatus());
			model.addAttribute("errorMessage", errorMessage);
			return "error";
		}

		return "redirect:/";
	}
}