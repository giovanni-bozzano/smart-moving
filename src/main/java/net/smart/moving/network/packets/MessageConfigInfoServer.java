package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.playerapi.PlayerAPIBridge;

public class MessageConfigInfoServer implements IMessage
{
    private String info;

    public MessageConfigInfoServer()
    {
    }

    public MessageConfigInfoServer(String info)
    {
        this.info = info;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.info = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.info);
    }

    public String getInfo()
    {
        return this.info;
    }

    public static class Handler implements IMessageHandler<MessageConfigInfoServer, IMessage>
    {
        @Override
        public IMessage onMessage(MessageConfigInfoServer message, MessageContext context)
        {
            SmartMovingMod.PROXY.processConfigInfoPacket(PlayerAPIBridge.getServerPlayerBase(context.getServerHandler().player), message.info);
            return null;
        }
    }
}
