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

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SmartMovingMod.ID, name = SmartMovingMod.NAME, useMetadata = true)
public class SmartMovingMod
{
    public final static String ID = "smartmoving";
    public final static String NAME = "Smart Moving";
    @SidedProxy(clientSide = "net.smart.moving.ProxyClient", serverSide = "net.smart.moving.Proxy")
    public static Proxy PROXY;

    @EventHandler
    public void preInitialize(FMLPreInitializationEvent event)
    {
        PROXY.preInitialize();
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event)
    {
        PROXY.initialize();
    }
}