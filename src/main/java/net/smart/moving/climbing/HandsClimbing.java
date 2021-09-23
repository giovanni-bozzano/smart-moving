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

public class HandsClimbing
{
    public static final int MIDDLE_GRAB = 2;
    public static final int UP_GRAB = 1;
    public static final int NO_GRAB = 0;
    public static final HandsClimbing NONE = new HandsClimbing(-3);
    public static final HandsClimbing SINK = new HandsClimbing(-2);
    public static final HandsClimbing TOP_HOLD = new HandsClimbing(-1);
    public static final HandsClimbing BOTTOM_HOLD = new HandsClimbing(0);
    public static final HandsClimbing UP = new HandsClimbing(1);
    public static final HandsClimbing FAST_UP = new HandsClimbing(2);

    private final int _value;

    private HandsClimbing(int value)
    {
        this._value = value;
    }

    public boolean IsRelevant()
    {
        return this._value > NONE._value;
    }

    public boolean IsUp()
    {
        return this == UP || this == FAST_UP;
    }

    public HandsClimbing ToUp()
    {
        if (this == BOTTOM_HOLD)
        {
            return UP;
        }
        return this;
    }

    public HandsClimbing ToDown()
    {
        if (this == TOP_HOLD)
        {
            return SINK;
        }
        return this;
    }

    public HandsClimbing max(HandsClimbing other, ClimbGap inout_thisClimbGap, ClimbGap otherClimbGap)
    {
        if (!otherClimbGap.skipGaps)
        {
            inout_thisClimbGap.canStand |= otherClimbGap.canStand;
            inout_thisClimbGap.mustCrawl |= otherClimbGap.mustCrawl;
        }
        if (this._value < other._value)
        {
            inout_thisClimbGap.block = otherClimbGap.block;
            inout_thisClimbGap.meta = otherClimbGap.meta;
            inout_thisClimbGap.direction = otherClimbGap.direction;
        }
        return get(Math.max(this._value, other._value));
    }

    @Override
    public String toString()
    {
        if (this._value <= NONE._value)
        {
            return "None";
        }
        if (this._value == SINK._value)
        {
            return "Sink";
        }
        if (this._value == BOTTOM_HOLD._value)
        {
            return "BottomHold";
        }
        if (this._value == TOP_HOLD._value)
        {
            return "TopHold";
        }
        if (this._value == UP._value)
        {
            return "Up";
        }
        return "FastUp";
    }

    private static HandsClimbing get(int value)
    {
        if (value <= NONE._value)
        {
            return NONE;
        }
        if (value == SINK._value)
        {
            return SINK;
        }
        if (value == BOTTOM_HOLD._value)
        {
            return BOTTOM_HOLD;
        }
        if (value == TOP_HOLD._value)
        {
            return TOP_HOLD;
        }
        if (value == UP._value)
        {
            return UP;
        }
        return FAST_UP;
    }
}