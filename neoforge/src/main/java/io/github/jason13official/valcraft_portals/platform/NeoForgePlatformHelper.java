package io.github.jason13official.valcraft_portals.platform;

import io.github.jason13official.valcraft_portals.platform.services.IPlatformHelper;
import java.nio.file.Path;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgePlatformHelper implements IPlatformHelper {

  @Override
  public String getPlatformName() {
    return "NeoForge";
  }

  @Override
  public boolean isModLoaded(String modId) {
    return ModList.get().isLoaded(modId);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return !FMLLoader.isProduction();
  }

  @Override
  public Builder creativeTabBuilder() {

    return CreativeModeTab.builder();
  }

  @Override
  public Path getConfigDirectory() {

    return FMLPaths.CONFIGDIR.get();
  }

  @Override
  public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {

    PacketDistributor.sendToPlayer(player, payload);
  }
}
