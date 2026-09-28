package io.github.jason13official.valcraft_portals.impl.common.block.entity;

import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PortalBlockEntity extends BlockEntity implements Nameable {

  public PortalBlockEntity(BlockPos pPos, BlockState pBlockState) {
    super(ModTiles.PORTAL, pPos, pBlockState);
  }

  public static void tickClient(Level pLevel, BlockPos pPos, BlockState pState, PortalBlockEntity portal) {
  }

  @Override
  public Component getName() {

    return Component.literal("");
  }

  public static void tickServer(Level pLevel, BlockPos pPos, BlockState pState, PortalBlockEntity portal) {
  }
}
