package us.cubk.clickgui.component;

import lombok.Getter;
import lombok.Setter;
import net.fpsboost.element.ElementManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.font.FontManager;
import us.cubk.clickgui.ClickGuiScreen;
import us.cubk.clickgui.component.components.Button;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Frame {

    @Getter
    public ArrayList<Component> components;
    @Getter
    @Setter
    private boolean open;
    @Getter
    private final int width;
    @Setter
    @Getter
    private int y;
    @Setter
    @Getter
    private int x;
    private final int barHeight;
    private boolean isDragging;
    public int dragX;
    public int dragY;
    public int i;

    public Frame(int i) {
        this.components = new ArrayList<>();
        this.width = 88;
        this.x = 5;
        this.y = 5;
        this.barHeight = 13;
        this.dragX = 0;
        this.open = true;
        this.isDragging = false;
        int tY = this.barHeight;
        this.i = i;
        if (i == 1) {
            for (Module module : ElementManager.elements) {
                Button modButton = new Button(module, this, tY);
                this.components.add(modButton);
                tY += 12;
            }
        } else {
            for (Module module : ModuleManager.modules) {
                Button modButton = new Button(module, this, tY);
                this.components.add(modButton);
                tY += 12;
            }
        }
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public void renderFrame() {
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, ClickGuiScreen.color);
        GL11.glPushMatrix();
        String name;
        if (ClientSettings.INSTANCE.cnMode.getValue()){
             name = (i == 1) ? "视觉" : "功能";
        }else{
            name = (i == 1) ? "Visual" : "Function";
        }
        FontManager.hanYi(18).drawStringWithShadow(name, (this.x + 2)  + 5, (this.y + 2.5f) , 0xFFFFFFFF);
        FontManager.hanYi(18).drawStringWithShadow(this.open ? "-" : "+", (this.x + this.width - 10)  + 3, (this.y + 2.5f) , -1);
        GL11.glPopMatrix();
        if (this.open) {
            if (!this.components.isEmpty()) {
                for (Component component : components) {
                    component.renderComponent();
                }
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

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - dragX);
            this.setY(mouseY - dragY);
        }
    }

    public boolean isWithinHeader(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
	}
}
