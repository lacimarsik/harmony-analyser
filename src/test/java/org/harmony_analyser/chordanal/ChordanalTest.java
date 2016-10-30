package org.harmony_analyser.chordanal;

import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Chordanal class
 */

@SuppressWarnings("ConstantConditions")

public class ChordanalTest {
	private Key key1, key2;

	@Before
	public void setUp() {
		key1 = new Key(0, Chordanal.MAJOR);
		key2 = new Key(0, Chordanal.MINOR);
	}

	@Test
	public void factoryMethodsShouldCreateEntities() {
		assertEquals(Chordanal.createToneFromName("Gb4").getName(), "F#4");
		assertEquals(Chordanal.createToneFromRelativeName("G").getName(), "G3");
		assertEquals(Chordanal.createHarmonyFromTones("C4 D4 E4 F4").getToneNames(), "C4 D4 E4 F4 ");
		assertEquals(Chordanal.createHarmonyFromRelativeTones("C D E F").getToneNames(), "C3 D3 E3 F3 ");
		assertEquals(Chordanal.createKeyFromName("A# minor").root,10);
		assertEquals(Chordanal.createKeyFromName("A# minor").keyType,Chordanal.MINOR);
	}

	@Test
	public void shouldGetAbbreviationsFromHarmonies() {
		assertEquals(Chordanal.getHarmonyAbbreviationsRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")).get(1), "M3,P5");
		assertEquals(Chordanal.getHarmonyAbbreviationIntervals(Chordanal.createHarmonyFromTones("C4 E4 G4")), "M3,P5");
		assertEquals(Chordanal.getHarmonyAbbreviationRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")), "maj5");
		assertEquals(Chordanal.getHarmonyAbbreviation(Chordanal.createHarmonyFromTones("C4 E4 G4")), "Cmaj5");
		assertEquals(Chordanal.getHarmonyAbbreviation(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp()), "Cmaj6");
		assertEquals(Chordanal.getHarmonyAbbreviation(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp().inversionUp()), "Cmaj6-4");
	}

	@Test
	public void shouldGetAbbreviationsFromKey() {
		assertEquals(Chordanal.getKeyAbbreviation(key1), "Cmajor");
	}

	@Test
	public void shouldGetNamesFromHarmonies() {
		assertEquals(Chordanal.getHarmonyNamesRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")).get(1), "major third,perfect fifth");
		assertEquals(Chordanal.getHarmonyNameIntervals(Chordanal.createHarmonyFromTones("C4 E4 G4")), "major third,perfect fifth");
		assertEquals(Chordanal.getHarmonyNameRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")), "major triad");
		assertEquals(Chordanal.getHarmonyName(Chordanal.createHarmonyFromTones("C4 E4 G4")), "C major triad");
		assertEquals(Chordanal.getHarmonyName(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp()), "C major sixth chord");
		assertEquals(Chordanal.getHarmonyName(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp().inversionUp()), "C major six-four chord");
		assertEquals(Chordanal.getKeyName(key1), "C major");
	}

	@Test
	public void shouldGetCharacter() {
		assertEquals(Chordanal.getHarmonyCharacter(Chordanal.createHarmonyFromTones("C4 E4 G4")), "consonant");
	}

	@Test
	public void shouldGetFifthIntervalFromHarmony() {
		assertEquals(Chordanal.getFifthToneFromHarmony(Chordanal.createHarmonyFromTones("E4 G4 C5")).getNameMapped(), "G");
	}

	@Test
	public void shouldGetRootTone() {
		assertEquals(Chordanal.getRootTone(Chordanal.createHarmonyFromTones("E4 G4 C5")).getNameMapped(), "C");
	}

	@Test
	public void shouldGetScaleFromKey() {
		assertEquals(Chordanal.getKeyScale(key1), "C D E F G A B ");
		assertEquals(Chordanal.getKeyScale(key2), "C D D# F G G# A# ");
	}
}
