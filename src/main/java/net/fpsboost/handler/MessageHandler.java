package net.fpsboost.handler;

import net.fpsboost.Wrapper;
import net.fpsboost.module.impl.ClickGUIModule;
import net.fpsboost.screen.clickgui.ClickGui;
import net.fpsboost.screen.clickgui.utils.MsTimer;
import net.fpsboost.screen.clickgui.utils.Rect;
import net.fpsboost.screen.clickgui.utils.Translate;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MessageHandler implements Wrapper {
    public static final List<Message> list = new ArrayList<>();

    public static void onRender2D() {
        ScaledResolution sr = new ScaledResolution(mc);
        int y = 0;
        int commandY = ClickGui.INSTANCE.IsOnCommandBox() ? 10 : 0;
        for (Message message : list) {
            double diff = message.draw(sr.getScaledWidth(), sr.getScaledHeight(), y + 18 + commandY, y);
            y += (int) (diff - commandY);
        }
        list.removeIf(Message::shouldRemove);
    }

    public static void addMessage(String message, MessageType type, int outtime, int color) {
        list.add(new Message(message, type, outtime, color));
    }

    public static void addMessage(String message, MessageType type, int outtime) {
        list.add(new Message(message, type, outtime));
    }

    public static void addMessage(String message, MessageType type) {
        list.add(new Message(message, type, 1500));
    }

    public enum MessageType {
        Info, Help, Warning, Error, Wrong, Right
    }

    // 突然想起来语法 所以不能这样改 我是JavaBigGod
    public static int getColor(int type) {
        Color rectColor, textColor;
        switch (ClickGUIModule.theme) {
            case Dark:
                rectColor = new Color(23, 32, 42);
                textColor = new Color(255, 255, 255);
                break;
            case Light:
                rectColor = new Color(242, 243, 244);
                textColor = new Color(23, 32, 42);
                break;
            default:
                return 0;
        }

        return (type == 0) ? rectColor.getRGB() : textColor.getRGB();
    }

    public static class Message {
        String message;
        MessageType type;
        Translate anim;
        MsTimer timer;
        int outTime;
        int color;
        final FontRenderer fr = FontManager.client(18);

        public Message(String message, MessageType type, int outTime, int color) {
            this.message = message;
            this.type = type;
            this.outTime = outTime;
            this.anim = new Translate(0, 0);
            this.timer = new MsTimer();
            this.color = color;
        }

        public Message(String message, MessageType type, int outTime) {
            this(message, type, outTime, -1);
        }

        public double draw(double x, double y, double targetY, double diff) {
            int width = 16 + 2 + fr.getStringWidth(this.message) + 2;
            new Rect(x - this.anim.getX(), y - this.anim.getY(), width, 16, getColor(0), Rect.RenderType.Expand).draw();
            FontManager.logo(24).drawString(this.getTypeString(), x - this.anim.getX() + 2, y + 5 - this.anim.getY(), getColor(1));
            fr.drawString(this.message, x - this.anim.getX() + 2 + 16, y + 5 - this.anim.getY(), color == -1 ? getColor(1) : color);

            if (timer.reach(outTime)) {
                this.anim.interpolate(0, 0, 0.3f);
            } else {
                this.anim.interpolate(width, (float) targetY, 0.3f);
            }

            return this.anim.getY() - diff;
        }

        public String getTypeString() {
            if (this.type == null) return "";
            switch (this.type) {
                case Error:
                    return "o";
                case Help:
                    return "m";
                case Info:
                    return "n";
                case Right:
                    return "r";
                case Warning:
                    return "l";
                case Wrong:
                    return "q";
                default:
                    return "";
            }
        }

        // 666这个三元运算符太吓人了
        public boolean shouldRemove() {
            return timer.reach(outTime) && this.anim.getX() == 0 && this.anim.getY() == 0;
        }
    }
}
