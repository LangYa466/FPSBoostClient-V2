package net.minecraft.client.renderer.chunk;

import net.minecraft.util.EnumFacing;

import java.util.Set;

public class SetVisibility {
    private static final int COUNT_FACES = EnumFacing.CACHED_VALUES.length;
    private long bits;

    public void setManyVisible(Set<EnumFacing> p_178620_1_) {
        for (EnumFacing enumfacing : p_178620_1_) {
            for (EnumFacing enumfacing1 : p_178620_1_) {
                this.setVisible(enumfacing, enumfacing1, true);
            }
        }
    }

    public void setVisible(EnumFacing facing, EnumFacing facing2, boolean p_178619_3_) {
        this.setBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES, p_178619_3_);
        this.setBit(facing2.ordinal() + facing.ordinal() * COUNT_FACES, p_178619_3_);
    }

    public void setAllVisible(boolean visible) {
        if (visible) {
            this.bits = -1L;
        } else {
            this.bits = 0L;
        }
    }

    public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
        return this.getBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(' ');

        for (EnumFacing enumfacing : EnumFacing.CACHED_VALUES) {
            stringbuilder.append(' ').append(enumfacing.toString().toUpperCase().charAt(0));
        }

        stringbuilder.append('\n');

        for (EnumFacing enumfacing2 : EnumFacing.CACHED_VALUES) {
            stringbuilder.append(enumfacing2.toString().toUpperCase().charAt(0));

            for (EnumFacing enumfacing1 : EnumFacing.CACHED_VALUES) {
                if (enumfacing2 == enumfacing1) {
                    stringbuilder.append("  ");
                } else {
                    boolean flag = this.isVisible(enumfacing2, enumfacing1);
                    stringbuilder.append(' ').append(flag ? 'Y' : 'n');
                }
            }

            stringbuilder.append('\n');
        }

        return stringbuilder.toString();
    }

    private boolean getBit(int p_getBit_1_) {
        return (this.bits & (long) (1L << p_getBit_1_)) != 0L;
    }

    private void setBit(int p_setBit_1_, boolean p_setBit_2_) {
        if (p_setBit_2_) {
            this.setBit(p_setBit_1_);
        } else {
            this.clearBit(p_setBit_1_);
        }
    }

    private void setBit(int p_setBit_1_) {
        this.bits |= 1L << p_setBit_1_;
    }

    private void clearBit(int p_clearBit_1_) {
        this.bits &= ~(1L << p_clearBit_1_);
    }
}
