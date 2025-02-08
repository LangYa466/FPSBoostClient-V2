package net.fpsboost.module.impl;

import net.fpsboost.handler.MessageHandler;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.mousefix.RawInputMod;
import net.fpsboost.value.impl.BooleanValue;

/**
 * @author LangYa
 * @since 2024/11/26 15:12
 */
public class ClientSettings extends Module {
    public static final ClientSettings INSTANCE = new ClientSettings();
    public static boolean isChinese = false;

    public ClientSettings() {
        super("ClientSettings", "客户端设置");
    }
    public BooleanValue cnMode = new BooleanValue("中文模式","Chinese mode",true) {
        @Override
        public void setValue(Boolean value) {
            // 重写方法 懒得用那个俩内存地址访问的低效率方式了
            ClientSettings.isChinese = value;
            // 没重新Sort的话有些傻逼就叫ClickGUI没排序
            ModuleManager.sortModules();
            super.setValue(value);
        }
    };
    public static final BooleanValue gc = new BooleanValue("世界内存优化(fps更高 加载时间增加)","Memory Fix",false);
    public final BooleanValue mouseFix = new BooleanValue("鼠标手感优化(会导致和原版灵敏度不一样)","Mouse Fix",false);
    private final BooleanValue mathFix = new BooleanValue("Riven算法优化Math 建议开启","Riven Math",true) {
        @Override
        public void setValue(Boolean value) {
            clientMathFix = value;
            super.setValue(value);
        }
    };
    private final RawInputMod rawInputMod = new RawInputMod();
    public static boolean clientMathFix = false;

    @Override
    public void onUpdate() {
        if (mouseFix.getValue()) {
            if (!rawInputMod.isStart) rawInputMod.start();
        } else {
            if (rawInputMod.isStart) rawInputMod.stop();
        }
        super.onUpdate();
    }

    @Override
    public void onDisable() {
        enable = true;
        MessageHandler.addMessage("不准关闭这个功能啊笨蛋", MessageHandler.MessageType.Warning);
        super.onDisable();
    }
}
