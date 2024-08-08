package it.hurts.sskirillss.rbocompat.entity;

import lombok.Setter;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ObserverEntity extends FlyingMob {
    @Setter
    private Player player;

    public ObserverEntity(EntityType<? extends FlyingMob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);

    }

    @Override
    public void tick() {
        super.tick();
        player = this.level().getNearestPlayer(this, 10);

        if (player == null) return;

        this.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition(1F));
        this.lookAt(EntityAnchorArgument.Anchor.FEET, player.getEyePosition(1F));
    }

}
