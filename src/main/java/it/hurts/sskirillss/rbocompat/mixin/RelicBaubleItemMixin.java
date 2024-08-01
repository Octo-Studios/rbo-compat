package it.hurts.sskirillss.rbocompat.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.item.relic.RelicBaubleItem;

@Mixin(RelicBaubleItem.class)
public class RelicBaubleItemMixin extends Item {
    public RelicBaubleItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void onInventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean held, CallbackInfo ci) {
        super.inventoryTick(stack, world, entity, slot, held);
    }
}