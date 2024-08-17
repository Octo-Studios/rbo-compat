package it.hurts.sskirillss.rbocompat.client.screen.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.relics.client.screen.description.data.base.ParticleData;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FloralParticleData extends ParticleData {

    private static final ResourceLocation TEXTURE = new ResourceLocation("relics", "textures/gui/description/general/pixel_particle.png");

    public FloralParticleData(Color color, float xStart, float yStart, float scale, int lifeTime) {
        super(TEXTURE, color, xStart, yStart, scale, lifeTime);

        RandomSource random = RandomSource.create();

        // Задаем случайное смещение от центра в пределах от -5 до 5 пикселей
        float randomXOffset = (random.nextFloat() * 10) - 3;
        float randomYOffset = (random.nextFloat() * 10) - 3;

        // Устанавливаем начальные координаты с учетом случайного смещения
        this.setX(xStart + randomXOffset);
        this.setY(yStart + randomYOffset);
    }

    @Override
    public void tick(Screen screen) {
        LocalPlayer player = screen.getMinecraft().player;
        if (player != null) {
            RandomSource random = player.getRandom();

            float xOffset = (random.nextFloat() - 0.5F) * 0.2F;
            float yOffset = (random.nextFloat() - 0.5F) * 0.2F;

            this.setX(this.getX() + xOffset);
            this.setY(this.getY() + yOffset);
        }
    }

    @Override
    public void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft MC = screen.getMinecraft();

        float lifePercentage = 1F - ((getMaxLifeTime() - getLifeTime()) / 100F);

        RenderSystem.setShaderColor(getColor().getRed() / 255F, getColor().getGreen() / 255F, getColor().getBlue() / 255F, 1F * lifePercentage);
        RenderSystem.setShaderTexture(0, getTexture());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        MC.getTextureManager().getTexture(getTexture()).setBlurMipmap(true, false);

        RenderUtils.renderTextureFromCenter(guiGraphics.pose(), getX(), getY(), 8, 8, getScale() * lifePercentage);

        MC.getTextureManager().getTexture(getTexture()).restoreLastBlurMipmap();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }
}