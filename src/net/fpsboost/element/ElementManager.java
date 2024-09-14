package net.fpsboost.element;

import net.fpsboost.element.impl.*;

import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/8/30 22:38
 */
public class ElementManager {
    public static ArrayList<Element> elements = new ArrayList<>();
    public static boolean dragging;

    public static void init() {
        elements.add(new FPSDisplay());
        elements.add(new TimeDisplay());
        elements.add(new ComboDisplay());
        elements.add(new KeyStore());
        elements.add(new PotionDisplay());

        elements.forEach(Element::init);
    }

}
