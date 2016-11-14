package org.harmony_analyser.plugins.chromanal_plugins;

import org.harmony_analyser.chromanal.*;
import java.util.*;

/**
 * Plugin for high-level audio analysis using chroma input, based on Chromanal model
 */

/*
 * TonalDifferencePlugin
 *
 * - requires: chroma
 * - complexity of transition = sum of absolute value of changes
 */

@SuppressWarnings("SameParameterValue")

public class TonalDifferencePlugin extends ChromaAnalyserPlugin {
	public TonalDifferencePlugin() {
		pluginKey = "chroma_analyser:tonal_difference";
		pluginName = "Tonal Difference";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas");
		inputFileExtension = ".txt";

		outputFileSuffix = "-chroma-complexity-tonal";

		parameters = new HashMap<>();

		setParameters();
	}

	public float getChromaComplexity(Chroma previousChroma, Chroma chroma) throws Chroma.WrongChromaSize {
		return Chromanal.getChromaComplexityTonal(previousChroma, chroma, false);
	}
}
