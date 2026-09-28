package io.github.jason13official.valcraft_portals.impl.common.block.entity;

import io.github.jason13official.valcraft_portals.impl.client.PortalEffects;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.portal.PortalNetwork;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PortalBlockEntity extends BlockEntity implements Nameable {

  public static final int MAX_TAG_LENGTH = 10;

  private String tag = "";

  public PortalBlockEntity(BlockPos pPos, BlockState pBlockState) {
    super(ModTiles.PORTAL, pPos, pBlockState);
  }

  public static void tickClient(Level pLevel, BlockPos pPos, BlockState pState, PortalBlockEntity portal) {

    if (pState.getValue(PortalBlock.LIT)) {
      PortalEffects.tick(pLevel, pPos, pState, pLevel.getRandom());
    }
  }

  public static void tickServer(Level pLevel, BlockPos pPos, BlockState pState, PortalBlockEntity portal) {

    if (!(pLevel instanceof ServerLevel level) || (level.getGameTime() + pPos.asLong()) % 20 != 0) {
      return;
    }

    portal.sync(level);
  }

  public void sync(ServerLevel level) {

    PortalNetwork network = PortalNetwork.get(level.getServer());
    GlobalPos self = GlobalPos.of(level.dimension(), worldPosition);

    if (!network.tagOf(self).map(tag::equals).orElse(false)) {
      network.register(level.getServer(), self, tag);
    }
    network.refreshLit(level, worldPosition);
  }

  public String getTag() {
    return tag;
  }

  public void setTag(ServerLevel level, String tag) {

    this.tag = sanitize(tag);
    setChanged();
    PortalNetwork.get(level.getServer()).register(level.getServer(), GlobalPos.of(level.dimension(), worldPosition), this.tag);
  }

  public static String sanitize(String tag) {

    String filtered = StringUtil.filterText(tag);
    return filtered.length() > MAX_TAG_LENGTH ? filtered.substring(0, MAX_TAG_LENGTH) : filtered;
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
    super.loadAdditional(nbt, registries);
    this.tag = sanitize(nbt.getString("Tag"));
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
    super.saveAdditional(nbt, registries);
    nbt.putString("Tag", tag);
  }

  @Override
  public Component getName() {

    return Component.literal(tag);
  }
}
