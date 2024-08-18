package it.hurts.sskirillss.rbocompat.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.rbocompat.entity.BaseBabylonianWeaponEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.client.core.handler.MiscellaneousModels;

import java.util.Random;

public class BaseBabylonianWeaponRender extends EntityRenderer<BaseBabylonianWeaponEntity> {
    public BaseBabylonianWeaponRender(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BaseBabylonianWeaponEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource buffers, int pPackedLight) {
        poseStack.pushPose();

        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.translate(-0.5, 0, -0.5);

        BakedModel model = MiscellaneousModels.INSTANCE.kingKeyWeaponModels[5];

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(Sheets.translucentItemSheet()), null, model, 1.0F, 1.0F, 1.0F, 15728880, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(BaseBabylonianWeaponEntity pEntity) {
        return null;
    }
}
