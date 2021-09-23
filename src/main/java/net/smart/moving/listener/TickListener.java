package net.smart.moving.listener;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smart.moving.ContextBase;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class TickListener
{
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        ContextBase.onTickInGame();
    }
}