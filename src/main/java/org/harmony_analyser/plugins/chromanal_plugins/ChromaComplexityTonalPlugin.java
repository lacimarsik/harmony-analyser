package org.harmony_analyser.plugins.chromanal_plugins;

import org.harmony_analyser.chromanal.*;
import java.util.*;

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

public class ChromaComplexityTonalPlugin extends ChromaComplexityPlugin {
	public ChromaComplexityTonalPlugin() {
		pluginKey = "chromanal:chroma_complexity_tonal";
		pluginName = "Tonal Chroma Complexity";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas.txt");

		outputFileSuffix = "-chroma-complexity-tonal.txt";

		parameters = new HashMap<>();

		setParameters();
	}

	public float getChromaComplexity(Chroma previousChroma, Chroma chroma) throws Chroma.WrongChromaSize {
		return Chromanal.getChromaComplexityTonal(previousChroma, chroma);
	}
}
