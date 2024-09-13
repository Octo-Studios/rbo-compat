package it.hurts.sskirillss.rbocompat;

import it.hurts.sskirillss.rbocompat.init.EntityRegistry;
import it.hurts.sskirillss.rbocompat.init.ItemRegistry;
import it.hurts.sskirillss.rbocompat.network.NetworkHandler;
import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.common.item.BotaniaItems;

@Mod(RBOCompat.MODID)
public class RBOCompat {
    public static final String MODID = "rbocompat";

    public RBOCompat() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        ItemRegistry.register();
        EntityRegistry.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }
}