package it.hurts.sskirillss.rbocompat.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryUtil {

    public static ItemStack getItemStackTerraPix() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return ItemStack.EMPTY;

        return player.getMainHandItem();
    }
}
