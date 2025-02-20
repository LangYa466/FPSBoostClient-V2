package net.minecraft.client.renderer;

import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.optifine.SmartAnimations;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;

public class WorldRenderer {
    // 内部缓冲区及视图
    private ByteBuffer byteBuffer;
    public IntBuffer rawIntBuffer;
    private ShortBuffer rawShortBuffer;
    public FloatBuffer rawFloatBuffer;

    // 顶点计数及格式控制
    public int vertexCount;
    private VertexFormatElement vertexFormatElement;
    private int vertexFormatIndex;
    private boolean noColor;

    public int drawMode;
    private double xOffset, yOffset, zOffset;
    @Getter
    private VertexFormat vertexFormat;
    private boolean isDrawing;
    private EnumWorldBlockLayer blockLayer = null;

    // 多重纹理支持
    private boolean[] drawnIcons = new boolean[256];
    private TextureAtlasSprite[] quadSprites = null;
    private TextureAtlasSprite[] quadSpritesPrev = null;
    private TextureAtlasSprite quadSprite = null;

    // Optifine 着色器相关
    public SVertexBuilder sVertexBuilder;
    public RenderEnv renderEnv = null;
    public BitSet animatedSprites = null;
    public BitSet animatedSpritesCached = new BitSet();

    // 三角形模式相关
    private boolean modeTriangles = false;
    private ByteBuffer byteBufferTriangles;

    public WorldRenderer(int bufferSizeIn) {
        this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
        this.rawIntBuffer = this.byteBuffer.asIntBuffer();
        this.rawShortBuffer = this.byteBuffer.asShortBuffer();
        this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
        SVertexBuilder.initVertexBuilder(this);
    }

    /**
     * 检查并扩展内部缓冲区，保证剩余空间足够存放新数据
     */
    private void growBuffer(int additionalInts) {
        if (additionalInts > this.rawIntBuffer.remaining()) {
            int currentCapacity = this.byteBuffer.capacity();
            int requiredSize = this.rawIntBuffer.position() + additionalInts;
            int newSize = Math.max(currentCapacity * 2, requiredSize);
            // TODO: 可以在此处输出日志记录缓冲区扩展情况
            int originalPosition = this.rawIntBuffer.position();
            ByteBuffer newByteBuffer = GLAllocation.createDirectByteBuffer(newSize);
            this.byteBuffer.position(0);
            newByteBuffer.put(this.byteBuffer);
            newByteBuffer.rewind();
            this.byteBuffer = newByteBuffer;
            this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
            this.rawIntBuffer = this.byteBuffer.asIntBuffer();
            this.rawIntBuffer.position(originalPosition);
            this.rawShortBuffer = this.byteBuffer.asShortBuffer();
            this.rawShortBuffer.position(originalPosition << 1);

            // 如果quadSprites已存在，则更新其大小
            if (this.quadSprites != null) {
                TextureAtlasSprite[] oldQuadSprites = this.quadSprites;
                int newQuadSize = this.getBufferQuadSize();
                this.quadSprites = new TextureAtlasSprite[newQuadSize];
                System.arraycopy(oldQuadSprites, 0, this.quadSprites, 0, Math.min(oldQuadSprites.length, this.quadSprites.length));
                this.quadSpritesPrev = null;
            }
        }
    }

    /**
     * 根据摄像机位置对顶点数据排序，利用lambda表达式与BitSet优化数据交换过程
     */
    public void sortVertexData(float camX, float camY, float camZ) {
        int quadCount = this.vertexCount / 4;
        final float[] distances = new float[quadCount];
        for (int i = 0; i < quadCount; i++) {
            distances[i] = getDistanceSq(this.rawFloatBuffer,
                    (float)(camX + this.xOffset), (float)(camY + this.yOffset), (float)(camZ + this.zOffset),
                    this.vertexFormat.getIntegerSize(), i * this.vertexFormat.getNextOffset());
        }

        Integer[] indices = new Integer[quadCount];
        for (int i = 0; i < quadCount; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (a, b) -> Float.compare(distances[b], distances[a]));

        BitSet processed = new BitSet();
        int stride = this.vertexFormat.getNextOffset();
        int[] temp = new int[stride];

        for (int i = processed.nextClearBit(0); i < indices.length; i = processed.nextClearBit(i + 1)) {
            int current = indices[i];
            if (current != i) {
                // 备份当前顶点数据
                this.rawIntBuffer.limit(current * stride + stride);
                this.rawIntBuffer.position(current * stride);
                this.rawIntBuffer.get(temp);

                int swapIndex = current;
                while (swapIndex != i) {
                    int nextIndex = indices[swapIndex];
                    this.rawIntBuffer.limit(nextIndex * stride + stride);
                    this.rawIntBuffer.position(nextIndex * stride);
                    IntBuffer slice = this.rawIntBuffer.slice();

                    this.rawIntBuffer.limit(swapIndex * stride + stride);
                    this.rawIntBuffer.position(swapIndex * stride);
                    this.rawIntBuffer.put(slice);

                    processed.set(swapIndex);
                    swapIndex = nextIndex;
                }
                this.rawIntBuffer.limit(i * stride + stride);
                this.rawIntBuffer.position(i * stride);
                this.rawIntBuffer.put(temp);
            }
            processed.set(i);
        }
        // 恢复buffer状态
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(this.getBufferSize());

        // 若quadSprites存在，则按相同顺序交换
        if (this.quadSprites != null) {
            TextureAtlasSprite[] sortedSprites = new TextureAtlasSprite[quadCount];
            for (int i = 0; i < indices.length; i++) {
                sortedSprites[i] = this.quadSprites[indices[i]];
            }
            System.arraycopy(sortedSprites, 0, this.quadSprites, 0, sortedSprites.length);
        }
    }

    public State getVertexState() {
        this.rawIntBuffer.rewind();
        int size = this.getBufferSize();
        this.rawIntBuffer.limit(size);
        int[] stateBuffer = new int[size];
        this.rawIntBuffer.get(stateBuffer);
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(size);
        TextureAtlasSprite[] stateQuadSprites = null;
        if (this.quadSprites != null) {
            int quadCount = this.vertexCount / 4;
            stateQuadSprites = new TextureAtlasSprite[quadCount];
            System.arraycopy(this.quadSprites, 0, stateQuadSprites, 0, quadCount);
        }
        return new State(stateBuffer, new VertexFormat(this.vertexFormat), stateQuadSprites);
    }

    public int getBufferSize() {
        return this.vertexCount * this.vertexFormat.getIntegerSize();
    }

    /**
     * 根据四个顶点数据计算平均位置与摄像机距离的平方
     */
    private static float getDistanceSq(FloatBuffer buffer, float camX, float camY, float camZ, int stride, int offset) {
        float x0 = buffer.get(offset);
        float y0 = buffer.get(offset + 1);
        float z0 = buffer.get(offset + 2);

        float x1 = buffer.get(offset + stride);
        float y1 = buffer.get(offset + stride + 1);
        float z1 = buffer.get(offset + stride + 2);

        float x2 = buffer.get(offset + stride * 2);
        float y2 = buffer.get(offset + stride * 2 + 1);
        float z2 = buffer.get(offset + stride * 2 + 2);

        float x3 = buffer.get(offset + stride * 3);
        float y3 = buffer.get(offset + stride * 3 + 1);
        float z3 = buffer.get(offset + stride * 3 + 2);

        float avgX = (x0 + x1 + x2 + x3) * 0.25F - camX;
        float avgY = (y0 + y1 + y2 + y3) * 0.25F - camY;
        float avgZ = (z0 + z1 + z2 + z3) * 0.25F - camZ;

        return avgX * avgX + avgY * avgY + avgZ * avgZ;
    }

    public void setVertexState(State state) {
        this.rawIntBuffer.clear();
        this.growBuffer(state.getRawBuffer().length);
        this.rawIntBuffer.put(state.getRawBuffer());
        this.vertexCount = state.getVertexCount();
        this.vertexFormat = new VertexFormat(state.getVertexFormat());
        if (state.stateQuadSprites != null) {
            if (this.quadSprites == null) {
                this.quadSprites = this.quadSpritesPrev;
            }
            if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
                this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
            }
            TextureAtlasSprite[] sprites = state.stateQuadSprites;
            System.arraycopy(sprites, 0, this.quadSprites, 0, sprites.length);
        } else {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }
            this.quadSprites = null;
        }
    }

    public void reset() {
        this.vertexCount = 0;
        this.vertexFormatElement = null;
        this.vertexFormatIndex = 0;
        this.quadSprite = null;
        if (SmartAnimations.isActive()) {
            if (this.animatedSprites == null) {
                this.animatedSprites = this.animatedSpritesCached;
            }
            this.animatedSprites.clear();
        } else if (this.animatedSprites != null) {
            this.animatedSprites = null;
        }
        this.modeTriangles = false;
    }

    public void begin(int glMode, VertexFormat format) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already building!");
        }
        this.isDrawing = true;
        this.reset();
        this.drawMode = glMode;
        this.vertexFormat = format;
        this.vertexFormatElement = format.getElement(this.vertexFormatIndex);
        this.noColor = false;
        this.byteBuffer.limit(this.byteBuffer.capacity());
        if (Config.isShaders()) {
            SVertexBuilder.endSetVertexFormat(this);
        }
        if (Config.isMultiTexture()) {
            if (this.blockLayer != null) {
                if (this.quadSprites == null) {
                    this.quadSprites = this.quadSpritesPrev;
                }
                if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
                    this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
                }
            }
        } else {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }
            this.quadSprites = null;
        }
    }

    public WorldRenderer tex(double u, double v) {
        if (this.quadSprite != null && this.quadSprites != null) {
            u = this.quadSprite.toSingleU((float) u);
            v = this.quadSprite.toSingleV((float) v);
            this.quadSprites[this.vertexCount / 4] = this.quadSprite;
        }
        int index = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(index, (float) u);
                this.byteBuffer.putFloat(index + 4, (float) v);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(index, (int) u);
                this.byteBuffer.putInt(index + 4, (int) v);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(index, (short) ((int) v));
                this.byteBuffer.putShort(index + 2, (short) ((int) u));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(index, (byte) ((int) v));
                this.byteBuffer.put(index + 1, (byte) ((int) u));
                break;
        }
        nextVertexFormatIndex();
        return this;
    }

    public WorldRenderer lightmap(int u, int v) {
        int index = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(index, (float) u);
                this.byteBuffer.putFloat(index + 4, (float) v);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(index, u);
                this.byteBuffer.putInt(index + 4, v);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(index, (short) v);
                this.byteBuffer.putShort(index + 2, (short) u);
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(index, (byte) v);
                this.byteBuffer.put(index + 1, (byte) u);
                break;
        }
        nextVertexFormatIndex();
        return this;
    }

    public void putBrightness4(int br1, int br2, int br3, int br4) {
        int index = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() +
                this.vertexFormat.getUvOffsetById(1) / 4;
        int offset = this.vertexFormat.getNextOffset() >> 2;
        this.rawIntBuffer.put(index, br1);
        this.rawIntBuffer.put(index + offset, br2);
        this.rawIntBuffer.put(index + offset * 2, br3);
        this.rawIntBuffer.put(index + offset * 3, br4);
    }

    public void putPosition(double x, double y, double z) {
        int intSize = this.vertexFormat.getIntegerSize();
        int baseIndex = (this.vertexCount - 4) * intSize;
        for (int i = 0; i < 4; i++) {
            int posIndex = baseIndex + i * intSize;
            // 假定顶点位置位于posIndex处（posIndex, posIndex+1, posIndex+2）
            float origX = Float.intBitsToFloat(this.rawIntBuffer.get(posIndex));
            float origY = Float.intBitsToFloat(this.rawIntBuffer.get(posIndex + 1));
            float origZ = Float.intBitsToFloat(this.rawIntBuffer.get(posIndex + 2));
            this.rawIntBuffer.put(posIndex, Float.floatToRawIntBits((float)(x + this.xOffset) + origX));
            this.rawIntBuffer.put(posIndex + 1, Float.floatToRawIntBits((float)(y + this.yOffset) + origY));
            this.rawIntBuffer.put(posIndex + 2, Float.floatToRawIntBits((float)(z + this.zOffset) + origZ));
        }
    }

    public int getColorIndex(int vertexOffset) {
        return ((this.vertexCount - vertexOffset) * this.vertexFormat.getNextOffset() +
                this.vertexFormat.getColorOffset()) / 4;
    }

    public void putColorMultiplier(float red, float green, float blue, int vertexOffset) {
        int index = this.getColorIndex(vertexOffset);
        int color = this.rawIntBuffer.get(index);
        if (!this.noColor) {
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                int r = (int) ((color & 0xFF) * red);
                int g = (int) (((color >> 8) & 0xFF) * green);
                int b = (int) (((color >> 16) & 0xFF) * blue);
                color = (color & 0xFF000000) | (b << 16) | (g << 8) | r;
            } else {
                int r = (int) (((color >> 24) & 0xFF) * red);
                int g = (int) (((color >> 16) & 0xFF) * green);
                int b = (int) (((color >> 8) & 0xFF) * blue);
                color = (color & 0xFF) | (r << 24) | (g << 16) | (b << 8);
            }
        }
        this.rawIntBuffer.put(index, color);
    }

    private void putColor(int argb, int vertexOffset) {
        int index = this.getColorIndex(vertexOffset);
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int a = (argb >> 24) & 0xFF;
        putColorRGBA(index, r, g, b, a);
    }

    public void putColorRGB_F(float red, float green, float blue, int vertexOffset) {
        int index = this.getColorIndex(vertexOffset);
        int r = MathHelper.clamp_int((int)(red * 255.0F), 0, 255);
        int g = MathHelper.clamp_int((int)(green * 255.0F), 0, 255);
        int b = MathHelper.clamp_int((int)(blue * 255.0F), 0, 255);
        putColorRGBA(index, r, g, b, 255);
    }

    public void putColorRGBA(int index, int red, int green, int blue, int alpha) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.rawIntBuffer.put(index, (alpha << 24) | (blue << 16) | (green << 8) | red);
        } else {
            this.rawIntBuffer.put(index, (red << 24) | (green << 16) | (blue << 8) | alpha);
        }
    }

    public void noColor() {
        this.noColor = true;
    }

    public WorldRenderer color(float red, float green, float blue, float alpha) {
        return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
    }

    public WorldRenderer color(int red, int green, int blue, int alpha) {
        if (this.noColor) {
            return this;
        }
        int index = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(index, red / 255.0F);
                this.byteBuffer.putFloat(index + 4, green / 255.0F);
                this.byteBuffer.putFloat(index + 8, blue / 255.0F);
                this.byteBuffer.putFloat(index + 12, alpha / 255.0F);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putFloat(index, red);
                this.byteBuffer.putFloat(index + 4, green);
                this.byteBuffer.putFloat(index + 8, blue);
                this.byteBuffer.putFloat(index + 12, alpha);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(index, (short) red);
                this.byteBuffer.putShort(index + 2, (short) green);
                this.byteBuffer.putShort(index + 4, (short) blue);
                this.byteBuffer.putShort(index + 6, (short) alpha);
                break;
            case UBYTE:
            case BYTE:
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    this.byteBuffer.put(index, (byte) red);
                    this.byteBuffer.put(index + 1, (byte) green);
                    this.byteBuffer.put(index + 2, (byte) blue);
                    this.byteBuffer.put(index + 3, (byte) alpha);
                } else {
                    this.byteBuffer.put(index, (byte) alpha);
                    this.byteBuffer.put(index + 1, (byte) blue);
                    this.byteBuffer.put(index + 2, (byte) green);
                    this.byteBuffer.put(index + 3, (byte) red);
                }
                break;
        }
        nextVertexFormatIndex();
        return this;
    }

    public void addVertexData(int[] vertexData) {
        if (Config.isShaders()) {
            SVertexBuilder.beginAddVertexData(this, vertexData);
        }
        this.growBuffer(vertexData.length);
        this.rawIntBuffer.position(this.getBufferSize());
        this.rawIntBuffer.put(vertexData);
        this.vertexCount += vertexData.length / this.vertexFormat.getIntegerSize();
        if (Config.isShaders()) {
            SVertexBuilder.endAddVertexData(this);
        }
    }

    public void endVertex() {
        ++this.vertexCount;
        this.growBuffer(this.vertexFormat.getIntegerSize());
        this.vertexFormatIndex = 0;
        this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
        if (Config.isShaders()) {
            SVertexBuilder.endAddVertex(this);
        }
    }

    public WorldRenderer pos(double x, double y, double z) {
        if (Config.isShaders()) {
            SVertexBuilder.beginAddVertex(this);
        }
        int index = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(index, (float)(x + this.xOffset));
                this.byteBuffer.putFloat(index + 4, (float)(y + this.yOffset));
                this.byteBuffer.putFloat(index + 8, (float)(z + this.zOffset));
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(index, Float.floatToRawIntBits((float)(x + this.xOffset)));
                this.byteBuffer.putInt(index + 4, Float.floatToRawIntBits((float)(y + this.yOffset)));
                this.byteBuffer.putInt(index + 8, Float.floatToRawIntBits((float)(z + this.zOffset)));
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(index, (short)((int)(x + this.xOffset)));
                this.byteBuffer.putShort(index + 2, (short)((int)(y + this.yOffset)));
                this.byteBuffer.putShort(index + 4, (short)((int)(z + this.zOffset)));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(index, (byte)((int)(x + this.xOffset)));
                this.byteBuffer.put(index + 1, (byte)((int)(y + this.yOffset)));
                this.byteBuffer.put(index + 2, (byte)((int)(z + this.zOffset)));
                break;
        }
        nextVertexFormatIndex();
        return this;
    }

    public void putNormal(float x, float y, float z) {
        int packedNormal = ((byte)((int)(x * 127.0F)) & 0xFF)
                | (((byte)((int)(y * 127.0F)) & 0xFF) << 8)
                | (((byte)((int)(z * 127.0F)) & 0xFF) << 16);
        int stride = this.vertexFormat.getNextOffset() >> 2;
        int baseIndex = (this.vertexCount - 4) * stride + this.vertexFormat.getNormalOffset() / 4;
        for (int i = 0; i < 4; i++) {
            this.rawIntBuffer.put(baseIndex + i * stride, packedNormal);
        }
    }

    private void nextVertexFormatIndex() {
        ++this.vertexFormatIndex;
        this.vertexFormatIndex %= this.vertexFormat.getElementCount();
        this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
        if (this.vertexFormatElement.getUsage() == VertexFormatElement.EnumUsage.PADDING) {
            nextVertexFormatIndex();
        }
    }

    public WorldRenderer normal(float x, float y, float z) {
        int index = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
        switch (this.vertexFormatElement.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(index, x);
                this.byteBuffer.putFloat(index + 4, y);
                this.byteBuffer.putFloat(index + 8, z);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(index, (int)x);
                this.byteBuffer.putInt(index + 4, (int)y);
                this.byteBuffer.putInt(index + 8, (int)z);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(index, (short)((int)(x * 32767.0F) & 0xFFFF));
                this.byteBuffer.putShort(index + 2, (short)((int)(y * 32767.0F) & 0xFFFF));
                this.byteBuffer.putShort(index + 4, (short)((int)(z * 32767.0F) & 0xFFFF));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(index, (byte)((int)(x * 127.0F) & 0xFF));
                this.byteBuffer.put(index + 1, (byte)((int)(y * 127.0F) & 0xFF));
                this.byteBuffer.put(index + 2, (byte)((int)(z * 127.0F) & 0xFF));
                break;
        }
        nextVertexFormatIndex();
        return this;
    }

    public void setTranslation(double x, double y, double z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
    }

    public void finishDrawing() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not building!");
        }
        this.isDrawing = false;
        this.byteBuffer.position(0);
        this.byteBuffer.limit(this.getBufferSize() * 4);
    }

    public ByteBuffer getByteBuffer() {
        return this.modeTriangles ? this.byteBufferTriangles : this.byteBuffer;
    }

    public int getVertexCount() {
        return this.modeTriangles ? (this.vertexCount / 4 * 6) : this.vertexCount;
    }

    public int getDrawMode() {
        return this.modeTriangles ? GL11.GL_TRIANGLES : this.drawMode;
    }

    public void putColor4(int argb) {
        for (int i = 0; i < 4; i++) {
            this.putColor(argb, i + 1);
        }
    }

    public void putColorRGB_F4(float red, float green, float blue) {
        for (int i = 0; i < 4; i++) {
            this.putColorRGB_F(red, green, blue, i + 1);
        }
    }

    public void putSprite(TextureAtlasSprite sprite) {
        if (this.animatedSprites != null && sprite != null && sprite.getAnimationIndex() >= 0) {
            this.animatedSprites.set(sprite.getAnimationIndex());
        }
        if (this.quadSprites != null) {
            int index = this.vertexCount / 4;
            this.quadSprites[index - 1] = sprite;
        }
    }

    public void setSprite(TextureAtlasSprite sprite) {
        if (this.animatedSprites != null && sprite != null && sprite.getAnimationIndex() >= 0) {
            this.animatedSprites.set(sprite.getAnimationIndex());
        }
        if (this.quadSprites != null) {
            this.quadSprite = sprite;
        }
    }

    public boolean isMultiTexture() {
        return this.quadSprites != null;
    }

    public void drawMultiTexture() {
        if (this.quadSprites != null) {
            int spriteCount = Config.getMinecraft().getTextureMapBlocks().getCountRegisteredSprites();
            if (this.drawnIcons.length <= spriteCount) {
                this.drawnIcons = new boolean[spriteCount + 1];
            }
            Arrays.fill(this.drawnIcons, false);
            int drawCalls = 0;
            int specialIndex = -1;
            int quadCount = this.vertexCount / 4;
            for (int i = 0; i < quadCount; i++) {
                TextureAtlasSprite sprite = this.quadSprites[i];
                if (sprite != null) {
                    int spriteIndex = sprite.getIndexInMap();
                    if (!this.drawnIcons[spriteIndex]) {
                        if (sprite == TextureUtils.iconGrassSideOverlay) {
                            if (specialIndex < 0) {
                                specialIndex = i;
                            }
                        } else {
                            i = this.drawForIcon(sprite, i) - 1;
                            ++drawCalls;
                            if (this.blockLayer != EnumWorldBlockLayer.TRANSLUCENT) {
                                this.drawnIcons[spriteIndex] = true;
                            }
                        }
                    }
                }
            }
            if (specialIndex >= 0) {
                this.drawForIcon(TextureUtils.iconGrassSideOverlay, specialIndex);
                ++drawCalls;
            }
            // drawCalls可用于统计绘制调用数
        }
    }

    private int drawForIcon(TextureAtlasSprite sprite, int startIndex) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sprite.glSpriteTextureId);
        int firstUnmatched = -1;
        int matchedStart = -1;
        int quadCount = this.vertexCount / 4;
        for (int i = startIndex; i < quadCount; i++) {
            TextureAtlasSprite current = this.quadSprites[i];
            if (current == sprite) {
                if (matchedStart < 0) {
                    matchedStart = i;
                }
            } else if (matchedStart >= 0) {
                this.draw(matchedStart, i);
                if (this.blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
                    return i;
                }
                matchedStart = -1;
                if (firstUnmatched < 0) {
                    firstUnmatched = i;
                }
            }
        }
        if (matchedStart >= 0) {
            this.draw(matchedStart, quadCount);
        }
        if (firstUnmatched < 0) {
            firstUnmatched = quadCount;
        }
        return firstUnmatched;
    }

    private void draw(int startQuad, int endQuad) {
        int quadCount = endQuad - startQuad;
        if (quadCount > 0) {
            int start = startQuad * 4;
            int count = quadCount * 4;
            GL11.glDrawArrays(this.drawMode, start, count);
        }
    }

    public void setBlockLayer(EnumWorldBlockLayer layer) {
        this.blockLayer = layer;
        if (layer == null) {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }
            this.quadSprites = null;
            this.quadSprite = null;
        }
    }

    private int getBufferQuadSize() {
        int size = this.rawIntBuffer.capacity() * 4 / (this.vertexFormat.getIntegerSize() * 4);
        return size;
    }

    public RenderEnv getRenderEnv(IBlockState state, BlockPos pos) {
        if (this.renderEnv == null) {
            this.renderEnv = new RenderEnv(state, pos);
        } else {
            this.renderEnv.reset(state, pos);
        }
        return this.renderEnv;
    }

    public boolean isDrawing() {
        return this.isDrawing;
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public double getZOffset() {
        return this.zOffset;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return this.blockLayer;
    }

    public void putColorMultiplierRgba(float rMult, float gMult, float bMult, float aMult, int vertexOffset) {
        int index = this.getColorIndex(vertexOffset);
        int color = this.rawIntBuffer.get(index);
        if (!this.noColor) {
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                int r = (int) ((color & 0xFF) * rMult);
                int g = (int) (((color >> 8) & 0xFF) * gMult);
                int b = (int) (((color >> 16) & 0xFF) * bMult);
                int a = (int) (((color >> 24) & 0xFF) * aMult);
                color = (a << 24) | (b << 16) | (g << 8) | r;
            } else {
                int r = (int) (((color >> 24) & 0xFF) * rMult);
                int g = (int) (((color >> 16) & 0xFF) * gMult);
                int b = (int) (((color >> 8) & 0xFF) * bMult);
                int a = (int) ((color & 0xFF) * aMult);
                color = (r << 24) | (g << 16) | (b << 8) | a;
            }
        }
        this.rawIntBuffer.put(index, color);
    }

    public void quadsToTriangles() {
        if (this.drawMode == GL11.GL_QUADS) {
            if (this.byteBufferTriangles == null) {
                this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
            }
            if (this.byteBufferTriangles.capacity() < this.byteBuffer.capacity() * 2) {
                this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
            }
            int stride = this.vertexFormat.getNextOffset();
            int originalLimit = this.byteBuffer.limit();
            this.byteBuffer.rewind();
            this.byteBufferTriangles.clear();
            for (int quad = 0; quad < this.vertexCount; quad += 4) {
                // 复制四边形的第一个三角形（顶点0,1,2）
                this.byteBuffer.limit((quad + 3) * stride);
                this.byteBuffer.position(quad * stride);
                this.byteBufferTriangles.put(this.byteBuffer);
                // 复制第二个三角形（顶点0,2,3）
                this.byteBuffer.limit((quad + 1) * stride);
                this.byteBuffer.position(quad * stride);
                this.byteBufferTriangles.put(this.byteBuffer);
                this.byteBuffer.limit((quad + 2 + 2) * stride);
                this.byteBuffer.position((quad + 2) * stride);
                this.byteBufferTriangles.put(this.byteBuffer);
            }
            this.byteBuffer.limit(originalLimit);
            this.byteBuffer.rewind();
            this.byteBufferTriangles.flip();
            this.modeTriangles = true;
        }
    }

    public boolean isColorDisabled() {
        return this.noColor;
    }

    public class State {
        private final int[] stateRawBuffer;
        private final VertexFormat stateVertexFormat;
        private TextureAtlasSprite[] stateQuadSprites;

        public State(int[] buffer, VertexFormat format, TextureAtlasSprite[] quadSprites) {
            this.stateRawBuffer = buffer;
            this.stateVertexFormat = format;
            this.stateQuadSprites = quadSprites;
        }

        public State(int[] buffer, VertexFormat format) {
            this.stateRawBuffer = buffer;
            this.stateVertexFormat = format;
        }

        public int[] getRawBuffer() {
            return this.stateRawBuffer;
        }

        public int getVertexCount() {
            return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
        }

        public VertexFormat getVertexFormat() {
            return this.stateVertexFormat;
        }
    }
}
