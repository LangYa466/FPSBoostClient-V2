package net.fpsboost.screen;
////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//         佛祖保佑       永无BUG     永不修改                  //
////////////////////////////////////////////////////////////////////

import net.fpsboost.module.Module;
import net.fpsboost.module.ModuleManager;
import net.fpsboost.util.HoveringUtil;
import net.fpsboost.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

/**
 * @author LangYa
 * @since 2024/9/25 18:48
 */
public class SimpleClickGUI extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int y = 0;
        int x = 1;
        for (Module module : ModuleManager.getAllModules()) {
            String nameText = String.format("%s - %s",module.cnName,module.name);
            y += 15;
            RenderUtil.drawRectWithOutline(0, y - 1, RenderUtil.getStringWidth(nameText + module.description) + 8, 9, new Color(0,0,0,80).getRGB(),-1);
            RenderUtil.drawString(nameText, x, y, -1);
            RenderUtil.drawString(module.description, x + RenderUtil.getStringWidth(nameText) + 5, y, -1);
            RenderUtil.drawRectWithOutline(x + RenderUtil.getStringWidth(nameText + module.description) + 10, y + 2, 5,5,module.enable ? Color.GREEN.getRGB() : Color.RED.getRGB(),-1);
            if(!module.values.isEmpty()) RenderUtil.drawStringWithShadow("打开设置",x + RenderUtil.getStringWidth(nameText + module.description) + 30, y,-1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int y = 0;
        int x = 1;
        for (Module module : ModuleManager.getAllModules()) {
            String nameText = String.format("%s - %s",module.cnName,module.name);
            y += 15;
            if (HoveringUtil.isHovering(x + RenderUtil.getStringWidth(nameText + module.description) + 10, y, 5,7,mouseX,mouseY)) module.toggle();
            if (!module.values.isEmpty() && HoveringUtil.isHovering(x + RenderUtil.getStringWidth(nameText + module.description) + 30, y,RenderUtil.getStringWidth("打开设置") + 4,mc.fontRendererObj.FONT_HEIGHT,mouseX,mouseY)) mc.displayGuiScreen(new ValueScreen(module));
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
