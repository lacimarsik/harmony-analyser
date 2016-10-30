package org.harmony_analyser.plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.application.visualizations.VisualizationData;
import org.harmony_analyser.chromanal.Chroma;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

/**
 * Abstract class for low and high-level audio analysis plugin
 */

@SuppressWarnings("SameParameterValue")

public abstract class AnalysisPlugin {
	protected static List<String> inputFileSuffixes;
	protected String inputFileExtension;
	protected static String outputFileSuffix;
	protected String pluginKey;
	protected String pluginName;
	protected Map<String, Float> parameters;

	/* Exceptions */

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

	public class ParseOutputError extends Exception {
		public ParseOutputError(String message) {
			super(message);
		}
	}


	/* Public / Package methods */

	@SuppressWarnings("WeakerAccess")

	protected void checkInputFiles(String inputFileWav, boolean force) throws AudioAnalyser.IncorrectInputException, OutputAlreadyExists {
		File file = new File(inputFileWav + outputFileSuffix + ".txt");
		if (file.exists() && !file.isDirectory() && !force) {
			throw new OutputAlreadyExists("Output already exists");
		}
		for (String suffix : inputFileSuffixes) {
			String fileName = inputFileWav + suffix + inputFileExtension;
			File fileInput = new File(fileName);
			if (!fileInput.exists() || fileInput.isDirectory()) {
				throw new AudioAnalyser.IncorrectInputException("Input file " + fileName + " does not exist");
			}
		}
	}

	public List<String> getInputFileExtensions() {
		return inputFileSuffixes;
	}

	protected List<String> readOutputFile(String outputFile) throws OutputNotReady, IOException {
		File file = new File(outputFile + outputFileSuffix + ".txt");
		if (!file.exists() || file.isDirectory()) {
			throw new OutputNotReady("Output is not ready yet");
		}
		return Files.readAllLines(file.toPath(), Charset.defaultCharset());
	}

	public String printParameters() {
		String result = "";

		result += "\n> Parameters for " + pluginName + "\n";
		result += "identifier: " + pluginKey + "\n\n";
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

	public String analyse(String inputFileWav, boolean force, boolean verbose) throws IOException, AudioAnalyser.IncorrectInputException, OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = "";
		checkInputFiles(inputFileWav, force);
		result += "\nBeginning analysis: " + pluginKey + "\n";

		result += "Input file(s):\n";
		for (String suffix : inputFileSuffixes) {
			String inputFileName = inputFileWav + suffix + inputFileExtension;
			result += inputFileName + "\n";
		}
		result += "\nOutput file:\n" + inputFileWav + outputFileSuffix + ".txt" + "\n";
		if (verbose) {
			result += "Verbose Output:\n" +inputFileWav + outputFileSuffix + ".txt" + "\n";
		}
		return result;
	}

	protected VisualizationData prepareVisualizationData() {
		VisualizationData visualizationData = new VisualizationData();
		visualizationData.setPluginName(pluginName);
		return visualizationData;
	}

	public abstract VisualizationData getDataFromOutput(String outputFile) throws IOException, OutputNotReady, ParseOutputError;
}
