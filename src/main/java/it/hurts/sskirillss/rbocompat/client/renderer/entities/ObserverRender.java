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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

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

        poseStack.translate(0, 2, 0);
        Vec3 angle = entity.getLookAngle();
        poseStack.translate(angle.x, angle.y, angle.z);

        double angleY = Math.toDegrees(Math.atan2(angle.x, angle.z));
        double angleZ = Math.toDegrees(Math.atan2(Math.sqrt(angle.x * angle.x + angle.z * angle.z), angle.y));

        poseStack.mulPose(Axis.YP.rotationDegrees((float) angleY));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) angleZ + 90F));

        model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(location)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return location;
    }
}
