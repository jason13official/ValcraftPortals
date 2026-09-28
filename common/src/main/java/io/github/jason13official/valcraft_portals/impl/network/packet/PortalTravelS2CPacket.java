package io.github.jason13official.valcraft_portals.impl.network.packet;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PortalTravelS2CPacket(boolean arrived) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<PortalTravelS2CPacket> TYPE = new CustomPacketPayload.Type<>(ValcraftPortals.id("portal_travel"));

  public static final StreamCodec<ByteBuf, PortalTravelS2CPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, PortalTravelS2CPacket::arrived,
      PortalTravelS2CPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
