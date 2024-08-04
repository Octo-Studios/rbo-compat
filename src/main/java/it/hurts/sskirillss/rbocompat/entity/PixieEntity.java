package it.hurts.sskirillss.rbocompat.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PixieEntity extends Mob {
    @Setter
    @Getter
    private Player owner;

    private double angle;

    public PixieEntity(EntityType<? extends Mob> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);

        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (owner == null) return;

        moveAroundThePlayerInACircle();
    }

    private void moveAroundThePlayerInACircle() {
        double orbitRadius = 3 + Math.sin(tickCount * 0.075) * 0.5;

        angle += 0.07; // speed

        if (angle >= 2 * Math.PI)
            angle = 0;

        HitResult result = this.level().clip(new ClipContext(new Vec3(this.getX(), owner.getY() + 2, this.getZ()), new Vec3(this.getX(),
                owner.getY() - 2, this.getZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        Vec3 position = result.getLocation();
        if (result.getType() == HitResult.Type.MISS || level().getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z)).blocksMotion())
            position = owner.position();

        // this.setDeltaMovement(this.getDeltaMovement().add(0, new Vec3(x, y, z).subtract(this.position()).normalize().scale(0.5).y,0));

        this.setPos(
                owner.getX() + orbitRadius * Math.cos(angle),
                owner.getZ() + orbitRadius * Math.sin(angle),
                position.y + 0.5
        );
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
