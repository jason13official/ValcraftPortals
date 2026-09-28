package io.github.jason13official.valcraft_portals.impl.common.registry;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

  public static Block PORTAL;

  public static void register(BiConsumer<Block, ResourceLocation> consumer) {

    PORTAL = new PortalBlock(BlockBehaviour.Properties.of().noCollission().noOcclusion());

    consumer.accept(PORTAL, ValcraftPortals.id("portal"));
  }
}
