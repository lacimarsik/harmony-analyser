package harmanal.vamp_plugins;

import org.harmony_analyser.vamp_plugins.ChordinoPlugin;
import org.junit.*;
import java.io.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ChordinoPlugin class
 */

@SuppressWarnings("ConstantConditions")

public class ChordinoPluginTest {
	private ChordinoPlugin chordino;
	private File testWavFile;

	@Before
	public void setUp() throws Exception {
		chordino = new ChordinoPlugin();
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getPath());
	}

	@Test
	public void shouldExtractChords() {
		chordino.analyze(testWavFile.toString(), testWavFile.toString() + "-segmentation.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testWavFile.toString() + "-segmentation.txt"));
			String line = reader.readLine();
			assertEquals(" 0.371519274: N", line);
			line = reader.readLine();
			assertEquals(" 0.464399092: B", line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
