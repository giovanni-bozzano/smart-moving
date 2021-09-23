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

    public final static int SPRINTING = 0;
    public final static int RUNNING = 1;
    public final static int WALKING = 2;
    public final static int SNEAKING = 3;
    public final static int STANDING = 4;
    public final static int UP = 0;
    public final static int CHARGE_UP = 1;
    public final static int ANGLE = 2;
    public final static int HEAD_UP = 3;
    public final static int SLIDE_DOWN = 4;
    public final static int CLIMB_UP = 5;
    public final static int CLIMB_UP_HANDS_ONLY = 6;
    public final static int CLIMB_BACK_UP = 7;
    public final static int CLIMB_BACK_UP_HANDS_ONLY = 8;
    public final static int CLIMB_BACK_HEAD = 9;
    public final static int CLIMB_BACK_HEAD_HANDS_ONLY = 10;
    public final static int WALL_UP = 11;
    public final static int WALL_HEAD = 12;
    public final static int WALL_UP_SLIDE = 13;
    public final static int WALL_HEAD_SLIDE = 14;

    public static boolean isJumpingEnabled(int speed, int type)
    {
        if (type == CHARGE_UP)
        {
            return SmartMovingConfig.HEAD_JUMPING.enable;
        }
        if (type == SLIDE_DOWN)
        {
            return SmartMovingConfig.SLIDING.enable;
        }
        if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY)
        {
            return SmartMovingConfig.CLIMB_JUMPING.enable;
        }
        if (type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            return SmartMovingConfig.CLIMB_BACK_JUMPING.enable;
        }
        if (type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            return SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.enable;
        }
        if (type == WALL_UP)
        {
            return SmartMovingConfig.WALL_JUMPING.enable;
        }
        if (type == WALL_HEAD)
        {
            return SmartMovingConfig.WALL_HEAD_JUMPING.enable;
        }

        if (speed == SPRINTING)
        {
            return SmartMovingConfig.JUMPING.sprint;
        }
        else if (speed == RUNNING)
        {
            return SmartMovingConfig.JUMPING.run;
        }
        else if (speed == WALKING)
        {
            return SmartMovingConfig.JUMPING.walk;
        }
        else if (speed == SNEAKING)
        {
            return SmartMovingConfig.JUMPING.sneak;
        }
        else if (speed == STANDING)
        {
            return SmartMovingConfig.JUMPING.stand;
        }

        return true;
    }

    public static boolean isJumpExhaustionEnabled(int speed, int type)
    {
        boolean result = SmartMovingConfig.JUMP_EXHAUSTION.enable;
        if (type == SLIDE_DOWN)
        {
            return result && SmartMovingConfig.JUMP_EXHAUSTION.slideEnable;
        }
        else if (type == ANGLE)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.angleEnable;
        }
        else if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.climbUpEnable;
        }
        else if (type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.climbBackEnable;
        }
        else if (type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.climbBackHeadEnable;
        }
        else if (type == WALL_UP)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.wallUpEnable;
        }
        else if (type == WALL_HEAD)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.wallHeadEnable;
        }
        else
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.upEnable;
        }

        if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY || type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY || type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            return result && SmartMovingConfig.JUMP_EXHAUSTION.climbEnable;
        }

        if (type == WALL_UP || type == WALL_HEAD)
        {
            return result && SmartMovingConfig.JUMP_EXHAUSTION.wallEnable;
        }

        if (speed == SPRINTING)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.sprintEnable;
        }
        else if (speed == RUNNING)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.runEnable;
        }
        else if (speed == WALKING)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.walkEnable;
        }
        else if (speed == SNEAKING)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.sneakEnable;
        }
        else if (speed == STANDING)
        {
            result &= SmartMovingConfig.JUMP_EXHAUSTION.standEnable;
        }

        if (type == CHARGE_UP)
        {
            result |= SmartMovingConfig.JUMP_EXHAUSTION.chargeEnable;
        }

        return result;
    }

    public static float getJumpExhaustionGain(int speed, int type, float jumpCharge)
    {
        float result = SmartMovingConfig.EXHAUSTION.gainFactor * SmartMovingConfig.JUMP_EXHAUSTION.gainFactor;

        if (type == SLIDE_DOWN)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.slideGainFactor;
        }
        else if (type == ANGLE)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.angleGainFactor;
        }
        else if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbUpGainFactor;
        }
        else if (type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackGainFactor;
        }
        else if (type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackHeadGainFactor;
        }
        else if (type == WALL_UP)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallUpGainFactor;
        }
        else if (type == WALL_HEAD)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallHeadGainFactor;
        }
        else
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.upGainFactor;
        }

        if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY || type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY || type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.climbGainFactor;
        }

        if (type == WALL_UP || type == WALL_HEAD)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.wallGainFactor;
        }

        if (speed == SPRINTING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sprintGainFactor;
        }
        else if (speed == RUNNING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.runGainFactor;
        }
        else if (speed == WALKING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.walkGainFactor;
        }
        else if (speed == SNEAKING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sneakGainFactor;
        }
        else if (speed == STANDING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.standGainFactor;
        }

        if (type == CHARGE_UP)
        {
            if (!isJumpExhaustionEnabled(speed, UP))
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

        if (type == SLIDE_DOWN)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.slideStopFactor;
        }
        else if (type == ANGLE)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.angleStopFactor;
        }
        else if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbUpStopFactor;
        }
        else if (type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackStopFactor;
        }
        else if (type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.climbBackHeadStopFactor;
        }
        else if (type == WALL_UP)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallUpStopFactor;
        }
        else if (type == WALL_HEAD)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.wallHeadStopFactor;
        }
        else
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.upStopFactor;
        }

        if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY || type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY || type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.climbStopFactor;
        }

        if (type == WALL_UP || type == WALL_HEAD)
        {
            return result * SmartMovingConfig.JUMP_EXHAUSTION.wallStopFactor;
        }

        if (speed == SPRINTING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sprintStopFactor;
        }
        else if (speed == RUNNING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.runStopFactor;
        }
        else if (speed == WALKING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.walkStopFactor;
        }
        else if (speed == SNEAKING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.sneakStopFactor;
        }
        else if (speed == STANDING)
        {
            result *= SmartMovingConfig.JUMP_EXHAUSTION.standStopFactor;
        }

        if (type == CHARGE_UP)
        {
            if (!isJumpExhaustionEnabled(speed, UP))
            {
                result += getJumpExhaustionGain(speed, UP, 0);
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

        if (type == ANGLE)
        {
            return result * SmartMovingConfig.SIDE_AND_BACK_JUMPING.verticalFactor;
        }
        if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_JUMPING.verticalFactor;
        }
        if (type == CLIMB_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_JUMPING.handsOnlyVerticalFactor;
        }
        if (type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.verticalFactor;
        }
        if (type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.handsOnlyVerticalFactor;
        }
        if (type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.verticalFactor;
        }
        if (type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.handsOnlyVerticalFactor;
        }
        if (type == WALL_UP || type == WALL_HEAD)
        {
            result *= SmartMovingConfig.WALL_JUMPING.verticalFactor;
        }
        if (type == WALL_HEAD)
        {
            result *= SmartMovingConfig.WALL_HEAD_JUMPING.verticalFactor;
        }

        if (type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY || type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY || type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY || type == WALL_UP || type == WALL_HEAD)
        {
            return result;
        }

        if (speed == SPRINTING)
        {
            result *= SmartMovingConfig.JUMPING.sprintVerticalFactor;
        }
        else if (speed == RUNNING)
        {
            result *= SmartMovingConfig.JUMPING.runVerticalFactor;
        }
        else if (speed == WALKING)
        {
            result *= SmartMovingConfig.JUMPING.walkVerticalFactor;
        }
        else if (speed == SNEAKING)
        {
            result *= SmartMovingConfig.JUMPING.sneakVerticalFactor;
        }
        else if (speed == STANDING)
        {
            result *= SmartMovingConfig.JUMPING.standVerticalFactor;
        }

        return result;
    }

    public static float getJumpHorizontalFactor(int speed, int type)
    {
        float result = SmartMovingConfig.JUMPING.horizontalFactor;

        if (type == ANGLE)
        {
            result *= SmartMovingConfig.SIDE_AND_BACK_JUMPING.horizontalFactor;
        }
        if (type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.horizontalFactor;
        }
        if (type == CLIMB_BACK_UP_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_JUMPING.handsOnlyHorizontalFactor;
        }
        if (type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.horizontalFactor;
        }
        if (type == CLIMB_BACK_HEAD_HANDS_ONLY)
        {
            result *= SmartMovingConfig.CLIMB_BACK_HEAD_JUMPING.handsOnlyHorizontalFactor;
        }
        if (type == WALL_UP)
        {
            result *= SmartMovingConfig.WALL_JUMPING.horizontalFactor;
        }
        if (type == WALL_HEAD)
        {
            result *= SmartMovingConfig.WALL_HEAD_JUMPING.horizontalFactor;
        }

        if (type == ANGLE || type == CLIMB_UP || type == CLIMB_UP_HANDS_ONLY || type == CLIMB_BACK_UP || type == CLIMB_BACK_UP_HANDS_ONLY || type == CLIMB_BACK_HEAD || type == CLIMB_BACK_HEAD_HANDS_ONLY || type == WALL_UP || type == WALL_HEAD)
        {
            return result;
        }

        if (speed == SPRINTING)
        {
            result *= SmartMovingConfig.JUMPING.sprintHorizontalFactor;
        }
        else if (speed == RUNNING)
        {
            result *= SmartMovingConfig.JUMPING.runHorizontalFactor;
        }
        else if (speed == WALKING)
        {
            result *= SmartMovingConfig.JUMPING.walkHorizontalFactor;
        }
        else if (speed == SNEAKING)
        {
            result *= SmartMovingConfig.JUMPING.sneakHorizontalFactor;
        }
        else if (speed == STANDING)
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

        if (speed == SPRINTING)
        {
            maxMotion *= SmartMovingConfig.GENERIC_SPRINTING.factor;
        }
        else if (speed == RUNNING)
        {
            maxMotion *= SmartMovingConfig.STANDARD_SPRINTING.factor;
        }
        else if (speed == SNEAKING)
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
            for (int i = SPRINTING; i <= STANDING; i++)
            {
                for (int n = UP; n <= WALL_HEAD_SLIDE; n++)
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