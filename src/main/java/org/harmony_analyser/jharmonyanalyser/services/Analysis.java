package org.harmony_analyser.jharmonyanalyser.services;

import org.harmony_analyser.application.visualizations.VisualizationData;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for analysis (plugin / filter)
 */

@SuppressWarnings("SameParameterValue")

public abstract class Analysis {
	protected static List<String> inputFileSuffixes;
	protected String inputFileExtension;
	protected static String outputFileSuffix;
	protected String key;
	protected String name;
	protected Map<String, Float> parameters;

	/* Public / Package methods */

	@SuppressWarnings("WeakerAccess")
	public void checkInputFiles(String inputFile, boolean force) throws AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists {
		File file = new File(inputFile + outputFileSuffix + ".txt");
		if (file.exists() && !file.isDirectory() && !force) {
			throw new AudioAnalyser.OutputAlreadyExists("Output already exists");
		}
		for (String suffix : inputFileSuffixes) {
			String fileName = inputFile + suffix + inputFileExtension;
			File fileInput = new File(fileName);
			if (!fileInput.exists() || fileInput.isDirectory()) {
				throw new AudioAnalyser.IncorrectInputException("Input file " + fileName + " does not exist");
			}
		}
	}

	public List<String> getInputFileExtensions() {
		return inputFileSuffixes;
	}

	protected List<String> readOutputFile(String outputFile) throws AudioAnalyser.OutputNotReady, IOException {
		File file = new File(outputFile + outputFileSuffix + ".txt");
		if (!file.exists() || file.isDirectory()) {
			throw new AudioAnalyser.OutputNotReady("Output is not ready yet");
		}
		return Files.readAllLines(file.toPath(), Charset.defaultCharset());
	}

	public String printParameters() {
		String result = "";

		result += "\n> Parameters for " + name + "\n";
		result += "identifier: " + key + "\n\n";
		result += "number of inputs: " + inputFileSuffixes.size() + "\n";
		result += "expected extensions:\n";
		for (String suffix : inputFileSuffixes) {
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

	public String analyse(String inputFile, boolean force, boolean verbose) throws IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = "";
		checkInputFiles(inputFile, force);
		result += "\nBeginning analysis: " + key + "\n";

		result += "Input file(s):\n";
		for (String suffix : inputFileSuffixes) {
			String inputFileName = inputFile + suffix + inputFileExtension;
			result += inputFileName + "\n";
		}
		result += "\nOutput file:\n" + inputFile + outputFileSuffix + ".txt" + "\n";
		if (verbose) {
			result += "Verbose Output:\n" +inputFile + outputFileSuffix + ".txt" + "\n";
		}
		return result;
	}

	public abstract VisualizationData getDataFromOutput(String outputFile) throws IOException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError;
}
