package it.hurts.sskirillss.rbocompat.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class BaseBabylonianWeaponEntity extends FlyingMob {

    @Setter
    @Getter
    private UUID playerUUID;

    public BaseBabylonianWeaponEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (playerUUID != null && !this.level().isClientSide) {
            Player player = this.level().getPlayerByUUID(playerUUID);

            if (player != null) {
                Vec3 playerPos = player.position();

                double distanceBehind = 1; // Смещение назад относительно игрока по оси Z
                double heightAbove = 1.0; // Смещение вверх относительно игрока по оси Y
                double sideOffset = 0.0; // Смещение влево-вправо по оси X (можно сделать 0 для центра за спиной)

                Vec3 behindPos = playerPos.add(sideOffset, heightAbove, distanceBehind);

                this.setPos(behindPos.x(), behindPos.y(), behindPos.z());

                this.setXRot(player.getRotationVector().x);
                this.setYRot(player.getRotationVector().y);
            }
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putUUID("PlayerUUID", playerUUID);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("PlayerUUID")) {
            this.playerUUID = tag.getUUID("PlayerUUID");
        }
    }
}
