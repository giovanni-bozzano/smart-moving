package net.smart.moving.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.playerapi.PlayerAPIBridge;

public class MessageSoundServer implements IMessage
{
    private String soundId;
    private float volume;
    private float pitch;

    public MessageSoundServer()
    {
    }

    public MessageSoundServer(String soundId, float volume, float pitch)
    {
        this.soundId = soundId;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.soundId = ByteBufUtils.readUTF8String(buffer);
        this.volume = Float.parseFloat(ByteBufUtils.readUTF8String(buffer));
        this.pitch = Float.parseFloat(ByteBufUtils.readUTF8String(buffer));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.soundId);
        ByteBufUtils.writeUTF8String(buf, Float.toString(this.volume));
        ByteBufUtils.writeUTF8String(buf, Float.toString(this.pitch));
    }

    public String getSoundId()
    {
        return this.soundId;
    }

    public float getVolume()
    {
        return this.volume;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public static class Handler implements IMessageHandler<MessageSoundServer, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSoundServer message, MessageContext context)
        {
            SmartMovingMod.PROXY.processSoundPacket(PlayerAPIBridge.getServerPlayerBase(context.getServerHandler().player), message.soundId, message.volume, message.pitch);
            return null;
        }
    }
}
