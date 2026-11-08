package io.phanisment.urars.skill.mechanics;

import io.phanisment.urars.parser.math.ast.Node;
import io.phanisment.urars.skill.SkillMechanic;
import io.phanisment.urars.skill.SkillContext;
import io.phanisment.urars.skill.annotation.RegistryInfo;
import io.phanisment.urars.skill.config.SkillLineConfig;
import io.phanisment.urars.skill.target.IEntityTarget;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import static io.phanisment.urars.util.ActionUtils.*;

@RegistryInfo(
		author = "Phanisment",
		key = "playSound",
		aliases = {
				"sound",
				"ps"
		}
)
public class PlaySoundMechanic extends SkillMechanic implements IEntityTarget {

	protected final Node sound;
	protected final Node volume;
	protected final Node pitch;

	protected final SoundSource category;

	public PlaySoundMechanic(SkillLineConfig config) {
		super(config);

		this.sound = config.getMathString(aliases("sound", "s"), "minecraft:entity.player.levelup");
		this.volume = config.getMathNumber(aliases("volume", "v"), 1.0);
		this.pitch = config.getMathNumber(aliases("pitch", "p"), 1.0);

		this.category = SoundSource.PLAYERS;
	}

	@Override
	public void castAtEntity(SkillContext ctx, Entity target) {
		if (!(target.level() instanceof ServerLevel level)) return;

		var id = Identifier.tryParse(sound.evalAsString(ctx.evalContext()));

		if (id == null) return;

		SoundEvent event = BuiltInRegistries.SOUND_EVENT.getValue(id);

		if (event == null) return;

		float volumeValue = volume.evalAsFloat(ctx.evalContext());
		float pitchValue = pitch.evalAsFloat(ctx.evalContext());

		level.playSound(null, target.getX(), target.getY(), target.getZ(), event, category, volumeValue, pitchValue);
	}
}