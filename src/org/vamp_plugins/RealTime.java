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

public class RealTime
{
    public RealTime(int s, int n) { initialise(s, n); }

    public native void dispose();

    public native int sec();
    public native int nsec();

    public native int usec();
    public native int msec();

    /// Return a debug-type string to full precision
    public native String toString();

    /// Return a user-readable formatted string to the nearest millisecond
    public native String toText();

    public native static RealTime fromSeconds(double sec);
    public native static RealTime fromMilliseconds(int msec);
    
    public native static RealTime frame2RealTime(long frame, int sampleRate);
    public native static long realTime2Frame(RealTime r, int sampleRate);

    private native void initialise(int s, int n);
    private long nativeHandle;
}


