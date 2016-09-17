package org.harmony_analyser.plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.plugins.chromanal_plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.PluginLoader;

import java.util.Arrays;

/**
 * Singleton factory to create Analysis plugins on demand
 */

public class AnalysisPluginFactory {
	private static AnalysisPluginFactory instance = null;

	protected AnalysisPluginFactory() {
		// Avoid instantiation
	}

	public static AnalysisPluginFactory getInstance() {
		if (instance == null) {
			instance = new AnalysisPluginFactory();
		}
		return instance;
	}

	public static AnalysisPlugin createPlugin(String pluginKey) throws AudioAnalyser.LoadFailedException {
		AnalysisPlugin plugin;
		if (!Arrays.asList(AudioAnalyser.AVAILABLE_PLUGINS).contains(pluginKey)) {
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
