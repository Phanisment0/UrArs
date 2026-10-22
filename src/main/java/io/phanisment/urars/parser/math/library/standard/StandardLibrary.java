package io.phanisment.urars.parser.math.library.standard;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.function.FunctionValue;
import io.phanisment.urars.parser.math.library.IMathLibrary;
import io.phanisment.urars.parser.math.value.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class StandardLibrary implements IMathLibrary {

	@Override
	public String name() {
		return "std";
	}

	@Override
	public void register(ObjectValue value) {
		/*value.member("if", new FunctionValue(args -> {
			if (args.size() < 2) throw new InvalidMathOperationException("if expects (condition, then[, else])");
			if (truthy(args.get(0))) return args.get(1).invoke(List.of());
			if (args.size() > 2) return args.get(2).invoke(List.of());
			return null;
		}));*/

		value.member("list", new FunctionValue(args -> new ListValue(new ArrayList<>(args.get()))));
		value.member("map", new FunctionValue(args -> new MapValue(new HashMap<>())));
	}

	private static boolean truthy(IValue<?> value) {
		return value instanceof BooleanValue(Boolean raw) && raw;
	}
}