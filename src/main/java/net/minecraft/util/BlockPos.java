package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import net.minecraft.entity.Entity;

import java.util.Iterator;

public class BlockPos extends Vec3i {
    // 方块位置原点
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    // 计算方块位置的位数
    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    // 构造函数，使用整数坐标
    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    // 构造函数，使用浮动坐标
    public BlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    // 构造函数，使用实体位置
    public BlockPos(Entity source) {
        this(source.posX, source.posY, source.posZ);
    }

    // 构造函数，使用向量坐标
    public BlockPos(Vec3 source) {
        this(source.xCoord, source.yCoord, source.zCoord);
    }

    // 构造函数，使用Vec3i坐标
    public BlockPos(Vec3i source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    // 坐标加法，使用浮动坐标
    public BlockPos add(double x, double y, double z) {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double) this.getX() + x, (double) this.getY() + y, (double) this.getZ() + z);
    }

    // 坐标加法，使用整数坐标
    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    // 使用Vec3i进行加法
    public BlockPos add(Vec3i vec) {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPos(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    // 坐标减法，使用Vec3i
    public BlockPos subtract(Vec3i vec) {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0 ? this : new BlockPos(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
    }

    // 向上偏移1
    public BlockPos up() {
        return this.up(1);
    }

    // 向上偏移n
    public BlockPos up(int n) {
        return this.offset(EnumFacing.UP, n);
    }

    // 向下偏移1
    public BlockPos down() {
        return this.down(1);
    }

    // 向下偏移n
    public BlockPos down(int n) {
        return this.offset(EnumFacing.DOWN, n);
    }

    // 向北偏移1
    public BlockPos north() {
        return this.north(1);
    }

    // 向北偏移n
    public BlockPos north(int n) {
        return this.offset(EnumFacing.NORTH, n);
    }

    // 向南偏移1
    public BlockPos south() {
        return this.south(1);
    }

    // 向南偏移n
    public BlockPos south(int n) {
        return this.offset(EnumFacing.SOUTH, n);
    }

    // 向西偏移1
    public BlockPos west() {
        return this.west(1);
    }

    // 向西偏移n
    public BlockPos west(int n) {
        return this.offset(EnumFacing.WEST, n);
    }

    // 向东偏移1
    public BlockPos east() {
        return this.east(1);
    }

    // 向东偏移n
    public BlockPos east(int n) {
        return this.offset(EnumFacing.EAST, n);
    }

    // 使用EnumFacing偏移1
    public BlockPos offset(EnumFacing facing) {
        return this.offset(facing, 1);
    }

    // 使用EnumFacing偏移n
    public BlockPos offset(EnumFacing facing, int n) {
        return n == 0 ? this : new BlockPos(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }

    // 计算向量的叉积
    public BlockPos crossProduct(Vec3i vec) {
        return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    // 将坐标转换为长整型
    public long toLong() {
        return ((long) this.getX() & X_MASK) << X_SHIFT | ((long) this.getY() & Y_MASK) << Y_SHIFT | ((long) this.getZ() & Z_MASK) << 0;
    }

    // 从长整型还原坐标
    public static BlockPos fromLong(long serialized) {
        int i = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(i, j, k);
    }

    // 获取包围盒内所有方块的位置
    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        final BlockPos blockpos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos>() {
            public Iterator<BlockPos> iterator() {
                return new AbstractIterator<BlockPos>() {
                    private BlockPos lastReturned = null;

                    protected BlockPos computeNext() {
                        if (this.lastReturned == null) {
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        } else if (this.lastReturned.equals(blockpos1)) {
                            return this.endOfData();
                        } else {
                            int i = this.lastReturned.getX();
                            int j = this.lastReturned.getY();
                            int k = this.lastReturned.getZ();

                            if (i < blockpos1.getX()) {
                                ++i;
                            } else if (j < blockpos1.getY()) {
                                i = blockpos.getX();
                                ++j;
                            } else if (k < blockpos1.getZ()) {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.lastReturned = new BlockPos(i, j, k);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    // 获取包围盒内所有方块的位置（可变）
    public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
        final BlockPos blockpos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos.MutableBlockPos>() {
            public Iterator<BlockPos.MutableBlockPos> iterator() {
                return new AbstractIterator<BlockPos.MutableBlockPos>() {
                    private BlockPos.MutableBlockPos theBlockPos = null;

                    protected BlockPos.MutableBlockPos computeNext() {
                        if (this.theBlockPos == null) {
                            this.theBlockPos = new BlockPos.MutableBlockPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                            return this.theBlockPos;
                        } else if (this.theBlockPos.equals(blockpos1)) {
                            return this.endOfData();
                        } else {
                            int i = this.theBlockPos.getX();
                            int j = this.theBlockPos.getY();
                            int k = this.theBlockPos.getZ();

                            if (i < blockpos1.getX()) {
                                ++i;
                            } else if (j < blockpos1.getY()) {
                                i = blockpos.getX();
                                ++j;
                            } else if (k < blockpos1.getZ()) {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.theBlockPos.x = i;
                            this.theBlockPos.y = j;
                            this.theBlockPos.z = k;
                            return this.theBlockPos;
                        }
                    }
                };
            }
        };
    }

    /**
     * 可变方块位置类
     */
    public static final class MutableBlockPos extends BlockPos {
        private int x;
        private int y;
        private int z;

        // 默认构造函数
        public MutableBlockPos() {
            this(0, 0, 0);
        }

        // 使用指定坐标构造
        public MutableBlockPos(int x_, int y_, int z_) {
            super(0, 0, 0);
            this.x = x_;
            this.y = y_;
            this.z = z_;
        }

        // 获取X坐标
        public int getX() {
            return this.x;
        }

        // 获取Y坐标
        public int getY() {
            return this.y;
        }

        // 获取Z坐标
        public int getZ() {
            return this.z;
        }

        // 设置坐标
        public BlockPos.MutableBlockPos set(int xIn, int yIn, int zIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            return this;
        }
    }
}