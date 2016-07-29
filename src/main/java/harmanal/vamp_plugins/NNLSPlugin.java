package harmanal.vamp_plugins;

import org.vamp_plugins.*;

/*
 * Implements NNLS Chroma plugin using JVamp wrappers
 * http://www.isophonics.net/nnls-chroma
 * https://code.soundsoftware.ac.uk/projects/jvamp
 *
 * NNLS Chroma Plugin (Excerpts from http://www.isophonics.net)
 * 
 * - spectral frame-wise input -> log-frequency spectrum
 * - first there are 3 bins per semitone, bins 2, 5, 8 correspond to semitones
 * - tuning so that out-tuned input is well-analyzed as well
 * - spectral whitening, NNLS approximate transcription - spectrum mapped to 12 bins
 *
 * parameters
 * - use NNLS chroma transcription: on or off (for linear spectral mapping)
 * -- preferred: on
 * - spectral roll-on: 0.0-5.0% removing the low-frequency noise, useful for quiet recordings
 * -- preferred: 1.0%
 * - tuning mode: global or local, how to find out the tuning - locally or globally
 * -- preferred: global
 * - spectral whitening: 0.0-1.0 defines how much is the log-frequency spectrum whitened
 * -- preferred: 1.0
 * - spectral shape: 0.5-0.9 - shape of a note (amplitude has a decreasing pattern) 
 * -- preferred: 0.7
 * - chroma normalisation: none/max/L1/L2
 * -- determines the type of chroma normalisation
 *
 * outputs
 * 0: log-frequency spectrum (3 bins per semitone)
 * 1: tuned log-frequency spectrum
 * 2: semitone spectrum
 * 3: chromagram (12-dimensional)
 * 4: bass chromagram (12-dimensional)
 * 5: chromagram and bass chromagram
 */

/**
 * Wrapper for NNLS Chroma VAMP plugin
 */

public class NNLSPlugin extends VampPlugin {
	public NNLSPlugin() throws PluginLoader.LoadFailedException {
		System.out.println("Plugin crash course started");

		output = 3;
		outputType = OutputType.ARRAY;
		sampleRate = 44100;

		plugin = PluginLoader.getInstance().loadPlugin("nnls-chroma:nnls-chroma", sampleRate, adapterFlag);

		System.out.println("Plugin " + plugin.getName() + " loaded");

		plugin.setParameter("useNNLS", 1);
		plugin.setParameter("rollon", (float) 1.0);
		plugin.setParameter("tuningMode", 0);
		plugin.setParameter("whitening", (float) 1.0);
		plugin.setParameter("s", (float) 0.7);
		plugin.setParameter("chromanormalize", 0);

		System.out.println("All parameters set.");
	}
}
