package org.harmony_analyser.plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.plugins.chromanal_plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.PluginLoader;

import java.util.Arrays;

/**
 * Factory to create Analysis plugins on demand
 */

public class AnalysisPluginFactory {
	private final String[] AVAILABLE_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino",
		"harmanal:transition_complexity",
		"chromanal:chroma_complexity_simple",
		"chromanal:chroma_complexity_tonal"
	};

	private final String[] WRAPPED_VAMP_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino"
	};

	public String[] getAvailablePlugins() {
		return AVAILABLE_PLUGINS;
	}

	public String[] getWrappedVampPlugins() {
		return WRAPPED_VAMP_PLUGINS;
	}

	public AnalysisPlugin createPlugin(String pluginKey) throws AudioAnalyser.LoadFailedException {
		AnalysisPlugin plugin;
		if (!Arrays.asList(AVAILABLE_PLUGINS).contains(pluginKey)) {
			throw new AudioAnalyser.LoadFailedException("Plugin with key " + pluginKey + " is not available");
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
				case "chromanal:chroma_complexity_simple":
					plugin = new ChromaComplexitySimplePlugin();
					break;
				case "chromanal:chroma_complexity_tonal":
					plugin = new ChromaComplexityTonalPlugin();
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
