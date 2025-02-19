package net.fpsboost.module.impl.entityculling;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LangYa
 * @since 2025/02/19
 */
public class CubeVBOProvider {

    private static final CubeVBOProvider INSTANCE = new CubeVBOProvider();
    private static final int GL_DRAW_MODE = GL11.GL_TRIANGLE_STRIP;
    private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormats.POSITION;
    private static final int TEN_BITS = 0b1111111111; // 10-bit masking for ID

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
        int vboId = getVBOId(xUnits, yUnits, zUnits);

        if (vbos.containsKey(vboId)) {
            return vbos.get(vboId);
        }

        int bufferId = OpenGlHelper.glGenBuffers();
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, bufferId);

        ByteBuffer vertexData = renderAABB(xUnits, yUnits, zUnits);
        OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);

        vbos.put(vboId, bufferId);
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

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bufferBuilder.getByteBuffer().remaining());
        byteBuffer.put(bufferBuilder.getByteBuffer());
        byteBuffer.flip();
        return byteBuffer;
    }
}
