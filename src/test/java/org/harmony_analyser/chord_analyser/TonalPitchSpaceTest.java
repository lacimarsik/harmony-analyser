package org.harmony_analyser.chord_analyser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for TonalPitchSpace class
 */

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})

public class TonalPitchSpaceTest {
	private TonalPitchSpace tps;
	private Key key12, key2, key34, keyX1, keyX2;
	private Tone root1, root2, root3, root4, toneX1, toneX2, toneX3, toneX4;
	private Harmony harmony1, harmony2, harmony3, harmony4, octaveLevel, fifthsLevel, triadicLevel, diatonicLevel;

	@Before
	public void setUp() {
		// Test triplets for final TPS distance
		key12 = Chordanal.createKeyFromName("C major"); // C major key
		harmony1 = Chordanal.createHarmonyFromRelativeTones("C E G"); // C major chord
		root1 = Chordanal.getRootTone(harmony1); // C

		harmony2 = Chordanal.createHarmonyFromRelativeTones("G B D F"); // G7 chord
		root2 = Chordanal.getRootTone(harmony2); // G

		key2 = Chordanal.createKeyFromName("G major");

		key34 = Chordanal.createKeyFromName("D major"); // D major key
		harmony3 = Chordanal.createHarmonyFromRelativeTones("D F# A"); // D major chord
		root3 = Chordanal.getRootTone(harmony3); // D

		harmony4 = Chordanal.createHarmonyFromRelativeTones("D F A"); // D minor chord
		root4 = Chordanal.getRootTone(harmony4); // D

		// Additional test data
		keyX1 = Chordanal.createKeyFromName("G major"); // G major key
		keyX2 = Chordanal.createKeyFromName("Gb minor"); // Gb minor key
		toneX1 = Chordanal.createToneFromRelativeName("G");
		toneX2 = Chordanal.createToneFromRelativeName("Eb");
		toneX3 = Chordanal.createToneFromRelativeName("H#");
		toneX4 = Chordanal.createToneFromRelativeName("Cb");

		octaveLevel = Chordanal.createHarmonyFromRelativeTones("C"); // C
		fifthsLevel = Chordanal.createHarmonyFromRelativeTones("C G"); // C G
		triadicLevel = Chordanal.createHarmonyFromRelativeTones("C E G"); // C major chord
		diatonicLevel = Chordanal.createKeyFromName("C major").getScaleHarmony(); // C major scale
		// Tonal Pitch Space
		tps = new TonalPitchSpace(octaveLevel, fifthsLevel, triadicLevel, diatonicLevel);
	}

	@Test
	public void shouldGetRootDistance() {
		assertEquals(1, TonalPitchSpace.getRootDistance(root1, root2));
		assertEquals(4, TonalPitchSpace.getRootDistance(toneX1, toneX2));
		assertEquals(5, TonalPitchSpace.getRootDistance(toneX3, toneX4));
	}

	@Test
	public void shouldGetKeyDistance() {
		assertEquals(1, TonalPitchSpace.getKeyDistance(key12, keyX1));
		assertEquals(6, TonalPitchSpace.getKeyDistance(key12, keyX2));
		assertEquals(5, TonalPitchSpace.getKeyDistance(keyX1, keyX2));
	}

	@Test
	public void shouldPlotTonalPitchSpace() {
		tps.plot();
	}

	@Test
	public void shouldGetNonCommonPitchClassesDistance() {
		assertEquals(9f, TonalPitchSpace.getNonCommonPitchClassesDistance(harmony1, harmony2, key12, true), 0.01);
		assertEquals(3f, TonalPitchSpace.getNonCommonPitchClassesDistance(harmony3, harmony4, key34, true), 0.01);
	}

	@Test
	public void shouldGetTPSDistance() {
		assertEquals(10f, TonalPitchSpace.getTPSDistance(harmony1, root1, key12, harmony2, root2, key12, true), 0.01);
		assertEquals(3f, TonalPitchSpace.getTPSDistance(harmony3, root3, key34, harmony4, root4, key34, true), 0.01);

		// Symmetry test
		assertEquals(11.5f, TonalPitchSpace.getTPSDistance(harmony1, root1, key12, harmony2, root2, key2, true), 0.01);
		assertEquals(11.5f, TonalPitchSpace.getTPSDistance(harmony2, root2, key2, harmony1, root1, key12, true), 0.01);
	}
}
