package io.github.jason13official.valcraft_portals.impl.common.registry;

import io.github.jason13official.valcraft_portals.Constants;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.platform.Services;
import java.util.function.BiConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModTabs {

  public static CreativeModeTab VALCRAFT_PORTALS;

  public static void register(BiConsumer<CreativeModeTab, ResourceLocation> consumer) {

    VALCRAFT_PORTALS = Services.PLATFORM.creativeTabBuilder()
        .icon(() -> new ItemStack(ModItems.PORTAL))
        .title(Component.translatable("itemGroup." + Constants.MOD_ID))
        .displayItems((itemDisplayParameters, output) -> {

          output.accept(ModItems.PORTAL);
          output.accept(ModItems.STONE_PORTAL);
        })
        .build();

    consumer.accept(VALCRAFT_PORTALS, ValcraftPortals.id(Constants.MOD_ID));
  }
}
