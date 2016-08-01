package vamp_plugins;

import harmanal.vamp_plugins.*;
import org.junit.*;
import java.io.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for NNLSPlugin class
 */

public class NNLSPluginTest {
	NNLSPlugin nnls;
	File testWavFile;

	@Before
	public void setUp() throws Exception {
		nnls = new NNLSPlugin();
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getPath().toString());
	}

	@Test
	public void shouldExtractChromas() {
		nnls.analyze(testWavFile.toString(), testWavFile.toString() + "-chromas.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testWavFile.toString() + "-chromas.txt"));
			String line = reader.readLine();
			assertEquals(line, " 0.185759637: 0.11778839 0.5713705 2.2835577 0.5476588 0.25853446 0.15571728 1.1885182 0.6510234 0.6329706 1.3712279 0.22570816 0.1900833 ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
