package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.client.renderer.PortalRenderer;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import io.github.jason13official.valcraft_portals.impl.network.packet.OpenPortalTagScreenS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ValcraftPortalsClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    ValcraftPortalsClient.c2s = ClientPlayNetworking::send;
    ValcraftPortalsClient.init();

    EntityModelLayerRegistry.registerModelLayer(PortalModel.WOOD_LAYER, PortalModel::createWoodLayer);
    EntityModelLayerRegistry.registerModelLayer(PortalModel.STONE_LAYER, PortalModel::createStoneLayer);
    BlockEntityRenderers.register(ModTiles.PORTAL, PortalRenderer::new);

    ClientPlayNetworking.registerGlobalReceiver(OpenPortalTagScreenS2CPacket.TYPE, (payload, context) -> ValcraftPortalsClient.handleOpenPortalTagScreen(payload));
  }
}
