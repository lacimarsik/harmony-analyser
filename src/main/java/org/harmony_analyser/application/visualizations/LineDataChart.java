package org.harmony_analyser.application.visualizations;

class LineDataChart extends DataChart {
	LineDataChart(VisualizationData visualizationData) {
		super(visualizationData);

		// Create JavaFX Chart
		/*
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries complexityInTime = new XYSeries("Distance");
		for (int i = 0; i < visualizationData.getValues().size(); i++) {
			complexityInTime.add(visualizationData.getTimestamps().get(i), visualizationData.getValues().get(i));
		}
		dataset.addSeries(complexityInTime);
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisTitle, yAxisTitle, dataset, PlotOrientation.VERTICAL, true, true, true);
		XYPlot xyPlot = chart.getXYPlot();
		xyPlot.setRangeGridlinePaint(gridLinePaint);
		xyPlot.setBackgroundPaint(Color.WHITE);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		chart.getTitle().setFont(new Font("Sans", Font.PLAIN, 15));
		this.removeAll();
		ChartPanel chartPanel = new ChartPanel(chart, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), false, true, true, true, true, true);
		this.add(chartPanel);
		this.validate();*/
	}
}
