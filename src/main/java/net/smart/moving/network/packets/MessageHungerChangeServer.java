package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.playerapi.PlayerAPIBridge;

public class MessageHungerChangeServer implements IMessage
{
    private float hungerChange;

    public MessageHungerChangeServer()
    {
    }

    public MessageHungerChangeServer(float hungerChange)
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

    public static class Handler implements IMessageHandler<MessageHungerChangeServer, IMessage>
    {
        @Override
        public IMessage onMessage(MessageHungerChangeServer message, MessageContext context)
        {
            SmartMovingMod.PROXY.processHungerChangePacket(PlayerAPIBridge.getServerPlayerBase(context.getServerHandler().player), message.hungerChange);
            return null;
        }
    }
}
