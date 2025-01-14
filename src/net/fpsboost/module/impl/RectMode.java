package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.ModeValue;
import net.fpsboost.value.impl.NumberValue;

/**
 * @author LangYa466
 * @since 2025/1/11
 */
public class RectMode extends Module {
    public RectMode() {
        super("RectMode", "矩形模式");
        enable = true;
    }

    public static float radius = 0F;
    public static int mode = 0;
    private boolean init;

    public static final ModeValue modeValue = new ModeValue("模式","Mode","无瑕疵圆角(优化一般)","无瑕疵圆角(优化一般)","有瑕疵圆角(优化好)","直角") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            switch (value) {
                case "直角":
                    mode = 0;
                    break;
                case "无瑕疵圆角(优化一般)":
                    mode = 1;
                    break;
                case "有瑕疵圆角(优化好)":
                    mode = 2;
                    break;
            }
        }
    };
    public static final NumberValue radiusValue = new NumberValue("圆角值(越高越圆)","Radius",2,10,1,1) {
        @Override
        public void setValue(Double value) {
            radius = value.intValue();
            super.setValue(value);
        }
    };

    @Override
    public void onUpdate() {
        if (!init) {
            radius = radiusValue.getValue().intValue();
            init = true;
        }
        super.onUpdate();
    }

    @Override
    public void onDisable() {
        enable = true;
        super.onDisable();
    }
}
