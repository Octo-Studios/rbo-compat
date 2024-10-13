package it.hurts.sskirillss.rbocompat.mixin.client;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.rbocompat.events.RingOfThorItemEvent;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vazkii.botania.api.item.WireframeCoordinateListProvider;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.relic.RingOfThorItem;

import java.util.List;

@Mixin(RingOfThorItem.class)
public class TerraShattererItemClientMixin implements WireframeCoordinateListProvider {
    @Unique
    private static final String TAG_X_ORIGIN = "xOrigin";

    @Unique
    private static final String TAG_Y_ORIGIN = "yOrigin";

    @Unique
    private static final String TAG_Z_ORIGIN = "zOrigin";

    @Override
    public BlockPos getSourceWireframe(Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        if (EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing) != stack) {
            return null;
        }

        BlockPos currentBuildCenter = getBindingCenter(stack);
        if (currentBuildCenter.getY() != Integer.MIN_VALUE) {
            return currentBuildCenter;
        } else if (mc.hitResult instanceof BlockHitResult hitRes
                && mc.hitResult.getType() == HitResult.Type.BLOCK
                && !RingOfThorItemEvent.getCursorList(stack).isEmpty()) {
            return hitRes.getBlockPos();
        }

        return null;
    }

    @Override
    public List<BlockPos> getWireframesToDraw(Player player, ItemStack stack) {
        if (EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing) != stack)
            return ImmutableList.of();

        HitResult lookPos = Minecraft.getInstance().hitResult;

        if (lookPos != null
                && lookPos.getType() == HitResult.Type.BLOCK
                && !player.level().isEmptyBlock(((BlockHitResult) lookPos).getBlockPos())) {
            List<BlockPos> list = RingOfThorItemEvent.getCursorList(stack);
            BlockPos origin = getBindingCenter(stack);

            for (int i = 0; i < list.size(); i++) {
                if (origin.getY() != Integer.MIN_VALUE) {
                    list.set(i, list.get(i).offset(origin));
                } else {
                    list.set(i, list.get(i).offset(((BlockHitResult) lookPos).getBlockPos()));
                }
            }

            return list;
        }

        return ImmutableList.of();
    }

    private static BlockPos getBindingCenter(ItemStack stack) {
        int x = NBTUtils.getInt(stack, TAG_X_ORIGIN, 0);
        int y = NBTUtils.getInt(stack, TAG_Y_ORIGIN, Integer.MIN_VALUE);
        int z = NBTUtils.getInt(stack, TAG_Z_ORIGIN, 0);

        return new BlockPos(x, y, z);
    }
}
