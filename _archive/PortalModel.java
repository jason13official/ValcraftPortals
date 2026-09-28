public class PortalModel<T extends Entity> extends EntityModel<T> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "portal"), "main");

	private final ModelPart arch;
	private final ModelPart right;
	private final ModelPart left;
	private final ModelPart top;
	private final ModelPart center;

	public PortalModel(ModelPart root) {
		this.arch = root.getChild("arch");
		this.right = this.arch.getChild("right");
		this.left = this.arch.getChild("left");
		this.top = this.arch.getChild("top");
		this.center = this.arch.getChild("center");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition arch = partdefinition.addOrReplaceChild("arch", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right = arch.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 50).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(16, 50).addBox(-2.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 50).addBox(-3.0F, -22.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(23.0F, -24.0F, 0.0F));

		PartDefinition left = arch.addOrReplaceChild("left", CubeListBuilder.create().texOffs(8, 50).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 50).addBox(0.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 50).addBox(1.0F, -22.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-23.0F, -24.0F, 0.0F));

		PartDefinition top = arch.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 46).addBox(-21.0F, -1.0F, -1.0F, 42.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -47.0F, 0.0F));

		PartDefinition center = arch.addOrReplaceChild("center", CubeListBuilder.create().texOffs(0, 0).addBox(-22.0F, -46.0F, -0.5F, 44.0F, 46.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		arch.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}