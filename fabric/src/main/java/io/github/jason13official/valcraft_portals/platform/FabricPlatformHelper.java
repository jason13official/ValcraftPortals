package io.github.jason13official.valcraft_portals.platform;

import io.github.jason13official.valcraft_portals.platform.services.IPlatformHelper;
import java.nio.file.Path;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab.Builder;

public class FabricPlatformHelper implements IPlatformHelper {

  @Override
  public String getPlatformName() {
    return "Fabric";
  }

  @Override
  public boolean isModLoaded(String modId) {
    return FabricLoader.getInstance().isModLoaded(modId);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }

  @Override
  public Builder creativeTabBuilder() {

    return FabricItemGroup.builder();
  }

  @Override
  public Path getConfigDirectory() {

    return FabricLoader.getInstance().getConfigDir();
  }

  @Override
  public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {

    ServerPlayNetworking.send(player, payload);
  }
}
