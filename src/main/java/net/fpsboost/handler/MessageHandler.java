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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MessageHandler implements Wrapper {
    private static final List<Message> list = new LinkedList<>();

    public static void onRender2D() {
        ScaledResolution sr = new ScaledResolution(mc);
        int y = 0;
        int commandY = ClickGui.INSTANCE.IsOnCommandBox() ? 10 : 0;

        Iterator<Message> iterator = list.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            double diff = message.draw(sr.getScaledWidth(), sr.getScaledHeight(), y + 18 + commandY, y);
            y += (int) (diff - commandY);
            if (message.shouldRemove()) {
                iterator.remove();
            }
        }
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

    private static final int DARK_RECT_COLOR = new Color(23, 32, 42).getRGB();
    private static final int DARK_TEXT_COLOR = new Color(255, 255, 255).getRGB();
    private static final int LIGHT_RECT_COLOR = new Color(242, 243, 244).getRGB();
    private static final int LIGHT_TEXT_COLOR = new Color(23, 32, 42).getRGB();

    public static int getColor(int type) {
        if (ClickGUIModule.theme == ClickGUIModule.ThemeType.Dark) {
            return (type == 0) ? DARK_RECT_COLOR : DARK_TEXT_COLOR;
        } else if (ClickGUIModule.theme == ClickGUIModule.ThemeType.Light) {
            return (type == 0) ? LIGHT_RECT_COLOR : LIGHT_TEXT_COLOR;
        }
        return 0;
    }

    public static class Message {
        private final String message;
        private final MessageType type;
        private final Translate anim = new Translate(0, 0);
        private final MsTimer timer = new MsTimer();
        private final int outTime;
        private final int color;
        private final FontRenderer fr = FontManager.client(18);

        public Message(String message, MessageType type, int outTime, int color) {
            this.message = message;
            this.type = type;
            this.outTime = outTime;
            this.color = color;
        }

        public Message(String message, MessageType type, int outTime) {
            this(message, type, outTime, -1);
        }

        public double draw(double x, double y, double targetY, double diff) {
            int width = 20 + fr.getStringWidth(this.message);
            double animX = anim.getX();
            double animY = anim.getY();

            if (timer.reach(outTime)) {
                anim.interpolate(0, 0, 0.3f);
            } else {
                anim.interpolate(width, (float) targetY, 0.3f);
            }

            new Rect(x - animX, y - animY, width, 16, getColor(0), Rect.RenderType.Expand).draw();
            FontManager.logo(24).drawString(getTypeString(), x - animX + 2, y + 5 - animY, getColor(1));
            fr.drawString(message, x - animX + 20, y + 5 - animY, (color == -1) ? getColor(1) : color);

            return anim.getY() - diff;
        }

        public String getTypeString() {
            if (type == null) return "";
            switch (type) {
                case Error: return "o";
                case Help: return "m";
                case Info: return "n";
                case Right: return "r";
                case Warning: return "l";
                case Wrong: return "q";
                default: return "";
            }
        }

        public boolean shouldRemove() {
            return timer.reach(outTime) && anim.getX() == 0 && anim.getY() == 0;
        }
    }
}
