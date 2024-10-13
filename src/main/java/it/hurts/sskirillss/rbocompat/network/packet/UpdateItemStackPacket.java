package it.hurts.sskirillss.rbocompat.network.packet;

import it.hurts.sskirillss.rbocompat.items.TerraShattererItemImplementation;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

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
            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() != BotaniaItems.terraPick)
                return;

            CompoundTag tag = stack.getOrCreateTag();

            NBTUtils.setInt(stack, "GetXPos", Math.max(1, tag.getInt("GetXPos") + msg.xPos));

            if (NBTUtils.getInt(stack, "GetXPos", 0)
                    * NBTUtils.getInt(stack, "GetYPos", 0)
                    * NBTUtils.getInt(stack, "GetZPos", 0) > valueBockLimit(player))
                NBTUtils.setInt(stack, "GetXPos", Math.max(1, tag.getInt("GetXPos") - 2));

            NBTUtils.setInt(stack, "GetYPos", Math.max(1, tag.getInt("GetYPos") + msg.yPos));

            if (NBTUtils.getInt(stack, "GetXPos", 0)
                    * NBTUtils.getInt(stack, "GetYPos", 0)
                    * NBTUtils.getInt(stack, "GetZPos", 0) > valueBockLimit(player))
                NBTUtils.setInt(stack, "GetYPos", Math.max(1, tag.getInt("GetYPos") - 1));

            NBTUtils.setInt(stack, "GetZPos", Math.max(1, tag.getInt("GetZPos") + msg.zPos));

            if (NBTUtils.getInt(stack, "GetXPos", 0)
                    * NBTUtils.getInt(stack, "GetYPos", 0)
                    * NBTUtils.getInt(stack, "GetZPos", 0) > valueBockLimit(player))
                NBTUtils.setInt(stack, "GetZPos", Math.max(1, tag.getInt("GetZPos") - 1));
        });

        ctx.get().setPacketHandled(true);
    }

    public static int valueBockLimit(Player player) {
        ItemStack itemStack = EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing);

        if (!(itemStack.getItem() instanceof IRelicItem relic) || itemStack.getItem() != BotaniaItems.thorRing)
            return 0;

        int picLevel = TerraShattererItem.getLevel(player.getItemInHand(InteractionHand.MAIN_HAND));

        return (int) ((picLevel * (220 - (10 - picLevel) * 22)) + relic.getAbilityValue(itemStack, "entropy", "capacity"));
    }
}
