package it.hurts.sskirillss.rbocompat.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PixieEntity extends Mob {
    private double angle;

    public PixieEntity(EntityType<? extends Mob> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    public void tick() {
        super.tick();
        this.noPhysics = true;
        this.setNoGravity(true);

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        double orbitRadius = 3 + Math.sin(tickCount * 0.075) * 0.5;

        angle += 0.07; // speed
        if (angle >= 2 * Math.PI) {
            angle = 0;
        }

        HitResult result = this.level().clip(new ClipContext(new Vec3(this.getX(), player.getY() + 2, this.getZ()), new Vec3(this.getX(),
                player.getY() - 2, this.getZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        Vec3 position = result.getLocation();
        if (result.getType() == HitResult.Type.MISS || level().getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z)).blocksMotion())
            position = player.position();

        double x = player.getX() + orbitRadius * Math.cos(angle);
        double z = player.getZ() + orbitRadius * Math.sin(angle);
        double y = position.y + 0.5;


        // this.setDeltaMovement(this.getDeltaMovement().add(0, new Vec3(x, y, z).subtract(this.position()).normalize().scale(0.5).y,0));
        this.setPos(x, y, z);
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return false;
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
