package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.AnalysisPlugin;
import org.vamp_plugins.PluginLoader;

import java.io.IOException;

/**
 * Factory to create Draw panels on demand
 */

@SuppressWarnings("SameParameterValue")

public class DrawPanelFactory {
	private final String[] VISUAL_PLUGINS = new String[] {
		"nnls-chroma:chordino",
		"harmanal:transition_complexity",
		"chromanal:chroma_complexity_simple",
		"chromanal:chroma_complexity_tonal"
	};

	private final String[] STATIC_VISUALIZATIONS = new String[] {
		"chord_palette"
	};

	private String[] getVisualPlugins() {
		return VISUAL_PLUGINS;
	}

	private String[] getStaticVisualizations() {
		return STATIC_VISUALIZATIONS;
	}

	public String[] getAllVisualizations() {
		String[] all_visualizations = new String[getStaticVisualizations().length + getVisualPlugins().length];
		System.arraycopy(getStaticVisualizations(), 0, all_visualizations, 0, getStaticVisualizations().length);
		System.arraycopy(getVisualPlugins(), 0, all_visualizations, getStaticVisualizations().length, getVisualPlugins().length);
		return all_visualizations;
	}

	public DrawPanel createDrawPanel(String pluginKey, VisualizationData visualizationData) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, PluginLoader.LoadFailedException {
		switch (pluginKey) {
			case "nnls-chroma:chordino":
				return new SegmentationDrawPanel(visualizationData);
			case "chord_palette":
				return new PaletteDrawPanel(visualizationData);
			case "harmanal:transition_complexity":
				return new ComplexityChartDrawPanel(visualizationData);
			case "chromanal:chroma_complexity_simple":
			case "chromanal:chroma_complexity_tonal":
				return new ChromaDrawPanel(visualizationData);
			default:
				return null;
		}
	}
}

