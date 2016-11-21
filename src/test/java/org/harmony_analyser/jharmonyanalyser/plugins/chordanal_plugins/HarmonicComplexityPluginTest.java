package org.harmony_analyser.jharmonyanalyser.plugins.chordanal_plugins;

import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.plugins.AnalysisPlugin;
import org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins.*;
import org.vamp_plugins.*;

import org.junit.*;
import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for AverageChordComplexityDistancePlugin class
 */

@SuppressWarnings("ConstantConditions")

public class HarmonicComplexityPluginTest {
	private File testWavFile, testReportFixture;
	private List<String> inputFilesVamp, inputFilesComplexity;

	@Before
	public void setUp() {
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getFile());
		testReportFixture = new File(classLoader.getResource("test-harmonicComplexityFixture.txt").getFile());
	}

	@Test
	public void shouldCreateReport() throws IOException, AudioAnalyser.IncorrectInputException, PluginLoader.LoadFailedException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		new NNLSPlugin().analyse(testWavFile.toString(), true, false);
		new ChordinoLabelsPlugin().analyse(testWavFile.toString(), true, false);
		new AverageChordComplexityDistancePlugin().analyse(testWavFile.toString(), true, false);
		BufferedReader readerReport = new BufferedReader(new FileReader(testWavFile.toString() + "-harmonic-complexity.txt"));
		BufferedReader readerFixture = new BufferedReader(new FileReader(testReportFixture));
		StringBuilder reportString = new StringBuilder();
		StringBuilder fixtureString = new StringBuilder();
		String line;
		while ((line = readerReport.readLine()) != null) { // Check for null is valid
			reportString.append(line);
		}
		while ((line = readerFixture.readLine()) != null) { // Check for null is valid
			fixtureString.append(line);
		}
		assertEquals(fixtureString.toString(), reportString.toString());
	}
}
