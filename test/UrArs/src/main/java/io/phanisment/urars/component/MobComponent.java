package io.phanisment.urars.component;

import org.jspecify.annotations.NonNull;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MobComponent implements CardinalComponent {
	private static final String KEY_ID = "id";
	private String id;

	public void id(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	@SuppressWarnings("null")
	@Override
	public void readData(@NonNull ValueInput read) {
		this.id = read.getStringOr(KEY_ID, null);
	}

	@Override
	public void writeData(@NonNull ValueOutput write) {
		if (id != null && !id.isEmpty()) write.putString(KEY_ID, id);
	}
}
