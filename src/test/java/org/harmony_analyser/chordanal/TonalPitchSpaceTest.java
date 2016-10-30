package org.harmony_analyser.chordanal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for TonalPitchSpace class
 */

@SuppressWarnings("ConstantConditions")

public class TonalPitchSpaceTest {
	private Key key1, key2, keyX1, keyX2;
	private Tone root1, root2, toneX1, toneX2, toneX3, toneX4;
	private Harmony harmony1, harmony2;

	@Before
	public void setUp() {
		// Test triplets for final TPS distance
		key1 = new Key(0, Chordanal.MAJOR); // C major key
		harmony1 = Chordanal.createHarmonyFromTones("C4 E4 G4"); // C major chord
		root1 = Chordanal.getRootTone(harmony1); // C

		key2 = new Key(0, Chordanal.MAJOR); // C major key
		harmony2 = Chordanal.createHarmonyFromTones("G4 B4 D5 F5"); // G7 chord
		root2 = Chordanal.getRootTone(harmony2); // G

		// Additional test data
		keyX1 = new Key(7, Chordanal.MAJOR); // G major key
		keyX2 = new Key(6, Chordanal.MINOR); // Gb minor key
		toneX1 = Chordanal.createToneFromRelativeName("G");
		toneX2 = Chordanal.createToneFromRelativeName("Eb");
		toneX3 = Chordanal.createToneFromRelativeName("H#");
		toneX4 = Chordanal.createToneFromRelativeName("Cb");
	}

	@Test
	public void shouldGetRootDistance() {
		assertEquals(1, TonalPitchSpace.getRootDistance(root1, root2));
		assertEquals(4, TonalPitchSpace.getRootDistance(toneX1, toneX2));
		assertEquals(5, TonalPitchSpace.getRootDistance(toneX3, toneX4));
	}

	@Test
	public void shouldGetKeyDistance() {
		assertEquals(1, TonalPitchSpace.getKeyDistance(key1, keyX1));
		assertEquals(6, TonalPitchSpace.getKeyDistance(key1, keyX2));
		assertEquals(5, TonalPitchSpace.getKeyDistance(keyX1, keyX2));
	}

	@Test
	public void shouldGetNonCommonPitchClassesDistance() {
		assertEquals(4.5f, TonalPitchSpace.getNonCommonPitchClassesDistance(harmony1, harmony2, key1), 0.01);
	}
}
