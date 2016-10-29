package org.harmony_analyser.application.visualizations;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;

import java.awt.*;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

class LineChartDrawPanel extends DrawPanel {
	LineChartDrawPanel(VisualizationData visualizationData) {
		super(visualizationData);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawLineChart(g);
	}

	/* Private methods */

	/* Complet analysis */

	private void drawLineChart(Graphics g) {
		drawLineChart(g, visualizationData.getPluginName(), "", "", Color.BLACK);
	}

	/* Analysis components */

	/**
	 * Draws simple line chart using JFreeChart
	 * @param g [Graphics] main Graphics object
	 * @param chartTitle [String] name of the chart
	 * @param xAxisTitle [String] name of X axis
	 * @param yAxisTitle [String] name of Y axis
	 * @param gridLinePaint [Color] color of the line paint
	 */
	private void drawLineChart(Graphics g, String chartTitle, String xAxisTitle, String yAxisTitle, Color gridLinePaint) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries complexityInTime = new XYSeries("Complexity");
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
		this.validate();
	}
}
