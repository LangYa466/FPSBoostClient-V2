package net.fpsboost.element.impl;

import net.fpsboost.Client;
import net.fpsboost.element.Element;
import net.fpsboost.module.impl.ClientSettings;
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
 * @author SmokeClient
 * 反编译
 */
public class SmokePlayerInfo extends Element {
    public SmokePlayerInfo() {
        super("SmokePlayerInfo", "Smoke客户端信息显示");
    }

    public final BooleanValue NickName = new BooleanValue("名字显示", "NickName", true);
    public final BooleanValue FPS = new BooleanValue("FPS显示", "FPS", true);
    public final BooleanValue Ping = new BooleanValue("Ping显示", "Ping", true);
    public final BooleanValue XYZ = new BooleanValue("XYZ显示", "XYZ", true);
    public final BooleanValue Directions = new BooleanValue("方向显示", "Directions", true);
    public final BooleanValue Server = new BooleanValue("服务器显示", "Server", true);
    private final List<String> Info = new ArrayList<>();
    private final String[] directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
    private String getDirectionsName(String mod) {
        if (!(ClientSettings.INSTANCE.cnMode.getValue())) return mod;
        switch (mod) {
            case "S": {
                return "南";
            }
            case "SW": {
                return "西南";
            }
            case "W": {
                return "西";
            }
            case "NW": {
                return "西北";
            }
            case "N": {
                return "北";
            }
            case "NE": {
                return "东北";
            }
            case "E": {
                return "东";
            }
            case "SE": {
                return "东南";
            }
        }
        return mod;
    }

    public static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int)((double)(yaw + (float)(360 / (2 * zones))) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }

    @Override
    public void onDraw() {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }
        String text = ClientSettings.INSTANCE.cnMode.getValue() ? "显示信息" : "Hud Info";
        FontManager.hanYi().drawStringWithShadow(text, 3, 3, new Color(0, 160, 255, 160).getRGB());
        text = "_______________";
        FontManager.hanYi().drawStringWithShadow(text, 3, 5, new Color(0, 160, 255, 160).getRGB());
        if (NickName.getValue()) {
            this.Info.add((ClientSettings.INSTANCE.cnMode.getValue() ? "<昵称> " : "<NickName> ") + EnumChatFormatting.WHITE + mc.thePlayer.getName());
        }
        if (FPS.getValue()) {
            StringBuilder var10001 = new StringBuilder(ClientSettings.INSTANCE.cnMode.getValue() ? "<帧数> " : "<FPS> ").append(EnumChatFormatting.WHITE);
            this.Info.add(var10001.append(Minecraft.getDebugFPS()).toString());
        }
        if (Ping.getValue()) {
            this.Info.add((ClientSettings.INSTANCE.cnMode.getValue() ? "<延迟> " : "<Ping> ") + EnumChatFormatting.WHITE + (mc.isSingleplayer() ? "%" : PingDisplay.getPing()));
        }
        if (XYZ.getValue()) {
            this.Info.add((ClientSettings.INSTANCE.cnMode.getValue() ? "<坐标> " : "<XYZ> ") + EnumChatFormatting.WHITE + MathHelper.floor_double(mc.thePlayer.posX) + "," + MathHelper.floor_double(mc.thePlayer.posY) + "," + MathHelper.floor_double(mc.thePlayer.posZ));
        }
        if (Directions.getValue()) {
            this.Info.add((ClientSettings.INSTANCE.cnMode.getValue() ? "<方向> " : "<Directions> ") + EnumChatFormatting.WHITE + getDirectionsName(this.directions[wrapAngleToDirection(mc.thePlayer.rotationYaw, this.directions.length)]));
        }
        if (Server.getValue()) {
            this.Info.add((ClientSettings.INSTANCE.cnMode.getValue() ? "<服务器> " : "<Server> ") + EnumChatFormatting.WHITE + (mc.isSingleplayer() ? ((ClientSettings.INSTANCE.cnMode.getValue()).booleanValue() ? "单人世界" : "SingleWorld") : mc.getCurrentServerData().serverIP));
        }
        int index = 0;
        for (String str : this.Info) {
            FontManager.hanYi().drawStringWithShadow(str, 2, 5 + 10 * ++index, new Color(255, 160, 0, 160).getRGB());
        }
        this.Info.clear();
        String clientText = EnumChatFormatting.WHITE + Client.name + " " + Client.version;
        ScaledResolution sr = new ScaledResolution(mc);
        FontManager.hanYi().drawStringWithShadow(clientText, 4, sr.getScaledHeight() - (mc.ingameGUI.getChatGUI().getChatOpen() ? 22 : 9), new Color(255, 255, 255, 160).getRGB());
        super.onDraw();
    }
}
