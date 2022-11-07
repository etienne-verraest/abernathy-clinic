package com.abernathy.patients.util;

import java.util.Random;

import com.abernathy.patients.model.Patient;

public class IdGeneratorUtil {

	private IdGeneratorUtil() {
	}

	public static String generateIdentifier(Patient patient) {

		StringBuilder id = new StringBuilder();

		// Get first letter of first name and last name and append to create the id
		char firstNameLetter = patient.getFirstName().toUpperCase().charAt(0);
		char lastNameLetter = patient.getLastName().toUpperCase().charAt(0);
		id.append(firstNameLetter);
		id.append(lastNameLetter);

		// Generate a number between 0 and 99999 and append it to the string
		// If the number is <= 9999, there will be trailing zeroes
		int randomNumber = new Random().nextInt(99999);
		String numberWithTrailingZeros = String.format("%05d", randomNumber);
		id.append(numberWithTrailingZeros);

		// Returning the ID
		return id.toString();
	}
}
