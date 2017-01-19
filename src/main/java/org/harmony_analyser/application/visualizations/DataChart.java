package org.harmony_analyser.application.visualizations;

import javafx.scene.chart.Chart;
import javafx.scene.paint.Color;
import java.util.ArrayList;

/**
 * Class to encapsulate JavaFX Chart and its data
 */

public class DataChart {
	final VisualizationData visualizationData;

	static final java.util.List<Color> palette;
	private static final int paletteStepCount = 2; // how many steps in one color space (R / G / B)
	public Chart chart;

	static {
		palette = new ArrayList<>();
		for (int r = 0; r < paletteStepCount; r++) palette.add(Color.rgb(r * 255 / paletteStepCount, 255, 0));
		for (int g = paletteStepCount; g > 0; g--) palette.add(Color.rgb(255, g * 255 / paletteStepCount, 0));
		for (int b = 0; b < paletteStepCount; b++) palette.add(Color.rgb(255, 0, b*255 / paletteStepCount));
		for (int r = paletteStepCount; r>0; r--) palette.add(Color.rgb(r * 255 / paletteStepCount, 0, 255));
		for (int g = 0; g < paletteStepCount; g++) palette.add(Color.rgb(0, g * 255 / paletteStepCount, 255));
		for (int b = paletteStepCount; b>0; b--) palette.add(Color.rgb(0, 255, b * 255 / paletteStepCount));
		palette.remove(palette.size() - 1);
		palette.add(Color.rgb(0, 0, 0));
		palette.add(Color.rgb(150, 150, 150));
	}

	DataChart(VisualizationData visualizationData) {
		this.visualizationData = visualizationData;
	}
}
