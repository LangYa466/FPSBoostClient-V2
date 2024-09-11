package net.fpsboost.screen.clickGui;

import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import net.fpsboost.screen.clickGui.component.Frame;
import net.fpsboost.screen.clickGui.component.Component;

public class ClickGuiScreen extends GuiScreen {

    public static ArrayList<Frame> frames;
    public static int color = new Color(62, 175, 255).getRGB();

    public ClickGuiScreen(){
        frames = new ArrayList<>();
        Frame frame = new Frame();
        frames.add(frame);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        for (Frame frame : frames) {
            frame.renderFrame();
            for (Component comp : frame.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for (Frame frame : frames) {
            for (Component component : frame.getComponents()) {
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        for (Frame frame : frames) {
            if (keyCode != 1) {
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
            if (!frame.getComponents().isEmpty()) {
                for (Component component : frame.getComponents()) {
                    component.mouseReleased(mouseX, mouseY, state);
                }
            }
        }
    }

}
