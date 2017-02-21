package org.harmony_analyser.jharmonyanalyser.chroma_analyser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Chromanal class
 */

@SuppressWarnings("ConstantConditions")

public class ChromanalTest {
	private Chroma chroma1, chroma2;

	@Before
	public void setUp() throws Chroma.WrongChromaSize {
		chroma1 = new Chroma(new float[]{ 1f, 0.3f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f }); // C E G
		chroma2 = new Chroma(new float[]{ 1f, 0f, 0.5f, 0f, 0f, 0f, 0f, 0.8f, 0f, 0f, 0f, 0f }); // C D G
	}

	@Test
	public void shouldGetChromaComplexitySimple() {
		// C#: 0.3 => 0
		// D: 0 => 0.5
		// E: 1 => 0
		// G: 1 => 0.8
		// Sum of Deltas: 2.0
		assertEquals(2.0, Chromanal.getChromaComplexitySimple(chroma1, chroma2), 0.01);
	}

	@Test
	public void shouldGetChromaComplexityTonal() throws Chroma.WrongChromaSize {
		// COMMON ROOT HARMONY: C, G (ignoring)
		// C#: 0.3 => 0
		// D: 0.5 => 0
		// E: 1 => 0
		// Sum of Deltas: 1.8
		assertEquals(1.8, Chromanal.getChromaComplexityTonal(chroma1, chroma2, 0.07f, true), 0.01);
	}
}
