package net.fpsboost.screen;

import net.fpsboost.config.ConfigManager;
import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.HoveringUtil;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.ThemeUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

/**
 * @author LangYa
 * @since 2024/9/25 18:48
 */
public class SimpleClickGUI extends GuiScreen {
    private int scrollOffset = 2;
    private static final int MAX_SCROLL = 200;
    private static final int MIN_SCROLL = 0;
    private int addWidth,delWidth;

    @Override
    public void onGuiClosed() {
        ConfigManager.saveConfig("Module.json");
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int y = 0;
        int x = 1;

        RenderUtil.drawRect(width - 11,1,6,MAX_SCROLL + 11,Color.gray);
        RenderUtil.drawRect(width - 10,scrollOffset,5,10,Color.white);
        addWidth = RenderUtil.drawText("+",width - 30,1,true, ThemeUtil.bgColor.getRGB(),-1,true);
        delWidth = RenderUtil.drawText("-",width - 30,30,true, ThemeUtil.bgColor.getRGB(),-1,true);

        for (Module module : ModuleManager.getAllModules()) {
            y += 15;

            int displayY = y - scrollOffset;

            // 可见
            if (displayY >= 0 && displayY <= height - 15) {
                String nameText;
                String description;
                String settingsText;
                if (ClientSettings.INSTANCE.cnMode.getValue()){
                    nameText = module.cnName;
                    description = module.cnDescription;
                    settingsText = "打开设置";
                }else{
                    nameText = module.name;
                    description = module.description;
                    settingsText = "Settings";
                }
                RenderUtil.drawRectWithOutline(0, displayY - 1, RenderUtil.getStringWidth(nameText + description) + 8, 9, new Color(0, 0, 0, 80).getRGB(), -1);
                RenderUtil.drawString(nameText, x, displayY, -1);
                RenderUtil.drawString(description, x + RenderUtil.getStringWidth(nameText) + 5, displayY, -1);
                RenderUtil.drawRectWithOutline(x + RenderUtil.getStringWidth(nameText + description) + 10, displayY + 2, 5, 5, module.enable ? Color.GREEN.getRGB() : Color.RED.getRGB(), -1);

                if (!module.values.isEmpty())
                    RenderUtil.drawStringWithShadow(settingsText, x + RenderUtil.getStringWidth(nameText + description) + 30, displayY, -1);
            }
        }


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int y = 0;
        int x = 1;

        for (Module module : ModuleManager.getAllModules()) {
            y += 15;

            int displayY = y - scrollOffset;

            if (displayY >= 0 && displayY <= height - 15) {
                String nameText;
                String description;
                String settingsText;
                if (ClientSettings.INSTANCE.cnMode.getValue()){
                    nameText = module.cnName;
                    description = module.cnDescription;
                    settingsText = "打开设置";
                }else{
                    nameText = module.name;
                    description = module.description;
                    settingsText = "Settings";
                }
                if (HoveringUtil.isHovering(x + RenderUtil.getStringWidth(nameText + description) + 10, displayY, 5, 7, mouseX, mouseY))
                    module.toggle();

                if (!module.values.isEmpty() && HoveringUtil.isHovering(x + RenderUtil.getStringWidth(nameText + description) + 30, displayY, RenderUtil.getStringWidth(settingsText) + 4, mc.fontRendererObj.FONT_HEIGHT, mouseX, mouseY))
                    mc.displayGuiScreen(new ValueScreen(module));
            }
        }

        if (HoveringUtil.isHovering(width - 30,1,addWidth,addWidth,mouseX,mouseY)) {
            int after = scrollOffset + 15;
            if (after < MAX_SCROLL) scrollOffset = after;
        }

        if (HoveringUtil.isHovering(width - 30,30,delWidth,delWidth,mouseX,mouseY)) {
            int after = scrollOffset - 15;
            if (after > MIN_SCROLL) scrollOffset = after;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int wheel = Integer.signum(Mouse.getDWheel());
        if (wheel != 0) {
            scrollOffset -= wheel * 15;

            // 限制滚动范围
            scrollOffset = Math.max(MIN_SCROLL, Math.min(scrollOffset, MAX_SCROLL));
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}
