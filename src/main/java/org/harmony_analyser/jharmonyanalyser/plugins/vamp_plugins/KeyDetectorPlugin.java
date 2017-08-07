package org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins;

import org.vamp_plugins.PluginLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Wrapper for Key Detector Vamp plugin from QM Vamp Plugin set http://vamp-plugins.org/plugin-doc/qm-vamp-plugins.html
 */

/*
 * Implements Key Detector plugin using JVamp wrappers
 * http://vamp-plugins.org/plugin-doc/qm-vamp-plugins.html
 * https://code.soundsoftware.ac.uk/projects/qm-vamp-plugins
 *
 * Key Detector Plugin (Excerpts from http://vamp-plugins.org/plugin-doc/qm-vamp-plugins.html)
 * 
 * - analyses a single channel of audio and continuously estimates the key of the music
 * - block-by-block comparison of chromagram to the stored key profiles for each major and minor key
 * - key profiles from analysis of Book I of the Well Tempered Klavier by J S Bach
 *
 * parameters
 * - Tuning Frequency – The frequency of concert A in the music under analysis.
 * -- preferred: 440
 * - Window Length – The number of chroma analysis frames taken into account for key estimation.
 * This controls how eager the key detector will be to return short-duration tonal changes as new key changes
 * (the shorter the window, the more likely it is to detect a new key change).
 * -- preferred: 10
 *
 * outputNumber
 * 0: simplechord - Tonic Pitch, value counted from 1 to 12 where C is 1, C# or Db is 2, and so on up to B which is 12
 * 1: chordnotes - The major or minor mode of the estimated key, where major is 0 and minor is 1
 * 2: harmonicchange - Key, with value counted from 1 to 24 where 1-12 are the major keys and 13-24 are the minor keys
 * 3: loglikelihood - A grid representing the ongoing key "probability" throughout the music
 */

public class KeyDetectorPlugin extends SegmentationVampPlugin {
	public KeyDetectorPlugin() throws PluginLoader.LoadFailedException {
		key = "qm-vamp-plugins:qm-keydetector";
		name = "Key Detector";
		description = "Key Detector QM Vamp Plugin";
		outputNumber = 2;
		outputType = OutputType.LABEL_ONLY;
		blockSize = 16384;

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add(""); // Plugin handles raw WAV files
		inputFileExtension = ".wav";

		outputFileSuffix = "-key";
		outputFileExtension = ".txt";

		parameters = new HashMap<>();
		parameters.put("tuning", (float) 440.0);
		parameters.put("length", (float) 10.0);

		p = loader.loadPlugin(key, defaultRate, adapterFlag);
		setParameters();
	}
}
