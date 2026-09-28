package io.github.jason13official.valcraft_portals.impl.network.packet;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenPortalTagScreenS2CPacket(BlockPos pos, String tag) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<OpenPortalTagScreenS2CPacket> TYPE = new CustomPacketPayload.Type<>(ValcraftPortals.id("open_portal_tag_screen"));

  public static final StreamCodec<ByteBuf, OpenPortalTagScreenS2CPacket> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, OpenPortalTagScreenS2CPacket::pos,
      ByteBufCodecs.stringUtf8(PortalBlockEntity.MAX_TAG_LENGTH), OpenPortalTagScreenS2CPacket::tag,
      OpenPortalTagScreenS2CPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
