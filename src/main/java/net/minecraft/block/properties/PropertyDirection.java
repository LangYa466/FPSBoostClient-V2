package net.minecraft.block.properties;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;

import java.util.Collection;

public class PropertyDirection extends PropertyEnum<EnumFacing> {
    protected PropertyDirection(String name, Collection<EnumFacing> values) {
        super(name, EnumFacing.class, values);
    }

    public static PropertyDirection create(String name) {
        return create(name, Predicates.alwaysTrue());
    }

    public static PropertyDirection create(String name, Predicate<EnumFacing> filter) {
        return create(name, Collections2.filter(Lists.newArrayList(EnumFacing.CACHED_VALUES), filter));
    }

    public static PropertyDirection create(String name, Collection<EnumFacing> values) {
        return new PropertyDirection(name, values);
    }
}
