package io.github.jason13official.valcraft_portals.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.client.PortalEffects;
import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModBlocks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class PortalRenderer implements BlockEntityRenderer<PortalBlockEntity> {

  private static final ResourceLocation WOOD_TEXTURE = ValcraftPortals.id("textures/entity/portal/wood.png");
  private static final ResourceLocation STONE_TEXTURE = ValcraftPortals.id("textures/entity/portal/stone.png");
  private static final ResourceLocation GLYPH_TEXTURE = ValcraftPortals.id("textures/entity/portal/glyphs.png");
  private static final ResourceLocation SWIRL_TEXTURE = ValcraftPortals.id("textures/entity/portal/swirl.png");
  private static final ResourceLocation GLOW_TEXTURE = ValcraftPortals.id("textures/entity/portal/glow.png");

  private static final int WOOD_CARVING = 0xFF3A2410;
  private static final int STONE_CARVING = 0xFF6A3A22;

  private static final float SWIRL_RADIUS = 16.5F / 16.0F;
  private static final float GLOW_RADIUS = 30.0F / 16.0F;

  private final PortalModel woodModel;
  private final PortalModel stoneModel;

  public PortalRenderer(BlockEntityRendererProvider.Context context) {

    this.woodModel = new PortalModel(context.bakeLayer(PortalModel.WOOD_LAYER));
    this.stoneModel = new PortalModel(context.bakeLayer(PortalModel.STONE_LAYER));
  }

  @Override
  public void render(PortalBlockEntity portal, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

    BlockState state = portal.getBlockState();
    if (!(state.getBlock() instanceof PortalBlock)) {
      return;
    }

    boolean stone = state.is(ModBlocks.STONE_PORTAL);
    boolean lit = state.getValue(PortalBlock.LIT);
    PortalModel model = stone ? stoneModel : woodModel;

    pPoseStack.pushPose();
    pPoseStack.translate(0.5F, 0.0F, 0.5F);
    pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.getValue(PortalBlock.FACING).toYRot()));
    pPoseStack.translate(0.0F, (float) PortalEffects.RING_CENTER_Y, 0.0F);
    pPoseStack.scale(-1.0F, -1.0F, 1.0F);

    model.renderFrame(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(stone ? STONE_TEXTURE : WOOD_TEXTURE)), pPackedLight, pPackedOverlay, -1);
    model.renderGlyphs(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(GLYPH_TEXTURE)), pPackedLight, pPackedOverlay, stone ? STONE_CARVING : WOOD_CARVING);

    if (lit && portal.getLevel() != null) {
      float time = (portal.getLevel().getGameTime() % 72000L) + pPartialTick;
      float pulse = 0.85F + 0.15F * Mth.sin(time * 0.15F);

      int glyphColor = FastColor.ARGB32.color(255, (int) (255 * pulse), (int) (150 * pulse), (int) (60 * pulse));
      model.renderGlyphs(pPoseStack, pBufferSource.getBuffer(RenderType.eyes(GLYPH_TEXTURE)), LightTexture.FULL_BRIGHT, pPackedOverlay, glyphColor);

      VertexConsumer swirl = pBufferSource.getBuffer(RenderType.entityTranslucent(SWIRL_TEXTURE));
      pPoseStack.pushPose();
      pPoseStack.mulPose(Axis.ZP.rotation(time * 0.05F));
      quad(pPoseStack.last(), swirl, SWIRL_RADIUS, -0.004F, 0xE6FFFFFF, pPackedLight, pPackedOverlay);
      pPoseStack.popPose();

      pPoseStack.pushPose();
      pPoseStack.mulPose(Axis.ZP.rotation(-time * 0.03F));
      quad(pPoseStack.last(), swirl, SWIRL_RADIUS * 0.9F, 0.004F, 0xB3FFFFFF, pPackedLight, pPackedOverlay);
      pPoseStack.popPose();

      int glow = (int) (200 * pulse);
      quad(pPoseStack.last(), pBufferSource.getBuffer(RenderType.eyes(GLOW_TEXTURE)), GLOW_RADIUS, 0.03F, FastColor.ARGB32.color(255, glow, glow, glow),
          LightTexture.FULL_BRIGHT, pPackedOverlay);
    }

    pPoseStack.popPose();
  }

  private static void quad(PoseStack.Pose pose, VertexConsumer consumer, float half, float z, int color, int light, int overlay) {

    vertex(pose, consumer, -half, -half, z, 0.0F, 0.0F, color, light, overlay);
    vertex(pose, consumer, -half, half, z, 0.0F, 1.0F, color, light, overlay);
    vertex(pose, consumer, half, half, z, 1.0F, 1.0F, color, light, overlay);
    vertex(pose, consumer, half, -half, z, 1.0F, 0.0F, color, light, overlay);
  }

  private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, int color, int light, int overlay) {

    consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(overlay).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
  }

  @Override
  public boolean shouldRenderOffScreen(PortalBlockEntity portal) {
    return true;
  }

  @Override
  public int getViewDistance() {
    return 128;
  }
}
