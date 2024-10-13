package it.hurts.sskirillss.rbocompat.network.packet.client;

import it.hurts.sskirillss.rbocompat.client.screen.MiningAreaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenScreenPacket {

    public OpenScreenPacket() {

    }

    public static OpenScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenScreenPacket();
    }

    public static void encode(OpenScreenPacket packet, FriendlyByteBuf buffer) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(this::openScreen);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void openScreen() {
        Minecraft.getInstance().setScreen(new MiningAreaScreen());
    }
}
