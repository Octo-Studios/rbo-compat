package it.hurts.sskirillss.rbocompat.mixin.item;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.rbocompat.client.screen.MiningAreaScreen;
import it.hurts.sskirillss.rbocompat.events.RingOfThorItemEvent;
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
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.api.item.WireframeCoordinateListProvider;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.common.item.relic.RelicBaubleItem;
import vazkii.botania.common.item.relic.RingOfThorItem;

import java.util.List;
import java.util.Random;

@Mixin(RingOfThorItem.class)
public class RingOfThorItemMixin extends RelicBaubleItem implements ICurioItem, IRelicItem, WireframeCoordinateListProvider {
    @Unique
    private static final String TAG_X_ORIGIN = "xOrigin";
    @Unique
    private static final String TAG_Y_ORIGIN = "yOrigin";
    @Unique
    private static final String TAG_Z_ORIGIN = "zOrigin";

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
                                        .castPredicate("entropy", (player, stack) -> player.getMainHandItem().getItem() == BotaniaItems.terraPick)
                                        .build())
                                .stat(StatData.builder("capacity")
                                        .initialValue(10D, 15D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("revelation")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(5D, 7D)
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

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Inject(method = "getThorRing", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getThorRing(Player player, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(ItemStack.EMPTY);
    }

    @Override
    public BlockPos getSourceWireframe(Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        if (EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing) != stack) {
            return null;
        }

        BlockPos currentBuildCenter = getBindingCenter(stack);
        if (currentBuildCenter.getY() != Integer.MIN_VALUE) {
            return currentBuildCenter;
        } else if (mc.hitResult instanceof BlockHitResult hitRes
                && mc.hitResult.getType() == HitResult.Type.BLOCK
                && !RingOfThorItemEvent.getCursorList(stack).isEmpty()) {
            return hitRes.getBlockPos();
        }

        return null;
    }

    @Override
    public List<BlockPos> getWireframesToDraw(Player player, ItemStack stack) {
        if (EntityUtils.findEquippedCurio(player, BotaniaItems.thorRing) != stack)
            return ImmutableList.of();

        HitResult lookPos = Minecraft.getInstance().hitResult;

        if (lookPos != null
                && lookPos.getType() == HitResult.Type.BLOCK
                && !player.level().isEmptyBlock(((BlockHitResult) lookPos).getBlockPos())) {
            List<BlockPos> list = RingOfThorItemEvent.getCursorList(stack);
            BlockPos origin = getBindingCenter(stack);

            for (int i = 0; i < list.size(); i++) {
                if (origin.getY() != Integer.MIN_VALUE) {
                    list.set(i, list.get(i).offset(origin));
                } else {
                    list.set(i, list.get(i).offset(((BlockHitResult) lookPos).getBlockPos()));
                }
            }

            return list;
        }

        return ImmutableList.of();
    }

    private static BlockPos getBindingCenter(ItemStack stack) {
        int x = NBTUtils.getInt(stack, TAG_X_ORIGIN, 0);
        int y = NBTUtils.getInt(stack, TAG_Y_ORIGIN, Integer.MIN_VALUE);
        int z = NBTUtils.getInt(stack, TAG_Z_ORIGIN, 0);

        return new BlockPos(x, y, z);
    }

}