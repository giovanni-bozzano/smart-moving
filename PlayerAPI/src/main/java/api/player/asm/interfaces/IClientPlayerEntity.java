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

import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;

import java.util.Set;

public interface IClientPlayerEntity
{
    ClientPlayerAPI getClientPlayerAPI();

    ClientPlayerEntityBase getClientPlayerBase(String baseId);

    Set<String> getClientPlayerBaseIds();

    Object dynamic(String key, Object[] parameters);

    void superUpdateSize();

    void superWriteEntityToNbt(NBTTagCompound compound);

    boolean superIsInsideOfMaterial(Material material);

    int superGetBrightnessForRender();

    void superAddExhaustion(float exhaustion);

    void superAddExperienceLevel(int levels);

    void superAddMovementStat(double x, double y, double z);

    void superAttackTargetEntityWithCurrentItem(Entity targetEntity);

    boolean superCanBreatheUnderwater();

    boolean superCanTriggerWalking();

    void realDamageEntity(DamageSource source, float amount);

    float superGetAIMoveSpeed();

    float superGetBrightness();

    double superGetDistanceSq(double x, double y, double z);

    double superGetDistanceSq(Entity entity);

    double superGetDistanceSq(Vec3d pos);

    float superGetFovModifier();

    SoundEvent superGetHurtSound(DamageSource source);

    ITextComponent superGetName();

    int superGetSleepTimer();

    void superGiveExperiencePoints(int points);

    boolean superHandleWaterMovement();

    void realHeal(float amount);

    void superHeal(float amount);

    boolean superIsEntityInsideOpaqueBlock();

    boolean superIsInWater();

    boolean superIsOnLadder();

    boolean realIsShiftKeyDown();

    boolean superIsShiftKeyDown();

    boolean superIsSleeping();

    boolean superIsSprinting();

    void superJump();

    void superKnockBack(Entity entity, float strength, double xRatio, double zRatio);

    void realLivingTick();

    void superLivingTick();

    void superMove(MoverType type, double x, double y, double z);

    void superMoveRelative(float friction, Vec3d relative);

    void superOnDeath(DamageSource cause);

    boolean superOnLivingFall(float distance, float damageMultiplier);

    boolean realPushOutOfBlocks(double x, double y, double z);

    boolean superPushOutOfBlocks(double x, double y, double z);

    void superRecalculateSize();

    void superRemove();

    void realSetPlayerSPHealth(float health);

    void superSetPosition(double x, double y, double z);

    void superSetPositionAndRotation(double x, double y, double z, float yaw, float pitch);

    void superSetSneaking(boolean sneaking);

    void superSetSprinting(boolean sprinting);

    boolean realStartRiding(Entity entity, boolean force);

    boolean superStartRiding(Entity entity, boolean force);

    void realTick();

    void superTick();

    void superTravel(float strafe, float vertical, float forward);

    EntityPlayer.SleepResult superTrySleep(BlockPos at);

    void realUpdateEntityActionState();

    void superUpdateEntityActionState();

    void superUpdatePotionEffects();

    void realUpdateRidden();

    void superUpdateRidden();

    void superWakeUpPlayer(boolean immediately, boolean updateWorldFlag);
}
