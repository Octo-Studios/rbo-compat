package it.hurts.sskirillss.rbocompat.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.CancelSelectionModeWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.ConfirmSelectionModeWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.base.CentralPanelBaseWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.base.LeftSwitchBaseWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.base.RightSwitchBaseWidget;
import it.hurts.sskirillss.rbocompat.items.TerraShattererItemImplementation;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

import java.awt.*;

public class MiningAreaScreen extends Screen {
    private final Minecraft MC = Minecraft.getInstance();

    public MiningAreaScreen() {
        super(Component.literal("MiningAreaScreen"));
    }

    @Override
    public void tick() {
        super.tick();
        int x = getTextureCenter()[0];
        int y = getTextureCenter()[1];

        RandomSource random = MC.player.getRandom();
        int xOff = random.nextInt(25);

        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(0x9B1D8F),
                x + 8 + xOff, y + 25, 1F + (random.nextFloat() * 0.25F), 40 + random.nextInt(20)));

        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(0x9B1D8F),
                x + 20, y + 40, 1F + (random.nextFloat() * 0.25F), 40 + random.nextInt(20)));

        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(0xD5354A),
                x + 18 + random.nextInt(15), y + 158, 1F + (random.nextFloat() * 0.25F), 40 + random.nextInt(20)));

        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(0xD5354A),
                x + 37 + random.nextInt(15), y + 165, 1F + (random.nextFloat() * 0.25F), 40 + random.nextInt(20)));

        ParticleStorage.addParticle(this, new ExperienceParticleData(new Color(0x264E4D),
                x + 318 + xOff, y + 158, 1F + (random.nextFloat() * 0.25F), 40 + random.nextInt(20)));
    }

    @Override
    protected void init() {
        int centerX = getTextureCenter()[0];
        int centerY = getTextureCenter()[1];

        int button1Width = 20;
        int button1Height = 21;

        int[] buttonPos1 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 46);
        this.addRenderableWidget(new LeftSwitchBaseWidget(buttonPos1[0], buttonPos1[1], button1Width, button1Height, Component.literal("x")));

        int[] buttonPos2 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 70);
        this.addRenderableWidget(new LeftSwitchBaseWidget(buttonPos2[0], buttonPos2[1], button1Width, button1Height, Component.literal("y")));

        int[] buttonPos3 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 93);
        this.addRenderableWidget(new LeftSwitchBaseWidget(buttonPos3[0], buttonPos3[1], button1Width, button1Height, Component.literal("z")));

        int[] buttonPos1Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 46);
        this.addRenderableWidget(new RightSwitchBaseWidget(buttonPos1Left[0], buttonPos1Left[1], button1Width, button1Height, Component.literal("x")));

        int[] buttonPos2Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 70);
        this.addRenderableWidget(new RightSwitchBaseWidget(buttonPos2Left[0], buttonPos2Left[1], button1Width, button1Height, Component.literal("y")));

        int[] buttonPos3Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 93);
        this.addRenderableWidget(new RightSwitchBaseWidget(buttonPos3Left[0], buttonPos3Left[1], button1Width, button1Height, Component.literal("z")));

        int buttonCentralWidth = 66;
        int buttonCentralHeight = 21;

        int[] buttonPosCentral1 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 46);
        this.addRenderableWidget(new CentralPanelBaseWidget(buttonPosCentral1[0], buttonPosCentral1[1], buttonCentralWidth, buttonCentralHeight, Component.literal("x")));

        int[] buttonPosCentral2 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 70);
        this.addRenderableWidget(new CentralPanelBaseWidget(buttonPosCentral2[0], buttonPosCentral2[1], buttonCentralWidth, buttonCentralHeight, Component.literal("y")));

        int[] buttonPosCentral3 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 93);
        this.addRenderableWidget(new CentralPanelBaseWidget(buttonPosCentral3[0], buttonPosCentral3[1], buttonCentralWidth, buttonCentralHeight, Component.literal("z")));

        int button2Width = 35;
        int button2Height = 34;

        int[] button2Pos = calculateButtonPosition(centerX, centerY, button2Width, button2Height, 212, 126);

        this.addRenderableWidget(new ConfirmSelectionModeWidget(button2Pos[0], button2Pos[1], button2Width, button2Height));

        int[] button1Pos = calculateButtonPosition(centerX, centerY, button2Width, button2Height, 172, 126);
        this.addRenderableWidget(new CancelSelectionModeWidget(button1Pos[0], button1Pos[1], button2Width, button2Height));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);

        Player player = MC.player;

        ItemStack stack = EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing);

        if (!(stack.getItem() instanceof IRelicItem relic) || stack.getItem() != BotaniaItems.thorRing)
            return;

        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/mining_area_main_screen.png");

        TextureManager manager = MC.getTextureManager();

        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();

        int centerX = getTextureCenter()[0];
        int centerY = getTextureCenter()[1];

        int scale = 2;

        int textureWidth = 700;
        int textureHeight = 350;

        manager.bindForSetup(texture);
        pGuiGraphics.blit(texture, centerX, centerY, textureWidth / scale, textureHeight / scale, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        ItemRenderer itemRenderer = MC.getItemRenderer();

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(centerX + 83, centerY + 85, 100);
        poseStack.translate(0, Math.sin((MC.level.getGameTime() + pPartialTick) / 20.0) * 2.0f, 0);

        poseStack.mulPose(Axis.YP.rotationDegrees(MC.level.getGameTime() % 360 + pPartialTick));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-135));

        poseStack.scale(74, 74, 74);
        Lighting.setupForFlatItems();

        MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();

        itemRenderer.renderStatic(new ItemStack(BotaniaItems.terraPick), ItemDisplayContext.GUI, 15728880, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, MC.level, 0);
        Lighting.setupFor3DItems();

        poseStack.popPose();

        poseStack.pushPose();

        pGuiGraphics.drawString(MC.font, String.valueOf(TerraShattererItemImplementation.volumeCalculation()), centerX + 303 - (MC.font.width(String.valueOf(TerraShattererItemImplementation.volumeCalculation())) / 2), centerY + 53, 0xFFFFFF);

        String value = String.valueOf((int) Math.floor(relic.getAbilityValue(stack, "entropy", "capacity")) + TerraShattererItem.getLevel(InventoryUtil.getItemStackTerraPix()) * 50);
        pGuiGraphics.drawString(MC.font, value, centerX + 303 - (MC.font.width(value) / 2), centerY + 77, 0xFFFFFF);

        poseStack.popPose();

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (MC.options.keyInventory.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            this.onClose();

            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int[] getTextureCenter() {
        int scale = 2;
        int textureWidth = 700;
        int textureHeight = 350;

        int screenWidth = MC.getWindow().getGuiScaledWidth();
        int screenHeight = MC.getWindow().getGuiScaledHeight();

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
