package it.hurts.sskirillss.rbocompat.mixin.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import it.hurts.sskirillss.rbocompat.entity.BaseBabylonianWeaponEntity;
import it.hurts.sskirillss.rbocompat.init.EntityRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.relic.KeyOfTheKingsLawItem;
import vazkii.botania.common.item.relic.RelicItem;

import java.util.Random;

@Mixin(KeyOfTheKingsLawItem.class)
public class KeyOfTheKingsLawItemMixin extends RelicItem implements ICurioItem, IRelicItem {
    private static boolean flag = true;

    public KeyOfTheKingsLawItemMixin(Properties props) {
        super(props);
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("arsenal")
                                .stat(StatData.builder("amount")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.01D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("edict")
                                .stat(StatData.builder("example")
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!(entity instanceof Player player) || player.level().isClientSide) return;

        Item heldItemMainHand = player.getMainHandItem().getItem();
        Item heldItemOffHand = player.getOffhandItem().getItem();

        if ((heldItemMainHand.equals(BotaniaItems.kingKey) || heldItemOffHand.equals(BotaniaItems.kingKey)) && flag) {
            for (int i = 0; i < (int) this.getAbilityValue(stack, "arsenal", "amount"); i++) {
                BaseBabylonianWeaponEntity weapon = new BaseBabylonianWeaponEntity(EntityRegistry.BABYLON_WEAPON.get(), level);

                weapon.setPlayerUUID(player.getUUID());

                double offsetX = 0.5 * i;
                double offsetZ = 0.5 * i;
                weapon.setPos(player.position().add(offsetX, 0, offsetZ));

                level.addFreshEntity(weapon);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.babylonSpawn, SoundSource.PLAYERS, 1.0F, 1.0F + level.random.nextFloat() * 3.0F);

            flag = false;
            return;
        }

        if (!(heldItemMainHand.equals(BotaniaItems.kingKey)) && !(heldItemOffHand.equals(BotaniaItems.kingKey)) && !flag) {
            for (BaseBabylonianWeaponEntity searchEntity : level.getEntitiesOfClass(BaseBabylonianWeaponEntity.class, player.getBoundingBox().inflate(10))) {
                if (searchEntity.getPlayerUUID() == player.getUUID()) {
                    searchEntity.discard();
                }
            }
            flag = true;
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 7.0, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = false)
    private void onUnequipped(Level world, Player player, @NotNull InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);

        cir.setReturnValue(InteractionResultHolder.pass(stack));
    }

}