package it.hurts.sskirillss.rbocompat.entity;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.UUID;

public class PixieEntity extends Mob {

    @Setter
    private Player player;

    @Getter
    @Setter
    private UUID playerUUID;

    private double angle;

    public PixieEntity(EntityType<? extends Mob> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);

        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (player == null && playerUUID != null)
            player = this.level().getPlayerByUUID(playerUUID);

        if (player == null) return;

        moveAroundThePlayerInACircle();
        collide();

    }

    private void moveAroundThePlayerInACircle() {
        double orbitRadius = 3 + Math.sin(tickCount * 0.075) * 0.5;

        angle += 0.07; // speed

        if (angle >= 2 * Math.PI)
            angle = 0;

        HitResult result = this.level().clip(new ClipContext(new Vec3(this.getX(), player.getY() + 2, this.getZ()), new Vec3(this.getX(),
                player.getY() - 2, this.getZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        Vec3 position = result.getLocation();
        if (result.getType() == HitResult.Type.MISS || level().getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z)).blocksMotion())
            position = player.position();

        this.setPos(
                player.getX() + orbitRadius * Math.cos(angle),
                position.y + 0.5,
                player.getZ() + orbitRadius * Math.sin(angle)
        );
    }

    private void collide() {
        BlockPos blockPos = this.blockPosition();
        BlockState blockState = level().getBlockState(blockPos);
        Block block = level().getBlockState(this.blockPosition()).getBlock();

        if (block instanceof CropBlock cropBlock) {
            if (tickCount % 2 != 0) return;

            int currentAge = cropBlock.getAge(blockState);
            int maxAge = cropBlock.getMaxAge();

            if (currentAge < maxAge) {
                level().setBlock(blockPos, cropBlock.getStateForAge(currentAge + 1), 2);

                for (int i = 0; i < 10; i++) {
                    ((ServerLevel) level()).sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            blockPos.getX() + level().random.nextDouble() * 0.6D + 0.2D,
                            blockPos.getY() + level().random.nextDouble() * 0.6D + 0.2D,
                            blockPos.getZ() + level().random.nextDouble() * 0.6D + 0.2D,
                            1,
                            0.0D, 0.0D, 0.0D,
                            0.1D
                    );
                }

            }
        } else {
            if (tickCount % 5 != 0) return;

            for (Entity searchEntity : this.level().getEntities(this, this.getBoundingBox())) {
                if (!(searchEntity instanceof PixieEntity) && searchEntity instanceof LivingEntity entity && entity.getUUID() != getPlayerUUID()) {
                    entity.addEffect(new MobEffectInstance(EffectRegistry.PARALYSIS.get(), 100, 1));

                    for (int i = 0; i < 200; i++) {
                        double theta = level().random.nextDouble() * Math.PI * 2;
                        double phi = level().random.nextDouble() * Math.PI;

                        double particleX = this.getX() + Math.sin(phi) * Math.cos(theta);
                        double particleY = this.getY() + Math.sin(phi) * Math.sin(theta);
                        double particleZ = this.getZ() + 1 * Math.cos(phi);

                        ((ServerLevel) level()).sendParticles(
                                (ParticleUtils.constructSimpleSpark(Color.green, 0.5F, 1, 0.9f)),
                                particleX,
                                particleY,
                                particleZ,
                                1,
                                0.0D, 0.0D, 0.0D,
                                0.1D
                        );
                    }
                }
            }
        }

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        this.discard();

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.player != null) {
            tag.putUUID("Player", this.player.getUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("Player")) {
            this.playerUUID = tag.getUUID("Player");
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }
}
