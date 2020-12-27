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
package api.player.asm.mixins.net.minecraft.entity.player;

import api.player.asm.interfaces.IServerPlayerEntity;
import api.player.asm.interfaces.IServerPlayerEntityAccessor;
import api.player.server.ServerPlayerAPI;
import api.player.server.ServerPlayerEntityBase;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.Set;

@Mixin(value = EntityPlayerMP.class, priority = 1001)
public abstract class MixinServerPlayerEntity extends EntityPlayer implements IServerPlayerEntity, IServerPlayerEntityAccessor
{
    @Mutable
    @Shadow
    @Final
    public PlayerInteractionManager interactionManager;
    private ServerPlayerAPI serverPlayerAPI;
    private boolean callReal;

    public MixinServerPlayerEntity(World world, GameProfile gameProfile)
    {
        super(world, gameProfile);
    }

    @Override
    public ServerPlayerAPI getServerPlayerAPI()
    {
        return this.serverPlayerAPI;
    }

    @Override
    public ServerPlayerEntityBase getServerPlayerBase(String baseId)
    {
        return ServerPlayerAPI.getServerPlayerBase(this, baseId);
    }

    @Override
    public Set<String> getServerPlayerBaseIds()
    {
        return ServerPlayerAPI.getServerPlayerBaseIds(this);
    }

    @Override
    public Object dynamic(String key, Object[] parameters)
    {
        return ServerPlayerAPI.dynamic(this, key, parameters);
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayerMP;interactionManager:Lnet/minecraft/server/management/PlayerInteractionManager;"))
    public void beforeInit(EntityPlayerMP redirectedServerPlayerEntity, PlayerInteractionManager redirectedInteractionManager, MinecraftServer minecraftServer, WorldServer serverWorld, GameProfile gameProfile, PlayerInteractionManager playerInteractionManager)
    {
        this.serverPlayerAPI = ServerPlayerAPI.create(this);
        ServerPlayerAPI.beforeLocalConstructing(this, minecraftServer, serverWorld, gameProfile, playerInteractionManager);
        this.interactionManager = redirectedInteractionManager;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void afterInit(MinecraftServer minecraftServer, WorldServer serverWorld, GameProfile gameProfile, PlayerInteractionManager playerInteractionManager, CallbackInfo callbackInfo)
    {
        ServerPlayerAPI.afterLocalConstructing(this, minecraftServer, serverWorld, gameProfile, playerInteractionManager);
    }

    // ############################################################################

    @Override
    public void updateSize()
    {
        ServerPlayerAPI.updateSize(this);
    }

    @Override
    public void superUpdateSize()
    {
        super.updateSize();
    }

    // ############################################################################

    @Override
    public float getEyeHeight()
    {
        return ServerPlayerAPI.getEyeHeight(this);
    }

    @Override
    public float superGetEyeHeight()
    {
        return super.getEyeHeight();
    }

    // ############################################################################

    @Override
    public float getBrightness()
    {
        return ServerPlayerAPI.getBrightness(this);
    }

    @Override
    public float superGetBrightness()
    {
        return super.getBrightness();
    }

    // ############################################################################

    @Override
    public int getSleepTimer()
    {
        return ServerPlayerAPI.getSleepTimer(this);
    }

    @Override
    public int superGetSleepTimer()
    {
        return super.getSleepTimer();
    }

    // ############################################################################

    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        return ServerPlayerAPI.isEntityInsideOpaqueBlock(this);
    }

    @Override
    public boolean superIsEntityInsideOpaqueBlock()
    {
        return super.isEntityInsideOpaqueBlock();
    }

    // ############################################################################

    @Override
    public boolean isInWater()
    {
        return ServerPlayerAPI.isInWater(this);
    }

    @Override
    public boolean superIsInWater()
    {
        return super.isInWater();
    }

    // ############################################################################

    @Override
    public boolean isOnLadder()
    {
        return ServerPlayerAPI.isOnLadder(this);
    }

    @Override
    public boolean superIsOnLadder()
    {
        return super.isOnLadder();
    }

    // ############################################################################

    @Override
    public boolean isSneaking()
    {
        return ServerPlayerAPI.isShiftKeyDown(this);
    }

    @Override
    public boolean superIsShiftKeyDown()
    {
        return super.isSneaking();
    }

    // ############################################################################

    @Override
    public boolean superIsSleeping()
    {
        return super.sleeping;
    }

    // ############################################################################

    @Override
    public boolean isSprinting()
    {
        return ServerPlayerAPI.isSprinting(this);
    }

    @Override
    public boolean superIsSprinting()
    {
        return super.isSprinting();
    }

    // ############################################################################

    @Override
    public void jump()
    {
        ServerPlayerAPI.jump(this);
    }

    @Override
    public void superJump()
    {
        super.jump();
    }

    // ############################################################################

    @Override
    public void onLivingUpdate()
    {
        ServerPlayerAPI.livingTick(this);
    }

    @Override
    public void superLivingTick()
    {
        super.onLivingUpdate();
    }

    // ############################################################################

    @Shadow
    public abstract void setEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking);

    @Inject(method = "setEntityActionState", at = @At("HEAD"), cancellable = true)
    protected void beforeSetEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            ServerPlayerAPI.beforeSetEntityActionState(callbackInfo, this, strafe, forward, jumping, sneaking);
        }
        this.callReal = false;
    }

    @Inject(method = "setEntityActionState", at = @At("RETURN"))
    protected void afterSetEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking, CallbackInfo callbackInfo)
    {
        ServerPlayerAPI.afterSetEntityActionState(this, strafe, forward, jumping, sneaking);
    }

    @Override
    public void realSetEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking)
    {
        this.callReal = true;
        this.setEntityActionState(strafe, forward, jumping, sneaking);
    }

    // ############################################################################

    @Override
    public void setSneaking(boolean sneaking)
    {
        ServerPlayerAPI.setSneaking(this, sneaking);
    }

    @Override
    public void superSetSneaking(boolean sneaking)
    {
        super.setSneaking(sneaking);
    }

    // ############################################################################

    @Shadow
    public abstract boolean startRiding(@Nonnull Entity entity, boolean force);

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    public void beforeStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        if (!this.callReal) {
            ServerPlayerAPI.beforeStartRiding(callbackInfo, this, entity, force);
        }
        this.callReal = false;
    }

    @Inject(method = "startRiding", at = @At("RETURN"))
    public void afterStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        ServerPlayerAPI.afterStartRiding(this, entity, force);
    }

    @Override
    public boolean realStartRiding(Entity entity, boolean force)
    {
        this.callReal = true;
        return this.startRiding(entity, force);
    }

    @Override
    public boolean superStartRiding(Entity entity, boolean force)
    {
        return super.startRiding(entity, force);
    }

    // ############################################################################

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void beforeTick(CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            ServerPlayerAPI.beforeTick(callbackInfo, this);
        }
        this.callReal = false;
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void afterTick(CallbackInfo callbackInfo)
    {
        ServerPlayerAPI.afterTick(this);
    }

    @Override
    public void realTick()
    {
        this.callReal = true;
        this.onUpdate();
    }

    @Override
    public void superTick()
    {
        super.onUpdate();
    }

    // ############################################################################

    @Shadow
    @Nonnull
    public abstract EntityPlayer.SleepResult trySleep(@Nonnull BlockPos at);

    @Shadow public abstract void readEntityFromNBT(NBTTagCompound compound);

    @Shadow public NetHandlerPlayServer connection;

    @Inject(method = "trySleep", at = @At("HEAD"), cancellable = true)
    public void beforeTrySleep(BlockPos at, CallbackInfoReturnable<EntityPlayer.SleepResult> callbackInfo)
    {
        if (!this.callReal) {
            ServerPlayerAPI.beforeTrySleep(callbackInfo, this, at);
        }
        this.callReal = false;
    }

    @Inject(method = "trySleep", at = @At("RETURN"))
    public void afterTrySleep(BlockPos at, CallbackInfoReturnable<EntityPlayer.SleepResult> callbackInfo)
    {
        ServerPlayerAPI.afterTrySleep(this, at);
    }

    @Override
    public EntityPlayer.SleepResult realTrySleep(BlockPos at)
    {
        this.callReal = true;
        return this.trySleep(at);
    }

    @Override
    public EntityPlayer.SleepResult superTrySleep(BlockPos at)
    {
        return super.trySleep(at);
    }

    // ############################################################################

    @Override
    protected void updateEntityActionState()
    {
        ServerPlayerAPI.updateEntityActionState(this);
    }

    @Override
    public void superUpdateEntityActionState()
    {
        super.updateEntityActionState();
    }

    // ############################################################################

    @Override
    public void updateRidden()
    {
        ServerPlayerAPI.updateRidden(this);
    }

    @Override
    public void superUpdateRidden()
    {
        super.updateRidden();
    }

    // ############################################################################

    @Override
    public NetHandlerPlayServer getNetHandlerPlayServer()
    {
        return this.connection;
    }
}
