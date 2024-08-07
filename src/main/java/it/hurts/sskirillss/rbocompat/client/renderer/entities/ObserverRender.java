package it.hurts.sskirillss.rbocompat.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.model.ObserverModel;
import it.hurts.sskirillss.rbocompat.entity.ObserverEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ObserverRender<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation location = new ResourceLocation(RBOCompat.MODID, "textures/entity/observer.png");
    private final ObserverModel<ObserverEntity> model;

    public ObserverRender(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);

        model = new ObserverModel<>(ObserverModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(@NotNull T entity, float p_114486_, float p_114487_, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.scale(1, -1, 1);
        poseStack.translate(0, -1.55, 0);

        Player player = Minecraft.getInstance().player;

        if (player != null) {
            double deltaX = player.getX() - entity.getX();
            double deltaZ = player.getZ() - entity.getZ();
            float yaw = (float) Math.toDegrees(Math.atan2(deltaX, deltaZ)) + 180;

            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        }

        model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(location)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return location;
    }
}
