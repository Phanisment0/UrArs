package io.phanisment.urars.item;

import io.phanisment.urars.UrArs;
import io.phanisment.urars.parser.file.config.IConfig;
import io.phanisment.urars.skill.MechanicsExecutor;
import io.phanisment.urars.skill.config.SkillConfigSection;
import io.phanisment.urars.util.ItemBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemMeta {
	public static final String ITEM_KEY = "urars:item";
	private final Identifier id;

	private final Identifier type;
	private final String name;

	private final MechanicsExecutor skills;

	public ItemMeta(final Identifier id, final File file, final SkillConfigSection config) {
		this.id = id;

		type = parseType(config);
		name = config.getString("name");
		skills = config.getMechanics("skills");
	}

	public MechanicsExecutor skills() {
		return skills;
	}

	private Identifier parseType(IConfig config) {
		String type_str = config.getString("type", "stone");
		Identifier type_id = Identifier.tryParse(type_str);
		if (type_id == null || !BuiltInRegistries.ITEM.containsKey(type_id)) {
			UrArs.LOGGER.error("Unknown item type `{}` in {} use stone instead as default", type_str, id);
			return Identifier.fromNamespaceAndPath("minecraft", "stone");
		}
		return type_id;
	}

	public ItemStack build() {
		var builder = ItemBuilder.of(type);
		builder.customData(c -> c.putString(ITEM_KEY, id.toString()));

		if (name != null) builder.nameNoItalic(name);

		return builder.get();
	}
}