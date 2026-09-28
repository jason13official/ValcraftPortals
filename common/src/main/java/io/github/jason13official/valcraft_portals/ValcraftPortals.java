package io.github.jason13official.valcraft_portals;

import net.minecraft.resources.ResourceLocation;

public class ValcraftPortals {

  public static void init() {
  }

  public static ResourceLocation id(String path) {

    return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
  }
}
