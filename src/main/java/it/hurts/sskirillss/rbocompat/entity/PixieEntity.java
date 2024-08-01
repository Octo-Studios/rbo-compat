package it.hurts.sskirillss.rbocompat.entity;

import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PixieEntity extends ThrowableProjectile {
    public PixieEntity(EntityType<? extends ThrowableProjectile> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    public void tick() {
//        super.tick();
//
//        Vec3 targetPos = getVec3();
//        Vec3 currentPos = this.position();
//
//        Vec3 direction = targetPos.subtract(currentPos);
//        Vec3 desiredMovement = direction.normalize().scale(0.2);
//
//        Vec3 newMovement = this.getDeltaMovement().lerp(desiredMovement, 0.5);
//
//        this.setDeltaMovement(newMovement);
//        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private @NotNull Vec3 getVec3() {
        Player targetPlayer = Minecraft.getInstance().player;

        double flyRadius = 2.0;
        double amplitude = 1.0;
        double frequency = 1.0;

        double angle = this.tickCount * 0.1;

        double targetX = targetPlayer.getX() + flyRadius * Math.cos(angle);
        double targetZ = targetPlayer.getZ() + flyRadius * Math.sin(angle);
        double targetY = targetPlayer.getY() + amplitude * Math.sin(frequency * angle);

        return new Vec3(targetX, targetY, targetZ);
    }

    @Override
    protected void defineSynchedData() {

    }


    @Override
    public boolean isNoGravity() {
        return true;
    }
}
