package net.smart.moving;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.smart.moving.network.MessageHandler;
import net.smart.moving.network.packets.MessageConfigInfoServer;
import net.smart.moving.playerapi.CustomServerPlayerEntityBase;
import net.smart.moving.playerapi.Factory;

import java.util.Map;

import static net.smart.moving.ContextBase.*;

public class ProxyClient extends Proxy
{
    @Override
    public void processStatePacket(CustomServerPlayerEntityBase player, int entityId, long state)
    {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);
        if (!(entity instanceof EntityOtherPlayerMP)) {
            return;
        }
        ControllerOther moving = Factory.getInstance().getOtherSmartMoving((EntityOtherPlayerMP) entity);
        if (moving != null) {
            moving.processStatePacket(state);
        }
    }

    @Override
    public void processConfigInfoPacket(CustomServerPlayerEntityBase player, String info)
    {
    }

    @Override
    public void processConfigContentPacket(CustomServerPlayerEntityBase player, Map<String, String> content, String username)
    {
        processConfigPacket(content, false);
    }

    @Override
    public void processConfigChangePacket(CustomServerPlayerEntityBase player)
    {
    }

    @Override
    public void processSpeedChangePacket(CustomServerPlayerEntityBase player, int difference, String username)
    {
        if (difference == 0) {
            net.smart.moving.config.Options.writeNoRightsToChangeSpeedMessageToChat(isConnectedToRemoteServer());
        } else {
            Config.changeSpeed(difference);
            Options.writeServerSpeedMessageToChat(username, Config._globalConfig.getValue());
        }
    }

    @Override
    public void processHungerChangePacket(CustomServerPlayerEntityBase player, float hunger)
    {
        player.localAddExhaustion(hunger);
    }

    @Override
    public void processSoundPacket(CustomServerPlayerEntityBase player, String soundId, float volume, float pitch)
    {
    }

    public static void processConfigPacket(Map<String, String> content, boolean blockCode)
    {
        boolean first = Config != ServerConfig;

        if (content.size() != 0) {
            ServerConfig.loadFromProperties(content, blockCode);
        } else {
            Config = Options;
            return;
        }

        Config = ServerConfig;

        if (!first) {
            return;
        }

        if (!blockCode) {
            MessageHandler.INSTANCE.sendToServer(new MessageConfigInfoServer(net.smart.moving.config.Config._sm_current));
        }
    }

    private static boolean isConnectedToRemoteServer()
    {
        IntegratedServer integratedServer = Minecraft.getMinecraft().getIntegratedServer();
        return FMLCommonHandler.instance().getMinecraftServerInstance() == null || (integratedServer == null || !integratedServer.isSinglePlayer());
    }
}
