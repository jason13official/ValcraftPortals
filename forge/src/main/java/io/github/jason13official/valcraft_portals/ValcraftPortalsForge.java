package io.github.jason13official.valcraft_portals;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class ValcraftPortalsForge {

  public static IEventBus EVENT_BUS;

  public ValcraftPortalsForge(FMLJavaModLoadingContext context) {

    EVENT_BUS = context.getModEventBus();

    ValcraftPortals.init();

    EVENT_BUS.addListener((FMLCommonSetupEvent event) -> {});
  }
}
