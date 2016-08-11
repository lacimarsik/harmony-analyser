package org.harmony_analyser.plugins.vamp_plugins;

import org.harmony_analyser.plugins.*;
import org.junit.*;
import java.io.*;
import java.util.*;

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
		inputFiles = new ArrayList<>();
		inputFiles.add(testWavFile.toString());
	}

	@Test
	public void shouldExtractChromas() {
		try {
			nnls.analyse(inputFiles, testWavFile.toString() + "-chromas.txt");
			BufferedReader reader = new BufferedReader(new FileReader(testWavFile.toString() + "-chromas.txt"));
			String line = reader.readLine();
			assertEquals(" 0.371519274: 0.3387495 0.48584637 1.1177865 0.70092547 0.9088075 0.10086642 1.0208882 0.56879604 0.7536442 1.5223277 0.08982226 0.535174 ", line);
		} catch (IOException | AnalysisPlugin.IncorrectInput e) {
			e.printStackTrace();
		}
	}
}
