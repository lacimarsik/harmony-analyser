package org.harmony_analyser.application.services;

import org.harmony_analyser.application.visualizations.*;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;

import java.io.*;
import java.util.*;

/**
 * Class to direct all levels of audio analysis, using available plugins and visualizations
 */

public class AudioAnalyser {
	private final AnalysisPluginFactory analysisPluginFactory;
	private final DrawPanelFactory drawPanelFactory;

	public static class LoadFailedException extends Exception {
		public LoadFailedException(String message) {
			super(message);
		}
	}

	// Dependency injection: AnalysisPluginFactory, DrawPanelFactory
	public AudioAnalyser(AnalysisPluginFactory analysisPluginFactory, DrawPanelFactory drawPanelFactory) {
		this.analysisPluginFactory = analysisPluginFactory;
		this.drawPanelFactory = drawPanelFactory;
	}

	/* Public / Package methods */

	public String[] getVisualPlugins() {
		String[] all_visualizations = new String[drawPanelFactory.STATIC_VISUALIZATIONS.length + drawPanelFactory.VISUAL_PLUGINS.length];
		System.arraycopy(drawPanelFactory.STATIC_VISUALIZATIONS, 0, all_visualizations, 0, drawPanelFactory.STATIC_VISUALIZATIONS.length);
		System.arraycopy(drawPanelFactory.VISUAL_PLUGINS, 0, all_visualizations, drawPanelFactory.STATIC_VISUALIZATIONS.length, drawPanelFactory.VISUAL_PLUGINS.length);
		return all_visualizations;
	}

	public String printPlugins() {
		String result = "";
		result += "\n> Available plugins (" + analysisPluginFactory.AVAILABLE_PLUGINS.length + "):\n";

		for (String availablePluginKey : analysisPluginFactory.AVAILABLE_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += "\n> Available visualizations (" + drawPanelFactory.VISUAL_PLUGINS.length + "):\n";

		for (String availablePluginKey : drawPanelFactory.VISUAL_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += VampPlugin.printInstalledVampPlugins();

		return result;
	}

	public String printParameters(String pluginKey) throws LoadFailedException {
		return analysisPluginFactory.createPlugin(pluginKey).printParameters();
	}

	public String runAnalysis(String inputFile, String pluginKey, boolean force) throws AnalysisPlugin.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, IOException, LoadFailedException, Chroma.WrongChromaSize {
		if (Arrays.asList(drawPanelFactory.STATIC_VISUALIZATIONS).contains(pluginKey)) {
			return "\nPerforming static visualization: (" + pluginKey + ")\n";
		} else {
			AnalysisPlugin plugin = analysisPluginFactory.createPlugin(pluginKey);
			return plugin.analyse(inputFile, force);
		}
	}
}
