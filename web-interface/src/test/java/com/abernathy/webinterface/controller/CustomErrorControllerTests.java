package com.abernathy.webinterface.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CustomErrorController.class)
class CustomErrorControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testError_ShouldReturnTo_HomePage() throws Exception {
		mockMvc.perform(get("/error")).andExpect(status().is3xxRedirection());
	}
}
