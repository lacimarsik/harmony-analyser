package org.harmony_analyser.application;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

import org.harmony_analyser.application.visualizations.DrawPanelFactory;
import org.harmony_analyser.jharmonyanalyser.services.*;

/**
 * Controller for Audio Analysis Tool Events
 */

public class AudioAnalysisToolController implements Initializable {
	@FXML
	private StackPane browsePane;

	@FXML
	private TreeView<File> browse;

	@FXML
	private TextArea console;

	@FXML
	private ListView<String> vampAvailable;

	@FXML
	private Label vampTitle;

	@FXML
	private Label vampDescription;

	@FXML
	private Button vampSettings;

	@FXML
	private Button vampAnalyse;

	@FXML
	private ListView<String> caAvailable;

	@FXML
	private Label caTitle;

	@FXML
	private Label caDescription;

	@FXML
	private Button caSettings;

	@FXML
	private Button caAnalyse;

	@FXML
	private ListView<String> chrAvailable;

	@FXML
	private Label chrTitle;

	@FXML
	private Label chrDescription;

	@FXML
	private Button chrSettings;

	@FXML
	private Button chrAnalyse;

	@FXML
	private ListView<String> ppAvailable;

	@FXML
	private Label ppTitle;

	@FXML
	private Label ppDescription;

	@FXML
	private Button ppSettings;

	@FXML
	private Button ppAnalyse;

	@FXML
	private TextField ppExtension;

	private AudioAnalyser audioAnalyser;

	public static Image folderCollapseImage = new Image(ClassLoader.getSystemResourceAsStream("folder.png"));
	public static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("file.png"));
	public static Image computerImage = new Image(ClassLoader.getSystemResourceAsStream("computer.png"));

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		//initialize AudioAnalyser
		AnalysisFactory analysisFactory = new AnalysisFactory();
		DrawPanelFactory drawPanelFactory = new DrawPanelFactory();
		audioAnalyser = new AudioAnalyser(analysisFactory, drawPanelFactory);

		//create the tree view
		// TODO: Check unchecked assignments
		browse = buildFileSystemBrowser();
		browsePane.getChildren().add(browse);

		//load plugins
		ObservableList<String> vampPlugins = FXCollections.observableArrayList(audioAnalyser.getAllWrappedVampPlugins());
		vampAvailable.setItems(vampPlugins);
		ObservableList<String> chordAnalyserPlugins = FXCollections.observableArrayList(audioAnalyser.getAllChordAnalyserPlugins());
		caAvailable.setItems(chordAnalyserPlugins);
		ObservableList<String> chromaAnalyserPlugins = FXCollections.observableArrayList(audioAnalyser.getAllChromaAnalyserPlugins());
		chrAvailable.setItems(chromaAnalyserPlugins);
		ObservableList<String> postProcessingFilters = FXCollections.observableArrayList(audioAnalyser.getAllPostProcessingFilters());
		ppAvailable.setItems(postProcessingFilters);

		//init UI
		vampAvailable.getSelectionModel().select(0);
		caAvailable.getSelectionModel().select(0);
		chrAvailable.getSelectionModel().select(0);
		ppAvailable.getSelectionModel().select(0);
		try {
			vampTitle.setText(audioAnalyser.getPluginName(vampAvailable.getSelectionModel().getSelectedItem()));
			vampDescription.setText(audioAnalyser.getPluginDescription(vampAvailable.getSelectionModel().getSelectedItem()));
			caTitle.setText(audioAnalyser.getPluginName(caAvailable.getSelectionModel().getSelectedItem()));
			caDescription.setText(audioAnalyser.getPluginDescription(caAvailable.getSelectionModel().getSelectedItem()));
			chrTitle.setText(audioAnalyser.getPluginName(chrAvailable.getSelectionModel().getSelectedItem()));
			chrDescription.setText(audioAnalyser.getPluginDescription(chrAvailable.getSelectionModel().getSelectedItem()));
			ppTitle.setText(audioAnalyser.getPluginName(ppAvailable.getSelectionModel().getSelectedItem()));
			ppDescription.setText(audioAnalyser.getPluginDescription(ppAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}

		vampAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				vampTitle.setText(audioAnalyser.getPluginName(vampAvailable.getSelectionModel().getSelectedItem()));
				vampDescription.setText(audioAnalyser.getPluginDescription(vampAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		caAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				caTitle.setText(audioAnalyser.getPluginName(caAvailable.getSelectionModel().getSelectedItem()));
				caDescription.setText(audioAnalyser.getPluginDescription(caAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		chrAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				chrTitle.setText(audioAnalyser.getPluginName(chrAvailable.getSelectionModel().getSelectedItem()));
				chrDescription.setText(audioAnalyser.getPluginDescription(chrAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		ppAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				ppTitle.setText(audioAnalyser.getPluginName(ppAvailable.getSelectionModel().getSelectedItem()));
				ppDescription.setText(audioAnalyser.getPluginDescription(ppAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});
	}

	private TreeView buildFileSystemBrowser() {
		TreeItem<File> root = createNode(new File("/"));
		root.setGraphic(new ImageView(computerImage));
		return new TreeView<File>(root);
	}

	// This method creates a TreeItem to represent the given File. It does this
	// by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods
	// anonymously, but this could be better abstracted by creating a
	// 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
	// for the reader.
	private TreeItem<File> createNode(final File f) {
		return new TreeItem<File>(f) {
			// We cache whether the File is a leaf or not. A File is a leaf if
			// it is not a directory and does not have any files contained within
			// it. We cache this as isLeaf() is called often, and doing the
			// actual check on File is expensive.
			private boolean isLeaf;

			// We do the children and leaf testing only once, and then set these
			// booleans to false so that we do not check again during this
			// run. A more complete implementation may need to handle more
			// dynamic file system situations (such as where a folder has files
			// added after the TreeView is shown). Again, this is left as an
			// exercise for the reader.
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override public ObservableList<TreeItem<File>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;

					// First getChildren() call, so we actually go off and
					// determine the children of the File contained in this TreeItem.
					super.getChildren().setAll(buildChildren(this));
				}
				return super.getChildren();
			}

			@Override public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					File f = getValue();
					isLeaf = f.isFile();
				}

				return isLeaf;
			}

			private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
				File f = TreeItem.getValue();
				if (f != null && f.isDirectory()) {
					File[] files = f.listFiles();
					if (files != null) {
						ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

						for (File childFile : files) {
							TreeItem item = createNode(childFile);
							if (childFile.isDirectory()){
								item.setGraphic(new ImageView(folderCollapseImage));
							} else {
								item.setGraphic(new ImageView(fileImage));
							}
							children.add(item);
						}

						return children;
					}
				}

				return FXCollections.emptyObservableList();
			}
		};
	}

	@FXML
	void printVampSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(vampAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void printChordAnalyserSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(caAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void printChromaAnalyserSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(chrAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void printPostProcessingSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(ppAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void runVampAnalysis(ActionEvent event) {
		analyzeFolder(browse.getSelectionModel().getSelectedItem().getValue(), vampAvailable.getSelectionModel().getSelectedItem(), ".wav");
	}

	@FXML
	void runChordAnalyserAnalysis(ActionEvent event) {
		analyzeFolder(browse.getSelectionModel().getSelectedItem().getValue(), caAvailable.getSelectionModel().getSelectedItem(), ".wav");
	}

	@FXML
	void runChromaAnalyserAnalysis(ActionEvent event) {
		analyzeFolder(browse.getSelectionModel().getSelectedItem().getValue(), chrAvailable.getSelectionModel().getSelectedItem(), ".wav");
	}

	@FXML
	void runPostProcessingAnalysis(ActionEvent event) {
		String extension = ppExtension.getText();
		if (extension.equals("")) {
			console.setText("\n> Extension for filtering not specified. Please enter extension.");
		} else {
			analyzeFolder(browse.getSelectionModel().getSelectedItem().getValue(), ppAvailable.getSelectionModel().getSelectedItem(), extension);
		}
	}

	private void analyzeFolder(File inputFolder, String pluginKey, String suffixAndExtension) {
		if (inputFolder.isFile()) {
			console.setText("\n> Folder needs to be selected for Audio Analysis Tool");
			return;
		}
		try {
			console.setText("\n> Analyzing input folder using plugin: " + pluginKey);
			Files.walkFileTree(inputFolder.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
					console.setText(console.getText() + "\nDir: " + dir.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					if (file.toString().endsWith(suffixAndExtension)) {
						console.setText(console.getText() + "\nProcessing: " + file.toString() + "\n");
						try {
							String analysisResult = audioAnalyser.runAnalysis(file.toString(), pluginKey, true, false);
							console.setText(console.getText() + "\n" + analysisResult);
						} catch (AudioAnalyser.IncorrectInputException | AudioAnalyser.LoadFailedException e) {
							console.setText(console.getText() + "\nERROR: " + e.getMessage());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException e) {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}