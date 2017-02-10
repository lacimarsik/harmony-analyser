package org.harmony_analyser.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

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
