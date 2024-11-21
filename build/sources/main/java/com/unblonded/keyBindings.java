package com.unblonded;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;


public class keyBindings {
    static String CATEGORY = "Unblonded's Mods";
    public static KeyBinding macroKey;

    public static void register() {
        macroKey = new KeyBinding("Toggle Farming Macro", Keyboard.KEY_M, CATEGORY);
        ClientRegistry.registerKeyBinding(macroKey);
    }
}
