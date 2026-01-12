package me.seyit;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpeedBuildersHelper {

    private static final List<SavedBlock> SAVED_BLOCKS = new CopyOnWriteArrayList<>();
    private static boolean enabled = false;
    private static int scanRadius = 15;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static List<SavedBlock> getSavedBlocks() {
        return SAVED_BLOCKS;
    }

    public static void clearBlocks() {
        SAVED_BLOCKS.clear();
    }

    public static void addBlock(SavedBlock block) {
        SAVED_BLOCKS.add(block);
    }

    public static int getScanRadius() {
        return scanRadius;
    }

    public static void setScanRadius(int radius) {
        scanRadius = radius;
    }

    public static SavedBlock getSavedBlockAt(BlockPos pos) {
        for (SavedBlock block : SAVED_BLOCKS) {
            if (block.pos.equals(pos)) {
                return block;
            }
        }
        return null;
    }

    public static class SavedBlock {
        private final BlockState state;
        private final BlockPos pos;

        public SavedBlock(BlockState state, BlockPos pos) {
            this.state = state;
            this.pos = pos;
        }

        public BlockState getState() {
            return state;
        }

        public BlockPos getPos() {
            return pos;
        }
    }
}
