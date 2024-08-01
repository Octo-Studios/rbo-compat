package it.hurts.sskirillss.rbocompat.entity;

import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PixieEntity extends FlyingMob {
    private static final double ORBIT_RADIUS = 5.0;
    private static final double ORBIT_SPEED = 0.05;
    private double angle;

    public PixieEntity(EntityType<? extends FlyingMob> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    public void tick() {
        super.tick();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            angle += ORBIT_SPEED;
            if (angle >= 2 * Math.PI) {
                angle = 0;
            }

            // Вычисляем новую позицию
            double x = player.getX() + ORBIT_RADIUS * Math.cos(angle);
            double z = player.getZ() + ORBIT_RADIUS * Math.sin(angle);
            double y = player.getY() + 1.5;

            this.setPos(x, y, z);
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }


    @Override
    public boolean isNoGravity() {
        return true;
    }
}
