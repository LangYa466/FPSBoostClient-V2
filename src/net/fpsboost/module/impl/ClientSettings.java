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
        super("ClientSettings", "客户端设置");
    }
    public BooleanValue cnMode = new BooleanValue("中文模式","Chinese mode",true);
    public final BooleanValue gc = new BooleanValue("世界内存优化(fps更高 加载时间增加)","Memory Fix",false);
    public final BooleanValue mouseFix = new BooleanValue("鼠标手感优化(会导致和原版灵敏度不一样)","Mouse Fix",false);

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
