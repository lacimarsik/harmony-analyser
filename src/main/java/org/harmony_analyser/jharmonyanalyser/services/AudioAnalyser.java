package org.harmony_analyser.jharmonyanalyser.services;

import org.harmony_analyser.application.visualizations.*;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.vamp_plugins.PluginLoader;

import java.io.*;
import java.util.*;

/**
 * Class to orchestrate all levels of audio analysis, using available plugins and visualizations
 */

@SuppressWarnings("SameParameterValue")
public class AudioAnalyser {
	private final AnalysisFactory analysisFactory;
	private final DataChartFactory dataChartFactory;

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

	// Dependency injection: AnalysisFactory, DataChartFactory
	public AudioAnalyser(AnalysisFactory analysisFactory, DataChartFactory dataChartFactory) {
		this.analysisFactory = analysisFactory;
		this.dataChartFactory = dataChartFactory;
	}

	/* Public / Package methods */

	public String[] getAllWrappedVampPlugins() { return analysisFactory.getWrappedVampPlugins(); }

	public String[] getAllChordAnalyserPlugins() { return analysisFactory.getChordAnalyserPlugins(); }

	public String[] getAllChromaAnalyserPlugins() { return analysisFactory.getChromaAnalyserPlugins(); }

	public String[] getAllPostProcessingFilters() { return analysisFactory.getPostProcessingFilters(); }

	public String[] getAllVisualizations() {
		return dataChartFactory.getAllVisualizations();
	}

	public String printPlugins() {
		String result = "";
		result += "\n> Available plugins (" + analysisFactory.getAvailablePlugins().length + "):\n";

		for (String availablePluginKey : analysisFactory.getAvailablePlugins()) {
			result += availablePluginKey + "\n";
		}

		result += "\n> Available visualizations (" + dataChartFactory.getAllVisualizations().length + "):\n";

		for (String availablePluginKey : dataChartFactory.getAllVisualizations()) {
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
			for (String wrapped_plugin : analysisFactory.getWrappedVampPlugins()) {
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

	public String getPluginName(String pluginKey) throws LoadFailedException {
		return analysisFactory.createPlugin(pluginKey).name;
	}

	public String getPluginDescription(String pluginKey) throws LoadFailedException {
		return analysisFactory.createPlugin(pluginKey).description;
	}

	public String printParameters(String pluginKey) throws LoadFailedException {
		return analysisFactory.createPlugin(pluginKey).printParameters();
	}

	public String runAnalysis(String inputFile, String analysisKey, boolean force, boolean verbose) throws AudioAnalyser.IncorrectInputException, OutputAlreadyExists, IOException, LoadFailedException, Chroma.WrongChromaSize {
		return analysisFactory.createPlugin(analysisKey).analyse(inputFile, force, verbose);
	}

	public DataChart createDataChart(String inputFile, String pluginKey) throws LoadFailedException, OutputNotReady, ParseOutputError, IOException {
		return dataChartFactory.createDataChart(pluginKey, analysisFactory.createPlugin(pluginKey).getDataFromOutput(inputFile));
	}
}
