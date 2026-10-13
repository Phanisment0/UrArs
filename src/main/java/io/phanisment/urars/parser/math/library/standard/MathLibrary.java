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
		value.member("E", new NumberValue(Math.E));
		value.member("PI", new NumberValue(Math.PI));
		value.member("TAU", new NumberValue(Math.TAU));

		value.member("sin", new FunctionValue(args -> new NumberValue(Math.sin(args.number(0)))));
		value.member("cos", new FunctionValue(args -> new NumberValue(Math.cos(args.number(0)))));
		value.member("tan", new FunctionValue(args -> new NumberValue(Math.tan(args.number(0)))));
		value.member("asin", new FunctionValue(args -> new NumberValue(Math.asin(args.number(0)))));
		value.member("acos", new FunctionValue(args -> new NumberValue(Math.acos(args.number(0)))));
		value.member("atan", new FunctionValue(args -> new NumberValue(Math.atan(args.number(0)))));
		
		value.member("abs", new FunctionValue(args -> new NumberValue(Math.abs(args.number(0)))));
		value.member("sqrt", new FunctionValue(args -> new NumberValue(Math.sqrt(args.number(0)))));
		value.member("cbrt", new FunctionValue(args -> new NumberValue(Math.cbrt(args.number(0)))));
		value.member("ceil", new FunctionValue(args -> new NumberValue(Math.ceil(args.number(0)))));
		value.member("rint", new FunctionValue(args -> new NumberValue(Math.rint(args.number(0)))));
		value.member("log", new FunctionValue(args -> new NumberValue(Math.log(args.number(0)))));
		value.member("log10", new FunctionValue(args -> new NumberValue(Math.log10(args.number(0)))));
		value.member("exp", new FunctionValue(args -> new NumberValue(Math.exp(args.number(0)))));

		value.member("power", new FunctionValue(args -> new NumberValue(Math.pow(args.number(0), args.number(1)))));
		value.member("floor", new FunctionValue(args -> new NumberValue(Math.floor(args.number(0)))));
		value.member("round", new FunctionValue(args -> new NumberValue(Math.round(args.number(0)))));
		value.member("min", new FunctionValue(args -> new NumberValue(Math.min(args.number(0), args.number(1)))));
		value.member("max", new FunctionValue(args -> new NumberValue(Math.max(args.number(0), args.number(1)))));
		value.member("clamp", new FunctionValue(args -> new NumberValue(Math.clamp(args.number(0), args.number(1), args.number(2)))));

		value.member("toDegrees", new FunctionValue(args -> new NumberValue(Math.toDegrees(args.number(0)))));
		value.member("toRadians", new FunctionValue(args -> new NumberValue(Math.toRadians(args.number(0)))));
	}
}
