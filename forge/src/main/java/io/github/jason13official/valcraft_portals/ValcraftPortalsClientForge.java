package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.client.renderer.PortalRenderer;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ValcraftPortalsClientForge {

  public ValcraftPortalsClientForge(IEventBus eventBus) {

    ValcraftPortalsClient.c2s = ValcraftPortalsForge.CHANNEL::sendToServer;
    ValcraftPortalsClient.init();

    eventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
      event.registerLayerDefinition(PortalModel.WOOD_LAYER, PortalModel::createWoodLayer);
      event.registerLayerDefinition(PortalModel.STONE_LAYER, PortalModel::createStoneLayer);
    });

    eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> event.registerBlockEntityRenderer(ModTiles.PORTAL, PortalRenderer::new));
  }
}
