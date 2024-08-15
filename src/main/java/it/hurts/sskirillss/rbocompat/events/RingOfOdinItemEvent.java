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
        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack stack = EntityUtils.findEquippedCurio(player, BotaniaItems.odinRing);

        if (!(stack.getItem() instanceof IRelicItem relic) || stack.getItem() != BotaniaItems.odinRing)
            return;

        double multiplier = relic.getAbilityValue(stack, "retribution", "multiplier");
        int manaCost = (int) Math.round(multiplier * 1000);

        if (!ManaUtils.hasEnoughMana(player, manaCost))
            return;

        if (NBTUtils.getBoolean(stack, "toggled", true)) {
            event.setAmount((float) (event.getAmount() * relic.getAbilityValue(stack, "retribution", "multiplier")));
        } else {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker))
                return;

            float damage = event.getAmount();

            EntityUtils.hurt(attacker, source, (float) (damage * multiplier));
        }

        ManaUtils.consumeMana(player, manaCost);
    }
}