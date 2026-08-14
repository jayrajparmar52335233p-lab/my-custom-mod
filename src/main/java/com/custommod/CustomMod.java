package com.custommod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.command.argument.EntityArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class CustomMod implements ModInitializer {
    public static final String MOD_ID = "custommod";
    public static boolean dispenserPlaceBlocks = true;

    @Override
    public void onInitialize() {
        registerDispenserBehaviors();
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
}
