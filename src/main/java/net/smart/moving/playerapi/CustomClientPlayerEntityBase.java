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
package net.smart.moving.playerapi;

import api.player.asm.interfaces.IClientPlayerEntity;
import api.player.asm.interfaces.IClientPlayerEntityAccessor;
import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.smart.moving.ControllerSelf;
import net.smart.moving.SmartMovingMod;

public class CustomClientPlayerEntityBase extends ClientPlayerEntityBase
{
    public static void registerPlayerBase()
    {
        ClientPlayerAPI.register(SmartMovingMod.ID, CustomClientPlayerEntityBase.class);
    }

    public static CustomClientPlayerEntityBase getPlayerBase(EntityPlayerSP player)
    {
        return (CustomClientPlayerEntityBase) ((IClientPlayerEntity) player).getClientPlayerBase(SmartMovingMod.ID);
    }

    public CustomClientPlayerEntityBase(ClientPlayerAPI playerApi)
    {
        super(playerApi);
        this.controller = new ControllerSelf(this.playerEntity, this);
    }

    @Override
    public void updateSize()
    {
        float width = 0.6F;
        float height = 1.8F;

        if (this.controller.isSwimming || this.controller.isDiving || this.controller.isCrawling || this.controller.isSliding || this.controller.isFlying) {
            height = 0.8F;
        } else if (this.playerEntity.isElytraFlying()) {
            height = 0.6F;
        } else if (this.playerEntity.isPlayerSleeping()) {
            width = 0.2F;
            height = 0.2F;
        } else if (this.playerEntity.isSneaking()) {
            height = 1.65F;
        }

        if (width != this.playerEntity.width || height != this.playerEntity.height) {
            AxisAlignedBB axisalignedbb = this.playerEntity.getEntityBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);
            if (!this.playerEntity.world.collidesWithAnyBlock(axisalignedbb)) {
                if (width != this.playerEntity.width || height != this.playerEntity.height) {
                    float previousWidth = this.playerEntity.width;
                    this.playerEntity.width = width;
                    this.playerEntity.height = height;

                    if (this.playerEntity.width < previousWidth) {
                        double d0 = (double) width / 2.0D;
                        this.playerEntity.setEntityBoundingBox(new AxisAlignedBB(this.playerEntity.posX - d0, this.playerEntity.posY, this.playerEntity.posZ - d0, this.playerEntity.posX + d0, this.playerEntity.posY + (double) this.playerEntity.height, this.playerEntity.posZ + d0));
                        return;
                    }

                    AxisAlignedBB bb = this.playerEntity.getEntityBoundingBox();
                    this.playerEntity.setEntityBoundingBox(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.minX + (double) this.playerEntity.width, bb.minY + (double) this.playerEntity.height, bb.minZ + (double) this.playerEntity.width));
                }
            }
        }
        FMLCommonHandler.instance().onPlayerPostTick(this.playerEntity);
    }

    @Override
    public void beforeMove(MoverType type, double d, double d1, double d2)
    {
        this.controller.beforeMoveEntity(d, d1, d2);
    }

    @Override
    public void afterMove(MoverType type, double d, double d1, double d2)
    {
        this.controller.afterMoveEntity();
    }

    @Override
    public void beforeTrySleep(BlockPos pos)
    {
        this.controller.beforeSleepInBedAt();
    }

    @Override
    public float getBrightness()
    {
        return this.controller.getBrightness();
    }

    public float localGetBrightness()
    {
        return super.getBrightness();
    }

    @Override
    public int getBrightnessForRender()
    {
        return this.controller.getBrightnessForRender();
    }

    public int localGetBrightnessForRender()
    {
        return super.getBrightnessForRender();
    }

    @Override
    public boolean pushOutOfBlocks(double d, double d1, double d2)
    {
        return this.controller.pushOutOfBlocks(d, d1, d2);
    }

    @Override
    public void beforeTick()
    {
        this.controller.beforeOnUpdate();
    }

    @Override
    public void afterTick()
    {
        this.controller.afterOnUpdate();
    }

    @Override
    public void beforeLivingTick()
    {
        this.controller.beforeOnLivingUpdate();
    }

    @Override
    public void afterLivingTick()
    {
        this.controller.afterOnLivingUpdate();
    }

    public boolean getSleepingField()
    {
        return ((IClientPlayerEntityAccessor) this.playerEntity).isSleeping();
    }

    public boolean getIsInWebField()
    {
        return ((IClientPlayerEntityAccessor) this.playerEntity).isInWeb();
    }

    public void setIsInWebField(boolean newIsInWeb)
    {
        ((IClientPlayerEntityAccessor) this.playerEntity).setIsInWeb(newIsInWeb);
    }

    public boolean getIsJumpingField()
    {
        return ((IClientPlayerEntityAccessor) this.playerEntity).isJumping();
    }

    public Minecraft getMcField()
    {
        return ((IClientPlayerEntityAccessor) this.playerEntity).getMinecraft();
    }

    @Override
    public void travel(float strafe, float vertical, float forward)
    {
        this.controller.moveEntityWithHeading(strafe, forward);
    }

    @Override
    public boolean canTriggerWalking()
    {
        return this.controller.canTriggerWalking();
    }

    @Override
    public boolean isOnLadder()
    {
        return this.controller.isOnLadderOrVine();
    }

    public ControllerSelf getController()
    {
        return this.controller;
    }

    @Override
    public void updateEntityActionState()
    {
        this.controller.updateEntityActionState(false);
    }

    public void localUpdateEntityActionState()
    {
        super.updateEntityActionState();
    }

    public void setIsJumpingField(boolean flag)
    {
        ((IClientPlayerEntityAccessor) this.playerEntity).setIsJumping(flag);
    }

    public void setMoveForwardField(float f)
    {
        this.playerEntity.moveForward = f;
    }

    public void setMoveStrafingField(float f)
    {
        this.playerEntity.moveStrafing = f;
    }

    @Override
    public boolean isInsideOfMaterial(Material material)
    {
        return this.controller.isInsideOfMaterial(material);
    }

    public boolean localIsInsideOfMaterial(Material material)
    {
        return super.isInsideOfMaterial(material);
    }

    @Override
    public void writeEntityToNbt(NBTTagCompound nBTTagCompound)
    {
        this.controller.writeEntityToNBT(nBTTagCompound);
    }

    public void localWriteEntityToNBT(NBTTagCompound nBTTagCompound)
    {
        super.writeEntityToNbt(nBTTagCompound);
    }

    @Override
    public boolean isShiftKeyDown()
    {
        return this.controller.isSneaking();
    }

    public boolean localIsSneaking()
    {
        return this.iClientPlayerEntity.superIsShiftKeyDown();
    }

    @Override
    public float getFovModifier()
    {
        return this.controller.getFOVMultiplier();
    }

    public float localGetFOVMultiplier()
    {
        return this.iClientPlayerEntity.superGetFovModifier();
    }

    @Override
    public void beforeSetPositionAndRotation(double d, double d1, double d2, float f, float f1)
    {
        this.controller.beforeSetPositionAndRotation();
    }

    @Override
    public void jump()
    {
        this.controller.jump();
    }

    public ControllerSelf controller;
}