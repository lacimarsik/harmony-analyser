package org.harmony_analyser.application.visualizations;

import java.util.List;

/**
 * Wrapper for data to visualzize
 */

public class VisualizationData {
	private List<Float> timestamps;
	private List<Float> values;
	private List<String> labels;

	List<Float> getTimestamps() {
		return timestamps;
	}

	public List<Float> getValues() {
		return values;
	}

	List<String> getLabels() {
		return labels;
	}

	public void setTimestamps(List<Float> timestamps) {
		this.timestamps = timestamps;
	}

	public void setValues(List<Float> values) {
		this.values = values;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
}
