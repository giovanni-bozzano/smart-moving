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
        return SmartMovingConfig.climb.base.equals("standard");
    }

    public static boolean isSimpleBaseClimb()
    {
        return SmartMovingConfig.climb.base.equals("simple");
    }

    public static boolean isSmartBaseClimb()
    {
        return SmartMovingConfig.climb.base.equals("smart");
    }

    public static boolean isFreeBaseClimb()
    {
        return !isStandardBaseClimb() && !isSimpleBaseClimb() && !isSmartBaseClimb();
    }

    public static boolean isTotalFreeLadderClimb()
    {
        return isFreeBaseClimb() && SmartMovingConfig.climb.baseLadder;
    }

    public static boolean isTotalFreeVineClimb()
    {
        return isFreeBaseClimb() && SmartMovingConfig.climb.baseVine;
    }

    public static boolean isExhaustionLossHungerEnabled()
    {
        return SmartMovingConfig.exhaustion.hunger && SmartMovingConfig.hunger.enable;
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
        if (type == ChargeUp) {
            return SmartMovingConfig.headJumping.enable;
        } else if (type == SlideDown) {
            return SmartMovingConfig.sliding.enable;
        } else if (type == ClimbUp || type == ClimbUpHandsOnly) {
            return SmartMovingConfig.climbJumping.enable;
        } else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            return SmartMovingConfig.climbBackJumping.enable;
        } else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return SmartMovingConfig.climbBackHeadJumping.enable;
        } else if (type == WallUp) {
            return SmartMovingConfig.wallJumping.enable;
        } else if (type == WallHead) {
            return SmartMovingConfig.wallHeadJumping.enable;
        } else if (speed == Sprinting) {
            return SmartMovingConfig.jumping.sprint;
        } else if (speed == Running) {
            return SmartMovingConfig.jumping.run;
        } else if (speed == Walking) {
            return SmartMovingConfig.jumping.walk;
        } else if (speed == Sneaking) {
            return SmartMovingConfig.jumping.sneak;
        } else if (speed == Standing) {
            return SmartMovingConfig.jumping.stand;
        }
        return true;
    }

    public static boolean isJumpExhaustionEnabled(int speed, int type)
    {
        boolean result = SmartMovingConfig.jumpExhaustion.enable;
        if (type == SlideDown) {
            result &= SmartMovingConfig.jumpExhaustion.slideEnable;
        } else if (type == Angle) {
            result &= SmartMovingConfig.jumpExhaustion.angleEnable;
        } else if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result &= SmartMovingConfig.jumpExhaustion.climbUpEnable;
        } else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result &= SmartMovingConfig.jumpExhaustion.climbBackEnable;
        } else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result &= SmartMovingConfig.jumpExhaustion.climbBackHeadEnable;
        } else if (type == WallUp) {
            result &= SmartMovingConfig.jumpExhaustion.wallUpEnable;
        } else if (type == WallHead) {
            result &= SmartMovingConfig.jumpExhaustion.wallHeadEnable;
        } else {
            result &= SmartMovingConfig.jumpExhaustion.upEnable;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return result && SmartMovingConfig.jumpExhaustion.climbEnable;
        }

        if (type == WallUp || type == WallHead) {
            return result && SmartMovingConfig.jumpExhaustion.wallEnable;
        }

        if (speed == Sprinting) {
            result &= SmartMovingConfig.jumpExhaustion.sprintEnable;
        } else if (speed == Running) {
            result &= SmartMovingConfig.jumpExhaustion.runEnable;
        } else if (speed == Walking) {
            result &= SmartMovingConfig.jumpExhaustion.walkEnable;
        } else if (speed == Sneaking) {
            result &= SmartMovingConfig.jumpExhaustion.sneakEnable;
        } else if (speed == Standing) {
            result &= SmartMovingConfig.jumpExhaustion.standEnable;
        }

        if (type == ChargeUp) {
            result |= SmartMovingConfig.jumpExhaustion.chargeEnable;
        }

        return result;
    }

    public static float getJumpExhaustionGain(int speed, int type, float jumpCharge)
    {
        float result = SmartMovingConfig.exhaustion.gainFactor * SmartMovingConfig.jumpExhaustion.gainFactor;

        if (type == SlideDown) {
            result *= SmartMovingConfig.jumpExhaustion.slideGainFactor;
        } else if (type == Angle) {
            result *= SmartMovingConfig.jumpExhaustion.angleGainFactor;
        } else if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result *= SmartMovingConfig.jumpExhaustion.climbUpGainFactor;
        } else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= SmartMovingConfig.jumpExhaustion.climbBackGainFactor;
        } else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= SmartMovingConfig.jumpExhaustion.climbBackHeadGainFactor;
        } else if (type == WallUp) {
            result *= SmartMovingConfig.jumpExhaustion.wallUpGainFactor;
        } else if (type == WallHead) {
            result *= SmartMovingConfig.jumpExhaustion.wallHeadGainFactor;
        } else {
            result *= SmartMovingConfig.jumpExhaustion.upGainFactor;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return result * SmartMovingConfig.jumpExhaustion.climbGainFactor;
        }

        if (type == WallUp || type == WallHead) {
            return result * SmartMovingConfig.jumpExhaustion.wallGainFactor;
        }

        if (speed == Sprinting) {
            result *= SmartMovingConfig.jumpExhaustion.sprintGainFactor;
        } else if (speed == Running) {
            result *= SmartMovingConfig.jumpExhaustion.runGainFactor;
        } else if (speed == Walking) {
            result *= SmartMovingConfig.jumpExhaustion.walkGainFactor;
        } else if (speed == Sneaking) {
            result *= SmartMovingConfig.jumpExhaustion.sneakGainFactor;
        } else if (speed == Standing) {
            result *= SmartMovingConfig.jumpExhaustion.standGainFactor;
        }

        if (type == ChargeUp) {
            if (!isJumpExhaustionEnabled(speed, Up)) {
                result = 0;
            }

            result += SmartMovingConfig.exhaustion.gainFactor *
                    SmartMovingConfig.jumpExhaustion.gainFactor *
                    SmartMovingConfig.jumpExhaustion.upGainFactor *
                    SmartMovingConfig.jumpExhaustion.chargeGainFactor *
                    Math.min(jumpCharge, SmartMovingConfig.chargedJumping.maximum) / SmartMovingConfig.chargedJumping.maximum;
        }

        return result;
    }

    public static float getJumpExhaustionStop(int speed, int type, float jumpCharge)
    {
        float result = SmartMovingConfig.jumpExhaustion.stopFactor;

        if (type == SlideDown) {
            result *= SmartMovingConfig.jumpExhaustion.slideStopFactor;
        } else if (type == Angle) {
            result *= SmartMovingConfig.jumpExhaustion.angleStopFactor;
        } else if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result *= SmartMovingConfig.jumpExhaustion.climbUpStopFactor;
        } else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= SmartMovingConfig.jumpExhaustion.climbBackStopFactor;
        } else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= SmartMovingConfig.jumpExhaustion.climbBackHeadStopFactor;
        } else if (type == WallUp) {
            result *= SmartMovingConfig.jumpExhaustion.wallUpStopFactor;
        } else if (type == WallHead) {
            result *= SmartMovingConfig.jumpExhaustion.wallHeadStopFactor;
        } else {
            result *= SmartMovingConfig.jumpExhaustion.upStopFactor;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return result * SmartMovingConfig.jumpExhaustion.climbStopFactor;
        }

        if (type == WallUp || type == WallHead) {
            return result * SmartMovingConfig.jumpExhaustion.wallStopFactor;
        }

        if (speed == Sprinting) {
            result *= SmartMovingConfig.jumpExhaustion.sprintStopFactor;
        } else if (speed == Running) {
            result *= SmartMovingConfig.jumpExhaustion.runStopFactor;
        } else if (speed == Walking) {
            result *= SmartMovingConfig.jumpExhaustion.walkStopFactor;
        } else if (speed == Sneaking) {
            result *= SmartMovingConfig.jumpExhaustion.sneakStopFactor;
        } else if (speed == Standing) {
            result *= SmartMovingConfig.jumpExhaustion.standStopFactor;
        }

        if (type == ChargeUp) {
            if (!isJumpExhaustionEnabled(speed, Up)) {
                result += getJumpExhaustionGain(speed, Up, 0);
            }

            result -= SmartMovingConfig.jumpExhaustion.stopFactor *
                    SmartMovingConfig.jumpExhaustion.upStopFactor *
                    SmartMovingConfig.jumpExhaustion.chargeStopFactor *
                    Math.min(jumpCharge, SmartMovingConfig.chargedJumping.maximum) / SmartMovingConfig.chargedJumping.maximum;
        }

        return result;
    }

    public static float getJumpChargeFactor(float jumpCharge)
    {
        if (!SmartMovingConfig.chargedJumping.enable) {
            return 1.0F;
        }
        jumpCharge = Math.min(jumpCharge, SmartMovingConfig.chargedJumping.maximum);
        return 1.0F + jumpCharge / SmartMovingConfig.chargedJumping.maximum * (SmartMovingConfig.chargedJumping.maximum - 1F);
    }

    public static float getHeadJumpFactor(float headJumpCharge)
    {
        if (!SmartMovingConfig.headJumping.enable) {
            return 1.0F;
        }
        headJumpCharge = Math.min(headJumpCharge, SmartMovingConfig.headJumping.maximum);
        return (headJumpCharge - 1) / (SmartMovingConfig.headJumping.maximum - 1);
    }

    public static float getJumpVerticalFactor(int speed, int type)
    {
        float result = SmartMovingConfig.jumping.verticalFactor;

        if (type == Angle) {
            result *= SmartMovingConfig.sideAndBackJumping.verticalFactor;
        }
        if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result *= SmartMovingConfig.climbJumping.verticalFactor;
        }
        if (type == ClimbUpHandsOnly) {
            result *= SmartMovingConfig.climbJumping.handsOnlyVerticalFactor;
        }
        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= SmartMovingConfig.climbBackJumping.verticalFactor;
        }
        if (type == ClimbBackUpHandsOnly) {
            result *= SmartMovingConfig.climbBackJumping.handsOnlyVerticalFactor;
        }
        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= SmartMovingConfig.climbBackHeadJumping.verticalFactor;
        }
        if (type == ClimbBackHeadHandsOnly) {
            result *= SmartMovingConfig.climbBackHeadJumping.handsOnlyVerticalFactor;
        }
        if (type == WallUp || type == WallHead) {
            result *= SmartMovingConfig.wallJumping.verticalFactor;
        }
        if (type == WallHead) {
            result *= SmartMovingConfig.wallHeadJumping.verticalFactor;
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead) {
            return result;
        }

        if (speed == Sprinting) {
            result *= SmartMovingConfig.jumping.sprintVerticalFactor;
        } else if (speed == Running) {
            result *= SmartMovingConfig.jumping.runVerticalFactor;
        } else if (speed == Walking) {
            result *= SmartMovingConfig.jumping.walkVerticalFactor;
        } else if (speed == Sneaking) {
            result *= SmartMovingConfig.jumping.sneakVerticalFactor;
        } else if (speed == Standing) {
            result *= SmartMovingConfig.jumping.standVerticalFactor;
        }

        return result;
    }

    public static float getJumpHorizontalFactor(int speed, int type)
    {
        float result = SmartMovingConfig.jumping.horizontalFactor;

        if (type == Angle) {
            result *= SmartMovingConfig.sideAndBackJumping.horizontalFactor;
        }
        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= SmartMovingConfig.climbBackJumping.horizontalFactor;
        }
        if (type == ClimbBackUpHandsOnly) {
            result *= SmartMovingConfig.climbBackJumping.handsOnlyHorizontalFactor;
        }
        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= SmartMovingConfig.climbBackHeadJumping.horizontalFactor;
        }
        if (type == ClimbBackHeadHandsOnly) {
            result *= SmartMovingConfig.climbBackHeadJumping.handsOnlyHorizontalFactor;
        }
        if (type == WallUp) {
            result *= SmartMovingConfig.wallJumping.horizontalFactor;
        }
        if (type == WallHead) {
            result *= SmartMovingConfig.wallHeadJumping.horizontalFactor;
        }

        if (type == Angle || type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead) {
            return result;
        }

        if (speed == Sprinting) {
            result *= SmartMovingConfig.jumping.sprintHorizontalFactor;
        } else if (speed == Running) {
            result *= SmartMovingConfig.jumping.runHorizontalFactor;
        } else if (speed == Walking) {
            result *= SmartMovingConfig.jumping.walkHorizontalFactor;
        } else if (speed == Sneaking) {
            result *= SmartMovingConfig.jumping.sneakHorizontalFactor;
        } else if (speed == Standing) {
            result *= 0.0F;
        }

        return result;
    }

    public static float getMaxHorizontalMotion(int speed, boolean inWater)
    {
        float maxMotion = 0.117852041920949F;

        if (inWater) {
            maxMotion = 0.07839602977037292F;
        }

        if (speed == Sprinting) {
            maxMotion *= SmartMovingConfig.genericSprinting.factor;
        } else if (speed == Running) {
            maxMotion *= SmartMovingConfig.standardSprinting.factor;
        } else if (speed == Sneaking) {
            maxMotion *= SmartMovingConfig.genericSneaking.factor;
        }

        return maxMotion;
    }

    public static float getMaxExhaustion()
    {
        float result = 0.0F;

        if (SmartMovingConfig.standardSprinting.enable && SmartMovingConfig.standardSprinting.exhaustion) {
            result = max(result, SmartMovingConfig.standardSprinting.exhaustionStop);
        }

        if (SmartMovingConfig.genericSprinting.enable && SmartMovingConfig.genericSprinting.exhaustion) {
            result = max(result, SmartMovingConfig.genericSprinting.exhaustionStop);
        }

        if (SmartMovingConfig.jumping.enable) {
            for (int i = Sprinting; i <= Standing; i++) {
                for (int n = Up; n <= WallHeadSlide; n++) {
                    if (isJumpExhaustionEnabled(i, n)) {
                        for (int t = 0; t <= 1; t++) {
                            result = max(result, getJumpExhaustionStop(i, n, t) + getJumpExhaustionGain(i, n, t));
                        }
                    }
                }
            }
        }

        if (SmartMovingConfig.climb.enable && SmartMovingConfig.climb.exhaustion) {
            result = max(result, SmartMovingConfig.climb.exhaustionStop);
        }

        if (SmartMovingConfig.climb.ceiling && SmartMovingConfig.climb.ceilingExhaustion) {
            result = max(result, SmartMovingConfig.climb.ceilingExhaustionStop);
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

        float factor = hunger ? SmartMovingConfig.hunger.gainFactor : SmartMovingConfig.exhaustion.lossFactor;
        if (airBorne) {
            factor *= hunger ? 0.0F : SmartMovingConfig.exhaustion.fallLossFactor;
        } else if (isSprinting) {
            factor *= hunger ? SmartMovingConfig.hunger.sprintGainFactor : SmartMovingConfig.exhaustion.sprintLossFactor;
        } else if (isRunning) {
            factor *= hunger ? SmartMovingConfig.hunger.runGainFactor : SmartMovingConfig.exhaustion.runLossFactor;
        } else if (isSneaking) {
            factor *= hunger ? SmartMovingConfig.hunger.sneakGainFactor : SmartMovingConfig.exhaustion.sneakLossFactor;
        } else if (isStanding) {
            factor *= hunger ? SmartMovingConfig.hunger.standGainFactor : SmartMovingConfig.exhaustion.standLossFactor;
        } else {
            factor *= hunger ? SmartMovingConfig.hunger.walkGainFactor : SmartMovingConfig.exhaustion.walkLossFactor;
        }

        if (isClimbing) {
            factor *= hunger ? SmartMovingConfig.hunger.climbGainFactor : SmartMovingConfig.exhaustion.climbLossFactor;
        } else if (isCrawling) {
            factor *= hunger ? SmartMovingConfig.hunger.crawlGainFactor : SmartMovingConfig.exhaustion.crawlLossFactor;
        } else if (isCeilingClimbing) {
            factor *= hunger ? SmartMovingConfig.hunger.climbCeilingGainFactor : SmartMovingConfig.exhaustion.climbCeilingLossFactor;
        } else if (isSwimming) {
            factor *= hunger ? SmartMovingConfig.hunger.swimGainFactor : SmartMovingConfig.exhaustion.swimLossFactor;
        } else if (isDiving) {
            factor *= hunger ? SmartMovingConfig.hunger.diveGainFactor : SmartMovingConfig.exhaustion.diveLossFactor;
        } else if (isDipping) {
            factor *= hunger ? SmartMovingConfig.hunger.dipGainFactor : SmartMovingConfig.exhaustion.dipLossFactor;
        } else {
            factor *= hunger ? SmartMovingConfig.hunger.normalGainFactor : SmartMovingConfig.exhaustion.normalLossFactor;
        }

        return factor;
    }
}