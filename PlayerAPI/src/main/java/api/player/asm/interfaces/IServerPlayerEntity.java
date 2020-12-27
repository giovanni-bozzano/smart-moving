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
package api.player.asm.interfaces;

import api.player.server.ServerPlayerAPI;
import api.player.server.ServerPlayerEntityBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;

import java.util.Set;

public interface IServerPlayerEntity
{
    ServerPlayerAPI getServerPlayerAPI();

    ServerPlayerEntityBase getServerPlayerBase(String baseId);

    Set<String> getServerPlayerBaseIds();

    Object dynamic(String key, Object[] parameters);

    void superUpdateSize();

    float superGetEyeHeight();

    void superAddExhaustion(float exhaustion);

    void realAddExperienceLevel(int levels);

    void superAddMovementStat(double x, double y, double z);

    boolean realAttackEntityFrom(DamageSource source, float amount);

    void realAttackTargetEntityWithCurrentItem(Entity targetEntity);

    void superAttackTargetEntityWithCurrentItem(Entity targetEntity);

    boolean superCanBreatheUnderwater();

    boolean superCanTriggerWalking();

    void superDamageEntity(DamageSource source, float amount);

    float superGetAIMoveSpeed();

    float superGetBrightness();

    double superGetDistanceSq(double x, double y, double z);

    double superGetDistanceSq(Entity entity);

    double superGetDistanceSq(Vec3d pos);

    SoundEvent superGetHurtSound(DamageSource source);

    ITextComponent superGetName();

    int superGetSleepTimer();

    void realGiveExperiencePoints(int points);

    void superGiveExperiencePoints(int points);

    boolean superHandleWaterMovement();

    void superHeal(float amount);

    boolean superIsEntityInsideOpaqueBlock();

    boolean superIsInWater();

    boolean superIsOnLadder();

    boolean superIsShiftKeyDown();

    boolean superIsSleeping();

    boolean superIsSprinting();

    void superJump();

    void superKnockBack(Entity entity, float strength, double xRatio, double zRatio);

    void superLivingTick();

    void superMove(MoverType type, Vec3d pos);

    void superMoveRelative(float friction, Vec3d relative);

    void realOnDeath(DamageSource cause);

    boolean superOnLivingFall(float distance, float damageMultiplier);

    RayTraceResult superPick(double blockReachDistance, float partialTicks, boolean anyFluid);

    void realPlayerTick();

    void superPushOutOfBlocks(double x, double y, double z);

    void superRemove();

    void realSetEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking);

    void superSetPosition(double x, double y, double z);

    void superSetPositionAndRotation(double x, double y, double z, float yaw, float pitch);

    void superSetSneaking(boolean sneaking);

    void superSetSprinting(boolean sprinting);

    boolean realStartRiding(Entity entity, boolean force);

    boolean superStartRiding(Entity entity, boolean force);

    void realTick();

    void superTick();

    void superTravel(Vec3d pos);

    EntityPlayer.SleepResult realTrySleep(BlockPos at);

    EntityPlayer.SleepResult superTrySleep(BlockPos at);

    void superUpdateEntityActionState();

    void superUpdatePotionEffects();

    void superUpdateRidden();

    void realWakeUpPlayer(boolean immediately, boolean updateWorldFlag);

    void superWakeUpPlayer(boolean immediately, boolean updateWorldFlag);
}
