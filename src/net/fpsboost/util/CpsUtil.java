package net.fpsboost.util;

import com.google.common.collect.Lists;

import java.util.List;

public enum CpsUtil {
    ;
    private static final List<MouseButton> leftCounter = Lists.newArrayList();
    private static final List<MouseButton> rightCounter = Lists.newArrayList();

    public static int getLeftCps() {
        CpsUtil.update();
        return CpsUtil.leftCounter.size();
    }

    public static int getRightCps() {
        CpsUtil.update();
        return CpsUtil.rightCounter.size();
    }

    public static void update() {
        CpsUtil.leftCounter.removeIf(MouseButton::canBeReduced);
        CpsUtil.rightCounter.removeIf(MouseButton::canBeReduced);
    }

    public static void update(final int type) {
        switch (type) {
            case 0:
                CpsUtil.leftCounter.add(new MouseButton(System.currentTimeMillis()));
                break;
            case 1:
                CpsUtil.rightCounter.add(new MouseButton(System.currentTimeMillis()));
                break;
        }
    }
}