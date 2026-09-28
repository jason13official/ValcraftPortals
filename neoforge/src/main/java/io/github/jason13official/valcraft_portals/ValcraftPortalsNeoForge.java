package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.common.registry.ModBlocks;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModItems;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTabs;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import io.github.jason13official.valcraft_portals.impl.network.packet.OpenPortalTagScreenS2CPacket;
import io.github.jason13official.valcraft_portals.impl.network.packet.SetPortalTagC2SPacket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ValcraftPortalsNeoForge {

  public static IEventBus EVENT_BUS;

  public ValcraftPortalsNeoForge(IEventBus eventBus) {

    EVENT_BUS = eventBus;

    ValcraftPortals.init();

    bind(Registries.BLOCK, ModBlocks::register);
    bind(Registries.ITEM, ModItems::register);
    bind(Registries.BLOCK_ENTITY_TYPE, ModTiles::register);
    bind(Registries.CREATIVE_MODE_TAB, ModTabs::register);

    EVENT_BUS.addListener((RegisterPayloadHandlersEvent event) -> {

      PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);
      registrar.playToClient(OpenPortalTagScreenS2CPacket.TYPE, OpenPortalTagScreenS2CPacket.STREAM_CODEC,
          (payload, context) -> ValcraftPortalsClient.handleOpenPortalTagScreen(payload));
      registrar.playToServer(SetPortalTagC2SPacket.TYPE, SetPortalTagC2SPacket.STREAM_CODEC, (payload, context) -> {
        if (context.player() instanceof ServerPlayer player) {
          SetPortalTagC2SPacket.handleOnServer(payload, player);
        }
      });
    });

    if (FMLLoader.getDist() == Dist.CLIENT) {
      new ValcraftPortalsClientNeoForge(EVENT_BUS);
    }
  }

  private <T> void bind(ResourceKey<Registry<T>> registryKey, Consumer<BiConsumer<T, ResourceLocation>> source) {

    EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
      if (registryKey.equals(event.getRegistryKey())) {
        source.accept((t, rl) -> event.register(registryKey, rl, () -> t));
      }
    });
  }
}
