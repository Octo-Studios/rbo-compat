package it.hurts.sskirillss.rbocompat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.ConfirmSelectionModeWidget;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.SwitchBaseWidget;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.Button;

public class MiningAreaScreen extends Screen {
    public MiningAreaScreen() {
        super(Component.literal("MiningAreaScreen"));
    }

    @Override
    protected void init() {
        int buttonWidth = 20;
        int buttonHeight = 21;
        int x = this.width / 2;
        int y = this.height / 2 + 100;

        this.addRenderableWidget(new SwitchBaseWidget(x, y, buttonWidth, buttonHeight));
        this.addRenderableWidget(new ConfirmSelectionModeWidget(x + 20, y, buttonWidth, buttonHeight));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/mining_area_main_screen.png");
        TextureManager manager = Minecraft.getInstance().getTextureManager();

        int scale = 2;

        int textureWidth = 700;
        int textureHeight = 350;

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int x = (screenWidth - textureWidth / scale) / 2;
        int y = (screenHeight - textureHeight / scale) / 2 - 20;

        manager.bindForSetup(texture);
        pGuiGraphics.blit(texture, x, y, textureWidth / scale, textureHeight / scale, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
