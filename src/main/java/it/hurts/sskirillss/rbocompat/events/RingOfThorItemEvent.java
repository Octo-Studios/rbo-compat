package it.hurts.sskirillss.rbocompat.events;

import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;

@Mod.EventBusSubscriber
public class RingOfThorItemEvent {

    private static int consecutiveBlocksMined = 0;
    private static long lastBlockMinedTime = 0;

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        long currentTime = System.currentTimeMillis();
        System.out.println(getPickaxeEfficiency(player));

        if (currentTime - lastBlockMinedTime <= getPickaxeEfficiency(player))
            consecutiveBlocksMined++;
        else
            consecutiveBlocksMined = 0;

        lastBlockMinedTime = currentTime;

    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        ItemStack heldItem = player.getMainHandItem();
        float newSpeed = event.getOriginalSpeed() + (consecutiveBlocksMined * 0.1f);
        System.out.println(newSpeed);
        event.setNewSpeed(newSpeed);

    }

    public static double getPickaxeEfficiency(Player player) {
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof TieredItem pickaxe)
            return calculate(pickaxe.getTier().getLevel());

        return 0;
    }

    public static double calculate(double input) {
        double baseValue = 1700;
        double decrementPerUnit = 230;

        if (input <= 2) {
            return baseValue;
        }

        double result = baseValue - (input * decrementPerUnit);
        return Math.max(result, 0);
    }

}
