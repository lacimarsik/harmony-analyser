package org.harmony_analyser.application.visualizations;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

class ComplexityChartDrawPanel extends DrawPanel {
	ComplexityChartDrawPanel(VisualizationData visualizationData) {
		super(visualizationData);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawComplexityColumnGraph(g);
	}

	/* Private methods */

	/* Complet analysis */

	private void drawComplexityColumnGraph(Graphics g) {
		drawColumnChart(g, visualizationData.getPluginName(), "", "", Color.BLACK);
	}

	/* Analysis components */

	/**
	 * Draws simple column chart using JFreeChart
	 * @param g [Graphics] main Graphics object
	 * @param chartTitle [String] name of the chart
	 * @param xAxisTitle [String] name of X axis
	 * @param yAxisTitle [String] name of Y axis
	 * @param gridLinePaint [Color] color of the line paint
	 */
	private void drawColumnChart(Graphics g, String chartTitle, String xAxisTitle, String yAxisTitle, Color gridLinePaint) {
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
	}
}
