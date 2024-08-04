package it.hurts.sskirillss.rbocompat.events;

import it.hurts.sskirillss.rbocompat.utils.ManaUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.common.item.BotaniaItems;

@Mod.EventBusSubscriber
public class RingOfOdinItemEvent {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        int mana = 100000;

        if (!(event.getEntity() instanceof Player player) || !ManaUtils.hasEnoughMana(player, mana))
            return;

        ItemStack stack = EntityUtils.findEquippedCurio(player, BotaniaItems.odinRing);

        if (!(stack.getItem() instanceof IRelicItem) || stack.getItem() != BotaniaItems.odinRing || ManaUtils.getTotalMana(player) <= 0)
            return;

        if (NBTUtils.getBoolean(stack, "toggled", true)) {
            ManaUtils.consumeMana(player, mana);

            event.setAmount(0);
        }

        if (!(NBTUtils.getBoolean(stack, "toggled", true))) {
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            LivingEntity target = event.getEntity();

            if (!(attacker instanceof LivingEntity || target == null))
                return;

            float damage = event.getAmount();

            ManaUtils.consumeMana(player, mana);

            attacker.hurt(source, damage * 0.5F);
        }

    }
}
