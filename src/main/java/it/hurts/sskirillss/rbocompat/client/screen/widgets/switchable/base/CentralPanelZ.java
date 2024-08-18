package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.base;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CentralPanelZ extends CentralPanelBaseWidget {
    public CentralPanelZ(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int textX = this.getX() + (this.width - MC.font.width(this.getMessage())) / 2;
        int textY = this.getY() + (this.height - MC.font.lineHeight) / 2;

        pGuiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(InventoryUtil.getItemStackTerraPix().getTag().get("GetZPos"))), textX - 2, textY, 0x556B2F);
    }

}
