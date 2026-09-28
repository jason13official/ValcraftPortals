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
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PortalBlockEntity extends BlockEntity implements Nameable {

  public static final int MAX_TAG_LENGTH = 10;

  private static final int LINGER_TICKS = 40;
  private static final float FADE_STEP = 0.1F;

  private String tag = "";

  private long lastPlayerNearby = -LINGER_TICKS - 1;
  private float activation;
  private float previousActivation;

  public PortalBlockEntity(BlockPos pPos, BlockState pBlockState) {
    super(ModTiles.PORTAL, pPos, pBlockState);
  }

  public static void tickClient(Level pLevel, BlockPos pPos, BlockState pState, PortalBlockEntity portal) {

    long time = pLevel.getGameTime();
    boolean linked = pState.getValue(PortalBlock.LIT);

    if (linked && isPlayerNearby(pLevel, PortalBlock.activationArea(pPos, pState.getValue(PortalBlock.FACING)))) {
      portal.lastPlayerNearby = time;
    }

    boolean active = linked && time - portal.lastPlayerNearby <= LINGER_TICKS;

    portal.previousActivation = portal.activation;
    portal.activation = Mth.clamp(portal.activation + (active ? FADE_STEP : -FADE_STEP), 0.0F, 1.0F);

    if (active) {
      PortalEffects.tick(pLevel, pPos, pState, pLevel.getRandom());
    }
  }

  private static boolean isPlayerNearby(Level level, AABB area) {

    for (Player player : level.players()) {
      if (!player.isSpectator() && player.getBoundingBox().intersects(area)) {
        return true;
      }
    }
    return false;
  }

  public float getActivation(float partialTick) {
    return Mth.lerp(partialTick, previousActivation, activation);
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
