package org.harmony_analyser.cli_scripts;

import org.apache.commons.cli.*;

/**
 * Script to perform arbitrary harmony-analyser analysis in the current directory
 */

public class HarmonyAnalyserScript {
	public static void main(String[] args) {
		CommandLine commandLine;
		Option option_a = OptionBuilder.withArgName("analysisKey").hasArg().withDescription("Analysis key option").create("a");
		Option option_s = OptionBuilder.withArgName("suffixAndExtension").hasArg().withDescription("Suffix option").create("s");
		Options options = new Options();
		CommandLineParser parser = new GnuParser();

		options.addOption(option_a);
		options.addOption(option_s);

		System.out.println("--- Starting Harmony Analyser 1.2-beta ---");
		System.out.println("Author: marsik@ksi.mff.cuni.cz");
		System.out.println("harmony-analyser.org");
		System.out.println();

		try {
			commandLine = parser.parse(options, args);

			System.out.println("PARSING ARGUMENTS ...");
			System.out.println();

			if (commandLine.hasOption("a")) {
				System.out.print("Analysis key was set to: ");
				System.out.println(commandLine.getOptionValue("a"));
			}

			if (commandLine.hasOption("s")) {
				System.out.print("Suffix was set to:");
				System.out.println(commandLine.getOptionValue("s"));
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

		}
		catch (ParseException exception)
		{
			System.out.print("Parse error: ");
			System.out.println(exception.getMessage());
		}
	}

	private

	def
}
