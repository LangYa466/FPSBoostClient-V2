package net.fpsboost.module.impl;

import net.fpsboost.module.Module;

/**
 * @author LangYa
 * @since 2024/9/1 19:11
 */
public class TestModule extends Module {
    public TestModule() {
        super("test", "test啊", "213");
    }

    @Override
    public void onEnable() {
        System.out.println("Enable");
    }

    @Override
    public void onDisable() {
        System.out.println("Disable");
    }
}
