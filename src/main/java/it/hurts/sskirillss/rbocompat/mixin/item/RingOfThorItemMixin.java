package it.hurts.sskirillss.rbocompat.mixin.item;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.rbocompat.client.screen.MiningAreaScreen;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.block.Bound;
import vazkii.botania.api.item.SequentialBreaker;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.advancements.LokiPlaceTrigger;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.relic.RelicBaubleItem;
import vazkii.botania.common.item.relic.RingOfThorItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(RingOfThorItem.class)
public class RingOfThorItemMixin extends RelicBaubleItem implements IRelicItem {
    private static final String TAG_CURSOR_LIST = "cursorList";
    private static final String TAG_CURSOR_PREFIX = "cursor";
    private static final String TAG_CURSOR_COUNT = "cursorCount";
    private static final String TAG_X_OFFSET = "xOffset";
    private static final String TAG_Y_OFFSET = "yOffset";
    private static final String TAG_Z_OFFSET = "zOffset";
    private static final String TAG_X_ORIGIN = "xOrigin";
    private static final String TAG_Y_ORIGIN = "yOrigin";
    private static final String TAG_Z_ORIGIN = "zOrigin";
    private static boolean recCall = false;

    public RingOfThorItemMixin(Properties props) {
        super(props);
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("entropy")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .castPredicate("entropy", (player, stack) -> player.getMainHandItem().getItem().toString().contains("terra_pick"))
                                        .build())
                                .stat(StatData.builder("capacity")
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("capacity")
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("revelation")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(9D, 19D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("rumble")
                                .stat(StatData.builder("capacity")
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (ability.equals("entropy") && player.level().isClientSide) {
            Minecraft.getInstance().setScreen(new MiningAreaScreen());
        }

        if (ability.equals("revelation")) {
            BlockPos pos = player.blockPosition();
            Level world = player.level();
            int range = (int) this.getAbilityValue(stack, "revelation", "radius");
            long seedRandom = world.random.nextLong();

            for (BlockPos pos_ : BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range))) {
                BlockState state = world.getBlockState(pos_);
                Block block = state.getBlock();

                if (state.is(BlockTags.create(new ResourceLocation("forge", "ores")))) {

                    Random rand = new Random((long) BuiltInRegistries.BLOCK.getKey(block).hashCode() ^ seedRandom);
                    WispParticleData data = WispParticleData.wisp(0.25F, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 8.0F, false);
                    world.addParticle(data, true, (float) pos_.getX() + world.random.nextFloat(), (float) pos_.getY() + world.random.nextFloat(), (float) pos_.getZ() + world.random.nextFloat(), 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Inject(method = "getThorRing", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getThorRing(Player player, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(ItemStack.EMPTY);
    }

}