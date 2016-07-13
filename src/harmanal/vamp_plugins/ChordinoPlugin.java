package harmanal.vamp_plugins;

import org.vamp_plugins.*;

/*
 * Notes: Vamp plugins using JVamp wrappers
 * http://www.vamp-plugins.org/
 * https://code.soundsoftware.ac.uk/projects/jvamp
 * 
 * - extract from Vamp SDK documentation
 * 
 * Chordino Plugin
 * 
 */

/**
 * Wrapper for Chordino VAMP plugin
 */

public class ChordinoPlugin extends VampPlugin {
	public ChordinoPlugin() throws PluginLoader.LoadFailedException {
		System.out.println("Plugin crash course started");

		int sampleRate = 44100;
		int adapterFlag = 0xff;

		plugin = PluginLoader.getInstance().loadPlugin("nnls-chroma:chordino", sampleRate, adapterFlag);

		System.out.println("Plugin " + plugin.getName() + " loaded");

		plugin.setParameter("useNNLS", 1);
		plugin.setParameter("rollon", 1);
		plugin.setParameter("tuningMode", 0);
		plugin.setParameter("whitening", 1);
		plugin.setParameter("s", (float) 0.7);
		plugin.setParameter("chromanormalize", 0);

		System.out.println("All parameters set.");
	}
}
