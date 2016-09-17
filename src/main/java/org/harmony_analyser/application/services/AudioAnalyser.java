package org.harmony_analyser.application.services;

import org.harmony_analyser.application.visualizations.*;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.*;
import org.vamp_plugins.PluginLoader;

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
		String[] all_visualizations = new String[drawPanelFactory.getStaticVisualizations().length + drawPanelFactory.getVisualPlugins().length];
		System.arraycopy(drawPanelFactory.getStaticVisualizations(), 0, all_visualizations, 0, drawPanelFactory.getStaticVisualizations().length);
		System.arraycopy(drawPanelFactory.getVisualPlugins(), 0, all_visualizations, drawPanelFactory.getStaticVisualizations().length, drawPanelFactory.getVisualPlugins().length);
		return all_visualizations;
	}

	public String printPlugins() {
		String result = "";
		result += "\n> Available plugins (" + analysisPluginFactory.getAvailablePlugins().length + "):\n";

		for (String availablePluginKey : analysisPluginFactory.getAvailablePlugins()) {
			result += availablePluginKey + "\n";
		}

		result += "\n> Available visualizations (" + drawPanelFactory.getVisualPlugins().length + "):\n";

		for (String availablePluginKey : drawPanelFactory.getVisualPlugins()) {
			result += availablePluginKey + "\n";
		}

		result += printInstalledVampPlugins();

		return result;
	}

	public String printInstalledVampPlugins() {
		String result = "";
		String[] plugins = PluginLoader.getInstance().listPlugins();
		result += "\n> Locally installed VAMP plugins (" + plugins.length + "):\n";
		for (int i = 0; i < plugins.length; ++i) {
			result += i + ": " + plugins[i] + "\n";
		}

		List<String> wrappedPlugins = new ArrayList<>();
		for (int i = 0; i < plugins.length; ++i) {
			for (String wrapped_plugin : analysisPluginFactory.getWrappedVampPlugins()) {
				if (plugins[i].equals(wrapped_plugin)) {
					wrappedPlugins.add(i + ": " + plugins[i] + "\n");
				}
			}
		}
		result += "\n> Implemented VAMP plugins (" + wrappedPlugins.size() + "):\n";
		for (String s : wrappedPlugins) {
			result += s;
		}
		return result;
	}

	public String printParameters(String pluginKey) throws LoadFailedException {
		return analysisPluginFactory.createPlugin(pluginKey).printParameters();
	}

	public String runAnalysis(String inputFile, String pluginKey, boolean force) throws AnalysisPlugin.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, IOException, LoadFailedException, Chroma.WrongChromaSize {
		if (Arrays.asList(drawPanelFactory.getStaticVisualizations()).contains(pluginKey)) {
			return "\nPerforming static visualization: (" + pluginKey + ")\n";
		} else {
			AnalysisPlugin plugin = analysisPluginFactory.createPlugin(pluginKey);
			return plugin.analyse(inputFile, force);
		}
	}
}
