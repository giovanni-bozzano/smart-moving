package net.smart.moving.asm.mixins.net.minecraft.block;

import net.minecraft.block.BlockWall;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.smart.moving.asm.interfaces.IBlockWall;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockWall.class)
public abstract class MixinBlockWall implements IBlockWall
{
    @Shadow
    protected abstract boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing);

    @Override
    public boolean publicCanConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        return this.canConnectTo(world, pos, facing);
    }
}
