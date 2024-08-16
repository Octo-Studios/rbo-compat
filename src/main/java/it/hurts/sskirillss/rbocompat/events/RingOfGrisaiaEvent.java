package it.hurts.sskirillss.rbocompat.events;

import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.items.relics.InfinityHamItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static net.minecraft.world.item.alchemy.PotionUtils.TAG_POTION;

@Mod.EventBusSubscriber
public class RingOfGrisaiaEvent {
    @SubscribeEvent
    public static void onSlotClick(ContainerSlotClickEvent event) {
        if (event.getAction() != ClickAction.PRIMARY)
            return;

        Player player = event.getEntity();

        ItemStack heldStack = event.getHeldStack();
        ItemStack slotStack = event.getSlotStack();

        if (!(heldStack.getItem() instanceof PotionItem) || !(slotStack.getItem() instanceof InfinityHamItem relic)
                || !relic.canUseAbility(slotStack, "infusion"))
            return;

        CompoundTag tag = slotStack.getOrCreateTag();
        ListTag list = tag.getList(TAG_POTION, 9);

        List<MobEffectInstance> effects = PotionUtils.getMobEffects(heldStack);

        if (effects.isEmpty()) {
            NBTUtils.clearTag(slotStack, TAG_POTION);
        } else {
            effects = effects.stream().filter(effect -> effect != null && !effect.getEffect().isInstantenous()).toList();

            if (effects.isEmpty())
                return;

            for (MobEffectInstance effect : effects)
                list.add(effect.save(new CompoundTag()));

            tag.put(TAG_POTION, list);
        }

        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);

        if (player.containerMenu.getCarried().getCount() <= 1)
            player.containerMenu.setCarried(bottle);
        else {
            player.containerMenu.getCarried().shrink(1);

            EntityUtils.addItem(player, bottle);
        }

        player.playSound(SoundEvents.BOTTLE_FILL, 1F, 1F);

        event.setCanceled(true);
    }

}
