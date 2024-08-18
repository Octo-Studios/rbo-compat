package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.base;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CentralPanelBaseWidget extends AbstractButton {
    private final Minecraft MC = Minecraft.getInstance();

    @Getter
    @Setter
    private String coordinate;

    public CentralPanelBaseWidget(int pX, int pY, int pWidth, int pHeight, Component component) {
        super(pX, pY, pWidth, pHeight, component);

        this.active = false;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/button/central_panel.png");

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);
        pGuiGraphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);

        int textX = this.getX() + (this.width - MC.font.width(this.getMessage())) / 2;
        int textY = this.getY() + (this.height - MC.font.lineHeight) / 2;

        pGuiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), textX, textY, 0x556B2F);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onPress() {

    }
}
