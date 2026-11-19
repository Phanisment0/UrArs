package io.phanisment.urars.skill.value;

import io.phanisment.urars.parser.math.value.NumberValue;
import io.phanisment.urars.parser.math.value.ObjectValue;
import io.phanisment.urars.util.Location;

public class LocationValue extends ObjectValue {
	public LocationValue(Location l) {
		super(l);
		number("x", l.x());
		number("y", l.y());
		number("z", l.z());
		number("block_x", (int) Math.floor(l.x()));
		number("block_y", (int) Math.floor(l.y()));
		number("block_z", (int) Math.floor(l.z()));
		number("x_rot", l.x_rot());
		number("y_rot", l.y_rot());
	}

	private void number(String name, Number value) {
		member(name, new NumberValue(value));
	}
}