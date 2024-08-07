package it.hurts.sskirillss.rbocompat.entity;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class PixieEntity extends Mob {

    @Setter
    private Player player;
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

        if (this.hasEffect(EffectRegistry.PARALYSIS.get()))
            removeEffect(EffectRegistry.PARALYSIS.get());
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

        double x = player.getX() + orbitRadius * Math.cos(angle);
        double y = position.y + 0.5;
        double z = player.getZ() + orbitRadius * Math.sin(angle);

        // this.setDeltaMovement(this.getDeltaMovement().add(0, new Vec3(x, y, z).subtract(this.position()).normalize().scale(0.5).y, 0));

        this.setPos(x, y, z);
    }

    private void collide() {
        BlockPos blockPos = this.blockPosition();
        Block block = this.level().getBlockState(this.blockPosition()).getBlock();

        if (block instanceof CropBlock) {
            //  System.out.println(level().getBlockEntity(blockPos).saveWithoutMetadata());
//            if (this.level().isClientSide)
//                this.level().levelEvent(2005, blockPos, 0); // Показать частицы костной муки


        } else {
            for (Entity searchEntity : this.level().getEntities(this, this.getBoundingBox()))
                if (searchEntity instanceof LivingEntity entity)
                    entity.addEffect(new MobEffectInstance(EffectRegistry.PARALYSIS.get(), 100, 1));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.player != null) {
            tag.putUUID("Player", this.player.getUUID());
            System.out.println(tag.get("Player"));
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
