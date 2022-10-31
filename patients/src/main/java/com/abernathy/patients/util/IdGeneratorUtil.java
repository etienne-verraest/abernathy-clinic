package com.abernathy.patients.util;

import java.util.Random;

import com.abernathy.patients.model.Patient;

public class IdGeneratorUtil {

	public static String generateIdentifier(Patient patient) {

		StringBuilder id = new StringBuilder();

		// Get first letter of first name and last name and append to create the id
		char firstNameLetter = patient.getFirstName().toUpperCase().charAt(0);
		char lastNameLetter = patient.getLastName().toUpperCase().charAt(0);
		id.append(firstNameLetter);
		id.append(lastNameLetter);

		// Generate 5 Digts random number and append it to the string
		int randomNumber = new Random().nextInt(99999);
		id.append(randomNumber);

		// Returning the ID
		return id.toString();
	}
}
