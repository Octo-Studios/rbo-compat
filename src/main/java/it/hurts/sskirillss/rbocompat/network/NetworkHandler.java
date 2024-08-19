package it.hurts.sskirillss.rbocompat.network;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateModeShatterPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(RBOCompat.MODID, "network"),
                () -> "1.0",
                s -> true,
                s -> true);
        INSTANCE.messageBuilder(UpdateItemStackPacket.class, nextID())
                .encoder(UpdateItemStackPacket::encode)
                .decoder(UpdateItemStackPacket::decode)
                .consumerMainThread(UpdateItemStackPacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateModeShatterPacket.class, nextID())
                .encoder(UpdateModeShatterPacket::encode)
                .decoder(UpdateModeShatterPacket::decode)
                .consumerMainThread(UpdateModeShatterPacket::handle)
                .add();

    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendToClients(PacketDistributor.PacketTarget target, Object packet) {
        INSTANCE.send(target, packet);
    }
}