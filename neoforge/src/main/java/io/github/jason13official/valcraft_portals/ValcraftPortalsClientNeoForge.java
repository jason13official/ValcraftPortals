package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.client.renderer.PortalRenderer;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ValcraftPortalsClientNeoForge {

  public ValcraftPortalsClientNeoForge(IEventBus eventBus) {

    ValcraftPortalsClient.c2s = PacketDistributor::sendToServer;
    ValcraftPortalsClient.init();

    eventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
      event.registerLayerDefinition(PortalModel.WOOD_LAYER, PortalModel::createWoodLayer);
      event.registerLayerDefinition(PortalModel.STONE_LAYER, PortalModel::createStoneLayer);
    });

    eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> event.registerBlockEntityRenderer(ModTiles.PORTAL, PortalRenderer::new));
  }
}
