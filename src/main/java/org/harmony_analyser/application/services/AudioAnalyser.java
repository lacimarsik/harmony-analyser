package org.harmony_analyser.application.services;

import org.harmony_analyser.application.visualizations.*;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.*;

import java.io.*;
import java.util.*;

/**
 * Class to direct all levels of audio analysis, using available plugins
 */

public class AudioAnalyser {
	private final AnalysisPluginFactory analysisPluginFactory;

	// Dependency injection: AnalysisPluginFactory
	public AudioAnalyser(AnalysisPluginFactory analysisPluginFactory) {
		this.analysisPluginFactory = analysisPluginFactory;
	}

	public static class LoadFailedException extends Exception {
		public LoadFailedException(String message) {
			super(message);
		}
	}

	private final String[] VISUAL_PLUGINS = new String[] {
		"nnls-chroma:chordino",
		"harmanal:transition_complexity",
		"chromanal:chroma_complexity_simple",
		"chromanal:chroma_complexity_tonal"
	};

	private final String[] STATIC_VISUALIZATIONS = new String[] {
		"chord_palette"
	};

	/* Public / Package methods */

	public String[] getVisualPlugins() {
		String[] all_visualizations = new String[STATIC_VISUALIZATIONS.length + VISUAL_PLUGINS.length];
		System.arraycopy(STATIC_VISUALIZATIONS, 0, all_visualizations, 0, STATIC_VISUALIZATIONS.length);
		System.arraycopy(VISUAL_PLUGINS, 0, all_visualizations, STATIC_VISUALIZATIONS.length, VISUAL_PLUGINS.length);
		return all_visualizations;
	}

	public String printPlugins() {
		String result = "";
		result += "\n> Available plugins (" + analysisPluginFactory.AVAILABLE_PLUGINS.length + "):\n";

		for (String availablePluginKey : analysisPluginFactory.AVAILABLE_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += "\n> Available visualizations (" + VISUAL_PLUGINS.length + "):\n";

		for (String availablePluginKey : VISUAL_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += VampPlugin.printInstalledVampPlugins();

		return result;
	}

	public String printParameters(String pluginKey) throws LoadFailedException {
		return analysisPluginFactory.createPlugin(pluginKey).printParameters();
	}

	public String runAnalysis(String inputFile, String pluginKey, boolean force) throws AnalysisPlugin.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, IOException, LoadFailedException, Chroma.WrongChromaSize {
		if (Arrays.asList(STATIC_VISUALIZATIONS).contains(pluginKey)) {
			return "\nPerforming static visualization: (" + pluginKey + ")\n";
		} else {
			AnalysisPlugin plugin = analysisPluginFactory.createPlugin(pluginKey);
			return plugin.analyse(inputFile, force);
		}
	}

	public DrawPanel getDrawPanel(String inputFile, String pluginKey) throws IOException, LoadFailedException, AnalysisPlugin.OutputNotReady, DrawPanel.CannotVisualize, PluginLoader.LoadFailedException {
		switch (pluginKey) {
			case "nnls-chroma:chordino":
				return new SegmentationDrawPanel(inputFile);
			case "chord_palette":
				return new PaletteDrawPanel(inputFile);
			case "harmanal:transition_complexity":
				return new ComplexityChartDrawPanel(inputFile);
			case "chromanal:chroma_complexity_simple":
				return new ChromaDrawPanel(inputFile, "Simple");
			case "chromanal:chroma_complexity_tonal":
				return new ChromaDrawPanel(inputFile, "Tonal");
			default:
				return null;
		}
	}
}
