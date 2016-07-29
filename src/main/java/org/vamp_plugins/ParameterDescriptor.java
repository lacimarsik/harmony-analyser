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
 * ParameterDescriptor describes the properties of a configurable
 * parameter of a Plugin.
 */
public class ParameterDescriptor {

    /**
     * The name of the parameter, in computer-usable form.  Will
     * contain only the characters [a-zA-Z0-9_-].
     */
    public String identifier;

    /**
     * The human-readable name of the parameter.
     */
    public String name;

    /**
     * A human-readable short text describing the parameter.  May be
     * empty if the name has said it all already.
     */
    public String description;

    /**
     * The unit of the parameter, in human-readable form.
     */
    public String unit;

    /**
     * The minimum value of the parameter.
     */
    public float minValue;

    /**
     * The maximum value of the parameter.
     */
    public float maxValue;

    /**
     * The default value of the parameter.  The plugin should
     * ensure that parameters have this value on initialisation
     * (i.e. the host is not required to explicitly set parameters
     * if it wants to use their default values).
     */
    public float defaultValue;
	
    /**
     * True if the parameter values are quantized to a particular
     * resolution.
     */
    public boolean isQuantized;

    /**
     * Quantization resolution of the parameter values (e.g. 1.0
     * if they are all integers).  Undefined if isQuantized is
     * false.
     */
    public float quantizeStep;

    /**
     * Names for the quantized values.  If isQuantized is true,
     * this may either be empty or contain one string for each of
     * the quantize steps from minValue up to maxValue inclusive.
     * Undefined if isQuantized is false.
     *
     * If these names are provided, they should be shown to the
     * user in preference to the values themselves.  The user may
     * never see the actual numeric values unless they are also
     * encoded in the names.
     */
    public String[] valueNames;
	
    ParameterDescriptor() {
	minValue = 0;
	maxValue = 0;
	defaultValue = 0;
	isQuantized = false;
    }
}

