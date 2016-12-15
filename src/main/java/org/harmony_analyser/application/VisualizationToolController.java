package org.harmony_analyser.application;

import javafx.fxml.*;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Visualization Tool Events
 */

public class VisualizationToolController implements Initializable {
	@FXML
	private TreeView<?> browse;

	@FXML
	private ChoiceBox<?> plugin1;

	@FXML
	private ChoiceBox<?> plugin2;

	@FXML
	private ChoiceBox<?> plugin3;

	@FXML
	private Button analyse;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

	}
}
