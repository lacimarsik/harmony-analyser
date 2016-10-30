package org.harmony_analyser.plugins.chordanal_plugins;

import java.util.*;

/**
 * Plugin for high-level audio analysis using chroma / chord transcription input, based on Chordanal model
 */

/*
 * TonalDistancePlugin
 *
 * - requires: chroma, segmentation
 * - Averages chroma in each segment
 * - Selects the tones with the biggest activation, based on audibleThreshold
 * - Names the chord using Chordanal
 * - derives tonal distance for each tuple of subsequent chords
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

public class TonalDistancePlugin extends ChordanalPlugin {
	public TonalDistancePlugin() {
		pluginKey = "chordanal:tonal_distance";
		pluginName = "Tonal Distance";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas");
		inputFileSuffixes.add("-chordino");
		inputFileExtension = ".txt";

		outputFileSuffix = "-tonal-distance";

		parameters = new HashMap<>();
		parameters.put("audibleThreshold", (float) 0.07);
		parameters.put("maximumNumberOfChordTones", (float) 4.0);
		parameters.put("maximalComplexity", (float) 7.0);

		setParameters();
	}

	protected String getTransitionOutput(float timestamp, int transitionComplexity) {
		return timestamp + ": " + transitionComplexity + "\n";
	}

	protected String getFinalResult(float hc, float acc, float rtd) {
		return ""; // This plugin doesn't provide final result
	}
}
