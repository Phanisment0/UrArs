package io.phanisment.urars.config;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;

/**
 * Yaml I/O input for {@link ConfigSection}.
 */
public class YamlConfig extends ConfigSection {
	private static final Yaml yaml = new Yaml();
	private File file;
	
	/**
	 * Load data with {@link InputStream}.
	 * 
	 * @param io new instance with data on the I/O
	 */
	public YamlConfig(InputStream io) {
		Map<String, Object> map = yaml.load(io);
		if (map != null) data.putAll(map);
	}
	
	/**
	 * Load data with {@link Reader}.
	 * 
	 * @param io new instance with data on the I/O
	 */
	public YamlConfig(Reader io) {
		Map<String, Object> map = yaml.load(io);
		if (map != null) data.putAll(map);
	}
	
	/**
	 * Load data with string.
	 * 
	 * @param string new instance with data on string
	 */
	public YamlConfig(String string) {
		Map<String, Object> map = yaml.load(string);
		if (map != null) data.putAll(map);
	}
	
	/**
	 * Load data with {@link Reader}.
	 * 
	 * @param io new instance with data on the IO
	 * @throws IOException if file is not found or not a file
	 */
	public YamlConfig(File io) throws IOException {
		this.file = io;
		load();
	}
	
	public void load() throws IOException {
		if (!file.exists()) return;
		try (var reader = new FileReader(file)) {
			Map<String, Object> map = yaml.load(reader);
			if (map != null) data.putAll(map);
		}
	}
	
	public void save() throws IOException {
		try (var writer = new FileWriter(file)) {
			yaml.dump(data, writer);
		}
	}
}