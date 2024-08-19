package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.base;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import vazkii.botania.common.item.BotaniaItems;

public class LeftSwitchBaseWidget extends AbstractButton {

    public LeftSwitchBaseWidget(int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/button/left_button.png");
        float alpha = 1.0F;

        if (EntityUtils.findEquippedCurio(Minecraft.getInstance().player, BotaniaItems.lokiRing).getTag().getBoolean("selectMode")) {
            this.active = false;
            alpha = 0.7F;
        } else {
            this.active = true;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);
        pGuiGraphics.blit(texture, getX(), getY(), 0, 0, width, height, width, height);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onPress() {
        if (this.getMessage().contains(Component.nullToEmpty("x"))) {
            NetworkHandler.sendToServer(new UpdateItemStackPacket(-1, 0, 0));
        }
        if (this.getMessage().contains(Component.nullToEmpty("y"))) {
            NetworkHandler.sendToServer(new UpdateItemStackPacket(0, -1, 0));
        }
        if (this.getMessage().contains(Component.nullToEmpty("z"))) {
            NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 0, -1));
        }

    }
}

