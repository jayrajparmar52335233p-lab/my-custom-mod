package com.custommod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CustomMod implements ModInitializer {
    public static final String MOD_ID = "custommod";

    @Override
    public void onInitialize() {
        registerDispenserBehaviors();
    }

    private void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(Items.DIRT, new ItemDispenserBehavior() {
            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
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
                    }
