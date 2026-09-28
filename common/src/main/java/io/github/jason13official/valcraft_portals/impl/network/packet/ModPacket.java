package io.github.jason13official.valcraft_portals.impl.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ModPacket {

  ResourceLocation id();

  void write(FriendlyByteBuf output);
}
