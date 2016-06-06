/*
    jVamp

    A Java host interface for Vamp audio analysis plugins

    Centre for Digital Music, Queen Mary, University of London.
    Copyright 2012 Chris Cannam and QMUL.
  
    Permission is hereby granted, free of charge, to any person
    obtaining a copy of this software and associated documentation
    files (the "Software"), to deal in the Software without
    restriction, including without limitation the rights to use, copy,
    modify, merge, publish, distribute, sublicense, and/or sell copies
    of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be
    included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR
    ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
    CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
    WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

    Except as contained in this notice, the names of the Centre for
    Digital Music; Queen Mary, University of London; and Chris Cannam
    shall not be used in advertising or otherwise to promote the sale,
    use or other dealings in this Software without prior written
    authorization.
*/

package org.vamp_plugins;

/**
 * PluginLoader loads a Vamp plugin by searching the standard Vamp
 * installation path, and returns a Plugin object wrapping the native
 * plugin.
 *
 * To load a plugin call PluginLoader.getInstance().loadPlugin(key,
 * rate), where rate is the processing sample rate and key is the
 * plugin key consisting of the plugin's library base name and its
 * identifier, colon-separated. For example,
 *
 * Plugin p = PluginLoader.getInstance().loadPlugin("vamp-example-plugins:percussiononsets", 44100);
 */
public class PluginLoader
{
    public class LoadFailedException extends Exception { };

    /**
     * PluginLoader is a singleton. Return the instance of it.
     */
    public static synchronized PluginLoader getInstance() {
	if (inst == null) {
	    inst = new PluginLoader();
	    inst.initialise();
	}
	return inst;
    }

    /**
     * Search for all available Vamp plugins, and return a list of
     * their plugin keys (suitable for passing to loadPlugin) in the
     * order in which they were found.
     */
    public native String[] listPlugins();

    /**
     * AdapterFlags contains a set of values that may be OR'd together
     * and passed to loadPlugin() to indicate which of the properties
     * of a plugin the host would like PluginLoader to take care of
     * for it, rather than having to handle itself.
     *
     * Use of these flags permits the host to cater more easily for
     * plugins with varying requirements for their input formats, at
     * some expense in flexibility.
     */
    public class AdapterFlags {

	/**
	 * ADAPT_INPUT_DOMAIN - If the plugin expects frequency domain
	 * input, automatically convert it to one that expects
	 * time-domain input by interpolating an adapter that carries
	 * out the FFT conversion silently.
	 *
	 * This enables a host to accommodate time- and
	 * frequency-domain plugins without needing to do any
	 * conversion itself, but it means the host gets no control
	 * over the windowing and FFT methods used.  A Hann window is
	 * used, and the FFT is unlikely to be the fastest native
	 * implementation available.
	 */
	public static final int ADAPT_INPUT_DOMAIN = 1;

	/**
	 * ADAPT_CHANNEL_COUNT - Automatically handle any discrepancy
	 * between the number of channels supported by the plugin and
	 * the number provided by the host when calling
	 * Plugin.initialise().  This enables a host to use plugins
	 * that may require the input to be mixed down to mono, etc.,
	 * without having to worry about doing that itself.
	 */
	public static final int ADAPT_CHANNEL_COUNT = 2;

	/**
	 * ADAPT_BUFFER_SIZE - Permit the host to ignore the preferred
	 * step and block size reported by the plugin when calling
	 * initialise(), and to provide whatever step and block size
	 * are most convenient instead.
	 *
	 * This may require modifying the sample type and rate
	 * specifications for the plugin outputs and modifying the
	 * timestamps on the output features in order to obtain
	 * correct time stamping.
	 */
	public static final int ADAPT_BUFFER_SIZE = 4;

	/** 
	 * ADAPT_ALL - Perform all available adaptations that are
	 * meaningful for the plugin.
	 */
	public static final int ADAPT_ALL = 255;
	
	/**
	 * ADAPT_NONE - If passed to loadPlugin as the adapterFlags
	 * value, causes no adaptations to be done.
	 */
	public static final int ADAPT_NONE = 0;
    };

    /**
     * Load a native Vamp plugin from the plugin path. If the plugin
     * cannot be loaded, throw LoadFailedException.
     * 
     * key is the plugin key consisting of the plugin's library base
     * name and its identifier, colon-separated; inputSampleRate is
     * the processing sample rate for input audio.
     *
     * adapterFlags should contain an OR of the desired AdapterFlags
     * options for the plugin, or AdapterFlags.ADAPT_NONE if no
     * automatic adaptations are to be made.
     */
    public Plugin loadPlugin(String key, 
			     float inputSampleRate,
			     int adapterFlags)
	throws LoadFailedException {
	long handle = loadPluginNative(key, inputSampleRate, adapterFlags);
	if (handle != 0) return new Plugin(handle);
	else throw new LoadFailedException();
    }

    /**
     * Return the category hierarchy for a Vamp plugin, given its
     * identifying key. The hierarchy is a sequence of category names
     * giving the location of a plugin within a category forest,
     * containing the human-readable names of the plugin's category
     * tree root, followed by each of the nodes down to the leaf
     * containing the plugin.
     *
     * If the plugin has no category information, return an empty
     * list.
     */
    public native String[] getPluginCategory(String key);

    /**
     * Return the plugin path, that is, the series of local file
     * folders that will be searched for plugin files. This is
     * platform-specific; it may be a default path, or it may be
     * determined by factors such as the VAMP_PATH environment
     * variable.
     */
    public native String[] getPluginPath();

    private PluginLoader() { initialise(); }
    private native long loadPluginNative(String key, float inputSampleRate,
					 int adapterFlags);
    private native void initialise();
    private static PluginLoader inst;
    private long nativeHandle;

    static {
	System.loadLibrary("vamp-jni");
    }
}

