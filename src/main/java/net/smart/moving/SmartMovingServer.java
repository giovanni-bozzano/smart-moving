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

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.smart.moving.network.MessageHandler;
import net.smart.moving.network.packets.MessageStateClient;
import net.smart.moving.playerapi.CustomServerPlayerEntityBase;

import java.util.List;

public class SmartMovingServer
{
    public static final float SMALL_SIZE_ITEM_GRAB_HEIGHT = 0.25F;
    protected final CustomServerPlayerEntityBase playerBase;
    private boolean resetFallDistance = false;
    private boolean resetTicksForFloatKick = false;
    public boolean crawlingInitialized;
    public int crawlingCooldown;
    public boolean isCrawling;
    public boolean isSmall;

    public SmartMovingServer(CustomServerPlayerEntityBase playerBase)
    {
        this.playerBase = playerBase;
    }

    public void processStatePacket(int entityId, long state)
    {
        boolean isCrawling = ((state >>> 13) & 1) != 0;
        this.setCrawling(isCrawling);

        boolean isSmall = ((state >>> 15) & 1) != 0;
        this.setSmall(isSmall);

        boolean isClimbing = ((state >>> 14) & 1) != 0;
        boolean isCrawlClimbing = ((state >>> 12) & 1) != 0;
        boolean isCeilingClimbing = ((state >>> 18) & 1) != 0;

        boolean isWallJumping = ((state >>> 31) & 1) != 0;

        this.resetFallDistance = isClimbing || isCrawlClimbing || isCeilingClimbing || isWallJumping;
        this.resetTicksForFloatKick = isClimbing || isCrawlClimbing || isCeilingClimbing;
        MessageHandler.INSTANCE.sendToAllTracking(new MessageStateClient(entityId, state), this.playerBase.getPlayer());
    }

    public void processHungerChangePacket(float hunger)
    {
        this.playerBase.localAddExhaustion(hunger);
    }

    public void processSoundPacket(String soundId, float volume, float pitch)
    {
        this.playerBase.localPlaySound(soundId, volume, pitch);
    }

    public void afterOnUpdate()
    {
        if (this.resetFallDistance) {
            this.playerBase.resetFallDistance();
        }
        if (this.resetTicksForFloatKick) {
            this.playerBase.resetTicksForFloatKick();
        }
    }

    public void setCrawling(boolean crawling)
    {
        if (!crawling && this.isCrawling) {
            this.crawlingCooldown = 10;
        }
        this.isCrawling = crawling;
    }

    public void setSmall(boolean isSmall)
    {
        this.playerBase.setHeight(isSmall ? 0.8F : 1.8F);
        this.isSmall = isSmall;
    }

    public void afterSetPosition()
    {
        if (!this.crawlingInitialized) {
            this.playerBase.setMaxY(this.playerBase.getMinY() + this.playerBase.getHeight() - 1);
        }
    }

    public void beforeIsPlayerSleeping()
    {
        if (!this.crawlingInitialized) {
            this.playerBase.setMaxY(this.playerBase.getMinY() + this.playerBase.getHeight());
            this.crawlingInitialized = true;
        }
    }

    public void beforeOnUpdate()
    {
        if (this.crawlingCooldown > 0) {
            this.crawlingCooldown--;
        }
    }

    public void afterOnLivingUpdate()
    {
        if (!this.isSmall) {
            return;
        }

        if (this.playerBase.doGetHealth() <= 0) {
            return;
        }

        double offset = SMALL_SIZE_ITEM_GRAB_HEIGHT;
        AxisAlignedBB box = this.playerBase.expandBox(this.playerBase.getBox(), 1, offset, 1);

        List<?> offsetEntities = this.playerBase.getEntitiesExcludingPlayer(box);
        if (offsetEntities != null && offsetEntities.size() > 0) {
            Object[] offsetEntityArray = offsetEntities.toArray();

            box = this.playerBase.expandBox(box, 0, -offset, 0);
            List<?> standardEntities = this.playerBase.getEntitiesExcludingPlayer(box);

            for (Object o : offsetEntityArray) {
                Entity offsetEntity = (Entity) o;
                if (standardEntities != null && standardEntities.contains(offsetEntity)) {
                    continue;
                }

                if (!this.playerBase.isDeadEntity(offsetEntity)) {
                    this.playerBase.onCollideWithPlayer(offsetEntity);
                }
            }
        }
    }

    public boolean isEntityInsideOpaqueBlock()
    {
        if (this.crawlingCooldown > 0) {
            return false;
        }

        return this.playerBase.localIsEntityInsideOpaqueBlock();
    }

    public void addMovementStat(double var1, double var3, double var5)
    {
        this.playerBase.localAddMovementStat(var1, var3, var5);
    }

    public void addExhaustion(float exhaustion)
    {
        this.playerBase.localAddExhaustion(exhaustion);
    }

    public boolean isSneaking()
    {
        return this.playerBase.getItemInUseCount() > 0 || this.playerBase.localIsSneaking();
    }
}