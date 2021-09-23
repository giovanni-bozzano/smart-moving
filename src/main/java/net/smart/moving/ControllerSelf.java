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
package net.smart.moving;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.smart.moving.asm.interfaces.IModifiableAttributeInstance;
import net.smart.moving.climbing.ClimbGap;
import net.smart.moving.climbing.FeetClimbing;
import net.smart.moving.climbing.HandsClimbing;
import net.smart.moving.network.MessageHandler;
import net.smart.moving.network.packets.MessageHungerChangeServer;
import net.smart.moving.network.packets.MessageSoundServer;
import net.smart.moving.network.packets.MessageStateServer;
import net.smart.moving.playerapi.CustomClientPlayerEntityBase;

import java.util.HashSet;
import java.util.Random;

import static net.smart.render.SmartRenderUtilities.*;

public class ControllerSelf extends Controller
{
    private boolean initialized;
    private int multiPlayerInitialized;
    private float swimmingDistance;
    private final EntityPlayer player;
    private final Potion jumpBoost = Potion.REGISTRY.getObject(new ResourceLocation("jump_boost"));

    public ControllerSelf(EntityPlayer player, CustomClientPlayerEntityBase playerBase)
    {
        super(player, playerBase);

        this.player = player;

        this.initialized = false;

        this.nextClimbDistance = 0;
        this.distanceClimbedModified = 0;

        this.exhaustion = 0;
        this.lastHorizontalCollisionX = 0;
        this.lastHorizontalCollisionZ = 0;
        this.lastHungerIncrease = -2;

        this.prevPacketState = null;
    }

    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (this.player.motionX == 0 && this.prevMotionX < 0.005)
        {
            this.player.motionX = this.prevMotionX;
        }

        if (this.player.motionZ == 0 && this.prevMotionZ < 0.005)
        {
            this.player.motionZ = this.prevMotionZ;
        }

        if (this.player.capabilities.isFlying && !SmartMovingConfig.SMART_FLYING.enable)
        {
            double d3 = this.player.motionY;
            float f2 = this.player.jumpMovementFactor;
            this.player.jumpMovementFactor = 0.05F;
            this.superMoveEntityWithHeading(strafe, forward);
            this.player.motionY = d3 * 0.6D;
            this.player.jumpMovementFactor = f2;
        }
        else
        {
            this.superMoveEntityWithHeading(strafe, forward);
        }
    }

    private void superMoveEntityWithHeading(float strage, float forward)
    {
        if (this.isRunning() && !SmartMovingConfig.STANDARD_SPRINTING.enable)
        {
            this.player.setSprinting(false);
        }

        boolean wasShortInWater = this.isSwimming || this.isDiving;
        boolean wasSwimming = this.isSwimming;
        boolean wasClimbing = this.isClimbing;
        boolean wasDiving = this.isDiving;
        boolean wasCeilingClimbing = this.isCeilingClimbing;
        boolean wasJumpingOutOfWater = this.isJumpingOutOfWater;

        this.handleJumping();

        double d_S = this.player.posX;
        double d1_S = this.player.posY;
        double d2_S = this.player.posZ;

        if (this.player.collidedHorizontally)
        {
            this.lastHorizontalCollisionX = this.player.posX;
            this.lastHorizontalCollisionZ = this.player.posZ;
        }

        float speedFactor = this.getSpeedFactor(forward, strage);

        boolean isLiquidClimbing = SmartMovingConfig.CLIMB.enable && this.player.fallDistance <= 3.0 && this.wantClimbUp && this.player.collidedHorizontally && !this.isDiving;
        boolean handledSwimming = this.handleSwimming(forward, strage, speedFactor, wasSwimming, wasDiving, isLiquidClimbing, wasJumpingOutOfWater);
        boolean handledLava = this.handleLava(forward, strage, handledSwimming, isLiquidClimbing);
        boolean handledAlternativeFlying = this.handleAlternativeFlying(forward, strage, speedFactor, handledSwimming, handledLava);
        this.handleLand(forward, strage, speedFactor, handledSwimming, handledLava, handledAlternativeFlying, wasShortInWater, wasClimbing, wasCeilingClimbing);

        this.handleWallJumping();

        double diffX = this.player.posX - d_S;
        double diffY = this.player.posY - d1_S;
        double diffZ = this.player.posZ - d2_S;

        this.player.addMovementStat(diffX, diffY, diffZ);

        this.handleExhaustion(diffX, diffY, diffZ);
    }

    private float getSpeedFactor()
    {
        return this.player.isSprinting() ? 1.3F : 1F;
    }

    private float getSpeedFactor(float moveForward, float moveStrafing)
    {
        float speedFactor = this.getSpeedFactor();

        if (this.player.getItemInUseCount() > 0)
        {
            float itemFactor;
            Item item = this.player.getActiveItemStack().getItem();
            if (item instanceof ItemSword)
            {
                itemFactor = SmartMovingConfig.ITEM_USAGE.swordSpeedFactor;
            }
            else if (item instanceof ItemBow)
            {
                itemFactor = SmartMovingConfig.ITEM_USAGE.bowSpeedFactor;
            }
            else if (item instanceof ItemFood)
            {
                itemFactor = SmartMovingConfig.ITEM_USAGE.foodSpeedFactor;
            }
            else
            {
                itemFactor = SmartMovingConfig.ITEM_USAGE.speedFactor;
            }
            speedFactor *= itemFactor;
        }

        if (this.isCrawling || (this.isCrawlClimbing && !this.isClimbCrawling))
        {
            speedFactor *= SmartMovingConfig.CRAWLING.factor;
        }
        else if (this.isSlow)
        {
            speedFactor *= SmartMovingConfig.GENERIC_SNEAKING.factor;
        }

        if (this.isFast)
        {
            speedFactor *= SmartMovingConfig.GENERIC_SPRINTING.factor;
        }

        if (this.isClimbing)
        {
            if (moveStrafing != 0F || moveForward != 0F)
            {
                speedFactor *= SmartMovingConfig.CLIMB.freeHorizontalSpeedFactor;
            }
        }

        if (this.isCeilingClimbing)
        {
            speedFactor *= SmartMovingConfig.CLIMB.ceilingSpeedFactor;
        }

        return speedFactor;
    }

    private boolean handleSwimming(float moveForward, float moveStrafing, float speedFactor, boolean wasSwimming, boolean wasDiving, boolean isLiquidClimbing, boolean wasJumpingOutOfWater)
    {
        boolean handleSwimmingRejected = false;
        boolean handleSwimming = !this.isFlying && !isLiquidClimbing && (this.player.isInWater() || (wasSwimming && this.isInLiquid()) || (SmartMovingConfig.LAVA.likeWater && this.player.isInLava()));
        if (handleSwimming)
        {
            this.resetClimbing();

            float wasHeightOffset = this.heightOffset;

            boolean useStandard = !SmartMovingConfig.SWIMMING.enable && !SmartMovingConfig.DIVING.enable;
            if (this.player.isRiding())
            {
                this.resetSwimming();
                useStandard = true;
            }

            if (useStandard && this.isCrawling)
            {
                this.standupIfPossible();
            }
            else
            {
                this.resetHeightOffset();
            }

            if (!useStandard)
            {
                this.resetSwimming();

                int i = MathHelper.floor(this.player.posX);
                int j = MathHelper.floor(this.getBoundingBox().minY);
                int k = MathHelper.floor(this.player.posZ);

                boolean swimming = false;
                boolean diving = false;
                boolean dipping = false;

                double j_offset = this.getBoundingBox().minY - j;

                double totalSwimWaterBorder = this.getMaxPlayerLiquidBetween(this.getBoundingBox().maxY - 1.8, this.getBoundingBox().maxY + 1.2);
                double minPlayerSwimWaterCeiling = this.getMinPlayerSolidBetween(this.getBoundingBox().maxY - 1.8, this.getBoundingBox().maxY + 1.2, 0);
                double realTotalSwimWaterBorder = Math.min(totalSwimWaterBorder, minPlayerSwimWaterCeiling);
                double minPlayerSwimWaterDepth = totalSwimWaterBorder - this.getMaxPlayerSolidBetween(totalSwimWaterBorder - 2, totalSwimWaterBorder, 0);
                double realMinPlayerSwimWaterDepth = totalSwimWaterBorder - this.getMaxPlayerSolidBetween(realTotalSwimWaterBorder - 2, realTotalSwimWaterBorder, 0);
                double playerSwimWaterBorder = totalSwimWaterBorder - j - j_offset;

                if (this.isCrawling && playerSwimWaterBorder > SWIM_CRAWL_WATER_TOP_BORDER)
                {
                    this.standupIfPossible();
                }

                double motionYDiff = 0;
                boolean couldStandUp = playerSwimWaterBorder >= 0 && minPlayerSwimWaterDepth <= 1.5;

                boolean diveUp = this.playerBase.getIsJumpingField();
                boolean diveDown = ((EntityPlayerSP) this.entityPlayer).movementInput.sneak && SmartMovingConfig.DIVING.downSneak;
                boolean swimDown = ((EntityPlayerSP) this.entityPlayer).movementInput.sneak && SmartMovingConfig.SWIMMING.downSneak;

                boolean wantShallowSwim = couldStandUp && (wasSwimming || wasDiving);
                if (wantShallowSwim)
                {
                    HashSet<Orientation> orientations = Orientation.getClimbingOrientations(this.player, true, true);
                    for (Orientation orientation : orientations)
                    {
                        if (!(wantShallowSwim = !orientation.isTunnelAhead(this.player.world, i, j, k)))
                        {
                            break;
                        }
                    }
                }

                if (wasSwimming && wantShallowSwim && swimDown)
                {
                    swimDown = false;
                    this.isFakeShallowWaterSneaking = true;
                }

                if (this.isDiving && diveUp && diveDown)
                {
                    diveUp = diveDown = false;
                }

                if (this.isCrawling || this.isClimbCrawling || this.isCrawlClimbing)
                {
                    this.isDipping = true;
                }
                else if (playerSwimWaterBorder >= 0 && playerSwimWaterBorder <= 2)
                {
                    double offset = playerSwimWaterBorder + 0.1625D; // for fine tuning
                    boolean moveSwim = this.player.rotationPitch < 0F && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F || this.player.rotationPitch > 0F && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward < 0F;
                    if (diveUp || moveSwim || wantShallowSwim)
                    {
                        if (offset < 1.4)
                        {
                            dipping = true;
                            if (offset < 1)
                            {
                                motionYDiff = -0.02D;
                            }
                            else
                            {
                                motionYDiff = -0.01D;
                            }
                        }
                        else if (offset < 1.9)
                        {
                            swimming = true;
                            if (offset < 1.6)
                            {
                                motionYDiff = -0.01D;
                            }
                            else if (offset < 1.62)
                            {
                                motionYDiff = -0.005D;
                            }
                            else if (offset < 1.64)
                            {
                                motionYDiff = -0.0025D;
                            }
                            else if (offset < 1.66)
                            {
                                motionYDiff = -0.00125D;
                            }
                            else if (offset < 1.664)
                            {
                                motionYDiff = -0.000625D;
                            }
                            else if (offset < 1.668)
                            {
                                motionYDiff = 0D;
                            }
                            else if (offset < 1.672)
                            {
                                motionYDiff = 0.000625D;
                            }
                            else if (offset < 1.676)
                            {
                                motionYDiff = 0.00125D;
                            }
                            else if (offset < 1.68)
                            {
                                motionYDiff = 0.0025D;
                            }
                            else if (offset < 1.7)
                            {
                                motionYDiff = 0.005D;
                            }
                            else if (offset < 1.8)
                            {
                                motionYDiff = 0.01D;
                            }
                            else
                            {
                                motionYDiff = 0.02D;
                            }
                        }
                        else
                        {
                            diving = true;
                            if (diveUp)
                            {
                                motionYDiff = 0.05D * (this.isFast ? SmartMovingConfig.GENERIC_SPRINTING.factor : 1F);
                            }
                            else if (diveDown)
                            {
                                motionYDiff = 0.01 - 0.1 * speedFactor;
                            }
                            else
                            {
                                motionYDiff = moveSwim ? 0.04D : 0.02D;
                            }
                        }
                    }
                    else
                    {
                        if (offset < 1.5)
                        {
                            dipping = true;
                            motionYDiff = -0.02D;
                        }
                        else
                        {
                            diving = true;
                            if (diveDown)
                            {
                                motionYDiff = 0.01 - 0.1 * speedFactor;
                            }
                            else if (offset < 1.8)
                            {
                                motionYDiff = -0.02D;
                            }
                            else if (offset < 1.82)
                            {
                                motionYDiff = -0.01D;
                            }
                            else if (offset < 1.84)
                            {
                                motionYDiff = -0.005D;
                            }
                            else if (offset < 1.86)
                            {
                                motionYDiff = -0.0025D;
                            }
                            else if (offset < 1.864)
                            {
                                motionYDiff = -0.00125D;
                            }
                            else if (offset < 1.868)
                            {
                                motionYDiff = 0D;
                            }
                            else if (offset < 1.872)
                            {
                                motionYDiff = 0.00125D;
                            }
                            else if (offset < 1.876)
                            {
                                motionYDiff = 0.0025D;
                            }
                            else if (offset < 1.88)
                            {
                                motionYDiff = 0.005D;
                            }
                            else if (offset < 1.9)
                            {
                                motionYDiff = 0.01D;
                            }
                            else
                            {
                                motionYDiff = 0.01D;
                            }
                        }
                    }
                }
                else if (playerSwimWaterBorder > 2)
                {
                    diving = true;
                    if (diveUp)
                    {
                        if (this.isFast && playerSwimWaterBorder < 2.5 && this.isAirBlock(i, j + 3, k))
                        {
                            motionYDiff = 0.11D / SmartMovingConfig.GENERIC_SPRINTING.factor;
                        }
                        else
                        {
                            motionYDiff = 0.01 + 0.1 * speedFactor;
                        }
                    }
                    else if (diveDown)
                    {
                        motionYDiff = 0.01 - 0.1 * speedFactor;
                    }
                    else
                    {
                        motionYDiff = 0.01D;
                    }
                }
                else
                {
                    handleSwimmingRejected = true;
                }

                this.dippingDepth = (float) playerSwimWaterBorder;
                float playerCrawlWaterBorder = this.dippingDepth + wasHeightOffset;
                if ((this.isCrawling || this.isSliding) && playerCrawlWaterBorder < SWIM_CRAWL_WATER_MAX_BORDER)
                {
                    if (playerCrawlWaterBorder < SWIM_CRAWL_WATER_TOP_BORDER)
                    {
                        // continue crawling in shallow water
                        this.setHeightOffset(wasHeightOffset);
                        handleSwimmingRejected = true;
                    }
                    else
                    {
                        // from crawling in shallow water to swimming/diving
                        if (wantShallowSwim)
                        {
                            this.move(0, 0.1, 0, true); // to avoid diving in shallow water
                        }
                        this.isCrawling = false;
                        this.isDiving = false;
                        this.isSwimming = true;
                        this.isDipping = false;
                    }
                }

                if (!handleSwimmingRejected)
                {
                    swimming = swimming && SmartMovingConfig.SWIMMING.enable;
                    diving = diving && SmartMovingConfig.DIVING.enable;
                    dipping = dipping && SmartMovingConfig.SWIMMING.enable;
                    useStandard = !swimming && !diving && !dipping;

                    if (!useStandard)
                    {
                        if (diveUp)
                        {
                            this.player.motionY -= 0.04D;
                        }

                        if (swimming)
                        {
                            this.player.motionX *= 0.85D;
                            this.player.motionY *= 0.85D;
                            this.player.motionZ *= 0.85D;
                        }
                        else if (diving)
                        {
                            this.player.motionX *= 0.83D;
                            this.player.motionY *= 0.83D;
                            this.player.motionZ *= 0.83D;
                        }
                        else if (dipping)
                        {
                            this.player.motionX *= 0.80D;
                            this.player.motionY *= 0.83D;
                            this.player.motionZ *= 0.80D;
                        }
                        else
                        {
                            this.player.motionX *= 0.9D;
                            this.player.motionY *= 0.85D;
                            this.player.motionZ *= 0.9D;
                        }

                        boolean moveFlying = true;
                        boolean levitating = diving && !diveUp && !diveDown && moveStrafing == 0F && moveForward == 0F;

                        if (diving)
                        {
                            speedFactor *= SmartMovingConfig.DIVING.speedFactor;
                        }
                        if (swimming)
                        {
                            speedFactor *= SmartMovingConfig.SWIMMING.speedFactor;
                        }

                        if (swimming || diving)
                        {
                            this.waterMovementTicks++;
                        }
                        else
                        {
                            this.waterMovementTicks = 0;
                        }

                        boolean wantJumpOutOfWater = (moveForward != 0 || moveStrafing != 0) && this.player.collidedHorizontally && diveUp && !this.isSlow;
                        this.isJumpingOutOfWater = wantJumpOutOfWater && (this.waterMovementTicks > 10 || this.player.onGround || wasJumpingOutOfWater);

                        if (diving)
                        {
                            if (diveUp || diveDown || levitating)
                            {
                                this.player.motionY = (this.player.motionY + motionYDiff) * 0.6;
                            }
                            else
                            {
                                this.moveFlying((float) motionYDiff, moveStrafing, moveForward, 0.02F * speedFactor, SmartMovingConfig.USER_INTERFACE.diveControlVertical);
                            }
                            moveFlying = false;
                        }
                        else if (swimming && swimDown)
                        {
                            this.player.motionY = (this.player.motionY + motionYDiff) * 0.6;
                        }
                        else if (this.isJumpingOutOfWater)
                        {
                            this.player.motionY = 0.3D;
                        }
                        else
                        {
                            this.player.motionY += motionYDiff;
                        }

                        this.isDiving = diving;
                        this.isLevitating = levitating;
                        this.isSwimming = swimming;
                        this.isShallowDiveOrSwim = couldStandUp && (this.isDiving || this.isSwimming);
                        this.isDipping = dipping;

                        if (this.isDiving || this.isSwimming)
                        {
                            this.setHeightOffset(-1F);
                        }

                        if (this.isShallowDiveOrSwim && realMinPlayerSwimWaterDepth < SWIM_CRAWL_WATER_BOTTOM_BORDER)
                        {
                            if (this.isSlow)
                            {
                                // from swimming/diving in shallow water to crawling in shallow water
                                this.setHeightOffset(-1F);
                                this.isCrawling = true;
                            }
                            else
                            {
                                // from swimming/diving in shallow water to walking in shallow water
                                this.resetHeightOffset();
                                this.player.move(MoverType.SELF, 0, this.getMaxPlayerSolidBetween(this.getBoundingBox().minY, this.getBoundingBox().maxY, 0) - this.getBoundingBox().minY, 0);
                                this.isCrawling = false;
                            }
                            this.isDiving = false;
                            this.isSwimming = false;
                            this.isShallowDiveOrSwim = false;
                            this.isDipping = true;
                        }

                        if (moveFlying)
                        {
                            this.moveFlying(0f, moveStrafing, moveForward, 0.02F * speedFactor, false);
                        }
                        this.player.move(MoverType.SELF, this.player.motionX, this.player.motionY, this.player.motionZ);
                    }
                }
            }
            else
            {
                this.isDiving = false;
                this.isSwimming = false;
                this.isShallowDiveOrSwim = false;
                this.isDipping = false;
                this.isStillSwimmingJump = false;
            }

            if (useStandard)
            {
                this.resetSwimming();

                if (this.isCrawling)
                {
                    this.setHeightOffset(wasHeightOffset);
                }

                double dY = this.player.posY;
                this.moveFlying(0f, moveStrafing, moveForward, 0.02F, false);
                this.player.move(MoverType.SELF, this.player.motionX, this.player.motionY, this.player.motionZ);

                this.player.motionX *= 0.80000001192092896D;
                this.player.motionY *= 0.80000001192092896D;
                this.player.motionZ *= 0.80000001192092896D;
                this.player.motionY -= 0.02D;
                if (this.player.collidedHorizontally && this.player.isOffsetPositionInLiquid(this.player.motionX, ((this.player.motionY + 0.60000002384185791D) - this.player.posY) + dY, this.player.motionZ))
                {
                    this.player.motionY = 0.30000001192092896D;
                }
            }
        }

        return handleSwimming && !handleSwimmingRejected;
    }

    private boolean handleLava(float moveForward, float moveStrafing, boolean handledSwimming, boolean isLiquidClimbing)
    {
        boolean handleLava = !this.isFlying && !handledSwimming && !isLiquidClimbing && this.player.isInLava();
        if (handleLava)
        {
            this.standupIfPossible();
            this.resetClimbing();
            this.resetSwimming();

            double d1 = this.player.posY;
            this.moveFlying(0f, moveStrafing, moveForward, 0.02F, false);
            this.player.move(MoverType.SELF, this.player.motionX, this.player.motionY, this.player.motionZ);
            this.player.motionX *= 0.5D;
            this.player.motionY *= 0.5D;
            this.player.motionZ *= 0.5D;
            this.player.motionY -= 0.02D;
            if (this.player.collidedHorizontally && this.player.isOffsetPositionInLiquid(this.player.motionX, ((this.player.motionY + 0.60000002384185791D) - this.player.posY) + d1, this.player.motionZ))
            {
                this.player.motionY = 0.30000001192092896D;
            }
        }
        return handleLava;
    }

    private boolean handleAlternativeFlying(float moveForward, float moveStrafing, float speedFactor, boolean handledSwimming, boolean handledLava)
    {
        boolean handleAlternativeFlying = !handledSwimming && !handledLava && this.player.capabilities.isFlying && SmartMovingConfig.SMART_FLYING.enable;
        if (handleAlternativeFlying)
        {
            this.resetSwimming();
            this.resetClimbing();

            float moveUpward = 0F;
            if (((EntityPlayerSP) this.entityPlayer).movementInput.sneak)
            {
                this.player.motionY += 0.15D;
                moveUpward -= 0.98F;
            }
            if (((EntityPlayerSP) this.entityPlayer).movementInput.jump)
            {
                this.player.motionY -= 0.15D;
                moveUpward += 0.98F;
            }

            this.moveFlying(moveUpward, moveStrafing, moveForward, speedFactor * 0.05F * SmartMovingConfig.SMART_FLYING.speedFactor, SmartMovingConfig.USER_INTERFACE.flyControlVertical);

            this.player.move(MoverType.SELF, this.player.motionX, this.player.motionY, this.player.motionZ);

            this.player.motionX *= HORIZONTAL_AIR_DAMPING;
            this.player.motionY *= HORIZONTAL_AIR_DAMPING;
            this.player.motionZ *= HORIZONTAL_AIR_DAMPING;
        }
        return handleAlternativeFlying;
    }

    private void handleLand(float moveForward, float moveStrafing, float speedFactor, boolean handledSwimming, boolean handledLava, boolean handledAlternativeFlying, boolean wasShortInWater, boolean wasClimbing, boolean wasCeilingClimbing)
    {
        if (!handledSwimming && !handledLava && !handledAlternativeFlying)
        {
            this.resetSwimming();

            if (!this.grabButton.isPressed)
            {
                this.fromSwimmingOrDiving(wasShortInWater);
            }

            boolean isOnLadder = this.isOnLadder(this.isClimbCrawling);
            boolean isOnVine = this.isOnVine(this.isClimbCrawling);

            float horizontalDamping = this.landMotion(moveForward, moveStrafing, speedFactor, isOnLadder, isOnVine);
            boolean crawlingThroughWeb = (this.isCrawling || this.isCrawlClimbing) && this.playerBase.getIsInWebField();
            this.move(this.player.motionX, this.player.motionY, this.player.motionZ, crawlingThroughWeb);

            this.handleClimbing(isOnLadder, isOnVine, wasClimbing);
            this.handleCeilingClimbing(wasCeilingClimbing);
            this.setLandMotions(horizontalDamping);
        }

        this.landMotionPost(wasShortInWater);
    }

    private void move(double motionX, double motionY, double motionZ, boolean relocate)
    {
        boolean isInWeb = this.playerBase.getIsInWebField();
        if (relocate)
        {
            this.playerBase.setIsInWebField(false);
        }
        this.player.move(MoverType.SELF, motionX, motionY, motionZ);
        if (relocate)
        {
            this.playerBase.setIsInWebField(isInWeb);
        }
    }

    private float landMotion(float moveForward, float moveStrafing, float speedFactor, boolean isOnLadder, boolean isOnVine)
    {
        float horizontalDamping;
        final boolean isUsingItem = this.player.getItemInUseCount() > 0;
        if (this.player.onGround && !this.isJumping)
        {
            Block block = this.getBlock(MathHelper.floor(this.player.posX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.player.posZ));
            if (block != null)
            {
                horizontalDamping = block.slipperiness * HORIZONTAL_AIR_DAMPING;
            }
            else
            {
                horizontalDamping = HORIZONTAL_GROUND_DAMPING;
            }

            if (((EntityPlayerSP) this.entityPlayer).movementInput.jump && this.isFast && ConfigHelper.isJumpingEnabled(ConfigHelper.Sprinting, ConfigHelper.Up))
            {
                speedFactor *= SmartMovingConfig.JUMPING.sprintVerticalFactor;
            }
        }
        else
        {
            horizontalDamping = HORIZONTAL_AIR_DAMPING;
        }

        if (this.isClimbing && this.climbingUpIsBlockedByLadder())
        {
            this.moveFlying(0.07F, moveStrafing, moveForward, 0.07F, true);
        }
        else if (this.isClimbing && this.climbingUpIsBlockedByTrapDoor())
        {
            this.moveFlying(0F, moveStrafing, moveForward, 0.09F, true);
        }
        else if (this.isClimbing && this.climbingUpIsBlockedByCobbleStoneWall())
        {
            this.moveFlying(0F, moveStrafing, moveForward, 0.07F, true);
        }
        else if (!this.isSliding)
        {
            if (this.isHeadJumping)
            {
                speedFactor *= SmartMovingConfig.HEAD_JUMPING.controlFactor;
            }
            else if (!this.player.onGround && !this.player.capabilities.isFlying && !this.isFlying)
            {
                speedFactor *= SmartMovingConfig.JUMPING.controlFactor;
            }

            float f3 = 0.1627714F / (horizontalDamping * horizontalDamping * horizontalDamping);
            float f4 = this.player.onGround ? this.getLandMovementFactor() * f3 : this.player.jumpMovementFactor;
            float rawSpeed = this.player.isSprinting() ? f4 / 1.3F : f4;
            if (SmartMovingConfig.STANDARD_SPRINTING.enable && this.isRunning() && !this.isFast)
            {
                speedFactor *= SmartMovingConfig.STANDARD_SPRINTING.factor;
            }

            this.moveFlying(0F, moveStrafing, moveForward, rawSpeed * speedFactor, false);
        }

        if (this.player.onGround && !this.isJumping)
        {
            Block block = this.getBlock(MathHelper.floor(this.player.posX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.player.posZ));
            if (block != null)
            {
                float slipperiness = block.slipperiness;
                if (this.isSliding)
                {
                    horizontalDamping = 1F / (((1F / slipperiness) - 1F) / 25F * SmartMovingConfig.SLIDING.slipperinessFactor + 1F) * 0.98F;
                    if (moveStrafing != 0 && SmartMovingConfig.SLIDING.controlAngle > 0)
                    {
                        double angle = -Math.atan(this.player.motionX / this.player.motionZ);
                        if (!Double.isNaN(angle))
                        {
                            if (this.player.motionZ < 0)
                            {
                                angle += Math.PI;
                            }

                            angle -= SmartMovingConfig.SLIDING.controlAngle / RadiantToAngle * Math.signum(moveStrafing);

                            double hMotion = Math.sqrt(this.player.motionX * this.player.motionX + this.player.motionZ * this.player.motionZ);
                            this.player.motionX = hMotion * -Math.sin(angle);
                            this.player.motionZ = hMotion * Math.cos(angle);
                        }
                    }
                }
                else
                {
                    horizontalDamping = slipperiness * HORIZONTAL_AIR_DAMPING;
                }
            }
            else
            {
                horizontalDamping = HORIZONTAL_GROUND_DAMPING;
            }
        }
        else if (this.isAerodynamic)
        {
            horizontalDamping = HORIZONTAL_AERODYNAMIC_DAMPING;
        }
        else
        {
            horizontalDamping = HORIZONTAL_AIR_DAMPING;
        }

        if (isOnLadder || isOnVine)
        {
            float f4 = 0.15F;
            if (this.player.motionX < -f4)
            {
                this.player.motionX = -f4;
            }
            if (this.player.motionX > f4)
            {
                this.player.motionX = f4;
            }
            if (this.player.motionZ < (-f4))
            {
                this.player.motionZ = -f4;
            }
            if (this.player.motionZ > f4)
            {
                this.player.motionZ = f4;
            }
            boolean notTotalFreeClimbing = !this.isClimbing && isOnLadder && !ConfigHelper.isTotalFreeLadderClimb() || isOnVine && !ConfigHelper.isTotalFreeVineClimb();
            if (notTotalFreeClimbing)
            {
                this.player.fallDistance = 0.0F;
                this.player.motionY = Math.max(this.player.motionY, -0.15 * this.getSpeedFactor());
            }
            if (ConfigHelper.isFreeBaseClimb())
            {
                if (isUsingItem || ((EntityPlayerSP) this.entityPlayer).movementInput.sneak && this.player.motionY < 0.0D && !this.player.onGround && notTotalFreeClimbing)
                {
                    this.player.motionY = 0.0D;
                }
            }
            else
            {
                if (isUsingItem || this.playerBase.localIsSneaking() && this.player.motionY < 0.0D)
                {
                    this.player.motionY = 0.0D;
                }
            }
        }
        else if (SmartMovingConfig.CLIMB.freeLadderAuto && moveForward > 0)
        {
            int j = MathHelper.floor(this.getBoundingBox().minY);
            double jGap = this.getBoundingBox().minY - j;

            if (jGap < 0.1)
            {
                int i = MathHelper.floor(this.player.posX);
                int k = MathHelper.floor(this.player.posZ);

                if (Orientation.isLadder(this.getState(i, j - 1, k)))
                {
                    this.player.motionY = Math.max(this.player.motionY, 0.0);
                }
            }
        }
        return horizontalDamping;
    }

    private void handleClimbing(boolean isOnLadder, boolean isOnVine, boolean wasClimbing)
    {
        this.resetClimbing();

        boolean isOnLadderOrVine = isOnLadder || isOnVine;

        if (ConfigHelper.isStandardBaseClimb() && this.player.collidedHorizontally && isOnLadderOrVine)
        {
            this.player.motionY = 0.2 * this.getSpeedFactor();
        }

        if (ConfigHelper.isSimpleBaseClimb() && this.player.collidedHorizontally && isOnLadderOrVine)
        {
            int i = MathHelper.floor(this.player.posX);
            int j = MathHelper.floor(this.getBoundingBox().minY);
            int k = MathHelper.floor(this.player.posZ);

            boolean feet = Orientation.isClimbable(this.player.world, i, j, k);
            boolean hands = Orientation.isClimbable(this.player.world, i, j + 1, k);

            if (feet && hands)
            {
                this.player.motionY = FAST_UP_MOTION;
            }
            else if (feet)
            {
                this.player.motionY = FAST_UP_MOTION;
            }
            else if (hands)
            {
                this.player.motionY = SLOW_UP_MOTION;
            }
            else
            {
                this.player.motionY = 0.0D;
            }

            this.player.motionY *= this.getSpeedFactor();
        }

        if (ConfigHelper.isSmartBaseClimb() || SmartMovingConfig.CLIMB.enable)
        {
            double id = this.player.posX;
            double jd = this.getBoundingBox().minY;
            double kd = this.player.posZ;

            int i = MathHelper.floor(id);
            int j = MathHelper.floor(jd);
            int k = MathHelper.floor(kd);

            if (ConfigHelper.isSmartBaseClimb() && isOnLadderOrVine && this.player.collidedHorizontally)
            {
                boolean feet = Orientation.isClimbable(this.player.world, i, j, k);
                boolean hands = Orientation.isClimbable(this.player.world, i, j + 1, k);

                if (feet && hands)
                {
                    this.player.motionY = FAST_UP_MOTION;
                }
                else if (feet)
                {
                    boolean handsSubstitute = Orientation.PZ.isHandsLadderSubstitute(this.player.world, i, j + 1, k) || Orientation.NZ.isHandsLadderSubstitute(this.player.world, i, j + 1, k) || Orientation.ZP.isHandsLadderSubstitute(this.player.world, i, j + 1, k) || Orientation.ZN.isHandsLadderSubstitute(this.player.world, i, j + 1, k);

                    if (handsSubstitute)
                    {
                        this.player.motionY = FAST_UP_MOTION;
                    }
                    else
                    {
                        this.player.motionY = SLOW_UP_MOTION;
                    }
                }
                else if (hands)
                {
                    boolean feetSubstitute = Orientation.ZZ.isFeetLadderSubstitute(this.player.world, i, j, k) || Orientation.PZ.isFeetLadderSubstitute(this.player.world, i, j, k) || Orientation.NZ.isFeetLadderSubstitute(this.player.world, i, j, k) || Orientation.ZP.isFeetLadderSubstitute(this.player.world, i, j, k) || Orientation.ZN.isFeetLadderSubstitute(this.player.world, i, j, k);
                    if (feetSubstitute)
                    {
                        this.player.motionY = FAST_UP_MOTION;
                    }
                    else
                    {
                        this.player.motionY = SLOW_UP_MOTION;
                    }
                }
                else
                {
                    this.player.motionY = 0.0D;
                }

                this.player.motionY *= this.getSpeedFactor();
            }

            if (SmartMovingConfig.CLIMB.enable && this.player.fallDistance <= SmartMovingConfig.CLIMB.fallMaximumDistance && (!isOnLadderOrVine || ConfigHelper.isFreeBaseClimb()))
            {
                boolean exhaustionAllowsClimbing = !SmartMovingConfig.CLIMB.exhaustion || (this.exhaustion <= SmartMovingConfig.CLIMB.exhaustionStop && (wasClimbing || this.exhaustion <= SmartMovingConfig.CLIMB.exhaustionStart));

                boolean preferClimb = false;
                if (this.wantClimbUp || this.wantClimbDown)
                {
                    if (SmartMovingConfig.CLIMB.exhaustion)
                    {
                        this.maxExhaustionForAction = Math.min(this.maxExhaustionForAction, SmartMovingConfig.CLIMB.exhaustionStop);
                        this.maxExhaustionToStartAction = Math.min(this.maxExhaustionToStartAction, SmartMovingConfig.CLIMB.exhaustionStart);
                    }
                    if (exhaustionAllowsClimbing)
                    {
                        preferClimb = true;
                    }
                }
                if (preferClimb)
                {
                    boolean isSmallClimbing = this.isCrawling || this.isSliding;
                    if (this.isClimbCrawling || this.isCrawlClimbing || isSmallClimbing)
                    {
                        jd += -1D;
                    }

                    float rotation = this.player.rotationYaw % 360F;
                    if (rotation < 0)
                    {
                        rotation += 360F;
                    }

                    double jh = jd * 2D + 1;

                    HandsClimbing handsClimbing = HandsClimbing.NONE;
                    FeetClimbing feetClimbing = FeetClimbing.NONE;

                    inout_handsClimbing[0] = handsClimbing;
                    inout_feetClimbing[0] = feetClimbing;

                    out_handsClimbGap.reset();
                    out_feetClimbGap.reset();

                    Orientation.PZ.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
                    Orientation.NZ.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
                    Orientation.ZP.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
                    Orientation.ZN.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, isSmallClimbing, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);

                    handsClimbing = inout_handsClimbing[0];
                    feetClimbing = inout_feetClimbing[0];

                    this.isNeighborClimbing = handsClimbing != HandsClimbing.NONE || feetClimbing != FeetClimbing.NONE;
                    this.hasNeighborClimbGap = out_handsClimbGap.canStand || out_feetClimbGap.canStand;
                    this.hasNeighborClimbCrawlGap = out_handsClimbGap.mustCrawl || out_feetClimbGap.mustCrawl;

                    if (!isSmallClimbing)
                    {
                        Orientation.PP.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, false, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
                        Orientation.NP.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, false, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
                        Orientation.NN.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, false, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
                        Orientation.PN.seekClimbGap(rotation, this.player.world, i, id, jh, k, kd, this.isClimbCrawling, this.isCrawlClimbing, false, inout_handsClimbing, inout_feetClimbing, out_handsClimbGap, out_feetClimbGap);
                    }

                    handsClimbing = inout_handsClimbing[0];
                    feetClimbing = inout_feetClimbing[0];

                    this.hasClimbGap = out_handsClimbGap.canStand || out_feetClimbGap.canStand;
                    this.hasClimbCrawlGap = out_handsClimbGap.mustCrawl || out_feetClimbGap.mustCrawl;

                    if (handsClimbing == HandsClimbing.BOTTOM_HOLD && Orientation.isLadder(this.getState(i, j + 2, k)))
                    {
                        Orientation ladderOrientation = Orientation.getKnownLadderOrientation(this.player.world, i, j + 2, k);
                        int remote_i = i + ladderOrientation.i;
                        int remote_k = k + ladderOrientation.k;
                        if (!this.getState(remote_i, j, remote_k).getMaterial().isSolid() && !this.getState(remote_i, j + 1, remote_k).getMaterial().isSolid())
                        {
                            handsClimbing = HandsClimbing.NONE;
                        }
                    }

                    if (!this.grabButton.isPressed && handsClimbing == HandsClimbing.UP && feetClimbing == FeetClimbing.NONE)
                    {
                        if (!this.player.collidedHorizontally && this.isAirBlock(i, j, k) && this.isAirBlock(i, j + 1, k))
                        {
                            handsClimbing = HandsClimbing.NONE;
                        }
                    }

                    // feet climbing only with balancing in gaps or combined with hand climbing
                    if (feetClimbing.IsRelevant() || handsClimbing.IsRelevant())
                    {
                        if (this.wantClimbUp)
                        {
                            if (this.isSliding && handsClimbing.IsRelevant())
                            {
                                this.isSliding = false;
                                this.isCrawling = true;
                            }

                            handsClimbing = handsClimbing.ToUp();

                            if (feetClimbing == FeetClimbing.FAST_UP && !(handsClimbing == HandsClimbing.NONE && this.player.onGround && out_feetClimbGap.block != Block.getBlockFromName("bed")))
                            {
                                // climbing fast
                                this.setShouldClimbSpeed(FAST_UP_MOTION, HandsClimbing.NO_GRAB, FeetClimbing.DOWN_STEP);
                            }
                            else if ((this.hasClimbGap || this.hasClimbCrawlGap) && handsClimbing == HandsClimbing.FAST_UP && (feetClimbing == FeetClimbing.NONE || feetClimbing == FeetClimbing.BASE_WITH_HANDS))
                            {
                                // climb into crawl gap
                                this.setShouldClimbSpeed(feetClimbing == FeetClimbing.NONE ? SLOW_UP_MOTION : FAST_UP_MOTION, HandsClimbing.MIDDLE_GRAB, FeetClimbing.DOWN_STEP);
                            }
                            else if (feetClimbing.IsRelevant() && handsClimbing.IsRelevant() && !(feetClimbing == FeetClimbing.BASE_HOLD && handsClimbing == HandsClimbing.SINK) && !(handsClimbing == HandsClimbing.SINK && feetClimbing == FeetClimbing.TOP_WITH_HANDS) && !(handsClimbing == HandsClimbing.TOP_HOLD && feetClimbing == FeetClimbing.TOP_WITH_HANDS))
                            {
                                // climbing all limbed
                                this.setShouldClimbSpeed(MEDIUM_UP_MOTION, (this.hasClimbGap || this.hasClimbCrawlGap) && !(handsClimbing == HandsClimbing.SINK && feetClimbing == FeetClimbing.BASE_WITH_HANDS) ? HandsClimbing.MIDDLE_GRAB : HandsClimbing.UP_GRAB, FeetClimbing.DOWN_STEP);
                            }
                            else if (handsClimbing.IsUp())
                            {
                                // climbing slow
                                this.setShouldClimbSpeed(SLOW_UP_MOTION);
                            }
                            else if (handsClimbing == HandsClimbing.TOP_HOLD || feetClimbing == FeetClimbing.BASE_HOLD || (feetClimbing == FeetClimbing.SLOW_UP_WITH_HOLD_WITHOUT_HANDS && handsClimbing == HandsClimbing.NONE))
                            {
                                // holding at top
                                if (!this.jumpButton.startPressed || !(this.isClimbJumping = this.tryJump(feetClimbing != FeetClimbing.NONE ? ConfigHelper.ClimbUp : ConfigHelper.ClimbUpHandsOnly, null, null, null)))
                                {
                                    if (handsClimbing == HandsClimbing.SINK && feetClimbing == FeetClimbing.BASE_HOLD || handsClimbing == HandsClimbing.TOP_HOLD && feetClimbing == FeetClimbing.TOP_WITH_HANDS)
                                    {
                                        this.setShouldClimbSpeed(HOLD_MOTION, HandsClimbing.MIDDLE_GRAB, FeetClimbing.DOWN_STEP);
                                    }
                                    else
                                    {
                                        this.setShouldClimbSpeed(HOLD_MOTION);
                                    }
                                }
                            }
                            else if (handsClimbing == HandsClimbing.SINK || (feetClimbing == FeetClimbing.SLOW_UP_WITH_SINK_WITHOUT_HANDS && handsClimbing == HandsClimbing.NONE))
                            {
                                // sinking unwillingly
                                this.setShouldClimbSpeed(SINK_DOWN_MOTION);
                            }
                        }
                        else if (this.wantClimbDown)
                        {
                            handsClimbing = handsClimbing.ToDown();

                            if (handsClimbing == HandsClimbing.BOTTOM_HOLD && !feetClimbing.IsIndependentlyRelevant())
                            {
                                // holding at bottom
                                this.setShouldClimbSpeed(HOLD_MOTION);
                            }
                            else if (handsClimbing.IsRelevant())
                            {
                                // sinking willingly
                                if (feetClimbing == FeetClimbing.FAST_UP)
                                {
                                    this.setShouldClimbSpeed(CLIMB_DOWN_MOTION, HandsClimbing.NO_GRAB, FeetClimbing.DOWN_STEP);
                                }
                                else if (feetClimbing == FeetClimbing.SLOW_UP_WITH_HOLD_WITHOUT_HANDS)
                                {
                                    this.setShouldClimbSpeed(CLIMB_DOWN_MOTION);
                                }
                                else if (feetClimbing == FeetClimbing.TOP_WITH_HANDS)
                                {
                                    this.setShouldClimbSpeed(CLIMB_DOWN_MOTION);
                                }
                                else if (feetClimbing == FeetClimbing.BASE_WITH_HANDS || feetClimbing == FeetClimbing.BASE_HOLD)
                                {
                                    if ((handsClimbing != HandsClimbing.NONE && handsClimbing != HandsClimbing.UP) || (handsClimbing == HandsClimbing.UP && feetClimbing == FeetClimbing.BASE_HOLD))
                                    {
                                        this.setShouldClimbSpeed(CLIMB_DOWN_MOTION);
                                    }
                                    else
                                    {
                                        this.setShouldClimbSpeed(SINK_DOWN_MOTION);
                                    }
                                }
                                else
                                {
                                    this.setShouldClimbSpeed(SINK_DOWN_MOTION, handsClimbing == HandsClimbing.FAST_UP ? HandsClimbing.MIDDLE_GRAB : HandsClimbing.UP_GRAB, FeetClimbing.NO_STEP);
                                }
                            }

                            if (this.isClimbHolding)
                            {
                                // holding
                                this.setOnlyShouldClimbSpeed(HOLD_MOTION);

                                if (this.jumpButton.startPressed)
                                {
                                    boolean handsOnly = feetClimbing != FeetClimbing.NONE;

                                    int type = (SmartMovingConfig.USER_INTERFACE.jumpClimbBackHeadOnGrab == this.grabButton.isPressed) ? (handsOnly ? ConfigHelper.ClimbBackHead : ConfigHelper.ClimbBackHeadHandsOnly) : (handsOnly ? ConfigHelper.ClimbBackUp : ConfigHelper.ClimbBackUpHandsOnly);

                                    float jumpAngle = this.player.rotationYaw + 180F;
                                    if (this.tryJump(type, null, null, jumpAngle))
                                    {
                                        this.continueWallJumping = !this.isHeadJumping;
                                        this.isClimbing = false;
                                        this.player.rotationYaw = jumpAngle;
                                        this.onStartClimbBackJump();
                                    }
                                }
                            }
                        }

                        if (this.isClimbing)
                        {
                            this.handleCrash(SmartMovingConfig.CLIMB.fallDamageStartDistance, SmartMovingConfig.CLIMB.fallDamageFactor);
                        }

                        if (this.wantClimbUp || this.wantClimbDown)
                        {
                            if (handsClimbing == HandsClimbing.NONE)
                            {
                                this.actualHandsClimbType = HandsClimbing.NO_GRAB;
                            }
                            else if (feetClimbing == FeetClimbing.NONE)
                            {
                                this.actualFeetClimbType = FeetClimbing.NO_STEP;
                            }

                            this.handsEdgeBlock = out_handsClimbGap.block;
                            this.handsEdgeMeta = out_handsClimbGap.meta;
                            this.feetEdgeBlock = out_feetClimbGap.block;
                            this.feetEdgeMeta = out_feetClimbGap.meta;
                        }
                    }
                }

                this.isHandsVineClimbing = this.isClimbing && this.handsEdgeBlock == Block.getBlockFromName("vine");
                this.isFeetVineClimbing = this.isClimbing && this.feetEdgeBlock == Block.getBlockFromName("vine");

                this.isVineAnyClimbing = this.isHandsVineClimbing || this.isFeetVineClimbing;

                this.isVineOnlyClimbing = this.isVineAnyClimbing && !(this.handsEdgeBlock != null && this.handsEdgeBlock != Block.getBlockFromName("vine") || this.feetEdgeBlock != null && this.feetEdgeBlock != Block.getBlockFromName("vine"));
            }
        }
    }

    private void handleCeilingClimbing(boolean wasCeilingClimbing)
    {
        boolean exhaustionAllowsClimbCeiling = !SmartMovingConfig.CLIMB.ceilingExhaustion || (this.exhaustion <= SmartMovingConfig.CLIMB.ceilingExhaustionStop && (wasCeilingClimbing || this.exhaustion <= SmartMovingConfig.CLIMB.ceilingExhaustionStart));

        boolean climbCeilingCrawlingStartConflict = !SmartMovingConfig.CLIMB.enable && this.isCrawling && !this.wasCrawling;
        boolean couldClimbCeiling = this.wantClimbCeiling && !this.isClimbing && (!this.isCrawling || climbCeilingCrawlingStartConflict) && !this.isCrawlClimbing;
        if (couldClimbCeiling && SmartMovingConfig.CLIMB.ceilingExhaustion)
        {
            this.maxExhaustionForAction = Math.min(this.maxExhaustionForAction, SmartMovingConfig.CLIMB.ceilingExhaustionStop);
            this.maxExhaustionToStartAction = Math.min(this.maxExhaustionToStartAction, SmartMovingConfig.CLIMB.ceilingExhaustionStart);
        }

        if (couldClimbCeiling && exhaustionAllowsClimbCeiling)
        {
            double id = this.player.posX;
            double jd = this.getBoundingBox().maxY + (climbCeilingCrawlingStartConflict ? 1F : 0F);
            double kd = this.player.posZ;

            int i = MathHelper.floor(id);
            int j = MathHelper.floor(jd);
            int k = MathHelper.floor(kd);

            Block topBlock = this.supportsCeilingClimbing(i, j, k);
            Block bottomBlock = this.supportsCeilingClimbing(i, j + 1, k);

            boolean topCeilingClimbing = topBlock != null;
            boolean bottomCeilingClimbing = bottomBlock != null;

            if (topCeilingClimbing || bottomCeilingClimbing)
            {
                double jgap = 1D - jd + j;
                if (bottomCeilingClimbing)
                {
                    jgap++;
                }

                double actuallySolidHeight = this.getMinPlayerSolidBetween(jd, jd + 0.6, 0.2);
                if (jgap < 1.9 && actuallySolidHeight < jd + 0.5)
                {
                    if (jgap > 1.2)
                    {
                        this.player.motionY = 0.12;
                    }
                    else if (jgap > 1.115)
                    {
                        this.player.motionY = 0.08;
                    }
                    else
                    {
                        this.player.motionY = 0.04;
                    }

                    this.player.fallDistance = 0.0F;
                    this.isCeilingClimbing = true;
                    this.handsEdgeBlock = topCeilingClimbing ? topBlock : bottomBlock;
                }
            }
        }

        if (this.isCeilingClimbing && climbCeilingCrawlingStartConflict)
        {
            this.isCrawling = false;
            this.resetHeightOffset();
            this.move(0, 1D, 0, true);
        }
    }

    private void setLandMotions(float horizontalDamping)
    {
        this.player.motionY -= 0.08D;
        this.player.motionY *= 0.98D;
        this.player.motionX *= horizontalDamping;
        this.player.motionZ *= horizontalDamping;
    }

    private void handleExhaustion(double diffX, double diffY, double diffZ)
    {
        float hungerIncrease = 0;
        boolean isRunning = this.isRunning();
        boolean isVerticalStill = Math.abs(diffY) < 0.007;
        boolean isStill = this.isStanding && isVerticalStill;

        if (!this.player.isRiding())
        {
            float horizontalMovement = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
            float movement = MathHelper.sqrt(horizontalMovement * horizontalMovement + diffY * diffY);

            int relevantMovementFactor = Math.round(movement * 100F);

            if (SmartMovingConfig.HUNGER.enable)
            {
                float hungerGainFactor = ConfigHelper.getFactor(true, this.player.onGround, this.isStanding, isStill, this.isSlow, isRunning, this.isFast, this.isClimbing, this.isClimbCrawling, this.isCeilingClimbing, this.isDipping, this.isSwimming, this.isDiving, this.isCrawling, this.isCrawlClimbing);
                hungerIncrease += SmartMovingConfig.HUNGER.perTickGainFactor + relevantMovementFactor * 0.0001F * hungerGainFactor;
            }

            float additionalExhaustion = 0F;
            if (this.isClimbing && !isStill && SmartMovingConfig.CLIMB.exhaustion)
            {
                float climbingExhaustion = SmartMovingConfig.EXHAUSTION.gainFactor;
                if (isVerticalStill)
                {
                    climbingExhaustion *= SmartMovingConfig.CLIMB.strafeExhaustionGain;
                }
                else
                {
                    if (!this.isStanding)
                    {
                        if (this.wantClimbUp)
                        {
                            climbingExhaustion *= SmartMovingConfig.CLIMB.strafeUpExhaustionGain;
                        }
                        else if (this.wantClimbDown)
                        {
                            climbingExhaustion *= SmartMovingConfig.CLIMB.strafeDownExhaustionGain;
                        }
                        else
                        {
                            climbingExhaustion *= 0F;
                        }
                    }
                    else
                    {
                        if (this.wantClimbUp)
                        {
                            climbingExhaustion *= SmartMovingConfig.CLIMB.upExhaustionGain;
                        }
                        else if (this.wantClimbDown)
                        {
                            climbingExhaustion *= SmartMovingConfig.CLIMB.downExhaustionGain;
                        }
                        else
                        {
                            climbingExhaustion *= 0F;
                        }
                    }
                }
                additionalExhaustion += climbingExhaustion;
            }

            if (this.isCeilingClimbing && !this.isStanding && SmartMovingConfig.CLIMB.ceilingExhaustion)
            {
                additionalExhaustion += SmartMovingConfig.EXHAUSTION.gainFactor * SmartMovingConfig.CLIMB.ceilingExhaustionGain;
            }

            if (this.isFast && SmartMovingConfig.GENERIC_SPRINTING.exhaustion)
            {
                if (additionalExhaustion == 0)
                {
                    additionalExhaustion = SmartMovingConfig.EXHAUSTION.gainFactor;
                }

                additionalExhaustion *= SmartMovingConfig.GENERIC_SPRINTING.exhaustionGainFactor;
            }

            if (this.isRunning() && SmartMovingConfig.STANDARD_SPRINTING.exhaustion)
            {
                if (additionalExhaustion == 0)
                {
                    additionalExhaustion = SmartMovingConfig.EXHAUSTION.gainFactor;
                }

                additionalExhaustion *= SmartMovingConfig.STANDARD_SPRINTING.exhaustionGainFactor;
            }

            if (this.foreignExhaustionFactor > 0)
            {
                additionalExhaustion += this.foreignExhaustionFactor * SmartMovingConfig.EXHAUSTION.gainFactor;

                if (this.foreignMaxExhaustionForAction == Float.MAX_VALUE)
                {
                    this.foreignMaxExhaustionForAction = ConfigHelper.getMaxExhaustion();
                }
                this.maxExhaustionForAction = Math.min(this.maxExhaustionForAction, this.foreignMaxExhaustionForAction);

                if (this.foreignMaxExhaustionToStartAction == Float.MAX_VALUE)
                {
                    this.foreignMaxExhaustionToStartAction = ConfigHelper.getMaxExhaustion();
                }
                this.maxExhaustionToStartAction = Math.min(this.maxExhaustionToStartAction, this.foreignMaxExhaustionToStartAction);
            }

            this.exhaustion += additionalExhaustion;
        }

        if (this.exhaustion > 0)
        {
            boolean exhaustionLossPossible = !ConfigHelper.isExhaustionLossHungerEnabled() || this.player.getFoodStats().getFoodLevel() > SmartMovingConfig.EXHAUSTION.foodMinimum;
            if (exhaustionLossPossible)
            {
                float exhaustionLossFactor = ConfigHelper.getFactor(false, this.player.onGround, this.isStanding, isStill, this.isSlow, isRunning, this.isFast, this.isClimbing, this.isClimbCrawling, this.isCeilingClimbing, this.isDipping, this.isSwimming, this.isDiving, this.isCrawling, this.isCrawlClimbing);
                this.exhaustion -= exhaustionLossFactor;
                if (ConfigHelper.isExhaustionLossHungerEnabled())
                {
                    hungerIncrease += SmartMovingConfig.EXHAUSTION.hungerFactor * exhaustionLossFactor;
                }
            }
        }

        if (this.exhaustion < 0)
        {
            this.exhaustion = 0;
        }

        if (this.exhaustion == 0)
        {
            this.maxExhaustionForAction = this.maxExhaustionToStartAction = Float.NaN;
        }

        if (this.maxExhaustionForAction == Float.MAX_VALUE)
        {
            this.maxExhaustionForAction = this.prevMaxExhaustionForAction;
        }

        if (this.maxExhaustionToStartAction == Float.MAX_VALUE)
        {
            this.maxExhaustionToStartAction = this.prevMaxExhaustionToStartAction;
        }

        this.foreignExhaustionFactor = 0;
        this.foreignMaxExhaustionForAction = Float.MAX_VALUE;
        this.foreignMaxExhaustionToStartAction = Float.MAX_VALUE;

        this.player.addExhaustion(hungerIncrease);
        MessageHandler.INSTANCE.sendToServer(new MessageHungerChangeServer(hungerIncrease));
    }

    private void landMotionPost(boolean wasShortInWater)
    {
        if (this.grabButton.isPressed)
        {
            this.fromSwimmingOrDiving(wasShortInWater);
        }

        if (this.heightOffset != 0 && this.playerBase.getSleepingField())
        {
            // from swimming/diving to sleeping
            this.resetInternalHeightOffset();
        }
    }

    private void fromSwimmingOrDiving(boolean wasShortInWater)
    {
        boolean isShortInWater = this.isSwimming || this.isDiving;
        if (wasShortInWater && !isShortInWater && !this.playerBase.getSleepingField())
        {
            // from diving in deep water to walking/sneaking/crawling
            this.setHeightOffset(-1F);

            double crawlStandUpBottom = this.getMaxPlayerSolidBetween(this.getBoundingBox().minY - 1D, this.getBoundingBox().minY, 0);
            double crawlStandUpLiquidCeiling = this.getMinPlayerLiquidBetween(this.getBoundingBox().maxY, this.getBoundingBox().maxY + 1.1D);
            double crawlStandUpCeiling = this.getMinPlayerSolidBetween(this.getBoundingBox().maxY, this.getBoundingBox().maxY + 1.1D, 0);

            this.resetHeightOffset();

            if (crawlStandUpCeiling - crawlStandUpBottom < this.player.height)
            {
                // from diving in deep water to crawling in small hole
                this.isCrawling = true;
                this.isDipping = false;
                this.setHeightOffset(-1F);
            }
            else if (crawlStandUpLiquidCeiling - crawlStandUpBottom < this.player.height)
            {
                // from diving in deep water to crawling below the water
                this.isCrawling = true;
                this.contextContinueCrawl = true;
                this.isDipping = false;
                this.setHeightOffset(-1F);
            }
            else if (crawlStandUpBottom > this.getBoundingBox().minY)
            {
                // from diving in deep water to walking/crawling
                if (this.isSlow && crawlStandUpBottom > this.getBoundingBox().minY + 0.5D)
                {
                    // from diving in deep water to crawling
                    this.isCrawling = true;
                    this.isDipping = false;
                    this.setHeightOffset(-1F);
                }
                this.move(0, (crawlStandUpBottom - this.getBoundingBox().minY), 0, true);
            }
        }
    }

    private static final ClimbGap out_handsClimbGap = new ClimbGap();
    private static final ClimbGap out_feetClimbGap = new ClimbGap();
    private static final HandsClimbing[] inout_handsClimbing = new HandsClimbing[1];
    private static final FeetClimbing[] inout_feetClimbing = new FeetClimbing[1];
    public boolean wantClimbUp;
    public boolean wantClimbDown;
    public boolean wantSprint;
    public boolean wantCrawlNotClimb;
    public boolean wantClimbCeiling;
    public boolean isStanding;
    public boolean wouldIsSneaking;
    public boolean isVineOnlyClimbing;
    public boolean isVineAnyClimbing;
    public boolean isClimbingStill;
    public boolean isClimbHolding;
    public boolean isNeighborClimbing;
    public boolean hasClimbGap;
    public boolean hasClimbCrawlGap;
    public boolean hasNeighborClimbGap;
    public boolean hasNeighborClimbCrawlGap;
    public float dippingDepth;
    public boolean isJumping;
    public boolean isJumpingOutOfWater;
    public boolean isShallowDiveOrSwim;
    public boolean isFakeShallowWaterSneaking;
    public boolean isStillSwimmingJump;
    public boolean isGroundSprinting;
    public boolean isSprintJump;
    public boolean isAerodynamic;
    public Block handsEdgeBlock;
    public int handsEdgeMeta;
    public Block feetEdgeBlock;
    public int feetEdgeMeta;
    public int waterMovementTicks;
    public float exhaustion;
    public float jumpCharge;
    public float headJumpCharge;
    public boolean blockJumpTillButtonRelease;
    public float maxExhaustionForAction;
    public float maxExhaustionToStartAction;
    public float prevMaxExhaustionForAction = Float.NaN;
    public float prevMaxExhaustionToStartAction = Float.NaN;
    public float foreignExhaustionFactor;
    public float foreignMaxExhaustionForAction = Float.MAX_VALUE;
    public float foreignMaxExhaustionToStartAction = Float.MAX_VALUE;
    public double lastHorizontalCollisionX;
    public double lastHorizontalCollisionZ;
    public float lastHungerIncrease;

    public boolean canTriggerWalking()
    {
        return !this.isClimbing && !this.isDiving;
    }

    private void resetClimbing()
    {
        this.isClimbing = false;
        this.isHandsVineClimbing = false;
        this.isFeetVineClimbing = false;
        this.isVineOnlyClimbing = false;
        this.isVineAnyClimbing = false;
        this.isClimbingStill = false;
        this.isNeighborClimbing = false;
        this.actualHandsClimbType = HandsClimbing.NO_GRAB;
        this.actualFeetClimbType = FeetClimbing.NO_STEP;
        this.isCeilingClimbing = false;
    }

    private void resetSwimming()
    {
        this.dippingDepth = -1;
        this.isDipping = false;
        this.isSwimming = false;
        this.isDiving = false;
        this.isLevitating = false;
        this.isShallowDiveOrSwim = false;
        this.isFakeShallowWaterSneaking = false;
        this.isJumpingOutOfWater = false;
    }

    private void setShouldClimbSpeed(double value)
    {
        this.setShouldClimbSpeed(value, HandsClimbing.UP_GRAB, FeetClimbing.DOWN_STEP);
    }

    private void setShouldClimbSpeed(double value, int handsClimbType, int feetClimbType)
    {
        this.setOnlyShouldClimbSpeed(value);
        this.actualHandsClimbType = handsClimbType;
        this.actualFeetClimbType = feetClimbType;
    }

    private void setOnlyShouldClimbSpeed(double value)
    {
        this.isClimbing = true;

        if (this.climbIntoCount > 0)
        {
            value = HOLD_MOTION;
        }

        if (value != HOLD_MOTION)
        {
            float factor = this.getSpeedFactor();
            if (this.isFast)
            {
                factor *= SmartMovingConfig.GENERIC_SPRINTING.factor;
            }
            if (ConfigHelper.isFreeBaseClimb() && value == MEDIUM_UP_MOTION)
            {
                switch (this.getOnLadder(Integer.MAX_VALUE, false, this.isClimbCrawling))
                {
                    case 1:
                        factor *= SmartMovingConfig.CLIMB.freeLadderOneUpSpeedFactor;
                        break;
                    case 2:
                        factor *= SmartMovingConfig.CLIMB.freeLadderTwoUpSpeedFactor;
                        break;
                }
            }

            if (value > HOLD_MOTION)
            {
                value = ((value - HOLD_MOTION) * SmartMovingConfig.CLIMB.freeUpSpeedFactor * factor + HOLD_MOTION);
            }
            else
            {
                value = HOLD_MOTION - (HOLD_MOTION - value) * SmartMovingConfig.CLIMB.freeDownSpeedFactor * factor;
            }

            if (this.hasClimbCrawlGap && this.isClimbCrawling && value > HOLD_MOTION)
            {
                value = Math.min(CATCH_CRAWL_GAP_MOTION, value); // to avoid climbing over really small gaps (RedPowerWire Cover Top / RedPowerWire Cover Bottom)
            }
        }
        else
        {
            this.isClimbingStill = true;
        }

        boolean relevant = value < 0 || value > this.player.motionY;
        if (relevant)
        {
            this.player.motionY = value;
        }
        this.isClimbJumping = !relevant && !this.isClimbHolding;
    }

    public boolean isOnLadderOrVine()
    {
        return this.isOnLadderOrVine(this.isClimbCrawling);
    }

    private double beforeMoveEntityPosX;
    private double beforeMoveEntityPosY;
    private double beforeMoveEntityPosZ;
    private float beforeDistanceWalkedModified;
    private float horizontalCollisionAngle;

    public void beforeMoveEntity(double d, double d1, double d2)
    {
        this.beforeMoveEntityPosX = this.player.posX;
        this.beforeMoveEntityPosY = this.player.posZ;
        this.beforeMoveEntityPosZ = this.player.posY;

        if (this.isSliding || this.isCrawling)
        {
            this.beforeDistanceWalkedModified = this.player.distanceWalkedModified;
            this.player.distanceWalkedModified = Float.MIN_VALUE;
        }

        if (this.wantWallJumping)
        {
            int collisions = this.calculateSeparateCollisions(d, d1, d2);
            this.horizontalCollisionAngle = getHorizontalCollisionangle((collisions & CollidedPositiveZ) != 0, (collisions & CollidedNegativeZ) != 0, (collisions & CollidedPositiveX) != 0, (collisions & CollidedNegativeX) != 0);
        }
    }

    public void afterMoveEntity()
    {
        if (this.isSliding || this.isCrawling)
        {
            this.player.distanceWalkedModified = this.beforeDistanceWalkedModified;
        }

        if (this.heightOffset != 0F)
        {
            this.player.posY = this.player.posY + this.heightOffset;
        }

        this.wasOnGround = this.player.onGround;

        double d10 = this.player.posX - this.beforeMoveEntityPosX;
        double d12 = this.player.posZ - this.beforeMoveEntityPosY;
        double d13 = this.player.posY - this.beforeMoveEntityPosZ;

        double distance = MathHelper.sqrt(d10 * d10 + d12 * d12 + d13 * d13);

        if (this.isClimbing || this.isCeilingClimbing)
        {
            this.distanceClimbedModified += distance * (this.isClimbing ? 1.2 : 0.9);
            if (this.distanceClimbedModified > this.nextClimbDistance)
            {
                Block stepBlock;
                if (this.isClimbing)
                {
                    if (this.handsEdgeBlock == null)
                    {
                        if (this.feetEdgeBlock == null)
                        {
                            stepBlock = Block.getBlockFromName("cobblestone");
                        }
                        else
                        {
                            stepBlock = this.feetEdgeBlock;
                        }
                    }
                    else if (this.feetEdgeBlock == null)
                    {
                        stepBlock = this.handsEdgeBlock;
                    }
                    else
                    {
                        stepBlock = this.nextClimbDistance % 2 != 0 ? this.feetEdgeBlock : this.handsEdgeBlock;
                    }
                }
                else
                {
                    stepBlock = this.handsEdgeBlock;
                }

                this.nextClimbDistance++;
                if (stepBlock != null)
                {
                    SoundType stepsound = stepBlock.getSoundType(stepBlock.getDefaultState(), this.player.world, this.player.getPosition(), this.player);
                    this.playSound(stepsound.getStepSound(), stepsound.getVolume() * 0.15F, stepsound.getPitch());
                }
            }
        }

        if (this.isSwimming)
        {
            this.swimmingDistance += distance;
            if (this.swimmingDistance > SWIM_SOUND_DISTANCE)
            {
                Random rand = this.player.getRNG();
                this.playSound("random.splash", 0.05F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);

                this.swimmingDistance -= SWIM_SOUND_DISTANCE;
            }
        }
    }

    private void playSound(SoundEvent soundEvent, float volume, float pitch)
    {
        this.player.world.playSound(this.player, new BlockPos(this.player.posX, this.player.posY, this.player.posZ), soundEvent, this.player.getSoundCategory(), volume, pitch);
        MessageHandler.INSTANCE.sendToServer(new MessageSoundServer(soundEvent.getSoundName().toString(), volume, pitch));
    }

    private void playSound(String id, float volume, float pitch)
    {
        SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(id));
        if (soundEvent != null)
        {
            this.playSound(soundEvent, volume, pitch);
        }
    }

    public void beforeSleepInBedAt()
    {
        if (!this.playerBase.getSleepingField())
        {
            this.updateEntityActionState(true);
        }
    }

    private void resetHeightOffset()
    {
        AxisAlignedBB bb = this.getBoundingBox();
        bb = new AxisAlignedBB(bb.minX, bb.minY + this.heightOffset, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        this.setBoundingBox(bb);
        this.player.height -= this.heightOffset;
        this.heightOffset = 0F;
    }

    private void resetInternalHeightOffset()
    {
        this.player.height -= this.heightOffset;
        this.heightOffset = 0F;
    }

    private void setHeightOffset(float offset)
    {
        this.resetHeightOffset();
        if (offset == 0F)
        {
            return;
        }

        this.heightOffset = offset;

        AxisAlignedBB bb = this.getBoundingBox();
        bb = new AxisAlignedBB(bb.minX, bb.minY - this.heightOffset, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        this.setBoundingBox(bb);
        this.player.height += this.heightOffset;
    }

    boolean wasOnGround;

    public float getBrightness()
    {
        this.player.posY -= this.heightOffset;
        float result = this.playerBase.localGetBrightness();
        this.player.posY += this.heightOffset;
        return result;
    }

    public int getBrightnessForRender()
    {
        this.player.posY -= this.heightOffset;
        int result = this.playerBase.localGetBrightnessForRender();
        this.player.posY += this.heightOffset;
        return result;
    }

    public boolean pushOutOfBlocks(double d, double d1, double d2)
    {
        if (this.multiPlayerInitialized > 0)
        {
            return false;
        }

        boolean top = false;
        if (this.heightOffset != 0F)
        {
            top = this.player.height > 1F;
        }

        return this.pushOutOfBlocks(d, d1, d2, top);
    }

    public void beforeOnUpdate()
    {
        this.prevMotionX = this.player.motionX;
        this.prevMotionY = this.player.motionY;
        this.prevMotionZ = this.player.motionZ;

        this.wasCollidedHorizontally = this.player.collidedHorizontally;

        this.isJumping = false;
    }

    public void afterOnUpdate()
    {
        this.correctOnUpdate(this.isSwimming || this.isDiving || this.isDipping || this.isCrawling, this.isSwimming);

        this.spawnParticles(this.playerBase.getMcField(), this.player.motionX, this.player.motionZ);

        float landMovementFactor = this.getLandMovementFactor();

        float perspectiveFactor = landMovementFactor;
        if (this.isFast || this.isSprintJump || this.isRunning())
        {
            if (this.player.isSprinting())
            {
                perspectiveFactor /= 1.3F;
            }

            if (this.isFast || this.isSprintJump)
            {
                perspectiveFactor *= SmartMovingConfig.VIEWPOINT_PERSPECTIVE.sprintFactor;
            }
            else if (this.isRunning())
            {
                perspectiveFactor *= 1.3F * SmartMovingConfig.VIEWPOINT_PERSPECTIVE.runFactor;
            }
        }

        if (this.fadingPerspectiveFactor != -1)
        {
            this.fadingPerspectiveFactor += (perspectiveFactor - this.fadingPerspectiveFactor) * SmartMovingConfig.VIEWPOINT_PERSPECTIVE.fadeFactor;
        }
        else
        {
            this.fadingPerspectiveFactor = landMovementFactor;
        }

        if (this.player.capabilities.disableDamage)
        {
            this.exhaustion = 0;
        }

        if (this.player.capabilities.isFlying)
        {
            this.player.fallDistance = 0F;
        }

        if (this.player.collidedHorizontally)
        {
            this.collidedHorizontallyTickCount++;
        }
        else
        {
            this.collidedHorizontallyTickCount = 0;
        }

        this.addToSendQueue();

        if (this.wasInventory)
        {
            this.player.prevRotationYawHead = this.player.rotationYawHead;
        }
        this.wasInventory = this.playerBase.getMcField().currentScreen instanceof GuiInventory;
    }

    boolean wasCapabilitiesIsFlying;

    public void beforeOnLivingUpdate()
    {
        this.wasCapabilitiesIsFlying = this.player.capabilities.isFlying;
    }

    public void afterOnLivingUpdate()
    {
        if (SmartMovingConfig.USER_INTERFACE.flyGroundCollide && !(this.sneakButton.isPressed && this.grabButton.isPressed) && this.wasCapabilitiesIsFlying && !this.player.capabilities.isFlying && this.player.onGround)
        {
            this.player.cameraYaw = 0;
            this.player.prevCameraYaw = 0;
            this.player.capabilities.isFlying = true;
            this.player.sendPlayerAbilities();
        }
    }

    private float fadingPerspectiveFactor = -1;
    private boolean wasInventory;
    private double jumpMotionX;
    private double jumpMotionZ;

    public void handleJumping()
    {
        if (this.blockJumpTillButtonRelease && !((EntityPlayerSP) this.entityPlayer).movementInput.jump)
        {
            this.blockJumpTillButtonRelease = false;
        }

        if (this.isSwimming || this.isDiving)
        {
            return;
        }

        boolean jump = this.jumpAvoided && this.player.onGround && this.playerBase.getIsJumpingField() && !this.player.isInWater() && !this.player.isInLava();
        if (jump)
        {
            if (this.getBoundingBox().minY - this.getMaxPlayerSolidBetween(this.getBoundingBox().minY - 0.2D, this.getBoundingBox().minY, 0) >= 0.01D)
            {
                return; // Maybe SPC flying?
            }
        }

        this.jumpMotionX = this.player.motionX;
        this.jumpMotionZ = this.player.motionZ;

        boolean isJumpCharging = false;
        if (SmartMovingConfig.CHARGED_JUMPING.enable)
        {
            boolean isJumpChargingPossible = this.player.onGround && this.isStanding;
            isJumpCharging = isJumpChargingPossible && this.wouldIsSneaking;

            boolean actualJumpCharging = isJumpChargingPossible && (!SmartMovingConfig.CHARGED_JUMPING.sneakReleaseCancel || this.wouldIsSneaking);
            if (actualJumpCharging)
            {
                if (((EntityPlayerSP) this.entityPlayer).movementInput.jump && (SmartMovingConfig.CHARGED_JUMPING.sneakReleaseCancel || this.wouldIsSneaking))
                {
                    this.jumpCharge++;
                }
                else
                {
                    if (this.jumpCharge > 0)
                    {
                        this.tryJump(ConfigHelper.ChargeUp, null, null, null);
                    }
                    this.jumpCharge = 0;
                }
            }
            else
            {
                if (this.jumpCharge > 0)
                {
                    this.blockJumpTillButtonRelease = true;
                }
                this.jumpCharge = 0;
            }
        }

        boolean isHeadJumpCharging = false;
        if (SmartMovingConfig.HEAD_JUMPING.enable)
        {
            isHeadJumpCharging = this.grabButton.isPressed && (this.isGroundSprinting || this.isSprintJump || (this.isRunning() && this.player.onGround)) && !this.isCrawling;
            if (isHeadJumpCharging)
            {
                if (((EntityPlayerSP) this.entityPlayer).movementInput.jump)
                {
                    this.headJumpCharge++;
                }
                else
                {
                    if (this.headJumpCharge > 0 && this.player.onGround)
                    {
                        this.tryJump(ConfigHelper.HeadUp, null, null, null);
                    }
                    this.headJumpCharge = 0;
                }
            }
            else
            {
                if (this.headJumpCharge > 0)
                {
                    this.blockJumpTillButtonRelease = true;
                }
                this.headJumpCharge = 0;
            }
        }

        if (((EntityPlayerSP) this.entityPlayer).movementInput.jump && this.player.isInWater() && this.isDipping)
        {
            if (this.player.posY - MathHelper.floor(this.player.posY) > (this.isSlow ? 0.37 : 0.6))
            {
                this.player.motionY -= 0.04D;
                if (!this.isStillSwimmingJump && this.player.onGround && this.jumpCharge == 0)
                {
                    if (this.tryJump(ConfigHelper.Up, true, null, null))
                    {
                        Random rand = this.player.getRNG();
                        this.playSound("random.splash", 0.05F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
                    }
                }
            }
        }

        if (jump && !this.blockJumpTillButtonRelease && !isJumpCharging && !isHeadJumpCharging && !this.isVineAnyClimbing)
        {
            this.tryJump(ConfigHelper.Up, false, null, null);
        }

        int left = 0;
        int back = 0;
        if (this.leftJumpCount == -1)
        {
            left++;
        }
        if (this.rightJumpCount == -1)
        {
            left--;
        }
        if (this.backJumpCount == -1)
        {
            back++;
        }

        if (left != 0 || back != 0)
        {
            int angle;
            if (left > 0)
            {
                angle = back == 0 ? 270 : 225;
            }
            else if (left < 0)
            {
                angle = back == 0 ? 90 : 135;
            }
            else
            {
                angle = 180;
            }

            if (this.tryJump(ConfigHelper.Angle, null, null, this.player.rotationYaw + angle))
            {
                this.angleJumpType = ((360 - angle) / 45) % 8;
            }

            this.leftJumpCount = 0;
            this.rightJumpCount = 0;
            this.backJumpCount = 0;
        }
    }

    public void handleWallJumping()
    {
        if (!this.wantWallJumping || Double.isNaN(this.horizontalCollisionAngle))
        {
            return;
        }

        int jumpType;
        if (this.grabButton.isPressed)
        {
            if (this.player.fallDistance > SmartMovingConfig.WALL_HEAD_JUMPING.fallMaximumDistance)
            {
                return;
            }
            jumpType = this.wasCollidedHorizontally ? ConfigHelper.WallHeadSlide : ConfigHelper.WallHead;
        }
        else
        {
            if (this.player.fallDistance > SmartMovingConfig.WALL_JUMPING.fallMaximumDistance)
            {
                return;
            }
            jumpType = this.wasCollidedHorizontally ? ConfigHelper.WallUpSlide : ConfigHelper.WallUp;
        }

        float jumpAngle;
        if (!this.wasCollidedHorizontally)
        {
            float movementAngle = getAngle(this.jumpMotionZ, -this.jumpMotionX);
            if (Double.isNaN(movementAngle))
            {
                return;
            }

            jumpAngle = this.horizontalCollisionAngle * 2 - movementAngle + 180F;
        }
        else
        {
            jumpAngle = this.horizontalCollisionAngle;
        }

        while (jumpAngle > 360F)
        {
            jumpAngle -= 360F;
        }

        if (SmartMovingConfig.WALL_JUMPING.orthogonalTolerance != 0.0F)
        {
            float aligned = jumpAngle;
            while (aligned > 45F)
            {
                aligned -= 90F;
            }

            if (Math.abs(aligned) < SmartMovingConfig.WALL_JUMPING.orthogonalTolerance)
            {
                jumpAngle = Math.round(jumpAngle / 90F) * 90F;
            }
        }

        if (this.tryJump(jumpType, null, null, jumpAngle))
        {
            this.continueWallJumping = !this.isHeadJumping;
            this.player.collidedHorizontally = false;
            this.player.rotationYaw = jumpAngle;
            this.onStartWallJump(jumpAngle);
        }
    }

    public boolean tryJump(int type, Boolean inWaterOrNull, Boolean isRunningOrNull, Float angle)
    {
        boolean noVertical = false;
        if (type == ConfigHelper.WallUpSlide || type == ConfigHelper.WallHeadSlide)
        {
            type = type == ConfigHelper.WallUpSlide ? ConfigHelper.WallUp : ConfigHelper.WallHead;
            noVertical = true;
        }

        boolean inWater = inWaterOrNull != null ? inWaterOrNull : this.isDipping;
        boolean isRunning = isRunningOrNull != null ? isRunningOrNull : this.isRunning();
        boolean charged = type == ConfigHelper.ChargeUp;
        boolean up = type == ConfigHelper.Up || type == ConfigHelper.ChargeUp || type == ConfigHelper.HeadUp || type == ConfigHelper.ClimbUp || type == ConfigHelper.ClimbUpHandsOnly || type == ConfigHelper.ClimbBackUp || type == ConfigHelper.ClimbBackUpHandsOnly || type == ConfigHelper.ClimbBackHead || type == ConfigHelper.ClimbBackHeadHandsOnly || type == ConfigHelper.Angle || type == ConfigHelper.WallUp || type == ConfigHelper.WallHead;
        boolean head = type == ConfigHelper.HeadUp || type == ConfigHelper.ClimbBackHead || type == ConfigHelper.ClimbBackHeadHandsOnly || type == ConfigHelper.WallHead;

        int speed = getJumpSpeed(this.isStanding, this.isSlow, isRunning, this.isFast, angle);
        boolean enabled = ConfigHelper.isJumpingEnabled(speed, type);
        if (enabled)
        {
            boolean exhaustionEnabled = ConfigHelper.isJumpExhaustionEnabled(speed, type);
            if (exhaustionEnabled)
            {
                float maxExhaustionForJump = ConfigHelper.getJumpExhaustionStop(speed, type, this.jumpCharge);
                if (this.exhaustion > maxExhaustionForJump)
                {
                    return false;
                }
                this.maxExhaustionToStartAction = Math.min(this.maxExhaustionToStartAction, maxExhaustionForJump);
                this.maxExhaustionForAction = Math.min(this.maxExhaustionForAction, maxExhaustionForJump + ConfigHelper.getJumpExhaustionGain(speed, type, this.jumpCharge));
            }

            float jumpFactor = 1;
            if (this.player.isPotionActive(this.jumpBoost))
            {
                PotionEffect effect = this.player.getActivePotionEffect(this.jumpBoost);
                if (effect != null)
                {
                    jumpFactor = 1 + (effect.getAmplifier() + 1) * 0.2F;
                }
            }

            float horizontalJumpFactor = ConfigHelper.getJumpHorizontalFactor(speed, type) * jumpFactor;
            float verticalJumpFactor = ConfigHelper.getJumpVerticalFactor(speed, type) * jumpFactor;
            float jumpChargeFactor = charged ? ConfigHelper.getJumpChargeFactor(this.jumpCharge) : 1F;

            if (!up)
            {
                horizontalJumpFactor = MathHelper.sqrt(horizontalJumpFactor * horizontalJumpFactor + verticalJumpFactor * verticalJumpFactor);
                verticalJumpFactor = 0;
            }

            Double maxHorizontalMotion = null;
            double horizontalMotion = MathHelper.sqrt(this.jumpMotionX * this.jumpMotionX + this.jumpMotionZ * this.jumpMotionZ);
            double verticalMotion = -0.078 + 0.498 * verticalJumpFactor * jumpChargeFactor;

            if (horizontalJumpFactor > 1F && !this.player.collidedHorizontally)
            {
                maxHorizontalMotion = (double) ConfigHelper.getMaxHorizontalMotion(speed, inWater) * this.getSpeedFactor();
            }

            if (head)
            {
                double normalAngle = Math.atan(verticalMotion / horizontalMotion);
                double totalMotion = Math.sqrt(verticalMotion * verticalMotion + horizontalMotion * horizontalMotion);

                double newAngle = ConfigHelper.getHeadJumpFactor(this.headJumpCharge) * normalAngle;
                double newVerticalMotion = totalMotion * Math.sin(newAngle);
                double newHorizontalMotion = totalMotion * Math.cos(newAngle);

                if (maxHorizontalMotion != null)
                {
                    maxHorizontalMotion = maxHorizontalMotion * (newHorizontalMotion / horizontalMotion);
                }

                verticalMotion = newVerticalMotion;
                horizontalMotion = newHorizontalMotion;
            }

            if (angle != null)
            {
                float jumpAngle = angle / RadiantToAngle;
                boolean reset = type == ConfigHelper.WallUp || type == ConfigHelper.WallHead;

                double horizontal = Math.max(horizontalMotion, horizontalJumpFactor);
                double moveX = -Math.sin(jumpAngle);
                double moveZ = Math.cos(jumpAngle);

                this.player.motionX = getJumpMoving(this.jumpMotionX, moveX, reset, horizontal, horizontalJumpFactor);
                this.player.motionZ = getJumpMoving(this.jumpMotionZ, moveZ, reset, horizontal, horizontalJumpFactor);

                horizontalMotion = 0;
                verticalMotion = verticalJumpFactor;
            }

            if (horizontalMotion > 0)
            {
                double absoluteMotionX = Math.abs(this.player.motionX) * horizontalJumpFactor;
                double absoluteMotionZ = Math.abs(this.player.motionZ) * horizontalJumpFactor;

                if (maxHorizontalMotion != null)
                {
                    absoluteMotionX = Math.min(absoluteMotionX, maxHorizontalMotion * (horizontalJumpFactor * (Math.abs(this.player.motionX) / horizontalMotion)));
                    absoluteMotionZ = Math.min(absoluteMotionZ, maxHorizontalMotion * (horizontalJumpFactor * (Math.abs(this.player.motionZ) / horizontalMotion)));
                }

                this.player.motionX = Math.signum(this.player.motionX) * absoluteMotionX;
                this.player.motionZ = Math.signum(this.player.motionZ) * absoluteMotionZ;
            }

            if (up && !noVertical)
            {
                this.player.motionY = verticalMotion;
                this.player.addStat(StatList.JUMP, 1);
                this.isSprintJump = this.isFast;
            }

            if (exhaustionEnabled)
            {
                float exhaustionFromJump = ConfigHelper.getJumpExhaustionGain(speed, type, this.jumpCharge);
                this.exhaustion += exhaustionFromJump;
            }

            if (head)
            {
                this.isHeadJumping = true;
                this.setHeightOffset(-1);
            }
            this.player.isAirBorne = true;
            this.isJumping = true;
            this.onLivingJump();
        }
        return enabled;
    }

    private static double getJumpMoving(double actual, double move, boolean reset, double horizontal, float horizontalJumpFactor)
    {
        if (!reset)
        {
            return actual + move * horizontal;
        }
        else if (Math.signum(actual) != Math.signum(move))
        {
            return move * horizontalJumpFactor;
        }
        else
        {
            return Math.max(Math.abs(actual), Math.abs(move) * horizontal) * Math.signum(move);
        }
    }

    private static int getJumpSpeed(boolean isStanding, boolean isSlow, boolean isRunning, boolean isSprinting, Float angle)
    {
        isSprinting &= angle == null;
        isRunning &= angle == null;

        if (isSprinting)
        {
            return ConfigHelper.Sprinting;
        }
        else if (isRunning)
        {
            return ConfigHelper.Running;
        }
        else if (isSlow)
        {
            return ConfigHelper.Sneaking;
        }
        else if (isStanding)
        {
            return ConfigHelper.Standing;
        }
        else
        {
            return ConfigHelper.Walking;
        }
    }

    private void standupIfPossible()
    {
        if (this.heightOffset >= 0)
        {
            return;
        }

        double gapUnderneight = this.getGapUnderneight();
        boolean groundClose = gapUnderneight < 1D;
        if (!groundClose)
        {
            this.resetHeightOffset();
        }
        else
        {
            boolean standUpPossible = gapUnderneight + this.getGapOverneight() >= 1D;
            if (standUpPossible)
            {
                this.standUp(gapUnderneight);
            }
            else
            {
                this.toSlidingOrCrawling(gapUnderneight);
            }
        }
    }

    private void standupIfPossible(boolean tryLanding, boolean restoreFromFlying)
    {
        if (this.heightOffset >= 0)
        {
            return;
        }

        double gapUnderneight = this.getGapUnderneight();
        boolean groundClose = gapUnderneight < 1D;
        double gapOverneight = groundClose ? this.getGapOverneight() : -1D;
        boolean standUpPossible = gapUnderneight + gapOverneight >= 1D;

        if (tryLanding && groundClose && standUpPossible)
        {
            this.isFlying = false;
            this.player.capabilities.isFlying = false;
            restoreFromFlying = true;
        }

        if (!restoreFromFlying)
        {
            return;
        }

        if (!groundClose && !this.sneakButton.isPressed)
        {
            this.resetHeightOffset();
        }
        else if (standUpPossible && !(this.sneakButton.isPressed && this.grabButton.isPressed))
        {
            this.standUp(gapUnderneight);
        }
        else
        {
            this.toSlidingOrCrawling(gapUnderneight);
        }
    }

    private void standUp(double gapUnderneight)
    {
        this.move(0, (1D - gapUnderneight), 0, true);
        this.isCrawling = false;
        this.isHeadJumping = false;
        this.resetHeightOffset();
    }

    private void toSlidingOrCrawling(double gapUnderneight)
    {
        this.move(0, (-gapUnderneight), 0, true);

        if (SmartMovingConfig.SLIDING.enable && (this.grabButton.isPressed || this.wasHeadJumping))
        {
            this.isSliding = true;
        }
        else
        {
            this.wasCrawling = this.toCrawling();
        }
    }

    private void handleCrash(float fallDamageStartDistance, float fallDamageFactor)
    {
        if (this.player.fallDistance >= 2.0F)
        {
            this.player.addStat(StatList.FALL_ONE_CM, (int) Math.round(this.player.fallDistance * 100D));
        }

        if (this.player.fallDistance >= fallDamageStartDistance)
        {
            this.player.attackEntityFrom(DamageSource.FALL, (int) Math.ceil((this.player.fallDistance - fallDamageStartDistance) * fallDamageFactor));
            this.distanceClimbedModified = this.nextClimbDistance; // to force step sound
        }
        this.player.fallDistance = 0F;
    }

    public void beforeSetPositionAndRotation()
    {
        if (this.player.world.isRemote)
        {
            this.initialized = false;
            this.multiPlayerInitialized = 5;
        }
    }

    public void updateEntityActionState(boolean startSleeping)
    {
        this.jumpAvoided = false;

        this.prevMaxExhaustionForAction = this.maxExhaustionForAction;
        this.prevMaxExhaustionToStartAction = this.maxExhaustionToStartAction;

        this.maxExhaustionForAction = Float.MAX_VALUE;
        this.maxExhaustionToStartAction = Float.MAX_VALUE;

        boolean isLevitating = this.player.capabilities.isFlying && !this.isFlying;
        boolean isRunning = this.isRunning();

        boolean initializeCrawling = false;
        if (!this.initialized && !(this.player.world.isRemote && this.multiPlayerInitialized != 0) && !this.player.isRiding())
        {
            if (this.getMaxPlayerSolidBetween(this.getBoundingBox().minY, this.getBoundingBox().maxY, 0) > this.getBoundingBox().minY)
            {
                initializeCrawling = true;
                this.toCrawling();
            }

            this.initialized = true;
        }

        if (this.multiPlayerInitialized > 0)
        {
            this.multiPlayerInitialized--;
        }

        if (!((EntityPlayerSP) this.entityPlayer).movementInput.jump)
        {
            this.isStillSwimmingJump = false;
        }

        if (!startSleeping)
        {
            this.playerBase.localUpdateEntityActionState();
            this.playerBase.setMoveStrafingField(Math.signum(((EntityPlayerSP) this.entityPlayer).movementInput.moveStrafe));
            this.playerBase.setMoveForwardField(Math.signum(((EntityPlayerSP) this.entityPlayer).movementInput.moveForward));
            this.playerBase.setIsJumpingField(((EntityPlayerSP) this.entityPlayer).movementInput.jump && !this.isCrawling && !this.isSliding && !(SmartMovingConfig.HEAD_JUMPING.enable && this.grabButton.isPressed && this.player.isSprinting()) && !(SmartMovingConfig.CHARGED_JUMPING.enable && this.wouldIsSneaking && this.player.onGround && this.isStanding) && !this.blockJumpTillButtonRelease);
        }

        boolean isSleeping = this.playerBase.getSleepingField();
        boolean disabled = this.player.isRiding() || isSleeping || startSleeping;

        Minecraft minecraft = this.playerBase.getMcField();
        GameSettings gameSettings = minecraft.gameSettings;

        this.forwardButton.update(gameSettings.keyBindForward);
        this.leftButton.update(gameSettings.keyBindLeft);
        this.rightButton.update(gameSettings.keyBindRight);
        this.backButton.update(gameSettings.keyBindBack);
        this.jumpButton.update(gameSettings.keyBindJump);
        this.sprintButton.update(gameSettings.keyBindSprint);
        this.sneakButton.update(gameSettings.keyBindSneak);
        this.grabButton.update(Keybinds.GRAB);

        double horizontalSpeedSquare = this.player.motionX * this.player.motionX + this.player.motionZ * this.player.motionZ;
        // double verticalSpeedSquare = (sp.motionY + HoldMotion) * (sp.motionY + HoldMotion);
        // double speedSquare = horizontalSpeedSquare + verticalSpeedSquare;

        boolean blocked = minecraft.currentScreen != null && !minecraft.currentScreen.allowUserInput;

        boolean mustCrawl = false;
        double crawlStandUpBottom = -1;
        if (this.isCrawling || this.isClimbCrawling)
        {
            crawlStandUpBottom = this.getMaxPlayerSolidBetween(this.getBoundingBox().minY - (initializeCrawling ? 0D : 1D), this.getBoundingBox().minY, SmartMovingConfig.CRAWLING.edge ? 0 : -0.05);
            double crawlStandUpCeiling = this.getMinPlayerSolidBetween(this.getBoundingBox().maxY, this.getBoundingBox().maxY + 1.1D, 0);
            mustCrawl = crawlStandUpCeiling - crawlStandUpBottom < this.player.height - this.heightOffset;
        }

        if (this.entityPlayer.capabilities.isFlying && (SmartMovingConfig.SMART_FLYING.enable || SmartMovingConfig.STANDARD_FLYING.small))
        {
            mustCrawl = false;
        }

        boolean inputContinueCrawl = SmartMovingConfig.USER_INTERFACE.crawlToggle ? this.crawlToggled : this.sneakButton.isPressed || !SmartMovingConfig.CLIMB.enable && this.grabButton.isPressed;
        if (this.contextContinueCrawl)
        {
            if (inputContinueCrawl || this.player.isInWater() || mustCrawl)
            {
                this.contextContinueCrawl = false;
            }
            else if (this.isCrawling)
            {
                double crawlStandUpLiquidCeiling = this.getMinPlayerLiquidBetween(this.getBoundingBox().maxY, this.getBoundingBox().maxY + 1.1D);
                if (crawlStandUpLiquidCeiling - crawlStandUpBottom >= this.player.height + 1F)
                {
                    this.contextContinueCrawl = false;
                }
            }
        }
        boolean wouldWantCrawl = !this.entityPlayer.capabilities.isFlying && ((this.isCrawling && (inputContinueCrawl || this.contextContinueCrawl)) || (this.grabButton.startPressed && (this.sneakToggled || this.sneakButton.isPressed) && this.player.onGround));

        boolean wantCrawl = SmartMovingConfig.CRAWLING.enable && wouldWantCrawl;

        boolean canCrawl = !this.isSwimming && !this.isDiving && (!this.isDipping || (this.dippingDepth + this.heightOffset) < SWIM_CRAWL_WATER_TOP_BORDER) && !this.isClimbing && this.player.fallDistance < SmartMovingConfig.FALLING.distanceMinimum;

        this.wasCrawling = this.isCrawling;
        this.isCrawling = canCrawl && (wantCrawl || mustCrawl);

        if (!this.isCrawling)
        {
            this.contextContinueCrawl = false;
        }

        if (this.wasCrawling && !this.isCrawling && this.entityPlayer.capabilities.isFlying)
        {
            this.tryJump(ConfigHelper.Up, null, null, null);
        }

        this.wantCrawlNotClimb = (this.wantCrawlNotClimb || (this.grabButton.startPressed && !this.wasCrawling)) && this.grabButton.isPressed && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F && this.isCrawling && this.player.collidedHorizontally;

        boolean isFacedToSolidVine = this.isFacedToSolidVine(this.isClimbCrawling);

        boolean wouldWantClimb = (this.grabButton.isPressed || (this.isClimbHolding && this.sneakButton.isPressed) || (SmartMovingConfig.CLIMB.freeLadderAuto && this.isFacedToLadder(this.isClimbCrawling)) || (SmartMovingConfig.CLIMB.freeVineAuto && isFacedToSolidVine)) && (!this.isSliding || this.grabButton.isPressed && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F) && !this.isHeadJumping && !this.wantCrawlNotClimb && !disabled;

        boolean wantClimb = SmartMovingConfig.CLIMB.enable && wouldWantClimb;

        if (!wantClimb || this.player.collidedHorizontally)
        {
            this.isClimbJumping = false;
        }

        if (this.player.collided)
        {
            this.isClimbBackJumping = false;
        }

        this.wantClimbUp = wantClimb && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F || (this.isVineAnyClimbing && this.jumpButton.isPressed && !(this.sneakButton.isPressed && isFacedToSolidVine)) && (!this.isCrawling || this.player.collidedHorizontally) && (!this.isSliding || this.player.collidedHorizontally);

        this.wantClimbDown = wantClimb && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward <= 0F && !wantCrawl;

        this.wantClimbCeiling = SmartMovingConfig.CLIMB.ceiling && this.grabButton.isPressed && !this.wantCrawlNotClimb && !this.isSneaking() && this.player.getItemInUseCount() < 1 && !disabled;

        boolean restoreFromFlying = false;

        boolean wasFlying = this.isFlying;
        this.isFlying = SmartMovingConfig.SMART_FLYING.enable && this.player.capabilities.isFlying && !this.isSwimming && !this.isDiving;
        if (this.isFlying && !wasFlying)
        {
            this.setHeightOffset(-1);
        }
        else if (!this.isFlying && wasFlying)
        {
            restoreFromFlying = true;
        }

        if (!SmartMovingConfig.SMART_FLYING.enable && SmartMovingConfig.STANDARD_FLYING.small)
        {
            if (isLevitating && !this.wasLevitating)
            {
                this.setHeightOffset(-1);
            }
            else if (!isLevitating && this.wasLevitating)
            {
                restoreFromFlying = true;
            }
        }

        this.wasHeadJumping = this.isHeadJumping;
        this.isHeadJumping = this.isHeadJumping && !this.player.onGround && !(this.isSwimming || this.isDiving) && !(this.isFlying || this.player.capabilities.isFlying) && !(this.player.handleWaterMovement() && this.player.motionY < 0) && !this.player.isInLava();

        if (!this.isHeadJumping)
        {
            this.isAerodynamic = false;
        }

        if (this.wasHeadJumping && !this.isHeadJumping)
        {
            if (this.player.onGround)
            {
                this.handleCrash(SmartMovingConfig.HEAD_JUMPING.damageStartDistance, SmartMovingConfig.HEAD_JUMPING.damageFactor);
                restoreFromFlying = true;
            }
        }

        boolean tryLanding = this.isFlying && !SmartMovingConfig.USER_INTERFACE.flyGroundClose && horizontalSpeedSquare < 0.003D && this.player.motionY > -0.03D;
        if (restoreFromFlying || tryLanding)
        {
            this.standupIfPossible(tryLanding, restoreFromFlying);
        }

        if (this.isSliding && this.player.fallDistance > SLIDE_TO_HEAD_JUMPING_FALL_DISTANCE)
        {
            this.isSliding = false;
            this.isHeadJumping = true;
            this.isAerodynamic = true;
        }

        if (SmartMovingConfig.SLIDING.enable && this.grabButton.isPressed && (this.isGroundSprinting || (this.wasRunning && !isRunning && this.player.onGround)) && !this.isCrawling && this.sneakButton.startPressed && !this.isDipping)
        {
            this.setHeightOffset(-1);
            this.move(0, (-1D), 0, true);
            this.tryJump(ConfigHelper.SlideDown, false, this.wasRunning, null);
            this.isSliding = true;
            this.isHeadJumping = false;
            this.isAerodynamic = false;
        }

        if (this.isSliding && (!this.sneakButton.isPressed || horizontalSpeedSquare < SmartMovingConfig.SLIDING.speedStopFactor * 0.01))
        {
            this.isSliding = false;
            this.wasCrawling = this.toCrawling();
        }

        if (this.isSliding && this.player.fallDistance > SmartMovingConfig.FALLING.distanceMinimum)
        {
            this.isSliding = false;
            this.wasCrawling = true;
            this.isCrawling = false;
        }

        boolean sneakContinueInput = SmartMovingConfig.USER_INTERFACE.sneakToggle ? this.sneakToggled || this.sneakButton.startPressed : this.sneakButton.isPressed;
        boolean wouldWantSneak = !this.isFlying && !this.isSliding && !this.isHeadJumping && !(this.isDiving && SmartMovingConfig.DIVING.downSneak) && !(this.isSwimming && SmartMovingConfig.SWIMMING.downSneak && !this.isFakeShallowWaterSneaking) && sneakContinueInput && !wantCrawl && !mustCrawl && (!SmartMovingConfig.CRAWLING.enable || !this.grabButton.isPressed);

        boolean wantSneak = SmartMovingConfig.GENERIC_SNEAKING.enable && wouldWantSneak;

        boolean moveButtonPressed = ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward != 0F || ((EntityPlayerSP) this.entityPlayer).movementInput.moveStrafe != 0F;
        boolean moveForwardButtonPressed = ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F;

        this.wantSprint = SmartMovingConfig.GENERIC_SPRINTING.enable && !this.isSliding && this.sprintButton.isPressed && (moveForwardButtonPressed || this.isClimbing || (this.isSwimming && (moveButtonPressed || (this.sneakButton.isPressed && SmartMovingConfig.SWIMMING.downSneak))) || (this.isDiving && (moveButtonPressed || this.jumpButton.isPressed || (this.sneakButton.isPressed && SmartMovingConfig.DIVING.downSneak))) || (this.isFlying && (moveButtonPressed || this.jumpButton.isPressed || this.sneakButton.isPressed))) && !disabled;

        boolean exhaustionAllowsRunning = !SmartMovingConfig.STANDARD_SPRINTING.exhaustion || (this.exhaustion < SmartMovingConfig.STANDARD_SPRINTING.exhaustionStop && (this.wasRunning || this.exhaustion < SmartMovingConfig.STANDARD_SPRINTING.exhaustionStart));

        if (isRunning && this.player.onGround && SmartMovingConfig.STANDARD_SPRINTING.exhaustion)
        {
            this.maxExhaustionForAction = Math.min(this.maxExhaustionForAction, SmartMovingConfig.STANDARD_SPRINTING.exhaustionStop);
            this.maxExhaustionToStartAction = Math.min(this.maxExhaustionToStartAction, SmartMovingConfig.STANDARD_SPRINTING.exhaustionStart);
        }

        if (!exhaustionAllowsRunning && isRunning)
        {
            this.player.setSprinting(isRunning = false);
        }

        if (!this.player.onGround && this.isFast && !this.isClimbing && !this.isCeilingClimbing && !this.isDiving && !this.isSwimming)
        {
            this.isSprintJump = true;
        }

        boolean exhaustionAllowsSprinting = !SmartMovingConfig.GENERIC_SPRINTING.exhaustion || (this.exhaustion <= SmartMovingConfig.GENERIC_SPRINTING.exhaustionStop && (this.isFast || this.isSprintJump || this.exhaustion <= SmartMovingConfig.GENERIC_SPRINTING.exhaustionStart));

        if (this.player.onGround || this.isFlying || this.isSwimming || this.isDiving || this.player.isInLava())
        {
            this.isSprintJump = false;
        }

        boolean preferSprint = false;
        if (this.wantSprint && !wantSneak)
        {
            if (!this.isSprintJump && SmartMovingConfig.GENERIC_SPRINTING.exhaustion)
            {
                this.maxExhaustionForAction = Math.min(this.maxExhaustionForAction, SmartMovingConfig.GENERIC_SPRINTING.exhaustionStop);
                this.maxExhaustionToStartAction = Math.min(this.maxExhaustionToStartAction, SmartMovingConfig.GENERIC_SPRINTING.exhaustionStart);
            }

            if (exhaustionAllowsSprinting)
            {
                preferSprint = true;
            }
        }

        boolean isClimbSprintSpeed = true;
        if (this.isClimbing && preferSprint)
        {
            double minTickDistance;
            if (this.wantClimbUp)
            {
                minTickDistance = 0.07 * SmartMovingConfig.CLIMB.freeUpSpeedFactor;
            }
            else if (this.wantClimbDown)
            {
                minTickDistance = 0.11 * SmartMovingConfig.CLIMB.freeDownSpeedFactor;
            }
            else
            {
                minTickDistance = 0.07;
            }

            isClimbSprintSpeed = net.smart.render.statistics.SmartStatisticsFactory.getInstance(this.player).getTickDistance() >= minTickDistance;
        }

        boolean canAnySprint = preferSprint && !this.player.isBurning() && (SmartMovingConfig.ITEM_USAGE.sprint || this.player.getItemInUseCount() < 1);
        boolean canVerticallySprint = canAnySprint && !this.player.collidedHorizontally;
        boolean canHorizontallySprint = canAnySprint && this.collidedHorizontallyTickCount < 3;
        boolean canAllSprint = canHorizontallySprint && canVerticallySprint;

        boolean wasGroundSprinting = this.isGroundSprinting;
        this.isGroundSprinting = canHorizontallySprint && this.player.onGround && !this.isSwimming && !this.isDiving && !this.isClimbing;
        boolean isSwimSprinting = canHorizontallySprint && this.isSwimming;
        boolean isDiveSprinting = canAllSprint && this.isDiving;
        boolean isCeilingSprinting = canHorizontallySprint && this.isCeilingClimbing;
        boolean isFlyingSprinting = canAllSprint && this.isFlying;
        boolean isClimbSprinting = canAnySprint && this.isClimbing && isClimbSprintSpeed;

        this.isFast = this.isGroundSprinting || isSwimSprinting || isDiveSprinting || isCeilingSprinting || isFlyingSprinting || isClimbSprinting;

        if (this.isGroundSprinting && !wasGroundSprinting)
        {
            this.wasRunningWhenSprintStarted = this.player.isSprinting();
            this.player.setSprinting(this.isStandupSprintingOrRunning());
        }
        else if (wasGroundSprinting && !this.isGroundSprinting)
        {
            this.player.setSprinting(this.wasRunningWhenSprintStarted);
        }

        this.wouldIsSneaking = wouldWantSneak && !this.wantSprint && !this.isClimbing;

        boolean wasSneaking = this.isSlow;
        this.isSlow = wantSneak && this.wouldIsSneaking;

        boolean wantClimbHolding = (this.isClimbHolding && this.sneakButton.isPressed) || (this.isClimbing && blocked) || (wantClimb && !this.isSwimming && !this.isDiving && !this.isCrawling && (this.sneakButton.isPressed || this.crawlToggled));

        this.isClimbHolding = wantClimbHolding && this.isClimbing;

        this.isStanding = horizontalSpeedSquare < 0.0005;

        boolean wasCrawlClimbing = this.isCrawlClimbing;
        this.isCrawlClimbing = (this.wasCrawling || this.isCrawlClimbing) && this.isClimbing && this.isNeighborClimbing && (this.sneakButton.isPressed || this.crawlToggled) && ((EntityPlayerSP) this.entityPlayer).movementInput.moveForward > 0F;
        if (this.isCrawlClimbing)
        {
            boolean canStandUp = !this.isPlayerInSolidBetween(this.getBoundingBox().minY - (this.isClimbCrawling ? 0.95D : 1D), this.getBoundingBox().minY);
            if (canStandUp)
            {
                wasCrawlClimbing = false;
                this.isCrawlClimbing = false;
                if (!this.isClimbCrawling)
                {
                    this.resetHeightOffset();
                }
            }

            if (!wasCrawlClimbing)
            {
                this.wasCrawling = false;
                this.isCrawling = false;
            }
        }
        else if (wasCrawlClimbing)
        {
            boolean toCrawling = this.sneakButton.isPressed || this.crawlToggled;
            if (!this.isClimbing)
            {
                this.wasCrawling = this.toCrawling();

                double minY = this.getBoundingBox().minY;
                this.move(0, (-minY + Math.floor(minY)), 0, true);
            }
            else if (((EntityPlayerSP) this.entityPlayer).movementInput.moveForward <= 0F)
            {
                this.wasCrawling = toCrawling;
                this.isCrawling = toCrawling;

                this.wantClimbUp = false;
                this.wantClimbDown = false;

                if (!toCrawling)
                {
                    this.resetHeightOffset();
                }
                double minY = this.getBoundingBox().minY;
                this.move(0, (-minY + Math.floor(minY) + (toCrawling ? 0F : 1F)), 0, true);
            }
            else if (!toCrawling)
            {
                this.resetHeightOffset();
                double minY = this.getBoundingBox().minY;
                this.move(0, (Math.ceil(minY) - minY), 0, true);
            }
        }

        boolean wasClimbCrawling = this.isClimbCrawling;
        boolean needClimbCrawling = this.hasClimbCrawlGap || (this.hasClimbGap && this.isClimbHolding);
        boolean canClimbCrawling = wantClimbHolding && this.wantClimbUp;

        if (this.climbIntoCount > 1)
        {
            this.climbIntoCount--;
        }
        else if (this.isClimbCrawling && !needClimbCrawling && this.climbIntoCount == 0)
        {
            this.climbIntoCount = 6;
        }

        this.isClimbCrawling = canClimbCrawling && ((needClimbCrawling && this.climbIntoCount == 0) || this.climbIntoCount > 1);
        if (this.isClimbCrawling && !wasClimbCrawling)
        {
            this.setHeightOffset(-1F);

            boolean wasCollidedHorizontally = this.player.collidedHorizontally; // preserve the horizontal collision state
            this.move(0, 0.05, 0, true); // to avoid climb crawling into solid when standing with solid above head (SMP: Illegal Stance)
            this.player.collidedHorizontally = wasCollidedHorizontally; // to avoid climb crawling out of water bug
        }
        else if (!this.isClimbCrawling && wasClimbCrawling)
        {
            this.climbIntoCount = 0;
            if (mustCrawl || this.sneakButton.isPressed || this.crawlToggled)
            {
                double gapUnderneight = this.getBoundingBox().minY - this.getMaxPlayerSolidBetween(this.getBoundingBox().minY - 1D, this.getBoundingBox().minY, 0);
                if (gapUnderneight >= 0D && gapUnderneight < 1D)
                {
                    this.wasCrawling = this.toCrawling();
                    this.move(0, (-gapUnderneight), 0, true);
                }
                else
                {
                    this.resetHeightOffset();
                }
            }
            else
            {
                this.resetHeightOffset();
            }
        }

        if ((this.wasCrawling && !this.isCrawling) && !initializeCrawling && !this.entityPlayer.capabilities.isFlying)
        {
            this.resetHeightOffset();
            this.move(0, (crawlStandUpBottom - this.getBoundingBox().minY), 0, true);
        }
        else if ((this.isCrawling && !this.wasCrawling) || initializeCrawling)
        {
            this.setHeightOffset(-1F);

            if (!initializeCrawling || this.player.world.isRemote)
            {
                this.move(0, (-1D), 0, true);
            }

            if (initializeCrawling)
            {
                this.wasCrawling = this.toCrawling();
            }
        }

        if (this.grabButton.startPressed)
        {
            if (this.isShallowDiveOrSwim && wouldWantClimb)
            {
                // from swimming/diving in shallow water to walking in shallow water
                this.resetHeightOffset();
                this.move(0, (this.getMaxPlayerSolidBetween(this.getBoundingBox().minY, this.getBoundingBox().maxY, 0) - this.getBoundingBox().minY), 0, true);
                if (this.jumpButton.isPressed)
                {
                    this.isStillSwimmingJump = true;
                }
            }
            else if (this.isDipping && wouldWantCrawl && this.dippingDepth >= SWIM_CRAWL_WATER_BOTTOM_BORDER)
            {
                if (this.dippingDepth >= SWIM_CRAWL_WATER_MEDIUM_BORDER)
                {
                    // from sneaking in shallow water to swimming/diving in shallow water
                    this.setHeightOffset(-1F);
                    this.move(0, (-1.6F + this.dippingDepth), 0, true);
                    this.isCrawling = false;
                }
                else
                {
                    // from sneaking in shallow water to crawling in shallow water
                    this.setHeightOffset(-1F);
                    this.move(0, (-1D), 0, true);
                    this.wasCrawling = this.toCrawling();
                }
            }
        }

        this.isWallJumping = false;

        if (this.continueWallJumping && (this.player.onGround || this.isClimbing || !this.jumpButton.isPressed))
        {
            this.continueWallJumping = false;
        }

        boolean canWallJumping = SmartMovingConfig.WALL_JUMPING.enable && !this.isHeadJumping && !this.player.onGround && !this.isClimbing && !this.isSwimming && !this.isDiving && !isLevitating && !this.isFlying;
        boolean triggerWallJumping = false;

        if (SmartMovingConfig.USER_INTERFACE.jumpWallDoubleClick)
        {
            if (canWallJumping)
            {
                if (this.jumpButton.startPressed)
                {
                    if (this.wallJumpCount == 0)
                    {
                        this.wallJumpCount = SmartMovingConfig.USER_INTERFACE.jumpWallDoubleClickTicks;
                    }
                    else
                    {
                        triggerWallJumping = true;
                        this.wallJumpCount = 0;
                    }
                }
                else if (this.wallJumpCount > 0)
                {
                    this.wallJumpCount--;
                }
            }
            else
            {
                this.wallJumpCount = 0;
            }
        }
        else
        {
            triggerWallJumping = this.jumpButton.startPressed;
        }

        this.wantWallJumping = canWallJumping && (triggerWallJumping || this.continueWallJumping || (this.wantWallJumping && this.jumpButton.isPressed && !this.player.collidedHorizontally));

        boolean canAngleJump = !isSleeping && this.player.onGround && !this.isCrawling && !this.isClimbing && !this.isClimbCrawling && !this.isSwimming && !this.isDiving;
        boolean canSideJump = SmartMovingConfig.SIDE_AND_BACK_JUMPING.side && canAngleJump;
        boolean canLeftJump = canSideJump && !this.rightButton.isPressed;
        boolean canRightJump = canSideJump && !this.leftButton.isPressed;
        boolean canBackJump = SmartMovingConfig.SIDE_AND_BACK_JUMPING.back && canAngleJump && !this.forwardButton.isPressed && !this.isStandupSprintingOrRunning();

        if (canLeftJump)
        {
            if (this.leftButton.startPressed)
            {
                if (this.leftJumpCount == 0)
                {
                    this.leftJumpCount = SmartMovingConfig.USER_INTERFACE.jumpAngleDoubleClickTicks;
                }
                else
                {
                    this.leftJumpCount = -1;
                }
            }
            else if (this.leftJumpCount > 0)
            {
                this.leftJumpCount--;
            }
        }
        else
        {
            this.leftJumpCount = 0;
        }

        if (canRightJump)
        {
            if (this.rightButton.startPressed)
            {
                if (this.rightJumpCount == 0)
                {
                    this.rightJumpCount = SmartMovingConfig.USER_INTERFACE.jumpAngleDoubleClickTicks;
                }
                else
                {
                    this.rightJumpCount = -1;
                }
            }
            else if (this.rightJumpCount > 0)
            {
                this.rightJumpCount--;
            }
        }
        else
        {
            this.rightJumpCount = 0;
        }

        if (canBackJump)
        {
            if (this.backButton.startPressed)
            {
                if (this.backJumpCount == 0)
                {
                    this.backJumpCount = SmartMovingConfig.USER_INTERFACE.jumpAngleDoubleClickTicks;
                }
                else
                {
                    this.backJumpCount = -1;
                }
            }
            else if (this.backJumpCount > 0)
            {
                this.backJumpCount--;
            }
        }
        else
        {
            this.backJumpCount = 0;
        }

        if (this.rightJumpCount == -2 && this.backJumpCount <= 0)
        {
            this.rightJumpCount = -1;
        }
        if (this.leftJumpCount == -2 && this.backJumpCount <= 0)
        {
            this.leftJumpCount = -1;
        }
        if (this.backJumpCount == -2 && (this.leftJumpCount <= 0 || this.rightJumpCount <= 0))
        {
            this.backJumpCount = -1;
        }

        if (this.rightJumpCount == -1 && this.backJumpCount > 0)
        {
            this.rightJumpCount = -2;
        }
        if (this.leftJumpCount == -1 && this.backJumpCount > 0)
        {
            this.leftJumpCount = -2;
        }
        if (this.backJumpCount == -1 && (this.leftJumpCount > 0 || this.rightJumpCount > 0))
        {
            this.backJumpCount = -2;
        }

        if (this.player.onGround || this.player.collidedHorizontally)
        {
            this.angleJumpType = 0;
        }

        boolean isSneakToggleEnabled = SmartMovingConfig.USER_INTERFACE.sneakToggle;
        boolean isCrawlToggleEnabled = SmartMovingConfig.USER_INTERFACE.crawlToggle;

        boolean willStopCrawl = false;
        boolean willStopCrawlStartSneak = false;
        if (isSneakToggleEnabled || isCrawlToggleEnabled)
        {
            if (this.isCrawling && this.jumpButton.stopPressed)
            {
                willStopCrawlStartSneak = true;
            }
            if (this.isCrawling && this.sneakButton.stopPressed && !this.ignoreNextStopSneakButtonPressed)
            {
                willStopCrawlStartSneak = true;
            }
            if (!this.isCrawling && !this.isCrawlClimbing && !this.isClimbCrawling)
            {
                willStopCrawl = true;
            }

            willStopCrawl |= willStopCrawlStartSneak;
        }

        boolean willStopSneak = false;
        if (isSneakToggleEnabled)
        {
            if (this.isCrawling && !willStopCrawlStartSneak)
            {
                willStopSneak = true;
            }
            if (wantSneak && this.wantSprint && this.sneakButton.startPressed && this.sneakToggled)
            {
                willStopSneak = true;
                this.ignoreNextStopSneakButtonPressed = true;
            }
            if (wasSneaking && this.sneakButton.startPressed)
            {
                willStopSneak = true;
            }
            if (!this.isSwimming && !this.isDiving && this.jumpButton.stopPressed)
            {
                willStopSneak = true;
            }
        }

        boolean willStartSneak = false;
        if (isSneakToggleEnabled)
        {
            if (willStopCrawlStartSneak && this.sneakButton.stopPressed)
            {
                willStartSneak = true;
            }
            if (this.isFast && this.sneakButton.stopPressed && !this.ignoreNextStopSneakButtonPressed)
            {
                willStartSneak = true;
            }
            if (this.isSlow && !wasSneaking)
            {
                willStartSneak = true;
            }
        }

        boolean willStartCrawl = false;
        if (isCrawlToggleEnabled)
        {
            if (this.isCrawling && !this.wasCrawling)
            {
                willStartCrawl = true;
            }
            if (this.isClimbCrawling && !wasClimbCrawling)
            {
                willStartCrawl = true;
            }
        }

        if (isSneakToggleEnabled)
        {
            if (willStartSneak)
            {
                this.sneakToggled = true;
            }
            if (willStopSneak)
            {
                this.sneakToggled = false;
            }
        }

        if (isCrawlToggleEnabled)
        {
            if (willStartCrawl)
            {
                this.crawlToggled = true;
                this.ignoreNextStopSneakButtonPressed = this.sneakButton.isPressed;
            }
            if (willStopCrawl)
            {
                this.crawlToggled = false;
            }
        }

        if (this.sneakButton.stopPressed)
        {
            this.ignoreNextStopSneakButtonPressed = false;
        }

        this.wasRunning = isRunning;
        this.wasLevitating = isLevitating;
    }

    private boolean toCrawling()
    {
        this.isCrawling = true;
        if (SmartMovingConfig.USER_INTERFACE.crawlToggle)
        {
            this.crawlToggled = true;
        }
        this.ignoreNextStopSneakButtonPressed = true;
        return true;
    }

    public double prevMotionX;
    public double prevMotionY;
    public double prevMotionZ;
    public final Button forwardButton = new Button();
    public final Button leftButton = new Button();
    public final Button rightButton = new Button();
    public final Button backButton = new Button();
    public final Button jumpButton = new Button();
    public final Button sneakButton = new Button();
    public final Button grabButton = new Button();
    public final Button sprintButton = new Button();
    public boolean wasRunning;
    public boolean wasLevitating;
    public boolean wasCrawling;
    public boolean wasHeadJumping;
    private boolean contextContinueCrawl;
    private boolean ignoreNextStopSneakButtonPressed;
    private int collidedHorizontallyTickCount;
    private boolean wantWallJumping;
    private boolean continueWallJumping;
    private boolean wasCollidedHorizontally;
    private boolean wasRunningWhenSprintStarted;
    private boolean jumpAvoided;
    private int climbIntoCount;
    private int leftJumpCount;
    private int rightJumpCount;
    private int backJumpCount;
    private int wallJumpCount;
    private int nextClimbDistance;
    public float distanceClimbedModified;
    private boolean sneakToggled = false;
    private boolean crawlToggled = false;
    private int lastWorldPlayerEntitiesSize = -1;
    private int lastWorldPlayerLastEnttyId = -1;

    public void addToSendQueue()
    {
        if (!this.player.world.isRemote)
        {
            return;
        }

        NBTTagCompound state = new NBTTagCompound();

        state.setInteger("actual_feet_climb_type", this.actualFeetClimbType);
        state.setInteger("actual_hands_climb_type", this.actualHandsClimbType);
        state.setBoolean("is_jumping", this.playerBase.getIsJumpingField());
        state.setBoolean("is_diving", this.isDiving);
        state.setBoolean("is_dipping", this.isDipping);
        state.setBoolean("is_swimming", this.isSwimming);
        state.setBoolean("is_crawl_climbing", this.isCrawlClimbing);
        state.setBoolean("is_crawling", this.isCrawling);
        state.setBoolean("is_climbing", this.isClimbing);
        state.setBoolean("is_small", this.player.height < 1);
        state.setBoolean("is_falling", this.doFallingAnimation());
        state.setBoolean("is_flying", this.doFlyingAnimation());
        state.setBoolean("is_ceiling_climbing", this.isCeilingClimbing);
        state.setBoolean("is_levitating", this.isLevitating);
        state.setBoolean("is_head_jumping", this.isHeadJumping);
        state.setBoolean("is_sliding", this.isSliding);
        state.setInteger("angle_jump_type", this.angleJumpType);
        state.setBoolean("is_feet_vine_climbing", this.isFeetVineClimbing);
        state.setBoolean("is_hands_vine_climbing", this.isHandsVineClimbing);
        state.setBoolean("is_climb_jumping", this.isClimbJumping);
        state.setBoolean("is_climb_back_jumping", this.isClimbBackJumping);
        state.setBoolean("is_slow", this.isSlow);
        state.setBoolean("is_fast", this.isFast);
        state.setBoolean("is_wall_jumping", this.isWallJumping);
        state.setBoolean("is_rope_sliding", this.isRopeSliding);

        boolean sendStatePacket = this.prevPacketState == null || state.hashCode() != this.prevPacketState.hashCode();

        int currentWorldPlayerEntitiesSize = this.player.world.playerEntities.size();
        if (currentWorldPlayerEntitiesSize == 0)
        {
            sendStatePacket = false;
            this.lastWorldPlayerEntitiesSize = currentWorldPlayerEntitiesSize;
            this.lastWorldPlayerLastEnttyId = -1;
        }
        else
        {
            int currentWorldPlayerLastEnttyId = this.player.world.playerEntities.get(currentWorldPlayerEntitiesSize - 1).getEntityId();
            if (currentWorldPlayerEntitiesSize != this.lastWorldPlayerEntitiesSize)
            {
                if (currentWorldPlayerEntitiesSize > this.lastWorldPlayerEntitiesSize)
                {
                    sendStatePacket = true;
                }
                this.lastWorldPlayerEntitiesSize = currentWorldPlayerEntitiesSize;
                this.lastWorldPlayerLastEnttyId = currentWorldPlayerLastEnttyId;
            }
            else if (currentWorldPlayerLastEnttyId != this.lastWorldPlayerLastEnttyId)
            {
                sendStatePacket = true;
                this.lastWorldPlayerLastEnttyId = currentWorldPlayerLastEnttyId;
            }
        }

        if (sendStatePacket)
        {
            MessageHandler.INSTANCE.sendToServer(new MessageStateServer(this.player.getEntityId(), state));
            this.prevPacketState = state;
        }
    }

    private NBTTagCompound prevPacketState;

    @Override
    public boolean isSneaking()
    {
        return (this.isSlow && (this.player.onGround || this.playerBase.getIsInWebField())) || (!SmartMovingConfig.GENERIC_SNEAKING.enable && this.wouldIsSneaking && this.jumpCharge > 0) || (this.player.isRiding() && this.playerBase.localIsSneaking()) || (!SmartMovingConfig.CRAWLING.edge && this.isCrawling && !this.isClimbing);
    }

    public boolean isStandupSprintingOrRunning()
    {
        return (this.isFast || this.player.isSprinting()) && this.player.onGround && !this.isSliding && !this.isCrawling;
    }

    public boolean isRunning()
    {
        return this.player.isSprinting() && !this.isFast && this.player.onGround;
    }

    public void jump()
    {
        this.jumpAvoided = true;
    }

    public void writeEntityToNBT(NBTTagCompound nBTTagCompound)
    {
        this.playerBase.localWriteEntityToNBT(nBTTagCompound);
        NBTTagCompound abilities = nBTTagCompound.getCompoundTag("abilities");
        if (abilities.hasKey("flying"))
        {
            abilities.setBoolean("flying", this.player.capabilities.isFlying);
        }
    }

    @Override
    public boolean isJumping()
    {
        return this.playerBase.getIsJumpingField();
    }

    @Override
    public boolean doFlyingAnimation()
    {
        if (SmartMovingConfig.SMART_FLYING.enable || SmartMovingConfig.STANDARD_FLYING.animation)
        {
            return this.player.capabilities.isFlying;
        }
        return false;
    }

    @Override
    public boolean doFallingAnimation()
    {
        if (SmartMovingConfig.FALLING.animation)
        {
            return !this.player.onGround && this.player.fallDistance > SmartMovingConfig.FALLING.animationDistanceMinimum;
        }
        return false;
    }

    public void onLivingJump()
    {
        net.minecraftforge.common.ForgeHooks.onLivingJump(this.player);
    }

    public float getFOVMultiplier()
    {
        float landMovementFactor = this.getLandMovementFactor();
        this.setLandMovementFactor(this.fadingPerspectiveFactor);
        float result = this.playerBase.localGetFOVMultiplier();
        this.setLandMovementFactor(landMovementFactor);
        return result;
    }

    public float getLandMovementFactor()
    {
        return this.player.getAIMoveSpeed();
    }

    public void setLandMovementFactor(float landMovementFactor)
    {
        ((IModifiableAttributeInstance) this.player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)).setCachedValue(landMovementFactor);
    }
}