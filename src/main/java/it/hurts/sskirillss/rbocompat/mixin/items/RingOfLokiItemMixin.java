package it.hurts.sskirillss.rbocompat.mixin.items;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.entity.PixieEntity;
import it.hurts.sskirillss.rbocompat.init.EntityRegistry;
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
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.common.item.relic.RelicBaubleItem;
import vazkii.botania.common.item.relic.RingOfLokiItem;

import java.awt.*;
import java.util.List;

@Mixin(RingOfLokiItem.class)
public abstract class RingOfLokiItemMixin extends RelicBaubleItem implements ICurioItem, IRelicItem {

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
                                        .initialValue(0.1D, 1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> MathUtils.round(value * 10, 1))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> (MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("immunity")
                                .stat(StatData.builder("radius")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (ability.equals("guardian") && !player.level().isClientSide) {
            Level level = player.getCommandSenderWorld();

            setAbilityCooldown(stack, "guardian", 400);

            PixieEntity pixieEntity = new PixieEntity(EntityRegistry.PIXIE.get(), level);

            pixieEntity.setLifeTimeEntity(500);
            pixieEntity.setPlayer(player);
            pixieEntity.setPlayerUUID(player.getUUID());
            pixieEntity.setPos(player.getX(), player.getY(), player.getZ());

            level.addFreshEntity(pixieEntity);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        for (Mob entity : gatherMobs(slotContext.entity().level(), player, stack)) {
            if (entity instanceof PixieEntity)
                return;

            double distance = player.distanceTo(entity);
            double radius = getAbilityValue(stack, "immunity", "radius");

            if (distance <= radius) {
                float reductionFactor = (float) (0.25 + ((radius - distance) / 10));

                EntityUtils.applyAttribute(entity, stack, Attributes.MOVEMENT_SPEED, -reductionFactor, AttributeModifier.Operation.MULTIPLY_TOTAL);
                spawnParticles(player.level(), player, entity, radius, distance);
            } else {
                EntityUtils.removeAttribute(entity, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
        }
    }

    private static void spawnParticles(Level level, Player player, Mob mob, double radius, double distance) {
        if (level.isClientSide)
            return;

        int particleCount = 20;

        Vec3 directionToPlayer = player.position().subtract(mob.position()).normalize();

        double angleToPlayer = Math.atan2(directionToPlayer.z, directionToPlayer.x);
        double arcAngle = Math.PI / 2;
        double angleStep = arcAngle / particleCount;

        for (int i = 0; i < particleCount; i++) {
            double angle = angleToPlayer - arcAngle / 2 + angleStep * i;

            RandomSource random = player.getRandom();

            ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(
                            new Color(random.nextInt(50), 100 + random.nextInt(155), random.nextInt(50)),
                            (float) ((radius - distance) / 10), 1, 0),
                    mob.getX() + Math.cos(angle), mob.getY() + 0.2, mob.getZ() + Math.sin(angle),
                    1, 0, 0, 0, 0
            );
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.getCommandSenderWorld().isClientSide()
                || stack.getItem() == newStack.getItem())
            return;

        for (Mob entity : gatherMobs(player.level(), player, stack)) {
            EntityUtils.removeAttribute(entity, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
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

    @Inject(method = "onPlayerInteract", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onPlayerAttacked(Player player, Level world, InteractionHand hand, BlockHitResult lookPos, CallbackInfoReturnable<InteractionResult> cir) {
        cir.cancel();
    }

}