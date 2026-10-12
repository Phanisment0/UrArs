package io.phanisment.urars.lib.file_parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.phanisment.urars.exception.UrArsParseException;
import io.phanisment.urars.lib.file_parser.parser.Parser;
import io.phanisment.urars.util.BufferedString;

public final class UrArsFileParser {
	private static final Parser parser = new Parser();

	public static Map<String, BufferedString> loadString(String text) throws UrArsParseException {
		return load(text.getBytes());
	}

	public static Map<String, BufferedString> load(String path) throws IOException, UrArsParseException {
		return load(Path.of(path));
	}

	public static Map<String, BufferedString> load(File file) throws IOException, UrArsParseException {
		return load(file.toPath());
	}

	public static Map<String, BufferedString> load(Path path) throws IOException, UrArsParseException {
		return load(Files.readAllBytes(path));
	}
	
	public static Map<String, BufferedString> load(BufferedString buffer) throws UrArsParseException {
		parser.reset(buffer);
		return parser.parse();
	}

	public static Map<String, BufferedString> load(InputStream buffer) throws UrArsParseException, IOException {
		parser.reset(buffer.readAllBytes());
		return parser.parse();
	}

	public static Map<String, BufferedString> load(byte[] buffer) throws UrArsParseException {
		parser.reset(buffer);
		return parser.parse();
	}
}