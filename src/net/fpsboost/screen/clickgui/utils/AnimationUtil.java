package net.fpsboost.screen.clickgui.utils;

public class AnimationUtil {

    public static double calculateCompensation(double target, double current, long delta, int speed) {
        if (delta < 1L) delta = 1L;

        final double difference = current - target;
        final double smoothing = speed * (delta / 16F);

        if (difference > speed)
            current = Math.max(current - (Math.max(smoothing, .2F)), target);
        else if (difference < -speed)
            current = Math.min(current + (Math.max(smoothing, .2F)), target);
        else current = target;

        return current;
    }

}