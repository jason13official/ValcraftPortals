package io.github.jason13official.valcraft_portals.impl.network.packet;

import io.github.jason13official.valcraft_portals.Constants;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.github.jason13official.valcraft_portals.impl.common.portal.PortalNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SetPortalTagC2SPacket(BlockPos pos, String tag) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<SetPortalTagC2SPacket> TYPE = new CustomPacketPayload.Type<>(ValcraftPortals.id("set_portal_tag"));

  public static final StreamCodec<ByteBuf, SetPortalTagC2SPacket> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, SetPortalTagC2SPacket::pos,
      ByteBufCodecs.stringUtf8(PortalBlockEntity.MAX_TAG_LENGTH), SetPortalTagC2SPacket::tag,
      SetPortalTagC2SPacket::new);

  private static final double REACH = 3.0;

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handleOnServer(SetPortalTagC2SPacket packet, ServerPlayer player) {

    ServerLevel level = player.serverLevel();
    BlockPos pos = packet.pos();

    if (!level.isLoaded(pos) || !player.canInteractWithBlock(pos, REACH)) {
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
