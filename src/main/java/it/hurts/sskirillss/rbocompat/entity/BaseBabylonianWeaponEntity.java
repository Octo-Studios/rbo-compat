package it.hurts.sskirillss.rbocompat.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BaseBabylonianWeaponEntity extends FlyingMob {

    @Setter
    @Getter
    private UUID playerUUID;

    @Setter
    @Getter
    private int customValue;

    public BaseBabylonianWeaponEntity(EntityType<? extends FlyingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.playerUUID != null) {
            tag.putUUID("Player", playerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("Player")) {
            this.playerUUID = tag.getUUID("Player");
        }
    }
}
