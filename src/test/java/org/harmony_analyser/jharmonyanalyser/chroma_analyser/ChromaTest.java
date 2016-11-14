package org.harmony_analyser.jharmonyanalyser.chroma_analyser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Chroma class
 */

@SuppressWarnings("UnusedAssignment")
public class ChromaTest {
	private Chroma chroma;
	private float[] chromaVector;
	private float[] wrongChromaVector;

	@Before
	public void setUp() {
		chromaVector = new float[]{ 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f };
		wrongChromaVector = new float[]{ 1f, 2f, 3f, 4f, 5f, 6f };
	}

	@Test
	public void shouldCreateChromaFromFloatArray() throws Chroma.WrongChromaSize {
		Chroma chroma = new Chroma(chromaVector);

		assertEquals(chroma.values[0], (float) 1.0, 0);
		assertEquals(chroma.values[5], (float) 6.0, 0);
	}

	@Test(expected = Chroma.WrongChromaSize.class)
	public void shouldWorkOnlyWithProperSizedArrays() throws Chroma.WrongChromaSize {
		Chroma chroma = new Chroma(wrongChromaVector);
	}
}
