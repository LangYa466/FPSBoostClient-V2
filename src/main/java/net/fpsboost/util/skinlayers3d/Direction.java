package net.fpsboost.util.skinlayers3d;

import net.minecraft.util.Vec3i;
import org.lwjgl.util.vector.Vector3f;

public enum Direction {
    DOWN(new Vec3i(0, -1, 0)),
    UP(new Vec3i(0, 1, 0)),
    NORTH(new Vec3i(0, 0, -1)),
    SOUTH(new Vec3i(0, 0, 1)),
    WEST(new Vec3i(-1, 0, 0)),
    EAST(new Vec3i(1, 0, 0));

    private final Vec3i normal;

    private Direction(final Vec3i normal) {
        this.normal = normal;
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public int getStepZ() {
        return this.normal.getZ();
    }

    public Vector3f step() {
        return new Vector3f((float) this.getStepX(), (float) this.getStepY(), (float) this.getStepZ());
    }
}
