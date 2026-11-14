package io.phanisment.urars.parser.file;

import io.phanisment.urars.util.BufferedString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class UrArsYaml {
	private final byte[] buffer;
	private final int end;
	private int pos;
	private int line_no;

	// current look-ahead line
	private boolean has;
	private int indent, start, stop, no;

	private UrArsYaml(BufferedString src) {
		this(src.buffer, src.start, src.end);
	}

	public UrArsYaml(byte[] buffer, int start, int end) {
		this.buffer = buffer;
		this.start = start;
		this.end = end;
	}

	public static Map<BufferedString, Object> parseStr(String text) {
		return parse(new BufferedString(text));
	}

	public static Map<BufferedString, Object> parse(String path) throws IOException {
		return parse(Path.of(path));
	}

	public static Map<BufferedString, Object> parse(File file) throws IOException {
		return parse(file.toPath());
	}

	public static Map<BufferedString, Object> parse(Path path) throws IOException {
		return parse(Files.readAllBytes(path));
	}

	public static Map<BufferedString, Object> parse(InputStream input) throws IOException {
		return parse(input.readAllBytes());
	}

	public static Map<BufferedString, Object> parse(byte[] buffer) {
		return parse(new BufferedString(buffer));
	}

	public static Map<BufferedString, Object> parse(BufferedString src) {
		var p = new UrArsYaml(src);
		p.next();
		Map<BufferedString, Object> root = p.map(0);
		if (p.has) throw p.error("unexpected indent");
		return root;
	}

	private Map<BufferedString, Object> map(int ind) {
		Map<BufferedString, Object> out = new LinkedHashMap<>();
		while (has && indent == ind) {
			int colon = keyEnd(start, stop);
			if (colon < 0) throw error("expected 'key: value'");

			BufferedString key = new BufferedString(buffer, start, colon);
			int vs = colon + 1;
			while (vs < stop && ws(buffer[vs])) vs++;
			int ve = stop;
			next();

			Object value;
			if (vs < ve) value = new BufferedString(buffer, vs, ve);          // key: value
			else if (has && indent == ind && isItem()) value = list(ind);  // list at same indent as key
			else if (has && indent > ind) value = map(indent);             // nested map
			else value = null;                                             // key:

			if (out.containsKey(key)) throw new UrArsYamlParseException(no, "duplicate key " + key);
			out.put(key, value);
		}
		return out;
	}

	private List<Object> list(int ind) {
		List<Object> out = new ArrayList<>();
		while (has && indent == ind && isItem()) {
			int body = start + 1;
			while (body < stop && ws(buffer[body])) body++;
			int content_col = ind + (body - start);

			if (keyEnd(body, stop) >= 0) {
				// "- key: v": treat the line as starting at the content column, then reuse map()
				start = body;
				indent = content_col;
				out.add(map(content_col));
				if (has && endsItem(ind)) throw error("unexpected indent in list item");
			} else {
				// text item: one slice from the first char to the end of the last continuation line
				int from = body, to = stop;
				next();
				while (has && endsItem(ind)) {
					if (from == to) from = start;   // item began with a bare "-"
					to = stop;
					next();
				}
				out.add(new BufferedString(buffer, from, to));
			}
		}
		return out;
	}

	/** True if the current line is not part of the list item that sits at indent `ind`. */
	private boolean endsItem(int ind) {
		return indent >= ind && (indent != ind || (!isItem() && keyEnd(start, stop) < 0));
	}

	/** Loads the next non-blank line; sets has=false at end of input. */
	private void next() {
		while (pos < end) {
			int ls = pos, le = ls;
			while (le < end && buffer[le] != '\n') le++;
			pos = le < end ? le + 1 : end;
			line_no++;

			int s = ls;
			while (s < le && buffer[s] == ' ') s++;
			int e = le;
			while (e > s && ws(buffer[e - 1])) e--;
			if (e == s) continue; // blank

			if (buffer[s] == '\t') throw new UrArsYamlParseException(line_no, "tabs are not allowed for indentation");
			indent = s - ls;
			start = s;
			stop = e;
			no = line_no;
			has = true;
			return;
		}
		has = false;
	}

	private boolean isItem() {
		return buffer[start] == '-' && (stop - start == 1 || buffer[start + 1] == ' ');
	}

	/** Index of ':' if [s,e) begins with "key:" (followed by space or end), else -1. */
	private int keyEnd(int s, int e) {
		if (s >= e || !(letter(buffer[s]) || buffer[s] == '_')) return -1;
		int i = s + 1;
		while (i < e && (letter(buffer[i]) || (buffer[i] >= '0' && buffer[i] <= '9') || buffer[i] == '_' || buffer[i] == '-' || buffer[i] < 0)) i++;
		if (i >= e || buffer[i] != ':') return -1;
		return (i + 1 == e || ws(buffer[i + 1])) ? i : -1;
	}

	private static boolean letter(byte c) { return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); }
	private static boolean ws(byte c) { return c == ' ' || c == '\t' || c == '\r' || c == '\n'; }

	private UrArsYamlParseException error(String msg) { return new UrArsYamlParseException(no, msg); }

	public static final class UrArsYamlParseException extends RuntimeException {
		public UrArsYamlParseException(int line, String msg) { super("line " + line + ": " + msg); }
	}
}