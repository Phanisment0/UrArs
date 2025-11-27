package io.phanisment.urars.mob.type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import io.phanisment.urars.mob.Mob;
import io.phanisment.urars.mob.MobManager;
import io.phanisment.urars.mob.MobConstant;

import java.util.Optional;

public class ConfigurableEntity extends Entity implements GeoEntity, IConfigurableEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private static final TrackedData<String> MODEL;
	private static final TrackedData<String> TEXTURE;
	private static final TrackedData<String> ANIMATION;
	private Identifier mob_id;
	
	public ConfigurableEntity(EntityType<? extends ConfigurableEntity> type, World world) {
		super(type, world);
	}
	
	public void setMobId(Identifier mob_id) {
		this.mob_id = mob_id;
		this.applyData();
	}
	
	private void applyData() {
		Optional<Mob> mob_data = MobManager.getMob(this.mob_id);
		if (!mob_data.isPresent()) return;
		Mob mob = mob_data.get();
		this.setModel(mob.getModel());
		this.setTexture(mob.getTexture());
		this.setAnimation(mob.getAnimation());
	}
	
	@Override
	public void setModel(Identifier path) {
		this.dataTracker.set(MODEL, path.toString());
	}
	
	@Override
	public void setTexture(Identifier path) {
		this.dataTracker.set(TEXTURE, path.toString());
	}
	
	@Override
	public void setAnimation(Identifier path) {
		this.dataTracker.set(ANIMATION, path.toString());
	}
	
	@Override
	public Identifier getModel() {
		return Identifier.of(this.dataTracker.get(MODEL));
	}
	
	@Override
	public Identifier getTexture() {
		return Identifier.of(this.dataTracker.get(TEXTURE));
	}
	
	@Override
	public Identifier getAnimation() {
		return Identifier.of(this.dataTracker.get(ANIMATION));
	}
	
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
	
	@Override
	public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, state -> PlayState.CONTINUE));
	}
	
	@Override
	public void initDataTracker(DataTracker.Builder builder) {
		builder.add(MODEL, MobConstant.MODEL.toString());
		builder.add(TEXTURE, MobConstant.TEXTURE.toString());
		builder.add(ANIMATION, MobConstant.ANIMATION.toString());
	}
	
	// Load the data
	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("mob_id")) {
			this.mob_id = Identifier.of(nbt.getString("mob_id"));
			this.applyData();
		}
	}
	
	// Save the data
	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		if (mob_id != null) nbt.putString("mob_id", mob_id.toString());
	}
	
	static {
		MODEL = DataTracker.registerData(ConfigurableEntity.class, TrackedDataHandlerRegistry.STRING);
		TEXTURE = DataTracker.registerData(ConfigurableEntity.class, TrackedDataHandlerRegistry.STRING);
		ANIMATION = DataTracker.registerData(ConfigurableEntity.class, TrackedDataHandlerRegistry.STRING);
	}
}