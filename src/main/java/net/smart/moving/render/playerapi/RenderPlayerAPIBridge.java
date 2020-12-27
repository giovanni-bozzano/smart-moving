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
package net.smart.moving.render.playerapi;

import api.player.model.IModelPlayerAPI;
import api.player.model.ModelPlayerAPI;
import api.player.model.ModelPlayerBaseSorting;
import api.player.render.RenderPlayerAPI;
import api.player.render.RenderPlayerBaseSorting;
import net.minecraft.client.model.ModelBiped;
import net.smart.moving.SmartMovingMod;
import net.smart.render.SmartRenderMod;

public abstract class RenderPlayerAPIBridge
{
    public static void register()
    {
        String[] inferiors = new String[]{SmartRenderMod.ID};

        RenderPlayerBaseSorting renderSorting = new RenderPlayerBaseSorting();
        renderSorting.setAfterLocalConstructingInferiors(inferiors);
        renderSorting.setOverrideDoRenderInferiors(inferiors);
        renderSorting.setOverrideRotateCorpseInferiors(inferiors);
        renderSorting.setOverrideRenderLivingAtInferiors(inferiors);
        RenderPlayerAPI.register(SmartMovingMod.ID, CustomRenderPlayerBase.class, renderSorting);

        ModelPlayerBaseSorting modelSorting = new ModelPlayerBaseSorting();
        modelSorting.setAfterLocalConstructingInferiors(inferiors);
        ModelPlayerAPI.register(SmartMovingMod.ID, CustomModelPlayerBase.class, modelSorting);
    }

    public static CustomModelPlayerBase getPlayerBase(ModelBiped modelPlayer)
    {
        return (CustomModelPlayerBase) ((IModelPlayerAPI) modelPlayer).getModelPlayerBase(SmartMovingMod.ID);
    }
}