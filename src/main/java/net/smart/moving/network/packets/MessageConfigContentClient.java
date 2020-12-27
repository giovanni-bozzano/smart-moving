package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;

import java.util.HashMap;
import java.util.Map;

public class MessageConfigContentClient implements IMessage
{
    private Map<String, String> content = new HashMap<>();
    private String username;

    public MessageConfigContentClient()
    {
    }

    public MessageConfigContentClient(Map<String, String> content, String username)
    {
        this.content = new HashMap<>(content);
        this.username = username;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        NBTTagCompound nbt = ByteBufUtils.readTag(buffer);
        if (nbt == null) {
            return;
        }
        for (String key : nbt.getKeySet()) {
            this.content.put(key, nbt.getString(key));
        }
        this.username = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Map.Entry<String, String> entry : this.content.entrySet()) {
            nbt.setString(entry.getKey(), entry.getValue());
        }
        ByteBufUtils.writeTag(buf, nbt);
        ByteBufUtils.writeUTF8String(buf, this.username);
    }

    public Map<String, String> getContent()
    {
        return this.content;
    }

    public String getUsername()
    {
        return this.username;
    }

    public static class Handler implements IMessageHandler<MessageConfigContentClient, IMessage>
    {
        @Override
        public IMessage onMessage(MessageConfigContentClient message, MessageContext context)
        {
            SmartMovingMod.PROXY.processConfigContentPacket(null, message.content, message.username);
            return null;
        }
    }
}
