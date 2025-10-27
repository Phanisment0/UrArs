package io.phanisment.urars.config;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class YamlConfig extends ConfigSection {
	private static final Yaml yaml = new Yaml();
	private final File file;
	
	public YamlConfig(File file) throws IOException {
		this.file = file;
		load();
	}
	
	public void load() throws IOException {
		if (!file.exists()) return;
		try (var reader = new FileReader(file)) {
			Map<String, Object> map = yaml.load(reader);
			if (map != null) serialize().putAll(map);
		}
	}
	
	public void save() throws IOException {
		try (var writer = new FileWriter(file)) {
			yaml.dump(serialize(), writer);
		}
	}
}