package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.playerapi.PlayerAPIBridge;

public class MessageConfigChangeServer implements IMessage
{
    public MessageConfigChangeServer()
    {
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    public static class Handler implements IMessageHandler<MessageConfigChangeServer, IMessage>
    {
        @Override
        public IMessage onMessage(MessageConfigChangeServer message, MessageContext context)
        {
            SmartMovingMod.PROXY.processConfigChangePacket(PlayerAPIBridge.getServerPlayerBase(context.getServerHandler().player));
            return null;
        }
    }
}
