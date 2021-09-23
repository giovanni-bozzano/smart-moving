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
package net.smart.moving.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.smart.moving.Controller;
import net.smart.moving.SmartMovingConfig;
import net.smart.moving.playerapi.Factory;
import net.smart.moving.render.playerapi.CustomRenderPlayerBase;
import net.smart.render.statistics.SmartStatistics;
import net.smart.render.statistics.SmartStatisticsFactory;

public class CustomRender extends ContextRender
{
    public static CustomModel currentMainModel;
    public CustomRenderPlayerBase customRenderPlayerBase;
    public final CustomModel modelBipedMain;

    public CustomRender(CustomRenderPlayerBase customRenderPlayerBase)
    {
        this.customRenderPlayerBase = customRenderPlayerBase;

        this.modelBipedMain = customRenderPlayerBase.getMovingModelBipedMain().getMovingModel();
        CustomModel modelArmorChestplate = customRenderPlayerBase.getMovingModelArmorChestplate().getMovingModel();
        CustomModel modelArmor = customRenderPlayerBase.getMovingModelArmor().getMovingModel();

        this.modelBipedMain.scaleArmType = SCALE;
        this.modelBipedMain.scaleLegType = SCALE;
        modelArmorChestplate.scaleArmType = NO_SCALE_START;
        modelArmorChestplate.scaleLegType = NO_SCALE_END;
        modelArmor.scaleArmType = NO_SCALE_START;
        modelArmor.scaleLegType = SCALE;
    }

    public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
    {
        IModelPlayer[] modelPlayers = null;
        Controller moving = Factory.getInstance().getPlayerInstance(entityplayer);
        if (moving != null)
        {
            boolean isInventory = d == 0.0F && d1 == 0.0F && d2 == 0.0F && f == 0.0F && renderPartialTicks == 1.0F;

            boolean isClimb = moving.isClimbing && !moving.isCrawling && !moving.isCrawlClimbing && !moving.isClimbJumping;
            boolean isClimbJump = moving.isClimbJumping;
            int handsClimbType = moving.actualHandsClimbType;
            int feetClimbType = moving.actualFeetClimbType;
            boolean isHandsVineClimbing = moving.isHandsVineClimbing;
            boolean isFeetVineClimbing = moving.isFeetVineClimbing;
            boolean isCeilingClimb = moving.isCeilingClimbing;
            boolean isSwim = moving.isSwimming && !moving.isDipping;
            boolean isDive = moving.isDiving;
            boolean isLevitate = moving.isLevitating;
            boolean isCrawl = moving.isCrawling && !moving.isClimbing;
            boolean isCrawlClimb = moving.isCrawlClimbing || (moving.isClimbing && moving.isCrawling);
            boolean isJump = moving.isJumping();
            boolean isHeadJump = moving.isHeadJumping;
            boolean isFlying = moving.doFlyingAnimation();
            boolean isSlide = moving.isSliding;
            boolean isFalling = moving.doFallingAnimation();
            boolean isGenericSneaking = moving.isSlow;
            boolean isAngleJumping = moving.isAngleJumping();
            int angleJumpType = moving.angleJumpType;
            boolean isRopeSliding = moving.isRopeSliding;

            SmartStatistics statistics = SmartStatisticsFactory.getInstance(entityplayer);
            float currentHorizontalSpeedFlattened = statistics != null ? statistics.getCurrentHorizontalSpeedFlattened(renderPartialTicks, -1) : Float.NaN;
            float smallOverGroundHeight = isCrawlClimb || isHeadJump ? (float) moving.getOverGroundHeight(5D) : 0F;
            Block overGroundBlock = isHeadJump && smallOverGroundHeight < 5F ? moving.getOverGroundBlockId(smallOverGroundHeight) : null;

            modelPlayers = this.customRenderPlayerBase.getMovingModels();

            for (IModelPlayer player : modelPlayers)
            {
                CustomModel modelPlayer = player.getMovingModel();
                modelPlayer.isClimb = isClimb;
                modelPlayer.isClimbJump = isClimbJump;
                modelPlayer.handsClimbType = handsClimbType;
                modelPlayer.feetClimbType = feetClimbType;
                modelPlayer.isHandsVineClimbing = isHandsVineClimbing;
                modelPlayer.isFeetVineClimbing = isFeetVineClimbing;
                modelPlayer.isCeilingClimb = isCeilingClimb;
                modelPlayer.isSwim = isSwim;
                modelPlayer.isDive = isDive;
                modelPlayer.isCrawl = isCrawl;
                modelPlayer.isCrawlClimb = isCrawlClimb;
                modelPlayer.isJump = isJump;
                modelPlayer.isHeadJump = isHeadJump;
                modelPlayer.isSlide = isSlide;
                modelPlayer.isFlying = isFlying;
                modelPlayer.isLevitate = isLevitate;
                modelPlayer.isFalling = isFalling;
                modelPlayer.isGenericSneaking = isGenericSneaking;
                modelPlayer.isAngleJumping = isAngleJumping;
                modelPlayer.angleJumpType = angleJumpType;
                modelPlayer.isRopeSliding = isRopeSliding;

                modelPlayer.currentHorizontalSpeedFlattened = currentHorizontalSpeedFlattened;
                modelPlayer.smallOverGroundHeight = smallOverGroundHeight;
                modelPlayer.overGroundBlock = overGroundBlock;
            }

            if (!isInventory && entityplayer.isSneaking() && !(entityplayer instanceof EntityPlayerSP) && isCrawl)
            {
                d1 += 0.125D;
            }
        }

        currentMainModel = this.modelBipedMain;
        this.customRenderPlayerBase.superRenderDoRender(entityplayer, d, d1, d2, f, renderPartialTicks);
        currentMainModel = null;

        if (moving != null && moving.isLevitating)
        {
            for (IModelPlayer modelPlayer : modelPlayers)
            {
                modelPlayer.getMovingModel().renderModel.currentHorizontalAngle = modelPlayer.getMovingModel().renderModel.currentCameraAngle;
            }
        }
    }

    public void rotateCorpse(AbstractClientPlayer player, float totalTime, float actualRotation, float f2)
    {
        Controller moving = Factory.getInstance().getPlayerInstance(player);
        if (moving != null)
        {
            boolean isInventory = f2 == 1.0F && moving.playerBase != null && moving.playerBase.getMcField().currentScreen instanceof GuiInventory;
            if (!isInventory)
            {
                float forwardRotation = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f2;

                if (moving.isClimbing || moving.isClimbCrawling || moving.isCrawlClimbing || moving.isFlying || moving.isSwimming || moving.isDiving || moving.isCeilingClimbing || moving.isHeadJumping || moving.isSliding || moving.isAngleJumping())
                {
                    player.renderYawOffset = forwardRotation;
                }
            }
        }
        this.customRenderPlayerBase.superRenderRotateCorpse(player, totalTime, actualRotation, f2);
    }

    public void renderLivingAt(AbstractClientPlayer player, double d, double d1, double d2)
    {
        if (player instanceof EntityOtherPlayerMP)
        {
            Controller moving = Factory.getInstance().getOtherSmartMoving(player.getEntityId());
            if (moving != null && moving.heightOffset != 0)
            {
                d1 += moving.heightOffset;
            }
        }
        this.customRenderPlayerBase.superRenderRenderLivingAt(player, d, d1, d2);
    }

    public void renderName(AbstractClientPlayer player, double d, double d1, double d2)
    {
        boolean changedIsSneaking = false, originalIsSneaking = false;
        if (Minecraft.isGuiEnabled() && player != this.customRenderPlayerBase.getMovingRenderManager().pointedEntity)
        {
            Controller moving = Factory.getInstance().getPlayerInstance(player);
            if (moving != null)
            {
                originalIsSneaking = player.isSneaking();
                boolean temporaryIsSneaking = originalIsSneaking;
                if (moving.isCrawling && !moving.isClimbing)
                {
                    temporaryIsSneaking = !SmartMovingConfig.CRAWLING.name;
                }
                else if (originalIsSneaking)
                {
                    temporaryIsSneaking = !SmartMovingConfig.GENERIC_SNEAKING.name;
                }

                changedIsSneaking = temporaryIsSneaking != originalIsSneaking;
                if (changedIsSneaking)
                {
                    player.setSneaking(temporaryIsSneaking);
                }

                if (moving.heightOffset == -1)
                {
                    d1 -= 0.2F;
                }
                else if (originalIsSneaking && !temporaryIsSneaking)
                {
                    d1 -= 0.05F;
                }
            }
        }

        this.customRenderPlayerBase.superRenderRenderName(player, d, d1, d2);

        if (changedIsSneaking)
        {
            player.setSneaking(originalIsSneaking);
        }
    }
}