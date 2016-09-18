package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.application.services.*;
import org.harmony_analyser.chordanal.*;
import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.PluginLoader;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

class SegmentationDrawPanel extends DrawPanel {
	SegmentationDrawPanel(VisualizationData visualizationData) throws AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, IOException, PluginLoader.LoadFailedException {
		super(visualizationData);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawChordSegmentation(g);
	}

	/* Private methods */

	/* Complet analysis */

	private void drawChordSegmentation(Graphics g) {
		List<Float> timestampsCopy = new ArrayList<>(visualizationData.getTimestamps());
		float maximalTimestamp = timestampsCopy.get(timestampsCopy.size() - 1);

		cursor.setLocation(0, 0);
		float previousTimestamp = timestampsCopy.get(0);
		float segmentSize;
		String relativeToneName;
		int i = 0;
		timestampsCopy.remove(0); // Skip first timestamp
		for (float timestamp : timestampsCopy) {
			relativeToneName = visualizationData.getLabels().get(i).substring(0, Math.min(visualizationData.getLabels().get(i).length(), 2));
			segmentSize = ((timestamp - previousTimestamp) / maximalTimestamp);
			drawSegment(g, segmentSize, getColorForTone(relativeToneName));
			previousTimestamp = timestamp;
			i++;
		}
		cursor.setLocation(0, 0);
	}

	/* Analysis components */

	private Color getColorForTone(String relativeToneName) {
		String notAllowedCharacters = "mda/";
		if ((relativeToneName.length() == 2) && ((Character.isDigit(relativeToneName.charAt(1)) || (notAllowedCharacters.contains(relativeToneName.substring(1, 1)))))) {
			relativeToneName = relativeToneName.substring(0,1);
		}

		Tone tone = Chordanal.createToneFromRelativeName(relativeToneName);
		if (tone == null) {
			return palette.get(12);
		} else {
			return palette.get(tone.getNumberMapped());
		}
	}
}
