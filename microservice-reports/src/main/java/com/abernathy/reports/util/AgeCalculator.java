package com.abernathy.reports.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeCalculator {

	private AgeCalculator() {

	}

	public static final String BIRTHDATE_FORMAT = "yyyy-MM-dd";

	public static Integer calculateAge(String birthdate) {

		// Parse birthdate to the correct formatting
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(BIRTHDATE_FORMAT);
		LocalDate from = LocalDate.parse(birthdate, formatter);

		// Calculates the time elapsed between 2 dates
		Period age = Period.between(from, LocalDate.now());
		return age.getYears();
	}
}