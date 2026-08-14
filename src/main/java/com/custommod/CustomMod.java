package com.custommod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CustomMod implements ModInitializer {
    public static final String MOD_ID = "custommod";
    public static boolean dispenserPlaceBlocks = true;

    @Override
    public void onInitialize() {
        registerDispenserBehaviors();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("dispenser")
                .then(CommandManager.literal("place")
                    .then(CommandManager.literal("block")
                        .then(CommandManager.literal("permanent")
                            .then(CommandManager.literal("true").executes(context -> {
                                dispenserPlaceBlocks = true;
                                context.getSource().sendMessage(Text.literal("§a[Red Carpet] Dispenser Placement: TRUE"));
                                return 1;
                            }))
                            .then(CommandManager.literal("false").executes(context -> {
                                dispenserPlaceBlocks = false;
                                context.getSource().sendMessage(Text.literal("§c[Red Carpet] Dispenser Placement: FALSE"));
                                return 1;
                            }))
                        )
                    )
                )
            );
        });
    }

    private void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(Items.DIRT, new ItemDispenserBehavior() {
            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                if (!dispenserPlaceBlocks) {
                    return super.dispenseSilently(pointer, stack);
                }

                ServerWorld world = pointer.world();
                Direction facing = pointer.state().get(DispenserBlock.FACING);
                BlockPos targetPos = pointer.pos().offset(facing);

                if (world.getBlockState(targetPos).isAir() && stack.getItem() instanceof BlockItem blockItem) {
                    world.setBlockState(targetPos, blockItem.getBlock().getDefaultState());
                    stack.decrement(1);
                    return stack;
                }
                return super.dispenseSilently(pointer, stack);
            }
        });
    }

    public static boolean isTreeAbove(ServerWorld world, BlockPos pos) {
        BlockPos dirtPos = pos.up();
        BlockPos logPos = dirtPos.up();
        BlockState logState = world.getBlockState(logPos);
        return logState.isIn(BlockTags.LOGS);
    }
}
