package org.harmony_analyser.application.visualizations;

class AveragesDataChart extends DataChart {
	AveragesDataChart(VisualizationData visualizationData) {
		super(visualizationData);

		// Create JavaFX Chart
		/*
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < visualizationData.getValues().size(); i++) {
			dataset.setValue(visualizationData.getValues().get(i), visualizationData.getLabels().get(i), "");
		}
		JFreeChart chart = ChartFactory.createBarChart(chartTitle, xAxisTitle, yAxisTitle, dataset, PlotOrientation.VERTICAL, true, true, true);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		categoryPlot.setRangeGridlinePaint(gridLinePaint);
		categoryPlot.setBackgroundPaint(Color.WHITE);
		CategoryPlot yPlot = chart.getCategoryPlot();
		NumberAxis rangeAxis = (NumberAxis) yPlot.getRangeAxis();
		rangeAxis.setRange(0.0, 4.0);
		chart.getTitle().setFont(new Font("Sans", Font.PLAIN, 15));
		this.removeAll();
		ChartPanel chartPanel = new ChartPanel(chart, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), false, true, true, true, true, true);
		this.add(chartPanel);
		this.validate();
		 */
	}
}
