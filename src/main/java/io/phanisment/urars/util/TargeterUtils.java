package io.phanisment.urars.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public final class TargeterUtils {

	public static @Nullable Entity getTargetedEntity(Entity entity, int range) {
		if (!(entity.level() instanceof  ServerLevel level)) return null;
		MinecraftServer server = level.getServer();

		if (server.isSameThread()) return findTarget(entity, level, range);
		try {
			return server.submit(() -> Objects.requireNonNull(findTarget(entity, level, range))).get();
		} catch (InterruptedException | ExecutionException e) {
			UrArsDebugger.warn("Somehow its error", e);
			return null;
		}
	}

	public static @Nullable Entity findTarget(Entity entity, ServerLevel level, int range) {
		Vec3 eye = entity.getEyePosition();
		Vec3 dir = entity.getViewVector(1);
		Vec3 far_end = eye.add(dir.scale(range));

		BlockHitResult block_hit = BlockGetter.<BlockHitResult, Level>traverseBlocks(eye, far_end, level, (lvl, pos) -> {
			BlockState state = lvl.getBlockState(pos);
			if (state.is(Blocks.BARRIER)) return null;
			VoxelShape shape = state.getCollisionShape(lvl, pos);
			return shape.isEmpty() ? null : shape.clip(eye, far_end, pos);
		}, lvl -> null);
		Vec3 end = block_hit.getLocation();
		AABB search = entity.getBoundingBox().expandTowards(end.subtract(eye)).inflate(1);
		Predicate<Entity> filter = e -> e instanceof Entity && e != entity && !e.isSpectator() && e.isPickable() && !(e instanceof Player p);
		EntityHitResult hit = ProjectileUtil.getEntityHitResult(entity, eye, end, search, filter, eye.distanceTo(end));
		return hit != null ? hit.getEntity() : null;
	}
}
