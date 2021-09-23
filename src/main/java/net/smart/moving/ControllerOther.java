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
import net.minecraft.nbt.NBTTagCompound;
import net.smart.moving.asm.interfaces.IEntity;

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

    public void processStatePacket(NBTTagCompound state)
    {
        this.actualFeetClimbType = state.getInteger("actual_feet_climb_type");
        this.actualHandsClimbType = state.getInteger("actual_hands_climb_type");
        this.isJumping = state.getBoolean("is_jumping");
        this.isDiving = state.getBoolean("is_diving");
        this.isDipping = state.getBoolean("is_dipping");
        this.isSwimming = state.getBoolean("is_swimming");
        this.isCrawlClimbing = state.getBoolean("is_crawl_climbing");
        this.isCrawling = state.getBoolean("is_crawling");
        this.isClimbing = state.getBoolean("is_climbing");
        boolean isSmall = state.getBoolean("is_small");
        this.heightOffset = isSmall ? -1 : 0;
        ((IEntity) this.entityPlayer).publicSetSize(this.entityPlayer.width, 1.8F + this.heightOffset);
        this.doFallingAnimation = state.getBoolean("is_falling");
        this.doFlyingAnimation = state.getBoolean("is_flying");
        this.isCeilingClimbing = state.getBoolean("is_ceiling_climbing");
        this.isLevitating = state.getBoolean("is_levitating");
        this.isHeadJumping = state.getBoolean("is_head_jumping");
        this.isSliding = state.getBoolean("is_sliding");
        this.angleJumpType = state.getInteger("angle_jump_type");
        this.isFeetVineClimbing = state.getBoolean("is_feet_vine_climbing");
        this.isHandsVineClimbing = state.getBoolean("is_hands_vine_climbing");
        this.isClimbJumping = state.getBoolean("is_climb_jumping");
        boolean wasClimbBackJumping = this.isClimbBackJumping;
        this.isClimbBackJumping = state.getBoolean("is_climb_back_jumping");
        if (!wasClimbBackJumping && this.isClimbBackJumping)
        {
            this.onStartClimbBackJump();
        }
        this.isSlow = state.getBoolean("is_slow");
        this.isFast = state.getBoolean("is_fast");
        boolean wasWallJumping = this.isWallJumping;
        this.isWallJumping = state.getBoolean("is_wall_jumping");
        if (!wasWallJumping && this.isWallJumping)
        {
            this.onStartWallJump(null);
        }
        this.isRopeSliding = state.getBoolean("is_rope_sliding");
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