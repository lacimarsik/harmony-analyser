package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.plugins.AnalysisPlugin;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.*;

import org.junit.*;
import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Harmanal class
 */

@SuppressWarnings("ConstantConditions")

public class TransitionComplexityPluginTest {
	private File testWavFile, testReportFixture;
	private List<String> inputFilesVamp, inputFilesComplexity;

	@Before
	public void setUp() {
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getFile());
		testReportFixture = new File(classLoader.getResource("test-reportFixture.txt").getFile());
	}

	@Test
	public void shouldCreateReport() {
		try {
			new NNLSPlugin().analyse(testWavFile.toString(), true);
			new ChordinoPlugin().analyse(testWavFile.toString(), true);
			new TransitionComplexityPlugin().analyse(testWavFile.toString(), true);
			BufferedReader readerReport = new BufferedReader(new FileReader(testWavFile.toString() + "-report.txt"));
			BufferedReader readerFixture = new BufferedReader(new FileReader(testReportFixture));
			StringBuilder reportString = new StringBuilder();
			StringBuilder fixtureString = new StringBuilder();
			String line;
			while ((line = readerReport.readLine()) != null) {
				reportString.append(line);
			}
			while ((line = readerFixture.readLine()) != null) {
				fixtureString.append(line);
			}
			assertEquals(fixtureString.toString(), reportString.toString());
		} catch (IOException | AnalysisPlugin.IncorrectInputException | PluginLoader.LoadFailedException | AnalysisPlugin.OutputAlreadyExists e) {
			e.printStackTrace();
		}
	}
}
