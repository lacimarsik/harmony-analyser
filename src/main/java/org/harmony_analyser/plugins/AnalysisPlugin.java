package org.harmony_analyser.plugins;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

/**
 * Abstract class for low and high-level audio analysis plugin
 */

@SuppressWarnings("SameParameterValue")

public abstract class AnalysisPlugin {
	protected static List<String> inputFileExtensions;
	protected static String outputFileExtension;
	protected String pluginKey;
	protected String pluginName;
	protected Map<String, Float> parameters;

	/* Exceptions */

	public class IncorrectInputException extends Exception {
		public IncorrectInputException(String message) {
			super(message);
		}
	}

	public class OutputAlreadyExists extends Exception {
		public OutputAlreadyExists(String message) {
			super(message);
		}
	}

	public class OutputNotReady extends Exception {
		OutputNotReady(String message) {
			super(message);
		}
	}

	/* Public / Package methods */

	@SuppressWarnings("WeakerAccess")

	protected void checkInputFiles(String inputFile, boolean force) throws IncorrectInputException, OutputAlreadyExists {
		File file = new File(inputFile + outputFileExtension);
		if (file.exists() && !file.isDirectory() && !force) {
			throw new OutputAlreadyExists("Output already exists");
		}
		for (String inputFileExtension : inputFileExtensions) {
			String fileName = inputFile + inputFileExtension;
			File fileInput = new File(inputFile + inputFileExtension);
			if (!fileInput.exists() || fileInput.isDirectory()) {
				throw new IncorrectInputException("Input file " + fileName + " does not exist");
			}
		}
	}

	public List<String> getInputFileExtensions() {
		return inputFileExtensions;
	}

	public List<String> getResultFromFile(String inputFile) throws OutputNotReady, IOException {
		File file = new File(inputFile + outputFileExtension);
		if (!file.exists() || file.isDirectory()) {
			throw new OutputNotReady("Output is not ready yet");
		}
		return Files.readAllLines(file.toPath(), Charset.defaultCharset());
	}

	public String printParameters() {
		String result = "";

		result += "\n> Parameters for " + pluginName + "\n";
		result += "identifier: " + pluginKey + "\n\n";
		result += "number of inputs: " + inputFileExtensions.size() + "\n";
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

	public String analyse(String inputFile, boolean force) throws IOException, IncorrectInputException, OutputAlreadyExists {
		checkInputFiles(inputFile, force);
		return "\nBeginning analysis: " + pluginKey + "\n";
	}
}
