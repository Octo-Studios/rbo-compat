package it.hurts.sskirillss.rbocompat.events;

import it.hurts.sskirillss.rbocompat.utils.ManaUtil;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.relic.RingOfLokiItem;

@Mod.EventBusSubscriber
public class RingOfOdinItemEvent {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide)
            return;

        ItemStack stack = EntityUtils.findEquippedCurio(player, BotaniaItems.odinRing);

        if (!(stack.getItem() instanceof IRelicItem relic) || stack.getItem() != BotaniaItems.odinRing)
            return;

        double multiplier = relic.getAbilityValue(stack, "retribution", "multiplier") / 100;
        int manaCost = (int) Math.round(multiplier * 1000);

        if (!ManaUtil.hasEnoughMana(player, manaCost))
            return;

        if (NBTUtils.getBoolean(stack, "toggled", true))
            event.setAmount((float) (event.getAmount() * multiplier));
        else {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker))
                return;

            attacker.hurt(source, (float) (event.getAmount() * multiplier));
        }

        ManaUtil.consumeMana(player, manaCost);
    }
}