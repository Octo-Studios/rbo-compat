package it.hurts.sskirillss.rbocompat.events;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.common.item.BotaniaItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod.EventBusSubscriber
public class RingOfThorItemEvent {
    private static final TagKey<Block> ORES_TAG = BlockTags.create(new ResourceLocation("forge", "ores"));
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static int consecutiveBlocksMined = 0;
    private static long lastBlockMinedTime = 0;

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        ServerLevel world = player.serverLevel();
        BlockPos playerPos = player.blockPosition();
        int radius = 25;
        int minY = world.getMinBuildHeight();

        List<CompletableFuture<Map<Block, Integer>>> futures = new ArrayList<>();

        int chunkSize = 25;
        for (int x = -radius; x <= radius; x += chunkSize) {
            for (int z = -radius; z <= radius; z += chunkSize) {
                int finalX = x;
                int finalZ = z;
                futures.add(CompletableFuture.supplyAsync(() -> scanArea(world, playerPos, finalX, finalZ, chunkSize, minY), executor));
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            Map<Block, Integer> oreCounts = new HashMap<>();
            for (CompletableFuture<Map<Block, Integer>> future : futures) {
                try {
                    Map<Block, Integer> result = future.get();
                    result.forEach((block, count) -> oreCounts.merge(block, count, Integer::sum));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            oreCounts.forEach((block, count) -> player.sendSystemMessage(Component.literal(block.getName().getString() + ": " + count)));
        });
    }

    private static Map<Block, Integer> scanArea(ServerLevel world, BlockPos centerPos, int offsetX, int offsetZ, int chunkSize, int minY) {
        Map<Block, Integer> oreCounts = new HashMap<>();
        for (int x = offsetX; x < offsetX + chunkSize; x++) {
            for (int z = offsetZ; z < offsetZ + chunkSize; z++) {
                for (int y = centerPos.getY(); y >= minY; y--) {
                    BlockPos pos = centerPos.offset(x, y - centerPos.getY(), z);
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block.defaultBlockState().is(ORES_TAG)) {
                        oreCounts.put(block, oreCounts.getOrDefault(block, 0) + 1);
                    }
                }
            }
        }
        return oreCounts;
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
