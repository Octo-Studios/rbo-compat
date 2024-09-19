package it.hurts.sskirillss.rbocompat;

import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.common.item.BotaniaItems;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabEvent {

    @SubscribeEvent
    public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == CreativeTabRegistry.RELICS_TAB.get()) {
            event.accept(BotaniaItems.lokiRing);
            event.accept(BotaniaItems.odinRing);
            event.accept(BotaniaItems.thorRing);
        }
    }
}
