package harmanal;

import java.awt.*;
import java.awt.event.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * GUI for Harmanal
 */

public class Application {
	
	// Maximum number of shown MIDI input devices
	public static final int MAX_DEVICES = 5;

	private JFrame frameHarmanal;
	private JTextField txtInputFile;
	private JTextField txtOutputChromas;
	
	private NNLSPlugin nnls;
	private Harmony harmony1,harmony2 = null;

	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frameHarmanal.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	public Application() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		
		// Obtain information about all the installed input MIDI devices (maximum: MAX_DEVICES)		
		
		final MidiHandler midiHandler = new MidiHandler(); 
		String[] inputDevices = midiHandler.getInputDeviceList();
		
		// Build GUI
		
		frameHarmanal = new JFrame();
		frameHarmanal.setBounds(100, 100, 850, 600);
		frameHarmanal.setTitle("Harmanal");
		frameHarmanal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frameHarmanal.getContentPane().setLayout(new BorderLayout(0, 0));
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frameHarmanal.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		final JPanel panelTransition = new JPanel();
		tabbedPane.addTab("Chord Transition tool", null, panelTransition, null);
		panelTransition.setLayout(null);
		
		final JLabel lblInputDevice = new JLabel("MIDI Input device");
		lblInputDevice.setBounds(18, 32, 125, 15);
		panelTransition.add(lblInputDevice);
			
		final Label lblAvailable = new Label("Available MIDI input devices:");
		lblAvailable.setBounds(18, 51, 189, 21);
		panelTransition.add(lblAvailable);
		
		final JScrollPane scrListDevices = new JScrollPane();
		scrListDevices.setBounds(213, 51, 189, 61);
		panelTransition.add(scrListDevices);
		final JList listDevices = new JList(inputDevices);
		listDevices.setToolTipText("Please select from the available MIDI input devices");
		listDevices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listDevices.setBounds(212, 51, 182, 61);
		scrListDevices.setViewportView(listDevices);
		
		final JButton btnSelect = new JButton("Select");
		btnSelect.setEnabled(false);
		btnSelect.setToolTipText("Confirm selection of a device");
		btnSelect.setBounds(406, 51, 98, 25);
		panelTransition.add(btnSelect);
		
		final JScrollPane scrDevice = new JScrollPane();
		scrDevice.setBounds(516, 51, 295, 61);
		panelTransition.add(scrDevice);
		final JTextPane txtDevice = new JTextPane();
		txtDevice.setToolTipText("Active MIDI input device");
		scrDevice.setViewportView(txtDevice);
		
		final JLabel lblChordSelection = new JLabel("Chord Selection");
		lblChordSelection.setBounds(18, 135, 125, 15);
		panelTransition.add(lblChordSelection);
		
		final JLabel lblMidiInput = new JLabel("MIDI Input:");
		lblMidiInput.setFont(new Font("Dialog", Font.BOLD, 12));
		lblMidiInput.setBounds(183, 173, 80, 15);
		panelTransition.add(lblMidiInput);
		
		final JLabel lblRelative = new JLabel("Relative Input:");
		lblRelative.setFont(new Font("Dialog", Font.BOLD, 12));
		lblRelative.setBounds(156, 198, 112, 15);
		panelTransition.add(lblRelative);
		
		final JLabel lblAbsolute = new JLabel("Absolute Input:");
		lblAbsolute.setFont(new Font("Dialog", Font.BOLD, 12));
		lblAbsolute.setBounds(150, 221, 112, 15);
		panelTransition.add(lblAbsolute);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblName.setBounds(219, 244, 189, 15);
		panelTransition.add(lblName);
		
		final JLabel lblStructure = new JLabel("Structure:");
		lblStructure.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblStructure.setBounds(196, 266, 189, 15);
		panelTransition.add(lblStructure);
		
		final JLabel lblFunction = new JLabel("Function:");
		lblFunction.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblFunction.setBounds(201, 313, 68, 15);
		panelTransition.add(lblFunction);
		
		JLabel lblComplexity = new JLabel("Complexity:");
		lblComplexity.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblComplexity.setBounds(188, 360, 80, 15);
		panelTransition.add(lblComplexity);
		
		final JLabel lblChord1 = new JLabel("Chord 1");
		lblChord1.setBounds(293, 144, 189, 23);
		panelTransition.add(lblChord1);
		lblChord1.setHorizontalAlignment(SwingConstants.CENTER);
		
		final JToggleButton tglbtnCapture1 = new JToggleButton("Capture");
		tglbtnCapture1.setEnabled(false);
		tglbtnCapture1.setBounds(293, 169, 189, 25);
		panelTransition.add(tglbtnCapture1);
		
		final JScrollPane scrRelative1 = new JScrollPane();
		scrRelative1.setBounds(293, 194, 189, 23);
		panelTransition.add(scrRelative1);
		final JTextPane txtRelative1 = new JTextPane();
		scrRelative1.setViewportView(txtRelative1);
		
		final JScrollPane scrAbsolute1 = new JScrollPane();
		scrAbsolute1.setBounds(293, 217, 189, 23);
		panelTransition.add(scrAbsolute1);
		final JTextPane txtAbsolute1 = new JTextPane();
		scrAbsolute1.setViewportView(txtAbsolute1);
		
		final JScrollPane scrStructure1 = new JScrollPane();
		scrStructure1.setBounds(293, 263, 189, 46);
		panelTransition.add(scrStructure1);
		final JTextPane txtStructure1 = new JTextPane();
		scrStructure1.setViewportView(txtStructure1);
		
		final JScrollPane scrName1 = new JScrollPane();
		scrName1.setBounds(293, 240, 189, 23);
		panelTransition.add(scrName1);
		final JTextPane txtName1 = new JTextPane();
		scrName1.setViewportView(txtName1);
		
		final JScrollPane scrFunction1 = new JScrollPane();
		scrFunction1.setBounds(293, 310, 189, 46);
		panelTransition.add(scrFunction1);
		final JTextPane txtFunction1 = new JTextPane();
		scrFunction1.setViewportView(txtFunction1);
		
		final JScrollPane scrComplexity1 = new JScrollPane();
		scrComplexity1.setBounds(293, 356, 189, 23);
		panelTransition.add(scrComplexity1);
		final JTextPane txtComplexity1 = new JTextPane();
		scrComplexity1.setViewportView(txtComplexity1);
		
		final JButton btnPlay1 = new JButton("Play");
		btnPlay1.setEnabled(false);
		btnPlay1.setBounds(293, 381, 189, 23);
		panelTransition.add(btnPlay1);					
		
		final JLabel lblChord2 = new JLabel("Chord 2");
		lblChord2.setBounds(519, 144, 189, 23);
		panelTransition.add(lblChord2);
		lblChord2.setHorizontalAlignment(SwingConstants.CENTER);
		
		final JToggleButton tglbtnCapture2 = new JToggleButton("Capture");
		tglbtnCapture2.setEnabled(false);
		tglbtnCapture2.setBounds(519, 169, 189, 25);
		panelTransition.add(tglbtnCapture2);
		
		final JScrollPane scrRelative2 = new JScrollPane();
		scrRelative2.setBounds(519, 194, 189, 23);
		panelTransition.add(scrRelative2);
		final JTextPane txtRelative2 = new JTextPane();
		scrRelative2.setViewportView(txtRelative2);
		
		final JScrollPane scrAbsolute2 = new JScrollPane();
		scrAbsolute2.setBounds(519, 217, 189, 23);
		panelTransition.add(scrAbsolute2);
		final JTextPane txtAbsolute2 = new JTextPane();
		scrAbsolute2.setViewportView(txtAbsolute2);
		
		final JScrollPane scrName2 = new JScrollPane();
		scrName2.setBounds(519, 240, 189, 23);
		panelTransition.add(scrName2);
		final JTextPane txtName2 = new JTextPane();
		scrName2.setViewportView(txtName2);
		
		final JScrollPane scrStructure2 = new JScrollPane();
		scrStructure2.setBounds(519, 263, 189, 46);
		panelTransition.add(scrStructure2);
		final JTextPane txtStructure2 = new JTextPane();
		scrStructure2.setViewportView(txtStructure2);
		
		final JScrollPane scrFunction2 = new JScrollPane();
		scrFunction2.setBounds(519, 310, 189, 46);
		panelTransition.add(scrFunction2);
		final JTextPane txtFunction2 = new JTextPane();
		scrFunction2.setViewportView(txtFunction2);
		
		final JScrollPane scrComplexity2 = new JScrollPane();
		scrComplexity2.setBounds(519, 356, 189, 23);
		panelTransition.add(scrComplexity2);
		final JTextPane txtComplexity2 = new JTextPane();
		scrComplexity2.setViewportView(txtComplexity2);
		
		final JButton btnPlay2 = new JButton("Play");
		btnPlay2.setEnabled(false);
		btnPlay2.setBounds(519, 381, 189, 23);
		panelTransition.add(btnPlay2);
		
		final JLabel lblResults = new JLabel("Results");
		lblResults.setBounds(18, 420, 125, 15);
		panelTransition.add(lblResults);
		
		final JLabel lblTransition = new JLabel("Transition:");
		lblTransition.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblTransition.setBounds(192, 431, 80, 15);
		panelTransition.add(lblTransition);
		
		final JScrollPane scrTransition = new JScrollPane();
		scrTransition.setBounds(293, 428, 415, 46);
		panelTransition.add(scrTransition);
		final JTextPane txtTransition = new JTextPane();
		scrTransition.setViewportView(txtTransition);
		
		JLabel lblTransitionComplexity = new JLabel("Complexity:");
		lblTransitionComplexity.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblTransitionComplexity.setBounds(188, 477, 80, 15);
		panelTransition.add(lblTransitionComplexity);
		
		final JScrollPane scrTransitionComplexity = new JScrollPane();
		scrTransitionComplexity.setBounds(293, 474, 415, 23);
		panelTransition.add(scrTransitionComplexity);
		final JTextPane txtTransitionComplexity = new JTextPane();
		scrTransitionComplexity.setViewportView(txtTransitionComplexity);

		final JPanel panelAudio = new JPanel();
		tabbedPane.addTab("Audio Analysis tool", null, panelAudio, null);
		panelAudio.setLayout(null);
		
		final JLabel lblVampPlugins = new JLabel("VAMP plugins");
		lblVampPlugins.setBounds(18, 32, 106, 15);
		panelAudio.add(lblVampPlugins);
		
		final JButton btnLoadPlugins = new JButton("Load Plugins");
		btnLoadPlugins.setBounds(28, 59, 149, 25);
		panelAudio.add(btnLoadPlugins);
		
		final JLabel lblVampPluginsNeed = new JLabel("Note: VAMP plugins need to be installed on Your computer. See the installation instructions.");
		lblVampPluginsNeed.setFont(new Font("Dialog", Font.ITALIC, 12));
		lblVampPluginsNeed.setBounds(28, 96, 598, 15);
		panelAudio.add(lblVampPluginsNeed);
		
		final JLabel lblInputFile = new JLabel("Input file");
		lblInputFile.setBounds(18, 135, 70, 15);
		panelAudio.add(lblInputFile);
		
		txtInputFile = new JTextField();
		txtInputFile.setText("Baywatch.wav");
		txtInputFile.setBounds(28, 161, 183, 19);
		panelAudio.add(txtInputFile);
		txtInputFile.setColumns(10);
		
		final JLabel lblOutputChromas = new JLabel("Output file");
		lblOutputChromas.setBounds(18, 205, 130, 15);
		panelAudio.add(lblOutputChromas);
		
		txtOutputChromas = new JTextField();
		txtOutputChromas.setText("output.txt");
		txtOutputChromas.setBounds(28, 232, 183, 19);
		panelAudio.add(txtOutputChromas);
		txtOutputChromas.setColumns(10);
		
		final JButton btnAnalyze = new JButton("Analyze");
		btnAnalyze.setBounds(28, 274, 149, 25);
		panelAudio.add(btnAnalyze);
		
		// User actions
		
		listDevices.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				btnSelect.setEnabled(true);
			}
		});
		
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				MidiDevice selectedDevice = midiHandler.getMidiDevice(listDevices.getSelectedValue().toString());
				txtDevice.setText("device: " + selectedDevice.getDeviceInfo().getName() +
						          "\nvendor: " + selectedDevice.getDeviceInfo().getVendor().substring(0, selectedDevice.getDeviceInfo().getVendor().indexOf(" "))+
						          "\ndescription: " + selectedDevice.getDeviceInfo().getDescription().substring(0, selectedDevice.getDeviceInfo().getDescription().indexOf(",")));
				
				midiHandler.close();
				midiHandler.initialize(null, null, selectedDevice, null, null);
				midiHandler.connectInputSynthesizer();
				
				tglbtnCapture1.setEnabled(true);
				tglbtnCapture2.setEnabled(true);
				tglbtnCapture1.setSelected(false);
				tglbtnCapture2.setSelected(false);
				btnPlay1.setEnabled(false);
				btnPlay2.setEnabled(false);
			}
		});
		
		tglbtnCapture1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (tglbtnCapture1.isSelected()) {
						midiHandler.connectInputDecoder();
						tglbtnCapture2.setEnabled(false);
					} else {
						midiHandler.inputDevice.close();
						harmony1 =  midiHandler.getBufferHarmony();
						if (harmony1 != null) {
							analyzeHarmony(harmony1,txtRelative1,txtAbsolute1,txtName1,txtStructure1,txtFunction1,txtComplexity1);
							btnPlay1.setEnabled(true);
							if (harmony2 != null) {
								analyzeTransition(harmony1,harmony2,txtTransition,txtTransitionComplexity);
							}
						}
						midiHandler.decoder.close();
						midiHandler.inputDevice.open();
						midiHandler.connectInputSynthesizer();
						tglbtnCapture2.setEnabled(true);
					}
				} catch (MidiUnavailableException e) {

					e.printStackTrace();
				}
			}
		});
		
		tglbtnCapture2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (tglbtnCapture2.isSelected()) {
						midiHandler.connectInputDecoder();
						tglbtnCapture1.setEnabled(false);
					} else {
						midiHandler.inputDevice.close();		
						harmony2 = midiHandler.getBufferHarmony();
						if (harmony2 != null) { 
							analyzeHarmony(harmony2,txtRelative2,txtAbsolute2,txtName2,txtStructure2,txtFunction2,txtComplexity2);
							btnPlay2.setEnabled(true);
							if (harmony1 != null) {
								analyzeTransition(harmony1,harmony2,txtTransition,txtTransitionComplexity);
							}
						}
						midiHandler.decoder.close();
						midiHandler.inputDevice.open();
						midiHandler.connectInputSynthesizer();
						tglbtnCapture1.setEnabled(true);
					}
				} catch (MidiUnavailableException e) {

					e.printStackTrace();
				}
			}
		});
		
		txtRelative1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				
				harmony1 = Chordanal.createHarmonyFromRelativeTones(txtRelative1.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1,txtRelative1,txtAbsolute1,txtName1,txtStructure1,txtFunction1,txtComplexity1);
					btnPlay1.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2,txtTransition,txtTransitionComplexity);
					}
				} else {
					txtRelative1.setText("");
					txtAbsolute1.setText("");
				}
				
			}
		});
		
		txtAbsolute1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				
				harmony1 = Chordanal.createHarmonyFromTones(txtAbsolute1.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1,txtRelative1,txtAbsolute1,txtName1,txtStructure1,txtFunction1,txtComplexity1);
					btnPlay1.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2,txtTransition,txtTransitionComplexity);
					}
				} else {
					txtRelative1.setText("");
					txtAbsolute1.setText("");
				}
			}
		});
		
		txtRelative2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				
				harmony2 = Chordanal.createHarmonyFromRelativeTones(txtRelative2.getText());
				if (harmony2 != null) { 
					analyzeHarmony(harmony2,txtRelative2,txtAbsolute2,txtName2,txtStructure2,txtFunction2,txtComplexity2);
					btnPlay2.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2,txtTransition,txtTransitionComplexity);
					}
				} else {
					txtRelative2.setText("");
					txtAbsolute2.setText("");
				}
				
			}
		});
		
		txtAbsolute2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				
				harmony2 = Chordanal.createHarmonyFromTones(txtAbsolute2.getText());
				if (harmony2 != null) { 
					analyzeHarmony(harmony2,txtRelative2,txtAbsolute2,txtName2,txtStructure2,txtFunction2,txtComplexity2);
					btnPlay2.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2,txtTransition,txtTransitionComplexity);
					}
				} else {
					txtRelative2.setText("");
					txtAbsolute2.setText("");
				}
			}
		});
		
		btnPlay1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				midiHandler.play(harmony1);
			}
		});
		
		btnPlay2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				midiHandler.play(harmony2);
			}
		});
		
		btnLoadPlugins.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				nnls = new NNLSPlugin();
				
			}
		});
		
		btnAnalyze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
				    Path startPath = Paths.get("/mnt/work/school/mff/Experiments/Bordeaux/new");
				    Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
				        @Override
				        public FileVisitResult preVisitDirectory(Path dir,
				                BasicFileAttributes attrs) {
				            System.out.println("Dir: " + dir.toString());
				            return FileVisitResult.CONTINUE;
				        }

				        @Override
				        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				        	
				        	if (file.toString().endsWith(".wav")) {
					        	System.out.println("Analyzing: " + file.toString());
					        	nnls = new NNLSPlugin();
					        	nnls.analyze(file.toString(), file.toString() + "-chromas.txt");
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
				
				
				//String genre = "Electronic";
				//String song = "01 - Andrew Bayer - Nexus 6";
				
				//nnls.analyze("NS/" + genre + "/" + song + ".wav", "NS/" + genre + "/" + song + "-chromas.txt");
				
				//String genre = "ROCK";
				//String artist = "Pink Floyd";
				//String song = "GoodbyeBlueSky";
				//nnls.analyze("Experiments/" + genre + "/" + artist + "/" + song + ".wav", "Experiments/" + genre + "/" + artist + "/" + song + "-chromas.txt");
				
				//Harmanal.anal("resources/" + temp,"resources/" + temp,txtOutputChromas.getText());
			}
		});
				
	}
	
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
}
