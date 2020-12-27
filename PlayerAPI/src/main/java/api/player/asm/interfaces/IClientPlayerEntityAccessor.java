package api.player.asm.interfaces;

import net.minecraft.client.Minecraft;

public interface IClientPlayerEntityAccessor
{
    Minecraft getMinecraft();

    boolean isSleeping();

    boolean isInWeb();

    boolean isJumping();

    void setIsJumping(boolean isJumping);

    void setIsInWeb(boolean isInWeb);

    float getFOVMultiplier();
}
