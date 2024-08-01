package it.hurts.sskirillss.rbocompat.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.model.PixieModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PixieRender<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation location = new ResourceLocation(RBOCompat.MODID, "textures/entity/pixie.png");
    PixieModel model;

    public PixieRender(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);

        model = new PixieModel(vazkii.botania.client.model.PixieModel.createMesh().getRoot().bake(32, 32));
    }

    @Override
    public void render(T p_114485_, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(location));
        poseStack.pushPose();

        poseStack.scale(1, -1, 1);
        poseStack.translate(0, -1.35, 0);

        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(p_114485_, p_114486_, p_114487_, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return location;
    }

}
