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

import java.util.Map;
import java.util.List;

/**
 * A Java wrapper for a native-code Vamp plugin. Plugins are obtained
 * using PluginLoader and must be freed by calling dispose() after use
 * (being native code they cannot be garbage collected).
 *
 * The plugin lifecycle looks roughly like this, from the host's
 * perspective:
 *
 * - Plugin is loaded using PluginLoader
 *
 * - Host may query the plugin's available outputs with
 *    getOutputDescriptors(). This will report what outputs exist,
 *    but their properties (e.g. resolution, value count, extents)
 *    are not yet fixed
 *
 * - Host may query and set the plugin's programs and parameters with
 *    getPrograms(), getParameterDescriptors(), setParameter() etc
 *
 * - After all parameters are set, host queries the plugin's preferred
 *    step size, block size, and channel count (which may depend on
 *    the parameter settings)
 *
 * - Host initialises plugin by calling initialise(). If it returns
 *    false, initialise failed -- most likely because the step size,
 *    block size, or channel count was rejected
 *
 * - Host may now get final values for the output properties using
 *    getOutputDescriptors()
 *
 * - Host calls process() repeatedly to process data. This may return
 *    some results as it goes along (if the plugin is causal)
 *
 * - Host calls getRemainingFeatures() exactly once when all input has
 *    been processed, to obtain any non-causal or leftover features.
 *
 * - At any point after initialise() has been called, host may call
 *    reset() to restart processing. Parameter values remain fixed
 *    across reset() calls.
 *
 * - When host is finished with plugin, it calls dispose().
 *
 * The host may not change any parameter or program settings after
 * calling initialise(), and may not call initialise() more than once
 * on any given plugin.
 *
 * See the PluginBase and Plugin classes in the C++ Vamp plugin SDK
 * for further documentation.
 */
public class Plugin
{
    private long nativeHandle;
    protected Plugin(long handle) { nativeHandle = handle; }

    /**
     * Dispose of this Plugin. Call this when you have finished using
     * it to ensure the native code object is released.
     */
    public native void dispose();

    /**
     * Get the Vamp API compatibility level of the plugin.
     */
    public native int getVampApiVersion();

    /**
     * Get the computer-usable name of the plugin.  This will contain
     * only the characters [a-zA-Z0-9_-].  This is the authoritative
     * way for a host to identify a plugin within a given library, but
     * it is not the primary label shown to the user (that will be the
     * name, below).
     */
    public native String getIdentifier();

    /**
     * Get a human-readable name or title of the plugin.  This is the
     * main identifying label shown to the user.
     */
    public native String getName();

    /**
     * Get a human-readable description for the plugin, typically
     * a line of text that may optionally be displayed in addition
     * to the plugin's "name".  May be empty if the name has said
     * it all already.
     */
    public native String getDescription();
    
    /**
     * Get the name of the author or vendor of the plugin in
     * human-readable form. This should be short enough to be used to
     * label plugins from the same source in a tree or menu if
     * appropriate.
     */
    public native String getMaker();

    /**
     * Get the copyright statement or licensing summary for the
     * plugin.
     */
    public native String getCopyright();

    /**
     * Get the version number of the plugin.
     */
    public native int getPluginVersion();

    /**
     * Get the controllable parameters of this plugin.
     */
    public native ParameterDescriptor[] getParameterDescriptors();

    /**
     * Get the value of a named parameter.  The argument is the identifier
     * field from that parameter's descriptor.
     */
    public native float getParameter(String identifier);

    /**
     * Set a named parameter.  The first argument is the identifier field
     * from that parameter's descriptor.
     */
    public native void setParameter(String identifier, float value);

    /**
     * Get the program settings available in this plugin.  A program
     * is a named shorthand for a set of parameter values; changing
     * the program may cause the plugin to alter the values of its
     * published parameters (and/or non-public internal processing
     * parameters).  The host should re-read the plugin's parameter
     * values after setting a new program.
     *
     * The programs must have unique names.
     */
    public native String[] getPrograms();

    /**
     * Get the current program (if any).
     */
    public native String getCurrentProgram();

    /**
     * Select a program.  (If the given program name is not one of the
     * available programs, do nothing.)
     */
    public native void selectProgram(String program);

    /**
     * Initialise a plugin to prepare it for use with the given number
     * of input channels, step size (window increment, in sample
     * frames) and block size (window size, in sample frames).
     *
     * The input sample rate should have been already specified when
     * loading the plugin.
     * 
     * Return true for successful initialisation, false if the number
     * of input channels, step size and/or block size cannot be
     * supported.
     */
    public native boolean initialise(int inputChannels,
				     int stepSize,
				     int blockSize);

    /**
     * Reset the plugin after use, to prepare it for another clean
     * run.
     */
    public native void reset();
    
    public static enum InputDomain { TIME_DOMAIN, FREQUENCY_DOMAIN };
    
    /**
     * Get the plugin's required input domain.
     *
     * If this is TimeDomain, the samples provided to the process()
     * function (below) must be in the time domain, as for a
     * traditional audio processing plugin.
     *
     * If this is FrequencyDomain, the host must carry out a windowed
     * FFT of size equal to the negotiated block size on the data
     * before passing the frequency bin data in to process().  The
     * input data for the FFT will be rotated so as to place the
     * origin in the centre of the block.  The plugin does not get to
     * choose the window type -- the host will either let the user do
     * so, or will use a Hanning window.
     */
    public native InputDomain getInputDomain();
    
    /**
     * Get the preferred block size (window size -- the number of
     * sample frames passed in each block to the process() function).
     * This should be called before initialise().
     *
     * A plugin that can handle any block size may return 0.  The
     * final block size will be set in the initialise() call.
     */
    public native int getPreferredBlockSize();

    /**
     * Get the preferred step size (window increment -- the distance
     * in sample frames between the start frames of consecutive blocks
     * passed to the process() function) for the plugin.  This should
     * be called before initialise().
     *
     * A plugin may return 0 if it has no particular interest in the
     * step size.  In this case, the host should make the step size
     * equal to the block size if the plugin is accepting input in the
     * time domain.  If the plugin is accepting input in the frequency
     * domain, the host may use any step size.  The final step size
     * will be set in the initialise() call.
     */
    public native int getPreferredStepSize();

    /**
     * Get the minimum supported number of input channels.
     */
    public native int getMinChannelCount();

    /**
     * Get the maximum supported number of input channels.
     */
    public native int getMaxChannelCount();

    /**
     * Get the outputs of this plugin.  An output's index in this list
     * is used as its numeric index when looking it up in the
     * FeatureSet returned from the process() call.
     */
    public native OutputDescriptor[] getOutputDescriptors();

    /**
     * Process a single block of input data.
     * 
     * If the plugin's inputDomain is TimeDomain, inputBuffers must
     * contain one array of floats per input channel, and each of
     * these arrays will contain blockSize consecutive audio samples
     * (the host will zero-pad as necessary).  The timestamp in this
     * case will be the real time in seconds of the start of the
     * supplied block of samples.
     *
     * If the plugin's inputDomain is FrequencyDomain, inputBuffers
     * must contain one array of floats per input channel, and each of
     * these arrays will contain blockSize/2+1 consecutive pairs of
     * real and imaginary component floats corresponding to bins
     * 0..(blockSize/2) of the FFT output.  That is, bin 0 (the first
     * pair of floats) contains the DC output, up to bin blockSize/2
     * which contains the Nyquist-frequency output.  There will
     * therefore be blockSize+2 floats per channel in total.  The
     * timestamp will be the real time in seconds of the centre of the
     * FFT input window (i.e. the very first block passed to process
     * might contain the FFT of half a block of zero samples and the
     * first half-block of the actual data, with a timestamp of zero).
     *
     * Return any features that have become available after this
     * process call.  (These do not necessarily have to fall within
     * the process block, except for OneSamplePerStep outputs.)
     */
    public Map<Integer, List<Feature>>
	process(float[][] inputBuffers,
		RealTime timestamp) {
	return process(inputBuffers, 0, timestamp);
    }

    /**
     * As process() above, but taking input data starting at the given
     * offset from within each of the channel arrays. Provided to
     * avoid potentially having to extract a set of sub-arrays from
     * longer arrays (fiddly in Java).
     */
    public native Map<Integer, List<Feature>>
	process(float[][] inputBuffers,
		int offset,
		RealTime timestamp);

    /**
     * After all blocks have been processed, calculate and return any
     * remaining features derived from the complete input.
     */
    public native Map<Integer, List<Feature>>
	getRemainingFeatures();
}

