package it.hurts.sskirillss.rbocompat.events;

import com.mojang.datafixers.types.templates.Tag;
import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.common.item.BotaniaItems;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class RingOfThorItemEvent {
    private static final TagKey<Block> ORES_TAG = BlockTags.create(new ResourceLocation("forge", "ores"));

    private static int consecutiveBlocksMined = 0;
    private static long lastBlockMinedTime = 0;

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        ServerLevel world = player.serverLevel();
        BlockPos playerPos = player.blockPosition();
        int radius = 5;

        Map<Block, Integer> oreCounts = new HashMap<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = playerPos.getY(); y >= 2; y--) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block.defaultBlockState().is(ORES_TAG)) {
                        oreCounts.put(block, oreCounts.getOrDefault(block, 0) + 1);
                    }
                }
            }
        }

        for (Map.Entry<Block, Integer> entry : oreCounts.entrySet()) {
            player.sendSystemMessage(Component.literal(entry.getKey().getName().getString() + ": " + entry.getValue()));
        }

    }

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
