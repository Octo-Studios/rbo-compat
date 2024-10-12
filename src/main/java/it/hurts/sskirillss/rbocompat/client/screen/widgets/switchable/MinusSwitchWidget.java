package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.IScrollingScreen;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.BaseAreaWidget;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import vazkii.botania.common.item.BotaniaItems;

public class MinusSwitchWidget extends BaseAreaWidget {

    public MinusSwitchWidget(int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/button/left_button.png");

        this.active = !EntityUtils.findEquippedCurio(Minecraft.getInstance().player, BotaniaItems.thorRing).getTag().getBoolean("selectMode");

        float alpha = 1F;

        int x = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetXPos");
        int y = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetYPos");
        int z = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetZPos");

        switch (message()) {
            case "x":
                if (x <= 1) {
                    active = false;
                    alpha = 0.7F;
                }

                break;
            case "y":
                if (y <= 1) {
                    active = false;
                    alpha = 0.7F;
                }

                break;
            case "z":
                if (z <= 1) {
                    active = false;
                    alpha = 0.7F;
                }

                break;
            default:
                active = true;
                break;
        }

        if (!this.active)
            alpha = 0.7F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);
        pGuiGraphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    public void onPress() {
        setRemoveVolume();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (scrollAmount <= 0) {
            setRemoveVolume();
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    public void setRemoveVolume() {
        switch (message()) {
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

