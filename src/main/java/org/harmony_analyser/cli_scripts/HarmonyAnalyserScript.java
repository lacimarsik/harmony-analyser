package org.harmony_analyser.cli_scripts;

import org.apache.commons.cli.*;

/**
 * Script to perform arbitrary harmony-analyser analysis in the current directory
 */
public class HarmonyAnalyserScript {
	public static void main(String[] args) {
		CommandLine commandLine;
		Option option_A = OptionBuilder.withArgName("opt3").hasArg().withDescription("The A option").create("A");
		Option option_r = OptionBuilder.withArgName("opt1").hasArg().withDescription("The r option").create("r");
		Option option_S = OptionBuilder.withArgName("opt2").hasArg().withDescription("The S option").create("S");
		Option option_test = new Option("test", "The test option");
		Options options = new Options();
		CommandLineParser parser = new GnuParser();

		String[] testArgs =
				{ "-r", "opt1", "-S", "opt2", "arg1", "arg2",
						"arg3", "arg4", "--test", "-A", "opt3", };

		options.addOption(option_A);
		options.addOption(option_r);
		options.addOption(option_S);
		options.addOption(option_test);

		try
		{
			commandLine = parser.parse(options, testArgs);

			if (commandLine.hasOption("A"))
			{
				System.out.print("Option A is present.  The value is: ");
				System.out.println(commandLine.getOptionValue("A"));
			}

			if (commandLine.hasOption("r"))
			{
				System.out.print("Option r is present.  The value is: ");
				System.out.println(commandLine.getOptionValue("r"));
			}

			if (commandLine.hasOption("S"))
			{
				System.out.print("Option S is present.  The value is: ");
				System.out.println(commandLine.getOptionValue("S"));
			}

			if (commandLine.hasOption("test"))
			{
				System.out.println("Option test is present.  This is a flag option.");
			}

			{
				String[] remainder = commandLine.getArgs();
				System.out.print("Remaining arguments: ");
				for (String argument : remainder)
				{
					System.out.print(argument);
					System.out.print(" ");
				}

				System.out.println();
			}

		}
		catch (ParseException exception)
		{
			System.out.print("Parse error: ");
			System.out.println(exception.getMessage());
		}
	}
}
