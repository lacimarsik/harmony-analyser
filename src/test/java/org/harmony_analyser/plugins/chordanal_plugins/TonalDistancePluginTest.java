package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.AnalysisPlugin;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.junit.Before;
import org.junit.Test;
import org.vamp_plugins.PluginLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for TonalDistancePlugin class
 */

@SuppressWarnings("ConstantConditions")

public class TonalDistancePluginTest {
	private File testWavFile, testReportFixture;
	private List<String> inputFilesVamp, inputFilesComplexity;

	@Before
	public void setUp() {
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getFile());
		testReportFixture = new File(classLoader.getResource("test-tonalDistanceFixture.txt").getFile());
	}

	@Test
	public void shouldCreateReport() throws IOException, AnalysisPlugin.IncorrectInputException, PluginLoader.LoadFailedException, AnalysisPlugin.OutputAlreadyExists, Chroma.WrongChromaSize {
		new NNLSPlugin().analyse(testWavFile.toString(), true, false);
		new ChordinoPlugin().analyse(testWavFile.toString(), true, false);
		new TonalDistancePlugin().analyse(testWavFile.toString(), true, false);
		BufferedReader readerReport = new BufferedReader(new FileReader(testWavFile.toString() + "-tonal-distance.txt"));
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
