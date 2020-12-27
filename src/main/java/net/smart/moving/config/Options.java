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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.Loader;
import net.smart.moving.ContextBase;
import net.smart.moving.SmartMovingMod;
import net.smart.properties.Property;
import net.smart.utilities.Reflect;

import java.io.File;
import java.lang.reflect.Field;

public class Options extends ClientConfig
{
    public final Property<Boolean> _localUserHasChangeConfigRight = Unmodified("move.global.config.right.local.user").comment("Whether the current local user has the right to change the global configuration in-game (despite of the names listed in \"move.global.config.right.user.names\"").section();
    public final Property<Boolean> _localUserHasChangeSpeedRight = Unmodified("move.global.speed.right.local.user").comment("Whether the current local user has the right to change the global speed in-game (despite of the names listed in \"move.global.config.right.user.names\"");
    public final Property<Float> _perspectiveFadeFactor = PositiveFactor("move.perspective.fade.factor").values(0.5F, 0.1F, 1F).comment("Fading speed factor between the different perspectives (>= 0.1, <= 1, set to '1' to switch off)").book("Viewpoint perspective", "Below you find the options to manipulate the viewpoint perspective");
    public final Property<Float> _perspectiveRunFactor = Float("move.perspective.run.factor").key("move.run.perspective.factor", _pre_sm_2_1).defaults(1F).comment("Standard sprinting perspective (set to '0' to switch off)");
    public final Property<Float> _perspectiveSprintFactor = Float("move.perspective.sprint.factor").key("move.sprint.perspective.factor", _pre_sm_2_1).defaults(1.5F).comment("Smart on ground sprinting perspective (set to '0' to switch off)");
    public final Property<Float> _angleJumpDoubleClickTicks = Positive("move.jump.angle.double.click.ticks").singular().up(3F, 2F).comment("The maximum number of ticks between two clicks to trigger a side or back jump (>= 2)").book("User interface", "Below you find the options to manipulate Smart Moving's user interface");
    public final Property<Boolean> _wallJumpDoubleClick = Unmodified("move.jump.wall.double.click").singular().comment("Whether wall jumping should be triggered by single or double clicking (and then press and holding) the jump button").section();
    public final Property<Float> _wallJumpDoubleClickTicks = Positive("move.jump.wall.double.click.ticks").singular().up(3F, 2F).comment("The maximum number of ticks between two clicks to trigger a wall jump (>= 2, depends on \"move.jump.wall.double.click\")");
    public final Property<Boolean> _climbJumpBackHeadOnGrab = Unmodified("move.jump.climb.back.head.on.grab").singular().comment("Whether pressing or not pressing the grab button while climb jumping back results in a head jump").section();
    public final Property<Boolean> _displayExhaustionBar = Unmodified("move.gui.exhaustion.bar").singular().comment("Whether to display the exhaustion bar in the game overlay").section();
    public final Property<Boolean> _displayJumpChargeBar = Unmodified("move.gui.jump.charge.bar").singular().comment("Whether to display the jump charge bar in the game overlay");
    public final Property<Boolean> _sneakToggle = Modified("move.sneak.toggle").comment("To switch on/off sneak toggling").section();
    public final Property<Boolean> _crawlToggle = Modified("move.crawl.toggle").comment("To switch on/off crawl toggling");
    public final Property<Boolean> _flyCloseToGround = Modified("move.fly.ground.close").comment("To switch on/off flying close to the ground").section();
    public final Property<Boolean> _flyWhileOnGround = Modified("move.fly.ground.collide").depends(this._flyCloseToGround).comment("To switch on/off flying while colliding with the grond (Relevant only if \"move.fly.ground.close\" is true)");
    public final Property<Boolean> _flyControlVertical = Unmodified("move.fly.control.vertical").comment("Whether flying control also depends on where the player looks vertically.").section();
    public final Property<Boolean> _diveControlVertical = Unmodified("move.dive.control.vertical").comment("Whether diving control also depends on where the player looks vertically.");
    private final Property<Integer> _old_toggleKeyCode = Integer("move.toggle.key", _pre_sm_1_7).singular().defaults(67);
    private final Property<String> _defaultConfigToggleKeyName = String("move.config.toggle.default.key.name").key("move.toggle.key.name", _pre_sm_3_2).singular().defaults("F9").source(this._old_toggleKeyCode.toKeyName(), _pre_sm_1_7).singular().comment("Key name to toggle Smart Moving features in-game (default: \"F9\")").section();
    private final Property<String> _defaultGrabKeyName = String("move.grab.default.key.name").singular().defaults("LCONTROL").singular().comment("Default key name to \"grab\" (default: \"LCONTROL\")");
    private final Property<String> _speedIncreaseKeyName = String("move.speed.increase.default.key.name").key("move.speed.increase.key.name", _pre_sm_3_2).singular().defaults("O").singular().comment("Key name to increase the moving speed ingame (default: \"O\")");
    private final Property<String> _speedDecreaseKeyName = String("move.speed.decrease.default.key.name").key("move.speed.decrease.key.name", _pre_sm_3_2).singular().defaults("I").singular().comment("Key name to decrease the moving speed ingame (default: \"I\")");
    public final Property<Integer> _defaultConfigToggleKeyCode = this._defaultConfigToggleKeyName.toKeyCode(67);
    public final Property<Integer> _defaultGrabKeyCode = this._defaultGrabKeyName.toKeyCode(29);
    public final Property<Integer> _defaultSpeedIncreaseKeyCode = this._speedIncreaseKeyName.toKeyCode(24);
    public final Property<Integer> _defaultSpeedDecreaseKeyCode = this._speedDecreaseKeyName.toKeyCode(23);
    public final Property<Boolean> _speedChat = Unmodified("move.speed.chat").singular().comment("To switch on/off speed messages via chat system").section();
    public final Property<Boolean> _speedChatInit = Unmodified("move.speed.chat.init").depends(this._speedChat).singular().comment("To switch on/off the intial speed message when starting a game (Relevant only if \"move.speed.chat\" is not false)");
    public final Property<Boolean> _speedChatServer = Unmodified("move.config.chat.server").depends(this._speedChat).singular().comment("To switch on/off the server speed change message when joining a multiplayer game (Relevant only if \"move.speed.chat\" is not false)");
    public KeyBinding keyBindGrab;
    public KeyBinding keyBindConfigToggle;
    public KeyBinding keyBindSpeedIncrease;
    public KeyBinding keyBindSpeedDecrease;
    public static final File optionsPath = Loader.instance().getConfigDir();

    public Options()
    {
        this.loadFromOptionsFile(optionsPath);
        this.saveToOptionsFile(optionsPath);

        this.keyBindGrab = new KeyBinding("key.climb", this._defaultGrabKeyCode.getValue(), "key.categories.gameplay");
        this.keyBindConfigToggle = new KeyBinding("key.config.toggle", this._defaultConfigToggleKeyCode.getValue(), "key.categories.smartmoving");
        this.keyBindSpeedIncrease = new KeyBinding("key.speed.increase", this._defaultSpeedIncreaseKeyCode.getValue(), "key.categories.smartmoving");
        this.keyBindSpeedDecrease = new KeyBinding("key.speed.decrease", this._defaultSpeedDecreaseKeyCode.getValue(), "key.categories.smartmoving");
    }

    public boolean isSneakToggleEnabled()
    {
        return this._sneakToggle.getValue() && this.enabled;
    }

    public boolean isCrawlToggleEnabled()
    {
        return this._crawlToggle.getValue() && this.enabled;
    }

    public int angleJumpDoubleClickTicks()
    {
        return (int) Math.ceil(this._angleJumpDoubleClickTicks.getValue());
    }

    public int wallJumpDoubleClickTicks()
    {
        return (int) Math.ceil(this._wallJumpDoubleClickTicks.getValue());
    }

    @Override
    public void toggle()
    {
        super.toggle();

        Property<String> defaultKey;
        switch (this.gameType) {
            default:
            case Survival:
                defaultKey = this._survivalDefaultConfigKey;
                break;
            case Creative:
                defaultKey = this._creativeDefaultConfigKey;
                break;
            case Adventure:
                defaultKey = this._adventureDefaultConfigKey;
                break;
        }

        if (defaultKey != null) {
            String currentKey = this.getCurrentKey();
            defaultKey.setValue(currentKey);
            this.saveToOptionsFile(optionsPath);
        }
    }

    @Override
    public void changeSpeed(int difference)
    {
        super.changeSpeed(difference);
        this.writeClientSpeedMessageToChat(false);
        this.saveToOptionsFile(optionsPath);
    }

    public void writeClientSpeedMessageToChat(boolean everyone)
    {
        if (!this._speedChat.getValue()) {
            return;
        }

        Object percent = ContextBase.Config.getSpeedPercent();
        String prefix = getClientEveryonePrefix("move.speed.chat.client", everyone);
        String key = prefix + (percent.equals(Config.defaultSpeedPercent) ? "reset" : "change");
        writeToChat(key, SmartMovingMod.ID.hashCode(), percent);
    }

    private static String getClientEveryonePrefix(String base, boolean everyone)
    {
        String result = base + ".";
        if (everyone) {
            result += "everyone.";
        }
        return result;
    }

    public void writeServerSpeedMessageToChat(String username, boolean everyone)
    {
        if (Minecraft.getMinecraft().player.getGameProfile().getName().equals(username)) {
            this.writeClientSpeedMessageToChat(everyone);
        } else if (this._speedChatServer.getValue()) {
            Object percent = ContextBase.Config.getSpeedPercent();
            String prefix = "move.speed.chat.server.";
            if (percent.equals(Config.defaultSpeedPercent)) {
                writeToChat(prefix + "reset", SmartMovingMod.ID.hashCode(), username);
            } else {
                writeToChat(prefix + "change", SmartMovingMod.ID.hashCode(), percent, username);
            }
        }
    }

    public static void writeNoRightsToChangeSpeedMessageToChat(boolean isRemote)
    {
        writeToChat("move.speed.chat.server.illegal." + (isRemote ? "remote" : "local"), SmartMovingMod.ID.hashCode());
    }

    private static void writeToChat(String key, int id, Object... parameters)
    {
        String message = parameters == null || parameters.length == 0
                ? I18n.format(key)
                : I18n.format(key, parameters);

        GuiNewChat guiChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();

        // bugfix: also delete multi-lined chat messages
        if (id != 0) {
            for (int i = 0; i < 5; i++) {
                guiChat.deleteChatLine(id);
            }
        }

        guiChat.printChatMessageWithOptionalDeletion(new TextComponentString(message), id);
    }

    public void resetForNewGame()
    {
        this.gameType = -1;
    }

    public void initializeForGameIfNeccessary()
    {
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        if (controller == null) {
            return;
        }

        int currentGameType = ((GameType) Reflect.GetField(_currentGameType, controller)).getID();
        if (currentGameType == this.gameType) {
            return;
        }

        this.gameType = currentGameType;

        String[] keys = null;
        String defaultKey;

        switch (this.gameType) {
            case Survival:
                keys = this._survivalConfigKeys.getValue();
                defaultKey = this._survivalDefaultConfigKey.getValue();
                break;
            case Creative:
                keys = this._creativeConfigKeys.getValue();
                defaultKey = this._creativeDefaultConfigKey.getValue();
                break;
            case Adventure:
                keys = this._adventureConfigKeys.getValue();
                defaultKey = this._adventureDefaultConfigKey.getValue();
                break;
            default:
                defaultKey = "";
        }

        this.setKeys(keys);
        if (!defaultKey.isEmpty()) {
            this.setCurrentKey(defaultKey);
        }

        if (this.isUserSpeedEnabled() && this._speedChatInit.getValue()) {
            Object speedPercent = this.getSpeedPercent();

            if (!speedPercent.equals(defaultSpeedPercent)) {
                writeToChat("move.speed.chat.client.init", SmartMovingMod.ID.hashCode(), speedPercent);
            }
        }
    }

    public int gameType;
    private static final Field _currentGameType = Reflect.GetField(PlayerControllerMP.class, SmartMovingMod.PlayerControllerMP_currentGameType);
}