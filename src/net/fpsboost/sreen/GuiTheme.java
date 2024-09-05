package net.fpsboost.sreen;

import net.fpsboost.util.ThemeUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

/**
 * @author LangYa
 * @since 2024/9/3 20:21
 */
public class GuiTheme extends GuiScreen {

    // idea显示Java代码的String的高亮提示颜色
    private final int textColor = new Color(106, 171, 115).getRGB();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(0);
        int x = this.width / 4;
        int y = this.height / 4;
        mc.fontRendererObj.drawStringWithShadow("您当前的主题为: " + ThemeUtil.themeName, x, y, textColor);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        int y = this.height / 4;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, y + 72 + 12, 98, 20, "亮白主题"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, y + 72 + 12, 98, 20, "暗黑主题"));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            Color tempColor = ThemeUtil.pressbgColor;
            Color tempColor2 = ThemeUtil.bgColor;
            ThemeUtil.bgColor = tempColor;
            ThemeUtil.pressbgColor = tempColor2;
            ThemeUtil.themeName = "亮白主题";
        } else {
            Color tempColor = ThemeUtil.pressbgColor;
            Color tempColor2 = ThemeUtil.bgColor;
            ThemeUtil.bgColor = tempColor;
            ThemeUtil.pressbgColor = tempColor2;
            ThemeUtil.themeName = "暗黑主题";
        }
        super.actionPerformed(button);
    }
}
