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
import net.fpsboost.util.HoveringUtil;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.value.Value;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ModeValue;
import net.fpsboost.value.impl.NumberValue;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author LangYa
 * @since 2024/9/25 18:19
*/
public class ValueScreen extends GuiScreen {

    private final Module module;
    private String keyDisplayString = "";
    public ValueScreen(Module module) {
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        RenderUtil.drawStringWithShadow(module.cnName,1,0,-1);
        RenderUtil.drawStringWithShadow("返回",50,0,-1);
        int y = 5;
        int x = 0;
        for (Value<?> value : module.values) {
            if (value.isHide) return;
            y += 10;
            if (value instanceof BooleanValue) {
                RenderUtil.drawString(value.name, x, y, -1);
                RenderUtil.drawRectWithOutline(x + RenderUtil.getStringWidth(value.name) + 5,y + 3,5,5,((BooleanValue)value).getValue() ? Color.GREEN.getRGB() : Color.RED.getRGB(),-1);
            }
            if (value instanceof ModeValue) {
                RenderUtil.drawString(value.name, x, y, -1);
                RenderUtil.drawString(((ModeValue)value).getValue(), x + RenderUtil.getStringWidth(value.name) + 5,y, -1);
            }
            if (value instanceof NumberValue) {
                RenderUtil.drawString(value.name, x, y, -1);
                RenderUtil.drawString(String.valueOf(((NumberValue)value).getValue()), x + RenderUtil.getStringWidth(value.name) + 5,y, -1);
                RenderUtil.drawStringWithOutline("+", x + RenderUtil.getStringWidth(value.name) + 35,y, -1,Color.GREEN.getRGB());
                RenderUtil.drawStringWithOutline("-", x + RenderUtil.getStringWidth(value.name) + 55,y, -1,Color.RED.getRGB());
            }
        }
        if (module.keyCode != 0) {
            keyDisplayString = "按我绑定开关按键";
        } else {
            keyDisplayString = "绑定的按键为: " + Keyboard.getKeyName(module.keyCode);
        }

        if (HoveringUtil.isHovering(0,y + 20,RenderUtil.getStringWidth(keyDisplayString),mc.fontRendererObj.FONT_HEIGHT,mouseX,mouseY)) {
            keyDisplayString = "按一下你要绑定的按键即可绑定";
        }

        mc.fontRendererObj.drawStringWithShadow(keyDisplayString ,0,y + 20,-1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int y = 5;
        int x = 0;
        if (HoveringUtil.isHovering(50,0,RenderUtil.getStringWidth("返回"),mc.fontRendererObj.FONT_HEIGHT,mouseX,mouseY)) mc.displayGuiScreen(new SimpleClickGUI());
        for (Value<?> value : module.values) {
            if (value.isHide) return;
            y += 10;
            if (value instanceof BooleanValue) {
                if (HoveringUtil.isHovering(x + RenderUtil.getStringWidth(value.name) + 5, y + 3, 5, 5, mouseX, mouseY)) {
                    ((BooleanValue) value).toggle();
                }
            }
            if (value instanceof ModeValue) {
                if (HoveringUtil.isHovering(x + RenderUtil.getStringWidth(value.name) + 5,y,5,5,mouseX,mouseY)) {
                    ((ModeValue)value).setNextValue();
                }
            }
            if (value instanceof NumberValue) {
                if (HoveringUtil.isHovering(x + RenderUtil.getStringWidth(value.name) + 35,y,RenderUtil.getStringWidth("+") + 4,mc.fontRendererObj.FONT_HEIGHT,mouseX,mouseY)) {
                    ((NumberValue)value).add();
                }
                if (HoveringUtil.isHovering(x + RenderUtil.getStringWidth(value.name) + 55,y,RenderUtil.getStringWidth("-") + 4,mc.fontRendererObj.FONT_HEIGHT,mouseX,mouseY)) {
                    ((NumberValue)value).cut();
                }
            }
        }
        if (HoveringUtil.isHovering(0,y + 20,RenderUtil.getStringWidth(keyDisplayString),mc.fontRendererObj.FONT_HEIGHT,mouseX,mouseY)) {
            module.keyCode = mouseButton;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
