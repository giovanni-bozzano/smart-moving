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

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smart.moving.asm.interfaces.IBlockWall;
import net.smart.moving.playerapi.CustomClientPlayerEntityBase;
import net.smart.render.SmartRenderRender;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static net.smart.render.SmartRenderUtilities.*;

public abstract class Controller extends ContextBase
{
    public final EntityPlayer entityPlayer;
    public final CustomClientPlayerEntityBase playerBase;
    public boolean isSlow;
    public boolean isFast;
    public boolean isClimbing;
    public boolean isHandsVineClimbing;
    public boolean isFeetVineClimbing;
    public boolean isClimbJumping;
    public boolean isClimbBackJumping;
    public boolean isWallJumping;
    public boolean isClimbCrawling;
    public boolean isCrawlClimbing;
    public boolean isCeilingClimbing;
    public boolean isRopeSliding;
    public boolean isDipping;
    public boolean isSwimming;
    public boolean isDiving;
    public boolean isLevitating;
    public boolean isHeadJumping;
    public boolean isCrawling;
    public boolean isSliding;
    public boolean isFlying;
    public int actualHandsClimbType;
    public int actualFeetClimbType;
    public int angleJumpType;
    public float heightOffset;
    private float spawnSlidingParticle;
    private float spawnSwimmingParticle;

    public Controller(EntityPlayer player, CustomClientPlayerEntityBase playerBase)
    {
        this.entityPlayer = player;
        this.playerBase = playerBase;
    }

    public Block getBlock(int x, int y, int z)
    {
        return getBlock(this.entityPlayer.world, x, y, z);
    }

    public IBlockState getState(BlockPos blockPos)
    {
        return getState(this.entityPlayer.world, blockPos);
    }

    public IBlockState getState(int x, int y, int z)
    {
        return getState(this.entityPlayer.world, x, y, z);
    }

    public Material getMaterial(int x, int y, int z)
    {
        return getMaterial(this.entityPlayer.world, x, y, z);
    }

    public boolean isAirBlock(int x, int y, int z)
    {
        return this.entityPlayer.world.isAirBlock(new BlockPos(x, y, z));
    }

    public AxisAlignedBB getBoundingBox()
    {
        return this.entityPlayer.getEntityBoundingBox();
    }

    public void setBoundingBox(AxisAlignedBB boundingBox)
    {
        this.entityPlayer.setEntityBoundingBox(boundingBox);
    }

    protected void moveFlying(float moveUpward, float moveStrafing, float moveForward, float speedFactor, boolean treeDimensional)
    {
        float diffMotionXStrafing = 0, diffMotionXForward = 0, diffMotionZStrafing = 0, diffMotionZForward = 0;
        {
            float total = MathHelper.sqrt(moveStrafing * moveStrafing + moveForward * moveForward);
            if (total >= 0.01F)
            {
                if (total < 1.0F)
                {
                    total = 1.0F;
                }

                float moveStrafingFactor = moveStrafing / total;
                float moveForwardFactor = moveForward / total;
                float sin = MathHelper.sin((this.entityPlayer.rotationYaw * 3.141593F) / 180F);
                float cos = MathHelper.cos((this.entityPlayer.rotationYaw * 3.141593F) / 180F);
                diffMotionXStrafing = moveStrafingFactor * cos;
                diffMotionXForward = -moveForwardFactor * sin;
                diffMotionZStrafing = moveStrafingFactor * sin;
                diffMotionZForward = moveForwardFactor * cos;
            }
        }

        float rotation = treeDimensional ? this.entityPlayer.rotationPitch / RadiantToAngle : 0;
        float divingHorizontalFactor = MathHelper.cos(rotation);
        float divingVerticalFactor = -MathHelper.sin(rotation) * Math.signum(moveForward);

        float diffMotionX = diffMotionXForward * divingHorizontalFactor + diffMotionXStrafing;
        float diffMotionY = MathHelper.sqrt(diffMotionXForward * diffMotionXForward + diffMotionZForward * diffMotionZForward) * divingVerticalFactor + moveUpward;
        float diffMotionZ = diffMotionZForward * divingHorizontalFactor + diffMotionZStrafing;

        float total = MathHelper.sqrt(MathHelper.sqrt(diffMotionX * diffMotionX + diffMotionZ * diffMotionZ) + diffMotionY * diffMotionY);
        if (total > 0.01F)
        {
            float factor = speedFactor / total;
            this.entityPlayer.motionX += diffMotionX * factor;
            this.entityPlayer.motionY += diffMotionY * factor;
            this.entityPlayer.motionZ += diffMotionZ * factor;
        }
    }

    protected Block supportsCeilingClimbing(int i, int j, int k)
    {
        IBlockState state = this.getState(i, j, k);
        if (state == null)
        {
            return null;
        }

        String name = state.getBlock().getTranslationKey();

        if (name.equals("tile.fenceIron"))
        {
            return state.getBlock();
        }

        if ((name.equals("tile.trapdoor") || name.equals("tile.trapdoor_iron")) && !getValue(state, BlockTrapDoor.OPEN))
        {
            return state.getBlock();
        }

        return null;
    }

    protected boolean isLava(IBlockState state)
    {
        return state.getMaterial() == Material.LAVA;
    }

    protected float getLiquidBorder(int i, int j, int k)
    {
        Block block = this.getBlock(i, j, k);
        if (block == Block.getBlockFromName("water") || block == Block.getBlockFromName("flowing_water"))
        {
            return this.getNormalWaterBorder(i, j, k);
        }
        if (block == Block.getBlockFromName("lava") || block == Block.getBlockFromName("flowing_lava"))
        {
            return SmartMovingConfig.LAVA.likeWater ? this.getNormalWaterBorder(i, j, k) : 0F;
        }

        Material material = this.getMaterial(i, j, k);
        if (material == null || material == Material.LAVA)
        {
            return SmartMovingConfig.LAVA.likeWater ? 1F : 0F;
        }
        if (material == Material.WATER)
        {
            return this.getNormalWaterBorder(i, j, k);
        }
        if (material.isLiquid())
        {
            return 1F;
        }

        return 0F;
    }

    protected float getNormalWaterBorder(int i, int j, int k)
    {
        int level = getValue(this.getState(i, j, k), BlockLiquid.LEVEL);
        if (level >= 8)
        {
            return 1F;
        }
        if (level == 0)
        {
            if (this.getMaterial(i, j + 1, k) == Material.AIR)
            {
                return 0.8875F;
            }
            else
            {
                return 1F;
            }
        }
        return (8 - level) / 8F;
    }

    public boolean isFacedToLadder(boolean isSmall)
    {
        return this.getOnLadder(1, true, isSmall) > 0;
    }

    public boolean isFacedToSolidVine(boolean isSmall)
    {
        return this.getOnVine(1, true, isSmall) > 0;
    }

    public boolean isOnLadderOrVine(boolean isSmall)
    {
        return this.getOnLadderOrVine(1, false, isSmall) > 0;
    }

    public boolean isOnVine(boolean isSmall)
    {
        return this.getOnLadderOrVine(1, false, false, true, isSmall) > 0;
    }

    public boolean isOnLadder(boolean isSmall)
    {
        return this.getOnLadderOrVine(1, false, true, false, isSmall) > 0;
    }

    protected int getOnLadder(int maxResult, boolean faceOnly, boolean isSmall)
    {
        return this.getOnLadderOrVine(maxResult, faceOnly, true, false, isSmall);
    }

    protected int getOnVine(int maxResult, boolean faceOnly, boolean isSmall)
    {
        return this.getOnLadderOrVine(maxResult, faceOnly, false, true, isSmall);
    }

    protected int getOnLadderOrVine(int maxResult, boolean faceOnly, boolean isSmall)
    {
        return this.getOnLadderOrVine(maxResult, faceOnly, true, true, isSmall);
    }

    protected int getOnLadderOrVine(int maxResult, boolean faceOnly, boolean ladder, boolean vine, boolean isSmall)
    {
        int i = MathHelper.floor(this.entityPlayer.posX);
        int minj = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.entityPlayer.posZ);

        if (ConfigHelper.isStandardBaseClimb())
        {
            Block block = getBlock(this.entityPlayer.world, i, minj, k);
            if (ladder)
            {
                if (vine)
                {
                    return Orientation.isClimbable(this.entityPlayer.world, i, minj, k) ? 1 : 0;
                }
                else
                {
                    return block != Block.getBlockFromName("vine") && Orientation.isClimbable(this.entityPlayer.world, i, minj, k) ? 1 : 0;
                }
            }
            else if (vine)
            {
                return block == Block.getBlockFromName("vine") && Orientation.isClimbable(this.entityPlayer.world, i, minj, k) ? 1 : 0;
            }
            else
            {
                return 0;
            }
        }
        else
        {
            if (isSmall)
            {
                minj--;
            }

            HashSet<Orientation> facedOnlyTo = null;
            if (faceOnly)
            {
                facedOnlyTo = Orientation.getClimbingOrientations(this.entityPlayer, true, false);
            }

            int result = 0;
            int maxj = MathHelper.floor(this.entityPlayer.getEntityBoundingBox().minY + Math.ceil(this.getBoundingBox().maxY - this.getBoundingBox().minY)) - 1;
            for (int j = minj; j <= maxj; j++)
            {
                IBlockState state = this.getState(i, j, k);
                if (ladder)
                {
                    boolean localLadder = Orientation.isKnownLadder(state);
                    Orientation localLadderOrientation = null;
                    if (localLadder)
                    {
                        localLadderOrientation = Orientation.getKnownLadderOrientation(this.entityPlayer.world, i, j, k);
                        if (facedOnlyTo == null || facedOnlyTo.contains(localLadderOrientation))
                        {
                            result++;
                        }
                    }

                    for (Orientation direction : facedOnlyTo != null ? facedOnlyTo : Orientation.ORTHOGONALS)
                    {
                        if (result >= maxResult)
                        {
                            return result;
                        }

                        if (direction != localLadderOrientation)
                        {
                            IBlockState remoteState = this.getState(i + direction.i, j, k + direction.k);
                            if (Orientation.isKnownLadder(remoteState))
                            {
                                Orientation remoteLadderOrientation = Orientation.getKnownLadderOrientation(this.entityPlayer.world, i + direction.i, j, k + direction.k);
                                if (remoteLadderOrientation.rotate(180) == direction)
                                {
                                    result++;
                                }
                            }
                        }
                    }
                }

                if (result >= maxResult)
                {
                    return result;
                }

                if (vine && Orientation.isVine(state))
                {
                    if (facedOnlyTo == null)
                    {
                        result++;
                    }
                    else
                    {
                        for (Orientation climbOrientation : facedOnlyTo)
                        {
                            if (climbOrientation.hasVineOrientation(this.entityPlayer.world, i, j, k) && climbOrientation.isRemoteSolid(this.entityPlayer.world, i, j, k))
                            {
                                result++;
                                break;
                            }
                        }
                    }
                }

                if (result >= maxResult)
                {
                    return result;
                }
            }
            return result;
        }
    }

    public boolean climbingUpIsBlockedByLadder()
    {
        if (this.entityPlayer.collidedHorizontally && this.entityPlayer.collidedVertically && !this.entityPlayer.onGround && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F)
        {
            Orientation orientation = Orientation.getOrientation(this.entityPlayer, 20F, true, false);
            if (orientation != null)
            {
                int i = MathHelper.floor(this.entityPlayer.posX);
                int j = MathHelper.floor(this.getBoundingBox().maxY);
                int k = MathHelper.floor(this.entityPlayer.posZ);
                if (Orientation.isLadder(this.getState(i, j, k)))
                {
                    return Orientation.getKnownLadderOrientation(this.entityPlayer.world, i, j, k) == orientation;
                }
            }
        }
        return false;
    }

    public boolean climbingUpIsBlockedByTrapDoor()
    {
        if (this.entityPlayer.collidedHorizontally && this.entityPlayer.collidedVertically && !this.entityPlayer.onGround && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F)
        {
            Orientation orientation = Orientation.getOrientation(this.entityPlayer, 20F, true, false);
            if (orientation != null)
            {
                int i = MathHelper.floor(this.entityPlayer.posX);
                int j = MathHelper.floor(this.getBoundingBox().maxY);
                int k = MathHelper.floor(this.entityPlayer.posZ);
                if (Orientation.isTrapDoor(this.getState(i, j, k)))
                {
                    return Orientation.getOpenTrapDoorOrientation(this.entityPlayer.world, i, j, k) == orientation;
                }
            }
        }
        return false;
    }

    public boolean climbingUpIsBlockedByCobbleStoneWall()
    {
        if (this.entityPlayer.collidedHorizontally && this.entityPlayer.collidedVertically && !this.entityPlayer.onGround && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F)
        {
            Orientation orientation = Orientation.getOrientation(this.entityPlayer, 20F, true, false);
            if (orientation != null)
            {
                int i = MathHelper.floor(this.entityPlayer.posX);
                int j = MathHelper.floor(this.getBoundingBox().maxY);
                int k = MathHelper.floor(this.entityPlayer.posZ);
                Block block = this.getBlock(i, j, k);
                if (block == Block.getBlockFromName("cobblestone_wall"))
                {
                    ((IBlockWall) block).publicCanConnectTo(this.entityPlayer.world, new BlockPos(i - orientation.i, j, k - orientation.k), orientation.facing);
                }
            }
        }
        return false;
    }

    private List<?> getPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
    {
        AxisAlignedBB bb = this.getBoundingBox();
        bb = new AxisAlignedBB(bb.minX, yMin, bb.minZ, bb.maxX, yMax, bb.maxZ);
        return this.entityPlayer.world.getCollisionBoxes(this.entityPlayer, horizontalTolerance == 0 ? bb : bb.expand(horizontalTolerance, 0, horizontalTolerance));
    }

    protected boolean isPlayerInSolidBetween(double yMin, double yMax)
    {
        return this.getPlayerSolidBetween(yMin, yMax, 0).size() > 0;
    }

    protected double getMaxPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
    {
        List<?> solids = this.getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
        double result = yMin;
        for (Object solid : solids)
        {
            AxisAlignedBB box = (AxisAlignedBB) solid;
            if (this.isCollided(box, yMin, yMax, horizontalTolerance))
            {
                result = Math.max(result, box.maxY);
            }
        }
        return Math.min(result, yMax);
    }

    protected double getMinPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
    {
        List<?> solids = this.getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
        double result = yMax;
        for (Object solid : solids)
        {
            AxisAlignedBB box = (AxisAlignedBB) solid;
            if (this.isCollided(box, yMin, yMax, horizontalTolerance))
            {
                result = Math.min(result, box.minY);
            }
        }
        return Math.max(result, yMin);
    }

    protected boolean isInLiquid()
    {
        return this.getMaxPlayerLiquidBetween(this.getBoundingBox().minY, this.getBoundingBox().maxY) != this.getBoundingBox().minY || this.getMinPlayerLiquidBetween(this.getBoundingBox().minY, this.getBoundingBox().maxY) != this.getBoundingBox().maxY;
    }

    protected double getMaxPlayerLiquidBetween(double yMin, double yMax)
    {
        int i = MathHelper.floor(this.entityPlayer.posX);
        int jMin = MathHelper.floor(yMin);
        int jMax = MathHelper.floor(yMax);
        int k = MathHelper.floor(this.entityPlayer.posZ);

        for (int j = jMax; j >= jMin; j--)
        {
            float swimWaterBorder = this.getLiquidBorder(i, j, k);
            if (swimWaterBorder > 0)
            {
                return j + swimWaterBorder;
            }
        }
        return yMin;
    }

    protected double getMinPlayerLiquidBetween(double yMin, double yMax)
    {
        int i = MathHelper.floor(this.entityPlayer.posX);
        int jMin = MathHelper.floor(yMin);
        int jMax = MathHelper.floor(yMax);
        int k = MathHelper.floor(this.entityPlayer.posZ);

        for (int j = jMin; j <= jMax; j++)
        {
            float swimWaterBorder = this.getLiquidBorder(i, j, k);
            if (swimWaterBorder > 0)
            {
                if (j > yMin)
                {
                    return j;
                }
                else if (j + swimWaterBorder > yMin)
                {
                    return yMin;
                }
            }
        }
        return yMax;
    }

    public boolean isCollided(AxisAlignedBB box, double yMin, double yMax, double horizontalTolerance)
    {
        return box.maxX >= this.getBoundingBox().minX - horizontalTolerance && box.minX <= this.getBoundingBox().maxX + horizontalTolerance && box.maxY >= yMin && box.minY <= yMax && box.maxZ >= this.getBoundingBox().minZ - horizontalTolerance && box.minZ <= this.getBoundingBox().maxZ + horizontalTolerance;
    }

    private boolean isHeadspaceFree(BlockPos pos, int height, boolean top)
    {
        for (int y = 0; y < height; y++)
        {
            if (this.isOpenBlockSpace(pos.add(0, y, 0), top))
            {
                return false;
            }
        }
        return true;
    }

    protected boolean pushOutOfBlocks(double x, double y, double z, boolean top)
    {
        BlockPos blockpos = new BlockPos(x, y, z);
        double d3 = x - blockpos.getX();
        double d4 = z - blockpos.getZ();

        int entHeight = Math.max(Math.round(this.entityPlayer.height), 1);

        boolean inTranslucentBlock = this.isHeadspaceFree(blockpos, entHeight, top);
        if (inTranslucentBlock)
        {
            byte b0 = -1;
            double d5 = 9999.0D;
            if ((!this.isHeadspaceFree(blockpos.west(), entHeight, top)) && (d3 < d5))
            {
                d5 = d3;
                b0 = 0;
            }
            if ((!this.isHeadspaceFree(blockpos.east(), entHeight, top)) && (1.0D - d3 < d5))
            {
                d5 = 1.0D - d3;
                b0 = 1;
            }
            if ((!this.isHeadspaceFree(blockpos.north(), entHeight, top)) && (d4 < d5))
            {
                d5 = d4;
                b0 = 4;
            }
            if ((!this.isHeadspaceFree(blockpos.south(), entHeight, top)) && (1.0D - d4 < d5))
            {
                b0 = 5;
            }

            float f = 0.1F;
            if (b0 == 0)
            {
                this.entityPlayer.motionX = -f;
            }
            if (b0 == 1)
            {
                this.entityPlayer.motionX = f;
            }
            if (b0 == 4)
            {
                this.entityPlayer.motionZ = -f;
            }
            if (b0 == 5)
            {
                this.entityPlayer.motionZ = f;
            }
        }
        return false;
    }

    private boolean isOpenBlockSpace(BlockPos pos, boolean top)
    {
        IBlockState blockState = this.getState(pos);
        IBlockState upBlockState = this.getState(pos.up());
        return !blockState.getBlock().isNormalCube(blockState, this.entityPlayer.world, pos) && (!top || !upBlockState.getBlock().isNormalCube(blockState, this.entityPlayer.world, pos.up()));
    }

    public boolean isInsideOfMaterial(Material material)
    {
        return this.playerBase.localIsInsideOfMaterial(material);
    }

    public int calculateSeparateCollisions(double x, double y, double z)
    {
        boolean isInWeb = this.playerBase.getIsInWebField();
        AxisAlignedBB boundingBox = this.entityPlayer.getEntityBoundingBox();
        boolean onGround = this.entityPlayer.onGround;
        World worldObj = this.entityPlayer.world;
        Entity _this = this.entityPlayer;
        float stepHeight = this.entityPlayer.stepHeight;

        if (isInWeb)
        {
            x *= 0.25D;
            y *= 0.05D;
            z *= 0.25D;
        }
        double d6 = x;
        double d7 = y;
        double d8 = z;
        boolean flag = onGround && this.isSneaking();
        if (flag)
        {
            double d9 = 0.05D;
            for (; (x != 0.0D) && (worldObj.getCollisionBoxes(_this, boundingBox.offset(x, -1.0D, 0.0D)).isEmpty()); d6 = x)
            {
                if ((x < d9) && (x >= -d9))
                {
                    x = 0.0D;
                }
                else if (x > 0.0D)
                {
                    x -= d9;
                }
                else
                {
                    x += d9;
                }
            }
            for (; (z != 0.0D) && (worldObj.getCollisionBoxes(_this, boundingBox.offset(0.0D, -1.0D, z)).isEmpty()); d8 = z)
            {
                if ((z < d9) && (z >= -d9))
                {
                    z = 0.0D;
                }
                else if (z > 0.0D)
                {
                    z -= d9;
                }
                else
                {
                    z += d9;
                }
            }
            for (; (x != 0.0D) && (z != 0.0D) && (worldObj.getCollisionBoxes(_this, boundingBox.offset(x, -1.0D, z)).isEmpty()); d8 = z)
            {
                if ((x < d9) && (x >= -d9))
                {
                    x = 0.0D;
                }
                else if (x > 0.0D)
                {
                    x -= d9;
                }
                else
                {
                    x += d9;
                }
                d6 = x;
                if ((z < d9) && (z >= -d9))
                {
                    z = 0.0D;
                }
                else if (z > 0.0D)
                {
                    z -= d9;
                }
                else
                {
                    z += d9;
                }
            }
        }
        List<AxisAlignedBB> list1 = worldObj.getCollisionBoxes(_this, boundingBox.expand(x, y, z));
        AxisAlignedBB axisalignedbb = boundingBox;
        AxisAlignedBB axisalignedbb1;
        for (Iterator<AxisAlignedBB> iterator = list1.iterator(); iterator.hasNext(); y = axisalignedbb1.calculateYOffset(boundingBox, y))
        {
            axisalignedbb1 = iterator.next();
        }
        boundingBox = boundingBox.offset(0.0D, y, 0.0D);
        boolean flag1 = onGround || ((d7 != y) && (d7 < 0.0D));
        AxisAlignedBB axisalignedbb2;
        for (Iterator<AxisAlignedBB> iterator8 = list1.iterator(); iterator8.hasNext(); x = axisalignedbb2.calculateXOffset(boundingBox, x))
        {
            axisalignedbb2 = iterator8.next();
        }
        boundingBox = boundingBox.offset(x, 0.0D, 0.0D);
        for (Iterator<AxisAlignedBB> iterator8 = list1.iterator(); iterator8.hasNext(); z = axisalignedbb2.calculateZOffset(boundingBox, z))
        {
            axisalignedbb2 = iterator8.next();
        }
        if ((stepHeight > 0.0F) && (flag1) && ((d6 != x) || (d8 != z)))
        {
            double d14 = x;
            double d10 = y;
            double d11 = z;
            boundingBox = axisalignedbb;
            y = stepHeight;
            List<AxisAlignedBB> list = worldObj.getCollisionBoxes(_this, boundingBox.expand(d6, y, d8));
            AxisAlignedBB axisalignedbb4 = boundingBox;
            AxisAlignedBB axisalignedbb5 = axisalignedbb4.expand(d6, 0.0D, d8);
            double d12 = y;
            AxisAlignedBB axisalignedbb6;
            for (Iterator<AxisAlignedBB> iterator1 = list.iterator(); iterator1.hasNext(); d12 = axisalignedbb6.calculateYOffset(axisalignedbb5, d12))
            {
                axisalignedbb6 = iterator1.next();
            }
            axisalignedbb4 = axisalignedbb4.offset(0.0D, d12, 0.0D);
            double d18 = d6;
            AxisAlignedBB axisalignedbb7;
            for (Iterator<AxisAlignedBB> iterator2 = list.iterator(); iterator2.hasNext(); d18 = axisalignedbb7.calculateXOffset(axisalignedbb4, d18))
            {
                axisalignedbb7 = iterator2.next();
            }
            axisalignedbb4 = axisalignedbb4.offset(d18, 0.0D, 0.0D);
            double d19 = d8;
            AxisAlignedBB axisalignedbb8;
            for (Iterator<AxisAlignedBB> iterator3 = list.iterator(); iterator3.hasNext(); d19 = axisalignedbb8.calculateZOffset(axisalignedbb4, d19))
            {
                axisalignedbb8 = iterator3.next();
            }
            axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d19);
            AxisAlignedBB axisalignedbb13 = boundingBox;
            double d20 = y;
            AxisAlignedBB axisalignedbb9;
            for (Iterator<AxisAlignedBB> iterator4 = list.iterator(); iterator4.hasNext(); d20 = axisalignedbb9.calculateYOffset(axisalignedbb13, d20))
            {
                axisalignedbb9 = iterator4.next();
            }
            axisalignedbb13 = axisalignedbb13.offset(0.0D, d20, 0.0D);
            double d21 = d6;
            AxisAlignedBB axisalignedbb10;
            for (Iterator<AxisAlignedBB> iterator5 = list.iterator(); iterator5.hasNext(); d21 = axisalignedbb10.calculateXOffset(axisalignedbb13, d21))
            {
                axisalignedbb10 = iterator5.next();
            }
            axisalignedbb13 = axisalignedbb13.offset(d21, 0.0D, 0.0D);
            double d22 = d8;
            AxisAlignedBB axisalignedbb11;
            for (Iterator<AxisAlignedBB> iterator6 = list.iterator(); iterator6.hasNext(); d22 = axisalignedbb11.calculateZOffset(axisalignedbb13, d22))
            {
                axisalignedbb11 = iterator6.next();
            }
            axisalignedbb13 = axisalignedbb13.offset(0.0D, 0.0D, d22);
            double d23 = d18 * d18 + d19 * d19;
            double d13 = d21 * d21 + d22 * d22;
            if (d23 > d13)
            {
                x = d18;
                z = d19;
                boundingBox = axisalignedbb4;
            }
            else
            {
                x = d21;
                z = d22;
                boundingBox = axisalignedbb13;
            }
            y = -stepHeight;
            AxisAlignedBB axisalignedbb12;
            for (Iterator<AxisAlignedBB> iterator7 = list.iterator(); iterator7.hasNext(); y = axisalignedbb12.calculateYOffset(boundingBox, y))
            {
                axisalignedbb12 = iterator7.next();
            }
            if (d14 * d14 + d11 * d11 >= x * x + z * z)
            {
                x = d14;
                y = d10;
                z = d11;
            }
        }

        boolean isCollidedPositiveX = d6 > x;
        boolean isCollidedNegativeX = d6 < x;
        boolean isCollidedPositiveY = d7 > y;
        boolean isCollidedNegativeY = d7 < y;
        boolean isCollidedPositiveZ = d8 > z;
        boolean isCollidedNegativeZ = d8 < z;

        int result = 0;
        if (isCollidedPositiveX)
        {
            result += CollidedPositiveX;
        }
        if (isCollidedNegativeX)
        {
            result += CollidedNegativeX;
        }
        if (isCollidedPositiveY)
        {
            result += CollidedPositiveY;
        }
        if (isCollidedNegativeY)
        {
            result += CollidedNegativeY;
        }
        if (isCollidedPositiveZ)
        {
            result += CollidedPositiveZ;
        }
        if (isCollidedNegativeZ)
        {
            result += CollidedNegativeZ;
        }
        return result;
    }

    public final static int CollidedPositiveX = 1;
    public final static int CollidedNegativeX = 2;
    public final static int CollidedPositiveY = 4;
    public final static int CollidedNegativeY = 8;
    public final static int CollidedPositiveZ = 16;
    public final static int CollidedNegativeZ = 32;

    public boolean isSneaking()
    {
        return this.entityPlayer.isSneaking();
    }

    public void correctOnUpdate(boolean isSmall, boolean reverseMaterialAcceleration)
    {
        double d = this.entityPlayer.posX - this.entityPlayer.prevPosX;
        double d1 = this.entityPlayer.posZ - this.entityPlayer.prevPosZ;
        float f = MathHelper.sqrt(d * d + d1 * d1);
        if (f < 0.05F && f > 0.02 && isSmall)
        {
            float f1 = ((float) Math.atan2(d1, d) * 180F) / 3.141593F - 90F;

            if (this.entityPlayer.swingProgress > 0.0F)
            {
                f1 = this.entityPlayer.rotationYaw;
            }
            float f4 = f1 - this.entityPlayer.renderYawOffset;
            for (; f4 < -180F; f4 += 360F)
            {
            }
            for (; f4 >= 180F; f4 -= 360F)
            {
            }
            float x = this.entityPlayer.renderYawOffset + f4 * 0.3F;
            float f5 = this.entityPlayer.rotationYaw - x;
            for (; f5 < -180F; f5 += 360F)
            {
            }
            for (; f5 >= 180F; f5 -= 360F)
            {
            }
            if (f5 < -75F)
            {
                f5 = -75F;
            }
            if (f5 >= 75F)
            {
                f5 = 75F;
            }
            this.entityPlayer.renderYawOffset = this.entityPlayer.rotationYaw - f5;
            if (f5 * f5 > 2500F)
            {
                this.entityPlayer.renderYawOffset += f5 * 0.2F;
            }
            for (; this.entityPlayer.renderYawOffset - this.entityPlayer.prevRenderYawOffset < -180F; this.entityPlayer.prevRenderYawOffset -= 360F)
            {
            }
            for (; this.entityPlayer.renderYawOffset - this.entityPlayer.prevRenderYawOffset >= 180F; this.entityPlayer.prevRenderYawOffset += 360F)
            {
            }
        }

        if (reverseMaterialAcceleration)
        {
            this.reverseHandleMaterialAcceleration();
        }
    }

    protected double getGapUnderneight()
    {
        return this.getBoundingBox().minY - this.getMaxPlayerSolidBetween(this.getBoundingBox().minY - 1.1D, this.getBoundingBox().minY, 0);
    }

    protected double getGapOverneight()
    {
        return this.getMinPlayerSolidBetween(this.getBoundingBox().maxY, this.getBoundingBox().maxY + 1.1D, 0) - this.getBoundingBox().maxY;
    }

    public double getOverGroundHeight(double maximum)
    {
        if (this.entityPlayer instanceof EntityPlayerSP)
        {
            return (this.getBoundingBox().minY - this.getMaxPlayerSolidBetween(this.getBoundingBox().minY - maximum, this.getBoundingBox().minY, 0));
        }
        return (this.getBoundingBox().minY + 1D - this.getMaxPlayerSolidBetween(this.getBoundingBox().minY - maximum + 1D, this.getBoundingBox().minY + 1D, 0.1));
    }

    public Block getOverGroundBlockId(double distance)
    {
        int x = MathHelper.floor(this.entityPlayer.posX);
        int y = MathHelper.floor(this.getBoundingBox().minY);
        int z = MathHelper.floor(this.entityPlayer.posZ);
        int minY = y - (int) Math.ceil(distance);

        if (!(this.entityPlayer instanceof EntityPlayerSP))
        {
            y++;
            minY++;
        }

        for (; y >= minY; y--)
        {
            Block block = this.getBlock(x, y, z);
            if (block != null)
            {
                return block;
            }
        }
        return null;
    }

    public void reverseHandleMaterialAcceleration()
    {
        World _this = this.entityPlayer.world;
        AxisAlignedBB axisalignedbb = this.getBoundingBox().expand(0.0D, -0.40000000596046448D, 0.0D).shrink(0.001D);
        Material material = Material.WATER;
        Entity entity = this.entityPlayer;

        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.floor(axisalignedbb.maxX + 1.0D);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.floor(axisalignedbb.maxY + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.floor(axisalignedbb.maxZ + 1.0D);
        if (!_this.isAreaLoaded(new BlockPos(i, k, i1), new BlockPos(j, l, j1), true))
        {
            return;
        }

        Vec3d vec3 = new Vec3d(0.0D, 0.0D, 0.0D);
        for (int k1 = i; k1 < j; k1++)
        {
            for (int l1 = k; l1 < l; l1++)
            {
                for (int i2 = i1; i2 < j1; i2++)
                {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    IBlockState iblockstate = _this.getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    if (iblockstate.getMaterial() == material)
                    {
                        double d0 = l1 + 1 - BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL));
                        if (l >= d0)
                        {
                            vec3 = block.modifyAcceleration(_this, blockpos, entity, vec3);
                        }
                    }
                }
            }
        }

        if ((vec3.length() > 0.0D) && (entity.isPushedByWater()))
        {
            vec3 = vec3.normalize();
            double d1 = -0.014D; // instead +0.014D for reversal
            entity.motionX += vec3.x * d1;
            entity.motionY += vec3.y * d1;
            entity.motionZ += vec3.z * d1;
        }
    }

    public boolean isAngleJumping()
    {
        return this.angleJumpType > 1 && this.angleJumpType < 7;
    }

    public abstract boolean isJumping();

    public abstract boolean doFlyingAnimation();

    public abstract boolean doFallingAnimation();

    public void spawnParticles(Minecraft minecraft, double playerMotionX, double playerMotionZ)
    {
        float horizontalSpeedSquare = 0;
        if (this.isSliding || this.isSwimming)
        {
            horizontalSpeedSquare = (float) (playerMotionX * playerMotionX + playerMotionZ * playerMotionZ);
        }

        if (this.isSliding)
        {
            int i = MathHelper.floor(this.entityPlayer.posX);
            int j = MathHelper.floor(this.entityPlayer.getEntityBoundingBox().minY - 0.5);
            int k = MathHelper.floor(this.entityPlayer.posZ);
            double posY = this.entityPlayer.getEntityBoundingBox().minY + 0.1D;
            double motionX = -playerMotionX * 4D;
            double motionY = 1.5D;
            double motionZ = -playerMotionZ * 4D;

            this.spawnSlidingParticle += horizontalSpeedSquare;

            float maxSpawnSlidingParticle = SmartMovingConfig.SLIDING.particlePeriodFactor * 0.1F;
            while (this.spawnSlidingParticle > maxSpawnSlidingParticle)
            {
                double posX = this.entityPlayer.posX + this.getSpawnOffset();
                double posZ = this.entityPlayer.posZ + this.getSpawnOffset();
                IBlockState state = this.getState(i, j, k);
                if (state.getMaterial() != Material.AIR)
                {
                    this.entityPlayer.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX, posY, posZ, motionX, motionY, motionZ, Block.getStateId(state));
                }
                this.spawnSlidingParticle -= maxSpawnSlidingParticle;
            }
        }

        if (this.isSwimming)
        {
            float posY = MathHelper.floor(this.entityPlayer.getEntityBoundingBox().minY) + 1.0F;
            int x = (int) Math.floor(this.entityPlayer.posX);
            int y = (int) Math.floor(posY - 0.5);
            int z = (int) Math.floor(this.entityPlayer.posZ);

            boolean isLava = this.isLava(this.entityPlayer.world.getBlockState(new BlockPos(x, y, z)));
            this.spawnSwimmingParticle += horizontalSpeedSquare;

            float maxSpawnSwimmingParticle = (isLava ? SmartMovingConfig.LAVA.particlePeriodFactor : SmartMovingConfig.SWIMMING.particlePeriodFactor) * 0.01F;
            while (this.spawnSwimmingParticle > maxSpawnSwimmingParticle)
            {
                double posX = this.entityPlayer.posX + this.getSpawnOffset();
                double posZ = this.entityPlayer.posZ + this.getSpawnOffset();
                Particle splash = isLava ? new ParticleSplash.Factory().createParticle(EnumParticleTypes.LAVA.getParticleID(), this.entityPlayer.world, posX, posY, posZ, 0, 0.2, 0) : new ParticleSplash.Factory().createParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), this.entityPlayer.world, posX, posY, posZ, 0, 0.2, 0);
                if (splash != null)
                {
                    minecraft.effectRenderer.addEffect(splash);
                }
                this.spawnSwimmingParticle -= maxSpawnSwimmingParticle;
            }
        }
    }

    private float getSpawnOffset()
    {
        return (this.entityPlayer.getRNG().nextFloat() - 0.5F) * 2F * this.entityPlayer.width;
    }

    protected void onStartClimbBackJump()
    {
        SmartRenderRender.getPreviousRendererData(this.entityPlayer).rotateAngleY += this.isHeadJumping ? Half : Quarter;
        this.isClimbBackJumping = true;
    }

    protected void onStartWallJump(Float angle)
    {
        if (angle != null)
        {
            SmartRenderRender.getPreviousRendererData(this.entityPlayer).rotateAngleY = angle / RadiantToAngle;
        }
        this.isWallJumping = true;
        this.entityPlayer.fallDistance = 0F;
    }
}