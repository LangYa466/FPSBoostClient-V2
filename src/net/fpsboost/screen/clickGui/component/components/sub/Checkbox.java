package net.fpsboost.screen.clickGui.component.components.sub;

import net.fpsboost.screen.clickGui.ClickGuiScreen;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.BooleanValue;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import net.fpsboost.screen.clickGui.component.Component;
import net.fpsboost.screen.clickGui.component.components.Button;

public class Checkbox extends Component {

    private boolean hovered;
    private final BooleanValue op;
    private final Button parent;
    private int offset;
    private int x;
    private int y;

    public Checkbox(BooleanValue option, Button button, int offset) {
        this.op = option;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
        GL11.glPushMatrix();
        
        RenderUtil.drawStringWithShadow(this.op.name, (parent.parent.getX() + 10 + 4)  + 5, (parent.parent.getY() + offset + 2)  , -1);
        GL11.glPopMatrix();
        Gui.drawRect(parent.parent.getX() + 3 + 4, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 4, parent.parent.getY() + offset + 9, 0xFF999999);
        if (this.op.value)
            Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, ClickGuiScreen.color);
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.op.value = (!this.op.value);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
		return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
	}
}
