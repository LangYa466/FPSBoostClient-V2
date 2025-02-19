package net.fpsboost.element;

import net.fpsboost.Wrapper;
import net.fpsboost.element.impl.*;
import net.fpsboost.element.impl.text.TextDisplay;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.module.impl.Sprint;
import net.fpsboost.util.CpsUtil;
import net.fpsboost.util.MoveUtil;
import net.fpsboost.util.PingUtil;
import net.fpsboost.util.TimeUtil;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LangYa
 * @since 2024/8/30 22:38
 */
public class ElementManager implements Wrapper {
    public static final List<Element> elements = new ArrayList<>(16); // 预分配容量
    public static volatile boolean dragging; // 保证多线程可见性

    private static void addDynamicTextDisplayElement(String name, String cnName, Supplier<String> textSupplier) {
        elements.add(new TextDisplay(name, cnName, textSupplier));
    }

    public static void init() {
        // 批量添加静态元素
        Collections.addAll(elements,
                new KeyStore(),
                new PotionDisplay(),
                new ArmorDisplay(),
                new SmokePlayerInfo(),
                new ReachDisplay(),
                PackDisplay.INSTANCE,
                new ServerDisplay()
        );

        // 批量添加动态文本显示元素
        addDynamicTextDisplayElement("ComboDisplay", "Combo显示", () -> AttackHandler.currentCombo + " Combo");
        addDynamicTextDisplayElement("SpeedDisplay", "速度显示", () -> MoveUtil.getBPS() + " m/s");
        addDynamicTextDisplayElement("CPSDisplay", "CPS显示", () -> "CPS: " + CpsUtil.getLeftCps() + " | " + CpsUtil.getRightCps());
        addDynamicTextDisplayElement("TimeDisplay", "时间显示", () -> "时间: " + TimeUtil.getCurrentTimeStringHHMM());
        addDynamicTextDisplayElement("PingDisplay", "延迟显示", () -> PingUtil.getPing() + " ms");
        addDynamicTextDisplayElement("FPSDisplay", "FPS显示", () -> Minecraft.getDebugFPS() + " FPS");
        addDynamicTextDisplayElement("SprintDisplay", "疾跑显示", () -> "Sprint: " + (Sprint.isEnable ? "Toggled" : "Vanilla"));

        // 初始化所有元素
        elements.forEach(Element::init);
    }
}
