package it.hurts.sskirillss.rbocompat.client.screen.widgets;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateModeShatterPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfirmSelectionModeWidget extends AbstractButton {

    public ConfirmSelectionModeWidget(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/button/confirm_selection_mode.png");

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);
        pGuiGraphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onPress() {
        NetworkHandler.sendToServer(new UpdateModeShatterPacket(true));
    }
}
