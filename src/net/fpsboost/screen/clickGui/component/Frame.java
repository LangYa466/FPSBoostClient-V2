package net.fpsboost.screen.clickGui.component;

import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.screen.clickGui.component.components.Button;

import java.util.ArrayList;


public class Frame {

    public ArrayList<Component> components;
    private final int width;
    private int y;
    private int x;
    private final int barHeight;
    public int dragX;
    public int dragY;

    public Frame() {
        this.components = new ArrayList<>();
        this.width = 88;
        this.x = 5;
        this.y = 5;
        this.barHeight = 13;
        int tY = this.barHeight;
        for (Module mod : ModuleManager.modules) {
            Button modButton = new Button(mod, this, tY);
            this.components.add(modButton);
            tY += 12;
        }
        for (Module mod : ElementManager.elements) {
            Button modButton = new Button(mod, this, tY);
            this.components.add(modButton);
            tY += 12;
        }
    }

    public ArrayList<Component> getComponents() {
        return components;
    }


    public void renderFrame() {
        if (!this.components.isEmpty()) {
            for (Component component : components) {
                component.renderComponent();
            }
        }
    }

    public void refresh() {
        int off = this.barHeight;
        for (Component comp : components) {
            comp.setOff(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

}
