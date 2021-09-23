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
package net.smart.moving.climbing;

public class FeetClimbing
{
    public static final int DOWN_STEP = 1;
    public static final int NO_STEP = 0;
    public static final FeetClimbing NONE = new FeetClimbing(-3);
    public static final FeetClimbing BASE_HOLD = new FeetClimbing(-2);
    public static final FeetClimbing BASE_WITH_HANDS = new FeetClimbing(-1);
    public static final FeetClimbing TOP_WITH_HANDS = new FeetClimbing(0);
    public static final FeetClimbing SLOW_UP_WITH_HOLD_WITHOUT_HANDS = new FeetClimbing(1);
    public static final FeetClimbing SLOW_UP_WITH_SINK_WITHOUT_HANDS = new FeetClimbing(2);
    public static final FeetClimbing FAST_UP = new FeetClimbing(3);

    private final int value;

    private FeetClimbing(int value)
    {
        this.value = value;
    }

    public boolean IsRelevant()
    {
        return this.value > NONE.value;
    }

    public boolean IsIndependentlyRelevant()
    {
        return this.value > BASE_WITH_HANDS.value;
    }

    public FeetClimbing max(FeetClimbing other, ClimbGap inout_thisClimbGap, ClimbGap otherClimbGap)
    {
        if (!otherClimbGap.skipGaps)
        {
            inout_thisClimbGap.canStand |= otherClimbGap.canStand;
            inout_thisClimbGap.mustCrawl |= otherClimbGap.mustCrawl;
        }
        if (this.value < other.value)
        {
            inout_thisClimbGap.block = otherClimbGap.block;
            inout_thisClimbGap.meta = otherClimbGap.meta;
            inout_thisClimbGap.direction = otherClimbGap.direction;
        }
        return get(Math.max(this.value, other.value));
    }

    @Override
    public String toString()
    {
        if (this.value <= NONE.value)
        {
            return "None";
        }
        if (this.value == BASE_HOLD.value)
        {
            return "BaseHold";
        }
        if (this.value == BASE_WITH_HANDS.value)
        {
            return "BaseWithHands";
        }
        if (this.value == TOP_WITH_HANDS.value)
        {
            return "TopWithHands";
        }
        if (this.value == SLOW_UP_WITH_HOLD_WITHOUT_HANDS.value)
        {
            return "SlowUpWithHoldWithoutHands";
        }
        if (this.value == SLOW_UP_WITH_SINK_WITHOUT_HANDS.value)
        {
            return "SlowUpWithSinkWithoutHands";
        }
        return "FastUp";
    }

    private static FeetClimbing get(int value)
    {
        if (value <= NONE.value)
        {
            return NONE;
        }
        if (value == BASE_HOLD.value)
        {
            return BASE_HOLD;
        }
        if (value == BASE_WITH_HANDS.value)
        {
            return BASE_WITH_HANDS;
        }
        if (value == TOP_WITH_HANDS.value)
        {
            return TOP_WITH_HANDS;
        }
        if (value == SLOW_UP_WITH_HOLD_WITHOUT_HANDS.value)
        {
            return SLOW_UP_WITH_HOLD_WITHOUT_HANDS;
        }
        if (value == SLOW_UP_WITH_SINK_WITHOUT_HANDS.value)
        {
            return SLOW_UP_WITH_SINK_WITHOUT_HANDS;
        }
        return FAST_UP;
    }
}