package org.harmony_analyser.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.harmony_analyser.application.visualizations.DataChart;
import org.harmony_analyser.application.visualizations.DataChartFactory;
import org.harmony_analyser.application.visualizations.DrawPanelFactory;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.services.AnalysisFactory;
import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Visualization Tool Events
 */

public class HarmonyAnalyserController implements Initializable {
	@FXML
	private Tab chordTransitionToolTab;

	@FXML
	private BorderPane chordTransitionTool;

	@FXML
	private Tab audioAnalysisToolTab;

	@FXML
	private BorderPane audioAnalysisTool;

	@FXML
	private Tab visualizationToolTab;

	@FXML
	private BorderPane visualizationTool;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		try {
			Parent chordTransitionToolRoot, audioAnalysisToolRoot, visualizationToolRoot;
			ClassLoader classLoader = getClass().getClassLoader();
			chordTransitionToolRoot = FXMLLoader.load(classLoader.getResource("ChordTransitionTool.fxml"));
			chordTransitionTool.getChildren().setAll(chordTransitionToolRoot);
			audioAnalysisToolRoot = FXMLLoader.load(classLoader.getResource("AudioAnalysisTool.fxml"));
			audioAnalysisTool.getChildren().setAll(audioAnalysisToolRoot);
			visualizationToolRoot = FXMLLoader.load(classLoader.getResource("VisualizationTool.fxml"));
			visualizationTool.getChildren().setAll(visualizationToolRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
