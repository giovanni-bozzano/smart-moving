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
package api.player.asm.mixins.net.minecraft.client.entity.player;

import api.player.asm.interfaces.IClientPlayerEntity;
import api.player.asm.interfaces.IClientPlayerEntityAccessor;
import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerEntityBase;
import api.player.server.ServerPlayerAPI;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
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
import net.minecraft.world.World;
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

@Mixin(EntityPlayerSP.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayer implements IClientPlayerEntity, IClientPlayerEntityAccessor
{
    @Mutable
    @Shadow
    @Final
    public NetHandlerPlayClient connection;
    @Shadow
    protected Minecraft mc;
    private ClientPlayerAPI clientPlayerAPI;
    private boolean callReal;

    public MixinClientPlayerEntity(World world, GameProfile playerProfile)
    {
        super(world, playerProfile);
    }

    @Override
    public ClientPlayerAPI getClientPlayerAPI()
    {
        return this.clientPlayerAPI;
    }

    @Override
    public ClientPlayerEntityBase getClientPlayerBase(String baseId)
    {
        return ClientPlayerAPI.getClientPlayerBase(this, baseId);
    }

    @Override
    public Set<String> getClientPlayerBaseIds()
    {
        return ClientPlayerAPI.getClientPlayerBaseIds(this);
    }

    @Override
    public Object dynamic(String key, Object[] parameters)
    {
        return ClientPlayerAPI.dynamic(this, key, parameters);
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;connection:Lnet/minecraft/client/network/NetHandlerPlayClient;"))
    public void beforeInit(EntityPlayerSP redirectedClientPlayerEntity, NetHandlerPlayClient redirectedConnection, Minecraft minecraft, World clientWorld, NetHandlerPlayClient clientPlayNetHandler, StatisticsManager statisticsManager, RecipeBook clientRecipeBook)
    {
        this.clientPlayerAPI = ClientPlayerAPI.create(this);
        ClientPlayerAPI.beforeLocalConstructing(this, minecraft, clientWorld, redirectedConnection, statisticsManager, clientRecipeBook);
        this.connection = redirectedConnection;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void afterInit(Minecraft minecraft, World clientWorld, NetHandlerPlayClient clientPlayNetHandler, StatisticsManager statisticsManager, RecipeBook clientRecipeBook, CallbackInfo callbackInfo)
    {
        ClientPlayerAPI.afterLocalConstructing(this, minecraft, clientWorld, clientPlayNetHandler, statisticsManager, clientRecipeBook);
    }

    // ############################################################################

    @Override
    public void updateSize()
    {
        ClientPlayerAPI.updateSize(this);
    }

    @Override
    public void superUpdateSize()
    {
        super.updateSize();
    }

    // ############################################################################

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        ClientPlayerAPI.writeEntityToNbt(this, compound);
    }

    @Override
    public void superWriteEntityToNbt(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
    }

    // ############################################################################

    @Override
    public boolean isInsideOfMaterial(Material material)
    {
        return ClientPlayerAPI.isInsideOfMaterial(this, material);
    }

    @Override
    public boolean superIsInsideOfMaterial(Material material)
    {
        return super.isInsideOfMaterial(material);
    }

    // ############################################################################

    @Override
    public int getBrightnessForRender()
    {
        return ClientPlayerAPI.getBrightnessForRender(this);
    }

    @Override
    public int superGetBrightnessForRender()
    {
        return super.getBrightnessForRender();
    }

    // ############################################################################

    @Override
    public void addExhaustion(float exhaustion)
    {
        ClientPlayerAPI.addExhaustion(this, exhaustion);
    }

    @Override
    public void superAddExhaustion(float exhaustion)
    {
        super.addExhaustion(exhaustion);
    }

    // ############################################################################

    @Override
    public void addExperienceLevel(int levels)
    {
        ClientPlayerAPI.addExperienceLevel(this, levels);
    }

    @Override
    public void superAddExperienceLevel(int levels)
    {
        super.addExperienceLevel(levels);
    }

    // ############################################################################

    @Override
    public void addMovementStat(double x, double y, double z)
    {
        ClientPlayerAPI.addMovementStat(this, x, y, z);
    }

    @Override
    public void superAddMovementStat(double x, double y, double z)
    {
        super.addMovementStat(x, y, z);
    }

    // ############################################################################

    @Override
    public boolean canBreatheUnderwater()
    {
        return ClientPlayerAPI.canBreatheUnderwater(this);
    }

    @Override
    public boolean superCanBreatheUnderwater()
    {
        return super.canBreatheUnderwater();
    }

    // ############################################################################

    @Override
    protected boolean canTriggerWalking()
    {
        return ClientPlayerAPI.canTriggerWalking(this);
    }

    @Override
    public boolean superCanTriggerWalking()
    {
        return super.canTriggerWalking();
    }

    // ############################################################################

    @Override
    public float getAIMoveSpeed()
    {
        return ClientPlayerAPI.getAIMoveSpeed(this);
    }

    @Override
    public float superGetAIMoveSpeed()
    {
        return super.getAIMoveSpeed();
    }

    // ############################################################################

    @Override
    public float getBrightness()
    {
        return ClientPlayerAPI.getBrightness(this);
    }

    @Override
    public float superGetBrightness()
    {
        return super.getBrightness();
    }

    // ############################################################################

    @Override
    public float getFovModifier()
    {
        return ClientPlayerAPI.getFovModifier(this);
    }

    @Override
    public float superGetFovModifier()
    {
        return super.getFovModifier();
    }

    // ############################################################################

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource source)
    {
        return ClientPlayerAPI.getHurtSound(this, source);
    }

    @Override
    public SoundEvent superGetHurtSound(DamageSource source)
    {
        return super.getHurtSound(source);
    }

    // ############################################################################

    @Override
    public int getSleepTimer()
    {
        return ClientPlayerAPI.getSleepTimer(this);
    }

    @Override
    public int superGetSleepTimer()
    {
        return super.getSleepTimer();
    }

    // ############################################################################

    @Override
    public boolean handleWaterMovement()
    {
        return ClientPlayerAPI.handleWaterMovement(this);
    }

    @Override
    public boolean superHandleWaterMovement()
    {
        return super.handleWaterMovement();
    }

    // ############################################################################

    @Shadow
    protected abstract boolean pushOutOfBlocks(double x, double y, double z);

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    protected void beforePushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforePushOutOfBlocks(callbackInfo, this, x, y, z);
        }
        this.callReal = false;
    }

    @Inject(method = "pushOutOfBlocks", at = @At("RETURN"))
    protected void afterPushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        ClientPlayerAPI.afterPushOutOfBlocks(this, x, y, z);
    }

    @Override
    public boolean realPushOutOfBlocks(double x, double y, double z)
    {
        this.callReal = true;
        return this.pushOutOfBlocks(x, y, z);
    }

    @Override
    public boolean superPushOutOfBlocks(double x, double y, double z)
    {
        return super.pushOutOfBlocks(x, y, z);
    }
    // ############################################################################

    @Shadow
    public abstract void heal(float amount);

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    public void beforeHeal(float amount, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforeHeal(callbackInfo, this, amount);
        }
        this.callReal = false;
    }

    @Inject(method = "heal", at = @At("RETURN"))
    public void afterHeal(float amount, CallbackInfo callbackInfo)
    {
        ClientPlayerAPI.afterHeal(this, amount);
    }

    @Override
    public void realHeal(float amount)
    {
        this.callReal = true;
        this.heal(amount);
    }

    @Override
    public void superHeal(float amount)
    {
        super.heal(amount);
    }

    // ############################################################################

    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        return ClientPlayerAPI.isEntityInsideOpaqueBlock(this);
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
        return ClientPlayerAPI.isInWater(this);
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
        return ClientPlayerAPI.isOnLadder(this);
    }

    @Override
    public boolean superIsOnLadder()
    {
        return super.isOnLadder();
    }

    // ############################################################################

    @Shadow
    public abstract boolean isSneaking();

    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    public void beforeIsShiftKeyDown(CallbackInfoReturnable<Boolean> callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforeIsShiftKeyDown(callbackInfo, this);
        }
        this.callReal = false;
    }

    @Inject(method = "isSneaking", at = @At("HEAD"))
    public void afterIsShiftKeyDown(CallbackInfoReturnable<Boolean> callbackInfo)
    {
        ClientPlayerAPI.afterIsShiftKeyDown(this);
    }

    @Override
    public boolean realIsShiftKeyDown()
    {
        this.callReal = true;
        return this.isSneaking();
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
        return ClientPlayerAPI.isSprinting(this);
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
        ClientPlayerAPI.jump(this);
    }

    @Override
    public void superJump()
    {
        super.jump();
    }

    // ############################################################################

    @Override
    public void knockBack(@Nonnull Entity entity, float strength, double xRatio, double zRatio)
    {
        ClientPlayerAPI.knockBack(this, entity, strength, xRatio, zRatio);
    }

    @Override
    public void superKnockBack(Entity entity, float strength, double xRatio, double zRatio)
    {
        super.knockBack(entity, strength, xRatio, zRatio);
    }

    // ############################################################################

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void beforeLivingTick(CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforeLivingTick(callbackInfo, this);
        }
        this.callReal = false;
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void afterLivingTick(CallbackInfo callbackInfo)
    {
        ClientPlayerAPI.afterLivingTick(this);
    }

    @Override
    public void realLivingTick()
    {
        this.callReal = true;
        this.onUpdate();
    }

    @Override
    public void superLivingTick()
    {
        super.onUpdate();
    }

    // ############################################################################

    @Override
    public void move(@Nonnull MoverType type, double x, double y, double z)
    {
        ClientPlayerAPI.move(this, type, x, y, z);
    }

    @Override
    public void superMove(MoverType type, double x, double y, double z)
    {
        super.move(type, x, y, z);
    }

    // ############################################################################

    @Override
    public void onDeath(@Nonnull DamageSource cause)
    {
        ClientPlayerAPI.onDeath(this, cause);
    }

    @Override
    public void superOnDeath(DamageSource cause)
    {
        super.onDeath(cause);
    }

    // ############################################################################

    @Shadow
    public abstract void setPlayerSPHealth(float health);

    @Inject(method = "setPlayerSPHealth", at = @At("HEAD"), cancellable = true)
    public void beforeSetPlayerSPHealth(float health, CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforeSetPlayerSPHealth(callbackInfo, this, health);
        }
        this.callReal = false;
    }

    @Inject(method = "setPlayerSPHealth", at = @At("RETURN"))
    public void afterSetPlayerSPHealth(float health, CallbackInfo callbackInfo)
    {
        ClientPlayerAPI.afterSetPlayerSPHealth(this, health);
    }

    @Override
    public void realSetPlayerSPHealth(float health)
    {
        this.callReal = true;
        this.setPlayerSPHealth(health);
    }

    // ############################################################################

    @Override
    public void setPosition(double x, double y, double z)
    {
        ClientPlayerAPI.setPosition(this, x, y, z);
    }

    @Override
    public void superSetPosition(double x, double y, double z)
    {
        super.setPosition(x, y, z);
    }

    // ############################################################################

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        ClientPlayerAPI.setPositionAndRotation(this, x, y, z, yaw, pitch);
    }

    @Override
    public void superSetPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        super.setPositionAndRotation(x, y, z, yaw, pitch);
    }

    // ############################################################################

    @Override
    public void setSneaking(boolean sneaking)
    {
        ClientPlayerAPI.setSneaking(this, sneaking);
    }

    @Override
    public void superSetSneaking(boolean sneaking)
    {
        super.setSneaking(sneaking);
    }

    // ############################################################################

    @Override
    public void setSprinting(boolean sprinting)
    {
        ClientPlayerAPI.setSprinting(this, sprinting);
    }

    @Override
    public void superSetSprinting(boolean sprinting)
    {
        super.setSprinting(sprinting);
    }

    // ############################################################################

    @Shadow
    public abstract boolean startRiding(@Nonnull Entity entity, boolean force);

    @Inject(method = "startRiding", at = @At("HEAD"), cancellable = true)
    public void beforeStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforeStartRiding(callbackInfo, this, entity, force);
        }
        this.callReal = false;
    }

    @Inject(method = "startRiding", at = @At("RETURN"))
    public void afterStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        ClientPlayerAPI.afterStartRiding(this, entity, force);
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
            ClientPlayerAPI.beforeTick(callbackInfo, this);
        }
        this.callReal = false;
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void afterTick(CallbackInfo callbackInfo)
    {
        ClientPlayerAPI.afterTick(this);
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

    @Override
    public void travel(float strafe, float vertical, float forward)
    {
        ClientPlayerAPI.travel(this, strafe, vertical, forward);
    }

    @Override
    public void superTravel(float strafe, float vertical, float forward)
    {
        super.travel(strafe, vertical, forward);
    }

    // ############################################################################

    @Override
    @Nonnull
    public EntityPlayer.SleepResult trySleep(@Nonnull BlockPos at)
    {
        return ClientPlayerAPI.trySleep(this, at);
    }

    @Override
    public EntityPlayer.SleepResult superTrySleep(BlockPos at)
    {
        return super.trySleep(at);
    }

    // ############################################################################

    @Shadow
    public abstract void updateEntityActionState();

    @Inject(method = "updateEntityActionState", at = @At("HEAD"), cancellable = true)
    protected void beforeUpdateEntityActionState(CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforeUpdateEntityActionState(callbackInfo, this);
        }
        this.callReal = false;
    }

    @Inject(method = "updateEntityActionState", at = @At("RETURN"))
    protected void afterUpdateEntityActionState(CallbackInfo callbackInfo)
    {
        ClientPlayerAPI.afterUpdateEntityActionState(this);
    }

    @Override
    public void realUpdateEntityActionState()
    {
        this.callReal = true;
        this.updateEntityActionState();
    }

    @Override
    public void superUpdateEntityActionState()
    {
        super.updateEntityActionState();
    }

    // ############################################################################

    @Override
    protected void updatePotionEffects()
    {
        ClientPlayerAPI.updatePotionEffects(this);
    }

    @Override
    public void superUpdatePotionEffects()
    {
        super.updatePotionEffects();
    }

    // ############################################################################

    @Shadow
    public abstract void updateRidden();

    @Inject(method = "updateRidden", at = @At("HEAD"), cancellable = true)
    public void beforeUpdateRidden(CallbackInfo callbackInfo)
    {
        if (!this.callReal) {
            ClientPlayerAPI.beforeUpdateRidden(callbackInfo, this);
        }
        this.callReal = false;
    }

    @Inject(method = "updateRidden", at = @At("RETURN"))
    public void afterUpdateRidden(CallbackInfo callbackInfo)
    {
        ClientPlayerAPI.afterUpdateRidden(this);
    }

    @Override
    public void realUpdateRidden()
    {
        this.callReal = true;
        this.updateRidden();
    }

    @Override
    public void superUpdateRidden()
    {
        super.updateRidden();
    }

    // ############################################################################

    @Override
    public Minecraft getMinecraft()
    {
        return this.mc;
    }

    // ############################################################################

    @Override
    public boolean isJumping()
    {
        return this.isJumping;
    }

    // ############################################################################

    @Override
    public void setIsJumping(boolean isJumping)
    {
        this.isJumping = isJumping;
    }

    // ############################################################################

    @Override
    public boolean isSleeping()
    {
        return this.sleeping;
    }

    // ############################################################################

    @Override
    public boolean isInWeb()
    {
        return this.isInWeb;
    }

    // ############################################################################

    @Override
    public void setIsInWeb(boolean isInWeb)
    {
        this.isInWeb = isInWeb;
    }
}
