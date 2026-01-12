package me.seyit;

import me.seyit.input.InputHandler;
import me.seyit.input.KeyBindings;
import me.seyit.render.BlockOverlayRenderer;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyBindings.register();
        InputHandler.register();
        BlockOverlayRenderer.register();
        AutoSwitchHandler.register();
    }
}
