package it.hurts.sskirillss.rbocompat.events;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.common.item.BotaniaItems;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class RingOfThorItemEvent {
    private static int consecutiveBlocksMined = 0;
    private static long lastBlockMinedTime = 0;


    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        ItemStack stack = EntityUtils.findEquippedCurio(event.getPlayer(), BotaniaItems.thorRing);

        if (!(stack.getItem() instanceof IRelicItem) || stack.getItem() != BotaniaItems.thorRing)
            return;

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBlockMinedTime <= getPickaxeEfficiency(event.getPlayer())) consecutiveBlocksMined++;
        else consecutiveBlocksMined = 0;

        lastBlockMinedTime = currentTime;
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), BotaniaItems.thorRing);

        if (!(stack.getItem() instanceof IRelicItem) || stack.getItem() != BotaniaItems.thorRing)
            return;

        event.setNewSpeed(event.getOriginalSpeed() + (consecutiveBlocksMined * 0.3f));
    }

    public static double getPickaxeEfficiency(Player player) {
        ItemStack heldItem = player.getMainHandItem();

        double calculate = heldItem.getItem().getDestroySpeed(new ItemStack(heldItem.getItem()), player.getBlockStateOn()) + (consecutiveBlocksMined * 0.3f);
        double baseValue = 1600;
        double decrementPerUnit = 200;

        if (calculate <= 2) return baseValue;

        return 550 + ((baseValue + calculate + decrementPerUnit) / calculate);
    }

}
