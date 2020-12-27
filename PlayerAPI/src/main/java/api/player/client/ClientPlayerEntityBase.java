// ==================================================================
// This file is part of Player API.
//
// Player API is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Player API is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Player API.
// If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package api.player.client;

import api.player.asm.interfaces.IClientPlayerEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public abstract class ClientPlayerEntityBase
{
    private final ClientPlayerAPI internalClientPlayerAPI;
    protected final IClientPlayerEntity iClientPlayerEntity;
    protected final EntityPlayerSP playerEntity;

    public ClientPlayerEntityBase(ClientPlayerAPI playerAPI)
    {
        this.internalClientPlayerAPI = playerAPI;
        this.iClientPlayerEntity = playerAPI.iClientPlayerEntity;
        this.playerEntity = (EntityPlayerSP) playerAPI.iClientPlayerEntity;
    }

    public Object dynamic(String key, Object[] parameters)
    {
        return this.internalClientPlayerAPI.dynamicOverwritten(key, parameters, this);
    }

    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    public void beforeBaseAttach(boolean onTheFly)
    {
    }

    public void afterBaseAttach(boolean onTheFly)
    {
    }

    public void beforeLocalConstructing(Minecraft paramMinecraft, World paramWorld, NetHandlerPlayClient paramNetHandlerPlayClient, StatisticsManager paramStatisticsManager, RecipeBook paramRecipeBook)
    {
    }

    public void afterLocalConstructing(Minecraft paramMinecraft, World paramWorld, NetHandlerPlayClient paramNetHandlerPlayClient, StatisticsManager paramStatisticsManager, RecipeBook paramRecipeBook)
    {
    }

    public void beforeBaseDetach(boolean onTheFly)
    {
    }

    public void afterBaseDetach(boolean onTheFly)
    {
    }

    // ############################################################################

    public void beforeWriteEntityToNbt(NBTTagCompound compound)
    {
    }

    public void writeEntityToNbt(NBTTagCompound compound)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenWriteEntityToNbt(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superWriteEntityToNbt(compound);
        } else if (overwritten != this) {
            overwritten.writeEntityToNbt(compound);
        }
    }

    public void afterWriteEntityToNbt(NBTTagCompound compound)
    {
    }

    // ############################################################################

    public void beforeIsInsideOfMaterial(Material material)
    {
    }

    public boolean isInsideOfMaterial(Material material)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenIsInsideOfMaterial(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superIsInsideOfMaterial(material);
        } else if (overwritten != this) {
            return overwritten.isInsideOfMaterial(material);
        } else {
            return false;
        }
    }

    public void afterIsInsideOfMaterial(Material material)
    {
    }

    // ############################################################################

    public void beforeGetBrightnessForRender()
    {
    }

    public int getBrightnessForRender()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetBrightnessForRender(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetBrightnessForRender();
        } else if (overwritten != this) {
            return overwritten.getBrightnessForRender();
        } else {
            return 0;
        }
    }

    public void afterGetBrightnessForRender()
    {
    }

    // ############################################################################

    public void beforeUpdateSize()
    {
    }

    public void updateSize()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenUpdateSize(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superUpdateSize();
        } else if (overwritten != this) {
            overwritten.updateSize();
        }
    }

    public void afterUpdateSize()
    {
    }

    // ############################################################################

    public void beforeAddExhaustion(float exhaustion)
    {
    }

    public void addExhaustion(float exhaustion)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenAddExhaustion(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superAddExhaustion(exhaustion);
        } else if (overwritten != this) {
            overwritten.addExhaustion(exhaustion);
        }
    }

    public void afterAddExhaustion(float exhaustion)
    {
    }

    // ############################################################################

    public void beforeAddExperienceLevel(int levels)
    {
    }

    public void addExperienceLevel(int levels)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenAddExperienceLevel(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superAddExperienceLevel(levels);
        } else if (overwritten != this) {
            overwritten.addExperienceLevel(levels);
        }
    }

    public void afterAddExperienceLevel(int levels)
    {
    }

    // ############################################################################

    public void beforeAddMovementStat(double x, double y, double z)
    {
    }

    public void addMovementStat(double x, double y, double z)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenAddMovementStat(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superAddMovementStat(x, y, z);
        } else if (overwritten != this) {
            overwritten.addMovementStat(x, y, z);
        }
    }

    public void afterAddMovementStat(double x, double y, double z)
    {
    }

    // ############################################################################

    public void beforeCanBreatheUnderwater()
    {
    }

    public boolean canBreatheUnderwater()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenCanBreatheUnderwater(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superCanBreatheUnderwater();
        } else if (overwritten != this) {
            return overwritten.canBreatheUnderwater();
        } else {
            return false;
        }
    }

    public void afterCanBreatheUnderwater()
    {
    }

    // ############################################################################

    public void beforeCanTriggerWalking()
    {
    }

    public boolean canTriggerWalking()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenCanTriggerWalking(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superCanTriggerWalking();
        } else if (overwritten != this) {
            return overwritten.canTriggerWalking();
        } else {
            return false;
        }
    }

    public void afterCanTriggerWalking()
    {
    }

    // ############################################################################

    public void beforeDamageEntity(DamageSource source, float amount)
    {
    }

    public void damageEntity(DamageSource source, float amount)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenDamageEntity(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.realDamageEntity(source, amount);
        } else if (overwritten != this) {
            overwritten.damageEntity(source, amount);
        }
    }

    public void afterDamageEntity(DamageSource source, float amount)
    {
    }

    // ############################################################################

    public void beforeGetAIMoveSpeed()
    {
    }

    public float getAIMoveSpeed()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetAIMoveSpeed(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetAIMoveSpeed();
        } else if (overwritten != this) {
            return overwritten.getAIMoveSpeed();
        } else {
            return 0;
        }
    }

    public void afterGetAIMoveSpeed()
    {
    }

    // ############################################################################

    public void beforeGetBrightness()
    {
    }

    public float getBrightness()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetBrightness(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetBrightness();
        } else if (overwritten != this) {
            return overwritten.getBrightness();
        } else {
            return 0;
        }
    }

    public void afterGetBrightness()
    {
    }

    // ############################################################################

    public void beforeGetDistanceSq(double x, double y, double z)
    {
    }

    public double getDistanceSq(double x, double y, double z)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetDistanceSq(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetDistanceSq(x, y, z);
        } else if (overwritten != this) {
            return overwritten.getDistanceSq(x, y, z);
        } else {
            return 0;
        }
    }

    public void afterGetDistanceSq(double x, double y, double z)
    {
    }

    // ############################################################################

    public void beforeGetDistanceSqToEntity(Entity entity)
    {
    }

    public double getDistanceSqToEntity(Entity entity)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetDistanceSqToEntity(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetDistanceSq(entity);
        } else if (overwritten != this) {
            return overwritten.getDistanceSqToEntity(entity);
        } else {
            return 0;
        }
    }

    public void afterGetDistanceSqToEntity(Entity entity)
    {
    }

    // ############################################################################

    public void beforeGetDistanceSqVec(Vec3d pos)
    {
    }

    public double getDistanceSqVec(Vec3d pos)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetDistanceSqVec(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetDistanceSq(pos);
        } else if (overwritten != this) {
            return overwritten.getDistanceSqVec(pos);
        } else {
            return 0;
        }
    }

    public void afterGetDistanceSqVec(Vec3d pos)
    {
    }

    // ############################################################################

    public void beforeGetFovModifier()
    {
    }

    public float getFovModifier()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetFovModifier(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetFovModifier();
        } else if (overwritten != this) {
            return overwritten.getFovModifier();
        } else {
            return 0;
        }
    }

    public void afterGetFovModifier()
    {
    }

    // ############################################################################

    public void beforeGetHurtSound(DamageSource source)
    {
    }

    public SoundEvent getHurtSound(DamageSource source)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetHurtSound(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetHurtSound(source);
        } else if (overwritten != this) {
            return overwritten.getHurtSound(source);
        } else {
            return null;
        }
    }

    public void afterGetHurtSound(DamageSource source)
    {
    }

    // ############################################################################

    public void beforeGetName()
    {
    }

    public ITextComponent getName()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetName(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetName();
        } else if (overwritten != this) {
            return overwritten.getName();
        } else {
            return null;
        }
    }

    public void afterGetName()
    {
    }

    // ############################################################################

    public void beforeGetSleepTimer()
    {
    }

    public int getSleepTimer()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGetSleepTimer(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superGetSleepTimer();
        } else if (overwritten != this) {
            return overwritten.getSleepTimer();
        } else {
            return 0;
        }
    }

    public void afterGetSleepTimer()
    {
    }

    // ############################################################################

    public void beforeGiveExperiencePoints(int points)
    {
    }

    public void giveExperiencePoints(int points)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenGiveExperiencePoints(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superGiveExperiencePoints(points);
        } else if (overwritten != this) {
            overwritten.giveExperiencePoints(points);
        }
    }

    public void afterGiveExperiencePoints(int points)
    {
    }

    // ############################################################################

    public void beforeHandleWaterMovement()
    {
    }

    public boolean handleWaterMovement()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenHandleWaterMovement(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superHandleWaterMovement();
        } else if (overwritten != this) {
            return overwritten.handleWaterMovement();
        } else {
            return false;
        }
    }

    public void afterHandleWaterMovement()
    {
    }

    // ############################################################################

    public void beforeHeal(float amount)
    {
    }

    public void heal(float amount)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenHeal(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.realHeal(amount);
        } else if (overwritten != this) {
            overwritten.heal(amount);
        }
    }

    public void afterHeal(float amount)
    {
    }

    // ############################################################################

    public void beforeIsEntityInsideOpaqueBlock()
    {
    }

    public boolean isEntityInsideOpaqueBlock()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenIsEntityInsideOpaqueBlock(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superIsEntityInsideOpaqueBlock();
        } else if (overwritten != this) {
            return overwritten.isEntityInsideOpaqueBlock();
        } else {
            return false;
        }
    }

    public void afterIsEntityInsideOpaqueBlock()
    {
    }

    // ############################################################################

    public void beforeIsInWater()
    {
    }

    public boolean isInWater()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenIsInWater(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superIsInWater();
        } else if (overwritten != this) {
            return overwritten.isInWater();
        } else {
            return false;
        }
    }

    public void afterIsInWater()
    {
    }

    // ############################################################################

    public void beforeIsOnLadder()
    {
    }

    public boolean isOnLadder()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenIsOnLadder(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superIsOnLadder();
        } else if (overwritten != this) {
            return overwritten.isOnLadder();
        } else {
            return false;
        }
    }

    public void afterIsOnLadder()
    {
    }

    // ############################################################################

    public void beforeIsShiftKeyDown()
    {
    }

    public boolean isShiftKeyDown()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenIsShiftKeyDown(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.realIsShiftKeyDown();
        } else if (overwritten != this) {
            return overwritten.isShiftKeyDown();
        } else {
            return false;
        }
    }

    public void afterIsShiftKeyDown()
    {
    }

    // ############################################################################

    public void beforeIsSleeping()
    {
    }

    public boolean isSleeping()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenIsSleeping(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superIsSleeping();
        } else if (overwritten != this) {
            return overwritten.isSleeping();
        } else {
            return false;
        }
    }

    public void afterIsSleeping()
    {
    }

    // ############################################################################

    public void beforeIsSprinting()
    {
    }

    public boolean isSprinting()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenIsSprinting(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superIsSprinting();
        } else if (overwritten != this) {
            return overwritten.isSprinting();
        } else {
            return false;
        }
    }

    public void afterIsSprinting()
    {
    }

    // ############################################################################

    public void beforeJump()
    {
    }

    public void jump()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenJump(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superJump();
        } else if (overwritten != this) {
            overwritten.jump();
        }
    }

    public void afterJump()
    {
    }

    // ############################################################################

    public void beforeKnockBack(Entity entity, float strength, double xRatio, double zRatio)
    {
    }

    public void knockBack(Entity entity, float strength, double xRatio, double zRatio)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenKnockBack(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superKnockBack(entity, strength, xRatio, zRatio);
        } else if (overwritten != this) {
            overwritten.knockBack(entity, strength, xRatio, zRatio);
        }
    }

    public void afterKnockBack(Entity entity, float strength, double xRatio, double zRatio)
    {
    }

    // ############################################################################

    public void beforeLivingTick()
    {
    }

    public void livingTick()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenLivingTick(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.realLivingTick();
        } else if (overwritten != this) {
            overwritten.livingTick();
        }
    }

    public void afterLivingTick()
    {
    }

    // ############################################################################

    public void beforeMove(MoverType type, double x, double y, double z)
    {
    }

    public void move(MoverType type, double x, double y, double z)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenMove(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superMove(type, x, y, z);
        } else if (overwritten != this) {
            overwritten.move(type, x, y, z);
        }
    }

    public void afterMove(MoverType type, double x, double y, double z)
    {
    }

    // ############################################################################

    public void beforeMoveRelative(float friction, Vec3d relative)
    {
    }

    public void moveRelative(float friction, Vec3d relative)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenMoveRelative(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superMoveRelative(friction, relative);
        } else if (overwritten != this) {
            overwritten.moveRelative(friction, relative);
        }
    }

    public void afterMoveRelative(float friction, Vec3d relative)
    {
    }

    // ############################################################################

    public void beforeOnDeath(DamageSource cause)
    {
    }

    public void onDeath(DamageSource cause)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenOnDeath(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superOnDeath(cause);
        } else if (overwritten != this) {
            overwritten.onDeath(cause);
        }
    }

    public void afterOnDeath(DamageSource cause)
    {
    }

    // ############################################################################

    public void beforeOnLivingFall(float distance, float damageMultiplier)
    {
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenOnLivingFall(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superOnLivingFall(distance, damageMultiplier);
        } else if (overwritten != this) {
            return overwritten.onLivingFall(distance, damageMultiplier);
        } else {
            return false;
        }
    }

    public void afterOnLivingFall(float distance, float damageMultiplier)
    {
    }

    // ############################################################################

    public void beforePushOutOfBlocks(double x, double y, double z)
    {
    }

    public boolean pushOutOfBlocks(double x, double y, double z)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenPushOutOfBlocks(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.realPushOutOfBlocks(x, y, z);
        } else if (overwritten != this) {
            return overwritten.pushOutOfBlocks(x, y, z);
        } else {
            return false;
        }
    }

    public void afterPushOutOfBlocks(double x, double y, double z)
    {
    }

    // ############################################################################

    public void beforeRecalculateSize()
    {
    }

    public void recalculateSize()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenRecalculateSize(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superRecalculateSize();
        } else if (overwritten != this) {
            overwritten.recalculateSize();
        }
    }

    public void afterRecalculateSize()
    {
    }

    // ############################################################################

    public void beforeRemove()
    {
    }

    public void remove()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenRemove(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superRemove();
        } else if (overwritten != this) {
            overwritten.remove();
        }
    }

    public void afterRemove()
    {
    }

    // ############################################################################

    public void beforeSetPlayerSPHealth(float health)
    {
    }

    public void setPlayerSPHealth(float health)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenSetPlayerSPHealth(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.realSetPlayerSPHealth(health);
        } else if (overwritten != this) {
            overwritten.setPlayerSPHealth(health);
        }
    }

    public void afterSetPlayerSPHealth(float health)
    {
    }

    // ############################################################################

    public void beforeSetPosition(double x, double y, double z)
    {
    }

    public void setPosition(double x, double y, double z)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenSetPosition(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superSetPosition(x, y, z);
        } else if (overwritten != this) {
            overwritten.setPosition(x, y, z);
        }
    }

    public void afterSetPosition(double x, double y, double z)
    {
    }

    // ############################################################################

    public void beforeSetPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
    }

    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenSetPositionAndRotation(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superSetPositionAndRotation(x, y, z, yaw, pitch);
        } else if (overwritten != this) {
            overwritten.setPositionAndRotation(x, y, z, yaw, pitch);
        }
    }

    public void afterSetPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
    }

    // ############################################################################

    public void beforeSetSneaking(boolean sneaking)
    {
    }

    public void setSneaking(boolean sneaking)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenSetSneaking(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superSetSneaking(sneaking);
        } else if (overwritten != this) {
            overwritten.setSneaking(sneaking);
        }
    }

    public void afterSetSneaking(boolean sneaking)
    {
    }

    // ############################################################################

    public void beforeSetSprinting(boolean sprinting)
    {
    }

    public void setSprinting(boolean sprinting)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenSetSprinting(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superSetSprinting(sprinting);
        } else if (overwritten != this) {
            overwritten.setSprinting(sprinting);
        }
    }

    public void afterSetSprinting(boolean sprinting)
    {
    }

    // ############################################################################

    public void beforeStartRiding(Entity entity, boolean force)
    {
    }

    public boolean startRiding(Entity entity, boolean force)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenStartRiding(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.realStartRiding(entity, force);
        } else if (overwritten != this) {
            return overwritten.startRiding(entity, force);
        } else {
            return false;
        }
    }

    public void afterStartRiding(Entity entity, boolean force)
    {
    }

    // ############################################################################

    public void beforeTick()
    {
    }

    public void tick()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenTick(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.realTick();
        } else if (overwritten != this) {
            overwritten.tick();
        }
    }

    public void afterTick()
    {
    }

    // ############################################################################

    public void beforeTravel(float strafe, float vertical, float forward)
    {
    }

    public void travel(float strafe, float vertical, float forward)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenTravel(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superTravel(strafe, vertical, forward);
        } else if (overwritten != this) {
            overwritten.travel(strafe, vertical, forward);
        }
    }

    public void afterTravel(float strafe, float vertical, float forward)
    {
    }

    // ############################################################################

    public void beforeTrySleep(BlockPos at)
    {
    }

    public EntityPlayer.SleepResult trySleep(BlockPos at)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenTrySleep(this);

        if (overwritten == null) {
            return this.iClientPlayerEntity.superTrySleep(at);
        } else if (overwritten != this) {
            return overwritten.trySleep(at);
        } else {
            return null;
        }
    }

    public void afterTrySleep(BlockPos at)
    {
    }

    // ############################################################################

    public void beforeUpdateEntityActionState()
    {
    }

    public void updateEntityActionState()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenUpdateEntityActionState(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.realUpdateEntityActionState();
        } else if (overwritten != this) {
            overwritten.updateEntityActionState();
        }
    }

    public void afterUpdateEntityActionState()
    {
    }

    // ############################################################################

    public void beforeUpdatePotionEffects()
    {
    }

    public void updatePotionEffects()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenUpdatePotionEffects(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superUpdatePotionEffects();
        } else if (overwritten != this) {
            overwritten.updatePotionEffects();
        }
    }

    public void afterUpdatePotionEffects()
    {
    }

    // ############################################################################

    public void beforeUpdateRidden()
    {
    }

    public void updateRidden()
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenUpdateRidden(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.realUpdateRidden();
        } else if (overwritten != this) {
            overwritten.updateRidden();
        }
    }

    public void afterUpdateRidden()
    {
    }

    // ############################################################################

    public void beforeWakeUpPlayer(boolean immediately, boolean updateWorldFlag)
    {
    }

    public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag)
    {
        ClientPlayerEntityBase overwritten = this.internalClientPlayerAPI.getOverwrittenWakeUpPlayer(this);

        if (overwritten == null) {
            this.iClientPlayerEntity.superWakeUpPlayer(immediately, updateWorldFlag);
        } else if (overwritten != this) {
            overwritten.wakeUpPlayer(immediately, updateWorldFlag);
        }
    }

    public void afterWakeUpPlayer(boolean immediately, boolean updateWorldFlag)
    {
    }
}
