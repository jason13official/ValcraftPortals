package io.github.jason13official.valcraft_portals.platform;

import io.github.jason13official.valcraft_portals.ValcraftPortalsForge;
import io.github.jason13official.valcraft_portals.impl.network.packet.ModPacket;
import io.github.jason13official.valcraft_portals.platform.services.IPlatformHelper;
import java.nio.file.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;

public class ForgePlatformHelper implements IPlatformHelper {

  @Override
  public String getPlatformName() {
    return "Forge";
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
  public void sendToPlayer(ServerPlayer player, ModPacket packet) {

    ValcraftPortalsForge.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
  }
}
