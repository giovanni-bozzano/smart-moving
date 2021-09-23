package net.smart.moving.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.network.packets.*;

public class MessageHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SmartMovingMod.ID);

    public static void registerPackets()
    {
        int id = 0;
        MessageHandler.INSTANCE.registerMessage(MessageHungerChangeServer.Handler.class, MessageHungerChangeServer.class, id++, Side.SERVER);
        MessageHandler.INSTANCE.registerMessage(MessageSoundServer.Handler.class, MessageSoundServer.class, id++, Side.SERVER);
        MessageHandler.INSTANCE.registerMessage(MessageStateServer.Handler.class, MessageStateServer.class, id++, Side.SERVER);

        MessageHandler.INSTANCE.registerMessage(MessageHungerChangeClient.Handler.class, MessageHungerChangeClient.class, id++, Side.CLIENT);
        MessageHandler.INSTANCE.registerMessage(MessageStateClient.Handler.class, MessageStateClient.class, id, Side.CLIENT);
    }
}