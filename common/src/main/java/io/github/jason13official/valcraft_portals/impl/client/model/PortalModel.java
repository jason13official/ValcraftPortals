package io.github.jason13official.valcraft_portals.impl.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class PortalModel extends Model {

  public static final ModelLayerLocation WOOD_LAYER = new ModelLayerLocation(ValcraftPortals.id("portal"), "main");
  public static final ModelLayerLocation STONE_LAYER = new ModelLayerLocation(ValcraftPortals.id("stone_portal"), "main");

  private static final int TEX_WIDTH = 128;
  private static final int TEX_HEIGHT = 64;
  private static final float GLYPH_OFFSET = 0.05F;

  private final ModelPart frame;
  private final ModelPart glyphs;

  public PortalModel(ModelPart root) {
    super(RenderType::entityCutoutNoCull);
    this.frame = root.getChild("frame");
    this.glyphs = root.getChild("glyphs");
  }

  public static LayerDefinition createWoodLayer() {

    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();
    PartDefinition frame = root.addOrReplaceChild("frame", CubeListBuilder.create(), PartPose.ZERO);
    PartDefinition glyphs = root.addOrReplaceChild("glyphs", CubeListBuilder.create(), PartPose.ZERO);

    float radius = 18.5F;
    float depth = 5.0F;
    int index = 0;
    int rune = 0;

    for (int degrees = -150; degrees <= 150; degrees += 20) {
      if (degrees > -10 && degrees < 30) {
        continue;
      }

      float angle = degrees * Mth.DEG_TO_RAD;
      float thickness = index % 2 == 0 ? 5.0F : 6.0F;
      float r = radius + (index % 3 - 1) * 0.5F;
      float x = r * Mth.sin(angle);
      float y = -r * Mth.cos(angle);

      frame.addOrReplaceChild("ring_" + index, CubeListBuilder.create().texOffs(uvU(index, 36), uvV(index, 12))
          .addBox(-3.75F, -thickness / 2.0F, -depth / 2.0F, 7.5F, thickness, depth), PartPose.offsetAndRotation(x, y, 0.0F, 0.0F, 0.0F, angle));

      if (Math.abs(degrees) >= 50 && Math.abs(degrees) <= 130) {
        addGlyph(glyphs, "glyph_" + index, rune++, x, y, angle, depth, 0.0F);
      }

      index++;
    }

    frame.addOrReplaceChild("log", CubeListBuilder.create().texOffs(0, 40).addBox(-17.0F, -2.5F, -3.0F, 34.0F, 5.0F, 6.0F),
        PartPose.offsetAndRotation(0.0F, 19.5F, 0.0F, 0.0F, 0.0F, 2.0F * Mth.DEG_TO_RAD));

    float[][] branches = {
        {-120, 35, 5, 3},
        {-75, 15, 4, 3},
        {-35, -10, 5, 3},
        {-14, 15, 4, 2},
        {40, 20, 5, 3},
        {95, -15, 5, 3},
        {125, -30, 4, 3}
    };

    for (int i = 0; i < branches.length; i++) {
      float angle = branches[i][0] * Mth.DEG_TO_RAD;
      float tilt = branches[i][1] * Mth.DEG_TO_RAD;
      float length = branches[i][2];
      float width = branches[i][3];
      float pivot = radius + 1.5F;

      PartDefinition branch = frame.addOrReplaceChild("branch_" + i, CubeListBuilder.create().texOffs(uvU(i + 20, 12), uvV(i + 20, 12))
              .addBox(-width / 2.0F, -length, -1.5F, width, length, 3.0F),
          PartPose.offsetAndRotation(pivot * Mth.sin(angle), -pivot * Mth.cos(angle), 0.0F, 0.0F, 0.0F, angle + tilt));

      float tipLength = length * 0.7F;
      branch.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(uvU(i + 40, 8), uvV(i + 40, 10))
              .addBox(-(width - 1.0F) / 2.0F, -tipLength, -1.0F, width - 1.0F, tipLength, 2.0F),
          PartPose.offsetAndRotation(0.0F, -length + 0.5F, 0.0F, 0.0F, 0.0F, Math.signum(tilt) * 0.25F));
    }

    return LayerDefinition.create(mesh, TEX_WIDTH, TEX_HEIGHT);
  }

  public static LayerDefinition createStoneLayer() {

    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();
    PartDefinition frame = root.addOrReplaceChild("frame", CubeListBuilder.create(), PartPose.ZERO);
    PartDefinition glyphs = root.addOrReplaceChild("glyphs", CubeListBuilder.create(), PartPose.ZERO);

    frame.addOrReplaceChild("base", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-18.0F, -1.5F, -6.0F, 36.0F, 3.0F, 12.0F)
            .texOffs(0, 24).addBox(-15.0F, -1.5F, -8.0F, 30.0F, 3.0F, 16.0F),
        PartPose.offset(0.0F, 20.5F, 0.0F));

    for (int side = -1; side <= 1; side += 2) {
      frame.addOrReplaceChild("footing_low_" + (side + 1), CubeListBuilder.create().texOffs(40, 44).addBox(-4.5F, -2.0F, -4.5F, 9.0F, 4.0F, 9.0F),
          PartPose.offsetAndRotation(side * 12.5F, 17.0F, 0.0F, 0.0F, 0.0F, side * 0.08F));
      frame.addOrReplaceChild("footing_high_" + (side + 1), CubeListBuilder.create().texOffs(80, 46).addBox(-3.5F, -1.5F, -4.0F, 7.0F, 3.0F, 8.0F),
          PartPose.offsetAndRotation(side * 12.0F, 13.5F, 0.0F, 0.0F, 0.0F, -side * 0.12F));
    }

    int index = 0;
    for (int degrees = -140; degrees <= 140; degrees += 20) {
      float angle = degrees * Mth.DEG_TO_RAD;
      float r = 15.5F;
      frame.addOrReplaceChild("arch_" + index, CubeListBuilder.create().texOffs(uvU(index, 30), uvV(index, 12))
          .addBox(-3.0F, -1.5F, -3.0F, 6.0F, 3.0F, 6.0F), PartPose.offsetAndRotation(r * Mth.sin(angle), -r * Mth.cos(angle), 0.0F, 0.0F, 0.0F, angle));
      index++;
    }

    int rune = 0;
    index = 0;
    for (int degrees = -140; degrees <= 140; degrees += 14) {
      float angle = degrees * Mth.DEG_TO_RAD;
      float width = 5.0F + Math.round(noise(index, 1));
      float depth = 7.0F + Math.round(noise(index, 2) * 2.0F);
      float inner = 2.0F;
      float outer = 3.0F + 3.0F * Math.max(0.0F, Mth.cos(angle)) + Math.round(noise(index, 3) * 2.0F);
      float tilt = (noise(index, 4) - 0.5F) * 0.3F;
      float pivot = 16.5F;
      float x = pivot * Mth.sin(angle);
      float y = -pivot * Mth.cos(angle);

      frame.addOrReplaceChild("stone_" + index, CubeListBuilder.create().texOffs(uvU(index, 32), uvV(index, 27))
              .addBox(-width / 2.0F, -outer, -depth / 2.0F, width, outer + inner, depth),
          PartPose.offsetAndRotation(x, y, 0.0F, 0.0F, 0.0F, angle + tilt));

      if (index % 2 == 1 && outer >= 5.0F) {
        addGlyph(glyphs, "glyph_" + index, rune++, x, y, angle + tilt, depth, (inner - outer) * 0.5F);
      }

      index++;
    }

    return LayerDefinition.create(mesh, TEX_WIDTH, TEX_HEIGHT);
  }

  private static void addGlyph(PartDefinition glyphs, String name, int rune, float x, float y, float angle, float depth, float radialOffset) {

    CubeListBuilder cubes = CubeListBuilder.create().texOffs((rune % 8) * 8, 0)
        .addBox(-2.0F, radialOffset - 2.5F, -depth / 2.0F - GLYPH_OFFSET, 4.0F, 5.0F, 0.0F)
        .addBox(-2.0F, radialOffset - 2.5F, depth / 2.0F + GLYPH_OFFSET, 4.0F, 5.0F, 0.0F);

    glyphs.addOrReplaceChild(name, cubes, PartPose.offsetAndRotation(x, y, 0.0F, 0.0F, 0.0F, angle));
  }

  private static int uvU(int index, int width) {
    return (index * 23) % (TEX_WIDTH - width);
  }

  private static int uvV(int index, int height) {
    return (index * 11) % (TEX_HEIGHT - height);
  }

  private static float noise(int index, int channel) {
    float value = Mth.sin(index * 12.9898F + channel * 78.233F) * 43758.547F;
    return value - Mth.floor(value);
  }

  public void renderFrame(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
    this.frame.render(poseStack, buffer, packedLight, packedOverlay, color);
  }

  public void renderGlyphs(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
    this.glyphs.render(poseStack, buffer, packedLight, packedOverlay, color);
  }

  @Override
  public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
    renderFrame(poseStack, buffer, packedLight, packedOverlay, color);
    renderGlyphs(poseStack, buffer, packedLight, packedOverlay, color);
  }
}
