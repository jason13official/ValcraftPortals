package io.github.jason13official.valcraft_portals.impl.client.config;

import io.github.jason13official.monolib.api.common.config.ConfigGetterSetter.Commented;

public class ClientConfig {

  private static boolean portalTravelScreen = true;

  public static Commented<Boolean> PORTAL_TRAVEL_SCREEN = new Commented<>("portal_travel_screen", () -> portalTravelScreen, value -> portalTravelScreen = value,
      "Whether the fiery loading screen is shown while travelling through a portal.");
}
