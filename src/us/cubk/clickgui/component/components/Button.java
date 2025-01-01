package us.cubk.clickgui.component.components;

import net.fpsboost.module.Module;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.Value;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.ModeValue;
import net.fpsboost.value.impl.NumberValue;
import us.cubk.clickgui.ClickGuiScreen;
import us.cubk.clickgui.component.Component;
import us.cubk.clickgui.component.Frame;
import us.cubk.clickgui.component.components.sub.*;
import us.cubk.clickgui.component.components.sub.Checkbox;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;

public class Button extends Component {

    public Module module;
    public Frame parent;
    public int offset;
    private boolean isHovered;
    private final ArrayList<Component> subcomponents;
    public boolean open;

    public Button(Module mod, Frame parent, int offset) {
        this.module = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<>();
        this.open = false;
        int opY = offset + HEIGHT;
        if (!module.values.isEmpty()) {
            for (Value<?> value : module.values) {
                if (value.isHide) return;
                if (value instanceof ModeValue) {
                    this.subcomponents.add(new ModeButton(this, (ModeValue) value, opY));
                    opY += HEIGHT;
                }
                if (value instanceof NumberValue) {
                    Slider slider = new Slider((NumberValue) value, this, opY);
                    this.subcomponents.add(slider);
                    opY += HEIGHT;
                }
                if (value instanceof BooleanValue) {
                    Checkbox check = new Checkbox((BooleanValue) value, this, opY);
                    this.subcomponents.add(check);
                    opY += HEIGHT;
                }
                if (value instanceof ColorValue) {
                    for (int i = 0; i < 4; i++) {
                        if (i == 3 && !((ColorValue)value).getHasAlpha()) return;
                        Slider slider = new Slider((ColorValue) value,i, this, opY);
                        this.subcomponents.add(slider);
                        opY += HEIGHT;
                    }
                }
            }
        }
        this.subcomponents.add(new Keybind(this, opY));
       // this.subcomponents.add(new VisibleButton(this, mod, opY));
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + HEIGHT;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += HEIGHT;
        }
    }

    @Override
    public void renderComponent() {
        if (isHovered) {
            Gui.drawRect(parent.getX(), this.parent.getY() + this.offset,
                    parent.getX() + parent.getWidth(), this.parent.getY() + HEIGHT + this.offset,
                    new Color(0xFF222222).getRGB());
        }
        
        GL11.glPushMatrix();
        String name;
        if (ClientSettings.INSTANCE.cnMode.getValue()) {
            name = module.cnName;
        } else {
            name = module.name;
        }
        FontManager.hanYi(18).drawStringWithShadow(name, (parent.getX() + 2) , (parent.getY() + offset + 2)  , module.enable ? ClickGuiScreen.color : 0xFFFFFFFF);
        if (this.subcomponents.size() > 2)
            FontManager.hanYi(18).drawStringWithShadow(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10) , (parent.getY() + offset + 2)  , 0xFFFFFFFF);
        GL11.glPopMatrix();
        if (this.open) {
            if (!this.subcomponents.isEmpty()) {
                for (Component comp : this.subcomponents) {
                    comp.renderComponent();
                }
//                Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + HEIGHT, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * HEIGHT), ClickGuiScreen.color);
            }
        }
    }

    public static final int HEIGHT = 12;

    @Override
    public int getHeight() {
        if (this.open) {
            return (HEIGHT * (this.subcomponents.size() + 1));
        }
        return HEIGHT;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            module.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
		return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + HEIGHT + this.offset;
	}
}
