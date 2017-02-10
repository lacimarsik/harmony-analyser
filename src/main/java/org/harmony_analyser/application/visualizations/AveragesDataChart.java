package org.harmony_analyser.application.visualizations;

import javafx.scene.chart.XYChart;

class AveragesDataChart extends DataChart {
	AveragesDataChart(VisualizationData visualizationData) {
		super(visualizationData);

		title = visualizationData.getPluginName();
		xLabel = "Country";
		yLabel = "Averages";
		series1.setName("Averages");

		for (int i = 0; i < visualizationData.getValues().size(); i++) {
			series1.getData().add(new XYChart.Data(visualizationData.getLabels().get(i), visualizationData.getValues().get(i)));
		}
	}
}
