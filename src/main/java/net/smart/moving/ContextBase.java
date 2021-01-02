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
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smart.moving.playerapi.Factory;
import net.smart.moving.render.ContextRender;

public abstract class ContextBase extends ContextRender
{
    public static final double FastUpMotion = 0.2D;
    public static final double MediumUpMotion = 0.14D;
    public static final double SlowUpMotion = 0.1D;
    public static final double HoldMotion = 0.08D;
    public static final double SinkDownMotion = 0.05D;
    public static final double ClimbDownMotion = 0.01D;
    public static final double CatchCrawlGapMotion = 0.17D;
    public static final float SwimCrawlWaterMaxBorder = 1F;
    public static final float SwimCrawlWaterTopBorder = 0.65F;
    public static final float SwimCrawlWaterMediumBorder = 0.6F;
    public static final float SwimCrawlWaterBottomBorder = 0.55F;
    public static final float HorizontalGroundDamping = 0.546F;
    public static final float HorizontalAirDamping = 0.91F;
    public static final float HorizontalAerodynamicDamping = 0.999F;
    public static final float SwimSoundDistance = 1F / 0.7F;
    public static final float SlideToHeadJumpingFallDistance = 0.05F;
    private static boolean wasInitialized;

    public static void onTickInGame()
    {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.world != null && minecraft.world.isRemote) {
            Factory.getInstance().handleMultiPlayerTick(minecraft);
        }
    }

    public static void initialize()
    {
        if (!wasInitialized) {
            net.smart.render.statistics.SmartStatisticsContext.setCalculateHorizontalStats(true);
        }

        if (wasInitialized) {
            return;
        }

        wasInitialized = true;
    }

    public static Block getBlock(World world, int x, int y, int z)
    {
        return getState(world, x, y, z).getBlock();
    }

    public static IBlockState getState(World world, BlockPos blockPos)
    {
        return world.getBlockState(blockPos);
    }

    public static IBlockState getState(World world, int x, int y, int z)
    {
        return world.getBlockState(new BlockPos(x, y, z));
    }

    public static Material getMaterial(World world, int x, int y, int z)
    {
        return getState(world, x, y, z).getMaterial();
    }

    public static boolean getValue(IBlockState state, PropertyBool property)
    {
        return state.getValue(property);
    }

    public static int getValue(IBlockState state, PropertyInteger property)
    {
        return state.getValue(property);
    }

    public static EnumFacing getValue(IBlockState state, PropertyDirection property)
    {
        Comparable<?> comparable = state.getProperties().get(property);
        return comparable == null ? null : property.getValueClass().cast(comparable);
    }

    public static <T extends Enum<T> & IStringSerializable> T getValue(IBlockState state, PropertyEnum<T> property)
    {
        return state.getValue(property);
    }
}