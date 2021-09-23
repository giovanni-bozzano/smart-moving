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

import api.player.model.ModelPlayerAPI;
import api.player.model.ModelPlayerBase;
import net.minecraft.client.model.ModelRenderer;
import net.smart.moving.render.CustomModel;
import net.smart.moving.render.IModelPlayer;
import net.smart.render.playerapi.SmartRender;

public class CustomModelPlayerBase extends ModelPlayerBase implements IModelPlayer
{
    private CustomModel model;

    public CustomModelPlayerBase(ModelPlayerAPI modelplayerapi)
    {
        super(modelplayerapi);
    }

    @Override
    public CustomModel getMovingModel()
    {
        if (this.model == null)
        {
            this.model = new CustomModel(SmartRender.getPlayerBase(this.modelBiped), this);
        }
        return this.model;
    }

    public void dynamicOverrideAnimateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateSleeping(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateRiding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateLeftArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateRightArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateWorkingArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateSneaking(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    public void dynamicOverrideAnimateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.getMovingModel().animateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    @Override
    public void superAnimateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateHeadRotation", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateSleeping", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateArmSwinging", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateRiding", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateLeftArmItemHolding", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateRightArmItemHolding", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateWorkingBody", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateWorkingArms", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateSneaking", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superApplyAnimationOffsets(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateArms", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Override
    public void superAnimateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        super.dynamic("animateBowAiming", new Object[]{totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor});
    }

    @Deprecated
    public ModelRenderer getOuter()
    {
        return this.getMovingModel().renderModel.bipedOuter;
    }

    @Deprecated
    public ModelRenderer getTorso()
    {
        return this.getMovingModel().renderModel.bipedTorso;
    }

    @Deprecated
    public ModelRenderer getBody()
    {
        return this.getMovingModel().renderModel.bipedBody;
    }

    @Deprecated
    public ModelRenderer getBreast()
    {
        return this.getMovingModel().renderModel.bipedBreast;
    }

    @Deprecated
    public ModelRenderer getNeck()
    {
        return this.getMovingModel().renderModel.bipedNeck;
    }

    @Deprecated
    public ModelRenderer getHead()
    {
        return this.getMovingModel().renderModel.bipedHead;
    }

    @Deprecated
    public ModelRenderer getHeadwear()
    {
        return this.getMovingModel().renderModel.bipedHeadwear;
    }

    @Deprecated
    public ModelRenderer getRightShoulder()
    {
        return this.getMovingModel().renderModel.bipedRightShoulder;
    }

    @Deprecated
    public ModelRenderer getRightArm()
    {
        return this.getMovingModel().renderModel.bipedRightArm;
    }

    @Deprecated
    public ModelRenderer getLeftShoulder()
    {
        return this.getMovingModel().renderModel.bipedLeftShoulder;
    }

    @Deprecated
    public ModelRenderer getLeftArm()
    {
        return this.getMovingModel().renderModel.bipedLeftArm;
    }

    @Deprecated
    public ModelRenderer getPelvic()
    {
        return this.getMovingModel().renderModel.bipedPelvic;
    }

    @Deprecated
    public ModelRenderer getRightLeg()
    {
        return this.getMovingModel().renderModel.bipedRightLeg;
    }

    @Deprecated
    public ModelRenderer getLeftLeg()
    {
        return this.getMovingModel().renderModel.bipedLeftLeg;
    }

    @Deprecated
    public ModelRenderer getEars()
    {
        return this.getMovingModel().renderModel.bipedEars;
    }

    @Deprecated
    public ModelRenderer getCloak()
    {
        return this.getMovingModel().renderModel.bipedCloak;
    }
}