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
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LangYa
 * @since 2024/8/30 22:38
 */
public class ElementManager implements Wrapper {
    public static List<Element> elements = new ArrayList<>();
    public static boolean dragging;

    private static void addDynamicTextDisplayElement(String name, String cnName, Supplier<String> textSupplier) {
        elements.add(new TextDisplay(name, cnName) {
            @Override
            public String getText() {
                return textSupplier.get();
            }
        });
    }

    public static void init() {
        elements.add(new KeyStore());
        elements.add(new PotionDisplay());
        elements.add(new ArmorDisplay());
        elements.add(new SmokePlayerInfo());
        elements.add(new ReachDisplay());
        elements.add(PackDisplay.INSTANCE);
        elements.add(new ServerDisplay());

        // 动态文本显示（使用 Supplier 直接绑定计算逻辑）
        addDynamicTextDisplayElement("ComboDisplay", "Combo显示", () -> String.format("%s Combo", AttackHandler.currentCombo));
        addDynamicTextDisplayElement("SpeedDisplay", "速度显示", () -> String.format("%s m/s", MoveUtil.getBPS()));
        addDynamicTextDisplayElement("CPSDisplay", "CPS显示", () -> String.format("CPS: %s | %s", CpsUtil.getLeftCps(), CpsUtil.getRightCps()));
        addDynamicTextDisplayElement("TimeDisplay", "时间显示", () -> String.format("时间: %s", TimeUtil.getCurrentTimeStringHHMM()));
        addDynamicTextDisplayElement("PingDisplay", "延迟显示", () -> String.format("%s ms", PingUtil.getPing()));
        addDynamicTextDisplayElement("FPSDisplay", "FPS显示", () -> String.format("%s FPS", Minecraft.getDebugFPS()));
        addDynamicTextDisplayElement("SprintDisplay", "疾跑显示", () -> String.format("Sprint: %s", Sprint.isEnable ? "Toggled" : "Vanilla"));

        elements.forEach(Element::init);
    }
}
