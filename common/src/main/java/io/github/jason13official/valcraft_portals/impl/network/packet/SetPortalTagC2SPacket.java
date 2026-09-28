package io.github.jason13official.valcraft_portals.impl.network.packet;

import io.github.jason13official.valcraft_portals.Constants;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.github.jason13official.valcraft_portals.impl.common.portal.PortalNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public record SetPortalTagC2SPacket(BlockPos pos, String tag) implements ModPacket {

  public static final ResourceLocation ID = ValcraftPortals.id("set_portal_tag");

  private static final double MAX_DISTANCE_SQR = 8.0 * 8.0;

  public static SetPortalTagC2SPacket read(FriendlyByteBuf input) {
    return new SetPortalTagC2SPacket(input.readBlockPos(), input.readUtf(PortalBlockEntity.MAX_TAG_LENGTH));
  }

  @Override
  public void write(FriendlyByteBuf output) {
    output.writeBlockPos(this.pos);
    output.writeUtf(this.tag, PortalBlockEntity.MAX_TAG_LENGTH);
  }

  @Override
  public ResourceLocation id() {
    return ID;
  }

  public static void handleOnServer(SetPortalTagC2SPacket packet, ServerPlayer player) {

    ServerLevel level = player.serverLevel();
    BlockPos pos = packet.pos();

    if (!level.isLoaded(pos) || player.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_DISTANCE_SQR) {
      Constants.LOG.debug("Player {} tried to retag an out of reach portal at {}", player.getName().getString(), pos);
      return;
    }

    if (!(level.getBlockState(pos).getBlock() instanceof PortalBlock) || !(level.getBlockEntity(pos) instanceof PortalBlockEntity portal)) {
      return;
    }

    portal.setTag(level, packet.tag());

    PortalNetwork network = PortalNetwork.get(level.getServer());
    GlobalPos self = GlobalPos.of(level.dimension(), pos);
    String key = network.partner(self).isPresent() ? "linked" : network.isOverflowing(self) ? "full" : "waiting";

    player.displayClientMessage(Component.translatable("message.valcraft_portals." + key, portal.getTag()), true);
  }
}
