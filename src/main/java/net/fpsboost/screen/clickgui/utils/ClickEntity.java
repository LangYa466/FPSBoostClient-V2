package net.fpsboost.screen.clickgui.utils;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;

public class ClickEntity extends RenderEntity {

    private int clicktick;
    private int clicktick_right;
    private int clicktick_middle;
    private final MouseBounds mousebounds;
    @Setter
    @Getter
    private Runnable click;
    @Setter
    @Getter
    private Runnable hold;
    @Setter
    @Getter
    private Runnable focus;
    @Setter
    @Getter
    private Runnable onBlur;
    @Setter
    @Getter
    private Runnable release;

    @Setter
    @Getter
    private boolean inArea;

    public ClickEntity(double x, double y, double x1, double y1, MouseBounds.CallType type, Runnable click, Runnable hold,
                       Runnable focus, Runnable release, Runnable onBlur) {
        this.clicktick = this.isLeftDown() ? 999 : 0;
        this.mousebounds = new MouseBounds(getMouseX(), getMouseY(), x, y, x1, y1, type);
        this.click = click;
        this.hold = hold;
        this.focus = focus;
        this.release = release;
        this.onBlur = onBlur;
        this.inArea = false;
    }

    public void tick() {
        this.mousebounds.setMouseX(this.getMouseX());
        this.mousebounds.setMouseY(this.getMouseY());

        if (mousebounds.isWhthinBounds()) {
            this.inArea = true;
            this.focus.run();
            if (clicktick > 0)
                this.hold.run();
            if (clicktick != 0)
                this.release.run();
            if (clicktick == 1) {
                this.click.run();
            }

        } else {
            this.inArea = false;
            this.onBlur.run();
        }
        if (this.isLeftDown()) {
            this.clicktick++;
        } else {
            this.clicktick = 0;
        }
        if (this.isRightDown()) {
            this.clicktick_right++;
        } else {
            this.clicktick_right = 0;
        }
        if (this.isMiddleDown()) {
            this.clicktick_middle++;
        } else {
            this.clicktick_middle = 0;
        }
    }

    public boolean isLeftDown() {
        return Mouse.isButtonDown(0);
    }

    public boolean isRightDown() {
        return Mouse.isButtonDown(1);
    }

    public boolean isMiddleDown() {
        return Mouse.isButtonDown(2);
    }

    public boolean isLeftPressed() {
        return clicktick == 1;
    }

    public boolean isRightPressed() {
        return clicktick_right == 1;
    }

    public boolean isMiddlePressed() {
        return clicktick_middle == 1;
    }

    public double getMouseX() {

        return (Mouse.getX() * this.getScaledWidth() / (double) this.getMinecraft().displayWidth);
    }

    public double getMouseY() {
        return (this.getScaledHeight()
                - Mouse.getY() * this.getScaledHeight() / (double) this.getMinecraft().displayHeight - 1);
    }

    public void setMouseX(double mouseX) {
        mousebounds.setMouseX(mouseX);
    }

    public void setMouseY(double mouseY) {
        mousebounds.setMouseY(mouseY);
    }

    public double getX() {
        return mousebounds.getX();
    }

    public void setX(double x) {
        mousebounds.setX(x);
    }

    public double getY() {
        return mousebounds.getY();
    }

    public void setY(double y) {
        mousebounds.setY(y);
    }

    public double getX1() {
        return mousebounds.getX1();
    }

    public void setX1(double x1) {
        mousebounds.setX1(x1);
    }

    public double getY1() {
        return mousebounds.getY1();
    }

    public void setY1(double y1) {
        mousebounds.setY1(y1);
    }

    //	public CallType getType() {
//		return mousebounds.getType();
//	}
//
//
//	public void setType(CallType type) {
//		mousebounds.setType(type);
//	}

}
