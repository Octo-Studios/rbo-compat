package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.client.screen.MiningAreaScreen;
import it.hurts.sskirillss.rbocompat.client.screen.widgets.BaseAreaWidget;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.utils.ClientInventoryUtil;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.common.item.BotaniaItems;

public class PlusSwitchWidget extends BaseAreaWidget {

    public PlusSwitchWidget(int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/button/right_button.png");

        this.active = !EntityUtils.findEquippedCurio(Minecraft.getInstance().player, BotaniaItems.thorRing).getTag().getBoolean("selectMode");

        float alpha = 1F;

        int x = ClientInventoryUtil.getItemStackTerraPix().getTag().getInt("GetXPos");
        int y = ClientInventoryUtil.getItemStackTerraPix().getTag().getInt("GetYPos");
        int z = ClientInventoryUtil.getItemStackTerraPix().getTag().getInt("GetZPos");

        if (!this.active)
            alpha = 0.7F;

        switch (message()) {
            case "x":
                if ((x + 2) * y * z > MiningAreaScreen.valueBockLimit()) {
                    alpha = 0.7F;
                    active = false;
                }

                break;
            case "y":
                if (x * (y + 1) * z > MiningAreaScreen.valueBockLimit()) {
                    alpha = 0.7F;
                    active = false;
                }

                break;
            case "z":
                if (x * y * (z + 1) > MiningAreaScreen.valueBockLimit()) {
                    alpha = 0.7F;
                    active = false;
                }

                break;
            default:
                alpha = 1F;
                active = true;
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
    public void onPress() {
        setAddVolume();
    }

    @OnlyIn(Dist.CLIENT)
    public void setAddVolume() {
        switch (this.message()) {
            case "x":
                NetworkHandler.sendToServer(new UpdateItemStackPacket(2, 0, 0));
                break;
            case "y":
                NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 1, 0));
                break;
            case "z":
                NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 0, 1));
                break;
        }
    }
}
