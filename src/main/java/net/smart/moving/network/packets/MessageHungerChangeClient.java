package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;

public class MessageHungerChangeClient implements IMessage
{
    private float hungerChange;

    public MessageHungerChangeClient()
    {
    }

    public MessageHungerChangeClient(float hungerChange)
    {
        this.hungerChange = hungerChange;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.hungerChange = Float.parseFloat(ByteBufUtils.readUTF8String(buffer));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, Float.toString(this.hungerChange));
    }

    public float getHungerChange()
    {
        return this.hungerChange;
    }

    public static class Handler implements IMessageHandler<MessageHungerChangeClient, IMessage>
    {
        @Override
        public IMessage onMessage(MessageHungerChangeClient message, MessageContext context)
        {
            SmartMovingMod.PROXY.processHungerChangePacket(null, message.hungerChange);
            return null;
        }
    }
}