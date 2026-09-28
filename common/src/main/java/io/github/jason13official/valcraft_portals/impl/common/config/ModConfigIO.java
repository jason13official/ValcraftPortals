package io.github.jason13official.valcraft_portals.impl.common.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.github.jason13official.monolib.api.common.config.ConfigGetterSetter.Commented;
import io.github.jason13official.valcraft_portals.Constants;
import io.github.jason13official.valcraft_portals.impl.client.config.ClientConfig;
import io.github.jason13official.valcraft_portals.platform.Services;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ModConfigIO {

  private static final List<Commented<Boolean>> SERVER_ENTRIES = List.of(ServerConfig.RESTRICT_ITEMS, ServerConfig.ALLOW_CROSS_DIMENSION);
  private static final List<Commented<Boolean>> CLIENT_ENTRIES = List.of(ClientConfig.PORTAL_TRAVEL_SCREEN);

  public static void getOrCreate() {

    load(Constants.MOD_ID + "-server.toml", SERVER_ENTRIES);
  }

  public static void getOrCreateClient() {

    load(Constants.MOD_ID + "-client.toml", CLIENT_ENTRIES);
  }

  private static void load(String fileName, List<Commented<Boolean>> entries) {

    Path configDir = Services.PLATFORM.getConfigDirectory();
    File configDirectory = new File(configDir.toUri());

    if (!configDirectory.isDirectory() && !configDirectory.mkdirs()) {

      return;
    }

    Path configFilepath = configDir.resolve(fileName);
    File configFile = new File(configFilepath.toUri());

    Config.setInsertionOrderPreserved(true);
    try (CommentedFileConfig config = CommentedFileConfig.builder(configFile).build()) {

      if (Files.exists(configFilepath)) {
        config.load();
      }

      for (Commented<Boolean> entry : entries) {
        entry.set(config.getOrElse(entry.key(), entry.get()));
      }

      for (Commented<Boolean> entry : entries) {
        config.setComment(entry.key(), entry.comment());
        config.set(entry.key(), entry.get());
      }

      config.save();
    } catch (Exception e) {
      Constants.LOG.error("Failed to read or write {}", configFilepath, e);
    }
  }
}
