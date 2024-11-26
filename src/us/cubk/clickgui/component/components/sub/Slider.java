package us.cubk.clickgui.component.components.sub;

import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.Value;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.NumberValue;
import us.cubk.clickgui.component.Component;
import us.cubk.clickgui.component.components.Button;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {

    private boolean hovered;

    private final Value val;
    private final Button parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;
    private int index;

    private double renderWidth;

    public Slider(NumberValue value, Button button, int offset) {
        this.val = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    public Slider(ColorValue value,int i, Button button, int offset) {
        this.val = value;
        this.parent = button;
        this.index = i;
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

        if (val instanceof NumberValue) {
            FontManager.hanYi(18).drawStringWithShadow(this.val.getName() + ": " + this.val.getValue(), (parent.parent.getX() + 8), (parent.parent.getY() + offset + 2) + 1, -1);
        } else if (val instanceof ColorValue) {
            if (index == 0) {
                FontManager.hanYi(18).drawStringWithShadow(String.format("%s-红- %s",val.getName(),((ColorValue) val).getRed()), (parent.parent.getX() + 8), (parent.parent.getY() + offset + 2) + 1, -1);
            }
            if (index == 1) {
                FontManager.hanYi(18).drawStringWithShadow(String.format("%s-绿- %s",val.getName(),((ColorValue) val).getGreen()), (parent.parent.getX() + 8), (parent.parent.getY() + offset + 2) + 1, -1);
            }
            if (index == 2) {
                FontManager.hanYi(18).drawStringWithShadow(String.format("%s-蓝- %s",val.getName(),((ColorValue) val).getBlue()), (parent.parent.getX() + 8), (parent.parent.getY() + offset + 2) + 1, -1);
            }
            if (index == 3) {
                FontManager.hanYi(18).drawStringWithShadow(String.format("%s-透明- %s",val.getName(),((ColorValue) val).getAlpha()), (parent.parent.getX() + 8), (parent.parent.getY() + offset + 2) + 1, -1);
            }
        }
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

        double min = 0;
        double max = 255;
        double inc = 10;
        if (val instanceof NumberValue) {
            NumberValue val1 = (NumberValue) val;
            min = val1.minValue;
            max = val1.maxValue;
            inc = val1.incValue;
            renderWidth = (88) * (val1.getValue() - min) / (max - min);
        } else if (val instanceof ColorValue) {
            ColorValue val1 = (ColorValue) val;
            if (index == 0) {
                renderWidth = (88) * (val1.getRed() - min) / (max - min);
            }
            if (index == 1) {
                renderWidth = (88) * (val1.getGreen() - min) / (max - min);
            }
            if (index == 2) {
                renderWidth = (88) * (val1.getBlue() - min) / (max - min);
            }
            if (index == 3) {
                renderWidth = (88) * (val1.getAlpha() - min) / (max - min);
            }
        }

        if (dragging) {
            double a = ((diff / 88) * (max - min) + min) * (1.0D / inc);

            if (val instanceof NumberValue) {
                if (diff == 0) {
                    val.setValue(min);
                }
                else {
                    double newValue = (double) Math.round(a) / (1.0D / inc);
                    val.setValue(newValue);
                }
            } else if (val instanceof ColorValue) {
                ColorValue val1 = (ColorValue) val;
                if (diff == 0) {
                    int min1 = (int) min;
                    if (index == 0) {
                        val1.setRed(min1);
                    }
                    if (index == 1) {
                        val1.setGreen(min1);
                    }
                    if (index == 2) {
                        val1.setBlue(min1);
                    }
                    if (index == 3) {
                        val1.setAlpha(min1);
                    }
                } else {
                    int newValue = (int) (Math.round(a) / (1.0D / inc));
                    if (index == 0) {
                        val1.setRed(newValue);
                    }
                    if (index == 1) {
                        val1.setGreen(newValue);
                    }
                    if (index == 2) {
                        val1.setBlue(newValue);
                    }
                    if (index == 3) {
                        val1.setAlpha(newValue);
                    }
                }
            }
        }
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
