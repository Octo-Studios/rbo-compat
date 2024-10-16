package it.hurts.sskirillss.rbocompat.mixin.items;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

import java.util.function.Predicate;

import static vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem.isEnabled;

@Mixin(TerraShattererItem.class)
public abstract class TerraShattererItemMixin {

    @Unique
    private static boolean recCall = false;

    @Inject(method = "inventoryTick", at = @At("HEAD"), remap = false)
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (itemStack.getTag() != null && !itemStack.getTag().contains("GetXPos") && !itemStack.getTag().contains("GetYPos")
                && !itemStack.getTag().contains("GetZPos") && !itemStack.getTag().contains("selectMode")) {

            NBTUtils.setInt(itemStack, "GetXPos", 1);
            NBTUtils.setInt(itemStack, "GetYPos", 1);
            NBTUtils.setInt(itemStack, "GetZPos", 1);

        }
    }

    @Inject(method = "breakOtherBlock", at = @At("HEAD"), cancellable = true, remap = false)
    public void breakOtherBlock(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side, CallbackInfo ci) {
        ItemStack itemStack = EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing);

        if (!(itemStack.getItem() instanceof IRelicItem) || itemStack.getItem() != BotaniaItems.thorRing || !player.getMainHandItem().is(BotaniaItems.terraPick))
            return;

        breakArea(player, stack, pos, side);

        if (player.isSecondaryUseActive())
            BotaniaAPI.instance().breakOnAllCursors(player, stack, pos, side);

        ci.cancel();
    }

    private static void breakArea(Player player, ItemStack stack, BlockPos pos, Direction side) {
        if (!isEnabled(stack))
            return;

        Level world = player.level();
        Predicate<BlockState> canMine = (state) -> {
            boolean rightToolForDrops = !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
            boolean rightToolForSpeed = stack.getDestroySpeed(state) > 1.0F || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_HOE);
            return rightToolForDrops && rightToolForSpeed;
        };

        if (canMine.test(world.getBlockState(pos))) {
            if (world.isEmptyBlock(pos))
                return;

            int rangeYHeight = player.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("GetYPos");

            int rangeX = player.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("GetXPos") / 2;
            int rangeY = rangeYHeight - 1;
            int rangeZ = player.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("GetZPos") - 1;

            boolean doHeight = rangeY == 0;

            Vec3i beginDiff, endDiff;

            switch (side) {
                case NORTH:
                    beginDiff = new Vec3i(-rangeX, doHeight ? 0 : -1, 0);
                    endDiff = new Vec3i(rangeX, doHeight ? rangeY : rangeY - 1, rangeZ);
                    break;
                case SOUTH:
                    beginDiff = new Vec3i(-rangeX, doHeight ? 0 : -1, 0);
                    endDiff = new Vec3i(rangeX, doHeight ? rangeY : rangeY - 1, -rangeZ);
                    break;
                case WEST:
                    beginDiff = new Vec3i(0, doHeight ? 0 : -1, -rangeX);
                    endDiff = new Vec3i(rangeZ, doHeight ? rangeY : rangeY - 1, rangeX);
                    break;
                case EAST:
                    beginDiff = new Vec3i(0, doHeight ? 0 : -1, -rangeX);
                    endDiff = new Vec3i(-rangeZ, doHeight ? rangeY : rangeY - 1, rangeX);
                    break;
                default:
                    beginDiff = new Vec3i(-rangeX, -rangeY, -rangeZ / 2);
                    endDiff = new Vec3i(rangeX, side == Direction.UP ? rangeYHeight : rangeY, rangeZ / 2);
            }

            removeBlocksInIteration(player, stack, world, pos, beginDiff, endDiff, canMine);
        }

    }

    private static void removeBlocksInIteration(Player player, ItemStack stack, Level world, BlockPos centerPos,
                                                Vec3i startDelta, Vec3i endDelta, Predicate<BlockState> filter) {
        if (recCall)
            return;

        recCall = true;
        try {
            for (BlockPos iterPos : BlockPos.betweenClosed(centerPos.offset(startDelta),
                    centerPos.offset(endDelta))) {
                if (iterPos.equals(centerPos)) {
                    continue;
                }
                removeBlockWithDrops(player, stack, world, iterPos, filter);
            }
        } finally {
            recCall = false;
        }
    }

    private static void removeBlockWithDrops(Player player, ItemStack stack, Level world, BlockPos pos, Predicate<BlockState> filter) {
        if (!world.hasChunkAt(pos))
            return;

        BlockState blockstate = world.getBlockState(pos);

        if (!world.isClientSide && blockstate.getDestroyProgress(player, world, pos) != 0 && filter.test(blockstate) && !blockstate.isAir()) {
            ItemStack save = player.getMainHandItem();

            player.setItemInHand(InteractionHand.MAIN_HAND, stack);

            ((ServerPlayer) player).gameMode.destroyBlock(pos);
            player.setItemInHand(InteractionHand.MAIN_HAND, save);
        }
    }
}
