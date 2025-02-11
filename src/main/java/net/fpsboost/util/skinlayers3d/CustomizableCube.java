package net.fpsboost.util.skinlayers3d;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.util.vector.Vector3f;

public class CustomizableCube {
    private final Direction[] hidden;
    private final Polygon[] polygons;
    private int polygonCount;
    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;

    public CustomizableCube(final int u, final int v, float x, float y, float z, final float sizeX, final float sizeY, final float sizeZ, final float extraX, final float extraY, final float extraZ, final boolean mirror, final float textureWidth, final float textureHeight, final Direction[] hide) {
        this.polygonCount = 0;
        this.hidden = hide;
        this.minX = x;
        this.minY = y;
        this.minZ = z;
        this.maxX = x + sizeX;
        this.maxY = y + sizeY;
        this.maxZ = z + sizeZ;
        this.polygons = new Polygon[6];
        float pX = x + sizeX;
        float pY = y + sizeY;
        float pZ = z + sizeZ;
        x -= extraX;
        y -= extraY;
        z -= extraZ;
        pX += extraX;
        pY += extraY;
        pZ += extraZ;
        if (mirror) {
            final float i = pX;
            pX = x;
            x = i;
        }
        final Vertex vertex = new Vertex(x, y, z, 0.0f, 0.0f);
        final Vertex vertex2 = new Vertex(pX, y, z, 0.0f, 8.0f);
        final Vertex vertex3 = new Vertex(pX, pY, z, 8.0f, 8.0f);
        final Vertex vertex4 = new Vertex(x, pY, z, 8.0f, 0.0f);
        final Vertex vertex5 = new Vertex(x, y, pZ, 0.0f, 0.0f);
        final Vertex vertex6 = new Vertex(pX, y, pZ, 0.0f, 8.0f);
        final Vertex vertex7 = new Vertex(pX, pY, pZ, 8.0f, 8.0f);
        final Vertex vertex8 = new Vertex(x, pY, pZ, 8.0f, 0.0f);
        final float l = u + sizeZ + sizeX;
        final float n = u + sizeZ + sizeX + sizeZ;
        final float q = v + sizeZ;
        final float r = v + sizeZ + sizeY;
        if (this.visibleFace(Direction.DOWN)) {
            this.polygons[this.polygonCount++] = new Polygon(new Vertex[]{vertex6, vertex5, vertex, vertex2}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.DOWN);
        }
        if (this.visibleFace(Direction.UP)) {
            this.polygons[this.polygonCount++] = new Polygon(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.UP);
        }
        if (this.visibleFace(Direction.WEST)) {
            this.polygons[this.polygonCount++] = new Polygon(new Vertex[]{vertex, vertex5, vertex8, vertex4}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.WEST);
        }
        if (this.visibleFace(Direction.NORTH)) {
            this.polygons[this.polygonCount++] = new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.NORTH);
        }
        if (this.visibleFace(Direction.EAST)) {
            this.polygons[this.polygonCount++] = new Polygon(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST);
        }
        if (this.visibleFace(Direction.SOUTH)) {
            this.polygons[this.polygonCount++] = new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.SOUTH);
        }
    }

    private boolean visibleFace(final Direction face) {
        for (final Direction dir : this.hidden) {
            if (dir == face) {
                return false;
            }
        }
        return true;
    }

    public void render(final WorldRenderer worldRenderer, boolean redTint) {
        redTint = false;
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        for (int id = 0; id < this.polygonCount; ++id) {
            final Polygon polygon = this.polygons[id];
            for (int i = 0; i < 4; ++i) {
                final Vertex vertex = polygon.vertices[i];
                worldRenderer.pos((double) vertex.pos.x, (double) vertex.pos.y, (double) vertex.pos.z).tex((double) vertex.u, (double) vertex.v).color(255, redTint ? 127 : 255, redTint ? 127 : 255, 255).normal(polygon.normal.x, polygon.normal.y, polygon.normal.z).endVertex();
            }
        }
        Tessellator.getInstance().draw();
    }

    private static class Polygon {
        public final Vertex[] vertices;
        public final Vector3f normal;

        public Polygon(final Vertex[] vertexs, final float f, final float g, final float h, final float i, final float j, final float k, final boolean bl, final Direction dir) {
            this.vertices = vertexs;
            final float l = 0.0f / j;
            final float m = 0.0f / k;
            vertexs[0] = vertexs[0].remap(h / j - l, g / k + m);
            vertexs[1] = vertexs[1].remap(f / j + l, g / k + m);
            vertexs[2] = vertexs[2].remap(f / j + l, i / k - m);
            vertexs[3] = vertexs[3].remap(h / j - l, i / k - m);
            if (bl) {
                for (int n = vertexs.length, o = 0; o < n / 2; ++o) {
                    final Vertex vertex = vertexs[o];
                    vertexs[o] = vertexs[n - 1 - o];
                    vertexs[n - 1 - o] = vertex;
                }
            }
            this.normal = dir.step();
            if (bl) {
                this.normal.setX(this.normal.getX() * -1.0f);
            }
        }
    }

    private static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;
        public final float o;
        public final float p;
        public final float q;

        public Vertex(final float f, final float g, final float h, final float i, final float j) {
            this(new Vector3f(f, g, h), i, j);
        }

        public Vertex remap(final float f, final float g) {
            return new Vertex(this.pos, f, g);
        }

        public Vertex(final Vector3f vector3f, final float f, final float g) {
            this.pos = vector3f;
            this.u = f;
            this.v = g;
            this.o = this.pos.x / 16.0f;
            this.p = this.pos.y / 16.0f;
            this.q = this.pos.z / 16.0f;
        }
    }
}
