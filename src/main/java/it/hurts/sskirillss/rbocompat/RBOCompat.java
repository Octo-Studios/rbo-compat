package it.hurts.sskirillss.rbocompat;

import it.hurts.sskirillss.rbocompat.entity.PixieEntity;
import it.hurts.sskirillss.rbocompat.init.EntityRegistry;
import it.hurts.sskirillss.rbocompat.init.ItemRegistry;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.List;
import java.util.Random;

@Mod(RBOCompat.MODID)
public class RBOCompat {
    public static final String MODID = "rbocompat";

    public RBOCompat() {
        MinecraftForge.EVENT_BUS.register(this);

        ItemRegistry.register();
        EntityRegistry.register();
        NetworkHandler.register();
    }

    public static void curioTick(SlotContext slotContext, ItemStack stack, List<Mob> gatherMobs, double s) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        for (Mob entity : gatherMobs) {
            if (entity instanceof PixieEntity)
                return;

            double distance = player.distanceTo(entity);

            if (distance <= s) {
                float reductionFactor = (float) (0.25 + ((s - distance) / 10));

                EntityUtils.applyAttribute(entity, stack, Attributes.MOVEMENT_SPEED, -reductionFactor, AttributeModifier.Operation.MULTIPLY_TOTAL);
                spawnParticles(player.level(), player, entity, reductionFactor);
            } else {
                EntityUtils.removeAttribute(entity, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
        }
    }

    public static void spawnParticles(Level level, Player player, Mob mob, double reductionFactor) {
        if (level.isClientSide)
            return;

        int particleCount = 30;

        Vec3 directionToPlayer = player.position().subtract(mob.position()).normalize();

        double angleToPlayer = Math.atan2(directionToPlayer.z, directionToPlayer.x);
        double arcAngle = Math.PI / 2;
        double angleStep = arcAngle / particleCount;
        double radius = 1.5;

        for (int i = 0; i < particleCount; i++) {
            float brightness = (float) (1.0 - reductionFactor);  // Интенсивность яркости

            double angle = angleToPlayer - arcAngle / 2 + angleStep * i;

            double offsetX = radius * Math.cos(angle);
            double offsetZ = radius * Math.sin(angle);

            RandomSource random = player.getRandom();

            ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(
                            new Color(random.nextInt(50), 100 + random.nextInt(155), random.nextInt(50)),
                            0.2F, 2, 0),
                    mob.getX() + offsetX, mob.getY() + 0.2, mob.getZ() + offsetZ,
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }
    }

}