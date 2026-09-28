package io.github.jason13official.valcraft_portals.impl.network.packet;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenPortalTagScreenS2CPacket(BlockPos pos, String tag) implements ModPacket {

  public static final ResourceLocation ID = ValcraftPortals.id("open_portal_tag_screen");

  public static OpenPortalTagScreenS2CPacket read(FriendlyByteBuf input) {
    return new OpenPortalTagScreenS2CPacket(input.readBlockPos(), input.readUtf(PortalBlockEntity.MAX_TAG_LENGTH));
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
}
