package io.phanisment.urars.skill.template.particle;

import io.phanisment.urars.parser.BufferedString;
import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

import static io.phanisment.urars.util.ActionUtils.*;

public class ParticleSpec {
	private static final int WHITE = 0xFFFFFFFF;

	protected final Node particle;
	protected final Node size;
	protected final Node block;
	protected final Node item;
	protected final Node power;
	protected final Node roll;
	protected final Node delay;
	private final Node color;
	private final Node to;

	public ParticleSpec(SkillLineConfig config) {
		this.particle = config.getMathString(aliases("particle", "p"), "flame");
		this.size     = config.getMathNumber(aliases("size", "scale"), 1);
		this.block    = config.getMathString(aliases("block"), "stone");
		this.item     = config.getMathString(aliases("item"), "stone");
		this.power    = config.getMathNumber(aliases("power"), 1);
		this.roll     = config.getMathNumber(aliases("roll"), 0);
		this.delay    = config.getMathNumber(aliases("shriekDelay"), 0);
		this.color    = config.getMathString(aliases("color", "c"), "");
		this.to       = config.getMathString(aliases("to", "color2", "c2"), "");
	}

	public @Nullable ParticleOptions build(EvalContext eval) {
		Identifier id = id(particle.evalAsString(eval));
		if (id == null) return null;

		ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.getOptional(id).orElse(null);
		if (type == null) return null;
		if (type instanceof SimpleParticleType simple) return simple;

		int main = color(color, eval);

		return switch (id.getPath()) {
			case "dust" -> new DustParticleOptions(main, (float) size.evalAsDouble(eval));
			case "dust_color_transition" ->
					new DustColorTransitionOptions(main, color(to, eval), (float) size.evalAsDouble(eval));
			case "entity_effect", "flash", "tinted_leaves" ->
					ColorParticleOption.create((ParticleType<ColorParticleOption>) type, main);
			case "effect", "instant_effect" ->
					SpellParticleOption.create((ParticleType<SpellParticleOption>) type, main, (float) power.evalAsDouble(eval));
			case "dragon_breath" ->
					PowerParticleOption.create((ParticleType<PowerParticleOption>) type, (float) power.evalAsDouble(eval));
			case "block", "block_marker", "falling_dust", "dust_pillar" -> {
				BlockState state = blockState(block.evalAsString(eval));
				yield state == null ? null : new BlockParticleOption((ParticleType<BlockParticleOption>) type, state);
			}
			case "item" -> {
				ItemStack stack = itemStack(item.evalAsString(eval));
				yield stack == null ? null : new ItemParticleOption((ParticleType<ItemParticleOption>) type, stack.getItem());
			}
			case "sculk_charge" -> new SculkChargeParticleOptions((float) roll.evalAsDouble(eval));
			case "shriek" -> new ShriekParticleOption(delay.evalAsInt(eval));
			default -> null;
		};
	}

	private static int color(Node node, EvalContext eval) {
		try {
			return new BufferedString(node.evalAsString(eval)).trim().toColor();
		} catch (NumberFormatException e) {
			return WHITE;
		}
	}

	private static @Nullable Identifier id(String name) {
		return Identifier.tryParse(name.trim().toLowerCase(Locale.ROOT));
	}

	private static @Nullable BlockState blockState(String name) {
		Identifier id = id(name);
		return id == null ? null : BuiltInRegistries.BLOCK.getOptional(id).map(Block::defaultBlockState).orElse(null);
	}

	private static @Nullable ItemStack itemStack(String name) {
		Identifier id = id(name);
		return id == null ? null : BuiltInRegistries.ITEM.getOptional(id).map(ItemStack::new).orElse(null);
	}
}