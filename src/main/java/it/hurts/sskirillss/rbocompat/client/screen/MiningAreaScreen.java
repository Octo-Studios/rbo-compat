package it.hurts.sskirillss.rbocompat.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.IScrollingScreen;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.CancelSelectionModeWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.ConfirmSelectionModeWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.CentralPanelBaseWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.MinusSwitchWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.PlusSwitchWidget;
import it.hurts.sskirillss.rbocompat.items.TerraShattererItemImplementation;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import it.hurts.sskirillss.relics.client.screen.description.data.ExperienceParticleData;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

        RandomSource random = minecraft.player.getRandom();
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
        this.addRenderableWidget(new MinusSwitchWidget(buttonPos1[0], buttonPos1[1], button1Width, button1Height,
                Component.literal("x"))).setTooltip(Tooltip.create(Component.translatable("screen.terra.area.minus.x")));

        int[] buttonPos2 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 70);
        this.addRenderableWidget(new MinusSwitchWidget(buttonPos2[0], buttonPos2[1], button1Width, button1Height,
                Component.literal("y"))).setTooltip(Tooltip.create(Component.translatable("screen.terra.area.minus.y")));

        int[] buttonPos3 = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 148, 93);
        this.addRenderableWidget(new MinusSwitchWidget(buttonPos3[0], buttonPos3[1], button1Width, button1Height,
                Component.literal("z"))).setTooltip(Tooltip.create(Component.translatable("screen.terra.area.minus.z")));

        int[] buttonPos1Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 46);
        this.addRenderableWidget(new PlusSwitchWidget(buttonPos1Left[0], buttonPos1Left[1], button1Width, button1Height,
                Component.literal("x"))).setTooltip(Tooltip.create(Component.translatable("screen.terra.area.plus.x")));

        int[] buttonPos2Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 70);
        this.addRenderableWidget(new PlusSwitchWidget(buttonPos2Left[0], buttonPos2Left[1], button1Width, button1Height,
                Component.literal("y"))).setTooltip(Tooltip.create(Component.translatable("screen.terra.area.plus.y")));

        int[] buttonPos3Left = calculateButtonPosition(centerX, centerY, button1Width, button1Height, 238, 93);
        this.addRenderableWidget(new PlusSwitchWidget(buttonPos3Left[0], buttonPos3Left[1], button1Width, button1Height,
                Component.literal("z"))).setTooltip(Tooltip.create(Component.translatable("screen.terra.area.plus.z")));

        int buttonCentralWidth = 66;
        int buttonCentralHeight = 21;

        int[] buttonPosCentral1 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 46);
        this.addRenderableWidget(new CentralPanelBaseWidget(buttonPosCentral1[0], buttonPosCentral1[1], buttonCentralWidth, buttonCentralHeight,
                Component.literal("x"))).setTooltip(Tooltip.create(Component.literal("")));

        int[] buttonPosCentral2 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 70);
        this.addRenderableWidget(new CentralPanelBaseWidget(buttonPosCentral2[0], buttonPosCentral2[1], buttonCentralWidth, buttonCentralHeight,
                Component.literal("y"))).setTooltip(Tooltip.create(Component.literal("")));

        int[] buttonPosCentral3 = calculateButtonPosition(centerX, centerY, buttonCentralWidth, buttonCentralHeight, 193, 93);
        this.addRenderableWidget(new CentralPanelBaseWidget(buttonPosCentral3[0], buttonPosCentral3[1], buttonCentralWidth, buttonCentralHeight,
                Component.literal("z"))).setTooltip(Tooltip.create(Component.literal("")));

        int button2Width = 35;
        int button2Height = 34;

        int[] button2Pos = calculateButtonPosition(centerX, centerY, button2Width, button2Height, 212, 126);

        this.addRenderableWidget(new ConfirmSelectionModeWidget(button2Pos[0], button2Pos[1], button2Width, button2Height))
                .setTooltip(Tooltip.create(Component.translatable("screen.terra.area.confirm")));

        int[] button1Pos = calculateButtonPosition(centerX, centerY, button2Width, button2Height, 172, 126);
        this.addRenderableWidget(new CancelSelectionModeWidget(button1Pos[0], button1Pos[1], button2Width, button2Height))
                .setTooltip(Tooltip.create(Component.translatable("screen.terra.area.cancel")));
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics) {
        super.renderBackground(pGuiGraphics);
        Player player = minecraft.player;

        ItemStack stack = EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing);

        if (!(stack.getItem() instanceof IRelicItem) || stack.getItem() != BotaniaItems.thorRing)
            return;

        PoseStack poseStack = pGuiGraphics.pose();

        float pPartialTick = Minecraft.getInstance().getFrameTime();
        float scale = 4.5F;

        poseStack.pushPose();

        poseStack.translate(getTextureCenter()[0] + 80, getTextureCenter()[1] + 87, 100);
        poseStack.translate(0, Math.sin((minecraft.level.getGameTime() + pPartialTick) / 20.0) * 2.0f, 0);

        poseStack.mulPose(Axis.YP.rotationDegrees((player.tickCount + pPartialTick) * 1.5F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45));

        poseStack.translate(-8 * scale, -8 * scale, -150 * scale);

        poseStack.scale(scale, scale, scale);

        pGuiGraphics.renderItem(player.getItemInHand(InteractionHand.MAIN_HAND), 0, 0);

        poseStack.popPose();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);

        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/mining_area_main_screen.png");

        TextureManager manager = minecraft.getTextureManager();

        PoseStack poseStack = pGuiGraphics.pose();

        poseStack.pushPose();

        int centerX = getTextureCenter()[0];
        int centerY = getTextureCenter()[1];

        int scale = 2;

        int textureWidth = 700;
        int textureHeight = 350;

        manager.bindForSetup(texture);
        pGuiGraphics.blit(texture, centerX, centerY, textureWidth / scale, textureHeight / scale, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        poseStack.popPose();

        poseStack.pushPose();

        float scaleText = 1.3F;
        int centerTextX = (int) ((centerX + 293) / scaleText);

        poseStack.scale(scaleText, scaleText, scaleText);

        String valueFlow = String.valueOf(TerraShattererItemImplementation.actualValue());
        pGuiGraphics.drawString(minecraft.font, valueFlow, centerTextX - (minecraft.font.width(valueFlow) / 2) + 8, (int) ((centerY + 52) / scaleText), 0x8ACE5A);

        String valueLimit = String.valueOf(TerraShattererItemImplementation.valueBockLimit());
        pGuiGraphics.drawString(minecraft.font, valueLimit, centerTextX - (minecraft.font.width(valueLimit) / 2) + 8, (int) ((centerY + 76) / scaleText), 0x8ACE5A);

        poseStack.popPose();

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
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
        int textureWidth = 350;
        int textureHeight = 175;


        int x = (this.width - textureWidth) / 2;
        int y = (this.height - textureHeight) / 2;

        return new int[]{x, y};
    }

    private int[] calculateButtonPosition(int centerX, int centerY, int buttonWidth, int buttonHeight, int offsetX, int offsetY) {
        int x = centerX - buttonWidth / 2 + offsetX;
        int y = centerY - buttonHeight / 2 + offsetY;
        return new int[]{x, y};
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class MiningScreenEvent {
        public static boolean canBeScroll;

        @SubscribeEvent
        public static void onScrollEvent(ScreenEvent.MouseScrolled.Pre event) {
            Player player = Minecraft.getInstance().player;

            ItemStack stack = EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing);

            if (!(stack.getItem() instanceof IRelicItem relic) || stack.getItem() != BotaniaItems.thorRing)
                return;

            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();

            for (Renderable widget : event.getScreen().renderables) {
                if (!(widget instanceof IScrollingScreen button)) return;

                int xButton = button.x();
                int yButton = button.y();

                if (mouseX >= xButton && mouseX <= xButton + button.width() && mouseY >= yButton && mouseY <= yButton + button.height()) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                    if (event.getScrollDelta() == 1) {
                        switch (button.message()) {
                            case "x":
                                NetworkHandler.sendToServer(new UpdateItemStackPacket(2, 0, 0));
                                break;
                            case "y":
                                NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 1, 0));
                                break;
                            case "z":
                                NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 0, 1));
                                break;
                        }
                    } else
                        switch (button.message()) {
                            case "x":
                                NetworkHandler.sendToServer(new UpdateItemStackPacket(-2, 0, 0));
                                break;
                            case "y":
                                NetworkHandler.sendToServer(new UpdateItemStackPacket(0, -1, 0));
                                break;
                            case "z":
                                NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 0, -1));
                                break;
                        }
                }
            }
        }
    }
}
