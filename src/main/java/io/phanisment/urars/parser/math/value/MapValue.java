package io.phanisment.urars.parser.math.value;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.function.FunctionValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.phanisment.urars.util.ValueUtils.same;

public class MapValue extends ObjectValue {
	public MapValue(Map<String, IValue<?>> map) {
		super(map);

		// Query
		member("size", new FunctionValue(args -> new NumberValue(map.size())));
		member("isEmpty", new FunctionValue(args -> BooleanValue.of(map.isEmpty())));
		member("has", new FunctionValue(args -> BooleanValue.of(map.containsKey(args.string(0)))));
		member("containsValue", new FunctionValue(args -> {
			IValue<?> target = args.get(0);
			for (IValue<?> v : map.values()) if (same(v, target)) return BooleanValue.TRUE;
			return BooleanValue.FALSE;
		}));
		member("get", new FunctionValue(args -> {
			String key = args.string(0);
			IValue<?> value = map.get(key);
			if (value == null) throw new InvalidMathOperationException("Unknown key: " + key);
			return value;
		}));
		member("getOrDefault", new FunctionValue(args -> {
			IValue<?> value = map.get(args.string(0));
			return value != null ? value : args.get(1);
		}));
		member("keys", new FunctionValue(args -> {
			List<IValue<?>> out = new ArrayList<>(map.size());
			for (String key : map.keySet()) out.add(new StringValue(key));
			return new ListValue(out);
		}));
		member("values", new FunctionValue(args -> new ListValue(new ArrayList<>(map.values()))));

		// Modify
		member("put", new FunctionValue(args -> {
			map.put(args.string(0), args.get(1));
			return null;
		}));
		member("putIfAbsent", new FunctionValue(args -> {
			String key = args.string(0);
			if (map.containsKey(key)) return BooleanValue.FALSE;
			map.put(key, args.get(1));
			return BooleanValue.TRUE;
		}));
		member("putAll", new FunctionValue(args -> {
			if (!(args.get(0) instanceof MapValue other)) throw new InvalidMathOperationException("putAll expects a map");
			map.putAll(new LinkedHashMap<>(other.raw()));
			return null;
		}));
		member("remove", new FunctionValue(args -> BooleanValue.of(map.remove(args.string(0)) != null)));
		member("clear", new FunctionValue(args -> {
			map.clear();
			return null;
		}));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, IValue<?>> raw() {
		return (Map<String, IValue<?>>)super.raw();
	}
}