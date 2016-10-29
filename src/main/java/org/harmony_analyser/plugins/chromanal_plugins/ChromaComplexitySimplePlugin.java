package org.harmony_analyser.plugins.chromanal_plugins;

import org.harmony_analyser.chromanal.*;
import java.util.*;

/**
 * Plugin for high-level audio analysis using chroma input, based on Chromanal model
 */

/*
 * ChromaComplexitySimplePlugin
 *
 * - requires: chroma
 * - complexity of transition = sum of absolute value of changes
 */

@SuppressWarnings("SameParameterValue")

public class ChromaComplexitySimplePlugin extends ChromaComplexityPlugin {
	public ChromaComplexitySimplePlugin() {
		pluginKey = "chromanal:chroma_complexity_simple";
		pluginName = "Simple Chroma Complexity";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas");
		inputFileExtension = ".txt";

		outputFileSuffix = "-chroma-complexity-simple";

		parameters = new HashMap<>();

		setParameters();
	}

	public float getChromaComplexity(Chroma previousChroma, Chroma chroma) {
		return Chromanal.getChromaComplexitySimple(previousChroma, chroma);
	}
}
