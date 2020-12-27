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
package net.smart.moving.config;

import net.minecraftforge.fml.common.FMLLog;
import net.smart.moving.playerapi.CustomServerPlayerEntityBase;
import net.smart.properties.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerOptions
{
    public final Config config;
    public final File optionsPath;
    private final Property<Map<String, String>> _userConfigKeys;

    public ServerOptions(Config config, File optionsPath, int gameType)
    {
        this.config = config;
        this.optionsPath = optionsPath;

        config.loadFromOptionsFile(optionsPath);
        config.saveToOptionsFile(optionsPath);

        Property<String> configKey;
        Property<String[]> configKeys;
        switch (gameType) {
            default:
            case Config.Survival:
                configKey = config._survivalDefaultConfigKey;
                configKeys = config._survivalConfigKeys;
                this._userConfigKeys = config._survivalDefaultConfigUserKeys;
                break;
            case Config.Creative:
                configKey = config._creativeDefaultConfigKey;
                configKeys = config._creativeConfigKeys;
                this._userConfigKeys = config._creativeDefaultConfigUserKeys;
                break;
            case Config.Adventure:
                configKey = config._adventureDefaultConfigKey;
                configKeys = config._adventureConfigKeys;
                this._userConfigKeys = config._adventureDefaultConfigUserKeys;
                break;
        }

        config.setKeys(configKeys.getValue());
        config.setCurrentKey(configKey != null && !configKey.getValue().isEmpty() ? configKey.getValue() : null);

        logConfigState(config, null, false);
    }

    public void toggle(CustomServerPlayerEntityBase player)
    {
        this.config.toggle();
        this.config.saveToOptionsFile(this.optionsPath);
        logConfigState(this.config, player.getUsername(), true);
    }

    public void changeSpeed(int difference, CustomServerPlayerEntityBase player)
    {
        this.config.changeSpeed(difference);
        this.config.saveToOptionsFile(this.optionsPath);
        logSpeedState(this.config, player.getUsername());
    }

    private static void logConfigState(Config config, String username, boolean reconfig)
    {
        String message = "Smart Moving ";
        if (config._globalConfig.getValue()) {
            if (!reconfig) {
                FMLLog.info(message + "overrides client configurations");
            }

            String postfix = getPostfix(username);

            if (config.enabled) {
                String currentKey = config.getCurrentKey();

                message += reconfig ? "changed to " : "uses ";
                if (currentKey == null) {
                    FMLLog.info(message + "default server configuration" + postfix);
                } else {
                    String configName = config._configKeyName.getValue();

                    message += "server configuration ";
                    if (configName.isEmpty()) {
                        FMLLog.info(message + "with key \"" + currentKey + "\"" + postfix);
                    } else {
                        FMLLog.info(message + "\"" + configName + "\"" + postfix);
                    }
                }
            } else {
                FMLLog.info(message + "disabled" + postfix);
            }
        } else {
            FMLLog.info(message + "allows client configurations");
        }
    }

    private static void logSpeedState(Config config, String username)
    {
        FMLLog.info("Smart Moving speed set to " + config.getSpeedPercent() + "%" + getPostfix(username));
    }

    private static String getPostfix(String username)
    {
        if (username == null) {
            return "";
        }
        return " by user '" + username + "'";
    }

    public Map<String, String> writeToProperties()
    {
        return this.writeToProperties(null, null);
    }

    public Map<String, String> writeToProperties(CustomServerPlayerEntityBase mp, String key)
    {
        Map<String, String> result = new HashMap<>();
        if (key == null ? !this.config.enabled : key.equals(Properties.Disabled)) {
            result.put(this.config._globalConfig.getCurrentKey(), this.config._globalConfig.getValueString());
            return result;
        }

        net.smart.properties.Properties properties = new net.smart.properties.Properties();
        this.config.write(properties, key);

        Iterator<Map.Entry<Object, Object>> keys = properties.entrySet().iterator();

        String speedUserExponentKey = mp != null ? this.config._speedUserExponent.getCurrentKey() : null;
        while (keys.hasNext()) {
            Map.Entry<Object, Object> entry = keys.next();
            String propertyKey = entry.getKey().toString();
            if (mp != null && propertyKey.equals(speedUserExponentKey)) {
                Integer userExponent = this.config._speedUsersExponents.getValue().get(mp.getUsername());
                if (userExponent != null) {
                    entry.setValue(this.config._speedUserExponent.getValueString(userExponent));
                }
            }
            result.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }

    public void changeSingleSpeed(CustomServerPlayerEntityBase player, int difference)
    {
        Integer exponent = this.getPlayerSpeedExponent(player);
        if (exponent == null) {
            exponent = this.config._speedUserExponent.getValue();
        }

        exponent += difference;
        this.setPlayerSpeedExponent(player, exponent);
    }

    public Integer getPlayerSpeedExponent(CustomServerPlayerEntityBase player)
    {
        return this.config._speedUsersExponents.getValue().get(player.getUsername());
    }

    public synchronized void setPlayerSpeedExponent(CustomServerPlayerEntityBase player, Integer exponent)
    {
        this.config._speedUsersExponents.getValue().put(player.getUsername(), exponent);
        this.config.saveToOptionsFile(this.optionsPath);
    }

    public Map<String, String> writeToProperties(CustomServerPlayerEntityBase player, boolean toggle)
    {
        String key = this.getPlayerConfigurationKey(player);
        if (key == null || !this.config.hasKey(key)) {
            key = this.config.getCurrentKey();
            this.setPlayerConfigurationKey(player, key);
        }

        if (toggle) {
            key = this.config.getNextKey(key);
            this.setPlayerConfigurationKey(player, key);
        }

        return this.writeToProperties(player, key);
    }

    public String getPlayerConfigurationKey(CustomServerPlayerEntityBase player)
    {
        return this._userConfigKeys.getValue().get(player.getUsername());
    }

    public synchronized void setPlayerConfigurationKey(CustomServerPlayerEntityBase player, String key)
    {
        this._userConfigKeys.getValue().put(player.getUsername(), key);
        this.config.saveToOptionsFile(this.optionsPath);
    }
}