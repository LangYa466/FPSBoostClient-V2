package net.fpsboost.screen.clickgui.utils;

public class Opacity {

    private double opacity;
    private long lastMS;

    public Opacity(float opacity) {
        this.opacity = opacity;
        lastMS = System.currentTimeMillis();
    }


    public void interpolate(int targetOpacity, int speed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - lastMS;
        lastMS = currentMS;
        opacity = AnimationUtil.calculateCompensation(targetOpacity, opacity, delta, speed);
    }

    public double getOpacity() {
        return this.opacity;
    }

}
