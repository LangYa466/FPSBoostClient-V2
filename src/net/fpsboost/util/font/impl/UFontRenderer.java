package net.fpsboost.util.font.impl;

import net.fpsboost.Wrapper;
import net.fpsboost.module.impl.NameProtect;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class UFontRenderer extends FontRenderer implements Wrapper {
    private StringCache stringCache;

    public UFontRenderer(Font font, int size) {
        super(
                mc.gameSettings,
                new ResourceLocation("textures/font/ascii.png"),
                mc.getTextureManager(),
                false
        );
        boolean antiAlias = true;

        ResourceLocation res = new ResourceLocation("textures/font/ascii.png");
        int[] colorCode = new int[32];
        for (int i = 0; i <= 31; i++) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;
            if (i == 6) {
                k += 85;
            }
            if (mc.gameSettings.anaglyph) {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }
            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }
            colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | (i1 & 255);
        }

        try {
            if (mc.getResourceManager().getResource(res).getResourceLocation().getResourcePath().equalsIgnoreCase("textures/font/ascii.png")) {
                stringCache = new StringCache(colorCode);
                stringCache.setDefaultFont(font, size, antiAlias);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Draws the specified string with a shadow.
     */
    @Override
    public int drawStringWithShadow(String text, float x, float y, int color) {
        Color color1 = new Color(color);
        this.drawString(text, x, y, new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()).getRGB(), true);
        return getStringWidth(text);
    }

    @Override
    public String trimStringToWidth(String text, int width) {
        return trimString(text, width, false, false);
    }

    public String trimString(String text, float width, boolean more, boolean reverse) {
        String realText = reverse? new StringBuilder(text).reverse().toString() : text;
        StringBuilder stringbuilder = new StringBuilder();
        for (char c : realText.toCharArray()) {
            if (getStringWidth(stringbuilder.toString() + c) < width)
                stringbuilder.insert(reverse? 0 : stringbuilder.length(), c);
            else
                break;
        }
        if (more) {
            if (!stringbuilder.toString().equals(text)) {
                int extraWidth = getStringWidth("...");
                do {
                    stringbuilder.deleteCharAt(reverse? 0 : stringbuilder.length() - 1);
                } while (getStringWidth(stringbuilder.toString()) > width - extraWidth && stringbuilder.length() > 0);
            }
        }
        return (more && reverse && !stringbuilder.toString().equals(text)? "..." : "") + stringbuilder + (more && !reverse && !stringbuilder.toString().equals(text)? "..." : "");
    }

    /**
     * Draws the specified string.
     */
    public float drawString(String text, float x, int y, int color) {
        return this.drawString(text, x, y, color, false);
    }

    public int drawString(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, false);
        return getStringWidth(text);
    }

    /**
     * Automatically 换行
     * @return Actual rows that are rendered.
     */
    public float drawTrimString(String text, float x, float y, float maxWidth, int maxRows, float gap, int color) {
        float currentY = y;
        int row = 1;
        while (row <= maxRows) {
            String toRender = trimString(text, maxWidth, row == maxRows, false);
            text = text.replace(toRender.replace("...", ""), "");
            drawString(toRender, x, currentY, color);
            currentY += toRender.isEmpty()? 0 : getHeight() + gap;
            row++;
        }
        return currentY;
    }

    public int drawStringCapableWithEmoji(String text, float x, float y, int color) {
        char[] chars = text.toCharArray();
        int lastCut = 0;
        float xOffset = x;
        for (int i = 0; i < chars.length; i++) {
            if (isEmojiCharacter(text.codePointAt(i))) {
                xOffset += this.drawString(text.substring(0, i), xOffset, y, color, false);
                this.drawString(String.valueOf(chars[i]), xOffset, y, color, false);
                xOffset += this.getStringWidth(String.valueOf(chars[i]));
                lastCut = i + 1;
            }
        }
        this.drawString(text.substring(lastCut), xOffset, y, color, false);
        return getStringWidth(text);
    }

    public static boolean isEmojiCharacter(int codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                (codePoint >= 0x20 && codePoint <= 0xD7FF) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD));
    }

    /**
     * Draws the specified string.
     */
    @Override
    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        text = NameProtect.onText(text);
        int shadowColor = 0;
        if (dropShadow) shadowColor = (color & 0xFCFCFC) >> 2 | color & new Color(20, 20, 20, 200).getRGB();
        float shadowWidth = stringCache.renderString(text, x + 1F, y + .5F, shadowColor, true);
        return (int) Math.max(shadowWidth, stringCache.renderString(text, x, y, color, false));
    }

    @Override
    public int getStringWidth(String text) {
        text = NameProtect.onText(text);
        return stringCache.getStringWidth(text);
    }

    public void drawCenteredString(String text, float x, float y, int color) {
        drawString(text, x - stringCache.getStringWidth(text) / 2f, y - stringCache.height / 4f - 1f, color, false);
    }

    public int drawCenteredString(String text, float x, float y, int color, boolean textShadow) {
        return drawString(text, x - stringCache.getStringWidth(text) / 2f, y - stringCache.height / 4f - 1f, color, textShadow);
    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawCenteredString(text,x,y,color,true);
    }

    public void drawCenteredStringH(String text, float x, float y, int color) {
        drawString(text, x - stringCache.getStringWidth(text) / 2f, y, color, false);
    }

    public void drawCenteredStringV(String text, float x, float y, int color) {
        drawString(text, x, y - stringCache.height / 4f - 1f, color, false);
    }

    @Override
    public int getHeight() {
        return stringCache.height / 2;
    }

    public float drawStringCapableWithEmojiWithShadow(String text, float x, float y, int color) {
        String[] sbs = new String[]{"\uD83C\uDF89", "\uD83C\uDF81", "\uD83D\uDC79", "\uD83C\uDFC0", "⚽", "\uD83C\uDF6D", "\uD83C\uDF20", "\uD83D\uDC7E", "\uD83D\uDC0D"
                , "\uD83D\uDD2E", "\uD83D\uDC7D", "\uD83D\uDCA3", "\uD83C\uDF6B", "\uD83C\uDF82"};
        for (String sb : sbs) {
            text = text.replaceAll(sb, "");
        }
        return drawStringWithShadow(text, x, y, color);
    }

    @Override
    public float getCharWidthFloat(char character) {
        // 颜色符号
        if (character == 167) {
            return -1.0F;
        }

        // 空格
        if (character == ' ' || character == 160) {
            return this.charWidthFloat[32]; // 返回空格宽度
        }

        int index = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(character);

        if (character > 0 && index != -1 && !this.unicodeFlag) {
            return this.charWidthFloat[index];
        }

        // 根据位操作计算宽度
        if (this.glyphWidth[character] != 0) {
            int left = this.glyphWidth[character] >>> 4;
            int right = this.glyphWidth[character] & 15;

            // 校正右边界值
            if (right > 7) {
                right = 15;
                left = 0;
            }

            return (float)((right - left + 1) / 2 + 1);
        }

        return 0.0F;
    }
}
