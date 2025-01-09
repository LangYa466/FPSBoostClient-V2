package net.fpsboost.screen.clickgui.utils;

public class Translate {

    private double x;
    private double y;
    private long lastMS;

    public Translate(double x, double y) {
        this.x = x;
        this.y = y;
        this.lastMS = System.currentTimeMillis();
    }

    public void interpolate(double targetX, double d, float smoothing) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        int deltaX = (int) (Math.abs(targetX - this.x) * smoothing);
        int deltaY = (int) (Math.abs(d - this.y) * smoothing);
        this.x = AnimationUtil.calculateCompensation(targetX, this.x, delta, deltaX);
        this.y = AnimationUtil.calculateCompensation(d, this.y, delta, deltaY);
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}
