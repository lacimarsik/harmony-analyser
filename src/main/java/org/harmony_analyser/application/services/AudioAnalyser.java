package org.harmony_analyser.application.services;

import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.chordanal_plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.harmony_analyser.application.*;
import org.vamp_plugins.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

import static org.harmony_analyser.plugins.AnalysisPlugin.*;

/**
 * Class to direct all levels of audio analysis, using available plugins
 */

public class AudioAnalyser {
	public static class LoadFailedException extends Exception {
		LoadFailedException(String message) {
			super(message);
		}
	}

	private static final String[] AVAILABLE_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino",
		"harmanal:transition_complexity"
	};

	private static final String[] VISUAL_PLUGINS = new String[] {
		"nnls-chroma:chordino",
		"harmanal:transition_complexity"
	};

	/* Public / Package methods */

	public static String[] getVisualPlugins() {
		return VISUAL_PLUGINS;
	}

	public static String printPlugins() {
		String result = "";
		result += "\n> Available plugins (" + AVAILABLE_PLUGINS.length + "):\n";

		for (String availablePluginKey : AVAILABLE_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += "\n> Available visualizations (" + VISUAL_PLUGINS.length + "):\n";

		for (String availablePluginKey : VISUAL_PLUGINS) {
			result += availablePluginKey + "\n";
		}

		result += VampPlugin.printInstalledVampPlugins();

		return result;
	}

	public static String printParameters(String pluginKey) throws LoadFailedException {
		return getPlugin(pluginKey).printParameters();
	}

	public String runAnalysis(List<String> inputFiles, String outputFile, String pluginKey) throws LoadFailedException, AnalysisPlugin.IncorrectInputException, IOException {
		AnalysisPlugin plugin = getPlugin(pluginKey);

		return plugin.analyse(inputFiles, outputFile);
	}

	public List<String> getInputFileExtensions(String pluginKey) {
		List<String> result = new ArrayList<>();
		switch (pluginKey) {
			case "nnls-chroma:nnls-chroma":
				result.add("");
				return result;
			case "nnls-chroma:chordino":
				result.add("");
				return result;
			case "harmanal:transition_complexity":
				result.add("-chromas.txt");
				result.add("-segmentation.txt");
				return result;
			default:
				return null;
		}
	}

	public String getOutputFileExtension(String pluginKey) {
		switch (pluginKey) {
			case "nnls-chroma:chordino":
				return "-segmentation.txt";
			case "harmanal:transition_complexity":
				return "-report.txt";
			default:
				return "-default.txt";
		}
	}

	public DrawPanel getDrawPanel(String outputFile, String pluginKey) throws IOException {
		List<Map<Float, String>> data = getDataFromOutput(outputFile, pluginKey);
		switch (pluginKey) {
			case "nnls-chroma:chordino":
				return new SegmentationDrawPanel(data);
			case "harmanal:transition_complexity":
				return new ComplexityChartDrawPanel(data);
			default:
				return null;
		}
	}

	/* Private methods */

	private List<Map<Float, String>> getDataFromOutput(String outputFile, String pluginKey) throws IOException {
		List<Map<Float, String>> result = new ArrayList<>();
		File file = new File(outputFile);
		if (!file.exists() || file.isDirectory()) {
			throw new IOException("Output file is invalid");
		} else {
			List<String> linesList = Files.readAllLines(new File(outputFile).toPath(), Charset.defaultCharset());
			switch (pluginKey) {
				case "nnls-chroma:nnls-chroma":
					// we do not visualize chroma files yet
					break;
				case "nnls-chroma:chordino":

					break;
				case "harmanal:transition_complexity":
					Scanner sc = new Scanner(linesList.get(linesList.size() - 3));
					sc.next(); // skip annotation
					String dataInString1 = new String();
					if (sc.hasNextFloat()) {
						dataInString1 = Float.toString(new Float(sc.nextFloat()));
					}
					Scanner sc = new Scanner(linesList.get(linesList.size() - 3));
					sc.next(); // skip annotation
					String dataInString2 = new String();
					if (sc.hasNextFloat()) {
						dataInString2 = Float.toString(new Float(sc.nextFloat()));
					}
					Scanner sc = new Scanner(linesList.get(linesList.size() - 3));
					sc.next(); // skip annotation
					String dataInString3 = new String();
					if (sc.hasNextFloat()) {
						dataInString3 = Float.toString(new Float(sc.nextFloat()));
					}
					break;
			}
		}
		return result;
	}

	private static AnalysisPlugin getPlugin(String pluginKey) throws LoadFailedException {
		AnalysisPlugin plugin;
		if (!Arrays.asList(AVAILABLE_PLUGINS).contains(pluginKey)) {
			throw new LoadFailedException("Plugin with key " + pluginKey + " is not available");
		}

		try {
			switch (pluginKey) {
				case "nnls-chroma:nnls-chroma":
					plugin = new NNLSPlugin();
					break;
				case "nnls-chroma:chordino":
					plugin = new ChordinoPlugin();
					break;
				case "harmanal:transition_complexity":
					plugin = new TransitionComplexityPlugin();
					break;
				default:
					throw new LoadFailedException("Plugin with key " + pluginKey + " is not available");
			}
		} catch (PluginLoader.LoadFailedException e) {
			throw new LoadFailedException(e.getMessage());
		}
		return plugin;
	}
}
