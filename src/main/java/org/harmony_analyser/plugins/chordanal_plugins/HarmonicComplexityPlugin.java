package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.application.visualizations.VisualizationData;
import org.vamp_plugins.PluginLoader;

import java.io.*;
import java.util.*;

/**
 * Plugin for high-level audio analysis using chroma / chord transcription input, based on Chordanal model
 */

/*
 * HarmonicComplexityPlugin
 *
 * - requires: chroma, segmentation
 * - Averages chroma in each segment
 * - Selects the tones with the biggest activation, based on audibleThreshold
 * - Names the chord using Chordanal
 * - derives tonal distance for each tuple of subsequent chords
 * - calculates average tonal distance (= harmonic complexity), average chord complexity, and relative tonal distance
 *
 * parameters
 * - threshold for audible tones (< 0.05 yields 4+ tones in each chord, > 0.1 yields to 2 and less tones in each chord)
 * -- preferred: 0.07
 * - maximum number of chord tones - used to simplify computation which is exponential to the number of chord tones
 * -- preferred: 4.0
 * - maximal complexity - is assigned when transition complexity cannot be found, as a penalization constant
 * -- preferred: 7.0
 */

@SuppressWarnings("SameParameterValue")

public class HarmonicComplexityPlugin extends ChordanalPlugin {
	private final static int NUMBER_OUTPUTS = 3;

	public HarmonicComplexityPlugin() {
		pluginKey = "chordanal:harmonic_complexity";
		pluginName = "Harmonic Complexity";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas");
		inputFileSuffixes.add("-chordino-labels");
		inputFileExtension = ".txt";

		outputFileSuffix = "-harmonic-complexity";

		parameters = new HashMap<>();
		parameters.put("audibleThreshold", (float) 0.07);
		parameters.put("maximumNumberOfChordTones", (float) 4.0);
		parameters.put("maximalComplexity", (float) 7.0);

		setParameters();
	}

	@Override
	public VisualizationData getDataFromOutput(String outputFile) throws IOException, OutputNotReady, ParseOutputError {
		VisualizationData data = super.prepareVisualizationData();
		List<Float> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> linesList = readOutputFile(outputFile);

		/* Plugin-specific parsing of the result */
		// get last NUMBER_OUTPUTS lines
		List<String> tail = linesList.subList(Math.max(linesList.size() - NUMBER_OUTPUTS, 0), linesList.size());
		for (String line : tail) {
			Scanner sc = new Scanner(line).useDelimiter("\\s*:\\s*");
			labels.add(sc.next());
			if (sc.hasNextFloat()) {
				values.add(sc.nextFloat());
			} else {
				throw new ParseOutputError("Output did not have the required fields");
			}
			sc.close();
		}
		data.setValues(values);
		data.setLabels(labels);
		return data;
	}

	protected String getTransitionOutput(float timestamp, int transitionComplexity) {
		return ""; // This plugin doesn't provide transition output
	}

	protected String getFinalResult(float hc, float acc, float rtd) {
		return "Harmonic Complexity (HC) = Average Tonal Distance (ATD): " + hc + "\n" +
			"Average Chord Complexity (ACC): " + acc + "\n" +
			"Relative Tonal Distance (RTD): " + rtd + "\n";
	}
}
