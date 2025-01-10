package net.fpsboost.util;

import com.google.common.collect.Lists;

import java.util.List;

public class CpsUtil {
    private static final List<MouseButton> leftCounter = Lists.newArrayList();
    private static final List<MouseButton> rightCounter = Lists.newArrayList();

    public static int getLeftCps() {
        return (int) leftCounter.stream().filter(MouseButton::canBeReduced).count();
    }

    public static int getRightCps() {
        return (int) rightCounter.stream().filter(MouseButton::canBeReduced).count();
    }

    public static void update() {
        // 直接在 update 中调用 removeIf 来移除过期的按钮
        leftCounter.removeIf(MouseButton::canBeReduced);
        rightCounter.removeIf(MouseButton::canBeReduced);
    }

    public static void update(int type) {
        long currentTime = System.currentTimeMillis();
        switch (type) {
            case 0:
                leftCounter.add(new MouseButton(currentTime));
                break;
            case 1:
                rightCounter.add(new MouseButton(currentTime));
                break;
        }
    }
}
