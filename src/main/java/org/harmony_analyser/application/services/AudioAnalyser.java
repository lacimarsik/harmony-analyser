package org.harmony_analyser.application.services;

import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.*;

import java.io.IOException;
import java.util.*;

/**
 * Class to direct all levels of audio analysis, using available plugins
 */

public class AudioAnalyser {
	public class LoadFailedException extends Exception {
		LoadFailedException(String message) {
			super(message);
		}
	}

	private static final String[] AVAILABLE_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino",
		"harmanal:transition_complexity"
	};

	/* Public / Package methods */

	public String runAnalysis(List<String> inputFiles, String outputFile, String pluginKey) throws LoadFailedException, AnalysisPlugin.IncorrectInputException, IOException {
		AnalysisPlugin plugin = getPlugin(pluginKey);

		return plugin.analyse(inputFiles, outputFile);
	}

	/* Private methods */

	private AnalysisPlugin getPlugin(String pluginKey) throws LoadFailedException {
		AnalysisPlugin plugin;
		if (!Arrays.asList(AVAILABLE_PLUGINS).contains(pluginKey)) {
			throw new LoadFailedException("Plugin with key " + pluginKey + " is not available");
		}

		try {
			switch (pluginKey) {
				case "nnls-chroma:nnls-chroma":
					plugin = new NNLSPlugin();
					break;
				case "nnls-chroma:chordino":
					plugin = new ChordinoPlugin();
					break;
				case "harmanal:transition_complexity":
					plugin = new TransitionComplexityPlugin();
					break;
				default:
					throw new LoadFailedException("Plugin with key " + pluginKey + " is not available");
			}
		} catch (PluginLoader.LoadFailedException e) {
			throw new LoadFailedException(e.getMessage());
		}
		return plugin;
	}
}