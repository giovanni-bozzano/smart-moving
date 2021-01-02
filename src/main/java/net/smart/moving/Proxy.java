package net.smart.moving;

import net.smart.moving.network.MessageHandler;
import net.smart.moving.playerapi.CustomServerPlayerEntityBase;

public class Proxy
{
    public void preInitialize()
    {
    }

    public void initialize()
    {
        CustomServerPlayerEntityBase.registerPlayerBase();
        MessageHandler.registerPackets();
    }

    public void processStatePacket(CustomServerPlayerEntityBase player, int entityId, long state)
    {
        player.getController().processStatePacket(entityId, state);
    }

    public void processHungerChangePacket(CustomServerPlayerEntityBase player, float hunger)
    {
        player.getController().processHungerChangePacket(hunger);
    }

    public void processSoundPacket(CustomServerPlayerEntityBase player, String soundId, float volume, float pitch)
    {
        player.getController().processSoundPacket(soundId, volume, pitch);
    }
}
