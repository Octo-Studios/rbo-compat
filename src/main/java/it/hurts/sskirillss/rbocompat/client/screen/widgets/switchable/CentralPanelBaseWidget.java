package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CentralPanelBaseWidget extends AbstractButton {
    public final Minecraft MC = Minecraft.getInstance();

    public CentralPanelBaseWidget(int pX, int pY, int pWidth, int pHeight, Component component) {
        super(pX, pY, pWidth, pHeight, component);

        this.active = false;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();

        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/button/central_panel.png");

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);
        pGuiGraphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);

        poseStack.popPose();

        poseStack.pushPose();

        int textX = this.getX() + (this.width - MC.font.width(this.getMessage())) / 2;
        int textY = this.getY() + (this.height - MC.font.lineHeight) / 2;

        if (this.getMessage().contains(Component.nullToEmpty("x")))
            pGuiGraphics.drawString(MC.font, Component.literal(String.valueOf(InventoryUtil.getItemStackTerraPix().getTag().get("GetXPos"))), textX + 2 - (MC.font.width(String.valueOf(InventoryUtil.getItemStackTerraPix().getTag().get("GetXPos"))) / 2), textY, 0x556B2F);

        else if (this.getMessage().contains(Component.nullToEmpty("y")))
            pGuiGraphics.drawString(MC.font, Component.literal(String.valueOf(InventoryUtil.getItemStackTerraPix().getTag().get("GetYPos"))), textX + 2 - (MC.font.width(String.valueOf(InventoryUtil.getItemStackTerraPix().getTag().get("GetYPos"))) / 2), textY, 0x556B2F);

        else if (this.getMessage().contains(Component.nullToEmpty("z")))
            pGuiGraphics.drawString(MC.font, Component.literal(String.valueOf(InventoryUtil.getItemStackTerraPix().getTag().get("GetZPos"))), textX + 2 - (MC.font.width(String.valueOf(InventoryUtil.getItemStackTerraPix().getTag().get("GetZPos"))) / 2), textY, 0x556B2F);

        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onPress() {

    }
}
