package harmanal;

import harmanal.vamp_plugins.*;
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;

/**
 * GUI for HarmonyAnalyser
 */

class HarmonyAnalyser extends JFrame {
	private JTabbedPane tabbedPane1;
	private JButton selectButton;
	private JTextPane textPane1;
	private JButton playButton;
	private JButton playButton1;
	private JTextPane textPane2;
	private JButton loadPluginsButton;
	private JList<String> list1;
	private JTextPane textPane4;
	private JTextPane textPane5;
	private JTextPane textPane6;
	private JTextPane textPane7;
	private JTextPane textPane8;
	private JTextPane textPane9;
	private JTextPane textPane10;
	private JTextPane textPane11;
	private JTextPane textPane12;
	private JTextPane textPane13;
	private JTextPane textPane14;
	private JTextPane textPane15;
	private JTextPane textPane16;
	private JTextPane textPane3;
	private JTextField textField8;
	private JPanel rootPanel;
	private JCheckBox captureMIDICheckBox;
	private JCheckBox captureMIDICheckBox1;
	private JButton browseButton;
	private JButton extractChromasButton;
	private JButton segmentTrackButton;
	private JButton analyzeComplexityButton;
	private JButton buttonNNLS;
	private JButton buttonChordino;
	private JFileChooser fileChooser;

	private Harmony harmony1,harmony2 = null;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		new HarmonyAnalyser();
	}

	/**
	 * Initialize the application.
	 */

	private HarmonyAnalyser() {
		/* GUI - Initialization */

		setContentPane(rootPanel);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setTitle("Harmony Analyser");

		/* Chord Transition Tool - Initialization */

		// Obtain information about all the installed input MIDI devices
		final MidiHandler midiHandler = new MidiHandler();
		String[] inputDevices = midiHandler.getInputDeviceList();
		list1.setListData(inputDevices);

		list1.addListSelectionListener(listSelectionEvent -> selectButton.setEnabled(true));

		selectButton.addActionListener(actionEvent -> {
			MidiDevice selectedDevice = midiHandler.getMidiDevice(list1.getSelectedValue());
			textPane1.setText("device: " + selectedDevice.getDeviceInfo().getName() +
				"\nvendor: " + selectedDevice.getDeviceInfo().getVendor().substring(0, selectedDevice.getDeviceInfo().getVendor().indexOf(" "))+
				"\ndescription: " + selectedDevice.getDeviceInfo().getDescription().substring(0, selectedDevice.getDeviceInfo().getDescription().indexOf(",")));

			midiHandler.close();
			midiHandler.initialize(null, null, selectedDevice, null, null);
			midiHandler.connectInputSynthesizer();

			captureMIDICheckBox.setEnabled(true);
			captureMIDICheckBox1.setEnabled(true);
			captureMIDICheckBox.setSelected(false);
			captureMIDICheckBox1.setSelected(false);
			playButton.setEnabled(false);
			playButton1.setEnabled(false);
		});

		captureMIDICheckBox.addActionListener(actionEvent -> {
			try {
				if (captureMIDICheckBox.isSelected()) {
					midiHandler.connectInputDecoder();
					captureMIDICheckBox1.setEnabled(false);
				} else {
					midiHandler.inputDevice.close();
					harmony1 = midiHandler.getBufferHarmony();
					if (harmony1 != null) {
						analyzeHarmony(harmony1,textPane10,textPane12,textPane4,textPane6,textPane8,textPane14);
						playButton.setEnabled(true);
						if (harmony2 != null) {
							analyzeTransition(harmony1,harmony2,textPane2,textPane16);
						}
					}
					midiHandler.decoder.close();
					midiHandler.inputDevice.open();
					midiHandler.connectInputSynthesizer();
					captureMIDICheckBox1.setEnabled(true);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		});

		captureMIDICheckBox1.addActionListener(actionEvent -> {
			try {
				if (captureMIDICheckBox1.isSelected()) {
					midiHandler.connectInputDecoder();
					captureMIDICheckBox.setEnabled(false);
				} else {
					midiHandler.inputDevice.close();
					harmony2 = midiHandler.getBufferHarmony();
					if (harmony2 != null) {
						analyzeHarmony(harmony2,textPane11,textPane13,textPane5,textPane7,textPane9,textPane15);
						playButton1.setEnabled(true);
						if (harmony1 != null) {
							analyzeTransition(harmony1,harmony2,textPane2,textPane16);
						}
					}
					midiHandler.decoder.close();
					midiHandler.inputDevice.open();
					midiHandler.connectInputSynthesizer();
					captureMIDICheckBox.setEnabled(true);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		});

		textPane10.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony1 = Chordanal.createHarmonyFromRelativeTones(textPane10.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1,textPane10,textPane12,textPane4,textPane6,textPane8,textPane14);
					playButton.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane10.setText("");
					textPane12.setText("");
				}
			}
		});

		textPane12.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony1 = Chordanal.createHarmonyFromTones(textPane12.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1,textPane10,textPane12,textPane4,textPane6,textPane8,textPane14);
					playButton.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane10.setText("");
					textPane12.setText("");
				}
			}
		});

		textPane11.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony2 = Chordanal.createHarmonyFromRelativeTones(textPane11.getText());
				if (harmony2 != null) {
					analyzeHarmony(harmony2,textPane11,textPane13,textPane5,textPane7,textPane9,textPane15);
					playButton1.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane11.setText("");
					textPane13.setText("");
				}
			}
		});

		textPane13.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony2 = Chordanal.createHarmonyFromTones(textPane13.getText());
				if (harmony2 != null) {
					analyzeHarmony(harmony2,textPane11,textPane13,textPane5,textPane7,textPane9,textPane15);
					playButton1.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane11.setText("");
					textPane13.setText("");
				}
			}
		});

		playButton.addActionListener(actionEvent -> midiHandler.play(harmony1));

		playButton1.addActionListener(actionEvent -> midiHandler.play(harmony2));

		/* Audio Analysis Tool - Initialization */

		loadPluginsButton.addActionListener(actionEvent -> {
			try {
				textPane3.setText(textPane3.getText() + VampPlugin.printPlugins() + VampPlugin.printWrappedPlugins());
			} catch (Exception e) {
				textPane3.setText(e.getMessage());
			}
		});

		buttonNNLS.addActionListener(actionEvent -> {
			try {
				NNLSPlugin nnls = new NNLSPlugin();
				textPane3.setText(textPane3.getText() + nnls.printParameters());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		buttonChordino.addActionListener(actionEvent -> {
			try {
				ChordinoPlugin chordino = new ChordinoPlugin();
				textPane3.setText(textPane3.getText() + chordino.printParameters());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		browseButton.addActionListener(actionEvent -> {
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				textPane3.setText(textPane3.getText() + "\n\n> Selected directory: " + file.getAbsolutePath());
				textField8.setText(file.getAbsolutePath());
			}
		});

		extractChromasButton.addActionListener(actionEvent -> {
		try {
			textPane3.setText(textPane3.getText() + "\n\n> Step 1: Extracting chromas from audio files");
			Path startPath = Paths.get(textField8.getText());
			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) {
					textPane3.setText(textPane3.getText() + "\nDir: " + dir.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

					if (file.toString().endsWith(".wav")) {
						textPane3.setText(textPane3.getText() + "\nAnalyzing: " + file.toString() + "\n");
						try {
							String analysisResult = new NNLSPlugin().analyze(file.toString(), file.toString() + "-chromas.txt");
							textPane3.setText(textPane3.getText() + "\n" + analysisResult);
						} catch (Exception e) {
							e.printStackTrace();
						}
						textPane3.setText(textPane3.getText() + "\nOutput saved in: " + file.toString() + "-chromas.txt\n");
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
		});

		segmentTrackButton.addActionListener(actionEvent -> {
			try {
				textPane3.setText(textPane3.getText() + "\n\n> Step 2: Segmenting audio tracks from given chroma files");
				Path startPath = Paths.get(textField8.getText());
				Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) {
						textPane3.setText(textPane3.getText() + "\nDir: " + dir.toString());
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						if (file.toString().endsWith(".wav")) {
							textPane3.setText(textPane3.getText() + "\nSegmenting: " + file.toString() + "\n");
							try {
								String analysisResult = new ChordinoPlugin().analyze(file.toString(), file.toString() + "-segmentation.txt");
								textPane3.setText(textPane3.getText() + "\n" + analysisResult);
							} catch (Exception e) {
								e.printStackTrace();
							}

							textPane3.setText(textPane3.getText() + "\nSegmentation saved in: " + file.toString() + "-segmentation.txt\n");
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
		});

		analyzeComplexityButton.addActionListener(actionEvent -> {
			try {
				textPane3.setText(textPane3.getText() + "\n\n> Step 3: Analyzing complexity for audio files");
				Path startPath = Paths.get(textField8.getText());
				Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) {
						textPane3.setText(textPane3.getText() + "\nDir: " + dir.toString());
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						if (file.toString().endsWith(".wav")) {
							textPane3.setText(textPane3.getText() + "\nAnalyzing: " + file.toString() + "\n");

							try {
								Harmanal.analyzeSong(
									file.toString() + "-chromas.txt",
									file.toString() + "-segmentation.txt",
									file.toString() + "-report.txt"
								);
							} catch (IOException | Harmanal.IncorrectInput e) {
								e.printStackTrace();
							}

							textPane3.setText(textPane3.getText() + "\nReport saved in: " + file.toString() + "-report.txt\n");
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
		});
	}

	/* Chord Transition Tool - Handling methods */

	private void analyzeHarmony(Harmony harmony, JTextPane txtRelative, JTextPane txtAbsolute, JTextPane txtName, JTextPane txtStructure, JTextPane txtFunction, JTextPane txtComplexity) {
		txtRelative.setText(harmony.getToneNamesMapped());
		txtRelative.setCaretPosition(0);
		txtAbsolute.setText(harmony.getToneNames());
		txtAbsolute.setCaretPosition(0);
		txtName.setText(Chordanal.getHarmonyAbbreviation(harmony));
		txtName.setCaretPosition(0);
		txtStructure.setText(listToString(Chordanal.getHarmonyNamesRelative(harmony)));
		txtStructure.setCaretPosition(0);
		txtFunction.setText(listToString(Harmanal.getRootsFormatted(harmony)));
		txtFunction.setCaretPosition(0);
		txtComplexity.setText(Integer.toString(Harmanal.getHarmonyComplexity(harmony)));
		txtComplexity.setCaretPosition(0);
	}

	private void analyzeTransition(Harmony harmony1, Harmony harmony2, JTextPane txtTransition, JTextPane txtTransitionComplexity) {
		txtTransition.setText(listToString(Harmanal.getTransitionsFormatted(harmony1,harmony2)));
		txtTransition.setCaretPosition(0);
		txtTransitionComplexity.setText(Integer.toString(Harmanal.getTransitionComplexity(harmony1, harmony2)));
	}

	/* Helpers */

	private String listToString(List<String> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size()-1) {
				result += list.get(i) + "\n";
			} else {
				result += list.get(i);
			}
		}
		return result;
	}
}
