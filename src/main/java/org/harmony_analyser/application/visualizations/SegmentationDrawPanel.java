package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.chordanal.*;

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
			relativeToneName = label.substring(0, Math.min(label.length(), 2));
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
		if (tone.equals(Tone.EMPTY_TONE)) {
			return palette.get(12);
		} else {
			return palette.get(tone.getNumberMapped());
		}
	}
}
