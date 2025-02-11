package net.minecraft.util;

import com.google.common.base.Objects;
import lombok.Getter;

@Getter
public class Vec3i implements Comparable<Vec3i> {
    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
    /**
     * -- GETTER --
     * 获取X轴坐标
     */
    private final int x;
    /**
     * -- GETTER --
     * 获取Y轴坐标
     */
    private final int y;
    /**
     * -- GETTER --
     * 获取Z轴坐标
     */
    private final int z;

    /**
     * 构造一个新的Vec3i对象，接受三个整数坐标
     *
     * @param xIn X轴坐标
     * @param yIn Y轴坐标
     * @param zIn Z轴坐标
     */
    public Vec3i(int xIn, int yIn, int zIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
    }

    /**
     * 构造一个新的Vec3i对象，接受三个浮动坐标并将它们四舍五入为整数
     *
     * @param xIn X轴坐标
     * @param yIn Y轴坐标
     * @param zIn Z轴坐标
     */
    public Vec3i(double xIn, double yIn, double zIn) {
        this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
    }

    /**
     * 判断当前Vec3i对象是否与另一个对象相等
     *
     * @param p_equals_1_ 比较的对象
     * @return 如果相等则返回true，否则返回false
     */
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof Vec3i)) {
            return false;
        } else {
            Vec3i vec3i = (Vec3i) p_equals_1_;
            return this.getX() == vec3i.getX() && (this.getY() == vec3i.getY() && this.getZ() == vec3i.getZ());
        }
    }

    /**
     * 获取当前Vec3i对象的哈希值
     *
     * @return 当前Vec3i对象的哈希值
     */
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    /**
     * 比较当前Vec3i对象与另一个Vec3i对象
     *
     * @param p_compareTo_1_ 需要比较的Vec3i对象
     * @return 如果当前对象小于、等于或大于指定对象，返回负值、零或正值
     */
    public int compareTo(Vec3i p_compareTo_1_) {
        return this.getY() == p_compareTo_1_.getY() ? (this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ()) : this.getY() - p_compareTo_1_.getY();
    }

    /**
     * 计算当前Vec3i对象与另一个Vec3i对象的叉积
     *
     * @param vec 另一个Vec3i对象
     * @return 叉积结果Vec3i对象
     */
    public Vec3i crossProduct(Vec3i vec) {
        return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    /**
     * 计算当前Vec3i对象到指定坐标的平方距离
     *
     * @param toX 目标X坐标
     * @param toY 目标Y坐标
     * @param toZ 目标Z坐标
     * @return 平方距离
     */
    public double distanceSq(double toX, double toY, double toZ) {
        double d0 = (double) this.getX() - toX;
        double d1 = (double) this.getY() - toY;
        double d2 = (double) this.getZ() - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * 计算当前Vec3i对象到目标坐标的平方距离（目标坐标为Vec3i中心点）
     *
     * @param xIn 目标X坐标
     * @param yIn 目标Y坐标
     * @param zIn 目标Z坐标
     * @return 平方距离
     */
    public double distanceSqToCenter(double xIn, double yIn, double zIn) {
        double d0 = (double) this.getX() + 0.5D - xIn;
        double d1 = (double) this.getY() + 0.5D - yIn;
        double d2 = (double) this.getZ() + 0.5D - zIn;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * 计算当前Vec3i对象到另一个Vec3i对象的平方距离
     *
     * @param to 目标Vec3i对象
     * @return 平方距离
     */
    public double distanceSq(Vec3i to) {
        return this.distanceSq(to.getX(), to.getY(), to.getZ());
    }

    /**
     * 获取Vec3i对象的字符串表示
     *
     * @return 字符串表示
     */
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }
}
