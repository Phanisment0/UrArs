package io.phanisment.urars.parser.file;

import io.phanisment.urars.exception.UrArsParserException;
import io.phanisment.urars.exception.UrArsParserException.SourceLocation;
import io.phanisment.urars.parser.BufferedString;
import static io.phanisment.urars.util.ParserUtils.*;

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
	private int line_start;

	private boolean has;
	private int indent, start, stop, no;

	private UrArsYaml(BufferedString src) {
		this(src.buffer, src.start, src.end);
	}

	public UrArsYaml(byte[] buffer, int start, int end) {
		this.buffer = buffer;
		this.start = start;
		this.end = end;
		this.pos = start;
	}

	public static Map<BufferedString, Object> parseStr(String text) throws UrArsParserException {
		return parse(new BufferedString(text));
	}

	public static Map<BufferedString, Object> parse(String path) throws IOException, UrArsParserException {
		return parse(Path.of(path));
	}

	public static Map<BufferedString, Object> parse(File file) throws IOException, UrArsParserException {
		return parse(file.toPath());
	}

	public static Map<BufferedString, Object> parse(Path path) throws IOException, UrArsParserException {
		return parse(Files.readAllBytes(path));
	}

	public static Map<BufferedString, Object> parse(InputStream input) throws IOException, UrArsParserException {
		return parse(input.readAllBytes());
	}

	public static Map<BufferedString, Object> parse(byte[] buffer) throws UrArsParserException {
		return parse(new BufferedString(buffer));
	}

	public static Map<BufferedString, Object> parse(BufferedString src) throws UrArsParserException {
		var p = new UrArsYaml(src);
		p.next();
		Map<BufferedString, Object> root = p.map(0);
		if (p.has) throw p.error("unexpected indent");
		return root;
	}

	private Map<BufferedString, Object> map(int ind) throws UrArsParserException {
		Map<BufferedString, Object> out = new LinkedHashMap<>();
		while (has && indent == ind) {
			int colon = keyEnd(start, stop);
			if (colon < 0) throw error("expected 'key: value'");

			// remember key position: next() will move to the following line
			SourceLocation key_loc = new SourceLocation(no, indent + 1, start, colon - start);

			BufferedString key = new BufferedString(buffer, start, colon);
			int vs = colon + 1;
			while (vs < stop && isWhitespace(buffer[vs])) vs++;
			int ve = stop;
			next();

			Object value;
			if (vs < ve) value = new BufferedString(buffer, vs, ve);
			else if (has && indent == ind && isItem()) value = list(ind);
			else if (has && indent > ind) value = map(indent);
			else value = null;

			if (out.containsKey(key)) throw new UrArsParserException("duplicate key " + key, key_loc);
			out.put(key, value);
		}
		return out;
	}

	private List<Object> list(int ind) throws UrArsParserException {
		List<Object> out = new ArrayList<>();
		while (has && indent == ind && isItem()) {
			int body = start + 1;
			while (body < stop && isWhitespace(buffer[body])) body++;
			int content_col = ind + (body - start);

			if (keyEnd(body, stop) >= 0) {
				start = body;
				indent = content_col;
				out.add(map(content_col));
				if (has && endsItem(ind)) throw error("unexpected indent in list item");
			} else {
				int from = body, to = stop;
				next();
				while (has && endsItem(ind)) {
					if (from == to) from = start;
					to = stop;
					next();
				}
				out.add(new BufferedString(buffer, from, to));
			}
		}
		return out;
	}

	private boolean endsItem(int ind) {
		return indent >= ind && (indent != ind || (!isItem() && keyEnd(start, stop) < 0));
	}

	private void next() throws UrArsParserException {
		while (pos < end) {
			int ls = pos, le = ls;
			while (le < end && buffer[le] != '\n') le++;
			pos = le < end ? le + 1 : end;
			line_no++;
			line_start = ls;

			int s = ls;
			while (s < le && buffer[s] == ' ') s++;
			int e = le;
			while (e > s && isWhitespace(buffer[e - 1])) e--;
			if (e == s) continue; // blank

			if (buffer[s] == '\t') throw new UrArsParserException("tabs are not allowed for indentation", new SourceLocation(line_no, s - ls + 1, s, 1));
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

	private int keyEnd(int s, int e) {
		if (s >= e || !(isLetter(buffer[s]) || buffer[s] == '_')) return -1;
		int i = s + 1;
		while (i < e && (isLetter(buffer[i]) || (buffer[i] >= '0' && buffer[i] <= '9') || buffer[i] == '_' || buffer[i] == '-' || buffer[i] < 0)) i++;
		if (i >= e || buffer[i] != ':') return -1;
		return (i + 1 == e || isWhitespace(buffer[i + 1])) ? i : -1;
	}

	private UrArsParserException error(String msg) {
		return new UrArsParserException(msg, new SourceLocation(no, start - line_start + 1, start, stop - start));
	}
}