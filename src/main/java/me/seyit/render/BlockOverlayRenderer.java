package me.seyit.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import me.seyit.SpeedBuildersHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class BlockOverlayRenderer {

    private static final int GREEN_FILL = 0x3200FF80;
    private static final int GREEN_OUTLINE = 0x9600FF80;
    private static final int RED_FILL = 0x32FF0000;
    private static final int RED_OUTLINE = 0x96FF0000;
    private static final float EPSILON = 1e-3f;

    public static void register() {
        WorldRenderEvents.LAST.register(BlockOverlayRenderer::onWorldRender);
    }

    private static void onWorldRender(WorldRenderContext context) {
        if (!SpeedBuildersHelper.isEnabled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null)
            return;

        MatrixStack matrices = context.matrixStack();

        for (SpeedBuildersHelper.SavedBlock savedBlock : SpeedBuildersHelper.getSavedBlocks()) {
            BlockPos pos = savedBlock.getPos();
            var currentState = client.world.getBlockState(pos);
            var savedState = savedBlock.getState();

            if (currentState.isAir()) {
                drawBox(matrices, new Box(pos), GREEN_FILL, GREEN_OUTLINE);
            } else if (!currentState.equals(savedState)) {
                drawBox(matrices, new Box(pos), RED_FILL, RED_OUTLINE);
            }
        }

        BlockPos playerPos = client.player.getBlockPos();
        int radius = SpeedBuildersHelper.getScanRadius();

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
                    var currentState = client.world.getBlockState(pos);

                    if (!currentState.isAir() && SpeedBuildersHelper.getSavedBlockAt(pos) == null) {
                        drawBox(matrices, new Box(pos), RED_FILL, RED_OUTLINE);
                    }
                }
            }
        }
    }

    private static void drawBox(MatrixStack stack, Box box, int fillColor, int outlineColor) {
        drawBoxFilled(stack, box, fillColor);
        drawBoxLines(stack, box, outlineColor);
    }

    private static void drawBoxFilled(MatrixStack stack, Box box, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Camera camera = mc.getEntityRenderDispatcher().camera;
        Vec3d camPos = camera.getPos();

        if (box.contains(camPos))
            return;

        float minX = (float) (box.minX - camPos.x);
        float minY = (float) (box.minY - camPos.y);
        float minZ = (float) (box.minZ - camPos.z);
        float maxX = (float) (box.maxX - camPos.x);
        float maxY = (float) (box.maxY - camPos.y);
        float maxZ = (float) (box.maxZ - camPos.z);

        boolean drawNorth = camPos.z < box.minZ - EPSILON;
        boolean drawSouth = camPos.z > box.maxZ + EPSILON;
        boolean drawWest = camPos.x < box.minX - EPSILON;
        boolean drawEast = camPos.x > box.maxX + EPSILON;
        boolean drawDown = camPos.y < box.minY - EPSILON;
        boolean drawUp = camPos.y > box.maxY + EPSILON;

        if (!drawDown && !drawUp && !drawNorth && !drawSouth && !drawWest && !drawEast)
            return;

        BufferBuilder buf = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = stack.peek().getPositionMatrix();

        if (drawDown) {
            buf.vertex(matrix, minX, minY, minZ).color(color);
            buf.vertex(matrix, maxX, minY, minZ).color(color);
            buf.vertex(matrix, maxX, minY, maxZ).color(color);
            buf.vertex(matrix, minX, minY, maxZ).color(color);
        }

        if (drawUp) {
            buf.vertex(matrix, minX, maxY, minZ).color(color);
            buf.vertex(matrix, minX, maxY, maxZ).color(color);
            buf.vertex(matrix, maxX, maxY, maxZ).color(color);
            buf.vertex(matrix, maxX, maxY, minZ).color(color);
        }

        if (drawNorth) {
            buf.vertex(matrix, minX, minY, minZ).color(color);
            buf.vertex(matrix, minX, maxY, minZ).color(color);
            buf.vertex(matrix, maxX, maxY, minZ).color(color);
            buf.vertex(matrix, maxX, minY, minZ).color(color);
        }

        if (drawSouth) {
            buf.vertex(matrix, minX, minY, maxZ).color(color);
            buf.vertex(matrix, maxX, minY, maxZ).color(color);
            buf.vertex(matrix, maxX, maxY, maxZ).color(color);
            buf.vertex(matrix, minX, maxY, maxZ).color(color);
        }

        if (drawWest) {
            buf.vertex(matrix, minX, minY, minZ).color(color);
            buf.vertex(matrix, minX, minY, maxZ).color(color);
            buf.vertex(matrix, minX, maxY, maxZ).color(color);
            buf.vertex(matrix, minX, maxY, minZ).color(color);
        }

        if (drawEast) {
            buf.vertex(matrix, maxX, minY, maxZ).color(color);
            buf.vertex(matrix, maxX, minY, minZ).color(color);
            buf.vertex(matrix, maxX, maxY, minZ).color(color);
            buf.vertex(matrix, maxX, maxY, maxZ).color(color);
        }

        Layers.getGlobalQuads().draw(buf.end());
    }

    private static void drawBoxLines(MatrixStack stack, Box box, int color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d camPos = mc.getEntityRenderDispatcher().camera.getPos();

        float minX = (float) (box.minX - camPos.x);
        float minY = (float) (box.minY - camPos.y);
        float minZ = (float) (box.minZ - camPos.z);
        float maxX = (float) (box.maxX - camPos.x);
        float maxY = (float) (box.maxY - camPos.y);
        float maxZ = (float) (box.maxZ - camPos.z);

        BufferBuilder buffer = Tessellator.getInstance()
                .begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        VertexRendering.drawBox(stack, buffer, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);

        Layers.getGlobalLines(2.0).draw(buffer.end());
    }
}
