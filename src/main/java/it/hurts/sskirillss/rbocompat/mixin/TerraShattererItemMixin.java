package it.hurts.sskirillss.rbocompat.mixin;

import it.hurts.sskirillss.rbocompat.CreativeTabEvent;
import it.hurts.sskirillss.rbocompat.utils.InventoryUtil;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.StoneOfTemperanceItem;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.common.item.relic.RingOfThorItem;
import vazkii.botania.common.lib.ResourceLocationHelper;

import java.util.function.Predicate;

import static vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem.getLevel;
import static vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem.isEnabled;

@Mixin(TerraShattererItem.class)
public abstract class TerraShattererItemMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true, remap = false)
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (itemStack.getTag() != null && !itemStack.getTag().contains("GetXPos") && !itemStack.getTag().contains("GetYPos")
                && !itemStack.getTag().contains("GetZPos") && !itemStack.getTag().contains("selectMode")) {

            NBTUtils.setInt(itemStack, "GetXPos", 1);
            NBTUtils.setInt(itemStack, "GetYPos", 1);
            NBTUtils.setInt(itemStack, "GetZPos", 1);
        }
    }

    /**
     * @author Amiri163
     * @reason Without this it won't work
     */
    @Overwrite(remap = false)
    public void breakOtherBlock(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
        CreativeTabEvent.breakOtherBlock(player, stack, pos, originPos, side);
    }
}
