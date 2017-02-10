package org.harmony_analyser.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * GUI for HarmonyAnalyser
 */

@SuppressWarnings("ConstantConditions")
public class HarmonyAnalyser extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception{
		ClassLoader classLoader = getClass().getClassLoader();
		Parent root = FXMLLoader.load(classLoader.getResource("HarmonyAnalyser.fxml"));
		primaryStage.setTitle("Harmony Analyser 1.2-beta");

		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}