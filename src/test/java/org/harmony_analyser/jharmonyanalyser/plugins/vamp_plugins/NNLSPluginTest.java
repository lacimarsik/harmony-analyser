package org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins;

import org.harmony_analyser.jharmonyanalyser.services.*;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.junit.*;
import java.io.*;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for NNLSPlugin class
 */

@SuppressWarnings("ConstantConditions")

public class NNLSPluginTest {
	private NNLSPlugin nnls;
	private File testWavFile;
	private List<String> inputFiles;

	@Before
	public void setUp() throws Exception {
		nnls = new NNLSPlugin();
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getPath());
	}

	@Test
	public void shouldExtractChromas() throws IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		nnls.analyse(testWavFile.toString(), true);
		BufferedReader reader = new BufferedReader(new FileReader(testWavFile.toString() + "-chromas.txt"));
		String line = reader.readLine();
		float timestamp = AudioAnalysisHelper.getTimestampFromLine(line);
		String chromaString = AudioAnalysisHelper.getLabelFromLine(line);
		Scanner scanner = new Scanner(chromaString);
		float[] chroma = new float[12];
		for (int i = 0; i < chroma.length; i++) {
			assert(scanner.hasNextFloat());
			chroma[i] = scanner.nextFloat();
		}

		assertEquals(timestamp, 0.371519274f, 0.000001f);
		assertArrayEquals(chroma, new float[]{ 0.3387495f, 0.48584637f, 1.1177865f, 0.70092547f, 0.9088075f, 0.10086642f, 1.0208882f, 0.56879604f, 0.7536442f, 1.5223277f, 0.08982226f, 0.535174f }, 0.000001f);
	}
}