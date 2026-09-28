package io.github.jason13official.valcraft_portals.impl.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import io.github.jason13official.valcraft_portals.impl.client.PortalEffects;
import io.github.jason13official.valcraft_portals.impl.client.model.PortalModel;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalPart;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModBlocks;
import io.github.jason13official.valcraft_portals.mixin.LevelRendererAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.SortedSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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
  public boolean shouldRender(PortalBlockEntity pBlockEntity, Vec3 pCameraPos) {

    return true;
  }

  @Override
  public void render(PortalBlockEntity portal, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

    BlockState state = portal.getBlockState();
    if (!(state.getBlock() instanceof PortalBlock)) {
      return;
    }

    boolean stone = state.is(ModBlocks.STONE_PORTAL);
    float activation = portal.getActivation(pPartialTick);
    PortalModel model = stone ? stoneModel : woodModel;
    PoseStack.Pose origin = pPoseStack.last();
    int progress = destroyProgress(portal.getBlockPos(), state);

    pPoseStack.pushPose();
    pPoseStack.translate(0.5F, 0.0F, 0.5F);
    pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.getValue(PortalBlock.FACING).toYRot()));
    pPoseStack.translate(0.0F, (float) PortalEffects.RING_CENTER_Y, 0.0F);
    pPoseStack.scale(-1.0F, -1.0F, 1.0F);

    model.renderFrame(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(stone ? STONE_TEXTURE : WOOD_TEXTURE)), pPackedLight, pPackedOverlay, -1);
    model.renderGlyphs(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(GLYPH_TEXTURE)), pPackedLight, pPackedOverlay, stone ? STONE_CARVING : WOOD_CARVING);

    if (progress >= 0) {
      VertexConsumer crumbling = new SheetedDecalTextureGenerator(
          Minecraft.getInstance().renderBuffers().crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(progress)), origin.pose(), origin.normal(), 1.0F);
      model.renderFrame(pPoseStack, crumbling, pPackedLight, pPackedOverlay, -1);
      model.renderGlyphs(pPoseStack, crumbling, pPackedLight, pPackedOverlay, -1);
    }

    if (activation > 0.0F && portal.getLevel() != null) {
      float time = (portal.getLevel().getGameTime() % 72000L) + pPartialTick;
      float pulse = (0.85F + 0.15F * Mth.sin(time * 0.15F)) * activation;
      int swirlAlpha = (int) (230 * activation);
      float swirlScale = 0.6F + 0.4F * activation;

      int glyphColor = FastColor.ARGB32.color(255, (int) (255 * pulse), (int) (150 * pulse), (int) (60 * pulse));
      model.renderGlyphs(pPoseStack, pBufferSource.getBuffer(RenderType.eyes(GLYPH_TEXTURE)), LightTexture.FULL_BRIGHT, pPackedOverlay, glyphColor);

      VertexConsumer swirl = pBufferSource.getBuffer(RenderType.entityTranslucent(SWIRL_TEXTURE));
      pPoseStack.pushPose();
      pPoseStack.mulPose(Axis.ZP.rotation(time * 0.05F));
      quad(pPoseStack.last(), swirl, SWIRL_RADIUS * swirlScale, -0.004F, FastColor.ARGB32.color(swirlAlpha, 255, 255, 255), pPackedLight, pPackedOverlay);
      pPoseStack.popPose();

      pPoseStack.pushPose();
      pPoseStack.mulPose(Axis.ZP.rotation(-time * 0.03F));
      quad(pPoseStack.last(), swirl, SWIRL_RADIUS * 0.9F * swirlScale, 0.004F, FastColor.ARGB32.color((int) (swirlAlpha * 0.78F), 255, 255, 255), pPackedLight, pPackedOverlay);
      pPoseStack.popPose();

      int glow = (int) (200 * pulse);
      quad(pPoseStack.last(), pBufferSource.getBuffer(RenderType.eyes(GLOW_TEXTURE)), GLOW_RADIUS, 0.03F, FastColor.ARGB32.color(255, glow, glow, glow),
          LightTexture.FULL_BRIGHT, pPackedOverlay);
    }

    pPoseStack.popPose();
  }

  private static int destroyProgress(BlockPos master, BlockState state) {

    Long2ObjectMap<SortedSet<BlockDestructionProgress>> destruction = ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).valcraft_portals$getDestructionProgress();

    int progress = -1;
    Direction facing = state.getValue(PortalBlock.FACING);
    for (PortalPart part : PortalPart.values()) {
      SortedSet<BlockDestructionProgress> set = destruction.get(PortalBlock.partPos(master, facing, part).asLong());
      if (set != null && !set.isEmpty()) {
        progress = Math.max(progress, set.last().getProgress());
      }
    }

    return Math.min(progress, ModelBakery.DESTROY_TYPES.size() - 1);
  }

  private static void quad(PoseStack.Pose pose, VertexConsumer consumer, float half, float z, int color, int light, int overlay) {

    vertex(pose, consumer, -half, -half, z, 0.0F, 0.0F, color, light, overlay);
    vertex(pose, consumer, -half, half, z, 0.0F, 1.0F, color, light, overlay);
    vertex(pose, consumer, half, half, z, 1.0F, 1.0F, color, light, overlay);
    vertex(pose, consumer, half, -half, z, 1.0F, 0.0F, color, light, overlay);
  }

  private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, int color, int light, int overlay) {

    consumer.vertex(pose.pose(), x, y, z).color(color).uv(u, v).overlayCoords(overlay).uv2(light).normal(pose.normal(), 0.0F, 0.0F, -1.0F).endVertex();
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
