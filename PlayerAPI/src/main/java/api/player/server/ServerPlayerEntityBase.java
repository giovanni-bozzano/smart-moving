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
package api.player.server;

import api.player.asm.interfaces.IServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;

public abstract class ServerPlayerEntityBase
{
    private final ServerPlayerAPI internalServerPlayerAPI;
    protected final IServerPlayerEntity iServerPlayerEntity;
    protected final EntityPlayerMP playerEntity;

    public ServerPlayerEntityBase(ServerPlayerAPI playerAPI)
    {
        this.internalServerPlayerAPI = playerAPI;
        this.iServerPlayerEntity = playerAPI.player;
        this.playerEntity = (EntityPlayerMP) playerAPI.player;
    }

    public Object dynamic(String key, Object[] parameters)
    {
        return this.internalServerPlayerAPI.dynamicOverwritten(key, parameters, this);
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

    public void beforeLocalConstructing(MinecraftServer paramMinecraftServer, WorldServer paramWorldServer, com.mojang.authlib.GameProfile paramGameProfile, PlayerInteractionManager paramPlayerInteractionManager)
    {
    }

    public void afterLocalConstructing(MinecraftServer paramMinecraftServer, WorldServer paramWorldServer, com.mojang.authlib.GameProfile paramGameProfile, PlayerInteractionManager paramPlayerInteractionManager)
    {
    }

    public void beforeBaseDetach(boolean onTheFly)
    {
    }

    public void afterBaseDetach(boolean onTheFly)
    {
    }

    // ############################################################################

    public void beforeUpdateSize()
    {
    }

    public void updateSize()
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenUpdateSize(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superUpdateSize();
        } else if (overwritten != this) {
            overwritten.updateSize();
        }
    }

    public void afterUpdateSize()
    {
    }

    // ############################################################################

    public void beforeGetEyeHeight()
    {
    }

    public float getEyeHeight()
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetEyeHeight(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetEyeHeight();
        } else if (overwritten != this) {
            return overwritten.getEyeHeight();
        } else {
            return 0.0F;
        }
    }

    public void afterGetEyeHeight()
    {
    }

    // ############################################################################

    public void beforeAddExhaustion(float exhaustion)
    {
    }

    public void addExhaustion(float exhaustion)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenAddExhaustion(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superAddExhaustion(exhaustion);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenAddExperienceLevel(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realAddExperienceLevel(levels);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenAddMovementStat(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superAddMovementStat(x, y, z);
        } else if (overwritten != this) {
            overwritten.addMovementStat(x, y, z);
        }
    }

    public void afterAddMovementStat(double x, double y, double z)
    {
    }

    // ############################################################################

    public void beforeAttackEntityFrom(DamageSource source, float amount)
    {
    }

    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenAttackEntityFrom(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.realAttackEntityFrom(source, amount);
        } else if (overwritten != this) {
            return overwritten.attackEntityFrom(source, amount);
        } else {
            return false;
        }
    }

    public void afterAttackEntityFrom(DamageSource source, float amount)
    {
    }

    // ############################################################################

    public void beforeAttackTargetEntityWithCurrentItem(Entity targetEntity)
    {
    }

    public void attackTargetEntityWithCurrentItem(Entity targetEntity)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenAttackTargetEntityWithCurrentItem(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realAttackTargetEntityWithCurrentItem(targetEntity);
        } else if (overwritten != this) {
            overwritten.attackTargetEntityWithCurrentItem(targetEntity);
        }
    }

    public void afterAttackTargetEntityWithCurrentItem(Entity targetEntity)
    {
    }

    // ############################################################################

    public void beforeCanBreatheUnderwater()
    {
    }

    public boolean canBreatheUnderwater()
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenCanBreatheUnderwater(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superCanBreatheUnderwater();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenCanTriggerWalking(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superCanTriggerWalking();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenDamageEntity(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superDamageEntity(source, amount);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetAIMoveSpeed(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetAIMoveSpeed();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetBrightness(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetBrightness();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetDistanceSq(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetDistanceSq(x, y, z);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetDistanceSqToEntity(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetDistanceSq(entity);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetDistanceSqVec(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetDistanceSq(pos);
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

    public void beforeGetHurtSound(DamageSource source)
    {
    }

    public SoundEvent getHurtSound(DamageSource source)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetHurtSound(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetHurtSound(source);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetName(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetName();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGetSleepTimer(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superGetSleepTimer();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenGiveExperiencePoints(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realGiveExperiencePoints(points);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenHandleWaterMovement(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superHandleWaterMovement();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenHeal(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superHeal(amount);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenIsEntityInsideOpaqueBlock(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superIsEntityInsideOpaqueBlock();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenIsInWater(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superIsInWater();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenIsOnLadder(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superIsOnLadder();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenIsShiftKeyDown(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superIsShiftKeyDown();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenIsSleeping(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superIsSleeping();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenIsSprinting(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superIsSprinting();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenJump(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superJump();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenKnockBack(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superKnockBack(entity, strength, xRatio, zRatio);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenLivingTick(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superLivingTick();
        } else if (overwritten != this) {
            overwritten.livingTick();
        }
    }

    public void afterLivingTick()
    {
    }

    // ############################################################################

    public void beforeMove(MoverType type, Vec3d pos)
    {
    }

    public void move(MoverType type, Vec3d pos)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenMove(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superMove(type, pos);
        } else if (overwritten != this) {
            overwritten.move(type, pos);
        }
    }

    public void afterMove(MoverType type, Vec3d pos)
    {
    }

    // ############################################################################

    public void beforeMoveRelative(float friction, Vec3d relative)
    {
    }

    public void moveRelative(float friction, Vec3d relative)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenMoveRelative(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superMoveRelative(friction, relative);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenOnDeath(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realOnDeath(cause);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenOnLivingFall(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superOnLivingFall(distance, damageMultiplier);
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

    public void beforePick(double blockReachDistance, float partialTicks, boolean anyFluid)
    {
    }

    public RayTraceResult pick(double blockReachDistance, float partialTicks, boolean anyFluid)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenPick(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superPick(blockReachDistance, partialTicks, anyFluid);
        } else if (overwritten != this) {
            return overwritten.pick(blockReachDistance, partialTicks, anyFluid);
        } else {
            return null;
        }
    }

    public void afterPick(double blockReachDistance, float partialTicks, boolean anyFluid)
    {
    }

    // ############################################################################

    public void beforePlayerTick()
    {
    }

    public void playerTick()
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenPlayerTick(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realPlayerTick();
        } else if (overwritten != this) {
            overwritten.playerTick();
        }
    }

    public void afterPlayerTick()
    {
    }

    // ############################################################################

    public void beforePushOutOfBlocks(double x, double y, double z)
    {
    }

    public void pushOutOfBlocks(double x, double y, double z)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenPushOutOfBlocks(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superPushOutOfBlocks(x, y, z);
        } else if (overwritten != this) {
            overwritten.pushOutOfBlocks(x, y, z);
        }
    }

    public void afterPushOutOfBlocks(double x, double y, double z)
    {
    }

    // ############################################################################

    public void beforeRemove()
    {
    }

    public void remove()
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenRemove(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superRemove();
        } else if (overwritten != this) {
            overwritten.remove();
        }
    }

    public void afterRemove()
    {
    }

    // ############################################################################

    public void beforeSetEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking)
    {
    }

    public void setEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenSetEntityActionState(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realSetEntityActionState(strafe, forward, jumping, sneaking);
        } else if (overwritten != this) {
            overwritten.setEntityActionState(strafe, forward, jumping, sneaking);
        }
    }

    public void afterSetEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking)
    {
    }

    // ############################################################################

    public void beforeSetPosition(double x, double y, double z)
    {
    }

    public void setPosition(double x, double y, double z)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenSetPosition(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superSetPosition(x, y, z);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenSetPositionAndRotation(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superSetPositionAndRotation(x, y, z, yaw, pitch);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenSetSneaking(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superSetSneaking(sneaking);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenSetSprinting(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superSetSprinting(sprinting);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenStartRiding(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.superStartRiding(entity, force);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenTick(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realTick();
        } else if (overwritten != this) {
            overwritten.tick();
        }
    }

    public void afterTick()
    {
    }

    // ############################################################################

    public void beforeTravel(Vec3d pos)
    {
    }

    public void travel(Vec3d pos)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenTravel(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superTravel(pos);
        } else if (overwritten != this) {
            overwritten.travel(pos);
        }
    }

    public void afterTravel(Vec3d pos)
    {
    }

    // ############################################################################

    public void beforeTrySleep(BlockPos at)
    {
    }

    public EntityPlayer.SleepResult trySleep(BlockPos at)
    {
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenTrySleep(this);

        if (overwritten == null) {
            return this.iServerPlayerEntity.realTrySleep(at);
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenUpdateEntityActionState(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superUpdateEntityActionState();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenUpdatePotionEffects(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superUpdatePotionEffects();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenUpdateRidden(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.superUpdateRidden();
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
        ServerPlayerEntityBase overwritten = this.internalServerPlayerAPI.getOverwrittenWakeUpPlayer(this);

        if (overwritten == null) {
            this.iServerPlayerEntity.realWakeUpPlayer(immediately, updateWorldFlag);
        } else if (overwritten != this) {
            overwritten.wakeUpPlayer(immediately, updateWorldFlag);
        }
    }

    public void afterWakeUpPlayer(boolean immediately, boolean updateWorldFlag)
    {
    }
}
