// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package net.smart.moving;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class Button extends ContextBase
{
    public boolean isPressed;
    public boolean wasPressed;
    public boolean startPressed;
    public boolean stopPressed;

    public void update(KeyBinding binding)
    {
        this.update(Minecraft.getMinecraft().inGameHasFocus && isKeyDown(binding, binding.isPressed()));
    }

    public void update(boolean pressed)
    {
        this.wasPressed = this.isPressed;
        this.isPressed = pressed;
        this.startPressed = !this.wasPressed && this.isPressed;
        this.stopPressed = this.wasPressed && !this.isPressed;
    }

    private static boolean isKeyDown(KeyBinding keyBinding, boolean wasDown)
    {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen == null || currentScreen.allowUserInput)
        {
            return GameSettings.isKeyDown(keyBinding);
        }
        return wasDown;
    }
}