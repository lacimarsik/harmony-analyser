package org.harmony_analyser.plugins.chromanal_plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.application.services.AudioAnalysisHelper;
import org.harmony_analyser.application.visualizations.VisualizationData;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.AnalysisPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for chrama complexity plugins
 */

@SuppressWarnings("SameParameterValue")

abstract class ChromaComplexityPlugin extends AnalysisPlugin {
	/**
	 * Analyzes the song: converts chroma information to chroma complexity descriptors
	 *
	 * @param inputFile [String] name of the WAV audio file
	 *    These additional files are expected in the folder
	 *    - chromaFile: name of the file containing chroma information (suffix: -chromas.txt)
	 */

	public String analyse(String inputFile, boolean force) throws IOException, IncorrectInputException, OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFile, force);
		String outputFile = inputFile + outputFileSuffix;

		String chromaFile = inputFile + "-chromas.txt";

		result += "Chroma file: " + chromaFile + "\n";
		result += "Output: " + outputFile + "\n";

		List<String> chromaLinesList = Files.readAllLines(new File(chromaFile).toPath(), Charset.defaultCharset());

		float[] chromaArray;
		Chroma chroma;
		Chroma previousChroma = null;
		float timestamp;
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

		// 1. Iterate over chromas, deriving complexities
		for (String line : chromaLinesList) {
			// Get chroma from the current line
			chromaArray = getChromaFromLine(line);
			timestamp = AudioAnalysisHelper.getTimestampFromLine(line);

			// Shift chroma for proper alignment for analysis
			// XXX: chromas from NNLS Chroma Vamp plugin start from A, chroma for Chordanal are starting from C)
			chromaArray = shiftChroma(chromaArray, 3);
			chroma = new Chroma(chromaArray);

			if (previousChroma == null) {
				out.write(timestamp + ": 0\n");
			} else {
				out.write(timestamp + ": " + Float.toString((this.getChromaComplexity(previousChroma, chroma))) + "\n");
			}

			previousChroma = chroma;
		}

		out.close();

		return result;
	}

	public VisualizationData getDataFromOutput(String outputFile) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, AnalysisPlugin.ParseOutputError {
		VisualizationData data = new VisualizationData();
		List<Float> timestamps = new ArrayList<>();
		List<Float> values = new ArrayList<>();
		List<String> linesList = readOutputFile(outputFile);

		float timestamp, value;

		/* Plugin-specific parsing of the result */
		try {
			for (String line : linesList) {
				timestamp = AudioAnalysisHelper.getTimestampFromLine(line);
				value = Float.parseFloat(AudioAnalysisHelper.getLabelFromLine(line));
				timestamps.add(timestamp);
				values.add(value);
			}
		} catch (NumberFormatException e) {
			throw new ParseOutputError("Output did not have the required fields");
		}
		data.setTimestamps(timestamps);
		data.setValues(values);
		return data;
	}

	protected abstract float getChromaComplexity(Chroma previousChroma, Chroma chroma) throws Chroma.WrongChromaSize;

	protected void setParameters() {
		// No parameters present
	}

	/* Private methods */
}
