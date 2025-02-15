package net.fpsboost.screen.newClickGUI;

import lombok.Getter;
import net.fpsboost.util.HoveringUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.ColorValue;
import net.fpsboost.screen.clickgui.utils.HSBColor;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.awt.Color;

/**
 * @author LangYa466
 * @since 2/15/2025
 */
public class ColorPickerWindow {

    private DragWindow dragWindow;
    private ColorValue colorValue;
    private FontRenderer fontRenderer = FontManager.client();

    // 当前正在拖动的滑块索引：0-色相, 1-饱和度, 2-亮度, 3-透明度；-1表示无
    private int activeSlider = -1;

    // 标记窗口是否关闭
    @Getter
    private boolean closed = false;

    // 滑块参数
    private final int sliderBarHeight = 10;
    private final int sliderSpacing = 25;

    public ColorPickerWindow(ColorValue colorValue, int x, int y) {
        this.colorValue = colorValue;
        // 初始化窗口大小，例如宽度250，高度200
        dragWindow = new DragWindow(x, y, 250, 200);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // 拖动处理
        dragWindow.drag(mouseX, mouseY);

        int x = dragWindow.getX();
        int y = dragWindow.getY();
        int width = dragWindow.getWidth();
        int height = dragWindow.getHeight();

        // 绘制窗口背景
        GuiScreen.drawRect(x, y, x + width, y + height, 0xCC000000);

        // 绘制标题栏
        GuiScreen.drawRect(x, y, x + width, y + 20, 0xFF222222);
        fontRenderer.drawStringWithShadow("颜色选择器(如果颜色不对可以试试看提高亮度)", x + 5, y + 5, 0xFFFFFFFF);
        // 绘制关闭按钮 (X)
        String closeText = "X";
        int closeTextWidth = fontRenderer.getStringWidth(closeText);
        int closeX = x + width - closeTextWidth - 5;
        fontRenderer.drawStringWithShadow(closeText, closeX, y + 5,
                HoveringUtil.isHovering(closeX, y + 5, closeTextWidth, fontRenderer.getHeight(), mouseX, mouseY)
                        ? 0xFFFFFF00 : 0xFFFF0000);

        // 绘制预览区域
        int previewX = x + 10;
        int previewY = y + 30;
        int previewWidth = width - 20;
        int previewHeight = 30;
        int colorInt = colorValue.getValueC();
        GuiScreen.drawRect(previewX, previewY, previewX + previewWidth, previewY + previewHeight, colorInt);
        // 绘制预览区边框
        drawRectOutline(previewX, previewY, previewWidth, previewHeight, 0xFF000000);

        // 绘制4个滑块（均使用渐变显示）
        int sliderStartY = previewY + previewHeight + 10;
        drawSlider("色相", colorValue.getValue().getHue(), x + 10, sliderStartY, width - 20, 0, mouseX, mouseY);
        drawSlider("饱和度", colorValue.getValue().getSaturation(), x + 10, sliderStartY + sliderSpacing, width - 20, 1, mouseX, mouseY);
        drawSlider("亮度", colorValue.getValue().getBrightness(), x + 10, sliderStartY + sliderSpacing * 2, width - 20, 2, mouseX, mouseY);
        float alphaNormalized = colorValue.getValue().getAlpha() / 255f;
        drawSlider("透明度", alphaNormalized, x + 10, sliderStartY + sliderSpacing * 3, width - 20, 3, mouseX, mouseY);
    }

    /**
     * 绘制滑块：
     * 1. 显示标签；
     * 2. 绘制一个渐变色的滑条；
     * 3. 在当前选中位置处绘制一个标记（2px宽的黑色竖线）。
     */
    private void drawSlider(String label, float value, int startX, int startY, int sliderTotalWidth, int sliderId, int mouseX, int mouseY) {
        // 绘制标签
        fontRenderer.drawStringWithShadow(label, startX, startY, 0xFFFFFFFF);
        int labelWidth = fontRenderer.getStringWidth(label);
        int barX = startX + labelWidth + 5;
        int barWidth = sliderTotalWidth - labelWidth - 5;

        // 绘制渐变色滑条（每个像素绘制一条竖线）
        for (int i = 0; i < barWidth; i++) {
            float normalized = (float) i / (barWidth - 1);
            int color = getSliderColor(sliderId, normalized);
            GuiScreen.drawRect(barX + i, startY, barX + i + 1, startY + sliderBarHeight, color);
        }

        // 绘制滑块边框
        drawRectOutline(barX, startY, barWidth, sliderBarHeight, 0xFF000000);

        // 绘制滑块标记，表示当前选择位置
        int markerX = barX + (int)(value * barWidth);
        GuiScreen.drawRect(markerX - 1, startY, markerX + 1, startY + sliderBarHeight, 0xFF000000);
    }

    private void drawRectOutline(int x, int y, int width, int height, int color) {
        // 上边框
        GuiScreen.drawRect(x, y, x + width, y + 1, color);
        // 下边框
        GuiScreen.drawRect(x, y + height - 1, x + width, y + height, color);
        // 左边框
        GuiScreen.drawRect(x, y, x + 1, y + height, color);
        // 右边框
        GuiScreen.drawRect(x + width - 1, y, x + width, y + height, color);
    }

    /**
     * 鼠标点击事件，返回true表示事件已被处理。
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int x = dragWindow.getX();
        int y = dragWindow.getY();
        int width = dragWindow.getWidth();

        // 判断是否点击了关闭按钮(X)
        String closeText = "X";
        int closeTextWidth = fontRenderer.getStringWidth(closeText);
        int closeX = x + width - closeTextWidth - 5;
        if (HoveringUtil.isHovering(closeX, y + 5, closeTextWidth, fontRenderer.getHeight(), mouseX, mouseY)) {
            closed = true;
            return true;
        }

        // 点击标题栏则开始拖拽
        if (HoveringUtil.isHovering(x, y, width, 20, mouseX, mouseY)) {
            dragWindow.startDrag(mouseX, mouseY);
        }

        // 检测是否点击了滑块区域
        int previewHeight = 30;
        int sliderStartY = y + 30 + previewHeight + 10;
        for (int i = 0; i < 4; i++) {
            int sliderY = sliderStartY + sliderSpacing * i;
            int barX = x + fontRenderer.getStringWidth(getLabel(i)) + 5 + 10;
            int barWidth = dragWindow.getWidth() - 20 - fontRenderer.getStringWidth(getLabel(i)) - 5;
            if (HoveringUtil.isHovering(barX, sliderY, barWidth, sliderBarHeight, mouseX, mouseY)) {
                activeSlider = i;
                updateSliderValue(mouseX, barX, barWidth, i);
                return true;
            }
        }
        return false;
    }

    /**
     * 鼠标拖动事件，用于更新当前正在拖动的滑块的值
     */
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (activeSlider != -1) {
            int x = dragWindow.getX();
            int width = dragWindow.getWidth();
            int barX = x + fontRenderer.getStringWidth(getLabel(activeSlider)) + 5 + 10;
            int barWidth = width - 20 - fontRenderer.getStringWidth(getLabel(activeSlider)) - 5;
            updateSliderValue(mouseX, barX, barWidth, activeSlider);
        }
    }

    /**
     * 鼠标释放时结束滑块拖动
     */
    public void mouseReleased(int mouseX, int mouseY, int state) {
        activeSlider = -1;
        dragWindow.stopDrag();
    }

    /**
     * 根据鼠标X坐标更新对应滑块的值，并更新ColorValue的HSBColor
     */
    private void updateSliderValue(int mouseX, int barX, int barWidth, int sliderId) {
        float normalized = (float)(mouseX - barX) / barWidth;
        if (normalized < 0) normalized = 0;
        if (normalized > 1) normalized = 1;
        HSBColor hsb = colorValue.getValue();
        switch (sliderId) {
            case 0:
                hsb.setHue(normalized);
                break;
            case 1:
                hsb.setSaturation(normalized);
                break;
            case 2:
                hsb.setBrightness(normalized);
                break;
            case 3:
                hsb.setAlpha((int)(normalized * 255));
                break;
        }
    }

    private String getLabel(int sliderId) {
        switch (sliderId) {
            case 0: return "色相";
            case 1: return "饱和度";
            case 2: return "亮度";
            case 3: return "透明度";
            default: return "";
        }
    }

    /**
     * 用于在主界面中转发鼠标拖动事件
     */
    public void drag(int mouseX, int mouseY) {
        dragWindow.drag(mouseX, mouseY);
    }

    /**
     * 根据sliderId和normalized值返回对应的颜色
     */
    private int getSliderColor(int sliderId, float normalized) {
        HSBColor hsb = colorValue.getValue();
        switch (sliderId) {
            case 0:
                // 色相滑块：显示从红色开始，遍历全部色相（全饱和、全亮度）
                return HSBtoRGB(normalized, 1.0f, 1.0f, 255);
            case 1:
                // 饱和度滑块：当前色相，亮度不变，从无饱和到全饱和
                return HSBtoRGB(hsb.getHue(), normalized, hsb.getBrightness(), 255);
            case 2:
                // 亮度滑块：当前色相、饱和度，从暗到亮
                return HSBtoRGB(hsb.getHue(), hsb.getSaturation(), normalized, 255);
            case 3:
                // 透明度滑块：当前色相、饱和度、亮度，透明度从0到255变化
                return HSBtoRGB(hsb.getHue(), hsb.getSaturation(), hsb.getBrightness(), (int)(normalized * 255));
            default:
                return 0xFFFFFFFF;
        }
    }

    /**
     * 将HSB颜色转换为ARGB格式的颜色值
     */
    private int HSBtoRGB(float hue, float saturation, float brightness, int alpha) {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return ((alpha & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
