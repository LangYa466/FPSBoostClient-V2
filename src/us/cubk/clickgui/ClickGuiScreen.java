package us.cubk.clickgui;

import net.fpsboost.config.ConfigManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.ColorUtil;
import net.fpsboost.util.font.FontManager;
import us.cubk.clickgui.component.Component;
import us.cubk.clickgui.component.Frame;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGuiScreen extends GuiScreen {

    public static final ClickGuiScreen INSTANCE = new ClickGuiScreen();
    public static ArrayList<Frame> frames;
    public static int color = new Color(62, 175, 255).getRGB();

    public void init(){
        frames = new ArrayList<>();
        int frameX = 5;
        for (int i = 0; i < 2; i++) {
            Frame frame = new Frame(i);
            frame.setX(frameX);
            frames.add(frame);
            frameX += frame.getWidth() + 1;
        }
    }

    @Override
    public void onGuiClosed() {
        ConfigManager.saveConfig("Module.json");
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // this.drawDefaultBackground();
        for (Frame frame : frames) {
            frame.renderFrame();
            frame.updatePosition(mouseX, mouseY);
            for (Component comp : frame.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
        String displayText = !ClientSettings.INSTANCE.cnMode.getValue()?"Press T or / to drag UI":"按 T 或 / 即可拖动UI";
        FontManager.hanYi().drawStringWithShadow(displayText,width - FontManager.hanYi().getStringWidth(displayText) * 1.5F,height - 18, ColorUtil.rainbow(3, (int) partialTicks).getRGB());
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for (Frame frame : frames) {
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
                frame.setDrag(true);
                frame.dragX = mouseX - frame.getX();
                frame.dragY = mouseY - frame.getY();
            }
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                frame.setOpen(!frame.isOpen());
            }
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        for (Frame frame : frames) {
            if (frame.isOpen() && keyCode != 1) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Frame frame : frames) {
            frame.setDrag(false);
        }
        for (Frame frame : frames) {
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseReleased(mouseX, mouseY, state);
                    }
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
