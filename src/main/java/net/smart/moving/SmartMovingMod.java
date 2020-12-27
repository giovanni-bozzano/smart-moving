// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package net.smart.moving;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.smart.moving.config.Config;
import net.smart.moving.listener.GuiEventListener;
import net.smart.moving.network.MessageHandler;
import net.smart.moving.playerapi.CustomServerPlayerEntityBase;
import net.smart.moving.playerapi.Factory;
import net.smart.moving.playerapi.PlayerAPIBridge;
import net.smart.moving.render.playerapi.RenderPlayerAPIBridge;
import net.smart.utilities.Name;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = SmartMovingMod.ID, name = SmartMovingMod.NAME, useMetadata = true)
public class SmartMovingMod
{
    public final static String ID = "smartmoving";
    public final static String NAME = "Smart Moving";
    @SidedProxy(clientSide = "net.smart.moving.ProxyClient", serverSide = "net.smart.moving.Proxy")
    public static Proxy PROXY;
    public static Logger logger;
    private final boolean isClient;
    public final static Name NetServerHandler_ticksForFloatKick = new Name("floatingTickCount", "field_147365_f", "f");
    public final static Name PlayerControllerMP_currentGameType = new Name("currentGameType", "field_78779_k", "k");

    public SmartMovingMod()
    {
        this.isClient = FMLCommonHandler.instance().getSide().isClient();
    }

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        if (this.isClient) {
            MinecraftForge.EVENT_BUS.register(GuiEventListener.class);
            RenderPlayerAPIBridge.register();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MessageHandler.registerPackets();

        if (this.isClient) {
            PlayerAPIBridge.register();
            PROXY.localUserNameProvider = new LocalUserNameProvider();
            this.registerGameTicks();
            Factory.initialize();
            ContextBase.initialize();
        } else {
            SmartMovingServer.initialize(new File("."), FMLCommonHandler.instance().getMinecraftServerInstance().getGameType().getID(), new Config());
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!this.isClient) {
            CustomServerPlayerEntityBase.registerPlayerBase();
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        ContextBase.onTickInGame();
    }

    public void registerGameTicks()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}