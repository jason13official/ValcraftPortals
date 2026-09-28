package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.common.registry.ModBlocks;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModItems;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTabs;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import io.github.jason13official.valcraft_portals.impl.network.packet.OpenPortalTagScreenS2CPacket;
import io.github.jason13official.valcraft_portals.impl.network.packet.PortalTravelS2CPacket;
import io.github.jason13official.valcraft_portals.impl.network.packet.SetPortalTagC2SPacket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ValcraftPortalsForge {

  private static final String PROTOCOL_VERSION = "1";

  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ValcraftPortals.id("main"),
      () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public static IEventBus EVENT_BUS;

  public ValcraftPortalsForge(FMLJavaModLoadingContext context) {

    EVENT_BUS = context.getModEventBus();

    ValcraftPortals.init();

    bind(Registries.BLOCK, ModBlocks::register);
    bind(Registries.ITEM, ModItems::register);
    bind(Registries.BLOCK_ENTITY_TYPE, ModTiles::register);
    bind(Registries.CREATIVE_MODE_TAB, ModTabs::register);

    CHANNEL.messageBuilder(OpenPortalTagScreenS2CPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
        .encoder(OpenPortalTagScreenS2CPacket::write)
        .decoder(OpenPortalTagScreenS2CPacket::read)
        .consumerMainThread((packet, ctx) -> ValcraftPortalsClient.handleOpenPortalTagScreen(packet))
        .add();

    CHANNEL.messageBuilder(PortalTravelS2CPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
        .encoder(PortalTravelS2CPacket::write)
        .decoder(PortalTravelS2CPacket::read)
        .consumerMainThread((packet, ctx) -> ValcraftPortalsClient.handlePortalTravel(packet))
        .add();

    CHANNEL.messageBuilder(SetPortalTagC2SPacket.class, 2, NetworkDirection.PLAY_TO_SERVER)
        .encoder(SetPortalTagC2SPacket::write)
        .decoder(SetPortalTagC2SPacket::read)
        .consumerMainThread((packet, ctx) -> {
          ServerPlayer player = ctx.get().getSender();
          if (player != null) {
            SetPortalTagC2SPacket.handleOnServer(packet, player);
          }
        })
        .add();

    if (FMLLoader.getDist() == Dist.CLIENT) {
      new ValcraftPortalsClientForge(EVENT_BUS);
    }
  }

  @Deprecated
  @SuppressWarnings("all")
  public ValcraftPortalsForge() {
    this(FMLJavaModLoadingContext.get());
  }

  private <T> void bind(ResourceKey<Registry<T>> registryKey, Consumer<BiConsumer<T, ResourceLocation>> source) {

    EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
      if (registryKey.equals(event.getRegistryKey())) {
        source.accept((t, rl) -> event.register(registryKey, rl, () -> t));
      }
    });
  }
}
