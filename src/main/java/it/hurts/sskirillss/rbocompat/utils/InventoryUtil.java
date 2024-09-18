package it.hurts.sskirillss.rbocompat.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

public class InventoryUtil {

    public static ItemStack getItemStackTerraPix() {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getMainHandItem().getItem() != BotaniaItems.terraPick) return ItemStack.EMPTY;

        return player.getMainHandItem();
    }
}
