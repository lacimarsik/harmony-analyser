package org.harmony_analyser.jharmonyanalyser.services;

import org.harmony_analyser.application.visualizations.*;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.vamp_plugins.PluginLoader;

import java.io.*;
import java.util.*;

/**
 * Class to direct all levels of audio analysis, using available plugins and visualizations
 */

@SuppressWarnings("SameParameterValue")
public class AudioAnalyser {
	private final AnalysisPluginFactory analysisPluginFactory;
	private final DrawPanelFactory drawPanelFactory;

	public static class IncorrectInputException extends Exception {
		public IncorrectInputException(String message) {
			super(message);
		}
	}

	public static class LoadFailedException extends Exception {
		public LoadFailedException(String message) {
			super(message);
		}
	}

	public static class OutputAlreadyExists extends Exception {
		public OutputAlreadyExists(String message) {
			super(message);
		}
	}

	public static class OutputNotReady extends Exception {
		OutputNotReady(String message) {
			super(message);
		}
	}

	public static class ParseOutputError extends Exception {
		public ParseOutputError(String message) {
			super(message);
		}
	}


	// Dependency injection: AnalysisPluginFactory, DrawPanelFactory
	public AudioAnalyser(AnalysisPluginFactory analysisPluginFactory, DrawPanelFactory drawPanelFactory) {
		this.analysisPluginFactory = analysisPluginFactory;
		this.drawPanelFactory = drawPanelFactory;
	}

	/* Public / Package methods */

	public String[] getAllVisualizations() {
		return drawPanelFactory.getAllVisualizations();
	}

	public String printPlugins() {
		String result = "";
		result += "\n> Available plugins (" + analysisPluginFactory.getAvailablePlugins().length + "):\n";

		for (String availablePluginKey : analysisPluginFactory.getAvailablePlugins()) {
			result += availablePluginKey + "\n";
		}

		result += "\n> Available visualizations (" + drawPanelFactory.getAllVisualizations().length + "):\n";

		for (String availablePluginKey : drawPanelFactory.getAllVisualizations()) {
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

	public String runAnalysis(String inputFile, String analysisKey, boolean force, boolean verbose) throws AudioAnalyser.IncorrectInputException, OutputAlreadyExists, IOException, LoadFailedException, Chroma.WrongChromaSize {
		return analysisPluginFactory.createPlugin(analysisKey).analyse(inputFile, force, verbose);
	}

	public DrawPanel createDrawPanel(String inputFile, String pluginKey) throws LoadFailedException, OutputNotReady, ParseOutputError, IOException {
		return drawPanelFactory.createDrawPanel(pluginKey, analysisPluginFactory.createPlugin(pluginKey).getDataFromOutput(inputFile));
	}
}
