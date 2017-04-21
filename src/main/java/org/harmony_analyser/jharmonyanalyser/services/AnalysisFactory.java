package org.harmony_analyser.jharmonyanalyser.services;

import org.harmony_analyser.jharmonyanalyser.plugins.EmptyPlugin;
import org.harmony_analyser.jharmonyanalyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.jharmonyanalyser.plugins.chromanal_plugins.*;
import org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins.*;
import org.harmony_analyser.jharmonyanalyser.filters.TimeSeriesFilter;
import org.vamp_plugins.PluginLoader;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Factory to create Analysis plugins on demand
 */

public class AnalysisFactory {
	private final String[] CHORD_ANALYSER_PLUGINS = new String[] {
		"chord_analyser:chord_complexity_distance",
		"chord_analyser:average_chord_complexity_distance"
	};

	private final String[] CHROMA_ANALYSER_PLUGINS = new String[] {
		"chroma_analyser:simple_difference",
		"chroma_analyser:complexity_difference"
	};

	private final String[] POST_PROCESSING_FILTERS = new String[] {
		"filters:time_series",
		"filters:chord_vectors"
	};

	private final String[] WRAPPED_VAMP_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino-labels",
		"nnls-chroma:chordino-tones",
		"qm-vamp-plugins:qm-keydetector"
	};

	//TODO: Array concat HAS TO has better solution...
	String[] getAvailablePlugins() {
		return Stream.concat(
			Stream.concat(Arrays.stream(CHORD_ANALYSER_PLUGINS), Arrays.stream(CHROMA_ANALYSER_PLUGINS)),
			Stream.concat(Arrays.stream(WRAPPED_VAMP_PLUGINS), Arrays.stream(POST_PROCESSING_FILTERS))
		).toArray(String[]::new);
	}

	String[] getChordAnalyserPlugins() {
		return CHORD_ANALYSER_PLUGINS;
	}

	String[] getChromaAnalyserPlugins() {
		return CHROMA_ANALYSER_PLUGINS;
	}

	String[] getPostProcessingFilters() {
		return POST_PROCESSING_FILTERS;
	}

	String[] getWrappedVampPlugins() {
		return WRAPPED_VAMP_PLUGINS;
	}

	public Analysis createPlugin(String analysisKey) throws AudioAnalyser.LoadFailedException {
		Analysis plugin;
		try {
			switch (analysisKey) {
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
				case "filters:chord_vectors":
					plugin = new ChordVectorsFilter();
					break;
				default:
					throw new AudioAnalyser.LoadFailedException("Analysis with key " + analysisKey + " is not available");
			}
		} catch (PluginLoader.LoadFailedException e) {
			throw new AudioAnalyser.LoadFailedException(e.getMessage());
		}
		return plugin;
	}
}
