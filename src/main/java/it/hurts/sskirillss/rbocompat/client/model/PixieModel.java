package it.hurts.sskirillss.rbocompat.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.rbocompat.entity.PixieEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import vazkii.botania.client.core.helper.RenderHelper;

public class PixieModel extends EntityModel<vazkii.botania.common.entity.PixieEntity> {
    private final ModelPart body;
    private final ModelPart leftWingT;
    private final ModelPart leftWingB;
    private final ModelPart rightWingT;
    private final ModelPart rightWingB;
    private static boolean evil = false;

    private static RenderType pixieLayer(ResourceLocation texture) {
        return evil ? RenderHelper.getDopplegangerLayer(texture) : RenderType.entityCutoutNoCull(texture);
    }

    public PixieModel(ModelPart root) {
        super(PixieModel::pixieLayer);
        this.body = root.getChild("body");
        this.leftWingT = root.getChild("leftWingT");
        this.leftWingB = root.getChild("leftWingB");
        this.rightWingT = root.getChild("rightWingT");
        this.rightWingB = root.getChild("rightWingB");
    }

    public static MeshDefinition createMesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().addBox(-2.5F, 0.0F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.offset(0.0F, 16.0F, 0.0F));
        root.addOrReplaceChild("leftWingT", CubeListBuilder.create().texOffs(0, 4).addBox(0.0F, -5.0F, 0.0F, 0.0F, 5.0F, 6.0F), PartPose.offsetAndRotation(2.5F, 18.0F, 0.5F, 0.2618F, 0.5236F, 0.2618F));
        root.addOrReplaceChild("leftWingB", CubeListBuilder.create().texOffs(0, 11).addBox(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 4.0F), PartPose.offsetAndRotation(2.5F, 18.0F, 0.5F, -0.2618F, 0.2618F, -0.2618F));
        root.addOrReplaceChild("rightWingT", CubeListBuilder.create().texOffs(0, 4).addBox(0.0F, -5.0F, 0.0F, 0.0F, 5.0F, 6.0F), PartPose.offsetAndRotation(-2.5F, 18.0F, 0.5F, 0.2618F, -0.5236F, -0.2618F));
        root.addOrReplaceChild("rightWingB", CubeListBuilder.create().texOffs(0, 11).addBox(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 4.0F), PartPose.offsetAndRotation(-2.5F, 18.0F, 0.5F, -0.2618F, -0.2618F, 0.2618F));
        return mesh;
    }

    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.body.render(ms, buffer, light, overlay);
        this.leftWingT.render(ms, buffer, light, overlay);
        this.leftWingB.render(ms, buffer, light, overlay);
        this.rightWingT.render(ms, buffer, light, overlay);
        this.rightWingB.render(ms, buffer, light, overlay);
    }

    public void setupAnim(vazkii.botania.common.entity.PixieEntity entity, float f, float f1, float f2, float f3, float f4) {
        evil = entity.getPixieType() == 1;
        this.rightWingT.yRot = -(Mth.cos(f2 * 1.7F) * 3.1415927F * 0.5F);
        this.leftWingT.yRot = Mth.cos(f2 * 1.7F) * 3.1415927F * 0.5F;
        this.rightWingB.yRot = -(Mth.cos(f2 * 1.7F) * 3.1415927F * 0.25F);
        this.leftWingB.yRot = Mth.cos(f2 * 1.7F) * 3.1415927F * 0.25F;
    }
}