package net.fpsboost.module.impl;

import net.fpsboost.module.Module;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

// 该类定义了自定义准星模块的功能
public class SmokeCrosshair extends Module {
    double curCord;
    static double cord;
    int curDynamicValues = 0;
    int DynamicValues = 0;
    public static ColorValue color = new ColorValue("颜色","color",-1);
    public static BooleanValue dot = new BooleanValue("点", "Dot", true);
    public static NumberValue size = new NumberValue("大小", "size", 0.0, 50.0, 0.0, 0.1);
    public static NumberValue gap = new NumberValue("间隔", "gap", 0.0, 50.0, 0.0, 0.1);
    public static NumberValue weight = new NumberValue("粗细", "weight", 0.0, 50.0, 0.0, 0.1);
    public static NumberValue dynamicGap = new NumberValue("动态间隔", "dynamicGap", 0.0, 50.0, 0.0, 0.1);
    public static NumberValue dynamicSpeed = new NumberValue("动态速度", "dynamicSpeed", 0.1, 10.0, 0.5, 0.1);
    public static NumberValue dynamicValue = new NumberValue("动态值", "dynamicValue", 1, 25, 5, 1);
    public static BooleanValue dynamicClick = new BooleanValue("动态点击", "dynamicClick", true);
    public static BooleanValue dynamic = new BooleanValue("动态", "dynamic", true);

    // 构造函数，初始化模块的属性
    public SmokeCrosshair() {
        super("Crosshair","自定义准星");
        cnDescription = "我抄袭Smoke客户端的";
        description = "Skid form SmokeClient";
    }

    // 渲染2D图形的方法
    @Override
    public void onRender2D() {
        if (mc.gameSettings.showDebugInfo) return;

        this.resetAnimations(aerodynamicGapValue());
        this.updateAnimations();
        this.updateDynamicValue();

        if (!isMoving()) {
            this.resetDynamicValue(dynamicValue.getValue());
        }
        if (isMoving()) {
            this.setDynamicValue(dynamicValue.getValue().intValue());
        }

        if (mc.currentScreen == null) {
            ScaledResolution sr = new ScaledResolution(mc);
            int Color2 = color.getColor();

            if (dot.getValue()) {
                drawCenteredDot(sr, Color2);
            }

            drawRectangles(sr, Color2);
        }

        super.onRender2D();
    }

    // 绘制中心的点
    private void drawCenteredDot(ScaledResolution sr, int color) {
        Gui.drawRect(
                (int) (sr.getScaledWidth() / 2 - 0.5),
                (int) (sr.getScaledHeight() / 2 - 0.5),
                (int) (sr.getScaledWidth() / 2 + 0.5),
                (int) (sr.getScaledHeight() / 2 + 0.5),
                color
        );
    }

    // 绘制矩形的方法
    private void drawRectangles(ScaledResolution sr, int color) {
        double centerX = sr.getScaledWidth() / 2;
        double centerY = sr.getScaledHeight() / 2;

        double calculatedGap = 0.5 + this.curCord + gap.getValue() + this.getDynamicValue();

        // 绘制矩形
        Gui.drawRect(
                (int) (centerX - size.getValue() - calculatedGap),
                (int) (centerY - weight.getValue() - 0.5),
                (int) (centerX + size.getValue() - (-0.5 + calculatedGap)),
                (int) (centerY + weight.getValue() + 0.5),
                color
        );
        Gui.drawRect(
                (int) (centerX + size.getValue() + calculatedGap),
                (int) (centerY - 0.5 - weight.getValue()),
                (int) (centerX - size.getValue() + (-0.5 + calculatedGap)),
                (int) (centerY + 0.5 + weight.getValue()),
                color
        );
        Gui.drawRect(
                (int) (centerX - 0.5 - weight.getValue()),
                (int) (centerY + size.getValue() + calculatedGap),
                (int) (centerX + 0.5 + weight.getValue()),
                (int) (centerY - size.getValue() + (-0.5 + calculatedGap)),
                color
        );
        Gui.drawRect(
                (int) (centerX - 0.5 - weight.getValue()),
                (int) (centerY - size.getValue() - calculatedGap),
                (int) (centerX + 0.5 + weight.getValue()),
                (int) (centerY + size.getValue() - (-0.5 + calculatedGap)),
                color
        );
    }

    // 判断玩家是否在移动
    public boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    // 处理鼠标事件的方法
    public static void onMouse(int buttonID,boolean buttonState) {
        if (buttonID == 0 && buttonState) {
            setAnimations(aerodynamicGapValue());
        }
        if (buttonID == 1 && buttonState) {
            setAnimations(aerodynamicGapValue());
        }
    }

    // 获取动态值的方法
    public int getDynamicValue() {
        if (dynamic.getValue()) {
            return this.curDynamicValues;
        }
        return 0;
    }

    // 更新动态值的方法
    public void updateDynamicValue() {
        if (!isMoving() && this.curDynamicValues > this.DynamicValues) {
            this.curDynamicValues -= this.aerodynamicSpeedValue();
        }
        if (this.curDynamicValues < this.DynamicValues) {
            this.curDynamicValues += (int) this.aerodynamicSpeedValue();
        }
        if (this.DynamicValues - this.curDynamicValues < this.aerodynamicSpeedValue() && this.DynamicValues - this.curDynamicValues > -this.aerodynamicSpeedValue()) {
            this.curDynamicValues = this.DynamicValues;
        }
    }

    // 设置动态值的方法
    public void setDynamicValue(int DynamicValues) {
        this.DynamicValues = 0;
        if (dynamicClick.getValue()) {
            this.DynamicValues = DynamicValues;
        }
    }

    // 重置动态值的方法
    public void resetDynamicValue(double DynamicValues) {
        if (this.curDynamicValues == DynamicValues) {
            this.DynamicValues = 0;
        }
    }

    // 获取动态间隔值的方法
    public static double aerodynamicGapValue() {
        return dynamicGap.getValue();
    }

    // 获取动态速度值的方法
    public int aerodynamicSpeedValue() {
        return (int) Math.round(dynamicSpeed.getValue());
    }

    // 更新动画状态的方法
    public void updateAnimations() {
        if (this.curCord > cord) {
            this.curCord -= this.aerodynamicSpeedValue();
        }
        if (this.curCord < cord) {
            this.curCord += this.aerodynamicSpeedValue();
        }
        if (cord - this.curCord < this.aerodynamicSpeedValue() && cord - this.curCord > -this.aerodynamicSpeedValue()) {
            this.curCord = cord;
        }
    }

    // 设置动画的方法
    public static void setAnimations(double coord) {
        SmokeCrosshair.cord = 0.0;
        if (dynamicClick.getValue()) {
            SmokeCrosshair.cord = coord;
        }
    }

    // 重置动画的方法
    public void resetAnimations(double coord) {
        if (this.curCord == coord) {
            SmokeCrosshair.cord = 0.0;
        }
    }
}
