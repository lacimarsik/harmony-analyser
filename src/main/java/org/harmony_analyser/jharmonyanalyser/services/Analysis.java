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
	public boolean verbose = false; // Verbose output on/off
	public String inputWavFile = ""; // inputWavFile is set with the analysis and remembered after. It is the name of the song.
	public String inputTitle = ""; // inputTitle is set with the analysis and remembered after. It is the name of the song without extension.
	public List<String> inputFiles = null; // inputFiles are set with the analysis and remembered after. They are the dependency files needed around the WAV file.
	public String outputFile = ""; // outputFile is set with the analysis and remembered after.

	protected static List<String> inputFileSuffixes;
	protected String inputFileExtension;
	protected static String outputFileSuffix;
	protected String outputFileExtension;
	protected String key;
	protected String name;
	protected String description;
	protected Map<String, Float> parameters;

	/* Public / Package methods */

	public void setFiles(String wavFile, boolean force) throws AudioAnalyser.OutputAlreadyExists, AudioAnalyser.IncorrectInputException {
		inputWavFile = wavFile;
		setTitle(inputWavFile);
		setOutputFile(inputTitle, force);
		checkInputFiles(inputTitle);
	}

	public void setTitle(String inputWavFile) {
		inputTitle = inputWavFile.substring(0, inputWavFile.lastIndexOf('.'));
	}

	public void setOutputFile(String inputTitle, boolean force) throws AudioAnalyser.OutputAlreadyExists {
		outputFile = inputTitle + outputFileSuffix + outputFileExtension;
		File file = new File(outputFile);
		if (file.exists() && !file.isDirectory() && !force) {
			throw new AudioAnalyser.OutputAlreadyExists("Output already exists");
		}
	}

	@SuppressWarnings("WeakerAccess")
	public void checkInputFiles(String inputTitle) throws AudioAnalyser.IncorrectInputException {
		for (String suffix : inputFileSuffixes) {
			String fileName = inputTitle + suffix + inputFileExtension;
			inputFiles.add(fileName);
			File fileInput = new File(fileName);
			if (!fileInput.exists() || fileInput.isDirectory()) {
				throw new AudioAnalyser.IncorrectInputException("Input file " + fileName + " does not exist");
			}
		}
	}

	public List<String> getInputFileExtensions() {
		return inputFileSuffixes;
	}

	protected List<String> readOutputFile(String inputWavFile) throws AudioAnalyser.OutputNotReady, IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists {
		setFiles(inputWavFile, false);
		File file = new File(outputFile);
		if (!file.exists() || file.isDirectory()) {
			throw new AudioAnalyser.OutputNotReady("Output is not ready yet");
		}
		return Files.readAllLines(file.toPath(), Charset.defaultCharset());
	}

	public String printParameters() {
		String result = "";

		result += "\n> Parameters for Analysis: " + name + "\n";
		result += "identifier: " + key + "\n\n";
		result += "number of inputs: " + inputFileSuffixes.size() + "\n";
		result += "expected extensions:\n";
		for (String suffix : inputFileSuffixes) {
			result += "  " + suffix + "\n";
		}
		result += "\n";

		result += "Analysis has " + parameters.size() + " parameters\n";
		for (Map.Entry<String, Float> entry : parameters.entrySet()) {
			result += entry.getKey() + " SET TO: " + entry.getValue() + "\n";
		}

		return result;
	}

	protected abstract void setParameters();

	public String analyse(String inputWavFile, boolean force) throws IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = "";
		setFiles(inputWavFile, force);
		result += "\nBeginning analysis: " + key + "\n";

		result += "Input file(s):\n";
		for (String inputFile : inputFiles) {
			result += inputFile + "\n";
		}
		result += "\nOutput file:\n" + outputFile + "\n";

		return result;
	}

	public void verboseLog(String message) {
		if (verbose) {
			System.out.println("(Verbose) " + message);
		}
	}

	public abstract VisualizationData getDataFromOutput(String inputWavFile) throws IOException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists;
}
