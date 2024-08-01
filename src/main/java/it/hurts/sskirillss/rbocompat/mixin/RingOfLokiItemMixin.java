package it.hurts.sskirillss.rbocompat.mixin;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.common.item.relic.RelicBaubleItem;
import vazkii.botania.common.item.relic.RingOfLokiItem;

@Mixin(RingOfLokiItem.class)
public class RingOfLokiItemMixin extends RelicBaubleItem implements ICurioItem, IRelicItem {
    public RingOfLokiItemMixin(Properties props) {
        super(props);
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("guardian")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .stat(StatData.builder("efficiency")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                                .stat(StatData.builder("cooldown")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("immunity")
                                .stat(StatData.builder("radius")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 1)))
                                        .build())
                                .stat(StatData.builder("efficiency")
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
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (ability.equals("guardian")) {
            int duration = (int) Math.round(getAbilityValue(stack, "guardian", "duration") * 20);

            setAbilityCooldown(stack, "guardian", duration);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
//        Player player = Minecraft.getInstance().player;
//        if (player == null || player.level().isClientSide())
//            return;

   //     Level world = player.level();
      //  System.out.println("GGG");
//        for (Entity entity : world.getEntities(player, player.getBoundingBox().inflate(10))) {
//            if (entity instanceof Player) break;
//
//            double distance = player.distanceTo(entity);
//            double slowFactor = Math.max(0.1, 1 - distance / 100000);
//            entity.setDeltaMovement(entity.getDeltaMovement().multiply(slowFactor, 1, slowFactor));
//
//        }
    }

    @Inject(method = "onUnequipped", at = @At("HEAD"), cancellable = true, remap = false)
    private void onUnequipped(ItemStack stack, LivingEntity living, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onPlayerInteract", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onPlayerAttacked(Player player, Level world, InteractionHand hand, BlockHitResult lookPos, CallbackInfoReturnable<InteractionResult> cir) {
        cir.cancel();
    }

    @Inject(method = "getUseOnContext", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getUseOnContext(Player player, InteractionHand hand, BlockPos pos, Vec3 lookHit, Direction direction, CallbackInfoReturnable<UseOnContext> cir) {
        cir.cancel();
    }
}