package io.github.jason13official.valcraft_portals.impl.network.packet;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PortalTravelS2CPacket(boolean arrived) implements ModPacket {

  public static final ResourceLocation ID = ValcraftPortals.id("portal_travel");

  public static PortalTravelS2CPacket read(FriendlyByteBuf input) {
    return new PortalTravelS2CPacket(input.readBoolean());
  }

  @Override
  public void write(FriendlyByteBuf output) {
    output.writeBoolean(this.arrived);
  }

  @Override
  public ResourceLocation id() {
    return ID;
  }
}
