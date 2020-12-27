package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.playerapi.PlayerAPIBridge;

public class MessageStateServer implements IMessage
{
    private int entityId;
    private long state;

    public MessageStateServer()
    {
    }

    public MessageStateServer(int entityId, long state)
    {
        this.entityId = entityId;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.entityId = ByteBufUtils.readVarInt(buffer, 4);
        this.state = Long.parseLong(ByteBufUtils.readUTF8String(buffer));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeVarInt(buf, this.entityId, 4);
        ByteBufUtils.writeUTF8String(buf, Long.toString(this.state));
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public long getState()
    {
        return this.state;
    }

    public static class Handler implements IMessageHandler<MessageStateServer, IMessage>
    {
        @Override
        public IMessage onMessage(MessageStateServer message, MessageContext context)
        {
            SmartMovingMod.PROXY.processStatePacket(PlayerAPIBridge.getServerPlayerBase(context.getServerHandler().player), message.entityId, message.state);
            return null;
        }
    }
}
