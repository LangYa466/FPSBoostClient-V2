package net.fpsboost.screen.clickgui.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;

public class ClickableString extends ClickEntity {
    FontRenderer font;
    @Setter
    @Getter
    String text;
    @Setter
    @Getter
    int color;

    public ClickableString(FontRenderer font, String text, double x, double y, int color, Runnable click,
                           Runnable hold, Runnable focus, Runnable release, Runnable onBlur) {
        super(x, y, 0, 0, MouseBounds.CallType.Expand, click, hold, focus, release, onBlur);
        this.font = font;
        this.text = text;
        this.color = color;
    }

    public void draw(boolean shadow) {
        int height = font.getHeight();
        int lenth = font.getHeight();
        if (shadow) {
            font.drawStringWithShadow(text, this.getX(), this.getY(), color);
        } else {
            font.drawString(text, this.getX(), this.getY(), color);
        }

        super.setX1(lenth);
        super.setY1(height);

        super.tick();
    }

    @Override
    public double getX() {
        return super.getX();
    }

    @Override
    public void setX(double x) {
        super.setX(x);
    }

    @Override
    public double getY() {
        return super.getY();
    }

    @Override
    public void setY(double y) {
        super.setY(y);
    }
}
