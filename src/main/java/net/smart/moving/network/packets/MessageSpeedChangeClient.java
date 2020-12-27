package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;

public class MessageSpeedChangeClient implements IMessage
{
    private int difference;
    private String username;

    public MessageSpeedChangeClient()
    {
    }

    public MessageSpeedChangeClient(int difference, String username)
    {
        this.difference = difference;
        this.username = username;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.difference = ByteBufUtils.readVarInt(buffer, 4);
        this.username = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeVarInt(buf, this.difference, 4);
        ByteBufUtils.writeUTF8String(buf, this.username);
    }

    public int getDifference()
    {
        return this.difference;
    }

    public String getUsername()
    {
        return this.username;
    }

    public static class Handler implements IMessageHandler<MessageSpeedChangeClient, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSpeedChangeClient message, MessageContext context)
        {
            SmartMovingMod.PROXY.processSpeedChangePacket(null, message.difference, message.username);
            return null;
        }
    }
}
