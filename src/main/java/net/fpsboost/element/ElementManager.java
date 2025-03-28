package net.fpsboost.element;

import net.fpsboost.Wrapper;
import net.fpsboost.element.impl.*;
import net.fpsboost.element.impl.text.TextDisplay;
import net.fpsboost.handler.AttackHandler;
import net.fpsboost.module.impl.Sprint;
import net.fpsboost.screen.musicPlayer.LyricDisplay;
import net.fpsboost.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author LangYa
 * @since 2024/8/30 22:38
 */
public class ElementManager implements Wrapper {
    public static final List<Element> elements = new CopyOnWriteArrayList<>();
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
                new ServerDisplay(),
                new LyricDisplay()
        );

        // 批量添加动态文本显示元素
        addDynamicTextDisplayElement("ComboDisplay", "Combo显示", () -> AttackHandler.currentCombo + " Combo");
        addDynamicTextDisplayElement("SpeedDisplay", "速度显示", () -> MoveUtil.getBPS() + " m/s");
        addDynamicTextDisplayElement("CPSDisplay", "CPS显示", () -> "CPS: " + CpsUtil.getLeftCps() + " | " + CpsUtil.getRightCps());
        addDynamicTextDisplayElement("TimeDisplay", "时间显示", () -> "时间: " + TimeUtil.getCurrentTimeStringHHMM());
        addDynamicTextDisplayElement("PingDisplay", "延迟显示", () -> PingUtil.getPing() + " ms");
        addDynamicTextDisplayElement("FPSDisplay", "FPS显示", () -> Minecraft.getDebugFPS() + " FPS");
        addDynamicTextDisplayElement("SprintDisplay", "疾跑显示", () -> "Sprint: " + (Sprint.isEnable ? "Toggled" : "Vanilla"));
        addDynamicTextDisplayElement("PosYDisplay", "Y轴显示", () -> "Y: " + (int) mc.thePlayer.posY);
        addDynamicTextDisplayElement("PotionCountDisplay", "药水数量显示", () -> {
            int amount = PotionUtil.getPotionsFromInv(Potion.heal);
            return amount + " " + (amount <= 1 ? "pot" : "pots");
        });

        // 初始化所有元素
        elements.forEach(Element::init);
    }
}
