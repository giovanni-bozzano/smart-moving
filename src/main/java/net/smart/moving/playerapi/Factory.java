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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.smart.moving.Controller;
import net.smart.moving.ControllerOther;

import java.util.Hashtable;
import java.util.Iterator;

public class Factory
{
    private static Factory instance;
    private Hashtable<Integer, ControllerOther> otherSmartMovings;

    public Factory()
    {
        if (instance != null) {
            throw new RuntimeException("FATAL: Can only create one instance of type 'SmartMovingFactory'");
        }
        instance = this;
    }

    public static void initialize()
    {
        if (instance == null) {
            new Factory();
        }
    }

    public void handleMultiPlayerTick(Minecraft minecraft)
    {
        for (EntityPlayer player : minecraft.world.playerEntities) {
            if (player instanceof EntityOtherPlayerMP) {
                EntityOtherPlayerMP otherPlayer = (EntityOtherPlayerMP) player;
                ControllerOther moving = this.getOtherSmartMoving(otherPlayer);
                moving.spawnParticles(minecraft, otherPlayer.posX - otherPlayer.prevPosX, otherPlayer.posZ - otherPlayer.prevPosZ);
                moving.foundAlive = true;
            }
        }

        if (this.otherSmartMovings == null || this.otherSmartMovings.isEmpty()) {
            return;
        }

        Iterator<Integer> entityIds = this.otherSmartMovings.keySet().iterator();
        while (entityIds.hasNext()) {
            Integer entityId = entityIds.next();
            ControllerOther moving = this.otherSmartMovings.get(entityId);
            if (moving.foundAlive) {
                moving.foundAlive = false;
            } else {
                entityIds.remove();
            }
        }
    }

    public Controller getOtherSmartMoving(int entityId)
    {
        Controller moving = this.tryGetOtherSmartMoving(entityId);
        if (moving == null) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);
            if (entity instanceof EntityOtherPlayerMP) {
                moving = this.addOtherSmartMoving((EntityOtherPlayerMP) entity);
            }
        }
        return moving;
    }

    public ControllerOther getOtherSmartMoving(EntityOtherPlayerMP entity)
    {
        ControllerOther moving = this.tryGetOtherSmartMoving(entity.getEntityId());
        if (moving == null) {
            moving = this.addOtherSmartMoving(entity);
        }
        return moving;
    }

    protected ControllerOther tryGetOtherSmartMoving(int entityId)
    {
        if (this.otherSmartMovings == null) {
            this.otherSmartMovings = new Hashtable<>();
        }
        return this.otherSmartMovings.get(entityId);
    }

    protected ControllerOther addOtherSmartMoving(EntityOtherPlayerMP entity)
    {
        ControllerOther moving = new ControllerOther(entity);
        this.otherSmartMovings.put(entity.getEntityId(), moving);
        return moving;
    }

    public Controller getPlayerInstance(EntityPlayer entityPlayer)
    {
        Controller moving = null;
        if (entityPlayer instanceof EntityOtherPlayerMP) {
            moving = this.getOtherSmartMoving(entityPlayer.getEntityId());
        }
        if (moving != null) {
            return moving;
        }

        CustomClientPlayerEntityBase playerBase = PlayerAPIBridge.getPlayerBase(entityPlayer);
        if (playerBase != null) {
            return playerBase.getController();
        }

        return null;
    }

    public static Factory getInstance()
    {
        return instance;
    }
}