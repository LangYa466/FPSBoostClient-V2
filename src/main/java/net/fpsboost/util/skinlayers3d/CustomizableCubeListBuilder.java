package net.fpsboost.util.skinlayers3d;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class CustomizableCubeListBuilder {
    @Getter
    private final List<CustomizableCube> cubes;
    private int xTexOffs;
    private int yTexOffs;
    private boolean mirror;

    public CustomizableCubeListBuilder() {
        this.cubes = Lists.newArrayList();
    }

    public static CustomizableCubeListBuilder create() {
        return new CustomizableCubeListBuilder();
    }

    public CustomizableCubeListBuilder texOffs(final int i, final int j) {
        this.xTexOffs = i;
        this.yTexOffs = j;
        return this;
    }

    public CustomizableCubeListBuilder mirror(final boolean bl) {
        this.mirror = bl;
        return this;
    }

    public CustomizableCubeListBuilder addBox(final float x, final float y, final float z, final float pixelSize, final Direction[] hide) {
        final int textureSize = 64;
        this.cubes.add(new CustomizableCube(this.xTexOffs, this.yTexOffs, x, y, z, pixelSize, pixelSize, pixelSize, 0.0f, 0.0f, 0.0f, this.mirror, textureSize, textureSize, hide));
        return this;
    }
}
