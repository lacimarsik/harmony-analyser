package org.harmony_analyser.plugins;

import java.io.IOException;
import java.util.*;

/**
 * Abstract class for low and high-level audio analysis plugin
 */

public abstract class AnalysisPlugin {
	protected static List<String> inputFileExtensions;
	protected String pluginKey;
	protected String pluginName;
	protected Map<String, Float> parameters;

	public class IncorrectInput extends Exception {
		public IncorrectInput(String message) {
			super(message);
		}
	}

	public List<String> getInputFileExtensions() {
		return inputFileExtensions;
	}

	protected void checkInputFiles(List<String> inputFiles) throws IncorrectInput {
		if (inputFiles.size() != inputFileExtensions.size()) {
			throw new IncorrectInput("Wrong number of input files, expected " + inputFileExtensions.size());
		}
		boolean correctInput;
		for (String inputFile : inputFiles) {
			correctInput = false;
			for (String suffix : inputFileExtensions) {
				if (inputFile.endsWith(suffix)) {
					correctInput = true;
				}
			}
			if (!correctInput) {
				throw new IncorrectInput("Input file " + inputFile + " does not have the required extension");
			}
		}
	}

	public String printParameters() {
		String result = "";

		result += "\n> Parameters for " + pluginName + "\n";
		result += "identifier: " + pluginKey + "\n\n";
		result += "number of inputs: " + inputFileExtensions.size();
		result += "expected extensions:\n";
		for (String suffix : inputFileExtensions) {
			result += "  " + suffix + "\n";
		}
		result += "\n";

		result += "Plugin has " + parameters.size() + " parameters\n";
		for (Map.Entry<String, Float> entry : parameters.entrySet()) {
			result += entry.getKey() + " SET TO: " + entry.getValue() + "\n";
		}

		return result;
	}

	protected abstract void setParameters();

	public abstract String analyse(List<String> inputFiles, String outputFile) throws IOException, IncorrectInput;
}
