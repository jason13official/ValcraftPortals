package io.github.jason13official.valcraft_portals.platform.services;

import java.nio.file.Path;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;

public interface IPlatformHelper {

  /**
   * Gets the name of the current platform
   *
   * @return The name of the current platform.
   */
  String getPlatformName();

  /**
   * Checks if a mod with the given id is loaded.
   *
   * @param modId The mod to check if it is loaded.
   * @return True if the mod is loaded, false otherwise.
   */
  boolean isModLoaded(String modId);

  /**
   * Check if the game is currently in a development environment.
   *
   * @return True if in a development environment, false otherwise.
   */
  boolean isDevelopmentEnvironment();

  /**
   * Gets the name of the environment type as a string.
   *
   * @return The name of the environment type.
   */
  default String getEnvironmentName() {
    return isDevelopmentEnvironment() ? "development" : "production";
  }

  CreativeModeTab.Builder creativeTabBuilder();

  Path getConfigDirectory();

  void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
}
