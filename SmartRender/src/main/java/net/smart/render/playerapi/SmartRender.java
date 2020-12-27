// ==================================================================
// This file is part of Smart Render.
//
// Smart Render is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Render is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Render. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package net.smart.render.playerapi;

import api.player.model.IModelPlayerAPI;
import api.player.model.ModelPlayerAPI;
import api.player.render.IRenderPlayerAPI;
import api.player.render.RenderPlayerAPI;
import net.smart.render.SmartRenderMod;

public abstract class SmartRender
{
    public static void register()
    {
        RenderPlayerAPI.register(SmartRenderMod.ID, SmartRenderRenderPlayerBase.class);
        ModelPlayerAPI.register(SmartRenderMod.ID, SmartRenderModelPlayerBase.class);
    }

    public static SmartRenderRenderPlayerBase getPlayerBase(net.minecraft.client.renderer.entity.RenderPlayer renderPlayer)
    {
        return (SmartRenderRenderPlayerBase) ((IRenderPlayerAPI) renderPlayer).getRenderPlayerBase(SmartRenderMod.ID);
    }

    public static SmartRenderModelPlayerBase getPlayerBase(net.minecraft.client.model.ModelBiped modelPlayer)
    {
        return (SmartRenderModelPlayerBase) ((IModelPlayerAPI) modelPlayer).getModelPlayerBase(SmartRenderMod.ID);
    }
}