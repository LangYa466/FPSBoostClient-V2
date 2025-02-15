package net.fpsboost.screen.newClickGUI;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LangYa466
 * @since 2/15/2025
 */
@Getter
@Setter
public class DragWindow {
    private int x, y, width, height;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    public DragWindow(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * 判断鼠标是否在窗口标题区域（高度 headerHeight 内）
     */
    public boolean isHoveringHeader(int mouseX, int mouseY, int headerHeight) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight;
    }

    /**
     * 开始拖拽，并记录初始偏移量
     */
    public void startDrag(int mouseX, int mouseY) {
        dragging = true;
        dragOffsetX = mouseX - x;
        dragOffsetY = mouseY - y;
    }

    /**
     * 拖拽中，更新窗口位置
     */
    public void drag(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }
    }

    /**
     * 停止拖拽
     */
    public void stopDrag() {
        dragging = false;
    }
}
