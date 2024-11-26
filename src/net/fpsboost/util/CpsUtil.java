package net.fpsboost.util;

import com.google.common.collect.Lists;

import java.util.List;

public class CpsUtil {
    private static final List<MouseButton> leftCounter = Lists.newArrayList();
    private static final List<MouseButton> rightCounter = Lists.newArrayList();

    public static int getLeftCps() {
        update();
        return leftCounter.size();
    }

    public static int getRightCps() {
        update();
        return rightCounter.size();
    }

    public static void update() {
        leftCounter.removeIf(MouseButton::canBeReduced);
        rightCounter.removeIf(MouseButton::canBeReduced);
    }

    public static void update(int type) {
        switch (type) {
            case 0:
                leftCounter.add(new MouseButton(System.currentTimeMillis()));
                break;
            case 1:
                rightCounter.add(new MouseButton(System.currentTimeMillis()));
                break;
        }
    }
}