package io.github.jason13official.valcraft_portals.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PortalRenderer implements BlockEntityRenderer<PortalBlockEntity> {

  private static final ResourceLocation PORTAL_LOCATION = ValcraftPortals.id("textures/blockentity/portal/portal.png");

  private final PortalModel portalModel;

  public PortalRenderer(BlockEntityRendererProvider.Context context) {

    this.portalModel = new PortalModel(context.bakeLayer(PortalModel.LAYER_LOCATION));
  }

  /// @see net.minecraft.client.renderer.blockentity.EnchantTableRenderer
  /// @see net.minecraft.client.model.HumanoidModel
  @Override
  public void render(PortalBlockEntity portal, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

    pPoseStack.pushPose();

    pPoseStack.translate(8 * 0.0625F, 0.0F, 8 * 0.0625F);
    pPoseStack.translate(0.0F, 1.5F, 0.0F);

    pPoseStack.scale(-1.0F, -1.0F, 1.0F);

    if (portal.getBlockState().getValue(PortalBlock.FACING).getAxis() == Axis.Z) {
      pPoseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0f));
    }

    // float f = (float)portal.time + pPartialTick;

    // this.portalModel.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
    VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(PORTAL_LOCATION)); // BOOK_LOCATION.buffer(pBufferSource, RenderType::entitySolid);
    this.portalModel.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, -1);

    pPoseStack.popPose();
  }
}
