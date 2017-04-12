[![Circle CI](https://circleci.com/gh/lacimarsik/harmony-analyser.png?style=shield&circle-token=75d33ca47b62b8f09af431379c206b6cf80bd361)](https://circleci.com/gh/lacimarsik/harmony-analyser)
[![Coverage Status](https://coveralls.io/repos/github/lacimarsik/harmony-analyser/badge.svg?branch=master)](https://coveralls.io/github/lacimarsik/harmony-analyser?branch=master)

# harmony-analyser
Cross-platform Java application for analysing harmonies and chord progressions of musical pieces or MIDI input.

Project website: [harmony-analyser.org](http://harmony-analyser.org)

## Installation
Pre-requisites:
* Running [JRE](https://www.java.com/en/download/) 8 or higher on your machine
* Download and install [libsndfile](http://www.mega-nerd.com/libsndfile/) 1.0.27 or higher (Linux: Usually supported by package manager)
* Download and install [Vamp Plugins SDK](https://code.soundsoftware.ac.uk/projects/vamp-plugin-sdk) 2.6 or higher (Linux: Supported by package manager, or `./configure && make sdk && make install`)
* Download and install [jVamp](https://code.soundsoftware.ac.uk/projects/jvamp) 1.3 or higher (Linux: compile and place `libvamp-jni.so` to your `java.library.path` folder; run `java -XshowSettings:properties` if you are unsure of the location)
* Download and install [Chordino and NNLS Chroma](http://www.isophonics.net/nnls-chroma) Vamp plugins 1.1 or higher (Linux: compile and place `nnls-chroma.so` to `usr/local/lib/vamp` folder)

To run the application:
* Download and open `harmony-analyser-<version>-jar-with-dependencies.jar` (where version is the currently available version) located in `target` folder, using your Java Virtual Machine (or invoke `java -jar target/harmony_analyser-<version>-jar-with-dependencies.jar` in command-line)
* For Command-line interface, move the file `harmony-analyser-script-jar-with-dependencies.jar` from `target` folder into the folder with WAV files for analysis. Then invoke `java -jar harmony-analyser-script-jar-with-dependencies -p <plugin name> -s .wav`, or `-h` for help.

## Development
In addition to installation pre-requisites, a functional [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) needs to be installed in order to compile the source.
Project uses [Maven](https://maven.apache.org/) as the project management tool. Dependencies available in the public Maven repositories are pulled in automatically.
Project uses one local dependency in form of JAR located in `lib`:
* [jVamp](https://code.soundsoftware.ac.uk/projects/jvamp): Since the Vamp plugins are typically written in C++, we are using jVamp wrappers to load them in Java (jVamp uses JNI to work with native C++ code)

To install the dependency properly, issue from the project root:
`mvn install:install-file -DgroupId=org.vamp_plugins -DartifactId=jvamp -Dversion=1.3 -Dpackaging=jar -Dfile=./lib/jvamp.jar`

## Documentation
For more details and sample analysis please refer to the documentation located in `documentation` folder.
For any questions and comments please contact the author on GitHub or [mail](mailto: marsik@ksi.mff.cuni.cz).

## Troubleshooting
1. You may get: `Exception in thread "main" java.lang.UnsupportedClassVersionError: org/harmony_analyser/application/HarmonyAnalyser : Unsupported major.minor version 52.0`. That means you have a wrong version of JRE set up as your default JRE. The same error occurs when you don't have Java 8 on your system. Make sure you have Java 8 installed and your JRE is set up properly. You can check the default Java version used on your UNIX system with: `sudo update-alternatives --config java`.
2. You may get `jni.h: no such file or directory` while failing to compile jVamp on UNIX. If so, you have to manually add the path to jni.h in the Makefile of jVamp. It should be something on line 18 looking like: `INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux` and you will have to update it to something like: `INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/path/to/jni.h`
