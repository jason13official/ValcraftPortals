package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.common.registry.ModBlocks;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModItems;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTabs;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.ModInitializer;
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
  }

  private <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {

    source.accept((t, rl) -> Registry.register(registry, rl, t));
  }
}
