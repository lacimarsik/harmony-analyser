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
 * Feature contains a single result returned from Plugin.process() or
 * Plugin.getRemainingFeatures().
 */
public class Feature {
    /**
     * True if an output feature has its own timestamp.  This is
     * mandatory if the output has VariableSampleRate, optional if
     * the output has FixedSampleRate, and unused if the output
     * has OneSamplePerStep.
     */
    public boolean hasTimestamp;

    /**
     * Timestamp of the output feature.  This is mandatory if the
     * output has VariableSampleRate or if the output has
     * FixedSampleRate and hasTimestamp is true, and unused
     * otherwise.
     */
    public RealTime timestamp;

    /**
     * True if an output feature has a specified duration.  This
     * is optional if the output has VariableSampleRate or
     * FixedSampleRate, and and unused if the output has
     * OneSamplePerStep.
     */
    public boolean hasDuration;

    /**
     * Duration of the output feature.  This is mandatory if the
     * output has VariableSampleRate or FixedSampleRate and
     * hasDuration is true, and unused otherwise.
     */
    public RealTime duration;
	
    /**
     * Results for a single sample of this feature.  If the output
     * hasFixedBinCount, there must be the same number of values
     * as the output's binCount count.
     */
    public float[] values;

    /**
     * Label for the sample of this feature.
     */
    public String label;

    Feature() {
	hasTimestamp = false; hasDuration = false;
    }
};

