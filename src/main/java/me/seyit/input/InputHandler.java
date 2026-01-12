package me.seyit.input;

import me.seyit.SpeedBuildersHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InputHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(InputHandler::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null)
            return;

        while (KeyBindings.toggleKey.wasPressed()) {
            SpeedBuildersHelper.toggle();
            String status = SpeedBuildersHelper.isEnabled() ? "§aEnabled" : "§cDisabled";
            client.player.sendMessage(Text.literal("§7[§bSpeedBuilders§7] " + status), true);
        }

        while (KeyBindings.saveKey.wasPressed()) {
            scanAndSaveBlocks(client);
        }
    }

    private static void scanAndSaveBlocks(MinecraftClient client) {
        SpeedBuildersHelper.clearBlocks();

        World world = client.world;
        BlockPos playerPos = client.player.getBlockPos();
        int radius = SpeedBuildersHelper.getScanRadius();
        int count = 0;

        int minX = playerPos.getX() - radius;
        int maxX = playerPos.getX() + radius;
        int minZ = playerPos.getZ() - radius;
        int maxZ = playerPos.getZ() + radius;
        int minY = playerPos.getY();
        int maxY = playerPos.getY() + radius;

        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (!state.isAir()) {
                        SpeedBuildersHelper.addBlock(new SpeedBuildersHelper.SavedBlock(state, pos));
                        count++;
                    }
                }
            }
        }

        client.player.sendMessage(Text.literal("§7[§bSpeedBuilders§7] §aSaved §f" + count + "§a blocks"), true);
    }
}
