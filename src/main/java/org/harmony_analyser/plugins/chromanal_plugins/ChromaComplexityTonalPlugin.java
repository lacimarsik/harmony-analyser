package org.harmony_analyser.plugins.chromanal_plugins;

import org.harmony_analyser.application.services.AudioAnalysisHelper;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.chromanal.Chromanal;
import org.harmony_analyser.plugins.AnalysisPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Plugin for high-level audio analysis using chroma input, based on Chromanal model
 */

/*
 * ChromaComplexityTonalPlugin
 *
 * - requires: chroma
 * - complexity of transition = sum of absolute value of changes
 */

@SuppressWarnings("SameParameterValue")

public class ChromaComplexityTonalPlugin extends AnalysisPlugin {
	public ChromaComplexityTonalPlugin() {
		pluginKey = "chromanal:chroma_complexity_tonal";
		pluginName = "Tonal Chroma Complexity";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas.txt");

		outputFileSuffix = "-chroma-complexity-tonal.txt";

		parameters = new HashMap<>();

		setParameters();
	}

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
				out.write(timestamp + ": " + Float.toString((Chromanal.getChromaComplexityTonal(previousChroma, chroma))) + "\n");
			}

			previousChroma = chroma;
		}

		out.close();

		return result;
	}

	protected void setParameters() {
		// No parameters present
	}

	/* Private methods */
}
