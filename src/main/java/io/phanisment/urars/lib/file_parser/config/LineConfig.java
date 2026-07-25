package io.phanisment.urars.lib.file_parser.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.phanisment.urars.lib.file_parser.util.BufferedString;

public class LineConfig extends ConfigSection {
	private final BufferedString key;
	private final BufferedString context;

	public LineConfig(BufferedString key, Map<BufferedString, BufferedString> arguments, BufferedString context) {
		super(arguments);
		this.key = key;
		this.context = context;
	}

	public BufferedString key() {
		return key;
	}

	public BufferedString context() {
		return context;
	}

	@Override
	protected List<BufferedString> parseList(BufferedString block) {
		if (block.length() == 0) return new ArrayList<>();

		if (block.buffer[block.start] != '[' || block.buffer[block.end - 1] != ']') throw new IllegalArgumentException("List must start with '[' and end with ']'");

		if (block.length() == 2) return new ArrayList<>();

		List<BufferedString> result = new ArrayList<>();

		int start = block.start + 1;
		int end = block.end - 1;

		int current = start;
		int depth = 0;

		for (int i = start; i < end; i++) {
			byte b = block.buffer[i];
			if (b == '{' || b == '[') depth++;
			else if (b == '}' || b == ']') depth--;
			else if (depth == 0 && (b == ';' || b == '-')) {
				if (i > current) result.add(new BufferedString(block.buffer, current, i));
				current = i + 1;
			}
		}
		if (current < end) result.add(new BufferedString(block.buffer, current, end));
		return result;
	}
}
