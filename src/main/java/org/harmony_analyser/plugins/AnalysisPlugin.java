package org.harmony_analyser.plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.application.visualizations.VisualizationData;
import org.harmony_analyser.chromanal.Chroma;
import org.vamp_plugins.PluginLoader;

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

	public class ParseOutputError extends Exception {
		public ParseOutputError(String message) {
			super(message);
		}
	}


	/* Public / Package methods */

	@SuppressWarnings("WeakerAccess")

	protected void checkInputFiles(String inputFileWav, boolean force) throws IncorrectInputException, OutputAlreadyExists {
		File file = new File(inputFileWav + outputFileSuffix + ".txt");
		if (file.exists() && !file.isDirectory() && !force) {
			throw new OutputAlreadyExists("Output already exists");
		}
		for (String suffix : inputFileSuffixes) {
			String fileName = inputFileWav + suffix + inputFileExtension;
			File fileInput = new File(fileName);
			if (!fileInput.exists() || fileInput.isDirectory()) {
				throw new IncorrectInputException("Input file " + fileName + " does not exist");
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

	public String analyse(String inputFileWav, boolean force, boolean verbose) throws IOException, IncorrectInputException, OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = "";
		checkInputFiles(inputFileWav, force);
		result += "\nBeginning analysis: " + pluginKey + "\n";

		result += "Input file(s):\n";
		for (String suffix : inputFileSuffixes) {
			String inputFileName = inputFileWav + suffix + ".txt";
			result += inputFileName + "\n";
		}
		result += "\nOutput file:\n" + inputFileWav + outputFileSuffix + ".txt" + "\n";
		if (verbose) {
			result += "Verbose Output:\n" +inputFileWav + outputFileSuffix + ".txt" + "\n";
		}
		return result;
	}

	public VisualizationData getDataFromOutput(String outputFile) throws IOException, AudioAnalyser.LoadFailedException, OutputNotReady, ParseOutputError, PluginLoader.LoadFailedException {
		VisualizationData visualizationData = new VisualizationData();
		visualizationData.setPluginName(pluginName);
		return visualizationData;
	}

	/* Helpers */

	// Read chroma information from the line of String
	protected float[] getChromaFromLine(String line) throws IncorrectInputException {
		float[] result = new float[12];
		Scanner sc = new Scanner(line);
		sc.next(); // skip timestamp
		for (int i = 0; i < 12; i++) {
			if (sc.hasNextFloat()) {
				result[i] = sc.nextFloat();
			} else {
				throw new IncorrectInputException("Chroma information is invalid.");
			}
		}
		return result;
	}

	// Shifts chroma a step semitones up
	protected float[] shiftChroma(float[] chroma, int step) {
		float[] result = new float[12];
		if (step < 0) {
			step = 12 - step;
		}
		for (int i = 0; i < 12; i++) {
			result[i] = chroma[(i + step) % 12];
		}
		return result;
	}
}
