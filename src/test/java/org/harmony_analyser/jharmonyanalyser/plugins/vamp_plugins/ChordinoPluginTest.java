package org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins;

import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.plugins.*;
import org.junit.*;
import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ChordinoLabelsPlugin class
 */

@SuppressWarnings("ConstantConditions")

public class ChordinoPluginTest {
	private ChordinoLabelsPlugin chordino;
	private File testWavFile;
	private List<String> inputFiles;

	@Before
	public void setUp() throws Exception {
		chordino = new ChordinoLabelsPlugin();
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getPath());
	}

	@Test
	public void shouldExtractChords() throws IOException, AudioAnalyser.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, Chroma.WrongChromaSize {
		chordino.analyse(testWavFile.toString(), true, false);
		BufferedReader reader = new BufferedReader(new FileReader(testWavFile.toString() + "-chordino-labels.txt"));
		String line = reader.readLine();
		assertEquals(" 0.371519274: N", line);
		line = reader.readLine();
		assertEquals(" 0.464399092: B", line);
	}
}
