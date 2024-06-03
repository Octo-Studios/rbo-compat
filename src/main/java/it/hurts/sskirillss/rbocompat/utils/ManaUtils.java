package it.hurts.sskirillss.rbocompat.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.mana.ManaItemHandler;

import java.util.Optional;

public class ManaUtils {

    public static int getTotalMana(Player player) {
        if (player == null) return 0;

        return player.getInventory().items.stream()
                .map(stack -> ManaItemHandler.instance().requestMana(stack, player, Integer.MAX_VALUE, false))
                .filter(mana -> mana > 0)
                .findFirst().orElse(0);
    }

    public static Optional<ItemStack> getFirstManaItem(Player player) {
        if (player == null) return Optional.empty();

        return player.getInventory().items.stream()
                .filter(stack -> ManaItemHandler.instance().requestMana(stack, player, Integer.MAX_VALUE, false) > 0)
                .findFirst();
    }

    public static void consumeMana(Player player, int amount) {
        if (player == null || amount <= 0) return;

        getFirstManaItem(player).ifPresent(stack -> ManaItemHandler.instance().requestMana(stack, player, amount, true));
    }

}
