package net.minecraft.client.renderer.vertex;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.OpenGlHelper;
import net.optifine.render.VboRange;
import net.optifine.render.VboRegion;
import org.lwjgl.opengl.GL11;

public class VertexBuffer {
    private int glBufferId;
    private final VertexFormat vertexFormat;
    private int count;
    // Getter for VboRegion
    @Getter
    private VboRegion vboRegion;
    // Getter for VboRange
    @Getter
    private VboRange vboRange;
    // Setter for Draw Mode
    // Getter for Draw Mode
    @Setter
    @Getter
    private int drawMode;

    // Constructor
    public VertexBuffer(VertexFormat vertexFormatIn) {
        this.vertexFormat = vertexFormatIn;
        this.glBufferId = OpenGlHelper.glGenBuffers();
    }

    // Bind the buffer (optimize by caching and reducing state changes)
    public void bindBuffer() {
        if (this.glBufferId >= 0) {
            OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
        }
    }

    // Buffer data (optimize by avoiding redundant bufferData calls)
    public void bufferData(ByteBuffer p_181722_1_) {
        if (this.vboRegion != null) {
            this.vboRegion.bufferData(p_181722_1_, this.vboRange);
        } else {
            this.bindBuffer();
            OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, p_181722_1_, 35044);
            this.unbindBuffer();
            this.count = p_181722_1_.limit() / this.vertexFormat.getNextOffset();
        }
    }

    // Draw the arrays with batch processing to minimize state changes
    public void drawArrays(int mode) {
        if (this.drawMode > 0) {
            mode = this.drawMode;
        }

        if (this.vboRegion != null) {
            this.vboRegion.drawArrays(mode, this.vboRange);
        } else {
            // Avoid unnecessary calls to GL11 if possible
            if (this.count > 0) {
                GL11.glDrawArrays(mode, 0, this.count);
            }
        }
    }

    // Unbind the buffer (optimize by minimizing redundant unbinds)
    public void unbindBuffer() {
        OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
    }

    // Delete the buffer, optimize by avoiding unnecessary deletion
    public void deleteGlBuffers() {
        if (this.glBufferId >= 0) {
            OpenGlHelper.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }

    // Set the VBO region (optimize by caching VBO regions)
    public void setVboRegion(VboRegion p_setVboRegion_1_) {
        if (p_setVboRegion_1_ != null) {
            // Only delete the buffer if absolutely necessary
            if (this.vboRegion != p_setVboRegion_1_) {
                this.deleteGlBuffers();
                this.vboRegion = p_setVboRegion_1_;
                this.vboRange = new VboRange();
            }
        }
    }
}
