package io.github.jason13official.valcraft_portals.impl.common.config;

import io.github.jason13official.monolib.api.common.config.ConfigGetterSetter.Commented;

public class ServerConfig {

  private static boolean restrictItems = true;
  private static boolean allowCrossDimension = true;

  public static Commented<Boolean> RESTRICT_ITEMS = new Commented<>("restrict_items", () -> restrictItems, value -> restrictItems = value,
      "Whether wooden portals refuse players carrying items tagged #valcraft_portals:portal_restricted (ores, metals, gems...). Stone portals never restrict.");
  public static Commented<Boolean> ALLOW_CROSS_DIMENSION = new Commented<>("allow_cross_dimension", () -> allowCrossDimension, value -> allowCrossDimension = value,
      "Whether linked portals in different dimensions can be travelled between.");
}
