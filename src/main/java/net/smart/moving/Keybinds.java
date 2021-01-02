package net.smart.moving;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class Keybinds
{
    public static KeyBinding GRAB;

    public static void register()
    {
        GRAB = new KeyBinding("key.climb", Keyboard.KEY_LCONTROL, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(GRAB);
    }
}