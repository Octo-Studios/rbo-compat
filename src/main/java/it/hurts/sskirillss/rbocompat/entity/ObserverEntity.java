package it.hurts.sskirillss.rbocompat.entity;

import lombok.Setter;
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

        if (player == null) return;

        //   this.setPos(player.getX(), player.getY() + 2, player.getX());
    }
}
