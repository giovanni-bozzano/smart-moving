package net.smart.moving.asm.mixins.net.minecraft.network;

import net.minecraft.network.NetHandlerPlayServer;
import net.smart.moving.asm.interfaces.INetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer implements INetHandlerPlayServer
{
    @Shadow private int floatingTickCount;

    @Override
    public void setFloatingTickCount(int floatingTickCount)
    {
        this.floatingTickCount = floatingTickCount;
    }
}
