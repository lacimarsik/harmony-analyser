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
 * OutputDescriptor describes the properties of an output of a Vamp
 * plugin. Returned by e.g. Plugin.getOutputDescriptors().
 */
public class OutputDescriptor {

    /**
     * The name of the output, in computer-usable form.  Will contain
     * the characters [a-zA-Z0-9_-] only.
     */
    public String identifier;

    /**
     * The human-readable name of the output.
     */
    public String name;

    /**
     * A human-readable short text describing the output.  May be
     * empty if the name has said it all already.
     */
    public String description;

    /**
     * The unit of the output, in human-readable form.
     */
    public String unit;

    /**
     * True if the output has the same number of values per sample
     * for every output sample.
     */
    public boolean hasFixedBinCount;

    /**
     * The number of values per result of the output.  Undefined
     * if hasFixedBinCount is false.  If this is zero, the output
     * is point data (i.e. only the time of each output is of
     * interest, the value list will be empty).
     */
    public int binCount;

    /**
     * The (human-readable) names of each of the bins, if
     * appropriate.  This is always optional.
     */
    public String[] binNames;

    /**
     * True if the results in each output bin fall within a fixed
     * numeric range (minimum and maximum values).  Undefined if
     * binCount is zero.
     */
    public boolean hasKnownExtents;

    /**
     * Minimum value of the results in the output.  Undefined if
     * hasKnownExtents is false or binCount is zero.
     */
    public float minValue;

    /**
     * Maximum value of the results in the output.  Undefined if
     * hasKnownExtents is false or binCount is zero.
     */
    public float maxValue;

    /**
     * True if the output values are quantized to a particular
     * resolution.  Undefined if binCount is zero.
     */
    public boolean isQuantized;

    /**
     * Quantization resolution of the output values (e.g. 1.0 if
     * they are all integers).  Undefined if isQuantized is false
     * or binCount is zero.
     */
    public float quantizeStep;

    public static enum SampleType {
	/// Results from each process() align with that call's block start
	OneSamplePerStep,

	/// Results are evenly spaced in time (sampleRate specified below)
	FixedSampleRate,

	/// Results are unevenly spaced and have individual timestamps
	VariableSampleRate
    };

    /**
     * Positioning in time of the output results.
     */
    public SampleType sampleType;

    /**
     * Sample rate of the output results, as samples per second.
     * Undefined if sampleType is OneSamplePerStep.
     *
     * If sampleType is VariableSampleRate and this value is
     * non-zero, then it may be used to calculate a resolution for
     * the output (i.e. the "duration" of each sample, in time,
     * will be 1/sampleRate seconds).  It's recommended to set
     * this to zero if that behaviour is not desired.
     */
    public float sampleRate;

    /**
     * True if the returned results for this output are known to
     * have a duration field.
     */
    public boolean hasDuration;

    OutputDescriptor() {
	hasFixedBinCount = false;
	hasKnownExtents = false;
	isQuantized = false;
	sampleType = SampleType.OneSamplePerStep;
	hasDuration = false;
    }
}
