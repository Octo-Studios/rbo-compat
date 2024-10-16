package it.hurts.sskirillss.rbocompat.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.common.item.BotaniaItems;

public class ClientInventoryUtil {

    public static ItemStack getItemStackTerraPix() {
        Player player = Minecraft.getInstance().player;

        if (player == null || player.getMainHandItem().getItem() != BotaniaItems.terraPick)
            return ItemStack.EMPTY;

        return player.getMainHandItem();
    }
}
