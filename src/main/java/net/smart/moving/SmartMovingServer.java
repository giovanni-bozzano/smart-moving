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

import api.player.asm.interfaces.IServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.smart.moving.config.Config;
import net.smart.moving.config.ServerOptions;
import net.smart.moving.network.MessageHandler;
import net.smart.moving.network.packets.MessageConfigChangeClient;
import net.smart.moving.network.packets.MessageConfigContentClient;
import net.smart.moving.network.packets.MessageSpeedChangeClient;
import net.smart.moving.network.packets.MessageStateClient;
import net.smart.moving.playerapi.CustomServerPlayerEntityBase;
import net.smart.properties.Property;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartMovingServer
{
    public static final float SMALL_SIZE_ITEM_GRAB_HEIGHT = 0.25F;
    protected final CustomServerPlayerEntityBase playerBase;
    private boolean resetFallDistance = false;
    private boolean resetTicksForFloatKick = false;
    private boolean initialized = false;
    public boolean crawlingInitialized;
    public int crawlingCooldown;
    public boolean isCrawling;
    public boolean isSmall;

    public SmartMovingServer(CustomServerPlayerEntityBase playerBase, boolean onTheFly)
    {
        this.playerBase = playerBase;
        if (onTheFly) {
            this.initialize(true);
        }
    }

    public void initialize(boolean alwaysSendMessage)
    {
        if (Options._globalConfig.getValue()) {
            MessageHandler.INSTANCE.sendTo(new MessageConfigContentClient(optionsHandler.writeToProperties(), this.playerBase.getUsername()), this.playerBase.getPlayer());
        } else if (Options._serverConfig.getValue()) {
            MessageHandler.INSTANCE.sendTo(new MessageConfigContentClient(optionsHandler.writeToProperties(this.playerBase, false), this.playerBase.getUsername()), this.playerBase.getPlayer());
        } else if (alwaysSendMessage) {
            MessageHandler.INSTANCE.sendTo(new MessageConfigContentClient(Options.enabled ? new HashMap<>() : null, this.playerBase.getUsername()), this.playerBase.getPlayer());
        }
        this.initialized = true;
    }

    public void processStatePacket(int entityId, long state)
    {
        if (!this.initialized) {
            this.initialize(false);
        }

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

    public void processConfigPacket(String clientConfigurationVersion)
    {
        boolean warn = true;
        String type = "unknown";
        if (clientConfigurationVersion != null) {
            for (int i = 0; i < Config._all.length; i++) {
                if (clientConfigurationVersion.equals(Config._all[i])) {
                    warn = i > 0;
                    type = warn ? "outdated" : "matching";
                    break;
                }
            }
        }

        String message = "Smart Moving player \"" + this.playerBase.getUsername() + "\" connected with " + type + " configuration system";
        if (clientConfigurationVersion != null) {
            message += " version \"" + clientConfigurationVersion + "\"";
        }

        if (warn) {
            FMLLog.warning(message);
        } else {
            FMLLog.info(message);
        }
    }

    public void processConfigChangePacket(String localUserName)
    {
        if (!Options._globalConfig.getValue()) {
            this.toggleSingleConfig();
            return;
        }

        String username = this.playerBase.getUsername();

        if (localUserName.equals(username)) {
            this.toggleConfig();
            return;
        }

        String[] rightPlayerNames = Options._usersWithChangeConfigRights.getValue();
        for (String rightPlayerName : rightPlayerNames) {
            if (rightPlayerName.equals(username)) {
                this.toggleConfig();
                return;
            }
        }

        MessageHandler.INSTANCE.sendTo(new MessageConfigChangeClient(), this.playerBase.getPlayer());
    }

    public void processSpeedChangePacket(int difference, String localUserName)
    {
        if (!Options._globalConfig.getValue()) {
            this.changeSingleSpeed(difference);
            return;
        }

        if (!this.hasRight(localUserName, Options._usersWithChangeSpeedRights)) {
            MessageHandler.INSTANCE.sendTo(new MessageSpeedChangeClient(0, null), this.playerBase.getPlayer());
        } else {
            this.changeSpeed(difference);
        }
    }

    public void processHungerChangePacket(float hunger)
    {
        this.playerBase.localAddExhaustion(hunger);
    }

    public void processSoundPacket(String soundId, float volume, float pitch)
    {
        this.playerBase.localPlaySound(soundId, volume, pitch);
    }

    private boolean hasRight(String localUserName, Property<String[]> rights)
    {
        String username = this.playerBase.getUsername();

        if (localUserName.equals(username)) {
            return true;
        }

        String[] rightPlayerNames = rights.getValue();
        for (String rightPlayerName : rightPlayerNames) {
            if (rightPlayerName.equals(username)) {
                return true;
            }
        }

        return false;
    }

    public void toggleSingleConfig()
    {
        MessageHandler.INSTANCE.sendTo(new MessageConfigContentClient(optionsHandler.writeToProperties(this.playerBase, true), this.playerBase.getUsername()), this.playerBase.getPlayer());
    }

    public CustomServerPlayerEntityBase[] getAllPlayers()
    {
        List<EntityPlayerMP> playerEntityList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        CustomServerPlayerEntityBase[] result = new CustomServerPlayerEntityBase[playerEntityList.size()];
        for (int i = 0; i < playerEntityList.size(); i++) {
            result[i] = (CustomServerPlayerEntityBase) ((IServerPlayerEntity) playerEntityList.get(i)).getServerPlayerBase(SmartMovingMod.ID);
        }
        return result;
    }

    public void toggleConfig()
    {
        optionsHandler.toggle(this.playerBase);
        Map<String, String> config = optionsHandler.writeToProperties();

        CustomServerPlayerEntityBase[] players = this.getAllPlayers();
        for (CustomServerPlayerEntityBase player : players) {
            MessageHandler.INSTANCE.sendTo(new MessageConfigContentClient(config, player.getUsername()), player.getPlayer());
        }
    }

    public void changeSingleSpeed(int difference)
    {
        optionsHandler.changeSingleSpeed(this.playerBase, difference);
        MessageHandler.INSTANCE.sendTo(new MessageSpeedChangeClient(difference, this.playerBase.getUsername()), this.playerBase.getPlayer());
    }

    public void changeSpeed(int difference)
    {
        optionsHandler.changeSpeed(difference, this.playerBase);
        CustomServerPlayerEntityBase[] players = this.getAllPlayers();
        for (CustomServerPlayerEntityBase player : players) {
            MessageHandler.INSTANCE.sendTo(new MessageSpeedChangeClient(difference, this.playerBase.getUsername()), player.getPlayer());
        }
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

    public static void initialize(File optionsPath, int gameType, Config config)
    {
        Options = config;
        optionsHandler = new ServerOptions(Options, optionsPath, gameType);
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

    public static Config Options = null;
    private static ServerOptions optionsHandler = null;
}