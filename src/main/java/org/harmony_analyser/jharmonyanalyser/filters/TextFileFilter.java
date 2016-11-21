package org.harmony_analyser.jharmonyanalyser.filters;

import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Abstract filter for further processing of result text files
 */

@SuppressWarnings("SameParameterValue")

abstract class TextFileFilter {
	protected static String inputTextFile;
	String filterKey;
	String filterName;
	protected Map<String, Float> parameters;

	/* Public / Package methods */

	public String printParameters() {
		String result = "";

		result += "\n> Parameters for " + filterName + "\n";
		result += "identifier: " + filterKey + "\n\n";

		result += "Filter has " + parameters.size() + " parameters\n";
		for (Map.Entry<String, Float> entry : parameters.entrySet()) {
			result += entry.getKey() + " SET TO: " + entry.getValue() + "\n";
		}

		return result;
	}

	protected abstract void setParameters();

	@SuppressWarnings("WeakerAccess")

	protected void checkInputFiles(String inputTextFile) throws AudioAnalyser.IncorrectInputException {
		File fileInput = new File(inputTextFile);
		if (!fileInput.exists() || fileInput.isDirectory()) {
			throw new AudioAnalyser.IncorrectInputException("Input file " + inputTextFile + " does not exist");
		}
	}

	public String filter(String inputTextFile) throws IOException, AudioAnalyser.IncorrectInputException {
		String result = "";
		checkInputFiles(inputTextFile);
		result += "\nBeginning filter: " + filterKey + "\n";

		result += "Input file: " + inputTextFile + "\n";
		return result;
	}
}