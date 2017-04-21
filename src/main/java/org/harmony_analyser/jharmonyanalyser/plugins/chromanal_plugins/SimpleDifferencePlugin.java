package org.harmony_analyser.jharmonyanalyser.plugins.chromanal_plugins;

import org.harmony_analyser.jharmonyanalyser.chroma_analyser.*;
import java.util.*;

/**
 * Plugin for high-level audio analysis using chroma input, based on Chromanal model
 */

/*
 * SimpleDifferencePlugin
 *
 * - requires: chroma
 * - complexity of transition = sum of absolute value of changes
 */

@SuppressWarnings("SameParameterValue")

public class SimpleDifferencePlugin extends ChromaAnalyserPlugin {
	public SimpleDifferencePlugin() {
		key = "chroma_analyser:simple_difference";
		name = "Simple Difference";
		description = "Derives simple differences from the subsequent chroma vectors";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas");
		inputFileExtension = ".txt";

		outputFileSuffix = "-chroma-simple-difference";

		parameters = new HashMap<>();

		setParameters();
	}

	public float getChromaComplexity(Chroma previousChroma, Chroma chroma) {
		return Chromanal.getChromaComplexitySimple(previousChroma, chroma);
	}

	@Override
	protected void setParameters() {
		// No parameters present for this plugin
	}
}
