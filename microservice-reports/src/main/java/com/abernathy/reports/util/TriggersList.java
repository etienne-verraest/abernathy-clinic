package com.abernathy.reports.util;

import java.util.ArrayList;
import java.util.List;

public class TriggersList {

	// Array containg every triggers words
	static String[] triggers = new String[] { "Hemoglobin A1C", "Microalbumin", "Height", "Weight", "Smoker",
			"Abnormal", "Cholesterol", "Vertigo", "Relapse", "Reaction", "Antibodies" };

	/**
	 * Generates an ArrayList of Triggers that will be used to find keywords in patient's notes
	 *
	 * @return									List<String> containing triggers/keywords
	 */
	public static List<String> generateTriggersList() {
		ArrayList<String> listOfTriggers = new ArrayList<>();
		for (String trigger : triggers) {
			listOfTriggers.add(trigger.toLowerCase());
		}
		return listOfTriggers;
	}

}
