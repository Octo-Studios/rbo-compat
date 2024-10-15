package it.hurts.sskirillss.rbocompat.network;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateItemStackPacket;
import it.hurts.sskirillss.rbocompat.network.packet.UpdateModeShatterPacket;
import it.hurts.sskirillss.rbocompat.network.packet.client.OpenScreenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
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
        INSTANCE.messageBuilder(OpenScreenPacket.class, nextID())
                .encoder(OpenScreenPacket::encode)
                .decoder(OpenScreenPacket::decode)
                .consumerMainThread(OpenScreenPacket::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}