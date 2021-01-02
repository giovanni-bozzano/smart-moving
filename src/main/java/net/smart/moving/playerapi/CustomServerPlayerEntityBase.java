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

import api.player.asm.interfaces.IServerPlayerEntity;
import api.player.server.ServerPlayerAPI;
import api.player.server.ServerPlayerEntityBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.SmartMovingServer;
import net.smart.moving.asm.interfaces.INetHandlerPlayServer;

import java.util.List;

public class CustomServerPlayerEntityBase extends ServerPlayerEntityBase
{
    public final SmartMovingServer controller;

    public static void registerPlayerBase()
    {
        ServerPlayerAPI.register(SmartMovingMod.ID, CustomServerPlayerEntityBase.class);
    }

    public static CustomServerPlayerEntityBase getPlayerBase(Object player)
    {
        return (CustomServerPlayerEntityBase) ((IServerPlayerEntity) player).getServerPlayerBase(SmartMovingMod.ID);
    }

    public CustomServerPlayerEntityBase(ServerPlayerAPI playerApi)
    {
        super(playerApi);
        this.controller = new SmartMovingServer(this);
    }

    @Override
    public void updateSize()
    {
        float width = 0.6F;
        float height = 1.8F;

        if (this.controller.isCrawling || this.controller.isSmall) {
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

    public float getHeight()
    {
        return this.playerEntity.height;
    }

    public double getMinY()
    {
        return this.playerEntity.getEntityBoundingBox().minY;
    }

    public void setMaxY(double maxY)
    {
        AxisAlignedBB box = this.playerEntity.getEntityBoundingBox();
        this.playerEntity.setEntityBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, maxY, box.maxZ));
    }

    @Override
    public void afterSetPosition(double d, double d1, double d2)
    {
        this.controller.afterSetPosition();
    }

    @Override
    public void beforeIsSleeping()
    {
        this.controller.beforeIsPlayerSleeping();
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
    public void afterLivingTick()
    {
        this.controller.afterOnLivingUpdate();
    }

    public float doGetHealth()
    {
        return this.playerEntity.getHealth();
    }

    public AxisAlignedBB getBox()
    {
        return this.playerEntity.getEntityBoundingBox();
    }

    public AxisAlignedBB expandBox(AxisAlignedBB box, double x, double y, double z)
    {
        return box.expand(x, y, z);
    }

    public List<?> getEntitiesExcludingPlayer(AxisAlignedBB box)
    {
        return this.playerEntity.world.getEntitiesWithinAABBExcludingEntity(this.playerEntity, box);
    }

    public boolean isDeadEntity(Entity entity)
    {
        return entity.isDead;
    }

    public void onCollideWithPlayer(Entity entity)
    {
        entity.onCollideWithPlayer(this.playerEntity);
    }

    @Override
    public float getEyeHeight()
    {
        return this.playerEntity.height - 0.18F;
    }

    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        return this.controller.isEntityInsideOpaqueBlock();
    }

    public boolean localIsEntityInsideOpaqueBlock()
    {
        return super.isEntityInsideOpaqueBlock();
    }

    @Override
    public void addExhaustion(float exhaustion)
    {
        this.controller.addExhaustion(exhaustion);
    }

    public void localAddExhaustion(float exhaustion)
    {
        this.playerEntity.getFoodStats().addExhaustion(exhaustion);
    }

    @Override
    public void addMovementStat(double x, double y, double z)
    {
        this.controller.addMovementStat(x, y, z);
    }

    public void localAddMovementStat(double x, double y, double z)
    {
        super.addMovementStat(x, y, z);
    }

    public void localPlaySound(String soundId, float volume, float pitch)
    {
        SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(soundId));
        if (soundEvent != null) {
            this.playerEntity.playSound(soundEvent, volume, pitch);
        }
    }

    @Override
    public boolean isShiftKeyDown()
    {
        return this.controller.isSneaking();
    }

    public boolean localIsSneaking()
    {
        return this.iServerPlayerEntity.superIsShiftKeyDown();
    }

    public void setHeight(float height)
    {
        this.playerEntity.height = height;
    }

    public EntityPlayerMP getPlayer()
    {
        return this.playerEntity;
    }

    public void resetFallDistance()
    {
        this.playerEntity.fallDistance = 0;
        this.playerEntity.motionY = 0.08;
    }

    public void resetTicksForFloatKick()
    {
        ((INetHandlerPlayServer) this.playerEntity.connection).setFloatingTickCount(0);
    }

    public SmartMovingServer getController()
    {
        return this.controller;
    }

    public int getItemInUseCount()
    {
        return this.playerEntity.getItemInUseCount();
    }
}