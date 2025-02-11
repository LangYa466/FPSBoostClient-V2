package net.fpsboost.screen;

import net.fpsboost.module.impl.RectMode;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * @author LangYa466
 * @since 2025/1/12
 */
public class GuiRectMode extends GuiScreen {

    @Override
    public void initGui() {
        int buttonX = this.width / 2 - 100;
        // buttonList.add(new GuiButton(1,buttonX,height / 2 - 20,80,20,"无瑕疵圆角(优化一般)"));
        buttonList.add(new GuiButton(2, buttonX, height / 2 + 5, "直角"));
        buttonList.add(new GuiButton(3, buttonX, height / 2 + 30, "有瑕疵圆角(优化好)"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawClientBackground();
        FontManager.client(70).drawCenteredStringWithShadow("全局绘制矩形模式", width / 2F, height / 4F, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected ResourceLocation actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                //  RectMode.mode = 1;
                break;
            case 2:
                RectMode.mode = 0;
                break;
            case 3:
                RectMode.mode = 2;
                break;
        }
        mc.displayGuiScreen(new GuiMainMenu());
        return super.actionPerformed(button);
    }
}
