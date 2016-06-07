# harmony-analyser
Cross-platform Java application for analysing harmonies and chord progressions of musical pieces or MIDI input.

## Installation
Pre-requisites:
* Running [JRE](https://www.java.com/en/download/) 6 or higher on your machine
* Download and install [JVamp](https://code.soundsoftware.ac.uk/projects/jvamp/files) 1.2 or higher (Linux: compile and place `libvamp-jni.so` in your `java.library.path`)
* Download and install [Chordino and NNLS Chroma](http://www.isophonics.net/nnls-chroma) Vamp plugins 1.1 or higher, and [Queen Mary Vamp Plugin Set](https://code.soundsoftware.ac.uk/projects/qm-vamp-plugins/files) 1.7 or higher (Linux: compile and place `nnls-chroma.so` and `qm-vamp-plugins.so` to `usr/local/lib/vamp` folder)

To run the application:
* Download and open `harmony-analyser.jar` located in `out` folder, using your Java Virtual Machine (or invoke `java -jar out/harmony_analyser.jar` in command-line)

## Development
In addition to installation pre-requisites, a functional [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) needs to be installed in order to compile the source of the application.
Since the Vamp plugins dependency requires a native C++ code, [JNA](https://github.com/java-native-access/jna) 3.5.2 or higher needs to be added as dependency.

## Documentation
For more details and sample analysis please refer to the documentation located in `documentation` folder.
For any questions and comments please contact the author on GitHub or [mail](mailto: marsik@ksi.mff.cuni.cz).
