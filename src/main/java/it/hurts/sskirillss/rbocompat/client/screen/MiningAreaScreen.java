package it.hurts.sskirillss.rbocompat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.screen.particle.FloralParticleData;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.*;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.common.item.BotaniaItems;

import java.awt.*;

public class MiningAreaScreen extends Screen {
    public MiningAreaScreen() {
        super(Component.literal("MiningAreaScreen"));
    }

    @Override
    public void tick() {
        super.tick();
        int x = getTextureCenter()[0];
        int y = getTextureCenter()[1];

        RandomSource random = Minecraft.getInstance().player.getRandom();
        ParticleStorage.addParticle(this, new FloralParticleData(new Color(82, 42, 114),
                x + 19, y + 30, 0.25F + (random.nextFloat() * 0.25F), 10 + random.nextInt(10)));
    }

    @Override
    protected void init() {
        int centerX = getTextureCenter()[0];
        int centerY = getTextureCenter()[1];

        int button1Width = 20;
        int button1Height = 21;

        int[] buttonPos1 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 46);
        this.addRenderableWidget(new LeftSwitchBaseWidget(buttonPos1[0], buttonPos1[1], button1Width, button1Height));

        int[] buttonPos2 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 70);
        this.addRenderableWidget(new LeftSwitchBaseWidget(buttonPos2[0], buttonPos2[1], button1Width, button1Height));

        int[] buttonPos3 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 93);
        this.addRenderableWidget(new LeftSwitchBaseWidget(buttonPos3[0], buttonPos3[1], button1Width, button1Height));

        int[] buttonPos1Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 46);
        this.addRenderableWidget(new RightSwitchBaseWidget(buttonPos1Left[0], buttonPos1Left[1], button1Width, button1Height));

        int[] buttonPos2Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 70);
        this.addRenderableWidget(new RightSwitchBaseWidget(buttonPos2Left[0], buttonPos2Left[1], button1Width, button1Height));

        int[] buttonPos3Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 93);
        this.addRenderableWidget(new RightSwitchBaseWidget(buttonPos3Left[0], buttonPos3Left[1], button1Width, button1Height));

        int buttonCentralWidth = 66;
        int buttonCentralHeight = 23;

        int[] buttonPosCentral1 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 46);
        this.addRenderableWidget(new CentralPanelWidget(buttonPosCentral1[0], buttonPosCentral1[1], buttonCentralWidth, buttonCentralHeight));

        int[] buttonPosCentral2 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 70);
        this.addRenderableWidget(new CentralPanelWidget(buttonPosCentral2[0], buttonPosCentral2[1], buttonCentralWidth, buttonCentralHeight));

        int[] buttonPosCentral3 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 93);
        this.addRenderableWidget(new CentralPanelWidget(buttonPosCentral3[0], buttonPosCentral3[1], buttonCentralWidth, buttonCentralHeight));

        int button2Width = 35;
        int button2Height = 34;
        int[] button2Pos = calculateButtonPosition(centerX, centerY, button2Width, button2Height, 212, 126);

        this.addRenderableWidget(new ConfirmSelectionModeWidget(button2Pos[0], button2Pos[1], button2Width, button2Height));

        int[] button1Pos = calculateButtonPosition(centerX, centerY, button2Width, button2Height, 172, 126);
        this.addRenderableWidget(new CancelSelectionModeWidget(button1Pos[0], button1Pos[1], button2Width, button2Height));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/mining_area_main_screen.png");
        TextureManager manager = Minecraft.getInstance().getTextureManager();

        int centerX = getTextureCenter()[0];
        int centerY = getTextureCenter()[1];

        int scale = 2;

        int textureWidth = 700;
        int textureHeight = 350;

        manager.bindForSetup(texture);
        pGuiGraphics.blit(texture, centerX, centerY, textureWidth / scale, textureHeight / scale, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();
        poseStack.translate(centerX + 80, centerY + 85, 0);
        poseStack.translate(0, Math.sin((mc.level.getGameTime() + pPartialTick) / 20.0) * 2.0f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(mc.level.getGameTime() % 360 + pPartialTick));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-135));
        poseStack.scale(74, 74, 74);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        itemRenderer.renderStatic(new ItemStack(BotaniaItems.terraPick), ItemDisplayContext.GUI, 240, 0, poseStack, bufferSource, mc.level, 0);

        bufferSource.endBatch();
        poseStack.popPose();

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int[] getTextureCenter() {
        int scale = 2;
        int textureWidth = 700;
        int textureHeight = 350;

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int x = (screenWidth - textureWidth / scale) / 2;
        int y = (screenHeight - textureHeight / scale) / 2 - 20;

        return new int[]{x, y};
    }

    private int[] calculateButtonPosition(int centerX, int centerY, int buttonWidth, int buttonHeight, int offsetX, int offsetY) {
        int x = centerX - buttonWidth / 2 + offsetX;
        int y = centerY - buttonHeight / 2 + offsetY;
        return new int[]{x, y};
    }
}
