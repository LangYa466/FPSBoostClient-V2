package net.fpsboost.element.impl;

import net.fpsboost.Client;
import net.fpsboost.element.Element;
import net.fpsboost.module.impl.ClientSettings;
import net.fpsboost.util.PingUtil;
import net.fpsboost.util.font.FontManager;
import net.fpsboost.value.impl.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LangYa466, ImFl0wow
 * @since 2025/1/10
 */
public class SmokePlayerInfo extends Element {
    private final BooleanValue NickName = new BooleanValue("名字显示", "NickName", true);
    private final BooleanValue FPS = new BooleanValue("FPS显示", "FPS", true);
    private final BooleanValue Ping = new BooleanValue("Ping显示", "Ping", true);
    private final BooleanValue XYZ = new BooleanValue("XYZ显示", "XYZ", true);
    private final BooleanValue Directions = new BooleanValue("方向显示", "Directions", true);
    private final BooleanValue Server = new BooleanValue("服务器显示", "Server", true);
    private final BooleanValue INFO = new BooleanValue("客戶端信息", "ClientInfo", true);

    private final String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public SmokePlayerInfo() {
        super("SmokePlayerInfo", "Smoke客户端信息显示");
    }

    private String getDirectionsName(String dir) {
        if (!ClientSettings.isChinese) return dir;
        if ("S".equals(dir)) return "南";
        if ("SW".equals(dir)) return "西南";
        if ("W".equals(dir)) return "西";
        if ("NW".equals(dir)) return "西北";
        if ("N".equals(dir)) return "北";
        if ("NE".equals(dir)) return "东北";
        if ("E".equals(dir)) return "东";
        if ("SE".equals(dir)) return "东南";
        return dir;
    }

    private static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int) (yaw + 360.0 / (2 * zones) + 0.5) % 360;
        return (angle < 0 ? angle + 360 : angle) / (360 / zones);
    }

    @Override
    public void onDraw() {
        if (mc.gameSettings.showDebugInfo) return;

        FontManager.client().drawStringWithShadow(
                ClientSettings.isChinese ? "显示信息" : "Hud Info",
                3, 3, new Color(0, 160, 255, 160).getRGB()
        );

        List<String> info = new ArrayList<>();
        if (NickName.getValue()) info.add((ClientSettings.isChinese ? "<昵称> " : "<Username> ") + EnumChatFormatting.WHITE + mc.thePlayer.getName());
        if (FPS.getValue()) info.add((ClientSettings.isChinese ? "<帧数> " : "<FPS> ") + EnumChatFormatting.WHITE + Minecraft.getDebugFPS());
        if (Ping.getValue()) info.add((ClientSettings.isChinese ? "<延迟> " : "<Ping> ") + EnumChatFormatting.WHITE + (mc.isSingleplayer() ? "N/A" : PingUtil.getPing()));
        if (XYZ.getValue()) info.add((ClientSettings.isChinese ? "<坐标> " : "<XYZ> ") + EnumChatFormatting.WHITE + MathHelper.floor_double(mc.thePlayer.posX) + ", " + MathHelper.floor_double(mc.thePlayer.posY) + ", " + MathHelper.floor_double(mc.thePlayer.posZ));
        if (Directions.getValue()) info.add((ClientSettings.isChinese ? "<方向> " : "<Directions> ") + EnumChatFormatting.WHITE + getDirectionsName(directions[wrapAngleToDirection(mc.thePlayer.rotationYaw, directions.length)]));
        if (Server.getValue()) info.add((ClientSettings.isChinese ? "<服务器> " : "<Server> ") + EnumChatFormatting.WHITE + (mc.isSingleplayer() ? (ClientSettings.isChinese ? "单人世界" : "SingleWorld") : mc.getCurrentServerData().serverIP));

        int y = 15;
        for (String line : info) {
            FontManager.client().drawStringWithShadow(line, 2, y, new Color(255, 160, 0, 160).getRGB());
            y += 10;
        }

        if (INFO.getValue()) {
            String clientInfo = EnumChatFormatting.WHITE + Client.name + " - " + EnumChatFormatting.GRAY + Client.version;
            ScaledResolution sr = new ScaledResolution(mc);
            FontManager.client().drawStringWithShadow(clientInfo, 4, sr.getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 11), new Color(255, 255, 255, 160).getRGB());
        }
    }
}