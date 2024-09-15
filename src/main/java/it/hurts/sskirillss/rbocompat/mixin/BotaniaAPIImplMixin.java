package it.hurts.sskirillss.rbocompat.mixin;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.events.RingOfThorItemEvent;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import vazkii.botania.api.item.SequentialBreaker;
import vazkii.botania.common.impl.BotaniaAPIImpl;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.List;

@Mixin(BotaniaAPIImpl.class)
public class BotaniaAPIImplMixin {
    private static boolean recCall = false;

    /**
     * @author Amiri163
     * @reason it doesn't work any other way lol
     */
    @Overwrite(remap = false)
    public void breakOnAllCursors(Player player, ItemStack stack, BlockPos pos, Direction side) {
        ItemStack stackRelic = EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing);

        if (!(stackRelic.getItem() instanceof IRelicItem) || stackRelic.getItem() != BotaniaItems.thorRing || !(stack.getItem() instanceof SequentialBreaker breaker))
            return;

        if (recCall)
            return;

        recCall = true;
        try {
            for (BlockPos offset : RingOfThorItemEvent.getCursorList(stackRelic)) {
                BlockPos coords = pos.offset(offset);
                BlockState state = player.level().getBlockState(coords);
                breaker.breakOtherBlock(player, stack, coords, pos, side);
                ToolCommons.removeBlockWithDrops(player, stack, player.level(), coords,
                        s -> s.is(state.getBlock()));
            }
        } finally {
            recCall = false;
        }
    }
}
