package io.phanisment.urars.mixin;

import io.phanisment.urars.skill.TriggerType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.phanisment.urars.util.ItemUtils.castItem;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "setShiftKeyDown", at = @At("HEAD"))
	private void urars$onSneaking(boolean shiftKeyDown, CallbackInfo ci) {
		Entity entity = (Entity)(Object)this;
		if (shiftKeyDown) castItem(entity, TriggerType.ON_SNEAK);
	}
}