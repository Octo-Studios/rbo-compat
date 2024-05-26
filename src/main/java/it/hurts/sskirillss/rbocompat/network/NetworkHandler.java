package it.hurts.sskirillss.rbocompat.network;

import it.hurts.sskirillss.rbocompat.RBOCompat;
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