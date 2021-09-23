package net.smart.moving.asm.interfaces;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IBlockWall
{
    void publicCanConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing);
}