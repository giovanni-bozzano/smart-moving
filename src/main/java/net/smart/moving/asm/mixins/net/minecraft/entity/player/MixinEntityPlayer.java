package net.smart.moving.asm.mixins.net.minecraft.entity.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.smart.moving.Controller;
import net.smart.moving.playerapi.Factory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase
{
    public MixinEntityPlayer(World worldIn)
    {
        super(worldIn);
    }

    /**
     * @author Giovanni Bozzano
     * @reason Overwrite for EntityOtherPlayerMP
     */
    @Overwrite
    protected void updateSize()
    {
        float width = 0.6F;
        float height = 1.8F;

        Controller controller = Factory.getInstance().getPlayerInstance((EntityPlayer) (Object) this);
        if (controller.isCrawling || controller.heightOffset == -1)
        {
            height = 0.8F;
        }
        else if (this.isElytraFlying())
        {
            height = 0.6F;
        }
        else if (this.isPlayerSleeping())
        {
            width = 0.2F;
            height = 0.2F;
        }
        else if (this.isSneaking())
        {
            height = 1.65F;
        }

        if (width != this.width || height != this.height)
        {
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);

            if (!this.world.collidesWithAnyBlock(axisalignedbb))
            {
                this.setSize(width, height);
            }
        }

        FMLCommonHandler.instance().onPlayerPostTick((EntityPlayer) (Object) this);
    }
}