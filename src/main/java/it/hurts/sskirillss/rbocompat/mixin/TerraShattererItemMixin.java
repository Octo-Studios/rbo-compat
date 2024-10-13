package it.hurts.sskirillss.rbocompat.mixin;

import it.hurts.sskirillss.rbocompat.items.TerraShattererItemImplementation;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

@Mixin(TerraShattererItem.class)
public abstract class TerraShattererItemMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true, remap = false)
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (itemStack.getTag() != null && !itemStack.getTag().contains("GetXPos") && !itemStack.getTag().contains("GetYPos")
                && !itemStack.getTag().contains("GetZPos") && !itemStack.getTag().contains("selectMode")) {

            NBTUtils.setInt(itemStack, "GetXPos", 1);
            NBTUtils.setInt(itemStack, "GetYPos", 1);
            NBTUtils.setInt(itemStack, "GetZPos", 1);

            NBTUtils.setInt(itemStack, "RingVolumeBonus", 1);
        }
    }

    /**
     * @author Amiri163
     * @reason Without this it won't work
     */
    @Overwrite(remap = false)
    public void breakOtherBlock(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
        TerraShattererItemImplementation.breakOtherBlock(player, stack, pos, originPos, side);
    }

}
