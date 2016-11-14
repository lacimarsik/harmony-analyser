package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.jharmonyanalyser.chord_analyser.*;

import java.awt.*;
import java.util.*;
import java.util.List;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

class SegmentationDrawPanel extends DrawPanel {
	SegmentationDrawPanel(VisualizationData visualizationData) {
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
			String label = visualizationData.getLabels().get(i);
			segmentSize = ((timestamp - previousTimestamp) / maximalTimestamp);
			drawSegment(g, segmentSize, getColorForChord(label));
			previousTimestamp = timestamp;
			i++;
		}
		cursor.setLocation(0, 0);
	}

	/* Analysis components */

	private Color getColorForChord(String label) {
		Tone tone = Chordanal.getRootToneFromChordLabel(label);

		if (tone.equals(Tone.EMPTY_TONE)) {
			return palette.get(12);
		} else {
			return palette.get(tone.getNumberMapped());
		}
	}
}
