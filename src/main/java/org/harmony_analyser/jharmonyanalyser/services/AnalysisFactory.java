package org.harmony_analyser.jharmonyanalyser.services;

import org.harmony_analyser.jharmonyanalyser.plugins.EmptyPlugin;
import org.harmony_analyser.jharmonyanalyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.jharmonyanalyser.plugins.chromanal_plugins.*;
import org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins.*;
import org.harmony_analyser.jharmonyanalyser.filters.TimeSeriesFilter;
import org.vamp_plugins.PluginLoader;

/**
 * Factory to create Analysis plugins on demand
 */

public class AnalysisFactory {
	private final String[] AVAILABLE_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino-labels",
		"nnls-chroma:chordino-tones",
		"chord_analyser:chord_complexity_distance",
		"chord_analyser:average_chord_complexity_distance",
		"chroma_analyser:simple_difference",
		"chroma_analyser:complexity_difference",
		"chord_analyser:chord_palette",
		"filters:time_series"
	};

	private final String[] WRAPPED_VAMP_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino-labels",
		"nnls-chroma:chordino-tones"
	};

	String[] getAvailablePlugins() {
		return AVAILABLE_PLUGINS;
	}

	String[] getWrappedVampPlugins() {
		return WRAPPED_VAMP_PLUGINS;
	}

	public Analysis createPlugin(String pluginKey) throws AudioAnalyser.LoadFailedException {
		Analysis plugin;
		try {
			switch (pluginKey) {
				case "nnls-chroma:nnls-chroma":
					plugin = new NNLSPlugin();
					break;
				case "nnls-chroma:chordino-labels":
					plugin = new ChordinoLabelsPlugin();
					break;
				case "nnls-chroma:chordino-tones":
					plugin = new ChordinoTonesPlugin();
					break;
				case "qm-vamp-plugins:qm-keydetector":
					plugin = new KeyDetectorPlugin();
					break;
				case "chord_analyser:average_chord_complexity_distance":
					plugin = new AverageChordComplexityDistancePlugin();
					break;
				case "chord_analyser:chord_complexity_distance":
					plugin = new ChordComplexityDistancePlugin();
					break;
				case "chord_analyser:tps_distance":
					plugin = new TPSDistancePlugin();
					break;
				case "chroma_analyser:simple_difference":
					plugin = new SimpleDifferencePlugin();
					break;
				case "chroma_analyser:complexity_difference":
					plugin = new ComplexityDifferencePlugin();
					break;
				case "chord_palette":
					plugin = new EmptyPlugin();
					break;
				case "filters:time_series":
					plugin = new TimeSeriesFilter();
					break;
				default:
					throw new AudioAnalyser.LoadFailedException("Plugin with key " + pluginKey + " is not available");
			}
		} catch (PluginLoader.LoadFailedException e) {
			throw new AudioAnalyser.LoadFailedException(e.getMessage());
		}
		return plugin;
	}
}
