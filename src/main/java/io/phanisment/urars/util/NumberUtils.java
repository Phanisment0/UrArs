package io.phanisment.urars.util;

import io.phanisment.urars.exception.InvalidMathOperationException;

public final class NumberUtils {
	public static final int INT = 0, LONG = 1, DOUBLE = 2;

	private NumberUtils() {
	}

	public static double d(Number n) {
		return n.doubleValue();
	}

	public static boolean isInteger(Number n) {
		return n instanceof Integer || n instanceof Long || n instanceof Short || n instanceof Byte;
	}

	public static long requireInt(Number n) {
		if (!isInteger(n)) throw new IllegalArgumentException("Expected integer but got " + n);
		return n.longValue();
	}

	/** Int if it fits, otherwise long. */
	public static Number narrow(long v) {
		if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) return (int) v;
		return v;
	}

	/** Same as narrow, but keeps Long as Long when the input was Long. */
	public static Number keepType(long v, Number original) {
		return original instanceof Long ? (Number) v : narrow(v);
	}

	public static int rank(Number n) {
		if (n instanceof Double || n instanceof Float) return DOUBLE;
		if (n instanceof Long) return LONG;
		return INT;
	}

	public static int rank(Number a, Number b) {
		return Math.max(rank(a), rank(b));
	}

	public static Number wrap(long value, int rank) {
		if (rank == INT && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return (int) value;
		return value;
	}

	public static Number add(Number a, Number b) {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() + b.doubleValue();
		try {
			return wrap(Math.addExact(a.longValue(), b.longValue()), r);
		} catch (ArithmeticException e) {
			return a.doubleValue() + b.doubleValue(); // long overflow -> double
		}
	}

	public static Number sub(Number a, Number b) {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() - b.doubleValue();
		try {
			return wrap(Math.subtractExact(a.longValue(), b.longValue()), r);
		} catch (ArithmeticException e) {
			return a.doubleValue() - b.doubleValue();
		}
	}

	public static Number mul(Number a, Number b) {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() * b.doubleValue();
		try {
			return wrap(Math.multiplyExact(a.longValue(), b.longValue()), r);
		} catch (ArithmeticException e) {
			return a.doubleValue() * b.doubleValue();
		}
	}

	public static Number div(Number a, Number b) throws InvalidMathOperationException {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() / b.doubleValue();
		if (b.longValue() == 0) throw new InvalidMathOperationException("Division by zero");
		return wrap(a.longValue() / b.longValue(), r);
	}

	public static Number mod(Number a, Number b) throws InvalidMathOperationException {
		int r = rank(a, b);
		if (r == DOUBLE) return a.doubleValue() % b.doubleValue();
		if (b.longValue() == 0) throw new InvalidMathOperationException("Modulo by zero");
		return wrap(a.longValue() % b.longValue(), r);
	}

	public static Number negate(Number a) {
		int r = rank(a);
		if (r == DOUBLE) return -a.doubleValue();
		long v = a.longValue();
		if (v == Long.MIN_VALUE) return -(double) v;
		return wrap(-v, r);
	}

	public static int compareNum(Number a, Number b) {
		if (rank(a, b) == DOUBLE) return Double.compare(a.doubleValue(), b.doubleValue());
		return Long.compare(a.longValue(), b.longValue());
	}

	public static Number abs(Number n) {
		if (!isInteger(n)) return Math.abs(d(n));
		long v = n.longValue();
		if (v == Long.MIN_VALUE) return Math.abs((double) v);
		return keepType(Math.abs(v), n);
	}

	public static Number sign(Number n) {
		return (int) Math.signum(d(n));
	}

	public static Number min(Number a, Number b) {
		if (isInteger(a) && isInteger(b)) return narrow(Math.min(a.longValue(), b.longValue()));
		return Math.min(d(a), d(b));
	}

	public static Number max(Number a, Number b) {
		if (isInteger(a) && isInteger(b)) return narrow(Math.max(a.longValue(), b.longValue()));
		return Math.max(d(a), d(b));
	}

	public static Number clamp(Number v, Number lo, Number hi) {
		if (d(lo) > d(hi)) throw new IllegalArgumentException(lo + " > " + hi);
		if (isInteger(v) && isInteger(lo) && isInteger(hi)) {
			return narrow(Math.min(hi.longValue(), Math.max(v.longValue(), lo.longValue())));
		}
		return Math.min(d(hi), Math.max(d(v), d(lo)));
	}

	public static Number floor(Number n) {
		return isInteger(n) ? n : (Number) Math.floor(d(n));
	}

	public static Number ceil(Number n) {
		return isInteger(n) ? n : (Number) Math.ceil(d(n));
	}

	public static Number trunc(Number n) {
		if (isInteger(n)) return n;
		double v = d(n);
		return v < 0 ? Math.ceil(v) : Math.floor(v);
	}

	public static Number round(Number n) {
		if (isInteger(n)) return n;
		double v = d(n);
		if (Double.isNaN(v) || Double.isInfinite(v)) return v;
		return narrow(Math.round(v));
	}

	public static long gcd(long a, long b) {
		a = Math.abs(a);
		b = Math.abs(b);
		while (b != 0) {
			long t = a % b;
			a = b;
			b = t;
		}
		return a;
	}

	public static long factorial(long n) {
		if (n < 0 || n > 20) throw new IllegalArgumentException("factorial is only defined for 0..20, got " + n);
		long result = 1;
		for (long i = 2; i <= n; i++) result *= i;
		return result;
	}
}