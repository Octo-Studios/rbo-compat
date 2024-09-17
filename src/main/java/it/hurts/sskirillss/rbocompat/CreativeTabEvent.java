package it.hurts.sskirillss.rbocompat;

import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.StoneOfTemperanceItem;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.relic.RingOfLokiItem;
import vazkii.botania.common.lib.ResourceLocationHelper;

import java.util.function.Predicate;

import static vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem.getLevel;
import static vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem.isEnabled;

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

    public static void breakOtherBlock(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
        if (isEnabled(stack)) {
            Level world = player.level();
            Predicate<BlockState> canMine = (state) -> {
                boolean rightToolForDrops = !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
                boolean rightToolForSpeed = stack.getDestroySpeed(state) > 1.0F || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_HOE);
                return rightToolForDrops && rightToolForSpeed;
            };
            BlockState targetState = world.getBlockState(pos);
            if (canMine.test(targetState)) {
                if (!world.isEmptyBlock(pos)) {

                    int origLevel = getLevel(stack);
                    int level = origLevel;

                    if (StoneOfTemperanceItem.hasTemperanceActive(player) && level > 2)
                        level = 2;
                    boolean doY = side.getStepY() == 0;

                    int range = level - 1;
                    if (range != 0 || level == 1) {
                        int rangeX = (player.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("GetXPos") / 2);
                        int rangeY = side.getStepY() == 0 ? ((player.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("GetYPos") / 2) * 2 - 1) : 0;
                        int rangeZ = player.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("GetZPos");
                        Vec3i beginDiff, endDiff;
                        System.out.println(side);
                        switch (side) {
                            case NORTH:
                                beginDiff = new Vec3i(-rangeX, doY ? -1 : 0, 0);
                                endDiff = new Vec3i(rangeX, rangeY, rangeZ);
                                break;
                            case SOUTH:
                                beginDiff = new Vec3i(-rangeX, doY ? -1 : 0, 0);
                                endDiff = new Vec3i(rangeX, rangeY, -rangeZ);
                                break;
                            case WEST:
                                beginDiff = new Vec3i(0, doY ? -1 : 0, -rangeX);
                                endDiff = new Vec3i(rangeZ, rangeY, rangeX);
                                break;
                            case EAST:
                                beginDiff = new Vec3i(0, doY ? -1 : 0, -rangeX);
                                endDiff = new Vec3i(-rangeZ, rangeY, rangeX);
                                break;
                            default:
                                beginDiff = new Vec3i(-rangeX, 0, -rangeZ);
                                endDiff = new Vec3i(rangeX, 10, rangeZ);
                        }

                        ToolCommons.removeBlocksInIteration(player, stack, world, pos, beginDiff, endDiff, canMine);
                        if (origLevel == 5) {
                            PlayerHelper.grantCriterion((ServerPlayer) player, ResourceLocationHelper.prefix("challenge/rank_ss_pick"), "code_triggered");
                        }

                    }
                }
            }
        }
    }
}
