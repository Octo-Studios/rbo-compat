package it.hurts.sskirillss.rbocompat.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MiningAreaScreen extends Screen {
    public MiningAreaScreen() {
        super(Component.literal("MiningAreaScreen"));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/main_screen.png");
        TextureManager manager = Minecraft.getInstance().getTextureManager();

        int scale = 2;

        int textureWidth = 700;
        int textureHeight = 350;

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int x = (screenWidth - textureWidth / scale) / 2;
        int y = (screenHeight - textureHeight / scale) / 2 + 10;

        manager.bindForSetup(texture);
        pGuiGraphics.blit(texture, x, y, textureWidth / scale, textureHeight / scale, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
