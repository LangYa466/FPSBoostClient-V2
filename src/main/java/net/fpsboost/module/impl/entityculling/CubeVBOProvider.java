package net.fpsboost.module.impl.entityculling;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * CubeVBOProvider - 修改后采用持久映射(Persistent Mapping)实现
 *
 * 修改说明：
 * 1. 使用了 GL_MAP_PERSISTENT_BIT 和 GL_MAP_COHERENT_BIT 标志，确保 VBO 一经映射后始终保持映射状态，
 *    不再每次写入数据后解除映射。
 * 2. 新增了一个 Map 用于保存每个 VBO 的持久映射 ByteBuffer，以便后续可以直接更新数据（如果需要）。
 * 3. 在 clearVBOs() 方法中，对持久映射的 VBO 先解除映射，再删除缓冲区（这一步可选，根据使用场景）。
 *
 * @author LangYa466
 * @since recode 2/19/2025 (modified for persistent mapping)
 */
public class CubeVBOProvider {
    private static final CubeVBOProvider INSTANCE = new CubeVBOProvider();
    private static final int GL_DRAW_MODE = GL11.GL_TRIANGLE_STRIP;
    private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormats.POSITION;
    private static final int TEN_BITS = 0b1111111111; // 10位掩码

    private final Map<Integer, Integer> vbos = new HashMap<>();
    // 新增：保存持久映射的 ByteBuffer 指针，key 为 VBO ID
    private final Map<Integer, ByteBuffer> persistentMappedBuffers = new HashMap<>();

    private int lastVBO = -1;

    private ByteBuffer tmpBuffer = BufferUtils.createByteBuffer(1024 * 1024 * 10); // 10MB

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
            // 如果存在持久映射的 Buffer，则解除映射（根据需要，可选择是否解除映射）
            if (persistentMappedBuffers.containsKey(vboId)) {
                OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, vboId);
                GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
            }
            OpenGlHelper.glDeleteBuffers(vboId);
        }
        vbos.clear();
        persistentMappedBuffers.clear();
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


        if (GLContext.getCapabilities().GL_ARB_buffer_storage) {
            // 持久映射常量
            int persistentFlags = GL30.GL_MAP_WRITE_BIT | GL44.GL_MAP_PERSISTENT_BIT | GL44.GL_MAP_COHERENT_BIT;
            ARBBufferStorage.glBufferStorage(GL15.GL_ARRAY_BUFFER, size, persistentFlags);

            ByteBuffer mappedBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, 0L, (long) size, persistentFlags, null);
            if (mappedBuffer == null) {
                throw new RuntimeException("Failed to map persistent buffer for VBO");
            }
            mappedBuffer.put(vertexData);
            mappedBuffer.flip();
            persistentMappedBuffers.put(bufferId, mappedBuffer);
        } else {
            // 回退方案
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, GL15.GL_STATIC_DRAW);
            ByteBuffer tempBuffer = GL30.glMapBufferRange(GL15.GL_ARRAY_BUFFER, 0L, (long) size, GL30.GL_MAP_WRITE_BIT, null);
            if (tempBuffer != null) {
                tempBuffer.put(vertexData);
                tempBuffer.flip();
                GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
            } else {
                throw new RuntimeException("Failed to map buffer for VBO");
            }
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
        if (tmpBuffer.capacity() < bufferBuilder.getByteBuffer().capacity()) {
            tmpBuffer = BufferUtils.createByteBuffer(bufferBuilder.getByteBuffer().capacity());
        }
        tmpBuffer.put(bufferBuilder.getByteBuffer());
        tmpBuffer.flip();
        return tmpBuffer;
    }
}
