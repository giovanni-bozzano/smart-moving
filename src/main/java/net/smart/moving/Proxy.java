package net.smart.moving;

import net.smart.moving.playerapi.CustomServerPlayerEntityBase;

import java.util.Map;

public class Proxy
{
    ILocalUserNameProvider localUserNameProvider = null;

    public void processStatePacket(CustomServerPlayerEntityBase player, int entityId, long state)
    {
        player.getController().processStatePacket(entityId, state);
    }

    public void processConfigInfoPacket(CustomServerPlayerEntityBase player, String info)
    {
        player.getController().processConfigPacket(info);
    }

    public void processConfigContentPacket(CustomServerPlayerEntityBase player, Map<String, String> content, String username)
    {
    }

    public void processConfigChangePacket(CustomServerPlayerEntityBase player)
    {
        player.getController().processConfigChangePacket(this.localUserNameProvider != null ? this.localUserNameProvider.getLocalConfigUserName() : "");
    }

    public void processSpeedChangePacket(CustomServerPlayerEntityBase player, int difference, String username)
    {
        player.getController().processSpeedChangePacket(difference, this.localUserNameProvider != null ? this.localUserNameProvider.getLocalSpeedUserName() : "");
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
