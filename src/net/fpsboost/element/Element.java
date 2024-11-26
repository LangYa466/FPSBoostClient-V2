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
    public float size = 1F;

    public Element(String name,String cnName) {
        super(name,cnName,"","");
    }

    public void onHover(int mouseX, int mouseY) {
        if (dragging) {
            xPos = mouseX - startX;
            yPos = mouseY - startY;
        }

        RenderUtil.drawOutline(xPos - 2, yPos - 2, this.width , this.height + 3, -1);
    }

    public void init() { }

    public void onDraw() {}

    public void onClick(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(xPos, yPos, width, height, mouseX, mouseY);
        ElementManager.dragging = canDrag;
        if (button == 0 && canDrag) {
            dragging = true;
            startX = mouseX - xPos;
            startY = mouseY - yPos;
        }
    }
    
    public void onRelease(int button) {
        if (button == 0) dragging = false;
    }
}