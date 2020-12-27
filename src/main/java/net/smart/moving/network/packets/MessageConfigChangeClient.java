package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;

public class MessageConfigChangeClient implements IMessage
{
    public MessageConfigChangeClient()
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

    public static class Handler implements IMessageHandler<MessageConfigChangeClient, IMessage>
    {
        @Override
        public IMessage onMessage(MessageConfigChangeClient message, MessageContext context)
        {
            SmartMovingMod.PROXY.processConfigChangePacket(null);
            return null;
        }
    }
}
