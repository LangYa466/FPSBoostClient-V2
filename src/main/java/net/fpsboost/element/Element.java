package net.fpsboost.element;

import net.fpsboost.Wrapper;
import net.fpsboost.module.Module;
import net.fpsboost.util.HoveringUtil;
import net.fpsboost.util.RenderUtil;

public class Element extends Module implements Wrapper {
    public int xPos;
    public int yPos;

    public int startX, startY;
    public boolean dragging;

    public int width, height;
    public boolean isHovering;
    public float scale = 1;

    public Element(String name, String cnName) {
        super(name, cnName, "", "");
    }

    public void onHover(int mouseX, int mouseY) {
        if (dragging) {
            // 拖动时更新位置
            xPos = mouseX - startX;
            yPos = mouseY - startY;

            // 对齐处理
            alignWithOtherElements();
        }

        isHovering = HoveringUtil.isHovering(xPos, yPos, width * scale, height * scale, mouseX, mouseY);

        // 绘制元素的边框
        RenderUtil.drawOutline(xPos - 2, yPos - 2, (int) (this.width * scale), (int) ((this.height + 3) * scale), -1);
    }

    // 对齐其他元素
    private void alignWithOtherElements() {
        // 对齐阈值
        int alignThreshold = 5;

        for (Element otherElement : ElementManager.elements) {
            // 跳过自己和未启用的元素
            if (otherElement == this || !otherElement.dragging) continue;

            // 对齐 X 轴
            if (Math.abs(xPos - otherElement.xPos) <= alignThreshold) {
                xPos = otherElement.xPos;
            } else if (Math.abs(xPos + width - otherElement.xPos) <= alignThreshold) {
                xPos = otherElement.xPos - width;
            }

            // 对齐 Y 轴
            if (Math.abs(yPos - otherElement.yPos) <= alignThreshold) {
                yPos = otherElement.yPos;
            } else if (Math.abs(yPos + height - otherElement.yPos) <= alignThreshold) {
                yPos = otherElement.yPos - height;
            }
        }
    }

    public void init() {
    }

    public void onDraw() {
    }

    public void onClick(int mouseX, int mouseY, int button) {
        isHovering = HoveringUtil.isHovering(xPos, yPos, width * scale, height * scale, mouseX, mouseY);
        ElementManager.dragging = isHovering;
        if (button == 0 && isHovering) {
            dragging = true;
            startX = mouseX - xPos;
            startY = mouseY - yPos;
        }
    }

    public void onRelease(int button) {
        if (button == 0) dragging = false;
    }

    public void setScale(float scale) {

        if (scale > 5.0 || scale < 0.2) {

            if (scale > 5.0) {
                this.scale = 5.0F;
            }

            if (scale < 0.2) {
                this.scale = 0.2F;
            }

            return;
        }

        this.scale = scale;
    }
}
