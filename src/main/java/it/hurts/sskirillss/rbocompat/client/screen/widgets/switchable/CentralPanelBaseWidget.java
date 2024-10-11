package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.IScrollingScreen;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.BaseAreaWidget;
import it.hurts.sskirillss.rbocompat.items.TerraShattererItemImplementation;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class CentralPanelBaseWidget extends BaseAreaWidget {
    public final Minecraft MC = Minecraft.getInstance();

    public CentralPanelBaseWidget(int pX, int pY, int pWidth, int pHeight, Component component) {
        super(pX, pY, pWidth, pHeight, component);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        setRelocateValue(scrollAmount);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    public void setRelocateValue(double scrollAmount) {
        int x = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetXPos");
        int y = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetYPos");
        int z = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetZPos");

        String message = this.getMessage().toString().replace("literal", "");

        if (scrollAmount == 1) {
            switch (message) {
                case "{x}":
                    if ((x + 2) * y * z < TerraShattererItemImplementation.valueBockLimit()) {
                        NetworkHandler.sendToServer(new UpdateItemStackPacket(2, 0, 0));
                    }
                    break;
                case "{y}":
                    if (x * (y + 1) * z < TerraShattererItemImplementation.valueBockLimit()) {
                        NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 1, 0));
                    }
                    break;
                case "{z}":
                    if (x * y * (z + 1) < TerraShattererItemImplementation.valueBockLimit()) {
                        NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 0, 1));
                    }
                    break;
            }
        } else
            switch (message) {
                case "{x}":
                    NetworkHandler.sendToServer(new UpdateItemStackPacket(-2, 0, 0));

                    break;
                case "{y}":
                    NetworkHandler.sendToServer(new UpdateItemStackPacket(0, -1, 0));

                    break;
                case "{z}":
                    NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 0, -1));

                    break;
            }
    }
}
