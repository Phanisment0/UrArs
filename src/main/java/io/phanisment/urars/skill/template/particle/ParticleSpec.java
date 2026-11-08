package io.phanisment.urars.skill.template.particle;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.parser.math.eval.EvalContext;
import io.phanisment.urars.skill.config.SkillLineConfig;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

import static io.phanisment.urars.util.ActionUtils.*;

public class ParticleSpec {
	protected final Node particle;
	protected final Node size;
	protected final Node block;
	protected final Node item;
	protected final Node power;
	protected final Node roll;
	protected final Node delay;
	private final ColorArg color;
	private final ColorArg to;

	public ParticleSpec(SkillLineConfig config) {
		this.particle = config.getMathString(aliases("particle", "p"), "flame");
		this.size     = config.getMathNumber(aliases("size", "scale"), 1);
		this.block    = config.getMathString(aliases("block"), "stone");
		this.item     = config.getMathString(aliases("item"), "stone");
		this.power    = config.getMathNumber(aliases("power"), 1);
		this.roll     = config.getMathNumber(aliases("roll"), 0);
		this.delay    = config.getMathNumber(aliases("shriekDelay"), 0);
		this.color    = ColorArg.of(config, aliases("color", "colour"), aliases("r", "red"), aliases("g", "green"), aliases("b", "blue"));
		this.to       = ColorArg.of(config, aliases("to", "color2"), aliases("r2"), aliases("g2"), aliases("b2"));
	}

	/** Builds the options for this cast, or null if the particle is unknown or unsupported. */
	@SuppressWarnings("unchecked")
	public @Nullable ParticleOptions build(EvalContext eval) {
		Identifier id = Identifier.tryParse(particle.evalAsString(eval).trim().toLowerCase(Locale.ROOT));
		if (id == null) return null;

		ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.getOptional(id).orElse(null);
		if (type == null) return null;
		if (type instanceof SimpleParticleType simple) return simple;

		int main = color.eval(eval, 0xFFFFFFFF);

		return switch (id.getPath()) {
			case "dust" -> new DustParticleOptions(main, (float) size.evalAsDouble(eval));
			case "dust_color_transition" ->
					new DustColorTransitionOptions(main, to.eval(eval, 0xFFFFFFFF), (float) size.evalAsDouble(eval));
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
			default -> null; // vibration, trail, ... need extra data
		};
	}

	private static @Nullable BlockState blockState(String name) {
		Identifier id = Identifier.tryParse(name.trim().toLowerCase(Locale.ROOT));
		if (id == null) return null;
		return BuiltInRegistries.BLOCK.getOptional(id).map(Block::defaultBlockState).orElse(null);
	}

	private static @Nullable ItemStack itemStack(String name) {
		Identifier id = Identifier.tryParse(name.trim().toLowerCase(Locale.ROOT));
		if (id == null) return null;
		return BuiltInRegistries.ITEM.getOptional(id).map(ItemStack::new).orElse(null);
	}

	static int parseHex(String text, int fallback) {
		String s = text.trim();
		if (s.isEmpty()) return fallback;
		if (s.startsWith("#")) s = s.substring(1);
		else if (s.startsWith("0x") || s.startsWith("0X")) s = s.substring(2);
		try {
			long v = Long.parseLong(s, 16);
			if (s.length() <= 6) return 0xFF000000 | (int) v;
			return (int) v;
		} catch (NumberFormatException e) {
			return fallback;
		}
	}

	private record ColorArg(Node text, Node r, Node g, Node b) {
		static ColorArg of(SkillLineConfig c, String[] text, String[] r, String[] g, String[] b) {
			return new ColorArg(
					c.getMathString(text, ""),
					c.getMathNumber(r, -1),
					c.getMathNumber(g, -1),
					c.getMathNumber(b, -1));
		}

		int eval(EvalContext eval) {
			return eval(eval, 0);
		}

		int eval(EvalContext eval, int fallback) {
			double r = this.r.evalAsDouble(eval);
			double g = this.g.evalAsDouble(eval);
			double b = this.b.evalAsDouble(eval);
			if (r >= 0 || g >= 0 || b >= 0) {
				return 0xFF000000 | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
			}
			return parseHex(text.evalAsString(eval), fallback);
		}

		private static int clamp(double v) {
			return (int)Math.clamp(v, 0, 255);
		}
	}
}