package it.hurts.sskirillss.rbocompat.mixin.init;

import it.hurts.sskirillss.rbocompat.items.RingOfLokiItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.ResourceLocationHelper;

@Mixin(BotaniaItems.class)
public class BotaniaItemsMixin {

//    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/item/BotaniaItems;make(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/Item;"))
//    private static Item onInventoryTick(ResourceLocation id, Item item) {
//        return switch (id.getPath()) {
//            case "loki_ring" -> make(id, new RingOfLokiItem());
//            default -> make(id, item);
//        };
//    }

    @Shadow
    private static <T extends Item> T make(ResourceLocation id, T item) {
        return null;
    }
}