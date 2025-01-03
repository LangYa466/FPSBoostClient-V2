package net.fpsboost.screen;

import lombok.var;
import net.fpsboost.util.ColorUtil;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LangYa
 * @since 2024/11/20 19:14
 */
public class GuiWelcome extends GuiScreen {

    private final List<String> infos = new ArrayList<>();

    @Override
    public void initGui() {
        infos.add("这个页面只有在第一次启动才会有!");
        infos.add("按T可以拖动UI");
        infos.add("按右边的Shift可以打开功能页面");
        infos.add("第一次玩记得先去客户端设置这个模块设置一下!");
        infos.add("在这个页面任意地方按一下就可以下一步!");
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawClientBackground();
        int y = 0;
        var fr = FontManager.client(24);
        for (String str : infos) {
            fr.drawStringWithShadow(str,width / 2 - (fr.getStringWidth(str) / 2),height / 4 + y, ColorUtil.rainbow(1, (int) partialTicks).getRGB());
            y += 20;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mc.displayGuiScreen(new GuiMainMenu());
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
