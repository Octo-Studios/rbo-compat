package it.hurts.sskirillss.rbocompat.events;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.block.Bound;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.advancements.LokiPlaceTrigger;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


@Mod.EventBusSubscriber
public class RingOfThorItemEvent {
    private static int consecutiveBlocksMined = 0;
    private static long lastBlockMinedTime = 0;

    private static final int MAX_NUM_CURSORS = 1023;
    private static final String TAG_CURSOR_LIST = "cursorList";
    private static final String TAG_CURSOR_PREFIX = "cursor";
    private static final String TAG_CURSOR_COUNT = "cursorCount";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Y_OFFSET = "yOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    private static final String TAG_X_ORIGIN = "xOrigin";
    private static final String TAG_Y_ORIGIN = "yOrigin";
    private static final String TAG_Z_ORIGIN = "zOrigin";

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

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getEntity();

        ItemStack stackRelic = EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing);

        if (!(stackRelic.getItem() instanceof IRelicItem) || stackRelic.getItem() != BotaniaItems.thorRing || !player.isShiftKeyDown()
                || !stackRelic.getTag().getBoolean("selectMode")) {
            return;
        }

        var hand = event.getHand();
        var world = player.level();
        var lookPos = event.getHitVec();

        ItemStack stack = player.getItemInHand(hand);
        List<BlockPos> cursors = getCursorList(stackRelic);

        if (lookPos.getType() != HitResult.Type.BLOCK)
            return;

        BlockPos hit = lookPos.getBlockPos();
        if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            BlockPos originCoords = getBindingCenter(stackRelic);
            if (!world.isClientSide) {
                if (originCoords.getY() == Integer.MIN_VALUE) {
                    setBindingCenter(stackRelic, hit);
                    setCursorList(stackRelic, null);
                } else {
                    if (originCoords.equals(hit)) {
                        exitBindingMode(stackRelic);
                    } else {
                        BlockPos relPos = hit.subtract(originCoords);

                        boolean removed = cursors.remove(relPos);
                        if (!removed) {
                            if (cursors.size() < MAX_NUM_CURSORS) {
                                cursors.add(relPos);
                            } else {
                                player.displayClientMessage(Component.translatable("botaniamisc.lokiRingLimitReached"), true);
                            }
                        }
                        setCursorList(stackRelic, cursors);
                    }
                }
            }

        } else {
            int numCursors = cursors.size();

            int cost = numCursors > 10 ? numCursors : Math.min(numCursors, (int) Math.pow(Math.E, numCursors * 0.25));
            ItemStack original = stack.copy();
            int successes = 0;
            for (BlockPos cursor : cursors) {
                BlockPos pos = hit.offset(cursor);
                if (ManaItemHandler.instance().requestManaExact(stackRelic, player, cost, false)) {
                    UseOnContext ctx = getUseOnContext(player, hand, pos, lookPos.getLocation(), lookPos.getDirection());

                    InteractionResult result;
                    if (player.isCreative()) {
                        result = PlayerHelper.substituteUse(ctx, original.copy());
                    } else {
                        result = stack.useOn(ctx);
                    }

                    if (result.consumesAction()) {
                        ManaItemHandler.instance().requestManaExact(stackRelic, player, cost, true);
                        successes++;
                    }
                } else {
                    break;
                }
            }
            if (successes > 0 && player instanceof ServerPlayer serverPlayer) {
                LokiPlaceTrigger.INSTANCE.trigger(serverPlayer, stackRelic, successes);
            }
        }
    }

    public static @NotNull UseOnContext getUseOnContext(Player player, InteractionHand hand, BlockPos pos, Vec3 lookHit, Direction direction) {
        Vec3 newHitVec = new Vec3((double) pos.getX() + Mth.frac(lookHit.x()), (double) pos.getY() + Mth.frac(lookHit.y()), (double) pos.getZ() + Mth.frac(lookHit.z()));
        BlockHitResult newHit = new BlockHitResult(newHitVec, direction, pos, false);
        return new UseOnContext(player, hand, newHit);
    }

    private static BlockPos getBindingCenter(ItemStack stack) {
        int x = ItemNBTHelper.getInt(stack, TAG_X_ORIGIN, 0);
        int y = ItemNBTHelper.getInt(stack, TAG_Y_ORIGIN, Integer.MIN_VALUE);
        int z = ItemNBTHelper.getInt(stack, TAG_Z_ORIGIN, 0);
        return new BlockPos(x, y, z);
    }

    private static void exitBindingMode(ItemStack stack) {
        setBindingCenter(stack, Bound.UNBOUND_POS);
    }

    private static void setCursorList(ItemStack stack, @Nullable List<BlockPos> cursors) {
        CompoundTag cmp = new CompoundTag();
        if (cursors != null) {
            int i = 0;
            for (BlockPos cursor : cursors) {
                CompoundTag cursorCmp = cursorToCmp(cursor);
                cmp.put(TAG_CURSOR_PREFIX + i, cursorCmp);
                i++;
            }
            cmp.putInt(TAG_CURSOR_COUNT, i);
        }

        ItemNBTHelper.setCompound(stack, TAG_CURSOR_LIST, cmp);
    }

    private static CompoundTag cursorToCmp(BlockPos pos) {
        CompoundTag cmp = new CompoundTag();
        cmp.putInt(TAG_X_OFFSET, pos.getX());
        cmp.putInt(TAG_Y_OFFSET, pos.getY());
        cmp.putInt(TAG_Z_OFFSET, pos.getZ());
        return cmp;
    }

    public static List<BlockPos> getCursorList(ItemStack stack) {
        if (!stack.getTag().getBoolean("selectMode"))
            return new ArrayList<>();

        CompoundTag cmp = ItemNBTHelper.getCompound(stack, TAG_CURSOR_LIST, false);

        List<BlockPos> cursors = new ArrayList<>();

        int count = cmp.getInt(TAG_CURSOR_COUNT);
        for (int i = 0; i < count; i++) {
            CompoundTag cursorCmp = cmp.getCompound(TAG_CURSOR_PREFIX + i);
            int x = cursorCmp.getInt(TAG_X_OFFSET);
            int y = cursorCmp.getInt(TAG_Y_OFFSET);
            int z = cursorCmp.getInt(TAG_Z_OFFSET);

            cursors.add(new BlockPos(x, y, z));
        }

        return cursors;
    }

    private static void setBindingCenter(ItemStack stack, BlockPos pos) {
        ItemNBTHelper.setInt(stack, TAG_X_ORIGIN, pos.getX());
        ItemNBTHelper.setInt(stack, TAG_Y_ORIGIN, pos.getY());
        ItemNBTHelper.setInt(stack, TAG_Z_ORIGIN, pos.getZ());
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
