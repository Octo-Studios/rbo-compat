package it.hurts.sskirillss.rbocompat.network.packet;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateItemStackPacket {
    private final int xPos;
    private final int yPos;
    private final int zPos;



    public UpdateItemStackPacket(int xPos, int yPos, int zPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public static void encode(UpdateItemStackPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.xPos);
        buf.writeInt(msg.yPos);
        buf.writeInt(msg.zPos);
    }

    public static UpdateItemStackPacket decode(FriendlyByteBuf buf) {
        int xPos = buf.readInt();
        int yPos = buf.readInt();
        int zPos = buf.readInt();
        return new UpdateItemStackPacket(xPos, yPos, zPos);
    }

    public static void handle(UpdateItemStackPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                CompoundTag tag = stack.getOrCreateTag();

                tag.putInt("GetXPos", Math.max(1, tag.getInt("GetXPos") + msg.xPos));
                tag.putInt("GetYPos", Math.max(1, tag.getInt("GetYPos") + msg.yPos));
                tag.putInt("GetZPos", Math.max(1, tag.getInt("GetZPos") + msg.zPos));

                stack.setTag(tag);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
