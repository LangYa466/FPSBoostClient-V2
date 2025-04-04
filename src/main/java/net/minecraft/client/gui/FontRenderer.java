package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import net.fpsboost.util.DrawTextHookManager;
import net.fpsboost.util.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;
import net.optifine.render.GlBlendState;
import net.optifine.util.FontUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class FontRenderer implements IResourceManagerReloadListener {
    private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
    private final int[] charWidth = new int[256];
    public Random fontRandom = new Random();
    protected byte[] glyphWidth = new byte[65536];
    private final int[] colorCode = new int[32];
    private ResourceLocation locationFontTexture;
    private final TextureManager renderEngine;
    private float posX;
    private float posY;
    protected boolean unicodeFlag;
    private boolean bidiFlag;
    private float red;
    private float blue;
    private float green;
    private float alpha;
    private int textColor;
    private boolean randomStyle;
    private boolean boldStyle;
    private boolean italicStyle;
    private boolean underlineStyle;
    private boolean strikethroughStyle;
    public GameSettings gameSettings;
    public ResourceLocation locationFontTextureBase;
    public float offsetBold = 1.0F;
    protected float[] charWidthFloat = new float[256];
    protected boolean blend = false;
    protected final GlBlendState oldBlendState = new GlBlendState();

    public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
        this.gameSettings = gameSettingsIn;
        this.locationFontTextureBase = location;
        this.locationFontTexture = location;
        this.renderEngine = textureManagerIn;
        this.unicodeFlag = unicode;
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
        this.bindTexture(this.locationFontTexture);

        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (gameSettingsIn.anaglyph) {
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

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }

        this.readGlyphSizes();
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

        Arrays.fill(unicodePageLocations, null);

        this.readFontTexture();
        this.readGlyphSizes();
    }

    private void readFontTexture() {
        BufferedImage image;
        try {
            image = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Properties properties = FontUtils.readFontProperties(this.locationFontTexture);
        this.blend = FontUtils.readBoolean(properties, "blend", false);

        int width = image.getWidth();
        int height = image.getHeight();
        int charWidth = width / 16;
        int charHeight = height / 16;
        float scale = Math.max(1.0F, Math.min((float) width / 128.0F, 2.0F));
        this.offsetBold = 1.0F / scale;

        float customOffsetBold = FontUtils.readFloat(properties, "offsetBold", -1.0F);
        if (customOffsetBold >= 0.0F) {
            this.offsetBold = customOffsetBold;
        }

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        // 计算字符宽度
        for (int i = 0; i < 256; ++i) {
            int cx = (i % 16) * charWidth;
            int cy = (i / 16) * charHeight;
            int maxX = charWidth - 1;

            for (; maxX >= 0; --maxX) {
                boolean empty = true;
                for (int y = 0; y < charHeight; ++y) {
                    if ((pixels[(cy + y) * width + (cx + maxX)] >> 24 & 255) > 16) {
                        empty = false;
                        break;
                    }
                }
                if (!empty) break;
            }

            // 处理空格字符宽度
            if (i == 32) {
                maxX = (charWidth <= 8) ? (int) (2.0F * scale) : (int) (1.5F * scale);
            }

            this.charWidthFloat[i] = (maxX + 2) / scale;
        }

        FontUtils.readCustomCharWidths(properties, this.charWidthFloat);

        for (int i = 0; i < this.charWidth.length; ++i) {
            this.charWidth[i] = Math.round(this.charWidthFloat[i]);
        }
    }

    private void readGlyphSizes() {
        InputStream inputstream = null;

        try {
            inputstream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
            inputstream.read(this.glyphWidth);
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        } finally {
            IOUtils.closeQuietly(inputstream);
        }
    }

    private float renderChar(char ch, boolean italic) {
        if (ch != 32 && ch != 160) {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch);
            return i != -1 && !this.unicodeFlag ? this.renderDefaultChar(i, italic) : this.renderUnicodeChar(ch, italic);
        } else {
            return !this.unicodeFlag ? this.charWidthFloat[ch] : 4.0F;
        }
    }

    private float renderDefaultChar(int ch, boolean italic) {
        int i = ch % 16 * 8;
        int j = ch / 16 * 8;
        int k = italic ? 1 : 0;

        this.bindTexture(this.locationFontTexture);

        float f = this.charWidthFloat[ch];
        float f1 = 7.99F;

        // 合并绘制操作，减少OpenGL状态切换
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f((float) i / 128.0F, (float) j / 128.0F);
        GL11.glVertex3f(this.posX + (float) k, this.posY, 0.0F);
        GL11.glTexCoord2f((float) i / 128.0F, ((float) j + f1) / 128.0F);
        GL11.glVertex3f(this.posX - (float) k, this.posY + f1, 0.0F);
        GL11.glTexCoord2f(((float) i + f1 - 1.0F) / 128.0F, (float) j / 128.0F);
        GL11.glVertex3f(this.posX + f1 - 1.0F + (float) k, this.posY, 0.0F);
        GL11.glTexCoord2f(((float) i + f1 - 1.0F) / 128.0F, ((float) j + f1) / 128.0F);
        GL11.glVertex3f(this.posX + f1 - 1.0F - (float) k, this.posY + f1, 0.0F);
        GL11.glEnd();

        return f;
    }

    private ResourceLocation getUnicodePageLocation(int page) {
        if (unicodePageLocations[page] == null) {
            unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", Integer.valueOf(page)));
            unicodePageLocations[page] = FontUtils.getHdFontLocation(unicodePageLocations[page]);
        }

        return unicodePageLocations[page];
    }

    private void loadGlyphTexture(int page) {
        this.bindTexture(this.getUnicodePageLocation(page));
    }

    private float renderUnicodeChar(char ch, boolean italic) {
        if (this.glyphWidth[ch] == 0) {
            return 0.0F;
        } else {
            int i = ch / 256;
            this.loadGlyphTexture(i);

            int j = this.glyphWidth[ch] >>> 4;
            int k = this.glyphWidth[ch] & 15;
            float f = (float) j;
            float f1 = (float) (k + 1);
            float f2 = (float) (ch % 16 * 16) + f;
            float f3 = (float) ((ch & 255) / 16 * 16);
            float f4 = f1 - f - 0.02F;
            float f5 = italic ? 1.0F : 0.0F;

            // 合并绘制操作，减少OpenGL状态切换
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
            GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
            GL11.glEnd();

            return (f1 - f) / 2.0F + 1.0F;
        }
    }

    public int drawStringWithShadow(String text, float x, float y, int color, boolean isMC) {
        return this.drawString(text, x, y, color, true, isMC);
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        return this.drawString(text, x, y, color, true);
    }

    public int drawStringWithShadow(String text, double x, double y, int color) {
        return this.drawString(text, x, y, color, true);
    }

    public int drawString(String text, int x, int y, int color) {
        return this.drawString(text, (float) x, (float) y, color, false);
    }

    public int drawString(String text, double x, double y, int color) {
        return this.drawString(text, (float) x, (float) y, color, false);
    }

    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        return drawString(text, x, y, color, dropShadow, false);
    }

    public int drawString(String text, double x, double y, int color, boolean dropShadow) {
        return drawString(text, (float) x, (float) y, color, dropShadow, false);
    }

    public int drawString(String text, float x, float y, int color, boolean dropShadow, boolean isMC) {
        text = DrawTextHookManager.hookMethod(text).getDisplayText();
        this.enableAlpha();

        if (this.blend) {
            GlStateManager.getBlendState(this.oldBlendState);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }

        this.resetStyles();
        int i;

        if (dropShadow) {
            i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
            i = Math.max(i, this.renderString(text, x, y, color, false));
        } else {
            i = this.renderString(text, x, y, color, false);
        }

        if (this.blend) {
            GlStateManager.setBlendState(this.oldBlendState);
        }

        return i;
    }

    private String bidiReorder(String text) {
        try {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException var3) {
            return text;
        }
    }

    private void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    private void renderStringAtPos(String text, boolean shadow) {
        int length = text.length();
        for (int i = 0; i < length; ++i) {
            char c0 = text.charAt(i);

            if (c0 == '§' && i + 1 < length) { // 解析颜色/样式代码
                char formatChar = Character.toLowerCase(text.charAt(i + 1));
                int l = "0123456789abcdefklmnor".indexOf(formatChar);

                if (l >= 0 && l <= 15) { // 颜色代码
                    resetStyles();
                    if (shadow) {
                        l += 16;
                    }
                    int color = this.colorCode[l];

                    if (Config.isCustomColors()) {
                        color = CustomColors.getTextColor(l, color);
                    }

                    this.textColor = color;
                    this.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, this.alpha);
                } else { // 样式代码
                    switch (l) {
                        case 16:
                            this.randomStyle = true;
                            break;
                        case 17:
                            this.boldStyle = true;
                            break;
                        case 18:
                            this.strikethroughStyle = true;
                            break;
                        case 19:
                            this.underlineStyle = true;
                            break;
                        case 20:
                            this.italicStyle = true;
                            break;
                        case 21:
                            this.randomStyle = false;
                            this.boldStyle = false;
                            this.strikethroughStyle = false;
                            this.underlineStyle = false;
                            this.italicStyle = false;
                            this.setColor(this.red, this.blue, this.green, this.alpha);
                            break;
                    }
                }
                i++; // 跳过格式代码
            } else {
                renderCharacter(c0, shadow);
            }
        }
    }

    private void renderCharacter(char c0, boolean shadow) {
        int j = "ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσµτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■ ".indexOf(c0);

        if (this.randomStyle && j != -1) {
            c0 = getRandomChar(j);
        }

        float offset = (j != -1 && !this.unicodeFlag) ? this.offsetBold : 0.5F;
        boolean shadowEffect = (c0 == 0 || j == -1 || this.unicodeFlag) && shadow;

        if (shadowEffect) {
            this.posX -= offset;
            this.posY -= offset;
        }

        float charWidth = this.renderChar(c0, this.italicStyle);

        if (shadowEffect) {
            this.posX += offset;
            this.posY += offset;
        }

        if (this.boldStyle) {
            this.posX += offset;
            if (shadowEffect) {
                this.posX -= offset;
                this.posY -= offset;
            }
            this.renderChar(c0, this.italicStyle);
            this.posX -= offset;
            if (shadowEffect) {
                this.posX += offset;
                this.posY += offset;
            }
            charWidth += offset;
        }

        this.doDraw(charWidth);
    }

    private char getRandomChar(int j) {
        char c1;
        do {
            int randIndex = this.fontRandom.nextInt("ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσµτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■ ".length());
            c1 = "ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσµτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■ ".charAt(randIndex);
        } while (this.getCharWidth(c1) != this.getCharWidth(c1));
        return c1;
    }

    protected void doDraw(float p_doDraw_1_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // 禁用纹理，只需要一次
        GlStateManager.disableTexture2D();

        // 开始绘制
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);

        // 删除线
        if (this.strikethroughStyle) {
            float yCenter = this.posY + (float) (this.getHeight() / 2);
            worldrenderer.pos(this.posX, yCenter, 0.0D).endVertex();
            worldrenderer.pos(this.posX + p_doDraw_1_, yCenter, 0.0D).endVertex();
            worldrenderer.pos(this.posX + p_doDraw_1_, yCenter - 1.0F, 0.0D).endVertex();
            worldrenderer.pos(this.posX, yCenter - 1.0F, 0.0D).endVertex();
        }

        // 下划线
        if (this.underlineStyle) {
            float yBottom = this.posY + (float) this.getHeight();
            int i = this.underlineStyle ? -1 : 0;
            worldrenderer.pos(this.posX + (float) i, yBottom, 0.0D).endVertex();
            worldrenderer.pos(this.posX + p_doDraw_1_, yBottom, 0.0D).endVertex();
            worldrenderer.pos(this.posX + p_doDraw_1_, yBottom - 1.0F, 0.0D).endVertex();
            worldrenderer.pos(this.posX + (float) i, yBottom - 1.0F, 0.0D).endVertex();
        }

        // 绘制所有线条
        tessellator.draw();

        // 恢复纹理
        GlStateManager.enableTexture2D();

        // 更新位置
        this.posX += p_doDraw_1_;
    }

    private int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow) {
        if (this.bidiFlag) {
            int i = this.getStringWidth(this.bidiReorder(text));
            x = x + width - i;
        }

        return this.renderString(text, (float) x, (float) y, color, dropShadow);
    }

    private int renderString(String text, float x, float y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        } else {
            if (this.bidiFlag) {
                text = this.bidiReorder(text);
            }

            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            if (dropShadow) {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            this.red = (float) (color >> 16 & 255) / 255.0F;
            this.blue = (float) (color >> 8 & 255) / 255.0F;
            this.green = (float) (color & 255) / 255.0F;
            this.alpha = (float) (color >> 24 & 255) / 255.0F;
            this.setColor(this.red, this.blue, this.green, this.alpha);
            this.posX = x;
            this.posY = y;
            this.renderStringAtPos(text, dropShadow);
            return (int) this.posX;
        }
    }

    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        } else {
            text = DrawTextHookManager.hookMethod(text).getDisplayText();
            float f = 0.0F;
            boolean flag = false;

            for (int i = 0; i < text.length(); ++i) {
                char c0 = text.charAt(i);
                float f1 = this.getCharWidthFloat(c0);

                if (f1 < 0.0F && i < text.length() - 1) {
                    ++i;
                    c0 = text.charAt(i);

                    if (c0 != 108 && c0 != 76) {
                        if (c0 == 114 || c0 == 82) {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }

                    f1 = 0.0F;
                }

                f += f1;

                if (flag && f1 > 0.0F) {
                    f += this.unicodeFlag ? 1.0F : this.offsetBold;
                }
            }

            return Math.round(f);
        }
    }

    public int getCharWidth(char character) {
        return Math.round(this.getCharWidthFloat(character));
    }

    protected float getCharWidthFloat(char character) {
        // 颜色符号
        if (character == 167) {
            return -1.0F;
        }

        // 空格
        if (character == ' ' || character == 160) {
            return this.charWidthFloat[32]; // 返回空格宽度
        }

        int index = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(character);

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

            return (float) ((right - left + 1) / 2 + 1);
        }

        return 0.0F;
    }


    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidth(String text, int width, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();
        float f = 0.0F;
        int i = reverse ? text.length() - 1 : 0;
        int j = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int k = i; k >= 0 && k < text.length() && f < (float) width; k += j) {
            char c0 = text.charAt(k);
            float f1 = this.getCharWidthFloat(c0);

            if (flag) {
                flag = false;

                if (c0 != 108 && c0 != 76) {
                    if (c0 == 114 || c0 == 82) {
                        flag1 = false;
                    }
                } else {
                    flag1 = true;
                }
            } else if (f1 < 0.0F) {
                flag = true;
            } else {
                f += f1;

                if (flag1) {
                    ++f;
                }
            }

            if (f > (float) width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c0);
            } else {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
        if (this.blend) {
            GlStateManager.getBlendState(this.oldBlendState);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }

        this.resetStyles();
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x, y, wrapWidth, false);

        if (this.blend) {
            GlStateManager.setBlendState(this.oldBlendState);
        }
    }

    private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
        for (String s : this.listFormattedStringToWidth(str, wrapWidth)) {
            this.renderStringAligned(s, x, y, wrapWidth, this.textColor, addShadow);
            y += this.getHeight();
        }
    }

    public int splitStringWidth(String str, int maxLength) {
        return this.getHeight() * this.listFormattedStringToWidth(str, maxLength).size();
    }

    public void setUnicodeFlag(boolean unicodeFlagIn) {
        this.unicodeFlag = unicodeFlagIn;
    }

    public boolean getUnicodeFlag() {
        return this.unicodeFlag;
    }

    public void setBidiFlag(boolean bidiFlagIn) {
        this.bidiFlag = bidiFlagIn;
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    String wrapFormattedStringToWidth(String str, int wrapWidth) {
        if (str.length() <= 1) {
            return str;
        } else {
            int i = this.sizeStringToWidth(str, wrapWidth);

            if (str.length() <= i) {
                return str;
            } else {
                String s = str.substring(0, i);
                char c0 = str.charAt(i);
                boolean flag = c0 == 32 || c0 == 10;
                String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
                return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
            }
        }
    }

    private int sizeStringToWidth(String str, int wrapWidth) {
        int i = str.length();
        float f = 0.0F;
        int j = 0;
        int k = -1;

        for (boolean flag = false; j < i; ++j) {
            char c0 = str.charAt(j);

            switch (c0) {
                case '\n':
                    --j;
                    break;

                case ' ':
                    k = j;

                default:
                    f += (float) this.getCharWidth(c0);

                    if (flag) {
                        ++f;
                    }

                    break;

                case '\u00a7':
                    if (j < i - 1) {
                        ++j;
                        char c1 = str.charAt(j);

                        if (c1 != 108 && c1 != 76) {
                            if (c1 == 114 || c1 == 82 || isFormatColor(c1)) {
                                flag = false;
                            }
                        } else {
                            flag = true;
                        }
                    }
            }

            if (c0 == 10) {
                ++j;
                k = j;
                break;
            }

            if (Math.round(f) > wrapWidth) {
                break;
            }
        }

        return j != i && k != -1 && k < j ? k : j;
    }

    private static boolean isFormatColor(char colorChar) {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
    }

    private static boolean isFormatSpecial(char formatChar) {
        return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114 || formatChar == 82;
    }

    public static String getFormatFromString(String text) {
        String s = "";
        int i = -1;
        int j = text.length();

        while ((i = text.indexOf(167, i + 1)) != -1) {
            if (i < j - 1) {
                char c0 = text.charAt(i + 1);

                if (isFormatColor(c0)) {
                    s = "\u00a7" + c0;
                } else if (isFormatSpecial(c0)) {
                    s = s + "\u00a7" + c0;
                }
            }
        }

        return s;
    }

    public boolean getBidiFlag() {
        return this.bidiFlag;
    }

    public int getColorCode(char character) {
        int i = "0123456789abcdef".indexOf(character);

        if (i >= 0 && i < this.colorCode.length) {
            int j = this.colorCode[i];

            if (Config.isCustomColors()) {
                j = CustomColors.getTextColor(i, j);

            }

            return j;
        } else {
            return 16777215;
        }
    }

    protected void setColor(float p_setColor_1_, float p_setColor_2_, float p_setColor_3_, float p_setColor_4_) {
        GlStateManager.color(p_setColor_1_, p_setColor_2_, p_setColor_3_, p_setColor_4_);
    }

    protected void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    protected void bindTexture(ResourceLocation p_bindTexture_1_) {
        this.renderEngine.bindTexture(p_bindTexture_1_);
    }

    protected InputStream getResourceInputStream(ResourceLocation p_getResourceInputStream_1_) throws IOException {
        return Minecraft.getMinecraft().getResourceManager().getResource(p_getResourceInputStream_1_).getInputStream();
    }

    public int getHeight() {
        return 9;
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2F, y, color);
    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawStringWithShadow(text, (x - getStringWidth(text) / 2F), y, color);
    }
}
