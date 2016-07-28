package harmanal.vamp_plugins;

import org.vamp_plugins.*;

/*
 * Implements Chordino plugin using JVamp wrappers
 * http://www.isophonics.net/nnls-chroma
 * https://code.soundsoftware.ac.uk/projects/jvamp
 * 
 * Chordino Plugin (Excerpts from http://www.isophonics.net)
 * 
 * - chord transcription, based on NNLS Chroma plugin
 * - chord profiles from the chord dictionary
 * - smoothing using:
 * -- chord change method
 * -- HMM/Viterbi approach
 *
 * parameters
 * - use NNLS chroma transcription: on or off (for linear spectral mapping)
 * -- preferred: on
 * * - spectral roll-on: 0.0-5.0% removing the low-frequency noise, useful for quiet recordings
 * -- preferred: 1.0%
 * - tuning mode: global or local, how to find out the tuning - locally or globally
 * -- preferred: global
 * - spectral whitening: 0.0-1.0 defines how much is the log-frequency spectrum whitened
 * -- preferred: 1.0
 * - spectral shape: 0.5-0.9 - shape of a note (amplitude has a decreasing pattern)
 * -- preferred: 0.7
 * - chroma normalisation: none/max/L1/L2
 * -- determines the type of chroma normalisation
 * - boost likelihood of the N label: Higher values lead to non-harmonic parts being recognized as 'no chord'
 * -- preferred: 0.1
 * - use Harte syntax: as described on ISMIR 2005
 * -- preferred: off
 *
 */

/**
 * Wrapper for Chordino VAMP plugin
 */

public class ChordinoPlugin extends VampPlugin {
	public ChordinoPlugin() throws PluginLoader.LoadFailedException {
		System.out.println("Plugin crash course started");

		output = 0;
		outputType = OutputType.LABEL;
		sampleRate = 22050;

		plugin = PluginLoader.getInstance().loadPlugin("nnls-chroma:chordino", sampleRate, adapterFlag);

		System.out.println("Plugin " + plugin.getName() + " loaded");

		plugin.setParameter("useNNLS", 1);
		plugin.setParameter("rollon", (float) 1.0);
		plugin.setParameter("tuningmode", 0);
		plugin.setParameter("whitening", (float) 1.0);
		plugin.setParameter("s", (float) 0.7);
		plugin.setParameter("boostn", (float) 0.1);
		plugin.setParameter("usehartesyntax", 0);

		System.out.println("All parameters set.");
	}
}
