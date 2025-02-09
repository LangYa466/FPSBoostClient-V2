package net.fpsboost.util.skinlayers3d;

import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public final class NativeImage implements AutoCloseable
{
    private final Format format;
    private final int width;
    private final int height;
    private ByteBuffer buffer;
    private final int size;

    public NativeImage(final int i, final int j, final boolean bl) {
        this(Format.RGBA, i, j, bl);
    }

    public NativeImage(final Format format, final int i, final int j, final boolean bl) {
        if (i <= 0 || j <= 0) {
            throw new IllegalArgumentException("Invalid texture size: " + i + "x" + j);
        }
        this.format = format;
        this.width = i;
        this.height = j;
        this.size = i * j * format.components();
        this.buffer = ByteBuffer.allocateDirect(this.size);
    }

    private boolean isOutsideBounds(final int i, final int j) {
        return i < 0 || i >= this.width || j < 0 || j >= this.height;
    }

    @Override
    public void close() {
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format format() {
        return this.format;
    }

    public int getPixelRGBA(final int i, final int j) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
        }
        if (this.isOutsideBounds(i, j)) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        final int l = (i + j * this.width) * 4;
        return this.buffer.getInt(l);
    }

    public void setPixelRGBA(final int i, final int j, final int k) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
        }
        if (this.isOutsideBounds(i, j)) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        final int l = (i + j * this.width) * 4;
        this.buffer.putInt(l, k);
    }

    public byte getLuminanceOrAlpha(final int i, final int j) {
        if (!this.format.hasLuminanceOrAlpha()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.format));
        }
        if (this.isOutsideBounds(i, j)) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        final int k = (i + j * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
        return this.buffer.get(k);
    }

    public void downloadTexture(final int i, final boolean bl) {
        this.format.setPackPixelStoreState();
        GL11.glGetTexImage(3553, i, this.format.glFormat(), 5121, this.buffer);
        if (bl && this.format.hasAlpha()) {
            for (int j = 0; j < this.getHeight(); ++j) {
                for (int k = 0; k < this.getWidth(); ++k) {
                    this.setPixelRGBA(k, j, this.getPixelRGBA(k, j) | 255 << this.format.alphaOffset());
                }
            }
        }
    }

    public static int getA(final int i) {
        return i >> 24 & 0xFF;
    }

    public static int getR(final int i) {
        return i >> 0 & 0xFF;
    }

    public static int getG(final int i) {
        return i >> 8 & 0xFF;
    }

    public static int getB(final int i) {
        return i >> 16 & 0xFF;
    }

    public static int combine(final int i, final int j, final int k, final int l) {
        return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | (l & 0xFF) << 0;
    }

    public enum InternalGlFormat
    {
        RGBA(6408),
        RGB(6407),
        RG(33319),
        RED(6403);

        private final int glFormat;

        private InternalGlFormat(final int j) {
            this.glFormat = j;
        }

        public int glFormat() {
            return this.glFormat;
        }
    }

    public enum Format
    {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, 33319, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, 6403, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        final int components;
        private final int glFormat;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceOffset;
        private final int alphaOffset;
        private final boolean supportedByStb;

        private Format(final int j, final int k, final boolean bl, final boolean bl2, final boolean bl3, final boolean bl4, final boolean bl5, final int l, final int m, final int n, final int o, final int p, final boolean bl6) {
            this.components = j;
            this.glFormat = k;
            this.hasRed = bl;
            this.hasGreen = bl2;
            this.hasBlue = bl3;
            this.hasLuminance = bl4;
            this.hasAlpha = bl5;
            this.redOffset = l;
            this.greenOffset = m;
            this.blueOffset = n;
            this.luminanceOffset = o;
            this.alphaOffset = p;
            this.supportedByStb = bl6;
        }

        public int components() {
            return this.components;
        }

        public void setPackPixelStoreState() {
            GL11.glPixelStorei(3333, this.components());
        }

        public void setUnpackPixelStoreState() {
            GL11.glPixelStorei(3317, this.components());
        }

        public int glFormat() {
            return this.glFormat;
        }

        public boolean hasRed() {
            return this.hasRed;
        }

        public boolean hasGreen() {
            return this.hasGreen;
        }

        public boolean hasBlue() {
            return this.hasBlue;
        }

        public boolean hasLuminance() {
            return this.hasLuminance;
        }

        public boolean hasAlpha() {
            return this.hasAlpha;
        }

        public int redOffset() {
            return this.redOffset;
        }

        public int greenOffset() {
            return this.greenOffset;
        }

        public int blueOffset() {
            return this.blueOffset;
        }

        public int luminanceOffset() {
            return this.luminanceOffset;
        }

        public int alphaOffset() {
            return this.alphaOffset;
        }

        public boolean hasLuminanceOrRed() {
            return this.hasLuminance || this.hasRed;
        }

        public boolean hasLuminanceOrGreen() {
            return this.hasLuminance || this.hasGreen;
        }

        public boolean hasLuminanceOrBlue() {
            return this.hasLuminance || this.hasBlue;
        }

        public boolean hasLuminanceOrAlpha() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int luminanceOrRedOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.redOffset;
        }

        public int luminanceOrGreenOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
        }

        public int luminanceOrBlueOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
        }

        public int luminanceOrAlphaOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
        }

        public boolean supportedByStb() {
            return this.supportedByStb;
        }

        static Format getStbFormat(final int i) {
            switch (i) {
                case 1: {
                    return Format.LUMINANCE;
                }
                case 2: {
                    return Format.LUMINANCE_ALPHA;
                }
                case 3: {
                    return Format.RGB;
                }
                default: {
                    return Format.RGBA;
                }
            }
        }
    }
}
