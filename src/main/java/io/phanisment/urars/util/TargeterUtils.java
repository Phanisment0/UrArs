package io.phanisment.urars.util;

import net.minecraft.core.particles.ParticleTypes;
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

import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TargeterUtils {

	public static @Nullable Entity getTargetedEntity(Entity entity, int range) {
		if (entity == null || !(entity.level() instanceof ServerLevel level)) return null;
		return onServerThread(level, () -> findTarget(entity, level, range));
	}

	public static @Nullable Location getTargetedLocation(Entity entity, double range) {
		if (entity == null || !(entity.level() instanceof ServerLevel level)) return null;
		return onServerThread(level, () -> rayEnd(entity, level, range));
	}

	public static @Nullable Entity findTarget(Entity entity, ServerLevel level, int range) {
		Vec3 eye = entity.getEyePosition();
		Vec3 end = rayEnd(entity, level, range).pos();

		AABB search = entity.getBoundingBox().expandTowards(end.subtract(eye)).inflate(1);
		Predicate<Entity> filter = e -> e != entity && !e.isSpectator() && e.isPickable() && !(e instanceof Player);
		EntityHitResult hit = ProjectileUtil.getEntityHitResult(entity, eye, end, search, filter, eye.distanceTo(end));
		return hit != null ? hit.getEntity() : null;
	}

	/** First block hit along the look direction, or the end of the ray if nothing is hit. Carries the entity's rotation. */
	public static Location rayEnd(Entity entity, ServerLevel level, double range) {
		Vec3 eye = entity.getEyePosition();
		Vec3 far_end = eye.add(entity.getViewVector(1).scale(range));

		BlockHitResult block_hit = BlockGetter.<BlockHitResult, Level>traverseBlocks(eye, far_end, level, (lvl, pos) -> {
			BlockState state = lvl.getBlockState(pos);
			if (state.is(Blocks.BARRIER)) return null;
			VoxelShape shape = state.getCollisionShape(lvl, pos);
			return shape.isEmpty() ? null : shape.clip(eye, far_end, pos);
		}, _ -> null);

		Vec3 end = block_hit != null ? block_hit.getLocation() : far_end;
		return new Location(level, end, entity.getXRot(), entity.getYRot());
	}

	private static <T> @Nullable T onServerThread(ServerLevel level, Supplier<T> task) {
		MinecraftServer server = level.getServer();
		if (server.isSameThread()) return task.get();
		try {
			return server.submit(task).get();
		} catch (InterruptedException | ExecutionException e) {
			UrArsDebugger.warn("Failed to run targeter on server thread", e);
			return null;
		}
	}

	public static void drawPoint(Level level, Vec3 pos, boolean enabled) {
		if (!enabled || !(level instanceof ServerLevel server_level)) return;
		server_level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z,1,0, 0,0,0);
	}

	public static void drawBox(ServerLevel level, AABB box) {
		Vec3 min = new Vec3(box.minX, box.minY, box.minZ);
		Vec3 max = new Vec3(box.maxX, box.maxY, box.maxZ);

		Vec3[] corners = new Vec3[8];

		for (int i = 0; i < 8; i++) corners[i] = new Vec3((i & 1) == 0 ? min.x : max.x, (i & 2) == 0 ? min.y : max.y, (i & 4) == 0 ? min.z : max.z);
		for (int i = 0; i < 8; i++) {
			for (int bit : new int[]{1, 2, 4}) {
				int j = i ^ bit;
				if (j <= i) continue;
				drawLine(level, corners[i], corners[j]);
			}
		}
	}

	public static void drawBox(Level level, OBB box) {
		if (!(level instanceof ServerLevel server_level)) return;
		Vec3[] corners = box.getCorners();

		// Connect the 8 corners into 12 edges.
		// Each corner has three neighboring corners.
		for (int i = 0; i < corners.length; i++) {
			for (int bit : new int[]{1, 2, 4}) {
				int j = i ^ bit;
				if (j <= i) continue;
				drawLine(server_level, corners[i], corners[j]);
			}
		}
	}

	private static void drawLine(ServerLevel level, Vec3 from, Vec3 to) {
		double distance = from.distanceTo(to);

		// One particle approximately every 0.25 blocks.
		int steps = Math.clamp((int)Math.ceil(distance / 0.25), 1, 64);

		for (int i = 0; i <= steps; i++) {
			double progress = (double) i / steps;
			Vec3 point = from.lerp(to, progress);
			level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0, 0, 0, 0);
		}
	}
}
