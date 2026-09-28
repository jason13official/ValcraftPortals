package io.github.jason13official.valcraft_portals;

import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.client.renderer.PortalRenderer;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import io.github.jason13official.valcraft_portals.impl.network.packet.OpenPortalTagScreenS2CPacket;
import io.github.jason13official.valcraft_portals.impl.network.packet.PortalTravelS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.FriendlyByteBuf;

public class ValcraftPortalsClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    ValcraftPortalsClient.c2s = packet -> {
      FriendlyByteBuf buf = PacketByteBufs.create();
      packet.write(buf);
      ClientPlayNetworking.send(packet.id(), buf);
    };
    ValcraftPortalsClient.init();

    EntityModelLayerRegistry.registerModelLayer(PortalModel.WOOD_LAYER, PortalModel::createWoodLayer);
    EntityModelLayerRegistry.registerModelLayer(PortalModel.STONE_LAYER, PortalModel::createStoneLayer);
    BlockEntityRenderers.register(ModTiles.PORTAL, PortalRenderer::new);

    ClientPlayNetworking.registerGlobalReceiver(OpenPortalTagScreenS2CPacket.ID, (client, handler, buf, responseSender) -> {
      OpenPortalTagScreenS2CPacket packet = OpenPortalTagScreenS2CPacket.read(buf);
      client.execute(() -> ValcraftPortalsClient.handleOpenPortalTagScreen(packet));
    });

    ClientPlayNetworking.registerGlobalReceiver(PortalTravelS2CPacket.ID, (client, handler, buf, responseSender) -> {
      PortalTravelS2CPacket packet = PortalTravelS2CPacket.read(buf);
      client.execute(() -> ValcraftPortalsClient.handlePortalTravel(packet));
    });
  }
}
