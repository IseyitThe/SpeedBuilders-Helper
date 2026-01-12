package me.seyit;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class AutoSwitchHandler {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> onHudRender());
    }

    private static void onHudRender() {
        if (!SpeedBuildersHelper.isEnabled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null)
            return;

        HitResult hitResult = client.crosshairTarget;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK)
            return;

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos().offset(blockHit.getSide());

        SpeedBuildersHelper.SavedBlock savedBlock = SpeedBuildersHelper.getSavedBlockAt(targetPos);
        if (savedBlock == null)
            return;

        if (!client.world.getBlockState(targetPos).isAir())
            return;

        Block targetBlock = savedBlock.getState().getBlock();
        PlayerInventory inventory = client.player.getInventory();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() == targetBlock) {
                    inventory.setSelectedSlot(i);
                    return;
                }
            }
        }
    }
}
