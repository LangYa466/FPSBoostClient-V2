package net.fpsboost.screen.clickGui.component.components.sub;

import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import net.fpsboost.screen.clickGui.component.Component;
import net.fpsboost.screen.clickGui.component.components.Button;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {

    private boolean hovered;

    private final NumberValue val;
    private final Button parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;

    private double renderWidth;

    public Slider(NumberValue value, Button button, int offset) {
        this.val = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, hovered ? 0xFF555555 : 0xFF444444);
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
        GL11.glPushMatrix();

        RenderUtil.drawStringWithShadow(this.val.name + ": " + this.val.value, (parent.parent.getX() + 8) , (parent.parent.getY() + offset + 2)  + 1, -1);

        GL11.glPopMatrix();
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();

        double diff = Math.min(88, Math.max(0, mouseX - this.x));

        double min = val.minValue;
        double max = val.maxValue;

        renderWidth = (88) * (val.value - min) / (max - min);

        if (dragging) {
            if (diff == 0) {
                val.value = (val.minValue);
            } else {
                val.value = ((double) Math.round(((diff / 88) * (max - min) + min) * (1.0D / val.incValue)) / (1.0D / val.incValue));
            }
        }
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    public boolean isMouseOnButtonD(int x, int y) {
		return x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12;
	}

    public boolean isMouseOnButtonI(int x, int y) {
		return x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12;
	}
}
