package it.hurts.sskirillss.rbocompat.network.packet;

import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import vazkii.botania.common.item.BotaniaItems;

import java.util.function.Supplier;

public class UpdateModeShatterPacket {
    private final boolean mode;

    public UpdateModeShatterPacket(boolean mode) {
        this.mode = mode;
    }

    public static void encode(UpdateModeShatterPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.mode);
    }

    public static UpdateModeShatterPacket decode(FriendlyByteBuf buf) {
        boolean flag = buf.readBoolean();
        return new UpdateModeShatterPacket(flag);
    }

    public static void handle(UpdateModeShatterPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player != null) {
                ItemStack stack = EntityUtils.findEquippedCurio(player, BotaniaItems.lokiRing);
                CompoundTag tag = stack.getOrCreateTag();

                NBTUtils.setBoolean(stack, "selectMode", msg.mode);

                stack.setTag(tag);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
