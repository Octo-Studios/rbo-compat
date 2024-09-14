package it.hurts.sskirillss.rbocompat.network.packet;

import it.hurts.sskirillss.rbocompat.client.screen.MiningAreaScreen;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import vazkii.botania.common.item.BotaniaItems;

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
                if (EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing).getTag().getBoolean("selectMode"))
                    return;

                ItemStack stack = player.getMainHandItem();
                CompoundTag tag = stack.getOrCreateTag();

                NBTUtils.setInt(stack, "GetXPos", Math.max(1, tag.getInt("GetXPos") + msg.xPos));
                NBTUtils.setInt(stack, "GetYPos", Math.max(1, tag.getInt("GetYPos") + msg.yPos));
                NBTUtils.setInt(stack, "GetZPos", Math.max(1, tag.getInt("GetZPos") + msg.zPos));

                stack.setTag(tag);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
