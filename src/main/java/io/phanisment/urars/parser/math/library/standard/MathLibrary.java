package io.phanisment.urars.parser.math.library.standard;

import io.phanisment.urars.parser.math.function.FunctionValue;
import io.phanisment.urars.parser.math.library.IMathLibrary;
import io.phanisment.urars.parser.math.value.ObjectValue;
import io.phanisment.urars.parser.math.value.NumberValue;

public class MathLibrary implements IMathLibrary {

	@Override
	public String name() {
		return "math";
	}

	@Override
	public void register(ObjectValue value) {
		// ---------- Constants ----------
		value.member("E", new NumberValue(Math.E));
		value.member("PI", new NumberValue(Math.PI));
		value.member("TAU", new NumberValue(Math.TAU));
		value.member("INFINITY", new NumberValue(Double.POSITIVE_INFINITY));
		value.member("NaN", new NumberValue(Double.NaN));
		value.member("MAX_INT", new NumberValue(Integer.MAX_VALUE));
		value.member("MIN_INT", new NumberValue(Integer.MIN_VALUE));
		value.member("MAX_LONG", new NumberValue(Long.MAX_VALUE));
		value.member("MIN_LONG", new NumberValue(Long.MIN_VALUE));

		// ---------- Trigonometry ----------
		value.member("sin", new FunctionValue(args -> new NumberValue(Math.sin(d(args.number(0))))));
		value.member("cos", new FunctionValue(args -> new NumberValue(Math.cos(d(args.number(0))))));
		value.member("tan", new FunctionValue(args -> new NumberValue(Math.tan(d(args.number(0))))));
		value.member("asin", new FunctionValue(args -> new NumberValue(Math.asin(d(args.number(0))))));
		value.member("acos", new FunctionValue(args -> new NumberValue(Math.acos(d(args.number(0))))));
		value.member("atan", new FunctionValue(args -> new NumberValue(Math.atan(d(args.number(0))))));
		value.member("atan2", new FunctionValue(args -> new NumberValue(Math.atan2(d(args.number(0)), d(args.number(1))))));
		value.member("sinh", new FunctionValue(args -> new NumberValue(Math.sinh(d(args.number(0))))));
		value.member("cosh", new FunctionValue(args -> new NumberValue(Math.cosh(d(args.number(0))))));
		value.member("tanh", new FunctionValue(args -> new NumberValue(Math.tanh(d(args.number(0))))));
		value.member("toDegrees", new FunctionValue(args -> new NumberValue(Math.toDegrees(d(args.number(0))))));
		value.member("toRadians", new FunctionValue(args -> new NumberValue(Math.toRadians(d(args.number(0))))));

		// ---------- Roots, powers, logs ----------
		value.member("sqrt", new FunctionValue(args -> new NumberValue(Math.sqrt(d(args.number(0))))));
		value.member("cbrt", new FunctionValue(args -> new NumberValue(Math.cbrt(d(args.number(0))))));
		value.member("hypot", new FunctionValue(args -> new NumberValue(Math.hypot(d(args.number(0)), d(args.number(1))))));
		value.member("power", new FunctionValue(args -> new NumberValue(Math.pow(d(args.number(0)), d(args.number(1))))));
		value.member("pow", new FunctionValue(args -> new NumberValue(Math.pow(d(args.number(0)), d(args.number(1))))));
		value.member("exp", new FunctionValue(args -> new NumberValue(Math.exp(d(args.number(0))))));
		value.member("expm1", new FunctionValue(args -> new NumberValue(Math.expm1(d(args.number(0))))));
		value.member("log", new FunctionValue(args -> new NumberValue(Math.log(d(args.number(0))))));
		value.member("log10", new FunctionValue(args -> new NumberValue(Math.log10(d(args.number(0))))));
		value.member("log1p", new FunctionValue(args -> new NumberValue(Math.log1p(d(args.number(0))))));
		value.member("log2", new FunctionValue(args -> new NumberValue(Math.log(d(args.number(0))) / Math.log(2))));

		// ---------- Rounding ----------
		value.member("floor", new FunctionValue(args -> new NumberValue(floor(args.number(0)))));
		value.member("ceil", new FunctionValue(args -> new NumberValue(ceil(args.number(0)))));
		value.member("round", new FunctionValue(args -> new NumberValue(round(args.number(0)))));
		value.member("rint", new FunctionValue(args -> new NumberValue(Math.rint(d(args.number(0))))));
		value.member("trunc", new FunctionValue(args -> new NumberValue(trunc(args.number(0)))));

		// ---------- Number-type aware ----------
		value.member("abs", new FunctionValue(args -> new NumberValue(abs(args.number(0)))));
		value.member("sign", new FunctionValue(args -> new NumberValue(sign(args.number(0)))));
		value.member("min", new FunctionValue(args -> new NumberValue(min(args.number(0), args.number(1)))));
		value.member("max", new FunctionValue(args -> new NumberValue(max(args.number(0), args.number(1)))));
		value.member("clamp", new FunctionValue(args -> new NumberValue(clamp(args.number(0), args.number(1), args.number(2)))));

		// ---------- Integer math ----------
		value.member("floorDiv", new FunctionValue(args -> new NumberValue(narrow(Math.floorDiv(requireInt(args.number(0)), requireInt(args.number(1)))))));
		value.member("floorMod", new FunctionValue(args -> new NumberValue(narrow(Math.floorMod(requireInt(args.number(0)), requireInt(args.number(1)))))));
		value.member("gcd", new FunctionValue(args -> new NumberValue(narrow(gcd(requireInt(args.number(0)), requireInt(args.number(1)))))));
		value.member("lcm", new FunctionValue(args -> {
			long a = requireInt(args.number(0)), b = requireInt(args.number(1));
			if (a == 0 || b == 0) return new NumberValue(0);
			return new NumberValue(narrow(Math.multiplyExact(Math.abs(a / gcd(a, b)), Math.abs(b))));
		}));
		value.member("factorial", new FunctionValue(args -> new NumberValue(narrow(factorial(requireInt(args.number(0)))))));

		// ---------- Utility ----------
		value.member("lerp", new FunctionValue(args -> {
			double a = d(args.number(0)), b = d(args.number(1)), t = d(args.number(2));
			return new NumberValue(a + (b - a) * t);
		}));
		value.member("random", new FunctionValue(args -> new NumberValue(Math.random())));

		// ---------- Casts ----------
		value.member("toInt", new FunctionValue(args -> new NumberValue(args.number(0).intValue())));
		value.member("toLong", new FunctionValue(args -> new NumberValue(args.number(0).longValue())));
		value.member("toDouble", new FunctionValue(args -> new NumberValue(args.number(0).doubleValue())));
	}

	private static double d(Number n) {
		return n.doubleValue();
	}

	private static boolean isInteger(Number n) {
		return n instanceof Integer || n instanceof Long || n instanceof Short || n instanceof Byte;
	}

	/** Int if it fits, otherwise long. */
	private static Number narrow(long v) {
		if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) return (int)v;
		return v;
	}

	/** Same as narrow, but keeps Long as Long when the input was Long. */
	private static Number keepType(long v, Number original) {
		return original instanceof Long ? (Number)v : narrow(v);
	}

	private static long requireInt(Number n) {
		if (!isInteger(n)) throw new IllegalArgumentException("Expected integer but got " + n);
		return n.longValue();
	}

	private static Number abs(Number n) {
		if (!isInteger(n)) return Math.abs(d(n));
		long v = n.longValue();
		if (v == Long.MIN_VALUE) return Math.abs((double)v);
		return keepType(Math.abs(v), n);
	}

	private static Number sign(Number n) {
		return (int)Math.signum(d(n));
	}

	private static Number min(Number a, Number b) {
		if (isInteger(a) && isInteger(b)) return narrow(Math.min(a.longValue(), b.longValue()));
		return Math.min(d(a), d(b));
	}

	private static Number max(Number a, Number b) {
		if (isInteger(a) && isInteger(b)) return narrow(Math.max(a.longValue(), b.longValue()));
		return Math.max(d(a), d(b));
	}

	private static Number clamp(Number v, Number lo, Number hi) {
		if (d(lo) > d(hi)) throw new IllegalArgumentException(lo + " > " + hi);
		if (isInteger(v) && isInteger(lo) && isInteger(hi)) {
			return narrow(Math.min(hi.longValue(), Math.max(v.longValue(), lo.longValue())));
		}
		return Math.min(d(hi), Math.max(d(v), d(lo)));
	}

	private static Number floor(Number n) {
		return isInteger(n) ? n : (Number)Math.floor(d(n));
	}

	private static Number ceil(Number n) {
		return isInteger(n) ? n : (Number)Math.ceil(d(n));
	}

	private static Number trunc(Number n) {
		if (isInteger(n)) return n;
		double v = d(n);
		return v < 0 ? Math.ceil(v) : Math.floor(v);
	}

	private static Number round(Number n) {
		if (isInteger(n)) return n;
		double v = d(n);
		if (Double.isNaN(v) || Double.isInfinite(v)) return v;
		return narrow(Math.round(v));
	}

	private static long gcd(long a, long b) {
		a = Math.abs(a);
		b = Math.abs(b);
		while (b != 0) {
			long t = a % b;
			a = b;
			b = t;
		}
		return a;
	}

	private static long factorial(long n) {
		if (n < 0 || n > 20) throw new IllegalArgumentException("factorial is only defined for 0..20, got " + n);
		long result = 1;
		for (long i = 2; i <= n; i++) result *= i;
		return result;
	}
}