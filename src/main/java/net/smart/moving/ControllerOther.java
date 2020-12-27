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

import net.minecraft.client.entity.EntityOtherPlayerMP;

public class ControllerOther extends Controller
{
    public boolean foundAlive;
    private boolean isJumping = false;
    private boolean doFlyingAnimation = false;
    private boolean doFallingAnimation = false;

    public ControllerOther(EntityOtherPlayerMP sp)
    {
        super(sp, null);
    }

    public void processStatePacket(long state)
    {
        this.actualFeetClimbType = (int) (state & 15);
        state >>>= 4;
        this.actualHandsClimbType = (int) (state & 15);
        state >>>= 4;
        this.isJumping = (state & 1) != 0;
        state >>>= 1;
        this.isDiving = (state & 1) != 0;
        state >>>= 1;
        this.isDipping = (state & 1) != 0;
        state >>>= 1;
        this.isSwimming = (state & 1) != 0;
        state >>>= 1;
        this.isCrawlClimbing = (state & 1) != 0;
        state >>>= 1;
        this.isCrawling = (state & 1) != 0;
        state >>>= 1;
        this.isClimbing = (state & 1) != 0;
        state >>>= 1;
        boolean isSmall = (state & 1) != 0;
        this.heightOffset = isSmall ? -1 : 0;
        this.entityPlayer.height = 1.8F + this.heightOffset;
        state >>>= 1;
        this.doFallingAnimation = (state & 1) != 0;
        state >>>= 1;
        this.doFlyingAnimation = (state & 1) != 0;
        state >>>= 1;
        this.isCeilingClimbing = (state & 1) != 0;
        state >>>= 1;
        this.isLevitating = (state & 1) != 0;
        state >>>= 1;
        this.isHeadJumping = (state & 1) != 0;
        state >>>= 1;
        this.isSliding = (state & 1) != 0;
        state >>>= 1;
        this.angleJumpType = (int) (state & 7);
        state >>>= 3;
        this.isFeetVineClimbing = (state & 1) != 0;
        state >>>= 1;
        this.isHandsVineClimbing = (state & 1) != 0;
        state >>>= 1;
        this.isClimbJumping = (state & 1) != 0;
        state >>>= 1;
        boolean wasClimbBackJumping = this.isClimbBackJumping;
        this.isClimbBackJumping = (state & 1) != 0;
        if (!wasClimbBackJumping && this.isClimbBackJumping) {
            this.onStartClimbBackJump();
        }
        state >>>= 1;
        this.isSlow = (state & 1) != 0;
        state >>>= 1;
        this.isFast = (state & 1) != 0;
        state >>>= 1;
        boolean wasWallJumping = this.isWallJumping;
        this.isWallJumping = (state & 1) != 0;
        if (!wasWallJumping && this.isWallJumping) {
            this.onStartWallJump(null);
        }
        state >>>= 1;
        this.isRopeSliding = (state & 1) != 0;
    }

    @Override
    public boolean isJumping()
    {
        return this.isJumping;
    }

    @Override
    public boolean doFlyingAnimation()
    {
        return this.doFlyingAnimation;
    }

    @Override
    public boolean doFallingAnimation()
    {
        return this.doFallingAnimation;
    }
}