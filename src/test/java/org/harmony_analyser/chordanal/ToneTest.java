package org.harmony_analyser.chordanal;

import org.harmony_analyser.chordanal.Tone;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Tone class
 */

public class ToneTest {
	private Tone tone;

	@Before
	public void setUp() {
		tone = new Tone(70, 100);
	}

	@Test
	public void shouldContainToneAndVolume() {
		assertEquals(tone.getNumber(), 70);
		assertEquals(tone.getNumberMapped(), 10);
		assertEquals(tone.getVolume(), 100);
	}

	@Test
	public void chromatizeShouldAlterTone() {
		tone.chromatizeUp();
		assertEquals(tone.getNumber(), 71);
		tone.chromatizeDown();
		assertEquals(tone.getNumber(), 70);
	}
}
