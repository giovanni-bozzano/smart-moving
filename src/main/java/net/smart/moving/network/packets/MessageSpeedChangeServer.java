package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.playerapi.PlayerAPIBridge;

public class MessageSpeedChangeServer implements IMessage
{
    private int difference;
    private String username;

    public MessageSpeedChangeServer()
    {
    }

    public MessageSpeedChangeServer(int difference, String username)
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

    public static class Handler implements IMessageHandler<MessageSpeedChangeServer, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSpeedChangeServer message, MessageContext context)
        {
            SmartMovingMod.PROXY.processSpeedChangePacket(PlayerAPIBridge.getServerPlayerBase(context.getServerHandler().player), message.difference, message.username);
            return null;
        }
    }
}
