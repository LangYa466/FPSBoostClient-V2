package net.fpsboost.module.impl.entityculling;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LangYa466
 * @since recode 2/19/2025
 */
public class CubeVBOProvider {
    private static final CubeVBOProvider INSTANCE = new CubeVBOProvider();
    private static final int GL_DRAW_MODE = GL11.GL_TRIANGLE_STRIP;
    private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormats.POSITION;
    private static final int TEN_BITS = 0b1111111111; // 10位掩码

    private final Map<Integer, Integer> vbos = new HashMap<>();
    private int lastVBO = -1;

    private CubeVBOProvider() {}

    public static CubeVBOProvider getInstance() {
        return INSTANCE;
    }

    public void resetVBO() {
        lastVBO = -1;
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
    }

    public void renderVBO(float xSize, float ySize, float zSize) {
        int xUnits = Math.round(xSize * 10);
        int yUnits = Math.round(ySize * 10);
        int zUnits = Math.round(zSize * 10);

        int vboId = getVBO(xUnits, yUnits, zUnits);
        if (lastVBO != vboId) {
            OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, vboId);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
            lastVBO = vboId;
        }

        GL11.glDrawArrays(GL_DRAW_MODE, 0, 14);
    }

    public void clearVBOs() {
        for (int vboId : vbos.values()) {
            OpenGlHelper.glDeleteBuffers(vboId);
        }
        vbos.clear();
        lastVBO = -1;
    }

    private int getVBOId(int xUnits, int yUnits, int zUnits) {
        return (xUnits & TEN_BITS) | ((yUnits & TEN_BITS) << 10) | ((zUnits & TEN_BITS) << 20);
    }

    private int getVBO(int xUnits, int yUnits, int zUnits) {
        int vboKey = getVBOId(xUnits, yUnits, zUnits);

        if (vbos.containsKey(vboKey)) {
            return vbos.get(vboKey);
        }

        int bufferId = OpenGlHelper.glGenBuffers();
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, bufferId);

        // 获取顶点数据
        ByteBuffer vertexData = renderAABB(xUnits, yUnits, zUnits);
        int size = vertexData.capacity();

        // 分配 VBO 内存：如果支持 GL_ARB_buffer_storage 则使用 glBufferStorage（不可变存储），否则使用 glBufferData
        if (GLContext.getCapabilities().GL_ARB_buffer_storage) {
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size,
                    GL30.GL_MAP_WRITE_BIT | GL30.GL_MAP_FLUSH_EXPLICIT_BIT);
        } else {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, GL15.GL_STATIC_DRAW);
        }

        // 映射 VBO 内存，注意传入额外的 null 参数满足 5 个参数版本的 glMapBufferRange
        ByteBuffer mappedBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, 0L, (long) size,
                GL30.GL_MAP_WRITE_BIT | GL30.GL_MAP_FLUSH_EXPLICIT_BIT, null);

        if (mappedBuffer != null) {
            // 写入顶点数据到映射缓冲区
            mappedBuffer.put(vertexData);
            // flip() 重置映射缓冲区的指针，使 limit = 写入的数据量
            mappedBuffer.flip();
            // 显式刷新映射区域，确保数据及时提交到显存
            int flushSize = mappedBuffer.remaining();
            GL30.glFlushMappedBufferRange(GL15.GL_ARRAY_BUFFER, 0, flushSize);
            GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        } else {
            throw new RuntimeException("Failed to map buffer for VBO");
        }

        vbos.put(vboKey, bufferId);
        return bufferId;
    }

    private ByteBuffer renderAABB(int xUnits, int yUnits, int zUnits) {
        WorldRenderer bufferBuilder = Tessellator.getInstance().getWorldRenderer();
        bufferBuilder.begin(GL_DRAW_MODE, VERTEX_FORMAT);

        float x = xUnits / 10.0f;
        float y = yUnits / 10.0f;
        float z = zUnits / 10.0f;

        bufferBuilder.pos(0, y, z).endVertex();
        bufferBuilder.pos(x, y, z).endVertex();
        bufferBuilder.pos(0, 0, z).endVertex();
        bufferBuilder.pos(x, 0, z).endVertex();
        bufferBuilder.pos(x, 0, 0).endVertex();
        bufferBuilder.pos(x, y, z).endVertex();
        bufferBuilder.pos(x, y, 0).endVertex();
        bufferBuilder.pos(0, y, z).endVertex();
        bufferBuilder.pos(0, y, 0).endVertex();
        bufferBuilder.pos(0, 0, z).endVertex();
        bufferBuilder.pos(0, 0, 0).endVertex();
        bufferBuilder.pos(x, 0, 0).endVertex();
        bufferBuilder.pos(0, y, 0).endVertex();
        bufferBuilder.pos(x, y, 0).endVertex();

        // 将缓冲区数据复制到新的 ByteBuffer，并 flip() 以便后续写入映射缓冲区
        ByteBuffer originalBuffer = bufferBuilder.getByteBuffer();
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(originalBuffer.remaining());
        byteBuffer.put(originalBuffer);
        byteBuffer.flip();
        return byteBuffer;
    }
}
