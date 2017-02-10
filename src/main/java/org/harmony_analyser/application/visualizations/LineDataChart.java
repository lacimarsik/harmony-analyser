package org.harmony_analyser.application.visualizations;

import javafx.scene.chart.XYChart;

class LineDataChart extends DataChart {
	LineDataChart(VisualizationData visualizationData) {
		super(visualizationData);

		title = visualizationData.getPluginName();
		xLabel = "Time";
		yLabel = "Dist.";
		series1.setName("Distances");
		type = "line";

		for (int i = 0; i < visualizationData.getValues().size(); i++) {
			series1.getData().add(new XYChart.Data(visualizationData.getTimestamps().get(i).toString(), visualizationData.getValues().get(i)));
		}
	}
}
