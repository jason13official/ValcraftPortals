package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.client.renderer.PortalRenderer;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModBlocks;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ValcraftPortalsClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    EntityModelLayerRegistry.registerModelLayer(PortalModel.LAYER_LOCATION, PortalModel::createBodyLayer);
    BlockEntityRenderers.register(ModTiles.PORTAL, PortalRenderer::new);
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PORTAL, RenderType.cutout());
  }
}
