package org.harmony_analyser.cli_scripts;

import org.apache.commons.cli.*;
import org.harmony_analyser.application.visualizations.DataChartFactory;
import org.harmony_analyser.jharmonyanalyser.services.*;

/**
 * Script to perform arbitrary harmony-analyser analysis in the current directory
 */

public class HarmonyAnalyserScript {
	private static String analysisKey;
	private static String suffixAndExtension;
	private static AudioAnalyser audioAnalyser;

	public static void main(String[] args) {
		System.out.println("--- Starting Harmony Analyser 1.2-beta ---");
		System.out.println("Author: marsik@ksi.mff.cuni.cz");
		System.out.println("harmony-analyser.org");
		System.out.println();

		AnalysisFactory analysisFactory = new AnalysisFactory();
		DataChartFactory dataChartFactory = new DataChartFactory();
		audioAnalyser = new AudioAnalyser(analysisFactory, dataChartFactory);

		boolean ready = parseArgumentsAndReady(args);
		if (!ready) {
			System.out.println();
			System.out.println("Harmony Analyser 1.2-beta has ended.");
		}
	}

	// Parses arguments andh handles -l (list plugins) and -h (get help) arguments.
	// Returns false if script has to end, and true if we can proceed with Analysis
	private static boolean parseArgumentsAndReady(String[] args) {
		// Inspired by: http://stackoverflow.com/questions/7341683/parsing-arguments-to-a-java-command-line-program
		CommandLine commandLine;
		Option option_a = OptionBuilder.withArgName("analysisKey").hasArg().withDescription("Analysis key option").create("a");
		Option option_s = OptionBuilder.withArgName("suffixAndExtension").hasArg().withDescription("Suffix option").create("s");
		Option option_l = new Option("l", "List analysisKeys option");
		Option option_h = new Option("h", "Get help");
		Options options = new Options();
		CommandLineParser parser = new GnuParser();

		options.addOption(option_a);
		options.addOption(option_s);
		options.addOption(option_l);
		options.addOption(option_h);

		try {
			commandLine = parser.parse(options, args);

			System.out.println("PARSING ARGUMENTS ...");
			System.out.println();

			if (commandLine.hasOption("l")) {
				System.out.println("Listing all available analysis ...");
				System.out.println();

				System.out.println(audioAnalyser.printPlugins());
				return false;
			}

			if (commandLine.hasOption("h")) {
				System.out.println("USAGE: java -jar harmony-analyser-script-jar-with-dependencies.jar -a <analysisKey> -s <suffixForFilesToBeAnalysed> [-h] [-l]");
				return false;
			}

			if (commandLine.hasOption("a")) {
				System.out.print("Analysis key was set to: ");
				System.out.println(commandLine.getOptionValue("a"));
			} else {
				System.out.print("ERROR: Analysis key was not set. Please use -a option to define what analysis to use for current folder.");
				return false;
			}

			if (commandLine.hasOption("s")) {
				System.out.print("Suffix was set to: ");
				System.out.println(commandLine.getOptionValue("s"));
			} else {
				System.out.print("ERROR: Suffix and extension was not set. Please use -s option to define suffix for files to be analysed.");
				return false;
			}

			String[] remainder = commandLine.getArgs();

			if (remainder.length > 0) {
				System.out.print("Remaining arguments were ignored: ");
				for (String argument : remainder) {
					System.out.print(argument);
					System.out.print(" ");
				}
			}
			System.out.println();
		} catch (ParseException exception) {
			System.out.print("ERROR: Parse error: ");
			System.out.println(exception.getMessage());
			return false;
		}
		return true;
	}
}
