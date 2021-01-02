package net.smart.moving;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smart.moving.playerapi.CustomClientPlayerEntityBase;
import net.smart.moving.playerapi.CustomServerPlayerEntityBase;
import net.smart.moving.playerapi.Factory;
import net.smart.moving.render.playerapi.RenderPlayerAPIBridge;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy
{
    @Override
    public void preInitialize()
    {
        RenderPlayerAPIBridge.register();
    }

    @Override
    public void initialize()
    {
        super.initialize();
        CustomClientPlayerEntityBase.registerPlayerBase();
        Factory.initialize();
        ContextBase.initialize();
        Keybinds.register();
    }

    @Override
    public void processStatePacket(CustomServerPlayerEntityBase player, int entityId, long state)
    {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);
        if (!(entity instanceof EntityOtherPlayerMP)) {
            return;
        }
        ControllerOther moving = Factory.getInstance().getOtherSmartMoving((EntityOtherPlayerMP) entity);
        if (moving != null) {
            moving.processStatePacket(state);
        }
    }

    @Override
    public void processHungerChangePacket(CustomServerPlayerEntityBase player, float hunger)
    {
        player.localAddExhaustion(hunger);
    }

    @Override
    public void processSoundPacket(CustomServerPlayerEntityBase player, String soundId, float volume, float pitch)
    {
    }
}
