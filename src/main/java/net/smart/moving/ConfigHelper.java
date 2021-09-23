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

public class ConfigHelper
{
    public static boolean isStandardBaseClimb()
    {
        return SmartMovingConfig.CLIMB.base.equals("standard");
    }

    public static boolean isSimpleBaseClimb()
    {
        return SmartMovingConfig.CLIMB.base.equals("simple");
    }

    public static boolean isSmartBaseClimb()
    {
        return SmartMovingConfig.CLIMB.base.equals("smart");
    }

    public static boolean isFreeBaseClimb()
    {
        return !isStandardBaseClimb() && !isSimpleBaseClimb() && !isSmartBaseClimb();
    }

    public static boolean isTotalFreeLadderClimb()
    {
        return isFreeBaseClimb() && SmartMovingConfig.CLIMB.baseLadder;
    }

    public static boolean isTotalFreeVineClimb()
    {
        return isFreeBaseClimb() && SmartMovingConfig.CLIMB.baseVine;
    }

    public static boolean isExhaustionLossHungerEnabled()
    {
        return SmartMovingConfig.EXHAUSTION.hunger && SmartMovingConfig.HUNGER.enable;
    }

    public final static int Sprinting = 0;
    public final static int Running = 1;
    public final static int Walking = 2;
    public final static int Sneaking = 3;
    public final static int Standing = 4;
    public final static int Up = 0;
    public final static int ChargeUp = 1;
    public final static int Angle = 2;
    public final static int HeadUp = 3;
    public final static int SlideDown = 4;
    public final static int ClimbUp = 5;
    public final static int ClimbUpHandsOnly = 6;
    public final static int ClimbBackUp = 7;
    public final static int ClimbBackUpHandsOnly = 8;
    public final static int ClimbBackHead = 9;
    public final static int ClimbBackHeadHandsOnly = 10;
    public final static int WallUp = 11;
    public final static int WallHead = 12;
    public final static int WallUpSlide = 13;
    public final static int WallHeadSlide = 14;

    public static boolean isJumpingEnabled(int speed, int type)
    {
        if (type == ChargeUp)
        {
            return SmartMovingConfig.HEAD_JUMPING.enable;
        }
        if (type == SlideDown)
        {
            return SmartMovingConfig.SLIDING.enable;
        }
        if (type == ClimbUp || type == ClimbUpHandsOnly)
        {
            return SmartMovingConfig.CLIMB_JUMPING.enable;
        }
        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly)
        {
            return SmartMovingConfig.CLIMB_BACK_JUMPING.enable;
        }
        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            return SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.enable;
        }
        if (type == WallUp)
        {
            return SmartMovingConfig.WALL_JUMPING.enable;
        }
        if (type == WallHead)
        {
            return SmartMovingConfig.WALL_HEAD_JUMPING.enable;
        }

        if (speed == Sprinting)
        {
            return SmartMovingConfig.JUMPING.sprint;
        }
        else if (speed == Running)
        {
            return SmartMovingConfig.JUMPING.run;
        }
        else if (speed == Walking)
        {
            return SmartMovingConfig.JUMPING.walk;
        }
        else if (speed == Sneaking)
        {
            return SmartMovingConfig.JUMPING.sneak;
        }
        else if (speed == Standing)
        {
            return SmartMovingConfig.JUMPING.stand;
        }

        return true;
    }

    public static boolean isJumpExhaustionEnabled(int speed, int type)
    {
        boolean result = SmartMovingConfig.JUMP_EXHAUSTION.enable;
        if (type == SlideDown)
        {
            return result && SmartMovingConfig.JUMP_EXHAUSTION.slideEnable;
        }
        else if (type == Angle)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.angleEnable;
        }
        else if (type == ClimbUp || type == ClimbUpHandsOnly)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.climbUpEnable;
        }
        else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.climbBackEnable;
        }
        else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.climbBackHeadEnable;
        }
        else if (type == WallUp)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.wallUpEnable;
        }
        else if (type == WallHead)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.wallHeadEnable;
        }
        else
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.upEnable;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            return result && SmartMovingConfig.JUMP_EXHAUSTION.climbEnable;
        }

        if (type == WallUp || type == WallHead)
        {
            return result && SmartMovingConfig.JUMP_EXHAUSTION.wallEnable;
        }

        if (speed == Sprinting)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.sprintEnable;
        }
        else if (speed == Running)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.runEnable;
        }
        else if (speed == Walking)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.walkEnable;
        }
        else if (speed == Sneaking)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.sneakEnable;
        }
        else if (speed == Standing)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.standEnable;
        }

        if (type == ChargeUp)
        {
            result |= SmartMovingConfig.JUMP_EXHAUSTION.chargeEnable;
        }

        return result;
    }

    public static float getJumpExhaustionGain(int speed, int type, float jumpCharge)
    {
        float result = SmartMovingConfig.EXHAUSTION.gainFactor * SmartMovingConfig.JUMP_EXHAUSTION.gainFactor;

        if (type == SlideDown)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.slideGainFactor;
        }
        else if (type == Angle)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.angleGainFactor;
        }
        else if (type == ClimbUp || type == ClimbUpHandsOnly)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbUpGainFactor;
        }
        else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackGainFactor;
        }
        else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackHeadGainFactor;
        }
        else if (type == WallUp)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallUpGainFactor;
        }
        else if (type == WallHead)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallHeadGainFactor;
        }
        else
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.upGainFactor;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.climbGainFactor;
        }

        if (type == WallUp || type == WallHead)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.wallGainFactor;
        }

        if (speed == Sprinting)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sprintGainFactor;
        }
        else if (speed == Running)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.runGainFactor;
        }
        else if (speed == Walking)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.walkGainFactor;
        }
        else if (speed == Sneaking)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sneakGainFactor;
        }
        else if (speed == Standing)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.standGainFactor;
        }

        if (type == ChargeUp)
        {
            if (!isJumpExhaustionEnabled(speed, Up))
            {
                result = 0;
            }

            result += SmartMovingConfig.EXHAUSTION.gainFactor * SmartMovingConfig.JUMP_EXHAUSTION.gainFactor * SmartMovingConfig.JUMP_EXHAUSTION.upGainFactor * SmartMovingConfig.JUMP_EXHAUSTION.chargeGainFactor * Math.min(jumpCharge, SmartMovingConfig.CHARGED_JUMPING.maximum) / SmartMovingConfig.CHARGED_JUMPING.maximum;
        }

        return result;
    }

    public static float getJumpExhaustionStop(int speed, int type, float jumpCharge)
    {
        float result = SmartMovingConfig.JUMP_EXHAUSTION.stopFactor;

        if (type == SlideDown)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.slideStopFactor;
        }
        else if (type == Angle)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.angleStopFactor;
        }
        else if (type == ClimbUp || type == ClimbUpHandsOnly)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbUpStopFactor;
        }
        else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackStopFactor;
        }
        else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackHeadStopFactor;
        }
        else if (type == WallUp)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallUpStopFactor;
        }
        else if (type == WallHead)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallHeadStopFactor;
        }
        else
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.upStopFactor;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.climbStopFactor;
        }

        if (type == WallUp || type == WallHead)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.wallStopFactor;
        }

        if (speed == Sprinting)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sprintStopFactor;
        }
        else if (speed == Running)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.runStopFactor;
        }
        else if (speed == Walking)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.walkStopFactor;
        }
        else if (speed == Sneaking)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sneakStopFactor;
        }
        else if (speed == Standing)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.standStopFactor;
        }

        if (type == ChargeUp)
        {
            if (!isJumpExhaustionEnabled(speed, Up))
            {
                result += getJumpExhaustionGain(speed, Up, 0);
            }

            result -= SmartMovingConfig.JUMP_EXHAUSTION.stopFactor * SmartMovingConfig.JUMP_EXHAUSTION.upStopFactor * SmartMovingConfig.JUMP_EXHAUSTION.chargeStopFactor * Math.min(jumpCharge, SmartMovingConfig.CHARGED_JUMPING.maximum) / SmartMovingConfig.CHARGED_JUMPING.maximum;
        }

        return result;
    }

    public static float getJumpChargeFactor(float jumpCharge)
    {
        if (!SmartMovingConfig.CHARGED_JUMPING.enable)
        {
            return 1.0F;
        }
        jumpCharge = Math.min(jumpCharge, SmartMovingConfig.CHARGED_JUMPING.maximum);
        return 1.0F + jumpCharge / SmartMovingConfig.CHARGED_JUMPING.maximum * (SmartMovingConfig.CHARGED_JUMPING.factor - 1F);
    }

    public static float getHeadJumpFactor(float headJumpCharge)
    {
        if (!SmartMovingConfig.HEAD_JUMPING.enable)
        {
            return 1.0F;
        }
        headJumpCharge = Math.min(headJumpCharge, SmartMovingConfig.HEAD_JUMPING.maximum);
        return (headJumpCharge - 1) / (SmartMovingConfig.HEAD_JUMPING.maximum - 1);
    }

    public static float getJumpVerticalFactor(int speed, int type)
    {
        float result = SmartMovingConfig.JUMPING.verticalFactor;

        if (type == Angle)
        {
            return result * SmartMovingConfig.SIDE_AND_BACK_JUMPING.verticalFactor;
        }
        if (type == ClimbUp || type == ClimbUpHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_JUMPING.verticalFactor;
        }
        if (type == ClimbUpHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_JUMPING.handsOnlyVerticalFactor;
        }
        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.verticalFactor;
        }
        if (type == ClimbBackUpHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.handsOnlyVerticalFactor;
        }
        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.verticalFactor;
        }
        if (type == ClimbBackHeadHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.handsOnlyVerticalFactor;
        }
        if (type == WallUp || type == WallHead)
        {
            result *= SmartMovingConfig.WALL_JUMPING.verticalFactor;
        }
        if (type == WallHead)
        {
            result *= SmartMovingConfig.WALL_HEAD_JUMPING.verticalFactor;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead)
        {
            return result;
        }

        if (speed == Sprinting)
        {
            result *= SmartMovingConfig.JUMPING.sprintVerticalFactor;
        }
        else if (speed == Running)
        {
            result *= SmartMovingConfig.JUMPING.runVerticalFactor;
        }
        else if (speed == Walking)
        {
            result *= SmartMovingConfig.JUMPING.walkVerticalFactor;
        }
        else if (speed == Sneaking)
        {
            result *= SmartMovingConfig.JUMPING.sneakVerticalFactor;
        }
        else if (speed == Standing)
        {
            result *= SmartMovingConfig.JUMPING.standVerticalFactor;
        }

        return result;
    }

    public static float getJumpHorizontalFactor(int speed, int type)
    {
        float result = SmartMovingConfig.JUMPING.horizontalFactor;

        if (type == Angle)
        {
            result *= SmartMovingConfig.SIDE_AND_BACK_JUMPING.horizontalFactor;
        }
        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.horizontalFactor;
        }
        if (type == ClimbBackUpHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.handsOnlyHorizontalFactor;
        }
        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.horizontalFactor;
        }
        if (type == ClimbBackHeadHandsOnly)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.handsOnlyHorizontalFactor;
        }
        if (type == WallUp)
        {
            result *= SmartMovingConfig.WALL_JUMPING.horizontalFactor;
        }
        if (type == WallHead)
        {
            result *= SmartMovingConfig.WALL_HEAD_JUMPING.horizontalFactor;
        }

        if (type == Angle || type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead)
        {
            return result;
        }

        if (speed == Sprinting)
        {
            result *= SmartMovingConfig.JUMPING.sprintHorizontalFactor;
        }
        else if (speed == Running)
        {
            result *= SmartMovingConfig.JUMPING.runHorizontalFactor;
        }
        else if (speed == Walking)
        {
            result *= SmartMovingConfig.JUMPING.walkHorizontalFactor;
        }
        else if (speed == Sneaking)
        {
            result *= SmartMovingConfig.JUMPING.sneakHorizontalFactor;
        }
        else if (speed == Standing)
        {
            result *= 0.0F;
        }

        return result;
    }

    public static float getMaxHorizontalMotion(int speed, boolean inWater)
    {
        float maxMotion = 0.117852041920949F;

        if (inWater)
        {
            maxMotion = 0.07839602977037292F;
        }

        if (speed == Sprinting)
        {
            maxMotion *= SmartMovingConfig.GENERIC_SPRINTING.factor;
        }
        else if (speed == Running)
        {
            maxMotion *= SmartMovingConfig.STANDARD_SPRINTING.factor;
        }
        else if (speed == Sneaking)
        {
            maxMotion *= SmartMovingConfig.GENERIC_SNEAKING.factor;
        }

        return maxMotion;
    }

    public static float getMaxExhaustion()
    {
        float result = 0.0F;

        if (SmartMovingConfig.STANDARD_SPRINTING.enable && SmartMovingConfig.STANDARD_SPRINTING.exhaustion)
        {
            result = max(result, SmartMovingConfig.STANDARD_SPRINTING.exhaustionStop);
        }

        if (SmartMovingConfig.GENERIC_SPRINTING.enable && SmartMovingConfig.GENERIC_SPRINTING.exhaustion)
        {
            result = max(result, SmartMovingConfig.GENERIC_SPRINTING.exhaustionStop);
        }

        if (SmartMovingConfig.JUMPING.enable)
        {
            for (int i = Sprinting; i <= Standing; i++)
            {
                for (int n = Up; n <= WallHeadSlide; n++)
                {
                    if (isJumpExhaustionEnabled(i, n))
                    {
                        for (int t = 0; t <= 1; t++)
                        {
                            result = max(result, getJumpExhaustionStop(i, n, t) + getJumpExhaustionGain(i, n, t));
                        }
                    }
                }
            }
        }

        if (SmartMovingConfig.CLIMB.enable && SmartMovingConfig.CLIMB.exhaustion)
        {
            result = max(result, SmartMovingConfig.CLIMB.exhaustionStop);
        }

        if (SmartMovingConfig.CLIMB.ceiling && SmartMovingConfig.CLIMB.ceilingExhaustion)
        {
            result = max(result, SmartMovingConfig.CLIMB.ceilingExhaustionStop);
        }
        return result;
    }

    private static float max(float value, float valueOrInfinite)
    {
        return valueOrInfinite == java.lang.Float.POSITIVE_INFINITY ? value : Math.max(value, valueOrInfinite);
    }

    public static float getFactor(boolean hunger, boolean onGround, boolean isStanding, boolean isStill, boolean isSneaking, boolean isRunning, boolean isSprinting, boolean isClimbing, boolean isClimbCrawling, boolean isCeilingClimbing, boolean isDipping, boolean isSwimming, boolean isDiving, boolean isCrawling, boolean isCrawlClimbing)
    {
        isClimbing |= isClimbCrawling;
        isCrawling |= isCrawlClimbing;
        boolean actionOverGround = isClimbing || isCeilingClimbing || isDiving || isSwimming;
        boolean airBorne = !onGround && !actionOverGround;
        isStanding = actionOverGround ? isStill : isStanding;
        isSneaking = isSneaking & !isStanding;

        float factor = hunger ? SmartMovingConfig.HUNGER.gainFactor : SmartMovingConfig.EXHAUSTION.lossFactor;
        if (airBorne)
        {
            factor *= hunger ? 0.0F : SmartMovingConfig.EXHAUSTION.fallLossFactor;
        }
        else if (isSprinting)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.sprintGainFactor : SmartMovingConfig.EXHAUSTION.sprintLossFactor;
        }
        else if (isRunning)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.runGainFactor : SmartMovingConfig.EXHAUSTION.runLossFactor;
        }
        else if (isSneaking)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.sneakGainFactor : SmartMovingConfig.EXHAUSTION.sneakLossFactor;
        }
        else if (isStanding)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.standGainFactor : SmartMovingConfig.EXHAUSTION.standLossFactor;
        }
        else
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.walkGainFactor : SmartMovingConfig.EXHAUSTION.walkLossFactor;
        }

        if (isClimbing)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.climbGainFactor : SmartMovingConfig.EXHAUSTION.climbLossFactor;
        }
        else if (isCrawling)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.crawlGainFactor : SmartMovingConfig.EXHAUSTION.crawlLossFactor;
        }
        else if (isCeilingClimbing)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.climbCeilingGainFactor : SmartMovingConfig.EXHAUSTION.climbCeilingLossFactor;
        }
        else if (isSwimming)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.swimGainFactor : SmartMovingConfig.EXHAUSTION.swimLossFactor;
        }
        else if (isDiving)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.diveGainFactor : SmartMovingConfig.EXHAUSTION.diveLossFactor;
        }
        else if (isDipping)
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.dipGainFactor : SmartMovingConfig.EXHAUSTION.dipLossFactor;
        }
        else
        {
            factor *= hunger ? SmartMovingConfig.HUNGER.normalGainFactor : SmartMovingConfig.EXHAUSTION.normalLossFactor;
        }

        return factor;
    }
}