package io.github.jason13official.valcraft_portals.impl.common.registry;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {

  public static Block PORTAL;
  public static Block STONE_PORTAL;

  public static void register(BiConsumer<Block, ResourceLocation> consumer) {

    PORTAL = new PortalBlock(true, portalProperties().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD));
    STONE_PORTAL = new PortalBlock(false, portalProperties().mapColor(MapColor.STONE).strength(3.0F).sound(SoundType.STONE));

    consumer.accept(PORTAL, ValcraftPortals.id("portal"));
    consumer.accept(STONE_PORTAL, ValcraftPortals.id("stone_portal"));
  }

  private static BlockBehaviour.Properties portalProperties() {

    return BlockBehaviour.Properties.of()
        .noCollission()
        .noOcclusion()
        .noTerrainParticles()
        .pushReaction(PushReaction.BLOCK)
        .lightLevel(state -> state.getValue(PortalBlock.LIT) && PortalBlock.isMaster(state) ? 12 : 0);
  }
}
