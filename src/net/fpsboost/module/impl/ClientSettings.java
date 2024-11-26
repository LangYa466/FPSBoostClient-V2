package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.util.mousefix.RawInputMod;
import net.fpsboost.value.impl.BooleanValue;

/**
 * @author LangYa
 * @since 2024/11/26 15:12
 */
public class ClientSettings extends Module {
    public static final ClientSettings INSTANCE = new ClientSettings();
    public ClientSettings() {
        super("ClientSettings", "客户端优化设置");
    }
    public final BooleanValue gc = new BooleanValue("世界内存优化",true);
    public final BooleanValue mouseFix = new BooleanValue("鼠标手感优化",true);

    private final RawInputMod rawInputMod = new RawInputMod();

    @Override
    public void onUpdate() {
        if (mouseFix.getValue()) {
            if (!rawInputMod.isStart) rawInputMod.start();
        } else {
            if (rawInputMod.isStart) rawInputMod.stop();
        }
        super.onUpdate();
    }
}
