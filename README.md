# harmony-analyser
Cross-platform Java application for analysing harmonies and chord progressions of musical pieces or MIDI input.

## Installation
Pre-requisites:
* Running [JRE](https://www.java.com/en/download/) 6 or higher on your machine
* Download and install [Chordino and NNLS Chroma](http://www.isophonics.net/nnls-chroma) Vamp plugins 1.1 or higher (Linux: compile and place `nnls-chroma.so` to `usr/local/lib/vamp` folder)

To run the application:
* Download and open `harmony-analyser-<version>.jar` (where version is the currently available version) located in `target` folder, using your Java Virtual Machine (or invoke `java -jar out/harmony_analyser-<version>.jar` in command-line)

## Development
In addition to installation pre-requisites, a functional [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) needs to be installed in order to compile the source.
Project uses [Maven](https://maven.apache.org/) as the project management tool. Dependencies available in the public Maven repositories are pulled in automatically.
Project uses a local dependency in form of JAR located in the local Maven repository `local-maven-repo`, which assures seamless integration using Maven:
* [jVamp](https://code.soundsoftware.ac.uk/projects/jvamp): Since the Vamp plugins are typically written in C++, we are using jVamp wrappers to load them in Java (jVamp uses JNI to work with native C++ code)

## Documentation
For more details and sample analysis please refer to the documentation located in `documentation` folder.
For any questions and comments please contact the author on GitHub or [mail](mailto: marsik@ksi.mff.cuni.cz).
