package io.phanisment.urars.util;

import io.phanisment.urars.parser.math.value.IValue;

import java.util.Objects;

public class ValueUtils {
	public static boolean same(IValue<?> a, IValue<?> b) {
		if (a == b) return true;
		if (a == null || b == null) return false;
		Object x = a.raw(), y = b.raw();
		if (x instanceof Number n && y instanceof Number m) return n.doubleValue() == m.doubleValue();
		return Objects.equals(x, y);
	}
}