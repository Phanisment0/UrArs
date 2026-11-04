package io.phanisment.urars.component;

import io.phanisment.urars.util.UrArsDebugger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class MobComponent implements CardinalComponent {
	private static final String KEY_ID = "id";
	private static final String KEY_OWNER = "owner";

	private @Nullable String id;
	private @Nullable UUID owner;

	public void id(String id) {
		this.id = id;
	}

	public void owner(UUID uuid) {
		this.owner = uuid;
	}

	public String id() {
		return id;
	}

	public UUID owner() {
		return owner;
	}

	@Override
	public void readData(@NonNull ValueInput read) {
		id = read.getStringOr(KEY_ID, null);

		String owner_str = read.getStringOr(KEY_OWNER, null);
		try {
			owner = owner_str != null ? UUID.fromString(owner_str) : null;
		} catch (IllegalArgumentException exception) {
			owner = null;
			UrArsDebugger.debug("Invalid owner UUID: {}", owner_str);
		}
	}

	@Override
	public void writeData(@NonNull ValueOutput write) {
		if (id != null && !id.isEmpty()) write.putString(KEY_ID, id);
		if (owner != null) write.putString(KEY_OWNER, owner.toString());
	}
}