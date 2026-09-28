package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.common.config.ModConfigIO;
import net.minecraft.resources.ResourceLocation;

public class ValcraftPortals {

  public static void init() {

    ModConfigIO.getOrCreate();
  }

  public static ResourceLocation id(String path) {

    return new ResourceLocation(Constants.MOD_ID, path);
  }
}
