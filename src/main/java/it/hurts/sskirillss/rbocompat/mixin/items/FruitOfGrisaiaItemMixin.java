package it.hurts.sskirillss.rbocompat.mixin.items;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.common.item.relic.FruitOfGrisaiaItem;
import vazkii.botania.common.item.relic.RelicItem;

import static it.hurts.sskirillss.relics.items.relics.InfinityHamItem.TAG_PIECES;
import static net.minecraft.world.item.alchemy.PotionUtils.TAG_POTION;

@Mixin(FruitOfGrisaiaItem.class)
public class FruitOfGrisaiaItemMixin extends RelicItem implements IRelicItem {
    public FruitOfGrisaiaItemMixin(Properties props) {
        super(props);
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("fetus")
                                .stat(StatData.builder("amount")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                               .stat(StatData.builder("volume")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

//        if (NBTUtils.getInt(stack, TAG_PIECES, 0) > 0
//                && (player.getFoodData().needsFood() || player.isCreative()))
            player.startUsingItem(hand);

        return super.use(world, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!(entity instanceof Player player))
            return;

        if (!player.getFoodData().needsFood() && !player.isCreative()) {
            player.stopUsingItem();

            return;
        }

        if (player.tickCount % 10 != 0)
            return;

        int pieces = NBTUtils.getInt(stack, TAG_PIECES, 0);
        CompoundTag nbt = stack.getOrCreateTag();

        if (pieces > 0) {
            NBTUtils.setInt(stack, TAG_PIECES, --pieces);

            int feed = (int) Math.round(getAbilityValue(stack, "autophagy", "feed"));

            player.getFoodData().eat(feed, feed);

            spreadExperience(player, stack, Math.max(1, Math.min(20 - player.getFoodData().getFoodLevel(), feed)));

            if (!canUseAbility(stack, "infusion") || !nbt.contains(TAG_POTION, 9))
                return;

            int duration = (int) Math.round(getAbilityValue(stack, "infusion", "duration") * 20);

            ListTag list = nbt.getList(TAG_POTION, 10);

            for (int i = 0; i < list.size(); ++i) {
                MobEffectInstance effect = MobEffectInstance.load(list.getCompound(i));

                if (effect == null || effect.getEffect().isInstantenous())
                    continue;

                MobEffectInstance currentEffect = player.getEffect(effect.getEffect());

                player.addEffect(new MobEffectInstance(effect.getEffect(), currentEffect == null ? duration : currentEffect.getDuration() + duration, effect.getAmplifier()));
            }

            if (pieces <= 0 && nbt.contains(TAG_POTION))
                nbt.remove(TAG_POTION);
        } else
            player.stopUsingItem();
    }
}