package io.phanisment.urars.lib.file_parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Map;

import io.phanisment.urars.lib.file_parser.config.ConfigSection;
import io.phanisment.urars.lib.file_parser.config.IConfig;
import io.phanisment.urars.lib.file_parser.parser.Parser;
import io.phanisment.urars.lib.file_parser.util.BufferedString;

public final class UrArsFileParser {
	private static final Parser parser = new Parser();

	public static ConfigSection loadString(String text) throws ParseException {
		return load(text.getBytes());
	}

	public static ConfigSection load(String path) throws IOException, ParseException {
		return load(Path.of(path));
	}

	public static ConfigSection load(Path path) throws IOException, ParseException {
		return load(Files.readAllBytes(path));
	}

	public static ConfigSection load(byte[] buffer) throws ParseException {
		return new ConfigSection(loadMap(buffer));
	}

	public static ConfigSection load(BufferedString buffer) throws ParseException {
		return new ConfigSection(loadMap(buffer));
	}

	public static Map<BufferedString, BufferedString> loadMap(byte[] buffer) throws ParseException {
		parser.reset(buffer);
		return parser.parse();
	}

	public static Map<BufferedString, BufferedString> loadMap(BufferedString buffer) throws ParseException {
		parser.reset(buffer);
		return parser.parse();
	}
}