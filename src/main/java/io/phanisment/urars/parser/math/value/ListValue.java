package io.phanisment.urars.parser.math.value;

import io.phanisment.urars.exception.InvalidMathOperationException;
import io.phanisment.urars.parser.math.function.Arguments;
import io.phanisment.urars.parser.math.function.FunctionValue;

import java.util.ArrayList;
import java.util.List;

import static io.phanisment.urars.util.ValueUtils.same;

public class ListValue extends ObjectValue {
	public ListValue(List<IValue<?>> list) {
		super(list);

		// Query
		member("size", new FunctionValue(args -> new NumberValue(list.size())));
		member("isEmpty", new FunctionValue(args -> BooleanValue.of(list.isEmpty())));
		member("get", new FunctionValue(args -> list.get(index(list, args, 0, false))));
		member("getFirst", new FunctionValue(args -> list.get(index(list, 0))));
		member("getLast", new FunctionValue(args -> list.get(index(list, list.size() - 1))));
		member("contains", new FunctionValue(args -> BooleanValue.of(indexOf(list, args.get(0)) >= 0)));
		member("indexOf", new FunctionValue(args -> new NumberValue(indexOf(list, args.get(0)))));

		// Add
		member("add", new FunctionValue(args -> BooleanValue.of(list.add(args.get(0)))));
		member("addFirst", new FunctionValue(args -> {
			list.addFirst(args.get(0));
			return null;
		}));
		member("addLast", new FunctionValue(args -> {
			list.addLast(args.get(0));
			return null;
		}));
		member("insert", new FunctionValue(args -> {
			list.add(index(list, args, 0, true), args.get(1));
			return null;
		}));
		member("addAll", new FunctionValue(args -> {
			if (!(args.get(0) instanceof ListValue other)) throw new InvalidMathOperationException("addAll expects a list");
			return BooleanValue.of(list.addAll(new ArrayList<>(other.raw())));
		}));

		// Modify / remove (these return the affected element)
		member("set", new FunctionValue(args -> list.set(index(list, args, 0, false), args.get(1))));
		member("remove", new FunctionValue(args -> list.remove(index(list, args, 0, false))));
		member("removeFirst", new FunctionValue(args -> list.remove(index(list, 0))));
		member("removeLast", new FunctionValue(args -> list.remove(index(list, list.size() - 1))));
		member("clear", new FunctionValue(args -> {
			list.clear();
			return null;
		}));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IValue<?>> raw() {
		return (List<IValue<?>>)super.raw();
	}

	/** Read an index argument and bounds-check it. {@code inclusive} allows index == size (for insert). */
	private static int index(List<IValue<?>> list, Arguments args, int arg, boolean inclusive) {
		int i = args.number(arg).intValue();
		int max = inclusive ? list.size() : list.size() - 1;
		if (i < 0 || i > max) throw new InvalidMathOperationException("List index " + i + " out of bounds for size " + list.size());
		return i;
	}

	private static int index(List<IValue<?>> list, int i) {
		if (list.isEmpty()) throw new InvalidMathOperationException("List is empty");
		return i;
	}

	private static int indexOf(List<IValue<?>> list, IValue<?> target) {
		for (int i = 0; i < list.size(); i++) if (same(list.get(i), target)) return i;
		return -1;
	}
}