package net.fpsboost.element;

import net.fpsboost.element.impl.DrawString;
import net.fpsboost.element.impl.KeyStore;

import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/8/30 22:38
 */
public class ElementManager {
    public static ArrayList<Element> elements = new ArrayList<>();

    public static void init() {
        elements.add(new DrawString());
        elements.add(new KeyStore());

        elements.forEach(Element::init);
    }

}
