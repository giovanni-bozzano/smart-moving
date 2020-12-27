package api.player.asm.interfaces;

import net.minecraft.network.NetHandlerPlayServer;

public interface IServerPlayerEntityAccessor
{
    NetHandlerPlayServer getNetHandlerPlayServer();
}
