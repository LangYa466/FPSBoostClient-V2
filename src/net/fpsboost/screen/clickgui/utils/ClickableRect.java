package net.fpsboost.screen.clickgui.utils;

public class ClickableRect extends ClickEntity {
    private final Rect rect;

    public ClickableRect(double x, double y, double x1, double y1, int color,
                         Rect.RenderType type, Runnable click, Runnable hold, Runnable focus, Runnable release, Runnable onBlur) {
        super(x, y, x1, y1, getCallType(type), click, hold, focus, release,onBlur);
        this.rect = new Rect(x, y, x1, y1, color, type);
    }

    public void draw() {
        rect.draw();
        super.tick();
    }

    public double getX() {
        return rect.getX();
    }

    public void setX(double x) {
        super.setX(x);
        rect.setX(x);
    }

    public double getY() {
        return rect.getY();
    }

    public void setY(double y) {
        super.setY(y);
        rect.setY(y);
    }

    public double getWidth() {
        return rect.getWidth();
    }

    public void setWidth(double width) {
        super.setX1(width);
        rect.setWidth(width);
    }

    public double getHeight() {
        return rect.getHeight();
    }

    public void setHeight(double height) {
        super.setY1(height);
        rect.setHeight(height);
    }

    public int getColor() {
        return rect.getColor();
    }

    public void setColor(int color) {
        rect.setColor(color);
    }

    public Rect.RenderType getType() {
        return rect.getType();
    }

    public void setType(Rect.RenderType type) {
        rect.setType(type);
    }

    private static MouseBounds.CallType getCallType(Rect.RenderType type) {
        switch (type) {
            case Position:
                return MouseBounds.CallType.Position;
            case Expand:
            default:
                return MouseBounds.CallType.Expand;
        }
    }

}
