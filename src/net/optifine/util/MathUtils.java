package net.optifine.util;

import net.minecraft.util.MathHelper;

public class MathUtils
{
    public static final float PI = (float)Math.PI;
    public static final float PI2 = PI * 2F;
    public static final float PId2 = PI / 2F;
    private static final float[] ASIN_TABLE = new float[65536];

    static
    {
        // 预计算ASIN_TABLE，避免每次加载类时填充
        for (int i = 0; i < 65536; ++i)
        {
            ASIN_TABLE[i] = (float)Math.asin((double)i / 32767.5D - 1.0D);
        }

        for (int j = -1; j < 2; ++j)
        {
            ASIN_TABLE[(int)(((double)j + 1.0D) * 32767.5D) & 65535] = (float)Math.asin(j);
        }
    }

    public static float asin(float value)
    {
        return ASIN_TABLE[(int)((value + 1.0F) * 32767.5D) & 65535];
    }

    public static float acos(float value)
    {
        return PId2 - ASIN_TABLE[(int)((value + 1.0F) * 32767.5D) & 65535];
    }

    public static int getAverage(int[] vals)
    {
        if (vals.length == 0) return 0;
        return getSum(vals) / vals.length;
    }

    public static int getSum(int[] vals)
    {
        int sum = 0;
        for (int val : vals)
        {
            sum += val;
        }
        return sum;
    }

    public static int roundDownToPowerOfTwo(int val)
    {
        int i = Integer.highestOneBit(val);  // 使用位操作代替MathHelper
        return val == i ? i : i >> 1;  // 除以2
    }

    public static boolean equalsDelta(float f1, float f2, float delta)
    {
        return Math.abs(f1 - f2) <= delta;
    }

    public static float toDeg(float angle)
    {
        return angle * 180.0F / PI;
    }

    public static float toRad(float angle)
    {
        return angle / 180.0F * PI;
    }

    public static float roundToFloat(double d)
    {
        return (float)Math.round(d * 1.0E8D) / 1.0E8F;  // 保持精度
    }
}
