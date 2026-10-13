package io.phanisment.urars.parser.math.library;

import io.phanisment.urars.parser.math.value.ObjectValue;

public interface IMathLibrary {
	String name();
	void register(ObjectValue value);
}