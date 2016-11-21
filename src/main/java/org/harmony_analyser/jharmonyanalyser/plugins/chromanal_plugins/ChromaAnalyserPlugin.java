package org.harmony_analyser.jharmonyanalyser.plugins.chromanal_plugins;

import org.harmony_analyser.jharmonyanalyser.services.*;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.plugins.LineChartPlugin;

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

abstract class ChromaAnalyserPlugin extends LineChartPlugin {
	/**
	 * Analyzes the song: converts chroma information to chroma complexity descriptors
	 *
	 * @param inputFileWav [String] name of the WAV audio file
	 *    These additional files are expected in the folder
	 *    - chromaFile: name of the file containing chroma information (suffix: -chromas.txt)
	 */

	public String analyse(String inputFileWav, boolean force, boolean verbose) throws IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFileWav, force, verbose);
		String outputFile = inputFileWav + outputFileSuffix + ".txt";
		List<String> inputFiles = new ArrayList<>();
		for (String suffix : inputFileSuffixes) {
			String inputFileName = inputFileWav + suffix + inputFileExtension;
			inputFiles.add(inputFileName);
		}

		List<String> chromaLinesList = Files.readAllLines(new File(inputFiles.get(0)).toPath(), Charset.defaultCharset());

		float[] chromaArray;
		Chroma chroma;
		Chroma previousChroma = Chroma.EMPTY_CHROMA;
		float timestamp;
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

		// 1. Iterate over chromas, deriving complexities
		for (String line : chromaLinesList) {
			// Get chroma from the current line
			chromaArray = AudioAnalysisHelper.getChromaFromLine(line);
			timestamp = AudioAnalysisHelper.getTimestampFromLine(line);

			// Shift chroma for proper alignment for analysis
			// XXX: chromas from NNLS Chroma Vamp plugin start from A, chroma for Chordanal are starting from C)
			chromaArray = AudioAnalysisHelper.shiftChroma(chromaArray, 3);
			chroma = new Chroma(chromaArray);

			if (previousChroma.equals(Chroma.EMPTY_CHROMA)) {
				out.write(timestamp + ": 0\n");
			} else {
				out.write(timestamp + ": " + Float.toString((this.getChromaComplexity(previousChroma, chroma))) + "\n");
			}

			previousChroma = chroma;
		}

		out.close();

		return result;
	}

	protected abstract float getChromaComplexity(Chroma previousChroma, Chroma chroma) throws Chroma.WrongChromaSize;

	protected void setParameters() {
		// No parameters present
	}

	/* Private methods */
}
