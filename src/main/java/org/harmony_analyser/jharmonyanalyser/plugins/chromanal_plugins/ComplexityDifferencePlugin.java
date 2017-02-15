package org.harmony_analyser.jharmonyanalyser.plugins.chromanal_plugins;

import org.harmony_analyser.jharmonyanalyser.chroma_analyser.*;
import java.util.*;

/**
 * Plugin for high-level audio analysis using chroma input, based on Chromanal model
 */

/*
 * ComplexityDifferencePlugin
 *
 * - requires: chroma
 * - complexity of transition = sum of absolute value of changes
 */

@SuppressWarnings("SameParameterValue")

public class ComplexityDifferencePlugin extends ChromaAnalyserPlugin {
	public ComplexityDifferencePlugin() {
		key = "chroma_analyser:complexity_difference";
		name = "Complexity Difference";
		description = "Derives complexity differences from the subsequent chroma vectors";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas");
		inputFileExtension = ".txt";

		outputFileSuffix = "-chroma-complexity-difference";

		parameters = new HashMap<>();
		parameters.put("audibleThreshold", (float) 0.98);

		setParameters();
	}

	public float getChromaComplexity(Chroma previousChroma, Chroma chroma) throws Chroma.WrongChromaSize {
		return Chromanal.getChromaComplexityTonal(previousChroma, chroma, audibleThreshold, false);
	}
}
