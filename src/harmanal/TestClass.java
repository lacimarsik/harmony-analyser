package harmanal;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit testing
 */

public class TestClass {

	Harmony harmony1, harmony2, harmony3, harmony4, harmony5;
	Key key1, key2;
	
	@Before
	public void setUp() throws Exception {
		int[] array1 = {60};
		int[] array2 = {60, 64};
		int[] array3 = {60, 64, 67};
		int[] array4 = {60, 64, 67, 70};
		int[] array5 = {60, 64, 67, 70, 72};
		harmony1 = new Harmony(array1);
		harmony2 = new Harmony(array2);
		harmony3 = new Harmony(array3);
		harmony4 = new Harmony(array4);
		harmony5 = new Harmony(array5);
		
		key1 = new Key(0,Chordanal.MAJOR);
		key2 = new Key(0,Chordanal.MINOR);
	}

	@Test
	public void testHarmony() {
		assertEquals(harmony1.getToneNames(),"C4 ");
		assertEquals(harmony1.getToneNamesMapped(),"C ");
		assertEquals(harmony2.getToneNames(),"C4 E4 ");
		assertEquals(harmony2.getToneNamesMapped(),"C E ");
		assertEquals(harmony3.getToneNames(),"C4 E4 G4 ");
		assertEquals(harmony3.getToneNamesMapped(),"C E G ");
		assertEquals(harmony4.getToneNames(),"C4 E4 G4 A#4 ");
		assertEquals(harmony4.getToneNamesMapped(),"C E G A# ");
		assertEquals(harmony5.getToneNames(),"C4 E4 G4 A#4 C5 ");
		assertEquals(harmony5.getToneNamesMapped(),"C E G A# ");
		
		assertEquals(harmony3.getIntervals()[0],"4");
		assertEquals(harmony3.getIntervals()[1],"7");
		
		assertEquals(harmony3.inversionUp().getToneNames(),"E4 G4 C5 ");
		assertEquals(harmony3.inversionDown().getToneNames(),"G3 C4 E4 ");
		
		assertEquals(Chordanal.createHarmonyFromTones("C4 E4 G4").getCommonTones(key1.getSubdominant()).getToneNamesMapped(),"C ");
		
		assert(harmony3.containsMapped(new Tone(60)));
		assert(harmony3.containsMapped(harmony2));
	}
	
	@Test
	public void testKey() {
		assertEquals(key1.getTonic().getToneNamesMapped(),"C E G ");
		assertEquals(key1.getSubdominant().getToneNamesMapped(),"C F A ");
		assertEquals(key1.getDominant().getToneNamesMapped(),"D G B ");
	}
	
	@Test
	public void testChordanalFactory() {
		assertEquals(Chordanal.createToneFromName("G4").getName(),"G4");
		assertEquals(Chordanal.createToneFromRelativeName("G").getName(),"G3");
		assertEquals(Chordanal.createHarmonyFromTones("C4 D4 E4 F4").getToneNames(),"C4 D4 E4 F4 ");
		assertEquals(Chordanal.createHarmonyFromRelativeTones("C D E F").getToneNames(),"C3 D3 E3 F3 ");
		assertEquals(Chordanal.createKeyFromName("A# minor").root,10);
		assertEquals(Chordanal.createKeyFromName("A# minor").keyType,Chordanal.MINOR);
	}
	
	@Test
	public void testChordanalAbbreviations() {
		assertEquals(Chordanal.getHarmonyAbbreviationsRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")).get(1),"M3,P5");
		assertEquals(Chordanal.getHarmonyAbbreviationIntervals(Chordanal.createHarmonyFromTones("C4 E4 G4")),"M3,P5");
		assertEquals(Chordanal.getHarmonyAbbreviationRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")),"maj5");
		assertEquals(Chordanal.getHarmonyAbbreviation(Chordanal.createHarmonyFromTones("C4 E4 G4")),"Cmaj5");
		assertEquals(Chordanal.getHarmonyAbbreviation(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp()),"Cmaj6");
		assertEquals(Chordanal.getHarmonyAbbreviation(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp().inversionUp()),"Cmaj6-4");
	}
	
	@Test
	public void testChordanalNames() {
		assertEquals(Chordanal.getHarmonyNamesRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")).get(1),"major third,perfect fifth");
		assertEquals(Chordanal.getHarmonyNameIntervals(Chordanal.createHarmonyFromTones("C4 E4 G4")),"major third,perfect fifth");
		assertEquals(Chordanal.getHarmonyNameRelative(Chordanal.createHarmonyFromTones("C4 E4 G4")),"major triad");
		assertEquals(Chordanal.getHarmonyName(Chordanal.createHarmonyFromTones("C4 E4 G4")),"C major triad");
		assertEquals(Chordanal.getHarmonyName(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp()),"C major sixth chord");
		assertEquals(Chordanal.getHarmonyName(Chordanal.createHarmonyFromTones("C4 E4 G4").inversionUp().inversionUp()),"C major six-four chord");
		assertEquals(Chordanal.getKeyName(key1),"C major");
	}
	
	@Test
	public void testChordanalMiscellaneous() {
		assertEquals(Chordanal.getHarmonyCharacter(Chordanal.createHarmonyFromTones("C4 E4 G4")),"consonant");
		assertEquals(Chordanal.getRootTone(Chordanal.createHarmonyFromTones("E4 G4 C5")).getNameMapped(),"C");
		assertEquals(Chordanal.getKeyScale(key1),"C D E F G A B ");
		assertEquals(Chordanal.getKeyScale(key2),"C D D# F G G# A# ");
	}
	
	@Test
	public void testHarmanalRootsFinding() {
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(0),"C major,Tonic,C E G ;0");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(1),"F major,Dominant,C E G ;0");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(2),"G major,Subdominant,C E G ;0");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(3),"C minor,Tonic,C G ;2");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(4),"E minor,Tonic,E G ;1");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(5),"F minor,Dominant,C G ;2");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(6),"G minor,Subdominant,C G ;2");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(7),"A minor,Dominant,E G ;1");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(8),"B minor,Subdominant,E G ;2");
		
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(0),"C major,Tonic,Subdominant,C E G ,C F A ;0,0");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(1),"F major,Dominant,Tonic,C E G ,C F A ;0,0");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(2),"E minor,Tonic,Subdominant,E G ,C A ;1,2");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(3),"A minor,Dominant,Tonic,E G ,C A ;1,1");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(4),"C minor,Tonic,Subdominant,C G ,C F ;2,2");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(5),"F minor,Dominant,Tonic,C G ,C F ;2,2");
		
		assertEquals(Harmanal.getCommonRoots(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("C4 E4 G#4")).getAll().get(0),"C major,Tonic,C E ;0,2");
		assertEquals(Harmanal.getCommonRoots(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("C4 E4 G#4")).getAll().get(1),"F major,Dominant,C E ;0,2");
		assertEquals(Harmanal.getCommonRoots(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("C4 E4 G#4")).getAll().get(2),"G major,Subdominant,C E ;0,2");
		
		assertEquals(Harmanal.getCommonAncestors(Chordanal.createHarmonyFromTones("C4 E4 A#4"), Chordanal.createHarmonyFromTones("C4 E4 G#4 A#4")).getAll().get(0),"F major,Dominant,C E A# ;0,2");
		assertEquals(Harmanal.getCommonAncestors(Chordanal.createHarmonyFromTones("C4 E4 A#4"), Chordanal.createHarmonyFromTones("C4 E4 G#4 A#4")).getAll().get(1),"C major,Tonic,C E A# ;0,2");
		assertEquals(Harmanal.getCommonAncestors(Chordanal.createHarmonyFromTones("C4 E4 A#4"), Chordanal.createHarmonyFromTones("C4 E4 G#4 A#4")).getAll().get(2),"G major,Subdominant,C E A# ;0,2");
	}
	
	@Test
	public void testHarmanalHarmonyDerivationAndComplexity() {
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(0),"C E ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(1),"C E F ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(2),"C E F# ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(3),"C E F# A ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(4),"C E F# G# ");
		
		assertEquals(Harmanal.getHarmonyComplexity(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1),4);
	}
	
	@Test
	public void testHarmanalTransitionComplexity() {
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(0),"C major,Tonic,0,Subdominant,0;0");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(1),"F major,Dominant,0,Tonic,0;0");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(2),"E minor,Tonic,1,Subdominant,2;3");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(3),"A minor,Dominant,1,Tonic,1;2");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(4),"C minor,Tonic,2,Subdominant,2;4");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(5),"F minor,Dominant,2,Tonic,2;4");
		
		assertEquals(Harmanal.getTransitionComplexity(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2 C#2")),2);
	}
	
	@Test
	public void doExperiments() {
		
		try {
		    Path startPath = Paths.get("/home/laci/work/fmfi/Rocnik2/java/ProjectDipl/resources/NS/");
		    Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
		        @Override
		        public FileVisitResult preVisitDirectory(Path dir,
		                BasicFileAttributes attrs) {
		            System.out.println("Dir: " + dir.toString());
		            return FileVisitResult.CONTINUE;
		        }

		        @Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		        	
		        	if (file.toString().endsWith(".wav")) {
		        		String songName = file.toString().substring(0, file.toString().length()-4);
			        	System.out.println("Analyzing: " + songName + ".wav");
			        	Harmanal.anal(songName + ".wav-chromas.txt", songName + "_vamp_nnls-chroma_chordino_simplechord.csv", songName + ".wav-results.txt", songName + ".wav-report.txt", songName + ".wav-timestamps.txt");
		        	}
		            return FileVisitResult.CONTINUE;
		        }

		        @Override
		        public FileVisitResult visitFileFailed(Path file, IOException e) {
		            return FileVisitResult.CONTINUE;
		        }
		    });
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		
		//Harmanal.anal("NS/Jazz/19 - John Coltrane - Part 4 - Psalm.wav-chromas.txt", "NS/Jazz/19 - John Coltrane - Part 4 - Psalm_vamp_nnls-chroma_chordino_simplechord.csv", "NS/Jazz/19 - John Coltrane - Part 4 - Psalm.wav-results.txt", "NS/Jazz/19 - John Coltrane - Part 4 - Psalm.wav-report.txt", "NS/Jazz/19 - John Coltrane - Part 4 - Psalm.wav-timestamps.txt");
		
		//String genre = "ROCK";
		//String artist = "Pink Floyd";
		//String song = "GoodbyeBlueSky";
		
		//Harmanal.anal("Experiments/" + genre + "/" + artist + "/" + song + "-chromas.txt", "Experiments/" + genre + "/" + artist + "/" + song + "-bars.txt", "Experiments/" + genre + "/" + artist + "/" + song + "-results.txt", "Experiments/" + genre + "/" + artist + "/" + song + "-report.txt", "Experiments/" + genre + "/" + artist + "/" + song + "-timestamps.txt");
		
		//Harmanal.anal("Experiments/ROCK/Queen/DontStop-chromas.txt", "Experiments/ROCK/Queen/DontStop-bars.txt", "Experiments/ROCK/Queen/DontStop-results.txt", "Experiments/ROCK/Queen/DontStop-report.txt", "Experiments/ROCK/Queen/DontStop-timestamps.txt");
		
	}
	
}
