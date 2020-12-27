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
package net.smart.moving.config;

public class ClientConfig extends Config
{
    public boolean isSneakingEnabled()
    {
        return !this.enabled || this._sneak.getValue();
    }

    public boolean isStandardBaseClimb()
    {
        return !this.enabled || this._isStandardBaseClimb.getValue();
    }

    public boolean isSimpleBaseClimb()
    {
        return this.enabled && this._isSimpleBaseClimb.getValue();
    }

    public boolean isSmartBaseClimb()
    {
        return this.enabled && this._isSmartBaseClimb.getValue();
    }

    public boolean isFreeBaseClimb()
    {
        return this.enabled && this._isFreeBaseClimb.getValue();
    }

    public boolean isTotalFreeLadderClimb()
    {
        return this.isFreeBaseClimb() && this._freeBaseLadderClimb.getValue();
    }

    public boolean isTotalFreeVineClimb()
    {
        return this.isFreeBaseClimb() && this._freeBaseVineClimb.getValue();
    }

    public boolean isFreeClimbAutoLaddderEnabled()
    {
        return this.enabled && this._freeClimbingAutoLaddder.getValue();
    }

    public boolean isFreeClimbAutoVineEnabled()
    {
        return this.enabled && this._freeClimbingAutoVine.getValue();
    }

    public boolean isFreeClimbingEnabled()
    {
        return this.enabled && this._freeClimb.getValue();
    }

    public boolean isCeilingClimbingEnabled()
    {
        return this.enabled && this._ceilingClimbing.getValue();
    }

    public boolean isSwimmingEnabled()
    {
        return this.enabled && this._swim.getValue();
    }

    public boolean isDivingEnabled()
    {
        return this.enabled && this._dive.getValue();
    }

    public boolean isLavaLikeWaterEnabled()
    {
        return this.enabled && this._lavaLikeWater.getValue();
    }

    public boolean isFlyingEnabled()
    {
        return this.enabled && this._fly.getValue();
    }

    public boolean isLevitateSmallEnabled()
    {
        return this.enabled && this._levitateSmall.getValue();
    }

    public boolean isRunningEnabled()
    {
        return !this.enabled || this._run.getValue();
    }

    public boolean isRunExhaustionEnabled()
    {
        return this.enabled && this._runExhaustion.getValue();
    }

    public boolean isClimbExhaustionEnabled()
    {
        return this.enabled && this._climbExhaustion.getValue();
    }

    public boolean isCeilingClimbExhaustionEnabled()
    {
        return this.enabled && this._ceilingClimbExhaustion.getValue();
    }

    public boolean isSprintingEnabled()
    {
        return this.enabled && this._sprint.getValue();
    }

    public boolean isSprintExhaustionEnabled()
    {
        return this.enabled && this._sprintExhaustion.getValue();
    }

    public boolean isJumpChargingEnabled()
    {
        return this.enabled && this._jumpCharge.getValue();
    }

    public boolean isHeadJumpingEnabled()
    {
        return this.enabled && this._headJump.getValue();
    }

    public boolean isSlidingEnabled()
    {
        return this.enabled && this._slide.getValue();
    }

    public boolean isCrawlingEnabled()
    {
        return this.enabled && this._crawl.getValue();
    }

    public boolean isExhaustionLossHungerEnabled()
    {
        return this.enabled && this._exhaustionLossHunger.getValue() && this._hungerGain.getValue();
    }

    public boolean isHungerGainEnabled()
    {
        return !this.enabled || this._hungerGain.getValue();
    }

    public boolean isLevitationAnimationEnabled()
    {
        return this.enabled && this._levitateAnimation.getValue();
    }

    public boolean isFallAnimationEnabled()
    {
        return this.enabled && this._fallAnimation.getValue();
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

    public boolean isJumpingEnabled(int speed, int type)
    {
        if (!this.enabled) {
            return true;
        }

        if (type == ChargeUp) {
            return this._jumpCharge.getValue();
        }
        if (type == SlideDown) {
            return this._slide.getValue();
        }
        if (type == ClimbUp || type == ClimbUpHandsOnly) {
            return this._climbUpJump.getValue();
        }
        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            return this._climbBackUpJump.getValue();
        }
        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return this._climbBackHeadJump.getValue();
        }

        if (type == WallUp) {
            return this._wallUpJump.getValue();
        }
        if (type == WallHead) {
            return this._wallHeadJump.getValue();
        }

        if (speed == Sprinting) {
            return this._sprintJump.getValue();
        } else if (speed == Running) {
            return this._runJump.getValue();
        } else if (speed == Walking) {
            return this._walkJump.getValue();
        } else if (speed == Sneaking) {
            return this._sneakJump.getValue();
        } else if (speed == Standing) {
            return this._standJump.getValue();
        }

        return true;
    }

    public boolean isSideJumpEnabled()
    {
        return this.enabled && this._angleJumpSide.getValue();
    }

    public boolean isBackJumpEnabled()
    {
        return this.enabled && this._angleJumpBack.getValue();
    }

    public boolean isWallJumpEnabled()
    {
        return this.enabled && this._wallUpJump.getValue();
    }

    public boolean isJumpExhaustionEnabled(int speed, int type)
    {
        if (!this.enabled) {
            return false;
        }

        boolean result = this._jumpExhaustion.getValue();

        if (type == SlideDown) {
            return result && this._jumpSlideExhaustion.getValue();
        } else if (type == Angle) {
            result &= this._angleJumpExhaustion.getValue();
        } else if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result &= this._climbJumpUpExhaustion.getValue();
        } else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result &= this._climbJumpBackUpExhaustion.getValue();
        } else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result &= this._climbJumpBackHeadExhaustion.getValue();
        } else if (type == WallUp) {
            result &= this._wallUpJumpExhaustion.getValue();
        } else if (type == WallHead) {
            result &= this._wallHeadJumpExhaustion.getValue();
        } else {
            result &= this._upJumpExhaustion.getValue();
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return result && this._climbJumpExhaustion.getValue();
        }

        if (type == WallUp || type == WallHead) {
            return result && this._wallJumpExhaustion.getValue();
        }

        if (speed == Sprinting) {
            result &= this._sprintJumpExhaustion.getValue();
        } else if (speed == Running) {
            result &= this._runJumpExhaustion.getValue();
        } else if (speed == Walking) {
            result &= this._walkJumpExhaustion.getValue();
        } else if (speed == Sneaking) {
            result &= this._sneakJumpExhaustion.getValue();
        } else if (speed == Standing) {
            result &= this._standJumpExhaustion.getValue();
        }

        if (type == ChargeUp) {
            result |= this._jumpChargeExhaustion.getValue();
        }

        return result;
    }

    public float getJumpExhaustionGain(int speed, int type, float jumpCharge)
    {
        if (!this.enabled) {
            return 0F;
        }

        float result = this._baseExhautionGainFactor.getValue() * this._jumpExhaustionGainFactor.getValue();

        if (type == SlideDown) {
            return result * this._jumpSlideExhaustionGainFactor.getValue();
        } else if (type == Angle) {
            result *= this._angleJumpExhaustionGainFactor.getValue();
        } else if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result *= this._climbJumpUpExhaustionGainFactor.getValue();
        } else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= this._climbJumpBackUpExhaustionGainFactor.getValue();
        } else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= this._climbJumpBackHeadExhaustionGainFactor.getValue();
        } else if (type == WallUp) {
            result *= this._wallUpJumpExhaustionGainFactor.getValue();
        } else if (type == WallHead) {
            result *= this._wallHeadJumpExhaustionGainFactor.getValue();
        } else {
            result *= this._upJumpExhaustionGainFactor.getValue();
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return result * this._climbJumpExhaustionGainFactor.getValue();
        }

        if (type == WallUp || type == WallHead) {
            return result * this._wallJumpExhaustionGainFactor.getValue();
        }

        if (speed == Sprinting) {
            result *= this._sprintJumpExhaustionGainFactor.getValue();
        } else if (speed == Running) {
            result *= this._runJumpExhaustionGainFactor.getValue();
        } else if (speed == Walking) {
            result *= this._walkJumpExhaustionGainFactor.getValue();
        } else if (speed == Sneaking) {
            result *= this._sneakJumpExhaustionGainFactor.getValue();
        } else if (speed == Standing) {
            result *= this._standJumpExhaustionGainFactor.getValue();
        }

        if (type == ChargeUp) {
            if (!this.isJumpExhaustionEnabled(speed, Up)) {
                result = 0;
            }

            result += this._baseExhautionGainFactor.getValue() *
                    this._jumpExhaustionGainFactor.getValue() *
                    this._upJumpExhaustionGainFactor.getValue() *
                    this._jumpChargeExhaustionGainFactor.getValue() *
                    Math.min(jumpCharge, this._jumpChargeMaximum.getValue()) / this._jumpChargeMaximum.getValue();
        }

        return result;
    }

    public float getJumpExhaustionStop(int speed, int type, float jumpCharge)
    {
        float result = this._jumpExhaustionStopFactor.getValue();

        if (type == SlideDown) {
            return result * this._jumpSlideExhaustionStopFactor.getValue();
        } else if (type == Angle) {
            result *= this._angleJumpExhaustionStopFactor.getValue();
        } else if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result *= this._climbJumpUpExhaustionStopFactor.getValue();
        } else if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= this._climbJumpBackUpExhaustionStopFactor.getValue();
        } else if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= this._climbJumpBackHeadExhaustionStopFactor.getValue();
        } else if (type == WallUp) {
            result *= this._wallUpJumpExhaustionStopFactor.getValue();
        } else if (type == WallHead) {
            result *= this._wallHeadJumpExhaustionStopFactor.getValue();
        } else {
            result *= this._upJumpExhaustionStopFactor.getValue();
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            return result * this._climbJumpExhaustionStopFactor.getValue();
        }

        if (type == WallUp || type == WallHead) {
            return result * this._wallJumpExhaustionStopFactor.getValue();
        }

        if (speed == Sprinting) {
            result *= this._sprintJumpExhaustionStopFactor.getValue();
        } else if (speed == Running) {
            result *= this._runJumpExhaustionStopFactor.getValue();
        } else if (speed == Walking) {
            result *= this._walkJumpExhaustionStopFactor.getValue();
        } else if (speed == Sneaking) {
            result *= this._sneakJumpExhaustionStopFactor.getValue();
        } else if (speed == Standing) {
            result *= this._standJumpExhaustionStopFactor.getValue();
        }

        if (type == ChargeUp) {
            if (!this.isJumpExhaustionEnabled(speed, Up)) {
                result += this.getJumpExhaustionGain(speed, Up, 0);
            }

            result -= this._jumpExhaustionStopFactor.getValue() *
                        this._upJumpExhaustionStopFactor.getValue() *
                        this._jumpChargeExhaustionStopFactor.getValue() *
                        Math.min(jumpCharge, this._jumpChargeMaximum.getValue()) / this._jumpChargeMaximum.getValue();
        }

        return result;
    }

    public float getJumpChargeFactor(float jumpCharge)
    {
        if (!this.enabled || !this._jumpCharge.getValue()) {
            return 1F;
        }
        if (this._jumpChargeMaximum.getValue() == null) {
            return 1.0F;
        }
        jumpCharge = Math.min(jumpCharge, this._jumpChargeMaximum.getValue());
        return 1F + jumpCharge / this._jumpChargeMaximum.getValue() * (this._jumpChargeFactor.getValue() - 1F);
    }

    public float getHeadJumpFactor(float headJumpCharge)
    {
        if (!this.enabled || !this._headJump.getValue()) {
            return 1F;
        }
        if (this._headJumpChargeMaximum.getValue() == null) {
            return 1.0F;
        }
        headJumpCharge = Math.min(headJumpCharge, this._headJumpChargeMaximum.getValue());
        return (headJumpCharge - 1) / (this._headJumpChargeMaximum.getValue() - 1);
    }

    public float getJumpVerticalFactor(int speed, int type)
    {
        if (!this.enabled) {
            return 1F;
        }

        float result = this._jumpVerticalFactor.getValue();

        if (type == Angle && this._angleJumpVerticalFactor.getValue() != null) {
            return result * this._angleJumpVerticalFactor.getValue();
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly) {
            result *= this._climbUpJumpVerticalFactor.getValue();
        }
        if (type == ClimbUpHandsOnly) {
            result *= this._climbUpJumpHandsOnlyVerticalFactor.getValue();
        }

        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= this._climbBackUpJumpVerticalFactor.getValue();
        }
        if (type == ClimbBackUpHandsOnly) {
            result *= this._climbBackUpJumpHandsOnlyVerticalFactor.getValue();
        }

        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= this._climbBackHeadJumpVerticalFactor.getValue();
        }
        if (type == ClimbBackHeadHandsOnly) {
            result *= this._climbBackHeadJumpHandsOnlyVerticalFactor.getValue();
        }

        if (type == WallUp || type == WallHead) {
            result *= this._wallUpJumpVerticalFactor.getValue();
        }
        if (type == WallHead) {
            result *= this._wallHeadJumpVerticalFactor.getValue();
        }

        if (type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead) {
            return result;
        }

        if (speed == Sprinting) {
            result *= this._sprintJumpVerticalFactor.getValue();
        } else if (speed == Running) {
            result *= this._runJumpVerticalFactor.getValue();
        } else if (speed == Walking) {
            result *= this._walkJumpVerticalFactor.getValue();
        } else if (speed == Sneaking) {
            result *= this._sneakJumpVerticalFactor.getValue();
        } else if (speed == Standing) {
            result *= this._standJumpVerticalFactor.getValue();
        }

        return result;
    }

    public float getJumpHorizontalFactor(int speed, int type)
    {
        if (!this.enabled) {
            return speed == Running ? 2F : 1F;
        }

        float result = this._jumpHorizontalFactor.getValue();

        if (type == Angle) {
            result *= this._angleJumpHorizontalFactor.getValue();
        }

        if (type == ClimbBackUp || type == ClimbBackUpHandsOnly) {
            result *= this._climbBackUpJumpHorizontalFactor.getValue();
        }
        if (type == ClimbBackUpHandsOnly) {
            result *= this._climbBackUpJumpHandsOnlyHorizontalFactor.getValue();
        }

        if (type == ClimbBackHead || type == ClimbBackHeadHandsOnly) {
            result *= this._climbBackHeadJumpHorizontalFactor.getValue();
        }
        if (type == ClimbBackHeadHandsOnly) {
            result *= this._climbBackHeadJumpHandsOnlyHorizontalFactor.getValue();
        }

        if (type == WallUp) {
            result *= this._wallUpJumpHorizontalFactor.getValue();
        }
        if (type == WallHead) {
            result *= this._wallHeadJumpHorizontalFactor.getValue();
        }

        if (type == Angle || type == ClimbUp || type == ClimbUpHandsOnly || type == ClimbBackUp || type == ClimbBackUpHandsOnly || type == ClimbBackHead || type == ClimbBackHeadHandsOnly || type == WallUp || type == WallHead) {
            return result;
        }

        if (speed == Sprinting) {
            result *= this._sprintJumpHorizontalFactor.getValue();
        } else if (speed == Running) {
            result *= this._runJumpHorizontalFactor.getValue();
        } else if (speed == Walking) {
            result *= this._walkJumpHorizontalFactor.getValue();
        } else if (speed == Sneaking) {
            result *= this._sneakJumpHorizontalFactor.getValue();
        } else if (speed == Standing) {
            result *= 0F;
        }

        return result;
    }

    public float getMaxHorizontalMotion(int speed, boolean inWater)
    {
        float maxMotion = 0.117852041920949F;
        if (!this.enabled) {
            return speed == Running ? maxMotion * 1.3F : maxMotion;
        }

        if (inWater) {
            maxMotion = 0.07839602977037292F;
        }

        if (speed == Sprinting) {
            maxMotion *= this._sprintFactor.getValue();
        } else if (speed == Running) {
            maxMotion *= this._runFactor.getValue();
        } else if (speed == Sneaking) {
            maxMotion *= this._sneakFactor.getValue();
        }

        return maxMotion;
    }

    public float getMaxExhaustion()
    {
        float result = 0F;

        if (this._run.getValue() && this._runExhaustion.getValue()) {
            result = max(result, this._runExhaustionStop.getValue());
        }

        if (this._sprint.getValue() && this._sprintExhaustion.getValue()) {
            result = max(result, this._sprintExhaustionStop.getValue());
        }

        if (this._jump.getValue() == null) {
            return result;
        }
        if (this._jump.getValue()) {
            for (int i = Sprinting; i <= Standing; i++) {
                for (int n = Up; n <= WallHeadSlide; n++) {
                    if (this.isJumpExhaustionEnabled(i, n)) {
                        for (int t = 0; t <= 1; t++) {
                            result = max(result, this.getJumpExhaustionStop(i, n, t) + this.getJumpExhaustionGain(i, n, t));
                        }
                    }
                }
            }
        }

        if (this._freeClimb.getValue() && this._climbExhaustion.getValue()) {
            result = max(result, this._climbExhaustionStop.getValue());
        }

        if (this._ceilingClimbing.getValue() && this._ceilingClimbExhaustion.getValue()) {
            result = max(result, this._ceilingClimbExhaustionStop.getValue());
        }
        return result;
    }

    private static float max(float value, float valueOrInfinite)
    {
        return valueOrInfinite == java.lang.Float.POSITIVE_INFINITY ? value : Math.max(value, valueOrInfinite);
    }

    public float getFactor(boolean hunger, boolean onGround, boolean isStanding, boolean isStill, boolean isSneaking, boolean isRunning, boolean isSprinting, boolean isClimbing, boolean isClimbCrawling, boolean isCeilingClimbing, boolean isDipping, boolean isSwimming, boolean isDiving, boolean isCrawling, boolean isCrawlClimbing)
    {
        isClimbing |= isClimbCrawling;
        isCrawling |= isCrawlClimbing;
        boolean actionOverGound = isClimbing || isCeilingClimbing || isDiving || isSwimming;
        boolean airBorne = !onGround && !actionOverGound;
        isStanding = actionOverGound ? isStill : isStanding;
        isSneaking = isSneaking & !isStanding;

        float factor = hunger ? this._baseHungerGainFactor.getValue() : this._baseExhautionLossFactor.getValue();
        if (airBorne) {
            factor *= hunger ? 0F : this._fallExhautionLossFactor.getValue();
        } else if (isSprinting) {
            factor *= hunger ? this._sprintingHungerGainFactor.getValue() : this._sprintingExhautionLossFactor.getValue();
        } else if (isRunning) {
            factor *= hunger ? this._runningHungerGainFactor.getValue() : this._runningExhautionLossFactor.getValue();
        } else if (isSneaking) {
            factor *= hunger ? this._sneakingHungerGainFactor.getValue() : this._sneakingExhautionLossFactor.getValue();
        } else if (isStanding) {
            factor *= hunger ? this._standingHungerGainFactor.getValue() : this._standingExhautionLossFactor.getValue();
        } else {
            factor *= hunger ? this._walkingHungerGainFactor.getValue() : this._walkingExhautionLossFactor.getValue();
        }

        if (isClimbing) {
            factor *= hunger ? this._climbingHungerGainFactor.getValue() : this._climbingExhaustionLossFactor.getValue();
        } else if (isCrawling) {
            factor *= hunger ? this._crawlingHungerGainFactor.getValue() : this._crawlingExhaustionLossFactor.getValue();
        } else if (isCeilingClimbing) {
            factor *= hunger ? this._ceilClimbingHungerGainFactor.getValue() : this._ceilClimbingExhaustionLossFactor.getValue();
        } else if (isSwimming) {
            factor *= hunger ? this._swimmingHungerGainFactor.getValue() : this._swimmingExhaustionLossFactor.getValue();
        } else if (isDiving) {
            factor *= hunger ? this._divingHungerGainFactor.getValue() : this._divingExhaustionLossFactor.getValue();
        } else if (isDipping) {
            factor *= hunger ? this._dippingHungerGainFactor.getValue() : this._dippingExhaustionLossFactor.getValue();
        } else if (onGround) {
            factor *= hunger ? this._normalHungerGainFactor.getValue() : this._normalExhaustionLossFactor.getValue();
        } else {
            factor *= hunger ? this._normalHungerGainFactor.getValue() : this._normalExhaustionLossFactor.getValue();
        }

        return factor;
    }
}