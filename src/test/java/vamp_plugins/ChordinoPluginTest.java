package vamp_plugins;

import harmanal.vamp_plugins.*;
import org.junit.*;
import java.io.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ChordinoPlugin class
 */

public class ChordinoPluginTest {
	ChordinoPlugin chordino;
	File testWavFile;

	@Before
	public void setUp() throws Exception {
		chordino = new ChordinoPlugin();
		testWavFile = new File(getClass().getResource("test.wav").getPath().toString());
	}

	@Test
	public void shouldExtractChords() {
		chordino.analyze(testWavFile.toString(), testWavFile.toString() + "-segmentation.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testWavFile.toString() + "-segmentation.txt"));
			String line = reader.readLine();
			assertEquals(line, " 0.371519274,\"N\"");
			line = reader.readLine();
			assertEquals(line, " 0.464399092,\"B\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
