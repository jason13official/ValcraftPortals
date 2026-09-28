package io.github.jason13official.valcraft_portals.impl.common.registry;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModTiles {

  public static BlockEntityType<PortalBlockEntity> PORTAL;

  public static void register(BiConsumer<BlockEntityType<?>, ResourceLocation> consumer) {

    PORTAL = BlockEntityType.Builder.of(PortalBlockEntity::new, ModBlocks.PORTAL, ModBlocks.STONE_PORTAL).build(null);

    consumer.accept(PORTAL, ValcraftPortals.id("portal"));
  }
}
