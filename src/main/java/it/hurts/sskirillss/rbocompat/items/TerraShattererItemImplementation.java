package it.hurts.sskirillss.rbocompat.items;

import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

import java.util.function.Predicate;

import static vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem.isEnabled;

public class TerraShattererItemImplementation {

    public static void breakOtherBlock(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
        if (!isEnabled(stack)) return;

        Level world = player.level();
        Predicate<BlockState> canMine = (state) -> {
            boolean rightToolForDrops = !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
            boolean rightToolForSpeed = stack.getDestroySpeed(state) > 1.0F || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_HOE);
            return rightToolForDrops && rightToolForSpeed;
        };
        BlockState targetState = world.getBlockState(pos);
        if (canMine.test(targetState)) {
            if (world.isEmptyBlock(pos)) return;

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

            ToolCommons.removeBlocksInIteration(player, stack, world, pos, beginDiff, endDiff, canMine);
        }

    }

    public static int sumTotalBlocks() {
        int picLevel = TerraShattererItem.getLevel(InventoryUtil.getItemStackTerraPix());
        return (picLevel * (220 - (10 - picLevel) * 22));
    }

    public static int volumeCalculation() {
        int x = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetXPos");
        int y = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetYPos");
        int z = InventoryUtil.getItemStackTerraPix().getTag().getInt("GetZPos");

        return x * y * z;
    }

}
