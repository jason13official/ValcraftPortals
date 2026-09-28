package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.client.config.ClientConfig;
import io.github.jason13official.valcraft_portals.impl.client.gui.PortalTagScreen;
import io.github.jason13official.valcraft_portals.impl.client.gui.PortalTravelScreen;
import io.github.jason13official.valcraft_portals.impl.common.config.ModConfigIO;
import io.github.jason13official.valcraft_portals.impl.network.packet.OpenPortalTagScreenS2CPacket;
import io.github.jason13official.valcraft_portals.impl.network.packet.PortalTravelS2CPacket;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ValcraftPortalsClient {

  public static Consumer<CustomPacketPayload> c2s = payload -> {};

  private static PortalTravelScreen travelScreen;

  public static void init() {

    ModConfigIO.getOrCreateClient();
  }

  public static void handleOpenPortalTagScreen(OpenPortalTagScreenS2CPacket packet) {

    Minecraft.getInstance().setScreen(new PortalTagScreen(packet.pos(), packet.tag()));
  }

  public static void handlePortalTravel(PortalTravelS2CPacket packet) {

    if (!ClientConfig.PORTAL_TRAVEL_SCREEN.get()) {
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();

    if (!packet.arrived() || travelScreen == null || travelScreen.isFinished()) {
      travelScreen = new PortalTravelScreen();
    }

    if (packet.arrived()) {
      travelScreen.arrive();
    }

    if (minecraft.screen != travelScreen) {
      minecraft.setScreen(travelScreen);
    }
  }
}
