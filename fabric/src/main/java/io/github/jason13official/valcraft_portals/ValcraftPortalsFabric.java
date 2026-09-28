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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ValcraftPortalsFabric implements ModInitializer {

  @Override
  public void onInitialize() {

    ValcraftPortals.init();

    bind(BuiltInRegistries.BLOCK, ModBlocks::register);
    bind(BuiltInRegistries.ITEM, ModItems::register);
    bind(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModTiles::register);
    bind(BuiltInRegistries.CREATIVE_MODE_TAB, ModTabs::register);

    PayloadTypeRegistry.playS2C().register(OpenPortalTagScreenS2CPacket.TYPE, OpenPortalTagScreenS2CPacket.STREAM_CODEC);
    PayloadTypeRegistry.playS2C().register(PortalTravelS2CPacket.TYPE, PortalTravelS2CPacket.STREAM_CODEC);
    PayloadTypeRegistry.playC2S().register(SetPortalTagC2SPacket.TYPE, SetPortalTagC2SPacket.STREAM_CODEC);
    ServerPlayNetworking.registerGlobalReceiver(SetPortalTagC2SPacket.TYPE, (payload, context) -> SetPortalTagC2SPacket.handleOnServer(payload, context.player()));
  }

  private <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {

    source.accept((t, rl) -> Registry.register(registry, rl, t));
  }
}
