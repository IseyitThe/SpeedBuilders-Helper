package me.seyit.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Util;

import java.util.OptionalDouble;
import java.util.function.Function;

import static me.seyit.render.Pipelines.GLOBAL_LINES_PIPELINE;
import static me.seyit.render.Pipelines.GLOBAL_QUADS_PIPELINE;

public class Layers {

    private static final RenderLayer GLOBAL_QUADS = RenderLayer.of(
            "speedbuilders_fill", 156, GLOBAL_QUADS_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder().build(false));

    private static final Function<Double, RenderLayer> GLOBAL_LINES = Util
            .memoize(width -> RenderLayer.of("speedbuilders_lines", 156, GLOBAL_LINES_PIPELINE,
                    RenderLayer.MultiPhaseParameters.builder()
                            .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(width)))
                            .build(false)));

    public static RenderLayer getGlobalQuads() {
        return GLOBAL_QUADS;
    }

    public static RenderLayer getGlobalLines(double width) {
        return GLOBAL_LINES.apply(width);
    }
}
