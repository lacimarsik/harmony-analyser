package org.harmony_analyser.application.services;

import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.harmony_analyser.application.*;
import org.vamp_plugins.*;

import java.io.*;
import java.util.*;

/**
 * Class to direct all levels of audio analysis, using available plugins
 */

public class AudioAnalyser {
	public static class LoadFailedException extends Exception {
		LoadFailedException(String message) {
			super(message);
		}
	}

	private static final String[] AVAILABLE_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino",
		"harmanal:transition_complexity"
	};

	private static final String[] VISUAL_PLUGINS = new String[] {
		"nnls-chroma:chordino",
		"harmanal:transition_complexity"
	};

	/* Public / Package methods */

	public static String[] getVisualPlugins() {
		return VISUAL_PLUGINS;
	}

	public static String printPlugins() {
		String result = "";
		result += "\n> Available plugins (" + AVAILABLE_PLUGINS.length + "):\n";

		for (String availablePluginKey : AVAILABLE_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += "\n> Available visualizations (" + VISUAL_PLUGINS.length + "):\n";

		for (String availablePluginKey : VISUAL_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += VampPlugin.printInstalledVampPlugins();

		return result;
	}

	public static String printParameters(String pluginKey) throws LoadFailedException {
		return getPlugin(pluginKey).printParameters();
	}

	public String runAnalysis(String inputFile, String pluginKey, boolean force) throws AnalysisPlugin.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, IOException, LoadFailedException {
		AnalysisPlugin plugin = getPlugin(pluginKey);

		return plugin.analyse(inputFile, force);
	}

	public DrawPanel getDrawPanel(String inputFile, String pluginKey) throws IOException, LoadFailedException, AnalysisPlugin.OutputNotReady {
		switch (pluginKey) {
			case "nnls-chroma:chordino":
				return new SegmentationDrawPanel(inputFile);
			case "harmanal:transition_complexity":
				return new ComplexityChartDrawPanel(inputFile);
			default:
				return null;
		}
	}

	/* Private methods */

	private static AnalysisPlugin getPlugin(String pluginKey) throws LoadFailedException {
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
