package org.harmony_analyser.application.visualizations;

import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.*;

class SegmentationDataChart extends DataChart {
	SegmentationDataChart(VisualizationData visualizationData) {
		super(visualizationData);

		title = visualizationData.getPluginName();
		xLabel = "Time";
		yLabel = "Distance";
		series1.setName("C");
		series2.setName("C#/Db");
		series3.setName("D");
		series4.setName("D#/Eb");
		series5.setName("E");
		series6.setName("F");
		series7.setName("F#/Gb");
		series8.setName("G");
		series9.setName("G#/Ab");
		series10.setName("A");
		series11.setName("A#/Bb");
		series12.setName("B");
		series13.setName("N/A");
		type = "area";

		for (int i = 0; i < visualizationData.getLabels().size(); i++) {
			String label = visualizationData.getLabels().get(i);
			Float timestamp  = visualizationData.getTimestamps().get(i);
			Float nextTimestamp;
			if (visualizationData.getTimestamps().size() > i + 1) {
				nextTimestamp = visualizationData.getTimestamps().get(i + 1);
			} else {
				nextTimestamp = timestamp;
			}
			XYChart.Series series = getSeriesForChord(label);
			Color color = getColorForChord(label);
			series.getData().add(new XYChart.Data(timestamp.toString(), 0));
			series.getData().add(new XYChart.Data(timestamp.toString(), 100));
			series.getData().add(new XYChart.Data(nextTimestamp.toString(), 100));
			series.getData().add(new XYChart.Data(nextTimestamp.toString(), 0));
		}
	}

	private XYChart.Series getSeriesForChord(String label) {
		Tone tone = Chordanal.getRootToneFromChordLabel(label);

		if (tone.equals(Tone.EMPTY_TONE)) {
			return series13;
		} else {
			switch(tone.getNumberMapped()) {
				case 0:
					return series1;
				case 1:
					return series2;
				case 2:
					return series3;
				case 3:
					return series4;
				case 4:
					return series5;
				case 5:
					return series6;
				case 6:
					return series7;
				case 7:
					return series8;
				case 8:
					return series9;
				case 9:
					return series10;
				case 10:
					return series11;
				case 11:
					return series12;
				default:
					return series13;
			}
		}
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
