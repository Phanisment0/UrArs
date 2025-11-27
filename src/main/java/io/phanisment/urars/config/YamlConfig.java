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
 * Implementation of {@link ConfigSection} that provides
 * support for reading and writing YAML configuration files.
 * <p>
 * This class uses the SnakeYAML library to parse YAML
 * content from various sources such as {@link InputStream},
 * {@link Reader}, {@link String}, or {@link File}.
 * </p>
 */
public class YamlConfig extends ConfigSection {
	private static final Yaml yaml = new Yaml();
	private File file;
	
	/**
	 * Creates a new {@link YamlConfig} by loading data from an {@link InputStream}.
	 *
	 * @param io the input stream containing YAML data
	 */
	@SuppressWarnings("unchecked")
	public YamlConfig(InputStream io) {
		this((Map<String, Object>)yaml.load(io));
	}
	
	/**
	 * Creates a new {@link YamlConfig} by loading data from a {@link Reader}.
	 *
	 * @param io the reader providing YAML data
	 */
	@SuppressWarnings("unchecked")
	public YamlConfig(Reader io) {
		this((Map<String, Object>)yaml.load(io));
	}
	
	/**
	 * Creates a new {@link YamlConfig} by loading data from a YAML-formatted string.
	 *
	 * @param string the YAML string to load
	 */
	@SuppressWarnings("unchecked")
	public YamlConfig(String string) {
		this((Map<String, Object>)yaml.load(string));
	}
	
	/**
	 * Creates a new {@link YamlConfig} that reads from a file on disk.
	 * <p>
	 * This constructor automatically calls {@link #load()} to read
	 * the file content if it exists.
	 * </p>
	 *
	 * @param io the file to read the YAML configuration from
	 * @throws IOException if the file cannot be read
	 */
	public YamlConfig(File io) throws IOException {
		this.file = io;
		load();
	}
	
	public YamlConfig(Map<String, Object> map) {
		this.data.putAll(map);
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}
	
	/**
	 * Loads the YAML configuration from the associated {@link #file}.
	 * <p>
	 * If the file does not exist, this method will simply return
	 * without throwing an exception.
	 * </p>
	 *
	 * @throws IOException if an error occurs while reading the file
	 */
	public void load() throws IOException {
		if (!file.exists()) return;
		try (var reader = new FileReader(file)) {
			Map<String, Object> map = yaml.load(reader);
			if (map != null) data.putAll(map);
		}
	}
	/**
	 * Saves the current configuration data into the associated {@link #file}.
	 * <p>
	 * If the file does not exist, it will be created automatically.
	 * </p>
	 *
	 * @throws IOException if an error occurs while writing to the file
	 */
	public void save() throws IOException {
		try (var writer = new FileWriter(file)) {
			yaml.dump(data, writer);
		}
	}
}