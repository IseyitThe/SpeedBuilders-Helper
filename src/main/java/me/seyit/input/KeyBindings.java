package me.seyit.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyBinding toggleKey;
    public static KeyBinding saveKey;

    public static void register() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle SpeedBuilders Helper",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "SpeedBuilders Helper"));

        saveKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Save Build",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "SpeedBuilders Helper"));
    }
}
