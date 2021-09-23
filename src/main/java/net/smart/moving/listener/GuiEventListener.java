package net.smart.moving.listener;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smart.moving.ConfigHelper;
import net.smart.moving.ControllerSelf;
import net.smart.moving.SmartMovingConfig;
import net.smart.moving.SmartMovingMod;
import net.smart.moving.playerapi.Factory;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class GuiEventListener
{
    private static final ResourceLocation ICONS = new ResourceLocation(SmartMovingMod.ID, "textures/gui/icons.png");

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.playerController == null)
        {
            return;
        }

        ControllerSelf moving = (ControllerSelf) Factory.getInstance().getPlayerInstance(minecraft.player);
        if (moving == null)
        {
            return;
        }

        if (SmartMovingConfig.USER_INTERFACE.guiExhaustionBar || SmartMovingConfig.USER_INTERFACE.guiJumpChargeBar || !minecraft.playerController.gameIsSurvivalOrAdventure())
        {
            ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();

            float maxExhaustion = ConfigHelper.getMaxExhaustion();
            float exhaustion = Math.min(moving.exhaustion, maxExhaustion);
            boolean drawExhaustion = exhaustion > 0 && exhaustion <= maxExhaustion;

            float maxStillJumpCharge = SmartMovingConfig.CHARGED_JUMPING.maximum;
            float stillJumpCharge = Math.min(moving.jumpCharge, maxStillJumpCharge);

            float maxRunJumpCharge = SmartMovingConfig.HEAD_JUMPING.maximum;
            float runJumpCharge = Math.min(moving.headJumpCharge, maxRunJumpCharge);

            boolean drawJumpCharge = stillJumpCharge > 0 || runJumpCharge > 0;
            float maxJumpCharge = stillJumpCharge > runJumpCharge ? maxStillJumpCharge : maxRunJumpCharge;
            float jumpCharge = Math.max(stillJumpCharge, runJumpCharge);

            if (drawExhaustion || drawJumpCharge)
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
                minecraft.getTextureManager().bindTexture(ICONS);
            }

            if (drawExhaustion)
            {
                float maxExhaustionForAction = Math.min(moving.maxExhaustionForAction, maxExhaustion);
                float maxExhaustionToStartAction = Math.min(moving.maxExhaustionToStartAction, maxExhaustion);

                float fitness = maxExhaustion - exhaustion;
                float minFitnessForAction = Float.isNaN(maxExhaustionForAction) ? 0 : maxExhaustion - maxExhaustionForAction;
                float minFitnessToStartAction = Float.isNaN(maxExhaustionToStartAction) ? 0 : maxExhaustion - maxExhaustionToStartAction;

                float maxFitnessDrawn = Math.max(Math.max(minFitnessToStartAction, fitness), minFitnessForAction);

                int halves = (int) Math.floor(maxFitnessDrawn / maxExhaustion * 21F);
                int fulls = halves / 2;
                int half = halves % 2;

                int fitnessHalves = (int) Math.floor(fitness / maxExhaustion * 21F);
                int fitnessFulls = fitnessHalves / 2;
                int fitnessHalf = fitnessHalves % 2;

                int minFitnessForActionHalves = (int) Math.floor(minFitnessForAction / maxExhaustion * 21F);
                int minFitnessForActionFulls = minFitnessForActionHalves / 2;
                int minFitnessForActionHalf = minFitnessForActionHalves % 2;

                int minFitnessToStartActionHalves = (int) Math.floor(minFitnessToStartAction / maxExhaustion * 21F);
                int minFitnessToStartActionFulls = minFitnessToStartActionHalves / 2;

                int yOffset = height - 39 - 10 - (minecraft.player.isInsideOfMaterial(Material.WATER) ? 10 : 0);
                for (int i = 0; i < Math.min(fulls + half, 10); i++)
                {
                    int xOffset = (width / 2 + 90) - (i + 1) * 8;
                    if (i < fitnessFulls)
                    {
                        if (i < minFitnessForActionFulls)
                        {
                            drawIcon(minecraft, 2, 2, xOffset, yOffset);
                        }
                        else if (i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
                        {
                            drawIcon(minecraft, 3, 2, xOffset, yOffset);
                        }
                        else
                        {
                            drawIcon(minecraft, 0, 0, xOffset, yOffset);
                        }
                    }
                    else if (i == fitnessFulls && fitnessHalf > 0)
                    {
                        if (i < minFitnessForActionFulls)
                        {
                            drawIcon(minecraft, 1, 2, xOffset, yOffset);
                        }
                        else if (i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
                        {
                            if (i < minFitnessToStartActionFulls)
                            {
                                drawIcon(minecraft, 3, 1, xOffset, yOffset);
                            }
                            else
                            {
                                drawIcon(minecraft, 4, 2, xOffset, yOffset);
                            }
                        }
                        else if (i < minFitnessToStartActionFulls)
                        {
                            drawIcon(minecraft, 1, 1, xOffset, yOffset);
                        }
                        else
                        {
                            drawIcon(minecraft, 1, 0, xOffset, yOffset);
                        }
                    }
                    else
                    {
                        if (i < minFitnessForActionFulls)
                        {
                            drawIcon(minecraft, 0, 2, xOffset, yOffset);
                        }
                        else if (i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
                        {
                            if (i < minFitnessToStartActionFulls)
                            {
                                drawIcon(minecraft, 2, 1, xOffset, yOffset);
                            }
                            else
                            {
                                drawIcon(minecraft, 5, 2, xOffset, yOffset);
                            }
                        }
                        else if (i < minFitnessToStartActionFulls)
                        {
                            drawIcon(minecraft, 0, 1, xOffset, yOffset);
                        }
                        else
                        {
                            drawIcon(minecraft, 4, 1, xOffset, yOffset);
                        }
                    }
                }
            }

            if (drawJumpCharge)
            {
                boolean max = jumpCharge == maxJumpCharge;
                int fulls = max ? 10 : (int) Math.ceil(((jumpCharge - 2) * 10D) / maxJumpCharge);
                int half = max ? 0 : (int) Math.ceil((jumpCharge * 10D) / maxJumpCharge) - fulls;

                int yOffset = height - 39 - 10 - (minecraft.player.getTotalArmorValue() > 0 ? 10 : 0);
                for (int i = 0; i < fulls + half; i++)
                {
                    int xOffset = (width / 2 - 91) + i * 8;
                    drawIcon(minecraft, i < fulls ? 2 : 3, 0, xOffset, yOffset);
                }
            }

            if (drawExhaustion || drawJumpCharge)
            {
                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                minecraft.getTextureManager().bindTexture(Gui.ICONS);
            }
        }
    }

    private static void drawIcon(Minecraft minecraft, int x, int y, int xOffset, int yOffset)
    {
        minecraft.ingameGUI.drawTexturedModalRect(xOffset, yOffset, x * 9, y * 9, 9, 9);
    }
}