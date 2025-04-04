package net.fpsboost.screen;

import net.fpsboost.Client;
import net.fpsboost.util.ColorUtil;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.animation.Animation;
import net.fpsboost.util.animation.Direction;
import net.fpsboost.util.animation.impl.SmoothStepAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/6
 **/
public class SplashScreen extends GuiScreen {
    private final GuiScreen current;
    private final GuiScreen target;
    private Animation alphaAnim;

    private final Minecraft mc = Minecraft.getMinecraft();

    public SplashScreen(GuiScreen current, GuiScreen target) {
        this.current = current;
        this.target = target;
    }

    @Override
    public void initGui() {
        if (alphaAnim == null) {
            alphaAnim = new SmoothStepAnimation(250, 1d);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (alphaAnim.finished(Direction.FORWARDS)) {
            current.onGuiClosed();

            if (target == Client.guimainMenu) {
                mc.gameSettings.showDebugInfo = false;
                mc.ingameGUI.getChatGUI().clearChatMessages();
            }

            mc.setIngameNotInFocus();
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            target.setWorldAndResolution(mc, i, j);
            mc.skipRenderWorld = false;

            alphaAnim.changeDirection();
        } else if (alphaAnim.getDirection() == Direction.FORWARDS) {
            current.drawScreen(mouseX, mouseY, partialTicks);
        }

        if (alphaAnim.finished(Direction.BACKWARDS)) {
            mc.displayScreen(target);
        } else if (alphaAnim.getDirection() == Direction.BACKWARDS) {
            target.drawScreen(mouseX, mouseY, partialTicks);
        }

        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), ColorUtil.applyOpacity(Color.BLACK, alphaAnim.getOutput().floatValue()).getRGB());
    }
}
