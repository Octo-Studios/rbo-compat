package it.hurts.sskirillss.rbocompat.mixin.item;

import it.hurts.sskirillss.rbocompat.entity.PixieEntity;
import it.hurts.sskirillss.rbocompat.init.EntityRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
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
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.relic.RelicBaubleItem;
import vazkii.botania.common.item.relic.RingOfLokiItem;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mixin(RingOfLokiItem.class)
public class RingOfLokiItemMixin extends RelicBaubleItem implements ICurioItem, IRelicItem {
    private static final String ORIGINAL_SPEED_KEY = "OriginalSpeed";

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
                                        .initialValue(1, 5)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5D)
                                        .formatValue(Double::doubleValue)
                                        .build())
                                .stat(StatData.builder("efficiency")
                                        .initialValue(1D, 5)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
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
            Level level = player.getCommandSenderWorld();

            if (level.isClientSide)
                return;

            //setAbilityCooldown(stack, "guardian", (int) Math.round(getAbilityValue(stack, "guardian", "duration") * 20));

            PixieEntity pixieEntity = new PixieEntity(EntityRegistry.PIXIE.get(), level);

            pixieEntity.setPlayer(player);
            pixieEntity.setPlayerUUID(player.getUUID());
            pixieEntity.setPos(player.getX(), player.getY(), player.getZ());

            level.addFreshEntity(pixieEntity);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;

        Level world = player.level();

        for (Mob entity : gatherMobs(world, player, stack)) {
            if (entity instanceof PixieEntity) return;

            AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);

            if (!entity.getPersistentData().contains(ORIGINAL_SPEED_KEY))
                entity.getPersistentData().putDouble(ORIGINAL_SPEED_KEY, speedAttribute.getBaseValue());

            double originalSpeed = entity.getPersistentData().getDouble(ORIGINAL_SPEED_KEY);

            if (player.distanceTo(entity) <= this.getAbilityValue(stack, "immunity", "radius")) {
                speedAttribute.setBaseValue(originalSpeed * Math.max(0.2, 1 - (player.distanceTo(entity) * this.getAbilityValue(stack, "immunity", "efficiency"))));
            } else {
                speedAttribute.setBaseValue(originalSpeed);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.getCommandSenderWorld().isClientSide()
                || EntityUtils.findEquippedCurio(player, BotaniaItems.lokiRing).getItem() instanceof RingOfLokiItem)
            return;


        for (Mob entity : gatherMobs(player.level(), player, stack)) {
            if (entity.getPersistentData().contains(ORIGINAL_SPEED_KEY))
                entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(entity.getPersistentData().getFloat(ORIGINAL_SPEED_KEY));
        }

        for (Entity entity : player.level().getEntities(player, new AABB(player.blockPosition()).inflate(10))) {
            if (entity instanceof PixieEntity pixieEntity && pixieEntity.getPlayerUUID().equals(player.getUUID()))
                pixieEntity.discard();
        }

    }

    private List<Mob> gatherMobs(Level level, Player player, ItemStack stack) {
        return level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(this.getAbilityValue(stack, "immunity", "radius") + 3));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
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