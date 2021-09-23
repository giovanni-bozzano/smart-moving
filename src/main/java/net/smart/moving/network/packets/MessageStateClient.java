package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;

public class MessageStateClient implements IMessage
{
    private int entityId;
    private NBTTagCompound state;

    public MessageStateClient()
    {
    }

    public MessageStateClient(int entityId, NBTTagCompound state)
    {
        this.entityId = entityId;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.entityId = ByteBufUtils.readVarInt(buffer, 4);
        this.state = ByteBufUtils.readTag(buffer);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeVarInt(buf, this.entityId, 4);
        ByteBufUtils.writeTag(buf, this.state);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public NBTTagCompound getState()
    {
        return this.state;
    }

    public static class Handler implements IMessageHandler<MessageStateClient, IMessage>
    {
        @Override
        public IMessage onMessage(MessageStateClient message, MessageContext context)
        {
            SmartMovingMod.PROXY.processStatePacket(null, message.entityId, message.state);
            return null;
        }
    }
}