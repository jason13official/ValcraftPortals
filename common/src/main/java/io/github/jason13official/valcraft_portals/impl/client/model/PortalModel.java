package io.github.jason13official.valcraft_portals.impl.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class PortalModel extends Model {

  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ValcraftPortals.id("portal"), "main");

  private final ModelPart root;
  private final ModelPart arch;
  private final ModelPart right;
  private final ModelPart left;
  private final ModelPart top;
  private final ModelPart center;

  public PortalModel(ModelPart root) {
    super(RenderType::entityCutoutNoCull);
    this.root = root;
    this.arch = this.root.getChild("arch");
    this.right = this.arch.getChild("right");
    this.left = this.arch.getChild("left");
    this.top = this.arch.getChild("top");
    this.center = this.arch.getChild("center");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition arch = partdefinition.addOrReplaceChild("arch", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition right = arch.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 50).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 50)
            .addBox(-2.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 50).addBox(-3.0F, -22.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(23.0F, -24.0F, 0.0F));

    PartDefinition left = arch.addOrReplaceChild("left", CubeListBuilder.create().texOffs(8, 50).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(24, 50)
            .addBox(0.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(40, 50).addBox(1.0F, -22.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(-23.0F, -24.0F, 0.0F));

    PartDefinition top = arch.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 46).addBox(-21.0F, -1.0F, -1.0F, 42.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, -47.0F, 0.0F));

    PartDefinition center = arch.addOrReplaceChild("center", CubeListBuilder.create().texOffs(0, 0).addBox(-22.0F, -46.0F, -0.5F, 44.0F, 46.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 0.0F, 0.0F));

    return LayerDefinition.create(meshdefinition, 128, 128);
  }

//  public void setupAnim(float pTime, float pRightPageFlipAmount, float pLeftPageFlipAmount, float pBookOpenAmount) {
//
//  }

  public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
    this.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);
  }

  public void render(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
    this.root.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);
  }
}