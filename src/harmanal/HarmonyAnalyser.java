package harmanal;

import harmanal.vamp_plugins.*;
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;

/**
 * GUI for HarmonyAnalyser
 */

public class HarmonyAnalyser extends JFrame {
	private JTabbedPane tabbedPane1;
	private JButton selectButton;
	private JTextPane textPane1;
	private JButton playButton;
	private JButton playButton1;
	private JTextPane textPane2;
	private JButton loadPluginsButton;
	private JList list1;
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
	private JFileChooser fileChooser;

	private NNLSPlugin nnls;
	private ChordinoPlugin chordino;
	private Harmony harmony1,harmony2 = null;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		HarmonyAnalyser harmonyAnalyser = new HarmonyAnalyser();
	}

	/**
	 * Initialize the application.
	 */

	public HarmonyAnalyser() {
		/* GUI - Initialization */

		setContentPane(rootPanel);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		/* Chord Transition Tool - Initialization */

		// Obtain information about all the installed input MIDI devices
		final MidiHandler midiHandler = new MidiHandler();
		String[] inputDevices = midiHandler.getInputDeviceList();
		list1.setListData(inputDevices);

		list1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				selectButton.setEnabled(true);
			}
		});

		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				MidiDevice selectedDevice = midiHandler.getMidiDevice(list1.getSelectedValue().toString());
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
			}
		});

		captureMIDICheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
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
			}
		});

		captureMIDICheckBox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
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

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				midiHandler.play(harmony1);
			}
		});

		playButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				midiHandler.play(harmony2);
			}
		});

		/* Audio Analysis Tool - Initialization */

		loadPluginsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					nnls = new NNLSPlugin();
					textPane3.setText("VAMP Plugins loaded");
				} catch (Exception e) {
					textPane3.setText(e.getStackTrace().toString());
				}
			}
		});

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					textPane3.setText(textPane3.getText() + "\nSelected directory: " + file.getAbsolutePath());
					textField8.setText(file.getAbsolutePath());
				}
			}
		});

		extractChromasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					textPane3.setText(textPane3.getText() + "\nStep 1: Extracting chromas from audio files");
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
								textPane3.setText(textPane3.getText() + "\nAnalyzing: " + file.toString());
								try {
									nnls = new NNLSPlugin();
								} catch (Exception e) {
									e.printStackTrace();
								}
								nnls.analyze(file.toString(), file.toString() + "-chromas.txt");
								textPane3.setText(textPane3.getText() + "\nOutput saved in: " + file.toString() + "-chromas.txt");
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
		});

		segmentTrackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					textPane3.setText(textPane3.getText() + "\nStep 2: Segmenting audio tracks from given chroma files");
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
								textPane3.setText(textPane3.getText() + "\nSegmenting: " + file.toString());
								try {
									chordino = new ChordinoPlugin();
								} catch (Exception e) {
									e.printStackTrace();
								}
								chordino.analyze(file.toString(), file.toString() + "-segmentation.txt");

								textPane3.setText(textPane3.getText() + "\nSegmentation saved in: " + file.toString() + "-segmentation.txt");
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
		});

		analyzeComplexityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					textPane3.setText(textPane3.getText() + "\nStep 3: Analyzing complexity for audio files");
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
								textPane3.setText(textPane3.getText() + "\nAnalyzing: " + file.toString());

								Harmanal.analyzeSong(
									file.toString() + "-chromas.txt",
									file.toString() + "-segmentation.txt",
									file.toString() + "-result.txt",
									file.toString() + "-report.txt",
									file.toString().replaceFirst("[.][^.]+$", "") + "_vamp_nnls-chroma_chordino_simplechord.csv-timestamps.txt"
								);

								textPane3.setText(textPane3.getText() + "\nReport saved in: " + file.toString() + "-report.txt");
								textPane3.setText(textPane3.getText() + "\nResult saved in: " + file.toString() + "-result.txt");
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
