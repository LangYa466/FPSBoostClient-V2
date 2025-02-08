package net.fpsboost.element;

import net.fpsboost.element.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LangYa
 * @since 2024/8/30 22:38
 */
public class ElementManager {
    public static List<Element> elements = new ArrayList<>();
    public static boolean dragging;

    public static void init() {
        elements.add(new FPSDisplay());
        elements.add(new TimeDisplay());
        elements.add(new ComboDisplay());
        elements.add(new KeyStore());
        elements.add(new PotionDisplay());
        elements.add(new ArmorDisplay());
        elements.add(new PingDisplay());
        elements.add(new CPSDisplay());
        elements.add(new SmokePlayerInfo());
        elements.add(new ReachDisplay());
        elements.add(new SprintDisplay());
        elements.add(PackDisplay.INSTANCE);
        elements.add(new ServerDisplay());
        elements.add(new SpeedDisplay());

        elements.forEach(Element::init);
    }
}
