package it.hurts.sskirillss.rbocompat.client.screen.widgets.switchable.base;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.items.TerraShattererItemImplementation;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.common.item.BotaniaItems;

public class RightSwitchBaseWidget extends AbstractButton {

    public RightSwitchBaseWidget(int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation texture = new ResourceLocation(RBOCompat.MODID, "textures/gui/button/right_button.png");
        int x = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetXPos");
        int y = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetYPos");
        int z = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetZPos");
        float alpha = 1F;

        if (!this.active)
            alpha = 0.7F;

        switch (this.getMessage().toString().replace("literal", "")) {
            case "{x}":
                if ((x + 2) * y * z > TerraShattererItemImplementation.sumTotalBlocks()) {
                    alpha = 0.7F;
                    active = false;
                }

                break;
            case "{y}":
                if (x * (y + 1) * z > TerraShattererItemImplementation.sumTotalBlocks()) {
                    alpha = 0.7F;
                    active = false;
                }

                break;
            case "{z}":
                if (x * y * (z + 1) > TerraShattererItemImplementation.sumTotalBlocks()) {
                    alpha = 0.7F;
                    active = false;
                }

                break;
            default:
                alpha = 1F;
                active = true;
        }

        this.active = !EntityUtils.findEquippedCurio(Minecraft.getInstance().player, BotaniaItems.thorRing).getTag().getBoolean("selectMode");

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
        int x = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetXPos");
        int y = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetYPos");
        int z = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetZPos");

        switch (this.getMessage().toString().replace("literal", "")) {
            case "{x}":
                if ((x + 2) * y * z < TerraShattererItemImplementation.sumTotalBlocks())
                    NetworkHandler.sendToServer(new UpdateItemStackPacket(2, 0, 0));

                break;
            case "{y}":
                if (x * (y + 1) * z < TerraShattererItemImplementation.sumTotalBlocks())
                    NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 1, 0));

                break;
            case "{z}":
                if (x * y * (z + 1) < TerraShattererItemImplementation.sumTotalBlocks())
                    NetworkHandler.sendToServer(new UpdateItemStackPacket(0, 0, 1));

                break;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
