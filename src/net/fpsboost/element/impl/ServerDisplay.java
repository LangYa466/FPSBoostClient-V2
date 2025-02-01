package net.fpsboost.element.impl;

import cn.langya.Logger;
import net.fpsboost.element.Element;
import net.fpsboost.util.RenderUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;
import net.fpsboost.value.impl.ColorValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author LangYa466
 * @since 2/2/2025
 */
public class ServerDisplay extends Element {
    public ServerDisplay() {
        super("ServerDisplay", "服务器显示");
    }

    private final BooleanValue backgroundValue = new BooleanValue("背景","Background",true);
    private final BooleanValue textShadowValue = new BooleanValue("字体阴影","Text Shadow",true);
    private final BooleanValue clientFontValue = new BooleanValue("更好的字体","Better Font",true);
    private final ColorValue bgColorValue = new ColorValue("背景颜色","Background Color",new Color(0,0,0,80),this);
    private final ColorValue textColorValue = new ColorValue("文本颜色","Text Color",Color.white,this);

    @Override
    public void onDraw() {
        FontRenderer fr;
        if (clientFontValue.getValue()) fr = FontManager.client(); else fr = mc.fontRendererObj;
        ServerData serverData = mc.getCurrentServerData();
        boolean integratedServerRunning = mc.isIntegratedServerRunning();
        String text = integratedServerRunning ? "SinglePlayer" : serverData.serverIP;
        width = 46 + fr.getStringWidth(text);
        height = 38;

        if (backgroundValue.getValue()) RenderUtil.drawRect(0, 0, width, height, bgColorValue.getValueC());
        ResourceLocation icon = (integratedServerRunning) ? ServerListEntryNormal.UNKNOWN_SERVER : getCurrentServerIcon(serverData);
        RenderUtil.drawImage(icon, 4.5F, 4.5F, 29, 29);
        fr.drawString(text, 40, 14.5F, textColorValue.getValueC(), textShadowValue.getValue());

        super.onDraw();
    }

    public static ResourceLocation getCurrentServerIcon(ServerData serverData) {
        for (ServerListEntryNormal serverListEntryNormal : GuiMultiplayer.serverListSelector.serverListInternet) {
            // check server
            if (serverListEntryNormal.server == serverData) {
                // check icon
                return (serverListEntryNormal.field_148305_h != null) ? serverListEntryNormal.serverIcon : serverListEntryNormal.UNKNOWN_SERVER;
            }
        }
        Logger.error("Can't get server({}) icon", serverData.toString());
        return null;
    }

    @Override
    public void init() {
        height = mc.fontRendererObj.getHeight();
    }
}
