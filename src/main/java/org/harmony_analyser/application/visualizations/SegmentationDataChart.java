package org.harmony_analyser.application.visualizations;

import javafx.scene.paint.Color;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.*;

class SegmentationDataChart extends DataChart {
	SegmentationDataChart(VisualizationData visualizationData) {
		super(visualizationData);

		// Create JavaFX Chart
	}

	private Color getColorForChord(String label) {
		Tone tone = Chordanal.getRootToneFromChordLabel(label);

		if (tone.equals(Tone.EMPTY_TONE)) {
			return palette.get(12);
		} else {
			return palette.get(tone.getNumberMapped());
		}
	}
}
