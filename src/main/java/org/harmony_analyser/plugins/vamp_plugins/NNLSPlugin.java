package org.harmony_analyser.plugins.vamp_plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.application.visualizations.VisualizationData;
import org.vamp_plugins.*;

import java.io.IOException;
import java.util.*;

/**
 * Wrapper for NNLS Chroma Vamp plugin http://www.isophonics.net/nnls-chroma
 */

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
		pluginKey = "nnls-chroma:nnls-chroma";
		pluginName = "NNLS Chroma";
		outputNumber = 3;
		blockSize = 16384;

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add(""); // Plugin handles raw WAV files

		outputFileSuffix = "-chromas.txt";

		parameters = new HashMap<>();
		parameters.put("useNNLS", (float) 1);
		parameters.put("rollon", (float) 1.0);
		parameters.put("tuningMode", (float) 0);
		parameters.put("whitening", (float) 1.0);
		parameters.put("s", (float) 0.7);
		parameters.put("chromanormalize", (float) 0);

		p = loader.loadPlugin(pluginKey, defaultRate, adapterFlag);
		setParameters();
	}

	public VisualizationData getDataFromOutput(String outputFile) throws IOException, AudioAnalyser.LoadFailedException, OutputNotReady, ParseOutputError, PluginLoader.LoadFailedException {
		return VisualizationData.VOID_VISUALIZATION_DATA;
	}
}
