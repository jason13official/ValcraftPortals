package io.github.jason13official.valcraft_portals.impl.client;

import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PortalEffects {

  public static final double RING_CENTER_Y = 22.0 / 16.0;
  public static final double RING_RADIUS = 18.5 / 16.0;

  public static void tick(Level level, BlockPos master, BlockState state, RandomSource random) {

    Direction facing = state.getValue(PortalBlock.FACING);
    Direction alongDir = PortalBlock.alongDirection(facing);
    Vec3 along = Vec3.atLowerCornerOf(alongDir.getNormal());
    Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
    Vec3 center = Vec3.atBottomCenterOf(master).add(0.0, RING_CENTER_Y, 0.0);

    Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    double side = camera.subtract(center).dot(normal) >= 0 ? -1.0 : 1.0;
    Vec3 sink = center.add(normal.scale(side * 0.9));

    for (int i = 0; i < 2; i++) {
      double angle = random.nextDouble() * Mth.TWO_PI;
      double radius = RING_RADIUS * Math.sqrt(random.nextDouble()) * 0.95;
      Vec3 radial = along.scale(Math.cos(angle)).add(0.0, Math.sin(angle), 0.0);
      Vec3 tangent = along.scale(-Math.sin(angle)).add(0.0, Math.cos(angle), 0.0);
      Vec3 start = center.add(radial.scale(radius)).add(normal.scale(-side * 0.15));
      Vec3 velocity = sink.subtract(start).scale(0.06).add(tangent.scale(0.04));

      level.addParticle(random.nextInt(4) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, start.x, start.y, start.z, velocity.x, velocity.y, velocity.z);
    }

    if (random.nextInt(3) == 0) {
      double angle = random.nextDouble() * Mth.TWO_PI;
      Vec3 radial = along.scale(Math.cos(angle)).add(0.0, Math.sin(angle), 0.0);
      Vec3 start = center.add(radial.scale(RING_RADIUS + random.nextDouble() * 0.2)).add(normal.scale((random.nextDouble() - 0.5) * 0.4));
      Vec3 velocity = radial.scale(0.015).add(0.0, 0.01 + random.nextDouble() * 0.02, 0.0);

      level.addParticle(random.nextInt(4) == 0 ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME, start.x, start.y, start.z, velocity.x, velocity.y, velocity.z);
    }

    if (random.nextInt(100) == 0) {
      level.playLocalSound(center.x, center.y, center.z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.6F + random.nextFloat() * 0.4F, 0.7F + random.nextFloat() * 0.3F, false);
    }
  }
}
