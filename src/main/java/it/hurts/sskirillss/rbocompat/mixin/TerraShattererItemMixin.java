package it.hurts.sskirillss.rbocompat.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

@Mixin(TerraShattererItem.class)
public class TerraShattererItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = false)
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);

        cir.setReturnValue(InteractionResultHolder.pass(stack));
    }

}
