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
import net.minecraft.util.math.MathHelper;
import net.smart.moving.climbing.FeetClimbing;
import net.smart.moving.climbing.HandsClimbing;
import net.smart.render.ModelRotationRenderer;

import static net.smart.render.SmartRenderUtilities.*;

public class CustomModel extends ContextRender
{
    public IModelPlayer modelPlayer;
    public net.minecraft.client.model.ModelBiped modelBiped;
    public net.smart.render.SmartRenderModel renderModel;

    public CustomModel(net.smart.render.IModelPlayer renderModel, IModelPlayer modelPlayer)
    {
        this.modelPlayer = modelPlayer;
        this.renderModel = renderModel.getRenderModel();
        this.modelBiped = this.renderModel.mp;

        if (CustomRender.CurrentMainModel != null) {
            this.isClimb = CustomRender.CurrentMainModel.isClimb;
            this.isClimbJump = CustomRender.CurrentMainModel.isClimbJump;
            this.handsClimbType = CustomRender.CurrentMainModel.handsClimbType;
            this.feetClimbType = CustomRender.CurrentMainModel.feetClimbType;
            this.isHandsVineClimbing = CustomRender.CurrentMainModel.isHandsVineClimbing;
            this.isFeetVineClimbing = CustomRender.CurrentMainModel.isFeetVineClimbing;
            this.isCeilingClimb = CustomRender.CurrentMainModel.isCeilingClimb;
            this.isSwim = CustomRender.CurrentMainModel.isSwim;
            this.isDive = CustomRender.CurrentMainModel.isDive;
            this.isCrawl = CustomRender.CurrentMainModel.isCrawl;
            this.isCrawlClimb = CustomRender.CurrentMainModel.isCrawlClimb;
            this.isJump = CustomRender.CurrentMainModel.isJump;
            this.isHeadJump = CustomRender.CurrentMainModel.isHeadJump;
            this.isSlide = CustomRender.CurrentMainModel.isSlide;
            this.isFlying = CustomRender.CurrentMainModel.isFlying;
            this.isLevitate = CustomRender.CurrentMainModel.isLevitate;
            this.isFalling = CustomRender.CurrentMainModel.isFalling;
            this.isGenericSneaking = CustomRender.CurrentMainModel.isGenericSneaking;
            this.isAngleJumping = CustomRender.CurrentMainModel.isAngleJumping;
            this.angleJumpType = CustomRender.CurrentMainModel.angleJumpType;
            this.isRopeSliding = CustomRender.CurrentMainModel.isRopeSliding;

            this.currentHorizontalSpeedFlattened = CustomRender.CurrentMainModel.currentHorizontalSpeedFlattened;
            this.smallOverGroundHeight = CustomRender.CurrentMainModel.smallOverGroundHeight;
            this.overGroundBlock = CustomRender.CurrentMainModel.overGroundBlock;
        }
    }

    @SuppressWarnings("unused")
    private void setRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        float FrequenceFactor = 0.6662F;

        this.isStandard = false;

        float currentCameraAngle = this.renderModel.currentCameraAngle;
        float currentHorizontalAngle = this.renderModel.currentHorizontalAngle;
        float currentVerticalAngle = this.renderModel.currentVerticalAngle;
        float forwardRotation = this.renderModel.forwardRotation;
        float currentVerticalSpeed = this.renderModel.currentVerticalSpeed;
        float totalVerticalDistance = this.renderModel.totalVerticalDistance;
        float totalDistance = this.renderModel.totalDistance;
        double horizontalDistance = this.renderModel.horizontalDistance;
        float currentSpeed = this.renderModel.currentSpeed;
        if (!Float.isNaN(this.currentHorizontalSpeedFlattened)) {
            currentHorizontalSpeed = this.currentHorizontalSpeedFlattened;
        }

        ModelRotationRenderer bipedOuter = this.renderModel.bipedOuter;
        ModelRotationRenderer bipedTorso = this.renderModel.bipedTorso;
        ModelRotationRenderer bipedBody = this.renderModel.bipedBody;
        ModelRotationRenderer bipedBreast = this.renderModel.bipedBreast;
        ModelRotationRenderer bipedHead = this.renderModel.bipedHead;
        ModelRotationRenderer bipedRightShoulder = this.renderModel.bipedRightShoulder;
        ModelRotationRenderer bipedRightArm = this.renderModel.bipedRightArm;
        ModelRotationRenderer bipedLeftShoulder = this.renderModel.bipedLeftShoulder;
        ModelRotationRenderer bipedLeftArm = this.renderModel.bipedLeftArm;
        ModelRotationRenderer bipedPelvic = this.renderModel.bipedPelvic;
        ModelRotationRenderer bipedRightLeg = this.renderModel.bipedRightLeg;
        ModelRotationRenderer bipedLeftLeg = this.renderModel.bipedLeftLeg;

        if (this.isRopeSliding) {
            float time = totalTime * 0.15F;

            bipedHead.rotateAngleZ = Between(-Sixteenth, Sixteenth, Normalize(currentCameraAngle - currentHorizontalAngle));
            bipedHead.rotateAngleX = Eighth;
            bipedHead.rotationPointY = 2F;

            bipedOuter.fadeRotateAngleY = false;
            bipedOuter.rotateAngleY = currentHorizontalAngle;
            bipedTorso.rotateAngleX = Sixteenth + Sixtyfourth * MathHelper.cos(time);

            bipedLeftArm.rotateAngleX = bipedRightArm.rotateAngleX = Half - bipedTorso.rotateAngleX;

            bipedRightArm.rotateAngleZ = Sixteenth + Thirtytwoth;
            bipedLeftArm.rotateAngleZ = -Sixteenth - Thirtytwoth;

            bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY = -2F;

            bipedPelvic.rotateAngleX = bipedTorso.rotateAngleX;

            bipedLeftLeg.rotateAngleZ = -Thirtytwoth;
            bipedRightLeg.rotateAngleZ = Thirtytwoth;

            bipedLeftLeg.rotateAngleX = Sixtyfourth * MathHelper.cos(time + Quarter);
            bipedRightLeg.rotateAngleX = Sixtyfourth * MathHelper.cos(time - Quarter);
        } else if (this.isClimb || this.isCrawlClimb) {
            bipedOuter.rotateAngleY = forwardRotation / RadiantToAngle;

            bipedHead.rotateAngleY = 0.0F;
            bipedHead.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;

            bipedLeftLeg.rotationOrder = ModelRotationRenderer.YZX;
            bipedRightLeg.rotationOrder = ModelRotationRenderer.YZX;

            float handsFrequenceUpFactor, handsDistanceUpFactor, handsDistanceUpOffset, feetFrequenceUpFactor, feetDistanceUpFactor, feetDistanceUpOffset;
            float handsFrequenceSideFactor, handsDistanceSideFactor, handsDistanceSideOffset, feetFrequenceSideFactor, feetDistanceSideFactor, feetDistanceSideOffset;

            int handsClimbType = this.handsClimbType;
            if (this.isHandsVineClimbing && handsClimbType == HandsClimbing.MiddleGrab) {
                handsClimbType = HandsClimbing.UpGrab;
            }

            float verticalSpeed = Math.min(0.5f, currentVerticalSpeed);
            float horizontalSpeed = Math.min(0.5f, currentHorizontalSpeed);

            switch (handsClimbType) {
                case HandsClimbing.MiddleGrab:
                    handsFrequenceSideFactor = FrequenceFactor;
                    handsDistanceSideFactor = 1.0F;
                    handsDistanceSideOffset = 0.0F;

                    handsFrequenceUpFactor = FrequenceFactor;
                    handsDistanceUpFactor = 2F;
                    handsDistanceUpOffset = -Quarter;
                    break;
                case HandsClimbing.UpGrab:
                    handsFrequenceSideFactor = FrequenceFactor;
                    handsDistanceSideFactor = 1.0F;
                    handsDistanceSideOffset = 0.0F;

                    handsFrequenceUpFactor = FrequenceFactor;
                    handsDistanceUpFactor = 2F;
                    handsDistanceUpOffset = -2.5F;
                    break;
                default:
                    handsFrequenceSideFactor = FrequenceFactor;
                    handsDistanceSideFactor = 1.0F;
                    handsDistanceSideOffset = 0.0F;

                    handsFrequenceUpFactor = FrequenceFactor;
                    handsDistanceUpFactor = 0F;
                    handsDistanceUpOffset = -0.5F;
                    break;
            }

            if (this.feetClimbType == HandsClimbing.UpGrab) {
                feetFrequenceUpFactor = FrequenceFactor;
                feetDistanceUpFactor = 0.3F / verticalSpeed;
                feetDistanceUpOffset = -0.3F;

                feetFrequenceSideFactor = FrequenceFactor;
                feetDistanceSideFactor = 0.5F;
                feetDistanceSideOffset = 0.0F;
            } else {
                feetFrequenceUpFactor = FrequenceFactor;
                feetDistanceUpFactor = 0.0F;
                feetDistanceUpOffset = 0.0F;

                feetFrequenceSideFactor = FrequenceFactor;
                feetDistanceSideFactor = 0.0F;
                feetDistanceSideOffset = 0.0F;
            }

            bipedRightArm.rotateAngleX = MathHelper.cos(totalVerticalDistance * handsFrequenceUpFactor + Half) * verticalSpeed * handsDistanceUpFactor + handsDistanceUpOffset;
            bipedLeftArm.rotateAngleX = MathHelper.cos(totalVerticalDistance * handsFrequenceUpFactor) * verticalSpeed * handsDistanceUpFactor + handsDistanceUpOffset;

            bipedRightArm.rotateAngleY = MathHelper.cos(totalHorizontalDistance * handsFrequenceSideFactor + Quarter) * horizontalSpeed * handsDistanceSideFactor + handsDistanceSideOffset;
            bipedLeftArm.rotateAngleY = MathHelper.cos(totalHorizontalDistance * handsFrequenceSideFactor) * horizontalSpeed * handsDistanceSideFactor + handsDistanceSideOffset;

            if (this.isHandsVineClimbing) {
                bipedLeftArm.rotateAngleY *= 1F + handsFrequenceSideFactor;
                bipedRightArm.rotateAngleY *= 1F + handsFrequenceSideFactor;

                bipedLeftArm.rotateAngleY += Eighth;
                bipedRightArm.rotateAngleY -= Eighth;

                this.setArmScales(Math.abs(MathHelper.cos(bipedRightArm.rotateAngleX)), Math.abs(MathHelper.cos(bipedLeftArm.rotateAngleX)));
            }

            if (!this.isFeetVineClimbing) {
                bipedRightLeg.rotateAngleX = MathHelper.cos(totalVerticalDistance * feetFrequenceUpFactor) * feetDistanceUpFactor * verticalSpeed + feetDistanceUpOffset;
                bipedLeftLeg.rotateAngleX = MathHelper.cos(totalVerticalDistance * feetFrequenceUpFactor + Half) * feetDistanceUpFactor * verticalSpeed + feetDistanceUpOffset;
            }

            bipedRightLeg.rotateAngleZ = -(MathHelper.cos(totalHorizontalDistance * feetFrequenceSideFactor) - 1.0F) * horizontalSpeed * feetDistanceSideFactor + feetDistanceSideOffset;
            bipedLeftLeg.rotateAngleZ = -(MathHelper.cos(totalHorizontalDistance * feetFrequenceSideFactor + Quarter) + 1.0F) * horizontalSpeed * feetDistanceSideFactor + feetDistanceSideOffset;

            if (this.isFeetVineClimbing) {
                float total = (MathHelper.cos(totalDistance + Half) + 1) * Thirtytwoth + Sixteenth;
                bipedRightLeg.rotateAngleX = -total;
                bipedLeftLeg.rotateAngleX = -total;

                float difference = Math.max(0, MathHelper.cos(totalDistance - Quarter)) * Sixtyfourth;
                bipedLeftLeg.rotateAngleZ += -difference;
                bipedRightLeg.rotateAngleZ += difference;

                this.setLegScales(Math.abs(MathHelper.cos(bipedRightLeg.rotateAngleX)), Math.abs(MathHelper.cos(bipedLeftLeg.rotateAngleX)));
            }

            if (this.isCrawlClimb) {
                float height = this.smallOverGroundHeight + 0.25F;
                float bodyLength = 0.7F;
                float legLength = 0.55F;

                float bodyAngleX, legAngleX, legAngleZ;
                if (height < bodyLength) {
                    bodyAngleX = Math.max(0, (float) Math.acos(height / bodyLength));
                    legAngleX = Quarter - bodyAngleX;
                    legAngleZ = Thirtytwoth;
                } else if (height < bodyLength + legLength) {
                    bodyAngleX = 0F;
                    legAngleX = Math.max(0, (float) Math.acos((height - bodyLength) / legLength));
                    legAngleZ = Thirtytwoth * (legAngleX / 1.537F);
                } else {
                    bodyAngleX = 0F;
                    legAngleX = 0F;
                    legAngleZ = 0F;
                }

                bipedTorso.rotateAngleX = bodyAngleX;

                bipedRightShoulder.rotateAngleX = -bodyAngleX;
                bipedLeftShoulder.rotateAngleX = -bodyAngleX;

                bipedHead.rotateAngleX = -bodyAngleX;

                bipedRightLeg.rotateAngleX = legAngleX;
                bipedLeftLeg.rotateAngleX = legAngleX;

                bipedRightLeg.rotateAngleZ = legAngleZ;
                bipedLeftLeg.rotateAngleZ = -legAngleZ;
            }

            if (handsClimbType == HandsClimbing.NoGrab && this.feetClimbType != FeetClimbing.NoStep) {
                bipedTorso.rotateAngleX = 0.5F;
                bipedHead.rotateAngleX -= 0.5F;
                bipedPelvic.rotateAngleX -= 0.5F;

                bipedTorso.rotationPointZ = -6.0F;
            }
        } else if (this.isClimbJump) {
            bipedRightArm.rotateAngleX = Half + Sixteenth;
            bipedLeftArm.rotateAngleX = Half + Sixteenth;

            bipedRightArm.rotateAngleZ = -Thirtytwoth;
            bipedLeftArm.rotateAngleZ = Thirtytwoth;
        } else if (this.isCeilingClimb) {
            float distance = totalHorizontalDistance * 0.7F;
            float walkFactor = Factor(currentHorizontalSpeed, 0F, 0.12951545F);
            float standFactor = Factor(currentHorizontalSpeed, 0.12951545F, 0F);
            float horizontalAngle = horizontalDistance < 0.015F ? currentCameraAngle : currentHorizontalAngle;

            bipedLeftArm.rotateAngleX = (MathHelper.cos(distance) * 0.52F + Half) * walkFactor + Half * standFactor;
            bipedRightArm.rotateAngleX = (MathHelper.cos(distance + Half) * 0.52F - Half) * walkFactor - Half * standFactor;

            bipedLeftLeg.rotateAngleX = -MathHelper.cos(distance) * 0.12F * walkFactor;
            bipedRightLeg.rotateAngleX = -MathHelper.cos(distance + Half) * 0.32F * walkFactor;

            float rotateY = MathHelper.cos(distance) * 0.44F * walkFactor;
            bipedOuter.rotateAngleY = rotateY + horizontalAngle;

            bipedRightArm.rotateAngleY = bipedLeftArm.rotateAngleY = -rotateY;
            bipedRightLeg.rotateAngleY = bipedLeftLeg.rotateAngleY = -rotateY;

            bipedHead.rotateAngleY = -rotateY;
        } else if (this.isSwim) {
            float walkFactor = Factor(currentHorizontalSpeed, 0.15679921F, 0.52264464F);
            float sneakFactor = Math.min(Factor(currentHorizontalSpeed, 0, 0.15679921F), Factor(currentHorizontalSpeed, 0.52264464F, 0.15679921F));
            float standFactor = Factor(currentHorizontalSpeed, 0.15679921F, 0F);
            float standSneakFactor = standFactor + sneakFactor;
            float horizontalAngle = horizontalDistance < (this.isGenericSneaking ? 0.005 : 0.015F) ? currentCameraAngle : currentHorizontalAngle;

            bipedHead.rotationOrder = ModelRotationRenderer.YXZ;
            bipedHead.rotateAngleY = MathHelper.cos(totalHorizontalDistance / 2.0F - Quarter) * walkFactor;
            bipedHead.rotateAngleX = -Eighth * standSneakFactor;
            bipedHead.rotationPointZ = -2F;

            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = Quarter - Sixteenth * standSneakFactor;
            bipedOuter.rotateAngleY = horizontalAngle;

            bipedBreast.rotateAngleY = bipedBody.rotateAngleY = MathHelper.cos(totalHorizontalDistance / 2.0F - Quarter) * walkFactor;

            bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

            bipedRightArm.rotateAngleZ = Quarter + Eighth + MathHelper.cos(totalTime * 0.1F) * standSneakFactor * 0.8F;
            bipedLeftArm.rotateAngleZ = -Quarter - Eighth - MathHelper.cos(totalTime * 0.1F) * standSneakFactor * 0.8F;

            bipedRightArm.rotateAngleX = ((totalHorizontalDistance * 0.5F) % Whole - Half) * walkFactor + Sixteenth * standSneakFactor;
            bipedLeftArm.rotateAngleX = ((totalHorizontalDistance * 0.5F + Half) % Whole - Half) * walkFactor + Sixteenth * standSneakFactor;

            bipedRightLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance) * 0.52264464F * walkFactor;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance + Half) * 0.52264464F * walkFactor;

            float rotateFeetAngleZ = Sixteenth * standSneakFactor + MathHelper.cos(totalTime * 0.1F) * 0.4F * (standFactor - sneakFactor);
            bipedRightLeg.rotateAngleZ = rotateFeetAngleZ;
            bipedLeftLeg.rotateAngleZ = -rotateFeetAngleZ;

            if (this.scaleLegType != NoScaleStart) {
                this.setLegScales(
                        1F + (MathHelper.cos(totalTime * 0.1F + Quarter) - 1F) * 0.15F * sneakFactor,
                        1F + (MathHelper.cos(totalTime * 0.1F + Quarter) - 1F) * 0.15F * sneakFactor);
            }

            if (this.scaleArmType != NoScaleStart) {
                this.setArmScales(
                        1F + (MathHelper.cos(totalTime * 0.1F - Quarter) - 1F) * 0.15F * sneakFactor,
                        1F + (MathHelper.cos(totalTime * 0.1F - Quarter) - 1F) * 0.15F * sneakFactor);
            }
        } else if (this.isDive) {
            float distance = totalDistance * 0.7F;
            float walkFactor = Factor(currentSpeed, 0F, 0.15679921F);
            float standFactor = Factor(currentSpeed, 0.15679921F, 0F);
            float horizontalAngle = totalDistance < (this.isGenericSneaking ? 0.005 : 0.015F) ? currentCameraAngle : currentHorizontalAngle;

            bipedHead.rotateAngleX = -Eighth;
            bipedHead.rotationPointZ = -2F;

            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = this.isLevitate ? Quarter - Sixteenth : (this.isJump ? 0F : Quarter - currentVerticalAngle);
            bipedOuter.rotateAngleY = horizontalAngle;

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance) + 1F) * 0.52264464F * walkFactor + Sixteenth * standFactor;
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance + Half) - 1F) * 0.52264464F * walkFactor - Sixteenth * standFactor;

            if (this.scaleLegType != NoScaleStart) {
                this.setLegScales(
                        1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor);
            }

            bipedRightArm.rotateAngleZ = (MathHelper.cos(distance + Half) * 0.52264464F * 2.5F + Quarter) * walkFactor + (Quarter + Eighth) * standFactor;
            bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * 0.52264464F * 2.5F - Quarter) * walkFactor - (Quarter + Eighth) * standFactor;

            if (this.scaleArmType != NoScaleStart) {
                this.setArmScales(
                        1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
                        1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor);
            }
        } else if (this.isCrawl) {
            float distance = totalHorizontalDistance * 1.3F;
            float walkFactor = Factor(this.currentHorizontalSpeedFlattened, 0F, 0.12951545F);
            float standFactor = Factor(this.currentHorizontalSpeedFlattened, 0.12951545F, 0F);

            bipedHead.rotateAngleZ = -viewHorizontalAngelOffset / RadiantToAngle;
            bipedHead.rotateAngleX = -Eighth;
            bipedHead.rotationPointZ = -2F;

            bipedTorso.rotationOrder = ModelRotationRenderer.YZX;
            bipedTorso.rotateAngleX = Quarter - Thirtytwoth;
            bipedTorso.rotationPointY = 3F;
            bipedTorso.rotateAngleZ = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor;
            bipedBody.rotateAngleY = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor;

            bipedRightLeg.rotateAngleX = (MathHelper.cos(distance - Quarter) * Sixtyfourth + Thirtytwoth) * walkFactor + Thirtytwoth * standFactor;
            bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance - Half - Quarter) * Sixtyfourth + Thirtytwoth) * walkFactor + Thirtytwoth * standFactor;

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) + 1F) * 0.25F * walkFactor + Thirtytwoth * standFactor;
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor - Thirtytwoth * standFactor;

            if (this.scaleLegType != NoScaleStart) {
                this.setLegScales(
                        1F + (MathHelper.cos(distance + Quarter - Quarter) - 1F) * 0.25F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter - Quarter) - 1F) * 0.25F * walkFactor);
            }

            bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

            bipedRightArm.rotateAngleX = Half + Eighth;
            bipedLeftArm.rotateAngleX = Half + Eighth;

            bipedRightArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth + Thirtytwoth) * walkFactor + Sixteenth * standFactor;
            bipedLeftArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth - Thirtytwoth) * walkFactor - Sixteenth * standFactor;

            bipedRightArm.rotateAngleY = -Quarter;
            bipedLeftArm.rotateAngleY = Quarter;

            if (this.scaleArmType != NoScaleStart) {
                this.setArmScales(
                        1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.15F * walkFactor);
            }
        } else if (this.isSlide) {
            float distance = totalHorizontalDistance * 0.7F;
            float walkFactor = Factor(currentHorizontalSpeed, 0F, 1F) * 0.8F;

            bipedHead.rotateAngleZ = -viewHorizontalAngelOffset / RadiantToAngle;
            bipedHead.rotateAngleX = -Eighth - Sixteenth;
            bipedHead.rotationPointZ = -2F;

            bipedOuter.fadeRotateAngleY = false;
            bipedOuter.rotateAngleY = currentHorizontalAngle;
            bipedOuter.rotationPointY = 5F;
            bipedOuter.rotateAngleX = Quarter;

            bipedBody.rotationOrder = ModelRotationRenderer.YXZ;
            bipedBody.offsetY = -0.4F;
            bipedBody.rotationPointY = +6.5F;
            bipedBody.rotateAngleX = MathHelper.cos(distance - Eighth) * Sixtyfourth * walkFactor;
            bipedBody.rotateAngleY = MathHelper.cos(distance + Eighth) * Sixtyfourth * walkFactor;

            bipedRightLeg.rotateAngleX = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor + Sixtyfourth;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor + Sixtyfourth;

            bipedRightLeg.rotateAngleZ = Thirtytwoth;
            bipedLeftLeg.rotateAngleZ = -Thirtytwoth;

            bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

            bipedRightArm.rotateAngleX = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor + Half - Sixtyfourth;
            bipedLeftArm.rotateAngleX = MathHelper.cos(distance - Half) * Sixtyfourth * walkFactor + Half - Sixtyfourth;

            bipedRightArm.rotateAngleZ = Sixteenth;
            bipedLeftArm.rotateAngleZ = -Sixteenth;

            bipedRightArm.rotateAngleY = -Quarter;
            bipedLeftArm.rotateAngleY = Quarter;
        } else if (this.isFlying) {
            float distance = totalDistance * 0.08F;
            float walkFactor = Factor(currentSpeed, 0F, 1);
            float standFactor = Factor(currentSpeed, 1F, 0F);
            float time = totalTime * 0.15F;
            float verticalAngle = this.isJump ? Math.abs(currentVerticalAngle) : currentVerticalAngle;
            float horizontalAngle = horizontalDistance < 0.05F ? currentCameraAngle : currentHorizontalAngle;

            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = (Quarter - verticalAngle) * walkFactor;
            bipedOuter.rotateAngleY = horizontalAngle;

            bipedHead.rotateAngleX = -bipedOuter.rotateAngleX / 2F;

            bipedRightArm.rotationOrder = ModelRotationRenderer.XZY;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.XZY;

            bipedRightArm.rotateAngleY = (MathHelper.cos(time) * Sixteenth) * standFactor;
            bipedLeftArm.rotateAngleY = (MathHelper.cos(time) * Sixteenth) * standFactor;

            bipedRightArm.rotateAngleZ = (MathHelper.cos(distance + Half) * Sixtyfourth + (Half - Sixteenth)) * walkFactor + Quarter * standFactor;
            bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * Sixtyfourth - (Half - Sixteenth)) * walkFactor - Quarter * standFactor;

            bipedRightLeg.rotateAngleX = MathHelper.cos(distance) * Sixtyfourth * walkFactor + MathHelper.cos(time + Half) * Sixtyfourth * standFactor;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor + MathHelper.cos(time) * Sixtyfourth * standFactor;

            bipedRightLeg.rotateAngleZ = Sixtyfourth;
            bipedLeftLeg.rotateAngleZ = -Sixtyfourth;
        } else if (this.isHeadJump) {
            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = (Quarter - currentVerticalAngle);
            bipedOuter.rotateAngleY = currentHorizontalAngle;

            bipedHead.rotateAngleX = -bipedOuter.rotateAngleX / 2F;

            float bendFactor = Math.min(Factor(currentVerticalAngle, Quarter, 0), Factor(currentVerticalAngle, -Quarter, 0));
            bipedRightArm.rotateAngleX = bendFactor * -Eighth;
            bipedLeftArm.rotateAngleX = bendFactor * -Eighth;

            bipedRightLeg.rotateAngleX = bendFactor * -Eighth;
            bipedLeftLeg.rotateAngleX = bendFactor * -Eighth;

            float armFactorZ = Factor(currentVerticalAngle, Quarter, -Quarter);
            if (this.overGroundBlock != null && this.overGroundBlock.getBlockState().getBaseState().getMaterial().isSolid()) {
                armFactorZ = Math.min(armFactorZ, this.smallOverGroundHeight / 5F);
            }

            bipedRightArm.rotateAngleZ = Half - Sixteenth + armFactorZ * Eighth;
            bipedLeftArm.rotateAngleZ = Sixteenth - Half - armFactorZ * Eighth;

            float legFactorZ = Factor(currentVerticalAngle, -Quarter, Quarter);
            bipedRightLeg.rotateAngleZ = Sixtyfourth * legFactorZ;
            bipedLeftLeg.rotateAngleZ = -Sixtyfourth * legFactorZ;
        } else if (this.isFalling) {
            float distance = totalDistance * 0.1F;

            bipedRightArm.rotationOrder = ModelRotationRenderer.XZY;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.XZY;

            bipedRightArm.rotateAngleY = (MathHelper.cos(distance + Quarter) * Eighth);
            bipedLeftArm.rotateAngleY = (MathHelper.cos(distance + Quarter) * Eighth);

            bipedRightArm.rotateAngleZ = (MathHelper.cos(distance) * Eighth + Quarter);
            bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * Eighth - Quarter);

            bipedRightLeg.rotateAngleX = (MathHelper.cos(distance + Half + Quarter) * Sixteenth + Thirtytwoth);
            bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance + Quarter) * Sixteenth + Thirtytwoth);

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance) * Sixteenth + Thirtytwoth);
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance) * Sixteenth - Thirtytwoth);
        } else {
            this.isStandard = true;
        }
    }

    private boolean isWorking()
    {
        return this.modelBiped.swingProgress > 0F;
    }

    private void animateAngleJumping()
    {
        float angle = this.angleJumpType * Eighth;
        this.renderModel.bipedPelvic.rotateAngleY -= this.renderModel.bipedOuter.rotateAngleY;
        this.renderModel.bipedPelvic.rotateAngleY += this.renderModel.currentCameraAngle;

        float backness = 1F - Math.abs(angle - Half) / Quarter;
        float leftness = -Math.min(angle - Half, 0F) / Quarter;
        float rightness = Math.max(angle - Half, 0F) / Quarter;

        this.renderModel.bipedLeftLeg.rotateAngleX = Thirtytwoth * (1F + rightness);
        this.renderModel.bipedRightLeg.rotateAngleX = Thirtytwoth * (1F + leftness);
        this.renderModel.bipedLeftLeg.rotateAngleY = -angle;
        this.renderModel.bipedRightLeg.rotateAngleY = -angle;
        this.renderModel.bipedLeftLeg.rotateAngleZ = Thirtytwoth * backness;
        this.renderModel.bipedRightLeg.rotateAngleZ = -Thirtytwoth * backness;

        this.renderModel.bipedLeftLeg.rotationOrder = ModelRotationRenderer.ZXY;
        this.renderModel.bipedRightLeg.rotationOrder = ModelRotationRenderer.ZXY;

        this.renderModel.bipedLeftArm.rotateAngleZ = -Sixteenth * rightness;
        this.renderModel.bipedRightArm.rotateAngleZ = Sixteenth * leftness;

        this.renderModel.bipedLeftArm.rotateAngleX = -Eighth * backness;
        this.renderModel.bipedRightArm.rotateAngleX = -Eighth * backness;
    }

    private void animateNonStandardWorking(float viewVerticalAngelOffset)
    {
        this.renderModel.bipedRightShoulder.ignoreSuperRotation = true;
        this.renderModel.bipedRightShoulder.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;
        this.renderModel.bipedRightShoulder.rotateAngleY = this.renderModel.workingAngle / RadiantToAngle;
        this.renderModel.bipedRightShoulder.rotateAngleZ = Half;
        this.renderModel.bipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;
        this.renderModel.bipedRightArm.reset();
    }

    private void animateNonStandardBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.renderModel.bipedRightShoulder.ignoreSuperRotation = true;
        this.renderModel.bipedRightShoulder.rotateAngleY = this.renderModel.workingAngle / RadiantToAngle;
        this.renderModel.bipedRightShoulder.rotateAngleZ = Half;
        this.renderModel.bipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;

        this.renderModel.bipedLeftShoulder.ignoreSuperRotation = true;
        this.renderModel.bipedLeftShoulder.rotateAngleY = this.renderModel.workingAngle / RadiantToAngle;
        this.renderModel.bipedLeftShoulder.rotateAngleZ = Half;
        this.renderModel.bipedLeftShoulder.rotationOrder = ModelRotationRenderer.ZYX;

        this.renderModel.bipedRightArm.reset();
        this.renderModel.bipedLeftArm.reset();

        float headRotateAngleY = this.renderModel.bipedHead.rotateAngleY;
        float outerRotateAngleY = this.renderModel.bipedOuter.rotateAngleY;
        float headRotateAngleX = this.renderModel.bipedHead.rotateAngleX;

        this.renderModel.bipedHead.rotateAngleY = 0;
        this.renderModel.bipedOuter.rotateAngleY = 0;
        this.renderModel.bipedHead.rotateAngleX = 0;

        this.modelPlayer.superAnimateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

        this.renderModel.bipedHead.rotateAngleY = headRotateAngleY;
        this.renderModel.bipedOuter.rotateAngleY = outerRotateAngleY;
        this.renderModel.bipedHead.rotateAngleX = headRotateAngleX;
    }

    public void animateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        this.setRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

        if (this.isStandard) {
            this.modelPlayer.superAnimateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            this.modelPlayer.superAnimateSleeping(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            if (this.isAngleJumping) {
                this.animateAngleJumping();
            } else {
                this.modelPlayer.superAnimateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
            }
        }
    }

    public void animateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            this.modelPlayer.superAnimateRiding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            this.modelPlayer.superAnimateLeftArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            this.modelPlayer.superAnimateRightArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            this.modelPlayer.superAnimateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        } else if (this.isWorking()) {
            this.animateNonStandardWorking(viewVerticalAngelOffset);
        }
    }

    public void animateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard || this.isWorking()) {
            this.modelPlayer.superAnimateWorkingArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard && !this.isAngleJumping) {
            this.modelPlayer.superAnimateSneaking(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            this.modelPlayer.superApplyAnimationOffsets(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    public void animateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor)
    {
        if (this.isStandard) {
            this.modelPlayer.superAnimateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        } else {
            this.animateNonStandardBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        }
    }

    private void setArmScales(float rightScale, float leftScale)
    {
        if (this.scaleArmType == Scale) {
            this.renderModel.bipedRightArm.scaleY = rightScale;
            this.renderModel.bipedLeftArm.scaleY = leftScale;
        } else if (this.scaleArmType == NoScaleEnd) {
            this.renderModel.bipedRightArm.offsetY -= (1F - rightScale) * 0.5F;
            this.renderModel.bipedLeftArm.offsetY -= (1F - leftScale) * 0.5F;
        }
    }

    private void setLegScales(float rightScale, float leftScale)
    {
        if (this.scaleLegType == Scale) {
            this.renderModel.bipedRightLeg.scaleY = rightScale;
            this.renderModel.bipedLeftLeg.scaleY = leftScale;
        } else if (this.scaleLegType == NoScaleEnd) {
            this.renderModel.bipedRightLeg.offsetY -= (1F - rightScale) * 0.5F;
            this.renderModel.bipedLeftLeg.offsetY -= (1F - leftScale) * 0.5F;
        }
    }

    private static float Factor(float x, float x0, float x1)
    {
        if (x0 > x1) {
            if (x <= x1) {
                return 1F;
            }
            if (x >= x0) {
                return 0F;
            }
            return (x0 - x) / (x0 - x1);
        } else {
            if (x >= x1) {
                return 1F;
            }
            if (x <= x0) {
                return 0F;
            }
            return (x - x0) / (x1 - x0);
        }
    }

    private static float Between(float min, float max, float value)
    {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    private static float Normalize(float radiant)
    {
        while (radiant > Half) {
            radiant -= Whole;
        }
        while (radiant < -Half) {
            radiant += Whole;
        }
        return radiant;
    }

    public boolean isStandard;
    public boolean isClimb;
    public boolean isClimbJump;
    public int feetClimbType;
    public int handsClimbType;
    public boolean isHandsVineClimbing;
    public boolean isFeetVineClimbing;
    public boolean isCeilingClimb;
    public boolean isSwim;
    public boolean isDive;
    public boolean isCrawl;
    public boolean isCrawlClimb;
    public boolean isJump;
    public boolean isHeadJump;
    public boolean isFlying;
    public boolean isSlide;
    public boolean isLevitate;
    public boolean isFalling;
    public boolean isGenericSneaking;
    public boolean isAngleJumping;
    public int angleJumpType;
    public boolean isRopeSliding;
    public float currentHorizontalSpeedFlattened;
    public float smallOverGroundHeight;
    public Block overGroundBlock;
    public int scaleArmType;
    public int scaleLegType;
}