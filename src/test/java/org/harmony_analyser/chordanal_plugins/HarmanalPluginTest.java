package org.harmony_analyser.chordanal_plugins;

import org.harmony_analyser.vamp_plugins.*;
import org.vamp_plugins.*;

import org.junit.*;
import java.io.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Harmanal class
 */

@SuppressWarnings("ConstantConditions")

public class HarmanalPluginTest {
	private File testWavFile, testReportFixture;

	@Before
	public void setUp() {
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getFile());
		testReportFixture = new File(classLoader.getResource("test-reportFixture.txt").getFile());
	}

	@Test
	public void shouldCreateReportAndResult() {
		try {
			new NNLSPlugin().analyze(testWavFile.toString(), testWavFile.toString() + "-chromas.txt");
			new ChordinoPlugin().analyze(testWavFile.toString(), testWavFile.toString() + "-segmentation.txt");
			new HarmanalPlugin().analyzeSong(
				testWavFile.toString() + "-chromas.txt",
				testWavFile.toString() + "-segmentation.txt",
				testWavFile.toString() + "-report.txt"
			);
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
		} catch (IOException | HarmanalPlugin.IncorrectInput | PluginLoader.LoadFailedException e) {
			e.printStackTrace();
		}
	}
}
