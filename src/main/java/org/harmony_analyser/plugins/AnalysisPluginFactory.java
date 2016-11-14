package org.harmony_analyser.plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.plugins.chromanal_plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.PluginLoader;

/**
 * Factory to create Analysis plugins on demand
 */

public class AnalysisPluginFactory {
	private final String[] AVAILABLE_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino-labels",
		"nnls-chroma:chordino-tones",
		"chord_analyser:average_chord_complexity_distance",
		"chroma_analyser:simple_difference",
		"chroma_analyser:tonal_difference",
		"chord_analyser:chord_palette"
	};

	private final String[] WRAPPED_VAMP_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino-labels",
		"nnls-chroma:chordino-tones"
	};

	public String[] getAvailablePlugins() {
		return AVAILABLE_PLUGINS;
	}

	public String[] getWrappedVampPlugins() {
		return WRAPPED_VAMP_PLUGINS;
	}

	public AnalysisPlugin createPlugin(String pluginKey) throws AudioAnalyser.LoadFailedException {
		AnalysisPlugin plugin;
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
				case "chord_analyser:harmonic_complexity":
					plugin = new AverageChordComplexityDistancePlugin();
					break;
				case "chord_analyser:chord_complexity_distance":
					plugin = new ChordComplexityDistancePlugin();
					break;
				case "chord_analyser:tps_distance":
					plugin = new TPSDistancePlugin();
					break;
				case "chroma_analyser:chroma_complexity_simple":
					plugin = new SimpleDifferencePlugin();
					break;
				case "chroma_analyser:chroma_complexity_tonal":
					plugin = new TonalDifferencePlugin();
					break;
				case "chord_palette":
					plugin = new EmptyPlugin();
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
