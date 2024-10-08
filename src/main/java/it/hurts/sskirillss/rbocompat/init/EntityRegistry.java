package it.hurts.sskirillss.rbocompat.init;

import it.hurts.sskirillss.rbocompat.RBOCompat;
import it.hurts.sskirillss.rbocompat.entity.BaseBabylonianWeaponEntity;
import it.hurts.sskirillss.rbocompat.entity.ObserverEntity;
import it.hurts.sskirillss.rbocompat.entity.PixieEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RBOCompat.MODID);

    public static final RegistryObject<EntityType<PixieEntity>> PIXIE = ENTITIES.register("pixie", () ->
            EntityType.Builder.of(PixieEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(RBOCompat.MODID, "pixie").toString()));

    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER = ENTITIES.register("observer", () ->
            EntityType.Builder.of(ObserverEntity::new, MobCategory.MISC)
                    .sized(1, 1)
                    .build(new ResourceLocation(RBOCompat.MODID, "observer").toString()));

    public static final RegistryObject<EntityType<BaseBabylonianWeaponEntity>> BABYLON_WEAPON = ENTITIES.register("babylon_weapon", () ->
            EntityType.Builder.of(BaseBabylonianWeaponEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(RBOCompat.MODID, "babylon_weapon").toString()));


    public static void register() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void setupClient(EntityAttributeCreationEvent event) {
        event.put(PIXIE.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1D).build());
        event.put(OBSERVER.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1D).build());
        event.put(BABYLON_WEAPON.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1D).build());
    }
}