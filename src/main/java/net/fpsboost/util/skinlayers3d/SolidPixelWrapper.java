package net.fpsboost.util.skinlayers3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolidPixelWrapper
{
    private static final int[][] offsets = new int[][] { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
    private static final Direction[] hiddenDirN = new Direction[] { Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN };
    private static final Direction[] hiddenDirS = new Direction[] { Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN };
    private static final Direction[] hiddenDirW = new Direction[] { Direction.SOUTH, Direction.NORTH, Direction.UP, Direction.DOWN };
    private static final Direction[] hiddenDirE = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN };
    private static final Direction[] hiddenDirUD = new Direction[] { Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH };

    public static CustomizableModelPart wrapBox(final NativeImage natImage, final int width, final int height, final int depth, final int textureU, final int textureV, final boolean topPivot, final float rotationOffset) {
        final List<CustomizableCube> cubes = new ArrayList<CustomizableCube>();
        final float pixelSize = 1.0f;
        final float staticXOffset = -width / 2.0f;
        final float staticYOffset = topPivot ? rotationOffset : (-height + rotationOffset);
        final float staticZOffset = -depth / 2.0f;
        for (int u = 0; u < width; ++u) {
            for (int v = 0; v < height; ++v) {
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == height - 1, textureU + depth + u, textureV + depth + v, staticXOffset + u, staticYOffset + v, staticZOffset, Direction.SOUTH);
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == height - 1, textureU + 2 * depth + width + u, textureV + depth + v, staticXOffset + width - 1.0f - u, staticYOffset + v, staticZOffset + depth - 1.0f, Direction.NORTH);
            }
        }
        for (int u = 0; u < depth; ++u) {
            for (int v = 0; v < height; ++v) {
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == depth - 1 || v == height - 1, textureU - 1 + depth - u, textureV + depth + v, staticXOffset, staticYOffset + v, staticZOffset + u, Direction.EAST);
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == depth - 1 || v == height - 1, textureU + depth + width + u, textureV + depth + v, staticXOffset + width - 1.0f, staticYOffset + v, staticZOffset + u, Direction.WEST);
            }
        }
        for (int u = 0; u < width; ++u) {
            for (int v = 0; v < depth; ++v) {
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == depth - 1, textureU + depth + u, textureV + depth - 1 - v, staticXOffset + u, staticYOffset, staticZOffset + v, Direction.UP);
                addPixel(natImage, cubes, pixelSize, u == 0 || v == 0 || u == width - 1 || v == depth - 1, textureU + depth + width + u, textureV + depth - 1 - v, staticXOffset + u, staticYOffset + height - 1.0f, staticZOffset + v, Direction.DOWN);
            }
        }
        return new CustomizableModelPart(cubes);
    }

    private static void addPixel(NativeImage natImage, List<CustomizableCube> cubes, float pixelSize, boolean onBorder, int u, int v, float x, float y, float z, Direction dir) {
        if (natImage.getLuminanceOrAlpha(u, v) != 0) {
            Set<Direction> hide = new HashSet<>();
            if (!onBorder) {
                for (int i = 0; i < SolidPixelWrapper.offsets.length; ++i) {
                    final int tU = u + SolidPixelWrapper.offsets[i][1];
                    final int tV = v + SolidPixelWrapper.offsets[i][0];
                    if (tU >= 0 && tU < 64 && tV >= 0 && tV < 64 && natImage.getLuminanceOrAlpha(tU, tV) != 0) {
                        if (dir == Direction.NORTH) {
                            hide.add(SolidPixelWrapper.hiddenDirN[i]);
                        }
                        if (dir == Direction.SOUTH) {
                            hide.add(SolidPixelWrapper.hiddenDirS[i]);
                        }
                        if (dir == Direction.EAST) {
                            hide.add(SolidPixelWrapper.hiddenDirE[i]);
                        }
                        if (dir == Direction.WEST) {
                            hide.add(SolidPixelWrapper.hiddenDirW[i]);
                        }
                        if (dir == Direction.UP || dir == Direction.DOWN) {
                            hide.add(SolidPixelWrapper.hiddenDirUD[i]);
                        }
                    }
                }
                hide.add(dir);
            }
            cubes.addAll(CustomizableCubeListBuilder.create().texOffs(u - 2, v - 1).addBox(x, y, z, pixelSize, hide.toArray(new Direction[hide.size()])).getCubes());
        }
    }
}
