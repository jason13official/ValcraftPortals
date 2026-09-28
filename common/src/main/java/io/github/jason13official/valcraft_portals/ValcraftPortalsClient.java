package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.client.gui.PortalTagScreen;
import io.github.jason13official.valcraft_portals.impl.network.packet.OpenPortalTagScreenS2CPacket;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ValcraftPortalsClient {

  public static Consumer<CustomPacketPayload> c2s = payload -> {};

  public static void init() {
  }

  public static void handleOpenPortalTagScreen(OpenPortalTagScreenS2CPacket packet) {

    Minecraft.getInstance().setScreen(new PortalTagScreen(packet.pos(), packet.tag()));
  }
}
