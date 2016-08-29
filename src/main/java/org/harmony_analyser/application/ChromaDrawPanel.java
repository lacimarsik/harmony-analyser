package org.harmony_analyser.application;

import org.harmony_analyser.application.services.*;
import org.harmony_analyser.plugins.AnalysisPlugin;
import org.harmony_analyser.plugins.chromanal_plugins.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

public class ChromaDrawPanel extends DrawPanel {
	private final List<Float> timestamps;
	private final List<Float> values;
	private final String type;

	public ChromaDrawPanel(String inputFile, String type) throws AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, IOException, CannotVisualize {
		super();
		timestamps = new ArrayList<>();
		values = new ArrayList<>();
		this.type = type;
		getData(inputFile);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawChromaComplexityGraph(g);
	}

	void getData(String inputFile) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, CannotVisualize {
		List<String> linesList = new ArrayList<>();
		switch (type) {
			case "Simple":
				linesList = new ChromaComplexitySimplePlugin().getResultForInputFile(inputFile);
				break;
			case "Tonal":
				linesList = new ChromaComplexityTonalPlugin().getResultForInputFile(inputFile);
				break;
		}

		float timestamp, value;

		/* Plugin-specific parsing of the result */
		try {
			for (String line : linesList) {
				timestamp = AudioAnalyser.getTimestampFromLine(line);
				value = Float.parseFloat(AudioAnalyser.getLabelFromLine(line));
				timestamps.add(timestamp);
				values.add(value);
			}
		} catch (NumberFormatException e) {
			throw new CannotVisualize("Output did not have the required fields");
		}
	}

	/* Private methods */

	/* Complet analysis */

	private void drawChromaComplexityGraph(Graphics g) {
		drawLineChart(g, "Chroma Complexity " + type, "", "", Color.BLACK);
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
		for (int i = 0; i < values.size(); i++) {
			complexityInTime.add(timestamps.get(i), values.get(i));
		}
		dataset.addSeries(complexityInTime);
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisTitle, yAxisTitle, dataset, PlotOrientation.VERTICAL, true, true, true);
		XYPlot xyPlot = chart.getXYPlot();
		xyPlot.setRangeGridlinePaint(gridLinePaint);
		xyPlot.setBackgroundPaint(Color.WHITE);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		//CategoryPlot yPlot = chart.getCategoryPlot();
		//NumberAxis rangeAxis = (NumberAxis) yPlot.getRangeAxis();
		//rangeAxis.setRange(0.0, 4.0);
		chart.getTitle().setFont(new Font("Sans", Font.PLAIN, 15));
		this.removeAll();
		ChartPanel chartPanel = new ChartPanel(chart, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), false, true, true, true, true, true);
		this.add(chartPanel);
		this.validate();
	}
}
