package urars;

import io.phanisment.urars.parser.file.LineParser;
import io.phanisment.urars.parser.BufferedString;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

class Main {
	@Test
	void test() {
		Map<BufferedString, Integer> map = new HashMap<>(1);
		Map<String, Integer> map_str = new HashMap<>(1);

		var key = "key";
		var buffered_key = new BufferedString(key);
		map.put(buffered_key, 1);
		map_str.put(key, 1);

		print("\nBufferedString Key get with String: " + map.get(key));
		print("String key get with BufferedString: " + map_str.get(buffered_key));

		assertNull(map.get(key));
		assertEquals(1, map_str.get(buffered_key));
	}

	@Test
	void parse() {
		var input = """
				foo{key=value;list=[
				  - foo @self
				  - foo @self
				]} @self
				""";
		var p = new LineParser(input);
		try {
			p.parse();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		var config = p.config();

		try {
			print(config.getLine("list").getFirst().config().key());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	private static void print(Object message) {
		System.out.println(message);
	}
}
