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

import api.player.render.RenderPlayerAPI;
import api.player.render.RenderPlayerBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.smart.moving.render.CustomRender;
import net.smart.moving.render.IModelPlayer;
import net.smart.moving.render.IRenderPlayer;
import net.smart.render.SmartRenderInstall;
import net.smart.utilities.Reflect;

import java.lang.reflect.Field;

public class CustomRenderPlayerBase extends RenderPlayerBase
{
    public CustomRenderPlayerBase(RenderPlayerAPI renderPlayerAPI)
    {
        super(renderPlayerAPI);
    }

    public CustomRender getRenderModel()
    {
        if (this.render == null) {
            this.render = new CustomRender(this);
        }
        return this.render;
    }

    @Override
    public void doRender(AbstractClientPlayer player, double d, double d1, double d2, float f, float renderPartialTicks)
    {
        this.getRenderModel().doRender(player, d, d1, d2, f, renderPartialTicks);
    }

    public void superRenderDoRender(AbstractClientPlayer player, double d, double d1, double d2, float f, float renderPartialTicks)
    {
        super.doRender(player, d, d1, d2, f, renderPartialTicks);
    }

    @Override
    public void rotateCorpse(AbstractClientPlayer player, float totalTime, float actualRotation, float f2)
    {
        this.getRenderModel().rotateCorpse(player, totalTime, actualRotation, f2);
    }

    public void superRenderRotateCorpse(AbstractClientPlayer player, float totalTime, float actualRotation, float f2)
    {
        super.rotateCorpse(player, totalTime, actualRotation, f2);
    }

    @Override
    public void renderLivingAt(AbstractClientPlayer player, double d, double d1, double d2)
    {
        this.getRenderModel().renderLivingAt(player, d, d1, d2);
    }

    public void superRenderRenderLivingAt(AbstractClientPlayer player, double d, double d1, double d2)
    {
        super.renderLivingAt(player, d, d1, d2);
    }

    @Override
    public void renderName(AbstractClientPlayer player, double d, double d1, double d2)
    {
        this.getRenderModel().renderName(player, d, d1, d2);
    }

    public void superRenderRenderName(AbstractClientPlayer player, double d, double d1, double d2)
    {
        super.renderName(player, d, d1, d2);
    }

    public RenderManager getMovingRenderManager()
    {
        return this.renderPlayerAPI.getRenderManagerField();
    }

    public IModelPlayer getMovingModelArmor()
    {
        return RenderPlayerAPIBridge.getPlayerBase(this.renderPlayer.getMainModel());
    }

    public IModelPlayer getMovingModelArmorChestplate()
    {
        for (Object layer : this.renderPlayerAPI.getLayerRenderersField()) {
            if (layer instanceof LayerArmorBase) {
                return RenderPlayerAPIBridge.getPlayerBase((net.minecraft.client.model.ModelBiped) Reflect.GetField(_modelArmorChestplate, layer));
            }
        }
        return null;
    }

    public IModelPlayer getMovingModelBipedMain()
    {
        for (Object layer : this.renderPlayerAPI.getLayerRenderersField()) {
            if (layer instanceof LayerArmorBase) {
                return RenderPlayerAPIBridge.getPlayerBase((net.minecraft.client.model.ModelBiped) Reflect.GetField(_modelArmor, layer));
            }
        }
        return null;
    }

    public IModelPlayer[] getMovingModels()
    {
        net.minecraft.client.model.ModelBiped[] modelPlayers = api.player.model.ModelPlayerAPI.getAllInstances();
        if (this.allModelPlayers != null && (this.allModelPlayers == modelPlayers || modelPlayers.length == 0 && this.allModelPlayers.length == 0)) {
            return this.allIModelPlayers;
        }

        this.allModelPlayers = modelPlayers;
        this.allIModelPlayers = new IModelPlayer[modelPlayers.length];
        for (int i = 0; i < this.allIModelPlayers.length; i++) {
            this.allIModelPlayers[i] = RenderPlayerAPIBridge.getPlayerBase(this.allModelPlayers[i]);
        }
        return this.allIModelPlayers;
    }

    private net.minecraft.client.model.ModelBiped[] allModelPlayers;
    private IModelPlayer[] allIModelPlayers;
    private CustomRender render;
    private final static Field _modelArmorChestplate = Reflect.GetField(LayerArmorBase.class, SmartRenderInstall.LayerArmorBase_modelArmorLeggings);
    private final static Field _modelArmor = Reflect.GetField(LayerArmorBase.class, SmartRenderInstall.LayerArmorBase_modelArmor);
}