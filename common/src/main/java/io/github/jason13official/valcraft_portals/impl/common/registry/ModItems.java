package io.github.jason13official.valcraft_portals.impl.common.registry;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class ModItems {

  public static Item PORTAL;
  public static Item STONE_PORTAL;

  public static void register(BiConsumer<Item, ResourceLocation> consumer) {

    PORTAL = new BlockItem(ModBlocks.PORTAL, new Properties().stacksTo(16));
    STONE_PORTAL = new BlockItem(ModBlocks.STONE_PORTAL, new Properties().stacksTo(16));

    consumer.accept(PORTAL, ValcraftPortals.id("portal"));
    consumer.accept(STONE_PORTAL, ValcraftPortals.id("stone_portal"));
  }
}
